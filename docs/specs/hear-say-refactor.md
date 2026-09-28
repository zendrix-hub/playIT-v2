# playIT: Hear It and Say It Refactor

## Adviser Response with SRS v3.0, SDD v2.0, and SPMP Updates

**Draft · September 25, 2026** · IT411 Capstone & Research 2 · College of Computer Studies, CIT-U

> **Team notes (delete before submission).** Items marked **[confirm]** or **[proposed]** need a team or adviser decision. This document supersedes Sections 2.1, 2.2, 2.3, 2.9, 3.1, and 3.2 of the Week 3 change set. Everything else in that change set still applies.

---

## 1. The Adviser's Questions, Answered

The adviser asked for four things: sharper objectives for Hear It and Say It, a learning process that works with no teacher or parent in the session, proof that AI-generated audio models each sound correctly, and the classroom teaching process the app will adapt.

### 1.1 Short Answer

playIT adopts the explicit phonics lesson that effective Grade 1 teachers already use: review, model, guided practice, independent practice with immediate correction, and later recall. It replaces the teacher's in-the-moment decisions with rules triggered by what the child does. The child is never left without a model, a next step, or a way forward, so no adult is needed in the session.

This lesson structure is the core of systematic, explicit phonics instruction, which produces reliable gains in Kindergarten and Grade 1 (National Institute of Child Health and Human Development [NICHD], 2000). Teachers deliver it through a gradual release of responsibility, often called "I do, we do, you do" (Pearson & Gallagher, 1983; Archer & Hughes, 2011), and through the principles of explicit instruction: begin with review, model in small steps, guide practice, check understanding, and review again later (Rosenshine, 2012). Marungko lessons, as commonly taught in Philippine Grade 1 classrooms, follow the same arc. A short story introduces the sound, the teacher presents the letter and its sound, the children say and trace it, find things that begin with it, and then read it in syllables and words **[confirm this step list with your teacher-evaluators and cite a Marungko teaching guide]**.

### 1.2 The Lesson Cycle for One Letter

**Table 1.** Classroom steps and their playIT equivalents

| Step | What an effective teacher does | What playIT does | Why it works |
|---|---|---|---|
| 1. Review | Asks for sounds learned earlier | Warm-up of 2–3 learned letters, letter only, no model | Daily review strengthens recall (Rosenshine, 2012) |
| 2. Hook | Tells a short story that features the sound | 10–20 s animated story; the letter drawn inside the key-word picture | Picture mnemonics that embed the letter shape speed up letter-sound learning (Ehri et al., 1984) |
| 3. I do (Hear It) | Says the pure sound, shows the mouth, gives a key word | Pure sound three times with a lip cue, then the key word, then the sound again | Clear models plus mouth-position pictures help beginners map sounds (Boyer & Ehri, 2011) |
| 4. We do | "Say it with me" | Choral practice twice, not scored, while the mascot's mouth moves | Guided practice comes before independent practice (Pearson & Gallagher, 1983) |
| 5. You do (Say It) | "Your turn," waits, listens, corrects at once | Mic opens, waits up to 5 s, judges, and corrects with a model and a retry | Immediate, specific feedback tells the child what to do next (Hattie & Timperley, 2007) |
| 6. Check (Find It) | "Which picture starts with /m/?" | Child taps pictures; a wrong tap names that picture's first sound | Checks listening, not just copying |
| 7. Later (Recall) | Asks again the next day | Letter only, no model, at the next warm-up and at session end | Spaced retrieval shows and strengthens learning (Cepeda et al., 2006) |

### 1.3 Teacher Moves Turned into App Rules

In a classroom, the teacher decides in the moment when to repeat, prompt, correct, or move on. With no adult present, playIT must make those decisions itself. Table 2 turns each teacher move into a rule the app can execute.

**Table 2.** Triggers and responses

| Trigger the app detects | What a teacher would do | playIT response |
|---|---|---|
| First visit to an activity | Demonstrates the task | Mascot demonstrates it once, with spoken narration |
| Child seems unsure | Repeats the instruction | Ear button on every screen replays the spoken instruction |
| No speech 3 s after "Your turn" | Waits, then prompts (Rowe, 1986) | Re-models: "Listen: /m/. Your turn." |
| Letter name heard ("em") | "That's its name. Its sound is /m/." | Same correction, spoken, then a retry |
| Added vowel heard ("ma") | "Just /m/, no 'ah.'" | Same correction with the lip cue, then a retry |
| Other wrong sound | Models again, with more support | Next level of the prompt ladder (Section 3.2) |
| Third miss in a row | Says it together, moves on, returns later | Says it together, marks the letter "needs practice," schedules a recheck |
| Correct answer | Gives specific praise | "Yes! /m/, lips together," a star, and the next step |
| No interaction for 10 s **[proposed]** | Calls the child's attention back | Mascot re-prompts with the instruction |
| Session reaches about 12 minutes **[proposed]** | Ends on a success | Finishes the current step, praises, and closes the session |

