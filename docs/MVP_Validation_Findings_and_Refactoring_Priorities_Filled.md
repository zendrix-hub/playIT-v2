# MVP Validation Findings & Refactoring Priorities

**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Target Milestone:** Weeks 1–2 MVP Validation Deliverable & Week 3 Refactoring Bridge  
**Document Version:** 2.1 (checked on 2026-10-10 against `docs/specs/validation-report.md` and the code)  
**Date of Submission:** October 10, 2026  

> This file is the source of the course form. `tools/docs/render_submission_package.py` fills the official template (`docs/MVP Validation Findings & Refactoring Priorities.docx`) from it, so keep the section headings, the numbered questions and the `- [x]` lines in this layout. Only the checked options are listed here; the form shows every option with its box ticked or empty. Figures come from `docs/specs/validation-report.md`.

---

## Section 1 — Project Information

- **Team/Group Name:** Group 56 — PlayIT Capstone Team
- **Project/System Title:** PlayIT: An Offline-First Gamified Early Literacy Mobile Application Using the DepEd Marungko Approach
- **Program / Section:** Bachelor of Science in Information Technology / IT411 G1–G8
- **Team Members:**
  1. Riva, Zendrix (Team Lead & Full-Stack Android Engineer)
  2. Palis, J. J. (Frontend & Audio Engineer)
  3. Miel, K. (UX/UI Designer & QA Specialist)
  4. Durano, A. S. (Data Architect & Backend Engineer)
  5. Bien, E. S. (Pedagogical Researcher & Documentation Specialist)
- **Adviser:** Mr. Joemarie C. Amparo
- **System URL / MVP Link:** Offline Android APK (`playit-debug.apk`; package `com.playit.app`; min SDK 26 / Android 8.0, target SDK 34 / Android 14). The app runs without an internet connection, so it has no web URL.
- **Date of MVP Validation:** September 16–23, 2026
- **Number of respondents/participants:** Total N = 25: 16 early learners, 5 parents or supervising teachers, and 4 DepEd-certified Grade 1 reading teachers. The plan was 30 including IT evaluators; none were recruited in Round 1.
- **Types of respondents involved:**
  - [x] **Customer:** parents and supervising teachers who watched a child use the app (N = 5)
  - [x] **End User:** Grade 1 and early learners, ages 5–7 (N = 16)
  - [x] **Subject Matter Expert:** DepEd-certified Grade 1 reading teachers, coded T-1 to T-4 (N = 4)

---

## Section 2 — MVP Validation Overview

### 1. What was the primary purpose of your MVP validation?
The Weeks 1–2 MVP validation was formative and diagnostic, not summative. The team wanted to learn whether the MVP's learning loop (Hear It for phoneme modeling, Say It for speech production with on-device speech recognition, Find It for sound discrimination, and Blend It for CVC word building) works for Grade 1 learners in real classroom, home and community settings, and what keeps it from reaching the project's SMART objectives. In particular, the team looked for pedagogical defects such as letter sounds with an added vowel, interaction problems such as children hesitating at the microphone, and accessibility gaps, so they could be fixed in the Week 3 refactoring of the SRS, SDD and SPMP before full implementation and the Round 2 evaluation.

### 2. What validation framework/model did your team use?
- **Framework/Model:** The Usability, Pedagogy, and Accessibility (UPA) educational evaluation framework, with two standard instruments: the System Usability Scale (SUS; Brooke, 1996) for parents and supervising teachers, and the Smileyometer from the Fun Toolkit (Read & MacFarlane, 2006) for the children.
- **Key constructs/criteria evaluated:**
  1. *Pedagogy (P):* alignment with DepEd Grade 1 literacy competencies, the logic of the Marungko-based letter sequence, and whether letter sounds are pronounced correctly (12-item teacher checklist).
  2. *Usability (U):* children's enjoyment and perceived ease, hesitation at the Say It microphone (facilitator notes), and caregiver-perceived usability against the SMART target of SUS ≥ 75.
  3. *Accessibility (A):* a 7-item caregiver checklist (text size, narration, tap-only input, contrast, language, and support for hearing and motor difficulties) and offline operation.
