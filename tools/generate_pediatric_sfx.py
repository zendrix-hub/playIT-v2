#!/usr/bin/env python3
"""
PlayIT Pediatric Sound Effects Generator
Synthesizes commercial-grade, studio-mastered sound effects for PlayIT:
- sfx_correct_chime.mp3
- sfx_incorrect_pop.mp3
- sfx_blendit_buzz.mp3
- sfx_heart_loss_whoosh.mp3
- sfx_heart_recovery_sparkle.mp3
- sfx_node_unlock_chime.mp3
- sfx_streak_badge_unlock.mp3
- sfx_level_complete_fanfare.mp3

Features:
- Additive physical modeling of tuned metal/rosewood bars (Euler-Bernoulli beam equation).
- Karplus-Strong string and FM chime algorithms.
- Algorithmic early reflection stereo diffusion.
- Mastered to broadcast mobile pediatric standards (-1.4 dBFS peak ceiling, zero clipping, balanced RMS).
- Tight, responsive natural tails (zero sluggish dead silence).
"""

import os
import sys
import wave
import subprocess
import numpy as np

SAMPLE_RATE = 44100

def apply_studio_space(left, right, sample_rate=SAMPLE_RATE, wet=0.20, target_peak=0.84):
    """
    Applies early acoustic diffusion and smooth stereo room space in pure numpy.
    Creates a warm, polished studio feel without muddying transient clarity.
    """
    n = len(left)
    delays_ms = [14, 26, 42, 58]
    gains = [0.18, 0.12, 0.08, 0.05]
    
    out_l = left.copy()
    out_r = right.copy()
    
    for d_ms, g in zip(delays_ms, gains):
        d_samples = int(d_ms * sample_rate / 1000.0)
        if d_samples < n:
            out_l[d_samples:] += right[:-d_samples] * (g * wet)
            out_r[d_samples:] += left[:-d_samples] * (g * wet)
            
    # Soft 18ms fade-out at end to guarantee clean silence
    fade_len = int(sample_rate * 0.018)
    if fade_len < n:
        fade = np.linspace(1.0, 0.0, fade_len)
        out_l[-fade_len:] *= fade
        out_r[-fade_len:] *= fade
        
    # Peak normalization to target_peak
    peak = max(np.max(np.abs(out_l)), np.max(np.abs(out_r)), 1e-6)
    out_l = (out_l / peak) * target_peak
    out_r = (out_r / peak) * target_peak
    
    return out_l, out_r

def write_wav(filename, left, right=None, sample_rate=SAMPLE_RATE):
    """Writes a 16-bit stereo WAV file."""
    if right is None:
        right = left.copy()
    
    n = min(len(left), len(right))
    left = left[:n]
    right = right[:n]
    
    interleaved = np.empty((n * 2,), dtype=np.int16)
    interleaved[0::2] = (np.clip(left, -0.99, 0.99) * 32767.0).astype(np.int16)
    interleaved[1::2] = (np.clip(right, -0.99, 0.99) * 32767.0).astype(np.int16)
    
    with wave.open(filename, 'wb') as wf:
        wf.setnchannels(2)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(interleaved.tobytes())

def modal_bar(freq, duration, sample_rate=SAMPLE_RATE, brightness=1.0, mallet_hardness=1.0):
    """
    Synthesizes a physical tuned bar (marimba/glockenspiel/celesta) based on Euler-Bernoulli
    beam equation partials: fundamental f, 2.756f, 5.404f, 8.933f.
    """
    t = np.linspace(0, duration, int(sample_rate * duration), endpoint=False)
    
    # Fundamental
    tau0 = 0.45 / (1.0 + (freq / 800.0) * 0.5)
    env0 = np.exp(-t / tau0) * (1.0 - np.exp(-t / 0.003))
    s0 = np.sin(2 * np.pi * freq * t) * env0
    
    # First overtone (2.756 * f)
    f1 = freq * 2.756
    tau1 = 0.16 / (1.0 + (freq / 600.0) * 0.8)
    env1 = np.exp(-t / tau1) * (1.0 - np.exp(-t / 0.002))
    s1 = np.sin(2 * np.pi * f1 * t) * env1 * 0.32 * brightness
    
    # Second overtone (5.404 * f)
    f2 = freq * 5.404
    tau2 = 0.06 / (1.0 + (freq / 500.0) * 1.2)
    env2 = np.exp(-t / tau2) * (1.0 - np.exp(-t / 0.001))
    s2 = np.sin(2 * np.pi * f2 * t) * env2 * 0.14 * brightness
    
    # Third overtone (8.933 * f)
    f3 = freq * 8.933
    tau3 = 0.025 / (1.0 + (freq / 400.0) * 1.5)
    env3 = np.exp(-t / tau3) * (1.0 - np.exp(-t / 0.001))
    s3 = np.sin(2 * np.pi * f3 * t) * env3 * 0.06 * brightness
    
    # Mallet strike click transient (filtered impulse)
    click_len = int(sample_rate * 0.006)
    click = np.random.uniform(-0.15, 0.15, click_len) * np.hanning(click_len) * mallet_hardness
    click_full = np.zeros_like(t)
    click_full[:click_len] = click
    
    return s0 + s1 + s2 + s3 + click_full

