# Software Requirements Specification (SRS)
## Project: PlayIT — An Offline-First Gamified Early Literacy Mobile Application
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Document Version:** 3.2 (Renewed & Fully Refactored Post-MVP Validation; automated coverage recorded 2026-10-09)  
**Publication Date:** September 26, 2026  
**Document Status:** Draft for adviser review (revised 2026-10-05 and 2026-10-09; items marked **[proposed]** await adviser approval)  

---

## Document Revision History

| Version | Date | Primary Author(s) | Description of Revision & Validation Traceability |
|:---:|:---:|:---:|---|
| **1.0** | May 4, 2026 | Requirements Lead | Initial draft compiled from IT332 Project Proposal (Revised 4). |
| **2.0** | May 4, 2026 | Requirements Lead | Comprehensive revision strictly aligned with approved proposal details, including Vosk offline engine, 28-letter sequence, Room DB, and preliminary gamification rules. |
| **3.0** | September 26, 2026 | Requirements Lead & Capstone Team | **Comprehensive Renewal and Refactoring Based on Weeks 1–2 MVP Field Validation (N=25):**<br>• **Curricular Re-alignment (§1.2):** Standardized curriculum to 26 letters (7 chapters); removed Ñ (purely Spanish/Filipino orthography) and deferred NG to Chapter 8 (advanced digraphs).<br>• **Pure Phoneme Modeling (FR-02, P0):** Eliminated schwa / letter-name vocal intrusion (e.g., "ma" → /m/ [m:]); mandated runtime carrier-phoneme-keyword sequence (≤15s) with persistent audio replay.<br>• **Microphone Visualizer & Tutoring Loop (FR-03, P1):** Replaced static button with dynamic 4-state visualizer (Idle, Listening with live RMS ripple, Processing, Result); added We Do/You Do prompt ladder; **eliminated heart deductions in Say It**.<br>• **Spaced Retrieval Mastery (FR-NEW-REC, P1):** Introduced un-modeled warm-up retrieval checks and end-of-session recall checks to establish true memory retention beyond immediate imitation.<br>• **Vosk Accuracy & Fairness (NFR-ASR-01, P1):** Established ≥80% agreement with teacher ratings, false rejects ≤15%, Philippine English acoustic calibration, and discrete error classification (`LETTER_NAME`, `ADDED_VOWEL`, `SUBSTITUTION`, `UNKNOWN`).<br>• **Latency Standards (NFR-PERF-01, P1):** Redefined latency from speech termination (`speech_end`) to feedback (P90 ≤0.5s); Find It tap latency (P90 ≤0.3s).<br>• **Decodable Word Bank (FR-13, P1):** Purged 5 non-decodable CVC words (AIM, BEE, TOY, BOY, ZOO) containing untaught vowel teams/diphthongs; replaced with AM, SUM, TUB, YAM, ZIP; flagged QUIZ as documented exception.<br>• **Classroom Multi-Profile (FR-14, P1):** Added independent profile management (up to 6 child profiles per device) for shared reading stations.<br>• **Offline Telemetry Logging & CSV Export (FR-NEW-TEL, P1):** Local event logging in Room DB; PIN-gated CSV/PDF export via Android Share Sheet with zero network transmission.<br>• **Pediatric Inclusivity & Ergonomics (NFR-ACC-01 / ACC-02, P2):** Integrated visual mouth articulation guides and sound captions; enforced minimum 64dp touch targets with ≥8dp margins.<br>• **Removed Requirement:** Eliminated acoustic pitch deviation (±10 cents) as inapplicable to speech phonetics. |
| **3.1** | October 5, 2026 | Capstone Team (Claude review) | Corrections for adviser review: status changed from "Approved" to draft; FR-03 states the hybrid scoring mode (word mode scored; pure sound only if the Vosk test passes); open items marked **[proposed]** (≤15 s sequence, automatic mic, ≥70% recall, 12-minute session, Chatterbox held sounds); teacher names replaced by codes T-1 to T-4 (RA 10173); Room schema v4 (v3 is the current version). |
| **3.2** | October 9, 2026 | Capstone Team (Claude, sprint synchronization) | Added §4.1, the automated test coverage of the requirements refactored in the Oct 9–10 sprint (FR-02, FR-03, FR-05, FR-13, NFR-ASR-01, NFR-ACC-01, NFR-ACC-02): each requirement is mapped to the shipped components and the passing unit-test classes (364 tests on 2026-10-09, 0 failures). Field targets in §4 (Round 2 instruments, Gate 3 teacher audit) are unchanged and still pending. |

---

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to provide a complete, renewed, and verifiable specification of the software requirements for **PlayIT**. This refactored version (SRS v3.0) incorporates the empirical findings, quantitative usability benchmarks (SUS score: 75.50 / Grade B+), qualitative expert recommendations from certified DepEd reading specialists, and caregiver feedback obtained during the Weeks 1–2 MVP Field Validation. It establishes the technical baseline governing the Software Design Description (SDD v2.0), the Software Test Documents (STD), and the Weeks 4–7 Full System Implementation.

