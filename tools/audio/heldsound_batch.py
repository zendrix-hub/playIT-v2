"""Held-sound review batch for several letters: Chatterbox takes plus Kokoro methods, one page.

Step 1 (Chatterbox venv):  python chatterbox_lab.py held --out <batch> --letters s,a,i --ref ... --ckpt ...
Step 2 (Kokoro venv):      python heldsound_batch.py --out <batch> --letters s,a,i --model ... --voices ...
                               --espeak-data ... --vosk app/src/main/assets/vosk-model
Step 2 adds, per letter, Kokoro methods A (phoneme input, closest to 800 ms), B1 (steadiest 800 ms window)
and C (cut from the key word, stretched; spec Table 5 fallback), measures every take, and writes
index.html (score mode). Review audio only: nothing here goes into app/src/main/assets/.

Checks per take (spec Table 6, Gate 1 helpers; the listener decides):
  all        sound length, Vosk foils (letter name, added vowel) heard with confidence >= 0.6
  vowels     mean F1/F2 of the middle of the take and the nearest English vowel (Hillenbrand et al. 1995,
             adult female means), plus how far F2 moves (a moving F2 suggests a diphthong such as "ay")
  fricatives voiced share and spectral centroid (/s/ should be voiceless with a high centroid)
"""
import argparse
import json
import pathlib

import numpy as np
import soundfile as sf

from review_page import write_review_page
from sustain import describe

SR = 24000
VOWEL_REF = {  # F1, F2 in Hz, adult female (Hillenbrand et al. 1995)
    "i (heed)": (437, 2761), "ɪ (hid)": (483, 2365), "eɪ (hayed, letter name a)": (536, 2530),
    "ɛ (head)": (731, 2058), "æ (had)": (669, 2349), "ɑ (hod)": (936, 1551), "ʌ (hud)": (753, 1426),
    "ɔ (hawed)": (781, 1136), "oʊ (hoed)": (555, 1035), "ʊ (hood)": (519, 1225), "u (who'd)": (459, 1105),
}
VOWELS = set("aeiou")
FRICATIVES = set("sfvz")


def core_ms(y, thr=0.02):
    idx = np.where(np.abs(y) > thr * np.max(np.abs(y)))[0]
    return 0 if not len(idx) else round((idx[-1] - idx[0]) / SR * 1000)


