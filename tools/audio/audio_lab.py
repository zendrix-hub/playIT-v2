"""playIT audio lab (local): held-sound method comparison and the tutor-script draft.

  heldsound  Several ways to make one held sound (default /m/) in the chosen voice, plus the
             af_heart reference, so the listener can pick the method used for all 13:
               A  current method: 4 takes (repeats 3-4, speeds 0.7/0.85), the one closest to 800 ms
               B1-B3  wider grid (repeats 2-8, speeds 0.5-1.0), trimmed to the steadiest 800 ms
                      (lowest spectral flux, the notebook's section 5 method); best three
               C  spec Table 5 fallback: cut the sound from the key word and time-stretch to 800 ms
               D  B1 with a gentle clarity EQ (cut below 70 Hz, +4 dB at 1.5-5 kHz)
               R1, R2  af_heart with methods A and B1, for reference
  script     Every tutor_script.py fragment alone, the 26 key words, the slow key word, and every
             composition played out for m/mouse (and f/fish for the substitution cue).
Each batch gets an index.html review page (review_page.py). Review audio only: nothing here
goes into app/src/main/assets/.

Usage:  python audio_lab.py heldsound --out <dir> --model ... --voices ... --espeak-data ... [--vosk ...]
        python audio_lab.py script --out <dir> --phoneme-method B1 ...
"""
import argparse, pathlib
import numpy as np, soundfile as sf
from kokoro_onnx import EspeakConfig, Kokoro
from kokoro_local import CONTINUOUS, KEY_WORDS, SR_OUT, clean, core_ms, synth, vosk_check
from review_page import write_review_page
from tutor_script import COMPOSE, FRAGMENTS, expand
from voice_candidates import parse_candidate

VOICE = "af_heart*0.7+af_bella*0.3"   # user's pick, 2026-09-30 round 2 review
TARGET_MS = 800
GAP_S = 0.15
N_FFT, HOP = 512, 120                 # 5 ms hop at 24 kHz

def trim(a, thr=0.02):
    idx = np.where(np.abs(a) > thr * np.max(np.abs(a)))[0]
    return a[idx[0]:idx[-1] + 1] if len(idx) else a

def spectral_flux(a):
    if len(a) < N_FFT:
        return np.array([1.0])
    frames = np.lib.stride_tricks.sliding_window_view(a, N_FFT)[::HOP] * np.hanning(N_FFT)
    S = np.abs(np.fft.rfft(frames, axis=1))
    S = S / (S.sum(axis=1, keepdims=True) + 1e-9)
    return np.r_[0.0, np.sqrt((np.diff(S, axis=0) ** 2).sum(axis=1))]

