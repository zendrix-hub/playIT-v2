@AGENTS.md

## Shortcut Command: "run and review"

When the user enters `"run and review"` (or `"review"`), Claude operates as **The Mind and Implementator** for the 2-day refactoring sprint (finishing Saturday, October 10, 2026), guided by `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md`.

### Continuous Multi-Card Execution Protocol (Active)

> **MANDATE:** Execute **ALL** remaining cards continuously in an uninterrupted sequence. **Do NOT stop or pause for agy validation between individual cards.** Implement, verify with local tests, commit, push, and immediately advance to the next card. Once ALL cards are pushed, agy will perform the comprehensive validation, Roborazzi 4-device screenshot matrix, and release APK assembly.

1. **Pull and verify newest state:**
   - `git pull origin refactor/hear-say-it`.
   - Read `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md` and `docs/tasks/SESSION_HANDOFF.md`.
   - Note: Cards 13, 26b, 18, 09b, 19, and 03b are ALREADY completed, validated, and pushed.

2. **Continuous Implementation Pipeline:**
   Execute the remaining cards in this exact staged order:
   - **Tier 1 (Core Cut Line):**
     1. **Card 20** (`docs/tasks/card-20-findit-blendit-fit.md`): Find It & Blend It layout in `LessonScaffold`; responsive grid/tile scaling; supersedes Card 16. Plan: Task 4.
     2. **Card 22** (`docs/tasks/card-22-map-overhaul.md`): Map overhaul (`MapLayout.kt`, rope trail, compact header, 16-char learner name fit, unlock moment). Plan: Task 6.
   - **Tier 2 (Presentation & Motion Polish):**
     3. **Card 21** (`docs/tasks/card-21-complete-splash-profile-fit.md`): Complete, Splash, Profile screens fit; child text styles. Plan: Task 5.
     4. **Card 23** (`docs/tasks/card-23-purposeful-effects.md`): Screen transitions, correct pop, heart wobble, star drop, centre confetti, zero haptics, `LocalReducedMotion`. Plan: Task 7.
   - **Tier 3 (Curriculum & Accessibility):**
     5. **Card 15** (`docs/tasks/card-15-decodable-blendit-words.md`): Purge 5 non-decodable Blend It words in `DatabaseModule.kt` (replace with AM, SUM, TUB, YAM, ZIP; QUIZ exception).
     6. **Card 24** (`docs/tasks/card-24-captions-mouth-cues.md`): Sound captions & mouth-shape cues (`ArticulationGroup`, `CaptionBubble`, `ArticulationCue`). Plan: Task 8.
   - **Tier 4 (Academic Documentation Refactoring):**
     7. **Card 27** (`docs/tasks/card-27-academic-docs-refactor-sdd-srs-spmp.md`): Refactor and synchronize `docs/SDD_v2.0_Refactored.md` (§2.0 status table & component designs), `docs/SRS_v3.0_Refactored.md` (RTM v3.0 mapping to test classes), and `docs/SPMP_v2.0_Refactored.md` (WBS, milestone progress) with the shipped codebase.

3. **Collision-Free Git Protocol (Overnight Autonomous Mode):**
   - **agy is idle during Claude's run**: agy will NOT make any commits or pushes while Claude is executing overnight. Claude has exclusive push access to `refactor/hear-say-it`.
   - **Safe sync before commit**: Before committing and pushing each card, run:
     `git pull --rebase origin refactor/hear-say-it`
   - **Push incrementally**: Push each card commit immediately to `origin refactor/hear-say-it` so progress is safe on GitHub.

4. **Per-Card Execution Loop:**
   For each card in succession:
   a. Implement code and unit tests directly in WSL.
   b. Run local unit tests: `tools/dev/gradlew_wsl.sh testDebugUnitTest` (or `./gradlew testDebugUnitTest`).
   c. Enforce Zero-Emoji Policy (`ZeroEmojiPolicyTest`).
   d. Run `python3 tools/dev/review_card.py <NN>` (must be PASS).
   e. Commit on `refactor/hear-say-it` with the card's designated commit format.
   f. Mark `Status: done` in the card file and tick in `docs/engineering-package/13_MASTER_TASKS.md`.
   g. Push to `origin refactor/hear-say-it`.
   h. **Do not wait for agy** — immediately start the next card in the sequence!

5. **Sprint Finalization & agy Hand-off:**
   After pushing Card 27:
   - Update `docs/tasks/SESSION_HANDOFF.md` summarizing all completed cards, commits, and passing tests.
   - Update `docs/tasks/VALIDATOR_RUNBOOK_OCT10.md` notifying agy that the entire sprint batch (Cards 20, 22, 21, 23, 15, 24, 27) is ready for final batch validation (full 280+ unit test run, 4-size Roborazzi screenshot audit, and release debug APK assembly).

---

## Final Submission Packaging & Evidence-Log Acceptance

When the sprint batch is validated by agy (verified at `c07206e`), Claude operates at full capacity to complete final acceptance and academic deliverables:

1. **Pull Latest Changes:**
   `git pull origin refactor/hear-say-it`

2. **Evidence-Log Acceptance (`docs/evidence-log.md`):**
   - Fill commit hashes for cards 03b (`339f309`), 20 (`9bb8cb7`), 22 (`3be745b`), 21 (`9bac8ca`), 23 (`dd42582`), 15 (`299cea2`), 24 (`20efb22`), and 27 (`eaf8634`).
   - Append `(accepted)` review entries referencing agy's automated validation verdict (364 unit tests passed with 100% pass rate, 41 Roborazzi layout screenshot matrix passed across 4 display sizes, and `playit-debug-B-eaf8634.apk` assembled).

3. **Render Official Submission Package (`docs/submission/`):**
   - Run the dedicated rendering tool:
     `python3 tools/docs/render_submission_package.py`
   - Confirms generation of all four official academic documents into `docs/submission/` in both PDF and DOCX formats:
     * `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` + `.docx`
     * `playIT_SDD_v2.0.pdf` + `.docx`
     * `playIT_SRS_v3.0.pdf` + `.docx`
     * `playIT_SPMP_v2.0.pdf` + `.docx`
     * `SUBMISSION_OVERVIEW_AND_REVIEW_CHECKLIST.md`

4. **Commit & Push:**
   - Commit: `docs(submission): formalize batch acceptance and build official IT411 submission package (PDF and DOCX)`
   - Push to `origin refactor/hear-say-it`.

