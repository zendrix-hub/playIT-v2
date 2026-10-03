@AGENTS.md

## Shortcut Command: "run and review"

When the user enters `"run and review"` (or `"review"`), execute the following workflow:

1. **Read Latest Handoff Context**:
   - Inspect `docs/tasks/SESSION_HANDOFF.md` for current sprint state, commit hashes, and notes.

2. **Run Mechanical Card Checks**:
   - Run the mechanical verification script in WSL for recent agy commits:
     ```bash
     python3 tools/dev/review_card.py 11
     python3 tools/dev/review_card.py 12
     ```
   - Confirm all checks (`files`, `tests`, `status`, `evidence-log`, `body`, `emoji`) output `PASS`.

3. **Verify Tests & CI**:
   - Run unit tests: `tools/dev/gradlew_wsl.sh testDebugUnitTest` (or `./gradlew testDebugUnitTest`).
   - Check CI status: `tools/dev/ci_status.sh`.

4. **Review Commits & Technical Acceptance**:
   - Review code diffs:
     - `0d9ad3a` — Card 11 (Stars math, 3-heart restart, 5-heart display, session hearts persistence).
     - `dcff981` — Capstone 2 Week 3 Specifications (`docs/SRS_v3.0_Refactored.md`, `docs/SDD_v2.0_Refactored.md`, `docs/SPMP_v2.0_Refactored.md`).
     - `ef03bea` — Card 12 (Find It distractor isolation, gentle correction orange, soft pop audio, `ZeroEmojiPolicyTest`).
   - Record technical acceptance in `docs/evidence-log.md` (change status from `pass (local)` / `pending` to `accepted`).

5. **Proceed with Claude-Owned Next Steps**:
   - **Card 09 (Held /m/)**: Run Kokoro / Chatterbox pipeline in `tools/audio/` to prepare the held sound release manifest.
   - **Card 13 (Batch-1 Images)**: Cut out backgrounds for the 29 candidate PNGs picked in Card 08 (`667ea0e`), verify transparency and dimensions, generate `docs/image-release/<date>/manifest.json`, and author Card 13.
   - **Task Cards**: Author Card 10 (Screenshot tests / Roborazzi) and Card 03b (Say It corrections).
