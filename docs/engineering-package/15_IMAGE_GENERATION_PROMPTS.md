# 15 — Image Generation Prompts

Templates + data tables, not 100+ repeated prose blocks — fill each template with a row from the relevant table. Universal parameters (style/lighting/color/negative-prompt) are defined once in §1 and apply to every image in this document unless a section explicitly overrides them.

## 1. Universal Parameters (apply to every prompt below)

- **Style:** flat-to-semi-flat 2D vector illustration, soft rounded shapes, thick consistent outline (per `16_ILLUSTRATION_STYLE_GUIDE.md`), child-friendly, Material 3 Expressive-compatible (works well inside rounded card containers).
- **Lighting:** soft, even, no harsh shadows or dramatic lighting — flat illustration lighting, gentle ambient highlight only.
- **Color guidance:** draw from the shipped Design System palette (`03_DESIGN_SYSTEM_SUMMARY.md §2`) — Learning Blue, Growth Green, Achievement Gold, Energy Orange, Friendly Purple as accents; Soft Sky/Cream White as background/neutral fields. **Never use red as a dominant color** (reserved narrowly per `03 §5.1`).
- **Transparent background:** required for every letter/picture/mascot asset (PNG, alpha channel) so they composite cleanly onto themed screen backgrounds; only full-screen background illustrations render with a filled background.
- **Aspect ratio / resolution:** square 1:1 for letter cards, picture-grid items, and mascot poses, at minimum 1024×1024px source (downscaled at build time for actual device density buckets); 16:9 for full-screen backgrounds/splash at minimum 1920×1080px source.
- **Negative prompt (all assets):** `photorealistic, 3D render, text, watermark, signature, scary, sharp teeth, weapons, blood, dark/muted color palette, harsh red, complex background clutter, small intricate details, adult content, brand logos`

## 2. Template A — Letter Card + Example-Word Illustration

> "A single friendly [EXAMPLE_WORD] character/object, [STYLE from §1], centered composition, simple joyful expression if a character, isolated on transparent background, bold clean outline, bright but controlled color palette using [1–2 accent colors from §1], designed to sit inside a rounded card for a children's reading app, no text or letters visible in the image itself."

### Letter Data Table (28 rows — fill Template A per row)

| Letter | Example word | Status |
|---|---|---|
| m | Mouse | Ready |
| s | Sun | Ready |
| a | Apple | Ready |
| i | Insect | Ready |
| o | Orange | Ready |
| b | Ball | Ready |
| e | Elephant | Ready |
| u | Umbrella | Ready |
| t | Tiger | Ready |
| k | Kite | Ready |
| l | Lion | Ready |
| y | Yoyo | Ready |
| n | Nest | Ready |
| g | Goat | Ready |
| **ng** | *(none)* | **PENDING SME REVIEW** — `ng` has no natural English word-initial sound; draft candidate is an ending-sound treatment ("siNG") but this needs reading-specialist sign-off before any image is commissioned (`01 §5`) |
| p | Pig | Ready |
| r | Rabbit | Ready |
| d | Dog | Ready |
| h | Hat | Ready |
| w | Watch | Ready |
| c | Cat | Ready |
| f | Fish | Ready |
| j | Jug | Ready |
| **ñ** | *(none)* | **PENDING SME REVIEW** — not a standard English letter at all; no draft candidate proposed here deliberately, to avoid inventing curriculum content (`01 §5`) |
| q | Queen | Ready |
| v | Van | Ready |
| x | *(box — ending sound)* | **DRAFT, PENDING SME CONFIRMATION** — word-initial `x` is vanishingly rare in English; convention is usually an ending-sound example like "boX" or "foX"; confirm this deviates acceptably from the initial-sound pattern used everywhere else |
| z | Zebra | Ready |

**Do not generate images for `ng`/`ñ`/`x` rows marked SME-pending** until `01_REQUIREMENTS_SUMMARY.md §5` and `13_MASTER_TASKS.md`'s open question are resolved.

## 3. Template B — Find It Picture-Grid Item

