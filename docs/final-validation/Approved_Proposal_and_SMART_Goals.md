# Approved Proposal and SMART Goals — playIT

**Course & Section:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Group Code / Team:** `2627-sem1-it411-56` (IT332 Baseline Code: `2526-sem2-it332-56`) — Group 56  
**Project Title:** *playIT: A Gamified Phonics-Based Mobile Application for Early Reading Literacy of Grade 1 Learners*  
**Team Members:** Riva, Zendrix (Team Lead); Palis, John James; Miel, Kaysean; Durano, Ashlee Skye; Bien, Erik Samuel  
**Adviser:** Mr. Joemarie C. Amparo | **Course Instructor:** Mr. Ralph P. Laviste  

---

## 1. Purpose of This Document

In compliance with **Requirement D (`Approved_Proposal_and_SMART_Goals`)** of the *Requirements Before Adviser Presentation* (`IT411 | G1 - G8 | SEM1 | 2026-27`), this document preserves the **originally approved IT332 Project Proposal (Revised 4, May 2026) and Baseline Software Requirements Specification (`2526-sem2-it332-56_playIT_SRS.pdf`, Version 2.0)** alongside a structured comparison table demonstrating how every originally approved SMART target has been preserved, operationalized, and strengthened in the completed **playIT v1.2.0-rc2 (SRS v3.0)** system.

---

## 2. Verbatim Problem Statement & Project Rationale (Approved Proposal Baseline)

### 2.1 Problem Context
Early reading literacy in Grade 1 (ages 6–7) relies on phonemic awareness and systematic phonics decoding—the ability to hear, produce, discriminate, and blend individual letter sounds (phonemes) into words. In Philippine public and private elementary schools implementing the Department of Education (DepEd) MATATAG and Marungko-aligned reading interventions, teachers face three persistent challenges:
1. **High Learner-to-Teacher Ratios & Limited 1-on-1 Articulation Feedback:** Classroom teachers cannot simultaneously listen to and correct every learner's individual phoneme pronunciation during practice drills.
2. **Lack of Offline, Culturally Grounded Phonics Tools for Budget Devices:** Existing commercial phonics apps (e.g., Duolingo ABC, Khan Academy Kids, Hooked on Phonics) require continuous broadband internet, rely on Western idioms/accents, or demand high-end hardware unavailable to many Filipino households.
3. **Absence of Actionable Phoneme-Level Diagnostic Reports for Parents and Teachers:** Caregivers and reading teachers lack offline, per-letter mastery and retention data to identify which specific phonemes a child is struggling to pronounce or discriminate.

---

## 3. Verbatim Approved General & Specific Objectives (Approved Proposal & SRS v2.0 §1.2)

### 3.1 General Objective
> *"To design, develop, and evaluate **playIT**, an offline, gamified phonics-based Android mobile application that assists Grade 1 learners (ages 6–7) in developing foundational English phonemic awareness, letter-sound articulation, visual-auditory letter discrimination, and early word blending, while providing parents and teachers with offline diagnostic progress analytics."*

### 3.2 Specific Objectives (Originally Approved SMART Goals)

1. **Specific Objective 1 — Progressive Marungko-Inspired Phonics Curriculum (`FR-01`, `FR-02`):**
   > *"To structure the 26 letters of the English alphabet into six progressive, frequency-based learning groups inspired by the Marungko approach—**Group 1 (`M, S, A, I, O, B`)**, **Group 2 (`E, U, T, K, L, Y`)**, **Group 3 (`N, G, P, R, D, H`)**, **Group 4 (`W, C, F`)**, **Group 5 (`J, Q, V, X, Z`)**, and **Group 6 (Comprehensive Blending & Mastery Review)**—enforcing sequential mastery gates before unlocking subsequent groups."*

2. **Specific Objective 2 — Three-Stage Multisensory Phonics Instruction (`Hear It`, `Say It`, `Find It`) & Word Blending (`FR-03`, `FR-04`, `FR-05`, `FR-NEW-01`):**
   > *"To implement an interactive, multisensory lesson loop for each letter consisting of:*
   > - ***Hear It (Auditory & Articulatory Modeling):** Plays accurate phoneme pronunciation audio paired with visual mouth-motion articulation cues and contextual object illustrations within $\le 5.0\text{ seconds}$ per clip at $100\%$ playback reliability;*
   > - ***Say It (Offline Speech Production & Verification):** Captures the learner's spoken phoneme/word offline using the embedded **Vosk** automatic speech recognition (ASR) engine, achieving $\ge 75\%$ recognition accuracy with response latency $\le 0.5\text{ seconds}$ ($500\text{ ms}$) across up to 3 guided attempts;*
   > - ***Find It (Visual-Auditory Letter Discrimination):** Evaluates letter-sound recognition across a 3-round, 3-choice interactive selection game requiring $3/3$ target identifications with tap-to-feedback latency $\le 0.3\text{ seconds}$ ($300\text{ ms}$); and*
   > - ***Blend It (Phoneme Synthesis into Decodable Words):** Guides learners at the end of each letter group to blend learned phonemes into decodable CVC/VVC words."*

