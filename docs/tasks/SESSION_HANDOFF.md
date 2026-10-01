# Session Handoff: Claude & agy Bridge

> This document is the asynchronous communication bridge between **Claude** (architect, card author, reviewer) and **agy** (implementer) on branch `refactor/hear-say-it`.
> Update this document at the conclusion of each session so the other agent has complete, structured context upon pulling.

---

## Latest Session Status (2026-10-01)

| Field | Value |
|---|---|
| **Author** | agy |
| **Branch** | `refactor/hear-say-it` |
| **Head Commit** | `pending` |
| **Subject** | `feat(sayit): feedback text follows the error type; debug transcript overlay (FR-03, NFR-ASR-01)` |
| **Active Card** | Card 06 (`Status: done`) |
| **Review Status** | Waiting for Claude's review & CI run on PR #2 |

---

## What Was Completed in This Session (Card 06)

1. **SayItFeedbackCopy Domain Manager**:
   - Created pure Kotlin `SayItFeedbackCopy.kt` with zero `android.*` imports.
   - Maps `TutorAction`, `SpeechErrorType`, `wordMode`, and `word` to pedagogical feedback strings for mascot bubble and banner.
   - Implements specific corrective copy for letter names, added vowels, substitutions, and no-speech; zero emojis and no "try again" phrases.
   - Added unit test suite `SayItFeedbackCopyTest.kt` covering all prompt ladder levels and asserting zero occurrences of "try again".

2. **SayItViewModel Diagnostics & Telemetry**:
   - Added `HeardAttempt` data class and `lastHeard: StateFlow<HeardAttempt?>`.
   - Records raw transcript, `SpeechErrorType`, correctness, and attempt count in `evaluateSpeech()`.
   - Resets `lastHeard` to `null` on `loadPhoneme()`.
   - Strictly avoided `android.util.Log` inside the ViewModel to protect unit test isolation.
   - Added unit tests in `SayItViewModelTest.kt`: `evaluateSpeech_setsLastHeard`, `lastHeard_tracksAttemptNumber`, and `loadPhoneme_resetsLastHeard`.

3. **SayItScreen Feedback & Diagnostics Binding**:
   - Collected `tutorAction` and `lastHeard` flows.
   - Connected `MascotSpeechHeader` message and bottom feedback banner to `SayItFeedbackCopy.forResult(...)`.
   - Added debug overlay (`if (BuildConfig.DEBUG)`):
     - On-screen diagnostic text under the banner: `Heard: "<transcript>" -> <errorType> (attempt <n>)`.
     - `LaunchedEffect(lastHeard)` sending matching `Log.d("PlayIT-SayIt", ...)` telemetry.
     - Release builds display and log nothing.

4. **Bookkeeping**:
   - Marked Card 06 as `Status: done` in `docs/tasks/card-06-sayit-feedback-diagnostics.md`.
   - Ticked Card 06 in `docs/engineering-package/13_MASTER_TASKS.md`.
   - Appended Card 06 row to `docs/evidence-log.md`.

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

### Claude, 2026-10-01 (later): tonight's queue
- **Two cards are ready, with disjoint files, so both may run tonight** (runbook "Two cards a night"): first `card-06-sayit-feedback-diagnostics.md`, then `card-07-idle-reprompt-and-next-cues.md`.
- Card 07's pre-step copies spoken UI clips only if `docs/audio-release/2026-10-01/manifest.json` exists. If it doesn't, ship the code only; the clips come later.
- Decisions added to AGENTS.md: idle re-prompt 10 s; avatar-only onboarding (onboarding is card 07b, not tonight).
- Phone test after these cards: Say It shows error-specific text and, in the debug build, a "Heard: ..." line; Hear It and Find It speak a next-step cue and pulse the button; 10 s without touching re-prompts at most 3 times.
- **After both cards:** build the debug APK (`./gradlew assembleDebug`) and report its path, so the user can run `docs/tasks/PHONE_TEST_cards-06-07.md`.
- **Card 07b** (`card-07b-avatar-onboarding-and-map-voice.md`) is `critiqued`, not ready. Do not run it tonight; Claude sets it to `ready` after card 07 is accepted.

### Claude, 2026-10-01 (night): relay night
- The user chose a **relay**: agy runs one card, the user tells Claude "pull and review", Claude accepts it or writes a fix card, and then agy starts the next card in a fresh session. See AGY_RUNBOOK.md "Relay mode" and the queue at its end. Always `git pull` first.
- **Card 06 first** (unchanged, ready).
- **Card 07 next, revised tonight:**
  - Blend It and Find It idle tests now stub the audio callback.
  - The complete screens get an `_isPlaying` flag.
  - Hear It "Next" unlocks after the first full playback.
  - Its pre-step now runs: `docs/audio-release/2026-10-01/manifest.json` exists with 18 clips. Verify all 18 hashes, copy only `ui_hearit_next`, `ui_findit_next` and `ui_complete_next`.
- More cards (09 /m/, 10 screenshot tests, 07b, 11 stars and hearts, 12 policy fixes, 03b) become `ready` one at a time; take them in the queue table's order.
- **Card 08 (images)** is an asset card for a separate agy session; start it only when it says `ready`.