def formants(y):
    import parselmouth
    s = parselmouth.Sound(np.asarray(y, dtype=np.float64), sampling_frequency=SR)
    f = s.to_formant_burg(time_step=0.01, max_number_of_formants=5, maximum_formant=5500)
    pitch = s.to_pitch_ac(time_step=0.01, pitch_floor=75, pitch_ceiling=500)
    ts = [t for t in pitch.xs() if pitch.get_value_at_time(t) == pitch.get_value_at_time(t)]  # voiced
    if len(ts) < 5:
        return None
    mid = ts[len(ts) // 5: len(ts) - len(ts) // 5] or ts
    f1 = np.array([f.get_value_at_time(1, t) for t in mid])
    f2 = np.array([f.get_value_at_time(2, t) for t in mid])
    ok = ~(np.isnan(f1) | np.isnan(f2))
    if ok.sum() < 3:
        return None
    f1, f2 = f1[ok], f2[ok]
    third = max(1, len(f2) // 3)
    return float(np.median(f1)), float(np.median(f2)), float(np.median(f2[-third:]) - np.median(f2[:third]))


def nearest_vowels(f1, f2):
    d = {k: np.hypot(np.log(f1 / a), np.log(f2 / b)) for k, (a, b) in VOWEL_REF.items()}
    return sorted(d, key=d.get)[:2]


def fricative_check(y):
    import parselmouth
    s = parselmouth.Sound(np.asarray(y, dtype=np.float64), sampling_frequency=SR)
    p = s.to_pitch_ac(time_step=0.01, pitch_floor=75, pitch_ceiling=500).selected_array["frequency"]
    voiced = float((p > 0).mean()) if len(p) else 0.0
    spec = np.abs(np.fft.rfft(y)) ** 2
    freqs = np.fft.rfftfreq(len(y), 1 / SR)
    centroid = float((spec * freqs).sum() / (spec.sum() + 1e-12))
    tail = y[-int(0.12 * SR):]
    tp = parselmouth.Sound(np.asarray(tail, dtype=np.float64), sampling_frequency=SR).to_pitch_ac(
        time_step=0.01, pitch_floor=75, pitch_ceiling=500).selected_array["frequency"]
    ends_voiced = len(tp) and (tp > 0).mean() > 0.5
    return f"voiced {voiced * 100:.0f}%, centroid {centroid / 1000:.1f} kHz" + ("; ends voiced (vowel tail?)" if ends_voiced else "")


def check(y, letter, vm):
    from kokoro_local import CONTINUOUS, vosk_check
    parts = [f"{core_ms(y)} ms of sound"]
    if letter in VOWELS:
        fm = formants(y)
        if fm:
            f1, f2, move = fm
            near = nearest_vowels(f1, f2)
            parts.append(f"F1 {f1:.0f} / F2 {f2:.0f} Hz, closest {near[0]}, then {near[1]}")
            if abs(move) > 300:
                parts.append(f"F2 moves {move:+.0f} Hz (diphthong?)")
    elif letter in FRICATIVES:
        parts.append(fricative_check(y))
    else:
        parts.append(describe(y, SR))
    if vm is not None:
        _, name_foil, cv_foil = CONTINUOUS[letter]
        foils = [f for f in (name_foil, cv_foil) if f]
        heard = [w for w, c in vosk_check(y, SR, foils, vm) if w in foils and c >= 0.6]
        parts.append(f"Vosk heard {heard}" if heard else "Vosk: no foil")
    return "; ".join(parts)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--out", required=True)
    ap.add_argument("--letters", default="s,a,i")
    ap.add_argument("--voice", default="af_heart*0.7+af_bella*0.3")
    ap.add_argument("--model", required=True)
    ap.add_argument("--voices", required=True)
    ap.add_argument("--espeak-data", default=None)
    ap.add_argument("--vosk", default=None)
    a = ap.parse_args()

    from kokoro_onnx import EspeakConfig, Kokoro
    from audio_lab import method_a, method_b, method_c
    from kokoro_local import CONTINUOUS, KEY_WORDS
    from voice_candidates import parse_candidate

    out = pathlib.Path(a.out)
    k = Kokoro(a.model, a.voices, espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    _, voice = parse_candidate(a.voice, k)
    vm = None
    if a.vosk:
        from vosk import Model, SetLogLevel
        SetLogLevel(-1)
        vm = Model(a.vosk)
    cb = []
    if (out / "rows_chatterbox.json").exists():
        cb = json.loads((out / "rows_chatterbox.json").read_text(encoding="utf-8"))

    rows = []
    for letter in a.letters.split(","):
        ipa = CONTINUOUS[letter][0]
        (out / letter).mkdir(parents=True, exist_ok=True)
        for r in (r for r in cb if r["letter"] == letter):
            y, _ = sf.read(out / r["file"], dtype="float32")
            rows.append({"file": r["file"], "group": f"/{ipa}/ ({letter}): Chatterbox, cloned voice",
                         "says": f"text {r['text']!r}, take {r['seed']}",
                         "listen_for": "A pure, steady sound; no letter name, no 'uh' at the end",
                         "auto_check": check(y, letter, vm)})
        kok = [("A", *method_a(k, voice, ipa))]
        au, how = method_b(k, voice, ipa, top=1)[0]
        kok.append(("B1", au, how))
        kok.append(("C", *method_c(k, voice, KEY_WORDS[letter])))
        for vid, au, how in kok:
            f = f"{letter}/kokoro_{vid}.wav"
            sf.write(out / f, au, SR)
            rows.append({"file": f, "group": f"/{ipa}/ ({letter}): Kokoro, the chosen voice",
                         "says": f"method {vid}: {how}",
                         "listen_for": "A pure, steady sound; no letter name, no 'uh' at the end",
                         "auto_check": check(au, letter, vm)})
        print(letter, "done")
    write_review_page(out, out.name, f"playIT held sounds: {a.letters}",
                      "Score each take 1-5. Chatterbox worked for /m/; for vowels the auto-check names the closest "
                      "vowel it measured (for a we want 'had', for i 'hid'). The spec asks for about 800 ms, and "
                      "a good short take can be slowed later, as with /m/. Export CSV when done.", rows, mode="score")
    print(f"wrote {len(rows)} takes to {out}")


if __name__ == "__main__":
    main()
