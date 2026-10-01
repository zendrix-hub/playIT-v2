# agy runbook: executing task cards

AGENTS.md links here. Follow this for every card in `docs/tasks/`.

## Roles
| Who | Does | Commits | Pushes |
|---|---|---|---|
| Claude (in WSL) | Specs, architecture, research, task cards, card critique, review of every agy commit, fix cards, evidence log. Owns the audio pipeline and runs it locally; review audio stays outside the repo. Gives technical acceptance. | Docs and `tools/` only, never app code | Its own commits |
| agy | Implements one `ready` card per session | App code, tests, bookkeeping | Its own commits |
| User | Starts agy sessions, asks Claude to pull and review, approves audio with a teacher, answers `QUESTIONS.md`, opens and merges the PR, gives final approval on [proposed] items with the adviser, gives the go signal for new cards | None | None |
| GitHub CI | Test gate: `.github/workflows/android_ci.yml` runs `./gradlew testDebugUnitTest` on the draft PR from `refactor/hear-say-it` to `main`, and again on every push to the branch while the PR is open | None | None |

Push rules for Claude and agy: push only to `origin refactor/hear-say-it`. Never push to `main`, never force-push, never rewrite pushed history.

## Session start
1. Read AGENTS.md, docs/tasks/README.md, and the spec sections the card cites.
2. Pick the first `docs/tasks/card-NN-*.md` (lowest NN) whose header says `Status: ready`. Card status values are defined in docs/tasks/README.md.
3. If no card is ready, say so and stop.

## Execute one card only
- Change only the files the card lists, plus the bookkeeping files: `docs/engineering-package/13_MASTER_TASKS.md`, `docs/evidence-log.md`, the card's own `Status:` line, and `docs/tasks/SESSION_HANDOFF.md` (session notes and phone-test results for Claude; facts only, quote code and commands instead of paraphrasing).
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
5. Push: `git push origin refactor/hear-say-it`. Then report the commit hash and subject so the user can ask Claude to review.

## Self-check before committing
Cards 03 and 04 were first committed without most of these (see the evidence log rows "03 (fix)" and "04 (fix)"). Check every line before you run `git commit`:
- [ ] Every test named in the card's Tests section exists, with the card's name.
- [ ] Every item in the card's Changes section is done. Where the card gives code or a signature, the code matches it.
- [ ] `git status` shows only the card's files and the bookkeeping files. Nothing extra (no new JSON, assets, or helpers the card doesn't list).
- [ ] If the card has a pre-step with a stop condition, you checked it. A stop condition that is met means stop, not continue.
- [ ] The card's `Status:` line says `done`, `13_MASTER_TASKS.md` is ticked, and `docs/evidence-log.md` has the row, all in this same commit.
- [ ] The commit body has the `Card / Requirement / Tests run / Decisions used` lines, "Unit tests not run locally; verify in CI" if you did not run them, and the Co-Authored-By line if your tool adds one.

## Audio gate
Audio goes into `app/src/main/assets/` only when a card says so, and only files listed in a `docs/audio-release/<date>/manifest.json` (clips the user approved in a review page), copied unchanged and checked against the manifest's SHA-256. Anything not in a release manifest is not approved. The teacher audit happens before merging to `main`. If a card needs audio that is not approved, write the code so a missing clip is skipped (`AudioPlayer` already does this), add no audio, and note it in the commit body.

## Review and fix cards
After agy pushes, the user asks Claude to pull and review. Claude checks the commit against the card, the spec, and the self-check above, and reads the CI result on the PR.
- No findings: Claude sets the card to `Status: accepted` and fills in the evidence-log row (hash, CI run, review result).
- Findings: Claude writes a fix card named after the card with a letter, e.g. `card-03b-tutor-policy-fixes.md`, with `Status: ready`. agy runs it like any other card. The original card stays `done` until its fix card is accepted.
- A new card starts only after the previous one is accepted and the user gives the go signal.

## Stop and ask
Write the question to `docs/tasks/QUESTIONS.md` (card, question, what you found, options). Commit and push only that file (`docs(tasks): question on card NN`), so Claude sees it on the next pull. Leave the card's other changes uncommitted, and stop. Do this when:
- a [confirm] or [proposed] item isn't covered by the Decisions in AGENTS.md;
- the card needs a file it doesn't list;
- a test fails twice;
- the card conflicts with the spec.

## Never
- Push to `main`, force-push, or push another branch.
- Edit `docs/specs/` unless a card says so.
- Add or replace audio in `app/src/main/assets/` unless a card says so.
- Use emojis in UI text.

## Current queue (2026-09-30, after CI)
The `Status:` line in each card is the source of truth; this table is a snapshot.

| Card | Status | Note |
|---|---|---|
| 00 | accepted | CI green (draft PR #2) |
| 01 | accepted | CI green; phone test pending |
| 02 | accepted | CI green; phone test pending |
| 03 | accepted | CI green; phone test pending. The tutor-script proposal (docs/proposals/2026-09-30-tutor-script.md) will need a fix card 03b once the user approves the script; it also covers the third-miss banner, which still says "Let's try again" |
| 04 | accepted | CI green; phone test pending |
| 05 | accepted | 85a6ca2; CI green; phone test 2026-09-30: Hear It and Say It ladder pass, foil feedback not observable yet |

No card is ready. Cards 06 onward are proposals in `docs/proposals/2026-09-29-next-cards-and-story-hook.md` and wait for the user's go signal.
