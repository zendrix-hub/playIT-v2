# Card 28: UX and Pedagogy Refinements (FR-01, FR-02, FR-03, FR-13, NFR-ACC-02)

Type: code
Status: ready (for Claude review and agy implementation)

## Why
Physical hardware testing of APK B by the user surfaced 6 critical usability and pedagogical friction points affecting 6-year-old Grade 1 learners using PlayIT independently without teacher or parent intervention:
1. **Hear It Screen Closure Clash**: `HearItSequenceBuilder.TEMPLATE` concludes with `car_say_it_with_me.wav` (*"Say it with me!"*), immediately followed by `ui_hearit_next.wav` (*"Great listening! Tap the big button."*), leaving children confused about when or where to speak on a purely receptive screen.
2. **Pre-reader Distraction in Hear It**: Textual caption bubbles (`CaptionBubble`) distract non-reading 6-year-olds from looking at the letter glyph and keyword picture card.
3. **Debug Telemetry on Child UI**: `SayItScreen.kt` renders `Heard: "..." -> errorType (attempt #)` on physical test builds.
4. **Say It 3rd-Miss Trapping**: After 3 misses, `TutorAction.LeadAndMoveOn` plays remodeling audio, but leaves the mic looking active, keeps the banner in orange retry mode, and doesn't clearly spotlight the Next CTA.
5. **Language Purity & Sound-Out Cadence in Blend It**: `BlendItCard.kt` has Tagalog copy (`"Pindutin para marinig"`). The phoneme replay delay (400ms) blurs phonemes together too quickly for early blending.
6. **Desynchronized Star Counters**: Map TopBar (`TopStatsBar`) and Profile Cards stay at 0 stars because `ProfileEntity.totalStars` is never aggregated from `lesson_progress` and `blend_it_progress`.
7. **Map Screen Friction**: Active pulsing nodes require a 2-tap popup dialog to start; companion speech bubbles violate the 16sp reading floor (11sp); greeting chip text truncates with ellipsis on phones.

All decisions resolved during the `/grill-me` design tree session (user approved Option A across all branches, 2026-10-10).

## Files to Modify

All paths relative to `app/src/main/java/com/playit/app/` or test directories:

1. **Hear It Screen**:
   - `domain/manager/HearItSequenceBuilder.kt`: Remove `car_say_it_with_me` token from `TEMPLATE`. Sequence terminates naturally on `KEYWORD, PHONEME`.
   - `presentation/hearit/HearItScreen.kt`: Remove `CaptionBubble` from the UI tree; retain only `ArticulationCue`. Expand `LetterCard` height bounds so the letter and keyword picture are visually prominent.
   - `presentation/hearit/HearItViewModel.kt`: Keep sequence playback callback triggering `ui_hearit_next` and activating `nextHighlighted` with breathing pulse.

2. **Say It Screen**:
   - `presentation/sayit/SayItScreen.kt`: Remove `BuildConfig.DEBUG && lastHeard != null` Compose Text element. Ensure `GummyButton` has `breathingPulse(enabled = canContinue)` and spring bounce when unlocked.
   - `presentation/sayit/SayItViewModel.kt`:
     - When `TutorAction.LeadAndMoveOn` triggers: transition feedback banner to warm encouraging copy (*"Nice try! Let's keep going!"*), ensure mic is disabled, and set `_canContinue.value = true` with chime.
     - Mark phoneme as `NEEDS_PRACTICE` in state/database for future recall checks.

3. **Blend It Screen**:
   - `presentation/components/BlendItCard.kt`: Replace `"Pindutin para marinig"` with `"Tap to hear word"`.
   - `presentation/blendit/BlendItViewModel.kt`:
     - Slow down phoneme loop delay from 400ms to **750ms** per letter tile with spring highlight.
     - Add a **500ms pause** with all tiles highlighted together.
     - Play whole word audio clearly, followed by celebration chime and star fanfare.

4. **Map & Profile Screens**:
   - `data/local/dao/ProfileDao.kt` / `LessonProgressDao.kt` / `BlendItProgressDao.kt`: Add dynamic star calculation query:
     `SELECT COALESCE(SUM(starsEarned), 0) FROM lesson_progress WHERE profileId = :profileId` and `blend_it_progress`.
   - `data/repository/ProfileRepositoryImpl.kt`: Dynamically compute or populate `totalStars` so `getAllProfiles()` and `getProfileById()` return live, accurate star counts matching `ReportGenerator`.
   - `presentation/map/MapScreen.kt`:
     - Direct tap on the active unlocked node (`node.orderIndex == activeNodeIndex` or uncompleted unlocked letter) immediately invokes `onNodeSelected(node.id)`. Completed nodes retain `NodeActionPopupDialog` for practice replay.
     - Update `LilyGreetingChip`: Shorten message or allow multiline wrap so child name and unit text never truncate with ellipsis.
     - Update `MascotMapDialogueBubble`: Raise text font size from 11sp to **16sp** (pediatric reading floor).

5. **Unit & Screenshot Tests**:
   - `domain/manager/HearItSequenceBuilderTest.kt`: Update expected template tokens.
   - `presentation/hearit/HearItViewModelTest.kt`: Verify sequence without `car_say_it_with_me`.
   - `presentation/sayit/SayItViewModelTest.kt`: Verify 3rd-miss `LeadAndMoveOn` state and `canContinue` activation.
   - `presentation/blendit/BlendItViewModelTest.kt`: Verify phoneme sound-out cadence and delay timings.
   - `data/repository/ProfileRepositoryTest.kt`: Verify dynamic totalStars aggregation.
   - `presentation/map/MapViewModelTest.kt`: Verify map node tap behaviors and reactive star stats.
   - Roborazzi screenshots (`./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`).

## Acceptance Criteria
- `./gradlew testDebugUnitTest` passes with zero failures.
- Zero Tagalog strings anywhere in child-facing gameplay.
- No `Heard: "..."` text rendered on screen in debug or release builds.
- Hear It sequence smoothly finishes on keyword/sound, followed by *"Great listening! Tap the big button."* and a pulsing Next button.
- Say It 3rd miss unlocks Next button with encouraging copy and celebratory pulse.
- Map TopBar and Profile Cards show the real cumulative star count earned from letter lessons and blend challenges.
- Active map node opens directly on single tap.
