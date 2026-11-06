"""Builds review batch 2026-11-06-umbrella-vs: new takes of the key word "umbrella" (user, 2026-11-06: "umbrella says
umbrelleh"). Today's clip (release 2026-09-30) is Kokoro from text, which Kokoro reads as /ʌmbɹˈɛlə/; its last
vowel comes out close to "eh". Hear It and Say It play it.

The takes come from VoiceStudio only (user, 2026-11-06: "if im prompting always use voicestudio"): the OmniVoice
clone of Lily (voicestudio_lily.py), as for the letter sounds. Three wordings, so the end can be heard three ways:
"Umbrella.", "Umbrella!" and "Um-brel-luh." (spelled to end in "uh"). The first row is today's clip, to compare.
Review audio only: nothing reaches the app without the user's OK.

Resumable: a take whose file exists is kept, so a rerun after VoiceStudio stops makes only the missing ones.
Load nothing else big while it runs: VoiceStudio reloads its model after an idle timeout and died once doing that
with 3.6 GB free (2026-11-06).

Usage: python -I keyword_fix_batch.py <out dir>
"""
import pathlib
import shutil
import sys

import soundfile as sf

sys.stdout.reconfigure(encoding="utf-8")
sys.stderr.reconfigure(encoding="utf-8")
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from kokoro_local import SR_OUT, clean  # noqa: E402
from review_page import write_review_page  # noqa: E402
from voicestudio_lily import REPO, VOSK, heard, reference, say  # noqa: E402

OUT = pathlib.Path(sys.argv[1])
OUT.mkdir(parents=True, exist_ok=True)
WORD = "umbrella"
TAKES = [  # (tag, text, seeds)
    ("a", "Umbrella.", (1, 2, 3, 4)),
    ("b", "Umbrella!", (1, 2)),
    ("c", "Um-brel-luh.", (1, 2)),
]
NUM_STEP = 16
GROUP = f"Key word: {WORD}"
LISTEN = ("Does it end in 'uh' (um-BREL-luh), not 'eh'? Sounds like Lily? "
          "Plays: Hear It (after the letter sound) and Say It (the word to copy).")

today = OUT / f"kw_{WORD}_today.wav"
shutil.copyfile(REPO / "app" / "src" / "main" / "assets" / "audio" / "keywords" / f"kw_{WORD}.wav", today)
made = []  # (file name, text, seed)
ref_wav, ref_text = reference()
for tag, text, seeds in TAKES:
    for seed in seeds:
        name = f"kw_{WORD}_{tag}_vs{seed}.wav"
        if not (OUT / name).exists():
            try:
                a, sr = say(text, seed, ref_wav, ref_text, num_step=NUM_STEP)
            except OSError as e:  # the engine stopped: keep what is made, rerun for the rest
                sys.exit(f"VoiceStudio not reachable ({e}) at {name}; rerun once it is up")
            assert sr == SR_OUT, sr
            sf.write(OUT / name, clean(a, sr), SR_OUT)
            print(name, "made", flush=True)
        made.append((name, text, seed))

from vosk import Model, SetLogLevel  # noqa: E402

SetLogLevel(-1)
vosk = Model(str(VOSK))
a, sr = sf.read(today, dtype="float32")
rows = [{"file": today.name, "group": GROUP, "says": f"today's clip (kw_{WORD}.wav, Kokoro from text, 2026-09-30)",
         "listen_for": "Today's clip, to compare: you heard 'umbrelleh'. Mark it FIX unless it is the best.",
         "auto_check": f"{len(a) / sr:.2f} s; Vosk hears: \"{heard(vosk, a, sr)}\""}]
for name, text, seed in made:
    au, sr = sf.read(OUT / name, dtype="float32")
    rows.append({"file": name, "group": GROUP, "says": f"{text} (VoiceStudio, Lily's voice)", "listen_for": LISTEN,
                 "auto_check": f"VoiceStudio (OmniVoice, Lily reference), \"{text}\", seed {seed}, num_step "
                               f"{NUM_STEP}, {len(au) / sr:.2f} s; Vosk hears: \"{heard(vosk, au, sr)}\""})

write_review_page(
    OUT, OUT.name, "playIT: the key word umbrella (VoiceStudio)",
    "You heard today's clip say 'umbrelleh'. The first row is today's clip. Every other row is Lily's voice from "
    "VoiceStudio, like the new letter sounds, in three wordings. Mark exactly ONE take OK and the others FIX "
    "(FIX all, with a note, if none is right). Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
