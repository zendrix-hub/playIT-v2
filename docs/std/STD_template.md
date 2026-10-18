# Software Test Document (STD)
## Project: PlayIT — An Offline-First Gamified Early Literacy Mobile Application
**Course:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Document Version:** 1.0 (test record of debug APK H, commit `0320385`)  
**Publication Date:** October 14, 2026  
**Document Status:** Submitted for Adviser Review and Evaluation  
**Prepared by:** Group 56 — PlayIT Capstone Team: Riva, Zendrix (Team Lead); Palis, John James; Miel, Kaysean; Durano, Ashlee Skye; Bien, Erik Samuel  
**Adviser:** Mr. Joemarie C. Amparo  

---

## Document Revision History

| Version | Date | Primary Author(s) | Description |
|:---:|:---:|:---:|---|
| **1.0** | October 14, 2026 | Capstone Team | First issue. Specifies the test cases for SRS v3.0 rev. 3.8, records a clean run of the whole automated suite on the code of debug APK H ({{COUNT:tests}} test cases: {{COUNT:passed}} passed, {{COUNT:skipped}} skipped by design, {{COUNT:failed}} failed), the 4-size layout matrix, two inspections of the build, and the incidents found in testing. It reports two open defects found while writing it (DEF-01, DEF-02). The tables of results are generated from the raw JUnit logs by `tools/docs/build_std.py`, which stops if a cited test is missing or did not pass. |
| **1.0.1** | October 18, 2026 | Capstone Team | The test item is now the team lead's build of the same commit `0320385` (SHA-256 `b5a54c18…42d4`, 108,317,310 bytes), the file installed on the team's Samsung Galaxy A21s and submitted for the midterm. The build inspected on October 14 (`d301007f…937d`, 108,489,471 bytes) was made from that commit on another PC. The two differ only in the debug signing key, the line endings of 8 text files of the speech model, and code that the compiler and Hilt generate; no class written by the team, picture or sound differs (`docs/std/evidence/2026-10-18-submitted-apk/apk_inspection.txt`). The submitted file was inspected on October 18 with the same permissions as on October 14 (§5.4). No test result changed. |

---

## 1. Introduction

### 1.1 Purpose
This Software Test Document records how the refactored PlayIT was tested against its requirements, and what the tests showed. It follows the chain the course asks for:

**Updated SRS (v3.0 rev. 3.8) → System Features (the six modules) → Test Cases (§4) → Test Execution (§5, §6) → Test Results (§5 to §8)**

For every test case it states the input, the expected result taken from the SRS, the actual result observed, and the evidence (the test methods that ran, or the inspection output). A test case is marked **Pass** only when every piece of its evidence passed in the recorded run. Where the build does not meet a requirement, the test case is marked **Fail** and the defect is reported in §7, not left out. Where a test case could not run on this build (a planned feature, a field measure, or a device timing), it is listed in §4.8 with the reason and the date it is scheduled.

The structure follows ISO/IEC/IEEE 29119-3:2021 (test documentation), condensed for one build: test plan (§2), test case specifications (§4), execution log (§5, Appendix A), incident report (§7) and test completion report (§8).

### 1.2 Test Item and Scope
- **Test item:** PlayIT debug APK H, `playit-debug-H-0320385.apk` (SHA-256 `b5a54c18…42d4`), built from branch `refactor/hear-say-it` at commit `0320385` on the team lead's PC. This is the build submitted with the SDD v2.0 rev. 2.8, SRS v3.0 rev. 3.8 and SPMP v2.0 rev. 2.6. An earlier build of the same commit (SHA-256 `d301007f…937d`) was inspected on October 14; revision 1.0.1 above records the change.
- **In scope:** all automated unit, component and layout tests in `app/src/test/`; the 4-size layout matrix; an inspection of the APK's permissions; an integrity check of every released audio clip; and the phone tests the team ran on earlier builds, which found the incidents in §7.1.
- **Out of scope for this run** (each listed in §4.8): Round 2 field validation with learners and teachers (Weeks 7–8), the Gate 3 teacher audit of audio, timing measured on the device, and the features the SDD §2.0 lists as planned (telemetry and CSV export, spaced recall, session pacing, the voice-driven mic ripple).

### 1.3 References
1. PlayIT Software Requirements Specification (SRS v3.0, rev. 3.8), October 14, 2026: requirement IDs and the RTM test case IDs (§4).
2. PlayIT Software Design Description (SDD v2.0, rev. 2.8), October 14, 2026: §2.0 implementation status, §6 verification plan.
3. PlayIT Software Project Management Plan (SPMP v2.0, rev. 2.6): WBS 7 (Verification and STD), Milestones 6 and 7.
4. PlayIT MVP Validation Findings and Refactoring Priorities (course form), October 14, 2026.
5. ISO/IEC/IEEE 29119-3:2021, *Software and systems engineering — Software testing — Part 3: Test documentation*.
6. IEEE Std 829-2008, *IEEE Standard for Software and System Test Documentation* (superseded by 29119-3; used for the incident report fields).

### 1.4 Definitions
| Term | Meaning in this document |
|---|---|
| **Test case (TC)** | One check of one requirement behaviour, with input, expected result, actual result and status. IDs follow the SRS RTM families (TC-AUD, TC-MIC, TC-ASR, TC-BLD, TC-ACC, TC-IND, TC-DB, TC-TEL, TC-EXP, TC-REV, TC-PERF, TC-SCR) and add TC-SAY, TC-FND, TC-PRG, TC-PZ, TC-SEC and TC-POL for behaviours the RTM groups together. |
| **Test method** | One JUnit test in `app/src/test/`; a test case cites one or more. |
| **Pass / Fail / Skip** | As recorded by JUnit. A skip is allowed only for the 6 font-scale checks on the 360x640 profile (§5.3). |
| **Not run** | The test case is specified but could not run on this build; §4.8 gives the reason. |
| **Robolectric** | Runs Android code, including Jetpack Compose screens, on the JVM, so screens can be tested without a phone. |
| **Roborazzi** | Captures the screens rendered by Robolectric as images (§6). |
| **Unclipped bounds** | A component's full position, even if it is scrolled or pushed out of view; the layout checks use it so that content off screen fails. |
| **Foil** | A wrong answer that sounds close to the right one: the letter's name ("em" for /m/), an added vowel ("muh"), or a substituted sound ("pa" for /f/). |
| **dp / sp** | Density-independent pixels (size) and scaled pixels (text). |

---

## 2. Test Approach

### 2.1 Test Levels
| Level | What it exercises | How it runs | Where reported |
|---|---|---|---|
| **L1 Unit** | Pure rules in `domain/`: hearts, stars, unlock, streak, grid, speech judge, prompt ladder, word bank | JUnit 4 on the JVM | §4, §5 |
| **L2 Component** | ViewModels with mocked repositories, audio and recognizer (MockK, coroutine test clock); Room DAO on an in-memory database | JUnit 4, Robolectric, kotlinx-coroutines-test | §4, §5 |
| **L3 UI and layout** | Compose screens on 4 screen profiles and at font scale 1.3; screen captures | Robolectric, Compose UI test, Roborazzi | §6 |
| **L4 Inspection** | The built APK's permissions; the released audio against the app's files; source policies (no emoji, no haptics) | `aapt dump`, `tools/docs/check_audio_release.py`, policy tests | §5.4 |
| **L5 Device (manual)** | The app on a Samsung Galaxy A21s, played as a child would | Team phone tests of APK B, D and E; APK H acceptance before submission | §5.5, §7.1 |
| **L6 Field** | Learners, parents and teachers (Round 2) | SPMP Milestones 5–6, Weeks 7–8 | §4.8 (not run) |