> "A single [WORD] icon/character, [STYLE from §1], centered, simple and instantly recognizable silhouette at small size (this image will render at ~64–96dp in a 5-item grid on-screen), isolated on transparent background, bold outline, one dominant color plus neutral shading, no background clutter, friendly and rounded, no text."

Build one image per unique target word across all 28 letters' Find It rounds (reuse across letters where a word is both a target for its own letter and a distractor for a later letter — see `14_ASSET_MANIFEST.md §1` for the ~60-unique-image budget). Do not commission a new image per grid slot; maintain one shared, tagged image library keyed by word, referenced by `PictureAssetRepository`.

## 4. Template C — Blend It Word Illustration

> "A single [WORD] scene, [STYLE from §1], simple and clear at card size, centered composition, isolated on transparent background, cheerful mood, bold outline, colors drawn from the Design System accent palette, no text or letters in the image."

Apply to the 35-word draft bank in `19_AUDIO_SCRIPTS.md §3` (7 groups × 5 words). Group 6/`ñ`-adjacent content note: none of the current draft Blend It words require `ñ`, so this template can proceed independent of the `ñ` content gap.

## 5. Unique Hero Assets — Individual Prompts

### Mascot pose set (8 images — see `17_CHARACTER_DESIGN_GUIDE.md` for full character spec first)
Base prompt for all 8, varying only the `[EXPRESSION/POSE]` bracket:

> "[MASCOT CHARACTER DESCRIPTION FROM 17_CHARACTER_DESIGN_GUIDE.md], [EXPRESSION/POSE], [STYLE from §1], three-quarter view, isolated on transparent background, bold consistent outline matching the character's established design, bright friendly colors, no text."

| Pose | Expression/Pose bracket |
|---|---|
| Happy | Wide warm smile, relaxed stance, one paw/hand raised in a small wave |
| Excited | Jumping slightly, arms/paws up, big open-mouth smile, small motion lines |
| Thinking | Head tilted, one paw near chin, curious raised-eyebrow expression |
| Encouraging | Gentle smile, leaning slightly forward, both paws open in a "you've got this" gesture |
| Celebrating | Arms/paws raised high, eyes closed in joy, confetti-ready open pose |
| Neutral/Idle | Calm, relaxed standing pose, soft pleasant smile, default state |
| Listening (Say It) | Paw cupped near ear, attentive focused expression, leaning in slightly |
| Pointing (onboarding) | One paw/arm extended forward pointing at an implied off-frame UI target, encouraging expression |

### Reward/celebration burst
> "Abstract confetti and sparkle burst illustration, [STYLE from §1] but with more energetic scattered composition, Achievement Gold/Energy Orange/Friendly Purple confetti pieces and star shapes radiating from center, isolated on transparent background, no characters, no text, celebratory and joyful, suitable as an overlay effect."

### Splash screen illustration
> "A warm, inviting title illustration for a children's reading app, featuring [MASCOT CHARACTER] in a Happy/Excited pose surrounded by a few floating letter-block shapes (non-specific, decorative, no real letters spelling anything), [STYLE from §1], Soft Sky background wash, aspect ratio 16:9, joyful and welcoming mood, no text/logo (logo composited separately)."

### Map background/terrain elements (~10 assets)
> "A single decorative prop for a winding path map in a children's reading app — [PROP: pencil tower / crayon bridge / stack of books / school-supply-themed decorative element], [STYLE from §1], isolated on transparent background, whimsical oversized/stylized proportions appropriate for a playful map, bold outline, bright controlled colors."

Generate one prompt per prop in the SDD's Map mockup reference (pencil tower, crayon bridge, book piles, plus additional path-filler props as needed for visual variety along a 35-node path).

## 6. Production Notes

- Generate at 2× the target render resolution and downscale, to keep edges crisp on high-density Android displays.
- Run every generated asset through a manual "silhouette test" (view at 15% opacity, confirm it's still recognizable) — this directly serves the Find It module's discrimination task, where fast recognizability at small size is a gameplay requirement, not just an aesthetic preference.
- Maintain one style-reference sheet (a few approved "gold standard" outputs) and feed it as a style reference to every subsequent generation batch, rather than relying on the text prompt alone to hold consistency across ~150 total image assets.