### 1.2 Scope and Pedagogical Boundaries
PlayIT is an offline-first, gamified native Android application engineered to teach early English phonemic awareness, speech production, and CVC word decoding to Grade 1 Filipino learners (ages 6–7). The pedagogical framework adapts the Department of Education's (DepEd) **Marungko Approach**, introducing high-frequency sounds to accelerate early reading acquisition.
- **Curricular Scope:** The curriculum encompasses **26 letters of the English alphabet** structured into 7 sequential chapters. Letters `Ñ` and `NG` are excluded from the initial letter track: `Ñ` does not represent a native English phoneme, while `NG` is an orthographic digraph deferred to an optional Chapter 8 extension track.
- **Core Activity Loop:** Each individual letter node contains three sequential sub-levels:
  1. **Hear It** (Auditory Phoneme Acquisition & Modeling Sequence)
  2. **Say It** (Speech Production, Offline Recognition & Formative Correction)
  3. **Find It** (Auditory-Visual Phonemic Discrimination)
- **Consolidation Checkpoints (Blend It):** Each of the 7 chapters concludes with a word construction challenge where learners assemble unlocked phonemes into decodable Consonant-Vowel-Consonant (CVC) words.
- **Support Modules:** A local Multi-Profile Switcher (supporting up to 6 learners per device) and an offline Parent/Facilitator Dashboard featuring developmental analytics, at-risk alerts, and local CSV/PDF report export.
- **Learner Independence Boundary:** PlayIT is engineered for complete child autonomy without requiring co-present adult intervention. The application does not claim to treat clinical speech pathologies or replace comprehensive classroom instruction.

### 1.3 Definitions, Acronyms, and Abbreviations
- **ASR:** Automatic Speech Recognition.
- **CVC:** Consonant-Vowel-Consonant word structure (e.g., *m-a-t*, *s-u-m*).
- **DepEd:** Department of Education (Republic of the Philippines).
- **FSM:** Finite State Machine.
- **Marungko Approach:** A phonics-based reading instructional method that introduces letters by acoustic frequency and blending utility rather than alphabetical order.
- **MVP:** Minimum Viable Product.
- **Pure Phoneme:** A speech sound produced in acoustic isolation without trailing vowel additions (schwas) or letter-name prefixes (e.g., /m/ = [m:], not "ma" or "em").
- **RMS:** Root Mean Square (audio signal amplitude measurement).
- **RTM:** Requirements Traceability Matrix.
- **SDD:** Software Design Description.
- **SMART:** Specific, Measurable, Achievable, Relevant, and Time-bound.
- **SPMP:** Software Project Management Plan.
- **SUS:** System Usability Scale (Brooke, 1996).
- **Vosk:** An open-source, lightweight, privacy-preserving offline speech recognition engine.

### 1.4 References
1. Brooke, J. (1996). SUS: A 'quick and dirty' usability scale. In P. W. Jordan et al. (Eds.), *Usability Evaluation in Industry* (pp. 189–194). Taylor & Francis.
2. Department of Education. (2016). *K to 12 Curriculum Guide: English (Grade 1 to Grade 10)*. Republic of the Philippines.
3. Ehri, L. C., Deffner, N. D., & Wilce, L. S. (1984). Pictorial mnemonics for phonics. *Journal of Educational Psychology*, 76(5), 880–893.
4. Gonzalez-Frey, S. M., & Ehri, L. C. (2021). Connected phonation is more effective than segmented phonation for teaching beginning readers to decode unfamiliar words. *Scientific Studies of Reading*, 25(3), 272–285.
5. IEEE Std 830-1998: *IEEE Recommended Practice for Software Requirements Specifications*.
6. Rosenshine, B. (2012). Principles of instruction: Research-based strategies that all teachers should know. *American Educator*, 36(1), 12–19, 39.
7. PlayIT IT411 Weeks 1–2 MVP Field Validation Highlights Report (September 2026).

---

## 2. Overall Description

### 2.1 Product Perspective & System Context
PlayIT is an independent, self-contained mobile application executing natively on the Android operating system.
- **Runtime Environment:** Android OS 8.0 (API Level 26, `Oreo`) or higher, targeting Android 14 (API Level 34).
- **Zero-Network Architecture:** The software operates 100% offline. All speech recognition decoding, local database queries, asset rendering, and telemetry logging take place on the local hardware. No remote server communication occurs.
- **Audio I/O Integration:** The application interfaces directly with the device's internal microphone (capturing 16 kHz mono 16-bit PCM audio) and internal loudspeaker or 3.5mm/Bluetooth audio output.
- **Environmental Baseline:** Calibrated for typical household and classroom noise environments with ambient sound levels $\le 40\,\text{dB}$.

### 2.2 User Characteristics and Personas
1. **Primary End-Users (Early Learners, Ages 5–7):** Kindergarten and Grade 1 Filipino learners. Characteristics: Emerging pre-readers; developing fine motor coordination (cannot reliably perform drag-and-drop or hit small UI controls); low frustration tolerance; requiring multimodal dual-coding (spoken carrier audio + visual cues).
2. **Secondary Users (Parents & Caregivers):** Household guardians monitoring student literacy progress. Require simple, unauthenticated access with safety speed-bumps (arithmetic gates) to prevent accidental data deletion by young children.
3. **Tertiary Users (Certified Teachers & Remedial Reading Facilitators):** Elementary educators deploying PlayIT on shared classroom Android tablets. Require multi-learner profile isolation, rapid switching, and zero-data local progress export (CSV/PDF) for diagnostic remediation.

