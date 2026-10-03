# Card 11: Stars and hearts use the child's real results (FR-04, FR-06, FR-13)

Status: done

Runs after card 07 is accepted. It edits files card 07 also edits, so work from the post-07 code and find code by function name, not by line number.

## Why
Code check of 2026-10-01:
1. **Stars are always 3.**
   - Letter Complete reads a `heartsLost` argument that the route never sends: `Routes.LETTER_COMPLETE = "letter_complete/{phonemeId}"`. It also clamps the value with `coerceIn(0, 2)` in `LetterCompleteViewModel.completeLesson()`, so a letter could never score below 2 stars anyway.
   - Blend It Complete calls `BlendItStarThresholds.calculateStars(groupId, totalHeartsLost = 0)` and saves `heartsLost = 0`. Navigation carries only `groupId`.
   - Parents see 3 stars for every letter.
2. **Losses before a restart vanish.** `HeartManager.resetForRestart()` sets `heartsLost = 0`.
3. **Blend It at 0 hearts shows nothing.** `BlendItUiState.HeartDepleted` has no screen handling. Also, `BlendItViewModel.restartSession()` calls `heartManager.reset()` (5 hearts), but the decided rule is a 3-heart restart (`13_MASTER_TASKS.md` Open Questions: "Standard 3-heart restarts enabled upon depletion").
4. **Two of the five hearts are invisible.** `LessonTopBar` defaults `maxHearts = 3`, while the pool is `GameplayConstants.STARTING_HEARTS = 5`.
5. **Heart recovery never happens.** `HeartManager.checkRecovery()` is never called (FR-07: +1 heart per 3 correct in a row, capped at the pool).

User decision 2026-10-01: fix these now under the current rules. The hearts memo to the adviser may change the rules later.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `domain/manager/HeartManager.kt` and test `domain/manager/HeartManagerTest.kt`
- Edit: `navigation/Routes.kt`, `navigation/NavGraph.kt`; new test `navigation/RoutesTest.kt`
- Edit: `presentation/components/LessonTopBar.kt`
- Edit: `presentation/findit/FindItViewModel.kt`, `presentation/findit/FindItScreen.kt`, test `presentation/findit/FindItViewModelTest.kt`
- Edit: `presentation/blendit/BlendItViewModel.kt`, `presentation/blendit/BlendItScreen.kt`, test `presentation/blendit/BlendItViewModelTest.kt`
- Edit: `presentation/lettercomplete/LetterCompleteViewModel.kt` and test `presentation/lettercomplete/LetterCompleteViewModelTest.kt`
- Edit: `presentation/blendit/BlendItCompleteViewModel.kt` and test `presentation/blendit/BlendItCompleteViewModelTest.kt`

## Changes
1. **`HeartManager`.** Add a session total that a restart keeps:
   ```kotlin
   /** Hearts lost in the whole session, including before a restart. Cleared only by reset(). */
   var sessionHeartsLost: Int = 0
       private set
   ```
   - `deductHeart()` increments `sessionHeartsLost` together with `heartsLost`.
   - `resetForRestart()` does **not** touch `sessionHeartsLost`.
   - `reset()` clears it.
2. **`Routes`.** Results travel as optional query arguments with defaults, so old call sites still work:
   ```kotlin
   const val LETTER_COMPLETE = "letter_complete/{phonemeId}?heartsLost={heartsLost}"
   const val BLEND_IT_COMPLETE = "blendit_complete/{groupId}?heartsLost={heartsLost}&wordsCorrect={wordsCorrect}&totalWords={totalWords}"
   fun letterComplete(phonemeId: String, heartsLost: Int = 0) = "letter_complete/$phonemeId?heartsLost=$heartsLost"
   fun blendItComplete(groupId: String, heartsLost: Int = 0, wordsCorrect: Int = 5, totalWords: Int = 5) =
       "blendit_complete/$groupId?heartsLost=$heartsLost&wordsCorrect=$wordsCorrect&totalWords=$totalWords"
   ```
3. **`NavGraph`.**
   - Give both destinations `arguments = listOf(navArgument(...) { type = NavType.StringType; defaultValue = "..." })` for each query argument. Defaults: `"0"` for `heartsLost`, `"5"` for `wordsCorrect` and `totalWords`. String type, because both ViewModels read `SavedStateHandle` strings.
   - Find It: `onNext = { phonemeId, heartsLost -> navController.navigate(Routes.letterComplete(phonemeId, heartsLost)) }`.
   - Blend It: `onSessionComplete = { result -> navController.navigate(Routes.blendItComplete(result.groupId.toString(), result.heartsLost, result.wordsCorrect, result.totalWords)) }`.
4. **`LessonTopBar`.** Change the default to `maxHearts: Int = GameplayConstants.STARTING_HEARTS`.
5. **`FindItViewModel`.**
   - Expose `val sessionHeartsLost: Int get() = heartManager.sessionHeartsLost`.
   - Keep a private `consecutiveCorrect` count. On a correct tap, increment it, then call `heartManager.checkRecovery(consecutiveCorrect)` and update `_hearts`. On a wrong tap, reset it to 0.
   - `restartSession()` keeps using `resetForRestart()`.
