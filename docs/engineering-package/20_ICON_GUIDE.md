# 20 — Icon Guide

## 1. Required Icons (see `14_ASSET_MANIFEST.md §3` for the count summary)

| Icon | States | Used in |
|---|---|---|
| Back | 1 | Every non-root screen's top bar |
| Close | 1 | Dialogs (arithmetic gate, confirmations) |
| Home/Map | 1 | Return-to-map affordance |
| Play | 1 | Hear It / Blend It word audio |
| Replay-with-count | 1 (+ dynamic count overlay) | Hear It's `ReplayCounter` |
| Microphone | 2 (idle, listening/active-pulse) | Say It |
| Correct indicator | 1 | Green checkmark, Find It/Blend It feedback |
| Incorrect indicator | 1 | Gentle "try again" glyph — **not an X or harsh cross shape**, per `03_DESIGN_SYSTEM_SUMMARY.md`'s no-punishment-visuals rule; recommend a soft circular-arrow "retry" glyph instead |
| Lock | 1 | Locked map nodes |
| Export/download | 1 | PDF export button |
| Parent/profile | 2 | Parent Dashboard entry, Profile switcher |
| Noise indicator | 2 (quiet/loud) | Say It's `NoiseLevelIndicator` |

## 2. Style

- Consistent with `16_ILLUSTRATION_STYLE_GUIDE.md`'s rounded, thick-outline visual language — icons should look like they belong to the same family as the character/picture illustrations, not a separate generic icon-font aesthetic.
- Filled style (not outline-only) for primary/active states, to maximize legibility at small sizes for early readers who are still building visual-discrimination skill (the exact skill Find It is teaching) — an outline-only icon set risks being harder to parse than a solid, high-contrast filled shape.
- Single accent color per icon, drawn from the Design System palette (`03 §2`) per its semantic role (e.g., correct-indicator in Growth Green, lock in Text Secondary gray) — never red for the incorrect indicator (`03 §5.1`).

## 3. Sizing

- Minimum rendered size 24dp for icons that sit *inside* a larger 64dp+ touch target (e.g., the play icon inside the `PlayButton`) — the icon itself doesn't need to be 64dp, the tappable container around it does (`03 §5.3`, `10 §3`).
- Icons that are themselves the entire tappable element (e.g., back button) must have a full 64dp touch target on child-facing screens, 48dp on adult-facing Parent Dashboard/Report Preview screens (`10 §3`).

## 4. Usage Rules

- Icons never stand alone as the only signal for a state change — always paired with color and, where applicable, motion/animation (`03 §6` dual-coding rule).
- The incorrect-indicator glyph specifically must avoid anything resembling a red X, consistent with the Design System's explicit "no large red X, no punishment visuals" rule (`03 §2`).

## 5. Material 3 Compatibility

- Build as vector drawables (`ImageVector`/`VectorDrawable`) rather than raster PNGs where possible, for clean scaling across density buckets and easy tinting to match the app's `ColorScheme` roles.
- Icons used inside Material 3 components (buttons, top app bars) should respect M3's default icon-sizing conventions (typically 24dp intrinsic size) even though their *touch target* padding follows this app's larger 64dp rule (`10 §3`) — the visual icon and its tappable bounds are sized independently.

## 6. Accessibility

- Every icon-only interactive element ships a content description (`contentDescription` in Compose) for screen-reader support, even though the primary target user is pre-literate — this matters for the Parent Dashboard surfaces and for any future accessibility-tooling audits.
- Maintain the dual-coding rule from `03 §6`/`16 §7`: no icon is the sole carrier of meaning.
