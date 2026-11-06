"""Search for near-miss decoys for a Say It key word (user, 2026-11-06: "aronge" and even "app range" passed as
"orange"; "check all the chapter 2 onwards").

For each word in decoy_words.json: Kokoro says the right word (in every accepted pronunciation) and the wrong first
sounds, in 8 voices at 3 speeds. Candidate decoys are short phrases, a wrong start ("a", "app", "on"...) plus a tail
that sounds like the word's ending ("range", "inch"...), and single words.

1. Screen: each candidate alone, added to the word's grammar, on 8 takes per utterance (speed 1.0). It must keep
   every right take and catch at least one wrong one.
2. Greedy: from the best screened candidates, add one at a time on all 24 takes per utterance, keeping it only if
   the right word still passes as often as with no decoys at all, and fewer wrong takes pass.

A take passes the way SpeechValidator.validateWord passes it: the transcript, or one of its words, is the key word.
Synthetic adult voices are not children: a result here is a candidate for the app, to be checked on a phone.

Usage: python -I decoy_search.py <models dir> <out.json> [word ...]
"""
import itertools
import json
import multiprocessing as mp
import pathlib
import sys

import numpy as np

HERE = pathlib.Path(__file__).resolve().parent
VOSK = str(HERE.parent.parent / "app" / "src" / "main" / "assets" / "vosk-model")
VOICES = ["af_heart", "af_bella", "af_nicole", "af_sarah", "af_sky", "am_adam", "am_michael", "bf_emma"]
SPEEDS = [0.85, 1.0, 1.15]
SCREEN_SPEED = 1.0
MAX_GREEDY = 24

_model = None


def _init():
    global _model
    from vosk import Model, SetLogLevel
    SetLogLevel(-1)
    _model = Model(VOSK)


def _decode(job):
    """(grammar json, pcm bytes) -> transcript."""
    from vosk import KaldiRecognizer
    g, pcm = job
    r = KaldiRecognizer(_model, 16000, g)
    r.AcceptWaveform(pcm)
    return json.loads(r.FinalResult()).get("text", "")


def passes(text, accept):
    return text in accept or any(t in accept for t in text.split())


def main():
    models = pathlib.Path(sys.argv[1])
    out_path = pathlib.Path(sys.argv[2])
    only = set(sys.argv[3:])
    import os
    spec = json.load(open(os.environ.get("DECOY_SPEC", HERE / "decoy_words.json"), encoding="utf-8"))
    from kokoro_onnx import Kokoro
    k = Kokoro(str(models / "kokoro-v1.0.onnx"), str(models / "voices-v1.0.bin"))

    def pcm(ipa, voice, speed):
        au, sr = k.create(ipa, voice=voice, speed=speed, lang="en-us", is_phonemes=True)
        au = np.asarray(au, dtype=np.float32)
        x = np.interp(np.linspace(0, len(au), int(len(au) * 16000 / sr), endpoint=False), np.arange(len(au)), au)
        x = x / (np.max(np.abs(x)) + 1e-9) * 0.7
        pad = np.zeros(4000)
        return (np.concatenate([pad, x, pad]) * 32767).astype(np.int16).tobytes()

    results = json.load(open(out_path, encoding="utf-8")) if out_path.exists() else {}
    with mp.Pool(max(1, mp.cpu_count() - 2), initializer=_init) as pool:
        for word, w in spec["words"].items():
            if only and word not in only:
                continue
            accept = set(w.get("accept", [word]))
            base = w["base"]  # the grammar the app has today: word, letter foils, today's decoys
            right = w["right"]
            wrong = w["wrong"]
            clips = {n: {(v, s): pcm(ipa, v, s) for v in VOICES for s in SPEEDS} for n, ipa in {**right, **wrong}.items()}

            def score(grammar, screen=False):
                g = json.dumps(grammar + ["[unk]"])
                keys = [(n, vs) for n in clips for vs in clips[n] if not screen or vs[1] == SCREEN_SPEED]
                texts = pool.map(_decode, [(g, clips[n][vs]) for n, vs in keys], chunksize=8)
                ok_right = sum(1 for (n, _), t in zip(keys, texts) if n in right and passes(t, accept))
                ok_wrong = sum(1 for (n, _), t in zip(keys, texts) if n in wrong and passes(t, accept))
                per = {}
                for (n, _), t in zip(keys, texts):
                    per.setdefault(n, [0, 0])
                    per[n][1] += 1
                    per[n][0] += passes(t, accept)
                return ok_right, ok_wrong, per

            pre = w.get("prefixes", [])
            tails = w.get("tails", [])
            cands = list(dict.fromkeys(
                [f"{p} {t}" for p, t in itertools.product(pre, tails)] + tails + w.get("singles", [])))
            cands = [c for c in cands if c not in base and not any(x in accept for x in c.split())]
            print(f"== {word}: {len(cands)} candidates", flush=True)

            r0s, w0s, _ = score(base, screen=True)
            screened = []
            for c in cands:
                r1, w1, _ = score(base + [c], screen=True)
                if r1 >= r0s and w1 < w0s:
                    screened.append((w0s - w1, c))
            screened.sort(key=lambda t: -t[0])
            print(f"   screened: {len(screened)} keep every right take; best {screened[:8]}", flush=True)

            r0, w0, per0 = score(base)
            chosen, best_w = [], w0
            for _, c in screened[:MAX_GREEDY]:
                r1, w1, _ = score(base + chosen + [c])
                if r1 >= r0 and w1 < best_w:
                    chosen.append(c)
                    best_w = w1
                    print(f"   + {c!r}: wrong passes {w1}/{sum(len(clips[n]) for n in wrong)}", flush=True)
            r1, w1, per1 = score(base + chosen)
            results[word] = {"chosen": chosen, "right_before": r0, "right_after": r1, "wrong_before": w0, "wrong_after": w1,
                             "per_before": per0, "per_after": per1}
            print(f"   {word}: right {r0} -> {r1}, wrong {w0} -> {w1}; per utterance after {per1}", flush=True)
            json.dump(results, open(out_path, "w", encoding="utf-8"), indent=1)


if __name__ == "__main__":
    main()
