# 06 — Implementation Roadmap

A phased build order. Each phase ends in a runnable, demoable state — don't move to the next phase with a broken build.

## Phase 0 — Project Scaffolding
- Android project init, package structure per `07_FOLDER_STRUCTURE.md`.
- Gradle deps: Compose, Material 3, Hilt, Room, Navigation Compose, Coroutines, Coil, Vosk (AAR), min SDK 26.
- Hilt application class + base DI modules (empty, wired).
- `PlayItDatabase` skeleton with zero entities — confirm it builds and migrations run.
- Empty `NavGraph` with a single placeholder `SplashScreen`.
- **Exit criteria:** app installs on a min-SDK-26 emulator and shows a blank splash.

## Phase 1 — Profile System (first vertical slice, part A)
- `Profile` entity + DAO + `ProfileRepository`/Impl.
- `SessionManager` singleton.
- `SplashScreen` → `ProfileSelectScreen` (empty state: "add profile" only) → `NamePromptScreen` (`AvatarPicker` with a placeholder icon set — real assets come later per `14_ASSET_MANIFEST.md`) → back to `ProfileSelectScreen` with the new `ProfileCard`.
- Enforce the 6-profile cap (`01_REQUIREMENTS_SUMMARY.md §5`).
- **Exit criteria:** create, select, and switch between profiles; data survives app restart.

## Phase 2 — Map + One Full Letter, End to End (first vertical slice, part B)
- `Phoneme`, `LessonProgress` entities + DAOs + repositories.
- `UnlockManager`, `MapViewModel`, `MapScreen` with exactly one unlocked node (`m`) and one locked node (`s`) — placeholder art is fine.
- `HearItScreen` (real `AudioPlayer`, one placeholder audio asset) → `SayItScreen` (real `VoskRecognizer` + `AudioCapture` + `SpeechValidator` + `HeartManager`, wired against the model bundled in this phase) → `FindItScreen` (real `GridGenerator` — exercise the Letter-1 fallback-distractor edge case from `01 §5` here, since Letter M is exactly that case) → `LetterCompleteScreen` (`StarCalculator`).
- **Exit criteria:** a single letter is fully playable with real speech recognition and real persistence; unlocking `s` on completion is visible on the Map.

## Phase 3 — Scale to All 28 Letters
- Seed `Phoneme`/asset data for all 28 letters (flag `ng`/`ñ` per `01 §5` if SME content isn't ready yet — build the pipeline so they slot in later without a schema change).
- `LetterGroup`, `LetterGroupMember` entities; extend `MapViewModel` to render the full winding path (all 28 nodes).
- Verify `GroupUnlockManager` against real group boundaries.
- **Exit criteria:** full 28-letter map renders and is playable start to finish (content quality aside — placeholder audio/art is acceptable here if `14`–`22` production hasn't landed yet).

## Phase 4 — Blend It
- `BlendItWord`, `BlendItProgress`, `BlendItAttempt` entities; `BlendItWordSelector`, `GroupUnlockManager` integration.
- `BlendItScreen` (tile bank, slot row, hint-after-2-wrong, heart pool with the no-restart depletion rule per `01 §1 Module 4`) → `BlendItCompleteScreen`.
- Implement the draft `BlendItStarThresholds` behind its named constant (`01 §7.4`).
- **Exit criteria:** a full 4-letter group unlocks its Blend It node and a 5-word session is playable.

## Phase 5 — Gamification Layer
- `StreakTracker`, `Achievement`/badge unlocking, badge UI on the Map's `TopStatsBar`.
- Heart-recovery cap (`01 §7.5`) implemented and tested.
- **Exit criteria:** streak badges unlock at the correct milestones; a 24h-inactivity reset is demonstrable in a test harness (don't wait 24 real hours — inject a clock).

## Phase 6 — Parent Dashboard & PDF
- `SayItAttempt`/`FindItAttempt` aggregation, `LetterStatusCalculator`, `RetentionCalculator`.
- `ParentDashboardScreen` + arithmetic entry gate (`01 §7.6`) + `ProfileSwitcherDropdown`.
- `ReportGenerator` + `PdfExporter` + `ReportPreviewScreen`.
- **Exit criteria:** dashboard reflects real Phase 1–5 gameplay data; PDF export produces a readable file on-device.

## Phase 7 — Content & Asset Integration Pass
- Swap every placeholder asset for the production asset from `14`–`22`.
- Full audio pass (`18`, `19`), full illustration pass (`15`, `16`), icons (`20`), animations (`21`).
- **Exit criteria:** zero placeholder assets remain; `22_FILE_NAMING_CONVENTION.md` compliance verified.

## Phase 8 — Hardening
- Performance pass on a 2GB-RAM/API-26 emulator profile (Baseline Profiles, recomposition audit per `05 §4`).
- Full `12_TESTING_STRATEGY.md` pass (unit, instrumented, manual QA script).
- Accessibility pass against `03_DESIGN_SYSTEM_SUMMARY.md §6` (contrast, touch targets, reduced-motion — note the missing Settings screen flagged there).
- **Exit criteria:** all `13_MASTER_TASKS.md` checkboxes ticked.

## Dependency Notes
- Phase 2 is the riskiest phase (first real Vosk integration) — do not let content production (Phase 7 assets) block starting Phase 2 with placeholders; the reverse dependency (waiting on real audio to test Say It) would stall the whole schedule.
- Phases 3 and 7 can partially overlap once Phase 2's pattern is proven: content/asset production (Phase 7) can start as soon as `14`–`22` specs are final, independent of app-code phases 3–6.
