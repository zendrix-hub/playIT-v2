# Software Project Management Plan (SPMP)
## Project: PlayIT — An Offline-First Gamified Early Literacy Mobile Application
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Institution:** College of Computer Studies, Cebu Institute of Technology – University  
**Document Version:** 2.2 (Refactored Post-MVP Validation; sprint progress synchronized 2026-10-09)  
**Publication Date:** September 26, 2026  
**Status:** Draft for adviser review (revised 2026-10-05 and 2026-10-09; items marked **[proposed]** await adviser approval)  

---

## Document Revision History

| Version | Date | Primary Author(s) | Management Plan Refactoring Description |
|:---:|:---:|:---:|---|
| **1.0** | May 4, 2026 | Project Manager | Initial SPMP baseline developed for IT332 Capstone 1. |
| **2.0** | September 26, 2026 | Project Manager & Capstone Team | **Comprehensive SPMP Refactoring Based on Weeks 1–2 MVP Validation Findings:**<br>• **Project Scope**: Formally added the Tutoring & Speech Judgment Layer, Three-Stage Audio Production Pipeline, Learner Autonomy scaffolding, and Local Telemetry Logging/Export; prioritized Chapter 1 (*m, s, a, i*) as the Round 2 vertical slice.<br>• **Activities & Schedule**: Re-baselined the Weeks 3–9 implementation and testing timeline to incorporate phonetic audio re-mastering, Vosk grammar spikes, and Round 2 Silent-Observer validation.<br>• **Work Breakdown Structure (WBS)**: Expanded into 9 structured work packages covering pedagogy design, audio pipeline, ASR spike/judge, tutoring layer, independence features, telemetry, testing, Round 2 validation, and documentation.<br>• **Resource Allocation**: Allocated compute and tooling for local Kokoro-82M neural TTS, Chatterbox-Turbo voice cloning, and physical Android test tablets.<br>• **Roles & Responsibilities**: Re-assigned 6 specialized functional roles across the 5 team members.<br>• **Risk Management**: Expanded the Risk Register to 8 targeted engineering and pedagogical risks (R1–R8) with concrete mitigation and contingency protocols. |
| **2.1** | October 5, 2026 | Capstone Team (Claude review) | Corrections for adviser review: status changed to draft; findings table separates implemented from planned work; teacher names replaced by codes; Room schema v4; Milestone 1 marked submitted and revised. |
| **2.2** | October 9, 2026 | Capstone Team (Claude, sprint synchronization) | Records the Oct 9–10 refactoring sprint on branch `refactor/hear-say-it` (cards 18–24, 03b, 15; 364 unit tests passing): §1.2 findings rows updated with what shipped; §3 adds the sprint as a completed internal milestone and updates Milestones 2–4 honestly (shipped vs still planned). Cross-references `MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md` and `engineering-package/13_MASTER_TASKS.md`. |

---

## 1. Project Overview & Scope Refactoring

### 1.1 Project Purpose & Executive Summary
PlayIT is an offline-first native Android educational mobile application designed to instruct Grade 1 Filipino learners in English phonemic awareness, speech production, and CVC word decoding following the Department of Education's (DepEd) Marungko Approach. Following the formative MVP Field Validation conducted in Weeks 1–2 (evaluating $N=25$ participants: 16 early learners, 5 parents, and 4 certified DepEd teachers), this SPMP v2.0 recalibrates project execution, resource allocation, and team milestones to directly address the empirical findings and advisory directives prior to full system rollout.

### 1.2 Interconnected Deliverables Traceability Baseline
In accordance with IT411 academic guidelines, the Weeks 1–6 project lifecycle follows an unbroken evidence-to-implementation chain:

$$\text{MVP Validation} \longrightarrow \text{Findings \& Feedback} \longrightarrow \text{Refactored Requirements (SRS v3.0)} \longrightarrow \text{Updated System Design (SDD v2.0)} \longrightarrow \text{Updated Project Plan (SPMP v2.0)}$$

The specific empirical findings and their resulting impact on project scope and management are summarized below:

