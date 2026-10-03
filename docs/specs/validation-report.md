# playIT: A Phonics-Based Gamified Mobile Application for Grade 1 Filipino Learners

## MVP Validation Report: Objective-Driven Revision

**Draft v3.0 · September 2026**

IT411 Capstone & Research 2 · Semester 1, AY 2026–2027
College of Computer Studies, Cebu Institute of Technology – University
Team: Palis, J. J. · Riva, Z. · Miel, K. · Durano, A. S. · Bien, E. S.

> **Team notes (delete before submission).** Cells marked "—" in Section 4.2 are filled after Round 2. Items marked **[confirm]** need a team or adviser decision. Targets marked **[proposed]** are new and need adviser approval before Round 2.

---

## Chapter 1: Introduction

### 1.1 Project Context

The Philippines faces a persistent early literacy crisis. Many Grade 3 learners in public schools still lack the letter-level decoding skills that should be mastered in Grade 1 (Duzon & Paragas, 2023), and children who do not reach decoding automaticity early struggle to access grade-level text in later years (Castles et al., 2018). Grade 1 is therefore the critical window for foundational phonics.

playIT (formerly BasaTrack) is an offline-first, gamified Android application that teaches English letter-sounds to Grade 1 Filipino learners. It adapts the Marungko approach (Ubagan & Osias, 2024) into a 26-letter English sequence organized in seven chapters. Each letter is taught through four activities: Hear It (phoneme modeling), Say It (speech production with on-device speech recognition), Find It (picture-based sound discrimination), and Blend It (CVC word building). All progress and telemetry are stored on the device, so the app works without an internet connection.

The MVP validation is formative and diagnostic. Its purpose is not to prove learning gains, which belong to the summative evaluation, but to find what must be fixed before feature freeze: pedagogical errors, interaction barriers, accessibility gaps, and unmet technical targets.

### 1.2 Why the Validation Was Revised

The first validation round ran from September 16 to 23, 2026. It combined a Yes/No DepEd teacher checklist, a child Smileyometer, facilitator observation notes, and a parent/teacher questionnaire containing the System Usability Scale (SUS), a Yes/No accessibility checklist, and two Likert items. The round produced one critical pedagogical finding and several useful signals, but most of its instruments measured general approval rather than the SMART target of each module.

Review feedback directed the team to move beyond generic validation metrics and align the validation with the project's SMART objectives. Round 1 is therefore reported as a pilot. Its findings drove the Week 3 refactoring of the requirements and design documents, and Round 2 re-collects data on the refactored build using new instruments, each mapped to a specific target.

### 1.3 Objectives of the MVP Validation

Table 1 lists the validation objectives. Objectives O1 to O3 restate General Objectives 1 to 3 of the project proposal. O4 covers the Blend It module added after the proposal (FR-13), O5 restates the completion target behind Research Question 4, and O6 restates the offline, persistence, and accessibility targets of the validation framework. The last column shows how each objective keeps the Usability (U), Pedagogy (P), and Accessibility (A) dimensions of the earlier plan.

**Table 1.** Validation objectives, targets, metrics, and instruments

| ID | Module | SMART target | Metric | Primary instrument | UPA |
|---|---|---|---|---|---|
| O1 | Hear It | 100% of phoneme clips are pure (no added vowel); playback per letter ≤5 s | Clips rated "Pure" by at least 3 of 4 teachers | A. Teacher Phoneme and Content Audit | P |
| O2a | Say It | App decision agrees with teacher judgment on ≥80% of attempts **[confirm]** | Agreement rate; false-reject and false-accept rates | B. Session Observation Sheet + C. Telemetry | P |
| O2b | Say It | Feedback appears ≤0.5 s after the child stops speaking | 90th-percentile (P90) latency | C. Telemetry | U |
| O2c | Say It | Children speak without hesitating at the mic **[proposed: ≥85% of mic turns]** | Share of mic turns with no hesitation event | B. Session Observation Sheet | U |
| O3 | Find It | Discrimination accuracy ≥80%; visual feedback ≤0.3 s | Correct taps ÷ total taps; P90 feedback latency | C. Telemetry | P, U |
| O4 | Blend It | 100% of words decodable with taught sounds; **[proposed: ≥80% of children build the chapter word within 2 attempts]** | Teacher decodability ratings; child success rate | A. Audit + C. Telemetry | P |
| O5 | Progression | ≥85% of children complete the tested nodes without an adult prompt | Completion rate | B. Session Observation Sheet | U |
| O6 | Architecture and access | 0 completed interactions lost across force-closes; all modules work offline; accessibility criteria met | Pass rate of verification checks | D. Technical and Accessibility Verification | A |

