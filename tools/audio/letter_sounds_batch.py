"""Builds review batch 2026-11-06-letter-sounds: Lily's letter sounds for every letter that still plays an old
placeholder (user, 2026-11-06: "the Letter G has a problem on the sound ... N has the same problem ... need to check
the other letters too").

Why: only m, s and i have approved sounds (docs/audio-release). The other 23 are the Edge TTS clips of 2026-08-31
(scripts/upgrade_neural_audio_pipeline.py, voice en-US-AnaNeural), made from texts such as "guh" and "nnn". The voice
spelled several of them out: phoneme_g.mp3 says "G, U, H" and phoneme_n.mp3 "eh, nah, nah" (Whisper, 2026-11-06).

How: the user asked for VoiceStudio (local OmniVoice engine, http://localhost:3900), cloned from Lily's own Kokoro
lesson clips (voicestudio_lily.py). Lily says the sound twice and then the key word ("guh, guh, goat."), the method of
the approved short i (release 2026-10-12-i), and the first sound is cut out where the loudness first dips 25 dB
(parts()). A take whose phrase does not split into three parts (the sounds ran together) is dropped. The held letters
also get a take of the sound alone ("Nnnnn."), used whole. Each cut is cleaned with
kokoro_local.clean (trim, RMS -20 dBFS, peak -1 dBFS). Today's clip is on the page too, to compare.
OmniVoice's weights are CC BY-NC: non-commercial, credited in the Parent Zone. Review audio only: nothing here goes
into app/src/main/assets/.

Usage: python -I letter_sounds_batch.py <out dir> [letters, e.g. g,n] [seeds, e.g. 1,2] [round, 1 or 2]
Round 2 uses ROUND2, the spellings from the user's notes, in its own batch folder.
"""
import json
import pathlib
import re
import sys

import numpy as np
import soundfile as sf

REPO = pathlib.Path(__file__).resolve().parents[2]
sys.path.insert(0, str(REPO / "tools" / "audio"))
from kokoro_local import SR_OUT, clean, core_ms  # noqa: E402
from review_page import write_review_page  # noqa: E402
from voicestudio_lily import heard, reference, say  # noqa: E402

ASSETS = REPO / "app" / "src" / "main" / "assets" / "audio"
# OmniVoice's diffusion steps. 16 takes about 43 s a take on this laptop's GTX 1650; the default took about 150 s.
NUM_STEP = 16

# letter -> (key word, the sound as the child should hear it, texts Lily says; the first sound is cut out)
LETTERS = {
    "a": ("apple", "short a, as at the start of 'apple'", ["a, a, apple.", "æ, æ, apple."]),
    # "oh, oh, orange.": the user's note on o_t1_vs2 (2026-11-06), "oh can try oh oh orange".
    "o": ("orange", "short o, as at the start of 'orange'", ["aw, aw, orange.", "ah, ah, orange.", "oh, oh, orange."]),
    "b": ("ball", "b, a quick 'buh'", ["buh, buh, ball."]),
    "e": ("elephant", "short e, as at the start of 'elephant'", ["eh, eh, elephant."]),
    "u": ("umbrella", "short u, as at the start of 'umbrella'", ["uh, uh, umbrella."]),
    "t": ("tiger", "t, a quick 'tuh'", ["tuh, tuh, tiger."]),
    "k": ("kite", "k, a quick 'kuh'", ["kuh, kuh, kite."]),
    "l": ("lion", "l, a held 'lll'", ["lll, lll, lion.", "Lllll."]),
    "y": ("yoyo", "y, a quick 'yuh'", ["yuh, yuh, yoyo."]),
    "n": ("nest", "n, a held 'nnn'", ["nnn, nnn, nest.", "Nnnnn."]),
    "g": ("goat", "g, a quick 'guh'", ["guh, guh, goat."]),
    "p": ("pig", "p, a quick 'puh'", ["puh, puh, pig."]),
    "r": ("rabbit", "r, a held 'rrr'", ["rrr, rrr, rabbit.", "Rrrrr."]),
    "d": ("dog", "d, a quick 'duh'", ["duh, duh, dog."]),
    "h": ("hat", "h, a breathy 'huh'", ["huh, huh, hat."]),
    "w": ("watch", "w, a quick 'wuh'", ["wuh, wuh, watch."]),
    "c": ("cat", "c, a quick 'kuh' (the same sound as k)", ["kuh, kuh, cat."]),
    "f": ("fish", "f, a held 'fff'", ["fff, fff, fish.", "Fffff."]),
    "j": ("jug", "j, a quick 'juh'", ["juh, juh, jug."]),
    "q": ("queen", "q, a quick 'kwuh'", ["kwuh, kwuh, queen."]),
    "v": ("van", "v, a held 'vvv'", ["vvv, vvv, van.", "Vvvvv."]),
    "x": ("box", "x, 'ks', as at the end of 'box'", ["ks, ks, box.", "kss, kss, box."]),
    "z": ("zebra", "z, a held 'zzz'", ["zzz, zzz, zebra.", "Zzzzz."]),
}


