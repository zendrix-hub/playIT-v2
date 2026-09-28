# Task cards for the Hear It / Say It refactor

Each card is one agent session. Work top to bottom. Do not start a card until the previous one is committed and reviewed.

| Card | Goal | Requirement |
|---|---|---|
| 00 | Agent rules, precedence, housekeeping | — |
| 01 | Speech judge returns error types; stop accepting letter names and added vowels | NFR-ASR-01 |
| 02 | Scope the Vosk grammar to each letter's foils; carry the error type into Say It state | NFR-ASR-01, FR-03 |
| 03 | Tutor policy: prompt ladder, no hearts in Say It, corrective audio | FR-03 |
| 04 | Hear It modeling sequence (I do) | FR-02 |

How to run a card with the agent:

> Implement docs/tasks/card-NN-*.md exactly. Change only the files it lists. Make every test in its "Tests" section pass, run ./gradlew testDebugUnitTest, then commit with the message in "Commit". Do not push.

Definition of done for every card: listed tests exist and pass, full unit test suite passes, no files changed outside the card's list, one commit.
