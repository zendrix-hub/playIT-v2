# playIT: Week 3 Refactoring Change Set

## Inputs for SRS v3.0, SDD v2.0, and RTM v3.0

**Due:** September 26, 2026 (per the Weeks 1–2 Highlights) · **Status:** draft for team merge

This change set lists every requirement, design, and traceability change that follows from the Round 1 pilot and from a review of the project documents. Merge each item into your current SRS, SDD, and RTM. IDs FR-02, FR-03, FR-13, and FR-14 come from the Weeks 1–2 Highlights; IDs marked NEW take the next free number in your SRS. Test-case IDs are proposed and should be aligned with your STD. Items marked **[confirm]** or **[proposed]** need a team or adviser decision.

Priority scale: **P0 Urgent** (fix before any further testing) · **P1 High** (fix before Round 2) · **P2 Medium** (fix before feature freeze) · **P3 Future** (roadmap only).

---

## 1. Consistency Fixes Across All Documents

The proposal, the drafts, and the validation framework currently describe slightly different systems. Apply these fixes everywhere so the SRS, SDD, RTM, and validation report tell one story.

| # | Topic | Conflict now | Change to | Where |
|---|---|---|---|---|
| 1 | Speech engine | PocketSphinx (proposal, drafts) vs Vosk (tested build) | Vosk everywhere; mention PocketSphinx once as the original plan | SRS constraints, SDD, report |
| 2 | Curriculum | 28 letters (proposal, drafts) vs 26 letters (framework) | 26 letters in 7 chapters; Ñ removed; NG deferred as a digraph | SRS scope, feature lists, RTM, report |
| 3 | Blend It scope | Proposal excludes CVC decoding; the build has a 33-word Blend It | Blend It in scope (FR-13); update the exclusion list | SRS scope |
| 4 | ASR target | ≥80% (General Objective 2) vs ≥75% (RQ2, workflow, limitations) | One target: ≥80% agreement with teacher judgment **[confirm]** | SRS, research questions |
| 5 | Latency definition | "Within 0.5 s of microphone activation" (proposal) vs "from mic release to feedback" (framework) | From the end of the child's speech to feedback shown, P90 ≤0.5 s | SRS NFRs, test cases |
| 6 | Find It accuracy | "≥80% discrimination accuracy (3 out of 3 correct taps)" mixes two measures | Accuracy = correct taps ÷ total taps, target ≥80%; pass rule = all 3 targets found before hearts reach 0 | SRS Find It FR, RQ3 |
| 7 | Heart rule | 5 hearts, restart with 3 (proposal) vs "three-heart retry buffer" (Highlights) | State the implemented rule once and reuse it **[confirm]** | SRS, SDD, report |
| 8 | Pitch requirement | Reference tones within "±10 cents" | Remove (see Section 2.11) | SRS Say It |
| 9 | Rewards in RTM | Proposal RTM traces points and badges; the feature list says no points | Trace to hearts, stars, progress bars, unlockable levels, and streaks only | RTM |
| 10 | Dashboard | RQ5 cites a teacher dashboard; the scope excludes one | Reword RQ5 for the parent/guardian dashboard, or tie it to facilitator export | Research questions, RTM |
| 11 | Summative evaluation | The proposal's Week 6 plan still lists SUS ≥70 | Decide with the adviser whether SUS stays in the summative study | Evaluation plan |
| 12 | Name | playIT vs BasaTrack | playIT; mention BasaTrack once as the former name | All |
| 13 | Priority labels | Articulation cues P1 (Highlights) vs Medium (drafts) | P2 Medium in this change set **[confirm]** | SRS, RTM |

---

## 2. SRS v3.0 Changes

### 2.1 FR-02 Hear It: Pure Phoneme Model · Modified · P0

**Requirement.** The system shall play a pure phoneme model for each of the 26 letters. Continuous sounds (a, e, i, o, u, f, l, m, n, r, s, v, z) shall be held for about 800 ms. All other sounds (b, c, d, g, h, j, k, p, q, t, w, x, y) shall be produced short. No clip shall contain an added vowel or the letter name. Phoneme audio shall be stored separately from example-word audio, and the full Hear It playback for a letter shall last no more than 5 s.

**Acceptance.** Each re-mastered clip is rated Pure by at least 3 of 4 teachers in validation Instrument A; 26 of 26 clips pass.

**Source.** Round 1 PED-08 (2 of 4 teachers flagged pronunciation) and teacher comments.

**Note.** Stop sounds are the most likely to pick up an added vowel ("buh" for b). Have one team member screen every clip against this requirement before the teacher audit.

### 2.2 FR-03 Say It: Visible Microphone State · Modified · P1

**Requirement.** The Say It screen shall always show one of four microphone states: Idle, Listening, Processing, or Result. The state shall change to Listening within 100 ms of the child's tap **[proposed]**. While Listening, the mic button shall display a ripple whose size follows the live input level.

