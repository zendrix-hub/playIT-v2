"""Check and split a groupmate's letter-sound recordings, then build a listening page for the owner.

The recorder follows docs/tasks/AUDIO_TASK_short-vowels.md: one WAV per sound (a.wav, i.wav, ...), with all the
takes in it and about 2 seconds of silence between takes. This script:
  1. checks each file: format, sample rate, clipping, background noise;
  2. splits it into takes at the silences, so take_01.wav, take_02.wav, ... (nothing else is changed);
  3. writes report.txt (what to fix, if anything) and index.html (a listening page: score 1-5, note, Export CSV).

It uses only the Python standard library, so it runs on any laptop with Python 3.8+ (Windows, macOS, Linux) and no
installs. Put this file and review_page.py (same folder in the repo) side by side.

Usage:
  python check_recordings.py <folder with a.wav, i.wav, ...> --out <output folder>
Then zip the output folder and send it to the project owner.
"""
import argparse
import math
import pathlib
import struct
import sys
import wave

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
try:
    from review_page import write_review_page
except ImportError:
    sys.exit("review_page.py is missing: download it from tools/audio/ into the same folder as this script.")

# Sound id -> (what to say, key word). Must match AUDIO_TASK_short-vowels.md.
SOUNDS = {
    "a": ("/a/, the first sound of 'apple'", "apple"),
    "i": ("/i/, the first sound of 'insect'", "insect"),
    "e": ("/e/, the first sound of 'egg'", "egg"),
    "o": ("/o/, the first sound of 'octopus'", "octopus"),
    "u": ("/u/, the first sound of 'umbrella'", "umbrella"),
}
TAKES_EXPECTED = 13          # 5 short + 5 a bit longer + 3 phrase
FRAME_MS = 10
MIN_GAP_MS = 900             # silence that separates two takes (pauses inside "a, a, apple" are shorter)
MIN_TAKE_MS = 60             # shorter blips are clicks or bumps, not takes
PAD_MS = 120                 # silence kept around each take


def read_wav(path):
    """Returns (mono samples as floats in -1..1, sample rate, bits). Raises a clear error for float WAVs."""
    try:
        w = wave.open(str(path), "rb")
    except wave.Error as e:
        raise ValueError(f"not a 16- or 24-bit PCM WAV ({e}). In Audacity: File > Export > WAV, "
                         f"Encoding 'Signed 16-bit PCM'.")
    with w:
        ch, width, rate, n = w.getnchannels(), w.getsampwidth(), w.getframerate(), w.getnframes()
        raw = w.readframes(n)
    if width == 2:
        ints = struct.unpack(f"<{len(raw) // 2}h", raw)
        scale = 32768.0
    elif width == 3:
        ints = [int.from_bytes(raw[i:i + 3], "little", signed=True) for i in range(0, len(raw), 3)]
        scale = 8388608.0
    elif width == 4:
        ints = struct.unpack(f"<{len(raw) // 4}i", raw)
        scale = 2147483648.0
    else:
        raise ValueError(f"{8 * width}-bit WAV is not supported; export Signed 16-bit PCM.")
    mono = [sum(ints[i:i + ch]) / ch / scale for i in range(0, len(ints), ch)]
    return mono, rate, 8 * width, ch


def db(x):
    return 20 * math.log10(max(x, 1e-9))


def frame_rms(y, rate):
    n = max(1, int(rate * FRAME_MS / 1000))
    return [math.sqrt(sum(s * s for s in y[i:i + n]) / max(1, len(y[i:i + n]))) for i in range(0, len(y), n)], n


def split_takes(y, rate):
    """Speech runs separated by at least MIN_GAP_MS of silence. Returns [(start, end) in samples], floor dB."""
    rms, n = frame_rms(y, rate)
    srt = sorted(rms)
    floor = srt[int(0.10 * len(srt))]
    peak = srt[-1]
    thr = max(floor * 4, peak * 0.05)
    loud = [r > thr for r in rms]
    runs, i = [], 0
    while i < len(loud):
        if loud[i]:
            j = i
            while j < len(loud) and loud[j]:
                j += 1
            runs.append([i, j])
            i = j
        else:
            i += 1
    merged = []
    gap = MIN_GAP_MS // FRAME_MS
    for r in runs:
        if merged and r[0] - merged[-1][1] < gap:
            merged[-1][1] = r[1]
        else:
            merged.append(r)
    pad = PAD_MS // FRAME_MS
    out = [(max(0, (a - pad) * n), min(len(y), (b + pad) * n)) for a, b in merged
           if (b - a) * FRAME_MS >= MIN_TAKE_MS]
    return out, db(floor), db(peak)