### 1.4 Scope and Limitations

Round 2 covers the Chapter 1 letter nodes (m, s, a, i) and the Chapter 1 Blend It word (SAM) on the refactored build, plus a teacher audit of all 26 phoneme clips and every Blend It word. Participants are Grade 1 learners, DepEd-certified Grade 1 teachers, and technical evaluators. The validation does not measure learning gains, retention, or parent dashboard use; these belong to the summative four-week evaluation described in the project proposal.

The main limitations are small samples from a small number of sites, facilitator-assisted child responses, approximate noise readings from phone-based meters, and a single short session per child. Results are treated as diagnostic signals that guide refactoring, not as generalizable evidence of effectiveness.

---

## Chapter 2: Literature and Framework

### 2.1 Technical Framework

playIT is a native Android application (minimum Android 8.0, API level 26) built with Jetpack Compose on an MVVM Clean Architecture. A Room (SQLite) database stores learner profiles, progress, heart states, and telemetry on the device. Speech recognition runs on-device through the Vosk offline engine, which replaced PocketSphinx, the engine named in the project proposal. No audio leaves the device and no connection is needed. The offline-first design responds to connectivity gaps and data costs in low-income households, which makes zero-data operation an equity requirement rather than a convenience.

### 2.2 Pedagogical Model

The curriculum adapts the 28-letter Marungko sequence of the Filipino alphabet to 26 letters for English phonics. Ñ is removed because it does not occur in English, and NG is deferred because it is a digraph for a single sound (/ŋ/), not a standalone letter. The 26 letters are grouped into seven chapters, and each chapter unlocks Blend It words that should be built only from sounds already taught.

Two phonics principles shape the evaluation criteria. The first is that letter-sounds must be modeled as pure phonemes. Continuous sounds such as /m/ and /s/ are held ("mmm"), and stop sounds such as /b/ and /t/ are clipped short, with no vowel added in either case. An added vowel breaks blending: a child who hears "ma" for m and "sa" for s will sound out SAM as "sa-a-ma" instead of /s/-/a/-/m/. Chapter 1 teaches only continuous sounds (m, s, a, i), which makes it the easiest chapter to blend and the one most damaged by added vowels.

The second principle is that feedback must be immediate and correct, because systematic phonics works through accurate, repeated practice (Castles et al., 2018; Lane et al., 2025). A speech recognizer that accepts wrong sounds or rejects right ones undermines the practice it is meant to support.

### 2.3 Objective-Driven Diagnostic Framework

The framework links every SMART target to evidence through a four-step chain: objective, observable metric, direct instrument, and decision rule. A target is marked Pass when the metric meets it and Refactor when it does not. Each Refactor result becomes a traceable change in the Software Requirements Specification (SRS), the Software Design Description (SDD), and the Requirements Traceability Matrix (RTM).

Three design rules keep the evidence diagnostic rather than generic. First, behavior and system logs are preferred over opinion: timings, taps, recognition results, and completion come from app telemetry and structured observation. Second, experts classify specific items instead of giving global approval. Teachers rate each phoneme clip and each Blend It word, which produces a defect list rather than a single "Yes"; global Yes/No approval items are prone to agreement bias and show little about what to fix. Third, roles are separated. The person who judges a child's pronunciation does not see the app's decision, team members do not facilitate child feedback, and all collected data are reported, including data not used as evidence.

The Usability–Pedagogy–Accessibility (UPA) dimensions of the earlier validation plan are kept as categories (Table 1), but each is now measured through module-level targets.

---

## Chapter 3: Validation Methodology

### 3.1 Design

The validation uses a two-round formative design. Round 1, the pilot, ran from September 16 to 23, 2026, on the MVP build. Its findings informed the Week 3 refactoring into SRS v3.0, SDD v2.0, and RTM v3.0. Round 2 runs on the refactored build with the instruments in Section 3.3 and is criterion-referenced: each metric is compared with its target in Table 1. For phoneme audio, teachers audit both the original and the re-mastered clips, which gives a before-and-after comparison for the most critical fix.

### 3.2 Round 1 (Pilot) Summary

Round 1 involved 16 early learners aged 5 to 7, five parents or supervising teachers, and four DepEd-certified Grade 1 reading teachers. Sessions used physical Android devices running playit-debug.apk, and the protocol specified ambient noise of 40 dB or less, checked with the app's built-in indicator. The instruments were a 12-item Yes/No teacher pedagogical checklist with open comments, a four-item child Smileyometer (Read & MacFarlane, 2006), facilitator observation notes, and a parent/teacher questionnaire containing the 10-item SUS (Brooke, 1996), a seven-item Yes/No accessibility checklist, and two Likert items on grade fit and recommendation.

