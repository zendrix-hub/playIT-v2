# Card 03: Tutor policy, prompt ladder, no hearts in Say It (FR-03)

## Why
The adviser requires the lesson to work with no adult present. Today a wrong Say It attempt removes a heart and plays a generic "try again". A child alone gets punished for recognizer mistakes and never hears how to fix the error. The prompt ladder in spec §3.2 (Table 7) replaces this.

## Files
- New: `domain/manager/TutorPolicy.kt` (pure Kotlin)
- New: `app/src/test/.../domain/manager/TutorPolicyTest.kt`
- Edit: `presentation/sayit/SayItViewModel.kt`, `presentation/sayit/SayItScreen.kt` (one line)
- Edit: `data/audio/AudioResolver.kt` (one new function)
- Edit: `app/src/test/.../presentation/sayit/SayItViewModelTest.kt`
- Add: tutor fragments to `app/src/main/assets/audio/vo/tutor/` (copy from the audio pack, only the files marked OK in listening_checklist.csv)

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
   - Remove the `heartManager.deductHeart()` call and the `_hearts` update from `evaluateSpeech()`. Keep the `hearts` flow so other code compiles.
   - Track `attemptNumber` (1-based, reset when the phoneme loads) and expose `tutorAction: StateFlow<TutorAction?>`.
   - After each judgement, call `tutorPolicy.next(attemptNumber, judgement)` and play one sequence with `audioPlayer.playSequence(...)`:
     - Praise: existing correct chime + `getRotatingCorrectVo()`, as now.
     - Correct, level 1: fragment by error type (LETTER_NAME → `fb_letter_name`, ADDED_VOWEL → `fb_added_vowel`, anything else → `fb_listen_again`), then the phoneme clip `getPhonemePath(letter)`, then `car_your_turn`.
     - Correct, level 2: `car_watch_my_lips`, the phoneme clip, `car_your_turn`.
     - LeadAndMoveOn: `car_lets_say_together`, the phoneme clip, `fb_try_later`. Then move to the same completion path a correct answer uses, but with `isCorrect = false` saved. Mastery tracking comes in a later card; leave a `// TODO(card-06): mark NEEDS_PRACTICE` comment.
   - In word mode, use the key word clip `getWordPath(targetWord)` instead of the phoneme clip in the three sequences above.
4. `SayItScreen`: pass `hearts = null` to `LessonTopBar` so no hearts show on Say It.
5. Don't change `HeartManager` or other screens. Find It keeps hearts.

## Tests
`TutorPolicyTest`:

| Test | Assertion |
|---|---|
| `correct_anyAttempt_praises` | next(1, correct) and next(3, correct) are Praise |
| `firstMiss_correctsAtLevel1_withErrorType` | next(1, LETTER_NAME miss) == Correct(LETTER_NAME, 1) |
| `secondMiss_correctsAtLevel2` | next(2, ADDED_VOWEL miss) == Correct(ADDED_VOWEL, 2) |
| `thirdMiss_leadsAndMovesOn` | next(3, any miss) == LeadAndMoveOn |

`SayItViewModelTest`:
- Replace `evaluateSpeech_incorrectTranscript_deductsHeart` with `evaluateSpeech_incorrectTranscript_doesNotDeductHeart` (hearts unchanged).
- Add `firstLetterNameMiss_playsLetterNameCorrection`: verify playSequence gets the `fb_letter_name` path, then the word clip, then `car_your_turn`.
- Add `thirdMiss_emitsLeadAndMoveOn_andSavesIncorrect`.

## Commit
`feat(sayit): tutor policy prompt ladder; Say It no longer costs hearts (FR-03)`