def write_wav(path, y, rate):
    with wave.open(str(path), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(rate)
        w.writeframes(struct.pack(f"<{len(y)}h", *(max(-32768, min(32767, int(round(s * 32767)))) for s in y)))


def style_of(k):
    """The order the task asks for: takes 1-5 short, 6-10 a bit longer, 11-13 the phrase."""
    return "short" if k <= 5 else "a bit longer" if k <= 10 else "phrase"


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("folder")
    ap.add_argument("--out", required=True)
    a = ap.parse_args()
    src, out = pathlib.Path(a.folder), pathlib.Path(a.out)
    out.mkdir(parents=True, exist_ok=True)
    report, rows, problems = [], [], 0
    files = {p.stem.lower(): p for p in src.glob("*.wav")}
    unknown = sorted(set(files) - set(SOUNDS))
    if unknown:
        report.append(f"IGNORED (unknown file names, use a.wav, i.wav, e.wav, o.wav, u.wav): {', '.join(unknown)}")
    for sid, (what, word) in SOUNDS.items():
        if sid not in files:
            if sid in ("a", "i"):
                report.append(f"MISSING  {sid}.wav  (required: Chapter 1)")
                problems += 1
            continue
        try:
            y, rate, bits, ch = read_wav(files[sid])
        except ValueError as e:
            report.append(f"FIX      {sid}.wav: {e}")
            problems += 1
            continue
        takes, floor_db, peak_db = split_takes(y, rate)
        clipped = sum(1 for s in y if abs(s) >= 0.99)
        notes = []
        if rate < 22050:
            notes.append(f"FIX: sample rate {rate} Hz is too low; record at 44100 or 48000 Hz")
        if clipped:
            notes.append(f"FIX: {clipped} clipped samples (too loud); move the phone a little further away")
        if floor_db > -45:
            notes.append(f"FIX: background noise {floor_db:.0f} dBFS is too high (want -50 or quieter); find a quieter room")
        elif floor_db > -50:
            notes.append(f"OK but noisy: background {floor_db:.0f} dBFS (best is -50 or quieter)")
        if peak_db < -30:
            notes.append(f"FIX: very quiet (loudest part {peak_db:.0f} dBFS); move closer or raise the input level")
        if len(takes) != TAKES_EXPECTED:
            notes.append(f"CHECK: found {len(takes)} takes, expected {TAKES_EXPECTED}. Leave about 2 seconds of "
                         f"silence between takes; if a take was split or merged, the style labels below may be off")
        problems += sum(1 for x in notes if x.startswith("FIX"))
        report.append(f"{'FIX' if any(x.startswith('FIX') for x in notes) else 'OK':8s} {sid}.wav: {rate} Hz, "
                      f"{bits}-bit, {'stereo->mono' if ch > 1 else 'mono'}, {len(takes)} takes, "
                      f"noise {floor_db:.0f} dBFS, peak {peak_db:.0f} dBFS")
        report += [f"         - {x}" for x in notes]
        (out / sid).mkdir(exist_ok=True)
        for k, (s, e) in enumerate(takes, 1):
            seg = y[s:e]
            f = f"{sid}/take_{k:02d}.wav"
            write_wav(out / f, seg, rate)
            ms = round((e - s) / rate * 1000) - 2 * PAD_MS
            rows.append({"file": f, "group": f"{sid}: {what}",
                         "says": f"take {k} ({style_of(k)})" if len(takes) == TAKES_EXPECTED else f"take {k}",
                         "listen_for": f"The first sound of '{word}': clean, no letter name, no 'uh' after it",
                         "auto_check": f"{ms} ms of sound, peak {db(max(abs(v) for v in seg)):.0f} dBFS"})
    (out / "report.txt").write_text("\n".join(report) + "\n", encoding="utf-8")
    if rows:
        write_review_page(out, out.name, "playIT: short vowel sounds (ElevenLabs, made by a team member)",
                          "Made by a team member with ElevenLabs (AUDIO_TASK_short-vowels.md). Score each take 1-5 "
                          "(5 = ship it) and note what is wrong. Export CSV when done.", rows, mode="score")
    print("\n".join(report))
    print(f"\n{len(rows)} takes written to {out}" + ("" if not problems else f"\n{problems} thing(s) to FIX: re-record those files."))
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