- **Why was this framework/model appropriate for your project?** Early literacy software can work and engage children yet still teach the wrong sounds, so usability alone is not enough. The UPA framework checks the three things the project's objectives depend on at once: curriculum and phonics accuracy (judged by DepEd teachers), child-appropriate interaction (judged with child-friendly instruments), and access for young learners on low-cost devices without an internet connection.

### 3. Briefly describe how the validation was conducted.
- **Who participated:** 25 respondents: 16 early learners aged 5–7 (some in kindergarten), 5 parents or supervising teachers, and 4 DepEd-certified Grade 1 reading teachers (coded T-1 to T-4 under the Data Privacy Act, RA 10173).
- **How they interacted with the MVP:** Each child used `playit-debug.apk` on a physical Android phone or tablet for about 15 minutes, in classroom, home and community settings, with no internet connection (the build has no network permission). The protocol specified ambient noise of 40 dB or less, checked with the app's built-in indicator.
- **What activities/tasks they performed:** Children played the Chapter 1 letters (m, s, a, i) through Hear It, Say It and Find It, then built the Chapter 1 Blend It word (SAM). Parents and supervising teachers watched a child play and then answered the 10-item SUS, the 7-item accessibility checklist and two Likert items (grade fit and recommendation). Teachers answered the 12-item Yes/No pedagogical checklist and wrote open comments.
- **How feedback/data was collected:** Three Google Forms (child Smileyometer, parent/teacher evaluation, teacher pedagogical checklist), compiled in the Master (responses).xlsx workbook (kept in the access-restricted team folder); facilitator observation notes; and the teachers' written comments.

---

## Section 3 — MVP Validation Findings

### Finding #1: Phonemic Audio Impurity and Trailing Vowel Intrusion
1. **What did you learn from the stakeholders?**  
   Some phoneme clips in Hear It and Say It did not model a pure letter sound: they added a trailing vowel (schwa, /ə/) or sounded like the letter name. The clearest case was /m/, which sounded like "ma, ma, ma" instead of a held hum [m:].
2. **What evidence supports this finding?**  
   - Pedagogical checklist item PED-08 ("Letter sounds pronounced correctly"): 2 of 4 DepEd teachers flagged it. It was the only one of the 12 items not endorsed by all four teachers.
   - Teacher T-1 (DepEd Grade 1 reading teacher): *"One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.' Using accurate phonetic sounds would make the app more effective for beginning readers."*
   - Teacher T-2 (reading facilitator): *"My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it."*

   *Evidence type:* Validation responses; Comments from users/SMEs
3. **Which stakeholder/user type provided this feedback?**  
   - [x] SME: 2 of the 4 DepEd-certified Grade 1 reading teachers (T-1, T-2)
4. **Which SMART objective or measurable outcome is affected?**  
   **Objective O1 (Hear It):** 100% of phoneme clips are pure (no added vowel), each rated "Pure" by at least 3 of 4 teachers; playback per letter ≤ 5 s.
5. **What measurable outcome could potentially be improved?**  
   - [x] Accuracy
   - [x] Effectiveness
6. **Explain how the finding affects the achievement of the objective.**  
   In the Marungko approach, children blend isolated sounds into words. A child who learns /m/ as "ma" and /s/ as "sa" decodes SAM as "sa-a-ma" instead of /s/-/a/-/m/. Because Chapter 1 teaches only continuous sounds, an added vowel damages the very first blending task, so Objective O1 cannot be met with the MVP's clips.

### Finding #2: Say It Microphone Hesitation and Unclear Listening State
1. **What did you learn from the stakeholders?**  
   Children hesitated at the Say It microphone button because the screen gave no sign that the app was listening, so they could not tell when to speak or whether they had been heard.
2. **What evidence supports this finding?**  
   - Facilitator observation notes recorded children hesitating at the microphone button, which had no listening indicator.
   - Child Smileyometer, perceived ease ("Was it easy to play?"): 9 of 16 children chose the top face and 7 of 16 (43.8%) did not. Ease was the lowest of the four Smileyometer items; enjoyment, characters and replay were 88–94% top face.

   *Evidence type:* Observation; Validation responses
