# 23 — Duolingo ABC-Inspired UI Refresh

> Additive to `03_DESIGN_SYSTEM_SUMMARY.md` and `10_UI_IMPLEMENTATION_GUIDE.md` — does not replace them. Every hard rule from those docs still applies unchanged: no red/flashing for errors, 64dp child touch-target floor, text+audio always paired, WCAG contrast floors, Lexend/Andika for reading-critical text. This document only changes *how playful and dimensional the shapes, buttons, and motion feel* — it does not touch pedagogy, gating logic, or accessibility rules.

## 0. Why

The shipped system (`03`) is a clean, correct Material 3 build — but Material 3's default flat-elevation cards and pill buttons read as "generic app," not "toy for a 6-year-old." Duolingo ABC's visual signature is specific and reproducible: **chunky, gummy, 3D-pressable shapes** with a toy/plastic-button quality, thick consistent stroke outlines, a wobbly hand-drawn path instead of a straight line, and mascots that occupy real screen weight rather than a small corner icon. This doc translates that signature into concrete, buildable Compose specs on top of the existing token set.

## 1. The signature element

**The gummy button.** Every primary interactive surface (buttons, letter tiles, map nodes, picture cards) gets a solid flat fill on top and a **4–6dp darker "depth" band of the same hue** along the bottom edge, mimicking a pressable plastic key. On tap, the element moves down into its own depth band (translateY + depth shrinks to ~1dp) and springs back. This one mechanic, applied consistently everywhere, is what makes Duolingo ABC feel like a toy rather than a form. Nothing else in this doc is more important than getting this right and applying it everywhere.

## 2. Color — depth variants (extends `03 §2`, does not replace it)

Add a **-20% luminance "shadow" token** for every existing role, used only for the gummy bottom-depth band and never as a standalone fill:

| Role | Face (unchanged from `03`) | Shadow (new) |
|---|---|---|
| Learning Blue | `#4A90E2` | `#2F6FBF` |
| Growth Green | `#4CAF50` | `#357A38` |
| Achievement Gold | `#FFC107` | `#C99000` |
| Energy Orange | `#FF9800` | `#C97200` |
| Friendly Purple | `#8E7DF2` | `#6656C9` |
| Gentle Correction Orange | `#FFB74D` | `#D9922E` |

No new hues, no red introduced for gameplay (the restricted destructive-red token from `03 §5.1` is untouched and stays confined to system dialogs). Backgrounds (Soft Sky, Cream White) stay flat — depth styling is reserved for things a child taps, not for containers.

## 3. Shape language (extends `03 §4`)

- Raise corner radius across the board: Learning Card 24dp → **28dp**, buttons 28dp → **32dp** (near-pill), letter tiles and map nodes become **full circles**, not rounded squares.
- Outline every child-facing tappable shape with a **3dp Text Primary (`#2D3748`) stroke** — this is the biggest single Duolingo-ABC tell (thick consistent outline on every character/tile/button) and is currently absent from the shipped system.
- Picture cards and letter cards get a very slight **-2° to +2° random static rotation** per card (seeded by card index, not runtime-random, so it's stable across recompositions) to break up the grid and read as hand-placed rather than machine-laid-out.

## 4. Motion (extends `03 §4` / `10 §4`)

| Interaction | Old spec | Refresh |
|---|---|---|
| Button/tile tap | scale 100→92→100 | **press-into-depth**: translateY 0→+5dp while depth band compresses to 1dp, `MediumBouncy` release |
| Active map node | breathing pulse | breathing pulse **+ idle bounce** (small -6dp translateY loop, 900ms, only on the current unlocked node — signals "tap me" the way Duolingo's path does) |
| Correct answer | green flash | gummy button briefly **squashes wide** (scaleX 1.08/scaleY 0.94) before the existing star/confetti reward, plus the flash |
| Screen mascot entrance | fade + slight upward | mascot **hops in** from off-canvas bottom on first screen load only (once per screen visit, not on every recomposition) — reduced-motion mode replaces this with the existing plain fade per `03 §6` |

All existing damping/stiffness values in `10 §4` still apply to the added animations; the refresh only adds new states, it doesn't change the physics constants already specified.

## 5. Mascot sizing (extends `03 §6`)

Shipped system treats the mascot as a bubble/corner element. Refresh: on `HearItScreen`, `SayItScreen`, `FindItScreen`, and both Complete screens, the mascot occupies **~25–30% of vertical screen real estate**, anchored bottom, with the speech bubble emerging from it rather than floating independently — matching Duolingo ABC's "mascot as co-player" framing rather than "mascot as tooltip." `MapScreen`'s mascot stays smaller/contextual since the path itself is the hero there.

## 6. Explicitly unchanged

- Typography scale, sizes, and Lexend/Andika choice (`10 §2`) — untouched.
- 64dp child touch-target floor (`03 §5.3`) — the gummy depth band is *within* that footprint, not additive to it; a 64dp button is still 64dp including its shadow band.
- Error/incorrect state color and language rules (`03 §2`, `03 §6`) — untouched; "gentle squash" motion above applies only to *correct* answers.
- All gating, hearts, stars, and pedagogy logic in `01_REQUIREMENTS_SUMMARY.md` — this is a visual-layer doc only.

## 7. Build order recommendation

If resourcing this incrementally rather than all at once: (1) gummy button component + depth-band shadows first, since every other screen consumes it; (2) map node circles + idle bounce; (3) mascot resizing on the three activity screens; (4) card rotation/outline polish last, since it's the smallest visual delta.
