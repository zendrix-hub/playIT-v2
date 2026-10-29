# Survey Responses and Synthesis Report (Round 1 MVP Field Validation -> Midterm Refactoring -> Round 2 Final Validation Readiness)

## Project: PlayIT — An Offline-First Gamified Phonics & Early Reading Application Using the Marungko Approach for Grade 1 Learners

**Document ID:** PLAYIT-QA-SURVEY-SYNTHESIS-v1.0
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027
**Institution:** College of Computer Studies, Cebu Institute of Technology – University
**Deliverable Classification:** Section E — Testing and Quality Assurance (Survey Responses and Synthesis)
**Capstone Adviser:** Mr. Joemarie C. Amparo
**Evaluation Period (Round 1):** September 16–23, 2026 ($N = 25$ Total Respondents)
**Refactored Build Baseline:** `playit-debug-H-0320385.apk` (Midterm Frozen Baseline) & `refactor/hear-say-it` (`9031d8f`, Post-Card-32 Release)

---

## 1. Executive Summary & Purpose of This Document

This report consolidates the complete empirical **Survey Responses and Synthesis** for **PlayIT** to support **Section E (`Testing and Quality Assurance — Survey Responses and Synthesis`)** of the Face-to-Face Adviser Verification Consultation. It provides:

1. **Direct Access to Primary Survey Evidence & Raw Datasets:** Verified links to the three Google Forms, the raw and cleaned Google Sheets workbooks (`Master (Responses)` and `Cleaned Master (Responses)`), school permission documentation (`school-permission.HEIC`), and 30 field photographs and 2 classroom evaluation videos (`evidence/`).
2. **Complete Itemized Respondent Tabulations ($N = 25$):** Every individual response across **Cohort 1** ($n = 16$ Early Learners, ages 5–7, Child Smileyometer), **Cohort 2** ($n = 5$ Parents and Supervising Teachers, System Usability Scale `SUS-01..10`, Accessibility `ACC-01..07`, and Grade-Fit `FIT-01..02`), and **Cohort 3** ($n = 4$ Certified DepEd Grade 1 Reading Teachers, Pedagogical Checklist `PED-01..12` and verbatim qualitative critiques).
3. **Diagnostic Synthesis & Engineering Traceability (`Findings #1–#5` $\rightarrow$ `Priorities 1–6` $\rightarrow$ `Cards 01–32`):** Proof of how each empirical finding from the pre-midterm survey directly drove the software refactoring in **SRS v3.0.1**, **SDD v2.0.1**, and **STD v1.0.1**, verified by **435 automated JUnit/Robolectric/Roborazzi tests** and physical device acceptance on the **Samsung Galaxy A21s**.
4. **Methodological Bridge to Week 12 Final User Validation (`04_Final-User_Validation/`):** How the five structural limitations identified in the Round 1 survey instruments have been systematically resolved in the Round 2 Final Validation Instrument Package (`Instruments A, B, C, D, and E`).

---

## 2. Survey Administration, Respondent Profile & Evidence Repository ($N = 25$)

### 2.1 Multi-Stakeholder Respondent Cohorts

During the Round 1 Field Evaluation (September 16–23, 2026), **25 unique participants** tested the initial PlayIT prototype on physical Android smartphones across classroom, home, and community settings in Cebu:

| Cohort ID | Stakeholder Group | Sample Size ($n$) | Instrument Administered | Role in Validation Synthesis |
|---|---|:---:|---|---|
| **Cohort 1 (`C-01`–`C-16`)** | Early Learners (Grade 1 / Kindergarten-transition, ages 5–7) | $n = 16$ | **4-Item Child Smileyometer** (Read & MacFarlane 5-point pictorial facial affect scale, facilitated 1-on-1) | Evaluates child affective enjoyment, perceived ease of play, character appeal, and replay motivation. |
| **Cohort 2 (`P-1`–`P-5`)** | Parents, Guardians & Supervising Classroom Teachers | $n = 5$ | **10-Item System Usability Scale (SUS)** + **7-Item Accessibility Audit** + **2-Item Grade-Fit Likert** | Evaluates adult-perceived system usability, learnability, ergonomic accessibility, and Grade 1 appropriateness. |
| **Cohort 3 (`T-1`–`T-4`)** | Certified DepEd Grade 1 Reading Teachers (Subject-Matter Experts) | $n = 4$ | **12-Item DepEd Pedagogical Checklist** (`Yes` / `No` / `Qualified`) + Open-Ended Qualitative Critique | Evaluates Marungko phonics sequence alignment, phonetic accuracy of audio clips, scaffolding quality, and classroom utility. |
| **Total Sample** | **Triangulated Multi-Stakeholder Cohort** | **$N = 25$** | **3 Linked Google Forms** | **Empirical basis for the Capstone 2 Hear It / Say It / Blend It architectural refactoring.** |

