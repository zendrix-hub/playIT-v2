# Evidence log: Hear It / Say It refactor

One row per commit. agy adds a row with each card (CI pending, phone test pending); the reviewer fills in the commit hash, CI run, and phone test.

| Card | Adviser finding | Requirement | Commit | CI run | Phone test |
|---|---|---|---|---|---|
| 00 | The refactor spec supersedes the engineering package for Hear It and Say It (adviser directive) | — | f16ec21 | pending | pending |
| 01 | Say It accepted letter names and added vowels ("ma" in Round 1); the judge must record the error type (spec §1.4, §5.5) | NFR-ASR-01 | 930e9ed | pending | pending |
| 02 | The grammar must hold each letter's foils so Vosk reports the actual error (spec §3.3) | NFR-ASR-01, FR-03 | cf235dc | pending | pending |
| 02 (fix) | False rejects must stay at or below 15% of teacher-correct attempts (spec §5.5); stopping on a wrong partial could reject a correct word | NFR-ASR-01 | b92e909 | pending | pending |
| 00 (fix) | Card 00 dropped rules from CLAUDE.md (review finding) | — | 5b8fd75 | pending | pending |
| 01 (fix) | Vosk cannot confirm a pure sound, so held letter names could pass the 400 ms rule (spec §3.3, risk R2; vosk-foil-spike.md) | NFR-ASR-01 | a43237d | pending | pending |