def steadiest_window(a, target_ms=TARGET_MS):
    """Returns (segment, mean flux) for the steadiest target_ms window of a trimmed take."""
    a = trim(a)
    n = int(target_ms / 1000 * SR_OUT)
    flux = spectral_flux(a)
    if len(a) <= n:
        return a, float(flux.mean())
    w = max(1, (n - N_FFT) // HOP)
    csum = np.r_[0.0, np.cumsum(flux)]
    means = (csum[w:] - csum[:-w]) / w
    start = int(np.argmin(means[: max(1, len(flux) - w)]))
    return a[start * HOP:start * HOP + n], float(means[start])

def method_a(k, voice, ipa):
    cands = []
    for reps in (3, 4):
        for speed in (0.7, 0.85):
            au, sr = synth(k, ipa + "ː" * reps, voice, speed, phonemes=True)
            au = clean(au, sr)
            cands.append((abs(core_ms(au, sr) - TARGET_MS), au))
    return min(cands, key=lambda c: c[0])[1], "repeats 3-4, speeds 0.7/0.85, closest to 800 ms"

def method_b(k, voice, ipa, top=3):
    takes = []
    for reps in (2, 3, 4, 6, 8):
        for speed in (0.5, 0.6, 0.7, 0.8, 1.0):
            au, _ = synth(k, ipa + "ː" * reps, voice, speed, phonemes=True)
            seg, flux = steadiest_window(np.asarray(au, dtype=np.float32))
            ms = 1000 * len(seg) / SR_OUT
            takes.append((flux + abs(ms - TARGET_MS) / 1000, reps, speed, seg))
    takes.sort(key=lambda t: t[0])
    return [(clean(seg, SR_OUT), f"repeats {r}, speed {s}, steadiest 800 ms (score {sc:.3f})")
            for sc, r, s, seg in takes[:top]]

def method_c(k, voice, word):
    import librosa
    au, _ = synth(k, word, voice, 0.85)
    a = trim(np.asarray(au, dtype=np.float32))
    flux = spectral_flux(a)
    min_f = int(0.040 * SR_OUT / HOP)
    thr = flux.mean() + 1.5 * flux.std()
    peaks = [i for i in range(min_f, len(flux) - 1)
             if flux[i] > thr and flux[i] >= flux[i - 1] and flux[i] >= flux[i + 1]]
    end = peaks[0] * HOP if peaks else len(a) // 3
    seg = a[:end]
    core = seg[int(0.2 * len(seg)):int(0.8 * len(seg))]
    rate = (len(core) / SR_OUT) / (TARGET_MS / 1000)
    out = librosa.effects.time_stretch(core, rate=rate) if len(core) > N_FFT else core
    return clean(out, SR_OUT), f"cut from '{word}' (0-{end / SR_OUT * 1000:.0f} ms), stretched x{1 / rate:.1f}"

def clarity_eq(a):
    spec = np.fft.rfft(a)
    f = np.fft.rfftfreq(len(a), 1 / SR_OUT)
    hp = np.clip((f - 40) / 30, 0, 1)                        # fade in 40-70 Hz
    presence = np.interp(f, [0, 1000, 1500, 5000, 7000, SR_OUT / 2], [0, 0, 4, 4, 0, 0])
    return clean(np.fft.irfft(spec * hp * 10 ** (presence / 20), n=len(a)).astype(np.float32), SR_OUT)

def check(a, letter, vm):
    ms = core_ms(a, SR_OUT)
    length = f"core {ms} ms" + ("" if 700 <= ms <= 900 else " (outside 700-900)")
    if vm is None:
        return length
    ipa, name_foil, cv_foil = CONTINUOUS[letter]
    foils = [f for f in (name_foil, cv_foil) if f]
    heard = [w for w, c in vosk_check(a, SR_OUT, foils, vm) if w in foils and c >= 0.6]
    return (f"Vosk heard {heard}; " if heard else "clean; ") + length

def heldsound(k, a, vm, voice):
    letter = a.letter
    ipa, _, _ = CONTINUOUS[letter]
    word = KEY_WORDS[letter]
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    variants = [("A", *method_a(k, voice, ipa))]
    b = method_b(k, voice, ipa)
    variants += [(f"B{i + 1}", au, how) for i, (au, how) in enumerate(b)]
    variants.append(("C", *method_c(k, voice, word)))
    variants.append(("D", clarity_eq(b[0][0]), "B1 + clarity EQ (cut < 70 Hz, +4 dB 1.5-5 kHz)"))
    heart = "af_heart"
    ra, rhow = method_a(k, heart, ipa)
    rb, rbhow = method_b(k, heart, ipa, top=1)[0]
    variants += [("R1", ra, "af_heart, " + rhow), ("R2", rb, "af_heart, " + rbhow)]
    rows = []
    for vid, au, how in variants:
        f = f"ph_{letter}_{vid}.wav"
        sf.write(out / f, au, SR_OUT)
        group = "Reference: pure af_heart" if vid.startswith("R") else "Chosen voice: heart 70 / bella 30"
        rows.append({"file": f, "group": group, "says": f"/{ipa}/ held. Method {vid}: {how}",
                     "listen_for": "Clear, steady, pure sound; no 'em' or 'muh'; about 0.8 s",
                     "auto_check": check(au, letter, vm)})
        print(vid, rows[-1]["auto_check"], "|", how)
    write_review_page(out, out.name, f"playIT held-sound lab: /{ipa}/ ({letter})",
                      "Eight ways to make the same held sound. Score each 1-5; the best method is then "
                      "used for all 13 held sounds. Export CSV when done.", rows, mode="score")

def script(k, a, vm, voice):
    out = pathlib.Path(a.out); (out / "lines").mkdir(parents=True, exist_ok=True)
    (out / "keywords").mkdir(exist_ok=True); (out / "sequences").mkdir(exist_ok=True)
    clips, rows = {}, []
    for cid, (text, speed, source) in FRAGMENTS.items():
        au = clean(*synth(k, text, voice, speed)); clips[cid] = au
        sf.write(out / "lines" / f"{cid}.wav", au, SR_OUT)
        rows.append({"file": f"lines/{cid}.wav", "group": "1. Tutor lines (each alone)", "says": text,
                     "listen_for": f"Clear, warm, natural for a 6-year-old? ({source})", "auto_check": ""})
    for letter, word in KEY_WORDS.items():
        au = clean(*synth(k, word, voice, 0.85)); clips[f"kw_{word}"] = au
        sf.write(out / "keywords" / f"kw_{word}.wav", au, SR_OUT)
        note = ("Teachers check: starts with 'or', not the short o /ɑ/; spec suggests 'octopus'"
                if word == "orange" else "")
        rows.append({"file": f"keywords/kw_{word}.wav", "group": "2. Key words", "says": word,
                     "listen_for": f"Clear word, starts with the {letter} sound?"
                                   + (" (x: sound at the end)" if letter == "x" else ""),
                     "auto_check": note})
    for word in ("mouse", "fish"):
        au = clean(*synth(k, word, voice, 0.65)); clips[f"kwslow_{word}"] = au
        sf.write(out / "keywords" / f"kwslow_{word}.wav", au, SR_OUT)
        rows.append({"file": f"keywords/kwslow_{word}.wav", "group": "2. Key words", "says": f"{word} (slow)",
                     "listen_for": "Slow but still natural (attempt 2 slow model)?", "auto_check": ""})
    # Phoneme placeholders for the sequences, made with the chosen held-sound method.
    for letter in ("m", "f"):
        ipa = CONTINUOUS[letter][0]
        au = method_a(k, voice, ipa)[0] if a.phoneme_method == "A" else method_b(k, voice, ipa, top=1)[0][0]
        clips[f"ph_{letter}"] = au
    gap = np.zeros(int(GAP_S * SR_OUT), dtype=np.float32)
    sound_only = ("praise_sound", "remodel_sound")
    examples = [(n, "m", "mouse") for n in COMPOSE if n not in sound_only + ("corr_substitution",)]
    examples += [(n, "m", None) for n in sound_only]
    examples.append(("corr_substitution", "f", "fish"))
    for name, letter, word in examples:
        ids = expand(name, letter, word)
        parts = []
        for cid in ids:
            if cid.startswith("PAUSE_"):
                parts.append(np.zeros(int(int(cid[6:]) / 1000 * SR_OUT), dtype=np.float32))
            else:
                parts += [clips[cid], gap]
        f = f"sequences/{name}_{letter}_{word or 'sound'}.wav"
        sf.write(out / f, np.concatenate(parts), SR_OUT)
        says = " + ".join(FRAGMENTS[c][0] if c in FRAGMENTS else (f"/{CONTINUOUS[c[3:]][0]}/" if c.startswith("ph_")
                          else c.split("_", 1)[1] if c.startswith("kw") else c) for c in ids)
        rows.append({"file": f, "group": "3. Joined sequences (as the app plays them)", "says": says,
                     "listen_for": f"{name}, {'word' if word else 'sound'} mode: flows naturally, kind, clear?",
                     "auto_check": f"held sound: method {a.phoneme_method} (placeholder until the lab picks one)"})
    write_review_page(out, out.name, "playIT tutor script draft (heart 70 / bella 30)",
                      "Mark each clip OK or FIX and note what to change (wording, speed, tone). Blank means "
                      "not approved. Export CSV when done.", rows, mode="okfix")
    print(f"wrote {len(rows)} clips to {out}")

CONTOURS = {  # (0..1 time, pitch factor); "expressive" is close to the app's older /m/ (range ~170 Hz)
    "flat": ((0, 1.0), (1, 1.0)),
    "arch": ((0, 0.96), (0.35, 1.10), (1, 0.92)),
    "expressive": ((0, 0.92), (0.3, 1.30), (1, 0.86)),
}

def heldsound2(k, a, vm, voice):
    """Round 2 for one voiced held sound: clean voiced core + PSOLA + contour + swell."""
    import librosa
    from sustain import describe, median_f0, psola_sustain, swell, voiced_core
    letter = a.letter
    ipa, _, _ = CONTINUOUS[letter]
    out = pathlib.Path(a.out); (out / "context").mkdir(parents=True, exist_ok=True)
    rows = []
    ref = pathlib.Path(__file__).resolve().parents[2] / f"app/src/main/assets/audio/phonemes/phoneme_{letter}.mp3"
    if ref.exists():
        y, _ = librosa.load(ref, sr=SR_OUT)
        sf.write(out / f"ref_app_{letter}.wav", y, SR_OUT)
        rows.append({"file": f"ref_app_{letter}.wav", "group": "Reference: the app's current clip",
                     "says": f"/{ipa}/ from the app today (Edge TTS 'Ana'; reference only, its license "
                             "does not allow shipping)", "listen_for": "Score it too, as the bar to beat",
                     "auto_check": describe(y, SR_OUT)})
    seeds = []   # (label, take, prefer)
    for ps in (f"{ipa}ːːː!", f"ˈ{ipa}ːːː!", f"{ipa}ːːː", f"ˈ{ipa}ːːː."):
        for sp in (0.6, 0.7, 0.8, 0.9):
            au, _ = synth(k, ps, voice, sp, phonemes=True)
            seeds.append(("ipa", np.asarray(au, dtype=np.float32), "hnr"))
    if letter == "m":
        for ps in ("hˈʌm!", "hˈʌmː!", "hˈʌmːː."):
            for sp in (0.7, 0.85):
                au, _ = synth(k, ps, voice, sp, phonemes=True)
                seeds.append(("hum", np.asarray(au, dtype=np.float32), "last"))
    cores = {}
    for label, take, prefer in seeds:
        r = voiced_core(take, SR_OUT, win_ms=200 if label == "hum" else 250, prefer=prefer)
        if r and (label not in cores or r[1] > cores[label][1]):
            cores[label] = r
    f0 = median_f0(cores["ipa"][0], SR_OUT)
    plan = [("P1", "ipa", "flat", 1.0, 0), ("P2", "ipa", "arch", 1.0, 0),
            ("P3", "ipa", "expressive", 1.0, 0), ("P4", "ipa", "expressive", 1.26, 0),
            ("P5", "ipa", "arch", 1.0, 0.015)]
    if "hum" in cores:
        plan += [("H1", "hum", "arch", 1.0, 0), ("H2", "hum", "expressive", 1.0, 0)]
    made = {}
    for vid, src, contour, shift, vib in plan:
        core = cores[src][0]
        # Target pitch comes from the IPA core: a word-final coda (hum) often ends in creak,
        # which halves its measured pitch.
        y = psola_sustain(core, SR_OUT, target_ms=TARGET_MS, f0=f0 * shift,
                          contour=CONTOURS[contour], vibrato_hz=5 if vib else 0, vibrato_depth=vib)
        y = clean(swell(y, SR_OUT), SR_OUT)
        made[vid] = y
        f = f"ph_{letter}_{vid}.wav"
        sf.write(out / f, y, SR_OUT)
        how = (f"core from {'IPA takes' if src == 'ipa' else 'the end of a stressed hum'} "
               f"(HNR {cores[src][1]:.0f} dB), PSOLA to 800 ms, {contour} pitch"
               + (f" +{12 * np.log2(shift):.0f} semitones" if shift != 1 else "")
               + (", gentle vibrato" if vib else "") + ", loudness swell")
        rows.append({"file": f, "group": "New method: clean voiced core + PSOLA", "says": f"/{ipa}/ {vid}: {how}",
                     "listen_for": "Clear, steady, natural hum; no 'em' or 'muh'",
                     "auto_check": describe(y, SR_OUT) + ("; " + check(y, letter, vm) if vm else "")})
        print(vid, rows[-1]["auto_check"])
    # The same new sound inside two app sequences (P3), to judge it in context.
    lines = {cid: clean(*synth(k, FRAGMENTS[cid][0], voice, FRAGMENTS[cid][1]))
             for cid in ("car_listen", "car_this_letter_says", "car_say_it_with_me", "fb_almost_just", "fb_no_ah", "car_your_turn")}
    word = KEY_WORDS[letter]
    lines[f"kw_{word}"] = clean(*synth(k, word, voice, 0.85))
    lines[f"ph_{letter}"] = made["P3"]
    gap = np.zeros(int(GAP_S * SR_OUT), dtype=np.float32)
    for name in ("hearit_sequence", "corr_added_vowel"):
        parts = []
        for cid in expand(name, letter, word):
            parts += [np.zeros(int(int(cid[6:]) / 1000 * SR_OUT), dtype=np.float32)] if cid.startswith("PAUSE_") else [lines[cid], gap]
        f = f"context/{name}_{letter}_P3.wav"
        sf.write(out / f, np.concatenate(parts), SR_OUT)
        rows.append({"file": f, "group": "In context (uses P3)", "says": " + ".join(expand(name, letter, word)),
                     "listen_for": "Does the sound fit the voice and flow in the sequence?", "auto_check": ""})
    write_review_page(out, out.name, f"playIT held-sound lab, round 2: /{ipa}/ ({letter})",
                      "The new method keeps only the cleanest part of the voice and lengthens it with "
                      "PSOLA, then adds a natural pitch and loudness shape. Score each 1-5, including the "
                      "app's current clip as the bar to beat. Export CSV when done.", rows, mode="score")

def redo(k, a, vm, voice):
    """Alternate takes for lines and key words marked FIX: pick the best take of each."""
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    items = [x for x in a.items.split(",") if x]
    takes = [("mix, speed 0.80", voice, 0.80), ("mix, speed 0.95", voice, 0.95),
             ("mix, speed 1.05", voice, 1.05), ("pure af_heart, speed 0.95", "af_heart", 0.95)]
    rows = []
    for item in items:
        slow = item.startswith("slow:")
        text = FRAGMENTS[item][0] if item in FRAGMENTS else item.split(":", 1)[-1]
        for i, (label, v, sp) in enumerate(takes, 1):
            au = clean(*synth(k, text, v, sp * (0.75 if slow else 1)))
            f = f"{item.replace(':', '_')}_take{i}.wav"
            sf.write(out / f, au, SR_OUT)
            rows.append({"file": f, "group": f"{text}{' (slow)' if slow else ''}", "says": text,
                         "listen_for": "Pick the best take: clear, natural, right sounds",
                         "auto_check": label + (" x0.75 (slow)" if slow else "")})
    write_review_page(out, out.name, "playIT redo: lines and key words marked FIX",
                      "Four takes of each clip you marked FIX. Score each take 1-5 and note what is still "
                      "wrong (speed, a sound, the voice). Export CSV when done.", rows, mode="score")
    print(f"wrote {len(rows)} clips to {out}")

KW_SPEED = 0.95        # redo review 2026-09-30: 0.95 won for 6 of 7 key words and car_listen
KW_SLOW_SPEED = 0.79   # slow model (attempt 2): 1.05 x 0.75 won for slow mouse

def keywords(k, a, vm, voice):
    """All 26 key words at KW_SPEED, plus fish variants (every fish take scored <= 2)."""
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    rows = []
    for letter, word in KEY_WORDS.items():
        au = clean(*synth(k, word, voice, KW_SPEED))
        sf.write(out / f"kw_{word}.wav", au, SR_OUT)
        rows.append({"file": f"kw_{word}.wav", "group": "1. Key words at speed 0.95", "says": word,
                     "listen_for": f"Clear word, starts with the {letter} sound?", "auto_check": ""})
    fish = [("text 'fish', speed 0.95, af_heart", "fish", "af_heart", 0.95, False),
            ("text 'Fish.', speed 0.95", "Fish.", voice, 0.95, False),
            ("text 'Fish!', speed 0.95", "Fish!", voice, 0.95, False),
            ("phonemes fˈɪʃ, speed 0.95", "fˈɪʃ", voice, 0.95, True),
            ("phonemes fˈɪːʃ (longer vowel), speed 0.95", "fˈɪːʃ", voice, 0.95, True),
            ("phonemes fːˈɪʃ (longer f), speed 0.95", "fːˈɪʃ", voice, 0.95, True)]
    for i, (how, text, v, sp, ph) in enumerate(fish, 1):
        vv = parse_candidate(v, k)[1] if isinstance(v, str) else v
        au = clean(*synth(k, text, vv, sp, phonemes=ph))
        sf.write(out / f"fish_v{i}.wav", au, SR_OUT)
        rows.append({"file": f"fish_v{i}.wav", "group": "2. Fish variants", "says": "fish",
                     "listen_for": "Clear 'fish': a hissy /f/ start and a 'sh' end?", "auto_check": how})
    for word in ("mouse", "fish"):
        au = clean(*synth(k, word, voice, KW_SLOW_SPEED))
        sf.write(out / f"kwslow_{word}.wav", au, SR_OUT)
        rows.append({"file": f"kwslow_{word}.wav", "group": "3. Slow key words (speed 0.79)", "says": f"{word} (slow)",
                     "listen_for": "Slow but natural?", "auto_check": ""})
    write_review_page(out, out.name, "playIT key words at the new speed",
                      "All key words at speed 0.95 (your redo winner), fish variants, and the slow words. "
                      "Mark OK or FIX, add a note for FIX. Export CSV when done.", rows, mode="okfix")
    print(f"wrote {len(rows)} clips to {out}")

def ui(k, a, vm, voice):
    """Spoken UI lines (tutor_script.UI_LINES) for the no-reading pass, OK/FIX review."""
    from tutor_script import UI_LINES
    out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
    rows = []
    for cid, (text, speed, source) in UI_LINES.items():
        au = clean(*synth(k, text, voice, speed))
        sf.write(out / f"{cid}.wav", au, SR_OUT)
        rows.append({"file": f"{cid}.wav", "group": "Spoken UI lines (no-reading pass)", "says": text,
                     "listen_for": f"Clear, friendly, natural for a 6-year-old? ({source})", "auto_check": ""})
    write_review_page(out, out.name, "playIT spoken UI lines",
                      "Lines that tell a child who cannot read what to tap next. Mark OK or FIX, add a note "
                      "for FIX (wording, speed, tone). Export CSV when done.", rows, mode="okfix")
    print(f"wrote {len(rows)} clips to {out}")

def main():
    p = argparse.ArgumentParser()
    p.add_argument("batch", choices=["heldsound", "heldsound2", "script", "redo", "keywords", "ui"])
    p.add_argument("--items", default="", help="redo: comma list of fragment ids, words, or slow:<word>")
    p.add_argument("--out", required=True)
    p.add_argument("--voice", default=VOICE)
    p.add_argument("--letter", default="m", help="heldsound: which continuous letter")
    p.add_argument("--phoneme-method", default="B1", choices=["A", "B1"], help="script: held-sound placeholder")
    p.add_argument("--model", default="kokoro-v1.0.onnx")
    p.add_argument("--voices", default="voices-v1.0.bin")
    p.add_argument("--espeak-data", default=None)
    p.add_argument("--vosk", default=None)
    a = p.parse_args()
    k = Kokoro(a.model, a.voices,
               espeak_config=EspeakConfig(data_path=a.espeak_data) if a.espeak_data else None)
    vm = None
    if a.vosk:
        from vosk import Model, SetLogLevel; SetLogLevel(-1); vm = Model(a.vosk)
    _, voice = parse_candidate(a.voice, k)
    {"heldsound": heldsound, "heldsound2": heldsound2, "script": script, "redo": redo, "keywords": keywords, "ui": ui}[a.batch](k, a, vm, voice)

if __name__ == "__main__":
    main()