### 1.4 The Error to Design Against

Filipino beginning readers learn consonant–vowel syllables such as ma, sa, and ba early, so adding a vowel to an English consonant is a predictable transfer error. AI voices make the same error when asked to say a consonant alone, which likely explains the "ma" that teachers heard in Round 1. The proposal already plans to calibrate recognition for Filipino English patterns such as /f/ versus /p/ and /v/ versus /b/. The refactor uses these patterns directly, as foils the recognizer can detect and as the target of specific corrective feedback.

### 1.5 What Independence Changes

Four design decisions follow from the independence requirement. First, the proposal's mitigation for misrecognition, a parent manually marking an attempt correct, no longer works because no parent is present; a false-reject limit and no-penalty retries replace it. Second, Say It no longer costs hearts, since a false reject is the app's error rather than the child's and no adult is there to reassure the child **[confirm]**. Third, every instruction is spoken and demonstrated, because a Grade 1 child cannot read directions. Fourth, a letter counts as mastered only when the child produces its sound from the letter alone after a delay, not when the child echoes a model.

---

## 2. Hear It: Composition and AI Audio

### 2.1 The Modeling Sequence

Hear It plays a fixed sequence assembled at runtime from separate audio files. Keeping each phoneme in its own file means it is never recorded inside a phrase, where a voice tends to add a vowel.

**Table 3.** Hear It sequence (I do)

| Order | Segment | Letter m (continuous) | Letter b (short) | Asset |
|---|---|---|---|---|
| 1 | Attention | "Listen!" | "Listen!" | Carrier phrase |
| 2 | Letter reveal | m appears inside its picture mnemonic | b appears inside its picture mnemonic | Animation |
| 3 | Introduction | "This letter says…" | "This letter says…" | Carrier phrase |
| 4 | Pure sound three times, with lip cue | /m/ held about 800 ms, 0.5 s pause, twice more | /b/ short, 0.5 s pause, twice more | Phoneme clip |
| 5 | Key word | /m/ … mouse | /b/ … ball | Key-word clip |
| 6 | Pure sound again | /m/ | /b/ | Phoneme clip |
| 7 | Hand-off | "Say it with me!" | "Say it with me!" | Carrier phrase |

The full sequence should last no more than 15 s **[proposed]**, and an ear button replays steps 3 to 6 at any time. Continuous sounds are held because held sounds can be joined in blending ("mmmaaa"), and connected phonation teaches decoding better than separated sounds (Gonzalez-Frey & Ehri, 2021).

### 2.2 Phoneme Specification

**Table 4.** Target sound for each letter

| Letter | Sound | Type | Clip length | Key word (suggested) | Errors to avoid |
|---|---|---|---|---|---|
| a | /æ/ | Continuous | About 800 ms | apple | "ey" |
| b | /b/ | Short (stop) | ≤250 ms | ball | "buh", "bee" |
| c | /k/ | Short (stop) | ≤250 ms | cat | "see", "kuh" |
| d | /d/ | Short (stop) | ≤250 ms | dog | "duh", "dee" |
| e | /ɛ/ | Continuous | About 800 ms | egg | "ee" |
| f | /f/ | Continuous | About 800 ms | fan | "ef", "fuh", /p/ |
| g | /g/ | Short (stop) | ≤250 ms | goat | "guh", "jee" |
| h | /h/ | Short (breath) | ≤300 ms | hat | "huh", "eych" |
| i | /ɪ/ | Continuous | About 800 ms | insect | "ay" |
| j | /dʒ/ | Short | ≤250 ms | jam | "juh", "jay" |
| k | /k/ | Short (stop) | ≤250 ms | kite | "kuh", "kay" |
| l | /l/ | Continuous | About 800 ms | leaf | "el", "luh" |
| m | /m/ | Continuous | About 800 ms | mouse | "em", "ma" |
| n | /n/ | Continuous | About 800 ms | nest | "en", "na" |
| o | /ɑ/ | Continuous | About 800 ms | octopus | "oh" |
| p | /p/ | Short (stop) | ≤250 ms | pig | "puh", "pee" |
| q | /kw/ | Short | ≤300 ms | queen | "kyoo" |
| r | /ɹ/ | Continuous | About 800 ms | rabbit | "ar", "ruh" |
| s | /s/ | Continuous | About 800 ms | sun | "es", "sa" |
| t | /t/ | Short (stop) | ≤250 ms | top | "tuh", "tee" |
| u | /ʌ/ | Continuous | About 800 ms | umbrella | "yoo" |
| v | /v/ | Continuous | About 800 ms | van | "vee", "vuh", /b/ |
| w | /w/ | Short (glide) | ≤300 ms | web | "wuh", "double-u" |
| x | /ks/ | Short | ≤350 ms | box (sound at the end) | "eks", /z/ |
| y | /j/ | Short (glide) | ≤300 ms | yo-yo | "wai", "yuh" |
| z | /z/ | Continuous | About 800 ms | zebra | "zee", "zuh", /s/ |

