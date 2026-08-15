# 31 — Free Tools, Resources & Asset Production Guide

> Practical production guide for the assets flagged `[ASSET: ...]` across `25`–`30`. Pricing/free-tier details below were checked in August 2026 — these change often, so re-verify each tool's live pricing page before budgeting a production session against a specific number.

## 1. Free AI art tools

| Tool | Free tier (checked Aug 2026) | Best for | Caveat |
|---|---|---|---|
| **Leonardo.ai** | 150 "Fast Tokens"/day, resets daily, ≈10–30 images depending on model/resolution | Mascot states, prop/terrain assets — strongest free-tier volume of the group | **Free-tier generations are public** (visible/remixable by other Leonardo users) and Leonardo retains reuse rights on them. For a thesis asset set you want to claim as your own original work, either keep free-tier prompts generic/non-identifying, or budget for the cheapest paid tier ($12/mo, private generations) once you've locked Lily's final look and are producing the real asset set. |
| **Ideogram** | Free tier has been shrinking through 2026 — most current sources report **~10 "slow" credits/week** (down from an earlier ~25/day); confirm on ideogram.ai/pricing before planning around a number | Strong at rendering legible text inside an image — not a primary need here since none of these assets need embedded text, so it's a lower-priority tool for this project | Free-tier images are public; no reference-image upload on free tier |
| **Adobe Firefly** | 25 generative credits/month (free), **outputs are watermarked** | Background/terrain illustration exploration | Watermarked free-tier output is unusable as a final shipped asset — treat Firefly's free tier as a sketching/concepting tool only, then either pay ($9.99/mo Standard, unlimited standard generations, no watermark) or move the concept over to Leonardo/Ideogram for a clean final generation |
| **Canva (AI features)** | Free tier available | Asset composition, background removal, quick mockups of how assets look in-context | Not a primary generation tool — best used after generation, to composite/crop/arrange |

**Prompting approach — a note on originality.** Don't prompt any of these tools with a competitor's brand name (e.g., "in the style of Duolingo") — both because it tends to produce weaker, more derivative results than describing the actual visual attributes directly, and because a thesis asset set should be defensibly original work rather than a named-brand imitation. Instead, prompt the underlying stylistic attributes directly:

> *"Flat vector illustration of [subject], thick dark-brown outline (~4px equivalent), chunky rounded shapes, bright saturated primary colors, soft flat cel-shading, no photorealism, simple friendly facial features, centered composition, plain or transparent background, children's educational app character style."*

Swap `[subject]` per asset — e.g., "a small Philippine tarsier character, round oversized eyes, sitting pose" for Lily states, or "a cluster of bamboo stalks" for terrain props. Keep the outline/flat-shading/rounded-shape language consistent across every prompt so the resulting asset set reads as one coherent world rather than seven separately-styled images.

## 2. Lottie animation sources

**LottieFiles.com** free library — confirmed active and current as of August 2026, with a dedicated free-license filter. Search terms and use cases are detailed in `29 §7` (confetti/celebration effects). General guidance: the site mixes free and paid results in the same search results page — always check the license tag on an individual animation before downloading, since "Free" and "Premium" listings appear interleaved. Downloaded `.json` files are bundled as local app assets (no runtime network dependency), which keeps this compatible with the app's "zero network after install" constraint.

## 3. Free font resources

