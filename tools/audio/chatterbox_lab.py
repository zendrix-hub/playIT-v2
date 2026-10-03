"""playIT route A: held /m/ from Chatterbox-Turbo (MIT, Resemble AI), cloned to the chosen voice.

Why: Kokoro spells "mmm" as letter names, and its IPA held nasal is flat and breathy (rated 1/5
twice). Chatterbox reads text directly (no espeak G2P), so "Mmmmm." becomes a real hum, the way
the app's older Edge-TTS clip was made, and it can copy a voice from a ~10 s reference.
Note: Chatterbox embeds an inaudible PerTh watermark in every clip (MIT; allowed).

Run in its own venv (torch CPU), made as in tools/dev (see chatterbox_setup notes):
  1. reference (Kokoro venv):  python chatterbox_lab.py ref --out <batch> --model ... --voices ... --espeak-data ...
  2. hums (Chatterbox venv):    python chatterbox_lab.py hums --out <batch> [--vosk app/src/main/assets/vosk-model]
Writes <batch>/index.html (review_page.py, score mode). Review audio only: nothing here goes
into app/src/main/assets/.
  3. held (Chatterbox venv), any held sound, after /m/ worked (user pick 2026-10-01: raw "Mmm!"):
       python chatterbox_lab.py held --out <batch> --letters s,a,i --ref <reference_voice.wav> --ckpt ...
     Writes <batch>/<letter>/cb_t<N>_s<seed>.wav and <batch>/rows_chatterbox.json; heldsound_batch.py
     adds the Kokoro methods, the checks and the review page.
"""
import argparse, pathlib
import numpy as np

SR = 24000
TEXTS = ["Mmmmm.", "Mmmmmmmm...", "Mmm!", "Mmmm, mmmm.", "Mmmmmm, mmmm!"]
SEEDS = [1, 2, 3]
REF_LINES = [  # spoken by the chosen Kokoro voice to make the cloning reference (~12 s)
    "Listen! Here is a mouse, a fan, and a big red ball.",
    "Say it with me! Yes, you did it all by yourself.",
    "Let's try the next one together. Watch my lips. Your turn!",
]

def clean(a, sr=SR, thr=0.02, pad_s=0.05, fade_s=0.008):
    """Same editing standard as kokoro_local.clean (trim, fade, RMS -20 dBFS, peak <= -1, pads)."""
    a = np.asarray(a, dtype=np.float32)
    idx = np.where(np.abs(a) > thr * np.max(np.abs(a)))[0]
    a = a[idx[0]:idx[-1] + 1]
    n = int(fade_s * sr); ramp = np.linspace(0, 1, n)
    a[:n] *= ramp; a[-n:] *= ramp[::-1]
    a = a * (10 ** (-20 / 20) / (np.sqrt(np.mean(a ** 2)) + 1e-9))
    a = a / max(1.0, np.max(np.abs(a)) / 10 ** (-1 / 20))
    pad = np.zeros(int(pad_s * sr), dtype=np.float32)
    return np.concatenate([pad, a, pad])

def vosk_words(a, vm, grammar):
    """Words the app's Vosk model hears in a clip, restricted to grammar (same as kokoro_local)."""
    import json
    from vosk import KaldiRecognizer
    x = np.interp(np.linspace(0, len(a), int(len(a) * 16000 / SR), endpoint=False), np.arange(len(a)), a)
    r = KaldiRecognizer(vm, 16000, json.dumps(grammar + ["[unk]"])); r.SetWords(True)
    r.AcceptWaveform((np.clip(x, -1, 1) * 32767).astype(np.int16).tobytes())
    return [(w["word"], round(w["conf"], 2)) for w in json.loads(r.FinalResult()).get("result", [])]

