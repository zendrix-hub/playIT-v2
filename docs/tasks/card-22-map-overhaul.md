# Card 22: Map overhaul

Status: done

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 6". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- The header (stats bar plus a 3-5 line greeting) takes about 230 dp of a 640 dp screen.
- The trail is a faint 4 dp grey dash (`MapPathCanvas.kt:43-83`).
- Node x positions use a fixed 50 dp amplitude, so the path is narrow on tablets.
- 6 companion animals float forever.
- `measuredCenters` is remembered without a key (`MapScreen.kt:287`).
- A long name pushes the stat pills off-screen (`TopStatsBar.kt:109`).
- Coming back from a lesson shows no unlock moment.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Create: `presentation/map/MapLayout.kt` (pure math)
- Modify: `presentation/map/MapScreen.kt`:
  - the greeting becomes a one-line Lily chip that speaks the full greeting on tap;
  - nodes use `d.mapNodeSize`;
  - x offsets come from `MapLayout`;
  - `measuredCenters` is keyed on `mapNodes`;
  - companions: only the player's avatar beside the current node, static;
  - the current node keeps its pulse ring (decision 4);
  - unlock moment;
  - `testTag("map_current_node")`.
- Modify: `presentation/map/components/MapPathCanvas.kt` (rope trail: 10 dp `Color(0xFFC9A66B)` with a 3 dp `DarkBrownOutline` edge, rounded caps, dash 18/12 dp; completed segments solid `EmeraldLeaf`)
- Modify: `presentation/map/components/ChocolateHillsBackground.kt` (build all paths in `drawWithCache`, so scrolling doesn't rebuild them)
- Modify: `presentation/map/components/TopStatsBar.kt` (name `weight(1f)`, `maxLines = 1`, `TextOverflow.Ellipsis`; `testTag("stats_pills")` on the pill row)
- Modify: `presentation/map/components/MapCompanionFriends.kt` (static avatar beside the current node; no infinite transitions)
- Modify: `presentation/map/MapViewModel.kt` (`newlyUnlockedNodeId: StateFlow<String?>`, set when a node's state goes locked -> unlocked between two loads; `fun onUnlockShown()`)
- Test: `presentation/map/MapLayoutTest.kt`, `presentation/map/components/TopStatsBarLayoutTest.kt`, additions to `presentation/map/MapViewModelTest.kt` and `screenshot/LayoutMatrixTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| MapLayoutTest | `offsetsStayInsideTheWidth` | as written in the plan |
| MapLayoutTest | `widerScreen_widerPath` | as written in the plan |
| TopStatsBarLayoutTest | `longName_pillsStayVisible` | Review Focus 3 |
| MapViewModelTest | `unlockBetweenLoads_setsNewlyUnlocked` | as written in the plan |
| MapViewModelTest | `onUnlockShown_clears` | as written in the plan |
| LayoutMatrixTest | `map_currentNodeVisible` | as written in the plan |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(map): clear rope trail, compact header, scales to every phone, unlock moment (FR-01)`

Decisions used: the current map node keeps its pulse; effects on meaningful moments only (user decisions 2026-10-06).
