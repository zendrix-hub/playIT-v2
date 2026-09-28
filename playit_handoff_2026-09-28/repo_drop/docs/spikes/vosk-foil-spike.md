# Spike: can the app's Vosk model judge isolated sounds? (spec §3.3, risk R2)

Date: 2026-09-28 · Model: `app/src/main/assets/vosk-model` (small en-us) · Audio: Kokoro-82M, voice af_heart, resampled to 16 kHz

This is a first pass on synthetic adult-voice audio. It must be repeated on-device with children's voices before Round 2.

## Vocabulary check
Every candidate token exists in the model vocabulary except "sss" and "ssss". That includes m, mm, mmm, hmm, em, es, ess, ma, sa, buh, ba, bee, b, ah, uh, am, is, and ay. So `SpeechValidator`'s "sss" variant is silently dropped from any grammar.

## Recognition results

| Input audio | Open vocabulary | Grammar [mm, mmm, hmm, m, em, ma, mom] or [ss, s, es, sa, see, yes] |
|---|---|---|
| Held /m/ (about 0.8 s) | "the my hair" | [unk] (conf 0.49) |
| "ma" (added vowel) | "the my" | **ma (0.85)** |
| "muh" | "the law" | **ma (0.83)** |
| "em" (letter name) | "on" | (empty) |
| Held /s/ | "is there" | [unk] (1.0) |
| "sa" (added vowel) | "sir" | **sa (1.0)** |
| "es" (letter name) | "as" | [unk] (1.0) |

## Conclusions
1. Open vocabulary is useless for isolated sounds, so a scoped grammar is required.
2. With a grammar, Vosk **detects added vowels reliably** ("ma" and "muh" both come back as "ma").
3. Vosk **cannot confirm a correct pure sound**: a held /m/ never matches mm, mmm, or hmm. Their dictionary pronunciations differ from a held nasal.
4. Letter names came back empty or [unk] here, so letter-name detection needs on-device testing.

## Design decision (implemented in card 01)
- Word mode stays the scored Say It check for all letters.
- Sound mode (recall checks) confirms a correct sound only for the 13 continuous sounds, only when Vosk reports no foil, and only when the sound was sustained for at least 400 ms. Stops are never judged by sound.
- The sustain duration must come from the audio stream (`AudioCapture`), which is a later card.

## To repeat on-device
Record 5 children × 4 Chapter 1 sounds × 3 productions (correct, added vowel, letter name). Run them through the grammar and fill in the same table. Report false-accept and false-reject rates against teacher judgment (SI-1).