### 2.3 General Constraints & Policies
- **Zero-Emoji Architectural Policy:** Emojis are strictly prohibited in all UI text strings, button labels, speech bubbles, cards, dialogs, and titles across child-facing and adult-facing screens. Visual communication relies exclusively on clean Android Vector Graphics (`Icons.Filled.*`) or transparent production PNG assets.
- **Pediatric Touch-Target Floor:** Every interactive UI component must possess a minimum touch target bounding box of $64\times 64\,\text{dp}$, separated by at least $8\,\text{dp}$ margins.
- **Speech Engine Constraint:** Complete reliance on local Vosk 0.3.47 runtime (`vosk-model-small-en-us-0.15`). Cloud speech APIs are strictly prohibited.

### 2.4 Assumptions and Dependencies
- The device microphone functions correctly and provides uncorrupted 16 kHz PCM audio.
- The user grants runtime audio recording permissions (`RECORD_AUDIO`) upon initial application launch.
- The host device maintains at least 250 MB of free internal storage for the bundled APK, Vosk acoustic model, neural audio WAV assets, and local SQLite database.

---

## 3. Specific Requirements

### 3.1 External Interface Requirements

#### 3.1.1 Hardware Interfaces
- **Microphone Interface:** Standard built-in device microphone supporting 16,000 Hz, 16-bit linear PCM mono input.
- **Speaker Interface:** Internal device audio transducer supporting 44.1 kHz, 16-bit uncompressed WAV / OGG output.

#### 3.1.2 Software Interfaces
- **Operating System:** Android API Level 26+ (`minSdkVersion = 26`, `targetSdkVersion = 34`).
- **Offline ASR Engine:** Vosk Android SDK v0.3.47 bundled with a lightweight acoustic model.
- **Persistence Engine:** Android Jetpack Room 2.6+ backed by SQLite.
- **UI Toolkit:** Jetpack Compose 1.5+ utilizing Material Design 3 tokens.

#### 3.1.3 Communications Interfaces
- No runtime external network communication interfaces are utilized. All data processing occurs locally.

---

### 3.2 Refactored Functional Requirements (FR)

#### Module 1: Letter-Sound Acquisition ("Hear It")

##### [FR-01] Carrier Narration & Audio Guidance
- **Description:** The system shall play clear, accent-neutral spoken carrier instructions voiced by the pedagogical persona (Kokoro-82M / Bella).
- **Pre-condition:** User selects an unlocked letter node and enters Hear It.
- **Main Flow:**
  1. System initializes the audio subsystem.
  2. System loads the carrier audio asset (`car_listen.wav`) and outputs audio through the device speaker within $\le 0.2\,\text{s}$.
  3. Mascot animates speech synchronized with audio playback.
- **Post-condition:** Carrier instruction completes, transitioning to the letter reveal.

##### [FR-02] Pure Phoneme Modeling & Modeling Sequence (Refactored · P0)
- **Validation Finding:** MVP teachers flagged vocal schwa intrusions (e.g., /m/ pronounced as "ma, ma, ma" rather than pure continuous /m/ [m:]), causing blending interference in CVC synthesis (PED-08).
- **Requirement:**
  1. The system shall play an acoustically pure phoneme model for each of the 26 letters. Continuous phonemes (/a, e, i, o, u, f, l, m, n, r, s, v, z/) shall be sustained for approximately $800\,\text{ms}$. Stop consonants (/b, c, d, g, h, j, k, p, q, t, w, x, y/) shall be clipped cleanly at $\le 250\,\text{ms}$ ($\le 300\,\text{ms}$ for breaths/glides).
  2. Under no circumstance shall any phoneme audio contain an added trailing schwa (/ə/), syllabic vowel, or letter-name pronunciation.
  3. Phoneme audio clips shall be stored and played independently from carrier phrases and example-word audio.
  4. The modeling sequence shall execute runtime assembly:
     - Carrier (*"Listen!"*)
     - Visual Letter Reveal with Picture Mnemonic
     - Carrier (*"This letter says..."*)
     - Pure Phoneme (3 iterations with $500\,\text{ms}$ pauses accompanied by visual lip articulation cues)
     - Key Word Audio (e.g., *"mouse"*)
     - Pure Phoneme (1 iteration)
     - Carrier (*"Say it with me!"*)
  5. **[proposed]** The entire modeling sequence shall execute within $\le 15\,\text{s}$.
  6. A persistent Audio Replay ("Ear") button shall be present on screen to allow immediate replay of steps 3–6 at any time.
- **Acceptance Criteria:** 26 of 26 phoneme clips rated "Pure" by at least 3 of 4 DepEd reading teachers (HI-1); modeling sequence completed within $\le 15\,\text{s}$ (HI-2).

---

#### Module 2: Speech Production & Formative Tutoring ("Say It")

