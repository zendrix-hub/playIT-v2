# Task cards for the Hear It / Say It refactor

Each card is one agent session. Work top to bottom. Do not start a card until the previous one is committed and reviewed.

Every card has a `Status:` line under its title:

| Status | Meaning |
|---|---|
| draft | Written, not yet critiqued. Do not run. |
| critiqued | Claude in WSL has critiqued it; fixes or acceptance pending. Do not run. |
| ready | Critique addressed; agy may run it. |
| done | agy has committed it. CI and phone results are tracked in `docs/evidence-log.md`. |

| Card | Goal | Requirement |
|---|---|---|
| 00 | Agent rules, precedence, housekeeping | — |
| 01 | Speech judge returns error types; stop accepting letter names and added vowels | NFR-ASR-01 |
| 02 | Scope the Vosk grammar to each letter's foils; carry the error type into Say It state | NFR-ASR-01, FR-03 |
| 03 | Tutor policy: prompt ladder, no hearts in Say It, corrective audio | FR-03 |
| 04 | Hear It modeling sequence (I do) | FR-02 |

How to run a card with the agent: agy follows `docs/tasks/AGY_RUNBOOK.md`. Start the session with:

> Follow docs/tasks/AGY_RUNBOOK.md.

Definition of done for every card: listed tests exist; the full unit test suite passes locally, or the commit body says "Unit tests not run locally; verify in CI" and CI passes; the card's item in `docs/engineering-package/13_MASTER_TASKS.md` is ticked; the card says `Status: done`; `docs/evidence-log.md` has its row; no files changed outside the card's list and those bookkeeping files; one commit.
