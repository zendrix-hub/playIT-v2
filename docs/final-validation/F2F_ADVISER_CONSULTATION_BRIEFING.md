# Face-to-Face Adviser Consultation Master Briefing & Live Demo Guide

## Project: PlayIT — An Offline-First Gamified Phonics & Early Reading Application Using the Marungko Approach for Grade 1 Learners

**Document ID:** PLAYIT-F2F-BRIEFING-v1.0
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027
**Institution:** College of Computer Studies, Cebu Institute of Technology – University
**Capstone Adviser:** Mr. Joemarie C. Amparo
**Consultation Agenda:** `A. Software Functionality and Completion` | `B. Deployment and Source-Code Repository` | `E. Testing and Quality Assurance (Survey Responses and Synthesis)`
**Team Members:** Zendrix (`zendrix-hub`, Team Lead) and Groupmates

---

## 1. Quick-Reference Readiness Scorecard for Mr. Amparo's 3 Agenda Items

| Adviser Agenda Item | Primary Files to Open During F2F | Headline Verification Proof |
|---|---|---|
| **A. Software Functionality & Completion** | - Physical Android phone running `playit-debug-H-0320385.apk` (or `playit-debug-card32-latest.apk`)<br>- `01_Software_and_Deployment/FEATURE_COMPLETION_MATRIX.pdf`<br>- `01_Software_and_Deployment/CODE_REFACTORING_EVIDENCE.pdf` | - **14 / 14 functional requirements (`FR-01`–`FR-14`) implemented and verified** across 12 Jetpack Compose screens and 12 Room SQLite tables.<br>- **Cards 00–32 completed** on `refactor/hear-say-it` (`9031d8f`): 4-step pure-phoneme `Hear It`, offline Vosk grammar + 3-attempt scaffolding + mouth cue `Say It`, 5-card `Find It`, 33-word deranged `Blend It`, Avatar-only onboarding, and 1-page offline PDF Parent Report.<br>- **Zero `INTERNET` permission** (`100%` offline operation). |
| **B. Deployment & Source-Code Repository** | - `01_Software_and_Deployment/DEPLOYED_SYSTEM_AND_ACCESS_INSTRUCTIONS.pdf`<br>- `01_Software_and_Deployment/DEPLOYMENT_AND_PERMISSIONS_INSPECTION.pdf`/`.md`<br>- Live GitHub: `https://github.com/zendrix-hub/PlayIT` (`6ca3315`) & Draft PR #2 (`https://github.com/zendrix-hub/playIT-v2/pull/2`) | - **Submitted Midterm APK H (`playit-debug-H-0320385.apk`):** `108,317,310 bytes`, SHA-256 `b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4`.<br>- **Clean Architecture Verified:** `domain/` contains **0 `android.*` imports** (`DomainPurityTest`).<br>- **Asset Release Gates Verified:** 201 audio clips (`Kokoro-82M`, `Chatterbox-Turbo`, `ElevenLabs`) and 80 child-safe WebP illustrations tracked via release manifests. |
| **E. Testing & QA (Survey Responses & Synthesis)** | - `03_Testing_and_QA/SURVEY_RESPONSES_AND_SYNTHESIS_REPORT.pdf`<br>- `03_Testing_and_QA/Cleaned_Master_Responses_N25.xlsx` & `ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv`<br>- `03_Testing_and_QA/ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf` (Signed)<br>- `02_Updated_Documentation/playIT_STD_v1.0.pdf` (Signed)<br>- `04_Final-User_Validation/SMART_Goal_Matrix.pdf` & `Final_Validation_Instrument.pdf` | - **Round 1 Survey ($N = 25$):** Cohort 1 Children ($n=16$, `93.8%` Enjoyment, `56.3%` Perceived Ease), Cohort 2 Parents ($n=5$, Mean SUS `75.50`, Grade-Fit `4.60/5.00`), Cohort 3 DepEd Grade 1 Teachers ($n=4$, `100%` curriculum alignment, `50%` `PED-08` phoneme critique that drove the Hear It / Say It overhaul).<br>- **Software QA:** **433 automated tests** in STD v1.0.1 (`435` on `HEAD`), **83 functional test cases**, **7 closed defects (`INC-01`–`07`)**, **2 transparently tracked open items (`DEF-01`, `DEF-02`)**, and signed physical **Samsung Galaxy A21s** acceptance. |

---

## 2. Part A — 5-Minute Live Software Transactions Walkthrough Script

Before starting the demo on the Android phone, turn **Airplane Mode ON** (and Wi-Fi OFF) in front of Mr. Amparo to prove visually that PlayIT operates **100% offline** with zero network dependency.

