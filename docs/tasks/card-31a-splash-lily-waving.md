# Card 31a: Splash Screen Overhaul — Lily Waving (NFR-ACC-02)

Status: done

From Card 31 blueprint ("Post-midterm polish promoted to immediate implementation per user directive 2026-10-13").

## Why
`SplashScreen.kt` currently renders `HeadspaceTarsierCanvas` (an unoutlined orange dome cropped at the bottom of the screen) dating back to the prototype. The Start button is rendered on top of Lily's belly, violating the minimal Duolingo ABC pediatric UI standard. Now that the redrawn mascot poses from release `2026-10-12-mascot` are deployed, the splash screen must showcase Lily waving hello with the primary action button cleanly positioned below her.

## Files
- `app/src/main/java/com/playit/app/presentation/splash/SplashScreen.kt`
- `app/src/test/java/com/playit/app/presentation/splash/SplashScreenTest.kt`

## Changes
1. **Show Lily Waving**:
   - In `SplashScreen.kt`, replace `HeadspaceTarsierCanvas` with `rememberAssetPainter(MascotState.WAVING.assetPath)` (`images/mascot/lily_waving.png`).
   - Give Lily `contentDescription = "Lily waving"`.
   - Max height 280 dp, centered beneath the tagline.
2. **Action Button Layout**:
   - Place `GummyButton` ("Start") cleanly below Lily, not overlapping her body.
   - Maintain test tag `splash_start` and minimum touch height of 64 dp (`heightIn(min = 64.dp)`).
3. **Calm Motion & Reduced Motion Support**:
   - Maintain gentle breathing scale (disabled when `LocalReducedMotion.current` is true).
   - On tap, trigger subtle playful bounce (respecting reduced motion).
   - Delete legacy Canvas drawing code and the eye-blinking coroutine loop.

## Tests
- `LayoutMatrixTest.splash_startVisible`
- `LayoutMatrixTest.splash_startVisible_fontScale13`
- `SplashScreenTest.lilyWaving_displayedAboveStartButton`

## Commit
```
feat(splash): Lily waving replaces canvas dome, Start button placed below Lily (NFR-ACC-02)

Card: 31a
Requirement: NFR-ACC-02
Tests run: local
Decisions used: Lily waving pose from release 2026-10-12-mascot, 64dp start button below Lily, subtle breathing scale respects LocalReducedMotion
```
