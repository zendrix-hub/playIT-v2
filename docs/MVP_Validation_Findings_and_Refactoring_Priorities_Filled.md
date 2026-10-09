# MVP Validation Findings & Refactoring Priorities

**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Target Milestone:** Weeks 1–2 MVP Validation Deliverable & Week 3 Refactoring Bridge  
**Document Version:** 2.0 (Objective-Driven Refactoring)  
**Date of Submission:** October 10, 2026  

---

## Section 1 — Project Information

- **Team/Group Name:** Group 56 — PlayIT Capstone Team
- **Project/System Title:** **PlayIT: An Offline-First Gamified Early Literacy Mobile Application Using the DepEd Marungko Approach**
- **Program / Section:** Bachelor of Science in Information Technology / IT411 G1–G8
- **Team Members:**
  1. Riva, Zendrix (Team Lead & Full-Stack Android Engineer)
  2. Palis, J. J. (Frontend & Audio Engineer)
  3. Miel, K. (UX/UI Designer & QA Specialist)
  4. Durano, A. S. (Data Architect & Backend Engineer)
  5. Bien, E. S. (Pedagogical Researcher & Documentation Specialist)
- **Adviser:** Prof. [Adviser Name]
- **System URL / MVP Link:** Offline Android APK (`playit-debug.apk`, Package: `com.playit.app`, Min SDK: 26 / Android 8.0 Oreo, Target SDK: 34 / Android 14)
- **Date of MVP Validation:** September 16–23, 2026
- **Number of respondents/participants:** Total $N = 25$ participants (Classroom, home, and community testing sites)
- **Types of respondents involved:**
  - [x] **Customer:** Parents / Guardians & Supervising Teachers ($N = 5$)
  - [x] **End User:** Grade 1 & Early Learners (Ages 5–7) ($N = 16$)
  - [x] **Subject Matter Expert:** Certified DepEd Grade 1 Reading Teachers ($N = 4$)
  - [x] **Decision Maker:** School Coordinators & Reading Specialists
  - [x] **Other:** Technical Evaluators ($N = 3$) for local performance and accessibility testing

---

## Section 2 — MVP Validation Overview

### 1. What was the primary purpose of your MVP validation?
The primary purpose of the Weeks 1–2 MVP Validation was **formative and diagnostic** rather than summative. Specifically, the validation evaluated whether the MVP's foundational learning architecture—comprising Hear It (phoneme modeling), Say It (speech production with on-device ASR), Find It (auditory sound discrimination), and Blend It (CVC word construction)—could achieve its intended pedagogical and usability outcomes in authentic low-resource Philippine settings. The team sought to identify critical pedagogical defects (such as phonetic distortion in synthetic speech), user interaction friction points (especially young children hesitating at the microphone CTA), and accessibility barriers before entering full-scale system implementation and the formal Capstone 2 Round 2 evaluation.

### 2. What validation framework/model did your team use?
- **Framework/Model:** **Usability, Pedagogy, and Accessibility (UPA) Educational Evaluation Framework**, integrated with standardized quantitative instruments: Brooke’s (1996) **System Usability Scale (SUS)** for adult caregivers and Read & MacFarlane’s (2006) **Smileyometer (Fun Toolkit)** for early learners.
- **Key constructs/criteria evaluated:**
  1. *Pedagogy (P):* Alignment with DepEd Grade 1 competencies, phonetic purity of letter sounds (absence of intrusive schwas / trailing syllables), sequence logic of the 7-chapter Marungko curriculum, and decodability of blending words.
  2. *Usability (U):* Child navigation autonomy, clarity of microphone state, feedback latency ($P90 \le 0.5\text{ s}$), perceived ease of use, and caregiver perceived usability (target SUS $\ge 75$).
  3. *Accessibility (A):* Compliance with pediatric physical floors ($\ge 64\text{ dp}$ touch targets, $\ge 24\text{ sp}$ child reading typography), accommodations for hearing difficulties (visual articulation cues and captions), and 100% offline functionality.