| Step & Time | Screen / Feature | Exact Action to Perform on Phone | What to Point Out to Mr. Amparo (Transaction & DB Proof) |
|---|---|---|---|
| **Transaction 1** (`0:00–0:45`) | **Splash & Avatar-Only Onboarding (`FR-01`, `FR-14`)** | Launch PlayIT $\rightarrow$ Tap **"New Learner"** on `ProfileSelectScreen` $\rightarrow$ Pick one of the 6 illustrated avatars $\rightarrow$ Tap **Start**. | - **Survey Synthesis Link:** In Round 1, Grade 1 children struggled to type their names on an Android keyboard. Card 08 replaced text typing with **1-tap Avatar-Only Onboarding**.<br>- **Database Transaction:** Inserts a new row into Room table `profiles` (`ProfileEntity`) and sets `SessionManager.activeProfileId`. |
| **Transaction 2** (`0:45–1:15`) | **Curriculum Map & Chapter Gating (`FR-05`, `FR-08`)** | Show the vertical scrolling Archipelago Map (`CurriculumMapScreen`) with 7 chapters (26 English letters, Marungko sequence starting with Chapter 1 `M, S, A, I`) and 16 Philippine landmarks. Tap a locked node (`S`), then tap unlocked node **`M`**. | - **Business Rule Proof:** Locked nodes cannot be entered until the preceding letter is completed (`LessonUnlockManager`).<br>- **Scope Resolution:** `Ñ` and `NG` were formally removed in Card 14 (`FR-05`) because they lack English phonics exemplars. |
| **Transaction 3** (`1:15–2:00`) | **Stage 1: `Hear It` Stepped Phoneme Modeling (`FR-02`, `NFR-AUD-01`)** | Enter Letter **`M` (`Hear It`)**. Let Lily play the 4-step sequence: Step 1 (`"Listen!"`) $\rightarrow$ Step 2 (`/m/` played $3\times$ with 700 ms pauses) $\rightarrow$ Step 3 (`"mouse"` card reveal) $\rightarrow$ Step 4 (`/m/` closure). Point out that the **Next** button is disabled until Step 4 finishes. | - **Survey Synthesis Link:** Directly resolves **Teacher Finding #1 (`T-1` Leony Layaguin & `T-3` Teacher Joy)**: *"The sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.'"*<br>- **Audio Engineering:** Uses voice-matched Chatterbox-Turbo/Kokoro pure continuant `/m/` (`"Mmm!"`) with zero intrusive schwa vowel. |
| **Transaction 4** (`2:00–3:15`) | **Stage 2: `Say It` Offline Vosk Speech Recognition & 3-Attempt Scaffolding Ladder (`FR-03`)** | 1. **Attempt 1 (Intentional Foil):** Tap the Mic (`Idle` $\rightarrow$ `Listening` $\rightarrow$ `Heard` $\rightarrow$ `Result`) and say the letter name **`"em"`** (or **`"muh"`**). Show Lily's specific corrective feedback (*"That's the letter name! Make just the /m/ sound"*).<br>2. **Attempt 2 (Mouth Cue):** Point out the **`ArticulationCue`** (`lips_together` diagram) that appears beside the mic on Attempt 2.<br>3. **Attempt 3 / Correct Utterance:** Say **`"mmm"`** (or `"mouse"` in echo step) to pass, OR fail a 3rd time to demonstrate **Echo Together** auto-advance with **zero heart loss**. | - **Survey Synthesis Link:** Directly resolves **Child Ease Friction (`Q2 = 56.3%`)** and **Accessibility Flag `ACC-06` (`60%` hearing support)**.<br>- **Technical Architecture:** `VoskSpeechRecognitionService` runs a constrained JSON grammar `[target, foils..., "[unk]"]` completely offline; `SpeechValidator` classifies utterances into `Correct`, `LetterName`, `AddedVowel`, `OtherWrong`, or `Silence`; Room records every attempt in `say_it_attempts`. |
| **Transaction 5** (`3:15–4:00`) | **Stage 3: `Find It` 5-Card Picture Discrimination & Heart/Star Gamification (`FR-04`, `FR-07`, `FR-09`)** | On `Find It`, show the 5 illustrated cards (**3 target `/m/` pictures** + **2 non-`/m/` distractors** from our 80 new child-safe WebP illustrations). Tap 1 wrong card to show **1 heart deducted** (`5 -> 4`), then tap all 3 correct `/m/` cards (`mouse`, `mango`, `milk`) to reach `LetterCompleteScreen` (1–3 stars). | - **Database Transaction:** Logs each card tap into `find_it_attempts`, updates `lesson_progress` (`bestStars = max(existing, new)`), and unlocks the next letter node on the map. |
| **Transaction 6** (`4:00–4:30`) | **Checkpoint: `Blend It` Deranged Word Construction (`FR-13`)** | Open Chapter 1 `Blend It` (`SAM` / `AM`). Show that the letter tiles (`S`, `A`, `M`) are **always dealt in a deranged (unsolved) order** (`TC-BLD-02`). Tap the tiles in order `S` $\rightarrow$ `A` $\rightarrow$ `M` to hear the blended word. | - **Curriculum Fix (`Card 15`):** Replaced the 5 non-decodable sight words (`AIM, BEE, TOY, BOY, ZOO`) with 100% Marungko-decodable words (`AM, SUM, TUB, YAM, ZIP`) across the 33-word bank. |
| **Transaction 7** (`4:30–5:00`) | **Parent Zone Arithmetic Gate & Offline PDF Progress Report (`FR-10`, `FR-11`, `FR-12`)** | Tap the Parent Zone icon $\rightarrow$ Solve the **two-digit addition gate** (e.g., `24 + 17 = 41`) $\rightarrow$ Show per-letter mastery, speech accuracy, ElevenLabs attribution, and tap **Export PDF Report** (`ReportPreviewScreen`). | - **Technical Proof:** Uses Android's native `android.graphics.pdf.PdfDocument` (`PdfExporter.kt`) to render a clean, 1-page clinical progress summary completely offline (`report_logs` table). |

