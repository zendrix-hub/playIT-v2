# Card 29: Mascot Poses and Companion Avatar Art Refresh (Asset Card)

Type: asset
Status: ready (for Claude review and image generation pipeline)

## Why
While baseline assets for Lily and companion animal avatars currently exist in `app/src/main/assets/images/mascot/`, the user noted during physical device testing that their visual style should be refreshed to strictly match the anchor style sheet (`images/_style-reference-sheet/anchor_letter-card.png`, `16_ILLUSTRATION_STYLE_GUIDE.md`):
- Flat warm shapes, 3dp DarkBrownOutline (`#4A2E18`), rounded pediatric anatomy, zero harsh reds.
- Lily remains the orange tarsier (`images/mascot/lily_idle.png` as reference) per AGENTS.md. Her poses must be redrawn for clean silhouette consistency across all states.
- Companion avatars (`companion_avatar_01_cat.png` through `06_owl.png`) should have expressive, friendly faces matching the modern PlayIT design language.

## Asset Scope

1. **Lily Mascot Poses (8 total)**:
   - `lily_idle.png`: Relaxed standing pose, warm smile, big nocturnal tarsier eyes with soft white gleam.
   - `lily_listening.png`: Hand cupped near ear, focused curious tilt (used during mic listening in Say It).
   - `lily_celebrating.png`: Hands raised high, energetic happy leap with celebratory confetti sparkles.
   - `lily_encouraging.png`: Reassuring gentle smile, thumbs up / welcoming open arm (used on gentle retries).
   - `lily_thinking.png`: Pondering pose, one finger on chin, looking upward thoughtfully.
   - `lily_pointing.png`: Pointing arm towards screen content or button.
   - `lily_waving.png`: Friendly welcoming wave (used on onboarding and return welcome).
   - `splash_tarsier_headspace.png`: Hero splash illustration lockup.

2. **Companion Animal Avatars (6 total)**:
   - `companion_avatar_01_cat.png`: Calico kitten, friendly round face.
   - `companion_avatar_02_monkey.png`: Playful Philippine monkey, warm brown fur.
   - `companion_avatar_03_bunny.png`: Soft cream rabbit, upright ears.
   - `companion_avatar_04_bear.png`: Gentle honey bear, rounded ears.
   - `companion_avatar_05_frog.png`: Cheerful tree frog, bright green, gentle eyes.
   - `companion_avatar_06_owl.png`: Wise little barn owl, big soft circular eyes.

## Pipeline & Governance
- Generation tool: Nano Banana Pro / Imagen 3 / Pollinations in batch rounds (`Documents/playIT-image-batches/2026-10-11-mascot-refresh/`).
- Style reference: `images/_style-reference-sheet/anchor_letter-card.png`.
- Background removal: Local `rembg` (RGBA transparent background, no halos, dark brown `#4A2E18` preserved).
- Review page: Built via `tools/images/review_page.py` for user approval.
- Asset Gate: Images enter `app/src/main/assets/images/mascot/` only via official release manifest (`docs/image-release/`).
