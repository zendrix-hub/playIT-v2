# 25 — Duolingo Kids Visual Language Upgrade

> Additive to `03_DESIGN_SYSTEM_SUMMARY.md`, `10_UI_IMPLEMENTATION_GUIDE.md`, and `23_DUOLINGO_ABC_UI_REFRESH.md`. Nothing here changes a color hue, a type size, the 64dp touch-target floor, gating logic, or pedagogy. This is the "make it feel like a toy world, not a toy form" pass, sitting on top of the already-implemented gummy system (`GummyContainer`, `GummyButton`, `GummyStaticContainer`, `GummyIconButton`, `PediatricButton`).

## ⚠️ Sync note before implementing anything in this file

Two things Antigravity should reconcile before touching new code:

1. **`Color.kt`, not `03 §2`, is the source of truth for hex values.** The shipped `Color.kt` already went through the WCAG-contrast pass `03 §5.1` calls for — `LearningBlue` is `#1D62B4` (not the `#4A90E2` printed in the `03` table), `GrowthGreen` is `#2E7D32`, `EnergyOrange` is `#D97706`, `FriendlyPurple` is `#6B5BCE`, `GentleCorrectionOrange` is `#FF8F00`, and `TextSecondary`/`BorderColor`/`DisabledColor` have all been retuned. `DestructiveRed` (`#B3261E`) is already present and already scoped to system dialogs only, exactly per `03 §5.1`'s resolution. **Every spec below references token names, never hex values, for this reason** — treat any hex printed in `03` as historical, not current.
2. **The map's sky gradient is currently inline, not a token.** `MapScreen.kt`'s background `Brush.verticalGradient` uses `SoftSky → Color(0xFFE0F2FE) → CreamWhite`, with the middle stop hardcoded rather than pulled from `Color.kt`. Section 1 below asks other screens to reuse this exact gradient, so promote that middle stop to a named token first: add `val SkyGradientMid = Color(0xFFE0F2FE)` to `Color.kt` and update `MapScreen.kt` to reference it. Small fix, but it's the one piece of plumbing every background in this doc depends on.

---

## 1. Background system

**Principle:** flat single-color backgrounds (plain `SoftSky` or plain `CreamWhite` fills, as currently used on `SayItScreen`) read as a form. A soft vertical gradient plus a thin ambient decoration layer reads as a place. Every child-facing screen gets a gradient background; only the two adult-only surfaces stay flat.

