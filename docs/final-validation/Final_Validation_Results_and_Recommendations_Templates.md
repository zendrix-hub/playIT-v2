# Final Validation Results, Analysis & Recommendations Templates (Week 12 Deliverables 3, 4 & 5)

**Course & Section:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Group Code / Team:** `2627-sem1-it411-56` — Group 56 (`Riva, Zendrix`; `Palis, John James`; `Miel, Kaysean`; `Durano, Ashlee Skye`; `Bien, Erik Samuel`)  
**Adviser:** Mr. Joemarie C. Amparo | **Course Instructor:** Mr. Ralph P. Laviste  
**Deliverable Reference:** Week 12 Deliverables 3, 4 & 5 (`03_Testing_Evidence`, `04_Results_and_Analysis`, `05_Recommendations_and_Improvements`, Due October 31, 2026)  

> [!IMPORTANT]
> **Adviser Approval Gate Reminder:**  
> Per the official IT411 course announcement, **do not populate final field conclusions until Mr. Joemarie C. Amparo has reviewed `SMART_Goal_Matrix`, `Approved_Proposal_and_SMART_Goals`, `Final_Validation_Plan_and_Framework`, and `Final_Validation_Instrument`** during the Adviser Verification Presentation and given approval to proceed with Week 12 Final User/Stakeholder Validation.

---

## Folder 03 Template: `03_Testing_Evidence/TESTING_EVIDENCE_INDEX.md`

### 1. Summary of Executed Validation Sessions
- **Software Version Evaluated:** `playIT v1.2.0-rc2` (`refactor/hear-say-it`, offline production APK)
- **Test Device(s):** Samsung Galaxy A21s (`SM-A217F`, Android 12, 3 GB RAM, Airplane Mode ON)
- **Data Collection Period:** October `[__]` – October `[__]`, 2026 (Post-Adviser Verification Approval)

### 2. Evidence Inventory & Anonymized Session Log

| Evidence Folder / File | Description | Respondent Codes Covered | Linked Instrument |
| :--- | :--- | :--- | :--- |
| `03_Testing_Evidence/Teacher_Audits/` | Completed Gate 3 Blind 26-Phoneme & 33-Word CVC Audit sheets | `T-01` .. `T-05` | `Instrument A` |
| `03_Testing_Evidence/Child_Observation_Logs/` | Completed Silent-Observer Navigation Sheets & Blind Dual-Teacher `Say It` Rating Logs | `C-01` .. `C-20` | `Instrument B` |
| `03_Testing_Evidence/System_Telemetry_CSVs/` | Exported Room SQLite CSV dumps (`say_it_attempts.csv`, `find_it_attempts.csv`, `blend_it_attempts.csv`, `lesson_progress.csv`) & generated A4 PDF Progress Reports | `C-01` .. `C-20` | `Instrument C` |
| `03_Testing_Evidence/Hardware_QA_Logs/` | ADB latency logs, `gfxinfo` 60 FPS dumps, `meminfo` dumps, and 20× force-close persistence log on `SM-A217F` | `QA-01` .. `QA-03` | `Instrument D` |
| `03_Testing_Evidence/Stakeholder_Surveys/` | Child Smileyometer/Exit Choice sheets (`Part E1`) & Teacher/Parent SUS + TAM response records (`Part E2`) | `C-01`..`C-20`, `T-01`..`T-05`, `P-01`..`P-10` | `Instrument E` |

---

## Folder 04 Template: `04_Results_and_Analysis/FINAL_VALIDATION_RESULTS_AND_ANALYSIS.md`

### 1. Respondent Profile & Sample Distribution

| Cohort | Target Role | Actual $N$ | Demographic / Qualification Summary |
| :--- | :--- | :---: | :--- |
| **Cohort 1 (`C-01`..`C-__`)** | Grade 1 Learners (Primary End-Users) | `[__]` | Ages 6–7 (`[__]` age 6, `[__]` age 7); Grade 1 emergent readers. |
| **Cohort 2 (`T-01`..`T-__`)** | DepEd Grade 1 Reading Teachers | `[__]` | Mean teaching experience: `[__]` years in Grade 1 Reading / MTB-MLE / English. |
| **Cohort 3 (`P-01`..`P-__`)** | Parents / Primary Caregivers | `[__]` | Parents/guardians of Grade 1 learners supervising home reading practice. |
| **Cohort 4 (`QA-01`..`QA-03`)** | Technical / QA Evaluators | `3` | Hardware & accessibility benchmark evaluators on `SM-A217F`. |

### 2. Empirical Results by Research Question & SMART Goal

