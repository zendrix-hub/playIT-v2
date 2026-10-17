# Card 28c: Hear It visual focus: mouth articulation cues reserved exclusively for Say It expressive scaffolding (NFR-ACC-01, NFR-ACC-02)

Type: code
Status: done

Follows the phone test of APK E on Galaxy A21s and the design grill interview on 2026-10-11.

## Why
1. **Hear It Visual Clutter & Cognitive Load**: In Hear It (the auditory-orthographic receptive "I do" modeling step), the static floating mouth-shape picture beside the central replay button created visual asymmetry and divided the child's attention between the letter symbol, anchor picture, and mouth image (Cognitive Load Theory; Sweller, 1988; Mayer, 2009).
2. **Purposeful Articulation Scaffolding in Say It**: Visual articulation mouth cues are most effective during expressive speech production as a targeted remedial prompt (Boyer & Ehri, 2011). In Say It, mouth cues appear dynamically after attempt 2 when Lily says *"Watch my lips! Let's try again!"*, providing high-context pedagogical scaffolding exactly when the child struggles.
3. **Clean Symmetrical Layout**: Removing the mouth cue from Hear It eliminates both the 72 dp cue box and the counter-balancing 72 dp invisible spacer, cleanly centering the primary speaker button with zero risk of viewport clipping on 360x640 compact displays.

## Changes
- **HearItScreen.kt**:
  - Remove `ArticulationCue` and the balancing `Spacer(Modifier.size(CUE_SIZE))`.
  - Center the `Box` containing the PrimaryJoy speaker replay button and its pulsing animation.
  - Remove unused `CUE_SIZE` constant and `ArticulationCue` import.
- **LayoutMatrixTest.kt**:
  - Update KDoc for `hearIt_stepsAboveNext` to reflect the clean centered play button.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/hearit/HearItScreen.kt`
- Tests: `screenshot/LayoutMatrixTest.kt`

## Tests
- `LayoutMatrixTest`: `hearIt_playVisible`, `hearIt_stepsAboveNext`
- Full suite: 403 tests, 0 failed, 6 skipped by design.
