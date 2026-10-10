# PlayIT defense reviewer

For the Week 9 midterm oral defense (Nov 2-7, 2026). This file replaces `docs/INTERVIEW_PREPARATION_GUIDE.md`. Numbers come from `docs/defense/NUMBERS.md`; if a number isn't there, don't say it.

**The rule for every answer:** say what the system does today, what evidence we have, and what is planned. A panel forgives a limitation you name yourself. It does not forgive a claim it catches.

**Answer shape (20-40 seconds):** the direct answer first, then one piece of evidence, then the limit or next step.

---

## 1. Pitch (60 seconds)

> PlayIT is an offline Android app that teaches Grade 1 Filipino learners the sounds of the 26 English letters, in the Marungko order used in DepEd classrooms: m, s, a, i first, in seven chapters.
>
> Each letter follows "I do, we do, you do". In **Hear It**, Lily the tarsier models the sound and a key word. In **Say It**, the child says the key word and an offline speech recognizer checks it; it never takes away hearts. In **Find It**, the child picks the pictures that start with the sound. After each chapter, **Blend It** has the child build short words from the sounds.
>
> It runs with no internet: there is no network permission, and recognition happens on the device with Vosk. Up to six children can share one device, and parents get a PDF progress report behind a parent gate.
>
> In Round 1 we tested with 25 people: 16 children, 5 caregivers and 4 DepEd teachers. The teachers found our letter sounds were sometimes the letter name ("ma" instead of /m/), so we rebuilt the audio pipeline and the Say It feedback. Round 2 in Week 7 measures what Round 1 could not: how well our speech judge agrees with a teacher.

Don't open with "gamified", "AI-powered" or "effective". Open with the child and the problem.

---

## 2. What we can and cannot claim

| We can claim | We cannot claim |
|---|---|
| Works fully offline (no network permission) | That children learn better with PlayIT (no pre/post test, no control group) |
| No child audio is stored | That the speech judge is accurate on children (not measured yet) |
| Teachers judged the content aligned with Grade 1 and the sequence logical (4 of 4, 11 of 12 items) | That SUS is significantly above average (n = 5, p = .32) |
| Caregivers' mean SUS was 75.5 (CI 57.3-93.7) | Any latency figure (never measured) |
| Children's ratings were very positive (weak evidence: facilitator-entered, happy-face bias) | That it works in noisy classrooms (no noise handling; not tested) |
| The design follows "I do, we do, you do" with non-punitive feedback | That it is compliant with COPPA or the Data Privacy Act (we follow its principles; no legal review) |

---

## 3. Panel questions

Each question has the answer to give, the evidence, the follow-up to expect, and what **not** to say. Risk: **H** = they can catch us out; **M** = needs care; **L** = safe ground.

### A. Speech recognition and audio

**A1. Which speech recognizer do you use, and why that one?** (M)
- **Answer:** Vosk 0.3.47 with the small US-English model (68 MB). It runs fully on the device and is open source (Apache-2.0). It also lets us restrict recognition to a short word list per letter. Cloud recognizers like Google's would break the offline requirement.
- **Evidence:** `data/speech/VoskRecognizer.kt`; `app/src/main/assets/vosk-model/README`.
- **Follow-up:** "Is that model trained on children?" Answer: no, on adult speech. That is our biggest technical risk, which is why Round 2 measures accuracy on children.
- **Don't say:** "Vosk has superior phoneme accuracy." Our own spike shows it cannot confirm a pure sound.

