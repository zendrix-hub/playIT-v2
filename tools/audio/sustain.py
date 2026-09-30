"""Held-sound builder for playIT: turn a short, clean stretch of a Kokoro take into a natural
~800 ms held continuous sound (spec §2.2 Table 4, §2.3 Table 5).

Why: Kokoro's phoneme input ("mːːː") gives a held nasal that is voiced only 55-90% of the time
and has a flat pitch and loudness, which listeners rated 1/5 "not clear" (2026-09-30 lab). The
app's older Edge-TTS /m/, rated much better, is fully voiced with a rise-fall pitch and a
loudness swell. So this module:
  1. finds the cleanest voiced window in several takes (Praat pitch + harmonicity, HNR),
  2. lengthens it to the target with Praat's PSOLA (overlap-add), which keeps the voice's
     texture (the phase-vocoder stretch of method C did not),
  3. gives it a natural pitch contour (optional gentle vibrato) and a loudness swell.
Uses praat-parselmouth (GPL-3; a build tool only, nothing of it ships in the app).
"""
import numpy as np
import parselmouth
from parselmouth.praat import call

def _snd(y, sr):
    return parselmouth.Sound(np.asarray(y, dtype=np.float64), sampling_frequency=sr)

def voiced_core(y, sr, win_ms=250, fmin=75, fmax=500, prefer="hnr"):
    """Returns (segment, mean HNR dB, voiced fraction of the take) for the window with the
    highest mean harmonicity in which every pitch frame is voiced; None if there is none.
    prefer="last" takes the last fully voiced window instead (a word-final coda such as the
    /m/ of "hum", where the vowel before it would otherwise win on HNR)."""
    s = _snd(y, sr)
    pitch = s.to_pitch_ac(time_step=0.01, pitch_floor=fmin, pitch_ceiling=fmax)
    f0 = pitch.selected_array["frequency"]
    hnr = s.to_harmonicity_cc(time_step=0.01, minimum_pitch=fmin).values[0]
    n = min(len(f0), len(hnr))
    f0, hnr = f0[:n], hnr[:n]
    voiced = f0 > 0
    w = int(win_ms / 10)
    best = None
    for i in range(0, max(0, n - w)):
        if voiced[i:i + w].all():
            m = float(np.mean(hnr[i:i + w]))
            if prefer == "last" or best is None or m > best[0]:
                best = (m, i)
    if best is None:
        return None
    t0 = pitch.xs()[best[1]] - 0.005
    a, b = int(max(0, t0) * sr), int((t0 + win_ms / 1000) * sr)
    return np.asarray(y[a:b], dtype=np.float32), best[0], float(voiced.mean())

def median_f0(y, sr, fmin=75, fmax=500):
    f0 = _snd(y, sr).to_pitch_ac(pitch_floor=fmin, pitch_ceiling=fmax).selected_array["frequency"]
    f0 = f0[f0 > 0]
    return float(np.median(f0)) if len(f0) else 200.0

def psola_sustain(core, sr, target_ms=800, f0=None, contour=((0, 1.0), (1, 1.0)),
                  vibrato_hz=0.0, vibrato_depth=0.0, fmin=75, fmax=500):
    """PSOLA-lengthens core to target_ms and imposes f0 * contour (list of (0..1 time, factor)),
    optionally with a sine vibrato (depth as a fraction of f0)."""
    s = _snd(core, sr)
    dur = s.get_total_duration()
    f0 = f0 or median_f0(core, sr, fmin, fmax)
    man = call(s, "To Manipulation", 0.01, fmin, fmax)
    dtier = call("Create DurationTier", "dur", 0, dur)
    call(dtier, "Add point", 0, target_ms / 1000 / dur)
    call([man, dtier], "Replace duration tier")
    # Pitch tier times are in the ORIGINAL time axis (before lengthening).
    ptier = call("Create PitchTier", "pitch", 0, dur)
    steps = 60
    for j in range(steps + 1):
        x = j / steps
        factor = float(np.interp(x, [p[0] for p in contour], [p[1] for p in contour]))
        if vibrato_hz:
            factor *= 1 + vibrato_depth * np.sin(2 * np.pi * vibrato_hz * x * target_ms / 1000)
        call(ptier, "Add point", x * dur, f0 * factor)
    call([man, ptier], "Replace pitch tier")
    out = call(man, "Get resynthesis (overlap-add)")
    return np.asarray(out.values[0], dtype=np.float32)

