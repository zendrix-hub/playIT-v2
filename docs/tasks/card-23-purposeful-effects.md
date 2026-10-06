# Card 23: Purposeful effects

Status: ready after 22 is accepted

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 7". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- There are no screen transitions (`navigation/NavGraph.kt`).
- Confetti falls from the top instead of bursting at the reward.
- Hearts drop with no feedback.
- Stars appear without the drop-in that makes them feel earned (mockup and 21 :26-28).
- Several effects ignore reduced motion: `shake`, the pulse rings, the BlendIt wobble, the locked-node shake.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `navigation/NavGraph.kt` (`NavHost` enter: `fadeIn(tween(SCREEN_MS)) + slideInVertically(tween(SCREEN_MS)) { it / 40 }`; exit: `fadeOut(tween(SCREEN_MS))`; `EnterTransition.None` and `ExitTransition.None` under reduced motion)
- Create: `presentation/components/FeedbackEffects.kt` (`Modifier.correctPop(trigger)`, `Modifier.heartLossWobble(trigger)`, `Modifier.starDrop(index, visible)`)
- Modify: `presentation/components/CelebrationOverlay.kt` (CONFETTI bursts from the centre, at most 24 particles; nothing under reduced motion)
- Modify: `presentation/components/ShakeModifier.kt` (no-op under reduced motion)
- Modify: `presentation/components/LessonTopBar.kt` (hearts use `heartLossWobble` when the count drops)
- Modify: `presentation/components/PediatricComponents.kt` (`StarDisplay` uses `starDrop`)
- Modify: `presentation/map/MapScreen.kt` (locked-node shake honours reduced motion)
- Test: `presentation/components/FeedbackEffectsTest.kt` (pure curves), `presentation/theme/PlayItMotionTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| PlayItMotionTest | `durationsInsideAnimationGuide` | as written in the plan |
| PlayItMotionTest | `reducedMotionSnaps` | as written in the plan |
| FeedbackEffectsTest | `heartWobble_reducedMotionIsStill` | as written in the plan |
| FeedbackEffectsTest | `heartWobble_movesThenSettles` | as written in the plan |
| FeedbackEffectsTest | `correctPop_peaksAt108Percent` | as written in the plan |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(ui): purposeful effects: screen transitions, correct pop, heart wobble, star drop, centre confetti`

Decisions used: effects on meaningful moments only (user decision 2026-10-06).