| Empirical Validation Finding (Weeks 1–2) | Stakeholder Feedback Source | SRS v3.0 Requirement Impact | SDD v2.0 Architectural Impact | SPMP v2.0 Scope & Management Impact |
|---|---|---|---|---|
| **Vocal schwa / letter-name intrusion** in synthesized phoneme models (e.g., "ma" instead of pure /m/ [m:]). | `PED-08` (50% flagged by DepEd teachers); Teachers T-1 and T-2 comments. | **FR-02, NFR-AUD-01:** Mandated pure acoustic phoneme models and runtime sequence assembly. | Created `AudioPlaybackManager` with `SoundPool` cache; split `ph_m.wav` and `ph_m_word.wav`. | **WBS 2 (Audio Pipeline):** Established 3-stage release gates (Gates 1–3) using Kokoro-82M and Chatterbox-Turbo. |
| **Child speech hesitation at mic**; lack of active listening indicator. | Facilitator notes; Perceived Ease lowest affective rating (7 of 16 children did not choose the top face). | **FR-03:** Mandated 4 mic states, live RMS ripple, tap-to-listen $\le 100\,\text{ms}$, no heart loss. | Implemented 2026-10-09: 4 mic states (`MicStatus`, `MicButton`, time-based ripple). Planned: `AudioRecord` PCM loop for the voice-driven ripple. | **WBS 4 & 5:** Added dynamic visualizer and mascot prompting tasks to sprint schedule. |
| **Untested speech recognition agreement** on child vocal productions. | Empirical validation gap; General Objective 2 requirement. | **NFR-ASR-01:** Established $\ge 80\%$ agreement target and $\le 15\%$ false reject ceiling. | Architected `SayItJudge` with per-letter dynamic grammars and error tagging. | **WBS 3 (ASR Spike):** Scheduled Chapter 1 acoustic calibration spike and threshold tuning. |
| **5 invalid CVC words** in Blend It containing unintroduced vowel teams. | Post-pilot pedagogical review; decodability violation. | **FR-13:** Replaced AIM, BEE, TOY, BOY, ZOO with AM, SUM, TUB, YAM, ZIP. | Implemented 2026-10-09: `BLEND_IT_WORD_SEEDS` reseeded on every open (card 15); teacher confirmation and 4 words' audio/pictures pending. | **WBS 1 (Pedagogy Design):** Curated approved 33 decodable word bank with teacher sign-off. |
| **Desire for classroom reading stations** on shared Android tablets. | `PED-12` (100% teacher endorsement); multi-profile need. | **FR-14:** Formalized multi-profile management (up to 6 child profiles per device). | `ProfileEntity` and `SessionManager` exist (multi-profile implemented). | **WBS 4:** Prioritized multi-profile state scoping and avatar switcher in sprint deliverables. |
| **Missing telemetry and latency metrics** during field validation. | Facilitator observation; teacher request for batch progress tracking. | **FR-NEW-TEL:** Mandated offline event logging and PIN-gated CSV/PDF export. | Planned: `TelemetryLogger` and `CsvExportManager`. | **WBS 6:** Dedicated work package for offline telemetry recording and local export. |
| **Hearing and motor accessibility gaps** (hearing score 60%, corner toggles). | `ACC-06` (60% Yes); `ACC-07` (80% Yes); evaluator comments. | **NFR-ACC-01, NFR-ACC-02:** Sound captions, visual lip cues, $\ge 64\,\text{dp}$ touch targets. | Implemented 2026-10-09: captions, `ArticulationCue` (pictures pending, card 25), adaptive `PlayItDimens` tokens with 64 dp floors and `LessonScaffold`. | **WBS 5:** Sourced visual mouth articulation assets and updated UI layout modifiers. |

### 1.3 Scope Baseline & Phased Implementation Strategy
- **Core Curricular Track:** 26 letters of the English alphabet structured across 7 chapters.
- **Round 2 Vertical Slice Strategy:** To de-risk speech recognition and audio modeling before scaling to all 26 letters, **Chapter 1 (*m, s, a, i*) serves as the primary vertical slice** for full tutoring integration, audio gate clearance, and Round 2 field testing.
- **Deferred Scope (P3):** Digraphs (e.g., *sh, ch, th*) and consonant clusters are documented as data-driven architectural extensions scheduled for post-capstone releases.

---

## 2. Work Breakdown Structure (WBS) & Deliverables

The refactored project scope is organized into 9 distinct Work Packages (WBS 1 to 9):

