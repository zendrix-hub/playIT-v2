# Agent Prompt — MapScreen Path & Terrain Refresh

You're closing a real gap: the current shipped `MapScreen` is a straight vertical line of
flat circles on a plain gradient. The target is a Duolingo-ABC-style winding path over an
illustrated background. This was already scoped in the existing docs but never built — you
are not inventing new direction, you're implementing what `14_ASSET_MANIFEST.md §2` and
`15_IMAGE_GENERATION_PROMPTS.md §5` already called for.

**Read these files first, in order, before writing any code:**
1. `24_MAP_PATH_VISUAL_REFRESH.md` — this round's spec, full source of truth for what to build.
2. `23_DUOLINGO_ABC_UI_REFRESH.md` — node shape/outline/idle-bounce, already implemented;
   don't rebuild it, just build on top of it.
3. `14_ASSET_MANIFEST.md §2` and `15_IMAGE_GENERATION_PROMPTS.md §1, §5` — asset counts,
   universal generation parameters, and the exact terrain-prop prompt template.
4. `16_ILLUSTRATION_STYLE_GUIDE.md` — every generated or hand-built asset must pass this.

## Hard constraints (same as every prior round — do not relitigate)

- No new colors outside the existing palette (`03 §2`) and the `23 §2` shadow tokens. 
- No changes to node unlock/gating logic, star/heart logic, or any file outside the
  presentation/UI layer.
- 64dp touch-target floor unchanged.
- No red, no flashing, anywhere.
- Node count, scroll length, and which nodes are visible at a given time do not change —
  this is a path-shape and background-art change, not a re-layout of progression.

## Task 1 — Curved path geometry

Replace the straight-line connector in `MapScreen.kt` with a seeded left/right-offset,
Bezier-curved path per `24 §1`. Offsets must be deterministic per node index (same rule
already used for card rotation in `23 §3`) — verify this with a quick check: reload the
screen twice and confirm the path looks identical both times.

## Task 2 — Background terrain

Per `24 §2` and `24 §5`:
- **If you have image-generation capability**, generate the ~10 terrain props now using the
  `15 §5` prompt template, substituting each `[PROP]` value (pencil tower, crayon bridge,
  book piles, plus a few filler props for variety). Follow `15 §1` universal parameters
  exactly (transparent background, no red, correct negative prompt). Run each through the
  silhouette test in `16 §6` before using it.
- **If you don't**, build simple Compose-drawn placeholder terrain (layered rounded shapes
  for foliage clusters, simple geometric prop silhouettes) in the correct scattered
  positions and z-order, and say clearly in your report that these are placeholders standing
  in for future generated art — don't silently ship placeholders as if they were final.
- Either way, scatter props at seeded (not runtime-random) positions along the path, behind
  the path/nodes in z-order, and verify placement doesn't reduce text/icon contrast below
  `03`'s WCAG floors.

## Task 3 — Locked node treatment

Per `24 §3`: locked nodes currently render as flat gray circles. Give them the same gummy
face + depth band + 3dp outline treatment as unlocked nodes — only the color/interactivity
should differ, not the shape language.

## What NOT to touch

- Node circle shape, 3dp outline, idle bounce, mascot sizing on `MapScreen` — already built
  and confirmed working in Round 3. Don't rebuild or "improve" these; if you think something
  needs to change here, flag it in your report instead of changing it silently.
- Anything in `BlendItScreen`, `HearItScreen`, `SayItScreen`, `FindItScreen`, or the domain
  layer.

## Report requirements

1. Real test output (same standard as before — full command, unedited counts, and be
   explicit if this UI-layer change has no automated coverage, same as the prior round's
   honest disclosure).
2. Code citations for: curved path implementation, terrain prop placement, locked node
   gummy treatment.
3. Explicit list of which terrain props (if any) are generated art vs. Compose placeholder.
4. A screenshot or screen recording of the resulting MapScreen if your environment supports
   capturing one — this is a visual change, and a code citation alone can't confirm a curve
   looks right or a prop isn't overlapping a node. If you can't produce one, say so and
   describe what a human should manually check before calling this done.
