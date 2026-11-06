"""Builds review batch 2026-11-06-next-lines: Lily's "tap the button" lines in new words.

The teacher, 2026-11-06: "Tap the big button" is hard for a child, because every button is big. The user asked for
words a child understands ("tap next to continue"). Each line now names the word on the button, and the buttons
say it: Next (Hear It, Say It, Find It, letter and word game complete), Play / Play again (map pop-up).
VoiceStudio takes cloned from Lily's clips (voicestudio_lily.py). Review audio only.

Usage: python -I next_lines_batch.py <out dir>
"""
import pathlib
import sys

import soundfile as sf

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from kokoro_local import SR_OUT, clean  # noqa: E402
from review_page import write_review_page  # noqa: E402
from voicestudio_lily import VOSK, heard, reference, say  # noqa: E402

OUT = pathlib.Path(sys.argv[1])
OUT.mkdir(parents=True, exist_ok=True)

LINES = [  # (clip id, version, text, where it plays, seeds)
    ("ui_hearit_next", "a", "Great listening! Now tap Next.", "end of Hear It; the button says Next", (1, 2)),
    ("ui_hearit_next", "b", "Great listening! Tap Next to keep going.", "end of Hear It; the button says Next", (1, 2)),
    ("ui_findit_next", "a", "You found them all! Now tap Next.", "end of Find It; the button says Next", (1, 2)),
    ("ui_findit_next", "b", "You found them all! Tap Next to keep going.", "end of Find It; the button says Next", (1, 2)),
    ("ui_complete_next", "a", "You did it! Now tap Next.", "letter and word game complete screens; the button says Next", (1, 2)),
    ("ui_complete_next", "b", "You did it! Tap Next to keep going.", "letter and word game complete screens; the button says Next", (1, 2)),
    ("ui_node_start", "a", "Tap Play to start!", "map pop-up of a letter or word game; the button says Play or Play again", (1, 2, 3)),
]

from vosk import Model, SetLogLevel  # noqa: E402

SetLogLevel(-1)
vosk = Model(str(VOSK))
ref_wav, ref_text = reference()
rows = []
for cid, version, text, where, seeds in LINES:
    for seed in seeds:
        a, sr = say(text, seed, ref_wav, ref_text)
        assert sr == SR_OUT, sr
        au = clean(a, sr)
        name = f"{cid}_{version}_vs{seed}.wav"
        sf.write(OUT / name, au, SR_OUT)
        rows.append({"file": name, "group": f"Lily: {cid}", "says": text,
                     "listen_for": f"Sounds like Lily? Clear for a 6-year-old? Plays: {where}.",
                     "auto_check": f"VoiceStudio (OmniVoice, Lily reference), seed {seed}, {len(au) / SR_OUT:.2f} s; "
                                   f"Vosk hears: \"{heard(vosk, au, SR_OUT)}\""})
    print(cid, version, "done", flush=True)

write_review_page(
    OUT, OUT.name, "playIT: tap Next (instead of the big button)",
    "The teacher said \"Tap the big button\" is hard, because every button is big. Each line now names the word on "
    "the button: Next, or Play on the map. Two wordings for the Next lines. For EACH line (each group), mark exactly "
    "ONE take OK and the others FIX (FIX all, with a note, if none is right). Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