- **Standard sky gradient** (the token fix above): `SoftSky → SkyGradientMid → CreamWhite`, vertical, top to bottom. This is already correct on `MapScreen` — reuse it verbatim on `HearItScreen`, `SayItScreen`, `FindItScreen`, `BlendItScreen`, `LetterCompleteScreen`, `BlendItCompleteScreen`.
- **Ambient decoration layer**: 2–4 low-opacity floating shapes per activity screen (not the full terrain treatment — that's `MapScreen`-only, see `27`). Small drifting cloud silhouettes or soft sparkle dots, 8–14% opacity, sized 60–140dp, positioned in the screen's dead corners (never overlapping the primary card or CTA). Code-drawn via `Canvas` (soft-edged ellipses / blurred circles) rather than assets — cheap, no asset dependency, and easy to reduced-motion-gate.
- **Motion on the decoration layer**: slow horizontal drift, one full traverse every 18–25s, `LinearEasing`, looping. When `LocalReducedMotion.current == true`, decorations render in a fixed resting position with no animation — never removed entirely, since they still contribute to "place" even static.
- **Onboarding hero treatment** (Splash, ProfileSelect): same gradient, but with a soft radial highlight (a low-opacity `RadialGradient` of `CreamWhite` over the sky gradient, centered where the mascot sits) so Lily reads as spotlit rather than pasted on top. Detailed in `30`.
- **Adult-only surfaces** (`ParentDashboardScreen`, `ReportPreviewScreen`): stay flat `CreamWhite`, no gradient, no decoration. These are utility surfaces for a parent scanning data — introducing "world" visual language here would work against the 7:1-contrast, no-distraction brief already set in `03 §6` / `10 §5`.

## 2. Typography upgrade

Sizes and the Lexend/Andika decision (`03 §5.2`, `10 §2`) are **unchanged** — this section only adds a decorative treatment, and only where it can't hurt reading.

- **Where a soft text shadow is allowed**: `displayLarge` (40sp) used purely decoratively — screen titles like "Word Adventures!", celebration headlines ("Great Job!"), the streak-badge number. Add a new token `DisplayTextShadow = TextPrimary.copy(alpha = 0.18f)`, offset 2dp down / 1dp right, no blur (a hard offset reads as a "sticker," a blurred one reads as muddy at 40sp). This is the one purely cosmetic type addition in this doc.
- **Where a text shadow is forbidden**: anything the child is meant to sound out — `bodyLarge` (24sp) phoneme/word displays, letter cards, `BlendIt` tiles, mascot dialogue. `03 §5.2`'s whole argument for raising the reading floor to 24sp was letterform clarity for early readers; a drop shadow directly undermines that. Do not apply `DisplayTextShadow` to any reading-critical text, full stop.
- **Button labels** (`labelLarge`, 18sp): stay plain, sentence case, no shadow — the white-on-color contrast inside a `GummyButton`/`PediatricButton` face is already strong; a shadow adds noise, not legibility.
- **Weight discipline**: `displayLarge`/`headlineLarge` stay `ExtraBold` (already spec'd). Don't introduce a heavier weight than Lexend's ExtraBold for "more toy feel" — an over-bolded rounded sans starts to blob at large sizes and hurts the single-story-letterform clarity `03 §5.2` cares about.

## 3. Decoration layer system

| Decoration | Screens | Build method | Notes |
|---|---|---|---|
| Drifting clouds (2–3 shapes) | HearIt, SayIt, FindIt, BlendIt, Splash, ProfileSelect | Code (`Canvas`, soft ellipse clusters) | Reuse across all — same 2–3 cloud silhouettes, don't need per-screen variants |
| Firefly / sparkle motes | Map, celebration screens | Code (`Canvas`, small circles with radial-gradient glow, twinkling alpha loop) | Ties to the forest world theme in `27` |
| Terrain props (bamboo, ferns, rocks, flowers) | Map only | **[ASSET]** — organic illustrated shapes are not worth faking in pure vector code | Full list and asset names in `27 §4` |
| Sun/light rays behind mascot | Onboarding hero moments only | Code (`Canvas`, 4–6 low-opacity triangles radiating from mascot anchor, static or very slow rotate) | Subtle — this is a spotlight cue, not a sunburst logo |
| Confetti / celebration particles | Win-state screens only | Code or Lottie — full spec in `29` | Not an ambient background decoration; only fires on win |

**Rule of restraint**: no more than 4 ambient decoration elements visible at once on any activity screen. The point is to keep the background from reading as dead space, not to compete with the activity card, which stays the visual anchor.

## 4. Upgrades to the existing gummy system

The gummy mechanic (`GummyContainer`/`GummyButton`/`GummyStaticContainer`/`GummyIconButton`) is correct and complete as coded — the flat-fill-plus-depth-band-plus-outline-plus-press-spring pattern from `23 §1` is already implemented and stays exactly as-is. Two small refinements to make it read closer to a toy/plastic key:

1. **Inner top highlight on hero elements only.** Add an optional `showHighlight: Boolean = false` parameter to `GummyContainer`. When true, draw a thin (~30% of the face's height, top-anchored) horizontal gradient band inside the face layer — the face color fading to ~18% white, then back to transparent — to suggest a glossy plastic sheen. Apply this only to `GummyIconButton` (mic button, big circular CTAs) and the primary `PediatricButton` on each screen's main CTA. Do **not** apply it to `GummyStaticContainer` (mascot bubble, cards) or to secondary/tertiary buttons — restraint here is what keeps it feeling like an accent instead of a gloss-everything skin.
2. **Proportional stroke width.** `strokeWidth` is currently a flat 3dp default regardless of element size. On elements ≥100dp (the 112dp mic button, 116dp `BlendItChallengeNodeCard`), bump to 4dp; on elements ≤48dp (small badge chips, if any get added per `27 §6`), drop to 2.5dp. The 92dp `LetterMapNodeCard` and standard buttons stay at the existing 3dp. This is a proportion fix, not a new visual language — a 3dp line on a 116dp circle already reads slightly thin next to a 3dp line on a 220dp `SayIt` status card. [CHECK WITH AGENT: confirm 2.5dp renders cleanly at typical device density; if not, floor it at 3dp and skip the small-element case.]

Nothing about `depthHeight` (6dp default), `DarkBrownOutline` as the universal stroke color, or the press-into-depth spring parameters changes.

## 5. Screen-by-screen background spec

- **`SplashScreen`**: Sky gradient + radial spotlight highlight behind Lily's entrance position + 2 drifting clouds, low opacity. No terrain. This is the very first frame of the world, so it should feel airy and inviting, not busy.
- **`ProfileSelectScreen` / `NamePromptScreen`**: Sky gradient + spotlight (softer, since the `ProfileCard` grid is the visual anchor here, not the mascot) + 2 drifting clouds. `AvatarPicker` grid sits on plain `CreamWhite` cards so the curated avatar art stays legible against a busier backdrop.
- **`MapScreen`**: Full world treatment — sky gradient (already correct) + firefly motes + the complete terrain-prop layer from `27`. This is the one screen that earns the richest background because it's the "place" the child returns to every session.
- **`HearItScreen`**: Sky gradient + 2 drifting clouds. Background stays calm and uncluttered — the point of this screen is focused listening, so competing visual motion is minimized relative to Map.
- **`SayItScreen`**: Sky gradient + 2 drifting clouds (already partially present via the existing `Brush.verticalGradient` — extend it to the full 3-stop token gradient per the sync note above).
- **`FindItScreen`**: Sky gradient + 1–2 sparkle motes only — this screen already has five picture cards competing for attention, so the ambient layer should be the lightest touch of any activity screen.
- **`BlendItScreen`**: Sky gradient + 2 drifting clouds. Keep the letter-tile bank and slot row as the clear focal band; decorations stay in the upper third of the screen only.
- **`LetterCompleteScreen` / `BlendItCompleteScreen`**: Sky gradient + confetti (win-state only, see `29`) + sun rays behind Lily during her celebration beat. This is the one place the background is allowed to get genuinely busy, because it's a short, bounded, high-reward moment rather than sustained screen time.
- **`ParentDashboardScreen` / `ReportPreviewScreen`**: Flat `CreamWhite`, no gradient, no decoration — unchanged from shipped.