The SUS was administered in Round 1 but is not used as validation evidence in this report, because it measures overall perceived usability and cannot show whether any module meets its target. Round 1 SUS results are kept in Appendix A for transparency, and no respondent was excluded from any Round 1 analysis.

Round 1 did not collect telemetry or structured timings, so ASR agreement, latency, discrimination accuracy, and completion rates were not measured. Two procedural points also shaped Round 2: several child ratings were entered by the same facilitator within one to two minutes of each other, and one child session was facilitated by a team member. Section 3.5 addresses both.

### 3.3 Round 2 Instruments

Round 2 uses four primary instruments and one secondary instrument. Full items are in Appendix B.

**Instrument A, Teacher Phoneme and Content Audit.** The four DepEd-certified teachers complete this audit independently. Part 1 presents all 26 phoneme clips in random order, and the teacher classifies each as Pure, Vowel added, Wrong sound, or Unclear audio. Part 2 lists every Blend It word with its chapter, and the teacher marks whether a Grade 1 child could decode the word using only the letter-sounds taught up to that chapter. Part 3 asks teachers to verify the team's mapping of each chapter to DepEd Grade 1 English competencies and to name any screen, instruction, or feedback message a child might misunderstand. The audit measures O1 and O4 and replaces the Round 1 global checklist.

**Instrument B, Child Session Observation Sheet.** A trained facilitator and a teacher-rater complete one sheet per child. The facilitator codes hesitation events, adult prompts, and completion for each node using the definitions in Appendix B. The teacher-rater sits beside the child without a view of the screen and judges each Say It attempt as correct or incorrect; these judgments are the reference for ASR agreement. The sheet measures O2a, O2c, and O5.

**Instrument C, App Telemetry Export.** The refactored build logs timestamped events to the local database, including mic taps, end of speech, recognition results with confidence, feedback display, Find It taps with correctness, Blend It submissions, heart changes, and node completion. A PIN-protected CSV export provides the raw data. Telemetry measures the app side of O2a, and all of O2b, O3, and O4 (child success).

**Instrument D, Technical and Accessibility Verification.** Three technical evaluators run scripted checks for recognition and feedback latency, clip length, frame rate during animated transitions, persistence across 20 force-close cycles in airplane mode, offline operation of every module, touch-target size, text contrast, and visual cues for audio content. The checks measure O2b, O3, and O6.

**Instrument E, Child Feedback (secondary).** After the session, the child points to the hardest part among four module pictures and is offered one more letter; the choice to keep playing is recorded as behavioral replay intention. An optional single Smileyometer item keeps continuity with Round 1. Young children tend to choose the happiest face (Read & MacFarlane, 2006), so Instrument E is used to prioritize fixes, not as validation evidence.

Parents and guardians give consent and may add open comments, but Round 2 has no parent rating scale because the MVP session does not include the parent dashboard. Dashboard use is evaluated in the summative study.

### 3.4 Participants and Sampling

Round 2 uses purposive sampling through partner schools. The target is 16 to 20 Grade 1 learners, with grade level recorded for every child and any non–Grade 1 child reported separately. The same four DepEd-certified teachers from Round 1 complete Instrument A so the original and re-mastered clips can be compared, and three technical evaluators run Instrument D. For ASR agreement, the session plan aims for at least 100 rated Say It attempts in total.

Facilitators are briefed on the coding definitions before sessions. Team members may operate devices and export telemetry but do not facilitate child feedback or rate pronunciation. Any relationship between an evaluator or facilitator and a team member is disclosed in this report.

### 3.5 Procedure

Before each session, the device is set to airplane mode and the ambient noise level is measured and recorded. Phone-based meters are approximate, so both the reading and the meter used are logged. Parents give written informed consent, and each child gives verbal assent.

Each child completes one session of about 20 minutes with one facilitator and one teacher-rater. The child selects a profile, enters the map, and completes Hear It, Say It, and Find It for each Chapter 1 node reached, followed by the Chapter 1 Blend It word. If a session yields few Say It attempts, a short probe follows in which the child says each Chapter 1 sound once more. Instrument E follows immediately. The facilitator completes Instrument B during the session and records the end time, and only one child is seated per facilitator at a time. Telemetry is exported from each device at the end of each day and matched to child codes.

Teachers complete Instrument A independently in about 25 minutes, using headphones in a quiet room. Clip order is randomized, and original and re-mastered clips are mixed without labels. Technical evaluators run Instrument D on at least two device models, including one low-end device near the minimum supported Android version.

