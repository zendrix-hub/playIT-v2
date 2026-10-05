# Card 12: Find It distractors, gentle correction, no emoji (FR-05, FR-12)

Status: accepted

Runs after card 11 is accepted. It edits `FindItScreen.kt`, `BlendItScreen.kt` and `BlendItViewModel.kt` after cards 07 and 11, so find code by name, not by line number.

## Why
Code check of 2026-10-01:
1. **A distractor can start with the target sound.** `GridGenerator.generate5ItemGrid()` takes the first picture of two random other banks, so:
   - target **b** can get "Box" (the x bank lists words that end in x);
   - **r** can get "Ring" (ng bank) and **p** can get "Piña" (ñ bank);
   - **c**, **k** and **q** can get each other's "Cat", "Kite" and "Queen", which all start with /k/.

   The child then taps a correct-sounding picture and loses a heart. The ng and ñ banks are not even letters in the database.
2. **Wrong answers are red, and Blend It buzzes.**
   - The Find It wrong card uses `CoralBerry` (#FF4757) in the three card blocks of `FindItScreen.kt`, the `isIncorrectSelection` branches.
   - Blend It's wrong slot uses `CoralBerry` (`BlendItUiState.WordIncorrect` branch in `BlendItScreen.kt`).
   - Blend It plays `SfxEvent.BLENDIT_BUZZ` on a wrong submit.
   - 03 §2 and §6 say: never red for a wrong answer (use `GentleCorrectionOrange`), and no buzzer.
3. **The parent PDF breaks the Zero-Emoji Policy.** `PdfExporter.kt` draws "⚠️ At-Risk Phonemes Requiring Practice" and "★" after star counts (2 places). A scan of `app/src/main` found nothing else, so a test can now keep it that way.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `domain/manager/GridGenerator.kt` and test `domain/manager/GridGeneratorTest.kt`
- Edit: `presentation/findit/FindItScreen.kt`
- Edit: `presentation/blendit/BlendItScreen.kt`, `presentation/blendit/BlendItViewModel.kt`, test `presentation/blendit/BlendItViewModelTest.kt`
- Edit: `data/pdf/PdfExporter.kt`
- New test: `ZeroEmojiPolicyTest.kt` (package `com.playit.app`)

## Changes
1. **`GridGenerator`:**
   ```kotlin
   /** Banks that never give distractors: x lists words that END in x; ng and ñ are not curriculum letters (pending SME). */
   private val noDistractorBanks = setOf("x", "ng", "ñ")
   /** Letters that share a first sound; a distractor never comes from the target's group. */
   private val sameSoundGroups = listOf(setOf("c", "k", "q"))

   internal fun distractorLettersFor(target: String): List<String> {
       val group = sameSoundGroups.firstOrNull { target in it } ?: setOf(target)
       return pictureBank.keys.filter { it !in group && it !in noDistractorBanks }
   }
   ```
   In `generate5ItemGrid`, build the distractor list from `distractorLettersFor(cleanTarget).shuffled()` instead of `pictureBank.keys.filter { it != cleanTarget }.shuffled()`. Change nothing else; the target pictures stay as they are.
2. **`FindItScreen`:** in every `isIncorrectSelection` branch, replace `CoralBerry` with `GentleCorrectionOrange` (and `CoralBerry.copy(alpha = …)` with `GentleCorrectionOrange.copy(alpha = …)`, same alpha). Keep the shake.
3. **`BlendItScreen`:** in the `BlendItUiState.WordIncorrect` colour branch, `CoralBerry` becomes `GentleCorrectionOrange`.
4. **`BlendItViewModel.submitWord()`:** on a wrong submit, play `SfxEvent.INCORRECT_POP` (the soft pop Find It uses) instead of `SfxEvent.BLENDIT_BUZZ`. Keep the rest of the sequence.
5. **`PdfExporter`:**
   - The at-risk title becomes `"At-Risk Phonemes Requiring Practice (${reportData.atRiskLetters.size}):"`, with no symbol.
   - `"${lp.starsEarned} ★"` becomes `"${lp.starsEarned} stars"`.
   - `"Total Stars: ${reportData.totalStars} ★   |   …"` becomes `"Total Stars: ${reportData.totalStars}   |   …"`.
6. **`ZeroEmojiPolicyTest`:**
   - Find `src/main` from the module directory, with a fallback `app/src/main` from the repo root, like `AudioCompletenessCheckTest`.
   - Read every `.kt` and `.xml` file.
   - Fail with the file, line and code point if any character falls in U+1F000-U+1FAFF, U+2600-U+27BF, U+2300-U+23FF, U+2B00-U+2BFF, or is U+FE0F.
7. Do not change the target pictures, hearts, texts, or other colours (the dashboard risk colours and the hearts stay as they are).

## Tests
| Test file | Test | Assertion |
|---|---|---|
| GridGeneratorTest | `distractors_neverShareTheTargetSound` | for every bank letter except x, ng and ñ, 200 grids each: no distractor's `phonemeLetter` equals the target, and for c, k and q none is in {c, k, q} |
| GridGeneratorTest | `distractors_neverFromSpecialBanks` | the same 200-grid loop: no distractor's `phonemeLetter` is x, ng or ñ |
| GridGeneratorTest | `distractorLettersFor_k_excludesSameSoundGroup` | `distractorLettersFor("k")` contains none of c, k, q, x, ng, ñ, and contains m |
| BlendItViewModelTest | `wrongSubmit_playsSoftPop_notBuzz` | after a wrong submit, the `playSequence` list contains the `INCORRECT_POP` path and not the `BLENDIT_BUZZ` path. Stub `getSfxPath(SfxEvent.INCORRECT_POP)` and `getSfxPath(SfxEvent.BLENDIT_BUZZ)` to different strings in the test |
| ZeroEmojiPolicyTest | `noEmojiInAppSources` | passes after change 5; it would fail on today's `PdfExporter.kt` |

All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`fix(ui): Find It distractors never share the target sound; gentle correction; no emoji (FR-05, FR-12)`

Decisions used: none new. The colour and buzzer rules are 03 §2 and §6 hard rules, and Zero-Emoji is in AGENTS.md.