Short-sound lengths are **[proposed]** upper limits. Vowels are the short vowel sounds, and teachers decide which Philippine English vowel qualities Say It accepts. Key words must be picturable, familiar to Filipino Grade 1 children, and begin with the letter's taught sound. X is the exception: its taught sound comes at the end of box, because x at the start of a word usually says /z/. Keep your current key words where teachers approve them.

### 2.3 AI Audio Production

The AI voice is Kokoro-82M, an open text-to-speech model released under the Apache-2.0 license. The team runs it in Google Colab with an American English voice at 24 kHz, using `tools/audio/playit_audio.ipynb`. Nothing in the app calls it at runtime.

Text-to-speech voices are trained on words and sentences. Asked to say "m," they say the letter name; asked for a short sound such as "b," they add a vowel ("buh"). The pipeline therefore never asks an AI voice to say a letter alone. Kokoro also accepts phoneme (IPA) input, which spells out the sound itself rather than the letter, so it can be asked for a held /m/ without the letter name.

**Table 5.** Production method by sound type

| Sound type | Recommended method | Fallback |
|---|---|---|
| Continuous (13 letters: a, e, i, o, u, f, l, m, n, r, s, v, z) | Give Kokoro the sound as phoneme input for a held sound, trying several repeat counts and speeds; trim the steadiest part to about 800 ms and keep it only if it passes Gate 1 | Generate the key word, cut out the target sound, and lengthen it to about 800 ms with a time-stretch that keeps pitch; keep only clips that pass all gates |
| Short (13 letters: b, c, d, g, h, j, k, p, q, t, w, x, y) | Record a human model (a teacher, or a team member coached by one) saying each sound clipped short, and use it as recorded (Kokoro has no speech-to-speech voice conversion) | Cut the release burst from an AI-generated word before the vowel begins, and keep it only if it passes all gates |
| Carrier phrases, key words, feedback | Generate with the AI voice, since these are normal words and sentences. Lines that contain a pure sound ("Yes! /m/, lips together") are generated as fragments and joined with the phoneme clip at runtime | None needed |

Every clip follows one editing standard. Trim the silence, add a fixed 50 ms pad at each end, apply a 5–10 ms fade to prevent clicks, normalize loudness so all clips play at the same level, and export mono WAV. Record the tool, voice, and settings for every clip in the asset manifest (SDD Section 6.5), and confirm that the tool's license allows use in a distributed app. The Colab notebook applies this standard, runs the automated part of Gate 1, and writes the manifest entries.

### 2.4 Quality Gates

**Table 6.** Release gates for every phoneme clip

| Gate | Who | Pass rule |
|---|---|---|
| 1. Acoustic check | Audio lead, using Praat (Boersma & Weenink, n.d.) | Length within Table 4; for short sounds, no vowel after the release on the spectrogram; for continuous sounds, a steady sound with no vowel at the end |
| 2. Blind listening screen | A team member who did not produce the clip | Matches Table 4 |
| 3. Teacher audit | Four DepEd teachers, validation Instrument A | Rated Pure by at least 3 of 4 |

A clip ships only after it passes all three gates, and the manifest records each result. Gate 1 catches most added vowels before teachers spend time on them.

---

## 3. Say It: The Tutoring Loop

### 3.1 Echo and Recall

Say It runs in two modes. Echo mode follows a model: the child hears /m/ and says it back. It runs inside the lesson and teaches production. Recall mode shows the letter with no model, so the child must retrieve the sound. It runs in the warm-up and at the end of each session, and it decides mastery, because echoing a model shows imitation while recall shows learning.

### 3.2 Prompt Ladder and Corrections

**Table 7.** Responses by attempt

| Attempt | If correct | If wrong or silent |
|---|---|---|
| 1 | Specific praise, a star, next step | Error-specific correction, model, "Your turn" |
| 2 | Specific praise, a star, next step | Slow model with an enlarged lip cue, "Your turn" |
| 3 | Specific praise, a star, next step | Say it together (not scored), mark "needs practice," continue the lesson, and recheck in recall mode later |

Corrections name the error. For a letter name, the app says "That's the letter's name. Its sound is /m/." For an added vowel, it says "Almost! Just /m/, no 'ah.'" For a Filipino English substitution such as /p/ for /f/, it says "Listen: /f/. Top teeth on your lip." For an unknown attempt, it models again. Praise names the success ("Yes! /m/, lips together"), not only "Good job."

### 3.3 How the App Judges an Attempt

