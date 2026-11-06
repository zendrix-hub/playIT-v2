"""Builds review batch 2026-11-06-sound-check: Lily's lines for the sound check that opens Blend It (user choice
2026-11-06, after the teacher asked how anyone can tell the child learned). The user asked for VoiceStudio
(local OmniVoice engine, http://localhost:3900): each line is cloned from Lily's own Kokoro lesson clips, so the
voice matches. One Kokoro take per line is on the page too, to compare. Review audio only.

Usage: python -I sound_check_batch.py <out dir> <models dir> [VoiceStudio URL]
"""
import io
import json
import pathlib
import sys
import urllib.request
import uuid

import numpy as np
import soundfile as sf

REPO = pathlib.Path(r"C:\Users\Erik\Desktop\playIT-v2")
sys.path.insert(0, str(REPO / "tools" / "audio"))
from review_page import write_review_page  # noqa: E402

OUT, MODELS = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
VS = sys.argv[3] if len(sys.argv) > 3 else "http://localhost:3900"
OUT.mkdir(parents=True, exist_ok=True)
LESSON = REPO / "app" / "src" / "main" / "assets" / "audio" / "vo" / "lesson"
VOICE = "af_heart*0.7+af_bella*0.3"
SEEDS = (1, 2, 3)

# Lily's reference: four lesson clips already in the app, joined with short gaps.
REFERENCE = [
    ("vo_welcome_01", "Hi there! I'm so happy you're here. Let's play and learn together!"),
    ("vo_blendit_intro_01", "Let's build some words together!"),
    ("vo_complete_01", "You did it! I'm so proud of you!"),
    ("vo_unlock_01", "A new letter is ready for you!"),
]

LINES = [  # (clip id, text, where it plays)
    ("chk_intro", "Sound check! Tap the letter you hear.", "start of the sound check, before Blend It"),
    ("chk_which_says", "Which letter says...", "before each sound; the letter's sound plays right after"),
    ("chk_good_try", "Good try! Let's do the next one.", "after a wrong tap (no help in the check)"),
    ("chk_done", "Sound check done! Great job!", "end of the sound check, before the words"),
]


def reference():
    parts, sr0 = [], None
    for name, _ in REFERENCE:
        a, sr = sf.read(LESSON / f"{name}.wav", dtype="float32", always_2d=True)
        sr0 = sr0 or sr
        assert sr == sr0, name
        parts += [a.mean(axis=1), np.zeros(int(0.35 * sr), dtype="float32")]
    buf = io.BytesIO()
    sf.write(buf, np.concatenate(parts), sr0, format="WAV")
    return buf.getvalue(), " ".join(t for _, t in REFERENCE)


def voicestudio(text, seed, ref_wav, ref_text):
    fields = {"text": text, "language": "English", "ref_text": ref_text, "seed": str(seed), "effect_preset": "raw"}
    b = uuid.uuid4().hex
    body = b"".join(f'--{b}\r\nContent-Disposition: form-data; name="{k}"\r\n\r\n{v}\r\n'.encode() for k, v in fields.items())
    body += (f'--{b}\r\nContent-Disposition: form-data; name="ref_audio"; filename="lily_ref.wav"\r\n'
             f"Content-Type: audio/wav\r\n\r\n").encode() + ref_wav + f"\r\n--{b}--\r\n".encode()
    req = urllib.request.Request(f"{VS}/generate", data=body, headers={"Content-Type": f"multipart/form-data; boundary={b}"})
    with urllib.request.urlopen(req, timeout=600) as r:
        if not r.headers.get("Content-Type", "").startswith("audio/"):
            raise RuntimeError(f"VoiceStudio returned {r.headers.get('Content-Type')}: {r.read()[:300]!r}")
        a, sr = sf.read(io.BytesIO(r.read()), dtype="float32", always_2d=True)
    return a.mean(axis=1), sr


def heard(model, a, sr):
    from vosk import KaldiRecognizer
    x = np.interp(np.linspace(0, len(a), int(len(a) * 16000 / sr), endpoint=False), np.arange(len(a)), a)
    rec = KaldiRecognizer(model, 16000)
    rec.AcceptWaveform((np.clip(x, -1, 1) * 32767).astype(np.int16).tobytes())
    return json.loads(rec.FinalResult()).get("text", "")


from kokoro_onnx import Kokoro  # noqa: E402
from kokoro_local import SR_OUT, clean, synth  # noqa: E402
from voice_candidates import parse_candidate  # noqa: E402
from vosk import Model, SetLogLevel  # noqa: E402

SetLogLevel(-1)
vosk = Model(str(REPO / "app" / "src" / "main" / "assets" / "vosk-model"))
k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
_, kvoice = parse_candidate(VOICE, k)
ref_wav, ref_text = reference()
rows = []
for cid, text, where in LINES:
    takes = []
    for seed in SEEDS:
        a, sr = voicestudio(text, seed, ref_wav, ref_text)
        assert sr == SR_OUT, sr
        takes.append((f"{cid}_vs{seed}.wav", clean(a, sr), f"VoiceStudio (OmniVoice, Lily reference), seed {seed}"))
    kk = clean(*synth(k, text, kvoice, 0.95))
    takes.append((f"{cid}_kokoro.wav", kk, f"Kokoro {VOICE}, speed 0.95 (to compare)"))
    for name, au, how in takes:
        sf.write(OUT / name, au, SR_OUT)
        rows.append({"file": name, "group": f"Lily: {cid}", "says": text,
                     "listen_for": f"Sounds like Lily? Warm, clear, easy for a 6-year-old? Plays: {where}.",
                     "auto_check": f"{how}, {len(au) / SR_OUT:.2f} s; Vosk hears: \"{heard(vosk, au, SR_OUT)}\""})
    print(cid, "done", flush=True)

write_review_page(
    OUT, OUT.name, "playIT: the sound check before Blend It",
    "Four new lines for the sound check (Lily says a sound, the child taps its letter). Three VoiceStudio takes "
    "made from Lily's own clips, and one Kokoro take to compare. For EACH line (each group), mark exactly ONE take "
    "OK and the others FIX (FIX all, with a note, if none is right). Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