#### 2.1 `RQ1` — Pedagogical & Acoustic Integrity (`SMART-01`, `SMART-02`, `HI-1`, `HI-2`)
- **Gate 3 Blind Phoneme Audit (`Instrument A` Part A1):** `[__] / 26` (`[___]%`) phoneme clips transcribed accurately and rated schwa-free (`>= 2/3` teachers).
- **Decodable CVC Word Bank Audit (`Instrument A` Part A2):** `[__] / 33` (`[___]%`) Blend It words verified as 100% cumulatively decodable.
- **Child `Hear It` Independent Completion (`Instrument B` Part B1):** `[__] / [__]` sessions (`[___]%`, 95% Wilson CI `[___%, ___%]`) completed `Hear It` with `0` adult prompts *(Target: $\ge 90.0\%$).*

#### 2.2 `RQ2` — Offline Speech Recognition & Articulatory Scaffolding (`SMART-03`, `SI-1`–`SI-4`)

| Metric | Formula | Empirical Result | 95% Wilson CI / Spread | Target Criterion | Verdict |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Teacher-ASR Agreement ($P_{\text{agree}}$)** | $(TP + TN) / N_{\text{trials}}$ | `[___]%` (`[__]/[__]`) | `[___%, ___%]` | **$\ge 80.0\%$** | `[PASS / FAIL]` |
| **False-Reject Rate ($P_{\text{FR}}$)** | $FN / (TP + FN)$ | `[___]%` (`[__]/[__]`) | `[___%, ___%]` | **$\le 15.0\%$** | `[PASS / FAIL]` |
| **Dual-Teacher Inter-Rater $\kappa$** | Cohen's $\kappa$ | `[0.__]` | — | **$\ge 0.70$** | `[PASS / FAIL]` |
| **3-Attempt Lesson Cleared Rate (`SI-1`)** | Cleared $\le 3$ att. / Total | `[___]%` | `[___%, ___%]` | **$\ge 80.0\%$** | `[PASS / FAIL]` |
| **Attempt 2/3 Mouth-Cue Recovery (`SI-2`)** | Att. 2–3 pass $\mid$ Att. 1 miss | `[___]%` | `[___%, ___%]` | Documented $> 0\%$ | `[PASS / FAIL]` |
| **`Say It` P90 Latency (`D-01`)** | $\text{P90}(t_{\text{feedback}} - t_{\text{EOS}})$ | `[___] ms` | `P50: [___] ms` | **$\le 500\text{ ms}$** | `[PASS / FAIL]` |

#### 2.3 `RQ3` — Child Usability, Discrimination & Blending Mastery (`SMART-04`, `SMART-05`, `O3`)
- **Zero-Prompt Child Screen Navigation (`O3`):** `[___]%` (`[__]/[__]` transitions) completed without adult intervention *(Target: $\ge 85.0\%$).*
- **`Find It` First-Tap Accuracy & Latency (`SMART-04`, `D-02`):** First-tap accuracy `[___]%`; P90 tap latency `[___] ms` *(Target: $\le 300\text{ ms}$).*
- **`Blend It` CVC Word Completion (`SMART-04`):** `[___]%` of learners solved all 3 group CVC words with $\ge 2\star$ mastery.
- **Child Affect & Voluntary Replay (`Instrument E` Part E1):** `[___]%` selected Happy Smile (`3/3`); `[___]%` voluntarily chose to play another letter when offered the exit prompt.

#### 2.4 `RQ4` — Hardware Performance, Offline Persistence & Privacy (`SMART-06`, `O4`, `O5`)
- **20-Cycle Force-Close Persistence (`D-05`):** `[20 / 20]` (`100%`) cycles retained exact SQLite state in Airplane Mode.
- **Airplane-Mode Module Operability (`D-06`):** `[4 / 4]` learning modules + offline PDF export (`100%`) operated without network connectivity.
- **Frame Stability & Memory (`D-04`):** Cold start `[___] ms` ($\le 2500\text{ ms}$); Jank `[___]%` ($\le 10\%$); Peak PSS RAM `[___] MB` ($\le 200\text{ MB}$).

#### 2.5 `RQ5` — Stakeholder Usability (SUS), TAM & Diagnostic Utility (`SMART-07`, `O6`)

| Stakeholder Scale / Dimension | Teachers ($n = [__]$) Mean (SD) | Parents ($n = [__]$) Mean (SD) | Combined ($N = [__]$) Mean (SD) | Target Criterion | Verdict |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Brooke System Usability Scale (SUS, $0\text{–}100$)** | `[___._] ([_._])` | `[___._] ([_._])` | **`[___._] ([_._])`** | **$\ge 75.0$** | `[PASS / FAIL]` |
| **TAM Perceived Usefulness (`PU1`–`PU4`, $1\text{–}5$)** | `[_. __] ([_.__])` | `[_. __] ([_.__])` | **`[_. __] ([_.__])`** | **$\ge 4.00 / 5.00$** | `[PASS / FAIL]` |
| **TAM Perceived Ease of Use (`PEOU1`–`PEOU3`, $1\text{–}5$)** | `[_. __] ([_.__])` | `[_. __] ([_.__])` | **`[_. __] ([_.__])`** | **$\ge 4.00 / 5.00$** | `[PASS / FAIL]` |
| **Pedagogical Alignment (`PA1`–`PA5`, $1\text{–}5$)** | `[_. __] ([_.__])` | N/A | **`[_. __] ([_.__])`** | **$\ge 4.00 / 5.00$** | `[PASS / FAIL]` |
| **Adult Diagnostic & PDF Task Success (`Tasks A1–A4`)** | `100%` | `100%` | **`100%`** | **$100\%$** | `[PASS / FAIL]` |

