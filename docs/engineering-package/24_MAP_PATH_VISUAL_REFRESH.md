# 24 — Map Screen Path & Terrain Refresh

> Additive to `23_DUOLINGO_ABC_UI_REFRESH.md` (which covers node shape/outline/idle-bounce)
> and to `14_ASSET_MANIFEST.md` / `15_IMAGE_GENERATION_PROMPTS.md` / `16_ILLUSTRATION_STYLE_GUIDE.md`
> (which already scoped this work — see `14 §2` "Map background/terrain elements ~10" and
> `15 §5` "Map background/terrain elements" prompt template). This doc closes the gap between
> what was speced and what shipped: **the terrain/path assets were planned but never built.**
> Nothing here changes colors, gating, node unlock logic, or accessibility rules.

## 0. Current state vs. target

Current shipped `MapScreen`: flat light-blue gradient background, nodes connected by a single
straight vertical line, no terrain art, no foliage, no path decoration. This is Task-1-of-4
from `23 §7` (gummy node treatment) without the terrain/path layer `14`/`15` already scoped
but which no prior round built.

Target (Duolingo ABC path screen): a **winding, non-straight path** over an illustrated
background (foliage clusters, dotted/curved connector, occasional decorative prop breaking
up the line), nodes as textured icon tiles sitting *on* the path rather than a bare circle
floating on a flat gradient.

## 1. Path geometry

- Replace the single straight vertical connector between nodes with a **gently curving path**:
  alternate node horizontal offset left/right of center (e.g. ±40–56dp) as the path descends,
  connected by a smooth curve (Compose `Path` with cubic/quadratic Bezier segments), not a
  straight `Canvas.drawLine`.
  - Offsets are **seeded by node index**, not runtime-random — must stay stable across
    recomposition and scroll, same rule already applied to card rotation in `23 §3`.
- Keep the connector visual language from `23`: still a thick stroke in Growth Green for
  completed segments, muted/gray for locked segments (unchanged semantics, only the shape
  changes from straight to curved).
- Node spacing and total scroll length must not change — this is a path-shape change only,
  not a re-layout of how many nodes are visible or how progression scrolls.

## 2. Background terrain

Use `15_IMAGE_GENERATION_PROMPTS.md §5` "Map background/terrain elements" prompt template
as-is — it is already written and scoped for this exact purpose:

> "A single decorative prop for a winding path map in a children's reading app —
> [PROP: pencil tower / crayon bridge / stack of books / school-supply-themed decorative
> element], [STYLE from §1], isolated on transparent background, whimsical oversized/stylized
> proportions appropriate for a playful map, bold outline, bright controlled colors."

- Generate the ~10 props called for in `14_ASSET_MANIFEST.md §2` (pencil tower, crayon bridge,
  book piles, plus filler props for path variety across the full node count).
- Scatter props along the path at irregular but seeded (not runtime-random) positions,
  similar spirit to the card-rotation seeding rule — same prop layout on every visit for a
  given user, not randomized per recomposition.
- Background stays behind the path/nodes in z-order and must not reduce text/icon contrast
  on top of it — check against `03`'s WCAG contrast floors before finalizing prop placement
  density.
- Follow every rule in `16_ILLUSTRATION_STYLE_GUIDE.md` §1–§7 for any newly generated prop:
  transparent background, thick consistent outline, silhouette test, no red as dominant color.

## 3. Node treatment (delta on top of `23 §3`)

`23 §3` already specs circular nodes with 3dp outline and idle bounce on the current-unlocked
node — that part is implemented and confirmed (Round 3 conformance check). This section adds:

- Locked nodes keep the padlock glyph but should sit on the same gummy circular treatment as
  unlocked nodes (face + depth band + 3dp outline), not a flat gray circle — locked ≠ exempt
  from the shape language, only from color/interactivity.
- Completed nodes (3-star) keep the star row inside the circle per current implementation —
  no change there.

## 4. Explicitly unchanged

- Node unlock/gating logic, star calculation, `Blend N` node type — untouched, this is a
  path/background visual layer only.
- Node circle shape, outline weight, idle bounce, mascot sizing on `MapScreen` — already
  speced in `23` and already implemented; this doc does not re-open those.
- Color tokens — no new hues; terrain props draw only from the existing accent palette per
  `15 §1` universal parameters.

## 5. Asset sourcing note

If the coding agent has image-generation capability, it should generate the ~10 terrain props
directly from the `15 §5` prompt template (one prompt per prop, substituting `[PROP]`) rather
than waiting on a separate art pass. If it does not, build the path/terrain layer with simple
Compose-drawn placeholder shapes (rounded blobs, foliage clusters as layered circles) in the
correct positions and z-order now, so the curved-path layout work isn't blocked on art — and
flag clearly in the report which props are placeholder-drawn vs. generated, so real assets can
be dropped in later without a layout rework.

## 6. Duolingo ABC Learning Path Structural Upgrades

In accordance with stakeholder guidance and pedagogical parity with Duolingo ABC:
- **Bilingual Marungko Group Chapter Banners**: Each Marungko letter group (Groups 1–6) begins with a milestone banner (`Pangkat X • Group X`, letter list summary, and completion/in-progress/locked status chip) to divide the 28-letter sequence into digestible, rewarding units.
- **Smooth Auto-Scroll to Active Node**: Upon entering `MapScreen`, the viewport automatically centers on the current active lesson node with smooth easing.
- **Dynamic Companion Mascot & Speech Dialogue**: Lily the mascot dynamically alternates left or right of the active node with an anchored mini dialogue bubble (`Tara na! • Let's go!`), reacting interactively with cheerful animations and sound on tap.
- **Active Pulsing Focus Ring & Locked Shake**: Active nodes feature a radial expanding focus aura; tapping locked nodes triggers a tactile horizontal shake animation and encouraging audio feedback.