### 2.2 Verified Links to Primary Survey Forms, Datasets & Field Evidence

All primary survey instruments, raw spreadsheet exports, and photographic/video field evidence are archived in the team's Survey Evidence Google Drive folder and mirrored in `03_Testing_and_QA/`:

| Artifact Category | Resource Name | Direct URL / Local Path | Verification Note |
|---|---|---|---|
| **Master Survey Drive Folder** | Pre-Midterm Survey Results & Field Evidence Root | `https://drive.google.com/drive/folders/1JhVlGw1ncpvOF6lQjBOQC4gwXKZdMbib` | Contains `evidence/`, `forms/`, `responses/`, and `refactored/`. |
| **Cleaned Dataset (3 Tabs)** | `Cleaned Master (Responses)` (Google Sheets & `.xlsx`) | `https://docs.google.com/spreadsheets/d/1SIsyr4YbdtqyEy_MYqIkOKnjnB6qVGFdwyMXCM9PO3s/edit` (`03_Testing_and_QA/Cleaned_Master_Responses_N25.xlsx`) | Contains `smileyometer` ($n=16$), `SUSaccessibility` ($n=5$), and `teacherpedagogy` ($n=4$). |
| **Raw Unedited Form Export** | `Master (Responses)` (Google Sheets) | `https://docs.google.com/spreadsheets/d/1mvyEgBKZrr8Gs9mSe85mWn0EylSkYn2bbv7r3aNztCU/edit` | Raw Google Forms sync. Note: Three facilitators (`Karen Mel Lerit`, `Leony Layaguin`, `Ashlee Skye Durano`) each labeled their first learner `C-1`, producing 16 distinct timestamped rows (`C-1..C-14` with three `C-1` entries). `Cleaned Master (Responses)` re-indexed these 16 rows sequentially as `C-1..C-16` without altering any response value. |
| **CSV Tabulation** | `ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv` | `03_Testing_and_QA/ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv` | De-identified flat CSV export of all 25 respondent records for statistical verification. |
| **Google Form 1 (Child)** | `playIT - Child Smileyometer Form` | `https://docs.google.com/forms/d/1eBuC-_TyWDQUlvcYewqGcI1pX4fTxlet-AJZvS9TdBA/edit` | 4-item pictorial affect survey (`C-01`–`C-16`). |
| **Google Form 2 (Parent)** | `playIT - parent/teacher evaluation` | `https://docs.google.com/forms/d/1vt9CBaW5ExV8XK82PaOxB7JQbj8Xk3tZOACpSZrgWg0/edit` | 10-item SUS + 7-item Accessibility + 2-item Grade-Fit (`P-1`–`P-5`). |
| **Google Form 3 (Teacher)** | `playIT - Teacher Pedagogical Checklist` | `https://docs.google.com/forms/d/1SrL37ei64ZJhqQfmof-P-kBDeiG2JMCYw-jsmSwM_No/edit` | 12-item DepEd Grade 1 literacy rubric (`T-1`–`T-4`). |
| **Field Evidence (Child)** | `evidence/child/` (16 child folders/items, 8 `.jpg` photos, 2 `.mp4` videos) | `https://drive.google.com/drive/folders/1Zi7MIf2eJ-aq9eEzCRQC5fdDeksRdheo` | Includes `child1`–`child16`, `video proof.mp4`, and `video proof 2.mp4`. |
| **Field Evidence (Adult & School)** | `evidence/parent/guardian/`, `teacher1`, `school-permission.HEIC` | `https://drive.google.com/drive/folders/1q68HBirPqxcfcw5WRw_6yx-14JOe8tNU` | School principal permission letter (`school-permission.HEIC`) and evaluator session photos. |

---

## 3. Complete Itemized Survey Responses ($N = 25$)

### 3.1 Cohort 1 — Child Smileyometer Itemized Responses ($n = 16$)

