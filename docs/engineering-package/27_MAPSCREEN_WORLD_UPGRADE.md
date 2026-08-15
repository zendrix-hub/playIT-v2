# 27 — Adventure Map: Full World Overhaul

> Builds on the existing `MapScreen.kt` — the winding Bezier path (`MapPathCanvas`), node layer, `TopStatsBar`, `MapTerrainProps`, and ambient mascot companion are all already implemented. This doc gives that structure a committed world identity and fills in the terrain/decoration content `MapTerrainProps` currently has a slot for but no defined contents.

## ⚠️ Sync note

`10_UI_IMPLEMENTATION_GUIDE.md §5` explicitly instructs: *"Use `LazyColumn`/stable keys for the node list... do not render all 35 nodes as a single non-lazy `Column` on low-end hardware."* The actual `MapScreen.kt` renders the node layer as a plain `Column` inside `verticalScroll(rememberScrollState())`, not a `LazyColumn` — every node composes and stays composed regardless of scroll position. This predates this doc and isn't something a visual-layer pass should silently absorb: flag it to Antigravity as a pre-existing performance item to fix (it also affects §7's auto-scroll behavior below, since a non-lazy `Column` can't use `LazyListState.animateScrollToItem` and needs a plain `ScrollState.animateScrollTo(pixelOffset)` instead). [CHECK WITH AGENT: confirm whether the `Column`→`LazyColumn` migration is being scheduled separately, since it changes how nodes access their `Offset` centers for the Bezier path draw and isn't a pure drop-in swap.]

## 1. World theme

**Committed theme: a Philippine forest trail** — bamboo groves, banana leaves, ferns, small footbridges over streams, fireflies at dusk. This is chosen deliberately, not generically: Lily is a Philippine tarsier (native to the Bohol/Visayas region), the design philosophy already commits to "cultural familiarity for Filipino learners" (`03 §1`), and a forest-trail theme gives Lily an actual habitat to walk through rather than an arbitrary backdrop. It also gives concrete, groundable asset direction (bamboo, banana leaf, capiz-lantern-style glow, native flora) instead of a generic "sky" or "space" theme that would sever the mascot from her world.

The path is Lily's forest trail; each `LetterNode` is a small clearing or landmark along it; each `BlendItChallengeNode` is a "puzzle grove" — a slightly larger, more overgrown stop that visually reads as a bigger event.

## 2. Path design

- **Width**: 16dp (up from whatever the current `MapPathCanvas` line weight is — [CHECK WITH AGENT: confirm current value]), rounded line caps and joins.
- **Fill**: new token `TrailTan = Color(0xFFC8A165)` — warm dirt-path tan, sits comfortably between `CreamWhite` and `AchievementGold` in the existing palette's warmth range without duplicating either.
- **Edge**: new token `TrailTanOutline = Color(0xFFA08151)`, derived by the same −20%-per-channel formula already used for every shadow token in `Color.kt` (0xC8→0xA0, 0xA1→0x81, 0x65→0x51) — drawn as a 2dp stroke along both edges of the path, consistent with how every other tappable/decorative element in the app gets an outline.
- **Texture**: a subtle dashed centerline (lighter tint of `TrailTan`, ~40% opacity, 6dp dash / 6dp gap) suggests a footpath rather than a solid ribbon. Purely a `Canvas` stroke effect — `PathEffect.dashPathEffect` — no asset needed.
- Both new tokens need the WCAG pairing check called for by `03 §5.1`'s standing QA rule before ship, even though the path itself isn't text — the check matters for the node/path contrast so locked-vs-path-vs-unlocked states stay visually distinguishable, not for text contrast.

## 3. Node redesign

| State | Visual | Notes |
|---|---|---|
| Locked | `DisabledColor` face, 🔒 glyph, no stroke emphasis, no motion | Unchanged from current `LetterMapNodeCard`/`BlendItChallengeNodeCard` implementation |
| Unlocked, not yet attempted (current active node) | Existing face color (`LearningBlue` for letters, `FriendlyPurple` for BlendIt) + `idleBounce` + `breathingPulse` (both already coded) + **new**: a soft pulsing glow ring | Glow ring: a radial `AchievementGold` gradient ring drawn just outside the node's circle bounds, scaling 1.0→1.15 while fading alpha 0.5→0, looping ~1.4s. This is the "TAP ME" signal — see §5. |
| Unlocked, completed (has stars) | Same face color, existing mini star row (already coded) | No change — the existing star-count display already communicates completion clearly |
| BlendIt node, unlocked/active | Same treatment as letter nodes, `FriendlyPurple`, larger 116dp size (already coded) | Puzzle-grove framing: slightly denser terrain-prop clustering immediately around BlendIt nodes specifically (see §4) so they read as a bigger event on the path without changing their actual size or color logic |

No changes to node sizes (92dp letter / 116dp BlendIt), colors, or the locked/unlocked/completed *logic* — this section is glow + terrain framing only.

## 4. Environment layers

From back to front, per the existing `MapScreen.kt` layer numbering (Layer 1 = terrain props, Layer 2 = path canvas, Layer 3 = mascot, Layer 4 = nodes):