6. **`FindItScreen`.** Change `onNext: (String) -> Unit` to `onNext: (phonemeId: String, heartsLost: Int) -> Unit`. The "Complete Lesson" button calls `onNext(targetPhoneme?.id?.toString() ?: "1", viewModel.sessionHeartsLost)`.
7. **`BlendItViewModel`.**
   - Add `data class BlendItResult(val groupId: Int, val heartsLost: Int, val wordsCorrect: Int, val totalWords: Int)` in the same file, and `fun result(): BlendItResult`. In it, `heartsLost = heartManager.sessionHeartsLost`, `totalWords = _words.value.size`, and `wordsCorrect` = words solved with no wrong attempt (count it when a word is correct and `_wrongAttemptsForCurrentWord.value == 0`; reset the count in `restartSession()`).
   - `_totalHeartsLost` follows `heartManager.sessionHeartsLost`.
   - Heart recovery as in Find It: count correct words in a row and call `checkRecovery`; a wrong submit resets the count.
   - `restartSession()` calls `heartManager.resetForRestart()` instead of `heartManager.reset()`, then `setupWordAtIndex(0)`.
8. **`BlendItScreen`.**
   - `onSessionComplete: (Int) -> Unit` becomes `onSessionComplete: (BlendItResult) -> Unit`, called with `viewModel.result()`.
   - Add the same overlay Find It uses for game over: `CelebrationOverlay(type = CelebrationType.STAR_BURST, isPlaying = uiState is BlendItUiState.HeartDepleted, onFinished = { viewModel.restartSession() })`, at the end of the root `Box`, as in `FindItScreen`.
9. **`LetterCompleteViewModel`.** `val heartsLost = heartsLostArg?.toIntOrNull()?.coerceAtLeast(0) ?: 0`. Remove the `coerceIn(0, 2)`. Pass it to `StarCalculator.calculateStars(heartsLost = heartsLost)` and save it, as today.
10. **`BlendItCompleteViewModel`.**
    - Read `heartsLost`, `wordsCorrect` and `totalWords` from `SavedStateHandle` (strings, defaults 0, 5, 5).
    - Call `BlendItStarThresholds.calculateStars(groupId, totalHeartsLost = heartsLost, wordsCorrect = wordsCorrect, totalWords = totalWords)`.
    - Save `heartsLost = heartsLost` in `BlendItProgress`.
11. **Do not change** star thresholds, heart counts, sounds, texts or layouts.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| HeartManagerTest | `sessionHeartsLost_survivesRestart` | 5 `deductHeart()` calls, then `resetForRestart()`: `currentHearts == 3`, `heartsLost == 0`, `sessionHeartsLost == 5` |
| HeartManagerTest | `reset_clearsSessionHeartsLost` | after 2 deductions and `reset()`: `sessionHeartsLost == 0` |
| RoutesTest | `letterComplete_carriesHeartsLost` | `Routes.letterComplete("3", 2) == "letter_complete/3?heartsLost=2"` |
| RoutesTest | `blendItComplete_carriesResults` | `Routes.blendItComplete("1", 1, 4, 5) == "blendit_complete/1?heartsLost=1&wordsCorrect=4&totalWords=5"` |
| FindItViewModelTest | `wrongTaps_countIntoSessionHeartsLost` | 2 wrong taps: `sessionHeartsLost == 2` |
| FindItViewModelTest | `sessionHeartsLost_survivesRestart` | 5 wrong taps (game over), `restartSession()`, 1 wrong tap: `sessionHeartsLost == 6`, `hearts.value == 2` |
| FindItViewModelTest | `threeCorrectInARow_recoversAHeart` | 1 wrong tap (4 hearts), then the 3 correct pictures: `hearts.value == 5` |
| BlendItViewModelTest | `heartDepleted_restartsWithThreeHearts` | 5 wrong submits gives `HeartDepleted`; after `restartSession()`: `hearts.value == 3` and `result().heartsLost == 5` |
| BlendItViewModelTest | `result_countsFirstTryWords` | word 1 correct first try, word 2 after one wrong submit: `result().wordsCorrect == 1`, `result().heartsLost == 1` |
| LetterCompleteViewModelTest | `heartsLostArg_threeOrMore_givesOneStar` | `savedStateHandle["heartsLost"]` = `"3"`: `starsEarned.value == 1`, and the saved progress has `heartsLost == 3` |
| LetterCompleteViewModelTest | `heartsLostArg_missing_givesThreeStars` | no argument: `starsEarned.value == 3` |
| BlendItCompleteViewModelTest | `starsUseNavResults` | `heartsLost` = `"1"`, `wordsCorrect` = `"4"`, `totalWords` = `"5"`: `starsEarned.value == 2`, and the saved progress has `heartsLost == 1` |

Use the existing test setups. Find It taps go through `selectPictureItem(item)`, as in `FindItViewModelTest.kt:106-175`; Blend It uses `placeTile(letter)` then `submitWord()`, as in `BlendItViewModelTest.kt:112-117`. Existing tests that build these ViewModels must still pass. If a strict `SavedStateHandle` mock fails on the new keys, stub them (`every { savedStateHandle.get<String>("heartsLost") } returns null` and so on). Run `./gradlew testDebugUnitTest`.

## Commit
`fix(progress): stars and hearts use the child's real results (FR-04, FR-06, FR-13)`

Decisions used: fix the star and heart bugs under the current rules (user decision 2026-10-01). The 3-heart restart, recovery cap and Blend It thresholds are already confirmed in `13_MASTER_TASKS.md` Open Questions.
