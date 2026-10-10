# Card 06: Say It feedback text and on-device diagnostics (FR-03, NFR-ASR-01)

Status: accepted

## Why
In the 2026-09-30 phone test, saying "em" or "muh" gave no visible correction. Two causes are possible, and the app cannot tell them apart:
1. Vosk heard the foil, but the correction clips are not shipped yet, and the screen text is generic: the mascot always says "Good try! Let's listen again." (`SayItScreen.kt` line 196), and the banner always says "Good try! Let's try again." (line 413).
2. Vosk did not hear the foil.

Research (docs/proposals/2026-10-01-learning-ux.md, principles 1, 5, 8) says feedback should name the error, praise effort, and never say "try again" when no retry follows. This card makes the text follow the judgement and the prompt ladder, and shows debug builds what Vosk heard.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- New: `domain/manager/SayItFeedbackCopy.kt` (pure Kotlin, no `android.*` imports) and test `domain/manager/SayItFeedbackCopyTest.kt`
- Edit: `presentation/sayit/SayItViewModel.kt` and test `presentation/sayit/SayItViewModelTest.kt`
- Edit: `presentation/sayit/SayItScreen.kt`

## Changes
1. `SayItFeedbackCopy.kt`:
   ```kotlin
   data class FeedbackCopy(val mascot: String, val banner: String)

   object SayItFeedbackCopy {
       /** Text for the result of one attempt. action = TutorPolicy's decision for it. */
       fun forResult(action: TutorAction, errorType: SpeechErrorType, wordMode: Boolean, word: String?): FeedbackCopy
   }
   ```
   Return exactly these strings (`$w` is `word` with its first letter capitalized, or "it" when `word` is null or `wordMode` is false):

   | Case | mascot | banner |
   |---|---|---|
   | `Praise` | "Yes! You said it!" | "Great listening!" |
   | `Correct`, level 1, `LETTER_NAME` | "That's the letter's name. Listen for its sound." | "Its sound, not its name" |
   | `Correct`, level 1, `ADDED_VOWEL` | "Almost! Just the sound, no 'ah'." | "Just the sound" |
   | `Correct`, level 1, `SUBSTITUTION` | "Listen closely to the sound." | "Listen closely" |
   | `Correct`, level 1, `NO_SPEECH` | "I didn't hear you. Say $w!" | "Say it out loud" |
   | `Correct`, level 1, any other type | "Good try! Listen again." | "Listen again" |
   | `Correct`, level 2 (any type) | "Watch my lips, then your turn." | "Watch my lips" |
   | `LeadAndMoveOn` | "Let's say it together. We'll practice later." | "Let's say it together" |

   No emojis. No "try again" anywhere, because after `LeadAndMoveOn` there is no retry.
2. `SayItViewModel`:
   - Add `data class HeardAttempt(val transcript: String, val errorType: SpeechErrorType, val isCorrect: Boolean, val attempt: Int)` (in the same file) and `val lastHeard: StateFlow<HeardAttempt?>`.
   - In `evaluateSpeech()`, after the judgement, set `lastHeard` to the transcript as received, the judgement's error type and correctness, and `attemptNumber`.
   - Reset it to null when a phoneme loads.
   - Do not add `android.util.Log` to the ViewModel: unit tests do not mock it, and `testOptions` does not return default values.
3. `SayItScreen`:
   - Collect `tutorAction` and `lastHeard`.
   - When the state is `Correct` or `Incorrect` and `tutorAction` is not null, use `SayItFeedbackCopy.forResult(...)` for the `MascotSpeechHeader` message (replacing the two `state is SayItState.Correct/Incorrect` branches at about line 195) and for the banner text (about line 413). `errorType` comes from `SayItState.Incorrect.errorType`, or `SpeechErrorType.NONE` for `Correct`.
   - Keep every other message branch (listening, noisy, initializing, permission) as it is.
   - Debug builds only (`if (BuildConfig.DEBUG)`, import `com.playit.app.BuildConfig`):
     - show a small text line under the banner: `Heard: "<transcript>" -> <errorType> (attempt <n>)`;
     - add a `LaunchedEffect(lastHeard)` that calls `Log.d("PlayIT-SayIt", ...)` with the same text.
     - Release builds show and log nothing.
4. Do not change `TutorPolicy`, the audio sequences, `SpeechValidator`, or any other screen.

## Tests
`SayItFeedbackCopyTest`:

| Test | Assertion |
|---|---|
| `praise_isEffortPraise` | `forResult(Praise(1), NONE, true, "mouse")` == ("Yes! You said it!", "Great listening!") |
| `letterName_level1_namesTheError` | `Correct(LETTER_NAME, 1)` gives the letter-name row |
| `addedVowel_level1_namesTheError` | `Correct(ADDED_VOWEL, 1)` gives the added-vowel row |
| `noSpeech_wordMode_saysTheWord` | `Correct(NO_SPEECH, 1)`, wordMode true, "mouse": mascot == "I didn't hear you. Say Mouse!" |
| `noSpeech_soundMode_saysIt` | the same, wordMode false: mascot == "I didn't hear you. Say it!" |
| `level2_isWatchMyLips_forAnyType` | `Correct(OTHER_WORD, 2)` and `Correct(LETTER_NAME, 2)` both give the level-2 row |
| `leadAndMoveOn_neverSaysTryAgain` | the `LeadAndMoveOn` row, and its mascot and banner do not contain "try again" (ignoring case) |
| `noRowSaysTryAgain` | for every `SpeechErrorType` x levels 1 and 2, plus `Praise` and `LeadAndMoveOn`: no text contains "try again" (ignoring case) |

`SayItViewModelTest`:

| Test | Assertion |
|---|---|
| `evaluateSpeech_setsLastHeard` | judgeWord("em","mouse","m") gives LETTER_NAME; after `evaluateSpeech("em")`, `lastHeard.value == HeardAttempt("em", LETTER_NAME, false, 1)` |
| `lastHeard_tracksAttemptNumber` | two misses: `lastHeard.value?.attempt == 2` |
| `loadPhoneme_resetsLastHeard` | `lastHeard.value` is null after the ViewModel loads |

All other existing tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(sayit): feedback text follows the error type; debug transcript overlay (FR-03, NFR-ASR-01)`

Decisions used: Say It never removes hearts; letter names and added vowels are foils (AGENTS.md Decisions). The text is defined in this card; no [proposed] item is involved.