3. **Which stakeholder/user type provided this feedback?**  
   - [x] End User: Grade 1 and early learners (Smileyometer ease ratings)
   - [x] Other: session facilitators (observation notes)
4. **Which SMART objective or measurable outcome is affected?**  
   **Objective O2c (Say It):** children speak without hesitating at the mic (proposed target: at least 85% of mic turns with no hesitation event). Also **Objective O2a:** the app's decision agrees with the teacher's judgment on at least 80% of attempts, because hesitant, clipped speech is harder to recognize.
5. **What measurable outcome could potentially be improved?**  
   - [x] Usability
   - [x] Efficiency
   - [x] User satisfaction
6. **Explain how the finding affects the achievement of the objective.**  
   A child who is unsure whether the app is listening speaks late, speaks too softly, or stops early. The recording then misses part of the word, the recognizer may reject a correct answer, and the child needs an adult to explain what to do. This works against O2c directly, against O2a through avoidable recognition errors, and against unprompted completion (O5).

### Finding #3: Accessibility Gaps for Hearing and Motor Difficulties
1. **What did you learn from the stakeholders?**  
   The MVP relied on sound alone to teach letter sounds, with no visual support for learners with hearing difficulties, and some corner controls were too tight for young children with motor difficulties.
2. **What evidence supports this finding?**  
   - Accessibility checklist item ACC-06 (options for learners with hearing difficulties): 3 of 5 Yes; 2 of 5 parents or supervising teachers reported no support. The MVP had no captions or mouth-shape cues.
   - Accessibility checklist item ACC-07 (easy navigation for learners with motor difficulties): 4 of 5 Yes. The one No was linked to tight padding around the corner menu toggles.
   - The other five accessibility items (text size, narration, tap-only input, contrast, language) were 5 of 5 Yes.

   *Evidence type:* Validation responses
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Customer: parents and supervising teachers (accessibility checklist, N = 5)
4. **Which SMART objective or measurable outcome is affected?**  
   **Objective O6 (architecture and access):** all modules work offline and the accessibility criteria are met, including visual cues for audio content and touch targets of at least 64 dp.
5. **What measurable outcome could potentially be improved?**  
   - [x] Usability
   - [x] Other: accessibility for learners with hearing or motor difficulties
6. **Explain how the finding affects the achievement of the objective.**  
   Every module depends on sound, so a child with a hearing difficulty, or any child in a noisy classroom, misses the model sound with no visual fallback. Tight corner controls cause missed taps and frustration for children with developing motor control. Both gaps keep the accessibility criteria of O6 from being met.

### Finding #4: Non-Decodable Blend It Words
1. **What did you learn from the stakeholders?**  
   A design review of the 33 seeded Blend It words, done by the team after the pilot, found 6 words that a child cannot decode with the short-vowel letter-sounds the app teaches.
2. **What evidence supports this finding?**  
   - The review of the seeded word list (`DatabaseModule.kt`) flagged AIM (vowel team *ai*), BEE (*ee*), TOY and BOY (diphthong *oy*), ZOO (*oo*) and QUIZ (*qu* = /kw/). The other 27 words are decodable.
   - These words passed the MVP's check because it tested whether each letter was unlocked, not whether the letter's sound in that word had been taught.
   - Teachers did not rate the word bank in Round 1; Round 2 adds a teacher decodability rating for every word (Instrument A, Part 2).

   *Evidence type:* Other evidence: post-pilot design review of the word list
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Other: project team (post-pilot curriculum design review); teachers confirm the revised list in Round 2
4. **Which SMART objective or measurable outcome is affected?**  
   **Objective O4 (Blend It):** 100% of words decodable with the sounds taught (proposed: at least 80% of children build the chapter word within 2 attempts).
5. **What measurable outcome could potentially be improved?**  
   - [x] Effectiveness
   - [x] Accuracy