The judge compares each attempt against a small, letter-specific set of choices instead of the whole English vocabulary. Vosk's small models accept a runtime grammar, which is a list of the only phrases the recognizer may return (Alpha Cephei, n.d.). For each letter, the grammar holds the target form, foils for the predictable errors (the letter name, the consonant plus "a," and any Filipino English substitution for that sound), and an unknown token. The attempt is accepted when the target wins with confidence at or above a threshold, and the winning foil selects the correction message.

Two cautions shape the plan. First, target tokens must be vocabulary words whose dictionary pronunciation is the sound alone. Single letters such as "m" are stored with their letter-name pronunciation in speech dictionaries, so using them as targets would reward the very error the app is correcting. A Week 4 spike must confirm that suitable targets exist and that Vosk separates targets from foils for the four Chapter 1 sounds before the design is locked (SPMP risk R2). Second, the threshold is tuned on teacher-labeled pilot attempts to keep false rejects low, because without an adult a false reject punishes a correct answer. Pronunciations that teachers accept as Philippine English count as correct; the teacher's judgment, not a native-speaker norm, is the reference.

### 3.4 Hearts and Pass Rule

Say It no longer removes hearts. Hearts stay in Find It, where a wrong tap is the child's choice rather than a recognizer error **[confirm]**. The pass rule changes from one correct production within five attempts to one correct production within three scored attempts, followed by together-practice and a later recheck if needed. A letter is marked Secure only after a correct recall-mode production at a later point in the session or in a later session.

---

## 4. Revised SMART Objectives

**Table 8.** Objectives for Hear It, Say It, and independent use

| ID | Objective | Target | Measured by | By when **[confirm]** |
|---|---|---|---|---|
| HI-1 | Every phoneme model is pure | 26 of 26 clips rated Pure by at least 3 of 4 DepEd teachers and passing Gate 1 | Instrument A; asset manifest | Week 6 (Oct 12–17, 2026) |
| HI-2 | Every letter follows the modeling sequence | 26 of 26 letters match their lesson script; each sequence ≤15 s **[proposed]** | Instrument D, new check D-11 | Week 6 |
| HI-3 | Children complete Hear It alone | ≥85% of children with zero adult prompts | Instrument B, silent-observer protocol | Round 2, Week 7 (Oct 19–24, 2026) |
| SI-1 | The judge agrees with teachers | ≥80% agreement; false rejects ≤15% of teacher-correct attempts **[proposed]** | Instruments B and C | Round 2 |
| SI-2 | Feedback is immediate | ≤0.5 s after the end of speech, 90th percentile | Instruments C and D | Round 2 |
| SI-3 | Children complete Say It alone | ≥85% of children with zero adult prompts | Instrument B, silent-observer protocol | Round 2 |
| SI-4 | Children learn the sound, not just copy it | Of letters a child cannot sound out at the pre-check, ≥70% **[proposed]** produced correctly in recall mode at session end | App pre-check and end check, teacher-rated | Round 2 |

---

## 5. SRS v3.0 Changes

### 5.1 FR-02 Hear It: Modeling Sequence · Modified · P0

**Requirement.** For each letter, the system shall play the modeling sequence in Table 3, assembled at runtime from separate carrier, phoneme, and key-word files. While the phoneme plays, the system shall show the letter, its picture mnemonic, and an articulation (lip) cue. Phoneme clips shall meet Table 4, and an ear button shall replay the sequence at any time.

**Acceptance.** HI-1 and HI-2 met.

**Source.** Round 1 PED-08; adviser review.

### 5.2 NFR-AUD-01 Audio Production and Release · NEW · P0

**Requirement.** No phoneme clip shall be produced by asking an AI voice to say a letter alone. Continuous sounds shall be produced with Kokoro-82M phoneme input, with extraction from Kokoro-generated key words as the fallback, and short sounds shall come from a human model. Every phoneme clip shall pass the three gates in Table 6 before release, and the asset manifest shall record its source, tool (Kokoro-82M and its version, or human model), voice, settings, license, and gate results.

**Acceptance.** The manifest shows three passed gates for 26 of 26 phoneme clips. TC-AUD-03 confirms that the build contains only released clips.

**Source.** Adviser review of AI-generated audio.

### 5.3 FR-03 Say It: Tutoring Loop · Modified · P1

**Requirement.** After the modeling sequence, the system shall run a we-do step ("Say it with me," twice, not scored) and then a you-do step. In the you-do step, the microphone shall open automatically after "Your turn!" **[proposed]** and wait up to 5 s. The mic shall always show one of four states (Idle, Listening, Processing, Result), and while Listening a ripple shall follow the live input level. The system shall respond to each attempt with the prompt ladder in Table 7 and the corrections in Section 3.2. Say It shall not remove hearts **[confirm]**.

**Acceptance.** TC-SAY-01 to 04, one per ladder path; SI-3 met.

**Source.** Adviser review; Round 1 hesitation at the mic.

