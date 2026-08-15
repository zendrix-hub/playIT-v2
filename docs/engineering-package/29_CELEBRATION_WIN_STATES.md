# 29 — Win State System: Full Celebration Pattern

> Builds on the already-implemented `StarDisplay` (staggered `popIn`), `HeartBar`, and the confetti/sound cues already named in `03 §4`/`03 §6`. This doc sequences those pieces into a full completion beat and specs the pieces that don't exist yet (confetti system, streak reveal, map unlock moment).

## 1. Completion screen redesign (`BlendItCompleteScreen`, same pattern applies to `LetterCompleteScreen`)

Layout: Lily centered, large scale (30–35% of vertical space — her biggest moment in the app, per `26 §2`), positioned above the star/streak stack rather than docked to an edge like activity screens. Sequenced beats, timed from screen entry:

| Time | Beat |
|---|---|
| 0–150ms | Background crossfades from the standard sky gradient to a slightly warmer variant (blend toward `SkyGradientMid`/`AchievementGold` at low opacity) — a soft "spotlight" shift, not a hard cut |
| 150–500ms | Lily hops in — reuse `DockedMascotWithBubble`'s existing hop-in spring (`DampingRatioLowBouncy`/`StiffnessLow`), scaled up for her larger celebration footprint |
| 500–1100ms | Stars pop in, staggered — this is already built (`StarDisplay`'s `popIn(delayMillis = (i - 1) * 120)`); no change needed, just confirm it fires in this position in the sequence |
| 900–1500ms | Confetti burst fires (§4 below) — overlaps the tail of the star pop-in rather than waiting for it to fully finish, so the two reward beats feel connected rather than sequential |
| 1500–2100ms | Streak badge reveals (§5) — deliberately *after* stars, not simultaneous, so the primary reward (stars) reads clearly before the secondary one (streak) arrives |
| 2100ms+ | `Continue`/`Next` CTA (`PediatricButton`) fades/slides up into place, now tappable |

Total runtime to interactive ≈ 2.0–2.2s. This is longer than any single motion duration in `03 §4`'s Celebration band (600–1200ms) — that's expected and not a conflict, since that band governs individual animations, not a composite multi-beat sequence built from several of them chained together.

**Reduced motion**: collapse the sequence — stars and streak badge appear via simple fades (no pop/bounce/hop), confetti is replaced by a single static sparkle graphic behind the star row (not omitted entirely — the "you did it" visual payoff still needs *something* fixed in its place, per `03 §6`'s reduced-motion rule of swapping bounce/particle effects for fades rather than deleting the moment). Total reduced-motion runtime can compress to ~800ms.

## 2. In-session correct-answer celebrations (lighter touch, not full completion barrage)

Every correct tap already triggers: mascot cut to `CELEBRATING` (`26 §4`), reward chime (`03 §6`), and the tapped element's own squash reaction (`isSquashed`, already wired). Add one small addition: a **localized sparkle burst** — 6–10 small star/dot particles bursting outward from the tapped element's center, ~400ms, fading as they travel — rather than a full-screen confetti burst.

**This restraint is deliberate.** Reserving full-screen confetti for lesson/word-level completion (§1) and keeping in-session correct-answer feedback to a smaller localized burst prevents celebration fatigue — if every single correct tap gets the same full-screen treatment as finishing a word, the biggest reward in the sequence stops feeling biggest. The size of the celebration should track the size of the achievement.

## 3. Star earn animation

Already implemented via `StarDisplay`'s `.popIn(delayMillis = (i - 1) * 120)` — this doc specs the motion that modifier is expected to produce, since its implementation wasn't among the uploaded files:

- Scale: 0 → 1.15 (overshoot) → 1.0 (settle)
- Alpha: 0 → 1 over the first ~200ms
- Spring: `MediumBouncy`/`Low`, consistent with the tap-feedback spring already used elsewhere
- Total single-star duration: ~400–500ms
- Stagger: 120ms per star (already parameterized correctly in the existing call site)

[CHECK WITH AGENT: confirm the actual `popIn` extension function (not among the uploaded files) matches this spec — if it currently only fades without the scale-overshoot, that's the gap to close.]

## 4. Confetti system

Two viable approaches — recommend starting with the first, since it adds no new dependency and matches the "extend, don't replace" instruction governing this whole refresh:

**Option A — Compose `Canvas` particle system (recommended default).** 30–50 small rect/circle particles in the existing brand palette (`LearningBlue`, `GrowthGreen`, `AchievementGold`, `EnergyOrange`, `FriendlyPurple` — reusing tokens ties the celebration visually back to the rest of the app rather than introducing new one-off colors). Each particle: random starting x across the top of the screen, falls under a simple simulated gravity + horizontal drift, rotates while falling, fades out over a 1200–1800ms lifespan. No external dependency, fully code-driven, trivial to gate behind `LocalReducedMotion`.

**Option B — Lottie asset from the free LottieFiles library.** More visual polish with less custom animation code, at the cost of one new dependency (`com.airbnb.android:lottie-compose`). Still compatible with "zero network after install," since a downloaded `.json` bundled as a local asset requires no runtime network call. Worth adopting later if Option A's particle system doesn't read as rich enough in practice — not a blocker for initial implementation. [CHECK WITH AGENT: confirm whether adding the Lottie Compose dependency is acceptable under the project's current dependency policy before committing to Option B.]

Either option: confetti is a win-state-only effect (§1, §2's smaller version) — never an ambient/idle decoration.

## 5. Streak badge reveal

Timing: fires after the star pop-in completes (§1's sequence table). Motion: scale-in 0.8 → 1.05 → 1.0 (spring, `MediumBouncy`/`Low`) combined with a small settle-rotation (−8° → 0°) rather than a flat scale, so it reads as "dropping into place" rather than just growing. Visual: `EnergyOrange`-faced `GummyStaticContainer` pill, flame glyph + streak count, consistent with the same pill treatment introduced for the map header in `27 §6`.

## 6. "Lesson unlocked" moment on the map

When the child returns to `MapScreen` after a completion and a previously-locked node has become available, that node gets a one-time unlock animation rather than silently appearing in its unlocked state on next render:

1. Brief radial flash/ring expands outward from the node's position — `AchievementGold`, scale 0 → 2.5, alpha fading out, ~500ms
2. Node crossfades from `DisabledColor`/🔒 to its real unlocked face color + symbol during the flash
3. Small settle-bounce (scale 1.0 → 1.1 → 1.0) as the crossfade completes

This needs a one-shot trigger — the animation should play once, the first time the map renders with that node newly unlocked, not replay every time the child revisits the map afterward. That requires a small piece of state to track "which node (if any) just unlocked," most naturally passed as a navigation argument from the completion screen or a one-shot flag on the map's `ViewModel` that clears itself once consumed. [CHECK WITH AGENT: this needs a navigation/ViewModel decision beyond what a pure visual spec can settle — flagging the requirement, not prescribing the plumbing.]

## 7. Lottie file list (if pursuing Option B, or for any team member wanting reference material)

Search LottieFiles.com's free library (filter results to the "Free" license tag — the site mixes free and paid results by default) for:

- **"confetti burst"** / **"party popper"** — primary completion-screen effect candidates
- **"star pop"** / **"star burst"** — reference/alternative to the code-driven `StarDisplay` motion in §3, useful even if not adopted directly, as a motion-timing reference
- **"celebration burst free"** — broader net for completion-moment effects
- **"fireworks small"** — an alternative/supplemental burst style, lighter-weight than full confetti, worth checking as an option for the in-session smaller celebration (§2) rather than only the full completion screen
- Avoid searching branded terms ("Duolingo confetti") — search for the generic effect type instead, both because branded searches return fewer usable free results and to keep the asset trail clean of any specific competitor's proprietary output for a thesis submission.
