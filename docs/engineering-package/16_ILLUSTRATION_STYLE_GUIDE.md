# 16 — Illustration Style Guide

> **Commercial-Grade Art-Direction Spec** — Every 2D illustration asset
> (Lily mascot poses, 28 phoneme picture cards, Blend It word objects,
> reward badges, map props) must meet the standards below before approval.
> This guide targets the same production quality as Duolingo ABC and
> Headspace Kids. Cross-reference: anchor style sheet
> `images/_style-reference-sheet/anchor_letter-card.png`, colour tokens
> `03_DESIGN_SYSTEM_SUMMARY.md §2`, gummy depth layer
> `23_DUOLINGO_ABC_UI_REFRESH.md`, character design `17_CHARACTER_DESIGN_GUIDE.md`,
> prompt templates `15_IMAGE_GENERATION_PROMPTS.md`, asset manifest
> `14_ASSET_MANIFEST.md`.

---

## 1. Visual Language — Core Pillars

| Pillar | Rule |
|---|---|
| **Style class** | Flat-to-semi-flat 2D vector illustration — Duolingo ABC's "bold, bouncy, bright" aesthetic combined with Headspace's organic softness. No photorealism, no complex gradients, no 3D renders. |
| **Shape language** | Soft, rounded geometric construction. Circles, rounded rectangles, and rounded triangles as the primary vocabulary. Every corner radius ≥ 4 dp at rendered size. Zero sharp/pointed edges on any child-facing asset. |
| **Readability mantra** | Every illustration must read as a simple, friendly, instantly recognizable shape from across a room. It must work at both large (letter card, 200 dp+) and small (Find It grid item, 64–96 dp) render sizes. |
| **Minimal internal detail** | Favour silhouette clarity over decorative complexity. A cat is a rounded head + two triangular ears + simple facial features — not individually rendered whiskers, fur texture, or complex inner geometry. 15–20 distinct shapes maximum per character/object (Duolingo ABC standard). |
| **Cultural neutrality** | Use animals, everyday objects, and natural elements. No depictions of specific real people, culturally sensitive imagery, or any content that could read as exclusionary. |

---

## 2. Line Art & Outline Matrix

PlayIT uses a **warm dark-brown outline** rather than harsh pure black. This is a deliberate art-direction choice shared with leading kids' apps — it feels friendlier, reduces visual harshness on bright backgrounds, and maintains sufficient contrast for accessibility.

### 2.1 Outline Color Token

| Token | Hex | Usage |
|---|---|---|
| `DarkBrownOutline` | `#4A2E18` | **All** illustration outlines — mascot, picture cards, Blend It objects, reward badges, map props, icons |
| `DarkEspressoOutline` | `#3C2415` | Reserved for Lily mascot only (see `17_CHARACTER_DESIGN_GUIDE.md`) — slightly deeper for mascot prominence |

> **Hard rule:** Never use pure black (`#000000`) for any illustration outline. Never use the app's `TextPrimary` (`#2D3748`) as an outline colour — that token is for text only.

### 2.2 Stroke Weight Matrix

All weights are specified in **density-independent pixels (dp)** at the asset's final rendered size. Source art should be created at 2× target resolution and downscaled.

| Element | Stroke Weight | Rationale |
|---|---|---|
| **Container / silhouette border** (outermost edge of any object or character) | **2.5 dp** | Primary contour — must pass the silhouette test at 64 dp grid size. Matches Duolingo ABC's "thick consistent outline" standard. |
| **Major inner divisions** (limb separations, ear boundaries, large colour-region borders) | **2.0 dp** | Secondary structure — clear at card size, still visible at grid-item scale. |
| **Fine inner details** (facial features — eyes, mouth, nostrils; small markings; object sub-parts like apple stem, kite string) | **1.5 dp** | Tertiary detail — visible at letter-card size, gracefully degrades at grid-item scale without creating visual noise. |
| **Decorative accents** (optional whisker hints, subtle pattern strokes on background props) | **1.0 dp** | Quaternary — used sparingly. Must not compete with functional silhouette lines. Invisible at grid-item scale by design. |

### 2.3 Stroke Consistency Rules

- **Uniform weight within each tier.** Never vary stroke weight within the same tier on a single asset.
- **Rounded stroke caps and joins** on all paths (round cap, round join). No butt caps, no miter joins.
- **No variable-width strokes.** Every stroke segment has a constant width for its tier.
- **Batch consistency:** When generating assets across multiple sessions, always verify stroke weights against the anchor reference sheet. Weight drift between batches is a production-lock blocker.