---

## 3. Part B — Deployment & Source-Code Repository Reference Sheet

### 3.1 Live URLs, Branches & Binary Checksums

| Item | Verified Value |
|---|---|
| **Public Midterm Snapshot Repository** | `https://github.com/zendrix-hub/PlayIT` (Branch `main`, tag `midterm-submission-2026-10`, commit `6ca3315`) |
| **Active Development Repository & PR** | `https://github.com/zendrix-hub/playIT-v2/pull/2` (Branch `refactor/hear-say-it`, Cards 00–32) |
| **Frozen Midterm Binary (`APK H`)** | `playit-debug-H-0320385.apk` (`108,317,310 bytes`, commit `0320385`)<br>**SHA-256:** `b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4` |
| **Post-Card-32 Latest Demo Binary** | `playit-debug-card32-latest.apk` (Built from `refactor/hear-say-it` HEAD `9031d8f`, includes Card 26c 80 WebP illustrations + Card 32 palette/PDF polish) |
| **Midterm Submission Google Drive** | `https://drive.google.com/drive/folders/1093ZTtHqgy6k-3D2V8FPYxK_LOiu8iL7` |
| **Pre-Midterm Survey Evidence Drive** | `https://drive.google.com/drive/folders/1JhVlGw1ncpvOF6lQjBOQC4gwXKZdMbib` |
| **Zero-Internet Offline Proof** | Verified via `aapt dump permissions playit-debug-H-0320385.apk`: requests **only** `android.permission.RECORD_AUDIO` and `android.permission.VIBRATE` (**zero** `android.permission.INTERNET`). |

### 3.2 Honest Engineering Disclosure on Open Defects & Deferred Scope (`STD v1.0.1` & `SRS v3.0.1`)

If Mr. Amparo asks *"Are there any known limitations or open bugs in your submitted midterm build?"*, give this exact, transparent engineering answer:

1. **`DEF-01` (Medium — Tracked in `DEFECT_LOG_AND_RETEST_RECORD.pdf`):** In `FindItViewModel.kt`, the 2 distractor cards are currently drawn from a static fallback list of 3 exemplars (`s_sun`, `a_apple`, `t_turtle`) instead of dynamically querying `PhonemeDao.getByChapter()` for other letters. **Impact:** Gameplay works without crash (`5` cards: `3` targets + `2` distractors), and dynamic distractor selection is scheduled as a post-validation improvement.
2. **`DEF-02` (Low — Tracked in `DEFECT_LOG_AND_RETEST_RECORD.pdf`):** `StarCalculator.calculate()` currently computes stars from `heartsRemaining` (`5 hearts = 3 stars`, `3–4 hearts = 2 stars`, `1–2 hearts = 1 star`), while the combined accuracy term in `SRS FR-09` is documented for alignment after the Week 12 empirical validation.
3. **Planned Post-Midterm Features (`FR-NEW-TEL` & `FR-NEW-REC`):** `SayItAttemptEntity` already logs `attemptNumber`, `recognizedText`, `isPassed`, and `timestamp` in Room SQLite (`export_validation_telemetry.py` extracts this for Week 12 validation). Adding the `errorType` column (`FR-NEW-TEL`) and the pure-phoneme recall prompt (`FR-NEW-REC`) are documented as post-midterm enhancements.

---

## 4. Part E — Survey Responses, QA Synthesis & Final Validation Gate

### 4.1 Summary of Round 1 Survey ($N = 25$) vs. Round 2 Final Validation Plan ($N = 28$–$37$)

