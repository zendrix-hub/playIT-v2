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

---

## APK Build & On-Device Testing Matrix

- **Artifact**: `./playit-debug.apk` (98 MB, assembled with full 170 unit test pass and 33 Kokoro audio assets).
- **Installed and Tested by User**: 2026-09-30 evening on physical Android device.

### Manual Verification Checklist & Phone Observations:
| Area | Verification Target | Expected Behavior | Observed Result |
|---|---|---|---|
| **Hear It (FR-02)** | Modeling Sequence | "Listen!" -> "This letter says..." -> /m/ x3 with 500 ms pauses -> key word "mouse" (Kokoro) -> /m/ -> "Say it with me!" (the /m/ is still the old Edge clip until the held-sound card) | **PASS** — Sequence plays cleanly with Kokoro clips. |
| **Hear It (FR-02)** | Replay Ear Button | Tap ear button on bottom right | **PASS** — Repeats sequence cleanly without overlap or crash. |
| **Say It (FR-03)** | Target Word Tap | Tap letter card speaker icon | **PASS** — Plays Kokoro key word clip (`kw_mouse.wav`), debounces rapid taps. |
| **Say It (NFR-ASR-01)** | Letter Name Foil | Utter letter name (e.g. "em" / `/ɛm/`) | **NOT EXECUTED / NO FEEDBACK** — Saying "em" did not trigger corrective feedback. See root cause analysis below. |
| **Say It (NFR-ASR-01)** | Added Vowel Foil | Utter added vowel (e.g. "muh" / `/mə/`) | **NOT EXECUTED / NO FEEDBACK** — Saying "muh" did not trigger corrective feedback. See root cause analysis below. |
| **Say It (FR-03)** | 3-Attempt Ladder | Miss 3 times in a row | **PASS** — 0 hearts lost; ladder proceeds; Continue button unlocks at 3rd miss. |
| **Say It (FR-03)** | Correct Utterance | Utter target word ("mouse") | **PASS** — Word validated, celebrations trigger, advances to next step. |
| **Offline (NFR-OFF)** | Airplane Mode | Enable device Airplane mode | **PASS** — 100% offline, zero network crashes. |
| **Zero-Emoji** | Child & Parent UI | Scan UI text across all screens | **PASS** — 100% compliant with zero-emoji policy. |

---

## Root Cause Analysis: Why Foil Cases 4 & 5 Appeared Not Executed

During tonight's test, uttering letter names ("em") and added vowels ("muh") did not produce the expected pedagogical feedback. Code inspection reveals three compounding causes:

1. **Missing Audio Assets in App**:
   - `SayItViewModel.kt` lines 360–374 assemble audio sequences using:
     - `fb_letter_name` (`audio/vo/tutor/fb_letter_name.wav`)
     - `fb_added_vowel` (`audio/vo/tutor/fb_added_vowel.wav`)
     - `fb_listen_again` (`audio/vo/tutor/fb_listen_again.wav`)
   - However, Card 05 shipped **only the 7 tutor carrier files** (`car_*.wav` + `fb_try_later.wav`). The corrective voice lines were never staged or approved in `docs/audio-release/2026-09-30/manifest.json`.
   - `AudioPlayer` intentionally ignores missing files without throwing, so the corrective voice prompt played silently.

2. **Static Fallback UI in `SayItScreen.kt`**:
   - When speech is incorrect, `SayItScreen.kt` displays hardcoded strings regardless of the error type:
     - Mascot Header (line 196): `state is SayItState.Incorrect -> "Good try! Let's listen again."`
     - Bottom Feedback Banner (line 413): `Text(text = ... else "Good try! Let's try again.")`
   - Neither the mascot bubble nor the bottom banner binds to `SayItState.Incorrect.errorType` (`LETTER_NAME`, `ADDED_VOWEL`, `SUBSTITUTION`). Thus, the child receives no visual cue that a foil was recognized.

3. **Vosk Grammar & Short Foil Acoustic Matching**:
   - In Word Mode ("mouse"), `speechValidator.grammarFor("m", "mouse")` returns `["mouse", "m", "em", "ma", "muh"]`; VoskRecognizer appends `[unk]`. (Corrected by Claude 2026-10-01: the earlier 12-word list here did not match the code.) All five words are in the Vosk model vocabulary, so a miss is acoustic, not a missing word.
   - If the acoustic model does not register the child's short utterance against "em" or "muh", it falls back to `[unk]` or empty string, which `judgeWord` classifies as `NO_SPEECH` / `OTHER_WORD` instead of `LETTER_NAME` or `ADDED_VOWEL`.

---

## Action Items for Claude Tomorrow

1. **Card 05 Technical Review**:
   - Review commit `85a6ca2` (`feat(audio): ship approved Kokoro key words and tutor carriers`).
   - Mark Card 05 as `Status: accepted` in `docs/tasks/card-05-keywords-and-carriers.md` and tick in `13_MASTER_TASKS.md`.
2. **Card 03b (Tutor Script Alignment & Foil UI Fixes)**:
   - Prepare `card-03b` incorporating the findings from tonight:
     - **Audio**: Synthesize and stage the missing corrective clips (`fb_letter_name`, `fb_added_vowel`, `fb_listen_again` or the fragment compositions in `docs/proposals/2026-09-30-tutor-script.md`).
     - **UI Bindings**: Update `SayItScreen.kt` to bind the mascot speech bubble and bottom banner directly to `errorType` so that even if audio is missing or quiet, the corrective copy ("That's the letter name. What sound does it make?" / "Clip it short — no 'uh' at the end!") is clearly visible.
     - **Diagnostic Telemetry**: Add debug logging for recognized raw Vosk transcripts during Say It attempts to aid phone testing calibration.
3. **Card 06**:
   - Audio manifest generation for remaining units / clips once Card 03b is accepted.

---

## Directives for Next Session (Reserved for Claude)
_Claude, please append your review notes, feedback, and next steps below before agy begins the next session._

### Claude review, 2026-10-01
- **Card 05 (85a6ca2): accepted.** The code matches the card. All 33 shipped clips match the manifest SHA-256 and nothing else was added under assets. The tests named in the card exist. CI is green (run 36711346769) and the local run is green. The `AudioCompletenessCheckTest` path fallback (works from the repo root or the module) is outside the card's text but harmless; kept.
- **This file (8e78bd5, 4316222):** kept as the session bridge and added to the runbook's bookkeeping files. Rule: facts only. Quote code and commands, don't paraphrase them; two statements here did not match the code and are corrected above.
- **Phone test, 2026-09-30:** recorded in the evidence log. The "em"/"muh" finding has two possible causes, and the app cannot tell them apart yet:
  1. Vosk heard the foil, but the correction clips are not shipped (fragments still pending the user's script approval), so the child heard only pop -> "mouse" -> "Your turn!", while the banner showed the generic "Good try! Let's try again."
  2. Vosk did not hear the foil (acoustic; the Sep 28 spike already saw "em" come back empty).
  The next card adds a debug-build log of what Vosk heard, so the phone test can separate 1 from 2.
- **Next:** no card is ready. Claude will propose the next card (Say It feedback text bound to the error type, and on-device transcript logging) for the user's go signal. Card 03b (fragment compositions) waits for the script approval and the /m/ decision.


