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
