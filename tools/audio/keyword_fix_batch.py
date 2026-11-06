"""Builds review batch 2026-11-06-umbrella: new takes of the key word "umbrella" (user, 2026-11-06: "umbrella says
umbrelleh"). Today's clip (release 2026-09-30) is Kokoro from text, which Kokoro reads as /ʌmbɹˈɛlə/; its last
vowel comes out close to "eh". Hear It and Say It play it.

New takes:
- Kokoro in the voice of every other key word (af_heart*0.7+af_bella*0.3), from phonemes so the last vowel can be
  set: /ə/ with a full stop, /ʌ/ ("uh") and /ɐ/; at 0.95 (the key words' speed) and 0.85.
- VoiceStudio (OmniVoice clone of Lily, voicestudio_lily.py): "Umbrella.", four seeds.
The first row is today's clip, to compare. Review audio only: nothing reaches the app without the user's OK.

Usage: python -I keyword_fix_batch.py <out dir> <models dir>
"""
import pathlib
import shutil
import sys

import soundfile as sf

sys.stdout.reconfigure(encoding="utf-8")
sys.stderr.reconfigure(encoding="utf-8")
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
from kokoro_local import SR_OUT, clean, synth  # noqa: E402
from review_page import write_review_page  # noqa: E402
from voicestudio_lily import REPO, VOSK, heard, reference, say  # noqa: E402

OUT, MODELS = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
OUT.mkdir(parents=True, exist_ok=True)
WORD = "umbrella"
VOICE = "af_heart*0.7+af_bella*0.3"
KOKORO = [  # (tag, phonemes, what the end should sound like)
    ("schwa", "ʌmbɹˈɛlə.", "a soft 'uh' end, with a full stop"),
    ("uh", "ʌmbɹˈɛlʌ", "a clear 'uh' end"),
    ("ah", "ʌmbɹˈɛlɐ", "an open 'uh' end, near 'ah'"),
]
SPEEDS = (0.95, 0.85)
VS_TEXT, VS_SEEDS, NUM_STEP = "Umbrella.", (1, 2, 3, 4), 16
GROUP = f"Key word: {WORD}"
LISTEN = ("Does it end in 'uh' (um-BREL-luh), not 'eh'? Sounds like Lily? "
          "Plays: Hear It (after the letter sound) and Say It (the word to copy).")

from kokoro_onnx import Kokoro  # noqa: E402
from voice_candidates import parse_candidate  # noqa: E402
from vosk import Model, SetLogLevel  # noqa: E402

SetLogLevel(-1)
vosk = Model(str(VOSK))
rows = []

today = OUT / f"kw_{WORD}_today.wav"
shutil.copyfile(REPO / "app" / "src" / "main" / "assets" / "audio" / "keywords" / f"kw_{WORD}.wav", today)
a, sr = sf.read(today, dtype="float32")
rows.append({"file": today.name, "group": GROUP, "says": f"today's clip (kw_{WORD}.wav, Kokoro from text, 2026-09-30)",
             "listen_for": "Today's clip, to compare: you heard 'umbrelleh'. Mark it FIX unless it is the best.",
             "auto_check": f"{len(a) / sr:.2f} s; Vosk hears: \"{heard(vosk, a, sr)}\""})

k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
_, voice = parse_candidate(VOICE, k)
for tag, ph, end in KOKORO:
    for speed in SPEEDS:
        au = clean(*synth(k, ph, voice, speed, phonemes=True))
        name = f"kw_{WORD}_k_{tag}_{round(speed * 100)}.wav"
        sf.write(OUT / name, au, SR_OUT)
        rows.append({"file": name, "group": GROUP, "says": f"{WORD} (Kokoro, {end})", "listen_for": LISTEN,
                     "auto_check": f"Kokoro {VOICE} from phonemes /{ph}/, speed {speed:.2f}, "
                                   f"{len(au) / SR_OUT:.2f} s; Vosk hears: \"{heard(vosk, au, SR_OUT)}\""})
    print("kokoro", tag, "done", flush=True)

ref_wav, ref_text = reference()
for seed in VS_SEEDS:
    try:
        a, sr = say(VS_TEXT, seed, ref_wav, ref_text, num_step=NUM_STEP)
    except OSError as e:  # the engine stopped or is closed: keep the takes made so far, rerun later for these
        print(f"VoiceStudio not reachable ({e}); the page has no VoiceStudio rows from seed {seed} on", flush=True)
        break
    assert sr == SR_OUT, sr
    au = clean(a, sr)
    name = f"kw_{WORD}_vs{seed}.wav"
    sf.write(OUT / name, au, SR_OUT)
    rows.append({"file": name, "group": GROUP, "says": f"{WORD} (VoiceStudio, Lily's voice)", "listen_for": LISTEN,
                 "auto_check": f"VoiceStudio (OmniVoice, Lily reference), \"{VS_TEXT}\", seed {seed}, num_step "
                               f"{NUM_STEP}, {len(au) / SR_OUT:.2f} s; Vosk hears: \"{heard(vosk, au, SR_OUT)}\""})
    print("voicestudio seed", seed, "done", flush=True)

write_review_page(
    OUT, OUT.name, "playIT: the key word umbrella",
    "You heard today's clip say 'umbrelleh'. The first row is today's clip. Kokoro rows use the same voice as every "
    "other key word, with the end of the word spelled out; VoiceStudio rows use Lily's cloned voice, like the new "
    "letter sounds. Mark exactly ONE take OK and the others FIX (FIX all, with a note, if none is right). "
    "Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