Each child respondent (`C-1` through `C-16`) rated four questions on the 5-point pictorial Smileyometer scale (`5 = Top-Box / Super Fun or Super Easy`, `4 = Second-Box / Good or Pretty Easy`, `3 = Neutral / Okay`, `2 = Not Really`, `1 = Not at All`) immediately after completing a guided gameplay session:

| Child ID | Raw Sheet ID | Facilitator | Q1: How fun was the game? | Q2: Was it easy to play? | Q3: Did you like the characters? | Q4: Would you play it again? |
|:---:|:---:|---|:---:|:---:|:---:|:---:|
| **`C-01`** | `C-1` | Karen Mel Lerit | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-02`** | `C-1` (2nd) | Leony Layaguin | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-03`** | `C-2` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-04`** | `C-3` | Joy Flores | `5` (Top-Box) | `4` (Second-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-05`** | `C-4` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-06`** | `C-5` | Joy Flores | `5` (Top-Box) | `4` (Second-Box) | `4` (Second-Box) | `5` (Top-Box) |
| **`C-07`** | `C-6` | Joy Flores | `5` (Top-Box) | `4` (Second-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-08`** | `C-7` | Joy Flores | `4` (Second-Box) | `4` (Second-Box) | `5` (Top-Box) | `4` (Second-Box) |
| **`C-09`** | `C-8` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `4` (Second-Box) |
| **`C-10`** | `C-9` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-11`** | `C-1` (3rd) | Ashlee Skye Durano | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-12`** | `C-10` | Joy Flores | `5` (Top-Box) | `3` (Neutral) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-13`** | `C-11` | Joy Flores | `5` (Top-Box) | `4` (Second-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-14`** | `C-12` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-15`** | `C-13` | Joy Flores | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) | `5` (Top-Box) |
| **`C-16`** | `C-14` | Joy Flores | `5` (Top-Box) | `4` (Second-Box) | `5` (Top-Box) | `5` (Top-Box) |

#### Statistical Summary of Cohort 1 (with 95% Wilson Score Confidence Intervals, $n = 16$)

| Smileyometer Dimension | Top-Box (`5`) Count & % | 95% Wilson CI (Top-Box) | Second-Box (`4`) | Neutral (`3`) | Negative (`1`–`2`) | Positive Affect (`4` or `5`) |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Q1: Enjoyment** (*How fun was the game?*) | **15 / 16 (`93.8%`)** | `71.7% – 98.9%` | 1 (`6.3%`) | 0 (`0.0%`) | 0 (`0.0%`) | **16 / 16 (`100.0%`)** |
| **Q2: Perceived Ease** (*Was it easy to play?*) | **9 / 16 (`56.3%`)** | `33.2% – 76.9%` | 6 (`37.5%`) | 1 (`6.3%`) | 0 (`0.0%`) | **15 / 16 (`93.8%`)** |
| **Q3: Character Appeal** (*Did you like the characters?*) | **15 / 16 (`93.8%`)** | `71.7% – 98.9%` | 1 (`6.3%`) | 0 (`0.0%`) | 0 (`0.0%`) | **16 / 16 (`100.0%`)** |
| **Q4: Replay Intent** (*Would you play it again?*) | **14 / 16 (`87.5%`)** | `64.0% – 96.5%` | 2 (`12.5%`) | 0 (`0.0%`) | 0 (`0.0%`) | **16 / 16 (`100.0%`)** |

**Key Synthesis Insight from Cohort 1:** While enjoyment (`93.8%` Top-Box) and character appeal (`93.8%` Top-Box) were near ceiling, **Perceived Ease (`Q2`) dropped to `56.3%` Top-Box** (with 6 learners selecting Second-Box and `C-12` selecting Neutral). Classroom observation confirmed that this friction occurred specifically during the **Say It** microphone interaction (children did not know when the mic had heard them or what mouth shape to make after a rejected attempt) and the **text-keyboard onboarding screen** (Grade 1 learners could not type their names).

---

### 3.2 Cohort 2 — Parent & Supervising Teacher SUS, Accessibility & Grade-Fit ($n = 5$)

#### Table 3.2A: Itemized System Usability Scale (`SUS-01` to `SUS-10`) Responses & Brooke Scoring

Standard Brooke (1996) formula: $\text{SUS} = 2.5 \times \left(\sum_{k \in \{1,3,5,7,9\}} (x_k - 1) + \sum_{k \in \{2,4,6,8,10\}} (5 - x_k)\right)$.

| Respondent ID | `SUS-01` (Use Freq +) | `SUS-02` (Complex -) | `SUS-03` (Easy +) | `SUS-04` (Tech Help -) | `SUS-05` (Integrated +) | `SUS-06` (Inconsistent -) | `SUS-07` (Learn Quick +) | `SUS-08` (Cumbersome -) | `SUS-09` (Confident +) | `SUS-10` (Learn Lot -) | **Computed SUS Score** |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **`P-1`** | `4` | `1` | `5` | `2` | `3` | `3` | `4` | `1` | `4` | `2` | **`77.50`** |
| **`P-2`** | `5` | `5` | `4` | `4` | `4` | `5` | `5` | `5` | `5` | `4` | **`50.00`** |
| **`P-3`** | `4` | `1` | `3` | `1` | `4` | `2` | `5` | `2` | `4` | `2` | **`80.00`** |
| **`P-4`** | `4` | `1` | `5` | `1` | `4` | `2` | `5` | `2` | `4` | `2` | **`85.00`** |
| **`P-5`** | `5` | `3` | `5` | `1` | `5` | `1` | `5` | `3` | `4` | `2` | **`85.00`** |
| **Item Mean** | **`4.40`** | **`2.20`** | **`4.40`** | **`1.80`** | **`4.00`** | **`2.60`** | **`4.80`** | **`2.60`** | **`4.20`** | **`2.40`** | **Mean = `75.50`** (`SD = 14.62`, Median = `80.00`, 95% $t$-CI `57.3–93.7`) |

*Methodological Note on Respondent `P-2` (`SUS = 50.00`):* Inspection of `P-2`'s raw vector (`[5, 5, 4, 4, 4, 5, 5, 5, 5, 4]`) reveals classic **acquiescence / straight-lining bias** on alternating-polarity items: `P-2` rated all five positive items `4` or `5` (and rated Grade-1 Appropriateness `5/5`), but also marked `4` or `5` ("Strongly Agree") on all five reverse-coded negative items. In accordance with academic reporting standards (`playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` §3.1), **all 5 respondents are retained in the official Mean SUS of `75.50`**, which exceeds the standard industry benchmark of `68.0` (Sauro & Lewis, 2016, Grade `B`), while the small sample size ($n = 5$) is transparently reported alongside the expanded Round 2 target ($n = 10$–$12$ parents).

#### Table 3.2B: Itemized Accessibility (`ACC-01` to `ACC-07`) & Grade-Fit (`FIT-01` to `FIT-02`) Responses

| Respondent ID | `ACC-01` Large Text? | `ACC-02` Audio Narration? | `ACC-03` Simple Tap (No Drag)? | `ACC-04` Color Contrast? | `ACC-05` Grade 1 Language? | `ACC-06` Hearing Options? | `ACC-07` Motor Ease? | `FIT-01` Appropriate for Grade 1 (1–5) | `FIT-02` Recommend to Others (1–5) |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **`P-1`** | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | **`No`** | `4.0` | `4.0` |
| **`P-2`** | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `5.0` | `4.0` |
| **`P-3`** | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | **`No`** | `Yes` | `5.0` | `5.0` |
| **`P-4`** | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | **`No`** | `Yes` | `5.0` | `4.0` |
| **`P-5`** | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `Yes` | `4.0` | `5.0` |
| **Aggregate** | **5/5 (`100%`)** | **5/5 (`100%`)** | **5/5 (`100%`)** | **5/5 (`100%`)** | **5/5 (`100%`)** | **3/5 (`60%`)** | **4/5 (`80%`)** | **Mean `4.60 / 5.00`** | **Mean `4.40 / 5.00`** |

**Key Synthesis Insight from Cohort 2:** Core visual and linguistic accessibility items (`ACC-01` through `ACC-05`) achieved **100% (`5/5`) approval**. However, **40% (`2/5`, `P-3` and `P-4`) flagged `ACC-06` ("No" on options for learners with hearing difficulties / visual articulation cues)**, and **20% (`1/5`, `P-1`) flagged `ACC-07` ("No" on easy navigation for learners with motor difficulties)**. These two flags directly motivated the **ArticulationCue mouth/tongue visual diagram (`Card 16`)**, the **4-state high-contrast Microphone Button (`Card 06`)**, and the **$\ge 64\text{ dp}$ child touch target enforcement (`NFR-ergonomics-01`)**.

---

### 3.3 Cohort 3 — DepEd Certified Grade 1 Reading Teacher Pedagogical Checklist ($n = 4$)

Four certified Department of Education (DepEd) Grade 1 Reading Educators evaluated PlayIT across 12 curriculum and pedagogical criteria (`PED-01` through `PED-12`):
- **`T-1`:** Ms. Leony Layaguin (`leony.layaguin01@deped.gov.ph`)
- **`T-2`:** Ms. Elena L. Bien (`elena.bien@deped.gov.ph`)
- **`T-3`:** Teacher Joy Flores (`flores.joy1824@gmail.com`)
- **`T-4`:** Ms. Luz Bajar Rapsing (`luzbajarrapsing@gmail.com`)

| Item ID | Pedagogical Evaluation Criterion | `T-1` (L. Layaguin) | `T-2` (E. Bien) | `T-3` (J. Flores) | `T-4` (L. Rapsing) | Agreement % (`Yes`) |
|:---:|---|:---:|:---:|:---:|:---:|:---:|
| **`PED-01`** | Content aligns with DepEd Grade 1 literacy competencies | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-02`** | Letter sequence follows a logical phonics progression | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-03`** | Difficulty level is appropriate for Grade 1 beginning readers | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-04`** | Learning objectives are clear and consistently presented | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-05`** | App provides immediate and corrective feedback | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-06`** | App uses scaffolding (hints, repetition) to support learning | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-07`** | App actively involves the child (not passive watching) | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-08`** | **Letter sounds are pronounced correctly** | **`No`** | `Yes` | **`Qualified` ("There are not properly pronounced.")** | `Yes` | **2 / 4 (`50%`) — CRITICAL DEFECT** |
| **`PED-09`** | Examples and images are familiar to Filipino children | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-10`** | Language is appropriate for Grade 1 learners in the Philippines | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-11`** | App supports early literacy development | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |
| **`PED-12`** | App could be used as a supplementary tool in Grade 1 | `Yes` | `Yes` | `Yes` | `Yes` | **4 / 4 (`100%`)** |