### 3.6 Data Analysis

Each metric is compared with its target in Table 1 and marked Pass or Refactor. All rates are reported as counts and percentages. Because the samples are small, ASR agreement and completion rates also carry 95% Wilson score intervals (Wilson, 1927). Latencies are summarized by the median and the 90th percentile, since a mean can hide the occasional long delay that children notice.

ASR agreement is analyzed as a two-by-two table of app decision against teacher judgment. The false-reject rate (the app says wrong when the teacher says right) signals frustration risk, and the false-accept rate (the app says right when the teacher says wrong) signals the risk of reinforcing errors. Both are reported with overall agreement, because agreement alone can look high when an app accepts almost everything and most children answer correctly.

For the phoneme audit, a clip passes when at least three of four teachers rate it Pure. Agreement among teachers is reported as percent agreement per clip and as Fleiss' kappa across all clips (Fleiss, 1971). Open comments and observation notes are grouped into a defect list, and each defect is assigned a severity and linked to a requirement in the RTM.

### 3.7 Ethics and Data Privacy

The study follows the Data Privacy Act of 2012 (Republic Act No. 10173, 2012). Children are identified only by codes (C-01, C-02, and so on), teachers by T-1 to T-4, parents or supervisors by P-1 to P-5, and facilitators by F-1 onward. No names, email addresses, or school-identifying details appear in any report. Forms that collect names are described as confidential rather than anonymous, since a form that records names cannot be anonymous. Telemetry stays on the test devices until export, exported files are kept in an access-restricted team folder, and all data are deleted at the end of the capstone term **[confirm retention period]**.

---

## Chapter 4: Results and Discussion

### 4.1 Round 1 (Pilot) Findings

Table 2 summarizes the pilot evidence for each objective. Most targets could not be tested in Round 1, which is the main reason for Round 2.

**Table 2.** Round 1 evidence by objective

| ID | Objective | Round 1 evidence | Status |
|---|---|---|---|
| O1 | Pure phonemes | 2 of 4 teachers flagged letter-sound pronunciation (added vowel, e.g., /m/ as "ma") | Defect found |
| O2a | ASR agreement | Not measured | Untested |
| O2b | Say It latency | Not measured | Untested |
| O2c | Mic hesitation | Facilitator notes: children hesitated at the mic; no visible recording state | Defect found |
| O3 | Find It accuracy and feedback | Not measured; no Find It issues in notes | Untested |
| O4 | Blend It decodability | Not rated by teachers; post-pilot design review found 6 of 33 words not decodable | Defect found |
| O5 | Completion without prompt | Not measured | Untested |
| O6 | Offline, persistence, access | Offline use and persistence observed, not formally tested; hearing support 3 of 5 Yes; motor navigation 4 of 5 Yes | Gaps found |

#### 4.1.1 Hear It: Phoneme Accuracy

Teachers agreed unanimously on 11 of the 12 checklist items, including alignment with DepEd Grade 1 competencies, the logic of the letter sequence, difficulty level, feedback, scaffolding, active involvement, familiar images, and suitability as a supplementary tool. The exception was the item on correct letter-sound pronunciation, which 2 of 4 teachers flagged. One teacher (T-1) explained that the app sometimes sounded as if it were saying the letter name rather than the sound, and gave the example of /m/ produced as "ma, ma, ma" instead of a held /m/. A second teacher (T-2) raised the same concern and warned that it could confuse beginning readers.

This is the most important pilot finding. Because Chapter 1 teaches only continuous sounds, an added vowel directly damages the first blending task (SAM). The finding also shows the limit of a global checklist: it revealed that a problem exists but not which clips are affected. Round 2's per-clip audit addresses this.

#### 4.1.2 Say It: Speech Production

Facilitator notes recorded that children hesitated at the microphone button because the screen gave no sign that the app was listening. When asked whether the game was easy to play, 9 of 16 children chose the top face, six the second face, and one the middle face. The ease ratings alone cannot show which module caused difficulty, but the notes point to the microphone. Recognition agreement and latency were not measured.

#### 4.1.3 Find It: Sound Discrimination

No Find It issues were recorded in facilitator notes, and teachers did not flag the activity. Discrimination accuracy and feedback latency were not measured, so Round 1 supports no conclusion about O3.

#### 4.1.4 Blend It: Word Decodability

