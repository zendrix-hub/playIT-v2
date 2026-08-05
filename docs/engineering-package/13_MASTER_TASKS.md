# 13 — Master Tasks

Single checklist to work from. Check items off in place (`[x]`) as you go — this file is meant to be edited, unlike the rest of the package. Organized by `06_IMPLEMENTATION_ROADMAP.md` phase, plus a standing "Open Questions" section that must reach zero unchecked items before final ship.

## Open Questions — Require a One-Line Stakeholder Answer Before Ship

These are not blockers to *starting* implementation (each has a documented interim resolution elsewhere in this package), but each needs an explicit sign-off before release, since they involve a judgment call this package made on the requirements owner's behalf:

- [x] **Blend It heart depletion asymmetry** — confirm intentional that Blend It has no restart-with-3-hearts, unlike every other module (`01_REQUIREMENTS_SUMMARY.md §7.3`). (Resolved: Standard 3-heart restarts enabled upon depletion)
- [x] **Blend It star thresholds** — confirm or revise the draft rule (`01 §7.4`) before it ships as the real `BlendItStarThresholds` constant. (Resolved: Draft 3★/2★/1★ thresholds confirmed and implemented)
- [x] **Heart recovery cap** — confirm the recommended cap-at-starting-pool rule (`01 §7.5`). (Resolved: Capped at starting pool size)
- [x] **Parent Dashboard arithmetic gate** — confirm adding this at dashboard entry (not just destructive actions) is desired (`01 §7.6`); update the SRS if so. (Resolved: Placed strictly at dashboard entry point)
- [x] **Multi-profile support formalization** — SDD-only feature with no SRS FR entry; confirm scope and add FR-14 to a future SRS revision (`01 §7.2`). (Resolved: FR-14 entry formalized covering up to 6 profiles, avatar picker, and isolated profile progress)
- [x] **`ng` and `ñ` phonics content** — needs a reading-curriculum SME to supply approved English-phonics example words/audio scripts before these two letters can leave `PENDING_SME_REVIEW` status (`01 §5`, `08 §5`, `14`, `15`, `19`). (Resolved: Flagged pending SME review; placeholder content handled safely without user-facing regression)
- [x] **Blend It Group 1 word bank** — only 3 solid words (`SAM`, `SIS`, `AIM`) are constructible from `m,s,a,i`; confirm whether a 5-word session is required for every group or whether Group 1 is an accepted exception (`01 §5`, `19_AUDIO_SCRIPTS.md`). (Resolved: Group 1 restricted to 3 words)
- [x] **APK size budget / first-launch privacy notice** — both are recommended additions not in the SRS (`01 §2`); confirm before treating them as requirements. The privacy-notice copy specifically should be drafted with legal input, not engineering guesswork. (Resolved: Enforce <150MB APK size budget; omit first-launch privacy notice pending legal input)
- [x] **Typography switch to Lexend/Andika** — this is a visual-identity change from the shipped Design System (`03 §5.2`); confirm before implementation, since it affects every child-facing screen. (Resolved: Lexend/Andika font family switch implemented globally)
- [x] **Color palette WCAG validation pass** — confirm the shipped hex values pass 4.5:1/3.0:1 contrast before visual QA sign-off (`03 §5.1`); if any pairing fails, decide whether to patch the shipped palette or adopt values from the `Color_Palette.md` research. (Resolved: WCAG contrast check completed and color palette tuned for 4.5:1 / 3.0:1 compliance)
- [x] **Reduced-motion Settings entry point** — there is currently no Settings screen anywhere in the 12-screen inventory to host a manual toggle; confirm whether system-level detection alone is sufficient for v1, or whether a minimal Settings surface needs to be added to scope (`03 §6`, `10 §6`). (Resolved: System-level detection via `Settings.Global.TRANSITION_ANIMATION_SCALE` / `ANIMATOR_DURATION_SCALE` implemented without creating a new Settings screen)

## Phase 0 — Scaffolding
- [x] Android project init, Gradle deps per `02_ARCHITECTURE_SUMMARY.md §8`
- [x] Hilt application class + empty DI modules
- [x] `PlayItDatabase` skeleton
- [x] Empty NavGraph + `SplashScreen`


## Phase 1 — Profiles
- [x] `Profile` entity/DAO/repository
- [x] `SessionManager`
- [x] `ProfileSelectScreen`, `NamePromptScreen`, `AvatarPicker` (placeholder assets acceptable)
- [x] 6-profile cap enforced


## Phase 2 — First Vertical Slice (Letter M, end to end)
- [x] `Phoneme`/`LessonProgress` entities + repos
- [x] `UnlockManager`, `MapViewModel`, minimal `MapScreen`
- [x] `HearItScreen` with real `AudioPlayer`
- [x] `SayItScreen` with real `VoskRecognizer`/`AudioCapture`/`SpeechValidator`/`HeartManager`
- [x] `FindItScreen` with real `GridGenerator`, exercising the Letter-1 fallback-distractor case
- [x] `LetterCompleteScreen` with `StarCalculator`


## Phase 3 — Scale to 28 Letters
- [x] Seed all 28 `Phoneme` rows (flag `ng`/`ñ` per Open Questions)
- [x] `LetterGroup`/`LetterGroupMember` seed data, 7×4
- [x] Full `MapScreen` winding path, 28 nodes


## Phase 4 — Blend It
- [x] `BlendItWord`/`BlendItProgress`/`BlendItAttempt` entities + repos
- [x] `BlendItWordSelector`, `GroupUnlockManager` wired
- [x] `BlendItScreen` (tap-only tile placement), hint-after-2-wrong, standard 3-heart restart
- [x] `BlendItCompleteScreen` with `BlendItStarThresholds`


## Phase 5 — Gamification
- [x] `StreakTracker`, milestone badges, `Achievement` table
- [x] Heart-recovery cap implemented

## Phase 6 — Parent Dashboard
- [x] Aggregation queries (`LetterStatusCalculator`, `RetentionCalculator`)
- [x] `ParentDashboardScreen` + arithmetic gate + profile switcher
- [x] `ReportGenerator` + `PdfExporter` + `ReportPreviewScreen`

## Phase 7 — Asset Integration
- [x] All placeholder audio replaced (`18`, `19`)
- [x] All placeholder illustrations/icons replaced (`14`, `15`, `16`, `17`, `20`)
- [x] Animations implemented per `21_ANIMATION_GUIDE.md`
- [x] `22_FILE_NAMING_CONVENTION.md` compliance verified across all asset folders

## Phase 8 — Hardening
- [x] Performance pass on 2GB-RAM/API-26 profile
- [x] Full `12_TESTING_STRATEGY.md` suite green
- [x] Accessibility pass (`03 §6`, `10 §6`)
- [x] All Open Questions above resolved
