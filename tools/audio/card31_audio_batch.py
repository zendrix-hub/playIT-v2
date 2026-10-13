"""Builds review batch 2026-10-13-card31-audio: Kenney CC0 sound-effect candidates and Kokoro takes of the two
shorter Lily lines (card 31b, 31c). Review audio only: nothing here reaches app assets.

Sound effects: each candidate is read from the Kenney packs, mixed to mono at its own rate (44.1 kHz), trimmed,
capped to the event's length with a 20 ms fade-out, levelled to -26 dBFS RMS with peaks at or under -3 dBFS,
and written as 16-bit WAV. The app's current sound is added as a reference row.
Lines: Kokoro (af_heart*0.7+af_bella*0.3) at speeds 0.95, 0.90 and 1.00, cleaned like every lesson line.

Usage: python -I card31_audio_batch.py <kenney dir> <out dir> <models dir>
"""
import pathlib
import shutil
import sys

import numpy as np
import soundfile as sf

REPO = pathlib.Path(r"C:\Users\Erik\Desktop\playIT-v2")
sys.path.insert(0, str(REPO / "tools" / "audio"))
from review_page import write_review_page  # noqa: E402

KENNEY, OUT, MODELS = (pathlib.Path(p) for p in sys.argv[1:4])
OUT.mkdir(parents=True, exist_ok=True)
IS = "interface-sounds/Audio"
PZ = "music-jingles/Audio/Pizzicato jingles"
ST = "music-jingles/Audio/Steel jingles"
TARGET_RMS, PEAK_MAX = -26.0, -3.0

# event suffix -> (what it is for, max seconds, current app file, candidates)
EVENTS = {
    "correct_chime": ("Correct answer (Find It, Blend It, Say It)", 0.60, "sfx_correct_chime.mp3",
                      [f"{IS}/confirmation_004.ogg", f"{IS}/confirmation_002.ogg", f"{IS}/confirmation_001.ogg"]),
    "incorrect_pop": ("Soft 'not yet' after a wrong answer, then Lily's kind words", 0.25, "sfx_incorrect_pop.mp3",
                      [f"{IS}/drop_002.ogg", f"{IS}/drop_004.ogg", f"{IS}/bong_001.ogg"]),
    "tile_tap": ("Neutral tap: taking a Blend It tile back (NEW, was the 'wrong' pop)", 0.15, None,
                 [f"{IS}/select_002.ogg", f"{IS}/select_001.ogg", f"{IS}/click_003.ogg"]),
    "heart_recovery_sparkle": ("Heart back after 3 right in a row; Blend It hint", 0.60, "sfx_heart_recovery_sparkle.mp3",
                               [f"{IS}/glass_001.ogg", f"{IS}/glass_003.ogg", f"{IS}/confirmation_003.ogg"]),
    "node_unlock_chime": ("Next letter unlocks on the map", 1.00, "sfx_node_unlock_chime.mp3",
                          [f"{PZ}/jingles_PIZZI16.ogg", f"{PZ}/jingles_PIZZI08.ogg", f"{IS}/glass_004.ogg"]),
    "level_complete_fanfare": ("Letter or Blend It group complete", 1.50, "sfx_level_complete_fanfare.mp3",
                               [f"{PZ}/jingles_PIZZI03.ogg", f"{PZ}/jingles_PIZZI07.ogg", f"{ST}/jingles_STEEL12.ogg"]),
    "streak_badge_unlock": ("Daily streak badge", 1.00, "sfx_streak_badge_unlock.mp3",
                            [f"{PZ}/jingles_PIZZI10.ogg", f"{ST}/jingles_STEEL09.ogg", f"{PZ}/jingles_PIZZI04.ogg"]),
}

LINES = {
    "vo_sayit_word_intro_01": ("Your turn! Say the word.",
                               "Now it's your turn! Say the whole word clearly into the microphone!"),
    "vo_findit_intro_01": ("Find the pictures that start with this sound.",
                           "Can you find all the pictures that start with this sound?"),
}


def db(x):
    return 20 * np.log10(x + 1e-12)