Teachers did not rate the Blend It word bank in Round 1. A design review after the pilot found six words that a child cannot decode with the short-vowel letter-sounds the app teaches: AIM (vowel team *ai*), BEE (*ee*), TOY and BOY (*oy*), ZOO (*oo*), and QUIZ (*qu*, pronounced /kw/). The other 27 words are decodable. These six passed the original constraint check because every letter had been unlocked; the check tested letters, not the sounds taught for them. Round 2 adds a teacher decodability rating for every word.

#### 4.1.5 Engagement

Children's ratings were very positive. Fifteen of 16 chose the top face for fun and for liking the characters, 14 of 16 for wanting to play again, and no child chose either of the two lowest faces on any item. Two cautions apply. Young children tend to pick the happiest face on the Smileyometer (Read & MacFarlane, 2006), and 13 of the 16 ratings were collected by one facilitator who was also a teacher-evaluator. The ratings suggest that the mascot and game loop are well received, but they are weak evidence and are not used to judge any objective.

#### 4.1.6 Accessibility and Architecture

All five parents or supervising teachers agreed that text size, audio narration, tap-only controls, color contrast, and language suited young learners. Two of five reported no support for learners with hearing difficulties, and one of five reported difficulty for learners with motor challenges, which the Weeks 1–2 summary linked to tight padding around the corner menu toggles. The app ran without connectivity throughout the pilot, and progress was observed to persist after force-closes, but no formal persistence or latency test was run.

The hearing gap is structural, because every module depends on sound. Visual articulation cues and on-screen sound captions are the planned response (NFR-ACC-01 in SRS v3.0).

### 4.2 Round 2 Results

*To be completed after Round 2 data collection.* For each Refactor result, the discussion names the defect, its likely cause, and the requirement change it triggers.

**Table 3.** Round 2 results against targets

| ID | Metric | Target | Result | 95% CI | Decision |
|---|---|---|---|---|---|
| O1 | Re-mastered clips rated Pure (≥3 of 4 teachers) | 26 of 26 | — | n/a | — |
| O1 | Original clips rated Pure (baseline) | reference only | — | n/a | n/a |
| O1 | Longest playback per letter | ≤5 s | — | n/a | — |
| O2a | Agreement with teacher judgment | ≥80% | — | — | — |
| O2a | False-reject rate / false-accept rate | reported | — / — | — | n/a |
| O2b | Say It latency, median / P90 | P90 ≤0.5 s | — / — | n/a | — |
| O2c | Mic turns with no hesitation event | ≥85% [proposed] | — | — | — |
| O3 | Discrimination accuracy | ≥80% | — | — | — |
| O3 | Find It feedback latency, median / P90 | P90 ≤0.3 s | — / — | n/a | — |
| O4 | Words rated decodable | all, or documented exceptions | — | n/a | — |
| O4 | Children building the chapter word within 2 attempts | ≥80% [proposed] | — | — | — |
| O5 | Children completing tested nodes without a prompt | ≥85% | — | — | — |
| O6 | Completed interactions lost in 20 force-close cycles | 0 | — | n/a | — |
| O6 | Modules working in airplane mode | 4 of 4 | — | n/a | — |
| O6 | Accessibility checks passed (D-07 to D-09) | 3 of 3 | — | n/a | — |

**Table 4.** Say It decisions against teacher judgment (counts)

| | Teacher: correct | Teacher: incorrect | Total |
|---|---|---|---|
| App accepted | — (true accept) | — (false accept) | — |
| App rejected | — (false reject) | — (true reject) | — |
| Total | — | — | — |

---

## Chapter 5: Conclusion and Recommendations

### 5.1 Summary

The pilot confirmed what teachers value in playIT: its curriculum sequence, feedback design, and offline operation. It also exposed one critical defect, phoneme clips with added vowels, along with an interaction barrier at the Say It microphone, a hearing-accessibility gap, and six non-decodable Blend It words. Just as important, the pilot showed what it could not measure. ASR agreement, latency, discrimination accuracy, and completion went untested, and the generic instruments could not locate defects precisely. Round 2 closes these gaps by measuring each SMART target directly on the refactored build.

### 5.2 Refactoring Recommendations

**Table 5.** Refactoring priorities (full specifications in the Week 3 change set)