**Acceptance.** Tap-to-Listening ≤100 ms (Instrument D, check D-10). At least 85% of mic turns show no hesitation event (Instrument B) **[proposed]**.

**Source.** Round 1 facilitator notes on hesitation at the microphone.

### 2.3 NFR-ASR-01 Say It: Recognition Agreement · NEW · P1

**Requirement.** The Say It decision shall agree with a teacher's judgment of the child's production on at least 80% of attempts by Grade 1 learners **[confirm target]**. False-reject and false-accept rates shall be reported separately.

**Acceptance.** Agreement ≥80%, reported with a 95% Wilson interval, from at least 100 rated attempts (Instruments B and C).

**Source.** General Objective 2; untested in Round 1.

### 2.4 NFR-PERF-01 Feedback Latency · Modified · P1

**Requirement.** Say It feedback shall appear within 0.5 s after the end of the child's speech, and Find It tap feedback shall appear within 0.3 s of the tap, both measured at the 90th percentile.

**Acceptance.** Instrument D, checks D-01 and D-02.

**Source.** Proposal specific objectives and framework check SYS-01. This replaces "within 0.5 seconds of microphone activation," which cannot be met while the child is still speaking.

### 2.5 FR-13 Blend It: Decodable Word Bank · Modified · P1

**Requirement.** Every Blend It word shall be decodable using only the letter-sounds taught up to its chapter.

**Change.** Replace five words and decide on QUIZ.

| Chapter | Remove | Why | Replace with | Letters used (all unlocked) |
|---|---|---|---|---|
| 1 | AIM | vowel team *ai* | AM | a, m |
| 2 | BEE | vowel team *ee* | SUM | s, u, m |
| 3 | TOY | diphthong *oy* | TUB | t, u, b |
| 3 | BOY | diphthong *oy* | YAM | y, a, m |
| 7 | ZOO | vowel team *oo* | ZIP | z, i, p |
| 7 | QUIZ | *qu* = /kw/ | Keep only if q is taught as the unit "qu", flagged as a documented exception | q, u, i, z |

**Acceptance.** Every word is rated decodable by at least 3 of 4 teachers, or listed as a documented exception (Instrument A, Part 2).

**Source.** Post-pilot design review. The original constraint check confirmed that letters were unlocked, not that their taught sounds produce the word. Teachers should confirm the replacements; if Blend It shows pictures, prefer picturable words.

### 2.6 FR-14 Multi-Profile Support · Modified · P1 [confirm current wording]

**Requirement.** The system shall support multiple child profiles on one device, each with separate progress, hearts, stars, and telemetry, so one device can serve a classroom learning station.

**Acceptance.** TC-DB-03: actions under one profile never change another profile's data.

**Source.** Teachers' stated interest in learning stations (Round 1 PED-12) and FR-14 in the Highlights.

### 2.7 FR-NEW-TEL Telemetry Logging and Local Export · NEW · P1 (required before Round 2)

**Requirement.** The system shall log the events listed in SDD Section 3.4 to the local database as they occur and shall export them per profile as a CSV file from a PIN-protected screen, without any network access.

**Acceptance.** TC-TEL-01: every event type appears in a test export with correct fields. TC-EXP-01: export is blocked without the PIN. TC-EXP-02: no network request occurs during export.

**Source.** Round 1 could not measure agreement, latency, discrimination accuracy, or completion; teachers requested a batch export for classroom stations.

### 2.8 NFR-REL-01 Offline Persistence · Modified · P1

**Requirement.** The system shall save progress and telemetry after every scored interaction, so a force-close loses no completed interaction, and shall need no network connection at any point.

**Acceptance.** Instrument D, checks D-05 and D-06; TC-DB-01 and TC-DB-02.

**Source.** Offline persistence was observed in Round 1 but not formally tested.

### 2.9 NFR-ACC-01 Hearing Support · NEW · P2 [confirm priority]

**Requirement.** For every phoneme, Hear It and Say It shall show an on-screen sound caption (for example, "mmm" for m) and a visual articulation cue showing mouth or lip position.

**Acceptance.** Caption and cue present for 26 of 26 phonemes (Instrument D, check D-09).

**Source.** Round 1 ACC-06 (3 of 5 Yes).

**Note.** This helps children with mild hearing difficulties but does not make the app fully accessible to deaf learners. State that limit in the SRS scope.

### 2.10 NFR-ACC-02 Motor Access · Modified · P2

**Requirement.** Every interactive element, including corner menu toggles, shall have a touch target of at least 64 dp, with at least 8 dp between adjacent targets.

**Acceptance.** Instrument D, check D-07; TC-ACC-02.

