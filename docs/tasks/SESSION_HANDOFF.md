# Session Handoff: Claude & agy Bridge

> This document is the asynchronous communication bridge between **Claude** (architect, card author, reviewer) and **agy** (implementer) on branch `refactor/hear-say-it`.
> Update this document at the conclusion of each session so the other agent has complete, structured context upon pulling.

---

## Latest Session Status (2026-09-30)

| Field | Value |
|---|---|
| **Author** | agy |
| **Branch** | `refactor/hear-say-it` |
| **Head Commit** | `85a6ca2` |
| **Subject** | `feat(audio): ship approved Kokoro key words and tutor carriers (NFR-AUD-01, FR-02)` |
| **Active Card** | Card 05 (`Status: done`) |
| **Review Status** | Waiting for Claude's review & CI run on PR #2 |

---

## What Was Completed in This Session (Card 05)

1. **Pre-Step Release Check**:
   - Validated all 33 WAV audio files against `docs/audio-release/2026-09-30/manifest.json`.
   - Verified 100% SHA-256 match for 26 key words and 7 tutor carriers.
2. **Audio Assets Shipped**:
   - `app/src/main/assets/audio/keywords/kw_<word>.wav` (26 files copied from release).
   - `app/src/main/assets/audio/vo/tutor/<id>.wav` (7 files copied from release).
3. **App Code Updated**:
   - `AudioResolver.kt`: added `getKeyWordPath(word: String): String`, mapped `audio/keywords/` to `WORD` and `audio/vo/tutor/` to `VO` in `getDevPlaceholderForAsset`.
   - `HearItViewModel.kt`: switched example word resolution in `buildSequence` to `getKeyWordPath`.
   - `SayItViewModel.kt`: switched model playback in `playWordAudio()` and `evaluateSpeech()` to `getKeyWordPath`.
4. **Unit Tests Added & Passing**:
   - `AudioResolverTest.kt`: `getKeyWordPath_returnsWavInKeywordsFolder`, `devPlaceholder_mapsKeywordsAndTutor`.
   - `AudioCompletenessCheckTest.kt`: `keywordClips_existForAll26SeededWords`, `approvedTutorCarriers_exist`.
   - `HearItViewModelTest.kt`: updated stubs and verify assertions for `getKeyWordPath`.
   - `SayItViewModelTest.kt`: updated stubs and verify assertions for `getKeyWordPath`.
5. **Bookkeeping**:
   - Ticked Card 05 in `13_MASTER_TASKS.md`.
   - Changed Card 05 header to `Status: done`.
   - Added pending row to `docs/evidence-log.md`.
   - Pushed commit `85a6ca2` to `origin refactor/hear-say-it`.

---

## Notes & Open Items for Claude

1. **Card 05 Technical Review**:
   - Please review commit `85a6ca2`.
   - If accepted, update Card 05 to `Status: accepted` and fill in the hash/CI run in `docs/evidence-log.md`.
2. **Card 03b (Tutor Script Alignment)**:
   - `docs/proposals/2026-09-30-tutor-script.md` details the proposed fragment breakdown and corrective compositions.
   - Once user approves, Claude will prepare `card-03b` for agy to execute.
3. **Card 06 (Audio Manifest)**:
   - Next queued proposal in `docs/proposals/2026-09-29-next-cards-and-story-hook.md`.
   - Ready to be drafted when user gives the go-signal.
4. **Teacher Audit**:
   - The 33 shipped clips currently have `teacherAudit: "pending"` in the manifest; audit is a gate before merging to `main`.

---

## APK Build & On-Device Testing Matrix

- **Artifact**: `./playit-debug.apk` (assembled with full 170 unit test pass and 33 Kokoro audio assets).
- **Installed and Tested by User**: Tonight (pending feedback below).

### Manual Verification Checklist for Phone:
| Area | Verification Target | Expected Behavior | Observed Result |
|---|---|---|---|
| **Hear It (FR-02)** | Modeling Sequence | Attention ("Look at Lily") -> Card flip reveal -> Pure sound 3x (`/m/`) -> Key word ("Mouse") -> Pure sound -> Hand-off ("Now your turn") | [ ] Pass / [ ] Note |
| **Hear It (FR-02)** | Replay Ear Button | Tap ear button on bottom right | Repeats full sequence cleanly without crashing | [ ] Pass / [ ] Note |
| **Say It (FR-03)** | Target Word Tap | Tap letter card speaker icon | Plays Kokoro key word clip (`kw_mouse.wav`), debounces rapid taps | [ ] Pass / [ ] Note |
| **Say It (NFR-ASR-01)** | Letter Name Foil | Utter letter name (e.g. "em" / `/ɛm/`) | Flagged as `LETTER_NAME`, zero hearts deducted, prompts with corrective feedback | [ ] Pass / [ ] Note |
| **Say It (NFR-ASR-01)** | Added Vowel Foil | Utter added vowel (e.g. "muh" / `/mə/`) | Flagged as `ADDED_VOWEL`, zero hearts deducted, prompts with corrective feedback | [ ] Pass / [ ] Note |
| **Say It (FR-03)** | 3-Attempt Ladder | Miss 3 times in a row | Level 1: "Listen again" + audio; Level 2: "Watch my lips"; Level 3: "Let's say it together" + unlocks Continue; 0 hearts deducted | [ ] Pass / [ ] Note |
| **Say It (FR-03)** | Correct Utterance | Utter target word ("mouse") | Accepted cleanly, moves to success / next letter | [ ] Pass / [ ] Note |
| **Offline (NFR-OFF)** | Airplane Mode | Enable device Airplane mode | Entire app operates 100% offline, zero network crashes | [ ] Pass / [ ] Note |
| **Zero-Emoji** | Child & Parent UI | Scan UI text across all screens | 100% compliant with zero-emoji policy | [ ] Pass / [ ] Note |

---

## Directives for Next Session (Reserved for Claude)
_Claude, please append your review notes, feedback, and next steps below before agy begins the next session._

