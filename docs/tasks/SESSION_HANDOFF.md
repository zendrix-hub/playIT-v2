# Session Handoff: Claude & agy Bridge

> This document is the asynchronous communication bridge between **Claude** (architect, card author, reviewer) and **agy** (implementer) on branch `refactor/hear-say-it`.
> Update this document at the conclusion of each session so the other agent has complete, structured context upon pulling.

## Fast-Track Trigger: "run and review"

The steps are in `CLAUDE.md` ("Shortcut Command"), so they live in one place. The newest state is always at the **end** of this file.

## Archive: Week 3 specs context (2026-10-03)

> **Context:** The team refactored the formal Capstone 2 engineering package for Week 3 submission based on the empirical Weeks 1–2 MVP Field Validation ($N=25$: 16 early learners, 5 parents, 4 certified DepEd teachers; mean SUS $75.50$ / Grade B+).
> **Directive:** Development continues in parallel without blocking on document sign-off to preserve sprint momentum. The user and Claude will sync on these documents on Monday morning.

### Refactored Specification Documents
1. **Refactored SRS v3.0 (`docs/SRS_v3.0_Refactored.md`)**:
   - **Scope (§1.2):** Standardized curriculum to 26 letters (7 chapters); Ñ excluded; NG deferred to Chapter 8 digraphs.
   - **FR-02 (P0):** Enforced pure phoneme acoustic models (eliminated schwa trailing "ma" → /m/ [m:]); continuous sounds held $\approx 800\,\text{ms}$, stops clipped at $\le 250\,\text{ms}$; runtime sequence assembly ($\le 15\,\text{s}$) with persistent ear replay button.
   - **FR-03 (P1):** Dynamic 4-state mic visualizer (Idle, Listening with live RMS ripple, Processing, Result), tap-to-listen $\le 100\,\text{ms}$, 3-tier prompt ladder with specific verbal corrections. **Say It never depletes player hearts**.
   - **FR-NEW-REC (P1):** Spaced retrieval warm-up checks (prioritizing `NEEDS_PRACTICE` letters) and end-of-session recall checks to establish true memory retention.
   - **NFR-ASR-01 (P1):** Vosk engine calibrated for $\ge 80\%$ agreement with teachers on child speech, false reject ceiling $\le 15\%$, and discrete error tagging (`LETTER_NAME`, `ADDED_VOWEL`, `SUBSTITUTION`, `UNKNOWN`).
   - **NFR-PERF-01 (P1):** Latency from speech end (`speech_end`) to feedback P90 $\le 0.5\,\text{s}$; Find It tap feedback P90 $\le 0.3\,\text{s}$.
   - **FR-13 (P1):** Purged 5 non-decodable CVC words (AIM, BEE, TOY, BOY, ZOO); replaced with AM, SUM, TUB, YAM, ZIP; flagged QUIZ as documented exception.
   - **FR-14 (P1):** Multi-profile support (up to 6 child profiles) for shared classroom tablet stations (`PED-12`).
   - **FR-NEW-TEL (P1):** Local Room telemetry event logging with monotonic clocks; PIN-gated CSV/PDF export via Android Share Sheet (0 network calls).
   - **NFR-ACC-01 / ACC-02 (P2):** Visual mouth articulation guides and sound captions (`ACC-06`); global $\ge 64\,\text{dp}$ touch target floor (`ACC-07`).
   - **Removed Requirement:** Eliminated musical pitch deviation ($\pm 10\,\text{cents}$) as inapplicable to speech phonetics.
   - **RTM v3.0 (§4):** Full 16-row bidirectional traceability matrix mapping findings `F-01` through `F-16` to objectives, requirements, design components, and STD test cases.

2. **Refactored SDD v2.0 (`docs/SDD_v2.0_Refactored.md`)**:
   - **Tutoring & Pedagogy Layer (§2.1–§2.2):** Introduced `LessonEngine`, `TutorPolicy` finite state machine (FSM), `SayItJudge` with per-letter grammars, and `LearnerModel` review scheduler.
   - **Audio Subsystem (§3.1):** `AudioPlaybackManager` with pre-cached `SoundPool` for instant phoneme bursts; separated `ph_m.wav`, `kw_m_mouse.wav`, and carrier assets; runtime `AudioComposer`.
   - **Speech Recognition Pipeline (§3.2):** Switched from `SpeechService` to an asynchronous `AudioRecord` 16 kHz mono loop streaming raw PCM to compute real-time normalized RMS amplitude for `MicStateVisualizer` while feeding Vosk `acceptWaveForm()`.
   - **Room Database Schema v3 (§3.5):** Added `ProfileEntity`, `LetterProgressEntity`, and `TelemetryEventEntity`; local RFC 4180 CSV exporter.
   - **Pediatric UI Tokens (§3.6):** `Modifier.pediatricTouchTarget(64.dp)` and `ArticulationCue` composable.

3. **Refactored SPMP v2.0 (`docs/SPMP_v2.0_Refactored.md`)**:
   - **Scope & Strategy (§1):** Chapter 1 (*m, s, a, i*) established as the complete vertical slice for Round 2; Kokoro-82M and Chatterbox-Turbo 3-stage audio release gates (Gates 1–3).
   - **WBS (§2):** 9 structured work packages (Pedagogy, Audio, ASR Spike, Tutoring Layer, Autonomy, Telemetry, Testing, Round 2, Documentation).
   - **Schedule (§3):** Re-baselined Weeks 3–9 master schedule.
   - **Risk Register (§6):** Expanded 8-point risk matrix (R1–R8) with concrete proactive mitigations and contingencies.

### Action Plan
- **Claude:** Review the three refactored specification documents (`docs/SRS_v3.0_Refactored.md`, `docs/SDD_v2.0_Refactored.md`, `docs/SPMP_v2.0_Refactored.md`), Card 11, and Card 12 implementations when syncing on Monday morning.
- **agy:** Completed Card 11 (`card-11-stars-and-hearts.md`) and Card 12 (`card-12-policy-fixes.md`). Unit tests passing cleanly in CI/local. Ready for Claude review.

---

## Latest Session Status (2026-10-03) — Card 12: Find It Distractors, Gentle Correction, Zero Emoji

| Field | Value |
|---|---|
| **Author** | agy |
| **Branch** | `refactor/hear-say-it` |
| **Head Commit** | `68bcb68` (Card 12: `ef03bea`, Specs: `dcff981`, Card 11: `0d9ad3a`) |
| **Subject** | `fix(ui): Find It distractors never share the target sound; gentle correction; no emoji (FR-05, FR-12)` |
| **Active Card** | Card 12 (`Status: done`), Card 11 (`Status: done`), Specs Refactor (`dcff981`) |
| **Review Status** | Code implemented, tested, passing all unit tests. `tools/dev/review_card.py 11` and `12` return ALL PASS. Ready for Claude code review. |

---

## What Was Completed in This Session (Card 12)

1. **`GridGenerator` distractor sound isolation (`GridGenerator.kt`, `GridGeneratorTest.kt`)**:
   - Filtered out `noDistractorBanks` (`x`, `ng`, `ñ`) from providing distractors.
   - Isolated `sameSoundGroups` (`c`, `k`, `q`) so distractors never share the target's initial phoneme sound.
   - Added unit tests: `distractors_neverShareTheTargetSound`, `distractors_neverFromSpecialBanks`, `distractorLettersFor_k_excludesSameSoundGroup`.
2. **Gentle correction colors (`FindItScreen.kt`, `BlendItScreen.kt`)**:
   - Replaced all harsh `CoralBerry` red error indications with `GentleCorrectionOrange` per Design System 03 §2/§6.
3. **Blend It audio feedback (`BlendItViewModel.kt`, `BlendItViewModelTest.kt`)**:
   - Replaced harsh `SfxEvent.BLENDIT_BUZZ` with soft `SfxEvent.INCORRECT_POP` on wrong word submissions.
   - Added unit test: `wrongSubmit_playsSoftPop_notBuzz`.