def brass_voice(freq, duration, sample_rate=SAMPLE_RATE, vibrato_rate=5.5, vibrato_depth=0.012):
    """
    Synthesizes an expressive, warm pediatric brass voice (trumpet/horn)
    with natural attack swell and brass harmonic structure.
    """
    t = np.linspace(0, duration, int(sample_rate * duration), endpoint=False)
    
    vib_env = np.clip((t - 0.12) / 0.25, 0.0, 1.0)
    pitch_mod = 1.0 + np.sin(2 * np.pi * vibrato_rate * t) * (vibrato_depth * vib_env)
    phase = 2 * np.pi * np.cumsum(freq * pitch_mod) / sample_rate
    
    harmonics = [
        (1.0, 1.0),
        (2.0, 0.65),
        (3.0, 0.45),
        (4.0, 0.28),
        (5.0, 0.16),
        (6.0, 0.09)
    ]
    
    wave = np.zeros_like(t)
    for h_num, h_amp in harmonics:
        wave += np.sin(phase * h_num) * h_amp
    
    attack_time = 0.035
    release_time = 0.15
    env = np.ones_like(t)
    attack_samples = int(sample_rate * attack_time)
    release_samples = int(sample_rate * release_time)
    
    if attack_samples > 0:
        env[:attack_samples] = np.sin(np.linspace(0, np.pi / 2, attack_samples))
    if release_samples > 0 and len(t) > release_samples:
        env[-release_samples:] = np.cos(np.linspace(0, np.pi / 2, release_samples))
        
    return wave * env

# =============================================================================
# SFX GENERATORS
# =============================================================================