def prepare(a, sr, max_s):
    """Trim silence (-50 dB from peak), cap the length with a fade-out, level to TARGET_RMS under PEAK_MAX."""
    hop = max(1, int(0.002 * sr))
    env = np.array([np.abs(a[i:i + hop]).max() for i in range(0, len(a), hop)])
    on = np.where(db(env / env.max()) > -50)[0]
    a = a[on[0] * hop:min(len(a), (on[-1] + 1) * hop)]
    cap = int(max_s * sr)
    capped = len(a) > cap
    a = a[:cap].copy()
    fin, fout = min(int(0.003 * sr), len(a) // 4), min(int(0.020 * sr), len(a) // 2)  # a click is only 10 ms
    a[:fin] *= np.linspace(0, 1, fin)
    a[-fout:] *= np.linspace(1, 0, fout)
    rms = np.sqrt(np.mean(a ** 2))
    gain = 10 ** ((TARGET_RMS - db(rms)) / 20)
    peak = np.abs(a).max() * gain
    if db(peak) > PEAK_MAX:
        gain *= 10 ** ((PEAK_MAX - db(peak)) / 20)
    a = a * gain
    return a, capped


rows = []
for event, (what, max_s, current, cands) in EVENTS.items():
    for i, rel in enumerate(cands, 1):
        a, sr = sf.read(KENNEY / rel, dtype="float32", always_2d=True)
        a, capped = prepare(a.mean(axis=1), sr, max_s)
        name = f"sfx_{event}_k{i}.wav"
        sf.write(OUT / name, a, sr, subtype="PCM_16")
        rms, peak = db(np.sqrt(np.mean(a ** 2))), db(np.abs(a).max())
        rows.append({"file": name, "group": f"Sound: {what}", "says": f"(sound) Kenney {pathlib.Path(rel).name}",
                     "listen_for": f"Calm, friendly, not harsh or shrill? Fits this moment? At most {max_s:.2f} s.",
                     "auto_check": f"{len(a) / sr:.2f} s{' (cut, faded)' if capped else ''}, RMS {rms:.1f} dBFS, "
                                   f"peak {peak:.1f} dBFS, {sr} Hz; source {rel} (CC0)"})
    if current:
        shutil.copyfile(REPO / "app/src/main/assets/audio/ui" / current, OUT / f"reference_{current}")
        rows.append({"file": f"reference_{current}", "group": f"Sound: {what}", "says": "(the app's sound today)",
                     "listen_for": "For comparison only. Mark FIX.", "auto_check": "current asset"})

from kokoro_onnx import Kokoro  # noqa: E402
from kokoro_local import SR_OUT, clean, synth  # noqa: E402
from voice_candidates import parse_candidate  # noqa: E402

k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
_, voice = parse_candidate("af_heart*0.7+af_bella*0.3", k)
for cid, (text, old) in LINES.items():
    for i, speed in enumerate((0.95, 0.90, 1.00), 1):
        au = clean(*synth(k, text, voice, speed))
        name = f"{cid}_t{i}.wav"
        sf.write(OUT / name, au, SR_OUT)
        rows.append({"file": name, "group": f"Lily: {text}", "says": text,
                     "listen_for": "Clear, warm, natural for a 6-year-old? Matches the words exactly?",
                     "auto_check": f"Kokoro af_heart*0.7+af_bella*0.3, speed {speed:.2f}, {len(au) / SR_OUT:.2f} s"})
    shutil.copyfile(REPO / "app/src/main/assets/audio/vo/lesson" / f"{cid}.wav", OUT / f"reference_{cid}.wav")
    rows.append({"file": f"reference_{cid}.wav", "group": f"Lily: {text}", "says": old,
                 "listen_for": "The line the app says today, for comparison only. Mark FIX.", "auto_check": "current asset"})

write_review_page(
    OUT, OUT.name, "playIT card 31: calmer sounds and shorter Lily lines",
    "Card 31b: new sound effects from Kenney's free packs (CC0), each levelled to -26 dBFS RMS and kept short. "
    "Card 31c: Lily's Say It and Find It lines cut to 8 words or fewer, so they fit the bubble. "
    "For EACH sound and EACH line, mark exactly ONE take OK and the others FIX (FIX all, with a note, if none is right). "
    "The 'reference' rows are what the app plays today: mark them FIX. Export CSV when done.",
    rows, mode="okfix")
print(f"wrote {len(rows)} rows to {OUT}")
