"""playIT audio pipeline (local, no Colab). Kokoro-82M via kokoro-onnx (Apache-2.0).
Generates: voice candidates, tutor/feedback fragments, key words, 13 continuous phonemes.
Runs Gate 1 (length + Vosk foil check using the app's own model). Writes manifest.json.
Short sounds (b c d g h j k p q t w x y) are human recordings; this script only checks them.

Setup:  pip install kokoro-onnx soundfile vosk numpy
Models: https://github.com/thewh1teagle/kokoro-onnx/releases (model-files-v1.0:
        kokoro-v1.0.onnx, voices-v1.0.bin). Use the full-precision model; the int8 one
        sometimes returns empty or clipped audio for short phoneme input.
Usage:  python kokoro_local.py --voice af_heart --out out/ --vosk app/src/main/assets/vosk-model
"""
import argparse, json, datetime, pathlib
import numpy as np, soundfile as sf
from kokoro_onnx import Kokoro

SR_OUT = 24000
PAD_S, FADE_S = 0.05, 0.008
CONTINUOUS = {  # letter: (IPA, letter-name foil, added-vowel foil)
    "a": ("æ", "ay", None), "e": ("ɛ", "ee", None), "i": ("ɪ", "eye", None),
    "o": ("ɑ", "oh", None), "u": ("ʌ", "you", None),
    "f": ("f", "ef", "fa"), "l": ("l", "el", "la"), "m": ("m", "em", "ma"),
    "n": ("n", "en", "na"), "r": ("ɹ", "ar", "ra"), "s": ("s", "es", "sa"),
    "v": ("v", "vee", "va"), "z": ("z", "zee", "za"),
}
KEY_WORDS = {  # from SpeechValidator.wordAcceptedVariants (seeded example words)
    "a": "apple", "b": "ball", "c": "cat", "d": "dog", "e": "elephant", "f": "fish",
    "g": "goat", "h": "hat", "i": "insect", "j": "jug", "k": "kite", "l": "lion",
    "m": "mouse", "n": "nest", "o": "orange", "p": "pig", "q": "queen", "r": "rabbit",
    "s": "sun", "t": "tiger", "u": "umbrella", "v": "van", "w": "watch", "x": "box",
    "y": "yoyo", "z": "zebra",
}
FRAGMENTS = {  # composed with phoneme clips at runtime (spec Table 3, Table 7)
    "car_listen": "Listen!", "car_this_letter_says": "This letter says",
    "car_say_it_with_me": "Say it with me!", "car_your_turn": "Your turn!",
    "car_watch_my_lips": "Watch my lips.", "car_lets_say_together": "Let's say it together.",
    "fb_letter_name": "That's the letter's name. Its sound is",
    "fb_added_vowel": "Almost! Just the sound, no ah. Listen:",
    "fb_listen_again": "Listen again:", "fb_try_later": "Good trying! We'll practice this one again soon.",
    "praise_yes": "Yes!", "praise_great": "Great job!", "praise_lips": "You got it!",
    "idle_reprompt": "Tap the ear to hear it again.",
}

def synth(k, text, voice, speed, phonemes=False, tries=3):
    for _ in range(tries):
        au, sr = k.create(text, voice=voice, speed=speed, lang="en-us", is_phonemes=phonemes, trim=False)
        if len(au) and not np.isnan(au).any() and np.max(np.abs(au)) > 1e-3:
            return au, sr
    raise RuntimeError(f"Kokoro returned empty audio for {text!r}")

def clean(a, sr, thr=0.02):
    a = np.asarray(a, dtype=np.float32)
    idx = np.where(np.abs(a) > thr * np.max(np.abs(a)))[0]
    a = a[idx[0]:idx[-1] + 1]
    n = int(FADE_S * sr); ramp = np.linspace(0, 1, n)
    a[:n] *= ramp; a[-n:] *= ramp[::-1]
    rms = np.sqrt(np.mean(a ** 2)) + 1e-9
    a = a * (10 ** (-20 / 20) / rms)                      # RMS -20 dBFS
    a = a / max(1.0, np.max(np.abs(a)) / 10 ** (-1 / 20))  # peak <= -1 dBFS
    pad = np.zeros(int(PAD_S * sr), dtype=np.float32)
    return np.concatenate([pad, a, pad])

def core_ms(a, sr):
    return round((len(a) / sr - 2 * PAD_S) * 1000)

