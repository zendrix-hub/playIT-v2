# PlayIT — Adviser Consultation Briefing & MVP Validation Protocol
**Course:** IT411 — Capstone & Research 2 | SY 2026–2027  
**Date of Consultation:** September 13, 2026  
**Document Purpose:** Presentation Briefing for Capstone Adviser to Obtain Validation Go-Signal

---

## 1. Executive Consultation Pitch (3-Minute Overview for Adviser)

> *"Good day, Ma'am/Sir! For PlayIT's Capstone 2 MVP Validation, our goal is not just testing whether the software runs, but validating its pedagogical appropriateness, early learner usability, and caregiver acceptance before we lock in final development.*  
> 
> *Based on IT411 guidelines, we have synthesized a **Tripartite Multi-Stakeholder Framework**:*
> 1. ***ISO 9241-11*** *for child task effectiveness and usability;*
> 2. ***UPA (Usability, Pedagogy, Accessibility)*** *for DepEd reading specialists to evaluate Marungko sequence fidelity;*
> 3. ***TAM (Technology Acceptance Model)*** *for parents to evaluate home-based utility and dashboard practicality.*
> 
> *We have structured a multi-role Google Form and observation rubric targeting **N = 30 respondents** across 4 cohorts (12 learners, 10 parents, 5 teachers, 3 IT evaluators). Our deployed APK is packaged, verified, and running 100% offline with zero external dependencies.*  
> 
> *We seek your approval on this validation design and our pedagogical adaptation to 26 English letters so we can commence field trials this week."*

---

## 2. Core Alignment Points to Present to Your Adviser

### A. The 26-Letter Marungko Adaptation (Crucial Decision)
* **What changed:** We adapted the traditional 28-letter DepEd Alpabetong Filipino Marungko sequence to **26 letters** by excluding `NG` and `Ñ`.
* **Why this is justified:**
  1. **Curricular Subject Alignment:** PlayIT is specifically an **English phonics** application. In English, `ñ` does not exist, and `ng` is a nasal consonant digraph (`/ŋ/`), not an individual single-letter grapheme. Teaching them as single letters causes cognitive confusion for Grade 1 children learning English decoding.
  2. **Vosk Speech Recognition Precision:** Testing demonstrated that young Filipino children struggle with isolated non-English grapheme vocalization on offline ASR, whereas whole-word recognition (e.g., *Mouse*, *Sun*) achieves high accuracy ($\ge 75\%$).
  3. **Preserved Marungko Structure:** The 26 letters still follow the exact frequency-based Marungko progression organized into **7 thematic chapters** ($4, 4, 4, 3, 4, 3, 4$ letters), each ending with a decodable CVC *Blend It* milestone challenge (33 words total).

---

### B. The 30-Respondent Cohort Distribution

| Respondent Group | Sample Size | Primary Evaluation Focus | Specific Instrument Used |
|---|:---:|---|---|
| **Early Learners (Ages 5–7)** | **12** | Task completion, ASR voice interaction, visual appeal, smiley affective rating. | Instrument 3 (Child Observational Rubric + 3-Point Visual Smiley Scale). |
| **Parents / Primary Caregivers** | **10** | Ease of home use, Parent Dashboard clarity, security math lock, offline practicality. | Instrument 2 (5-Point Likert TAM & Usability Questionnaire). |
| **DepEd Grade 1 Teachers / SMEs** | **5** | Marungko curricular fidelity, phoneme modeling accuracy, CVC decodability, 26-letter suitability. | Instrument 1 (Pedagogical Quality Checklist & Qualitative Interview). |
| **Technical / IT Evaluators** | **3** | Offline Room DB persistence, Vosk latency ($\le 0.5\text{s}$), 60 FPS animation stability. | Instrument 5 (ISO 25010 Software Quality Rubric). |
| **Total Target** | **30** | **Comprehensive Multi-Stakeholder Evidence Base** | **Unified Role-Branching Google Form** |

---

### C. SMART Objectives Summary Table

- **S (Specific):** Validate learner task completion ($\ge 85\%$) across *Hear It*, *Say It*, *Find It*, and *Blend It*, with first-pass Vosk speech accuracy ($\ge 75\%$).
- **M (Measurable):** Achieve a mean Parent/Teacher SUS score $\ge 75.0$ and TAM Perceived Usefulness rating $\ge 4.2 / 5.0$.
- **A (Achievable):** Pilot testing with 30 target users across physical Android smartphones/tablets (API 26+) in partner kindergarten/Grade 1 cohorts.
- **R (Relevant):** Proves that an offline-first, zero-subscription phonics app effectively addresses Philippine public school early reading remediation.
- **T (Time-Bound):** Complete validation survey next week, synthesize findings into Chapter 4 data, and submit refactored SRS v3.0 & SDD v2.0 by September 19, 2026.

---

## 3. Checklist for Adviser Sign-Off Tomorrow

During your consultation, obtain explicit confirmation on the following:

- [ ] **Validation Framework Approved:** Adviser confirms ISO 9241-11 + UPA + TAM multi-stakeholder model.
- [ ] **Instrument Structure Approved:** Adviser reviews question items in `02_GOOGLE_FORM_INSTRUMENT_SPECIFICATION.md`.
- [ ] **26-Letter Scope Confirmed:** Adviser confirms the exclusion of `ng` and `ñ` for English phonics alignment.
- [ ] **Field Testing Protocol Approved:** Adviser gives the go-signal to begin 30-user testing across target schools and households this week.

---

## 4. Immediate Next Steps Post-Approval

1. **Activate Google Form:** Copy the questions from `02_GOOGLE_FORM_INSTRUMENT_SPECIFICATION.md` into Google Forms and link it to the Google Sheet.
2. **Execute Field Testing:** Conduct the 30 observational and survey sessions using the packaged `playit-debug.apk`.
3. **Capture Validation Evidence:** Take photos/screenshots of testing sessions and observer rubrics for the Google Drive evidence folder.
4. **Transition to Week 3 Refactoring:** Incorporate user feedback into the **SRS v3.0** (adding FR-13 Blend It and FR-14 Multi-Profile) and **SDD v2.0** by September 19.