def swell(y, sr, attack_ms=90, peak_at=0.35, end_db=-7.0, release_ms=70):
    """Natural loudness shape: quick rise, peak at peak_at of the clip, gentle decay to end_db,
    then a short release. The app's older /m/ swells about 13 dB; this is gentler."""
    n = len(y)
    t = np.linspace(0, 1, n)
    body = np.interp(t, [0, peak_at, 1], [10 ** (-6 / 20), 1.0, 10 ** (end_db / 20)])
    a, r = int(attack_ms / 1000 * sr), int(release_ms / 1000 * sr)
    edge = np.ones(n)
    edge[:a] = np.sin(np.linspace(0, np.pi / 2, a)) ** 2
    edge[-r:] = np.cos(np.linspace(0, np.pi / 2, r)) ** 2
    return (y * body * edge).astype(np.float32)

def describe(y, sr, fmin=75, fmax=500):
    """Measurements shown next to each clip: voiced %, median f0, f0 range, loudness swell."""
    idx = np.where(np.abs(y) > 0.02 * np.max(np.abs(y)))[0]
    y = np.asarray(y[idx[0]:idx[-1] + 1] if len(idx) else y, dtype=np.float32)
    s = _snd(y, sr)
    f0 = s.to_pitch_ac(time_step=0.01, pitch_floor=fmin, pitch_ceiling=fmax).selected_array["frequency"]
    v = f0[f0 > 0]
    frame = int(0.02 * sr)
    rms = np.array([np.sqrt(np.mean(y[i:i + frame] ** 2)) for i in range(0, max(1, len(y) - frame), frame)])
    rms_db = 20 * np.log10(rms + 1e-9)
    active = rms_db > rms_db.max() - 35
    swell_db = rms_db.max() - np.median(rms_db[active][:5]) if active.any() else 0
    return (f"voiced {100 * len(v) / max(1, len(f0)):.0f}%, f0 {np.median(v) if len(v) else 0:.0f} Hz, "
            f"range {np.ptp(v) if len(v) else 0:.0f} Hz, swell {swell_db:.0f} dB")