---

## 3. Color & Lighting Consistency

### 3.1 Palette Alignment

All illustration colours must draw from the shipped Design System palette (`03 §2`). The primary accent family:

| Role | Hex | Illustration Use |
|---|---|---|
| Learning Blue | `#4A90E2` | Water, sky elements, blue objects (Umbrella, Jug water) |
| Growth Green | `#4CAF50` | Foliage, green animals (Goat), success badges |
| Achievement Gold | `#FFC107` | Stars, reward bursts, warm-toned objects (Sun, Lion mane) |
| Energy Orange | `#FF9800` | Mascot accents, Tiger stripes, Kite, encouragement elements |
| Friendly Purple | `#8E7DF2` | Blend It challenge assets, special reward variants |
| Cream White | `#FFFDF8` | Belly patches, highlight areas, neutral fills |

> **Hard rule:** Never use harsh red (`#FF0000`, `#E53E3E`, or similar saturated reds) as a dominant fill colour on any illustration. The only red-adjacent token in the system is `Gentle Correction Orange` (`#FFB74D`), and it is for UI state feedback, not illustration fills.

### 3.2 The 3-Tone Shading Rule

Every coloured region in every illustration uses exactly **three tones** — no more, no fewer. This creates the "semi-flat with depth" look that distinguishes commercial kids' apps from amateur flat art, without crossing into complex shading.

```
┌──────────────────────────────────────────────────┐
│                                                  │
│   ┌─────────┐   SPECULAR HIGHLIGHT               │
│   │ Tone 1  │   HSL: Base H, Base S - 5%,        │
│   │ (Light) │        Base L + 12–15%              │
│   └─────────┘   Position: top-left quadrant       │
│                  Shape: soft, organic, NO hard     │
│   ┌─────────┐    gradient — paint-bucket style     │
│   │ Tone 2  │   BASE COLOR                        │
│   │ (Base)  │   The palette hex as shipped         │
│   └─────────┘   Covers ~60–70% of the region      │
│                                                    │
│   ┌─────────┐   AMBIENT SHADOW                    │
│   │ Tone 3  │   HSL: Base H + 5°, Base S + 5%,   │
│   │ (Dark)  │        Base L - 12–15%              │
│   └─────────┘   Position: bottom-right quadrant   │
│                  Shape: soft, follows contour      │
│                                                    │
└──────────────────────────────────────────────────┘
```

### 3.3 Lighting Rules

| Rule | Specification |
|---|---|
| **Light source** | Implied top-left, ~10:30 on a clock face. Consistent across ALL assets in the library. |
| **Highlight style** | Soft, cel-shaded region (not a smooth gradient). Think "paint bucket fill into a closed shape," not "airbrush." |
| **Shadow style** | Same cel-shaded approach. One contiguous shadow region, bottom-right, following the object's contour. |
| **No cast shadows** | Objects do not cast shadows on the transparent background. If an object needs grounding, use a simple soft elliptical shadow under it — separate layer, 8–12% opacity, `#4A2E18`. |
| **No dramatic lighting** | No rim lighting, no backlighting, no coloured light sources, no environmental reflections. |
| **Specular eye dots** | Characters with eyes (Lily, animal picture cards) get one white specular highlight dot per eye, positioned consistently at top-left of each iris. Size: 15–20% of iris diameter. |

---

## 4. Transparency & Alpha Silhouette Standards

Clean transparency is a **production-critical requirement**, not a nice-to-have. Every illustration asset composites against the app's tonal surface colours (Soft Sky, Cream White, card backgrounds) and must never show white halos, fringing, or checkerboard artifacts.

### 4.1 Hard Rules

| Rule | Specification |
|---|---|
| **Background** | 100% transparent alpha channel on all assets except full-screen backgrounds. |
| **Edge treatment** | Hard, anti-aliased vector edge. No feathered or blurred outer edges. The `DarkBrownOutline` stroke IS the boundary; nothing exists outside it. |
| **White halo tolerance** | **Zero.** 0 px of white, off-white, or any non-transparent pixel beyond the outermost outline edge. |
| **Boundary bleed** | **0 px.** No colour bleeding beyond the silhouette outline into the alpha channel. |
| **Checkerboard artifacts** | **Zero.** Any baked-in checkerboard pattern from a generation tool is an automatic rejection. |
| **Semi-transparent pixels** | Allowed ONLY for the optional grounding shadow ellipse (§3.3) and for anti-aliasing on the outermost outline edge (1 px maximum anti-alias fringe). |

