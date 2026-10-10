# Card 01: Speech judge with error types (NFR-ASR-01)

Status: accepted

## Why
`SpeechValidator.validate()` accepts "em", "ma", "muh", "buh", anchor words, and anything that starts with the letter (the prefix rule). Those are exactly the errors teachers flagged. The app needs to reject them and say which error it heard, so Say It can correct the child with no adult present.

Spike result (docs/spikes/vosk-foil-spike.md): the app's Vosk model reliably reports "ma" and "sa" when a vowel is added, but it never reports a held "mmm" as any token. So the judge detects errors from the transcript and, for continuous sounds only, confirms a correct pure sound from how long the child sustained it.

## Files
- New: `domain/model/SpeechJudgement.kt`
- Edit: `domain/manager/SpeechValidator.kt`
- Edit: `app/src/test/.../domain/manager/SpeechValidatorTest.kt`

Stay pure Kotlin in domain/ (no android.* imports).

## Changes
1. `SpeechJudgement.kt`:
   ```kotlin
   enum class SpeechErrorType { NONE, LETTER_NAME, ADDED_VOWEL, SUBSTITUTION, OTHER_WORD, NO_SPEECH, UNCONFIRMED }
   data class SpeechJudgement(val isCorrect: Boolean, val errorType: SpeechErrorType, val heard: String)
   ```
2. In `SpeechValidator`, add three per-letter maps for the 26 letters (leave the ng and ñ entries of the old map untouched; card 05 removes them):
   - `letterNames`: a→[a, ay], b→[b, bee, be], c→[c, see, sea], d→[d, dee], e→[e, ee], f→[f, ef], g→[g, gee, jee], h→[h, aitch, eych], i→[i, eye], j→[j, jay], k→[k, kay], l→[l, el], m→[m, em], n→[n, en], o→[o, oh], p→[p, pee], q→[q, cue, queue], r→[r, ar], s→[s, es], t→[t, tee, tea], u→[u, you, yu], v→[v, vee], w→[w, double], x→[x, ex, eks], y→[y, why], z→[z, zee, zed]
   - `addedVowelForms`: every consonant → [<c>a, <c>uh] (for example ma, muh, ba, buh), plus c→[ka, kuh], q→[kwa], x→[eks], y→[ya, yuh], w→[wa, wuh]. Vowels have none.
   - `substitutions` (Filipino English patterns from the proposal): f→[p, pa, pee], v→[b, ba, bee], z→[s, sa, es]
   - `CONTINUOUS = setOf("a","e","i","o","u","f","l","m","n","r","s","v","z")`
3. Add `fun judgeSound(recognizedText: String?, letter: String, sustainedMs: Int?): SpeechJudgement`, checked in this order:
   1. Tokenize like `validate()` does. A token in `letterNames` gives LETTER_NAME. A token in `addedVowelForms` gives ADDED_VOWEL. A token in `substitutions` gives SUBSTITUTION. A token matching any seeded example word gives OTHER_WORD. All are `isCorrect = false`.
   2. If `letter` is not in CONTINUOUS, return UNCONFIRMED, false. Stops are judged in word mode, not by sound.
   3. If `sustainedMs == null`, return UNCONFIRMED, false.
   4. If `sustainedMs >= 400` and the transcript is blank or only `[unk]`, return NONE, true.
   5. Otherwise return NO_SPEECH, false.
4. Add `fun judgeWord(recognizedText: String?, targetWord: String, letter: String): SpeechJudgement`: blank gives NO_SPEECH. `validateWord()` true gives NONE, true. A letter-name token gives LETTER_NAME. An added-vowel token gives ADDED_VOWEL. Anything else gives OTHER_WORD.
5. Add `fun grammarFor(letter: String, targetWord: String?): List<String>`:
   - word mode (targetWord != null): accepted word variants + that letter's letterNames + addedVowelForms + substitutions
   - sound mode: letterNames + addedVowelForms + substitutions only (no positive token exists; `[unk]` is appended by VoskRecognizer)
6. Change `validate()` so that for the 26 letters it returns `judgeSound(text, letter, null).isCorrect`, which is always false: sound mode now needs a duration. For ng and ñ, keep the old behavior. Delete the prefix rule (step 4 of the old function). Mark `validate()` `@Deprecated("Use judgeSound or judgeWord")`.
7. Leave `validateWord()` and `wordAcceptedVariants` unchanged.

## Tests (SpeechValidatorTest)
Delete `letterNameVariations_returnTrue`, `phonicsSoundOnomatopoeias_returnTrue`, and `anchorCurriculumWords_returnTrue`. In `exactLetterMatch_returnsTrue`, keep only the ng and ñ lines and rename it `legacyNgEnye_exactMatch_returnsTrue`. Add:

| Test | Assertion |
|---|---|
| `judgeSound_letterName_rejected` | ("em","m",600), ("es","s",600), ("bee","b",600), ("zee","z",600), ("ay","a",600), ("m","m",600): isCorrect false, LETTER_NAME |
| `judgeSound_addedVowel_rejected` | ("ma","m",600), ("muh","m",600), ("sa","s",600), ("buh","b",600): false, ADDED_VOWEL |
| `judgeSound_substitution_rejected` | ("pa","f",600), ("ba","v",600), ("sa","z",600): false, SUBSTITUTION |
| `judgeSound_anchorWord_isOtherWord` | ("mouse","m",600): false, OTHER_WORD |
| `judgeSound_sustainedContinuous_accepted` | ("", "m", 600), ("[unk]", "s", 450): true, NONE |
| `judgeSound_tooShort_rejected` | ("", "m", 200): false, NO_SPEECH |
| `judgeSound_stop_neverAcceptedBySustain` | ("", "b", 800): false, UNCONFIRMED |
| `judgeSound_noDuration_unconfirmed` | ("", "m", null): false, UNCONFIRMED |
| `validate_prefixRuleRemoved` | validate("mo","m") false, validate("muh","m") false |
| `judgeWord_paths` | ("mouse","mouse","m") true NONE; ("em","mouse","m") LETTER_NAME; ("ma","mouse","m") ADDED_VOWEL; ("cat","mouse","m") OTHER_WORD; ("","mouse","m") NO_SPEECH |
| `grammarFor_wordMode_hasFoilsNotGenericDecoys` | grammarFor("m","mouse") contains mouse, em, ma; does not contain cat, dog, yes, no |
| `grammarFor_soundMode_hasNoAcceptedTokens` | grammarFor("m", null) contains em, ma; does not contain mm, mmm, mouse |

All existing word-mode tests must still pass unchanged.

## Commit
`feat(speech): error-typed judge; reject letter names and added vowels (NFR-ASR-01)`