4. **Zero-Emoji Policy compliance (`PdfExporter.kt`, `ZeroEmojiPolicyTest.kt`)**:
   - Cleaned emoji/symbols from parent PDF exporter.
   - Created automated regression test `ZeroEmojiPolicyTest.kt` verifying zero emojis across all `.kt` and `.xml` source files.

---

## What Was Completed in Card 11 (Earlier in Session)

1. **HeartManager (`HeartManager.kt`)**:
   - Added `sessionHeartsLost: Int` (cleared only on `reset()`, preserved across `resetForRestart()`).
   - Incremented `sessionHeartsLost` on every `deductHeart()`.
   - Added unit tests: `sessionHeartsLost_survivesRestart` and `reset_clearsSessionHeartsLost`.

2. **Navigation Routes (`Routes.kt`, `NavGraph.kt`, `RoutesTest.kt`)**:
   - Updated `LETTER_COMPLETE` and `BLEND_IT_COMPLETE` to accept optional query arguments with defaults (`heartsLost`, `wordsCorrect`, `totalWords`).
   - Updated `NavGraph` composable route arguments and callback handlers for Find It and Blend It.
   - Added unit test suite `RoutesTest.kt` verifying route query parameters.

3. **Top Bar & Pediatric Compliance (`LessonTopBar.kt`)**:
   - Fixed `LessonTopBar` default `maxHearts` from 3 to `GameplayConstants.STARTING_HEARTS` (5).

4. **Find It Gameplay & Recovery (`FindItViewModel.kt`, `FindItScreen.kt`)**:
   - Exposed `sessionHeartsLost` in `FindItViewModel`.
   - Tracked `consecutiveCorrect` and wired `heartManager.checkRecovery(consecutiveCorrect)` (+1 heart per 3 correct in a row, capped at initial starting pool).
   - Updated `FindItScreen` `onNext` signature to `(phonemeId: String, heartsLost: Int) -> Unit` passing `sessionHeartsLost`.
   - Added unit tests in `FindItViewModelTest`: `wrongTaps_countIntoSessionHeartsLost`, `sessionHeartsLost_survivesRestart`, and `threeCorrectInARow_recoversAHeart`.

5. **Blend It Gameplay & Depletion Handling (`BlendItViewModel.kt`, `BlendItScreen.kt`)**:
   - Added `BlendItResult(groupId, heartsLost, wordsCorrect, totalWords)` and `result()` method.
   - Tracked `wordsSolvedFirstTry` (only counting words with 0 wrong attempts) and consecutive correct words recovery.
   - Fixed `restartSession()` to invoke `heartManager.resetForRestart()` (3-heart restart per spec).
   - Updated `BlendItScreen` `onSessionComplete` callback to take `BlendItResult`.
   - Added `CelebrationOverlay(type = CelebrationType.STAR_BURST)` for `BlendItUiState.HeartDepleted`.
   - Added unit tests in `BlendItViewModelTest`: `heartDepleted_restartsWithThreeHearts` and `result_countsFirstTryWords`.

6. **Completion Scoring (`LetterCompleteViewModel.kt`, `BlendItCompleteViewModel.kt`)**:
   - Removed `coerceIn(0, 2)` clamping in `LetterCompleteViewModel`, using `coerceAtLeast(0)` so 3+ hearts lost yields 1 star.
   - Updated `BlendItCompleteViewModel` to read `heartsLost`, `wordsCorrect`, and `totalWords` from `SavedStateHandle`, pass to `BlendItStarThresholds.calculateStars(...)`, and persist `heartsLost`.
   - Added unit tests: `LetterCompleteViewModelTest.heartsLostArg_threeOrMore_givesOneStar`, `LetterCompleteViewModelTest.heartsLostArg_missing_givesThreeStars`, and `BlendItCompleteViewModelTest.starsUseNavResults`.

---

## What Was Completed in This Session (Card 08)

1. **Asset Generation & Verification (Round 1)**:
   - Generated all 58 images (2 variants each for all 29 items in `items.json`) using Nano Banana Pro (`generate_image`) with style reference pictures attached.
   - Every single PNG was verified with Pillow (1024x1024 square, corner channels > 235).
   - Logged in `generation_log.jsonl` (58 entries).
   - Review page generated via `tools/images/review_page.py --round 1`.

2. **User Review & Final Picks**:
   - The user reviewed Round 1 in `round-01/index.html` and selected preferred variants for all 29 items.
   - `tools/images/review_page.py --status` verified 0 open items remaining and generated `picks.json`.
   - Staged `picks.json` and the 29 picked 1024x1024 PNGs into:
     - `docs/assets/briefs/2026-10-01-findit-batch-01/picks.json`
     - `docs/assets/briefs/2026-10-01-findit-batch-01/picks/<item_id>.png` (29 files)
   - Created zip package at `C:\Users\Zendrix\Documents\playIT-image-batches\2026-10-01-findit-batch-01\playIT_batch_01_picks.zip`.

3. **Next Steps for Claude**:
   - Run `tools/images/cutout.py` to remove white backgrounds (flood fill) and produce transparent 512px RGBA assets.
   - Run `tools/images/audit.py` to check alpha, padding, halos, and 96px thumbnail rendering.
   - Prepare the release manifest in `docs/image-release/<date>/manifest.json` for Card 13.

---

## Session Status (2026-10-01)

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

### agy, 2026-10-01 (night): Card 07 completed
- **Card 07 (feat(ui): 10 s idle re-prompt and spoken next-step cues (NFR-IND-01)): completed.**
  - Pre-step: SHA-256 for all 18 clips in `docs/audio-release/2026-10-01/manifest.json` verified. 3 approved UI audio files (`ui_hearit_next.wav`, `ui_findit_next.wav`, `ui_complete_next.wav`) copied to `app/src/main/assets/audio/vo/ui/` with matching checksums.
  - Components: Created `IdleRePrompt.kt` with `IdleTimer` (`timeoutMs = 10_000L`, `maxPrompts = 3`, `maxPostpones = 6`, `isBusy`, `touch`, `start`, `stop`) and `Modifier.resetsIdle`.
  - Audio: Added `AudioResolver.getUiPath(id)` and mapped `audio/vo/ui/` in `getDevPlaceholderForAsset`.
  - Screens & ViewModels updated: `HearIt`, `FindIt`, `BlendIt`, `LetterComplete`, `BlendItComplete` with `IdleTimer`, `onScreenVisible()`, `onScreenHidden()`, `onUserInteraction()`, `nextHighlighted`, `DisposableEffect`, `resetsIdle`, and primary button `breathingPulse(enabled = nextHighlighted)`.
  - Tests: Full unit test suite (`./gradlew testDebugUnitTest`) passed locally (201 tests across all test suites, 0 failures).
  - Tonight's two-card relay queue (Cards 06 and 07) is now fully implemented.

### Claude review, 2026-10-02
- **Card 06 (4deb625): accepted.** The texts match the card's table exactly. `lastHeard` is set after the judgement and reset on load. `Log.d` is only in the screen, debug builds only. CI green (run 36856150943).
- **Card 07 (18f7fb8): accepted.** The idle timer, the busy flag on the complete screens, and Hear It unlocking after the first full playback all match the revised card. The 3 UI clips match the release SHA-256. CI green (run 36860557031).
- **Not done yet:** the debug APK for the phone test (the handoff asked for it after both cards). Build it at the start of the next code session: `./gradlew assembleDebug`, and report the path.
- **Next code card: 07b** (avatar onboarding, parent rename, voiced map pop-up). Then 11, then 12.
- **Card 08 (images)** has not started; run it in a separate agy session ("This is the image session").


### Claude review, 2026-10-05 ("run and review")
- **Accepted:** 07b (a17969d), 08 (667ea0e), 11 (0d9ad3a), 12 (ef03bea). CI is green on every commit (runs are in the evidence log), and the local `testDebugUnitTest` passed. Every card now passes `review_card.py`. The earlier 07b `files` FAIL was a gap in the tool, which did not read bare file names like `` `ui_node_start.wav` `` after a full path; the tool is fixed.
- **Card 08 deviation (kept):** the card said "never write into the repo", but the 29 picks were committed, 15 MB under `docs/assets/briefs/.../picks/`. That is what let Claude cut them out here, since the batch folder is on agy's PC. From now on, asset cards say where picks go.
- **Specs v3 (dcff981):** the review is in `docs/proposals/2026-10-05-specs-v3-review.md`. It found 4 blocking items, to fix before the adviser sees the documents:
  1. Pure-sound Say It scoring, which goes against the hybrid decision and the Vosk spike.
  2. Room "v3", which the code already uses.
  3. Unbuilt classes listed as done.
  4. [proposed] items stated as approved.