#### Verbatim Qualitative Critiques from DepEd Grade 1 Educators (`T-1` to `T-4`)

| Educator ID & Name | Verbatim Comment Recorded in Google Forms (`Cleaned Master (Responses)`) |
|---|---|
| **`T-1` — Ms. Leony Layaguin** (`leony.layaguin01@deped.gov.ph`) | *"The app is very useful and engaging for Grade 1 learners. It helps them learn the letter names, letter sounds, and words that begin with each letter in an interactive and child-friendly way. It can be a helpful tool in developing learners' early literacy and word recognition skills. **One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.' Using accurate phonetic sounds would make the app more effective for beginning readers.**"* |
| **`T-2` — Ms. Elena L. Bien** (`elena.bien@deped.gov.ph`) | *"Application is appropriate for Grade 1 learners"* |
| **`T-3` — Teacher Joy Flores** (`flores.joy1824@gmail.com`) | *"Good job on doing the app. It's a helpful tool for those beginning readers. Children enjoys it as it is digitized and interactive. **My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it.** Overall. great job! Hope you also develop reading app for advanced readers."* |
| **`T-4` — Ms. Luz Bajar Rapsing** (`luzbajarrapsing@gmail.com`) | *"The app is appropriate for grade 1 learners and is easy to navigate."* |