- **Why was this framework/model appropriate for your project?**
  Unlike generic software, early literacy applications for young Filipino children require strict pedagogical validity; a system may be technically functional and engaging yet pedagogically destructive if it teaches incorrect phonetics. The UPA framework provides a multi-dimensional evaluation that simultaneously assesses child developmental interaction, curriculum rigor according to DepEd standards, and environmental accessibility under offline, low-cost device constraints.

### 3. Briefly describe how the validation was conducted.
- **Who participated:** 16 Grade 1 and early learners (ages 5–7), 5 caregivers/supervising teachers, and 4 certified DepEd Grade 1 reading educators.
- **How they interacted with the MVP:** Learners interacted directly with `playit-debug.apk` installed on physical Android mobile devices and tablets under airplane mode (zero internet connection) in quiet settings ($\le 40\text{ dB}$ ambient noise checked via built-in indicator).
- **What activities/tasks they performed:**
  - Children selected or created an avatar profile, navigated the map trail, and played through Chapter 1 letter nodes (*m, s, a, i*) completing Hear It, Say It, and Find It, followed by the Chapter 1 milestone Blend It word (*SAM*).
  - Teachers completed an independent 12-item curriculum and phoneme audit checklist, listening to modeled letter sounds and examining the Marungko progression.
  - Caregivers observed learner sessions and completed the 10-item SUS and 7-item accessibility checklist.
- **How feedback/data was collected:** Data were collected through three synchronized Google Forms (`Master (responses).xlsx`: *smileyometer*, *SUSaccessibility*, *teacherpedagogy*), structured facilitator observation sheets, post-session child Smileyometer interviews, and qualitative teacher debriefs.

---

## Section 3 — MVP Validation Findings

### Finding #1: Phonemic Audio Impurity and Trailing Vowel Intrusion
1. **What did you learn from the stakeholders?**  
   Synthetic phoneme audio in *Hear It* and *Say It* suffered from phonetic impurity: instead of modeling isolated, pure letter-sounds, several clips added an intrusive trailing schwa (/ə/) or sounded like letter names. Specifically, the continuous consonant /m/ was pronounced as "ma, ma, ma" or "em" rather than a held hum [m:].
2. **What evidence supports this finding?**  
   - Pedagogical Checklist Item `PED-08` ("Letter sounds pronounced correctly"): **2 of 4 DepEd teachers flagged this item as a critical defect.**
   - Teacher T-1 (DepEd Grade 1 Reading Specialist): *"One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.' Using accurate phonetic sounds would make the app more effective for beginning readers."*
   - Teacher T-2: *"My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it."*
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Subject Matter Expert (DepEd Grade 1 Reading Teachers)
4. **Which SMART objective or measurable outcome is affected?**  
   **SMART Objective O1 (Hear It):** 100% of modeled phoneme audio clips are pure (no intrusive vowel); longest playback per letter $\le 5\text{ s}$.
5. **What measurable outcome could potentially be improved?**  
   - [x] Accuracy  
   - [x] Effectiveness  
6. **Explain how the finding affects the achievement of the objective:**  
   In foundational phonics (Marungko approach), children blend isolated sounds to form words. If a child learns /m/ as "ma" and /s/ as "sa", they decode the word *SAM* as "sa-a-ma" rather than /s/-/a/-/m/. Trailing vowels directly impair auditory blending, compromising foundational literacy acquisition and failing Objective O1.

---

### Finding #2: Say It Microphone Hesitation and Interaction Ambiguity
1. **What did you learn from the stakeholders?**  
   In *Say It*, children experienced hesitation and confusion when approaching the microphone CTA because the interface lacked clear real-time states indicating when the engine was actively listening, when speech had been detected, and when processing occurred.
2. **What evidence supports this finding?**  
   - Child Smileyometer Perceived Ease: **7 of 16 children (43.8%) did not select the top smiling face for ease of use.** Perceived ease was the lowest rated dimension across the entire evaluation.
   - Facilitator observation logs documented repeated hesitation events: children tapped the microphone but delayed speaking or repeatedly re-tapped the button because they were uncertain whether the app was recording.
3. **Which stakeholder/user type provided this feedback?**  
   - [x] End User (Grade 1 Early Learners)
   - [x] Customer / Facilitator (Supervising Observers)
