# Card 31e: The map's one header card, 16 sp text, 64 dp buttons and no harsh red (NFR-ACC-02)

Status: done

Part of card 31 (`card-31-post-midterm-polish.md`), team directive 2026-10-13.

## Why
- **Crowded header:** on 360x640 the stats card, the greeting and the unit banner filled about half the screen.
- **Small text:** map labels were 9.5 to 15 sp.
- **Small buttons:** this card's review also found four tap targets under 64 dp, which card 30's audit had missed:
  - the back button (46 dp)
  - the guidebook button (44 dp)
  - the two dialog close buttons (32 and 36 dp)
  - the dialogs' main buttons (52 to 56 dp)
- **Harsh red:** the church dome in the background was `0xFFDC2626`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/map/MapScreen.kt`
- Modify: `presentation/map/components/TopStatsBar.kt`
- Modify: `presentation/map/components/MarungkoGroupBanner.kt`
- Modify: `presentation/map/components/NodeActionPopupDialog.kt`
- Modify: `presentation/map/components/UnitGuidebookDialog.kt`
- Modify: `presentation/map/components/ChocolateHillsBackground.kt`
- Add: `presentation/map/MapTextFloorTest.kt`
- Modify: `presentation/map/components/TopStatsBarLayoutTest.kt`
- Modify: `screenshot/LayoutMatrixTest.kt`

## Changes
1. **One header card (`TopStatsBar`):**
   - It holds Lily waving and the greeting ("Hi, Maximilianoooooo!"). The name wraps to two lines rather than being cut off.
   - Below: the letters learned, the streak and star pills, and the progress bar.
   - The whole card, at least 64 dp tall, is Lily's tap target ("Let's go! Tap a letter...").
   - The separate greeting chip is gone.
2. **16 sp text floor:** every map text, its banners and its dialogs is at least 16 sp. The Blend It node badge was 9.5 sp.
3. **64 dp tap targets:**
   - The map back button and the guidebook button are 64 dp.
   - The dialog close buttons have a 64 dp touch area around a 40 dp circle.
   - The dialogs' main buttons are at least 64 dp tall.
4. **No harsh red:** the church dome is a muted terracotta, `0xFFC7764F`.

## Tests
| Test | Checks |
|---|---|
| `MapTextFloorTest.noMapTextUnder16sp` | A source scan of `presentation/map/` |
| `TopStatsBarLayoutTest.longName_greetingIsNotCut` | The 16-character greeting has no visual overflow |
| `TopStatsBarLayoutTest.card_isLilysTapTarget` | At least 64 dp, and a tap calls back |
| `TopStatsBarLayoutTest.nodeDialog_closeIs64dp`, `guidebookDialog_closeIs64dp` | Both close buttons are at least 64 dp |
| `LayoutMatrixTest.map_buttonsAre64dp` (4 sizes) | The back and guidebook buttons |
| `TopStatsBarLayoutTest.longName_pillsStayVisible`, `LayoutMatrixTest.map_unitBannerTitleFits` | Unchanged, still pass |

## Commit
`feat(map): one header card, 16 sp text, 64 dp buttons and no harsh red (NFR-ACC-02)`