# Interjection-style texts that a reader says as a held sound, not as a letter name. Vowels have no
# unambiguous spelling, so heldsound_batch.py measures F1/F2 and names the nearest vowel.
HELD_TEXTS = {
    "s": ["Sssss.", "Sssssss...", "Sss!", "Ssss, ssss."],
    "f": ["Fffff.", "Fffffff...", "Fff!"],
    "v": ["Vvvvv.", "Vvvvvvv...", "Vvv!"],
    "z": ["Zzzzz.", "Zzzzzzz...", "Zzz!"],
    "n": ["Nnnnn.", "Nnnnnnn...", "Nnn!"],
    "l": ["Lllll.", "Lllllll...", "Lll!"],
    "r": ["Rrrrr.", "Rrrrrrr...", "Grrr!"],
    "a": ["Aaaa!", "Aaaaaa...", "Aah!", "Aaa, aaa."],
    "e": ["Ehhh.", "Ehhhhh...", "Eh!"],
    "i": ["Ihhh.", "Ihhhhh...", "Ih!", "Ih, ih."],
    "o": ["Ahhh.", "Ahhhhh...", "Ah!"],
    "u": ["Uhhh.", "Uhhhhh...", "Uh!"],
}
HELD_SEEDS = [1, 2]

def held(a):
    import json
    import librosa, soundfile as sf, torch
    from chatterbox.tts_turbo import ChatterboxTurboTTS
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    model = (ChatterboxTurboTTS.from_local(a.ckpt, device="cpu") if a.ckpt
             else ChatterboxTurboTTS.from_pretrained(device="cpu"))
    rows = []
    for letter in a.letters.split(","):
        (out / letter).mkdir(exist_ok=True)
        for ti, text in enumerate(HELD_TEXTS[letter], 1):
            for seed in HELD_SEEDS:
                torch.manual_seed(seed)
                wav = model.generate(text, audio_prompt_path=str(a.ref))
                y = wav.squeeze(0).cpu().numpy().astype(np.float32)
                if model.sr != SR:
                    y = librosa.resample(y, orig_sr=model.sr, target_sr=SR)
                f = f"{letter}/cb_t{ti}_s{seed}.wav"
                sf.write(out / f, clean(y), SR)
                rows.append({"file": f, "letter": letter, "method": "Chatterbox-Turbo", "text": text, "seed": seed})
                print(f, text)
    (out / "rows_chatterbox.json").write_text(json.dumps(rows, indent=1), encoding="utf-8")
    print(f"wrote {len(rows)} Chatterbox takes to {out}")