3. **Specific Objective 3 — Child-Centric Gamification Mechanics (`Hearts`, `Stars`, `Streaks`, `Badges`) (`FR-06`–`FR-09`):**
   > *"To sustain learner motivation and mastery through a gamified feedback system featuring a **5-heart pool** per lesson (with a **3-heart recovery** upon lesson restart), a **1-to-3 star mastery rating** ($0\text{ hearts lost} = 3\star$; $1\text{–}2\text{ hearts lost} = 2\star$; $\ge 3\text{ hearts lost} = 1\star$), and daily learning streak milestones (**5, 10, 15, and 20 consecutive days**)."*

4. **Specific Objective 4 — Multi-Profile Support & Child-Safe Parent Gate (`FR-10`, `FR-11`):**
   > *"To support up to **6 independent learner profiles** on a single shared Android device with child-friendly avatar selection and a randomized **single-digit arithmetic gate** (`ArithmeticGuardDialog`) that prevents unauthorized child access to adult settings."*

5. **Specific Objective 5 — Offline Diagnostic Dashboard & PDF Progress Reporting (`FR-12`, `FR-13`):**
   > *"To equip parents and teachers with an offline **Parent/Teacher Dashboard** that computes 7-day retention scores, flags each letter's mastery status as **Green** ($\ge 80\%$ accuracy), **Yellow** ($60\text{–}79\%$ accuracy), or **Red/At-Risk** ($< 60\%$ accuracy or $> 3$ failed attempts), and exports a formatted **A4 PDF Progress Report** locally without internet access."*

6. **Specific Objective 6 — Offline-First Performance, Pediatric Accessibility & Evaluation (`NFR-Perf`, `NFR-Off`, `NFR-Acc`, `NFR-Usa`, `NFR-Sec`):**
   > *"To ensure $100\%$ offline functionality on entry-level Android devices (Android 8.0+ / API 26+, $\ge 2\text{–}3\text{ GB}$ RAM, $\le 150\text{ MB}$ storage footprint), pediatric ergonomic compliance ($\ge 24\text{ sp}$ primary reading text, high-contrast child UI, zero network permissions), and validate system effectiveness, usability, and pedagogical alignment with Grade 1 learners, DepEd reading teachers, and parents."*

---

## 4. Verbatim Baseline Measurable Targets (`2526-sem2-it332-56_playIT_SRS.pdf` v2.0)

The table below extracts the exact quantitative engineering and pedagogical targets approved in `2526-sem2-it332-56_playIT_SRS.pdf` (Sections 3.2 and 3.3):

| Baseline Requirement ID | Requirement Title | Approved Quantitative Target in SRS v2.0 |
| :--- | :--- | :--- |
| **`FR-01` & `FR-02`** | Progressive Phonics Map & Unlocking | **26 letters** organized into **6 Marungko-inspired groups**; Group 1 (`M, S, A, I, O, B`) unlocked by default; subsequent letters/groups unlock sequentially upon completion. |
| **`FR-03` & `NFR-Perf-02`** | Hear It Audio & Visual Modeling | **100% accurate playback** of phoneme sound + example word; each audio clip duration **$\le 5.0\text{ seconds}$**; synchronized mouth-motion visual and object illustration. |
| **`FR-04` & `NFR-Perf-01`** | Say It Offline Speech Recognition | Offline **Vosk** phoneme/word evaluation; **$\ge 75\%$ recognition accuracy**; response time **$\le 0.5\text{ seconds}$ ($500\text{ ms}$)**; maximum of **3 attempts** per letter before scaffolded progression. |
| **`FR-05` & `NFR-Perf-03`** | Find It Letter Discrimination | **3 rounds** per lesson with **3 choices** (1 target + 2 distractors) or contextual picture grid; tap-to-feedback response time **$\le 0.3\text{ seconds}$ ($300\text{ ms}$)**. |
| **`FR-06`–`FR-08`** | Heart Pool & Recovery Mechanics | **5-heart initial pool**; $-1$ heart per incorrect selection; at $0$ hearts, lesson restarts and refills to **3 hearts**. |
| **`FR-09`** | Star Calculation & Streak Milestones | **$3\star$** ($0$ hearts lost), **$2\star$** ($1\text{–}2$ hearts lost), **$1\star$** ($\ge 3$ hearts lost); streak badges awarded at **5, 10, 15, and 20 consecutive days**. |
| **`FR-10` & `FR-11`** | Multi-Profile & Arithmetic Gate | Up to **6 local learner profiles** per device; Parent Zone gated by randomized **single-digit addition/subtraction challenge**. |
| **`FR-12` & `FR-13`** | Diagnostic Risk Flags & PDF Export | **Green** ($\ge 80\%$ accuracy), **Yellow** ($60\text{–}79\%$), **Red/At-Risk** ($< 60\%$ or $> 3$ failed attempts); offline **A4 PDF report** saved to local storage. |
| **`NFR-Off-01` & `02`** | 100% Offline Execution & SQLite Persistence | **Zero internet dependency** after installation; **100% local SQLite (Room) persistence** across app restarts and force-closes. |
| **`NFR-Acc-01` & `02`** | Pediatric Accessibility & Typography | Minimum **24 pt (`24 sp`)** primary reading text; high-contrast child cards; maximum **5 distinct visual element groups** per child screen. |
| **`NFR-Sec-01`–`03`** | Child Data Privacy (RA 10173) | **Zero external data transmission**; no cloud accounts or PII collection; local-only profile storage. |

