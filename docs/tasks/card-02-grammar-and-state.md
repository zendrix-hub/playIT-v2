# Card 02: Scope the grammar and carry the error type (NFR-ASR-01, FR-03)

## Why
Say It builds its Vosk grammar from the accepted list plus six unrelated decoys (cat, dog, sun, ball, yes, no). Near-misses such as "ma" have nowhere to land except the target, which inflates false accepts. Using each letter's foils lets Vosk report the actual error.

## Files
- Edit: `presentation/sayit/SayItViewModel.kt`
- Edit: the file that defines `SayItState` (same package)
- Edit: `app/src/test/.../presentation/sayit/SayItViewModelTest.kt`

## Changes
1. In `startListening()`, replace the `setGrammar(...)` line with
   `voskRecognizer.setGrammar(speechValidator.grammarFor(letter, targetWord))`,
   where `letter = _phoneme.value?.letter?.lowercase() ?: "m"`.
2. Change `SayItState.Incorrect(transcript: String)` to
   `Incorrect(transcript: String, errorType: SpeechErrorType = SpeechErrorType.OTHER_WORD)`.
   Update every call site; the UI may ignore the new field for now.
3. In `evaluateSpeech()`, replace the two validate branches with a single judgement:
   - word mode: `speechValidator.judgeWord(transcript, targetWord, letter)`
   - sound mode: `speechValidator.judgeSound(transcript, letter, null)` (duration arrives in a later card)
   Use `judgement.isCorrect` where `isCorrect` was used, and pass `judgement.errorType` into `Incorrect`.
4. In the `onResult` partial callback, keep stopping early on a correct partial. Also stop early when the partial's judgement is LETTER_NAME or ADDED_VOWEL; there's no need to wait out the 3.8 s timeout.
5. Keep the ng and ñ legacy path working (the existing test `loadPhoneme_smePendingLetter_usesLegacyLetterMode` must pass).

## Tests (SayItViewModelTest)
Update the mocks from `validate`/`validateWord` to `judgeWord`/`judgeSound` where needed. Add:

| Test | Assertion |
|---|---|
| `startListening_setsLetterScopedGrammar` | verify `voskRecognizer.setGrammar(speechValidator.grammarFor("m","mouse"))` is called |
| `evaluateSpeech_letterName_setsIncorrectWithLetterName` | judgeWord returns (false, LETTER_NAME, "em"); state is `Incorrect` with errorType LETTER_NAME |
| `evaluateSpeech_addedVowel_setsIncorrectWithAddedVowel` | same, ADDED_VOWEL |

Leave `evaluateSpeech_incorrectTranscript_deductsHeart` alone in this card; card 03 changes the heart rule.

## Commit
`feat(sayit): letter-scoped grammar and error type in state (NFR-ASR-01, FR-03)`
