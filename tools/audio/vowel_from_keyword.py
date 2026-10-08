"""Short-vowel sounds cut from the user-approved key words ("apple" -> /a/, "insect" -> /i/), then lengthened.

Why: on 2026-10-08 the user rejected every Chatterbox and Kokoro vowel take (page 2026-10-01-heldsound-s-a-i);
they drifted to "ah/aw", "eh" or "ee". A vowel cut from the approved word matches that word exactly. Short vowels
are short in speech, so the lengths are 300-600 ms, not the 800 ms of held consonants.

The cut runs from voice onset to the next consonant (the end of the first voiced run, or where F2 falls below 75%
of its onset value, as at the /n/ of "insect"). Every cut fades in and out (no clicks). Praat's overlap-add
lengthens at most 3x per pass, so longer targets use two passes.

Not bit-reproducible: Praat's overlap-add places random pulses in voiceless stretches (the cut starts 15 ms
before voicing), so two runs differ slightly. A release always copies the reviewed file (make_release.py);
never regenerate a clip after the user has approved it.

Usage (Kokoro venv: numpy, soundfile, parselmouth):
  python tools/audio/vowel_from_keyword.py --out <batches>/2026-10-08-vowels-from-keywords
"""
import argparse, pathlib, sys
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import numpy as np, soundfile as sf, parselmouth
from sustain import stretch_keep_pitch, psola_sustain, swell, median_f0
from heldsound_batch import VOWEL_REF, formants, nearest_vowels
from review_page import write_review_page
from compose_review import released_clips, load, render, says_of, finish, SR
from tutor_script import expand

TARGET = {"a": ("apple", "æ (had)"), "i": ("insect", "ɪ (hid)")}


def dist(f1, f2, ref):
    a, b = VOWEL_REF[ref]
    return float(np.hypot(np.log(f1 / a), np.log(f2 / b)))


def vowel_span(y, ref):
    """The word-initial vowel: from voice onset until the next consonant, i.e. the end of the first voiced
    run (apple: the /p/ closure) or the first frame where F2 falls below 75% of its onset value (insect: the
    nasal /n/). Not chosen by formant target: Kokoro's words are heard right in context even when the cut
    vowel measures elsewhere, and the user judges by ear."""
    s = parselmouth.Sound(np.asarray(y, dtype=np.float64), sampling_frequency=SR)
    pitch = s.to_pitch_ac(time_step=0.01, pitch_floor=75, pitch_ceiling=500)
    fm = s.to_formant_burg(time_step=0.01, max_number_of_formants=5, maximum_formant=5500)
    ts = pitch.xs()
    voiced = [pitch.get_value_at_time(t) == pitch.get_value_at_time(t) for t in ts]
    i0 = voiced.index(True)
    i1 = i0
    while i1 + 1 < len(ts) and voiced[i1 + 1]:
        i1 += 1
    f2_on = np.nanmedian([fm.get_value_at_time(2, ts[i]) for i in range(i0, min(i0 + 5, i1))])
    end = i1
    for i in range(i0 + 5, i1 + 1):
        f2 = fm.get_value_at_time(2, ts[i])
        if f2 == f2 and f2 < 0.75 * f2_on:
            end = i - 1
            break
    a = int((ts[i0] - 0.015) * SR)
    b = int((ts[end] + 0.005) * SR)
    return max(0, a), b, (ts[i0], ts[i1])


def stretch(core, ms):
    """stretch_keep_pitch, in two passes when the factor is over 3 (Praat's overlap-add caps one pass at 3x)."""
    f = ms / (len(core) / SR * 1000)
    if f <= 2.9:
        return stretch_keep_pitch(core, SR, ms)
    mid = len(core) / SR * 1000 * np.sqrt(f)
    return stretch_keep_pitch(stretch_keep_pitch(core, SR, mid), SR, ms)


def held(core, ms, f0):
    """psola_sustain with the same two-pass rule."""
    f = ms / (len(core) / SR * 1000)
    if f > 2.9:
        core = stretch_keep_pitch(core, SR, len(core) / SR * 1000 * np.sqrt(f))
    return psola_sustain(core, SR, target_ms=ms, f0=f0, contour=((0, 1.03), (0.4, 1.0), (1, 0.92)))


def fade(y, ms_in=12, ms_out=25):
    y = y.copy()
    a, b = int(ms_in / 1000 * SR), int(ms_out / 1000 * SR)
    y[:a] *= np.sin(np.linspace(0, np.pi / 2, a)) ** 2
    y[-b:] *= np.cos(np.linspace(0, np.pi / 2, b)) ** 2
    return y