4. **Which SMART objective or measurable outcome is affected?**  
   **SMART Objective O2c (Say It Autonomy):** $\ge 85\%$ of microphone turns executed with zero hesitation events; **Objective O2a:** ASR agreement $\ge 80\%$.
5. **What measurable outcome could potentially be improved?**  
   - [x] Usability  
   - [x] Efficiency  
   - [x] User satisfaction  
6. **Explain how the finding affects the achievement of the objective:**  
   Hesitation causes clipped audio, premature speech-detection cutoffs, and unnecessary ASR recognition errors. Without immediate, visual confirmation of listening and speech detection, learner autonomy collapses, requiring continuous adult prompting and violating the target for unprompted learner progression.

---

### Finding #3: Structural Accessibility Deficits (Hearing Accommodations & Small Edge Controls)
1. **What did you learn from the stakeholders?**  
   The application lacked visual support for learners with hearing difficulties, and certain edge navigation buttons had insufficient touch padding for young children's motor dexterity.
2. **What evidence supports this finding?**  
   - Accessibility Checklist Item `ACC-06` ("Hearing difficulty accommodations"): **Rated 3 of 5 Yes (40% negative response rate).** Caregivers noted zero visual cues for sounds.
   - Accessibility Checklist Item `ACC-07` ("Motor difficulty accommodations"): **Rated 4 of 5 Yes.** Observations noted tight padding around corner menu toggles and back buttons.
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Customer (Parents & Supervising Teachers)
   - [x] Subject Matter Expert (Reading Facilitators)
4. **Which SMART objective or measurable outcome is affected?**  
   **SMART Objective O6 (Accessibility & Architecture):** 100% compliance with pediatric accessibility standards ($\ge 64\text{ dp}$ touch target floor, dual-modal visual/auditory cues).
5. **What measurable outcome could potentially be improved?**  
   - [x] Usability  
   - [x] Reliability  
6. **Explain how the finding affects the achievement of the objective:**  
   Relying exclusively on audio output excludes children with mild hearing impairments or in noisy classroom environments. Sub-64dp touch targets create motor frustration and accidental missed taps.

---

### Finding #4: Non-Decodable Blend It Word Bank Entries
1. **What did you learn from the stakeholders?**  
   A post-validation curriculum review of the seeded 33 Blend It words revealed that 6 words could not be phonetically decoded using the letter-sounds taught in their respective chapters under the Marungko sequence.
2. **What evidence supports this finding?**  
   - Teacher feedback on decodability and subsequent expert linguistic audit of `DatabaseModule.kt`: words *AIM* (vowel team *ai*), *BEE* (long vowel *ee*), *TOY* and *BOY* (diphthong *oy*), *ZOO* (vowel digraph *oo*), and *QUIZ* (*qu* /kw/) were unlocked based purely on letter availability rather than taught phonics rules.
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Subject Matter Expert (DepEd Teachers & Reading Curriculum Review)
4. **Which SMART objective or measurable outcome is affected?**  
   **SMART Objective O4 (Blend It Decodability):** 100% of unlocked challenge words are decodable using only previously mastered letter-sounds.
5. **What measurable outcome could potentially be improved?**  
   - [x] Effectiveness  
   - [x] Accuracy  
6. **Explain how the finding affects the achievement of the objective:**  
   Introducing irregular vowel digraphs in early CVC lessons contradicts systematic phonics. Learners cannot sound out words using taught single-letter phonemes, causing cognitive overload and blending failure.

---

## Section 4 — Potential Refactoring Priorities

### Refactoring Priority #1: Pure Phoneme Acoustic Modeling & Multi-Stage Production
1. **Validation Finding:** Finding #1 (Phonemic Audio Impurity; `PED-08`).
2. **What needs to be improved or changed?**  
   Re-master all 26 letter-sound audio clips as acoustically pure phonemes: continuous sounds (/m/, /s/, /f/, etc.) held for $\approx 800\text{ ms}$, short/stop sounds (/b/, /t/, /p/) clipped sharply without trailing schwas, verified by teacher audit before deployment.
3. **What type of refactoring is required?**  
   - [x] Functional requirement (FR-02)  
   - [x] Data/input/output (Audio Assets & Manifests)  
   - [x] System process/workflow (Audio Production Pipeline)