### 5.4 FR-NEW-REC Recall Checks and Mastery · NEW · P1

**Requirement.** The system shall run recall-mode checks (letter only, no model) in the warm-up of every node and at the end of every session. A letter shall be marked Secure only after a correct recall-mode production at least one activity after its lesson. Letters marked "needs practice" shall appear first in the next warm-up.

**Acceptance.** TC-REV-01 to 03; SI-4 measured in Round 2.

**Source.** Adviser review; spaced review (Cepeda et al., 2006).

### 5.5 NFR-ASR-01 Say It: Judgment Accuracy and Fairness · Modified · P1

**Requirement.** The Say It judge shall agree with teacher judgment on at least 80% of attempts by Grade 1 learners, with false rejects no higher than 15% of teacher-correct attempts **[proposed]**. The judge shall accept pronunciations the teacher panel accepts as Philippine English and shall record an error type (letter name, added vowel, substitution, or unknown) for each rejected attempt.

**Acceptance.** SI-1 met in Round 2, with 95% Wilson intervals and a table of rejections by error type; TC-ASR-06 checks the false-reject limit on the pilot set.

**Source.** General Objective 2; adviser review; the proposal's Filipino calibration plan. This requirement replaces the proposal's mitigation that lets a parent mark an attempt correct.

### 5.6 NFR-IND-01 Independent Use · NEW · P1

**Requirement.** A Grade 1 child shall be able to complete every activity with no adult present. All instructions shall be spoken, short (about eight words or fewer), and demonstrated by the mascot on first use. Every screen shall have an ear button that replays its instruction, no screen shall require reading, and the app shall re-prompt after 10 s without interaction **[proposed]**.

**Acceptance.** HI-3 and SI-3 met; Instrument D, new check D-12, confirms spoken instructions and an ear button on every screen; TC-IND-01 to 03.

**Source.** Adviser review.

**Note.** Confirm with your teacher-evaluators whether your learners understand English-only instructions. If not, add a Filipino or Cebuano instruction track as a P2 item.

### 5.7 FR-NEW-HOOK Story Hook · NEW · P2 [proposed]

**Requirement.** Each letter may open with a 10–20 s animated story featuring its sound and a picture mnemonic that embeds the letter shape, starting with the Chapter 1 letters.

**Acceptance.** Teacher review in Instrument A, Part 3.

**Source.** Marungko practice; Ehri et al. (1984).

### 5.8 NFR-SES-01 Session Length · NEW · P2 [proposed]

**Requirement.** The system shall close a session at the first completed step after about 12 minutes, always ending on a success. Set the exact limit with the teacher-evaluators.

**Acceptance.** TC-SES-01.

**Source.** Independent use by young children.

### 5.9 Proposal Statements to Remove or Change

**Table 9.** Changes to statements carried over from the proposal

| Current statement | Change |
|---|---|
| "Allow parent to manually mark correct if system misdetects" (risk mitigation) | Remove; replaced by NFR-ASR-01 and the prompt ladder |
| Audio "must be pre-recorded by a native English speaker" | Replace with NFR-AUD-01, the AI-assisted pipeline with release gates |
| Say It hearts (minus one per incorrect detection) | Remove from Say It; keep in Find It **[confirm]** |
| "Exactly 1 correct production to pass, with a maximum of 5 attempts" | One correct production within three scored attempts, then together-practice and a later recheck |
| Articulation cue as an accessibility item (NFR-ACC-01, P2) | Now part of FR-02 and FR-03 (P0 and P1); captions stay in NFR-ACC-01 |

---

## 6. SDD v2.0 Changes

### 6.1 Tutoring Layer

The lesson logic moves out of screen code into a small tutoring layer. It follows the usual split of tutoring systems into domain, learner, tutor, and interface parts (Woolf, 2009).

**Table 10.** New and changed components

| Component | Responsibility | Replaces or extends |
|---|---|---|
| LessonScript (data) | Per-letter JSON: steps, asset IDs, grammar, foils, key word | Hard-coded screen flow |
| LessonEngine | Runs a script's steps in order and emits events | Extends node navigation |
| TutorPolicy | Maps events (silence, error type, correct, third miss) to the responses in Tables 2 and 7 | New |
| SayItJudge | Vosk with a per-letter grammar; returns decision, error type, and confidence | Replaces open recognition |
| AudioComposer | Plays a sequence of clips and pauses | Extends AudioPlaybackManager |
| LearnerModel | Per-letter state and review queue in Room | Extends progress tables |

### 6.2 LessonScript

Each letter's lesson is data, so teachers' corrections change content without code changes. The grammar tokens below are placeholders until the Week 4 spike confirms which tokens the model can use.

