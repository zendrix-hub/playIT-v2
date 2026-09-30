# Card 03: Tutor policy, prompt ladder, no hearts in Say It (FR-03)

Status: accepted

## Why
The adviser requires the lesson to work with no adult present. Today a wrong Say It attempt removes a heart and plays a generic "try again". A child alone gets punished for recognizer mistakes and never hears how to fix the error. The prompt ladder in spec §3.2 (Table 7) and the state machine in §6.3 replace this.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- New: `domain/manager/TutorPolicy.kt` (pure Kotlin, no `android.*` imports)
- New: test `domain/manager/TutorPolicyTest.kt`
- Edit: `data/audio/AudioResolver.kt` and test `data/audio/AudioResolverTest.kt`
- Edit: `presentation/sayit/SayItViewModel.kt` and test `presentation/sayit/SayItViewModelTest.kt`
- Edit: `presentation/sayit/SayItScreen.kt` (top bar hearts and the Next button only)
- Add: `app/src/main/assets/audio/vo/tutor/<name>.wav`, only the approved carrier clips from the pre-step

## Pre-step: copy approved tutor fragments
1. Read `docs/audio-review/listening_checklist.csv` (columns `file`, `type`, `listen_for`, `OK_or_FIX`, `note`).
2. If any row with `type` = `carrier` has a blank `OK_or_FIX`, stop. Write this question to `docs/tasks/QUESTIONS.md`: "Listening checklist not complete. Which carrier clips are approved?" Do not continue with the card.
3. Otherwise, for every row where `type` is `carrier` and `OK_or_FIX` is `OK`, copy `docs/audio-review/<file>` into `app/src/main/assets/audio/vo/tutor/`, keeping the file name (`fragments/car_your_turn.wav` becomes `app/src/main/assets/audio/vo/tutor/car_your_turn.wav`). Create the folder if it doesn't exist.
4. Copy only `carrier` rows. Do not copy `phoneme`, `keyword`, or `voice` clips. Do not copy carrier rows marked `FIX`.

A clip marked `FIX` stays out of the app. `AudioPlayer` skips a missing asset (it calls `onComplete`), so its sequence still plays the model clip.

## Changes
1. `TutorPolicy.kt`:
   ```kotlin
   sealed interface TutorAction {
       data class Praise(val attempt: Int) : TutorAction
       data class Correct(val errorType: SpeechErrorType, val supportLevel: Int) : TutorAction // 1 = re-model, 2 = slow + lips
       data object LeadAndMoveOn : TutorAction
   }
   class TutorPolicy(private val maxScoredAttempts: Int = 3) {
       fun next(attempt: Int, judgement: SpeechJudgement): TutorAction = when {
           judgement.isCorrect -> TutorAction.Praise(attempt)
           attempt >= maxScoredAttempts -> TutorAction.LeadAndMoveOn
           else -> TutorAction.Correct(judgement.errorType, supportLevel = attempt)
       }
   }
   ```
2. `AudioResolver`: add `fun getTutorPath(id: String): String = "audio/vo/tutor/$id.wav"`.
3. `SayItViewModel`:
   - In `evaluateSpeech()`, remove `heartManager.deductHeart()`, the `_hearts` update, and the `HEART_LOSS_WHOOSH` sound. Keep the `hearts` flow and the `heartManager` field so other code compiles.
   - Add `private val tutorPolicy = TutorPolicy()` and `private var attemptNumber = 0`. Increment `attemptNumber` at the start of each `evaluateSpeech()`.
   - Expose `tutorAction: StateFlow<TutorAction?>` and `canContinue: StateFlow<Boolean>`. When a phoneme loads, reset `attemptNumber` to 0, `tutorAction` to null, and `canContinue` to false.
   - After the judgement, set `tutorAction` to `tutorPolicy.next(attemptNumber, judgement)`. Keep setting `SayItState.Correct` / `SayItState.Incorrect(transcript, errorType)` as now. Then play one `audioPlayer.playSequence(...)`, where `model` is `getWordPath(targetWord)` in word mode and `getPhonemePath(letter)` in legacy mode:
     - Praise: `[getSfxPath(CORRECT_CHIME), getRotatingCorrectVo()]` (as now). Set `canContinue = true`.
     - Correct, level 1: `[getSfxPath(INCORRECT_POP), getTutorPath(fb), model, getTutorPath("car_your_turn")]`, where `fb` is `fb_letter_name` for LETTER_NAME, `fb_added_vowel` for ADDED_VOWEL, and `fb_listen_again` for anything else.
     - Correct, level 2: `[getSfxPath(INCORRECT_POP), getTutorPath("car_watch_my_lips"), model, getTutorPath("car_your_turn")]`.
     - LeadAndMoveOn: `[getTutorPath("car_lets_say_together"), model, getTutorPath("fb_try_later")]`. Set `canContinue = true`. The attempt is already saved with `isCorrect = false`. Add the comment `// TODO(FR-NEW-REC): mark the letter NEEDS_PRACTICE and queue a recall check`.
   - `startListening()` returns immediately when `canContinue` is true: after praise or lead-and-move-on there are no more scored attempts until the phoneme reloads.