##### [FR-03] Tutoring Loop, Active Microphone States & Prompt Ladder (Refactored · P1)
- **Validation Finding:** MVP learners exhibited vocal latency and hesitation due to an ambiguous mic button that lacked visual listening feedback; teachers urged clear formative guidance without punitive penalties.
- **Requirement:**
  1. The Say It module shall transition through a structured tutoring sequence:
     - **We Do Step:** Two un-scored choral practice turns (*"Say it with me!"*) with animated mascot lip movements.
     - **You Do Step:** Independent vocal turn. **[proposed]** The microphone shall activate automatically following the carrier cue *"Your turn!"* (or upon explicit tap of the microphone button).
  2. The microphone component shall explicitly display one of four mutually exclusive operational states:
     - **IDLE:** Teal microphone icon, stationary.
     - **LISTENING:** Amber active icon surrounded by a real-time, audio-reactive animated ripple whose expansion radius scales with live input RMS amplitude ($0.0\text{--}1.0$).
     - **PROCESSING:** Lavender rotating spinner while Vosk processes the audio buffer.
     - **RESULT:** Success chime/green highlight or formative guidance prompt.
  3. **Visual Latency:** Tap-to-Listening transition shall occur within $\le 100\,\text{ms}$.
  4. **Formative Prompt Ladder:** If vocalization is absent after $3\,\text{s}$ or an error is detected, the system shall execute progressive pedagogical scaffolding across up to three scored attempts:
     - *Attempt 1 Miss:* Specific spoken correction addressing error type (e.g., Letter Name: *"That's the letter's name. Its sound is /m/."*; Added Vowel: *"Almost! Just /m/, no 'ah'."*) + re-model + *"Your turn."*
     - *Attempt 2 Miss:* Slower re-model with enlarged visual mouth articulation cue + *"Your turn."*
     - *Attempt 3 Miss:* System initiates together-practice (*"Let's say it together: /m/."*), marks the letter as `NEEDS_PRACTICE` in Room DB, advances the lesson without failure screens, and schedules a spaced recall recheck.
  5. **Heart Protection:** The Say It module shall **never deplete player hearts** upon incorrect pronunciation or recognition rejection.
  6. **Scoring Mode (hybrid; user decision 2026-10-01):** The scored Say It check uses **word mode**: the child says the key word (e.g., "mouse"), and the judge matches it against a per-letter grammar of the key word plus that letter's foils (letter name, added vowel). Pure-sound scoring is used for the recall check only if the on-device Vosk test with children shows that held letter names are rejected; the Week 4 spike found that Vosk cannot confirm a correct pure sound (`docs/spikes/vosk-foil-spike.md`), so pure-sound scoring is currently disabled. Letter names and added vowels are never accepted.
- **Acceptance Criteria:** Tap-to-Listening latency $\le 100\,\text{ms}$ (TC-MIC-01); all 3 prompt ladder branches successfully execute (TC-SAY-01..04); 0 hearts deducted across all Say It errors.

##### [FR-NEW-REC] Spaced Retrieval Warm-Up and Recall Mastery Checks (New · P1)
- **Requirement:**
  1. Phonetic mastery shall be determined by retrieval from memory rather than immediate imitation.
  2. Each learning session shall begin with a 2–3 letter **Warm-Up Retrieval Check** presenting learned letters in isolation (no audio model provided).
  3. Priority queue logic: Letters flagged as `NEEDS_PRACTICE` shall appear first, followed by oldest `SECURE` letters.
  4. Each completed node shall culminate in an **End-of-Session Recall Check**.
  5. A letter shall achieve permanent `SECURE` mastery status only after producing a correct vocalization during an unassisted recall check.
- **Acceptance Criteria:** TC-REV-01 to TC-REV-03 pass; **[proposed]** $\ge 70\%$ of previously un-mastered letters correctly produced during end-of-session recall checks (SI-4).

---

#### Module 3: Phoneme Discrimination ("Find It")

##### [FR-04] Dynamic Grid Discrimination
- **Requirement:** The system shall display a dynamic $3\times 2$ grid containing exactly 3 target images matching the current phoneme sound and 2 distractor images selected strictly from previously mastered letters.
- **Pass Rule:** The learner must identify all 3 target items before hearts reach 0.
- **Accuracy Metric:** $\text{Discrimination Accuracy} = \frac{\text{Correct Taps}}{\text{Total Taps}} \times 100\%$. Passing target: $\ge 80\%$.

##### [FR-05] Heart Depletion & Buffer in Find It
- **Requirement:** The learner begins Find It with 5 hearts. Each incorrect card tap deducts exactly 1 heart and plays a gentle corrective acoustic tone. If hearts reach 0, the activity pauses and reloads with a 3-heart retry buffer.

---

#### Module 4: Word Construction Checkpoint ("Blend It")

##### [FR-13] Decodable CVC Word Bank & Synthesis Engine (Refactored · P1)
- **Validation Finding:** Post-validation pedagogical audit revealed 5 words in the MVP word bank that contained unintroduced digraphs and vowel teams (e.g., *ai* in AIM, *ee* in BEE, *oy* in TOY/BOY, *oo* in ZOO), violating phonics decodability rules.
- **Requirement:**
  1. Every Blend It word shall be 100% decodable using exclusively the letter-sounds unlocked in the current and preceding chapters.
  2. The seeded database shall replace the 5 invalid words:
     - Chapter 1: Remove **AIM** $\rightarrow$ Replace with **AM** (Letters: *a, m*)
     - Chapter 2: Remove **BEE** $\rightarrow$ Replace with **SUM** (Letters: *s, u, m*)
     - Chapter 3: Remove **TOY** $\rightarrow$ Replace with **TUB** (Letters: *t, u, b*)
     - Chapter 3: Remove **BOY** $\rightarrow$ Replace with **YAM** (Letters: *y, a, m*)
     - Chapter 7: Remove **ZOO** $\rightarrow$ Replace with **ZIP** (Letters: *z, i, p*)
     - Chapter 7: Retain **QUIZ** strictly as a documented pedagogical exception flagged with `isDocumentedException = true` (explaining *qu* = /kw/).
  3. The activity shall display 3 letter slot boxes and draggable/tappable letter tiles. Learner must sequence phonemes into valid words.
- **Acceptance Criteria:** 100% of seeded word bank validated as decodable against unlocked chapter letters (TC-BLD-01).