```json
{
  "letter": "m",
  "chapter": 1,
  "soundType": "continuous",
  "hearIt": ["car_listen", "SHOW_LETTER", "car_this_letter_says",
             "ph_m", "PAUSE_500", "ph_m", "PAUSE_500", "ph_m",
             "kw_mouse", "ph_m", "car_say_it_with_me"],
  "cues": { "mnemonic": "anim_m", "articulation": "cue_lips_closed" },
  "weDoRepeats": 2,
  "sayIt": {
    "grammar": ["<target_m>", "em", "ma", "[unk]"],
    "target": "<target_m>",
    "foils": { "em": "LETTER_NAME", "ma": "ADDED_VOWEL" }
  }
}
```

### 6.3 TutorPolicy

TutorPolicy is a small state machine. In state TEST(k), a correct result moves to PRAISE and then to the next step. A wrong or silent result with k below 3 plays the correction for the detected error type plus support level k + 1 and moves to TEST(k + 1). A wrong or silent result at TEST(3) moves to LEAD, which plays the together-practice, marks the letter "needs practice," queues a recall check, and continues the lesson. Every transition writes a telemetry event.

### 6.4 SayItJudge

The repository reads the microphone with an `AudioRecord` loop at 16 kHz mono. Each buffer goes to `Recognizer.acceptWaveForm()` and to an RMS calculation that drives the mic ripple, since Vosk's `SpeechService` does not expose raw buffers. When Vosk reports the end of an utterance, the judge logs `speech_end`, reads the result with word confidence (enabled with `setWords(true)`), maps it to target or foil, and returns the decision, error type, and confidence. The confidence threshold lives in configuration so it can be tuned without a new release.

### 6.5 AudioComposer and Asset Manifest

SoundPool has no playback-completion callback, so the composer sequences clips using each clip's stored duration and explicit pause steps. A Media3 ExoPlayer playlist is an alternative for the modeling sequence, with SoundPool kept for instant feedback sounds. The asset manifest, stored as JSON in the app's assets, holds one entry per clip with these fields: clip ID, type (phoneme, key word, carrier, or feedback), letter, IPA, duration in milliseconds, source (AI word extraction (Kokoro), Kokoro phoneme input, or human recording), tool and voice, Gate 1 to 3 results, version, and release date. The build includes only released clips.

### 6.6 LearnerModel and Review Scheduler

A new Room entity, `LetterProgress`, stores for each profile and letter the state (NEW, INTRODUCED, NEEDS_PRACTICE, or SECURE), echo and recall results, the last-seen time, and the next review. The warm-up picks up to three letters: "needs practice" letters first, then Secure letters with the oldest last-seen time. Review intervals expand after each correct recall, for example the next session, then two sessions later, then four **[proposed]**.

### 6.7 Telemetry Additions

Add three fields to `TelemetryEvent`: mode (ECHO or RECALL), prompt level (1 to 3), and error type. Add five event types: `instruction_replay`, `demo_shown`, `lead_done`, `recall_result`, and `idle_reprompt`.

---

## 7. SPMP Updates

### 7.1 Scope Change

The scope baseline adds a tutoring layer for Hear It and Say It, an AI-assisted audio pipeline with release gates, independence features, and recall-based mastery. Chapter 1 (m, s, a, i) is the minimum complete slice for Round 2. Chapters 2 to 7 reuse the same scripts and pipeline and follow after Round 2 **[confirm with adviser]**.

### 7.2 Work Breakdown Structure

**Table 11.** Work packages

| WBS | Work package | Main deliverables | Depends on |
|---|---|---|---|
| 1 | Pedagogy design | Lesson scripts (Chapter 1 first), feedback phrase library, teacher review | None |
| 2 | Audio pipeline | Phoneme specification, produced clips, gate records, asset manifest | 1 |
| 3 | ASR spike and judge | Spike report for Chapter 1, per-letter grammar, tuned threshold | 2 |
| 4 | Tutoring layer | LessonEngine, TutorPolicy, AudioComposer, LearnerModel | 1, 3 |
| 5 | Independence features | Spoken instructions, first-use demos, ear buttons, idle re-prompts | 1, 4 |
| 6 | Telemetry and export | New fields and events, CSV export, PIN | 4 |
| 7 | Testing | STD test cases, internal QA, dry run with 3–5 children (with consent) | 4, 5, 6 |
| 8 | Round 2 validation | Instruments, consent, sessions, analysis | 7 |
| 9 | Documentation | SRS v3.0, SDD v2.0, RTM v3.0, SPMP update, validation report update | All |

### 7.3 Schedule

**Table 12.** Milestones by week