**Source.** Round 1 ACC-07 (4 of 5 Yes) and the Highlights note on corner toggles.

### 2.11 Removed Requirement

Remove the Say It objective to "synthesize clear auditory reference tones ... with a maximum frequency deviation of ±10 cents." Cents measure musical pitch, and pitch does not decide whether a phoneme is correct. The FR-02 teacher audit replaces it.

---

## 3. SDD v2.0 Changes

### 3.1 AudioPlaybackManager (FR-02)

Load the current chapter's phoneme clips into a SoundPool when the chapter opens and release them when it closes, so short clips play without start-up delay. Store each re-mastered phoneme as its own WAV file in `res/raw` (for example, `ph_m.wav`) and store example-word audio separately (`ph_m_word.wav`), so each can be played and audited alone. Log a `hear_play` event on every playback.

### 3.2 SpeechRecognitionRepository and MicStateVisualizer (FR-03, NFR-ASR-01, NFR-PERF-01)

The Vosk recognizer runs off the main thread and exposes three streams to the Say It ViewModel: mic state, input level, and the recognition result with its confidence. Input level is the RMS of each audio buffer, normalized from 0 to 1. Vosk's `SpeechService` reads the microphone internally and reports only recognition results, so if the app uses it, switch to an `AudioRecord` loop in the repository that computes the RMS and passes the same buffer to `Recognizer.acceptWaveForm()`.

On tap, the ViewModel sets the state to Listening at once, before the recognizer finishes starting, which keeps the visible response under 100 ms. The `MicStateVisualizer` composable draws the ripple from a smoothed input level. Each attempt logs `mic_tap`, `speech_end`, `asr_result`, and `feedback_shown`.

### 3.3 Blend It Word Bank (FR-13)

Update the seeded word list with the replacements in Section 2.5 and add an exception flag for any documented exception, such as QUIZ. Apply the change through the schema v3 migration or a versioned re-seed so existing installs update.

### 3.4 TelemetryLogger and Room Schema v3 (FR-NEW-TEL, FR-14, NFR-REL-01)

Add a `Profile` entity (FR-14) and a `TelemetryEvent` entity with the fields below.

| Field | Type | Purpose |
|---|---|---|
| id | Long (auto) | Primary key |
| profileId | Long | Links the event to a child profile |
| sessionId | String | Groups events in one session |
| chapter, letter | Int, String | Curriculum position |
| module | Enum: HEAR, SAY, FIND, BLEND | Activity |
| eventType | String | One of the event types below |
| elapsedMs | Long | `SystemClock.elapsedRealtime()`; monotonic, used for latency |
| wallClock | Long | Epoch time; used to match sessions and observation sheets |
| value | String? | Result, such as correct or incorrect, or the recognized text |
| confidence | Float? | ASR confidence, if available |

Event types are `session_start`, `session_end`, `node_enter`, `node_complete`, `hear_play`, `mic_tap`, `speech_end`, `asr_result`, `feedback_shown`, `find_tap`, `find_feedback_shown`, `blend_submit`, and `heart_change`.

Insert each event with a suspend DAO call, which Room runs off the main thread. Write the migration from your current schema to v3 so existing progress survives the upgrade, and verify it with `MigrationTestHelper`. The CSV exporter reads events per profile, writes the file to app-specific storage, and shares it through the Android share sheet behind a 4-digit PIN.

### 3.5 ArticulationCue and Touch-Target Token (NFR-ACC-01, NFR-ACC-02)

Add an `ArticulationCue` composable that shows a per-phoneme mouth image or short animation with its caption during Hear It and after each Say It attempt, with assets mapped by phoneme ID. Define one dimension token for the minimum touch target (64 dp) and apply it to every interactive element, including corner toggles, through a shared modifier.

### 3.6 Roadmap (P3)

Document future chapters for consonant blends and digraphs (sh, ch, th) as a data-driven extension of the chapter model. No v2.0 code change is needed beyond keeping chapters and word banks in data rather than in code.

---

## 4. RTM v3.0 Rows

Each finding maps to a requirement, a design element, test cases, and a Round 2 check. Keep Round 1 item codes (PED, ACC, SMILEY) as evidence sources, and do not trace SUS items to any requirement. TC-ASR-01 to 05 keep the IDs already planned in the Highlights.