---

#### Module 5: Mastery Progression, Gamification & Multi-Profile

##### [FR-06] Star Calculation Rules
- **Requirement:** Upon node completion, stars shall be awarded:
  - **3 Stars:** 100% discrimination accuracy and 0 hearts lost.
  - **2 Stars:** $\ge 80\%$ accuracy with $\le 2$ hearts lost.
  - **1 Star:** Completed activity, but with $>2$ hearts lost or accuracy between 50%–79%.

##### [FR-07] Heart Recovery System
- **Requirement:** The system shall award $+1$ heart for every 3 consecutive correct taps across Find It. Recovered hearts shall be capped at the session starting pool (5 for fresh sessions, 3 for restart sessions).

##### [FR-08] Linear Marungko Sequence Enforcer
- **Requirement:** Letter node $N+1$ shall remain strictly locked until Letter node $N$ has achieved at least 1 star across all three sub-levels.

##### [FR-09] Daily Practice Streaks
- **Requirement:** The system shall increment the active profile's streak counter upon the completion of at least one learning node within a calendar day. Streak reset occurs only after 24 hours of inactivity without penalizing stars or unlocked levels.

##### [FR-14] Multi-Profile Management for Shared Stations (Refactored · P1)
- **Validation Finding:** Certified teachers strongly requested multi-profile support (PED-12) to enable PlayIT deployment on shared tablets in classroom remedial reading stations.
- **Requirement:**
  1. The application shall support up to 6 distinct child profiles on a single device without requiring online authentication.
  2. Each profile shall possess fully isolated progression states, star tallies, heart pools, unlocked biomes, and telemetry event logs.
  3. An intuitive, visual Avatar Switcher shall be accessible from the main splash screen.
  4. Actions executed under Profile $A$ shall never modify or corrupt data belonging to Profile $B$.
- **Acceptance Criteria:** Unit/Instrumented test TC-DB-03 confirms complete profile isolation across concurrent SQLite write operations.

---

#### Module 6: Telemetry, Parent Dashboard & Diagnostics

##### [FR-10] Offline Parent Dashboard Display
- **Requirement:** An offline analytics dashboard shall render learning metrics: accuracy trend per letter, total attempts, hearts lost, time-on-task, retention scores, and color-coded flags (Green: $\ge 80\%$ mastered, Yellow: 50–79% developing, Red: $<50\%$ at-risk).
- **Access Control:** Protected by a lightweight pediatric arithmetic gate (e.g., *"Solve $7 + 5 = ?$"*) to prevent accidental child access.

##### [FR-12] Local PDF Progress Report Generation
- **Requirement:** The system shall compile the active profile's performance analytics into a multi-page, formatted PDF document written to local app-specific storage using Android's native `PdfDocument` API.

##### [FR-NEW-TEL] Offline Telemetry Logging & Local CSV Export (New · P1)
- **Validation Finding:** Round 1 lacked automated recording of speech latency, agreement logs, and error types; teachers requested direct data export to monitor remedial reading cohorts.
- **Requirement:**
  1. The system shall automatically record structured interaction telemetry to a local Room table (`TelemetryEvent`) upon every user event.
  2. Logged events shall capture: `profileId`, `sessionId`, `chapter`, `letter`, `module`, `eventType`, `elapsedRealtimeMs`, `wallClockEpoch`, `resultValue`, and `asrConfidence`.
  3. Event types: `session_start`, `session_end`, `node_enter`, `node_complete`, `hear_play`, `mic_tap`, `speech_end`, `asr_result`, `feedback_shown`, `find_tap`, `find_feedback_shown`, `blend_submit`, `heart_change`, `idle_reprompt`.
  4. The Parent Dashboard shall provide a PIN-protected **Export Telemetry (CSV)** function that compiles raw event logs into standard comma-separated text files shared via the native Android Share Sheet without network calls.
- **Acceptance Criteria:** TC-TEL-01 verifies schema fields; TC-EXP-01 confirms PIN lock; TC-EXP-02 confirms zero network transmission.

---

### 3.3 Non-Functional Requirements (NFR)

#### 3.3.1 Acoustic Quality & Speech Recognition Performance

##### [NFR-AUD-01] Phoneme Audio Production & Three-Stage Release Gate (New · P0)
- **Requirement:**
  1. Audio assets shall be generated using the Kokoro-82M neural model (Apache-2.0). Held (continuous) sounds may instead use Chatterbox-Turbo (MIT), voice-cloned from a Kokoro reference clip, chosen per clip by ear (user decision 2026-10-01; **[proposed]**, adviser confirmation pending).
  2. No phoneme model shall be synthesized by feeding single letters into raw text-to-speech. Continuous sounds shall utilize phoneme IPA tokens or phonetic interjections (e.g., *"Mmm!"*); short sounds shall utilize human phoneme recordings.
  3. Every shipped audio clip shall pass three strict verification gates:
     - **Gate 1 (Internal Acoustic Audit):** Zero audible trailing schwa, clipped at $\le 800\,\text{ms}$ (continuous) or $\le 250\,\text{ms}$ (stops), SNR $\ge 25\,\text{dB}$.
     - **Gate 2 (Vosk Technical Verification):** Phoneme audio tested against Vosk recognizer to verify target acceptance without tripping foil tokens.
     - **Gate 3 (Expert Pedagogical Audit):** Independent audit by $\ge 3$ certified reading teachers rating the clip as "Pure".
  4. A structured JSON Asset Manifest (`docs/audio-release/manifest.json`) shall document each clip's ID, source, tool version, voice, duration, license, and gate verification timestamps.