**A2. How accurate is Say It on children?** (H)
- **Answer:** We haven't measured it yet, and we say so. Round 1 did not record speech data. Round 2 compares the app's decision with a teacher's judgement on each attempt. Our target is at least 80% agreement and at most 15% false rejects of correct attempts.
- **Evidence:** spec `docs/specs/hear-say-refactor.md` (judge targets); `docs/specs/validation-report.md` (Round 2 plan).
- **Follow-up:** "What if you miss the target?" Answer: Say It already never removes hearts. After 3 misses it leads the child through the sound together and moves on (marking the letter for a later review is planned, FR-NEW-REC; don't say it already does). So a false reject costs the child a retry, not a penalty. If agreement is low we lower the stakes further (Say It as practice only) and report it.
- **Don't say:** "about 75%", "75% confidence threshold". There is no data and no threshold.
- **Extra evidence we found ourselves (2026-10-06):** even on clean synthetic speech, Vosk confused close short vowels (tub/tab, miss/mess, vet/vat) at low confidence (`docs/spikes/vosk-foil-spike.md`, addendum). So our grammars never pit words that differ only by a short vowel against each other, and we expect children's vowels to be harder still. Volunteering this shows we test our own assumptions.

**A3. Does Say It check the letter sound, like /m/?** (H)
- **Answer:** No. It checks the key word, like "mouse". Our spike found Vosk can't confirm a held /m/, because its dictionary has no entry that matches a held nasal. So pure-sound scoring is switched off. The child says the sound with Lily (unscored), then says the key word, which is scored. Pure-sound scoring comes back for the recall check only if the on-device test with children passes.
- **Evidence:** `SpeechValidator.kt:36` (`SOUND_MODE_ENABLED = false`); `docs/spikes/vosk-foil-spike.md`.
- **Follow-up:** "So you don't assess phonemes?" Answer: Find It assesses the sound-to-picture link. Say It assesses producing a word that starts with the sound. A teacher would assess pure sounds; we don't claim the app does.

**A4. How do you catch a child saying the letter name ("em") or adding a vowel ("muh")?** (H)
- **Answer:** Each letter has its own grammar: the key word plus that letter's foils, e.g. mouse, m, em, ma, muh. Vosk can only return one of those words or "unknown". If it returns a foil, the app gives the matching correction ("That's the letter's name..."). Added vowels were caught on test audio. Letter names often came back empty, so we don't rely on catching them yet.
- **Evidence:** `SpeechValidator.grammarFor()`; spike results; the phone test of 2026-09-30.
- **Follow-up:** "Can a letter name be accepted as correct?" Answer: no. The only accepted words are the key word and its variants, so a foil can never count as correct.

**A5. Do you use a confidence score?** (H)
- **Answer:** No. We read only the recognized text (`VoskRecognizer.kt:175`). The grammar does the filtering. A confidence threshold would be tuned on Round 2 data and tested on separate data, not guessed.
- **Don't say:** any threshold number.

**A6. What's your latency?** (H)
- **Answer:** Not measured yet. Recognition runs on the device with a small grammar, so there's no network wait. The listening window is at most 3.8 s. The specs set a target (feedback within 0.5 s at P90) that Round 2 will check.
- **Don't say:** "under 400 ms", "≤500 ms".

**A7. What about classroom noise?** (H)
- **Answer:** There's no noise handling yet. A small grammar helps, because Vosk must pick one of a few words, but noise can still cause false rejects. Round 1 protocol required ≤40 dB checked with an approximate indicator, and Round 2 logs the measured level. Say It shows the mic's state: listening, "I hear you" once the recognizer picks up words, then the result (card 19). A meter that follows the child's voice level and a noise warning are planned; the recognizer we use doesn't report loudness, so that needs a new audio loop.
- **Don't say:** "NoiseMonitor", "AGC", "works in noisy classrooms".

**A8. Where does the audio come from? Isn't it AI?** (M)
- **Answer:** Yes, it's synthetic. Teachers flagged our first voice (Edge TTS) for sounding like letter names. We moved to Kokoro-82M, which is open source under Apache-2.0, and for held sounds like /m/ to Chatterbox-Turbo (MIT) cloned from the same voice. A clip reaches the app only if a team member approves it on a listening page and it is listed in a release manifest with its checksum. A teacher audit of every clip is required before release.
- **Evidence:** `docs/audio-release/*/manifest.json`; `tools/audio/`.
- **Follow-up:** "Has a teacher approved them?" Answer: not yet. The teacher audit is in Week 6 and is pending for all shipped clips. Some older Edge clips (204) are still in the app and are being replaced.
- **Don't say:** "native speaker recordings".

**A9. Why not record a human teacher?** (M)
- **Answer:** Consistency and pace. One voice across hundreds of clips that we can regenerate when wording changes. Synthetic voices also had a specific failure, the added vowel, which our pipeline now checks for. Short stop sounds (/b/, /t/) are hard for TTS, and the spec allows a human model for those.

### B. Architecture and engineering

**B1. Describe the architecture.** (M)
- **Answer:** MVVM with a pure-Kotlin domain layer, in three layers:
  - Compose screens observe ViewModel state (unidirectional data flow).
  - ViewModels call domain managers: the tutor policy, the speech validator, hearts, stars, the grid generator.
  - Data classes handle Room, audio and Vosk.
  - Hilt wires everything together.
- **Evidence:** the `domain/` folder has no `android.*` imports.
- **Follow-up:** "Is it Clean Architecture?" Answer: partly. The domain layer is pure and testable. But ViewModels also call some data-layer classes directly (the audio player, the PDF exporter), and we don't have separate use-case classes. We call it MVVM with a domain layer.
- **Don't say:** "strict Clean Architecture".

**B2. Why Kotlin and Jetpack Compose?** (L)
- **Answer:** Compose suits a highly animated, state-driven child UI. State changes redraw the screen without manual view updates. Kotlin coroutines handle audio and recognition without blocking the UI.

**B3. Why offline-first?** (L)
- **Answer:** Many public-school learners have limited or costly data. Offline also keeps children's data on the device, and the app works the same anywhere. There's no network permission, and an airplane-mode test passed.

**B4. Describe the database.** (M)
- **Answer:** Room (SQLite), schema version 3, with 11 tables:
  - content: phonemes, letter groups, group members, Blend It words;
  - per-child progress and attempts: profiles, lesson progress, Say It / Find It / Blend It attempts, Blend It progress, achievements.

  Every attempt is tied to a profile.
- **Follow-up:** "What happens on an app update?" Answer: today a schema change wipes progress (destructive fallback). The next schema version (v4) needs proper migrations with exported schemas and a migration test. It's planned.
- **Don't say:** "12 tables", "ReportLog", "migrations are handled".

**B5. How do you test it?** (M)
- **Answer:**
  - 226 JVM unit tests, all passing. They cover the domain rules (hearts, stars, speech judging, grid, tutor policy) and the ViewModels, using MockK and coroutine test tools.
  - CI runs them on every push.
  - A test scans the source for emoji (our zero-emoji policy).
  - Screenshot tests (Roborazzi) render the screens in CI; the layout tests check 4 phone sizes (cards 10 and 18).
- **Follow-up:** "UI tests? Recognizer tests?" Answer: 4 instrumented UI test classes exist but don't run in CI. The recognizer is checked by the spike and phone tests, not by automated audio tests. That's a gap.
- **Don't say:** "Truth, Turbine, DAO tests".

**B6. How does multi-profile work?** (L)
- **Answer:** Up to 6 children per device. The active profile is saved in SharedPreferences through `SessionManager`, and every progress row carries its profile id. Onboarding is avatar-only: a child picks an animal and never types. A parent can rename the profile in the Parent Zone.
- **Follow-up:** "What if no profile is active?" Answer: some ViewModels fall back to profile 1. That's a known edge case and is guarded by onboarding.

**B7. How big is the app, and what phone does it need?** (M)
- **Answer:** The debug APK is about 98 MB. Most of that is the 68 MB speech model and audio, built for three CPU types. It needs Android 8.0 or later. We haven't measured RAM use. A release build with per-device splits would be smaller.

**B8. What was the hardest technical problem?** (L, a story)
- **Answer:** Getting a speech recognizer to tell "mmm" from "em". We ran a spike with test audio:
  - an open vocabulary was useless;
  - a per-letter grammar caught added vowels;
  - nothing could confirm a held /m/.

  So we changed the design: score the key word, coach the sound, and never punish Say It. The lesson was to test the risky assumption first and let the evidence change the design.

**B9. How do you work as a team, and with AI tools?** (H, see E1)

### C. Data, privacy and child safety

**C1. Do you record children's voices?** (L)
- **Answer:** No. Audio streams in memory to Vosk, and only the result is stored: which letter, correct or not, and when. No audio file is ever written.
- **Evidence:** `SayItAttemptEntity.kt`.

**C2. Does any data leave the device?** (H)
- **Answer:** There are no network calls. Data can leave the device only when a parent shares the PDF report through the Android share sheet. One gap we're closing: Android's auto-backup was enabled (`allowBackup="true"`), which could copy the database to the parent's Google backup. Card 14 turns it off.
- **Don't say:** "fully COPPA and RA 10173 compliant". Say "designed around data minimization; no legal review yet".

**C3. How can a parent delete a child's data?** (H)
- **Answer:** Today only by clearing app data or uninstalling. The repository already supports deleting a profile, with a cascading delete in the database. Card 14 adds a "Delete this child's data" action in the Parent Zone, plus a short privacy notice.
- **Follow-up:** "Is that done by the defense?" Say only what is merged at that point.

**C4. Is the parent gate strong enough?** (M)
- **Answer:** It's a random two-digit addition or subtraction problem. That's enough to stop accidental taps by 6-year-olds, and it's a common pattern in children's apps. It isn't security: a strong reader could solve it. The Parent Zone holds no secrets, only progress, rename and export.
- **Don't say:** "multiplication like 7×8".

**C5. Data Privacy Act (RA 10173) in your study?** (H)
- **Answer:**
  - Participants are coded: C- for children, T- for teachers, P- for parents.
  - Reports carry no names or emails.
  - The data are kept in a restricted team folder.
  - An earlier version of our highlights report listed teacher names and emails. We found it in our own review and corrected it on 2026-10-05.
- **Follow-up:** "Ethics approval?" See D5.

### D. Methodology and statistics

**D1. What was Round 1 for, and what did it show?** (M)
- **Answer:** It was a formative validation: find problems before building further, not prove effectiveness. Its main finding came from 2 of 4 teachers: our letter sounds sometimes sounded like letter names. That drove the audio rework. Children hesitated at the mic, which drove the planned mic states. Caregivers' SUS was 75.5.

**D2. Your SUS is 75.5. Is that good?** (H)
- **Answer:** It's above the commonly cited average of 68, a B on the Sauro-Lewis scale. But with 5 respondents the 95% CI is 57.3 to 93.7, and the difference from 68 is not significant (p = .32). We treat it as a signal, not a result. Caregivers answered it after watching their child, so it's perceived usability.
- **Follow-up:** "Respondent 2?" P-2 agreed with all ten items, including the negative ones, which looks like acquiescent responding. We had no exclusion rule set in advance, so we kept them. Without P-2 the mean is 81.9, but we show that only as a sensitivity check.
- **Don't say:** "adjusted SUS 81.88", "B+", "excellent usability".

**D3. Did 6-year-olds fill in the SUS?** (M)
- **Answer:** No. Children used a 4-question, 5-face Smileyometer, a method designed for children (Read & MacFarlane). Adults did the SUS.

**D4. How reliable are the children's ratings?** (H)
- **Answer:** Weak, and we say so. Young children tend to pick the happiest face. One facilitator, who was also a teacher-evaluator, recorded 13 of the 16 ratings. We use them only as engagement signals. The useful signal was the lowest item, "easy to play": 7 of 16 children didn't pick the top face. That matched the observed hesitation at the mic. Round 2 separates the facilitator and evaluator roles.

**D5. Ethics approval and consent?** (H)
- **Answer:** Round 1 collected parent consent in the form. [Fill in before the defense: was there a signed consent and a child assent? Is there institutional approval, i.e. CIT-U research ethics, and DepEd clearance under DepEd Order 16 s. 2017 for school sites?] Round 2 uses written parent consent and verbal child assent, and records attempts with teacher ratings.
- **Action for the team:** get the actual answer from the adviser and the files. Do not improvise this one.

**D6. How did you choose participants?** (M)
- **Answer:** Convenience sampling: classroom, home and community sites, ages 5 to 7, so some kindergarteners were included. We reached 25 of the planned 30, and no IT evaluators. That limits generalization, which is why we call the results diagnostic.

**D7. Does PlayIT improve phonics learning?** (H)
- **Answer:** We don't claim that. Round 1 measured usability and content validity. Round 2 measures the judge's accuracy and short-term recall within one session. A learning effect needs a pre/post test over weeks, ideally with a comparison group. That's in future work.
- **Don't say:** "effective", "accelerates learning", "proven".

**D8. How will you measure the judge's accuracy in Round 2?** (H)
- **Answer:** For every Say It attempt, a teacher judges it correct or not while the app makes its own decision. From the 2×2 table we report:
  - percent agreement;
  - the false-reject rate among teacher-correct attempts;
  - Cohen's kappa, because agreement alone looks high when most attempts are correct;
  - 95% Wilson intervals.
- **Planned additions:** a second rater on a subset, and judging pass/fail on the interval, not just the point estimate.
- **Follow-up:** "Sample size?" Answer: about 100+ attempts from 16-20 children. At 80 of 100 the CI is roughly 71-87%, so we report the interval honestly.

**D9. Why only 4 teachers? Any conflict of interest?** (M)
- **Answer:** They are content experts, not a population sample. 4 of 4 agreement means expert opinion, not a statistic. [Fill in: is any teacher related to a team member? If yes, disclose it here and in the report.]

**D10. Threats to validity?** (M)
- **Answer:** Novelty effect (one short session), facilitator influence, happy-face bias, small convenience samples, synthetic test audio in the spike, and the adult speech model. Mitigations: silent-observer protocol, separate facilitator and evaluator roles, coded data, and intervals instead of point claims.

### E. Ethics, AI use and authorship

**E1. Did you use AI to build this?** (H)
- **Answer:** Yes, and we can show how:
  - AI coding agents implemented task cards: Antigravity (agy) writes app code, and Claude writes specs and reviews every commit.
  - AI generated the voice (Kokoro, Chatterbox) and the new pictures (Gemini image model).
  - The team made the design decisions, approved every asset by ear or eye, and ran the field validation.
  - Every change is a reviewed commit with tests and a CI run, recorded in `docs/evidence-log.md`.
- **Follow-up:** "Can you explain this code yourself?" Be ready to walk through `SpeechValidator`, `HeartManager`, `TutorPolicy` and one screen.
- **Action for the team:** each member picks two modules to explain live. Check with the adviser whether the course needs a written AI-use statement. Prepare one either way.

**E2. Are the AI images and voices licensed for use?** (M)
- **Answer:** Kokoro is Apache-2.0, Chatterbox is MIT and Vosk is Apache-2.0. The fonts are OFL. The pictures were generated for us, and their terms depend on the image tool's license. The older Edge-TTS clips have unclear redistribution rights, which is one reason we're replacing them. A credits and licenses file is still to do.

**E3. Is it appropriate to show synthetic voices to children?** (M)
- **Answer:** The voice says only teaching lines, in a consistent friendly character (Lily). Teachers audit every clip before release (Gate 3). The added-vowel problem teachers found came from TTS, which is exactly why we added the gates.

### F. Pedagogy and curriculum

**F1. Why the Marungko order?** (M)
- **Answer:** It's widely used in Philippine Grade 1 reading. It starts with high-utility letters (m, s, a, i) so children can build words early. All 4 teachers endorsed the sequence (PED-02). We apply it to English letter sounds, and our 7-chapter grouping is our own design on top of it.
- **Follow-up:** "Marungko is for Filipino; you teach English?" Answer: we follow its order and method (sound first, then blending). Teachers judged it appropriate. Instruction language (Filipino or Cebuano support) is an open question with the adviser.

**F2. 26 or 28 letters? What about Ñ and NG?** (H)
- **Answer:** 26. NG and Ñ are excluded pending subject-matter review, because they aren't part of the English letter-sound set we teach. Some older documents said 28; the app and current specs say 26.

**F3. Are all Blend It words decodable?** (H)
- **Answer:** Not yet. Five words (AIM, BEE, TOY, BOY, ZOO) use vowel teams children haven't learned, and QUIZ is an exception. Card 15 replaces them with words built only from letters already taught, checked by a test, and confirmed by a teacher.

**F4. Why does Say It never remove hearts?** (L)
- **Answer:** The recognizer can be wrong about a child's voice. Punishing a correct child for a machine error would teach the wrong thing and hurt motivation. Say It gives specific corrections and moves on after 3 tries. Hearts apply only where the child's choice is unambiguous: tapping in Find It and Blend It.

**F5. Can a child earn 3 stars without saying the word right?** (H)
- **Answer:** Yes, today. Letter stars count hearts lost in Find It, and Say It results don't count. That's deliberate while the judge is unvalidated, so a recognition error never costs stars. The adviser memo asks whether stars should come from the recall check instead. That's a pending decision.

**F6. How does the app handle a struggling child?** (M)
- **Answer:**
  - Say It: a 3-step prompt ladder with specific corrections, a mouth-shape cue from the second try (card 24), then it says the sound together with the child and moves on.
  - Find It: hearts with recovery (+1 per 3 correct in a row), and a gentle 3-heart restart instead of a game-over screen.
  - Everywhere: gentle orange instead of red, a soft pop instead of a buzzer, and a 10 s idle re-prompt that repeats the instruction.

---

## 4. Limitations and future work (say this yourself, before they ask)

> Four things we haven't proven yet:
> 1. **Speech accuracy on children.** Our recognizer model is trained on adults. Round 2 measures agreement with teachers.
> 2. **Learning effect.** We have usability and expert validation, not a pre/post learning test. That needs a multi-week study.
> 3. **Classroom conditions.** No noise handling yet; latency is not measured.
> 4. **Audio audit.** Every clip still needs the teacher audit, and older clips are being replaced.
>
> Next steps:
> - a children's or fine-tuned speech model;
> - telemetry for latency and judge decisions (no audio stored);
> - a database migration path;
> - captions and mouth-shape cues for learners with hearing difficulties;
> - a Filipino/Cebuano instruction option.

---

## 5. Live demo script

**Before the room:**
- Install the APK the night before. Turn on airplane mode, so you can show it's offline.
- Turn the volume up, and pair a speaker if the room is large.
- Create one demo profile beforehand, plus one fresh install, to show onboarding.
- Practice in a room with similar noise.

**Flow (4 minutes):**
1. Onboarding: pick an avatar, no typing (10 s).
2. Map, then letter m.
3. Hear It: Lily models /m/ and "mouse". Show the replay ear.
4. Say It: say the sound with Lily, then say "mouse" when it's your turn (correct path).
5. Find It: tap pictures. Make one deliberate wrong tap to show the gentle orange, the soft pop and the heart.
6. Letter Complete: stars.
7. Parent gate, dashboard, PDF report.

**Do not demo live:**
- the letter-name foil ("em"): it isn't reliably detected;
- noisy conditions.

If asked, explain the foil design (A4) and show the grammar in code.

**Fallbacks:**
- **Mic doesn't respond:** say "the recognizer listens for 3.8 seconds", try once more, then show the code path and move on. Never retry more than twice.
- **App crashes:** have a screen recording of the full flow ready on the laptop.
- **The debug build shows a "Heard: ..." line under Say It:** say it's our debug diagnostic. It shows what the recognizer heard, which is how we calibrate.
- **Silence where a correction should play:** some correction clips aren't released yet, and the app skips a missing clip instead of crashing.

---

## 6. Before the defense: checklist
- [ ] Re-check every (re-check) number in `NUMBERS.md` at the commit you demo.
- [ ] Fill in D5 (ethics approval, consent, assent) and D9 (any relationship to disclose) with facts.
- [ ] Each member can explain two modules in code (E1).
- [ ] Practice the pitch to 60 seconds.
- [ ] Practice the limitations script.
- [ ] Record the demo video for the fallback.
- [ ] Two full mock defenses (drills in `docs/defense/drill-log.md`).