---

## 5. Traceability Comparison: Approved Baseline vs. Post-MVP Refactored System (SRS v3.0)

Following the **Weeks 1–2 MVP Validation** (conducted with 18 evaluators: 4 Grade 1 teachers, 6 parents, 5 child proxy sessions, and 3 QA evaluators), all original SMART targets were **preserved** and several were **strengthened** to resolve empirical findings (`MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md` and `SRS_v3.0_Refactored.md`):

| Domain Area | Originally Approved Target (SRS v2.0) | Post-MVP Refactored Enhancement (SRS v3.0 / `v1.2.0-rc2`) | Rationale / Evidence from MVP Validation |
| :--- | :--- | :--- | :--- |
| **1. Phoneme Audio Purity (`FR-03`)** | 100% playback accuracy; $\le 5.0\text{ s}$ duration. | **Preserved & Tightened (`HI-1`):** Replaced synthetic schwa-contaminated clips (`/mʌ/`, `/sʌ/`) with human/Kokoro/Chatterbox/ElevenLabs audited clips; requires **100% (26/26) Gate 3 blind teacher pass**. | Teachers in MVP validation flagged synthetic schwa intrusion (`/ʌ/`) on stop consonants as a critical phonics hazard. |
| **2. Offline Speech Recognition (`FR-04`)** | $\ge 75\%$ Vosk accuracy; $\le 0.5\text{ s}$ latency; 3 attempts. | **Preserved & Tightened (`SI-1`–`SI-3`):** Hybrid constrained grammar (`SpeechValidator`) rejecting letter-name (`"em"`) and vowel-added (`"muh"`) foils; target raised from $\ge 75\%$ to **$\ge 80\%$ teacher-ASR agreement** and **$\le 15\%$ false-reject rate**, while keeping **$\le 500\text{ ms}$ latency**. | Raw unconfined Vosk grammar in the MVP produced false rejections on high-pitched child speech and accepted letter names instead of phoneme sounds. |
| **3. Heart Deduction Policy (`FR-06`–`FR-08`)** | 5-heart pool; 3-heart refill on restart. | **Preserved with Pedagogical Guard (`SI-4`):** 5-heart pool and 3-heart refill remain intact for `Find It` and `Blend It`, while **`Say It` is made non-punitive (`0` hearts lost)** with scaffolded mouth cues on Attempts 2–3. | Child observation in MVP showed that losing hearts on ASR false-rejects caused microphone anxiety and refusal to speak. |
| **4. Word Blending (`FR-NEW-01` / `FR-13`)** | Proposal Objective 2 included *"blend sounds into words"*, but MVP only had single-letter stages. | **Fully Implemented (`BlendItScreen`):** Added 6 group-end **Blend It** treasure milestone nodes with **33 decodable CVC/VVC words** (`SAM, MAT, SIT, TUB, YAM, ...`) and 3D letter-tile synthesis. | Fulfilled the original proposal's word-blending promise and addressed teacher requests for phoneme synthesis practice. |
| **5. Child Onboarding & Touch Targets (`FR-10`, `NFR-Acc`)** | Up to 6 profiles; $\ge 24\text{ sp}$ text; $\le 5$ element groups. | **Enhanced (`O3`):** Avatar-first child onboarding (zero typing required for children; parent renames in Parent Zone); enforced **$\ge 64\text{ dp} \times 64\text{ dp}$** touch targets and **Duolingo ABC grounded pastel stages** (Card 32). | Pre-readers struggled with QWERTY keyboard entry during onboarding and sub-48dp buttons on compact phones. |
| **6. Parent Dashboard & PDF Report (`FR-12`, `FR-13`)** | Green/Yellow/Red flags, 7-day retention, offline A4 PDF export. | **Preserved & Aligned (Card 32):** Retains exact Green ($\ge 80\%$), Yellow ($60\text{–}79\%$), and At-Risk ($< 60\%$ or $> 3$ fails) logic, while updating `PdfExporter` to match `ParentDashboardScreen` and replacing harsh `#D63031` crimson with `#FF9F43` amber-coral. | Ensures 100% visual and numerical parity between on-screen analytics and printed A4 reports while honoring the Zero-Harsh-Red policy. |
