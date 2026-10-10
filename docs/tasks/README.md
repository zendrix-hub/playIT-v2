# Task cards for the Hear It / Say It refactor

Each card is one agent session. Work top to bottom. Do not start a card until the previous one is accepted and the user gives the go signal. Claude writes and reviews the cards; agy implements them (roles: `AGY_RUNBOOK.md`).

Every card has a `Status:` line under its title:

| Status | Meaning |
|---|---|
| draft | Written, not yet critiqued. Do not run. |
| critiqued | Claude has critiqued it; fixes pending. Do not run. |
| ready | Critique addressed; agy may run it. |
| done | agy has committed and pushed it. Waiting for Claude's review and CI. |
| accepted | Claude reviewed it with no open findings and CI passed. CI and phone results are in `docs/evidence-log.md`. |

Fix cards: when a review finds problems, Claude writes a fix card named after the card with a letter, e.g. `card-03b-tutor-policy-fixes.md`. It runs like any other card. The original card becomes `accepted` when its fix card is accepted.

| Card | Goal | Requirement |
|---|---|---|
| 00 | Agent rules, precedence, housekeeping | — |
| 01 | Speech judge returns error types; stop accepting letter names and added vowels | NFR-ASR-01 |
| 02 | Scope the Vosk grammar to each letter's foils; carry the error type into Say It state | NFR-ASR-01, FR-03 |
| 03 | Tutor policy: prompt ladder, no hearts in Say It, corrective audio | FR-03 |
| 04 | Hear It modeling sequence (I do) | FR-02 |
| 05 | Approved Kokoro key words and tutor carriers into the app | NFR-AUD-01, FR-02 |
| 06 | Say It feedback text follows the error type; debug transcript overlay | FR-03, NFR-ASR-01 |
| 07 | Idle re-prompt (10 s) and spoken next-step cues | NFR-IND-01 |
| 07b | Avatar-only onboarding, parent rename, voiced map pop-up (after 07 is accepted) | NFR-IND-01 |
| 08 | Asset card (agy image session): 29 Find It and key-word pictures, regenerated in rounds until the user picks one per item | FR-05 |
| 09 | The approved held /m/ replaces the Edge-TTS clip | NFR-AUD-01, FR-02 |
| 10 | Screenshot tests (Roborazzi) so Claude can see every changed screen; CI artifact | — |
| 11 | Stars and hearts: real inputs to the star math, 5-heart display, Blend It at 0 hearts | FR-04, FR-06, FR-13 |
| 12 | Policy fixes: Find It distractors, gentle correction colour (no red, no buzzer), no emoji in the PDF | FR-05, FR-12 |
| 13 | Approved batch-1 pictures into the app; "Up" gets its own picture | FR-05 |
| 03b | Say It corrections built from fragments around the sound (spec §2.3) | FR-03 |

How to run a card with the agent: agy follows `docs/tasks/AGY_RUNBOOK.md`. Start the session with:

> Follow docs/tasks/AGY_RUNBOOK.md.

Definition of done for every card (agy's part): listed tests exist; the full unit test suite passes locally, or the commit body says "Unit tests not run locally; verify in CI" and CI passes; the card's item in `docs/engineering-package/13_MASTER_TASKS.md` is ticked; the card says `Status: done`; `docs/evidence-log.md` has its row; no files changed outside the card's list and those bookkeeping files; one commit, pushed to `refactor/hear-say-it`.
