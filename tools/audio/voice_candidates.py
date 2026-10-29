"""playIT voice candidates (local). Kokoro-82M via kokoro-onnx (Apache-2.0).

Makes the same four clips in each candidate voice so the voice can be chosen before the full
pack is generated in that voice (kokoro_local.py):
  01_sample.wav             the notebook's section 2 sample (no letter names)
  02_say_it_with_me.wav     car_say_it_with_me
  03_correction_m.wav       fb_added_vowel followed by the held /m/, as the app joins them
  04_held_m.wav             held /m/ (phoneme input, the take closest to 800 ms)
Writes index.html (review_page.py): score each clip in the page, then Export CSV.
Review audio only: nothing here goes into app/src/main/assets/.

A candidate is a voice id ("af_heart") or a mix of voice styles ("af_heart*0.7+af_bella*0.3";
weights are normalized).

Setup and models: see kokoro_local.py.
If Kokoro fails with "Error processing file .../espeak-ng-data/phontab", the espeak-ng data path
is too long for espeak-ng (it keeps a fixed buffer of about 160 characters). Copy
espeakng_loader's espeak-ng-data folder (not a symlink) to a short path and pass --espeak-data.
Usage:  python voice_candidates.py --out <folder outside the repo> --set round2 \
            --model kokoro-v1.0.onnx --voices voices-v1.0.bin [--vosk app/src/main/assets/vosk-model]
"""
import argparse, pathlib
import numpy as np, soundfile as sf
from kokoro_onnx import EspeakConfig, Kokoro
from kokoro_local import FRAGMENTS, SR_OUT, clean, core_ms, synth, vosk_check
from review_page import write_review_page

SETS = {
    # 2026-09-30 first pass
    "round1": ["af_heart", "af_bella", "af_sarah", "af_nicole", "af_sky",
               "am_michael", "am_adam", "am_puck"],
    # 2026-09-30 second pass: the two finalists, the untried US female voices, and mixes
    "round2": ["af_heart", "af_bella",
               "af_alloy", "af_aoede", "af_jessica", "af_kore", "af_nova", "af_river",
               "af_heart*0.7+af_bella*0.3", "af_heart*0.5+af_bella*0.5",
               "af_heart*0.3+af_bella*0.7"],
}
SAMPLE = ("Listen! Here is a mouse, a fan, and a big red ball. "
          "Say it with me! "
          "Yes, you did it all by yourself. "
          "Let's try the next one together.")
GAP_S = 0.15  # silence between a fragment and the sound it introduces
M_FOILS = ["em", "ma", "mm", "mmm", "hmm"]

CLIPS = [  # file, what it says, what to listen for
    ("01_sample.wav", SAMPLE, "Warm, clear, slow enough for a 6-year-old?"),
    ("02_say_it_with_me.wav", FRAGMENTS["car_say_it_with_me"], "Inviting, not rushed?"),
    ("03_correction_m.wav", FRAGMENTS["fb_added_vowel"] + " /m/",
     "Kind, not scolding? Does the /m/ join smoothly?"),
    ("04_held_m.wav", "/m/ (held)", "Pure /m/, no 'em' or 'muh', steady for about 0.8 s?"),
]

def parse_candidate(spec, k):
    """Returns (folder name, voice for Kokoro: an id or a mixed style array)."""
    if "+" not in spec:
        return spec, spec
    parts = [p.split("*") for p in spec.split("+")]
    total = sum(float(w) for _, w in parts)
    style = sum(k.get_voice_style(v) * (float(w) / total) for v, w in parts)
    name = "mix_" + "_".join(f"{v.split('_', 1)[1]}{round(float(w) / total * 100)}" for v, w in parts)
    return name, style.astype(np.float32)

def held_m(k, voice):
    cands = []
    for reps in (3, 4):
        for speed in (0.7, 0.85):
            au, sr = synth(k, "m" + "ː" * reps, voice, speed, phonemes=True)
            au = clean(au, sr)
            cands.append((abs(core_ms(au, sr) - 800), au))
    return min(cands, key=lambda c: c[0])[1]

def auto_check(m, vm):
    if vm is None:
        return ""
    heard = vosk_check(m, SR_OUT, M_FOILS, vm)
    ms = core_ms(m, SR_OUT)
    words = [w for w, c in heard if w != "[unk]" and c >= 0.6]
    length = f"core {ms} ms" + ("" if 700 <= ms <= 900 else " (outside 700-900)")
    if "ma" in words:
        return f"Vosk heard 'ma' (possible vowel tail); {length}"
    if "em" in words:
        return f"Vosk heard 'em' (possible letter name); {length}"
    return f"clean; {length}"

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--out", required=True)
    p.add_argument("--set", default="round2", choices=sorted(SETS))
    p.add_argument("--only", default="", help="comma list of candidates; overrides --set")
    p.add_argument("--model", default="kokoro-v1.0.onnx")
    p.add_argument("--voices", default="voices-v1.0.bin")
    p.add_argument("--espeak-data", default=None, help="espeak-ng-data folder on a short path")
    p.add_argument("--vosk", default=None, help="app Vosk model folder, for the automatic foil check")
    a = p.parse_args()
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    k = Kokoro(a.model, a.voices,
               espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    vm = None
    if a.vosk:
        from vosk import Model, SetLogLevel; SetLogLevel(-1); vm = Model(a.vosk)
    specs = [s for s in (a.only.split(",") if a.only else SETS[a.set]) if s]
    available = set(k.get_voices())
    missing = sorted({v.split("*")[0] for s in specs for v in s.split("+")} - available)
    if missing:
        raise SystemExit(f"voices not in {a.voices}: {missing}")

    gap = np.zeros(int(GAP_S * SR_OUT), dtype=np.float32)
    rows = []
    for spec in specs:
        name, voice = parse_candidate(spec, k)
        d = out / name; d.mkdir(exist_ok=True)
        m = held_m(k, voice)
        sample = clean(*synth(k, SAMPLE, voice, 0.9))
        together = clean(*synth(k, FRAGMENTS["car_say_it_with_me"], voice, 0.9))
        correction = np.concatenate([clean(*synth(k, FRAGMENTS["fb_added_vowel"], voice, 0.9)), gap, m])
        for (fname, _, _), audio in zip(CLIPS, [sample, together, correction, m]):
            sf.write(d / fname, audio, SR_OUT)
        check = auto_check(m, vm)
        for fname, says, listen_for in CLIPS:
            rows.append({"file": f"{name}/{fname}", "group": f"{name}  ({spec})" if name != spec else name,
                         "says": says, "listen_for": listen_for,
                         "auto_check": check if fname == "04_held_m.wav" else ""})
        print(name, check or f"held /m/ core {core_ms(m, SR_OUT)} ms")

    write_review_page(out, batch_id=out.name, title=f"playIT voice candidates: {out.name}",
                      intro="Same four clips per voice. Score each clip 1-5 and add notes; your answers "
                            "save in this browser. When done, click Export CSV and tell Claude.",
                      rows=rows, mode="score")
    print(f"wrote {len(rows)} clips for {len(specs)} candidates to {out}")

if __name__ == "__main__":
    main()