4. **Current MVP approach:**  
   Used standard text-to-speech (Edge-TTS) with single-letter strings, resulting in synthetic letter-name pronunciation ("ma", "es", "bee").
5. **Proposed refactored approach:**  
   Multi-stage neural audio pipeline: local Kokoro-82M neural TTS combined with Chatterbox-Turbo voice cloning for held phonemes, governed by strict SHA-256 release manifests and teacher sign-off. Runtime sequence assembly separates pure sounds from key-word carrier phrases.
6. **Why is this change necessary?**  
   Guarantees that children learn pure phonemes essential for synthetic phonics blending, directly satisfying DepEd teacher requirements.
7. **Expected improvement:**  
   100% of phoneme audio clips rated "Pure" by at least 3 of 4 DepEd teachers; 0 trailing schwas.
8. **How will the improvement eventually be measured?**  
   Teacher Phoneme and Content Audit (Instrument A, Part 1).
9. **Priority:**  
   🔴 **High (P0 Urgent)** — Essential to achieving SMART Objective O1.

---

### Refactoring Priority #2: Real-Time 4-State Microphone Visualizer & Speech Detection
1. **Validation Finding:** Finding #2 (Say It Mic Hesitation; 43.8% non-top-face ease).
2. **What needs to be improved or changed?**  
   Replace static mic icon with a dynamic 4-state visualizer (`Idle`, `Listening` with animated amplitude ripple, `Heard` speech-detected confirmation, and `Result`), with $\le 100\text{ ms}$ response on tap and non-punitive guidance (Say It never deducts hearts).
3. **What type of refactoring is required?**  
   - [x] Functional requirement (FR-03)  
   - [x] Usability  
   - [x] System process/workflow  
4. **Current MVP approach:**  
   Static microphone icon that toggled without immediate visual state change or speech-detection feedback. Wrong answers penalized hearts.
5. **Proposed refactored approach:**  
   `MicButton` composable backed by `MicStatus` enum and reactive `SayItViewModel` state flow. On speech detection, the button immediately transitions to `Heard`, providing reassurance. A 3-tier prompt ladder provides encouraging phonetic corrections without depleting hearts.
6. **Why is this change necessary?**  
   Eliminates learner hesitation at the mic, establishes a calm learning environment, and prevents early learner speech anxiety.
7. **Expected improvement:**  
   Mic turns with zero hesitation improve from $< 60\%$ to $\ge 85\%$; feedback latency $P90 \le 0.5\text{ s}$.
8. **How will the improvement eventually be measured?**  
   Session Observation Sheet (Instrument B) and App Telemetry Export (Instrument C).
9. **Priority:**  
   🔴 **High (P1 High)** — Essential to achieving SMART Objectives O2a, O2b, and O2c.

---

### Refactoring Priority #3: Curricular Alignment of Blend It Word Bank
1. **Validation Finding:** Finding #4 (Non-Decodable Blend It Words).
2. **What needs to be improved or changed?**  
   Replace all 5 non-decodable CVC words (*AIM, BEE, TOY, BOY, ZOO*) with strictly decodable words (*AM, SUM, TUB, YAM, ZIP*); designate *QUIZ* as a documented exception.
3. **What type of refactoring is required?**  
   - [x] Functional requirement (FR-13)  
   - [x] Data/input/output (Database Module Seed Data)
4. **Current MVP approach:**  
   Permitted any word whose letters had been unlocked, regardless of whether the phoneme sound was taught as a short vowel.
5. **Proposed refactored approach:**  
   Database seed validation enforcing that every unlocked word is 100% decodable strictly with sounds taught in or prior to that chapter.
6. **Why is this change necessary?**  
   Preserves pedagogical integrity; ensures beginning readers can successfully blend every challenge word.
7. **Expected improvement:**  
   100% of unlocked Blend It words verified decodable by DepEd reading teachers.
8. **How will the improvement eventually be measured?**  
   Teacher Decodability Audit (Instrument A, Part 2).
9. **Priority:**  
   🔴 **High (P1 High)** — Essential to achieving SMART Objective O4.

---