# Round 2 (user's notes on the part 2 page, 2026-11-06): every take of these was FIX. The user wants a
# consonant with a short vowel after it, like the approved g and d, and for p and r the key word's vowel.
ROUND2 = {
    # "Luh. Luh. Lion." and "Fuh. Fuh. Fish.": full stops for clearer pauses, after every "luh, luh, lion." take
    # and both "fuh, fuh, ffff." takes ran together (2026-11-06).
    "l": ("lion", "l, 'luh'", ["luh, luh, lion.", "Luh. Luh. Lion."]),  # "should be luh luh lion"
    "p": ("pig", "p, 'puh' or 'pih'", ["puh, puh, pig.", "pih, pih, pig."]),  # "should be puh or pih since pig"
    "r": ("rabbit", "r, 'ruh' or 'rah'", ["ruh, ruh, rabbit.", "rah, rah, rabbit."]),  # "should be ruh or rah for rabbit"
    "h": ("hat", "h, 'huh' or 'hah'", ["huh, huh, hat.", "ha, ha, hat."]),  # FIX, no note: as p and r
    "f": ("fish", "f, 'fuh'", ["fuh, fuh, fish.", "fuh, fuh, ffff.", "Fuh. Fuh. Fish."]),  # "is it possible that fuh fuh ffff"
    "v": ("van", "v, 'vuh'", ["vuh, vuh, van."]),  # "should be vuh vuh"
    "z": ("zebra", "z, 'zuh'", ["zuh, zuh, zebra.", "zuh, zuh, zzz."]),  # "it should be zuh zuh zzz"
}


def is_held(text):
    """True for a sound said alone ("Nnnnn."): the whole take is the sound. "guh, guh, goat." and "Luh. Luh. Lion."
    have three parts, and their first sound is cut out."""
    return len([t for t in re.split(r"[,.]", text) if t.strip()]) == 1


# Words a child must never hear. Vosk maps a cut "fuh" to the nearest word it knows, and on 2026-11-06 it heard one
# of these, so such a take is flagged for the listener (the listener decides; Vosk is often wrong on one syllable).
RUDE = {"fuck", "fucking", "shit", "damn", "hell", "ass", "bitch", "dick", "cock", "piss", "crap"}


def rude_warning(*heard_texts):
    hits = sorted({w for t in heard_texts for w in t.split() if w in RUDE})
    return f"LISTEN CLOSELY: the checker heard a rude word ({', '.join(hits)}); mark FIX if it sounds like one. "         if hits else ""


def segments(a, sr, gap_ms=120, rel_db=-35):
    """(start, end) samples of each sounding stretch, split at silences of [gap_ms] or more."""
    hop = int(0.01 * sr)
    e = np.array([np.sqrt(np.mean(a[i:i + hop] ** 2) + 1e-12) for i in range(0, len(a) - hop, hop)])
    on = 20 * np.log10(e / e.max()) > rel_db
    segs, start, quiet = [], None, 0
    for i, v in enumerate(on):
        if v:
            if start is None:
                start = i
            quiet = 0
        elif start is not None:
            quiet += 1
            if quiet * 10 >= gap_ms:
                segs.append((start, i - quiet + 1))
                start, quiet = None, 0
    if start is not None:
        segs.append((start, len(on)))
    return [(s * hop, t * hop) for s, t in segs]


def parts(a, sr, dip_db=-25, min_ms=100):
    """(start, end) samples of the phrase's parts ("guh" | "guh" | "goat"). A part ends where the loudness dips
    [dip_db] or more below the phrase's peak: Lily leaves 120 ms of silence after "guh", 80 ms after "nnn", but only
    a 10-30 ms dip between "duh" and "duh". A part shorter than [min_ms] joins the next one."""
    hop = int(0.01 * sr)
    e = np.array([np.sqrt(np.mean(a[i:i + hop] ** 2) + 1e-12) for i in range(0, len(a) - hop, hop)])
    d = 20 * np.log10(e / e.max())
    on = np.where(d > -35)[0]
    if len(on) == 0:
        return []
    out, start = [], None
    for i in range(on[0], on[-1] + 1):
        if d[i] > dip_db:
            if start is None:
                start = i
        elif start is not None:
            out.append([start, i])
            start = None
    if start is not None:
        out.append([start, on[-1] + 1])
    merged = []
    for s, t in out:
        if merged and merged[-1][1] - merged[-1][0] < min_ms // 10:
            merged[-1][1] = t
        else:
            merged.append([s, t])
    if merged:
        merged[0][0] = on[0]  # a quiet onset (the hiss of "fff", the breath of "huh") belongs to the first sound
    return [(s * hop, t * hop) for s, t in merged]


def first_sound(a, sr):
    """The first sound of "guh, guh, goat.", or None when the phrase does not split into at least three parts."""
    p = parts(a, sr)
    if len(p) < 3:
        return None
    s, t = p[0]
    nxt = p[1][0]
    return a[max(0, s - int(0.03 * sr)):min(nxt, t + int(0.04 * sr))]