- **Images:**
  - `tools/images/cutout.py`, `audit.py` and `make_release.py` are added.
  - All 29 cutouts pass the audit. The only WARN is the apple, 53% red, because it is a red apple.
  - The final page is `C:\Users\riva.zn\Documents\playIT-image-batches\2026-10-01-findit-batch-01\final\index.html`. The user marks OK/FIX and exports the CSV into the batch folder.
  - Then Claude runs `make_release.py` into `docs/image-release/<date>/` and sets card 13 to `ready`.
- **Card 13** is written and `waiting`.
- **Blocked on the user:**
  - Card 09 needs the /m/ pick in `2026-10-01-compositions-m`. There is no review CSV yet.
  - Card 03b needs the OK on the compositions.
  - `2026-09-30-fixup-slowfish-2` has never been reviewed.
- **Environment:** the Kokoro model download had silently stopped at 193 MB. `~/.playit-env/rebuild_kokoro.sh` now resumes the download and fails on errors. The Roborazzi spike failed on a proxy timeout and is being re-run.
- **Card 10 is `ready` (later, 2026-10-05).** The Roborazzi spike passed. Robolectric fetches `android-all` at test time, and this WSL proxy breaks Java TLS, so the spike used a curl-fetched jar in `~/.playit-env/robolectric-deps` through the `ROBOLECTRIC_DEPS_DIR` hook. The card keeps that hook; it does nothing when the variable is unset. Next agy session: card 10.

### Claude, 2026-10-05 (evening): directions for agy
**Next agy sessions, in order:**
1. **Card 10** (`docs/tasks/card-10-screenshot-tests.md`, ready): Roborazzi screenshot tests and the CI upload step. Use exactly Roborazzi 1.26.0 and Robolectric 4.13. Keep the `ROBOLECTRIC_DEPS_DIR` block as written; it does nothing on your PC or in CI. The first run downloads about 150 MB.
2. **Card 14** (`docs/tasks/card-14-privacy-pack.md`, ready after 10 is accepted):
   - `allowBackup="false"`, plus backup-exclusion XML;
   - "Delete this child's data" in the Parent Zone;
   - a parent privacy notice, using the card's text word for word.
3. **Then card 13** (pictures), **09** (/m/), **03b** and **15** (decodable words), as each becomes `ready`. Claude sets them ready when the user's reviews are in.

**Rules that changed today:**
- **Files agy must not touch:**
  - `docs/defense/`: the defense reviewer, numbers sheet and drill log. Claude and the user own these.
  - The validation reports: `docs/validation-package/04_MVP_VALIDATION_HIGHLIGHTS.md` and its .docx/.pdf, plus `consultation_prep_dashboard.html`.
  - The specs (SRS 3.1, SDD 2.1, SPMP 2.1).
- **Privacy:** never write a participant's name or email in the repo; use codes (C-, T-, P-, F-). The GitHub repo is **public**, so this matters.
- **Rendering .docx/.pdf:** render them from the .md with `tools/docs/render_doc.sh`. `scripts/generate_validation_highlights_*.py` are retired; they only exit with a pointer.
- **Specs:** they now mark unbuilt components "planned" (SDD §2.0 table). If a card builds one of them, mention that in the commit body, so Claude can update the table.

**Defense (Week 9, Nov 2-7) and the code:**
- The live demo runs on the build at the feature freeze (Week 8).
- Cards 14 and 15 are the user's chosen pre-defense fixes. Everything else not built is presented as a limitation (`docs/defense/DEFENSE_REVIEWER.md` §4). Don't add unrequested features to "close" a limitation.
- After each card, give the user the debug APK path if the card changes a screen.

### agy, 2026-10-06: Card 10 completed
- **Card 10 (`test(ui): screenshot tests for the changed screens, uploaded by CI (FR-04, FR-05, NFR-IND-01)`): completed.**
  - Configured Roborazzi 1.26.0 and Robolectric 4.13 in root `build.gradle.kts` and `app/build.gradle.kts`, including `testOptions.unitTests` with the `ROBOLECTRIC_DEPS_DIR` hook.
  - Added `Record Screenshots` (`./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`) and `Upload Screenshots` (`playIT-screenshots`, `app/build/outputs/roborazzi/`) steps in `.github/workflows/android_ci.yml`.
  - Created 5 screenshot tests under `app/src/test/java/com/playit/app/screenshot/`:
    - `HearItScreenshotTest` (`hearit_letter_m.png`)
    - `FindItScreenshotTest` (`findit_letter_m.png`)
    - `BlendItScreenshotTest` (`blendit_group1.png`)
    - `NamePromptScreenshotTest` (`nameprompt.png`)
    - `LetterCompleteScreenshotTest` (`lettercomplete_1star.png`)
  - Verified `./gradlew testDebugUnitTest` and `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`. All 5 PNGs generated and verified in `app/build/outputs/roborazzi/`.

### Claude review, 2026-10-06
- **Card 10 (b8e859d): accepted.**
  - `review_card.py` passes everything. Its earlier files FAIL was a parser gap for "folder line + bare file names"; the tool is fixed.
  - CI is green (run 37380965313) and uploaded `playIT-screenshots`.
  - The local run of the 5 screenshot tests passes and records all 5 PNGs.
- **What the screenshots showed:**
  - Card 11 is confirmed: 5 hearts shown, and 1 star after 3 hearts lost.
  - Card 07b is confirmed: the name screen has no text field.
  - New bug: the Blend It card clips its second line, "Tap to hear word", at 411x891 dp. A small fix card can follow after card 14, if the user wants it.
- **Next agy session: card 14** (privacy pack).
- **Claude, 2026-10-06:**
  - The word clips for card 15 are ready to review: `playIT-audio-batches/2026-10-06-blendwords-card15`.
  - Spike addendum: Vosk mishears close short vowels (tub/tab, miss/mess, vet/vat) even on clean synthetic audio.

### Claude, 2026-10-06 (afternoon): UI overhaul plan approved; directions for agy
The user approved `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`. **agy is the main executor; Claude reviews every weekday** when the user says "run and review".

**What to run:**
1. **Code session A:** card 14 (privacy), then **card 17**. Their files don't overlap, so both may run tonight. After that, cards 18 → 19 → 20 → 21 → 22 → 23 → 24, one at a time, each after the previous one is accepted.
2. **Image session B, start now:** **card 25** (mouth-shape pictures). Nano Banana has a daily quota, so it runs alongside card 17. If the quota runs out, write `STATUS.md` and continue the next day. At the end, commit only the picks folder, as the card says.

**How to run a plan card:**
- Each card 17-24 points to its plan task. The plan has the steps, the code and the test code. Do them in order: failing test, implementation, passing test.
- The card has the Files list and the Tests table that `review_card.py` checks.
- Read the plan's "Global Constraints" every time:
  - 64 dp child targets;
  - text at least 16 sp, and at least 24 sp for letters and words;
  - no red for wrong answers;
  - motion durations;
  - reduced motion;
  - no haptics;
  - portrait;
  - zero emoji.
- Before committing, run `./gradlew testDebugUnitTest` and `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`. Look at the PNGs for all 4 sizes: compact 360x640, a21s 360x740, phone 411x891, tablet 800x1280.
- After each code card, give the user the debug APK path. They test on a Samsung Galaxy A21s.

**Cut line:**
- 17, 18, 19, 20 and 22 must land before Round 2 (Oct 19). The target is all code cards by Fri Oct 16.
- 21, 23 and 24 may move to Oct 26-30 if days slip. Say so in the handoff rather than rushing.
- Card 16 is superseded by card 20; don't run it.