### Refactoring Priority #4: Local Room Telemetry Logging & Monotonic Event Export
1. **Validation Finding:** Finding #5 (Lack of empirical interaction logs for ASR and timings).
2. **What needs to be improved or changed?**  
   Add local Room database telemetry event logging with monotonic timestamps (`TelemetryEventEntity`, `TelemetryDao`) and PIN-protected RFC 4180 CSV export.
3. **What type of refactoring is required?**  
   - [x] Functional requirement (FR-NEW-TEL)  
   - [x] System architecture/design  
   - [x] Performance / Analytics
4. **Current MVP approach:**  
   No persistent telemetry logging; only overall scores were stored, preventing latency and interaction analysis.
5. **Proposed refactored approach:**  
   `TelemetryManager` logs mic taps, speech end, ASR results, latencies, Find It taps, and Blend It attempts locally. Zero network egress. Exportable via Android Share Sheet behind parent arithmetic gate.
6. **Why is this change necessary?**  
   Enables objective verification of SMART objectives O2a, O2b, O3, O4, and O5 during Capstone 2 Round 2 validation.
7. **Expected improvement:**  
   $100\%$ of user interactions recorded with millisecond precision without cloud dependency.
8. **How will the improvement eventually be measured?**  
   Instrument C (Telemetry Export Verification).
9. **Priority:**  
   🔴 **High (P1 High)** — Essential for Capstone 2 empirical evaluation.

---

### Refactoring Priority #5: Visual Articulation Guides & Dual-Modal Sound Captions
1. **Validation Finding:** Finding #3 (Hearing Difficulty Accommodations rated 3/5; `ACC-06`).
2. **What needs to be improved or changed?**  
   Add visual mouth articulation cues (9 categorical shape groups covering all 26 letters) and synchronized on-screen sound captions.
3. **What type of refactoring is required?**  
   - [x] Non-functional requirement (NFR-ACC-01)  
   - [x] Usability & Accessibility  
   - [x] UI/UX Presentation
4. **Current MVP approach:**  
   Audio playback was accompanied only by static letter glyphs and mascot speech bubbles without mouth formation guides.
5. **Proposed refactored approach:**  
   `ArticulationCue` component displaying transparent illustrated mouth formations (e.g., lips together for /m/, teeth close for /s/) alongside `CaptionBubble` rendering exact spoken phonemes.
6. **Why is this change necessary?**  
   Supports pre-readers, hard-of-hearing learners, and noisy classroom environments through multi-modal reinforcement.
7. **Expected improvement:**  
   Accessibility verification check D-09 passed; caregiver hearing rating increases from 3/5 to 5/5.
8. **How will the improvement eventually be measured?**  
   Instrument D (Technical and Accessibility Verification).
9. **Priority:**  
   🟠 **Medium (P2 Medium)** — Significant improvement to system inclusivity and accessibility.

---

### Refactoring Priority #6: Adaptive Layout Architecture & 64dp Pediatric Touch Target Floor
1. **Validation Finding:** Finding #3 (Motor difficulties & tight edge padding; `ACC-07`).
2. **What needs to be improved or changed?**  
   Enforce a strict $\ge 64\text{ dp}$ touch target floor for all child-facing interactive components, wrap lesson screens in an adaptive `LessonScaffold`, and guarantee zero CTA clipping across screen profiles (360x640 to tablets).
3. **What type of refactoring is required?**  
   - [x] Non-functional requirement (NFR-ACC-02)  
   - [x] Usability & Presentation Architecture
4. **Current MVP approach:**  
   Fixed pixel heights and default 48dp Compose buttons that clipped on small devices (360x640) or under 1.3x system font scaling.
5. **Proposed refactored approach:**  
   `PlayItDimens` adaptive system (`WindowProfile.COMPACT`, `REGULAR`, `WIDE`), dynamic `heightIn(min = 64.dp)` containers, and `LessonScaffold` pinned bottom CTA bars with weight-based scrollable content bodies.
6. **Why is this change necessary?**  
   Ensures early learners with developing fine motor skills can tap reliably without missed presses or layout clipping.
7. **Expected improvement:**  
   Zero clipped CTAs across 4 device test matrix profiles (Roborazzi layout tests); 100% compliance with 64dp touch target floor.