6. **Explain how the finding affects the achievement of the objective.**  
   Systematic phonics asks children to sound out every letter. A word with an untaught vowel team cannot be sounded out with the short vowels taught so far, so the child guesses or fails the checkpoint, and the Blend It result no longer shows whether the child can blend. O4 requires every word to be decodable.

### Finding #5: Key SMART Targets Could Not Be Measured (No Interaction Telemetry)
1. **What did you learn from the stakeholders?**  
   Round 1 could not measure several SMART targets because the MVP stored only overall scores and recorded no interaction events: the Say It judge's agreement with teachers, feedback latency, Find It accuracy and unprompted completion all went untested. Teachers also asked for a way to track learners' progress.
2. **What evidence supports this finding?**  
   - The validation report has no Round 1 result for O2a (agreement), O2b (latency), O3 (discrimination accuracy and feedback time) or O5 (completion without an adult prompt).
   - Microphone hesitation was recorded only in free-text facilitator notes, not as countable events, so the hesitation rate behind O2c is unknown.
   - The Weeks 1–2 validation summary records the teachers' request for progress tracking and lists a teacher-facing progress export as a needed feature.

   *Evidence type:* Comments from users/SMEs; Other evidence: gaps in the Round 1 data
3. **Which stakeholder/user type provided this feedback?**  
   - [x] Other: the validation team's review of the Round 1 data, and teachers' request for progress tracking
4. **Which SMART objective or measurable outcome is affected?**  
   **Objectives O2a, O2b, O3 and O5**, which need event-level data: agreement with teacher judgment of at least 80%, feedback within 0.5 s at P90, discrimination accuracy of at least 80% with feedback within 0.3 s, and at least 85% of children completing nodes without an adult prompt.
5. **What measurable outcome could potentially be improved?**  
   - [x] Performance
   - [x] Other: measurability of the SMART objectives
6. **Explain how the finding affects the achievement of the objective.**  
   Without an event log the team can report only impressions: it cannot show that the refactored build meets O2a, O2b, O3 or O5, or find which letters and steps cause errors. Round 2 needs a timestamp for every tap, utterance and feedback event, recorded offline on the device.

---

## Section 4 — Potential Refactoring Priorities

### Refactoring Priority #1: Pure Phoneme Audio Modeling and Release Gates
1. **Validation Finding**  
   Finding #1 (phonemic audio impurity; PED-08).
2. **What needs to be improved or changed?**  
   Re-make all 26 letter-sound clips as pure phonemes: continuous sounds (/m/, /s/, /f/ and others) held for about 800 ms, and short sounds (/b/, /t/, /p/ and others) cut cleanly with no added vowel. No clip ships before a teacher audit.
3. **What type of refactoring is required?**  
   - [x] Functional requirement: FR-02 (pure phoneme modeling sequence)
   - [x] Non-functional requirement: NFR-AUD-01 (audio production and three release gates)
   - [x] System process/workflow: audio production and release pipeline
   - [x] Data/input/output: phoneme clips and release manifests
4. **Current MVP approach**  
   Letter sounds were generated with Edge-TTS from single-letter text, which produced letter names or added vowels ("ma", "es").
5. **Proposed refactored approach**  
   A local, offline audio pipeline. Kokoro-82M (Apache-2.0) voices the carrier lines and key words; held sounds are chosen by ear between Kokoro phoneme input and Chatterbox-Turbo (MIT) voice cloning; short sounds come from a human model (the short vowels are recorded by a team member and voice-converted with ElevenLabs, pending adviser confirmation). A clip enters the app only after the team approves it on a review page and it is listed, with its SHA-256 hash, in a release manifest; a teacher audit (Gate 3) follows before release. The Hear It sequence keeps pure sounds separate from key words and carrier lines.
6. **Why is this change necessary?**  
   Pure sounds are what children blend. Removing the added vowel removes the "sa-a-ma" error at its source and answers the teachers' request directly.
7. **Expected improvement**  
   All 26 phoneme clips rated "Pure" by at least 3 of 4 DepEd teachers; no added vowels; playback per letter of 5 s or less.