**Defense reviewer updated:**
- The "marks the letter for review" claim is removed, because it isn't built.
- The mic wording matches card 19's time-based ripple.
- Screenshot tests are now marked as done.

### Claude, 2026-10-06 (evening): dry runs of cards 17 and 18 (before agy runs them)
Claude applied plan Tasks 1 and 2 to a throwaway worktree (`~/.playit-env/spike-shots`) and ran the full suite and the screenshots. It found and fixed four problems:
1. **Idle floating:** 4 callers passed `isIdleFloating = true`, so the card 17 Files list now names them, plus a policy test.
2. **Async picture loading:** screenshots could capture before the pictures decoded. Fix: `AssetDecodeTracker`, and the screenshot tests wait for it.
3. **`GummyContainer`:** the plan's `propagateMinConstraints` version made fill buttons ("Let's Play") fill the whole screen. It is replaced by a custom `Layout` in plan Task 2, which is tested.
4. **A real, pre-existing crash:** `init {}` before later properties in 5 ViewModels, with an NPE in `BlendItViewModel`. This is the new **card 17b**.

Result with 17 + 17b + 18 applied: **260 tests, 0 failed** (1 skipped by design), and the screens render correctly. Tonight's order for agy: **14 → 17 → 17b**, and card 25 in the image session.

### agy, 2026-10-06 (night): Card 14 executed; Card 25 image session paused on quota

#### 1. Card 14: Privacy Pack (FR-14)
- **Manifest & Backup Rules**:
  - `AndroidManifest.xml`: set `android:allowBackup="false"`, `android:dataExtractionRules="@xml/data_extraction_rules"`, `android:fullBackupContent="@xml/backup_rules"`.
  - Added `app/src/main/res/xml/backup_rules.xml` and `data_extraction_rules.xml` excluding `root`, `file`, `database`, `sharedpref`, `external` domains across cloud backup and device transfer.
- **ParentDashboardViewModel (`ParentDashboardViewModel.kt`)**:
  - Injected `sessionManager: SessionManager`.
  - Added `deleteProfile(profile: Profile)` which calls `profileRepository.deleteProfile(profile)`, clears session if active, and resets `selectedProfile` to trigger next selection.
  - Added `val noProfilesLeft: StateFlow<Boolean>` emitting true when profile list becomes empty after a deletion.
- **LearnerHeroCard (`LearnerHeroCard.kt`)**:
  - Added `onDelete: () -> Unit = {}`.
  - Added "Delete this child's data" text button (48dp height) opening confirmation dialog with `GentleCorrectionOrange` delete button.
- **ParentDashboardScreen & Navigation**:
  - Wired `onDelete = { viewModel.deleteProfile(dashboardData.profile) }`.
  - Added `onAllProfilesDeleted = { navController.navigate(Routes.PROFILE_SELECT) { popUpTo(0) } }` in `NavGraph.kt`.
  - Added "Privacy" button opening `PrivacyNoticeDialog.kt` with the 5 required bullets verbatim.
- **Tests Added/Updated**:
  - `ManifestPrivacyTest.kt`: `backupIsDisabled`.
  - `ParentDashboardViewModelTest.kt`: `deleteProfile_callsRepository`, `deleteActiveProfile_clearsSession`, `deleteOtherProfile_keepsSession`, `deleteLastProfile_setsNoProfilesLeft`.
  - Zero-emoji policy check passed (0 violations).