### 4.2 Production Workflow for Clean Alpha

```
Step 1: GENERATE on a solid magenta background (#FF00FF)
        — NOT white, NOT transparent, NOT checkerboard.
        — Magenta provides maximum chromatic distance from all
          illustration palette colours, making removal trivial.

Step 2: REMOVE BACKGROUND using rembg, BiRefNet, or equivalent
        AI-powered background removal tool.
        — Never use magic-wand / colour-range selection on complex edges.

Step 3: EDGE REFINEMENT
        — Contract the selection by 1 px to eliminate any sub-pixel fringe.
        — Inspect at 400% zoom on both white AND dark backgrounds.
        — If any halo is visible, re-run removal or manually mask.

Step 4: ALPHA CHANNEL VERIFICATION
        — View the alpha channel in isolation (grayscale mask view).
        — The silhouette must be a clean, solid white shape on black.
        — No grey fringing, no noise, no floating pixels.

Step 5: EXPORT as PNG-32 (8-bit RGBA) at 2× target resolution.
        — Then convert to WebP (lossy, quality 90) for Android deployment.
        — Preserve the PNG-32 master in the asset source directory.
```

### 4.3 Composite Testing Checklist

Before any asset is approved, composite it against all four of these backgrounds and verify no artefacts appear:

- [ ] Soft Sky (`#EAF6FF`)
- [ ] Cream White (`#FFFDF8`)
- [ ] Achievement Gold (`#FFC107`)
- [ ] Near-Black (`#1A1A2E`)

---

## 5. Shape Language — Detailed Specifications

### 5.1 Geometric Vocabulary

| Shape | Use | Rules |
|---|---|---|
| **Circle / Oval** | Heads, eyes, body masses, fruit (Apple, Orange), balls | Default starting shape for any rounded object. Prefer true circles over ovals where possible. |
| **Rounded rectangle** | Bodies, trunks, containers, hats, books | Corner radius ≥ 25% of the shorter dimension. |
| **Rounded triangle** | Ears, beaks, fins, crowns, mountains | All vertices rounded to a minimum 4 dp radius. No sharp points. |
| **Rounded trapezoid** | Legs, tails, tree trunks | Same vertex-rounding rules as triangles. |
| **Organic blob** | Clouds, speech bubbles, decorative accents | Freeform curves, but all curvature must be smooth (no cusps, no inflection-point kinks). |

### 5.2 Proportions for Character/Animal Objects

- **Head-to-body ratio:** 1:1 to 1:1.5 (oversized head reads as cuter and more child-friendly).
- **Eye-to-head ratio:** Eyes occupy 25–35% of the head's width. Oversized eyes = immediate emotional connection.
- **Limb simplification:** Maximum 3 shape primitives per limb. No individually articulated digits — rounded paws/stumps only.
- **Feature count:** Strive for ≤ 20 distinct vector shapes per complete character or object. Every additional shape must justify its existence by aiding recognition.

### 5.3 The Asymmetry Principle (from Material 3 Expressive)

- **Decorative elements** (map props, background items, celebration confetti): Asymmetric, dynamic composition is encouraged — it creates visual interest and energy.
- **Functional elements** (letter cards, picture-card objects, Blend It word illustrations, buttons): Symmetric, stable, centred composition only. Recognizability and literal clarity always beat visual tension for elements the child must identify or interact with.

---

## 6. Pedagogical Action Alignment — Picture Card Standards

Picture cards are not just illustrations — they are **phonemic discrimination tools**. A 6-year-old must identify each object in under 2 seconds at grid-item scale (64–96 dp). Every card must be engineered for instant visual salience.

### 6.1 The Silhouette Test (mandatory, every asset)

```
1. Reduce the asset to 15% opacity.
2. View at the smallest intended render size (64 dp).
3. The object MUST still be identifiable by silhouette alone.
4. If it fails, simplify the shape until it passes.
```

### 6.2 Distinctive Silhouette Requirements by Category