8. **How will the improvement eventually be measured?**  
   Teacher Phoneme and Content Audit (Instrument A, Part 1): the four teachers rate each clip Pure or Not pure, with original and re-made clips mixed without labels.
   - [x] Accuracy rate
9. **Priority**  
   - [x] High: P0 (urgent); essential to SMART Objective O1

### Refactoring Priority #2: Four-State Microphone and Non-Punitive Prompt Ladder
1. **Validation Finding**  
   Finding #2 (microphone hesitation; 7 of 16 children did not choose the top face for ease).
2. **What needs to be improved or changed?**  
   Replace the static microphone icon with a mic that always shows one of four states (Idle; Listening, with an animated ripple; Heard, as soon as speech is detected; Result) and switches to Listening within 100 ms of a tap. Pair it with spoken, non-punitive corrections: Say It never removes hearts.
3. **What type of refactoring is required?**  
   - [x] Functional requirement: FR-03 (mic states and prompt ladder)
   - [x] System process/workflow: we-do and you-do tutoring steps with a 3-step prompt ladder
   - [x] Usability: a clear listening state for pre-readers
4. **Current MVP approach**  
   A static microphone icon that gave no sign of listening or of speech being detected; wrong answers cost hearts.
5. **Proposed refactored approach**  
   A `MicButton` driven by a `MicStatus` state in `SayItViewModel`: Heard appears as soon as the recognizer reports speech, and the mic returns to Idle if the app goes to the background. A miss gets a spoken correction matched to the error (letter name: "That's its name. Its sound is /m/."; added vowel: "Just /m/, no 'ah.'"), then a slower re-model with a mouth cue, then practice together; no hearts are lost. A ripple that follows the live input level comes with the planned `AudioRecord` capture loop after Round 2.
6. **Why is this change necessary?**  
   Children speak when the screen clearly shows the app is listening, and they hear exactly what to fix. Without heart loss, a recognition error does not feel like a failure.
7. **Expected improvement**  
   At least 85% of mic turns with no hesitation event (proposed target; Round 1 recorded hesitation only in notes); feedback within 0.5 s of the end of speech at P90.
8. **How will the improvement eventually be measured?**  
   Session Observation Sheet (Instrument B) for hesitation events, and the app's telemetry export (Instrument C) for latency.
   - [x] System response time
   - [x] Efficiency measure
9. **Priority**  
   - [x] High: P1; essential to SMART Objectives O2a, O2b and O2c

### Refactoring Priority #3: Decodable Blend It Word Bank
1. **Validation Finding**  
   Finding #4 (non-decodable Blend It words).
2. **What needs to be improved or changed?**  
   Replace the 5 non-decodable words (AIM, BEE, TOY, BOY, ZOO) with decodable ones (AM, SUM, TUB, YAM, ZIP), and keep QUIZ as the one documented exception (*qu* = /kw/).
3. **What type of refactoring is required?**  
   - [x] Functional requirement: FR-13 (decodable word bank)
   - [x] Data/input/output: seeded Blend It word list
4. **Current MVP approach**  
   Any word whose letters were all unlocked could appear, even if a letter's sound in that word had not been taught.
5. **Proposed refactored approach**  
   A seed list in which every word uses only sounds taught up to its chapter. Each replacement keeps the id of the word it replaces, so saved progress is kept, and the list is rewritten on every app start, so existing installs receive it. A unit test checks every word against the letters taught so far, with QUIZ as the only listed exception.
6. **Why is this change necessary?**  
   Beginning readers can sound out every challenge word, so Blend It measures blending rather than guessing.
7. **Expected improvement**  
   100% of Blend It words rated decodable by the DepEd teachers (or listed as documented exceptions); more children build the chapter word on their own.
8. **How will the improvement eventually be measured?**  
   Teacher decodability rating (Instrument A, Part 2) and Blend It attempts in the telemetry export (Instrument C).
   - [x] Accuracy rate
   - [x] Task completion rate