```
1.0 PlayIT System Refactoring & Implementation
  |-- 1.1 WBS 1: Pedagogy & Curriculum Design
  |     |-- 1.1.1 Curate 26-Letter Lesson Scripts (Chapter 1 Priority)
  |     |-- 1.1.2 Finalize 33 Decodable CVC Word Bank (Replace 5 Invalid Words)
  |     +-- 1.1.3 Construct Spoken Carrier & Formative Feedback Phrase Library
  |-- 1.2 WBS 2: Neural Audio Pipeline & Gate Auditing
  |     |-- 1.2.1 Deploy Local Kokoro-82M & Chatterbox-Turbo Tooling
  |     |-- 1.2.2 Execute Gate 1 Internal Acoustic Audit (Purity, Duration, SNR)
  |     |-- 1.2.3 Execute Gate 2 Vosk ASR Technical Acceptance
  |     +-- 1.2.4 Compile Formal JSON Asset Manifest (`manifest.json`)
  |-- 1.3 WBS 3: ASR Calibration Spike & Speech Judge
  |     |-- 1.3.1 Conduct Chapter 1 Vosk Grammar Spike (`<target>`, foils, `[unk]`)
  |     |-- 1.3.2 Implement `SayItJudge` Decision & Error-Tagging Engine
  |     +-- 1.3.3 Calibrate Confidence Thresholds Against Child Pilot Audio
  |-- 1.4 WBS 4: Tutoring Layer & State Machine
  |     |-- 1.4.1 Implement `LessonEngine` & `TutorPolicy` FSM
  |     |-- 1.4.2 Implement `AudioComposer` Dynamic Sequence Builder
  |     +-- 1.4.3 Integrate Room Schema v4 (`Profile`, `LetterProgress`, `Telemetry`)
  |-- 1.5 WBS 5: Learner Autonomy & Pediatric Accessibility
  |     |-- 1.5.1 Build Mascot Dual-Coding Demos & 10s Idle Re-Prompter
  |     |-- 1.5.2 Build `MicStateVisualizer` with Live RMS Audio Ripple
  |     +-- 1.5.3 Integrate `ArticulationCue` Guides & Global 64dp Touch Modifiers
  |-- 1.6 WBS 6: Offline Telemetry & Local Export Engine
  |     |-- 1.6.1 Implement Real-Time Event Logger (`SystemClock.elapsedRealtime`)
  |     +-- 1.6.2 Implement PIN-Protected Local CSV & PDF Exporter
  |-- 1.7 WBS 7: Verification & Software Test Documents (STD)
  |     |-- 1.7.1 Formulate Test Plan & Unit Test Suites (TC-AUD, TC-MIC, TC-ASR)
  |     +-- 1.7.2 Execute Internal Dry Run with 3–5 Early Learners
  |-- 1.8 WBS 8: Round 2 Field Validation Execution
  |     |-- 1.8.1 Administer Silent-Observer Protocol with Target Cohort (N=30)
  |     +-- 1.8.2 Conduct Gate 3 Pedagogical Teacher Audit on Audio Assets
  |-- 1.9 WBS 9: Capstone Documentation & Dissemination
        |-- 1.9.1 Compile Refactored SRS v3.0, SDD v2.0, and SPMP v2.0
        +-- 1.9.2 Draft Final Midterm Validation & Implementation Report
```

---

## 3. Activities, Milestones & Revised Master Schedule

The revised master schedule spans Weeks 3 through 9 of the First Semester, Academic Year 2026–2027:

| Milestone / Deliverable | Week | Target Dates (2026) | Primary Activities & Work Packages Involved | Status / Verification Gate |
|---|:---:|:---:|---|:---:|
| **Milestone 1:** Refactored Specifications Submission | **Week 3** | Sept 21 – Sept 26 | Finalize and submit refactored **SRS v3.0**, **SDD v2.0**, **RTM v3.0**, and **SPMP v2.0** incorporating MVP validation findings. | **Submitted; revised 2026-10-05 for adviser review** |
| **Milestone 2:** ASR Spike & Chapter 1 Audio Gate Clearance | **Week 4** | Sept 28 – Oct 3 | Conduct Vosk grammar spike on *m, s, a, i*; synthesize Chapter 1 phonemes via Kokoro/Chatterbox; execute Gates 1 & 2; verify decodable word bank. | **In Progress** (Spike Decision Gate); held /m/ and /s/ released; word list implemented, teacher audit pending |
| **Milestone 3:** Tutoring Engine & AudioComposer Integration | **Week 5** | Oct 5 – Oct 10 | Implement `LessonEngine`, `TutorPolicy` FSM, `AudioComposer`, and Room Schema v4 migration; synthesize remaining phoneme assets (Ch 2–7). | Internal Code Review |
| **Internal sprint:** UI fit, mic states, corrections, accessibility | **Week 5** | Oct 9 – Oct 10 | Cards 18–24, 03b and 15 on `refactor/hear-say-it`: adaptive layout on 4 device sizes, 4-state mic, spoken Say It corrections, decodable word list, responsive map, purposeful effects, captions and mouth cues (see `engineering-package/13_MASTER_TASKS.md`). | **Shipped to branch** (364 unit tests passing; agy batch validation and APK pending) |
| **Milestone 4:** Autonomy Features, Telemetry & Gate 3 Teacher Audit | **Week 6** | Oct 12 – Oct 17 | Implement `MicStateVisualizer`, 10s idle re-prompter, 64dp touch tokens, and CSV exporter; conduct Gate 3 teacher audit on audio; execute internal dry run ($N=5$). | **Partly done:** mic states, idle re-prompt and 64 dp tokens shipped (sprint, Oct 9–10); CSV exporter and Gate 3 audit pending |
| **Milestone 5:** Round 2 Field Validation Execution | **Week 7** | Oct 19 – Oct 24 | Deploy `playit-v2-debug.apk` to physical test tablets; conduct multi-stakeholder testing ($N \ge 30$) under the Silent-Observer protocol. | Field Empirical Data Collection |
| **Milestone 6:** Empirical Analysis & Feature Freeze | **Week 8** | Oct 26 – Oct 31 | Analyze Round 2 telemetry (ASR agreement, latency, recall retention, SUS); declare feature freeze; prepare Software Test Documents (STD). | Milestone Review Gate |
| **Milestone 7:** Final Midterm Submission & Oral Defense | **Week 9** | Nov 2 – Nov 7 | Finalize Capstone 2 Midterm Report, verified STD execution logs, and live system demonstration. | Midterm Defense |