| Priority | Change | Trigger | Requirement | Verified by |
|---|---|---|---|---|
| P0 Urgent | Re-master all 26 phoneme clips as pure sounds: continuous sounds held about 800 ms, other sounds short, no added vowel | O1 defect | FR-02 | Instrument A, Part 1 |
| P1 High | Show a clear mic state: instant change on tap, sound-reactive ripple while listening, distinct processing and result states | O2c defect | FR-03 | Instrument B; D-10 |
| P1 High | Replace non-decodable Blend It words; treat QUIZ as a documented exception | O4 defect | FR-13 | Instrument A, Part 2 |
| P1 High | Log timestamped telemetry with PIN-protected CSV export (needed before Round 2) | O2 to O5 untested | FR-NEW-TEL | Instruments C and D |
| P1 High | Save after every scored interaction; migrate the database without data loss | O6 untested | NFR-REL-01 | D-05, D-06 |
| P2 Medium | Add visual articulation cues and on-screen sound captions **[confirm priority]** | O6 hearing gap | NFR-ACC-01 | D-09 |
| P2 Medium | Enlarge corner menu toggles to the 64 dp minimum | O6 motor gap | NFR-ACC-02 | D-07 |
| P3 Future | Roadmap for advanced phonics (blends; digraphs such as sh, ch, th) | Teacher suggestion | Roadmap only | — |

### 5.3 Decision Rule and Next Steps

If every primary target passes in Round 2, the build proceeds to feature freeze. Any target marked Refactor triggers a focused fix and a re-test of that objective only. The next steps are submission of SRS v3.0, SDD v2.0, and RTM v3.0 (due September 26, 2026), implementation of the P0 and P1 changes, Round 2 data collection **[dates to be set with the adviser]**, and completion of Section 4.2.

---

## References

Brooke, J. (1996). SUS: A "quick and dirty" usability scale. In P. W. Jordan, B. Thomas, B. A. Weerdmeester, & I. L. McClelland (Eds.), *Usability evaluation in industry* (pp. 189–194). Taylor & Francis.

Castles, A., Rastle, K., & Nation, K. (2018). Ending the reading wars: Reading acquisition from novice to expert. *Psychological Science in the Public Interest, 19*(1), 5–51.

Duzon, K. J. G., & Paragas, J. P. (2023). Reading gaps of Grade-3 learners in the public elementary schools. *Psychology and Education: A Multidisciplinary Journal, 10*, 658–673.

Fleiss, J. L. (1971). Measuring nominal scale agreement among many raters. *Psychological Bulletin, 76*(5), 378–382.

Lane, H. B., Contesse, V. A., Gage, N. A., & Burns, M. K. (2025). Effect of an instructional program in foundational reading skills on early literacy development of students in kindergarten and first grade. *Reading Research Quarterly, 60*(1), e607.

Read, J. C., & MacFarlane, S. (2006). Using the Fun Toolkit and other survey methods to gather opinions in child computer interaction. In *Proceedings of the 2006 Conference on Interaction Design and Children* (pp. 81–88). ACM.

Republic Act No. 10173. (2012). *Data Privacy Act of 2012*. Official Gazette of the Republic of the Philippines.

Ubagan, M., & Osias, R. (2024). Marungko approach through multimedia-aided teaching and reading proficiency of Grade 1 learners. *International Journal of Emerging Technologies and Innovative Research, 11*(8).

Wilson, E. B. (1927). Probable inference, the law of succession, and statistical inference. *Journal of the American Statistical Association, 22*(158), 209–212.

---

## Appendix A: Round 1 (Pilot) Data

*Reported for transparency. The teacher checklist, facilitator notes, and accessibility items inform Chapter 4. SUS and fit items are not used as validation evidence.*

### A.1 System Usability Scale (n = 5)

Odd items are positively worded (+) and even items negatively worded (−), following Brooke (1996).

| Item | P-1 | P-2 | P-3 | P-4 | P-5 |
|---|---|---|---|---|---|
| SUS-01 (+) Would like to use frequently | 4 | 5 | 4 | 4 | 5 |
| SUS-02 (−) Unnecessarily complex | 1 | 5 | 1 | 1 | 3 |
| SUS-03 (+) Easy to use | 5 | 4 | 3 | 5 | 5 |
| SUS-04 (−) Would need technical support | 2 | 4 | 1 | 1 | 1 |
| SUS-05 (+) Functions well integrated | 3 | 4 | 4 | 4 | 5 |
| SUS-06 (−) Too much inconsistency | 3 | 5 | 2 | 2 | 1 |
| SUS-07 (+) Most people would learn quickly | 4 | 5 | 5 | 5 | 5 |
| SUS-08 (−) Very cumbersome | 1 | 5 | 2 | 2 | 3 |
| SUS-09 (+) Felt very confident | 4 | 5 | 4 | 4 | 4 |
| SUS-10 (−) Needed to learn a lot first | 2 | 4 | 2 | 2 | 2 |
| **SUS score** | **77.5** | **50.0** | **80.0** | **85.0** | **85.0** |

Mean = 75.5; SD = 14.62 (sample). P-2 agreed (4 or 5) with all ten items, including all five negatively worded items. This pattern suggests acquiescent or careless responding. No exclusion rule was set before data collection, so the response is retained.

