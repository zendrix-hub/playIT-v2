@AGENTS.md

## Shortcut Command: "run and review"

When the user enters `"run and review"` (or `"review"`), Claude operates as **The Mind and Implementator** for the 2-day refactoring sprint (finishing Saturday, October 10, 2026), guided by `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md`.

### Continuous Multi-Card Execution Protocol (Active)

> **MANDATE:** Execute **ALL** remaining cards continuously in an uninterrupted sequence. **Do NOT stop or pause for agy validation between individual cards.** Implement, verify with local tests, commit, push, and immediately advance to the next card. Once ALL cards are pushed, agy will perform the comprehensive validation, Roborazzi 4-device screenshot matrix, and release APK assembly.

1. **Pull and verify newest state:**
   - `git pull origin refactor/hear-say-it`.
   - Read `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md` and `docs/tasks/SESSION_HANDOFF.md`.
   - Note: Cards 13, 26b, 18, 09b, and 19 are ALREADY validated and accepted.

2. **Continuous Implementation Pipeline:**
   Execute the remaining cards in this exact staged order:
   - **Tier 1 (Core Cut Line):**
     1. **Card 03b** (`docs/tasks/card-03b-tutor-policy-fixes.md`): Spoken Say It corrections (`fb_no_ah` on `ADDED_VOWEL`, `fb_try_sound` on `LETTER_NAME`).
     2. **Card 20** (`docs/tasks/card-20-findit-blendit-fit.md`): Find It & Blend It layout in `LessonScaffold`; responsive grid/tile scaling; supersedes Card 16. Plan: Task 4.
     3. **Card 22** (`docs/tasks/card-22-map-overhaul.md`): Map overhaul (`MapLayout.kt`, rope trail, compact header, 16-char learner name fit, unlock moment). Plan: Task 6.
   - **Tier 2 (Presentation & Motion Polish):**
     4. **Card 21** (`docs/tasks/card-21-complete-splash-profile-fit.md`): Complete, Splash, Profile screens fit; child text styles. Plan: Task 5.
     5. **Card 23** (`docs/tasks/card-23-purposeful-effects.md`): Screen transitions, correct pop, heart wobble, star drop, centre confetti, zero haptics, `LocalReducedMotion`. Plan: Task 7.
   - **Tier 3 (Curriculum & Accessibility):**
     6. **Card 15** (`docs/tasks/card-15-decodable-blendit-words.md`): Purge 5 non-decodable Blend It words in `DatabaseModule.kt` (replace with AM, SUM, TUB, YAM, ZIP; QUIZ exception).
     7. **Card 24** (`docs/tasks/card-24-captions-mouth-cues.md`): Sound captions & mouth-shape cues (`ArticulationGroup`, `CaptionBubble`, `ArticulationCue`). Plan: Task 8.

3. **Per-Card Execution Loop:**
   For each card in succession:
   a. Implement code and unit tests directly in WSL.
   b. Run local unit tests: `tools/dev/gradlew_wsl.sh testDebugUnitTest` (or `./gradlew testDebugUnitTest`).
   c. Enforce Zero-Emoji Policy (`ZeroEmojiPolicyTest`).
   d. Run `python3 tools/dev/review_card.py <NN>` (must be PASS).
   e. Commit on `refactor/hear-say-it` with the card's designated commit format.
   f. Mark `Status: done` in the card file and tick in `docs/engineering-package/13_MASTER_TASKS.md`.
   g. Push to `origin refactor/hear-say-it`.
   h. **Do not wait for agy** — immediately start the next card in the sequence!

4. **Sprint Finalization & agy Hand-off:**
   After pushing the final card (Card 24):
   - Update `docs/tasks/SESSION_HANDOFF.md` summarizing all completed cards, commits, and passing tests.
   - Update `docs/tasks/VALIDATOR_RUNBOOK_OCT10.md` notifying agy that the entire sprint batch is ready for final batch validation (full 280+ unit test run, 4-size Roborazzi screenshot audit, and release debug APK assembly).
