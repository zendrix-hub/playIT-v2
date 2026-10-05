# PlayIT: An Offline-First Gamified Early Literacy Mobile Application Using the DepEd Marungko Approach

## Document: MVP Validation Highlights & Field Evaluation Report
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Target Milestone:** Weeks 1–2 MVP Validation Deliverable (Deliverable 4)  
**Evaluation Period:** September 16–23, 2026  
**Empirical Dataset:** `Master (responses).xlsx` (Synchronized with Google Forms; kept in the access-restricted team folder, not in this repository)  
**Evaluated Cohorts:** N=16 Early Learners, N=5 Parents/Guardians & Supervising Teachers, N=4 DepEd Certified Reading Educators (Total N=25)  
**Document Version:** 1.1 (corrected 2026-10-05: participants coded per RA 10173; SUS statistics recomputed; claims limited to what the data show. Full method and raw tables: `docs/specs/validation-report.md`)  

---

## 1. Executive Summary & Validation Purpose

The MVP Validation activity was conducted during Weeks 1–2 of Capstone 2 to gather diagnostic feedback from primary beneficiaries (Grade 1 learners and caregivers) and domain experts (certified DepEd reading teachers). This was a formative validation: its purpose was to find friction points, phonetic modeling problems and interaction hurdles to guide the **Week 3 Requirements & Design Refactoring (SRS v3.0 / SDD v2.0)**. It was not designed to measure learning outcomes.

Field evaluations used `playit-debug.apk` builds on physical Android smartphones and tablets in classroom, home and community settings. The protocol specified ambient noise of 40 dB or less, checked with the app's built-in indicator (approximate; not a calibrated meter). Across 25 evaluations, the MVP showed:
- **Mean System Usability Scale (SUS) score of 75.5 / 100 (n = 5 caregivers; SD 14.62; 95% CI 57.3–93.7).** This is above the commonly cited benchmark of 68 and at the SMART target of 75, but with five respondents the difference from either value is not statistically significant (one-sample t-test vs 68: t(4) = 1.15, p = .32).
- **Teacher agreement on 11 of 12 pedagogical checklist items (4 of 4 teachers),** including DepEd Grade 1 alignment and the Marungko-based letter sequence.
- **15 of 16 children chose the top face for enjoyment** (93.8%; 95% Wilson CI 72–99%).
- **Crucial diagnostic finding:** 2 of 4 teachers flagged letter-name / schwa intrusion in the phoneme audio (e.g., /m/ sounding like "ma" rather than a held /m/), the main engineering fix for Week 3.

---

## 2. Stakeholder Cohort & Empirical Data Overview

