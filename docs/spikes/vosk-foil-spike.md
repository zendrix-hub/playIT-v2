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

**Status (2026-09-29): the 400 ms rule is switched off** (`SpeechValidator.SOUND_MODE_ENABLED = false`). Held letter names can pass it: in this spike "em" came back empty and "es" came back [unk], so a child who holds a letter name ("emmm", "esss") for 400 ms or longer would be accepted as a correct pure sound. While the rule is off, `judgeSound` still reports foils and returns UNCONFIRMED for everything else. It stays off until the on-device test with children below shows that held letter names are caught.

## To repeat on-device
Record 5 children × 4 Chapter 1 sounds × 3 productions (correct, added vowel, letter name). Run them through the grammar and fill in the same table. Report false-accept and false-reject rates against teacher judgment (SI-1).

## Addendum 2026-10-06: close short vowels (found while making card 15 word clips)
Kokoro clips (voice af_heart 0.7 + af_bella 0.3, speed 0.95), checked against a 3-word grammar of the word and its close-vowel neighbors. The column "vowel F1/F2" is a rough median over the voiced part, measured with Praat via parselmouth.

| Kokoro said | Vosk heard (conf.) | vowel F1/F2 (Hz) | Same pair, other word: Kokoro said, Vosk heard |
|---|---|---|---|
| tub | tab (0.73) | 397/1550 | tab: tab (1.00), 966/1816 |
| miss | mess (0.63) | 789/2213 | mess: mess (1.00), 996/2086 |
| vet | vat (0.56) | 680/2032 | vat: vat (1.00), 958/1983 |
| sum | sum (0.50) | 304/1511 | sam: sam (1.00), 883/1830 |
| met | met (0.64) | 943/2094 | mat: mat (1.00), 1044/2010 |

The formants show that Kokoro produced different vowels in each pair. So the errors are Vosk's. On clean adult-like synthetic speech, the small English model often picks the wrong word when the grammar holds close short vowels. Even when it is right on these pairs, its confidence is low.

**Consequences:**
- A Say It grammar must not hold minimal-pair foils that differ only in a short vowel. Today's grammars hold only the key word, the letter names and the added vowels, so they are not affected. Keep it that way.
- Children's vowels vary more than synthetic ones, so vowel discrimination by Vosk should be assumed unreliable. This is a defense point (DEFENSE_REVIEWER A2/A4) and a Round 2 measurement item.
- Word clips are judged by ear, not by Vosk (2026-10-06-blendwords-card15 page note).
