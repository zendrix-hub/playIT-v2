# agy runbook: executing task cards

AGENTS.md links here. Follow this for every card in `docs/tasks/`.

## Roles
- agy implements cards, one per session.
- Claude in WSL critiques each card before agy runs it, and reviews the diff after.
- The Claude chat (capstone validator) writes cards and gives final acceptance.
- GitHub CI is the test gate: `.github/workflows/android_ci.yml` runs `./gradlew testDebugUnitTest` on the draft PR from `refactor/hear-say-it` to `main`. It does not run on branch pushes alone.
- The user pushes. agy never pushes.

## Session start
1. Read AGENTS.md, docs/tasks/README.md, and the spec sections the card cites.
2. Pick the first `docs/tasks/card-NN-*.md` (lowest NN) whose header says `Status: ready`. Card status values are defined in docs/tasks/README.md.
3. If no card is ready, say so and stop.

## Execute one card only
- Change only the files the card lists, plus the bookkeeping files: `docs/engineering-package/13_MASTER_TASKS.md`, `docs/evidence-log.md`, and the card's own `Status:` line.
- Write every test in the card's Tests section.

## Tests
- Run `./gradlew testDebugUnitTest`.
- If there is no JDK or Android SDK, do not claim the tests pass. Write "Unit tests not run locally; verify in CI" in the commit body.

## Commit
Make one commit per card, on `refactor/hear-say-it`. Include all of these in it:
1. Tick the card under "Hear It / Say It refactor" in `13_MASTER_TASKS.md`.
2. Change the card header to `Status: done`.
3. Add a row to `docs/evidence-log.md` with CI run `pending` and Phone test `pending`. Put the commit subject in the Commit column; the reviewer replaces it with the hash.
4. Use the message in the card's Commit section. The body must include:
   ```
   Card: NN
   Requirement: <IDs, or none>
   Tests run: local | CI only
   Decisions used: <list, or none>
   ```

Never push.

## Stop and ask
Write the question to `docs/tasks/QUESTIONS.md` (card, question, what you found, options), leave your changes uncommitted, and stop when:
- a [confirm] or [proposed] item isn't covered by the Decisions in AGENTS.md;
- the card needs a file it doesn't list;
- a test fails twice;
- the card conflicts with the spec.

## Never
- Push.
- Edit `docs/specs/` unless a card says so.
- Add or replace audio in `app/src/main/assets/` unless a card says so.
- Use emojis in UI text.

## Current queue (2026-09-29, updated)
The `Status:` line in each card is the source of truth; this table is a snapshot.

| Card | Status | Note |
|---|---|---|
| 00 | done | CI pending |
| 01 | done | CI pending |
| 02 | done | CI pending |
| 03 | ready | Adds no audio; tutor clips come after the listening checklist |
| 04 | ready | Run in a new session after 03 is committed |
