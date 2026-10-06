# Card 18: Adaptive layout foundation and the device matrix

Status: ready after 17b is accepted

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 2". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
**Dry-run note (Claude, 2026-10-06):** the plan's `GummyContainer` code was replaced after a dry run. Use the custom `Layout` version now in plan Task 2. The test files in the plan are complete and compiled. With cards 17, 17b and this card applied, the full suite was 260 tests, 0 failed, and the 4 scaffold sizes and 5 screens rendered correctly.

- `GummyContainer` draws its face with `matchParentSize()`, so content can never size it. Every button and card is stuck at its minimum, and content spills out. Examples: the Blend It card (176 dp); the Find It "Hear" pill, whose `wrapContentWidth()` collapses to 0 width.
- There are no dimension tokens: 164 hard-coded `fontSize` values and fixed card sizes such as `LetterCard` 280x290.
- `MascotSpeechHeader` takes about 260 dp on a 360-wide screen: an 86x98 dp mascot plus 24 sp text in a 132 dp column.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `presentation/components/GummyButton.kt` (`GummyContainer` measuring only; `GummyButton` stays the same)
- Create: `presentation/theme/Dimens.kt`
- Modify: `presentation/theme/Theme.kt` (provide `LocalPlayItDimens`)
- Create: `presentation/components/LessonScaffold.kt`
- Modify: `presentation/components/MascotSpeechHeader.kt` (sizes from dimens, `maxLines`, ellipsis)
- Modify (only `.height(N.dp)` -> `.heightIn(min = N.dp)` on a `GummyButton` or `GummyContainer`): `presentation/hearit/HearItScreen.kt`, `presentation/blendit/BlendItScreen.kt`, `presentation/blendit/BlendItCompleteScreen.kt`, `presentation/lettercomplete/LetterCompleteScreen.kt`, `presentation/findit/FindItScreen.kt`, `presentation/sayit/SayItScreen.kt`, `presentation/splash/SplashScreen.kt`, `presentation/components/FindItGrid.kt`, `presentation/components/LetterCard.kt`, `presentation/profile/ProfileSelectScreen.kt`, `presentation/profile/components/AddProfileButton.kt`, `presentation/profile/components/ProfileCard.kt`, `presentation/map/components/NodeActionPopupDialog.kt`, `presentation/map/components/UnitGuidebookDialog.kt`, `presentation/dashboard/ParentDashboardScreen.kt`, `presentation/dashboard/components/ArithmeticGuardDialog.kt`, `presentation/dashboard/components/ProfileSwitcherDropdown.kt`. Leave a fixed height that is not on a gummy component alone. If another file needs the change, name it in the commit body.
- Create: `app/src/test/java/com/playit/app/screenshot/Devices.kt` (qualifier constants + `WithFontScale`)
- Test: `app/src/test/java/com/playit/app/presentation/theme/DimensTest.kt`
- Test: `app/src/test/java/com/playit/app/presentation/components/GummyContainerLayoutTest.kt`
- Test: `app/src/test/java/com/playit/app/screenshot/LayoutMatrixTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| DimensTest | `compactUnder700dpTall` | as written in the plan |
| DimensTest | `a21sIsRegular` | as written in the plan |
| DimensTest | `tabletIsWide` | as written in the plan |
| DimensTest | `childTargetsNeverBelow64dp` | as written in the plan |
| DimensTest | `textNeverBelow16sp` | as written in the plan |
| GummyContainerLayoutTest | `contentTallerThanMin_growsContainer` | as written in the plan |
| GummyContainerLayoutTest | `wrapContentWidth_isNotZero` | as written in the plan |
| GummyContainerLayoutTest | `smallContent_keepsMinSize_andIsCentered` | as written in the plan |
| LayoutMatrixTest | `lessonScaffold_bottomBarAndBodyVisible` | runs on all 4 sizes |
| LayoutMatrixTest | `fontScale13_mainActionVisible` | all sizes except compact |
| LayoutMatrixTest | `tablet_contentWidthCapped` | content at most 560 dp wide |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(ui): adaptive dimensions, LessonScaffold, GummyContainer grows with content (NFR-ACC-02)`

Decisions used: keep the current style, fix fit; A21s target; 360x640 floor (user decisions 2026-10-06).