def noise_lengthen(seg, sr, factor, win_ms=40, keep_onset_ms=15, seed=0):
    """Lengthens a voiceless fricative (/f s sh/) by overlap-adding random Hann windows taken
    from its steady middle (PSOLA leaves voiceless stretches unchanged). Keeps the first
    keep_onset_ms as is, so the onset still sounds like the original."""
    rng = np.random.default_rng(seed)
    seg = np.asarray(seg, dtype=np.float32)
    target = int(len(seg) * factor)
    n_on = min(int(keep_onset_ms / 1000 * sr), len(seg) // 3)
    w = int(win_ms / 1000 * sr); hop = w // 2
    lo, hi = int(0.2 * len(seg)), int(0.8 * len(seg)) - w
    if hi <= lo:
        return seg
    body_len = max(0, target - n_on)
    out = np.zeros(body_len + w, dtype=np.float32)
    win = np.hanning(w).astype(np.float32)
    for pos in range(0, body_len, hop):
        a = rng.integers(lo, hi)
        out[pos:pos + w] += seg[a:a + w] * win
    rms_src = np.sqrt(np.mean(seg[lo:hi + w] ** 2)) + 1e-9
    out = out[:body_len] * (rms_src / (np.sqrt(np.mean(out[:body_len] ** 2)) + 1e-9))
    return np.concatenate([seg[:n_on], out])

def slow_word(y, sr, factor, fricative_gain_db=0.0, fmin=75, fmax=500):
    """Slows a word while keeping voiceless edges in proportion: the voiceless start and end
    (e.g. /f/ and /sh/ of "fish") are noise-lengthened, the voiced middle is PSOLA-lengthened.
    Stretching only the vowel makes a short /f/ sound like /v/ ("vish", 2026-09-30 review)."""
    y = np.asarray(y, dtype=np.float32)
    idx = np.where(np.abs(y) > 0.02 * np.max(np.abs(y)))[0]   # trim silence first, or it gets
    y = y[idx[0]:idx[-1] + 1] if len(idx) else y              # stretched as part of the /f/
    s = _snd(y, sr)
    pitch = s.to_pitch_ac(time_step=0.005, pitch_floor=fmin, pitch_ceiling=fmax)
    t, f0 = pitch.xs(), pitch.selected_array["frequency"]
    voiced = np.where(f0 > 0)[0]
    if len(voiced) == 0:
        return y
    a, b = int(t[voiced[0]] * sr), int(t[voiced[-1]] * sr)
    head, mid, tail = y[:a], y[a:b], y[b:]
    g = 10 ** (fricative_gain_db / 20)
    mid_long = np.asarray(call(_snd(mid, sr), "Lengthen (overlap-add)", fmin, fmax, factor).values[0], dtype=np.float32)
    parts = [noise_lengthen(head, sr, factor) * g if len(head) > sr * 0.03 else head * g, mid_long,
             noise_lengthen(tail, sr, factor, keep_onset_ms=5) if len(tail) > sr * 0.03 else tail]
    x = int(0.005 * sr)   # 5 ms crossfades between the pieces
    out = parts[0]
    for p in parts[1:]:
        if len(out) > x and len(p) > x:
            ramp = np.linspace(0, 1, x, dtype=np.float32)
            out = np.concatenate([out[:-x], out[-x:] * (1 - ramp) + p[:x] * ramp, p[x:]])
        else:
            out = np.concatenate([out, p])
    return out

def voiceless_runs(y, sr, min_ms=20, fmin=75, fmax=500):
    """(start, end) sample ranges where the clip is noisy but unvoiced (fricatives)."""
    y = np.asarray(y, dtype=np.float32)
    p = _snd(y, sr).to_pitch_ac(time_step=0.005, pitch_floor=fmin, pitch_ceiling=fmax)
    fr = int(0.005 * sr); peak = np.max(np.abs(y)); runs, cur = [], None
    for tt, f in zip(p.xs(), p.selected_array["frequency"]):
        a = int(tt * sr)
        noisy = f == 0 and np.sqrt(np.mean(y[max(0, a - fr):a + fr] ** 2)) > 0.02 * peak
        if noisy and cur is None:
            cur = a
        if not noisy and cur is not None:
            runs.append((cur, a)); cur = None
    if cur is not None:
        runs.append((cur, len(y)))
    return [(a, b) for a, b in runs if (b - a) > min_ms / 1000 * sr]

def splice_onset(word, fric, sr, fric_ms, gain_db=0.0, xfade_ms=8):
    """Cross-splices a voiceless fricative in front of a word whose own onset is too weak
    (Kokoro's "fish" is voiced from its first 2 ms, heard as "vish"). fric is the donor
    fricative (e.g. the final /f/ of "leaf"); its steady middle is used for fric_ms."""
    fric = np.asarray(fric, dtype=np.float32)
    n = int(fric_ms / 1000 * sr)
    if len(fric) < n:
        fric = noise_lengthen(fric, sr, n / len(fric) + 0.01, keep_onset_ms=0)
    mid = (len(fric) - n) // 2
    f = fric[mid:mid + n] * 10 ** (gain_db / 20)
    ramp_in = int(0.012 * sr)
    f[:ramp_in] *= np.linspace(0, 1, ramp_in, dtype=np.float32)   # soft fricative onset
    w = np.asarray(word, dtype=np.float32)
    idx = np.where(np.abs(w) > 0.02 * np.max(np.abs(w)))[0]
    w = w[idx[0]:idx[-1] + 1]
    x = int(xfade_ms / 1000 * sr)
    ramp = np.linspace(0, 1, x, dtype=np.float32)
    return np.concatenate([f[:-x], f[-x:] * (1 - ramp) + w[:x] * ramp, w[x:]])
