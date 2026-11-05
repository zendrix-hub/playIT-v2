"""Builds review batch 2026-11-05-return-greeting: Kokoro takes of Lily's welcome-back lines (adviser, 2026-11-05:
"welcome back, I missed you ... do you wish to continue on this letter") and the Profile Select prompt, plus the two
card 31c lines that still wait for an OK. Review audio only: nothing here reaches app assets.

Lines: Kokoro (af_heart*0.7+af_bella*0.3) at speeds 0.95, 0.90 and 1.00, cleaned like every lesson line. No letter
names (tutor_script.py rule): the greeting ends on the letter's own sound clip, so the last group joins take 1 of each
line with the app's /m/ to show how the whole greeting sounds.

Usage: python -I return_greeting_batch.py <out dir> <models dir> [<card 31 batch dir>]
"""
import pathlib
import shutil
import sys

import numpy as np
import soundfile as sf

REPO = pathlib.Path(r"C:\Users\Erik\Desktop\playIT-v2")
sys.path.insert(0, str(REPO / "tools" / "audio"))
from review_page import write_review_page  # noqa: E402

OUT, MODELS = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
CARD31 = pathlib.Path(sys.argv[3]) if len(sys.argv) > 3 else None
OUT.mkdir(parents=True, exist_ok=True)
VOICE = "af_heart*0.7+af_bella*0.3"
SPEEDS = (0.95, 0.90, 1.00)

LINES = {  # clip id: (text, where it plays)
    "ret_welcome_back": ("Welcome back! I missed you.",
                         "Map, when a child who has played before comes back (a later session or day)"),
    "ret_keep_going": ("Do you want to keep going with this sound?",
                       "Right after the welcome; then the next letter's sound plays and two buttons show"),
    "ret_build_words": ("Do you want to build some words?",
                        "Instead of the line above when the chapter's word game is next"),
    "ret_all_done": ("You learned all the sounds! Pick any letter to play again.",
                     "Instead, when every letter is done"),
    "ui_who_is_playing": ("Who is playing? Tap your picture.",
                          "Who is playing? screen, after the first use (the long hello plays only the first time)"),
}
CARD31_LINES = {
    "vo_sayit_word_intro_01": ("Your turn! Say the word.",
                               "Now it's your turn! Say the whole word clearly into the microphone!"),
    "vo_findit_intro_01": ("Find the pictures that start with this sound.",
                           "Can you find all the pictures that start with this sound?"),
}

from kokoro_onnx import Kokoro  # noqa: E402
from kokoro_local import SR_OUT, clean, synth  # noqa: E402
from voice_candidates import parse_candidate  # noqa: E402

k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
_, voice = parse_candidate(VOICE, k)
rows, first_take = [], {}
for cid, (text, where) in LINES.items():
    for i, speed in enumerate(SPEEDS, 1):
        au = clean(*synth(k, text, voice, speed))
        name = f"{cid}_t{i}.wav"
        sf.write(OUT / name, au, SR_OUT)
        first_take.setdefault(cid, au)
        rows.append({"file": name, "group": f"Lily: {text}", "says": text,
                     "listen_for": f"Warm, clear, easy for a 6-year-old? Matches the words exactly? Plays: {where}.",
                     "auto_check": f"Kokoro {VOICE}, speed {speed:.2f}, {len(au) / SR_OUT:.2f} s"})

if CARD31 and CARD31.exists():
    for cid, (text, old) in CARD31_LINES.items():
        for i in (1, 2, 3):
            src = CARD31 / f"{cid}_t{i}.wav"
            if src.exists():
                shutil.copyfile(src, OUT / src.name)
                rows.append({"file": src.name, "group": f"Lily (made Oct 13, still waiting): {text}", "says": text,
                             "listen_for": "Shorter line for the bubble. Clear and natural? Matches the words?",
                             "auto_check": f"from batch {CARD31.name}, take t{i}"})
        ref = REPO / "app/src/main/assets/audio/vo/lesson" / f"{cid}.wav"
        shutil.copyfile(ref, OUT / f"reference_{cid}.wav")
        rows.append({"file": f"reference_{cid}.wav", "group": f"Lily (made Oct 13, still waiting): {text}",
                     "says": old, "listen_for": "What the app says today, for comparison only. Mark FIX.",
                     "auto_check": "current asset"})


def load(path):
    a, sr = sf.read(path, dtype="float32", always_2d=True)
    a = a.mean(axis=1)
    if sr != SR_OUT:
        a = np.interp(np.linspace(0, len(a), int(len(a) * SR_OUT / sr), endpoint=False), np.arange(len(a)), a)
    return a.astype(np.float32)


gap = np.zeros(int(0.35 * SR_OUT), dtype=np.float32)
m = load(REPO / "app/src/main/assets/audio/phonemes/ph_m.wav")
preview = np.concatenate([first_take["ret_welcome_back"], gap, first_take["ret_keep_going"], gap, m])
sf.write(OUT / "preview_whole_greeting.wav", preview, SR_OUT)
rows.append({"file": "preview_whole_greeting.wav", "group": "The whole greeting, as the app will play it",
             "says": "Welcome back! I missed you. / Do you want to keep going with this sound? / (the letter's sound: mmm)",
             "listen_for": "Take 1 of each line joined, with the app's /m/. OK if the order and pauses feel right; "
                           "FIX with a note if not.",
             "auto_check": f"{len(preview) / SR_OUT:.2f} s, 0.35 s gaps"})
shutil.copyfile(REPO / "app/src/main/assets/audio/vo/lesson/vo_welcome_01.wav", OUT / "reference_vo_welcome_01.wav")
rows.append({"file": "reference_vo_welcome_01.wav", "group": "Lily: Who is playing? Tap your picture.",
             "says": "Hi there! I'm so happy you're here. Let's play and learn together!",
             "listen_for": "Today's hello, which will now play only the first time. For comparison only. Mark FIX.",
             "auto_check": "current asset"})

write_review_page(
    OUT, OUT.name, "playIT: Lily's welcome back and shorter lines",
    "The adviser asked that from the second time a child plays, Lily says welcome back and asks whether they want to "
    "keep going. Lily can't say names (the app has no text-to-speech), so the name shows on screen only. "
    "For EACH line, mark exactly ONE take OK and the others FIX (FIX all, with a note, if none is right). "
    "'reference' rows are what the app plays today: mark them FIX. Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
