"""playIT voice candidates (local). Kokoro-82M via kokoro-onnx (Apache-2.0).

Makes the same four clips in each candidate voice so the voice can be chosen before the full
pack is generated in that voice (kokoro_local.py):
  01_sample.wav             the notebook's section 2 sample (no letter names)
  02_say_it_with_me.wav     car_say_it_with_me
  03_correction_m.wav       fb_added_vowel followed by the held /m/, as the app joins them
  04_held_m.wav             held /m/ (phoneme input, the take closest to 800 ms)
Also writes candidates_checklist.csv and index.html (side-by-side listening page).
Review audio only: nothing here goes into app/src/main/assets/.

Setup and models: see kokoro_local.py.
If Kokoro fails with "Error processing file .../espeak-ng-data/phontab", the espeak-ng data path
is too long for espeak-ng (it keeps a fixed buffer of about 160 characters). Copy
espeakng_loader's espeak-ng-data folder (not a symlink) to a short path and pass --espeak-data.
Usage:  python voice_candidates.py --out <folder outside the repo> \
            --model kokoro-v1.0.onnx --voices voices-v1.0.bin
"""
import argparse, csv, html, pathlib
import numpy as np, soundfile as sf
from kokoro_onnx import EspeakConfig, Kokoro
from kokoro_local import FRAGMENTS, SR_OUT, clean, core_ms, synth

VOICES = ["af_heart", "af_bella", "af_sarah", "af_nicole", "af_sky",
          "am_michael", "am_adam", "am_puck"]
SAMPLE = ("Listen! Here is a mouse, a fan, and a big red ball. "
          "Say it with me! "
          "Yes, you did it all by yourself. "
          "Let's try the next one together.")
GAP_S = 0.15  # silence between a fragment and the sound it introduces

CLIPS = [  # file, what to listen for
    ("01_sample.wav", "Warm, clear, slow enough for a 6-year-old?"),
    ("02_say_it_with_me.wav", "Inviting, not rushed?"),
    ("03_correction_m.wav", "Kind, not scolding? Does the /m/ join smoothly?"),
    ("04_held_m.wav", "Pure /m/, no 'em' or 'muh', steady for about 0.8 s?"),
]

def held_m(k, voice):
    cands = []
    for reps in (3, 4):
        for speed in (0.7, 0.85):
            au, sr = synth(k, "m" + "ː" * reps, voice, speed, phonemes=True)
            au = clean(au, sr)
            cands.append((abs(core_ms(au, sr) - 800), au))
    return min(cands, key=lambda c: c[0])[1]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--out", required=True)
    p.add_argument("--model", default="kokoro-v1.0.onnx")
    p.add_argument("--voices", default="voices-v1.0.bin")
    p.add_argument("--only", default="", help="comma list of voices; default all candidates")
    p.add_argument("--espeak-data", default=None, help="espeak-ng-data folder on a short path")
    a = p.parse_args()
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    k = Kokoro(a.model, a.voices,
               espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    available = set(k.get_voices())
    voices = [v for v in (a.only.split(",") if a.only else VOICES) if v]
    missing = [v for v in voices if v not in available]
    if missing:
        raise SystemExit(f"voices not in {a.voices}: {missing}")

    gap = np.zeros(int(GAP_S * SR_OUT), dtype=np.float32)
    rows = []
    for v in voices:
        d = out / v; d.mkdir(exist_ok=True)
        m = held_m(k, v)
        sample = clean(*synth(k, SAMPLE, v, 0.9))
        together = clean(*synth(k, FRAGMENTS["car_say_it_with_me"], v, 0.9))
        correction = np.concatenate([clean(*synth(k, FRAGMENTS["fb_added_vowel"], v, 0.9)), gap, m])
        for (name, _), audio in zip(CLIPS, [sample, together, correction, m]):
            sf.write(d / name, audio, SR_OUT)
        for name, listen_for in CLIPS:
            rows.append({"file": f"{v}/{name}", "voice": v, "listen_for": listen_for,
                         "score_1_to_5": "", "note": ""})
        print(v, f"held /m/ core {core_ms(m, SR_OUT)} ms")

    with open(out / "candidates_checklist.csv", "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=["file", "voice", "listen_for", "score_1_to_5", "note"])
        w.writeheader(); w.writerows(rows)

    head = "".join(f"<th>{html.escape(n[3:-4].replace('_', ' '))}</th>" for n, _ in CLIPS)
    body = "".join(
        f"<tr><td>{v}</td>" + "".join(
            f'<td><audio controls preload="none" src="{v}/{n}"></audio></td>' for n, _ in CLIPS
        ) + "</tr>" for v in voices)
    (out / "index.html").write_text(
        "<!doctype html><meta charset='utf-8'><title>playIT voice candidates</title>"
        "<style>body{font-family:sans-serif}td,th{padding:6px 10px;text-align:left}</style>"
        "<h1>playIT voice candidates</h1><p>Same four clips per voice. Score each in "
        "candidates_checklist.csv.</p>"
        f"<table><tr><th>voice</th>{head}</tr>{body}</table>", encoding="utf-8")
    print(f"wrote {len(rows)} clips for {len(voices)} voices to {out}")

if __name__ == "__main__":
    main()
