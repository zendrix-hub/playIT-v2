"""Lily's voice through VoiceStudio (local OmniVoice engine, http://localhost:3900), cloned from her own Kokoro lesson
clips so a new line matches the old ones. Shared by the VoiceStudio review batches (sound_check_batch.py made the
first one, 2026-11-06). OmniVoice's weights are CC BY-NC: non-commercial, with credit (the Parent Zone credits it).
"""
import io
import json
import pathlib
import urllib.request
import uuid

import numpy as np
import soundfile as sf

REPO = pathlib.Path(__file__).resolve().parents[2]
LESSON = REPO / "app" / "src" / "main" / "assets" / "audio" / "vo" / "lesson"
VOSK = REPO / "app" / "src" / "main" / "assets" / "vosk-model"

# The reference: four lesson clips already in the app, joined with short gaps, with their text.
REFERENCE = [
    ("vo_welcome_01", "Hi there! I'm so happy you're here. Let's play and learn together!"),
    ("vo_blendit_intro_01", "Let's build some words together!"),
    ("vo_complete_01", "You did it! I'm so proud of you!"),
    ("vo_unlock_01", "A new letter is ready for you!"),
]


def reference():
    """(wav bytes, text) of Lily's reference."""
    parts, sr0 = [], None
    for name, _ in REFERENCE:
        a, sr = sf.read(LESSON / f"{name}.wav", dtype="float32", always_2d=True)
        sr0 = sr0 or sr
        assert sr == sr0, name
        parts += [a.mean(axis=1), np.zeros(int(0.35 * sr), dtype="float32")]
    buf = io.BytesIO()
    sf.write(buf, np.concatenate(parts), sr0, format="WAV")
    return buf.getvalue(), " ".join(t for _, t in REFERENCE)


def say(text, seed, ref_wav, ref_text, url="http://localhost:3900"):
    """One take of [text] in Lily's voice: (mono float32 samples, sample rate). Raises if VoiceStudio sends no audio."""
    fields = {"text": text, "language": "English", "ref_text": ref_text, "seed": str(seed), "effect_preset": "raw"}
    b = uuid.uuid4().hex
    body = b"".join(f'--{b}\r\nContent-Disposition: form-data; name="{k}"\r\n\r\n{v}\r\n'.encode() for k, v in fields.items())
    body += (f'--{b}\r\nContent-Disposition: form-data; name="ref_audio"; filename="lily_ref.wav"\r\n'
             f"Content-Type: audio/wav\r\n\r\n").encode() + ref_wav + f"\r\n--{b}--\r\n".encode()
    req = urllib.request.Request(f"{url}/generate", data=body, headers={"Content-Type": f"multipart/form-data; boundary={b}"})
    with urllib.request.urlopen(req, timeout=600) as r:
        if not r.headers.get("Content-Type", "").startswith("audio/"):
            raise RuntimeError(f"VoiceStudio returned {r.headers.get('Content-Type')}: {r.read()[:300]!r}")
        a, sr = sf.read(io.BytesIO(r.read()), dtype="float32", always_2d=True)
    return a.mean(axis=1), sr


def heard(model, a, sr):
    """What the app's own Vosk model hears in a clip, with no grammar."""
    from vosk import KaldiRecognizer
    x = np.interp(np.linspace(0, len(a), int(len(a) * 16000 / sr), endpoint=False), np.arange(len(a)), a)
    rec = KaldiRecognizer(model, 16000)
    rec.AcceptWaveform((np.clip(x, -1, 1) * 32767).astype(np.int16).tobytes())
    return json.loads(rec.FinalResult()).get("text", "")