4. `SayItScreen`:
   - Pass `hearts = null` to `LessonTopBar` so no hearts show on Say It. Remove the now-unused `hearts` collection line.
   - Collect `canContinue` and use it for the "Next: Find It" button: `onClick = { if (canContinue) onNext(...) }` and `enabled = canContinue`.
5. Don't change `HeartManager`, Find It, or Blend It. Find It keeps hearts.

## Tests
`TutorPolicyTest`:

| Test | Assertion |
|---|---|
| `correct_anyAttempt_praises` | next(1, correct) and next(3, correct) are Praise |
| `firstMiss_correctsAtLevel1_withErrorType` | next(1, LETTER_NAME miss) == Correct(LETTER_NAME, 1) |
| `secondMiss_correctsAtLevel2` | next(2, ADDED_VOWEL miss) == Correct(ADDED_VOWEL, 2) |
| `thirdMiss_leadsAndMovesOn` | next(3, any miss) == LeadAndMoveOn |

`AudioResolverTest`: add `getTutorPath_returnsWavInTutorFolder` (`getTutorPath("car_your_turn") == "audio/vo/tutor/car_your_turn.wav"`).

`SayItViewModelTest`:
- In `setup()`, add `every { audioResolver.getTutorPath(any()) } answers { "tutor/${firstArg<String>()}.wav" }`. `audioResolver` is a strict mock, so every new call needs a stub. `getSfxPath` already returns `"sfx_path"` and `getWordPath` returns `"word_path"`.
- Replace `evaluateSpeech_incorrectTranscript_deductsHeart` with `evaluateSpeech_incorrectTranscript_doesNotDeductHeart` (hearts unchanged).
- Add:

| Test | Assertion |
|---|---|
| `firstLetterNameMiss_playsLetterNameCorrection` | judgeWord("em","mouse","m") returns LETTER_NAME; after `evaluateSpeech("em")`, `playSequence(listOf("sfx_path", "tutor/fb_letter_name.wav", "word_path", "tutor/car_your_turn.wav"), any())` was called |
| `secondMiss_playsWatchMyLips` | two misses; the second `playSequence` list is `["sfx_path", "tutor/car_watch_my_lips.wav", "word_path", "tutor/car_your_turn.wav"]` |
| `thirdMiss_emitsLeadAndMoveOn_andSavesIncorrect` | three misses; `tutorAction` is LeadAndMoveOn, `canContinue` is true, `saveAttempt(1L, 1, false)` was called 3 times, hearts unchanged |
| `correctAfterMiss_praises_andCanContinue` | a miss then a correct word; `tutorAction` is Praise(2) and `canContinue` is true |
| `afterLeadAndMoveOn_startListeningIsIgnored` | after three misses, `startListening()` does not call `voskRecognizer.startListening` again |

All other existing tests must still pass.

## Commit
`feat(sayit): tutor policy prompt ladder; Say It no longer costs hearts (FR-03)`

Decisions used: Say It never removes hearts (spec §3.4 [confirm], covered by AGENTS.md Decisions).
