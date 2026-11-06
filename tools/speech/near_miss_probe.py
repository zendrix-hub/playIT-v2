"""Say It near-miss probe (user, 2026-11-06: "onsect", "ansect" and "applesect" were accepted as "insect").

For each key word in near_miss_words.json, Kokoro says the word and a few wrong first sounds (IPA input) in 8 voices
at 3 speeds. Each take goes through the app's own Vosk model twice: with the word-mode grammar the app had before
(word, letter names, added-vowel and substitution foils), and with the word's near-miss decoys added. A take
"passes" the way SpeechValidator.validateWord passes it: the transcript, or one of its words, is the key word.
Keep a decoy only if the right word passes as often with it as without it.

Usage: python -I near_miss_probe.py <out.json> <models dir> [word ...]
The models dir holds kokoro-v1.0.onnx and voices-v1.0.bin. Synthetic adult voices are not children: this measures
the grammar, not the false-reject rate with real children (spec target: at most 15%).
"""
import json
import pathlib
import sys

import numpy as np
from kokoro_onnx import Kokoro
from vosk import KaldiRecognizer, Model, SetLogLevel

HERE = pathlib.Path(__file__).resolve().parent
MODELS = pathlib.Path(sys.argv[2])
VOSK = HERE.parent.parent / "app" / "src" / "main" / "assets" / "vosk-model"
VOICES = ["af_heart", "af_bella", "af_nicole", "af_sarah", "af_sky", "am_adam", "am_michael", "bf_emma"]
SPEEDS = [0.85, 1.0, 1.15]

# The word-mode foils in SpeechValidator (letterNames, addedVowelForms, substitutions).
LETTER_NAMES = {"a": ["a", "ay"], "b": ["b", "bee", "be"], "c": ["c", "see", "sea"], "d": ["d", "dee"],
                "e": ["e", "ee"], "f": ["f", "ef"], "g": ["g", "gee", "jee"], "h": ["h", "aitch", "eych"],
                "i": ["i", "eye"], "j": ["j", "jay"], "k": ["k", "kay"], "l": ["l", "el"], "m": ["m", "em"],
                "n": ["n", "en"], "o": ["o", "oh"], "p": ["p", "pee"], "q": ["q", "cue", "queue"], "r": ["r", "ar"],
                "s": ["s", "es"], "t": ["t", "tee", "tea"], "u": ["u", "you", "yu"], "v": ["v", "vee"],
                "w": ["w", "double"], "x": ["x", "ex", "eks"], "y": ["y", "why"], "z": ["z", "zee", "zed"]}
ADDED = {"b": ["ba", "buh"], "c": ["ca", "cuh", "ka", "kuh"], "d": ["da", "duh"], "f": ["fa", "fuh"],
         "g": ["ga", "guh"], "h": ["ha", "huh"], "j": ["ja", "juh"], "k": ["ka", "kuh"], "l": ["la", "luh"],
         "m": ["ma", "muh"], "n": ["na", "nuh"], "p": ["pa", "puh"], "q": ["qa", "quh", "kwa"], "r": ["ra", "ruh"],
         "s": ["sa", "suh"], "t": ["ta", "tuh"], "v": ["va", "vuh"], "w": ["wa", "wuh"], "x": ["xa", "xuh", "eks"],
         "y": ["ya", "yuh"], "z": ["za", "zuh"]}
SUBS = {"f": ["p", "pa", "pee"], "v": ["b", "ba", "bee"], "z": ["s", "sa", "es"]}

W = json.load(open(HERE / "near_miss_words.json", encoding="utf-8"))
only = set(sys.argv[3:])

SetLogLevel(-1)
k = Kokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
model = Model(str(VOSK))


def pcm(ipa, voice, speed):
    au, sr = k.create(ipa, voice=voice, speed=speed, lang="en-us", is_phonemes=True)
    au = np.asarray(au, dtype=np.float32)
    x = np.interp(np.linspace(0, len(au), int(len(au) * 16000 / sr), endpoint=False), np.arange(len(au)), au)
    x = x / (np.max(np.abs(x)) + 1e-9) * 0.7
    pad = np.zeros(4000)
    return (np.concatenate([pad, x, pad]) * 32767).astype(np.int16).tobytes()


def run(words, clips, accept):
    g = json.dumps(words + ["[unk]"])
    heard = []
    for b in clips:
        r = KaldiRecognizer(model, 16000, g)
        r.AcceptWaveform(b)
        heard.append(json.loads(r.FinalResult()).get("text", ""))
    ok = sum(1 for h in heard if h in accept or any(t in accept for t in h.split()))
    common = {}
    for h in heard:
        common[h] = common.get(h, 0) + 1
    return ok, dict(sorted(common.items(), key=lambda t: -t[1])[:3])


out = {}
for word, spec in W.items():
    if only and word not in only:
        continue
    letter = spec["letter"]
    accept = set(spec.get("accept", [word]))
    before = list(dict.fromkeys(list(accept) + LETTER_NAMES.get(letter, []) + ADDED.get(letter, []) + SUBS.get(letter, [])))
    after = list(dict.fromkeys(before + spec["decoys"]))
    row = {}
    print(f"== {word} ({letter})  decoys: {spec['decoys']}", flush=True)
    for name, ipa in {word: spec["ipa"], **spec["wrong"]}.items():
        clips = [pcm(ipa, v, s) for v in VOICES for s in SPEEDS]
        b_ok, _ = run(before, clips, accept)
        a_ok, a_heard = run(after, clips, accept)
        tag = "RIGHT" if name == word else "wrong"
        print(f"   {tag} {name:12s} before {b_ok:2d}/{len(clips)}  with decoys {a_ok:2d}/{len(clips)}   {a_heard}", flush=True)
        row[name] = {"right": name == word, "before": b_ok, "with_decoys": a_ok, "of": len(clips), "heard": a_heard}
    out[word] = row
json.dump(out, open(sys.argv[1], "w", encoding="utf-8"), indent=1)