def make_ref(a):
    import soundfile as sf
    from kokoro_onnx import EspeakConfig, Kokoro
    from kokoro_local import synth
    from voice_candidates import parse_candidate
    k = Kokoro(a.model, a.voices, espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    _, voice = parse_candidate(a.voice, k)
    gap = np.zeros(int(0.35 * SR), dtype=np.float32)
    parts = []
    for line in REF_LINES:
        au, _ = synth(k, line, voice, 0.95)
        parts += [np.asarray(au, dtype=np.float32), gap]
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    y = clean(np.concatenate(parts))
    sf.write(out / "reference_voice.wav", y, SR)
    print(f"reference {len(y) / SR:.1f} s -> {out / 'reference_voice.wav'}")

def hums(a):
    import librosa, soundfile as sf, torch
    from chatterbox.tts_turbo import ChatterboxTurboTTS
    from review_page import write_review_page
    from sustain import describe, median_f0, psola_sustain, swell, voiced_core
    out = pathlib.Path(a.out); (out / "raw").mkdir(parents=True, exist_ok=True); (out / "shaped").mkdir(exist_ok=True)
    ref = out / "reference_voice.wav"
    # --ckpt: a local folder with only the files Turbo loads (from_pretrained would also fetch
    # the unused 1 GB s3gen.safetensors).
    model = (ChatterboxTurboTTS.from_local(a.ckpt, device="cpu") if a.ckpt
             else ChatterboxTurboTTS.from_pretrained(device="cpu"))
    vm = None
    if a.vosk:
        from vosk import Model, SetLogLevel; SetLogLevel(-1); vm = Model(a.vosk)
    rows = []
    app = pathlib.Path(__file__).resolve().parents[2] / "app/src/main/assets/audio/phonemes/phoneme_m.mp3"
    if app.exists():
        y, _ = librosa.load(app, sr=SR)
        sf.write(out / "ref_app_m.wav", y, SR)
        rows.append({"file": "ref_app_m.wav", "group": "Reference: the app's current /m/",
                     "says": "Edge TTS 'Ana', text 'mmm' (reference only; its license does not allow shipping)",
                     "listen_for": "The bar to beat", "auto_check": describe(y, SR)})
    for ti, text in enumerate(TEXTS, 1):
        for seed in SEEDS:
            torch.manual_seed(seed)
            wav = model.generate(text, audio_prompt_path=str(ref))
            y = wav.squeeze(0).cpu().numpy().astype(np.float32)
            if model.sr != SR:
                y = librosa.resample(y, orig_sr=model.sr, target_sr=SR)
            raw = clean(y)
            name = f"t{ti}_s{seed}"
            sf.write(out / "raw" / f"{name}.wav", raw, SR)
            check = describe(raw, SR)
            if vm is not None:
                heard = [w for w, c in vosk_words(raw, vm, ["em", "ma", "mm", "hmm"]) if w != "[unk]" and c >= 0.6]
                check += f"; Vosk heard {heard}" if heard else "; Vosk: nothing"
            rows.append({"file": f"raw/{name}.wav", "group": f"Chatterbox as generated: '{text}'",
                         "says": f"'{text}' take {seed} ({len(raw) / SR:.2f} s)",
                         "listen_for": "A real, clear hum; no 'em' or 'muh'?", "auto_check": check})
            core = voiced_core(y, SR, win_ms=300)
            if core is not None:
                shaped = clean(swell(psola_sustain(core[0], SR, target_ms=800, f0=median_f0(core[0], SR),
                                                   contour=((0, 0.96), (0.35, 1.10), (1, 0.92))), SR))
                sf.write(out / "shaped" / f"{name}.wav", shaped, SR)
                rows.append({"file": f"shaped/{name}.wav", "group": f"Chatterbox shaped to 800 ms: '{text}'",
                             "says": f"'{text}' take {seed}, cleanest 300 ms, PSOLA to 800 ms, gentle arch",
                             "listen_for": "Clear, steady hum at the target length?", "auto_check": describe(shaped, SR)})
            print(name, check)
    write_review_page(out, out.name, "playIT route A: /m/ from Chatterbox (cloned to the chosen voice)",
                      "Chatterbox reads 'Mmmmm.' as a real hum. Score each clip 1-5, including the app's "
                      "current /m/ as the bar to beat. 'As generated' keeps Chatterbox's own length and "
                      "melody; 'shaped' trims it to about 800 ms. Export CSV when done.", rows, mode="score")
    print(f"wrote {len(rows)} clips to {out}")

def main():
    p = argparse.ArgumentParser()
    p.add_argument("step", choices=["ref", "hums", "held"])
    p.add_argument("--out", required=True)
    p.add_argument("--voice", default="af_heart*0.7+af_bella*0.3")
    p.add_argument("--model", default="kokoro-v1.0.onnx")
    p.add_argument("--voices", default="voices-v1.0.bin")
    p.add_argument("--espeak-data", default=None)
    p.add_argument("--vosk", default=None)
    p.add_argument("--ckpt", default=None, help="hums/held: local Chatterbox-Turbo checkpoint folder")
    p.add_argument("--letters", default="s,a,i", help="held: comma-separated letters")
    p.add_argument("--ref", default=None, help="held: voice reference wav (default <out>/reference_voice.wav)")
    a = p.parse_args()
    if a.step == "held" and a.ref is None:
        a.ref = str(pathlib.Path(a.out) / "reference_voice.wav")
    {"ref": make_ref, "hums": hums, "held": held}[a.step](a)

if __name__ == "__main__":
    main()
