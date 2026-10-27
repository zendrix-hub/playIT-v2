# Recommendations and Post-Validation Improvements (`05_Recommendations_and_Improvements`)

**Course & Section:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Group Code / Team:** `2627-sem1-it411-56` — Group 56 (`Riva, Zendrix`; `Palis, John James`; `Miel, Kaysean`; `Durano, Ashlee Skye`; `Bien, Erik Samuel`)  
**Adviser:** Mr. Joemarie C. Amparo | **Course Instructor:** Mr. Ralph P. Laviste  
**Deliverable Reference:** Week 12 Deliverables 3, 4 & 5 (`03_Testing_Evidence`, `04_Results_and_Analysis`, `05_Recommendations_and_Improvements`, Due October 31, 2026)  

> [!IMPORTANT]
> **Adviser Approval Gate Reminder:**  
> Per the official IT411 course announcement, **do not populate final field conclusions until Mr. Joemarie C. Amparo has reviewed `SMART_Goal_Matrix`, `Approved_Proposal_and_SMART_Goals`, `Final_Validation_Plan_and_Framework`, and `Final_Validation_Instrument`** during the Adviser Verification Presentation and given approval to proceed with Week 12 Final User/Stakeholder Validation.

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
