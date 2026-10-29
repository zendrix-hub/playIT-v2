# Final Validation Results and Statistical Analysis Report (`04_Results_and_Analysis`)

**Course & Section:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Group Code / Team:** `2627-sem1-it411-56` — Group 56 (`Riva, Zendrix`; `Palis, John James`; `Miel, Kaysean`; `Durano, Ashlee Skye`; `Bien, Erik Samuel`)  
**Adviser:** Mr. Joemarie C. Amparo | **Course Instructor:** Mr. Ralph P. Laviste  
**Deliverable Reference:** Week 12 Deliverables 3, 4 & 5 (`03_Testing_Evidence`, `04_Results_and_Analysis`, `05_Recommendations_and_Improvements`, Due October 31, 2026)  

> [!IMPORTANT]
> **Adviser Approval Gate Reminder:**  
> Per the official IT411 course announcement, **do not populate final field conclusions until Mr. Joemarie C. Amparo has reviewed `SMART_Goal_Matrix`, `Approved_Proposal_and_SMART_Goals`, `Final_Validation_Plan_and_Framework`, and `Final_Validation_Instrument`** during the Adviser Verification Presentation and given approval to proceed with Week 12 Final User/Stakeholder Validation.

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