##### [NFR-ASR-01] Vosk Recognition Agreement, Fairness & Error Categorization (Refactored · P1)
- **Requirement:**
  1. The Vosk speech recognition engine shall achieve $\ge 80\%$ agreement with certified teacher perceptual judgments on vocal productions from Grade 1 Filipino learners.
  2. **False-Reject Bound:** The engine shall maintain a false-reject rate of $\le 15\%$ on teacher-rated correct attempts to prevent learner frustration.
  3. **Acoustic Adaptation:** The recognition grammar shall accommodate Philippine English phonological variations (e.g., lax/tense vowel substitutions, /p/-/f/ and /b/-/v/ developmental variations).
  4. Rejections shall be categorized into discrete diagnostic error types: `LETTER_NAME`, `ADDED_VOWEL`, `SUBSTITUTION`, or `UNKNOWN`.

##### [NFR-PERF-01] End-to-End Latency Thresholds (Refactored · P1)
- **Requirement:**
  1. **Say It Latency:** Measured from the acoustic termination of child speech (`speech_end`) to feedback rendering (`feedback_shown`), latency shall be $\le 0.5\,\text{s}$ at the 90th percentile (P90).
  2. **Find It Latency:** Measured from card tap (`find_tap`) to visual highlight/sound cue, latency shall be $\le 0.3\,\text{s}$ at P90.
  3. **Tap-to-Mic Latency:** Microphone state transition from Idle to Listening shall occur in $\le 100\,\text{ms}$.

#### 3.3.2 Pedagogical Usability & Accessibility

##### [NFR-IND-01] Independent Learner Usability & Dual-Coding (New · P1)
- **Requirement:**
  1. Grade 1 children must be able to navigate and complete all lessons independently without an adult present.
  2. All prompts must be spoken (dual-coded with clear vector icons). No activity shall require reading written instructions.
  3. Onboarding and first-time interactions shall feature an animated demonstration by Lily the Mascot.
  4. **Idle Re-Prompt:** If no user input is registered for $10\,\text{s}$, the mascot shall execute an idle bounce and replay the instructional carrier prompt.

##### [NFR-ACC-01] Visual Articulation Guides & Sound Captions (New · P2)
- **Validation Finding:** Accessibility checklist revealed a 60% score on hearing accommodations (`ACC-06`) due to absence of visual phonetic cues.
- **Requirement:**
  1. Hear It and Say It shall display a synchronized `ArticulationCue` component illustrating proper mouth, lip, and teeth placement for each phoneme.
  2. On-screen text captions representing the pure phonetic sound (e.g., *"mmm"*, *"sss"*, *"aaa"*) shall accompany audio playback to assist hearing-impaired learners.

##### [NFR-ACC-02] Pediatric Touch Target Ergonomics (Refactored · P2)
- **Validation Finding:** Observers noted children occasionally missed small corner navigation toggles (`ACC-07`, 80%).
- **Requirement:** Every clickable or interactive element across child-facing and adult-facing screens—including corner back arrows, settings icons, and ear buttons—shall enforce a minimum touch target bounding box of $64\times 64\,\text{dp}$ with at least $8\,\text{dp}$ separation from adjacent targets.

##### [NFR-SES-01] Developmental Session Pacing (New · P2)
- **Requirement:** To prevent cognitive exhaustion in 6-year-old learners, **[proposed]** the application shall gracefully terminate a learning session at the first completed node after approximately 12 minutes of continuous gameplay, celebrating earned stars and returning to the profile screen.

#### 3.3.3 Reliability, Offline Security & Architecture

##### [NFR-REL-01] Transactional Offline Persistence (Refactored · P1)
- **Requirement:** The application shall commit state changes to SQLite/Room immediately upon the completion of every sub-level interaction. An abrupt OS process termination or battery failure shall result in zero loss of completed progress.

##### [NFR-SEC-01] Complete Local Data Privacy
- **Requirement:** 100% of learner telemetry, vocal audio buffers, and profile records shall remain strictly within the device's private app sandbox (`/data/user/0/com.playit.app/`). The application shall contain zero network permissions (`android.permission.INTERNET` omitted from `AndroidManifest.xml`).

---

## 4. Requirements Traceability Matrix (RTM v3.0)

The following Requirements Traceability Matrix (RTM v3.0) demonstrates the bidirectional relationship linking **Empirical MVP Validation Findings & Stakeholder Evidence**, **Capstone Research Objectives**, **Refactored SRS Requirements**, **SDD v2.0 Architectural Components**, **Software Test Documents (STD) Test Cases**, and **Validation Verification Targets**.