| Category | Objects (from `15 §2`) | Silhouette Strategy |
|---|---|---|
| **Animals** | Mouse, Elephant, Tiger, Lion, Goat, Pig, Rabbit, Dog, Cat, Zebra, Fish, Insect | Ears/horns/tail/fins are the primary differentiators. Exaggerate the most distinctive anatomical feature. A mouse's round ears, a rabbit's tall ears, and a cat's pointed ears must be unmistakably different in silhouette. |
| **Round objects** | Apple, Orange, Ball, Sun | Differentiate by accessories: Apple has a stem + leaf, Orange has a navel texture dot + leaf, Ball has curved panel lines, Sun has radiating rays. Without these, all four read as "circle." |
| **Elongated objects** | Umbrella, Hat, Kite, Watch, Jug, Van, Yoyo | Lean into the unique profile: Umbrella's curved canopy + handle hook, Hat's brim, Kite's diamond + tail, Watch's band + circular face. |
| **Compound objects** | Nest, Queen (crown), Zebra | Use the most iconic sub-element as the silhouette anchor: Nest = bowl shape with eggs, Queen = crown atop simplified face, Zebra = horse silhouette with bold stripe pattern. |

### 6.3 Dual-Coding Rule

No two picture cards in the same Find It grid may be distinguishable **only** by colour. Every pair must differ in silhouette shape. If two objects have similar silhouettes (e.g., Apple vs. Orange), they must not appear as distractor options for each other unless their accessories (stem/leaf vs. navel/segments) are exaggerated enough to differentiate at grid-item scale.

### 6.4 Emotional Expression for Animal Cards

Animal picture cards should show a **gentle, friendly resting expression** — not the full range of mascot emotions. Specifically:

- Simple dot or oval eyes with a single specular highlight
- Gentle closed-mouth smile or neutral expression
- No teeth, tongue, or open-mouth expressions (these can read as aggressive to young children)
- Expression should not distract from the animal's identity — the child needs to recognize "Mouse," not "happy mouse vs. sad mouse"

---

## 7. Character Style (Mascot & Incidental Characters)

Detailed mascot art direction is in `17_CHARACTER_DESIGN_GUIDE.md` and `26_MASCOT_COPLAYER_SYSTEM.md`. This section covers **universal rules** applying to Lily and any incidental characters in Find It / Blend It illustrations.

### 7.1 Expression Rules

- Emotions must be legible from the enumerated states in `03 §6`:
  **Happy / Excited / Thinking / Encouraging / Celebrating** (for Lily);
  **Friendly resting** only (for picture-card animals — see §6.4).
- Simple, exaggerated but **never scary or uncanny** facial expressions.
- No implied violence, weapons, aggressive postures, or anything remotely unsettling.

### 7.2 Gummy Physicality (from `23_DUOLINGO_ABC_UI_REFRESH.md`)

Characters should feel like soft, squeezable, "gummy" objects:

- Rounded everywhere — no sharp joints, no angular elbows or knees.
- Plush, pear-shaped or egg-shaped body masses.
- Slight "squishy" overlap where limbs meet the body (no hard articulation lines).
- The 3-tone shading (§3.2) applied to body masses gives the impression of soft 3D volume without actual 3D rendering.

---

## 8. Material 3 Compatibility

| Requirement | Specification |
|---|---|
| **Card padding** | Leave ≥ 16 dp of visual "breathing room" between the illustration's outermost edge and the intended card container edge. This prevents the art from visually colliding with M3 `Card` rounded corners (24–32 dp radii per `03 §4`). |
| **Elevation harmony** | Illustrations sit inside cards with 4 dp elevation (`03 §4`). The illustration itself should NOT contain its own baked-in drop shadow (except the optional grounding ellipse in §3.3). The card's elevation shadow provides all necessary depth separation. |
| **Surface compatibility** | Transparent backgrounds throughout (§4) ensure illustrations composite correctly against the app's tonal surface colours in the shipped palette and any future dark-mode or contrast-adjusted palette. |
| **Shape language echo** | Illustration corner radii should harmonize with the app's UI radii (24–32 dp). An illustration that uses sharp internal rectangles while sitting inside a heavily rounded card creates visual dissonance. |

---

## 9. Accessibility Considerations

### 9.1 Self-Contrast

Every illustration must be **self-contrasting** — it cannot rely on the app's background colours to be legible:

- Light-coloured objects/characters need a sufficiently dark `DarkBrownOutline` to stay visible against a Cream White card background.
- Dark-coloured objects need an internal highlight tone bright enough to show shape detail against a dark background (future dark-mode proofing).
- Minimum contrast ratio between the `DarkBrownOutline` (`#4A2E18`) and the lightest fill colour in the illustration: **3.0:1** (WCAG 2.2 non-text contrast).

### 9.2 Colour Independence

Never encode meaning through colour alone within an illustration:

- "Correct" vs. "incorrect" states must differ in **shape/icon**, not just hue — consistent with `03 §6`.
- No two objects that appear in the same context (e.g., Find It grid) should be distinguishable only by colour (§6.3).

### 9.3 Grayscale Simulation Test

Every illustration must remain identifiable under a reduced-saturation / greyscale simulation (a direct check against Colour Vision Deficiency). Shape and value contrast must carry meaning even if hue information is lost.

### 9.4 Motion Sensitivity

Animated illustration variants (Lottie mascot poses, celebration bursts) must respect the user's `prefers-reduced-motion` setting at the app level — see `21_ANIMATION_GUIDE.md` for implementation.

---

## 10. Prompt Engineering Formula

### 10.1 Universal Parameters (apply to every AI-generated illustration)

These parameters are the **non-negotiable base** prepended to every generation prompt. They align with `15_IMAGE_GENERATION_PROMPTS.md §1` but add the precision required for commercial-grade output.

```
STYLE:
  Flat-to-semi-flat 2D vector illustration, soft rounded shapes,
  thick consistent DarkBrownOutline (#4A2E18), cel-shaded 3-tone
  lighting (soft top-left highlight, base fill, bottom-right shadow),
  child-friendly, Duolingo ABC aesthetic, Material 3 Expressive
  compatible, bold clean silhouette.

LIGHTING:
  Soft even illumination, implied top-left light source at 10:30,
  gentle cel-shaded highlight, no harsh shadows, no dramatic lighting,
  no rim light, no backlight.

COLOUR GUIDANCE:
  Draw from the PlayIT Design System palette: Learning Blue (#4A90E2),
  Growth Green (#4CAF50), Achievement Gold (#FFC107), Energy Orange
  (#FF9800), Friendly Purple (#8E7DF2), Cream White (#FFFDF8).
  Never use harsh red as a dominant colour.

BACKGROUND:
  Isolated on a plain solid magenta background (#FF00FF).
  NOT white. NOT transparent. NOT checkerboard.

RESOLUTION:
  Minimum 1024x1024 px source for square assets (letter cards,
  picture-grid items, mascot poses). Minimum 1920x1080 px source
  for 16:9 assets (splash, full-screen backgrounds).

OUTLINE SPEC:
  All outlines in warm dark brown (#4A2E18), NOT pure black.
  Container/silhouette border: 2.5 dp equivalent stroke.
  Inner detail lines: 1.5 dp equivalent stroke.
  Rounded stroke caps and joins throughout.
```

### 10.2 Universal Negative Prompt

Apply this negative prompt to **every** generation call, without exception:

```
NEGATIVE PROMPT:
  photorealistic, 3D render, CGI, complex gradients, gradient mesh,
  realistic fur, realistic feathers, realistic skin texture,
  text, watermark, signature, logo, brand marks,
  scary, sharp teeth, claws, weapons, blood, violence,
  dark muted colour palette, harsh red, neon colours,
  complex background clutter, busy patterns, intricate details,
  small fine details that disappear at small sizes,
  adult content, suggestive content,
  pure black outlines, thin hairline strokes,
  drop shadow, baked-in shadow on background,
  white background, grey background, checkerboard background,
  semi-transparent background, noisy edges, white halo, fringing,
  multiple subjects, group scene, cluttered composition,
  emoji, text overlays, UI elements baked into the image
```

### 10.3 Template: Phoneme Picture Card

```
"A single friendly [OBJECT_NAME], flat-to-semi-flat 2D vector illustration,
soft rounded shapes, thick consistent warm dark-brown outline (#4A2E18),
cel-shaded 3-tone lighting with soft top-left highlight,
centred composition, [SILHOUETTE_NOTE — e.g. 'exaggerated round ears
for instant recognition'], simple gentle resting expression if a character,
isolated on a plain solid magenta background (#FF00FF),
bold clean outline, bright controlled colour palette using
[1-2 ACCENT_COLOURS from the Design System palette],
designed to be instantly recognizable at 64dp in a rounded card
for a children's phonics app, no text or letters visible."

[+ Universal Negative Prompt from §10.2]
```