8. **How will the improvement eventually be measured?**  
   `LayoutMatrixTest.kt` automated tests and physical device verification.
9. **Priority:**  
   🟠 **Medium (P2 Medium)** — Essential pediatric usability standard.

---

## Section 5 — Refactoring Summary Matrix

| # | Validation Finding | Empirical Evidence | SMART Objective / Outcome Affected | Proposed Refactoring | Expected Improvement | Priority |
|---|---|---|---|---|---|:---:|
| **1** | Phonemic audio impurity (added schwas / letter names) | 2 of 4 DepEd teachers flagged `PED-08` (/m/ sounding as "ma") | **O1:** 100% pure phoneme modeling clips | Re-master 26 phoneme clips via Kokoro/Chatterbox; separate pure sounds from carriers | 100% pure sound rating on teacher audit; 0 intrusive vowels | 🔴 **High (P0)** |
| **2** | Microphone interaction hesitation & ambiguous state | 43.8% non-top-face ease rating; facilitator hesitation logs | **O2c:** $\ge 85\%$ turns with no hesitation; **O2a:** $\ge 80\%$ ASR agreement | 4-state `MicButton` (`Idle`, `Listening`, `Heard`, `Result`); Say It never removes hearts | $\ge 85\%$ mic turns without hesitation; $P90 \le 0.5\text{ s}$ latency | 🔴 **High (P1)** |
| **3** | Non-decodable Blend It word bank entries | 6 of 33 words contained untaught vowel teams or digraphs | **O4:** 100% decodable with taught letter-sounds | Replace *AIM, BEE, TOY, BOY, ZOO* with *AM, SUM, TUB, YAM, ZIP*; document *QUIZ* | 100% teacher decodability confirmation; higher child blending success | 🔴 **High (P1)** |
| **4** | Lack of structured empirical telemetry logging | O2–O5 untested quantitatively during Round 1 pilot | **O2–O5:** Empirical agreement, latency, and completion metrics | Local Room `TelemetryEventEntity` logging with PIN-gated CSV export | 100% of interaction events logged with monotonic timestamps | 🔴 **High (P1)** |
| **5** | Lack of visual support for hearing-impaired learners | Caregivers rated hearing accommodations 3 of 5 Yes (`ACC-06`) | **O6:** Dual-modal visual/auditory accessibility | Illustrated mouth articulation cues (9 groups) + on-screen sound captions | D-09 accessibility check pass; visual multi-modal learning | 🟠 **Medium (P2)** |
| **6** | Edge button padding and device layout clipping | Caregivers rated motor support 4 of 5 Yes (`ACC-07`); small screens clipped | **O6:** Pediatric touch targets and multi-device fit | Adaptive `PlayItDimens`, `LessonScaffold`, strict 64dp touch target floor | Zero clipped CTAs across 4 device profiles; 64dp compliance | 🟠 **Medium (P2)** |

---

## Section 6 — Items That Are NOT Refactoring Priorities

| Item | Identified? | Action & Justification |
|---|:---:|---|
| **Bugs / Errors** | Yes | *Fixed during implementation & testing.* Specific runtime bugs (e.g., ViewModel initialization order race conditions, audio player rapid double-tap debouncing) are handled via routine engineering hardening rather than architectural refactoring. |
| **Minor UI / UX Issues** | Yes | *Improved as appropriate.* Cosmetic visual tweaks (such as fine-tuning Chocolate Hills canvas backdrop curves or mascot idle breathing timings) are addressed in presentation polish without modifying core system requirements. |
| **Missing Original Requirement** | No | All proposed core modules (Hear It, Say It, Find It, Blend It) were designed in the initial Capstone 1 scope. |
| **Missing Essential Functional Requirement** | Yes | *Assessed and incorporated.* Local Telemetry Logging (`FR-NEW-TEL`) and Multi-Profile Classroom Station Support (`FR-14`) were identified as essential missing functional requirements and formally incorporated into SRS v3.0. |
| **Missing Non-Functional Requirement** | Yes | *Assessed and incorporated.* Visual Articulation Accommodations (`NFR-ACC-01`) and Pediatric Layout Minimums (`NFR-ACC-02`) were formally added to SRS v3.0 based on stakeholder evidence. |
| **New Feature Unrelated to SMART Objectives** | Yes | *Deferred / Not Prioritized.* Advanced phonics tracks (consonant blends, digraphs like SH/CH/TH, and diphthongs) suggested by Teacher T-2 are outside the Grade 1 single-letter foundational scope and are placed strictly on the post-capstone roadmap. |
| **Improvement to Measurable Outcome** | Yes | *Potential refactoring priority.* All items in Section 4 directly enhance measurable outcomes: phoneme purity (O1), hesitation reduction (O2c), latency (O2b), and decodability (O4). |
| **Change in System Approach / Design** | Yes | *Potential refactoring priority.* Shifting from speech-service listening to discrete state visualizers, separating pure phonemes from key-word carrier lines, and introducing adaptive lesson scaffolding represent structural system design refactorings. |
| **Improvement in Accuracy / Performance** | Yes | *Potential refactoring priority.* Offline Vosk grammar tuning for $\ge 80\%$ teacher agreement, sub-500ms feedback latency, and off-thread image downscaling directly optimize system accuracy and performance. |