| Stakeholder Cohort | Sample Size (N) | Evaluation Instrument & Live Form Link | Key Measurement Dimension | Testing Modality |
|---|:---:|---|---|---|
| **Early Learners (Ages 5–7)** | **N = 16** | Child Smileyometer Form (4 items)<br>[https://forms.gle/qmVvSp6ATpizXZqn6](https://forms.gle/qmVvSp6ATpizXZqn6) | Enjoyment, perceived ease, character liking, replay intention. | 15-minute gameplay + facilitated post-session rating |
| **Parents & Supervising Teachers** | **N = 5** | Parent/Teacher Evaluation Form (19 items)<br>[https://forms.gle/wce86JFVvUA5qfeJ7](https://forms.gle/wce86JFVvUA5qfeJ7) | Standard 10-Item SUS, 7-Item Accessibility Checklist, Grade 1 fit & recommendation. | Self-administered survey after observing a child use the app |
| **DepEd Grade 1 Teachers & SMEs** | **N = 4** | Teacher Pedagogical Checklist (12 Yes/No items + comments)<br>[https://forms.gle/Jzj4hggVzuacye2Y7](https://forms.gle/Jzj4hggVzuacye2Y7) | Curriculum alignment, sequence logic, phoneme sound accuracy, open comments. | Expert checklist review + written comments |
| **Total Evaluations** | **N = 25** | **3 Google Forms** | **Learner / caregiver / expert perspectives** | **Convenience sample; classroom, home and community sites** |

The planned split was 30 participants including IT evaluators; 25 were reached and no IT evaluators were recruited. Ages 5–7 means some kindergarten learners were included.

---

## 3. Quantitative Validation Findings

### 3.1 Adult System Usability Scale (SUS — Brooke, 1996; N=5)
The SUS was scored with the standard Brooke formula: odd items $(X_i - 1)$, even reverse-scored items $(5 - X_i)$, sum multiplied by 2.5:

$$\text{SUS Score} = \left[ \sum_{i \in \text{odd}} (X_i - 1) + \sum_{j \in \text{even}} (5 - X_j) \right] \times 2.5$$

The respondents were the five parents or supervising teachers who watched a child use the app. Children did not complete the SUS, so this is caregiver-perceived usability.

- **Mean SUS Score:** **75.5 / 100** (sample SD 14.62; 95% CI 57.3–93.7, t distribution, df = 4).
  - Benchmark comparison: above the commonly cited average of 68 and equal to the SMART target of 75. Neither difference is statistically significant with n = 5 (vs 68: t(4) = 1.15, p = .32; vs 75: p = .94). On the Sauro–Lewis curved grading scale, 75.5 corresponds to a **B**.
- **Individual Scores:** P-1 77.5, P-2 50.0, P-3 80.0, P-4 85.0, P-5 85.0.
- **Response-quality note:** P-2 agreed (4 or 5) with all ten items, including all five negatively worded ones, a pattern consistent with acquiescent or careless responding. No exclusion rule was set before data collection, so P-2 is **retained** in the reported mean. As a sensitivity check only, the mean without P-2 is 81.9 (n = 4); this figure is not a result of the study.

### 3.2 Caregiver Acceptance & Fit (5-Point Likert; N=5)
- **Appropriate for Grade 1 Level (`FIT-01`):** **Mean = 4.6 / 5** (responses 4, 5, 5, 5, 4; all five rated 4 or 5).
- **Recommend App to Other Parents & Teachers (`FIT-02`):** **Mean = 4.4 / 5** (responses 4, 4, 5, 4, 5; all five rated 4 or 5).

### 3.3 Accessibility Checklist (Yes/No; N=5)
These are caregiver judgements after one observed session, not measurements.
- **Large Text for Young Learners (`ACC-01`):** 5 of 5 Yes.
- **Audio Narration for Non-Readers (`ACC-02`):** 5 of 5 Yes.
- **Simple Touch (Tap, Not Drag) (`ACC-03`):** 5 of 5 Yes.
- **Sufficient Color Contrast (`ACC-04`):** 5 of 5 Yes (caregiver judgement; contrast ratios were not measured).
- **Appropriate Developmental Language (`ACC-05`):** 5 of 5 Yes.
- **Hearing Difficulty Accommodations (`ACC-06`):** **3 of 5 Yes** — area for improvement (no visual captions or articulation cues).
- **Motor Difficulty Accommodations (`ACC-07`):** **4 of 5 Yes** — recommendation for larger edge controls.

### 3.4 Child Smileyometer Results (5-Face Scale; N=16)

| Question | Top face (Love it) | Second (Good) | Middle (Okay) | Fourth | Lowest | Top face, 95% Wilson CI |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Enjoyment:** "How fun was the game?" | **15** | 1 | 0 | 0 | 0 | 93.8% (72–99%) |
| **Perceived Ease:** "Was it easy to play?" | **9** | 6 | 1 | 0 | 0 | 56.3% (33–77%) |
| **Characters:** "Did you like the characters?" | **15** | 1 | 0 | 0 | 0 | 93.8% (72–99%) |
| **Replay:** "Would you play it again?" | **14** | 2 | 0 | 0 | 0 | 87.5% (64–97%) |

*Interpretation:* Children's ratings were very positive, but young children tend to choose the happiest face (Read & MacFarlane, 2006), and one facilitator, who was also a teacher-evaluator, recorded 13 of the 16 ratings. These results are weak evidence of engagement. Perceived ease was the lowest item: 7 of 16 children (43.8%) did not choose the top face. Together with the facilitator notes on hesitation at the microphone, this points to Say It as the main usability problem.

---

## 4. DepEd Teacher Pedagogical Checklist (N=4)

Four DepEd-certified Grade 1 reading teachers (T-1 to T-4) answered 12 Yes/No items:

| Checklist Criterion | Teacher Responses | Finding & Implication |
|---|:---:|---|
| **PED-01:** DepEd Grade 1 literacy competencies alignment | 4 Yes / 0 No | Teachers judged the content aligned with Grade 1 competencies. |
| **PED-02:** Phonics progression sequence logic (Marungko) | 4 Yes / 0 No | Supports the 7-chapter sequence starting with m, s, a, i. |
| **PED-03:** Beginning reader difficulty level appropriateness | 4 Yes / 0 No | Difficulty judged appropriate for beginning readers. |
| **PED-04:** Clear and consistent learning objectives | 4 Yes / 0 No | The Hear, Say, Find, Blend cycle reads as a predictable structure. |
| **PED-05:** Immediate and corrective feedback delivery | 4 Yes / 0 No | Feedback judged immediate and encouraging. |
| **PED-06:** Scaffolding (hints, repetition) to support learning | 4 Yes / 0 No | Hints and repetition judged supportive. |
| **PED-07:** Active child engagement (non-passive viewing) | 4 Yes / 0 No | Speaking and tapping tasks keep the child active. |
| **PED-08:** Letter sounds pronounced correctly | **2 Yes / 2 Flagged** | **Critical gap:** the audio sometimes sounds like the letter name (e.g., "ma") instead of the pure sound /m/. |
| **PED-09:** Examples and images familiar to Filipino children | 4 Yes / 0 No | Pictures judged familiar. |
| **PED-10:** Language appropriate for Grade 1 Philippines | 4 Yes / 0 No | Language judged appropriate. |
| **PED-11:** App supports early literacy development | 4 Yes / 0 No | Teachers judged that the app can support early literacy; effectiveness was not measured. |
| **PED-12:** Supplementary tool readiness for Grade 1 classes | 4 Yes / 0 No | Teachers judged it usable as a supplementary tool. |

With four raters, a 4 of 4 result has a 95% Wilson interval of 51–100%, so these are expert opinions (content validity), not population estimates.

---

## 5. Qualitative Feedback (Teacher Comments, Verbatim)

Participants are identified by code only (Data Privacy Act of 2012, RA 10173; see `docs/specs/validation-report.md`).

> **T-1 (DepEd Grade 1 reading teacher):**  
> *"The app is very useful and engaging for Grade 1 learners. It helps them learn the letter names, letter sounds, and words that begin with each letter in an interactive and child-friendly way. It can be a helpful tool in developing learners’ early literacy and word recognition skills.  
> **One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.' Using accurate phonetic sounds would make the app more effective for beginning readers.**"*

> **T-2 (reading facilitator):**  
> *"Good job on doing the app. It’s a helpful tool for those beginning readers. Children enjoys it as it is digitized and interactive. **My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it.** Overall, great job! Hope you also develop reading app for advanced readers."*

> **T-3 (DepEd educator):**  
> *"Application is appropriate for Grade 1 learners."*

> **T-4 (basic education educator):**  
> *"The app is appropriate for grade 1 learners and is easy to navigate."*

---

## 6. Problems & Issues Encountered During Field Validation

1. **Phonemic Audio Impurity (Letter Name vs. Pure Phoneme):**  
   In *Hear It*, some synthesized clips added a trailing schwa (/ə/) or sounded like the letter name (/m/ as "ma, ma, ma", /b/ as "buh"). T-1 and T-2 flagged this. It can interfere with blending in *Blend It* (e.g., "ma-a-t" instead of "m-a-t").
2. **Speech Production Hesitation (Mic State Ambiguity):**  
   Facilitator notes record children hesitating in *Say It*: the microphone button had no live listening indicator, so children could not tell whether the app was listening.
3. **Accessibility Gap for Hearing-Impaired Learners:**  
   There are no visual articulation cues (mouth shapes) or sound captions, reflected in the 3 of 5 rating on hearing accommodation.
4. **Response Quality in the Adult SUS:**  
   One respondent (P-2) agreed with every item, including the negatively worded ones. Future rounds brief respondents on the mixed item wording and set any exclusion rule before data collection.
5. **Facilitation of Child Ratings:**  
   One facilitator recorded 13 of 16 child ratings and was also a teacher-evaluator; a team member facilitated one child. Round 2 separates these roles.

---

## 7. Positive Aspects to Retain

1. **Offline operation:** the app ran with no internet connection at every site (the build has no network permission). This was observed during testing; no survey item asked about it.
2. **Tap-only interaction and large targets:** all 5 caregivers answered Yes to simple touch (ACC-03) and large text (ACC-01).
3. **Mascot and non-punitive feedback:** 15 of 16 children chose the top face for liking the characters, and teachers judged the feedback encouraging (PED-05).
4. **Letter sequence:** all 4 teachers endorsed the Marungko-based progression (PED-02). The app teaches 26 letters; NG and Ñ are excluded pending SME review. The Blend It word list is being revised so that every word is decodable from letters already taught.

---

## 8. Features Requiring Improvement & Missing Requirements

- **P0 (Urgent): Pure Phoneme Audio:** re-synthesize letter sounds as pure phonemes without a trailing vowel (e.g., held /m/ "mmm"), with a teacher audit before release.
- **P1 (High): Active Microphone Visualizer:** show a live listening state on the *Say It* microphone button.
- **P1 (High): Visual Articulation Cues and Captions:** mouth-shape cues and sound captions for pre-readers and learners with hearing difficulties.
- **P2 (Medium): Advanced Reader Track:** blends and digraphs (SH, CH, TH) after CVC, as T-2 suggested.
- **P2 (Medium): Progress Report Export:** PIN-gated CSV/PDF export in the Parent Dashboard for teachers.

---

## 9. Actionable Recommendations & Week 3 Refactoring Roadmap

| Capstone Artifact | Validation Finding | Engineering Action & Document Refactoring | Priority / Status |
|---|---|---|:---:|
| **SRS v3.0**<br>Functional Requirements | Teachers flagged letter-name intrusion in phoneme modeling (`PED-08`). | Update **FR-02 (Hear It)**: phoneme audio must be pure (e.g., /m/ = [m:], about 800 ms for held sounds) with no syllable or letter-name added. | **P0**<br>Week 3 Refactor |
| **SRS v3.0**<br>UI & Accessibility | Children hesitated at the mic; hearing accommodation 3 of 5. | Update **FR-03 (Say It)**: live listening state on mic tap. Add **NFR-ACC** for visual articulation cues and captions. | **P1**<br>Week 3 Refactor |
| **SDD v2.0**<br>Audio Architecture | Phoneme accuracy depends on clean audio delivery. | Plan low-latency playback for short phoneme clips and keep pure phoneme files separate from key-word clips. | **P0**<br>Week 3 Refactor |
| **SDD v2.0**<br>Database & Telemetry | Offline storage confirmed; teachers asked for progress tracking. | Plan the next Room schema version (v4) for per-letter progress and local telemetry, with a migration. | **P1**<br>Week 3 Refactor |
| **Traceability Matrix**<br>(RTM v3.0) | Teachers endorsed the letter sequence; Blend It words under revision. | Map validation items (`PED-01..12`, `SUS-01..10`, `ACC-01..07`) to **FR-13 (Blend It)** and **FR-14 (Multi-Profile Support)**. | **P0**<br>Week 3 Refactor |
| **Software Test Doc**<br>(STD Test Cases) | Speech recognition accuracy on children's voices has not been measured. | Define **TC-ASR-01 to 05**: compare the app's Say It decisions with a teacher's judgement on recorded attempts across child voices and classroom noise. | **P1**<br>Weeks 4–7 Plan |

---

## 10. Conclusion & Midterm Readiness Statement

The Weeks 1–2 MVP Validation met its formative purpose: it produced specific, actionable findings for the Week 3 refactoring. Caregivers rated usability at a mean SUS of 75.5 (n = 5; 95% CI 57.3–93.7), teachers agreed on 11 of 12 pedagogical items, and children's ratings were very positive. Each result comes from a small convenience sample in a single session, so the findings are diagnostic signals, not evidence of learning effectiveness. The teachers' comments give the main engineering targets: pure phoneme audio and clearer microphone feedback. Round 2 is designed to measure what Round 1 could not, including the Say It judge's agreement with teachers.