**Fill-in variables per letter row from `15 §2`:**

| Variable | Source |
|---|---|
| `OBJECT_NAME` | Example-word column (e.g., "Mouse", "Sun", "Apple") |
| `SILHOUETTE_NOTE` | From §6.2's category-specific silhouette strategy |
| `ACCENT_COLOURS` | 1–2 colours from §3.1 that best fit the object's natural colouring |

### 10.4 Template: Lily Mascot Pose

```
"Pediatric vector illustration of Lily the cute Philippine tarsier mascot,
[EXPRESSION_POSE from 26_MASCOT_COPLAYER_SYSTEM.md §3],
small plush pear-shaped body, oversized rounded head,
giant warm hazel glossy eyes with single white specular highlight dot
at top-left of each iris, soft rounded plush ears with energy-orange
(#FF9800) inner glow, stubby rounded paws with no sharp claws,
thick clean dark espresso vector outline (#3C2415) at 2.5dp container
and 1.5dp inner detail, 3-tone cel shading (cream highlight, warm tan
base, soft brown shadow), Duolingo ABC and Headspace character design
style, centred composition, isolated on a plain solid magenta background
(#FF00FF), no text, no watermark, no signature."

[+ Universal Negative Prompt from §10.2]
```

### 10.5 Template: Blend It Word Illustration

```
"A single [WORD] scene/object, flat-to-semi-flat 2D vector illustration,
soft rounded shapes, thick warm dark-brown outline (#4A2E18),
cel-shaded 3-tone lighting, simple and clear at card size,
centred composition, isolated on a plain solid magenta background
(#FF00FF), cheerful mood, bold outline, colours drawn from the
PlayIT Design System accent palette, instantly recognizable silhouette,
no text or letters in the image."

[+ Universal Negative Prompt from §10.2]
```

### 10.6 Template: Reward Badge / Celebration Asset

```
"Abstract confetti and sparkle burst illustration, flat-to-semi-flat
2D vector style, energetic scattered composition,
Achievement Gold (#FFC107), Energy Orange (#FF9800), and
Friendly Purple (#8E7DF2) confetti pieces and star shapes
radiating from centre, thick warm dark-brown outlines (#4A2E18),
cel-shaded flat fills, isolated on a plain solid magenta background
(#FF00FF), no characters, no text, celebratory and joyful,
suitable as a transparent overlay effect."

[+ Universal Negative Prompt from §10.2]
```

### 10.7 Template: Map Background Prop

```
"A single decorative prop for a winding path map in a children's reading
app — [PROP_NAME: e.g. pencil tower, crayon bridge, stack of books],
flat-to-semi-flat 2D vector illustration, soft rounded shapes,
thick warm dark-brown outline (#4A2E18), cel-shaded 3-tone lighting,
isolated on a plain solid magenta background (#FF00FF),
whimsical oversized proportions, bold outline, bright controlled colours
from the PlayIT Design System palette."

[+ Universal Negative Prompt from §10.2]
```

---

## 11. Consistency Enforcement — Production Controls

### 11.1 Anchor Reference Sheet

One approved style-reference sheet (`images/_style-reference-sheet/anchor_letter-card.png`) is the **single visual anchor** for every generation and review session. This asset defines the canonical stroke weight, shading depth, colour vibrancy, outline colour, and overall "feel."

**Workflow:**

1. Before every generation batch, load the anchor reference sheet as a style reference input (image-to-image or style-reference parameter, depending on the generation tool).
2. After generation, place each new asset side-by-side with the anchor at 100% zoom.
3. If any of the following drift, reject and regenerate:
   - Outline colour appears too black or too light
   - Stroke weight appears thinner or thicker than anchor
   - Shading appears more complex (gradient) or flatter (no shading) than anchor
   - Overall colour vibrancy is noticeably different

### 11.2 Batch Verification Matrix

After each batch of generated assets, run this verification before merging into the asset library:

| Check | Pass Criteria |
|---|---|
| Silhouette test (§6.1) | Identifiable at 15% opacity, 64 dp |
| Alpha cleanliness (§4.1) | Zero white halos on all 4 test backgrounds |
| Stroke weight consistency | Visually matches anchor ±10% |
| Outline colour | `DarkBrownOutline` — not black, not grey |
| 3-tone shading | Exactly 3 tones per colour region, no gradients |
| Light direction | Highlight top-left, shadow bottom-right |
| Specular eye dot | Present, top-left, white, correct size |
| No text/emoji | Zero text, zero emoji, zero UI baked into art |
| Colour palette compliance | All fills traceable to Design System tokens |
| Dual-coding compliance | Not relying on colour alone for identity |
| Card padding clearance | ≥ 16 dp breathing room to intended card edge |

---

## 12. Child-Friendliness Checklist (apply to every asset before approval)

- [ ] Passes the silhouette / 15% opacity recognizability test (§6.1).
- [ ] No colour used as the sole distinguishing feature between two similar assets (§6.3).
- [ ] No harsh red as a dominant colour (`03 §5.1`).
- [ ] Reads clearly at both largest (letter card, 200 dp+) and smallest (grid item, 64 dp) intended render size.
- [ ] Consistent outline weight and proportion style with the anchor reference sheet — deviation reads as "off-brand."
- [ ] No text, numbers, letters, or emoji baked into the illustration.
- [ ] No teeth, claws, weapons, or anything remotely threatening or unsettling.
- [ ] Expression is friendly and gentle — never scary, aggressive, sad, or uncanny.
- [ ] Alpha channel is perfectly clean — zero white halos, zero boundary bleed (§4).
- [ ] Self-contrasts sufficiently for legibility against light AND dark backgrounds (§9.1).
- [ ] Remains identifiable under greyscale simulation (§9.3).
- [ ] Cultural neutrality maintained — no culturally sensitive or exclusionary imagery.
- [ ] Compliant with the Zero-Emoji Policy — no emoji anywhere in or on the asset.

---

## 13. Asset Type Quick-Reference

| Asset Type | Count | Resolution | Aspect | Outline Colour | Template |
|---|---|---|---|---|---|
| Phoneme picture cards | 28 | 1024×1024 px | 1:1 | `DarkBrownOutline` | §10.3 |
| Find It grid illustrations | ~60 unique | 1024×1024 px | 1:1 | `DarkBrownOutline` | `15 §3` + this guide |
| Blend It word illustrations | 35 | 1024×1024 px | 1:1 | `DarkBrownOutline` | §10.5 |
| Lily mascot poses | 7 | 1024×1024 px | 1:1 | `DarkEspressoOutline` | §10.4 |
| Avatar picker options | 8–12 | 512×512 px | 1:1 | `DarkBrownOutline` | Adapt §10.3 |
| Map props | ~10 | 512×512 px | 1:1 | `DarkBrownOutline` | §10.7 |
| Reward badges / bursts | 5+ | 512×512 px | 1:1 | `DarkBrownOutline` | §10.6 |
| Splash illustration | 1 | 1920×1080 px | 16:9 | `DarkBrownOutline` | `15 §5` |
| Map background | 1 | 1920×1080 px | 16:9 | — (filled bg) | `15 §5` |
| Star / Heart icons | 4 | 256×256 px | 1:1 | `DarkBrownOutline` | Adapt §10.6 |

---

## 14. Appendix — Competitive Research Summary

The following patterns were extracted from analysis of Duolingo ABC, Khan Academy Kids, Lingokids, and Headspace Kids to inform this guide:

| App | Key Pattern Adopted for PlayIT |
|---|---|
| **Duolingo ABC** | "Bold, bouncy, bright" aesthetic; 15–20 shape limit per character; flat-to-semi-flat with minimal shading; consistent thick outlines; silhouette-first design philosophy; gummy/pressable physicality. |
| **Khan Academy Kids** | Warm, mascot-centric character design with moderate-weight outlines; subtle soft drop shadows for spatial separation (adopted as optional grounding ellipse only); high-contrast colour choices for foreground/background separation; exaggerated friendly expressions. |
| **Lingokids** | Strict design-system enforcement for cross-platform consistency; silhouette testing as standard practice; varying stroke weights for visual hierarchy (adopted as the 4-tier stroke matrix); "Serious About Fun" balance of pedagogy and playfulness. |
| **Headspace** | Organic, soft, "bloby" shapes with intentional slight imperfection; warm and optimistic palette; minimalist face design (simplified to specular-dot + gentle-smile for picture cards); emotional warmth through shape language over complex rendering. |