---

## 4. Diagnostic Synthesis & Refactoring Traceability Matrix (`Findings #1–#5` $\rightarrow$ `Cards 01–32`)

Triangulating all 25 responses across the three cohorts yielded **five core empirical findings** that governed the Capstone 2 software refactoring (`refactor/hear-say-it`, Cards 00 through 32):

| Finding ID & Empirical Evidence | Root Cause in MVP Prototype | Refactored Engineering Solution (Cards 00–32) | Verification & STD Test Evidence |
|---|---|---|---|
| **Finding #1 (P0 Critical): Phoneme Pronunciation Defect** (`PED-08` = 50% agreement; `T-1` & `T-3` verbatim critiques on `/m/` sounding like `"em"` or `"ma"`) | MVP audio clips were generated via generic TTS that read orthographic letter names (`"em"`) or appended intrusive schwa vowels (`"muh"`, `"buh"`). | **1.** Rebuilt all 26 phoneme (`audio/phonemes/`), 26 word (`audio/words/`), and 149 tutor clips (`audio/tutor/`) using **Kokoro-82M**, **Chatterbox-Turbo** (`"Mmm!"`), and **ElevenLabs** (`a, i, e, o, u` onset rhythm + `am, tub, yam`), gated by `docs/audio-release/*/manifest.json` (`Cards 09, 09b, 09c, 15b, 20`).<br>**2.** Redesigned **Hear It (`FR-02`, `Card 04`)** into a 4-step sequence: Step 1 `"Listen!"` $\rightarrow$ Step 2 pure `/m/` $\times 3$ (700 ms pauses) $\rightarrow$ Step 3 exemplar word `"mouse"` $\rightarrow$ Step 4 pure `/m/` closure, with `Next` gated until Step 4 completes. | `AudioAssetIntegrityTest`, `HearItViewModelTest`, `TC-HI-01` to `TC-HI-04`, and `audio_release_check.txt` (**PASS**). |
| **Finding #2 (P0 High): Say It Speech Recognition & Child Ease Friction** (Child `Q2` Perceived Ease = `56.3%` Top-Box; Vosk accepted letter names and gave generic error prompts) | MVP `Say It` used unconstrained vocabulary matching, deducted hearts on speech failures, and lacked tiered scaffolding when children said the letter name (`"em"`) instead of `/m/`. | **1.** Refactored `VoskSpeechRecognitionService` and `SpeechValidator` (`Cards 01, 02, 03, 05, 07, 17, 19`) to construct explicit JSON grammar sets `[target, foils..., "[unk]"]` (`ClassifyResult`: `Correct`, `LetterName`, `AddedVowel`, `OtherWrong`, `Silence`).<br>**2.** Implemented the **3-Attempt Scaffolding Ladder (`FR-03`)**: Attempt 1 gives error-specific feedback (`letter_name` vs. `added_vowel`), Attempt 2 displays the **`ArticulationCue`** mouth diagram beside the mic, and Attempt 3 triggers **"Echo Together"** and auto-advances without losing hearts (**zero heart loss in Say It**).<br>**3.** Built the **4-state Microphone Button (`Card 06`)** (`Idle`, `Listening`, `Heard`, `Result`) with a 10 s idle timer. | `SpeechValidatorTest`, `VoskGrammarClassificationTest`, `SayItViewModelTest`, `ArticulationCueTest`, `TC-SI-01` to `TC-SI-09` (**PASS**). |
| **Finding #3 (P1 High): Visual Articulation & Motor Accessibility** (`ACC-06` = 60% Yes; `ACC-07` = 80% Yes; text-typing onboarding barrier) | Deaf/hard-of-hearing or struggling learners had no visual mouth-position cue when learning phonemes, and onboarding required typing a name on a tiny keyboard. | **1.** Built **`ArticulationCue.kt` (`Card 16`)** with 9 mouth/tongue illustration groups (`lips_together`, `teeth_on_lip`, `open_wide`, `tongue_behind_teeth`, etc.) for all 26 letters.<br>**2.** Replaced text-input onboarding with **Avatar-Only Onboarding (`FR-01`, `Card 08`)** (`ProfileSelectScreen` tap-to-create with 6 avatars; parent can edit name in Parent Zone).<br>**3.** Enforced $\ge 64\text{ dp}$ child touch targets across all gameplay controls (`Card 24`). | `ArticulationCueTest`, `ProfileSelectViewModelTest`, `ChildScreenLayoutTest` (Roborazzi across `360x640` and `fontScale=1.3`) (**PASS**). |
| **Finding #4 (P1 High): Curriculum Scope, Blend It Decodability & Card Art** (28-letter `Ñ`/`NG` scope gap; sight words `AIM/BEE/TOY/BOY/ZOO` in `Blend It`; inconsistent picture art) | `Ñ` and `NG` had no English phonics exemplars (`PENDING SME REVIEW`), `Blend It` included 5 non-decodable words, and tile dealing could appear already solved. | **1.** Scoped curriculum to **26 English letters across 7 chapters (`FR-05`, `Card 14`)**.<br>**2.** Replaced 5 sight words with decodable CVC/VC words (`AM, SUM, TUB, YAM, ZIP`, `Card 15`) and enforced **deranged tile dealing (`Card 11`, `TC-BLD-02`)**.<br>**3.** Shipped **80 child-safe WebP illustrations (`Card 26c`)** and unified stage/map/PDF color tokens (`Card 32`, zero harsh `#D63031` red). | `SeedDataIntegrityTest`, `BlendItViewModelTest`, `FindItViewModelTest`, `TC-BLD-01` to `TC-BLD-05` (**PASS**). |
| **Finding #5 (P2 Medium): Gamification Heart Recovery & Star Scoring Bugs** (`INC-03`, `INC-04` defects in heart clamping and star persistence) | Game-over heart reset and star accuracy calculation had edge-case defects, and celebration screens lacked distinct star tiers. | **1.** Fixed `HeartManager` 5-heart cap and 3-heart recovery on retry (`Card 10`).<br>**2.** Fixed `StarCalculator` 1–3 star persistence (`bestStars = max(existing, new)`, `Card 10`) and distinct 1/2/3-star visual celebration states (`Card 12`). | `HeartManagerTest`, `StarCalculatorTest`, `LessonCompletionCoordinatorTest`, `TC-GAM-01` to `TC-GAM-04` (**PASS**). |