### 2.2 Test Design Techniques
| Technique | Where it is used |
|---|---|
| **Equivalence partitioning** | Say It answers split into classes: the key word, the letter's name, an added vowel, a substitution, another word, a partial word, silence (TC-ASR-07 to TC-ASR-10). |
| **Boundary values** | Hearts 5 → 0 and the 3-heart restart; recovery at the pool's cap; 6 profiles; a 16-character learner name; idle at 9 s and 10.001 s; the smallest screen, 360x640 (TC-FND-05 to 07, TC-PRF-01, TC-IND-01, §6). |
| **State transition** | The four mic states and the prompt ladder from the first to the third miss (TC-MIC-03, TC-SAY-01 to 05). |
| **Decision table** | Star rules by hearts lost (TC-PRG-01, TC-PRG-02). |
| **Error guessing** | Rapid repeated taps, the recognizer stopped by the system, a tile deal that is already solved, a stale picture after "next letter" (TC-ASR-11, TC-ASR-12, TC-BLD-02, INC-04). |
| **Negative tests** | Unknown lesson IDs, blank or too-long names, wrong and non-numeric answers at the parent gate (TC-FND-08, TC-PRF-02, TC-PZ-01). |

### 2.3 Beyond the Happy Path
| Failure mode | Test cases |
|---|---|
| **Invalid inputs** | TC-ASR-07 (letter names, added vowels, substitutions rejected with their error type), TC-ASR-08 (partial, misspelled and other words rejected), TC-FND-08 (unknown lesson ID), TC-PRF-02 (blank or 17-character name ignored), TC-PZ-01 (wrong and non-numeric gate answers) |
| **Boundary conditions** | TC-FND-05 (hearts reach 0), TC-FND-06 (restart with 3 hearts, losses kept), TC-FND-07 (recovery capped at the starting pool), TC-BLD-05, TC-PRF-01 (6th profile), TC-PRG-01 (star thresholds), §6 (smallest screen, font scale 1.3, 16-character name) |
| **Missing inputs and timeouts** | TC-ASR-09 (blank transcript), TC-ASR-10 (no final result: judged at the 3.8 s timeout), TC-IND-01 (10 s without a touch), TC-ASR-11 (recognizer stopped by the system) |
| **Incorrect user actions** | TC-BLD-02 (tiles never dealt solved), TC-BLD-03 (wrong word: one heart, a soft sound, no buzz), TC-BLD-04 (hint after two mistakes), TC-ASR-12 (rapid taps), TC-SAY-04 (tapping the mic after the third miss does nothing) |
| **Unauthorized access and data leaving the device** | TC-PZ-01 (parent gate), TC-SEC-01 (no INTERNET permission in the APK), TC-SEC-02 (cloud backup of the database disabled) |

### 2.4 Pass/Fail, Entry and Exit Criteria
- **Entry:** the code compiles; the run starts from clean results (`cleanTestDebugUnitTest`), so no result is reused from an earlier run.
- **A test case passes** when every cited test method passed in the recorded run, or the inspection output equals the expected result. The generator of this document refuses to build if a cited test is missing from the run, failed, or was skipped outside the allowed font-scale checks.
- **A test case fails** when the build does not do what the SRS requires, even if no automated test fails. Such a failure is reported as a defect in §7.2.
- **Exit criteria for the submission:** 0 failed automated tests; every failing test case linked to a defect with a severity and an owner; every test case that did not run listed with its reason and schedule.