| Metric / Dimension | Round 1 MVP Survey Result (Sept 16–23, 2026, $N=25$) | Refactored Software Fix (Cards 00–32) | Round 2 Final Validation Target (Due Oct 31, 2026, $N=28$–$37$) |
|---|---|---|---|
| **Child Perceived Ease & Task Completion** | Smileyometer `Q2` Ease = **`56.3%` Top-Box** ($9/16$); `Q1` Fun = **`93.8%`** ($15/16$); `Q3` Characters = **`93.8%`** ($15/16$); `Q4` Replay = **`87.5%`** ($14/16$). | Avatar-only onboarding (`Card 08`), 4-state Mic (`Card 06`), 3-attempt scaffolding ladder (`Card 05`), `ArticulationCue` mouth diagram (`Card 16`). | **SMART Goal `SG-01`:** Task completion rate **$\ge 85\%$** across Scenarios S1–S4 (`Instrument A` + `Instrument C`), Child Positive Affect **$\ge 85\%$**. |
| **Phoneme Audio Accuracy (`PED-08`)** | DepEd Teachers rated `PED-08` **`50.0%` (`2/4`)**: `T-1` (L. Layaguin) & `T-3` (J. Flores) flagged `/m/` sounding like `"ma, ma, ma"` or letter name. All other 11 items = **`100%` (`4/4`)**. | Re-synthesized 201 audio clips (`Kokoro-82M`, `Chatterbox-Turbo` `"Mmm!"`, `ElevenLabs` short vowels/blend words) + 4-step `Hear It` sequence (`Card 04`). | **SMART Goal `SG-02`:** **100% (`26/26`)** phoneme clip pass rate on `Instrument B (Part B1)` Teacher Phonetic Audit (`Gate 3`). |
| **Offline Speech Recognition (`Say It`)** | Unconstrained Vosk accepted foils or caused frustration; hearts were deducted on speech errors. | Constrained Vosk grammar `[target, foils..., "[unk]"]` (`Cards 01–03`), `SpeechValidator` classification, **zero heart loss in Say It**. | **SMART Goal `SG-03`:** Teacher-vs-Vosk agreement **$\ge 80\%$** and false-rejection rate **$\le 15\%$** across $\ge 60$ utterances (`Instrument B Part B2`). |
| **Curriculum & Pedagogical Validity** | `100%` (`4/4`) on binary checklist (`PED-01..07, 09..12`), but lacked formal Content Validity Index (`CVI`) and had 5 sight words in `Blend It`. | 26-letter Marungko curriculum (`Card 14`), 33 decodable CVC/VC words (`Card 15`), deranged tile dealing (`Card 11`), 80 child-safe WebP pictures (`Card 26c`). | **SMART Goal `SG-05`:** Lynn (1986) **`I-CVI` $\ge 0.83$**, **`S-CVI/Ave` $\ge 0.90$**, and TAM **`PU` & `PEOU` $\ge 4.00/5.00$** (`Instrument B Part B3`). |
| **Parent Usability & Report Clarity** | Mean SUS = **`75.50`** ($n=5$, `SD = 14.62`, Grade `B`; `P-2 = 50.0` acquiescence); Grade-Fit = **`4.60/5.00`**; `ACC-06` Hearing = **`60%`**. | Added `ArticulationCue` visual mouth diagrams (`Card 16`), 1-page offline PDF report (`Card 32` clinical palette), and ElevenLabs attribution (`Card 15b`). | **SMART Goal `SG-06`:** Mean Parent SUS **$\ge 75.0$** across expanded **$n = 10$–$12$ parents** (`Instrument E`) and Report Clarity **$\ge 4.00/5.00$**. |

### 4.2 Pre-Presentation Deliverables Ready for Mr. Amparo's Sign-Off (`04_Final-User_Validation/`)

To secure Mr. Amparo's approval to proceed with Week 12 Final User Validation (due October 31, 2026), present these four completed documents inside `04_Final-User_Validation/`:
1. **`SMART_Goal_Matrix.pdf` / `.docx`** — Maps all 6 measurable SMART goals (`SG-01` to `SG-06`) to their exact indicator, instrument item, formula, and acceptance threshold.
2. **`Approved_Proposal_and_SMART_Goals.pdf` / `.docx`** — Preserves the original Capstone 1 proposal baselines and traces every adviser-approved scope refinement.
3. **`Final_Validation_Plan_and_Framework.pdf` / `.docx`** — Defines the 5 research questions (`RQ1`–`RQ5`), the 3 stakeholder cohorts ($N = 28$–$37$), and the triangulated evaluation framework (ISO/IEC 25010 + Davis TAM + Brooke SUS + Lynn CVI + Read & MacFarlane Smileyometer).
4. **`Final_Validation_Instrument.pdf` / `.docx`** — Contains the 5 ready-to-print evaluation instruments (`Instruments A, B, C, D, and E`).
