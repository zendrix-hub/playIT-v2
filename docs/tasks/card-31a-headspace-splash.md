# Card 31a: The Headspace splash matches Lily, and Start never covers her face (NFR-ACC-02, NFR-IND-01)

Status: done

Part of card 31 (`card-31-post-midterm-polish.md`). Team directive 2026-10-13: card 31 runs now, before the submission, and Claude owns the splash. The team kept the drawn Headspace tarsier (agy's Lily-picture version was reverted, `26446fd`).

## Why
- **Style:** the vector tarsier had no outline and its own colours, so it did not look like the redrawn Lily on every other screen (release `2026-10-12-mascot`).
- **Layout:** the dome was cropped at the bottom, and the blue Start button sat on her belly.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/splash/SplashScreen.kt`
- Modify: `presentation/theme/Color.kt` (the Lily colours)
- Add: `presentation/splash/SplashScreenTest.kt`
- Modify: `screenshot/LayoutMatrixTest.kt`

## Changes
1. **Lily as one soft dome, in her own colours:**
   - Colours sampled from `lily_idle.png`: orange `#F97B27`, cream `#FDEBD3`, ear `#FEAC7A`, blush `#FC9898`, pupils `#3B1F0A`, and the art's dark-brown outline `#4A2E18`.
   - Her head and body are one squircle (a superellipse, n = 2.6), with a thin darker crescent down her right side.
   - Cream eye patches with two catchlights, pink cheeks, a nose, a smile, a glossy highlight and her cream belly.
   - The shapes are built once per size (`drawWithCache`); only the draw phase reads the eyes.
2. **The start panel:** Lily rises from behind a white panel with a dark-brown top edge, and her paws rest on its edge.
   - The 64 dp green Start button sits in the panel, in thumb reach.
   - The panel covers only her lower body, below `LILY_FACE_BOTTOM` (66% of her height), on all 4 sizes.
3. **Size:** Lily takes at most 55% of the screen height, at most 400 dp, and the width less 24 dp. The title is centred in the space above her.
4. **Motion** (`splashMotion`, `LilyEyes`):
   - She breathes (3%, 3.8 s) and blinks every 3.6 s; a tap winks her right eye.
   - With reduced motion there is no breathing animation and no blink loop, and the wink is instant.

## Tests
| Test | Checks |
|---|---|
| `SplashScreenTest.start_callsOnStart` | Start calls back |
| `SplashScreenTest.motionPolicy_followsReducedMotion` | No breathing or blinking with reduced motion; breathing stays at or under 5% |
| `SplashScreenTest.wink_closesOnlyTheRightEye_thenReopens` | The instant wink shuts only the right eye and opens it after `WINK_HOLD_MS` |
| `SplashScreenTest.blink_closesBothEyes` | A blink shuts both eyes |
| `LayoutMatrixTest.splash_lilyFaceClearOfStart` (4 sizes), and in `splash_startVisible_fontScale13` | The panel, the paws (14 dp above it) and Start stay below her face |

## Commit
`feat(splash): the Headspace tarsier matches Lily, and Start sits in a panel below her face (NFR-ACC-02, NFR-IND-01)`