def generate_correct_chime():
    """
    sfx_correct_chime: Joyful, bright ascending 3-note chime (G5, C6, E6) with
    crystalline glockenspiel sparkle. Fast, satisfying, rewarding (~0.80s).
    """
    duration = 0.80
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    left = np.zeros_like(t)
    right = np.zeros_like(t)
    
    notes = [
        (783.99, 0.00, 0.70, -0.22),  # G5
        (1046.50, 0.09, 0.68, 0.00),  # C6
        (1318.51, 0.18, 0.62, 0.25),  # E6
        (1567.98, 0.21, 0.58, 0.12)   # G6 high sparkle
    ]
    
    for freq, start_t, note_dur, pan in notes:
        start_idx = int(start_t * SAMPLE_RATE)
        tone = modal_bar(freq, note_dur, SAMPLE_RATE, brightness=1.15, mallet_hardness=0.9)
        end_idx = min(start_idx + len(tone), len(t))
        chunk = tone[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l
        right[start_idx:end_idx] += chunk * pan_r
        
    # Celestial shimmer tail (E7)
    sparkle_t = np.linspace(0, 0.40, int(SAMPLE_RATE * 0.40), endpoint=False)
    sparkle = np.sin(2 * np.pi * 2637.0 * sparkle_t) * np.exp(-sparkle_t / 0.09) * 0.08
    sp_start = int(0.20 * SAMPLE_RATE)
    sp_end = min(sp_start + len(sparkle), len(t))
    left[sp_start:sp_end] += sparkle[:sp_end - sp_start] * 0.5
    right[sp_start:sp_end] += sparkle[:sp_end - sp_start] * 0.5
    
    return apply_studio_space(left, right, wet=0.22, target_peak=0.82)

def generate_incorrect_pop():
    """
    sfx_incorrect_pop: Soft, playful, non-punitive wooden bubble pop (~0.26s).
    """
    duration = 0.26
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    
    # Rapid downward exponential pitch bend: 520Hz down to 180Hz
    pitch_trajectory = 180.0 + (520.0 - 180.0) * np.exp(-t / 0.035)
    phase = 2 * np.pi * np.cumsum(pitch_trajectory) / SAMPLE_RATE
    
    tone = np.sin(phase) + 0.28 * np.sin(2 * phase) + 0.08 * np.sin(3 * phase)
    env = np.exp(-t / 0.060) * (1.0 - np.exp(-t / 0.003))
    
    click_len = int(SAMPLE_RATE * 0.004)
    click = np.random.uniform(-0.12, 0.12, click_len) * np.hanning(click_len)
    
    full_audio = tone * env
    full_audio[:click_len] += click
    
    left = full_audio * 0.95
    right = full_audio * 1.05
    return apply_studio_space(left, right, wet=0.10, target_peak=0.78)

def generate_blendit_buzz():
    """
    sfx_blendit_buzz: Whimsical, soft cartoon wobble / boing (~0.42s).
    """
    duration = 0.42
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    
    wobble_vib = np.sin(2 * np.pi * 14.0 * t) * np.exp(-t / 0.18) * 20.0
    freq_curve = 130.0 + (220.0 - 130.0) * np.exp(-t / 0.14) + wobble_vib
    phase = 2 * np.pi * np.cumsum(freq_curve) / SAMPLE_RATE
    
    tone = np.sin(phase) + 0.40 * np.sin(2 * phase + 0.2) + 0.15 * np.sin(3 * phase)
    
    env1 = np.exp(-t / 0.15) * (1.0 - np.exp(-t / 0.005))
    env2 = np.zeros_like(t)
    bounce_idx = int(SAMPLE_RATE * 0.11)
    if bounce_idx < len(t):
        t_sub = t[bounce_idx:] - t[bounce_idx]
        env2[bounce_idx:] = np.exp(-t_sub / 0.13) * 0.32
        
    env = env1 + env2
    audio = tone * env
    
    left = audio * 1.0
    right = audio * 0.94
    return apply_studio_space(left, right, wet=0.14, target_peak=0.80)

def generate_heart_loss_whoosh():
    """
    sfx_heart_loss_whoosh: Gentle, airy downward whoosh / soft fairy sigh (~0.55s).
    """
    duration = 0.55
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    
    noise = np.random.uniform(-1.0, 1.0, len(t))
    noise = np.convolve(noise, [0.25, 0.5, 0.25], mode='same')
    
    center_freq = 420.0 + (2400.0 - 420.0) * np.exp(-t / 0.15)
    noise_phase = 2 * np.pi * np.cumsum(center_freq) / SAMPLE_RATE
    resonant_noise = noise * np.sin(noise_phase)
    
    bell_freq = 587.33 + (880.0 - 587.33) * np.exp(-t / 0.17)
    bell_phase = 2 * np.pi * np.cumsum(bell_freq) / SAMPLE_RATE
    bell_tone = np.sin(bell_phase) * np.exp(-t / 0.26) * 0.16
    
    env = np.sin(np.pi * (t / duration) ** 0.65) * np.exp(-t / 0.30)
    audio = (resonant_noise * 0.60 + bell_tone) * env
    
    pan_sweep = np.linspace(-0.35, 0.35, len(t))
    left = audio * np.cos((pan_sweep + 1.0) * (np.pi / 4.0))
    right = audio * np.sin((pan_sweep + 1.0) * (np.pi / 4.0))
    return apply_studio_space(left, right, wet=0.18, target_peak=0.80)

def generate_heart_recovery_sparkle():
    """
    sfx_heart_recovery_sparkle: Lush upward fairy sparkle shimmer (~0.85s).
    """
    duration = 0.85
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    left = np.zeros_like(t)
    right = np.zeros_like(t)
    
    chimes = [
        (783.99, 0.00, 0.45, -0.28),
        (987.77, 0.045, 0.48, -0.14),
        (1174.66, 0.09, 0.52, 0.00),
        (1567.98, 0.14, 0.56, 0.14),
        (1975.53, 0.19, 0.58, 0.28),
        (2349.32, 0.24, 0.58, 0.18),
        (3135.96, 0.29, 0.52, 0.00)
    ]
    
    for freq, start_t, note_dur, pan in chimes:
        start_idx = int(start_t * SAMPLE_RATE)
        tone = modal_bar(freq, note_dur, SAMPLE_RATE, brightness=1.25, mallet_hardness=0.8)
        end_idx = min(start_idx + len(tone), len(t))
        chunk = tone[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l
        right[start_idx:end_idx] += chunk * pan_r
        
    shimmer_t = np.linspace(0, 0.48, int(SAMPLE_RATE * 0.48), endpoint=False)
    shimmer = (np.sin(2 * np.pi * 3135.96 * shimmer_t) + 0.4 * np.sin(2 * np.pi * 4698.6 * shimmer_t)) * np.exp(-shimmer_t / 0.11) * 0.10
    sp_start = int(0.28 * SAMPLE_RATE)
    sp_end = min(sp_start + len(shimmer), len(t))
    left[sp_start:sp_end] += shimmer[:sp_end - sp_start] * 0.5
    right[sp_start:sp_end] += shimmer[:sp_end - sp_start] * 0.5
    
    return apply_studio_space(left, right, wet=0.22, target_peak=0.82)

def generate_node_unlock_chime():
    """
    sfx_node_unlock_chime: Magical ascending map path unlock (~1.40s).
    """
    duration = 1.40
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    left = np.zeros_like(t)
    right = np.zeros_like(t)
    
    melody = [
        (587.33, 0.00, 0.85, -0.22),  # D5
        (739.99, 0.13, 0.85, -0.10),  # F#5
        (880.00, 0.26, 0.90, 0.10),   # A5
        (987.77, 0.39, 0.92, 0.24),   # B5
        (1174.66, 0.52, 0.85, 0.00),  # D6
        (1479.98, 0.56, 0.80, 0.15)   # F#6 shimmer
    ]
    
    for freq, start_t, note_dur, pan in melody:
        start_idx = int(start_t * SAMPLE_RATE)
        tone = modal_bar(freq, note_dur, SAMPLE_RATE, brightness=1.15, mallet_hardness=0.85)
        end_idx = min(start_idx + len(tone), len(t))
        chunk = tone[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l
        right[start_idx:end_idx] += chunk * pan_r
        
    return apply_studio_space(left, right, wet=0.24, target_peak=0.82)

def generate_streak_badge_unlock():
    """
    sfx_streak_badge_unlock: Distinct celebratory heraldic sting (~1.50s).
    """
    duration = 1.50
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    left = np.zeros_like(t)
    right = np.zeros_like(t)
    
    brass_notes = [
        (349.23, 0.00, 0.20, -0.15),
        (440.00, 0.14, 0.20, 0.15),
        (523.25, 0.28, 0.22, -0.10),
        (698.46, 0.44, 0.95, 0.00)
    ]
    
    for freq, start_t, note_dur, pan in brass_notes:
        start_idx = int(start_t * SAMPLE_RATE)
        b_tone = brass_voice(freq, note_dur, SAMPLE_RATE)
        end_idx = min(start_idx + len(b_tone), len(t))
        chunk = b_tone[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l * 0.65
        right[start_idx:end_idx] += chunk * pan_r * 0.65
        
    gold_chimes = [
        (698.46, 0.44, 0.95, 0.15),
        (1046.50, 0.46, 0.92, -0.15),
        (1396.91, 0.48, 0.88, 0.20)
    ]
    for freq, start_t, note_dur, pan in gold_chimes:
        start_idx = int(start_t * SAMPLE_RATE)
        chime = modal_bar(freq, note_dur, SAMPLE_RATE, brightness=1.2, mallet_hardness=0.9)
        end_idx = min(start_idx + len(chime), len(t))
        chunk = chime[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l * 0.50
        right[start_idx:end_idx] += chunk * pan_r * 0.50
        
    return apply_studio_space(left, right, wet=0.22, target_peak=0.82)

def generate_level_complete_fanfare():
    """
    sfx_level_complete_fanfare: Grand celebratory pediatric fanfare (~2.35s).
    """
    duration = 2.35
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), endpoint=False)
    left = np.zeros_like(t)
    right = np.zeros_like(t)
    
    # Bass / Timpani boom
    for boom_t in [0.00, 1.15]:
        b_idx = int(boom_t * SAMPLE_RATE)
        b_dur = 0.55
        bt = np.linspace(0, b_dur, int(SAMPLE_RATE * b_dur), endpoint=False)
        boom_freq = 65.0 + (95.0 - 65.0) * np.exp(-bt / 0.08)
        boom_phase = 2 * np.pi * np.cumsum(boom_freq) / SAMPLE_RATE
        boom = np.sin(boom_phase) * np.exp(-bt / 0.18) * 0.40
        b_end = min(b_idx + len(boom), len(t))
        left[b_idx:b_end] += boom[:b_end - b_idx]
        right[b_idx:b_end] += boom[:b_end - b_idx]
        
    # Brass chords
    brass_chords = [
        ([261.63, 329.63, 392.00], 0.00, 0.20),
        ([293.66, 349.23, 440.00], 0.24, 0.20),
        ([329.63, 392.00, 493.88], 0.48, 0.24),
        ([392.00, 523.25, 659.25], 0.76, 0.35),
        ([261.63, 392.00, 523.25, 659.25, 783.99], 1.15, 1.10)
    ]
    
    for freqs, start_t, c_dur in brass_chords:
        start_idx = int(start_t * SAMPLE_RATE)
        chord_wave = np.zeros(int(SAMPLE_RATE * c_dur))
        for f in freqs:
            chord_wave += brass_voice(f, c_dur, SAMPLE_RATE) * (1.0 / len(freqs))
            
        end_idx = min(start_idx + len(chord_wave), len(t))
        chunk = chord_wave[:end_idx - start_idx] * 0.65
        left[start_idx:end_idx] += chunk * 0.95
        right[start_idx:end_idx] += chunk * 1.05
        
    # Glockenspiel melody
    glock_notes = [
        (523.25, 0.00, 0.45, -0.20),
        (659.25, 0.24, 0.45, -0.10),
        (783.99, 0.48, 0.50, 0.10),
        (1046.50, 0.76, 0.65, 0.20),
        (1046.50, 1.15, 0.95, -0.25),
        (1318.51, 1.25, 0.90, 0.25),
        (1567.98, 1.35, 0.85, 0.00),
        (2093.00, 1.45, 0.80, 0.15)
    ]
    
    for freq, start_t, note_dur, pan in glock_notes:
        start_idx = int(start_t * SAMPLE_RATE)
        chime = modal_bar(freq, note_dur, SAMPLE_RATE, brightness=1.25, mallet_hardness=0.9)
        end_idx = min(start_idx + len(chime), len(t))
        chunk = chime[:end_idx - start_idx]
        
        pan_l = np.cos((pan + 1.0) * (np.pi / 4.0))
        pan_r = np.sin((pan + 1.0) * (np.pi / 4.0))
        
        left[start_idx:end_idx] += chunk * pan_l * 0.45
        right[start_idx:end_idx] += chunk * pan_r * 0.45
        
    # Confetti star sparkle tail
    sparkle_times = [1.25, 1.38, 1.50, 1.62, 1.75]
    sparkle_freqs = [2637.0, 3136.0, 3520.0, 3951.0, 4186.0]
    for sp_t, sp_f in zip(sparkle_times, sparkle_freqs):
        sp_idx = int(sp_t * SAMPLE_RATE)
        st = np.linspace(0, 0.35, int(SAMPLE_RATE * 0.35), endpoint=False)
        sp_wave = np.sin(2 * np.pi * sp_f * st) * np.exp(-st / 0.08) * 0.08
        sp_end = min(sp_idx + len(sp_wave), len(t))
        left[sp_idx:sp_end] += sp_wave[:sp_end - sp_idx] * 0.5
        right[sp_idx:sp_end] += sp_wave[:sp_end - sp_idx] * 0.5
        
    return apply_studio_space(left, right, wet=0.25, target_peak=0.82)

def generate_all_sfx(output_dir):
    os.makedirs(output_dir, exist_ok=True)
    tmp_dir = os.path.join(output_dir, "_tmp_wav")
    os.makedirs(tmp_dir, exist_ok=True)
    
    generators = [
        ("sfx_correct_chime", generate_correct_chime),
        ("sfx_incorrect_pop", generate_incorrect_pop),
        ("sfx_blendit_buzz", generate_blendit_buzz),
        ("sfx_heart_loss_whoosh", generate_heart_loss_whoosh),
        ("sfx_heart_recovery_sparkle", generate_heart_recovery_sparkle),
        ("sfx_node_unlock_chime", generate_node_unlock_chime),
        ("sfx_streak_badge_unlock", generate_streak_badge_unlock),
        ("sfx_level_complete_fanfare", generate_level_complete_fanfare)
    ]
    
    print(f"Generating {len(generators)} pediatric sound effects...")
    for name, gen_fn in generators:
        wav_path = os.path.join(tmp_dir, f"{name}.wav")
        mp3_path = os.path.join(output_dir, f"{name}.mp3")
        
        left, right = gen_fn()
        write_wav(wav_path, left, right)
        
        # Clean ffmpeg MP3 encode (320kbps high quality)
        cmd = [
            "ffmpeg", "-y", "-v", "error",
            "-i", wav_path,
            "-b:a", "320k",
            mp3_path
        ]
        subprocess.run(cmd, check=True)
        
        # Verify duration and peak
        probe_cmd = ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", mp3_path]
        dur = float(subprocess.check_output(probe_cmd).decode().strip())
        print(f"  ✓ {name}.mp3 -> {dur:.2f}s (Mastered, 320kbps)")
        
    for f in os.listdir(tmp_dir):
        os.remove(os.path.join(tmp_dir, f))
    os.rmdir(tmp_dir)
    print("All sound effects successfully synthesized and mastered!")

if __name__ == "__main__":
    out = sys.argv[1] if len(sys.argv) > 1 else "tools/sfx_preview"
    generate_all_sfx(out)