- **Layer 0 (new, behind everything): parallax clouds.** 2–3 large, very-low-opacity (8–12%) cloud silhouettes drifting slowly across the top third of the scrollable canvas. Code-drawn (`Canvas`, soft ellipse clusters, same technique as `25 §1`'s activity-screen clouds) — reuse the same shapes for visual consistency between Map and the activity screens.
- **Layer 1 (existing `MapTerrainProps`, contents defined here):**
  - `[ASSET: terrain_bamboo_cluster_01.png]` / `[ASSET: terrain_bamboo_cluster_02.png]` — vertical bamboo stalk groupings, placed along path edges
  - `[ASSET: terrain_fern_01.png]` — low ground fern, scattered near letter nodes
  - `[ASSET: terrain_banana_leaf_01.png]` — large banana leaf silhouette, corner/edge placement
  - `[ASSET: terrain_rock_01.png]` / `[ASSET: terrain_rock_02.png]` — small mossy rocks, path-edge filler
  - `[ASSET: terrain_flower_hibiscus_01.png]` — small accent flower cluster, sparse placement
  - `[ASSET: terrain_footbridge_01.png]` — small wooden footbridge, placed at 1–2 fixed points where the path crosses itself visually, for landmark variety on longer maps
  - **Firefly motes (code, not asset)**: small (4–6dp) soft-glow circles, `AchievementGold` at low opacity with radial blur, gently twinkling (alpha loop 0.3↔0.9, randomized per-mote phase, ~2–3s cycle), denser near `BlendItChallengeNode`s to reinforce the "bigger event" framing from §3
  - Placement should stay **seeded by node index** (the same convention `23 §3` specifies for card rotation) so terrain positions are stable across recompositions rather than re-randomizing on every scroll/recompose
- **Layer 2**: existing `MapPathCanvas`, updated per §2
- **Layer 3**: existing ambient mascot companion, unchanged positioning logic, now using the `WAVING`/`POINTING`/`IDLE` state set from `26`
- **Layer 4**: existing node layer, updated per §3

## 5. Active node "TAP ME" pointer

Three signals stack on the current active node, deliberately layered rather than any single big effect:

1. **Glow ring** (§3) — the base "something is happening here" cue, always on while a node is the active one.
2. **Mascot proximity** — already implemented (Lily stands beside the active node). Her `MascotState` here should default to `POINTING` on first arrival at a map with a newly-available node, easing to `IDLE` after ~1.5s or first scroll interaction (per `26 §4`'s reaction table), rather than always sitting in `IDLE`.
3. **Small ambient speech bubble** — a lightweight one-line bubble anchored above Lily's head (not the full `DockedMascotWithBubble` used on activity screens — that component is sized for activity-screen real estate, not a small map companion). Short copy only: "Let's go!" / "Tap here!" — auto-dismisses after ~2.5s or on first node tap, whichever comes first.

These three together are the full "TAP ME" language — no additional arrow glyph or flashing needed; stacking glow + companion + bubble is already more than most single-signal implementations and adding a fourth risks visual noise on a screen that also has terrain and path decoration.

## 6. Header bar redesign

`TopStatsBar(totalStars, currentStreak, unlockedBadgesCount)` already exists structurally. Upgrade each stat from plain text to its own small gummy pill so the header reads as a HUD rather than a label row:

- **Stars pill**: `GummyStaticContainer`, `AchievementGold` face, star glyph + bold count, ~40dp height
- **Streak pill**: `GummyStaticContainer`, `EnergyOrange` face, flame glyph + bold count, ~40dp height
- **Badges pill**: `GummyStaticContainer`, `FriendlyPurple` face, badge glyph + bold count, ~40dp height

These are **non-interactive display elements**, so the 64dp touch-target floor (`03 §5.3`) does not apply to their height — that floor governs interactive elements, and a 40dp *display-only* pill is proportionate for a HUD row without eating excessive header space. **If any of these pills becomes tappable later** (e.g., tapping streak opens a streak-detail view), it must grow to the 64dp floor at that point — flag this explicitly in code comments so a future feature addition doesn't silently ship a sub-floor tap target. `GummyStaticContainer` is the correct component choice here specifically because these aren't pressable (per its own doc comment: "no fake onClick bolted on to satisfy a pressable-only API").

## 7. Scroll behavior

Yes — auto-scroll to the active node on `MapScreen` open. Sequence:

1. On composition, after the node list and `nodeCenters` are calculated (existing `remember(mapNodes, widthPx)` block), compute the target scroll offset for `activeNodeIndex` (already identified in the existing `remember` block).
2. Animate the scroll container to that offset using `ScrollState.animateScrollTo(pixelOffset)` (matching the current plain-`Column`-in-`verticalScroll` structure — see the sync note above; this becomes `LazyListState.animateScrollToItem` if/when the `LazyColumn` migration happens).
3. Delay the animation start by ~250–300ms after first composition so it doesn't fire before the terrain/path layers have settled in, then run over ~600–800ms with a standard eased curve (matches the `NoBouncy`/`Medium` screen-transition damping already specified in `10 §4` — a bouncy overshoot on a scroll-to-position reads as janky, not playful).
4. **Reduced motion**: jump instantly to the target offset with no animation when `LocalReducedMotion.current == true`.

This should only fire once per screen-open, not on every recomposition triggered by, e.g., a star count updating — gate it with a `remember { mutableStateOf(false) }` "hasAutoScrolled" flag scoped to the composable's lifecycle, or an equivalent `LaunchedEffect(Unit)` guard.
