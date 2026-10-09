# Card 21: Complete screens, splash, profile screens, typography pass

Status: done

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 5". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- LetterComplete and BlendItComplete don't scroll, and their fixed content can push the button off.
- Splash uses a `.height(420.dp)` dome.
- The profile and name screens use fixed 90 dp headers.
- Child text is hard-coded at 164 sites. This task routes the child screens' text through `MaterialTheme.typography` with `maxLines`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `presentation/lettercomplete/LetterCompleteScreen.kt`, `presentation/blendit/BlendItCompleteScreen.kt` (body in a fit-or-scroll Column as in `LessonScaffold`; mascot `d.completeMascot`; button pinned; `testTag("complete_continue")`)
- Modify: `presentation/splash/SplashScreen.kt` (dome `fillMaxWidth().aspectRatio(0.9f).heightIn(max = 420.dp)`; Start button `testTag("splash_start")`)
- Modify: `presentation/profile/ProfileSelectScreen.kt`, `presentation/profile/NamePromptScreen.kt` (headers `heightIn(min = 72.dp)`; avatar grid adaptive)
- Modify: `presentation/theme/Type.kt` (child styles per 10_UI_IMPLEMENTATION_GUIDE :20-24: displayLarge 40, headlineLarge 28, titleMedium 22, bodyLarge 24, labelLarge 18; Lexend)
- Test: additions to `screenshot/LayoutMatrixTest.kt` (`complete_continueVisible`, `splash_startVisible`, `nameprompt_letsPlayVisible`, each with a font-scale 1.3 variant)

## Tests
| Test file | Test | Assertion |
|---|---|---|
| LayoutMatrixTest | `complete_continueVisible` | as written in the plan |
| LayoutMatrixTest | `complete_continueVisible_fontScale13` | as written in the plan |
| LayoutMatrixTest | `splash_startVisible` | as written in the plan |
| LayoutMatrixTest | `splash_startVisible_fontScale13` | as written in the plan |
| LayoutMatrixTest | `nameprompt_letsPlayVisible` | as written in the plan |
| LayoutMatrixTest | `nameprompt_letsPlayVisible_fontScale13` | as written in the plan |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(ui): complete, splash and profile screens fit every phone; child text styles (NFR-ACC-02)`

Decisions used: none new.