### A.2 Accessibility Checklist and Fit Items (n = 5)

| Item | Yes | No |
|---|---|---|
| ACC-01 Text large enough for young learners | 5 | 0 |
| ACC-02 Audio narration for non-readers | 5 | 0 |
| ACC-03 Simple touch (tap, not drag) | 5 | 0 |
| ACC-04 Sufficient color contrast | 5 | 0 |
| ACC-05 Language appropriate for Grade 1 | 5 | 0 |
| ACC-06 Options for learners with hearing difficulties | 3 | 2 |
| ACC-07 Easy navigation for learners with motor difficulties | 4 | 1 |

FIT-01 (appropriate for Grade 1 level): mean 4.6 (responses 4, 5, 5, 5, 4). FIT-02 (would recommend to others): mean 4.4 (responses 4, 4, 5, 4, 5).

### A.3 Child Smileyometer (n = 16)

| Item | Top face | Second | Middle | Fourth | Lowest |
|---|---|---|---|---|---|
| How fun was the game? | 15 | 1 | 0 | 0 | 0 |
| Was it easy to play? | 9 | 6 | 1 | 0 | 0 |
| Did you like the characters? | 15 | 1 | 0 | 0 | 0 |
| Would you play it again? | 14 | 2 | 0 | 0 | 0 |

Facilitation: one facilitator collected 13 ratings; three others collected one each, one of whom was a team member.

### A.4 Teacher Pedagogical Checklist (n = 4)

| Item | Yes (of 4) |
|---|---|
| PED-01 Aligns with DepEd Grade 1 literacy competencies | 4 |
| PED-02 Letter sequence follows a logical phonics progression | 4 |
| PED-03 Difficulty appropriate for beginning readers | 4 |
| PED-04 Learning objectives clear and consistent | 4 |
| PED-05 Immediate and corrective feedback | 4 |
| PED-06 Scaffolding (hints, repetition) | 4 |
| PED-07 Actively involves the child | 4 |
| PED-08 Letter sounds pronounced correctly | 2 |
| PED-09 Examples and images familiar to Filipino children | 4 |
| PED-10 Language appropriate for Grade 1 in the Philippines | 4 |
| PED-11 Supports early literacy development | 4 |
| PED-12 Usable as a supplementary tool in Grade 1 | 4 |

---

## Appendix B: Round 2 Instruments

### B.1 Instrument A: Teacher Phoneme and Content Audit

*About 25 minutes. Headphones, quiet room, completed independently.*

**Part 1: Phoneme clips.** The teacher plays each clip once or twice and marks one option. Clips carry codes (A-01, A-02, and so on) and appear in random order. Original and re-mastered versions are mixed without labels; the team keeps the answer key.

| Clip | Target sound | Pure | Vowel added (e.g., "ma", "buh") | Wrong sound | Unclear audio | Note |
|---|---|---|---|---|---|---|
| A-01 | /m/ as in *mouse* | ○ | ○ | ○ | ○ | |
| A-02 | (next clip) | ○ | ○ | ○ | ○ | |

**Part 2: Blend It words.** For each word, the teacher answers: "Can a Grade 1 child decode this word using only the letter-sounds taught up to this chapter?" Response: Yes or No; if No, the teacher writes why and suggests a replacement. The form lists every word below, one row per word, in random order. The five replaced words are included so teachers can confirm the design review independently.

| Chapter | Words in the Round 2 build | Replaced after the design review |
|---|---|---|
| 1 (m, s, a, i) | SAM, SIS, AM | AIM |
| 2 (o, b, e, u) | BUS, SUB, MOM, SUM, BIB | BEE |
| 3 (t, k, l, y) | BAT, MAT, KIT, TUB, YAM | TOY, BOY |
| 4 (n, g, p) | PIG, PAN, BUG, PIN, NAP | — |
| 5 (r, d, h, w) | DOG, HAT, HEN, BED, WEB | — |
| 6 (c, f, j) | CAT, FAN, CAP, CUP, JAM | — |
| 7 (q, v, x, z) | VAN, BOX, FOX, ZIP, QUIZ (documented exception) **[confirm]** | ZOO |

**Part 3: Curriculum and clarity.**

| Code | Prompt | Response |
|---|---|---|
| A3-1 | The team maps each chapter to a DepEd Grade 1 English competency **[insert codes]**. Is each mapping correct? | Yes or No per chapter, with correction |
| A3-2 | Which letters, if any, would you move in the sequence, and why? | Open |
| A3-3 | Name any screen, instruction, or feedback message a Grade 1 child might misunderstand. | Open |
| A3-4 | Describe any moment where the app could teach or accept an incorrect sound. | Open |
| A3-5 | Any other defect or risk you noticed. | Open |