| Finding ID | Empirical Validation Evidence & Stakeholder Voice | Capstone Objective / SMART Goal | Refactored Req. ID | SDD v2.0 Design Component | Proposed STD Test Case(s) | Round 2 Field Verification Target | Priority |
|:---:|---|:---:|:---:|---|---|---|:---:|
| **F-01** | **PED-08 (50% flagged):** Pronunciation of letter sounds; Teacher T-1: *"M should be /m/ (mmm) rather than 'ma'."* | **Gen Obj 1 / HI-1** (Pure Phoneme Model) | **FR-02** | `AudioPlaybackManager`, `SoundPool` cache, `raw/ph_*.wav` | **TC-AUD-01:** Phoneme playback $\le 15\,\text{s}$<br>**TC-AUD-02:** Zero schwa audio audit | **Instrument A (Part 1):** $\ge 3/4$ teachers rate pure across 26 letters | **P0** |
| **F-02** | **Facilitator Notes:** Children hesitated at mic; perceived ease was lowest dimension (7 of 16 children did not choose the top face = 43.8%). | **Gen Obj 2 / SI-3** (Child Independence) | **FR-03** | `MicStateVisualizer`, `AudioRecord` RMS loop | **TC-MIC-01:** Tap-to-Listen $\le 100\,\text{ms}$<br>**TC-MIC-02:** Ripple scales with RMS input | **Instrument B / D-10:** Child hesitation drops to $<15\%$ | **P1** |
| **F-03** | **Validation Gap:** MVP testing lacked empirical ASR agreement scoring against teacher judgments. | **Gen Obj 2 / SI-1** (ASR Agreement) | **NFR-ASR-01** | `SayItJudge`, Vosk Grammar Compiler | **TC-ASR-01 to 05:** Recognition across child pitch/noise $\le 40\,\text{dB}$<br>**TC-ASR-06:** False reject $\le 15\%$ | **Instruments B & C:** $\ge 80\%$ agreement with teacher ratings | **P1** |
| **F-04** | **Validation Gap:** Speech latency unmeasured in field due to missing diagnostic logging. | **Gen Obj 2 / SI-2** (Immediate Feedback) | **NFR-PERF-01** | `TelemetryLogger`, Monotonic Clock (`elapsedRealtime`) | **TC-PERF-01:** Say It P90 $\le 0.5\,\text{s}$<br>**TC-PERF-02:** Find It P90 $\le 0.3\,\text{s}$ | **Instrument D (D-01, D-02):** Timed latency verification | **P1** |
| **F-05** | **Design Audit:** 5 CVC words (AIM, BEE, TOY, BOY, ZOO) contained unintroduced vowel teams/diphthongs. | **Gen Obj 4 / O4** (Decodable CVC Synthesis) | **FR-13** | `BlendItWordBank`, Room Seed Migration v3 | **TC-BLD-01:** 100% words match unlocked chapter letters | **Instrument A (Part 2):** Teacher decodability audit | **P1** |
| **F-06** | **Teacher Feedback:** Teachers requested batch data export for classroom tracking; missing telemetry. | **Gen Obj 5 / O5** (Progress Tracking) | **FR-NEW-TEL** | `TelemetryLogger`, `CsvExportManager` | **TC-TEL-01:** Telemetry schema validation<br>**TC-EXP-01:** PIN gate enforcement<br>**TC-EXP-02:** Zero network calls | **Instrument C:** Successful CSV export on test tablet | **P1** |
| **F-07** | **PED-12 (100%):** Teachers desire app for remedial reading stations with shared devices. | **Gen Obj 5 / O5** (Classroom Stations) | **FR-14** | `ProfileEntity`, `ProfileRepository`, Avatar Switcher | **TC-DB-03:** Multi-profile data isolation test | **Instrument D:** 6 profiles operate concurrently without data bleed | **P1** |
| **F-08** | **Field Observation:** Need formal proof that app survives unexpected OS kill without progress loss. | **Gen Obj 5 / O6** (Data Reliability) | **NFR-REL-01** | Room Transaction DAOs, Schema v4 Migration (planned) | **TC-DB-01:** 20 force-close crash tests<br>**TC-DB-02:** Migration preservation test | **Instrument D (D-05, D-06):** 100% progress retained after process kill | **P1** |
| **F-09** | **ACC-06 (60% Yes):** Accessibility gap for hearing-impaired learners; missing visual cues. | **Gen Obj 1 / O6** (Pediatric Inclusion) | **NFR-ACC-01** | `ArticulationCue` Composable, Sound Captions | **TC-ACC-01:** Visual cue and caption present for 26/26 letters | **Instrument D (D-09):** Inspection of articulation cues | **P2** |
| **F-10** | **ACC-07 (80% Yes):** Evaluators recommended larger edge margins and corner touch areas. | **Gen Obj 5 / O6** (Ergonomics) | **NFR-ACC-02** | `Modifier.minimumTouchTarget(64.dp)` | **TC-ACC-02:** Automated layout audit confirming all targets $\ge 64\,\text{dp}$ | **Instrument D (D-07):** Physical layout verification | **P2** |
| **F-11** | **Teacher Feedback:** Teacher T-2 requested advanced reading expansion track. | **Long-term Roadmap** | **Future Scope (P3)** | Data-driven Chapter Model (`Chapter 8+`) | N/A (Deferred to Post-Capstone) | N/A | **P3** |
| **F-12** | **Adviser Review:** Modeling sequence lacked formal composition spec and replay mechanism. | **HI-2** (Sequence Spec) | **FR-02** | `AudioComposer`, `LessonScript` | **TC-SCR-01:** Runtime sequence execution $\le 15\,\text{s}$ | **Instrument D (D-11):** Sequence timing verification | **P0** |
| **F-13** | **Adviser Review:** Synthetic TTS creates schwa when prompt is a lone letter. | **HI-1** (Audio Pipeline) | **NFR-AUD-01** | Kokoro-82M / Chatterbox Pipeline, Asset Manifest | **TC-AUD-03:** Manifest gate verification check | **Gate 3 Audit:** Expert phoneme verification | **P0** |
| **F-14** | **Adviser Review:** Learning model must guarantee child can succeed without an adult co-present. | **HI-3, SI-3** (Autonomy) | **NFR-IND-01, FR-03** | `TutorPolicy`, Spoken Carriers, 10s Idle Re-prompt | **TC-IND-01 to 03:** Spoken instruction & idle prompts | **Silent Observer Protocol:** $\ge 85\%$ unprompted completion | **P1** |
| **F-15** | **Adviser Review:** Echoing an immediate model shows imitation, not true phonemic mastery. | **SI-4** (Spaced Recall) | **FR-NEW-REC** | `LearnerModel`, Review Scheduler | **TC-REV-01 to 03:** Warm-up & end-of-session checks | **Instrument B:** $\ge 70\%$ recall retention rate | **P1** |
| **F-16** | **Proposal Contradiction:** Proposal's parent override conflicts with child independence. | **SI-1** (ASR Fairness) | **NFR-ASR-01** | Tuned Vosk thresholds, Prompt Ladder (No Hearts) | **TC-ASR-06:** False reject $\le 15\%$ on child voices | **Field Pilot:** Zero parental overrides needed | **P1** |