| Week | Dates (2026) | Milestone |
|---|---|---|
| 3 | Through Sep 26 | SRS v3.0, SDD v2.0, RTM v3.0, and SPMP update submitted |
| 4 | Sep 28 – Oct 3 | ASR spike decision; audio pipeline piloted on m, s, a, i; Chapter 1 scripts approved by teachers; consent forms sent |
| 5 | Oct 5 – 10 | Tutoring layer and AudioComposer built; all 26 phoneme clips through Gates 1 and 2 |
| 6 | Oct 12 – 17 | Independence features and telemetry complete; teacher audio audit (Gate 3); internal QA; dry run with 3–5 children |
| 7 | Oct 19 – 24 | Round 2 sessions and technical checks |
| 8 | Oct 26 – 31 | Analysis, report Section 4.2, and feature-freeze decision |

Dates assume Week 3 ends on Saturday, September 26. Adjust them to the IT411 calendar **[confirm]**.

### 7.4 Roles

**Table 13.** Roles and responsibilities

| Role | Responsibilities | Member |
|---|---|---|
| Project manager | Schedule, risk register, weekly adviser check-ins | **[assign]** |
| Pedagogy lead | Lesson scripts, feedback library, teacher liaison | **[assign]** |
| Audio lead | Audio pipeline, Gate 1, asset manifest | **[assign]** |
| Speech lead | ASR spike, SayItJudge, threshold tuning | **[assign]** |
| Android lead | Tutoring layer, UI, telemetry | **[assign]** |
| QA and validation lead | STD, Gate 2, Round 2 logistics and analysis | **[assign]** |

Five members cover six roles, so one member holds two. The project manager role pairs well with QA and validation.

### 7.5 Risk Register

**Table 14.** New and revised risks

| ID | Risk | Likelihood | Impact | Mitigation | Contingency |
|---|---|---|---|---|---|
| R1 | The AI voice cannot produce pure short sounds | High | High | Human model, optionally voice-converted, for the 13 short sounds; Gate 1 screening | If a clip fails Gate 3 twice, use a human recording only |
| R2 | Vosk cannot separate targets from foils for isolated sounds | Medium | High | Week 4 spike on Chapter 1; per-letter grammar; threshold tuning | Judge the key word for production while keeping the pure sound for modeling, or train a small on-device classifier on teacher-labeled recordings; decide with the adviser |
| R3 | Children cannot follow English-only instructions alone | Medium | High | Spoken instructions with demos; Week 6 dry run | Add a Filipino or Cebuano instruction track |
| R4 | False rejects frustrate children with no adult present | Medium | High | No hearts in Say It; prompt ladder; false-reject limit in NFR-ASR-01 | Lower the threshold or widen accepted variants |
| R5 | Scripts and clips for 26 letters overrun the schedule | High | Medium | Chapter 1 as the Round 2 slice; one script template for all letters | Move Chapters 2 to 7 after Round 2 |
| R6 | Consent and school scheduling delay Round 2 | Medium | High | Send consent forms in Week 4 and book sessions early | Shift Round 2 by one week and compress analysis |
| R7 | The TTS model or voice license does not allow distribution | Low | Medium | Kokoro-82M code and weights are Apache-2.0; record the model version and license for every clip in the manifest. espeak-ng (GPL) is used only for text-to-phoneme conversion at build time in Colab and does not ship in the app | Use human recordings |
| R8 | Team availability drops (exams, internships) | Medium | Medium | Two people per critical task; buffer in Week 8 | Reassign tasks at the weekly check-in |

### 7.6 Configuration Management

Audio assets are versioned through the manifest, and audio sources are kept in the repository together with the tool settings used to make them. The Round 2 build is tagged, and its manifest version is recorded in the validation report. Document versions stay aligned: SRS v3.0, SDD v2.0, RTM v3.0, and the SPMP update are released together.

### 7.7 Quality Assurance, Verification, and Validation

Verification covers the STD test cases for every requirement in Section 5, Gates 1 and 2 for audio, and a Week 6 internal run of every lesson script. Validation covers the teacher audio audit (Gate 3) and Round 2 under the silent-observer protocol (Section 8.2). The risk register is reviewed at every weekly adviser check-in.

---

## 8. Impact on the Validation Report and RTM

### 8.1 Report Table 1

**Table 15.** Mapping from the earlier objectives

| Earlier objective | New objectives | What changes |
|---|---|---|
| O1 | HI-1, HI-2 | Adds Gate 1 and the sequence check |
| O2a | SI-1 | Adds the false-reject limit and accent fairness |
| O2b | SI-2 | Target unchanged |
| O2c | SI-3 | Hesitation becomes independent completion; the hesitation code stays as a diagnostic |
| O5 | HI-3, SI-3 | Measured under the silent-observer protocol |
| None | SI-4 | New: recall after the lesson |

Add two checks to Instrument D. D-11 confirms that each letter's sequence matches its lesson script and lasts no more than 15 s, and D-12 confirms spoken instructions and an ear button on every screen.

### 8.2 Report Section 3.5: Silent-Observer Protocol

Replace the child-session paragraph in Section 3.5 with this text:

