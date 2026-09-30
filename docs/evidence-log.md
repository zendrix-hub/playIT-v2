# Evidence log: Hear It / Say It refactor

One row per code commit. agy adds a row with each card (CI pending, phone test pending); the reviewer fills in the commit hash, CI run, and phone test.

| Card | Adviser finding | Requirement | Commit | CI run | Phone test |
|---|---|---|---|---|---|
| 00 | The refactor spec supersedes the engineering package for Hear It and Say It (adviser directive) | — | f16ec21 | pending | pending |
| 01 | Say It accepted letter names and added vowels ("ma" in Round 1); the judge must record the error type (spec §1.4, §5.5) | NFR-ASR-01 | 930e9ed | pending | pending |
| 02 | The grammar must hold each letter's foils so Vosk reports the actual error (spec §3.3) | NFR-ASR-01, FR-03 | cf235dc | pending | pending |
| 02 (fix) | False rejects must stay at or below 15% of teacher-correct attempts (spec §5.5); stopping on a wrong partial could reject a correct word | NFR-ASR-01 | b92e909 | pending | pending |
| 00 (fix) | Card 00 dropped rules from CLAUDE.md (review finding) | — | 5b8fd75 | pending | pending |
| 01 (fix) | Vosk cannot confirm a pure sound, so held letter names could pass the 400 ms rule (spec §3.3, risk R2; vosk-foil-spike.md) | NFR-ASR-01 | a43237d | pending | pending |
| — (runbook) | None: workflow docs (agy runbook, card status, evidence log, questions file) | — | f70620a | pending | pending |
| 03, 04 (critique) | None: review of cards 03 and 04 before agy runs them | FR-03, FR-02 | 45ff484 | pending | pending |
| 03 | Say It removed a heart on every miss and played a generic "try again"; a child alone never heard how to fix the error (spec §3.2 Table 7, §6.3) | FR-03 | 467d52c | pending | pending |
| 04 | Hear It played an intro and the sound once; the I-do step models the sound three times, the key word, and the sound again (spec §2.1 Table 3) | FR-02 | 68887ab | pending | pending |
| — (bookkeeping) | None: ticked cards 03 and 04 in a separate commit instead of each card's own commit | — | 8f11cb3 | pending | pending |
| 03 (fix) | Review of 467d52c: third miss shown as Correct, no canContinue gate, corrections missing INCORRECT_POP, four card tests missing; tutor clips copied although the listening checklist was blank | FR-03 | c06e8a3 | pending | pending |
| 04 (fix) | Review of 68887ab: builder hardcoded asset paths in domain, blank key word not dropped, ear-button replay included "say it with me", no pauseMillis, unlisted JSON asset | FR-02 | d79bd68 | pending | pending |
| — (runbook) | None: self-check before committing, audio gate, queue update, fix-commit hashes | — | ebfdfe1 | pending | pending |
| 03 (audio) | The 14 tutor clips from 467d52c were never approved (listening checklist blank), so they leave the app until the checklist marks them OK | FR-03 | 6e07d19 | pending | pending |
| — (tools) | None: voice candidates script and 8 notebook candidates; the review batch stays outside the repo | NFR-AUD-01 | 2882507 | pending | n/a |
| — (roles) | None: Sep 30 working agreement (Claude designs and reviews, agy implements and pushes, Claude runs the audio pipeline locally) | — | 87fda9f | pending | n/a |
| — (tools) | None: round 2 voice candidates (6 more US voices, 3 heart/bella mixes) and a shared review page with in-page answers and CSV export | NFR-AUD-01 | c333902 | pending | n/a |
| — (tools, proposal) | None: chosen voice heart 70 / bella 30 (round 2 review), held-sound lab for /m/, and a tutor-script draft built on spec §2.3 and §3.2 | NFR-AUD-01, FR-02, FR-03 | feat(tools): audio lab and tutor-script draft in the chosen voice (NFR-AUD-01) | pending | n/a |