### B.2 Instrument B: Child Session Observation Sheet

**Header.** Child code, grade, date, device ID, facilitator code, teacher-rater code, measured noise level (dB) and meter used, session start time, session end time.

**Node coding (facilitator).**

| Node | Hesitation events at the mic (count) | Adult prompts, all modules (count) | Completed without prompt (Y/N) | Notes |
|---|---|---|---|---|
| m | | | | |
| s | | | | |
| a | | | | |
| i | | | | |
| Blend It (SAM) | n/a | | | |

**Say It attempt log (teacher-rater, seated without a view of the screen).** The app's decision for each attempt comes from telemetry and is matched by letter and attempt order.

| Letter | Attempt | Rater judgment |
|---|---|---|
| m | 1 | Correct / Incorrect |
| m | 2 | Correct / Incorrect |
| (continue) | | |

**Definitions.**

| Code | Definition |
|---|---|
| Hesitation event | After tapping the mic, the child waits more than 3 s before speaking, taps the mic again before speaking, or looks to an adult for confirmation before speaking. |
| Adult prompt | Any verbal or gestural help from an adult, including pointing, repeating the instruction, or saying the sound. |
| Completed without prompt | The node (Hear It, Say It, and Find It) was finished with zero adult prompts. |

### B.3 Instrument C: App Telemetry Events

Every event carries the profile ID, session ID, a monotonic timestamp in milliseconds (for latency), and wall-clock time (for matching to observation sheets).

| Event | Logged when | Key fields |
|---|---|---|
| session_start, session_end | A session opens or closes | profile, session |
| node_enter, node_complete | The child enters or completes a letter node | letter, chapter, result |
| hear_play | A phoneme clip starts playing | letter, clip ID |
| mic_tap | The child taps the Say It mic | letter, attempt |
| speech_end | The recognizer detects the end of speech | letter, attempt |
| asr_result | The recognizer returns a decision | recognized text, confidence, accept or reject |
| feedback_shown | Say It feedback is rendered | letter, attempt, result |
| find_tap | The child taps a Find It picture | picture ID, correct (Y/N) |
| find_feedback_shown | The tap highlight is rendered | picture ID |
| blend_submit | The child submits a Blend It word | word, attempt, correct (Y/N) |
| heart_change | Hearts are gained or lost | module, new value |

Say It latency is feedback_shown minus speech_end. Find It latency is find_feedback_shown minus find_tap.

### B.4 Instrument D: Technical and Accessibility Verification

*Run on at least two device models, including one low-end device near Android 8.0.*

| Check | Procedure | Pass criterion |
|---|---|---|
| D-01 Say It latency | Compute from telemetry for all Round 2 attempts, plus 30 scripted adult attempts per device | P90 ≤0.5 s |
| D-02 Find It feedback latency | Compute from telemetry | P90 ≤0.3 s |
| D-03 Playback length | Read the duration of each letter's phoneme and example-word files | Each letter ≤5 s |
| D-04 Frame rate | Record map and mascot transitions with Android Studio Profiler or JankStats | Stable 60 FPS; report share of slow frames **[set threshold with adviser]** |
| D-05 Persistence | In airplane mode, force-stop the app at random points 20 times and compare progress before and after | 0 completed interactions lost |
| D-06 Offline operation | Complete Hear It, Say It, Find It, and Blend It in airplane mode | 4 of 4 work |
| D-07 Touch targets | Measure all interactive elements with Layout Inspector | All ≥64 dp, including corner toggles |
| D-08 Contrast | Scan every screen with Accessibility Scanner | Text ≥4.5:1 (WCAG 2.1 AA) |
| D-09 Visual cues for audio | Check each phoneme screen | Caption and articulation cue present for 26 of 26 |
| D-10 Mic state response | Measure tap to Listening state from telemetry or a 60 fps screen recording | ≤100 ms **[proposed]** |

### B.5 Instrument E: Child Feedback (Secondary)

*The facilitator reads each prompt aloud right after the session.*

| Code | Prompt | Record |
|---|---|---|
| E-1 | "Which part was the hardest?" (show pictures of Hear It, Say It, Find It, and Blend It, plus a "none" card) | Picture chosen |
| E-2 | "Do you want to play one more letter?" | Yes or No; if Yes, the child plays one more node, then stops |
| E-3 (optional) | "How fun was it?" with the five-face Smileyometer | Face chosen |
