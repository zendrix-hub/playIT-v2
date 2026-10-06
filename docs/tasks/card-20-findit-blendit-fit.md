# Card 20: Find It and Blend It fit (supersedes card 16)

Status: ready after 19 is accepted

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 4". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- On 360x640 the Find It header takes about 320 dp, so only one row of the grid shows.
- The Find It cards are a fixed 124 dp.
- The found/hear row overflows at font scale 1.3.
- The Blend It card clips its second line (card 16's finding).
- The Blend It tiles are a fixed 68 dp in a non-wrapping Row.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `presentation/findit/FindItScreen.kt` (`LessonScaffold`; found/hear row `FlowRow` or two weighted cells; grid in the body; `testTag("findit_grid")`)
- Modify: `presentation/components/FindItGrid.kt` (card height `heightIn(min = d.findItCardMinHeight)` + `aspectRatio(d.findItCardAspect)` within a 2-column grid; word text `maxLines = 1`; picture `maxSize = 120.dp`)
- Modify: `presentation/blendit/BlendItScreen.kt` (`LessonScaffold`; slots and tiles `d.tileSize` in a `FlowRow` with 8 dp spacing; `testTag("blendit_tiles")`)
- Modify: `presentation/components/BlendItCard.kt` (picture box `80.dp`, gold circle `72.dp`, image `76.dp`, spacer `4.dp`; card 16's exact fix)
- Test: `presentation/components/BlendItCardLayoutTest.kt` (card 16's test, unchanged)
- Test: additions to `screenshot/LayoutMatrixTest.kt`
- Bookkeeping: set `docs/tasks/card-16-blendit-card-fit.md` to `Status: superseded by card 20`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| BlendItCardLayoutTest | `secondLine_staysInsideCard` | card 16's test |
| LayoutMatrixTest | `findIt_wholeGridVisible` | as written in the plan |
| LayoutMatrixTest | `findIt_fontScale13` | as written in the plan |
| LayoutMatrixTest | `blendIt_tilesVisible` | as written in the plan |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`fix(ui): Find It and Blend It fit every phone; Blend It card fits its text (FR-05, FR-13)`

Decisions used: none new; supersedes card 16.