### 3. Comparison: Initial MVP Validation (Weeks 1–2) vs. Final System Validation (Week 12)

| Evaluation Dimension | Initial MVP Baseline (Weeks 1–2, $N=18$) | Final Refactored System (Week 12, `v1.2.0-rc2`) | Net Improvement & Engineering Root Cause |
| :--- | :---: | :---: | :--- |
| **Phoneme Audio Purity (Gate 3)** | `3.10 / 5.00` (Schwa `/ʌ/` on stop consonants) | **`[___]%` (26/26 Pass)** | Audited human/Kokoro/Chatterbox/ElevenLabs clips (`Cards 09a–09c, 15b, 19`). |
| **`Say It` ASR Child Agreement** | `2.85 / 5.00` (High false-rejects & foil acceptance) | **`[___]%` Agreement / `[___]%` FRR** | Constrained `SpeechValidator` grammar + foil rejection + non-punitive 3-attempt scaffolding (`Cards 02, 05, 13, 21`). |
| **Word Blending (`Blend It`)** | Absent in MVP (`0` words) | **33 Decodable CVC/VVC Words across 6 Groups** | Full `BlendItScreen` + non-solved tile deal guard (`Cards 08, 15, 25, 31f`). |
| **Child Touch & Stage Ergonomics** | `3.20 / 5.00` (Sub-48dp buttons, competing gradients) | **100% $\ge 64\text{ dp}$ targets + Duolingo ABC Grounded Stages** | `LessonScaffold`, 64dp child controls, Argon 4 illustrations, and Card 32 grounded horizon stages. |

---

## Folder 05 Template: `05_Recommendations_and_Improvements/RECOMMENDATIONS_AND_POST_VALIDATION_IMPROVEMENTS.md`

### 1. Issues & Concerns Raised by Users or Stakeholders During Final Validation

| Issue ID | Source Cohort (`C`, `T`, `P`, `QA`) | Module / Screen Affected | Description of Observed Issue or Stakeholder Feedback | Severity (`Critical`, `Major`, `Minor`, `Enhancement`) |
| :---: | :---: | :--- | :--- | :---: |
| **`FV-01`** | `[____]` | `[____]` | `[Populate from Instrument B / Instrument E OE-02 findings]` | `[____]` |
| **`FV-02`** | `[____]` | `[____]` | `[Populate from Instrument B / Instrument E OE-02 findings]` | `[____]` |
| **`FV-03`** | `[____]` | `[____]` | `[Populate from Instrument B / Instrument E OE-02 findings]` | `[____]` |

### 2. Prioritized Recommended Improvements & Corrective Actions Taken

| Priority Rank | Issue ID | Recommended Improvement / Corrective Action | Action Status (`Implemented in Code`, `Documented in SRS/SDD`, `Scheduled for Future Work`) | Verification / Commit Reference |
| :---: | :---: | :--- | :--- | :--- |
| **P1 (High)** | `FV-01` | `[Action description]` | `[Status]` | `[Commit / Test ID]` |
| **P2 (Medium)** | `FV-02` | `[Action description]` | `[Status]` | `[Commit / Test ID]` |
| **P3 (Low)** | `FV-03` | `[Action description]` | `[Status]` | `[Commit / Test ID]` |

### 3. Updates Required to Software, Technical Documentation, or Test Cases
- **Source Code Updates:** `[List any post-validation hotfixes or tuning commits]`
- **SRS / SDD / SPMP / STD / RTM Updates:** `[List section revisions reflecting final validation outcomes]`

### 4. Remaining Scope Limitations & Recommendations for Future Research
1. **Dialectal & Multilingual Expansion (Cebuano / Bisaya & Tagalog MTB-MLE):** While `playIT v1.2.0` targets the 26 English alphabet phonemes and 33 English CVC words using a Marungko-inspired sequence, future iterations can add dedicated Bisaya and Filipino orthography modules.
2. **Longitudinal Classroom Retention Study:** Extending the 7-day retention window (`RetentionCalculator`) to a full 8-week DepEd grading quarter across multiple public schools to measure longitudinal reading fluency gains (EGRA / Phil-IRI alignment).
3. **Adaptive Acoustic Calibration for High-Noise Public Classrooms:** Expanding the ambient noise detector in `SayItViewModel` with adaptive SNR thresholds for 40-student classrooms without external lapel microphones.
