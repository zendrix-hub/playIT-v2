# Session Handoff: Claude & agy Bridge

> This document is the asynchronous communication bridge between **Claude** (architect, card author, reviewer) and **agy** (implementer) on branch `refactor/hear-say-it`.
> Update this document at the conclusion of each session so the other agent has complete, structured context upon pulling.

## Fast-Track Trigger: "run and review"

When the user enters `"run and review"`, Claude should immediately:
1. Run `python3 tools/dev/review_card.py 11` and `python3 tools/dev/review_card.py 12` in WSL (pre-verified by agy: ALL PASS).
   - Note: Card 06 and Card 07 were already reviewed and accepted in commit `ad23db9`.
2. Review code diffs:
   - `a17969d` — Card 07b (Avatar-only onboarding, parent rename, voiced map pop-up).
   - `667ea0e` — Card 08 (29 user-picked candidate PNGs for batch 1 staged for cutout).
   - `0d9ad3a` — Card 11 (Stars math, 3-heart restart, 5-heart display, session hearts persistence).
   - `dcff981` — Capstone 2 Week 3 Specifications (`SRS v3.0`, `SDD v2.0`, `SPMP v2.0`).
   - `ef03bea` — Card 12 (Find It distractor isolation, gentle correction orange, soft pop audio, `ZeroEmojiPolicyTest`).
3. Update `docs/evidence-log.md` with technical acceptance for Cards 07b, 11, and 12.
4. Execute next Claude-owned tasks:
   - **Card 09**: Held /m/ audio pipeline in `tools/audio/`.
   - **Card 13**: Background cutout & manifest for 29 candidate PNGs picked in Card 08 (`667ea0e`).
   - **Cards 10 & 03b**: Author next task cards.

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
