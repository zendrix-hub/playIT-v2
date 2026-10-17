"""Short vowels in the "a, a, apple" onset style: the vowel at its natural length, cut from the start of the key word.

Why: on 2026-10-08 the user rejected the stretched vowels (vowel_from_keyword.py) as "robotic or buzzy" and "cut off
or choppy", and chose the onset style instead. Nothing here is stretched, which removes the robotic sound. Every
cut ends with a 60-90 ms natural decay instead of stopping at the consonant, which removes the choppy end.
Three onset sources per letter:
  O1  the user-approved key word (docs/audio-release), as it is
  O2  the same word said by Kokoro at speed 0.75
  O3  the same word at speed 0.60
Slowing barely lengthens the vowel itself (2026-10-08: /a/ 140-150 ms, /i/ 80-120 ms); Kokoro mostly slows the
consonants. So O2 and O3 differ in tone and attack, not in length.
Each onset is also played inside the Hear It line in the onset rhythm ("This letter says... a, a, apple").

Usage (Kokoro venv):
  python tools/audio/vowel_onset.py --out <batches>/2026-10-08-vowel-onsets \
      --model ~/.playit-env/models/kokoro/kokoro-v1.0.onnx --voices ~/.playit-env/models/kokoro/voices-v1.0.bin \
      --espeak-data ~/.playit-env/esd
"""
import argparse
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import numpy as np
import soundfile as sf
from kokoro_onnx import EspeakConfig, Kokoro

from compose_review import SR, finish, load, released_clips
from kokoro_local import clean, synth
from review_page import write_review_page
from voice_candidates import parse_candidate
import parselmouth

from vowel_from_keyword import check

VOICE = "af_heart*0.7+af_bella*0.3"
TARGET = {"a": ("apple", "æ (had)"), "i": ("insect", "ɪ (hid)")}
# The onset rhythm: "Listen! This letter says... a ... a ... apple. ... a. Say it with me!"
ONSET_SEQUENCE = ["car_listen", "car_this_letter_says", "PH", "PAUSE_300", "PH", "PAUSE_200", "KW",
                  "PAUSE_600", "PH", "car_say_it_with_me"]
GAP_S = 0.10


def stressed_vowel_span(y):
    """The voiced run that holds the word's loudest frame (the stressed first vowel of apple / insect), from its
    voice onset to the next consonant: the end of the run, or where F2 falls below 75% of its onset value (the
    /n/ of insect). Not simply the first voiced run: at slow speeds Kokoro puts a short 'ee'-like sound before
    the word (2026-10-08, slow 'apple' and 'insect')."""
    s = parselmouth.Sound(np.asarray(y, dtype=np.float64), sampling_frequency=SR)
    pitch = s.to_pitch_ac(time_step=0.01, pitch_floor=75, pitch_ceiling=500)
    fm = s.to_formant_burg(time_step=0.01, max_number_of_formants=5, maximum_formant=5500)
    inten = s.to_intensity(minimum_pitch=100, time_step=0.01)
    ts = pitch.xs()
    voiced = [pitch.get_value_at_time(t) == pitch.get_value_at_time(t) for t in ts]
    loud = [inten.get_value(t) if v else -1e9 for t, v in zip(ts, voiced)]
    peak = int(np.nanargmax(loud))
    i0 = peak
    while i0 > 0 and voiced[i0 - 1]:
        i0 -= 1
    i1 = peak
    while i1 + 1 < len(ts) and voiced[i1 + 1]:
        i1 += 1
    f2_on = np.nanmedian([fm.get_value_at_time(2, ts[i]) for i in range(i0, min(i0 + 5, i1 + 1))])
    end = i1
    for i in range(i0 + 5, i1 + 1):
        f2 = fm.get_value_at_time(2, ts[i])
        if f2 == f2 and f2 < 0.75 * f2_on:
            end = i - 1
            break
    return max(0, int((ts[i0] - 0.015) * SR)), int((ts[end] + 0.005) * SR)