def today(letter):
    """Today's clip for [letter], resampled to SR_OUT, and its file name."""
    import miniaudio
    p = ASSETS / "phonemes" / f"phoneme_{letter}.mp3"
    d = miniaudio.decode_file(str(p), output_format=miniaudio.SampleFormat.FLOAT32, nchannels=1, sample_rate=SR_OUT)
    return np.asarray(d.samples, dtype=np.float32), p.name


def main():
    out = pathlib.Path(sys.argv[1])
    out.mkdir(parents=True, exist_ok=True)
    table = ROUND2 if len(sys.argv) > 4 and sys.argv[4] == "2" else LETTERS
    letters = sys.argv[2].split(",") if len(sys.argv) > 2 and sys.argv[2] else list(table)
    seeds = [int(s) for s in sys.argv[3].split(",")] if len(sys.argv) > 3 else [1, 2]
    from vosk import Model, SetLogLevel
    SetLogLevel(-1)
    vosk = Model(str(REPO / "app" / "src" / "main" / "assets" / "vosk-model"))
    ref_wav, ref_text = reference()
    rows_path = out / "rows.json"
    rows = json.loads(rows_path.read_text(encoding="utf-8")) if rows_path.exists() else []
    done = {r["file"] for r in rows}
    for letter in letters:
        word, sound, texts = table[letter]
        group = f"Letter {letter.upper()} ({word})"
        listen = f"Is it the {sound}? Not the letter's name, not a different sound, no extra 'uh' on a held sound."
        old, old_name = today(letter)
        name = f"{letter}_today.wav"
        if name not in done:
            sf.write(out / name, clean(old, SR_OUT), SR_OUT)
            rows.append({"file": name, "group": group, "says": f"today's clip ({old_name}, Edge TTS, 2026-08-31)",
                         "listen_for": listen, "auto_check": f"to compare; Vosk hears: \"{heard(vosk, old, SR_OUT)}\""})
        # One spelling: three seeds, so there is still a choice. Two spellings: two seeds each.
        for ti, text in enumerate(texts):
            for seed in (seeds if len(texts) > 1 else seeds + [max(seeds) + 1]):
                name = f"{letter}_t{ti + 1}_vs{seed}.wav"
                if name in done or (out / "phrases" / name).exists():
                    continue  # made already (a take that ran together keeps only its phrase)
                a, sr = say(text, seed, ref_wav, ref_text, num_step=NUM_STEP)
                assert sr == SR_OUT, sr
                (out / "phrases").mkdir(exist_ok=True)
                sf.write(out / "phrases" / name, a, sr)
                held = is_held(text)
                cut = a if held else first_sound(a, sr)
                if cut is None:
                    print(f"{name}: the sounds ran together in {text!r}; dropped", flush=True)
                    continue
                au = clean(cut, sr)
                sf.write(out / name, au, SR_OUT)
                rows.append({"file": name, "group": group,
                             "says": f"\"{text}\" (the whole take)" if held else f"the first sound of \"{text}\"",
                             "listen_for": listen,
                             "auto_check": rude_warning(heard(vosk, au, SR_OUT), heard(vosk, a, sr)) +
                                           f"VoiceStudio (OmniVoice, Lily reference), seed {seed}, "
                                           f"{core_ms(au, SR_OUT)} ms; Vosk hears the sound as "
                                           f"\"{heard(vosk, au, SR_OUT)}\" and the phrase as \"{heard(vosk, a, sr)}\""})
                print(f"{name}: {core_ms(au, SR_OUT)} ms", flush=True)
                rows_path.write_text(json.dumps(rows, ensure_ascii=False, indent=1), encoding="utf-8")
        rows_path.write_text(json.dumps(rows, ensure_ascii=False, indent=1), encoding="utf-8")
    order = {l: i for i, l in enumerate(LETTERS)}  # chapter order; ROUND2 letters are all in LETTERS
    rows.sort(key=lambda r: (order[r["file"].split("_")[0]], r["file"] != f"{r['file'].split('_')[0]}_today.wav", r["file"]))
    intro = (
        "Every letter except m, s and i still plays an old computer clip, and some are wrong (g says 'G, U, H'). "
        "These are new takes in Lily's voice from VoiceStudio: she said the sound twice and the key word "
        "('guh, guh, goat'), and the first sound was cut out. The held letters (l, n, r, f, v, z) also have takes "
        "of the sound alone ('Nnnnn.'). The first row of each letter is today's clip, to compare. "
        if table is LETTERS else
        "Round 2 for the letters where every take was FIX, made with the spellings from your notes ('luh, luh, "
        "lion', 'puh' or 'pih' for pig, 'ruh' or 'rah' for rabbit, 'fuh', 'vuh', 'zuh'; for h, 'huh' and 'ha'). "
        "Lily said the sound twice and then the word, and the first sound was cut out. The first row of each letter "
        "is today's clip, to compare. ")
    write_review_page(
        out, out.name, "playIT: Lily's letter sounds" + ("" if table is LETTERS else ", round 2"),
        intro + "For EACH letter, mark exactly ONE new take OK and the others FIX (FIX all, with a note, if none "
        "is right). Export CSV when done.",
        rows, mode="okfix")
    print(f"wrote {len(rows)} rows to {out}")


if __name__ == "__main__":
    main()
