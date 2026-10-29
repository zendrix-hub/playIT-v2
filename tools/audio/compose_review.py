"""Review batch: tutor compositions played from RELEASED clips, plus candidate phoneme clips.

Unlike audio_lab.py's `script`, nothing here is synthesized from text. Every line comes from a
docs/audio-release/<date>/ folder, so what the listener approves is exactly what would ship. Only the
phoneme candidates are new: the chosen clip, and versions of it time-stretched with its own pitch
contour kept (sustain.stretch_keep_pitch), to settle the length question (spec Table 4, ~800 ms).

Usage (Kokoro venv: numpy, soundfile, parselmouth):
  python tools/audio/compose_review.py --out <batches>/2026-10-01-compositions-m \
      --phoneme <batches>/2026-09-30-routeA-chatterbox-m/raw/t3_s3.wav --letter m --word mouse \
      --stretch 700 800
"""
import argparse
import pathlib

import numpy as np
import soundfile as sf

from review_page import write_review_page
from sustain import describe, stretch_keep_pitch
from tutor_script import COMPOSE, FRAGMENTS, UI_LINES, expand

REPO = pathlib.Path(__file__).resolve().parents[2]
RELEASES = REPO / "docs" / "audio-release"
SR = 24000
GAP_S = 0.10          # about the MediaPlayer hand-over gap between clips on a phone
PAD_S, FADE_S = 0.05, 0.008


def load(path):
    y, sr = sf.read(path, dtype="float32")
    if y.ndim > 1:
        y = y.mean(axis=1)
    assert sr == SR, f"{path}: {sr} Hz, expected {SR}"
    return y


def released_clips():
    """clipId -> path, newest release wins."""
    out = {}
    for rel in sorted(p for p in RELEASES.iterdir() if p.is_dir()):
        for wav in rel.rglob("*.wav"):
            out[wav.stem] = wav
    return out


def finish(core_audio):
    """Same edit standard as kokoro_local.clean (spec §2.3): fades, RMS -20 dBFS, peak <= -1 dBFS, 50 ms pads."""
    a = np.asarray(core_audio, dtype=np.float32).copy()
    n = int(FADE_S * SR)
    ramp = np.linspace(0, 1, n)
    a[:n] *= ramp
    a[-n:] *= ramp[::-1]
    a *= 10 ** (-20 / 20) / (np.sqrt(np.mean(a ** 2)) + 1e-9)
    a /= max(1.0, np.max(np.abs(a)) / 10 ** (-1 / 20))
    pad = np.zeros(int(PAD_S * SR), dtype=np.float32)
    return np.concatenate([pad, a, pad])


def strip_pads(y, thr=0.02):
    idx = np.where(np.abs(y) > thr * np.max(np.abs(y)))[0]
    return y[idx[0]:idx[-1] + 1]


def says_of(cid, letter, word):
    if cid in FRAGMENTS:
        return FRAGMENTS[cid][0]
    if cid in UI_LINES:
        return UI_LINES[cid][0]
    if cid.startswith("ph_"):
        return f"/{letter}/"
    if cid.startswith("kwslow_"):
        return f"{word} (slow)"
    if cid.startswith("kw_"):
        return word
    return cid


def render(ids, clips, phoneme):
    gap = np.zeros(int(GAP_S * SR), dtype=np.float32)
    parts = []
    for cid in ids:
        if cid.startswith("PAUSE_"):
            parts.append(np.zeros(int(int(cid[6:]) / 1000 * SR), dtype=np.float32))
        elif cid.startswith("ph_"):
            parts += [phoneme, gap]
        else:
            parts += [load(clips[cid]), gap]
    return np.concatenate(parts)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--out", required=True)
    ap.add_argument("--phoneme", required=True, help="the user-approved phoneme take")
    ap.add_argument("--letter", default="m")
    ap.add_argument("--word", default="mouse")
    ap.add_argument("--stretch", type=int, nargs="*", default=[800], help="target sound lengths in ms")
    a = ap.parse_args()

    out = pathlib.Path(a.out)
    (out / "phoneme").mkdir(parents=True, exist_ok=True)
    (out / "sequences").mkdir(exist_ok=True)
    clips = released_clips()
    rows = []

    original = load(a.phoneme)
    core = strip_pads(original)
    variants = {"A_original": original}
    for ms in a.stretch:
        variants[f"stretched_{ms}ms"] = finish(stretch_keep_pitch(core, SR, ms))
    for name, y in variants.items():
        f = f"phoneme/ph_{a.letter}_{name}.wav"
        sf.write(out / f, y, SR)
        sound_ms = round((len(y) / SR - 2 * PAD_S) * 1000)
        rows.append({"file": f, "group": f"1. Which /{a.letter}/ ships? Mark OK on exactly one",
                     "says": f"/{a.letter}/ " + ("as you approved it" if name == "A_original"
                                                 else f"same take, slowed to {sound_ms} ms, pitch shape kept"),
                     "listen_for": "A clear, natural held sound. The spec asks for about 800 ms; teachers check length",
                     "auto_check": f"{sound_ms} ms of sound; {describe(y, SR)}"})
        ids = expand("hearit_sequence", a.letter, a.word)
        f2 = f"sequences/hearit_sequence_{name}.wav"
        seq = render(ids, clips, y)
        sf.write(out / f2, seq, SR)
        rows.append({"file": f2, "group": "2. Hear It, with each /m/ version (in context)",
                     "says": " + ".join(says_of(c, a.letter, a.word) for c in ids if not c.startswith("PAUSE_")),
                     "listen_for": f"With the {name.replace('_', ' ')} sound: does the whole sequence feel right?",
                     "auto_check": f"{len(seq) / SR:.1f} s total"})

    sound_only = ("praise_sound", "remodel_sound")
    names = [n for n in COMPOSE if n not in ("hearit_sequence", "corr_substitution")]
    for name in names:
        modes = [None] if name in sound_only else [a.word, None]
        if name in ("praise_word", "remodel_word"):
            modes = [a.word]
        for word in modes:
            ids = expand(name, a.letter, word)
            missing = [c for c in ids if not (c.startswith(("ph_", "PAUSE_")) or c in clips)]
            if missing:
                print(f"skip {name} ({'word' if word else 'sound'}): not released: {missing}")
                continue
            f = f"sequences/{name}_{'word' if word else 'sound'}.wav"
            y = render(ids, clips, original)
            sf.write(out / f, y, SR)
            rows.append({"file": f, "group": "3. What the app plays in Say It (with the /m/ you approved)",
                         "says": " + ".join(says_of(c, a.letter, word) for c in ids),
                         "listen_for": f"{name}, {'word' if word else 'sound'} mode: flows naturally, kind, clear?",
                         "auto_check": f"{len(y) / SR:.1f} s total"})

    write_review_page(
        out, out.name, f"playIT: /{a.letter}/ length and the Say It compositions (released clips only)",
        "Part 1: mark OK on exactly one /m/ version; that one ships. Part 2 plays Hear It with each version. "
        "Part 3 is what Say It plays after each kind of answer, built only from clips you already approved. "
        "Mark OK or FIX and say what to change. Export CSV when done.", rows, mode="okfix")
    print(f"wrote {len(rows)} clips to {out}")


if __name__ == "__main__":
    main()