9. **Priority**  
   - [x] High: P1; essential to SMART Objective O4

### Refactoring Priority #4: On-Device Interaction Telemetry and CSV Export
1. **Validation Finding**  
   Finding #5 (SMART targets not measurable without interaction telemetry).
2. **What needs to be improved or changed?**  
   Record every learning interaction on the device with precise timestamps, and let teachers export the log behind the parent gate.
3. **What type of refactoring is required?**  
   - [x] Functional requirement: FR-NEW-TEL (telemetry logging and export)
   - [x] System architecture/design: Room schema v4 with a migration
   - [x] Data/input/output: RFC 4180 CSV export
4. **Current MVP approach**  
   No interaction log: only overall scores were stored, so latency, agreement and completion could not be computed.
5. **Proposed refactored approach**  
   A `TelemetryLogger` writes a `TelemetryEventEntity` row for each mic tap, end of speech, recognizer result, feedback shown, Find It tap and Blend It attempt, with a monotonic `SystemClock.elapsedRealtime()` timestamp and the profile and session ids, in a Room schema v4 table (with a migration from v3). A PIN-gated CSV export shares the file through the Android share sheet; nothing is sent over a network.
6. **Why is this change necessary?**  
   The Round 2 targets (O2a, O2b, O3, O5) can be verified only from event-level data, and teachers asked for a way to follow learners' progress.
7. **Expected improvement**  
   100% of interaction events recorded with millisecond timestamps, so every Round 2 metric can be computed from the log.
8. **How will the improvement eventually be measured?**  
   Telemetry export check (Instrument C) on the test devices: the exported CSV is complete, and the latency, accuracy and completion figures can be computed from it.
   - [x] System response time
   - [x] Accuracy rate
   - [x] Task completion rate
9. **Priority**  
   - [x] High: P1; needed to measure O2a, O2b, O3 and O5 in Round 2

### Refactoring Priority #5: Sound Captions and Mouth-Shape Cues
1. **Validation Finding**  
   Finding #3 (hearing accommodations 3 of 5 Yes; ACC-06).
2. **What needs to be improved or changed?**  
   Show a caption for every clip as it plays, and a mouth-shape cue for each letter sound (9 mouth-shape groups cover the 26 letters).
3. **What type of refactoring is required?**  
   - [x] Non-functional requirement: NFR-ACC-01 (captions and articulation cues)
   - [x] Usability: visual support for hearing-impaired learners and noisy rooms
4. **Current MVP approach**  
   Audio played with only the letter, the picture and a mascot speech bubble; nothing on screen showed the sound or how to make it.
5. **Proposed refactored approach**  
   A `CaptionBubble` shows each clip as it starts (/m/ for the sound, the key word, the carrier line), and an `ArticulationCue` shows the mouth shape for the letter (lips together for /m/, teeth close together for /s/) in Hear It and from the second Say It miss.
6. **Why is this change necessary?**  
   Children who cannot hear the model clearly can still see the sound and how to make it, which also helps every child in a noisy classroom.
7. **Expected improvement**  
   Accessibility check D-09 passes; the hearing-accommodation rating rises from 3 of 5 to 5 of 5.
8. **How will the improvement eventually be measured?**  
   Technical and Accessibility Verification (Instrument D, check D-09) and the caregiver accessibility checklist in Round 2.
   - [x] Usability score
   - [x] User satisfaction
9. **Priority**  
   - [x] Medium: P2; significant improvement to accessibility (Objective O6)

### Refactoring Priority #6: Adaptive Layout and 64 dp Touch Targets
1. **Validation Finding**  
   Finding #3 (motor navigation 4 of 5 Yes; ACC-07).
2. **What needs to be improved or changed?**  
   Make every child-facing control at least 64 dp, and fit every lesson screen on every supported phone and tablet (360x640 dp and up) without cutting off the main button, including at 1.3x system font size.
3. **What type of refactoring is required?**  
   - [x] Non-functional requirement: NFR-ACC-02 (touch targets and layout)
   - [x] System architecture/design: adaptive layout tokens and a shared lesson scaffold
   - [x] Usability: reliable taps for children with developing motor skills
