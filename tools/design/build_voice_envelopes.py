#!/usr/bin/env python3
"""Write the loudness of every spoken clip, for Lily's moving mouth (TalkingLily).

The app plays speech through MediaPlayer, which gives no loudness while it plays. So this tool measures each clip
once: the RMS loudness of every 40 ms, in dB, scaled to 0..9 between 30 dB below the clip's loud parts (0, mouth
closed) and its loud parts (9, mouth wide open). The app reads the digit for the time since the clip started.

Spoken clips: every .mp3 and .wav under app/src/main/assets/audio/, except the sound effects (sfx_*), the dev
placeholders and the speech model. Writes app/src/main/assets/lipsync/voice_envelopes.json:
  {"frameMs": 40, "clips": {"audio/vo/ui/vo_splash_tagline.mp3": "0136899875310...", ...}}

Run it again whenever a clip is added or changed; VoiceEnvelopesTest fails until then.
Usage: python tools/design/build_voice_envelopes.py
Needs NumPy and miniaudio (pip install numpy miniaudio).
"""
from __future__ import annotations

import json
from pathlib import Path

import miniaudio
import numpy as np

REPO = Path(__file__).resolve().parents[2]
ASSETS = REPO / "app" / "src" / "main" / "assets"
OUT = ASSETS / "lipsync" / "voice_envelopes.json"
FRAME_MS = 40
RANGE_DB = 30.0


def is_speech(path: Path) -> bool:
    rel = path.relative_to(ASSETS).as_posix()
    return (path.suffix in (".mp3", ".wav") and rel.startswith("audio/") and not path.name.startswith("sfx_")
            and "_dev_placeholder" not in rel and "vosk-model" not in rel)


def envelope(path: Path) -> str:
    # decode() from bytes: decode_file() cannot open a name such as phoneme_ñ.mp3 on Windows
    decoded = miniaudio.decode(path.read_bytes(), output_format=miniaudio.SampleFormat.FLOAT32, nchannels=1)
    samples = np.asarray(decoded.samples, dtype=np.float32)
    frame = max(1, decoded.sample_rate * FRAME_MS // 1000)
    count = max(1, int(np.ceil(len(samples) / frame)))
    padded = np.zeros(count * frame, dtype=np.float32)
    padded[:len(samples)] = samples
    rms = np.sqrt(np.mean(padded.reshape(count, frame) ** 2, axis=1))
    db = 20 * np.log10(np.maximum(rms, 1e-6))
    loud = np.percentile(db, 95)
    level = np.clip((db - (loud - RANGE_DB)) / RANGE_DB, 0, 1)
    return "".join(str(int(round(v * 9))) for v in level)


def main():
    clips = {p.relative_to(ASSETS).as_posix(): envelope(p) for p in sorted(ASSETS.joinpath("audio").rglob("*"))
             if p.is_file() and is_speech(p)}
    OUT.parent.mkdir(exist_ok=True)
    OUT.write_text(json.dumps({"frameMs": FRAME_MS, "clips": clips}, indent=1) + "\n", encoding="utf-8", newline="\n")
    print(f"Wrote {len(clips)} clips to {OUT.relative_to(REPO)} ({OUT.stat().st_size:,} bytes)")


if __name__ == "__main__":
    main()