### 4.1 Automated Test Coverage of Refactored Requirements (RTM v3.2, checked 2026-10-09)

The table records which automated tests now verify each requirement refactored during the Oct 9–10 sprint (branch `refactor/hear-say-it`, `./gradlew testDebugUnitTest`: 364 tests, 0 failures, 6 font-scale checks skipped on the 360x640 profile by design). It complements, and does not replace, the field verification targets in §4.

| Req. ID | Implemented components (code) | Automated test classes | Still pending (not automatable) |
|:---:|---|---|---|
| **FR-02** | `HearItSequenceBuilder`, `HearItViewModel`, `AudioResolver` (released held /m/ and /s/) | `HearItSequenceBuilderTest`, `HearItViewModelTest`, `AudioResolverTest` | Gate 3 teacher audit of every phoneme clip; short vowels (ElevenLabs, due Oct 11) |
| **FR-03** | `MicStatus`, `MicButton`, `SayItViewModel` (spoken corrections, `onScreenHidden`), `TutorPolicy`, `SayItFeedbackCopy` | `MicStatusTest`, `SayItViewModelTest`, `TutorPolicyTest`, `SayItFeedbackCopyTest` | Round 2 hesitation measure (Instrument B / D-10); RMS-driven ripple (planned) |
| **FR-05** | `FindItScreen`, `FindItGrid`, `GridGenerator`, `HeartManager` | `FindItViewModelTest`, `GridGeneratorTest`, `PictureAssetsTest`, `LayoutMatrixTest` (`findIt_wholeGridVisible`, `findIt_fontScale13`) | — |
| **FR-13** | `BLEND_IT_WORD_SEEDS` (`DatabaseModule`), `BlendItWordSelector`, `BlendItScreen`, `BlendItCard` | `BlendItWordSeedsTest`, `BlendItWordSelectorTest`, `BlendItViewModelTest`, `BlendItCardLayoutTest`, `LayoutMatrixTest` (`blendIt_tilesVisible`) | Teacher decodability audit (Instrument A Part 2); word audio for AM, TUB, YAM and pictures for AM, TUB, YAM, ZIP |
| **NFR-ASR-01** | `SpeechValidator` (word-mode judge, foil grammars, error types), `SayItViewModel` (judges wrong partials only on a final result) | `SpeechValidatorTest`, `SayItViewModelTest` | Agreement with teachers $\ge 80\%$ and false rejects $\le 15\%$ (Instruments B and C) |
| **NFR-ACC-01** | `ArticulationGroup`, `CaptionText`, `CaptionBubble`, `ArticulationCue`, `AudioPlayer.playSequence(onItemStart)` | `ArticulationGroupTest`, `CaptionTextTest`, `ArticulationCueTest`, `HearItViewModelTest` (`caption_followsSequence`), `SayItViewModelTest` (`secondMiss_showsMouthCue`) | Mouth-shape pictures (card 25: 6 of 9 picked, release pending); D-09 inspection |
| **NFR-ACC-02** | `PlayItDimens`, `LessonScaffold`, `GummyContainer` layout, `PlayItMotion`, `FeedbackEffects`, reduced-motion handling | `DimensTest`, `GummyContainerLayoutTest`, `LayoutMatrixTest` (4 sizes, font scale 1.3), `TopStatsBarLayoutTest`, `MapLayoutTest`, `FeedbackEffectsTest`, `PlayItMotionTest`, `ZeroEmojiPolicyTest` | Physical layout verification on the A21s (D-07) |

---

## 5. Requirements Sign-Off & Verification Protocol

The refactored requirements specified in this SRS v3.0 have been verified against the Capstone 2 Midterm Targets. Implementation will proceed according to the three-tier verification protocol:
1. **Automated Unit & Instrumented Verification (CI/CD):** Execution of all unit tests (`./gradlew testDebugUnitTest`) mapped to test IDs `TC-AUD-*`, `TC-MIC-*`, `TC-ASR-*`, `TC-DB-*`, and `TC-TEL-*`.
2. **Pedagogical Expert Verification (Gate 3 Audit):** Formal review of remastered audio clips and decodable word banks by certified DepEd reading specialists.
3. **Round 2 Field Validation (Weeks 7–8):** Empirical execution of Silent-Observer testing across primary target cohorts to substantiate SMART objective achievement.
