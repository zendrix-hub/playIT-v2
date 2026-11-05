"""Builds review batch 2026-11-05-name-lines: Kokoro takes of the reworded "Who is playing?" line (user note on
2026-11-05-return-greeting: "tap your picture or like tap the animal or pick your animal") and of Lily's line for the
new name step on the New player screen (user, 2026-11-05: "the child has to type their name"). Review audio only.

Usage: python -I name_lines_batch.py <out dir> <models dir>
"""
import pathlib
import sys

import soundfile as sf

REPO = pathlib.Path(r"C:\Users\Erik\Desktop\playIT-v2")
sys.path.insert(0, str(REPO / "tools" / "audio"))
from review_page import write_review_page  # noqa: E402

OUT, MODELS = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
OUT.mkdir(parents=True, exist_ok=True)
VOICE = "af_heart*0.7+af_bella*0.3"
SPEEDS = (0.95, 1.00)

LINES = [  # (clip id, version, text, where it plays)
    ("ui_who_is_playing", "a", "Who is playing? Tap your animal!", "Who is playing? screen, after the first use"),
    ("ui_who_is_playing", "b", "Who is playing? Pick your animal!", "Who is playing? screen, after the first use"),
    ("ui_type_name", "a", "Now type your name!", "New player screen, once the child has picked an animal"),
    ("ui_type_name", "b", "Now type your name. A grown-up can help!", "New player screen, once the child has picked an animal"),
]

from kokoro_onnx import Kokoro  # noqa: E402
from kokoro_local import SR_OUT, clean, synth  # noqa: E402
from voice_candidates import parse_candidate  # noqa: E402

k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
_, voice = parse_candidate(VOICE, k)
rows = []
for cid, version, text, where in LINES:
    for i, speed in enumerate(SPEEDS, 1):
        au = clean(*synth(k, text, voice, speed))
        name = f"{cid}_{version}{i}.wav"
        sf.write(OUT / name, au, SR_OUT)
        rows.append({"file": name, "group": f"Lily: {cid}", "says": text,
                     "listen_for": f"Warm, clear, easy for a 6-year-old? Matches the words? Plays: {where}.",
                     "auto_check": f"Kokoro {VOICE}, speed {speed:.2f}, {len(au) / SR_OUT:.2f} s"})

write_review_page(
    OUT, OUT.name, "playIT: Who is playing? and the name step",
    "Two lines, each in two wordings. For EACH line (each group), mark exactly ONE take OK and the others FIX "
    "(FIX all, with a note, if none is right). Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