---

## 5. Methodological Bridge to Week 12 Final User Validation (`04_Final-User_Validation/`)

During our internal audit of the Round 1 MVP survey (`playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` §2.3), we identified five structural limitations in the Round 1 instruments that have now been systematically corrected in the **Round 2 Final User/Stakeholder Validation Package (`04_Final-User_Validation/`)** prepared for the Adviser Presentation:

| Round 1 Survey Limitation | How Round 2 (`04_Final-User_Validation/`) Resolves It | Mapped Round 2 Instrument & SMART Goal |
|---|---|---|
| **1. Opinion-Only Items vs. Behavioral Task Metrics:** Round 1 asked whether users *liked* the app (`Smileyometer`) but did not record objective task completion rates or times. | Round 2 pairs a **5-Scenario Standardized Walkthrough (`Instrument D`)** with a **Silent-Observer Behavioral Log (`Instrument A`)** and **Automated Room SQLite Telemetry (`Instrument C` via `export_validation_telemetry.py`)** to measure exact task completion rates ($\ge 85\%$) and sub-lesson durations ($\le 5.0\text{ min}$). | `Instrument A`, `Instrument C`, `Instrument D` $\rightarrow$ **SMART Goals `SG-01`, `SG-04`** |
| **2. Binary `PED-08` Phoneme Check (`Yes`/`No`):** Round 1 asked a single binary question (*"Letter sounds are pronounced correctly"*), without auditing individual phoneme clips. | Round 2 **Instrument B (Part B1)** provides a **Clip-by-Clip Teacher Phonetic Audit Sheet** covering all 26 phoneme clips (`audio/phonemes/*.mp3`) and 26 exemplar words, requiring **100% (`26/26`) teacher sign-off** (`Gate 3`). | `Instrument B (Part B1)` $\rightarrow$ **SMART Goal `SG-02`** |
| **3. No Teacher-vs-Vosk Agreement Rubric:** Round 1 did not measure whether Vosk's speech judgments agreed with a human reading teacher. | Round 2 **Instrument B (Part B2)** provides a **Live Say It Teacher-vs-Vosk Confusion Matrix** ($TP, TN, FP, FN$) across $\ge 60$ child utterances to verify **$\ge 80\%$ teacher agreement** and **$\le 15\%$ false-rejection rate**. | `Instrument B (Part B2)` $\rightarrow$ **SMART Goal `SG-03`** |
| **4. Small Parent Sample ($n = 5$) & `P-2` Acquiescence Bias:** Round 1 had only 5 adult SUS respondents, and `P-2` straight-lined alternating items. | Round 2 **Instrument E** expands the parent cohort to **$n = 10$–$12$ parents/guardians**, adds explicit **/+/- polarity anchors** on the form to prevent straight-lining, and adds a **5-item Diagnostic Clarity & Actionability Subscale (`PU-P1`–`PU-P5`)** evaluating the Parent Zone and exported PDF report. | `Instrument E` $\rightarrow$ **SMART Goal `SG-06`** |
| **5. Unvalidated Ad-Hoc Teacher Checklist:** Round 1 used a 12-item `Yes/No` checklist without formal content validity scoring. | Round 2 **Instrument B (Part B3)** uses **Lynn's (1986) 4-Point Content Validity Index (`I-CVI` $\ge 0.83$, `S-CVI/Ave` $\ge 0.90$)** and an adapted **Davis (1989) Technology Acceptance Model (`PU` & `PEOU` $\ge 4.00/5.00$)** rubric. | `Instrument B (Part B3)` $\rightarrow$ **SMART Goal `SG-05`** |