"Each child completes one session of about 20 minutes. An adult sits behind the child, out of the child's view, and does not speak or gesture except for safety or distress; any intervention is logged as an adult prompt. The app opens with a pre-check in recall mode, showing each Chapter 1 letter without feedback. The child then uses the app alone, and the session closes with the app's end-of-session recall check. Pronunciation is judged by a teacher-rater seated out of the child's view or, with parental consent, from audio recordings rated blind afterward."

### 8.3 New RTM Rows

**Table 16.** RTM v3.0 additions

| ID | Finding | Evidence | Requirement | Design | Test cases | Objective | Priority |
|---|---|---|---|---|---|---|---|
| F-12 | Hear It composition not specified | Adviser review | FR-02 | LessonScript; AudioComposer | TC-SCR-01 | HI-2 | P0 |
| F-13 | AI audio adds vowels to consonants | PED-08; adviser review | NFR-AUD-01 | Audio pipeline; asset manifest | TC-AUD-03 | HI-1 | P0 |
| F-14 | Learning depends on an adult being present | Adviser review | NFR-IND-01; FR-03 | TutorPolicy; demos; ear buttons | TC-IND-01 to 03; TC-SAY-01 to 04 | HI-3, SI-3 | P1 |
| F-15 | Mastery measured by imitation only | Adviser review | FR-NEW-REC | LearnerModel; review scheduler | TC-REV-01 to 03 | SI-4 | P1 |
| F-16 | Parent override conflicts with independence | Proposal risk table | NFR-ASR-01 | SayItJudge | TC-ASR-06 | SI-1 | P1 |

Update rows F-01 and F-02 from the earlier change set to point to HI-1 and SI-3.

---

## References

Alpha Cephei. (n.d.). *Vosk offline speech recognition API* [Computer software]. https://alphacephei.com/vosk/

Archer, A. L., & Hughes, C. A. (2011). *Explicit instruction: Effective and efficient teaching*. Guilford Press.

Boersma, P., & Weenink, D. (n.d.). *Praat: Doing phonetics by computer* [Computer software]. University of Amsterdam. https://www.fon.hum.uva.nl/praat/

Boyer, N., & Ehri, L. C. (2011). Contribution of phonemic segmentation instruction with letters and articulation pictures to word reading and spelling in beginners. *Scientific Studies of Reading, 15*(5), 440–470.

Cepeda, N. J., Pashler, H., Vul, E., Wixted, J. T., & Rohrer, D. (2006). Distributed practice in verbal recall tasks: A review and quantitative synthesis. *Psychological Bulletin, 132*(3), 354–380.

Ehri, L. C., Deffner, N. D., & Wilce, L. S. (1984). Pictorial mnemonics for phonics. *Journal of Educational Psychology, 76*(5), 880–893.

Gonzalez-Frey, S. M., & Ehri, L. C. (2021). Connected phonation is more effective than segmented phonation for teaching beginning readers to decode unfamiliar words. *Scientific Studies of Reading, 25*(3), 272–285.

Hattie, J., & Timperley, H. (2007). The power of feedback. *Review of Educational Research, 77*(1), 81–112.

National Institute of Child Health and Human Development. (2000). *Report of the National Reading Panel. Teaching children to read: An evidence-based assessment of the scientific research literature on reading and its implications for reading instruction* (NIH Publication No. 00-4769). U.S. Government Printing Office.

Pearson, P. D., & Gallagher, M. C. (1983). The instruction of reading comprehension. *Contemporary Educational Psychology, 8*(3), 317–344.

Rosenshine, B. (2012). Principles of instruction: Research-based strategies that all teachers should know. *American Educator, 36*(1), 12–19, 39.

Rowe, M. B. (1986). Wait time: Slowing down may be a way of speeding up! *Journal of Teacher Education, 37*(1), 43–50.

Woolf, B. P. (2009). *Building intelligent interactive tutors: Student-centered strategies for revolutionizing e-learning*. Morgan Kaufmann.

---

## Open Items

| # | Item | Why it matters |
|---|---|---|
| 1 | Name the AI voice tool you use | Its features (SSML, voice conversion, licensing) change pipeline details |
| 2 | Approve the **[proposed]** targets: sequence ≤15 s, false rejects ≤15%, recall ≥70%, short-sound lengths | They become SMART objectives |
| 3 | Confirm Say It without hearts | Changes the heart rule in the SRS and SDD |
| 4 | Confirm the Marungko step list with your teachers and cite a teaching guide | Supports Section 1.1 |
| 5 | Check whether learners follow English-only instructions | Decides whether an instruction track in Filipino or Cebuano is needed |
| 6 | Assign roles | SPMP Section 7.4 |
| 7 | Confirm the IT411 week calendar | SPMP Section 7.3 |
| 8 | Share the current SRS, SDD, and SPMP | Lets these changes be merged into full documents |
| 9 | Check every reference against its source before submission | Volume, issue, and page details |