4. **Current MVP approach**  
   Fixed heights and default 48 dp buttons. On small screens (360x640 dp) or at 1.3x font size, the team's layout tests found main buttons pushed below the visible area.
5. **Proposed refactored approach**  
   Adaptive `PlayItDimens` tokens for three window profiles (compact, regular, wide), a 64 dp minimum on every child-facing control, and a `LessonScaffold` that pins the top bar, header and bottom button while the body fits or scrolls.
6. **Why is this change necessary?**  
   Children with developing fine motor skills can hit every control, and no child loses the main button off-screen on a small phone.
7. **Expected improvement**  
   No cut-off main buttons on the 4 test sizes (360x640, 360x740, 411x891 and 800x1280 dp); every child-facing control at least 64 dp.
8. **How will the improvement eventually be measured?**  
   Automated layout tests (`LayoutMatrixTest` with Roborazzi screenshots on 4 sizes) and the touch-target check on a physical device (Instrument D, check D-07).
   - [x] Error rate
   - [x] Usability score
9. **Priority**  
   - [x] Medium: P2; essential pediatric usability standard (Objective O6)

---

## Section 5 — Refactoring Summary Matrix

| # | Validation Finding | Evidence | SMART Objective / Outcome Affected | Proposed Refactoring | Expected Improvement | Priority |
|:---:|---|---|---|---|---|:---:|
| 1 | Phoneme clips add a vowel or sound like letter names (Finding #1) | PED-08 flagged by 2 of 4 teachers; T-1 and T-2 comments | O1: 100% pure clips | Re-make the 26 clips through the gated audio pipeline (FR-02, NFR-AUD-01) | All clips rated Pure by at least 3 of 4 teachers | High (P0) |
| 2 | Children hesitate at the Say It mic (Finding #2) | Facilitator notes; 7 of 16 children did not choose the top face for ease | O2c: at least 85% of mic turns without hesitation (proposed); O2a: at least 80% agreement | Four-state mic and non-punitive prompt ladder (FR-03) | At least 85% of mic turns without hesitation; P90 feedback within 0.5 s | High (P1) |
| 3 | 6 of 33 Blend It words not decodable (Finding #4) | Post-pilot design review of the seeded word list | O4: 100% decodable words | Replace AIM, BEE, TOY, BOY, ZOO with AM, SUM, TUB, YAM, ZIP; QUIZ documented (FR-13) | 100% of words rated decodable by teachers | High (P1) |
| 4 | Agreement, latency, accuracy and completion not measurable (Finding #5) | No Round 1 result for O2a, O2b, O3, O5; teachers asked for progress tracking | O2a, O2b, O3, O5 | On-device telemetry log and PIN-gated CSV export (FR-NEW-TEL) | Every interaction event logged with millisecond timestamps | High (P1) |
| 5 | No visual support for hearing difficulties (Finding #3) | ACC-06: 3 of 5 Yes | O6: accessibility criteria met | Sound captions and mouth-shape cues (NFR-ACC-01) | D-09 passes; hearing rating 5 of 5 | Medium (P2) |
| 6 | Tight corner controls; main buttons cut off on small screens (Finding #3) | ACC-07: 4 of 5 Yes; team layout tests at 360x640 dp | O6: touch targets of at least 64 dp | Adaptive layout tokens, `LessonScaffold`, 64 dp floor (NFR-ACC-02) | No cut-off buttons on 4 sizes; all targets at least 64 dp | Medium (P2) |

---

## Section 6 — Items That Are NOT Refactoring Priorities

| Item | Identified? | Team's justification (added to the form's Action column) |
|---|:---:|---|
| Bugs/errors | Yes | Routine hardening, not refactoring: for example, overlapping audio on rapid taps in Say It is now debounced. |
| Minor UI/UX issues | Yes | Presentation polish with no requirement change, for example the map backdrop shapes and the mascot's idle timing. |
| Missing original requirement | No | All four core modules from the Capstone 1 proposal (Hear It, Say It, Find It, Blend It) were in the MVP. |
| Missing essential functional requirement | Yes | Incorporated: on-device telemetry and export (FR-NEW-TEL) is new in SRS v3.0. Multi-profile support for shared classroom devices already existed in the MVP and is now a formal requirement (FR-14). |
| Missing non-functional requirement | Yes | Incorporated: captions and mouth-shape cues (NFR-ACC-01) and the 64 dp touch-target and layout floor (NFR-ACC-02) were added to SRS v3.0. |
| New feature unrelated to SMART objectives | Yes | Not prioritized: T-2's suggestion of a reading app for advanced readers (blends and digraphs such as SH, CH, TH) is outside the Grade 1 single-letter scope and stays on the post-capstone roadmap. |
| Improvement to measurable outcome | Yes | Priorities #1 to #6 each target a measurable outcome (O1, O2a–O2c, O3–O6). |
| Change in system approach/design | Yes | A tutoring layer with a prompt ladder, pure phonemes kept separate from key-word and carrier clips, an adaptive lesson scaffold, and an on-device event log. |
| Improvement in accuracy/efficiency/effectiveness/performance | Yes | Per-letter recognition grammars with foils, targeting at least 80% agreement with teachers and P90 feedback within 0.5 s. |

### Important Question
Not applicable: the validation did identify significant requirement and design improvements (Section 4). The team limited the refactoring to findings that affect a SMART objective; routine bug fixes, visual polish and the advanced-reader track were kept out of the refactoring scope (table above).

---

## Section 7 — Final Team Reflection

### 1. What is the single most important thing your team learned from the MVP validation?
Pedagogical accuracy must govern the technology in early literacy software. In adult applications, small pronunciation differences in text-to-speech hardly matter; in foundational phonics for Grade 1 children, a synthetic voice that adds even a short trailing vowel (/m/ as "ma") damages the child's ability to blend sounds into words. An app that works and that children enjoy can still teach the wrong thing if it is not grounded in phonics.

### 2. What is the most important change your team should make based on this learning?
Rebuild the audio pipeline: replace the generic text-to-speech clips with verified pure phonemes (held continuous sounds and clean short sounds), and let a clip into the app only after it passes the release gates: team approval on a review page, a SHA-256 release manifest, and an audit by DepEd reading teachers.

### 3. How will this change help your project achieve its SMART objectives more effectively?
Children who use Hear It and Say It will learn correct, isolated sounds, so in Blend It they can blend /s/ + /a/ + /m/ into SAM without extra syllables. This serves Objective O1 (100% pure phonemes) directly, supports Objective O4 (building the chapter word), and gives teachers a reason to trust PlayIT as a supplementary classroom tool.

### 4. What requirements and/or design documents will need to be updated as a result?
- [x] **SRS:** v3.0 (rev. 3.3): FR-02 (pure phonemes), FR-03 (mic states and prompt ladder), FR-13 (decodable words), FR-14 (multi-profile), FR-NEW-REC (spaced recall), FR-NEW-TEL (telemetry), NFR-AUD-01, NFR-ACC-01 and NFR-ACC-02.
- [x] **SDD:** v2.0 (rev. 2.3): audio subsystem, tutoring layer (`TutorPolicy`, `SpeechValidator`), the Room schema v3 to v4 plan, the adaptive `LessonScaffold`, and a §2.0 table of what is implemented and what is planned.
- [x] **SPMP:** v2.0 (rev. 2.3): 9 work packages, the revised Weeks 3–9 schedule, audio tooling and test devices, and the risk register R1–R8.
- [x] **Other:** RTM v3.0 (in the SRS): 16 rows linking findings F-01 to F-16 to objectives, requirements, design components and test cases.

---

## Final Declaration

We certify that the findings and proposed refactoring priorities submitted in this form are based on actual MVP validation feedback gathered from our identified stakeholders/users. We understand that the proposed refactoring must be justified in relation to our project's SMART objectives and measurable outcomes.

**Team Lead:** Zendrix Riva  
**Date:** October 10, 2026