def onset(word_audio, ref, decay_ms):
    """The word's stressed first vowel, from voice onset to the next consonant, with a fade-in and a natural decay."""
    a, b = stressed_vowel_span(word_audio)
    y = np.asarray(word_audio[a:b], dtype=np.float32).copy()
    n_in, n_out = int(0.010 * SR), min(int(decay_ms / 1000 * SR), len(y) // 2)
    y[:n_in] *= np.sin(np.linspace(0, np.pi / 2, n_in)) ** 2
    y[-n_out:] *= np.cos(np.linspace(0, np.pi / 2, n_out)) ** 2
    return finish(y)


def sequence(clips, ph, kw):
    gap = np.zeros(int(GAP_S * SR), dtype=np.float32)
    parts = []
    for tok in ONSET_SEQUENCE:
        if tok.startswith("PAUSE_"):
            parts.append(np.zeros(int(int(tok[6:]) / 1000 * SR), dtype=np.float32))
        elif tok == "PH":
            parts += [ph, gap]
        elif tok == "KW":
            parts += [kw, gap]
        else:
            parts += [load(clips[tok]), gap]
    return np.concatenate(parts)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--out", required=True)
    ap.add_argument("--model", default="kokoro-v1.0.onnx")
    ap.add_argument("--voices", default="voices-v1.0.bin")
    ap.add_argument("--espeak-data", default=None)
    a = ap.parse_args()
    k = Kokoro(a.model, a.voices, espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    _, voice = parse_candidate(VOICE, k)
    out = pathlib.Path(a.out)
    clips = released_clips()
    rows, ctx = [], []
    for letter, (word, ref) in TARGET.items():
        (out / letter).mkdir(parents=True, exist_ok=True)
        kw = load(clips[f"kw_{word}"])
        sources = [("O1_from_approved_word", f"start of the approved '{word}', as it is", kw, 70)]
        for speed in (0.75, 0.60):
            w = clean(*synth(k, word, voice, speed))
            sources.append((f"O{2 if speed == 0.75 else 3}_from_slow_word_{int(speed * 100)}",
                            f"start of '{word}' said slowly (speed {speed}), not stretched", w, 90))
        for vid, how, src, decay in sources:
            ph = onset(src, ref, decay)
            f = f"{letter}/{vid}.wav"
            sf.write(out / f, ph, SR)
            rows.append({"file": f, "group": f"/{letter}/ for '{word}': the sound alone", "says": f"/{letter}/, {how}",
                         "listen_for": "Natural, not robotic; ends softly, not cut off. The start of the word, never 'ay' or 'ee'",
                         "auto_check": check(ph, ref) + f"; {decay} ms soft end"})
            seq = sequence(clips, ph, kw)
            f2 = f"{letter}/hearit_{vid}.wav"
            sf.write(out / f2, seq, SR)
            ctx.append({"file": f2, "group": f"/{letter}/ in Hear It, onset style",
                        "says": f"Listen! + This letter says + /{letter}/ + /{letter}/ + {word} + /{letter}/ + Say it with me!",
                        "listen_for": f"With {vid[:2]}: does 'a, a, apple' (here '{letter}, {letter}, {word}') flow and teach the sound?",
                        "auto_check": f"{len(seq) / SR:.1f} s total"})
    write_review_page(
        out, out.name, "playIT: /a/ and /i/, onset style ('a, a, apple')",
        "Your choice (2026-10-08): the vowel at its natural length, taken from the start of the key word. Nothing is "
        "stretched (that was the robotic sound), and every sound ends with a soft natural decay (that was the choppy "
        "end). O1 comes from the word you approved; O2 and O3 from the same word said more slowly. That changes the "
        "tone a little but hardly the length: these vowels are short (about 0.1-0.15 s), as in real speech. Each is "
        "also played in Hear It. Score 1-5 (5 = ship it), add a note, Export CSV.",
        rows + ctx, mode="score")
    print(f"wrote {len(rows) + len(ctx)} rows to {out}")


if __name__ == "__main__":
    main()