---

## 4. Resource Allocation & Infrastructure

### 4.1 Development & Processing Infrastructure
- **Workstations:** High-performance local development machines running Windows 11 with WSL2 (Ubuntu 22.04 LTS) for neural audio synthesis pipelines.
- **Neural TTS Engine:** Kokoro-82M PyTorch pipeline (Apache-2.0 license) executed in local Python environments; Chatterbox-Turbo (MIT license) for held continuous consonant synthesis.
- **Acoustic Analysis:** Praat phonetics toolkit for automated formant, duration, and schwa verification.

### 4.2 Target Hardware & Test Fleet
- **Physical Test Devices:** Fleet of 4 dedicated Android tablets (Samsung Galaxy Tab A7/A8, Android 11/12) and 2 smartphones (Android 10/13) representative of low-cost hardware used in Philippine public elementary schools.
- **Audio Accessories:** Calibrated external sound level meter to monitor ambient noise ($\le 40\,\text{dB}$) during testing.

### 4.3 Software Libraries & Frameworks
- **Programming Language:** Kotlin 1.9.22 (100% pure Kotlin in domain layer).
- **UI Framework:** Jetpack Compose 1.5.4 with Material 3 design tokens.
- **Offline ASR Engine:** Vosk Android SDK v0.3.47 bundled with `vosk-model-small-en-us-0.15` (approx. 40MB packaged in `assets/model/`).
- **Database & Persistence:** Room 2.6.1 + SQLite.
- **Document Generation:** Android native `PdfDocument` API.

---

## 5. Team Roles and Responsibilities

To ensure clear accountability, 6 key project functions are allocated across the 5 capstone team members:

| Functional Role | Designated Lead | Primary Responsibilities & Accountabilities | Mapped Work Packages |
|---|---|---|:---:|
| **Project Manager & Scrum Master** | Team Lead / Member 1 | Master schedule maintenance, sprint tracking, adviser liaison, risk register oversight, transmittal letter coordination. | WBS 8, WBS 9 |
| **Pedagogy & Curriculum Lead** | Member 2 | DepEd Marungko sequence alignment, 26-letter lesson scripts, decodable CVC word bank curation, teacher interview liaison. | WBS 1, WBS 8 |
| **Neural Audio & Acoustic Lead** | Member 3 | Kokoro-82M / Chatterbox synthesis pipelines, Gate 1 acoustic audits, asset manifest compilation, SoundPool integration. | WBS 2 |
| **Speech Recognition (ASR) Lead** | Member 4 | Vosk SDK integration, per-letter dynamic grammar compilation, ASR agreement calibration, latency optimization. | WBS 3 |
| **Android & Architecture Lead** | Member 5 | Clean Architecture maintenance, Compose UI, `LessonEngine`, `TutorPolicy` FSM, Room Schema v4 migration, telemetry logging. | WBS 4, WBS 5, WBS 6 |
| **QA & Validation Lead** | Member 1 (Dual Role) | Software Test Documents (STD), automated regression suites, Gate 2 technical verification, Round 2 Silent-Observer field logistics. | WBS 7, WBS 8 |

---

## 6. Comprehensive Risk Management & Mitigation Matrix

The following risk management matrix addresses 8 critical technical, pedagogical, and operational risks identified following MVP validation:

| Risk ID | Risk Description | Category | Likelihood | Impact | Proactive Mitigation Strategy | Reactive Contingency Plan | Owner |
|:---:|---|:---:|:---:|:---:|---|---|:---:|
| **R1** | Neural TTS cannot synthesize pure stop sounds without schwa ("buh" for /b/). | Technical / Audio | **High** | **High** | Continuous sounds generated via Kokoro/Chatterbox; short stops sourced directly from human reading specialist recordings; mandatory Gate 1 acoustic screening. | If an AI clip fails Gate 3 twice, replace immediately with a validated human studio recording. | Audio Lead |
| **R2** | Vosk cannot reliably discriminate target phonemes from foils in isolated sounds. | Technical / ASR | **Medium** | **High** | Conduct Chapter 1 Vosk grammar spike; compile per-letter grammars (`<target>`, foils, `[unk]`); tune confidence thresholds on pilot child audio. | Evaluate the key word for vocal scoring (e.g., *"mouse"*) while keeping the pure sound model for instruction; consult adviser. | Speech Lead |
| **R3** | Grade 1 children cannot navigate English-only instructions independently. | Pedagogical / UX | **Medium** | **High** | Implement dual-coding: short spoken carrier prompts ($\le 8$ words) paired with vector icons; mascot first-use demos; 10s idle re-prompt. | Deploy an optional auxiliary audio track with Filipino/Cebuano carrier explanations for remedial centers. | Pedagogy Lead |
| **R4** | ASR false rejections induce frustration in young children without an adult present. | User Affect / Gamification | **Medium** | **High** | Completely eliminate heart penalties in Say It; implement progressive 3-tier prompt ladder; enforce $\le 15\%$ false reject ceiling. | Broaden acoustic grammar tolerances or widen phonetic acceptance windows for Philippine English variants. | Android Lead |
| **R5** | Producing scripts, art, and audio for all 26 letters causes schedule overruns. | Project Schedule | **High** | **Medium** | Implement Chapter 1 (*m, s, a, i*) as the complete vertical slice for Round 2; establish standardized JSON script templates. | Move Chapters 2 to 7 production to sprint immediately following Round 2 validation. | Project Manager |
| **R6** | Administrative delays in parental consent and school scheduling for Round 2. | Operational / Logistics | **Medium** | **High** | Distribute parental consent forms and school coordination letters during Week 4; pre-book testing sessions early. | Shift Round 2 by 1 week, utilize community reading centers, and compress data analysis window. | QA / Validation Lead |
| **R7** | Third-party neural voice models present open-source licensing ambiguities. | Legal / Compliance | **Low** | **Medium** | Strictly enforce Apache-2.0 (Kokoro-82M) and MIT (Chatterbox-Turbo) licenses; log licenses in asset manifest; avoid GPL runtime dependencies. | Revert exclusively to human voice recordings if licensing disputes arise. | Audio Lead |
| **R8** | Team availability drops during midterm examination period. | Team Resources | **Medium** | **Medium** | Cross-train pairs on critical paths (Audio+Speech; Android+QA); maintain buffer days prior to milestones. | Reassign non-critical tasks during weekly sprint check-ins to protect primary deliverables. | Project Manager |

---

## 7. Configuration Management & Quality Assurance

### 7.1 Configuration Management & Asset Versioning
- **Source Code Repository:** Git version control hosted on GitHub (`refactor/hear-say-it` feature branch merged via reviewed pull requests).
- **Release Tagging:** Every deployable APK build tested in the field is assigned a semantic tag (e.g., `v2.0.0-rc1-round2`).
- **Asset Manifest Control:** Audio and visual assets are registered in `docs/audio-release/manifest.json`. The application build script validates that only assets passing all verification gates are bundled into the APK.
- **Document Synchronization:** SRS v3.0, SDD v2.0, RTM v3.0, and SPMP v2.0 are versioned and released concurrently as an integrated specification bundle.

### 7.2 Quality Assurance & Verification Protocol
1. **Continuous Integration (CI):** Every commit to the repository triggers automated Gradle verification (`./gradlew testDebugUnitTest`) executing unit tests across DAOs, ViewModels, and state machines.
2. **Audio Verification Gates (Gates 1, 2, 3):**
   - *Gate 1 (Internal):* Waveform duration and acoustic purity audit via Praat.
   - *Gate 2 (Technical):* Automated Vosk batch recognition testing.
   - *Gate 3 (Pedagogical):* Blind listening audit by $\ge 3$ DepEd reading teachers.
3. **Round 2 Field Validation (Silent-Observer Protocol):** Field testing conducted with $N \ge 30$ Grade 1 learners to verify unprompted completion ($\ge 85\%$) and true phonetic recall ($\ge 70\%$).
