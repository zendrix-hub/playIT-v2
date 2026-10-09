# Card 19: Hear It and Say It fit, plus the Say It mic states (FR-03)

Status: done

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 3". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- On 360x640 the Hear It play button is below the fold (content about 830 dp).
- The Say It mic is below the fold (content about 1000 dp; mic ring 180 dp).
- The Say It feedback banner sits below the fold.
- The legacy letter card at 116 dp overflows.
- The listening mic is red (`CoralBerry`, `SayItScreen.kt:322,331`), against 03 :24.
- There is no Processing or Heard state, and children hesitated at the mic in Round 1 (F-02).

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `presentation/hearit/HearItScreen.kt` (use `LessonScaffold`; letter card height from dimens; play button `d.primaryCta`; `testTag("hearit_play")`)
- Modify: `presentation/components/LetterCard.kt` (`fillMaxWidth()` + `heightIn(max = d.letterCardHeight)` + `aspectRatio(0.97f, matchHeightConstraintsFirst = true)` instead of 280x290; picture `maxSize = 200.dp`; letter text `maxLines = 1`)
- Modify: `presentation/sayit/SayItScreen.kt` (use `LessonScaffold`; mic from `MicButton`; feedback banner in the scaffold's bottom bar above the Next button; `testTag("sayit_mic")`)
- Modify: `presentation/sayit/SayItViewModel.kt` (`micStatus` flow; reset on screen hidden)
- Create: `presentation/sayit/MicStatus.kt`, `presentation/sayit/components/MicButton.kt`
- Test: `presentation/sayit/MicStatusTest.kt`, additions to `presentation/sayit/SayItViewModelTest.kt`, additions to `screenshot/LayoutMatrixTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| MicStatusTest | `idle` | as written in the plan |
| MicStatusTest | `listeningSilent` | as written in the plan |
| MicStatusTest | `listeningHeard` | as written in the plan |
| MicStatusTest | `correct` | as written in the plan |
| MicStatusTest | `incorrect` | as written in the plan |
| SayItViewModelTest | `partialSpeech_setsHeard` | as written in the plan |
| SayItViewModelTest | `recognizerStoppedExternally_returnsToIdle` | Review Focus 2 |
| LayoutMatrixTest | `hearIt_playVisible` | as written in the plan |
| LayoutMatrixTest | `sayIt_micVisible` | as written in the plan |
| LayoutMatrixTest | `sayIt_micVisible_fontScale13` | Review Focus 1 |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(sayit): Hear It and Say It fit every phone; mic shows listening, heard and result (FR-03)`

Decisions used: mic states Idle/Listening/Heard/Result with a time-based ripple; the voice-driven ripple is post-Round-2 (user decision 2026-10-06, plan decision 8).
