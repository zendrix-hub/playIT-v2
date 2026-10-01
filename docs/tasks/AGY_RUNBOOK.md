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
2. Pick the first `ready` card in the order of the "Current queue" table at the end of this file (a fix card `card-NNb` comes first). Card status values are defined in docs/tasks/README.md.
   - A code session takes only code cards.
   - An image session takes only asset cards (their header says `Type: asset`).
3. If no card is ready, say so and stop.

## Execute one card only (or two, see below)
- Change only the files the card lists, plus the bookkeeping files: `docs/engineering-package/13_MASTER_TASKS.md`, `docs/evidence-log.md`, the card's own `Status:` line, and `docs/tasks/SESSION_HANDOFF.md` (session notes and phone-test results for Claude; facts only, quote code and commands instead of paraphrasing).
- Write every test in the card's Tests section.

## Two cards a night
Allowed only when the queue below marks two cards `ready` and their Files lists share no file (bookkeeping files excepted). Run them in queue order:
1. Run the first card completely: tests, commit, push, and an update to `SESSION_HANDOFF.md`.
2. Start the second card in a fresh session from `Session start`, and give it its own commit and push.
3. If the first card stops (stop-and-ask) or its local tests fail, do not start the second card.

## Relay mode (user decision 2026-10-01)
On a relay night agy runs cards one after another, and Claude reviews each one before the next starts:
1. agy runs one card completely (tests, commit, push, `SESSION_HANDOFF.md` note), reports the hash, and stops.
2. The user tells Claude "pull and review". Claude either sets the card to `accepted` or writes its fix card `card-NNb-*.md` with `Status: ready`, and pushes.
3. The user starts a fresh agy session. agy pulls first, then takes the first `ready` card in the queue below (a fix card always comes before new cards).

In relay mode two cards may touch the same file, because each starts from reviewed code. Never start a card while the previous one is `done` but not yet reviewed.

## Pull before you start, and if your push is rejected
- Start every session with `git pull origin refactor/hear-say-it`. Claude pushes docs (cards, releases, reviews) between your sessions.
- If `git push` is rejected because Claude pushed meanwhile, run `git pull --rebase origin refactor/hear-say-it` and push again. Your commit is not pushed yet, so rebasing it rewrites nothing shared. If the rebase has a conflict in a file outside your card, stop and ask.

## Asset cards (images)
An asset card generates images with agy's built-in image model (Nano Banana Pro). It runs in its own agy session, alongside the code relay. The card says which files to read.
- It writes only into its batch folder outside the repo, `C:\Users\riva.zn\Documents\playIT-image-batches\<batch>\`. It never writes into `app/`, never commits, and never pushes.
- It works in rounds until the user has picked an image for every item; the card describes the loop. Claude sets the card's status from the batch folder (`picks.json`).

## Image gate
Images go into `app/src/main/assets/images/` only when a card says so, and only files listed in a `docs/image-release/<date>/manifest.json`. Those are images the user approved on a review page, after Claude's cutout and checks. Copy them unchanged and check them against the manifest's SHA-256, as with audio. Anything not in an image release manifest is not approved.

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

## Current queue (2026-10-01, relay night)
The `Status:` line in each card is the source of truth; this table is a snapshot. Cards 00-05 are accepted (CI green on draft PR #2; see `docs/evidence-log.md`).

Code relay (agy session A), in this order:

| Order | Card | Status | Note |
|---|---|---|---|
| 1 | 06 | ready | Say It feedback text and debug "Heard:" overlay |
| 2 | 07 | ready | Idle re-prompt and next-step cues. Revised 2026-10-01; its pre-step copies 3 UI clips from `docs/audio-release/2026-10-01/` |
| 3 | 07b | ready (after 07 is accepted) | Avatar-only onboarding, parent rename, voiced map pop-up; copies the other 3 UI clips |
| 4 | 11 | ready (after 07 is accepted) | Stars and hearts use real results |
| 5 | 12 | ready (after 11 is accepted) | Find It distractors, gentle correction colour, no emoji test |
| 6 | 09 | being written | Approved /m/ into the app; waits for the user's A/B pick |
| 7 | 10 | being written | Screenshot tests; waits for Claude's local spike |
| 8 | 03b | being written | Say It corrections from fragments; waits for the user's OK on the compositions |
| 9 | 13 | being written | Approved batch-1 pictures into the app; waits for card 08's picks |

Take the first card in this order whose status is `ready` and whose "after" card is `accepted`. Claude moves 09, 10 and 03b up as soon as they are ready.

Images (agy session B, in parallel):

| Card | Status | Note |
|---|---|---|
| 08 | ready | Asset card: 29 Find It and key-word pictures, in rounds until the user picks one per item. Writes only to `Documents\playIT-image-batches\2026-10-01-findit-batch-01\` |