---

## Section 7 — Final Team Reflection

### 1. What is the single most important thing your team learned from the MVP validation?
The single most critical lesson our team learned is that **pedagogical accuracy must strictly govern technology implementation in early literacy software**. In adult applications, minor pronunciation variations or trailing sounds in text-to-speech are negligible; however, in foundational phonics for Grade 1 children, a synthetic voice adding even a fraction of a second of trailing schwa (/m/ as "ma") fundamentally damages the child's ability to blend phonemes into words. Technology that appears functional can be pedagogically counterproductive if not grounded in precise phonological science.

### 2. What is the most important change your team should make based on this learning?
The most important change is the **complete restructuring of the acoustic modeling pipeline**: replacing all generic synthetic TTS clips with verified, acoustically pure phonemes (held continuous sounds and crisp short stops), establishing a multi-stage audio production pipeline with strict SHA-256 release gates, and mandating DepEd reading specialist audits before audio assets are permitted into the production application.

### 3. How will this change help your project achieve its SMART objectives more effectively?
This change ensures that when learners interact with *Hear It* and *Say It*, they internalize correct, isolated phonetic building blocks. Consequently, when they advance to *Blend It*, they can blend sounds naturally (/s/ + /a/ + /m/ = *SAM*) without intrusive extra syllables. This directly fulfills SMART Objective O1 (100% pure phonemes), enables Objective O4 (successful unprompted word blending), and raises teacher confidence in PlayIT as a valid classroom supplementary tool.

### 4. What requirements and/or design documents will need to be updated as a result?
- [x] **Software Requirements Specification (SRS v3.0):** Updated FR-02 (pure phonemes), FR-03 (4-state mic visualizer & prompt ladder), FR-13 (decodable word bank), FR-14 (multi-profile management), FR-NEW-TEL (local telemetry), NFR-ACC-01 (mouth cues/captions), and NFR-ACC-02 (pediatric touch targets & layout).
- [x] **Software Design Description (SDD v2.0):** Updated Audio Subsystem architecture (SoundPool + AudioResolver), Tutoring Layer FSM (`TutorPolicy`), Vosk speech recognizer pipeline, Room schema v3/v4 entity definitions, and adaptive `LessonScaffold` UI tokens.
- [x] **Software Project Management Plan (SPMP v2.0):** Re-baselined master WBS into 9 work packages, incorporating local Kokoro/Chatterbox audio engineering compute, hardware tablet testing protocols, and the 8-point pedagogical risk register (R1–R8).
- [x] **Requirements Traceability Matrix (RTM v3.0):** Complete 16-row bidirectional traceability linking empirical findings F-01 through F-16 to objectives, requirements, architecture, and STD test cases.

---

## Final Declaration

We certify that the findings and proposed refactoring priorities submitted in this form are based on actual MVP validation feedback gathered from our identified stakeholders, educators, and early learners during Weeks 1–2 of Capstone 2. We understand that the proposed refactoring must be justified in relation to our project's SMART objectives and measurable educational outcomes.

**Team Lead:** Zendrix Riva  
**Date:** October 10, 2026  