### 2.5 Test Environment
- **Automated runs:** the team's Windows 11 development laptop; OpenJDK 17.0.16 (Eclipse Temurin), Gradle 8.9, Android Gradle Plugin 8.4.0, Kotlin 1.9.23, JUnit 4.13.2, Robolectric 4.13 emulating Android SDK 34, Roborazzi 1.26.0, MockK 1.13.10. The app is offline, so no network service is involved.
- **Screen profiles (Robolectric qualifiers):** Compact `w360dp-h640dp-xhdpi`; A21s `w360dp-h740dp-xhdpi` (the team's test phone); Phone `w411dp-h891dp-xxhdpi`; Tablet `w800dp-h1280dp-mdpi`.
- **Device:** Samsung Galaxy A21s (the team's test phone), for the manual tests in §5.5.
- **Test data:** the seeded curriculum (26 letters, 7 chapters, 33 Blend It words); fakes and mocks inside each test; no learner data.

---

## 3. Traceability: SRS → Features → Test Cases → Results

| Requirement (SRS rev. 3.8) | System feature | Test cases (§4) | Result on APK H |
|---|---|---|---|
| **FR-01** Carrier narration | Hear It | TC-HI-01, TC-IND-02 | Pass |
| **FR-02** Pure phoneme model and sequence | Hear It | TC-HI-01 to TC-HI-05; TC-AUD-01, TC-AUD-02, TC-SCR-01 | Pass; not run: device timing, teacher audit |
| **FR-03** Tutoring loop, mic states, prompt ladder | Say It | TC-MIC-03, TC-SAY-01 to TC-SAY-06, TC-ASR-09 to TC-ASR-12; TC-MIC-01, TC-MIC-02 | Pass; not run: device timing, planned ripple |
| **FR-NEW-REC** Spaced recall | Say It | TC-REV-01 to TC-REV-03 | Not run: planned (`LearnerModel`) |
| **FR-04** Find It grid | Find It | TC-FND-01, TC-FND-02, TC-FND-09, TC-AUD-04; TC-FND-03 | Pass; **Fail: TC-FND-03 (DEF-01)** |
| **FR-05** Hearts in Find It | Find It | TC-FND-04 to TC-FND-06, TC-FND-08 | Pass |
| **FR-07** Heart recovery | Find It | TC-FND-07 | Pass |
| **FR-13** Decodable Blend It words | Blend It | TC-BLD-01 to TC-BLD-07 | Pass |
| **FR-06** Star rules | Progression | TC-PRG-01; TC-PRG-02 | Pass; **Fail: TC-PRG-02 (DEF-02)** |
| **FR-08** Linear unlock | Progression | TC-PRG-03 | Pass |
| **FR-09** Daily streak | Progression | TC-PRG-04 | Pass |
| **FR-14** Multi-profile | Profiles | TC-PRF-01 to TC-PRF-03; TC-DB-03 | Pass; partial: TC-DB-03 |
| **FR-10** Parent dashboard and gate | Parent Zone | TC-PZ-01, TC-PZ-04 | Pass |
| **FR-12** PDF report | Parent Zone | TC-PZ-02 | Pass |
| **FR-NEW-TEL** Telemetry and CSV export | Parent Zone | TC-TEL-01, TC-EXP-01, TC-EXP-02 | Not run: planned (Room schema v4) |
| **NFR-AUD-01** Audio release gate | Audio | TC-AUD-03, TC-HI-03, TC-BLD-06, TC-PZ-03; TC-AUD-02 | Pass; Gate 3 teacher audit pending |
| **NFR-ASR-01** Judge accuracy and error types | Say It | TC-ASR-07, TC-ASR-08, TC-ASR-13; TC-ASR-01 to TC-ASR-06 | Pass; not run: Round 2 agreement and false rejects |
| **NFR-PERF-01** Latency | All | TC-PERF-01, TC-PERF-02 | Not run: needs telemetry on the device |
| **NFR-IND-01** Independent use, idle re-prompt | All lessons | TC-IND-01, TC-IND-02, TC-PRF-03; TC-IND-03 | Pass; not run: Round 2 |
| **NFR-ACC-01** Mouth-shape cues | Say It | TC-ACC-01, TC-ACC-03, TC-SAY-03 | Pass |
| **NFR-ACC-02** 64 dp targets, layout | All screens | TC-ACC-02, TC-ACC-04, TC-POL-03, layout matrix (§6) | Pass |
| **NFR-SES-01** Session pacing | All | TC-SES-01 | Not run: [Planned — Round 2], not built |
| **NFR-REL-01** Offline persistence | Data | TC-DB-01, TC-DB-02 | Not run: needs schema v4 and the device |
| **NFR-SEC-01** Local data only | Build | TC-SEC-01, TC-SEC-02 | Pass |
| Team policies (zero emoji, no haptics) | All screens | TC-POL-01, TC-POL-02 | Pass |

---

## 4. Test Case Specifications and Results

Each table gives the input, the expected result (from the SRS), the actual result observed in the run, the evidence and the status. Evidence and status come from the JUnit logs (§5).

### 4.1 Module 1: Letter-Sound Acquisition (Hear It)

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-HI-01** | FR-02.4, FR-01 | Functional | Build the modeling sequence for M (key word "mouse") | Starts with the carrier "Listen!"; the sound 3 times with 500 ms pauses before the key word; the key word; the sound once more; ends on the sound, never "Say it with me!" | First clip `car_listen.wav`; 3 sounds before the key word, 1 after; 2 pauses of 500 ms; the last two clips are the key word and the sound; no "say it with me" clip. Loading a letter plays the whole sequence, then the next cue | {{tc: HearItSequenceBuilderTest.build_m_mouse_followsTable3, HearItViewModelTest.load_playsFullModelingSequence, HearItViewModelTest.firstSequenceEnd_playsNextCue_andHighlights}} |
| **TC-HI-02** | FR-02.6 | Functional | Tap replay | Replays steps 3–6 ("This letter says…" to the sound), not the opening carrier | Replay starts at `car_this_letter_says` and ends on the sound; no "Listen!", no "say it with me" | {{tc: HearItSequenceBuilderTest.replayTemplate_isSteps3to6, HearItViewModelTest.playPhonemeSound_playsReplaySegment, HearItViewModelTest.replayEnd_alsoUnlocks}} |
| **TC-HI-03** | FR-02.1–3, NFR-AUD-01 | Functional | Resolve the sound for M, S, I and the other letters | M, S and I play their released clips (`ph_m.wav`, `ph_s.wav`, `ph_i.wav`), stored apart from carriers and words; the files exist and are not empty | Paths resolve to the released WAV files, which exist with content; letters not yet remade keep the MVP clip | {{tc: AudioResolverTest.phonemeM_usesReleasedHeldSound, AudioResolverTest.phonemeS_usesReleasedHeldSound, AudioResolverTest.phonemeI_usesReleasedClip, AudioResolverTest.releasedPhoneme_fileExists, AudioResolverTest.otherPhonemes_keepOldPath}} |
| **TC-HI-04** | FR-02 | UI | Hear It while the sound plays | One message at a time: Lily says "Listen!", the card shows the sound once, the play button keeps its colour | "Sound: /m/" appears once, the bubble says "Listen!", and the play button keeps its colour | {{tc: HearItScreenFocusTest.whilePlaying_soundTextShowsOnce_andLilySaysListen, HearItScreenFocusTest.whilePlaying_playButtonKeepsItsColour}} |
| **TC-HI-05** | FR-02 | Negative | Open Hear It with an unknown or missing letter ID | No crash: an unknown ID sets a load error; a missing ID falls back to letter 1 | Load error set for an unknown ID; a missing ID loads letter 1 | {{tc: HearItViewModelTest.loadPhoneme_invalidId_setsLoadError, HearItViewModelTest.loadPhoneme_nullId_defaultsToId1}} |

### 4.2 Module 2: Speech Production and Tutoring (Say It)

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-MIC-03** | FR-03.2 | State transition | Map each Say It state to the mic | Four distinct states, none red: Idle, Listening, Heard (speech detected), Result (correct or try again); after the third miss the mic rests | Idle → IDLE; listening without speech → LISTENING; with speech → HEARD; correct → RESULT_CORRECT; wrong → RESULT_TRY_AGAIN; after the third miss → DONE | {{tc: MicStatusTest.*}} |
| **TC-SAY-01** | FR-03.4 | State transition | First miss: the child says the letter's name ("em") | Spoken correction for the error type: "That's the letter's name. Its sound is /m/", re-model, "Your turn" | Plays the soft sound, `fb_letter_name`, `fb_its_sound_is`, the sound, the word and `car_your_turn`; tutor action is a level-1 correction of type LETTER_NAME; Next stays locked | {{tc: SayItViewModelTest.firstLetterNameMiss_playsLetterNameCorrection, TutorPolicyTest.firstMiss_correctsAtLevel1_withErrorType}} |
| **TC-SAY-02** | FR-03.4 | State transition | First miss: an added vowel ("ma"); or another word | Added vowel: "Almost! Just /m/, no 'ah'" with a re-model; another word: "Listen" and a re-model | The added-vowel correction (`fb_no_ah`) plays; another word gets the "Listen" re-model | {{tc: SayItViewModelTest.addedVowelError_triggersFbNoAhSpokenCorrection, SayItViewModelTest.otherWordMiss_playsListenRemodel}} |
| **TC-SAY-03** | FR-03.4, NFR-ACC-01 | State transition | Second miss | Slower re-model with the mouth-shape cue | No cue after the first miss; the cue shows after the second, with "Watch my lips"; level-2 correction | {{tc: SayItViewModelTest.secondMiss_showsMouthCue, SayItViewModelTest.secondMiss_playsWatchMyLips, TutorPolicyTest.secondMiss_correctsAtLevel2}} |
| **TC-SAY-04** | FR-03.4 | State transition, negative | Third miss, then tap the mic again | Together-practice ("Let's say it together"), the lesson moves on without a failure screen, the miss is saved; the mic does not reopen | Plays `car_lets_say_together`, the word, `fb_try_later`; Next unlocks; 3 incorrect attempts saved; the mic shows DONE; a later tap does not start the recognizer | {{tc: SayItViewModelTest.thirdMiss_emitsLeadAndMoveOn_andSavesIncorrect, SayItViewModelTest.thirdMiss_micRestsAndNextUnlocks, SayItViewModelTest.afterLeadAndMoveOn_startListeningIsIgnored, TutorPolicyTest.thirdMiss_leadsAndMovesOn}} |
| **TC-SAY-05** | FR-03.5 | Boundary | Wrong answers, including three in a row | Say It never removes a heart | Hearts after a wrong answer and after the third miss equal the hearts before | {{tc: SayItViewModelTest.evaluateSpeech_incorrectTranscript_doesNotDeductHeart, SayItViewModelTest.thirdMiss_emitsLeadAndMoveOn_andSavesIncorrect}} |
| **TC-SAY-06** | FR-03 | Functional | Say the key word, first try or after a miss | Praise ("Yes!") and Next unlocks | State Correct; praise action; Next available after a miss too | {{tc: SayItViewModelTest.evaluateSpeech_correctTranscript_setsCorrectState, SayItViewModelTest.correctAfterMiss_praises_andCanContinue, TutorPolicyTest.correct_anyAttempt_praises}} |
| **TC-ASR-07** | NFR-ASR-01.4 | Invalid input | Foils: "em", "es", "bee", "zee", "ay", "m"; "ma", "muh", "sa", "buh"; "pa" for /f/, "ba" for /v/, "sa" for /z/ | Every foil is rejected and labelled LETTER_NAME, ADDED_VOWEL or SUBSTITUTION | All 13 foils rejected with the expected error type; the ViewModel shows the matching state | {{tc: SpeechValidatorTest.judgeSound_letterName_rejected, SpeechValidatorTest.judgeSound_addedVowel_rejected, SpeechValidatorTest.judgeSound_substitution_rejected, SayItViewModelTest.evaluateSpeech_letterName_setsIncorrectWithLetterName, SayItViewModelTest.evaluateSpeech_addedVowel_setsIncorrectWithAddedVowel}} |
| **TC-ASR-08** | FR-03.6 | Invalid input, equivalence | Word mode: the 26 key words; and "m", "muh", "mmm", "mous", "elphant", "sunny", "sub" for "sun", "box" for "fox" | Each key word is accepted (any case or spacing); letter sounds, partial or misspelled words and other words are rejected | 26 of 26 key words accepted; all wrong forms rejected; the grammar holds the word and its foils, not generic decoys | {{tc: SpeechValidatorTest.wordExactMatch_returnsTrue_forAll26ExampleWords, SpeechValidatorTest.wordMode_caseAndWhitespaceInsensitive, SpeechValidatorTest.wordMode_rejectsLetterSoundOnly, SpeechValidatorTest.wordMode_rejectsPartialOrMisspelledWords, SpeechValidatorTest.wordMode_rejectsOtherWordsAndMinimalPairs, SpeechValidatorTest.grammarFor_wordMode_hasFoilsNotGenericDecoys}} |
| **TC-ASR-09** | FR-03.4 | Missing input | Null, empty or blank transcript | Not accepted (counts as a miss) | Rejected in both letter and word mode | {{tc: SpeechValidatorTest.nullOrBlankText_returnsFalse, SpeechValidatorTest.wordMode_nullOrBlankTranscript_returnsFalse}} |
| **TC-ASR-10** | FR-03.4 | Timeout | A wrong partial result ("em", "ma") with no final result | A partial does not end the attempt (it may be the start of the word); without a final result the attempt is judged at the timeout | After wrong partials the mic keeps listening and nothing is scored; at the 3.8 s auto-stop "ma" is judged ADDED_VOWEL; a partial followed by the right word is correct | {{tc: SayItViewModelTest.onResult_partialFoil_keepsListening, SayItViewModelTest.onResult_partialAddedVowelThenTimeout_setsIncorrectWithAddedVowel, SayItViewModelTest.onResult_partialAddedVowelThenFinalWord_isCorrect, SayItViewModelTest.onResult_partialLetterNameThenFinalWord_isCorrect}} |
| **TC-ASR-11** | FR-03 | Error guessing | The system stops the recognizer while listening (app sent to the background) | Return to Idle; the dropped attempt is not scored | Mic IDLE, state Idle, no attempt recorded, and the cancelled timeout does not judge it later | {{tc: SayItViewModelTest.recognizerStoppedExternally_returnsToIdle}} |
| **TC-ASR-12** | FR-03 | Incorrect action | Tap "hear the word" three times quickly | The word plays once | One playback | {{tc: SayItViewModelTest.playWordAudio_rapidTaps_debounced}} |
| **TC-ASR-13** | FR-03.6 | Configuration | Default scoring mode | Pure-sound scoring is off (the Vosk spike could not confirm a pure sound); a held sound alone is never accepted while off | Sound mode off by default; a sustained sound is "unconfirmed"; the sound-mode grammar accepts nothing | {{tc: SpeechValidatorTest.soundMode_isOffByDefault, SpeechValidatorTest.judgeSound_soundModeOff_sustainedIsUnconfirmed, SpeechValidatorTest.grammarFor_soundMode_hasNoAcceptedTokens}} |

### 4.3 Module 3: Phoneme Discrimination (Find It)

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-FND-01** | FR-04 | Functional | Open Find It for M | 5 cards: exactly 3 targets and 2 distractors | 5 cards, 3 correct and 2 distractors | {{tc: GridGeneratorTest.generate5ItemGrid_returnsExactly5ItemsWith3CorrectAnd2Distractors, FindItViewModelTest.loadGrid_validId_loadsTargetAnd5ItemGrid}} |
| **TC-FND-02** | FR-04 | Functional | Generate 200 grids for every letter | No distractor starts with the target's sound (c, k and q count as one sound); no distractor from the x, ng or ñ banks | 0 violations | {{tc: GridGeneratorTest.distractors_neverShareTheTargetSound, GridGeneratorTest.distractorLettersFor_k_excludesSameSoundGroup, GridGeneratorTest.distractors_neverFromSpecialBanks}} |
| **TC-FND-03** | FR-04 | Functional | Open Find It for letter 1 (M) and letter 2 (S) on a new profile | Distractors come "strictly from previously mastered letters" (SRS FR-04; `01_REQUIREMENTS_SUMMARY.md`) | **Not met.** `generate5ItemGrid` ignores the list of letters it is given and draws distractors from every curriculum letter. The captures in Figure 3 show M with Elephant, Orange, Rabbit, Sun, Jug, Yoyo and Pig, none of them taught yet. No test covers this rule. | Code inspection: `GridGenerator.kt` lines 62–98; `FindItViewModel.kt` line 123; Figure 3 | **FAIL (DEF-01)** |
| **TC-FND-04** | FR-05 | Functional | Tap a distractor | Exactly 1 heart lost, a gentle sound, the miss saved | Hearts 5 → 4; state Incorrect; attempt saved as incorrect | {{tc: FindItViewModelTest.selectPictureItem_incorrectItem_deductsHeart, HeartManagerTest.deductHeart_decrementsCountAndIncrementsHeartsLost}} |
| **TC-FND-05** | FR-05 | Boundary | 5 wrong taps; then one more deduction | At 0 hearts the activity stops (game over); hearts never go below 0 | Hearts 0, state GameOver; a further deduction keeps 0 | {{tc: FindItViewModelTest.selectPictureItem_allHeartsLost_setsGameOver, HeartManagerTest.deductHeart_signalsGameOverWhenZero}} |
| **TC-FND-06** | FR-05 | Boundary | Restart after depletion | Reload with a 3-heart buffer; the hearts lost before the restart still count for stars | Hearts 3, found count 0, not game over; the session's hearts lost survive the restart | {{tc: FindItViewModelTest.restartSession_resetsHeartsAndState, HeartManagerTest.restartAtReducedPoolOnDepletion, FindItViewModelTest.sessionHeartsLost_survivesRestart, HeartManagerTest.sessionHeartsLost_survivesRestart, FindItViewModelTest.wrongTaps_countIntoSessionHeartsLost}} |
| **TC-FND-07** | FR-07 | Boundary | 1 wrong tap, then 3 right taps in a row; and 3 right taps at a full pool of 3 | +1 heart per 3 in a row, never above the session's starting pool | Hearts 4 → 5 after three in a row; a full pool of 3 stays 3 | {{tc: FindItViewModelTest.threeCorrectInARow_recoversAHeart, HeartManagerTest.checkRecovery_addsHeartEveryThreeConsecutiveCorrect, HeartManagerTest.recoveryCapsAtStartingPool_recommendedRule}} |
| **TC-FND-08** | FR-05 | Negative | Open Find It, Say It or Letter Complete with an unknown letter ID (999) | No crash; a load error is shown | Target null and load error set; same in Say It and Letter Complete | {{tc: FindItViewModelTest.loadGrid_invalidId_setsLoadError, SayItViewModelTest.loadPhoneme_invalidId_setsLoadError, LetterCompleteViewModelTest.completeLesson_handlesMissingPhoneme_andSetsLoadError}} |
| **TC-FND-09** | FR-04 | Functional | Find all 3 targets | The lesson completes and the next cue plays | Found count 3, lesson complete, next cue appended | {{tc: FindItViewModelTest.selectPictureItem_correctItem_incrementsFoundCount, FindItViewModelTest.selectPictureItem_allThreeCorrect_completesLesson, FindItViewModelTest.completion_appendsNextCue}} |

### 4.4 Module 4: Word Construction (Blend It)

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-BLD-01** | FR-13.1–2 | Data validation | Check every seeded word against the letters taught by its group | 100% decodable with the letters taught so far; AIM, BEE, TOY, BOY, ZOO gone; AM, SUM, TUB, YAM, ZIP present in the same slots; QUIZ the one documented exception | 33 words (3 in group 1, then 5 per group); 0 untaught letters except QUIZ; the 5 old words absent and the 5 new ones present | {{tc: BlendItWordSeedsTest.everyWordUsesOnlyTaughtLetters, BlendItWordSeedsTest.nonDecodableWordsAreGone, BlendItWordSeedsTest.replacementsKeepTheirSlots, BlendItWordSeedsTest.thirtyThreeWords_threeThenFivePerGroup, BlendItWordSelectorTest.*}} |
| **TC-BLD-02** | FR-13.3 | Incorrect action | Deal the tiles 200 times for SAM, SIS, AM, TUB, YAM, ZIP, QUIZ, MOM | Never dealt in the answer's order; always the same letters | 1,600 deals, none solved, none with a letter lost or added; a word with one possible order (MM) keeps it | {{tc: BlendItTileDealTest.*}} |
| **TC-BLD-03** | FR-13, FR-05 | Incorrect action | Submit M-A-S for SAM | One heart lost; a soft "not yet" sound, never a buzz; the miss saved | Hearts −1; 1 wrong attempt; state WordIncorrect; the pop plays and the buzz does not | {{tc: BlendItViewModelTest.submitWord_incorrectWord_deductsHeart, BlendItViewModelTest.wrongSubmit_playsSoftPop_notBuzz}} |
| **TC-BLD-04** | FR-13 | Incorrect action | Two wrong submissions, then the hint | The first letter is locked in place | 1 locked hint; the first placed tile is S | {{tc: BlendItViewModelTest.applyHint_afterTwoMistakes_locksCorrectCharacter}} |
| **TC-BLD-05** | FR-13, FR-05 | Boundary | 5 wrong submissions, then restart | At 0 hearts the depleted state shows; the restart gives 3 hearts and keeps the 5 lost for stars | Hearts 0 → HeartDepleted → restart: 3 hearts, word 1, Idle; result reports 5 hearts lost | {{tc: BlendItViewModelTest.heartDepleted_restartsWithThreeHearts}} |
| **TC-BLD-06** | FR-13, NFR-AUD-01 | Functional | Resolve the AM, TUB and YAM word clips | The released WAV clips (audio release `2026-10-11-words`) exist and are used | The three WAV files exist with content; the seeds point at them | {{tc: AudioResolverTest.releasedWords_filesExist, AudioResolverTest.releasedWords_useWav, BlendItWordSeedsTest.releasedWordsPointAtTheirWav}} |
| **TC-BLD-07** | FR-13 | Functional | A correct word | Each tile's sound plays in turn before the whole word; the next word follows | Every tile lights and sounds before the word; the word advances | {{tc: BlendItViewModelTest.soundOut_waitsForEachSound, BlendItViewModelTest.soundOut_lightsAllTilesBeforeWord, BlendItViewModelTest.submitWord_correctWord_transitionsToWordCorrectAndAdvances}} |

### 4.5 Module 5: Progression, Gamification and Profiles

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-PRG-01** | FR-06 | Decision table | Finish a letter with 0, 1, 2, 3 and 4 hearts lost; carry the count through navigation | 0 lost: 3 stars; 1–2 lost: 2 stars; 3 or more: 1 star; the count reaches Letter Complete; 1 star says "You finished", not "mastered" | 0 → 3, 1 → 2, 2 → 2, 3 → 1, 4 → 1; the route carries the count; the 1-star message is "You finished Letter M!" | {{tc: StarCalculatorTest.*, LetterCompleteViewModelTest.completeLesson_calculates3StarsFor0HeartsLost_andSavesProgress, LetterCompleteViewModelTest.completeLesson_calculates2StarsFor1HeartLost, LetterCompleteViewModelTest.heartsLostArg_threeOrMore_givesOneStar, RoutesTest.letterComplete_carriesHeartsLost, LetterCompleteMessageTest.*}} |
| **TC-PRG-02** | FR-06 | Decision table | Find It with 1 wrong tap (3 correct of 4 taps = 75% accuracy) | SRS FR-06: 2 stars need at least 80% accuracy, so 75% gives 1 star | **Not met.** Letter Complete passes only hearts lost to `StarCalculator`, whose accuracy defaults to 100%, so this case gets 2 stars; a test (`completeLesson_calculates2StarsFor1HeartLost`) expects 2. With 3 targets, 1 wrong tap is always 75%, so the SRS rule as written never gives 2 stars in Find It. | Code inspection: `LetterCompleteViewModel.kt` line 102, `StarCalculator.kt` | **FAIL (DEF-02)** |
| **TC-PRG-03** | FR-08 | Functional | Letter 2 with letter 1 incomplete, then complete | Letter N+1 stays locked until letter N is complete; Blend It opens when its group is complete | Locked, then unlocked; group and Blend It unlock follow the same rule | {{tc: UnlockManagerTest.*, GroupUnlockManagerTest.*, MapTapRulesTest.lockedNode_staysLocked}} |
| **TC-PRG-04** | FR-09 | Boundary | Activity on the first day, the same day, the next day, after 50 hours | First activity: streak 1; same day: unchanged; next day: +1; after a missed day: back to 1 on activity, 0 at the inactivity reset, stars untouched; badges at 5, 10, 15, 20 days | All as expected; 15 stars kept after the reset | {{tc: StreakTrackerTest.*}} |
| **TC-PRF-01** | FR-14.1 | Boundary | 5 profiles, then 6 | A new profile can be added below 6 and not at 6 | `canAddProfile` true at 5, false at 6 | {{tc: ProfileViewModelTest.canAddProfile_returnsTrueWhenUnderLimit_andFalseWhenAtMax}} |
| **TC-PRF-02** | FR-14 | Invalid input | Rename to "   " and to a 17-character name; rename with spaces around | Blank or longer than 16 characters is ignored; a valid name is trimmed | No update for the two invalid names; the valid one saved trimmed | {{tc: ParentDashboardViewModelTest.renameProfile_blankOrTooLong_isIgnored, ParentDashboardViewModelTest.renameProfile_updatesTrimmedName}} |
| **TC-PRF-03** | FR-14, NFR-IND-01 | Functional | Create a profile without typing a name; delete the active, another and the last profile | The child never has to type (the avatar's name is used); deleting the active profile ends its session; deleting the last shows "no profiles" | Avatar name used for a blank name; sessions and the "no profiles" state as expected | {{tc: ProfileViewModelTest.createProfile_blankName_usesAvatarName, ParentDashboardViewModelTest.deleteActiveProfile_clearsSession, ParentDashboardViewModelTest.deleteOtherProfile_keepsSession, ParentDashboardViewModelTest.deleteLastProfile_setsNoProfilesLeft}} |
| **TC-DB-03** | FR-14.2, 14.4 | Data isolation | Two profiles with their own lesson and Blend It progress (in-memory Room database) | Each profile's totals count only its own progress | Ana 2 stars, Ben 4 stars, in both queries. Only star totals are covered; concurrent writes and the other per-profile tables are not | {{tc(PARTIAL): ProfileDaoStarsTest.*}} |

### 4.6 Module 6: Parent Zone

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-PZ-01** | FR-10 | Unauthorized access | Answer the arithmetic gate with the right answer (with spaces), a wrong one, blank, "abc", "12a" | Only the right number opens the Parent Zone | Right answer (trimmed) accepted; the other four rejected | {{tc: ArithmeticGateManagerTest.*}} |
| **TC-PZ-02** | FR-12 | Functional | Export the PDF report; and an export that fails | Success and failure are both reported to the parent; no crash | Export status set for both | {{tc: ParentDashboardViewModelTest.exportPdfReport_success_updatesExportStatus, ParentDashboardViewModelTest.exportPdfReport_failure_updatesExportStatus}} |
| **TC-PZ-03** | NFR-AUD-01.1 | Compliance | Read the Parent Zone credit | Names ElevenLabs and "elevenlabs.io" (Free-plan terms for the AM, TUB, YAM and /i/ clips) | Credit present with the domain | {{tc: VoiceCreditTest.creditNamesElevenLabsAndItsDomain}} |
| **TC-PZ-04** | FR-10 | Functional | Open the dashboard with profiles, with none, and switch profile | The first profile's data loads; an empty list ends loading; switching loads the selected data; errors are handled | All four as expected | {{tc: ParentDashboardViewModelTest.init_selectsFirstProfileAndLoadsDashboardData, ParentDashboardViewModelTest.init_withEmptyProfiles_setsLoadingFalse, ParentDashboardViewModelTest.selectProfile_loadsDataForSelectedProfile, ParentDashboardViewModelTest.selectProfile_handlesDashboardDataError}} |

### 4.7 Cross-Cutting Requirements

| TC ID | Req. | Type | Input / steps | Expected result | Actual result | Evidence | Status |
|---|:---:|---|---|---|---|---|---|
| **TC-IND-01** | NFR-IND-01.4 | Timeout | No touch for 9 s, touch, then 10 s; audio still playing; screen hidden | Re-prompt after 10 s without input; a touch restarts the clock; no re-prompt while Lily is speaking or the screen is hidden; at most 3 re-prompts | No nudge at 9 s after a touch; 1 nudge at 10.001 s; none during 30 s of playing audio; none when hidden; stops after the maximum; same in Hear It, Find It and Blend It | {{tc: IdleTimerTest.*, SayItViewModelTest.idle_tenSecondsWithoutTouch_nudgesOnce, SayItViewModelTest.idle_waitsWhileTheModelPlays, SayItViewModelTest.idle_noNudgeOnceHidden, HearItViewModelTest.idle_nudgesTheScreen, FindItViewModelTest.idle_replaysIntro, BlendItViewModelTest.idle_replaysIntro}} |
| **TC-IND-02** | NFR-IND-01.2, FR-01 | Completeness | Look for every spoken prompt the lessons use | Every carrier, key word, Say It word prompt and lesson line exists on disk, so no step is silent | All required files present: the 26 key words, the Say It prompts, the approved tutor carriers, the lesson lines in WAV | {{tc: AudioCompletenessCheckTest.*, AudioResolverTest.lessonVo_allReleasedLinesUseWav}} |
| **TC-ACC-01** | NFR-ACC-01.1 | Completeness | Look up the mouth shape for all 26 letters | Every letter has a mouth-shape cue, and the picture is released | 26 of 26 mapped to the right group (lips together, teeth on lip, no round lips for short vowels); every cue's picture is in an image release | {{tc: ArticulationGroupTest.*, PictureAssetsTest.everyMouthCueIsReleased, ArticulationCueTest.*}} |
| **TC-ACC-03** | NFR-ACC-01 | Error guessing | Move to the next letter on a lesson screen | The new letter's picture shows, not the last one; a missing picture is blank, not a crash | The new picture replaces the old; a missing asset renders transparent | {{tc: AssetImageLoadTest.*}} |
| **TC-ACC-02** | NFR-ACC-02 | Boundary | Measure tiles, mic, main buttons, map buttons and dialog closes; map text sizes | Every child-facing target at least 64 x 64 dp; text at least 16 sp | Design tokens at least 64 dp on all profiles; map back and guidebook buttons and the two dialog closes at least 64 dp; no map text under 16 sp; the header card is Lily's 64 dp tap target | {{tc: DimensTest.*, LayoutMatrix*.map_buttonsAre64dp, TopStatsBarLayoutTest.nodeDialog_closeIs64dp, TopStatsBarLayoutTest.guidebookDialog_closeIs64dp, TopStatsBarLayoutTest.card_isLilysTapTarget, MapTextFloorTest.noMapTextUnder16sp}} |
| **TC-ACC-04** | NFR-ACC-02 | Boundary | A 16-character learner name on the map header | The greeting, letter count and pills are not cut | Not ellipsized; pills visible | {{tc: TopStatsBarLayoutTest.longName_greetingIsNotCut, TopStatsBarLayoutTest.longName_pillsStayVisible, TopStatsBarLayoutTest.lettersCount_isNotCut, MapTapRulesTest.greeting_fitsOneLine}} |
| **TC-POL-03** | NFR-ACC-02 | Accessibility | Reduced motion on (system setting) | Animations become fades or stop; nothing moves by itself | Splash, motion tokens and heart wobble are still with reduced motion | {{tc: SplashScreenTest.motionPolicy_followsReducedMotion, PlayItMotionTest.reducedMotionSnaps, FeedbackEffectsTest.heartWobble_reducedMotionIsStill}} |
| **TC-AUD-03** | NFR-AUD-01.4 | Inspection | Compare every clip in the 7 audio release manifests with the app's file (SHA-256) | Every released clip the app ships is byte-identical to its release | 76 released clips: 69 identical (PASS), 0 different, 7 approved but not used by APK H (no reference in the code). 159 shipped files predate the release gate (the MVP clips the SRS lists as pending) | `docs/std/evidence/2026-10-14-0320385/audio_release_check.txt` (`tools/docs/check_audio_release.py`) | **PASS** (69 of 69 shipped clips) |
| **TC-AUD-04** | FR-04, NFR-ACC-01 | Inspection | Compare every released picture with the app's file (SHA-256); every grid picture exists | Identical bytes; no missing picture | All identical; all grid pictures present | {{tc: PictureAssetsTest.releasedPicturesMatchManifest, PictureAssetsTest.everyGridPictureExists}} |
| **TC-SEC-01** | NFR-SEC-01 | Inspection | `aapt dump permissions` on the submitted APK | No `android.permission.INTERNET`; only the microphone (and legacy storage, capped at old Android versions) | Permissions: `RECORD_AUDIO`; `READ_EXTERNAL_STORAGE` (max SDK 32); `WRITE_EXTERNAL_STORAGE` (max SDK 28); the app's own receiver permission. **No INTERNET** | `docs/std/evidence/2026-10-14-0320385/apk_inspection.txt` | **PASS** |
| **TC-SEC-02** | NFR-SEC-01 | Inspection | Read the manifest and backup rules | Learner data is not copied off the device by Android backup | `allowBackup="false"`; the backup rules exclude the database (and all other app data); the data-extraction rules name the database | {{tc: ManifestPrivacyTest.backupIsDisabled}} |
| **TC-POL-01** | Team policy | Inspection | Scan every `.kt` and `.xml` file in `app/src/main` | No emoji anywhere in the app | 0 violations | {{tc: ZeroEmojiPolicyTest.noEmojiInAppSources}} |
| **TC-POL-02** | Team policy | Inspection | Scan the code | No haptics; portrait only; no endless idle animation on the letter card or mascot header | 0 haptic calls; portrait locked; no endless idle motion | {{tc: PerformancePolicyTest.*}} |

### 4.8 Test Cases Not Run on This Build

| TC ID | Req. | What it checks | Why it did not run | When and how (SPMP) |
|---|:---:|---|---|---|
| **TC-AUD-01** | FR-02 | SoundPool start latency ≤ 50 ms | Needs instrumented timing on the phone | Device test before Round 2 (Milestone 5) |
| **TC-AUD-02** | FR-02, NFR-AUD-01.3 | Zero schwa: 26 of 26 sounds rated "Pure" by ≥ 3 of 4 teachers | Gate 3 teacher audit not yet held; Chapter 1's short /a/ and Chapters 2–7 still use MVP clips | SPMP Milestone 4 (Week 6) planned the audit; not yet held. Instrument A, Part 1 |
| **TC-SCR-01** | FR-02.5 | Modeling sequence ≤ 15 s ([Planned — Round 2]) | Needs timing on the device | Instrument D (D-11), Round 2 |
| **TC-MIC-01** | FR-03.3, NFR-PERF-01.3 | Tap-to-Listening ≤ 100 ms | Needs timing on the device | Device test (Instrument D) |
| **TC-MIC-02** | FR-03.2 | Ripple follows the voice's loudness | Planned (`AudioRecord` loop, SDD §3.2); the ripple is time-based in APK H | After Round 2 (team decision 2026-10-06) |
| **TC-ASR-01 to 05** | NFR-ASR-01.1, 3 | ≥ 80% agreement with teachers across child voices and classroom noise | Needs recorded child attempts and teacher ratings | Instruments B and C, Round 2 (Weeks 7–8) |
| **TC-ASR-06** | NFR-ASR-01.2 | False rejects ≤ 15% of teacher-correct attempts | As above | Round 2 |
| **TC-PERF-01, 02** | NFR-PERF-01 | Say It P90 ≤ 0.5 s; Find It P90 ≤ 0.3 s | Needs the telemetry log (planned) on the device | After Room schema v4; Instrument D (D-01, D-02) |
| **TC-IND-03** | NFR-IND-01.1 | ≥ 85% of lessons completed without adult help | Field measure | Silent-Observer protocol, Round 2 |
| **TC-REV-01 to 03** | FR-NEW-REC | Warm-up and end-of-session recall checks | `LearnerModel` and the review scheduler are planned (SDD §2.0) | After Round 2 |
| **TC-TEL-01** | FR-NEW-TEL | Telemetry schema | `TelemetryEventEntity` and `TelemetryLogger` are planned (Room schema v4) | SPMP Milestone 4 (Week 6) planned it; not in APK H, to be re-scheduled |
| **TC-EXP-01, 02** | FR-NEW-TEL.4 | PIN-gated CSV export; zero network transmission | `CsvExportManager` is planned. (The app already has no INTERNET permission: TC-SEC-01.) | SPMP Milestone 4 (Week 6) planned it; not in APK H, to be re-scheduled |
| **TC-DB-01** | NFR-REL-01 | 20 force-close tests with no progress lost | Needs the device and the planned transactional DAOs | Instrument D (D-05, D-06) |
| **TC-DB-02** | NFR-REL-01 | Migration from schema v3 to v4 keeps data | Schema v4 is planned; `exportSchema` and explicit migrations are prerequisites (SDD §6) | With schema v4 |
| **TC-SES-01** | NFR-SES-01 | Session ends at the first completed node after about 12 minutes | [Planned — Round 2]; not built in APK H | Round 2 implementation |

---

## 5. Test Execution

### 5.1 Execution Record
{{RUN_SUMMARY}}

### 5.2 Results by Test Class
Every test class of the suite, the feature and requirement it verifies, and its result. The per-test log is in Appendix A.

{{CLASS_TABLE}}

### 5.3 Skipped Tests
The 6 skips are the font-scale 1.3 checks on the 360x640 profile (`fontScale13_mainActionVisible`, `sayIt_micVisible_fontScale13`, `findIt_fontScale13`, `complete_continueVisible_fontScale13`, `splash_startVisible_fontScale13`, `nameprompt_letsPlayVisible_fontScale13`). They skip by design through `assumeFontScaleChecks()`: a 360x640 screen at 130% text is below the supported floor. The same 6 checks pass on the other three profiles (§6.2).

### 5.4 Inspections
**APK permissions (TC-SEC-01).** `tools/docs/inspect_apk.py` ran `aapt dump permissions` on the submitted file on October 18, 2026 (full log in `docs/std/evidence/2026-10-18-submitted-apk/apk_inspection.txt`); the run of October 14 on the earlier build of the same commit gave the same permissions (`docs/std/evidence/2026-10-14-0320385/apk_inspection.txt`). Output:

```
package: com.playit.app
uses-permission: name='android.permission.RECORD_AUDIO'
uses-permission: name='android.permission.READ_EXTERNAL_STORAGE' maxSdkVersion='32'
uses-permission: name='android.permission.WRITE_EXTERNAL_STORAGE' maxSdkVersion='28'
permission: com.playit.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
uses-permission: name='com.playit.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION'
SHA-256 b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4
package com.playit.app, versionName 1.0.0, minSdk 26, targetSdk 34
```

**Audio releases (TC-AUD-03).** `tools/docs/check_audio_release.py` read the 7 release manifests in `docs/audio-release/` (76 clips) and compared each with the app's file by SHA-256: 69 identical, 0 different, 7 approved but not used by APK H. Of the 228 audio files the app ships, 69 come from a release and 159 are MVP clips from before the release gate (words 115, phonemes 29, UI lines 15); the SRS lists their remake and teacher audit as pending.

### 5.5 Device Tests on the Samsung Galaxy A21s
The team played each candidate build on the test phone as a child would. These sessions are exploratory, not scripted; what they found is in §7.1.

| Date | Build | What was checked | Result |
|---|---|---|---|
| October 10, 2026 | APK B | One letter end to end; the Hear It sequence; captions | 7 changes settled (card 28, `ac8e8c7`), among them: Hear It ends on the sound; Say It never traps the child; full Blend It sound-out; live star totals; one-tap map start; captions out of the child view (team decision) |
| October 11, 2026 | APK D | Section 1 on the A21s | INC-01, INC-02, INC-03 |
| October 11, 2026 | APK E | Section 1 after card 28b | INC-04; the mouth cue moved to Say It only (card 28c) |
| October 18, 2026 | Submitted APK (`b5a54c18`) | Acceptance: install on Samsung Galaxy A21s, open splash screen, verify map header, play Letter M through Hear It, Say It, Find It, and verify Letter Complete | PASS. Application installed cleanly, responsive audio playback, mic permission requested and handled, lesson progression verified on hardware. |

---

## 6. Layout Verification

### 6.1 Method
`LayoutMatrixTest` renders the real screens with Robolectric on the 4 profiles of §2.5 and checks positions with unclipped bounds, so a control that is pushed below the window or behind another one fails. The map uses a 16-character learner name ("Maximilianoooooo"); the font-scale checks use 130% text. Each check also saves a capture (Roborazzi) for a visual review. {{COUNT:layout_checks}} checks on 4 profiles make {{COUNT:layout_runs}} runs: {{COUNT:layout_passed}} passed and 6 skipped by design.

### 6.2 Results of the Layout Checks
{{LAYOUT_TABLE}}

\* Skipped by design: 360x640 at font scale 1.3 is below the supported floor (§5.3).

### 6.3 Visual Review of the Captures
The figures put the same screen side by side on the 4 profiles at their true relative size (0.55 px per dp). They were captured on October 14, 2026 from the code of APK H.

![Splash (top) and map (bottom) on the 4 profiles. Start stays below Lily's face; the map header holds a 16-character name.](std/figures/layout_splash_map.png)

![Hear It while the sound plays (top) and Say It before an attempt (bottom).](std/figures/layout_hearit_sayit.png)

![Say It after two misses, with the mouth-shape cue beside the mic (top); Find It for the letter M (bottom).](std/figures/layout_sayit_cue_findit.png)

![Blend It with the tiles dealt out of order (top) and Letter Complete with 1 star (bottom).](std/figures/layout_blendit_complete.png)

**What the review found:**
- Every control the child needs is on screen on all 4 profiles, which matches §6.2.
- **Figure 3, Find It for M:** the distractors are Elephant and Orange (360x640), Rabbit and Sun (360x740), Sun and Jug (411x891), Yoyo and Pig (800x1280). M is the first letter, so none of these sounds has been taught. This is DEF-01.
- **Figures 2 and 3, Lily's speech bubble:** the Say It line ("Now it's your turn! Say the whole word clearly into the microphone!") is cut with "…" on 360x640 and on the A21s profile, and the Find It line is cut on 360x640. The line is also spoken, so a pre-reader loses nothing, but the cut text looks unfinished (OBS-02).
- **Figure 4:** the Blend It tiles are dealt out of order on every profile (A-M-S, M-S-A, A-M-S, M-A-S for SAM), as TC-BLD-02 requires.

---

## 7. Incident and Defect Report

Severity: **High** — a child cannot finish a lesson or data is lost; **Medium** — a requirement is not met but the lesson works; **Low** — cosmetic or a gap in the tests.

### 7.1 Incidents Found and Closed

| ID | Found (date, how, build) | Expected | Actual | Fix (commit) | Re-test evidence | Status |
|---|---|---|---|---|---|---|
| **INC-01** | Oct 11, phone test, APK D | Blend It plays the words AM, TUB and YAM | Silent: the clips were never released into the app | Audio release `2026-10-11-words` (ElevenLabs, after the Kokoro TUB takes failed review); card 15b, `b99d839` | TC-BLD-06; TC-AUD-03 | Closed |
| **INC-02** | Oct 11, phone test, APK D | Each letter shows the mouth shape that makes its sound | Several letters showed the wrong mouth shape | Letter-to-mouth pairs remapped; card 28b, `ed997b6` | TC-ACC-01 (`ArticulationGroupTest`) | Closed |
| **INC-03** | Oct 11, phone test, APK D | Unit banner title and Say It attempt dots fit on small phones | Title clipped; dots overlapped on 360x640 | Card 28b, `ed997b6` | `map_unitBannerTitleFits` on 4 sizes (§6.2) | Closed |
| **INC-04** | Oct 11, phone test, APK E | After "next letter" the card shows the new letter's picture | The mouse from M stayed on most letters of section 1 | Picture loader restarts on a new path; card 28d, `e7f870e` | TC-ACC-03 (`AssetImageLoadTest`) | Closed |
| **INC-05** | Oct 1, code review | Stars and hearts follow the child's results | Stars were always 3; hearts lost before a restart vanished; Blend It at 0 hearts showed nothing; 2 of 5 hearts were not drawn | Card 11, `0d9ad3a` | TC-FND-06, TC-BLD-05, TC-PRG-01 | Closed |
| **INC-06** | Oct 13, card 31e (correcting the card 30 audit, which had called the floor met) | Every child-facing control is at least 64 dp | Map back 46 dp, guidebook 44 dp, dialog closes 32 and 36 dp, dialog buttons 52–56 dp | Card 31e, `1420c84` | TC-ACC-02 (`map_buttonsAre64dp` on 4 sizes, `TopStatsBarLayoutTest`) | Closed |
| **INC-07** | Oct 13, UI audit (card 31) | Blend It never starts with the word already built | A random deal could match the answer | Card 31f, `0320385` | TC-BLD-02 | Closed |

### 7.2 Open Defects and Observations

| ID | Req. | Description (expected vs actual) | Severity | Recommended action | Owner |
|---|:---:|---|:---:|---|---|
| **DEF-01** | FR-04 | Expected: Find It distractors only from letters already mastered. Actual: drawn from every curriculum letter; `generate5ItemGrid` ignores the letters it is given. Letters 1 and 2 have no mastered letters, so the rule also needs the curated fallback that `01_REQUIREMENTS_SUMMARY.md` specified. | Medium | Draw distractors from the profile's completed letters; use a fixed fallback set for the first letters; add a test that a new profile's grid for S uses only M. | Developer (fix card) |
| **DEF-02** | FR-06 | Expected (SRS): 2 stars need at least 80% accuracy. Actual: stars use hearts lost only, so 1 wrong tap (75%) gives 2 stars. With 3 targets the SRS rule cannot give 2 stars in Find It at all. | Low | Team decision: either amend FR-06 to the hearts-lost rule the app uses (0 → 3, 1–2 → 2, 3+ → 1, as `01_REQUIREMENTS_SUMMARY.md` §7.4 flagged), or pass accuracy to `StarCalculator`. Then align the test. | Team, with the adviser |
| **OBS-01** | FR-03.4 | The SRS starts the prompt ladder after 3 s of silence; the app listens up to 3.8 s before judging. No test covers a fully silent attempt from end to end (TC-ASR-09 and TC-ASR-10 cover its parts). | Low | Amend FR-03.4 to 3.8 s, or shorten the timer; add a ViewModel test for silence. | Team |
| **OBS-02** | NFR-ACC-02 | Lily's speech bubble is cut with "…" on 360 dp wide screens (Say It on 360x640 and 360x740; Find It on 360x640). The line is spoken, so nothing is lost for a pre-reader. | Low | Card 31c shortens both lines ("Your turn! Say the word."; "Find the pictures that start with this sound."); it waits for the team lead's listening review. | Team lead (review) |
| **OBS-03** | FR-04 | `GridGeneratorTest.letterOne_usesFallbackDistractorsWhenNotEnoughMasteredLetters` tests `generateGrid`, which Find It no longer calls; it does not test the screen's grid. | Low | Retarget the test to `generate5ItemGrid` when DEF-01 is fixed, and remove the unused function. | Developer |
| **OBS-04** | NFR-SEC-01 | The missing INTERNET permission is checked by inspection only (TC-SEC-01); no automated test fails if a library adds it. | Low | Add a test on the merged manifest. | Developer |

---

## 8. Test Summary Report

### 8.1 Results in Numbers
- **Automated suite:** {{COUNT:tests}} test cases in {{COUNT:classes}} classes; **{{COUNT:passed}} passed, {{COUNT:failed}} failed, {{COUNT:skipped}} skipped by design** (§5).
- **Layout matrix:** {{COUNT:layout_checks}} checks on 4 profiles = {{COUNT:layout_runs}} runs; {{COUNT:layout_passed}} passed, 6 skipped by design (§6).
- **Test cases specified in this STD:** {{COUNT:tc_total}}.
  - **Pass:** {{COUNT:tc_pass}}, of which {{COUNT:tc_pass_inspection}} by inspection of the build.
  - **Partial:** {{COUNT:tc_partial}} (TC-DB-03).
  - **Fail:** {{COUNT:tc_fail}} (TC-FND-03 = DEF-01, TC-PRG-02 = DEF-02).
  - **Not run:** {{COUNT:tc_notrun}} (§4.8: field measures, device timing and planned features).
- **Incidents:** 7 found in testing and closed (§7.1); 2 open defects and 4 observations (§7.2).

### 8.2 Assessment
APK H does what the SRS requires for every behaviour that can be checked on the build, with two exceptions found while writing this document: Find It's distractor source (DEF-01, Medium) and the accuracy term of the star rule (DEF-02, Low). Neither stops a child from finishing a lesson or loses data, so neither blocks the midterm submission. Both are stated here rather than left for the adviser to find.

The automated tests prove the logic and the layout, not the experience. The requirements that matter most for the research objectives still need people and the phone: teacher agreement with the speech judge (TC-ASR-01 to 06), the purity of the sounds (TC-AUD-02), latency (TC-PERF), and independent completion (TC-IND-03). They are scheduled in Round 2 (SPMP Milestones 5 and 6). Three items the SPMP placed in Milestone 4 (Week 6) are not in APK H: telemetry, the CSV export and the Gate 3 teacher audit. Their test cases (TC-TEL-01, TC-EXP-01, TC-EXP-02, TC-AUD-02) move with them, and the latency measures (TC-PERF) depend on the telemetry, so the schedule needs re-baselining before Round 2.

### 8.3 Next Steps
1. Before turning in: run the APK H acceptance on the A21s (§5.5).
2. Write the fix card for DEF-01; take DEF-02 and OBS-01 to the team and the adviser as SRS amendments.
3. Add the tests named in OBS-03 and OBS-04.
4. Re-baseline the SPMP for the Milestone 4 items not yet built (telemetry, CSV export) and the Gate 3 teacher audit.
5. When cards 31b and 31c ship, re-run this suite and the layout matrix, and update this document (OBS-02 should close).
6. Round 2: run the test cases of §4.8 and record them in version 2.0 of this document (SPMP Milestone 7: "verified STD execution logs").

### 8.4 Sign-Off

| Role | Name | Signature | Date |
|---|---|---|---|
| Prepared by (test execution and write-up) | Capstone Team | | October 14, 2026 |
| Reviewed by (Team Lead) | Riva, Zendrix | ____________________ | October 18, 2026 |
| Adviser | Mr. Joemarie C. Amparo | ____________________ | |

---

## Appendix A. Test Execution Log
Every test case of the recorded run (§5.1), from the JUnit XML in `docs/std/evidence/2026-10-14-0320385/junit/`.

{{TEST_LOG}}

## Appendix B. How to Reproduce
```
git checkout 0320385                      # the code of debug APK H
gradlew cleanTestDebugUnitTest testDebugUnitTest
#   results: app/build/test-results/testDebugUnitTest/*.xml
gradlew recordRoborazziDebug --tests "com.playit.app.screenshot.*"
#   captures: app/build/outputs/roborazzi/*.png
python tools/docs/check_audio_release.py audio_release_check.txt
aapt dump permissions playit-debug-H-0320385.apk
python tools/docs/build_std.py --figures   # rebuilds docs/STD_v1.0.md from the logs
```