def vosk_check(a, sr, grammar, model):
    if model is None: return None
    from vosk import KaldiRecognizer
    x = np.interp(np.linspace(0, len(a), int(len(a) * 16000 / sr), endpoint=False), np.arange(len(a)), a)
    r = KaldiRecognizer(model, 16000, json.dumps(grammar + ["[unk]"])); r.SetWords(True)
    r.AcceptWaveform((np.clip(x, -1, 1) * 32767).astype(np.int16).tobytes())
    res = json.loads(r.FinalResult()).get("result", [])
    return [(w["word"], round(w["conf"], 2)) for w in res]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--voice", default="af_heart"); p.add_argument("--out", default="out")
    p.add_argument("--model", default="kokoro-v1.0.onnx"); p.add_argument("--voices", default="voices-v1.0.bin")
    p.add_argument("--vosk", default=None)
    p.add_argument("--only", default="fragments,keywords,phonemes", help="comma list of sections to build")
    p.add_argument("--letters", default="", help="limit phonemes to these letters, e.g. msai")
    a = p.parse_args()
    out = pathlib.Path(a.out); k = Kokoro(a.model, a.voices)
    vm = None
    if a.vosk:
        from vosk import Model, SetLogLevel; SetLogLevel(-1); vm = Model(a.vosk)
    mpath = out / "manifest.json"
    manifest = json.loads(mpath.read_text()) if mpath.exists() else []
    now = datetime.date.today().isoformat()
    only = set(a.only.split(","))
    def entry(cid, typ, letter, ipa, path, audio, gate1, notes=""):
        manifest[:] = [m for m in manifest if m["clipId"] != cid]
        manifest.append({"clipId": cid, "type": typ, "letter": letter, "ipa": ipa,
            "file": str(path.relative_to(out)), "durationMs": round(len(audio) / SR_OUT * 1000),
            "source": "kokoro-82M (kokoro-onnx fp32)", "tool": "kokoro_local.py", "voice": a.voice,
            "license": "Apache-2.0", "gate1": gate1, "gate2": "pending", "gate3": "pending" if typ == "phoneme" else "n/a",
            "version": 1, "released": False, "generated": now, "notes": notes})
    for d in ["fragments", "keywords", "phonemes_continuous/alternates"]: (out / d).mkdir(parents=True, exist_ok=True)
    for cid, text in (FRAGMENTS.items() if "fragments" in only else []):
        au, sr = synth(k, text, a.voice, 0.9); au = clean(au, sr)
        f = out / "fragments" / f"{cid}.wav"; sf.write(f, au, sr); entry(cid, "carrier", None, None, f, au, "n/a")
    for letter, word in (KEY_WORDS.items() if "keywords" in only else []):
        au, sr = synth(k, word, a.voice, 0.85); au = clean(au, sr)
        f = out / "keywords" / f"kw_{word}.wav"; sf.write(f, au, sr)
        note = "Check: 'orange' starts with an r-colored vowel, not short /ɑ/; teachers may prefer 'octopus'." if word == "orange" else ""
        entry(f"kw_{word}", "keyword", letter, None, f, au, "n/a", note)
    for letter, (ipa, name_foil, cv_foil) in (CONTINUOUS.items() if "phonemes" in only else []):
        if a.letters and letter not in a.letters: continue
        cands = []
        for reps in (3, 4):
            for speed in (0.7, 0.85):
                au, sr = synth(k, ipa + "ː" * reps, a.voice, speed, phonemes=True)
                au = clean(au, sr); cands.append((abs(core_ms(au, sr) - 800), reps, speed, au))
        cands.sort(key=lambda c: c[0])
        for rank, (_, reps, speed, au) in enumerate(cands[:3]):
            ms = core_ms(au, SR_OUT); ok_len = 700 <= ms <= 900
            foils = [f for f in (name_foil, cv_foil) if f]
            heard = vosk_check(au, SR_OUT, foils, vm)
            foil_hit = bool(heard) and any(w in foils and c >= 0.6 for w, c in heard)
            gate1 = "pass" if ok_len and not foil_hit else "flag"
            why = []
            if not ok_len: why.append(f"core {ms} ms outside 700-900 band (audit assumption)")
            if foil_hit: why.append(f"Vosk heard foil {heard}: possible vowel tail or letter name")
            name = f"ph_{letter}.wav" if rank == 0 else f"alternates/ph_{letter}_alt{rank}.wav"
            f = out / "phonemes_continuous" / name; sf.write(f, au, SR_OUT)
            entry(f"ph_{letter}" + ("" if rank == 0 else f"_alt{rank}"), "phoneme", letter, f"/{ipa}/", f, au, gate1,
                  f"core {ms} ms; reps={reps}; speed={speed}; vosk={heard}; " + "; ".join(why))
    manifest.sort(key=lambda m: (m["type"], m["clipId"]))
    mpath.write_text(json.dumps(manifest, indent=2, ensure_ascii=False))
    print(f"wrote {len(manifest)} clips to {out}")

if __name__ == "__main__":
    main()
