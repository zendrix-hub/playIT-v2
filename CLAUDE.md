@AGENTS.md

## Shortcut Command: "run and review"

When the user enters `"run and review"` (or `"review"`), Claude operates as **The Mind and Implementator** for the 2-day refactoring sprint (finishing Saturday, October 10, 2026), guided by `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md`:

1. **Pull and read newest state:**
   - `git pull origin refactor/hear-say-it`.
   - Read `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md` and the latest handoff notes at the end of `docs/tasks/SESSION_HANDOFF.md`.
2. **Review & Accept prior commits:**
   - Commits awaiting acceptance: `b909329` (Card 13), `0886ec4` (Card 26b), `5489293` (Card 18), `1df3f56` (Card 09b).
   - Verify mechanical checks: `python3 tools/dev/review_card.py NN` (all PASS).
   - Update `docs/evidence-log.md` with commit hashes and set status to `(accepted)`.
3. **Plan & Implement the next Sprint Card:**
   - Follow the sequence in `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md` (Card 19 $\to$ Card 03b $\to$ Card 20 $\to$ Card 22 $\to$ Card 21 $\to$ Card 23 $\to$ Card 24).
   - For Card 19, follow Task 3 in `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`.
   - Write the Kotlin/Compose code and unit tests directly in WSL.
   - Run local unit tests: `tools/dev/gradlew_wsl.sh testDebugUnitTest`.
   - Ensure Zero-Emoji Policy compliance (`ZeroEmojiPolicyTest`).
   - Commit on `refactor/hear-say-it` with standard card commit headers and push to `origin refactor/hear-say-it`.
4. **Emit Validator Specification for agy:**
   - Update `docs/tasks/VALIDATOR_RUNBOOK_OCT10.md` specifying:
     * Card and commit hash ready for agy validation.
     * Automated test verification commands.
     * Layout matrix & Roborazzi screenshot checks across the 4 profiles (`compact`, `a21s`, `phone`, `tablet`).
     * APK build verification targets.
5. **Maintain Handoff Documentation:**
   - Append a structured entry to `docs/tasks/SESSION_HANDOFF.md` detailing changes implemented, tests passing, and the next active task.