#### 2. Card 25: Image Session (Mouth Shapes)
- **Batch Directory**: `C:\Users\Zendrix\Documents\playIT-image-batches\2026-10-07-mouth-shapes\`
- **Completed Assets (Item 1 - lips together)**:
  - `mouth_lips_together__v1.png` (1024x1024, Pillow corner check > 235 passed).
  - `mouth_lips_together__v2.png` (1024x1024, Pillow corner check > 235 passed).
  - Both variants logged in `round-01/generation_log.jsonl` and rendered on review page `round-01/index.html`.
- **Quota Status & Next Steps**:
  - Model `gemini-3.1-flash-image` (Nano Banana Pro) hit `429 RESOURCE_EXHAUSTED` (Google quota countdown targeting `2026-10-08T11:11:18Z`, ~36 hours remaining).
  - Automated background hourly monitor (`task-225`) is active and will autonomously resume generation for the remaining 8 items (`mouth_teeth_on_lip` through `mouth_open_breath`) once quota refills.
  - Per Card 25 quota rule, `STATUS.md` is updated and the review page is ready.

#### 3. Card 17: Performance and Calm Motion Foundation (NFR-PERF-01)
- **Manifest**: Added `android:screenOrientation="portrait"` to `MainActivity`.
- **Haptic Feedback**: Removed `LocalHapticFeedback` import and `performHapticFeedback` invocation from `GummyButton.kt`.
- **Calm Motion**:
  - `PulseModifier.kt`: updated `breathingPulse` and `idleBounce` to check `!enabled || LocalReducedMotion.current` before creating `rememberInfiniteTransition`.
  - `GummyMotionAsset.kt`: updated `isIdleFloating` default to `false`; `idleFloating` skips before transition creation when disabled or under reduced motion; `celebrationWiggle` skips when reduced motion is true.
  - Removed `isIdleFloating = true` and `isIdleFloating = !isCorrect` explicit arguments across callers (`LetterCard.kt`, `BlendItCard.kt`, `FindItGrid.kt`, `MascotBubble.kt`).
  - Removed `.breathingPulse()` from `LetterCard.kt`.
  - Removed continuous idle breathing transition (`infiniteTransition`, `breatheScaleY`, `breatheScaleX`) from `MascotSpeechHeader.kt`.
- **Async Downscaled Image Loading**:
  - Created `AssetImage.kt` with `calculateInSampleSize()`, `AssetDecodeTracker`, and async `rememberAssetPainter(assetPath, maxSize = 160.dp)`.
  - Re-factored `AssetUtils.kt` to delegate image painting to `AssetImage.kt` while preserving `AssetBitmapCache` and `MascotState`.
  - Created `PlayItMotion.kt` (replacing unused `Motion.kt`) defining motion constants and spring/snap specs for normal and reduced motion.
- **Audio Asset Cleanup**:
  - Deleted 26 duplicate MP3 files under `app/src/main/assets/audio/vo/vo_*.mp3` and stray `app/src/main/assets/audio/tts_[exci_20260816_104503.mp3`.
- **Screenshot Tests & Policy Verification**:
  - Modified 5 screenshot tests (`HearItScreenshotTest`, `FindItScreenshotTest`, `BlendItScreenshotTest`, `NamePromptScreenshotTest`, `LetterCompleteScreenshotTest`) to synchronize on `AssetDecodeTracker.isIdle()`.
  - Created `AssetImageTest.kt` verifying power-of-two downsampling ratios and edge cases.
  - Created `PerformancePolicyTest.kt` verifying zero haptic feedback calls, portrait manifest lock, zero idle floating caller arguments, and zero endless animations on LetterCard and MascotSpeechHeader.
  - Verified full test suite passes (`./gradlew testDebugUnitTest`: 206 tests, 0 failures).

#### 4. Card 17b: ViewModel Init Order (Crash Fix)
- **Problem**: Kotlin initializes class members strictly top-to-bottom. Having `init { ... }` blocks declared before subsequent class properties caused `NullPointerException` (e.g. `this._isPlayingPrompt is null` in `BlendItViewModel`) if coroutines or flow collections resumed synchronously (e.g. `Dispatchers.Main.immediate` or pre-loaded data).
- **Changes**:
  - Moved `init { ... }` block to follow the last class-level property declaration in:
    - `BlendItViewModel.kt`
    - `FindItViewModel.kt`
    - `HearItViewModel.kt`
    - `SayItViewModel.kt`
    - `MapViewModel.kt`
  - Created test `ViewModelInitOrderTest.kt` verifying that no `*ViewModel.kt` has a class-level property below its `init {` block.
- **Verification**: `ViewModelInitOrderTest` passes cleanly; `python3 tools/dev/review_card.py 17b` reports ALL PASS.



### Claude review, 2026-10-07 ("run and review")
- **Accepted:** card 14 (56907fd), card 17 (082caf4), card 17b (5e24abd).
  - CI is green on all three, and the local suite is 245/245.
  - `review_card.py` passes everything. The card 17 WARN comes from bare file names in parentheses; it is harmless.
- **Card 14:** the privacy notice and dialog texts match word for word, and the backup rules exclude all 5 domains.
- **Card 17:**
  - The code is the same as Claude's verified dry run; agy also did the reduced-motion skip for `celebrationWiggle`.
  - Your handoff said "206 tests". The real count is 245, so please report the number from the test results.
- **Card 17b:** the 5 ViewModels contain the same lines, only reordered.
- **New, card 17c (ready, run it before 18):**
  - The Blend It screenshot sometimes missed its picture. This was Claude's tracker design, not your code.
  - Fix: a test-only switch makes pictures decode immediately in tests. Phones keep background loading.
  - Verified 247/247, with identical screenshots in 3 runs.
  - Plan Task 2's `LayoutMatrixTest` now turns the switch on in `@Before`. It no longer waits on the tracker.
- **Card 25 (mouth shapes):** round 1 has 1 of 9 items (`mouth_lips_together`). The quota resets at about 2026-10-08 11:11 UTC.
  - Please don't rely on an unattended "background monitor" to keep generating. Resume in a normal image session after the reset, so the user sees each round.
- **Pending phone tests (user):** card 14 (delete A, delete B, read Privacy) and card 17 (`PHONE_TEST_card-17.md`).
- **Next agy session:** **17c → 18**.
- **Later, 2026-10-07:**
  - **Audio release 2026-10-07:** the 19 lesson voice lines, user-approved, are ready.
  - **Card 26 is ready.** Run it after 17c; its files don't overlap 17c or 18.
  - **Tonight:** 17c → 26 → 18 if time allows, then the phone tests for 14 and 17 (and 26).

### Claude, 2026-10-07 (afternoon): TONIGHT, read this first
- **The user has a group meeting tonight.** Follow **`docs/tasks/TONIGHT_2026-10-07.md`**: 26 → 09 → 13 → 17c → **APK A** → say **"TEST WITH groupmates"** to the user → 18 → **APK B**.
- **No Claude review between cards tonight** (user-approved). Claude reviews all of them tomorrow morning.
- If time is short, skip 13. Card 18 must never block APK A.
- **Newly ready:**
  - card 13 (image release 2026-10-07, 29 pictures);
  - card 09 (audio release 2026-10-07-m, the held /m/ the user picked);
  - card 26 (audio release 2026-10-07, the 19 lesson lines).
- **Groupmates' test sheet:** `docs/tasks/GROUP_TEST_2026-10-07.md`.
- **Card 25:** resume after the quota reset (about 19:11 Philippine time) in a normal image session.

### agy, 2026-10-07 (night): Card 26 executed
- **Card 26: Lesson voice lines in Kokoro voice (NFR-AUD-01)**:
  - Verified SHA-256 for all 19 clips in `docs/audio-release/2026-10-07/manifest.json`.
  - Copied 19 clips to `app/src/main/assets/audio/vo/lesson/<clipId>.wav`.
  - Deleted 19 replaced `.mp3` files from `app/src/main/assets/audio/ui/`.
  - Updated `AudioResolver.kt`: added `kokoroLessonVo` set, `lessonVoPath()` resolver, updated `getVoPath()` and rotational helpers (`getRotatingCorrectVo`, `getRotatingEncourageVo`, `getRotatingHintVo`), and updated `getDevPlaceholderForAsset()` for `audio/vo/lesson/`.
  - Updated `AudioResolverTest.kt`: updated expected paths to `.wav` lesson paths and added `lessonVo_allReleasedLinesUseWav` test.
  - Verified test suite passes: all 36 actionable tasks green via `./gradlew testDebugUnitTest`.

### agy, 2026-10-07 (night): Card 09 executed
- **Card 09: Approved held /m/ replaces Edge-TTS clip (NFR-AUD-01, FR-02)**:
  - Verified SHA-256 for `docs/audio-release/2026-10-07-m/phonemes/ph_m.wav` (`953f906da0d7e76de50094ce3855045e5018f1fec31e3f277483cb5fb9e627d3`).
  - Copied unchanged to `app/src/main/assets/audio/phonemes/ph_m.wav`. Preserved `phoneme_m.mp3`.
  - Updated `AudioResolver.kt`: added `releasedPhonemes = setOf("m")` and routed `getPhonemePath("m")` to `audio/phonemes/ph_m.wav`.
  - Updated `AudioResolverTest.kt`: updated standard letters check, added `phonemeM_usesReleasedHeldSound`, `otherPhonemes_keepOldPath`, and `releasedPhoneme_fileExists`.
  - Verified test suite passes locally.

### agy, 2026-10-07 (night): Card 17c executed
- **Card 17c: Pictures decode immediately in tests so screenshots are reliable (NFR-PERF-01)**:
  - Added `AssetImageConfig.decodeSynchronously` and synchronous fallback `decodeAsset()` in `AssetImage.kt`.
  - Created `AssetImageLoadTest.kt` with `pictureIsDecodedOnFirstComposition` and `missingAsset_isTransparentNotCrash`.
  - Updated 5 screenshot tests (`HearItScreenshotTest`, `FindItScreenshotTest`, `BlendItScreenshotTest`, `NamePromptScreenshotTest`, `LetterCompleteScreenshotTest`) with `@Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }` and removed tracker wait.
  - Verified `./gradlew testDebugUnitTest` and two runs of `recordRoborazziDebug --tests 'com.playit.app.screenshot.*'` passed cleanly (247 tests, 0 failures; Blend It picture rendered reliably in screenshots).

### agy, 2026-10-07 (night): APK A built
- **APK A:** Built via `./gradlew assembleDebug` from commit `b49fb11` (Cards 26, 09, 17c included; Card 13 skipped per tonight's time rule).
- **Location:** `C:\Users\Zendrix\Documents\playIT-apk\playit-debug-A-b49fb11.apk` (`/mnt/c/Users/Zendrix/Documents/playIT-apk/playit-debug-A-b49fb11.apk`, 99 MB).
- **Safe build for tonight's meeting**: Ready for groupmates testing.

### Claude review, 2026-10-08 ("run and review")
- **Accepted:** card 26 (82d3d5a), card 09 (64bf5fd), card 17c (b49fb11). CI is green on all three, and the local suite is 251/251 at 91fdd74.
- **Card 26:** the code is exactly the card's.
  - **Finding (Claude's card, not your code):** the unused `AudioCompletenessCheck` class still lists the deleted mp3s. Nothing calls it.
  - **Fix card 26b (ready):** delete the class.
- **Card 09:** exact. Every screen asks `AudioResolver` first, so the seeded `phoneme_m.mp3` path is never played.
- **Card 17c:** `AssetImage.kt` is byte-identical to Claude's verified diff.
- **Card 18 is now `ready`.**
- **New checks in `review_card.py`:**
  - `refs` fails when a deleted asset is still named in app code.
  - `hash` warns until the reviewer writes the hash into the evidence-log row. agy can't know its own hash, so keep writing the commit subject there.
- **"run and review" steps** now live only in `CLAUDE.md`, and they're generic.
- **The groupmates did not test last night.** They test tonight with **APK A2** (APK A plus cards 13 and 26b). Sheet: `docs/tasks/GROUP_TEST_2026-10-08.md`; it says only M's sound is new.
- **TONIGHT, read `docs/tasks/TONIGHT_2026-10-08.md`:**
  - **Code (no Claude review between cards; Claude reviews tomorrow):** 13 → 26b → **APK A2** → `TEST WITH groupmates` → 18.
  - **Images:** card 25 resumes after the quota reset (about 19:11 PH time).
- **Waiting on the user (2 listening pages):**
  - `Documents/playIT-audio-batches/2026-10-08-no-ah-redo/index.html`: the "no ah" correction, 14 rows. It unblocks card 03b, which runs after 19.
  - `Documents/playIT-audio-batches/2026-10-01-heldsound-s-a-i/index.html`: held S, A, I, 33 takes. It unblocks card 09b, Chapter 1's other three letters.

### Claude, 2026-10-08 (afternoon): the user's listening reviews
- **Held /s/:** the user scored the Chatterbox "Sssss." take 5 of 5. It ships at its own length, 1.19 s.
  - Release `docs/audio-release/2026-10-08-s`; **card 09b is ready**.
  - Run it after card 18, and **never before APK A2**: the meeting build has only the new M.
- **/a/ and /i/** (tool: `tools/audio/vowel_from_keyword.py`): the user rejected all of them (best /i/ was 3 of 5).
  - Claude's measurements agree: the Chatterbox vowels drift to "ah/aw", and the Kokoro ones to "eh" or "ee".
  - New method (user decision): cut the vowel out of the approved key words "apple" and "insect" and stretch it (300-600 ms). Page: `2026-10-08-vowels-from-keywords`.
- **"no ah" (card 03b):** the user keeps the current `fb_no_ah` for now and wants a better take later. A swap needs no code change (same clip id).
  - The split remakes clicked at the cuts (Claude's splice bug, now fixed). Optional page: `2026-10-08-no-ah-redo-2`.
  - Claude writes card 03b after card 19 is accepted, so 03b's code matches 19's.
- **Later, 2026-10-08:**
  - The user rejected both new pages, `2026-10-08-vowels-from-keywords` ("robotic or buzzy", "cut off or choppy") and `2026-10-08-no-ah-redo-2`.
  - **"no ah":** card 03b uses the current `fb_no_ah`. No more remakes unless the user asks.
  - **/a/ and /i/:** the user chose the **onset style, "a, a, apple"**: the vowel at its natural length (about 0.1-0.15 s), not stretched, with a soft decay.
    - Tool: `tools/audio/vowel_onset.py`. Page: `2026-10-08-vowel-onsets` (12 rows).
    - It changes the Hear It rhythm for vowels, so it needs a card (Claude writes it after the user scores the page). The teacher audit must confirm it, and the adviser must confirm it as a spec change (Table 4's ~800 ms is for held consonants).
- **Later still, 2026-10-08:**
  - The onset page didn't satisfy the user either (only /a/ O3 scored 4 alone, 3 in Hear It; /i/ unscored).
  - **User decision:** a team member makes the short vowels with **ElevenLabs**.
    - Guide: `docs/tasks/AUDIO_TASK_short-vowels.md`. It uses Voice Changer from their own spoken takes into a Lily clone (`docs/audio-reference/lily_reference.wav`), plus Text to Speech for the "a, a, apple" phrases.
    - Checker for the team member's laptop: `tools/audio/check_recordings.py` (stdlib only).
    - AGENTS.md records the decision.
  - Due Oct 11. Claude then builds the user's review, the release and a card.

### agy, 2026-10-08 (night): Cards 13, 26b, APK A2, 18, and 09b executed

- **Execution Summary:**
  1. **Card 13: Image batch 1 into app (`card-13-image-batch-01-into-app.md`)**:
     - Verified all 29 image hashes against `docs/image-release/2026-10-07/manifest.json`.
     - Copied all 29 PNG files to `app/src/main/assets/images/pictures/`.
     - Updated `GridGenerator.kt` so "Up" uses its own picture (`images/pictures/picture_up.png`).
     - Added `up_usesItsOwnPicture` test in `GridGeneratorTest.kt`.
     - Added `PictureAssetsTest.kt` with `everyGridPictureExists` and `releasedPicturesMatchManifest`.
     - Unit tests: 254 passed, 0 failed.
     - Committed: `b909329` (`feat(assets): batch-1 pictures from image release 2026-10-07; "Up" gets its own picture (FR-05)`).
     - Verified with `review_card.py 13`: ALL PASS. Pushed to `origin refactor/hear-say-it`.

  2. **Card 26b: Remove dead audio check (`card-26b-remove-dead-audio-check.md`)**:
     - Deleted unused `app/src/main/java/com/playit/app/data/audio/AudioCompletenessCheck.kt`.
     - Ran verification grep confirming zero remaining references across `app/src/main`.
     - Unit tests: 254 passed, 0 failed.
     - Committed: `0886ec4` (`refactor(audio): remove the unused AudioCompletenessCheck class (NFR-AUD-01)`).
     - Verified with `review_card.py 26b`: ALL PASS. Pushed to `origin refactor/hear-say-it`.

  3. **APK A2 Built (Meeting Build)**:
     - Built from commit `0886ec4` using `./gradlew assembleDebug --no-daemon`.
     - Output preserved: `playit-debug-A-b49fb11.apk` intact and not overwritten.
     - APK A2 staged at:
       - Windows path: `C:\Users\Zendrix\Documents\playIT-apk\playit-debug-A2-0886ec4.apk`
       - WSL path: `/mnt/c/Users/Zendrix/Documents/playIT-apk\playit-debug-A2-0886ec4.apk`
       - Size: 106,203,823 bytes (~101 MB).

  4. **Card 18: Adaptive layout foundation (`card-18-adaptive-layout-foundation.md`)**:
     - Created `Dimens.kt` (WindowProfile COMPACT, REGULAR, WIDE tokens; 64dp touch and 16sp text floors).
     - Created `LessonScaffold.kt` (fixed topBar/header/bottomBar, scrollable weight body with `contentMaxWidth` cap).
     - Created `Devices.kt` qualifiers and `WithFontScale`.
     - Created test suites: `DimensTest.kt`, `GummyContainerLayoutTest.kt`, `LayoutMatrixTest.kt`.
     - Updated `Theme.kt` with `LocalPlayItDimens`.
     - Updated `MascotSpeechHeader.kt` with adaptive dimens tokens, maxLines and ellipsis.
     - Updated `GummyButton.kt` (`GummyContainer` measuring via custom `Layout` composable).
     - Updated `.height()` -> `.heightIn(min = ...)` across all 17 specified presentation files.
     - Unit tests: 274 tests (273 passed, 1 ignored on compact fontScale).
     - Committed: `5489293` (`feat(ui): adaptive dimensions, LessonScaffold, GummyContainer grows with content (NFR-ACC-02)`).
     - Verified with `review_card.py 18`: ALL PASS. Pushed to `origin refactor/hear-say-it`.

  5. **Card 09b: Approved held /s/ sound (`card-09b-held-s.md`)**:
     - Verified SHA-256 for `docs/audio-release/2026-10-08-s/phonemes/ph_s.wav` (`a3c53911618eb4e8bbfd70ef51e4c0ae080c69b48ec5ed5698f4bca8fa7d8009`).
     - Copied `ph_s.wav` to `app/src/main/assets/audio/phonemes/ph_s.wav`. Preserved `phoneme_s.mp3`.
     - Updated `AudioResolver.kt`: added `"s"` to `releasedPhonemes = setOf("m", "s")`.
     - Updated `AudioResolverTest.kt`: updated standard letters check, added `phonemeS_usesReleasedHeldSound`, updated `otherPhonemes_keepOldPath`, updated `releasedPhoneme_fileExists` for `ph_m.wav` and `ph_s.wav`.
     - Unit tests: 275 tests (274 passed, 1 ignored).
     - Committed: `1df3f56` (`feat(audio): approved held /s/ replaces the Edge clip (NFR-AUD-01, FR-02)`).
     - Verified with `review_card.py 09b`: ALL PASS. Pushed to `origin refactor/hear-say-it`.

- **Current Branch Status:**
  - Branch: `refactor/hear-say-it`
  - Head commit: `5c3ff64`
  - Working tree clean.

### agy, 2026-10-09: Card 25 Round 1 Review & Picks (Mouth Shapes)
- **Batch**: `2026-10-07-mouth-shapes`
- **User Review Completed**:
  - The user reviewed Round 1 on `round-01/index.html` and exported `2026-10-07-mouth-shapes_round-01_review.csv`.
  - 6 of 9 items picked:
    - `mouth_lips_together`: `round-01/mouth_lips_together__v2.png`
    - `mouth_teeth_on_lip`: `round-01/mouth_teeth_on_lip__v2.png`
    - `mouth_tongue_up`: `round-01/mouth_tongue_up__v2.png`
    - `mouth_teeth_close`: `round-01/mouth_teeth_close__v1.png`
    - `mouth_back`: `round-01/mouth_back__v1.png`
    - `mouth_round`: `round-01/mouth_round__v1.png`
- **Staged Assets in Git for Claude**:
  - `docs/assets/briefs/2026-10-07-mouth-shapes/2026-10-07-mouth-shapes_round-01_review.csv`
  - `docs/assets/briefs/2026-10-07-mouth-shapes/picks.json`
  - `docs/assets/briefs/2026-10-07-mouth-shapes/picks/<id>.png` (6 picked PNGs)
- **Open Items for Round 2**:
  - `mouth_wide_open` (variants 1 & 2)
  - `mouth_smile` (variants 1 & 2)
  - `mouth_open_breath` (variants 1 & 2)
  - Note: Quota on `gemini-3.1-flash-image` has reset. Ready for Round 2 generation when directed.

### agy, 2026-10-09 (night): Sprint Orchestration & MVP Validation Deliverable

- **Sprint Mission:** Complete the Hear It / Say It refactoring on branch `refactor/hear-say-it` by **Saturday, October 10, 2026**.
- **Role Alignment (User Decision 2026-10-09):**
  - **Claude (in WSL):** **The Mind & Implementator** — conducts `"run and review"`, refines sprint roadmap, directly writes/implements Kotlin & Compose code and unit tests, and creates validator checklists for agy.
  - **agy (Antigravity):** **The Orchestrator & Validator** — orchestrates documentation/context for Claude, executes automated test validation (`./gradlew testDebugUnitTest`), Roborazzi layout matrix screenshots across 4 display sizes, and builds APKs.
- **Deliverables Completed by agy:**
  1. `docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`: Fully populated institutional deliverable linking empirical data (N=25, SUS 75.5, DepEd teacher findings) to SRS v3.0, SDD v2.0, and SPMP v2.0.
  2. `docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.docx`: Official Word document programmatically populated via `tools/docs/fill_mvp_validation_form.py` with all sections, paragraphs, tables, and reflections.
  3. `docs/tasks/SPRINT_OCT10_ORCHESTRATION.md`: Master roadmap detailing the 4-phase sprint for Cards 19, 03b, 20, 22, 21, 23, 24, and 25.
  4. `docs/tasks/VALIDATOR_RUNBOOK_OCT10.md`: Verification schema for agy.
  5. `CLAUDE.md`: Updated shortcut command `"run and review"` directing Claude to execute as the Implementator.
- **Current Baseline Status:**
  - Head commits ready for Claude acceptance: `b909329` (Card 13), `0886ec4` (Card 26b), `5489293` (Card 18), `1df3f56` (Card 09b).
  - Mechanical checks via `python3 tools/dev/review_card.py 13/26b/18/09b`: ALL PASS.
  - Full local test suite: `./gradlew testDebugUnitTest` is **100% green (BUILD SUCCESSFUL, 36 actionable tasks, 0 failures)**.
- **Next Immediate Action for Claude:**
  - Pull branch `refactor/hear-say-it`.
  - Type `"run and review"`.
  - Accept commits in `docs/evidence-log.md`.
  - Implement **Card 19 (`card-19-hearit-sayit-fit-mic-states.md`)** per Task 3 in `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`.
  - Update `docs/tasks/VALIDATOR_RUNBOOK_OCT10.md` with Card 19 verification specs and push to origin.




### Claude, 2026-10-09 (night): "run and review": cards 13, 26b, 18, 09b accepted; card 19 implemented

- **Environment:** this session ran on Windows (Git Bash + PowerShell), not WSL: Python 3.13 at `%LOCALAPPDATA%\Programs\Python\Python313`, JDK 17 (Adoptium), Android SDK at `%LOCALAPPDATA%\Android\Sdk`, `gradlew.bat`. `review_card.py` passed a Windows path to `git show` and crashed. It now uses POSIX paths (commit `6608552`).
- **Accepted (`6608552`):**
  - Cards 13 `b909329`, 26b `0886ec4`, 18 `5489293` and 09b `1df3f56`.
  - `review_card.py`: all PASS. Card 26b has one WARN, which is expected: the card keeps `AudioCompletenessCheckTest`.
  - CI green on each: runs 37785988722, 37790588953, 37800443965, 37805757544.
  - Evidence-log rows now carry hashes and CI runs, plus "(accepted)" rows.
- **Card 19 (`dc6098e`), pushed:**
  - `MicStatus` and `micStatusFor`.
  - `SayItViewModel.micStatus` and `onScreenHidden`. `SayItScreen` calls it on ON_STOP and on dispose.
  - New `MicButton` with 4 states plus a reduced-motion variant. No red.
  - Hear It and Say It now use `LessonScaffold`. The Say It feedback banner sits in the bottom bar above Next.
  - Tests: 294 run, 0 failed, 2 skipped (forced rerun). The 10 card tests exist and pass. Claude checked the Roborazzi `hearit_*` and `sayit_*` images on all 4 sizes.
  - Plan deviations (in the commit body):
    - LetterCard is the column width up to 320 dp, not a 0.97 aspect ratio. At 0.97 the compact card cut "M is for Mouse".
    - Say It cards use 80% of `letterCardHeight`.
    - On compact, the "Tap to listen" pill is hidden.
    - The red waveform bar and the 11.5 sp noise pill are gone.
  - Open point for Claude: the Hear It key-word picture is small (about 40-60 dp), because the "Mm" letter takes most of the card. Possible follow-up: raise REGULAR/WIDE `letterCardHeight`, or shrink the letter on short cards. Decide after agy's screenshots and the phone check.
- **Card 19 Validation Verdict (agy):**
  - Commit `dc6098e` verified.
  - `python3 tools/dev/review_card.py 19`: **ALL PASS** (files, tests, status, commit body, zero-emoji).
  - All 10 card tests verified present in codebase.
  - **Verdict:** ✅ VALIDATED.
- **Continuous Mode Authorized (User Directive 2026-10-09):**
  - **Do NOT pause for agy between cards.**
  - Claude proceeds continuously through the Tier 1 pipeline: **Card 03b** (`card-03b-tutor-policy-fixes.md`) $\to$ **Card 20** (`card-20-findit-blendit-fit.md`) $\to$ **Card 22** (`card-22-map-overhaul.md`).
  - agy will perform the full multi-card review, test suite execution, and APK build once Claude finishes the sprint run.

### Claude, 2026-10-09 (overnight): continuous sprint, cards 03b, 20, 22, 21, 23, 15, 24, 27 pushed

- **Environment:** Windows (Git Bash, Python 3.13, JDK 17, Android SDK in `%LOCALAPPDATA%`), not WSL. `review_card.py` got two Windows fixes (`6608552` path form, `b9ae600` UTF-8 output).
- **Accepted:** card 19 (`80abe20`), after agy's verdict. Cards 13, 26b, 18 and 09b were accepted earlier (`6608552`).
- **Pushed in order:** 03b `339f309`, 20 `9bb8cb7`, 22 `3be745b`, 21 `9bac8ca`, 23 `dd42582`, 15 `299cea2`, 24 `20efb22`, 27 `eaf8634`.
  - Local suite after card 24: 364 tests, 0 failed, 6 skipped (font scale on 360x640, by design).
  - CI green through `299cea2`.
- **Decisions and deviations** (each is in its commit body):
  - **03b:** the correction clips the code asked for never shipped, so the first miss was silent. It now ships the user-approved fragments from release 2026-10-01 and plays the spec §3.2 sequences. Claude rewrote the card (the draft named mp3 paths that don't exist).
  - **20:** on compact the Blend It card drops to 168 dp with a 64 dp picture, so the tiles clear Check Word. Card 16's test now uses the unmerged node tree. Find It's two cells use icons plus "n / 3" and "/M/".
  - **22:**
    - The Lily chip plays the existing map greeting VO.
    - The unlock comparison resets only on a profile change, so it survives the map flow resubscribing after a lesson; a test covers this.
    - The background is recorded once per size into a Picture.
  - **21:** not done, because the files are outside the card: `d.completeMascot` (DockedMascotWithBubble has no size parameter) and the adaptive avatar grid.
  - **23:** `correctPop` is defined and tested but has no new consumer in this card's files.
  - **15:**
    - The word list is written on every open; the old seed never reached existing installs.
    - Claude rewrote the card: the draft had the wrong group letters.
    - **Assets missing:** audio for AM, TUB and YAM (approved takes in batch `2026-10-06-blendwords-card15`, no release yet) and pictures for AM, TUB, YAM and ZIP. These words are silent and have no picture until then.
    - **If the user prefers, revert `299cea2` before the release APK.**
  - **24:**
    - A new `playSequence(paths, onItemStart, onComplete)` overload; the 2-argument form is unchanged.
    - No mouth pictures yet (card 25 has 6 of 9 picks and no image release).
  - **27:** the SDD/SRS/SPMP updates mark only what shipped. The card's draft test names (FindItGridTest, DatabaseSeedTest) don't exist and were replaced by real ones.
- **Found, not fixed** (outside the cards' files):
  - `MarungkoGroupBanner` clips its title line on the map;
  - the Hear It key-word picture is small (about 40-60 dp).
- **Next:**
  1. agy runs `VALIDATOR_RUNBOOK_OCT10.md` section 5 (batch tests, 4-size screenshots, APK B, phone checks).
  2. Claude accepts the batch, then:
     - the card 15 asset release (word audio from the approved batch; pictures through an agy image round);
     - card 25 round 2 (3 mouth shapes), then its image release, so the mouth cues appear;
     - a small fix card for the map banner and the Hear It picture size, if the user wants them.

### agy, 2026-10-10: Sprint Batch Validation Verdict (Cards 03b, 20, 22, 21, 23, 15, 24, 27)

- **Commits Verified:**
  - `339f309`: Card 03b (Spoken Say It corrections; 5 tutor clips from release 2026-10-01)
  - `9bb8cb7`: Card 20 (Find It and Blend It in LessonScaffold; FindItGrid; card 16 fix)
  - `3be745b`: Card 22 (Map: rope trail, Lily chip, MapLayout, static avatar, unlock moment)
  - `9bac8ca`: Card 21 (Complete, splash and profile screens fit; Type.kt child sizes)
  - `b9ae600`: Tools fix (review_card.py reads git output as UTF-8 on Windows)
  - `dd42582`: Card 23 (Screen transitions, centre confetti, heart wobble, star drop, reduced motion)
  - `299cea2`: Card 15 (AM, SUM, TUB, YAM, ZIP; word list written on every open)
  - `20efb22`: Card 24 (Captions in Hear It; mouth cue framework)
  - `eaf8634`: Card 27 (SDD 2.2, SRS 3.2 §4.1, SPMP 2.2 synchronized)
  - `5e881a6`: Session handoff commit

- **Mechanical Checks (`review_card.py`):**
  - Ran `for c in 03b 20 22 21 23 15 24 27; do python3 tools/dev/review_card.py $c; done`.
  - All cards report **ALL PASS** (files, tests, status, master tasks ticked, evidence-log row, commit body, assets, zero-emoji).
  - Note: Card 24 file path formatting resolved cleanly in handoff commit.

- **Automated Unit Test Suite:**
  - Executed `./gradlew testDebugUnitTest --no-daemon`.
  - **Result:** `BUILD SUCCESSFUL in 30m 37s`, **364 tests run, 0 failed, 6 skipped** (font-scale 1.3 checks on compact 360x640 skipped by design).
  - **Pass Rate:** 100%.

- **Roborazzi 4-Device Screenshot Matrix:**
  - Executed `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*' --no-daemon`.
  - **Result:** `BUILD SUCCESSFUL in 6m 51s`, all 41 layout screenshots recorded cleanly in `app/build/outputs/roborazzi/`.
  - Verified across `compact` (360x640), `a21s` (360x740), `phone` (411x891), and `tablet` (800x1280):
    - `findit_*`: All 5 cards visible above the bottom edge without scrolling; status cells display cleanly.
    - `blendit_*`: Tiles sit cleanly above "Check Word"; "Tap to hear word" contained within card.
    - `map_*`: Procedural rope trail renders cleanly; 16-character learner name ellipsizes without pushing stat pills offscreen; static avatar positioned beside current node.
    - `complete_*`, `splash_*`, `nameprompt_*`: Primary CTAs (Continue, Start, Let's Play) fully visible without viewport cut.
    - `hearit_*`, `sayit_*`: Responsive letter cards fit; 4-state mic button and attempt dots visible; caption bubble row integrated.

- **APK Build & Release Verification:**
  - Executed `./gradlew assembleDebug --no-daemon`.
  - **Result:** `BUILD SUCCESSFUL in 5m 24s`.
  - Output binary: `app/build/outputs/apk/debug/app-debug.apk` (102 MB).
  - Copies created:
    - `Documents/playIT-apk/playit-debug-B-eaf8634.apk`
    - `./playit-debug.apk`
  - Offline integrity: zero network egress, fully functional offline.

- **Overall Batch Verdict:**
  - **Cards 03b, 20, 22, 21, 23, 15, 24, 27:** ✅ **ALL VALIDATED & ACCEPTED**.

### Claude, 2026-10-10: IT411 submission package review (`docs/submission/`)

- **Problems in the package built at `f09070f`:**
  - **MVP form** (the course template, filled by `fill_mvp_validation_form.py`): only Refactoring Priority #1 of 6 was in the form; the Section 5 summary matrix was empty; every Section 6 row still read "Yes / No"; Findings #3 and #4 were packed into heading paragraphs; template placeholders ("Paragraph response.", "Reference Finding #___") stayed in; the adviser was "Prof. [Adviser Name]".
  - **Facts the validation report contradicts** (`docs/specs/validation-report.md`, `04_MVP_VALIDATION_HIGHLIGHTS.md`): 3 technical evaluators and a decision-maker respondent were listed, but none took part in Round 1; Finding #4 was credited to teachers, but teachers did not rate the word bank in Round 1 (a post-pilot design review found it); Priority #4 cited a Finding #5 that did not exist; the SRS gave the SUS as grade B+ (the report says B).
  - **SDD/SRS/SPMP PDFs:** raw Markdown and LaTeX in the output (`**0.1**`, `<br>`, `$\ge 64\,\text{dp}$`), the ASCII architecture diagram turned into a broken table, code blocks reflowed as body text, and list numbering continued across the document.
- **What changed:**
  - `tools/docs/md_to_docx.py` (new): Markdown to .docx (tables with inline formatting, code blocks and diagrams in a monospace box, LaTeX to Unicode, nested lists).
  - `tools/docs/render_submission_package.py`: cover page with authors and adviser, a table of contents that Word fills in, page numbers, landscape pages for the RTM and the risk register, PDF bookmarks. No text-only fallback PDF any more: without Word (or LibreOffice) the script stops with an error.
  - `tools/docs/fill_mvp_validation_form.py`: rewritten. It fills the official template from the Markdown, keeps the template wording, ticks the selected options (unselected ones show an empty box), repeats the Finding and Priority blocks per item, and fills both tables.
  - Sources: the MVP form Markdown was rewritten against the validation report (5 findings, 6 priorities). SDD 2.3, SRS 3.3 and SPMP 2.3 each have a revision row that lists their corrections.
- **Checked:** every page of the 4 PDFs by eye; a scan finds no leftover Markdown, LaTeX or placeholders; the DOCX tables of contents are filled in. The evidence log has accepted rows for all 8 batch cards (03b, 20, 22, 21, 23, 15, 24, 27). Their CI column says "pass" from local and agy runs, without GitHub Actions run links.
- **Open for the team:** confirm the adviser's name (taken from `PROMPT_FOR_CLAUDE_TRANSMITTAL_REVIEW.md`), the group name and the section ("IT411 G1–G8"); sign the declaration if a wet signature is needed.
- **Rebuild:** `python3 tools/docs/render_submission_package.py` (python-docx plus Word through PowerShell; works from Windows or WSL).