def check(y, ref):
    fmv = formants(y)
    if not fmv:
        return "no formants"
    f1, f2, move = fmv
    near = nearest_vowels(f1, f2)
    sound_ms = round((len(y) / SR - 0.1) * 1000)
    return (f"{sound_ms} ms of sound; F1 {f1:.0f} / F2 {f2:.0f} Hz, closest {near[0]}, then {near[1]}"
            + (f"; F2 moves {move:+.0f} Hz" if abs(move) > 300 else "")
            + ("" if near[0] == ref else f"  (target {ref})"))


ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
ap.add_argument("--out", required=True)
OUT = pathlib.Path(ap.parse_args().out)
OUT.mkdir(parents=True, exist_ok=True)
clips = released_clips()
rows, ctx = [], []
for letter, (word, ref) in TARGET.items():
    (OUT / letter).mkdir(exist_ok=True)
    w = load(clips[f"kw_{word}"])
    a, b, run = vowel_span(w, ref)
    core = w[a:b]
    print(f"{letter}: vowel {a / SR * 1000:.0f}-{b / SR * 1000:.0f} ms in kw_{word} "
          f"(voiced run {run[0] * 1000:.0f}-{run[1] * 1000:.0f} ms), {len(core) / SR * 1000:.0f} ms")
    sf.write(OUT / letter / f"00_cut_from_{word}.wav", finish(fade(core)), SR)
    rows.append({"file": f"{letter}/00_cut_from_{word}.wav", "group": f"/{letter}/ cut from the approved '{word}'",
                 "says": f"/{letter}/ as it is inside '{word}', unchanged", "listen_for": "Reference: the vowel at its own length",
                 "auto_check": check(finish(fade(core)), ref)})
    f0 = median_f0(core, SR)
    variants = []
    for ms in (300, 450, 600):
        variants.append((f"stretch_{ms}ms", f"stretched to {ms} ms, its own pitch kept",
                         finish(fade(stretch(core, ms)))))
    for ms in (450, 600):
        y = held(core, ms, f0)
        variants.append((f"held_{ms}ms", f"held {ms} ms, gentle falling pitch and loudness swell",
                         finish(fade(swell(y, SR, attack_ms=40, peak_at=0.3, end_db=-5)))))
    for vid, how, y in variants:
        f = f"{letter}/{vid}.wav"
        sf.write(OUT / f, y, SR)
        rows.append({"file": f, "group": f"/{letter}/ cut from the approved '{word}'", "says": f"/{letter}/ {how}",
                     "listen_for": f"Is it clearly the /{letter}/ of '{word}'? Not the letter name, no 'uh' after it",
                     "auto_check": check(y, ref)})
    for vid, how, y in variants:
        if vid not in ("stretch_450ms", "held_450ms"):
            continue
        ids = expand("hearit_sequence", letter, word)
        missing = [c for c in ids if not (c.startswith(("ph_", "PAUSE_")) or c in clips)]
        if missing:
            print("skip context:", missing)
            continue
        seq = render(ids, clips, y)
        f = f"{letter}/hearit_sequence_{vid}.wav"
        sf.write(OUT / f, seq, SR)
        ctx.append({"file": f, "group": "Hear It, in context (450 ms versions)",
                    "says": " + ".join(says_of(c, letter, word) for c in ids if not c.startswith("PAUSE_")),
                    "listen_for": f"With /{letter}/ {vid.replace('_', ' ')}: does the whole sequence feel right?",
                    "auto_check": f"{len(seq) / SR:.1f} s total"})

write_review_page(
    OUT, OUT.name, "playIT: /a/ and /i/ cut from 'apple' and 'insect'",
    "New method (your decision 2026-10-08): the vowel is cut out of the key word you already approved, so it matches "
    "the word exactly, then lengthened. Short vowels are short in speech, so the lengths are 300-600 ms, not 800. "
    "Score each 1-5 (5 = ship it). Honest note: cut out of 'apple', Kokoro's vowel measures closer to 'ah' (ɑ) than "
    "to the textbook æ, the same as the takes you rejected; inside the word it sounds right. So judge by ear: does it "
    "sound like the start of 'apple' / 'insect'? Never 'ay' or 'ee' (the letter names of A and E). Export CSV when done.", rows + ctx, mode="score")
print(f"wrote {len(rows) + len(ctx)} rows to {OUT}")