| ID | Finding | Evidence | Requirement | Design | Test cases (proposed) | Round 2 check (objective) | Priority |
|---|---|---|---|---|---|---|---|
| F-01 | Added vowel in phoneme clips | PED-08 (2 of 4); T-1, T-2 comments | FR-02 | AudioPlaybackManager; clip spec | TC-AUD-01 playback ≤5 s; TC-AUD-02 SoundPool preload | A Part 1 (O1) | P0 |
| F-02 | Hesitation at the Say It mic | Facilitator notes; ease top face 9 of 16 | FR-03 | MicStateVisualizer | TC-MIC-01 tap-to-Listening ≤100 ms; TC-MIC-02 ripple follows input | B, D-10 (O2c) | P1 |
| F-03 | ASR agreement untested | Round 1 gap | NFR-ASR-01 | SpeechRecognitionRepository | TC-ASR-01 to 05: child pitch range, noise up to 40 dB | B + C (O2a) | P1 |
| F-04 | Latency untested | Round 1 gap | NFR-PERF-01 | TelemetryLogger | TC-PERF-01 Say It P90; TC-PERF-02 Find It P90 | D-01, D-02 (O2b, O3) | P1 |
| F-05 | 6 of 33 Blend It words not decodable | Design review | FR-13 | Word bank seed | TC-BLD-01 seed matches approved list | A Part 2 (O4) | P1 |
| F-06 | No telemetry or export | Round 1 gap; teacher request | FR-NEW-TEL | TelemetryLogger; CSV exporter | TC-TEL-01; TC-EXP-01; TC-EXP-02 | C (O2 to O5) | P1 |
| F-07 | Stations need several profiles | PED-12 | FR-14 | Profile entity | TC-DB-03 profile isolation | — | P1 |
| F-08 | Persistence not formally tested | Round 1 observation | NFR-REL-01 | Save-per-interaction DAO; v3 migration | TC-DB-01 20 force-stops; TC-DB-02 migration keeps data | D-05, D-06 (O6) | P1 |
| F-09 | Hearing support gap | ACC-06 (3 of 5) | NFR-ACC-01 | ArticulationCue | TC-ACC-01 caption and cue for 26 of 26 | D-09 (O6) | P2 |
| F-10 | Small corner toggles | ACC-07 (4 of 5) | NFR-ACC-02 | Touch-target token | TC-ACC-02 all targets ≥64 dp | D-07 (O6) | P2 |
| F-11 | Advanced readers | Teacher comment | Roadmap only | SDD 3.6 | — | — | P3 |

---

## 5. Corrections to the Weeks 1–2 Highlights

If the Highlights stays on file or goes to the panel, fix these first.

| Section | Current text | Correction |
|---|---|---|
| 3A; 6 (Problem 4) | Respondent 2 selected 5.0 "uniformly across all 10 items" | Respondent 2 agreed (4 or 5) with all 10 items, including all five negatively worded ones. Raw responses: 5, 5, 4, 4, 4, 5, 5, 5, 5, 4 |
| 3A | Adjusted mean SUS of 81.88 after excluding Respondent 2 | Remove, or label it a sensitivity check; no exclusion rule was set in advance |
| 3A | SD 14.70 | SD 14.62 (sample) |
| 3A | "Grade B+ ... on the Bangor et al. (2008) scale" | 75.5 is a B on the Sauro–Lewis curved grading scale (Sauro & Lewis, 2016). Drop the grade if SUS is no longer used as evidence |
| 5 | Teacher names and email addresses | Replace with T-1 to T-4 and remove emails; the teacher form promised anonymity |
| 6 (Problem 1) | Blending SAM described as "ma-a-t" rather than "m-a-t" | Use one word: SAM sounded out as "sa-a-ma" instead of /s/-/a/-/m/ |
| 7 | Teachers "validated the intentional exclusion of NG and Ñ" and the 33 CVC words | Teachers confirmed the sequence logic (PED-02). The word bank and the NG/Ñ decision were not rated, and the design review later found 6 non-decodable words |

Reference: Sauro, J., & Lewis, J. R. (2016). *Quantifying the user experience: Practical statistics for user research* (2nd ed.). Morgan Kaufmann.

---

## 6. Open Items for the Team

| # | Item | Why it matters |
|---|---|---|
| 1 | Share the current SRS, SDD, and RTM | Merge these changes with your real IDs and wording |
| 2 | Confirm the implemented heart rule | The proposal and the Highlights disagree |
| 3 | Confirm the ASR target (80%) | General Objective 2 and RQ2 disagree |
| 4 | Confirm Round 2 runs on the refactored build | If not, only report Section 3.5 changes |
| 5 | Insert DepEd competency codes per chapter | Needed for Instrument A, Part 3 |
| 6 | Confirm the articulation-cue priority | The Highlights says P1; this change set says P2 |
| 7 | Verify the Smileyometer ease split (6 second face, 1 middle) against the sheet | The screenshot is too small to confirm the faces |
| 8 | Decide whether SUS stays in the summative evaluation | The proposal's Week 6 plan still lists it |
| 9 | Share the Round 1 observation notes | Makes report Section 4.1 more specific |
| 10 | Decide whether parents join Round 2 | Add a task-based dashboard check only if the adviser expects it |
| 11 | Set the data-retention period | Needed for report Section 3.7 |