- **Lexend** (variable) — confirmed available on Google Fonts, already the correct choice per `03 §5.2`'s research-driven recommendation (derived from Quicksand, designed around reading-proficiency research). No change needed to this decision.
- **Andika** — SIL's reading-focused typeface, available via Google Fonts and directly from SIL International, correctly specified as the static fallback for API<26 edge cases (`03 §5.2`).
- **Alternative kid-friendly options** (for any future decorative/non-reading-critical use, not a replacement for Lexend/Andika on reading-critical text): **Baloo 2** and **Fredoka** are both free, rounded, Google Fonts–hosted options in the same visual family as the "chunky toy" brief. **Quicksand** (Lexend's own typographic ancestor, per `03 §5.2`) is another safe, already-related option if a *display-only* decorative face is ever wanted distinct from Lexend. Avoid **Comic Neue** for this app specifically — its comic-lettering association reads as informal/parody in a way that works against the "professional educational tool a parent trusts" side of the brief, even though it's technically kid-friendly.

## 4. WCAG contrast verification

- **WebAIM Contrast Checker** and **Accessible Colors** (accessible-colors.com) — both free, both suitable for verifying any new color pairing introduced in this refresh (the `TrailTan`/`TrailTanOutline` tokens from `27 §2`, the `DisplayTextShadow` token from `25 §2`) against the same 4.5:1 normal-text / 3.0:1 large-text/UI-component floors already governing the rest of the palette per `03 §5.1`.
- Run every new token pairing through one of these before final visual QA — this extends, rather than replaces, the standing project rule that all shipped color pairings get a contrast check (`03 §5.1`).

## 5. Asset format guide

| Asset type | Format | Reasoning |
|---|---|---|
| Mascot states, terrain props, decorative illustrations | **PNG**, transparent background | Matches the existing convention already in use (`reward_star.png`, `reward_heart.png` loaded via `rememberAssetPainter`) — stay consistent with what's already shipping rather than introducing a second asset pipeline |
| Complex reusable effects (confetti, if Option B from `29 §4` is adopted) | **Lottie `.json`** | Only for effects, never for the mascot character itself — see `26 §5`'s reasoning for why the mascot stays PNG-based |
| Anything referenced in code as a vector path/icon glyph (rare in this system — most "icons" are emoji glyphs in the current `.kt` files, e.g., 🔒, 🎙️) | Keep as emoji/text glyph where already used | Don't introduce SVG/vector-drawable assets to replace something that's already working as a text glyph — that's added production and maintenance cost for no visual gain |

**Density export**: target device is described as a budget Android tablet, so prioritize **xhdpi and xxhdpi** as the primary export densities, with mdpi/hdpi as fallback for lower-end hardware. [CHECK WITH AGENT: whether xxxhdpi is worth exporting at all — on a budget-tablet target, the storage/APK-size cost of a full five-density asset set may not be justified by devices that can actually use the top density; skip xxxhdpi unless a specific target device list says otherwise.]

## 6. Asset handoff to Antigravity

Naming convention matches the existing pattern already visible in the codebase (`images/rewards/reward_star.png`, `images/rewards/reward_heart.png` referenced via `rememberAssetPainter("images/...")`):

- `images/mascot/lily_<state>.png` — e.g. `images/mascot/lily_idle.png`, `images/mascot/lily_waving.png`
- `images/map/terrain_<name>_<variant>.png` — e.g. `images/map/terrain_bamboo_cluster_01.png`
- `images/decor/cloud_<variant>.png` — if any cloud shapes end up asset-based rather than code-drawn (default recommendation throughout this doc set is code-drawn clouds; only fall back to PNG clouds if the code-drawn version doesn't read well in practice)
- `images/rewards/` — existing folder, no change

All lowercase, underscore-separated, descriptive-then-variant-numbered — consistent with the two existing filenames in the codebase.

**Placement**: the `rememberAssetPainter("images/...")` calls already in `PediatricComponents.kt`/`MapScreen.kt` imply assets load from the Android `assets/` folder (not `res/drawable`), most likely at `app/src/main/assets/images/<category>/<file>.png`. [CHECK WITH AGENT: confirm the exact module path and the `rememberAssetPainter` implementation, since neither the build config nor that composable's source was among the files provided — this doc infers the convention from existing call sites, not from a confirmed folder listing.]

## 7. Lily the Tarsier — ready-to-paste art prompts

One prompt per state from `26 §3`'s expression table. Each follows the style-attribute template from §1 above, with pose/expression swapped per state. Paste directly into Leonardo/Ideogram/Firefly.

- **`lily_idle.png`**: *"Flat vector illustration of a small Philippine tarsier character, standing pose, calm friendly expression, big round eyes looking forward, thick dark-brown outline, chunky rounded body shapes, bright saturated tan-and-cream fur coloring, soft flat cel-shading, no photorealism, centered composition, plain background, children's educational app mascot style."*
- **`lily_celebrating.png`**: *"...same character, arms raised mid-hop, big open joyful smile, eyes crinkled with happiness, dynamic celebratory pose..."*
- **`lily_encouraging.png`**: *"...same character, leaning slightly forward, one hand extended in a warm supportive gesture, gentle reassuring smile, soft welcoming eyes..."*
- **`lily_listening.png`**: *"...same character, head tilted to one side, one paw cupped near an ear, focused attentive expression..."*
- **`lily_pointing.png`**: *"...same character, one arm extended forward pointing, eyes following the pointing gesture, alert engaged posture..."*
- **`lily_waving.png`**: *"...same character, one arm raised in an open friendly wave, warm welcoming smile, slight forward lean..."*
- **`lily_thinking.png`**: *"...same character, one paw near chin, head tilted, curious puzzling expression, looking downward/sideways rather than at viewer..."*

Keep the character-description clause (species, proportions, coloring, outline weight) **identical** across all seven prompts — only the pose/expression clause changes — so the results read as one consistent character across states rather than seven different-looking creatures. If a tool's "character reference" or "consistent character" feature is available on whichever platform is used (both Leonardo and Ideogram have some version of this), generate `lily_idle.png` first and use it as the reference image for the remaining six, which will produce far more consistent results than seven independent prompts.
