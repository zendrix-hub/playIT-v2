# Card 17: Performance and calm motion foundation

Status: accepted

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 1". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- The user's A21s lags.
- `GummyContainer` fires a haptic on every tap (`components/GummyButton.kt:111`).
- `breathingPulse()` and `idleBounce()` start an endless animation *before* checking `enabled` or reduced motion (`components/PulseModifier.kt:17-61`), so disabled pulses still animate every frame.
- `LetterCard` breathes forever (`LetterCard.kt:88`).
- `GummyMotionAsset` floats every picture forever.
- `MascotSpeechHeader` breathes Lily forever (`:67-84`).
- Every image decodes at full 512 px (1 MB) on the main thread (`components/AssetUtils.kt:41-62`), though it is drawn at 70-120 dp.
- The app is not locked to portrait.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `app/src/main/AndroidManifest.xml` (activity `screenOrientation`)
- Modify: `presentation/components/GummyButton.kt` (remove haptic only)
- Modify: `presentation/components/PulseModifier.kt`
- Modify: `presentation/components/GummyMotionAsset.kt`
- Modify: `presentation/components/LetterCard.kt` (remove `.breathingPulse()` and its `isIdleFloating = true` argument only)
- Modify (remove the explicit `isIdleFloating = true` / `isIdleFloating = !isCorrect` argument only): `presentation/components/BlendItCard.kt`, `presentation/components/FindItGrid.kt`, `presentation/components/MascotBubble.kt` (and `LetterCard.kt`, above)
- Modify: `presentation/components/MascotSpeechHeader.kt` (remove breathing only)
- Modify: `presentation/components/AssetUtils.kt` (delegate to `AssetImage.kt`)
- Create: `presentation/components/AssetImage.kt`
- Create: `presentation/theme/PlayItMotion.kt`; Delete: `presentation/theme/Motion.kt` (no code uses it; checked 2026-10-06)
- Delete: `app/src/main/assets/audio/vo/vo_*.mp3` (26 byte-identical duplicates of `audio/ui/vo_*`; no code reference) and `app/src/main/assets/audio/tts_*.mp3` (one stray file, `tts_[exci_20260816_104503.mp3`)
- Test: `app/src/test/java/com/playit/app/presentation/components/AssetImageTest.kt`
- Test: `app/src/test/java/com/playit/app/PerformancePolicyTest.kt`
- Modify (screenshot tests only): `app/src/test/java/com/playit/app/screenshot/HearItScreenshotTest.kt`, `FindItScreenshotTest.kt`, `BlendItScreenshotTest.kt`, `NamePromptScreenshotTest.kt`, `LetterCompleteScreenshotTest.kt`. Wait for `AssetDecodeTracker.isIdle()` before each capture (plan Task 1, Files).

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AssetImageTest | `inSampleSize_512to120_is4` | returns 4 |
| AssetImageTest | `inSampleSize_512to200_is2` | returns 2 |
| AssetImageTest | `inSampleSize_512to512_is1` | returns 1 |
| AssetImageTest | `inSampleSize_neverZero` | returns 1 for a 0 request |
| PerformancePolicyTest | `noHapticFeedbackCalls` | no `performHapticFeedback` in app sources |
| PerformancePolicyTest | `activityIsPortraitOnly` | manifest has `screenOrientation="portrait"` |
| PerformancePolicyTest | `noIdleFloatingRequestedByCallers` | no caller passes `isIdleFloating = true` or `= !...` |
| PerformancePolicyTest | `noEndlessIdleAnimationOnLetterCardOrMascotHeader` | no `breathingPulse(` in LetterCard, no `rememberInfiniteTransition` in MascotSpeechHeader |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`perf(ui): downscaled async images, no haptics, no endless idle animation, portrait (NFR-PERF-01)`

Decisions used: remove haptics; effects on meaningful moments only; portrait only; A21s is the target phone (user decisions 2026-10-06).
