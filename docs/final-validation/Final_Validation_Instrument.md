# Final Validation Instrument Suite — playIT

**Course & Section:** IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Group Code / Team:** `2627-sem1-it411-56` — Group 56 (`Riva, Zendrix`; `Palis, John James`; `Miel, Kaysean`; `Durano, Ashlee Skye`; `Bien, Erik Samuel`)  
**Adviser:** Mr. Joemarie C. Amparo | **Course Instructor:** Mr. Ralph P. Laviste  
**Deliverable Reference:** Pre-Adviser Presentation Requirement D (`Final_Validation_Instrument`) & Week 12 Deliverable 2 (`02_Instruments_and_Data_Collection`, Due October 31, 2026)  

---

## Overview of the 5-Instrument Suite

To ensure that **every approved SMART goal is measured using an appropriate empirical tool**—and that system latency, speech recognition accuracy, child task completion, and offline persistence are measured through direct observation, blind acoustic rating, and system telemetry rather than opinion surveys—this package contains five linked instruments:

| Instrument Code | Instrument Title | Target Evaluator / Data Source | Measured SMART Goals & Sub-Objectives |
| :--- | :--- | :--- | :--- |
| **Instrument A** | **Teacher Phoneme, Decodable Word & Pedagogical Audit Form** | DepEd Grade 1 Reading Teachers ($N = 4\text{–}5$) | `SMART-01`, `SMART-02`, `HI-1`, `O6` (Gate 3 acoustic purity & curriculum alignment) |
| **Instrument B** | **Child Session Silent-Observer Sheet & Blind Teacher-Rater `Say It` Log** | Silent Observer + 2 Blind Teacher Raters during Child Sessions ($N = 16\text{–}20$ children) | `SMART-02`, `SMART-03`, `SMART-04`, `SMART-05`, `HI-2`, `SI-1`–`SI-4`, `O3` |
| **Instrument C** | **Automated App Telemetry & Room SQLite Extraction Protocol** | Local Device SQLite Database (`playit_database`) & PDF Report | `SMART-03`, `SMART-04`, `SMART-05`, `SMART-07` (Objective attempt counts, accuracy, stars, retention) |
| **Instrument D** | **Technical, Performance & Accessibility Verification Checklist (`D-01`–`D-12`)** | Technical / QA Evaluators ($N = 3$) on Samsung Galaxy A21s (`SM-A217F`) | `SMART-03`, `SMART-04`, `SMART-06`, `O3`, `O4`, `O5` (Latency, 60 FPS, 20× force-close, Airplane Mode) |
| **Instrument E** | **Post-Session Child Picture Choice & Adult Stakeholder Survey (SUS + TAM)** | Part E1: Grade 1 Learners ($N = 16\text{–}20$)<br>Part E2: Teachers & Parents ($N = 12\text{–}15$) | `SMART-05`, `SMART-07`, `O6` (Child affect/replay, Adult task completion, Brooke SUS, TAM) |

---

## Instrument A: Teacher Phoneme, Decodable Word & Pedagogical Audit Form

**Evaluator Code:** `T-____` | **Years Teaching Grade 1 Reading:** `____` | **Date:** `2026-10-____`  
**App Build Verified:** `playIT v1.2.0-rc2`

### Part A1 — Gate 3 Blind Phoneme Audio Audit (26 Isolated Phoneme Clips: `SMART-01`, `SMART-02`, `HI-1`)
*Instructions for Facilitator:* Play each clip from `audio/phonemes/{letter}.mp3` in randomized order (`Clip #01` to `Clip #26`) without showing the screen.  
*Instructions for Teacher:* Write the letter sound you hear, check **Yes** if any unwanted *"uh"* (`/ʌ/` schwa) vowel is attached to a consonant, and rate acoustic quality ($1 = \text{Reject / Mispronounced}$, $2 = \text{Acceptable for Grade 1}$, $3 = \text{Exemplary Model}$).

| Randomized Clip # | Actual File Key (Facilitator Key) | Target IPA & Articulatory Class | Teacher Blind Transcription (What letter sound did you hear?) | Schwa `/ʌ/` Intrusion Heard? (`Yes` / `No`) | Clarity & Purity Rating (`1`, `2`, or `3`) | Gate 3 Pass? (`Transcription Match` + `No Schwa` + `Rating >= 2`) |
| :---: | :--- | :--- | :--- | :---: | :---: | :---: |
| **A1-01** | `m.mp3` (Grp 1) | `/m/` (Bilabial nasal, continuous) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-02** | `s.mp3` (Grp 1) | `/s/` (Alveolar fricative, continuous) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-03** | `a.mp3` (Grp 1) | `/æ/` (Short front vowel — *"a, a, apple"*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-04** | `i.mp3` (Grp 1) | `/ɪ/` (Short high front vowel — *insect*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-05** | `o.mp3` (Grp 1) | `/ɒ/` (Short open back vowel — *orange*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-06** | `b.mp3` (Grp 1) | `/b/` (Voiced bilabial stop, clipped) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-07** | `e.mp3` (Grp 2) | `/ɛ/` (Short mid front vowel — *elephant*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-08** | `u.mp3` (Grp 2) | `/ʌ/` (Short central vowel — *umbrella*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-09** | `t.mp3` (Grp 2) | `/t/` (Voiceless alveolar stop, clipped) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-10** | `k.mp3` (Grp 2) | `/k/` (Voiceless velar stop, clipped) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-11** | `l.mp3` (Grp 2) | `/l/` (Alveolar lateral approximant) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-12** | `y.mp3` (Grp 2) | `/j/` (Palatal approximant — *yoyo*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-13** | `n.mp3` (Grp 3) | `/n/` (Alveolar nasal, continuous) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-14** | `g.mp3` (Grp 3) | `/ɡ/` (Voiced velar stop — *gift*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-15** | `p.mp3` (Grp 3) | `/p/` (Voiceless bilabial stop, clipped) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-16** | `r.mp3` (Grp 3) | `/ɹ/` (Postalveolar approximant — *rabbit*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-17** | `d.mp3` (Grp 3) | `/d/` (Voiced alveolar stop, clipped) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-18** | `h.mp3` (Grp 3) | `/h/` (Voiceless glottal fricative — *hat*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-19** | `w.mp3` (Grp 4) | `/w/` (Labio-velar approximant — *watch*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-20** | `c.mp3` (Grp 4) | `/k/` (Hard C velar stop — *cat*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-21** | `f.mp3` (Grp 4) | `/f/` (Voiceless labiodental fricative) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-22** | `j.mp3` (Grp 5) | `/dʒ/` (Voiced palato-alveolar affricate) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-23** | `q.mp3` (Grp 5) | `/kw/` (Labialized velar cluster — *queen*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-24** | `v.mp3` (Grp 5) | `/v/` (Voiced labiodental fricative) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-25** | `x.mp3` (Grp 5) | `/ks/` (Final cluster as in *box*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |
| **A1-26** | `z.mp3` (Grp 5) | `/z/` (Voiced alveolar fricative — *zip*) | `[        ]` | `[ ] Yes  [ ] No` | `[ ] 1  [ ] 2  [ ] 3` | `[ ] PASS  [ ] FAIL` |

### Part A2 — Decodable `Blend It` Word Bank Audit (33 Words Across Groups 1–6: `SMART-01`, `SMART-04`)
Verify that every `Blend It` word uses **only** letters taught up to that group (`100%` cumulative decodability) and that the spoken word clip and Argon 4 picture are age-appropriate for Grade 1:

| Group | Cumulative Letters Taught | Audited Decodable Words (`audio/words/word_*.mp3` & `images/pictures/blendword_*.png`) | Strictly Decodable? (`Yes`/`No`) | Audio & Picture Clear for Grade 1? (`Yes`/`No`) | Teacher Remarks |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **Group 1** | `M, S, A, I, O, B` | `AM, SAM, AIM, SIS, SOB, BIM` *(6 words)* | `[ ] Yes [ ] No` | `[ ] Yes [ ] No` | |
| **Group 2** | `+ E, U, T, K, L, Y` | `MAT, SAT, SIT, BAT, BET, BIT, TUB, KIT, LIT, YAM` *(10 words)* | `[ ] Yes [ ] No` | `[ ] Yes [ ] No` | |
| **Group 3** | `+ N, G, P, R, D, H` | `MAN, SUN, PIN, PAN, DOG, PIG, HAT, RED, HUG` *(9 words)* | `[ ] Yes [ ] No` | `[ ] Yes [ ] No` | |
| **Group 4** | `+ W, C, F` | `CAT, CUP, FAN, FIN, WET, WEB` *(6 words)* | `[ ] Yes [ ] No` | `[ ] Yes [ ] No` | |
| **Group 5–6** | `+ J, Q, V, X, Z` | `JAM, JET, VAN, VET, BOX, SIX, ZIP` *(7 words; 33 active pool)* | `[ ] Yes [ ] No` | `[ ] Yes [ ] No` | |

### Part A3 — Teacher Pedagogical & Curriculum Alignment Scale (`SMART-01`, `O6`)
Rate each statement from **1 (Strongly Disagree)** to **5 (Strongly Agree)**:

| Item ID | Pedagogical Alignment Statement | Rating (`1`–`5`) |
| :--- | :--- | :---: |
| **`PA-01`** | The 6-group Marungko-inspired letter progression (`M, S, A, I, O, B` first) aligns with how early phonics and decoding should be sequenced for Grade 1 learners. | `[1] [2] [3] [4] [5]` |
| **`PA-02`** | The `Hear It` 3-step sequence (pure phoneme sound $\to$ example word $\to$ guided prompt) models accurate phonemic awareness without confusing letter names with letter sounds. | `[1] [2] [3] [4] [5]` |
| **`PA-03`** | The `Say It` 3-attempt scaffolding (adding the slow phoneme model and synchronized mouth-shape cue on Attempt 2 while never deducting hearts) is developmentally appropriate for hesitant 6-to-7-year-old speakers. | `[1] [2] [3] [4] [5]` |
| **`PA-04`** | The `Find It` picture discrimination grid and `Blend It` CVC tile-building activities effectively reinforce grapheme-phoneme correspondence and early word synthesis. | `[1] [2] [3] [4] [5]` |
| **`PA-05`** | The Parent/Teacher Dashboard (`Green` / `Yellow` / `At-Risk` flags) and exported A4 PDF report provide actionable diagnostic data for classroom reading remediation. | `[1] [2] [3] [4] [5]` |

---

## Instrument B: Child Session Silent-Observer Sheet & Blind Teacher-Rater `Say It` Log

**Learner Code:** `C-____` | **Age:** `[ ] 6  [ ] 7` | **Gender:** `____` | **Session Date:** `2026-10-____`  
**Silent Observer Code:** `F-____` | **Blind Teacher Raters:** `T-____` & `T-____`

### Part B1 — Screen-by-Screen Silent-Observer Navigation & Task Log (`SMART-02`, `SMART-04`, `SMART-05`, `HI-2`, `O3`)
*Observer Rule:* Do **not** coach or point unless the child explicitly asks for help or is stuck for $\ge 15\text{ s}$ after Lily's 10s idle prompt. Tally every adult prompt.

| Task / Stage | Target Action Observed | Completed? (`Pass`/`Fail`) | Adult Prompts Needed (`0, 1, 2+`) | Stage Time (sec) | Behavioral & Ergonomic Notes (e.g., missed tap, hesitation, smile/delight) |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **1. Splash & Avatar Onboarding** | Taps `Start` $\to$ `+ New Player` $\to$ selects animal friend $\to$ taps `Let's Play` | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | |
| **2. Bohol Adventure Map** | Locates pulsing active node (`M` / `S` / `A`) and taps to launch lesson | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | |
| **3. Hear It (Letter 1: `____`)** | Listens to 3-step sequence (`Sound` $\to$ `Word` $\to$ `Prompt`), taps `Play` & `Next` | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | |
| **4. Say It (Letter 1: `____`)** | Taps 96dp mic button, speaks into mic, completes echo & recall steps | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | Mic hesitation? `[ ] None [ ] Mild [ ] Refused` |
| **5. Find It (Letter 1: `____`)** | Completes 3 rounds of picture/letter selection and taps `Continue` | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | Hearts remaining: `[  ] / 5` |
| **6. Letter Complete** | Views earned stars ($1\star\text{–}3\star$) and taps `Continue` to return to Map | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | Stars earned: `[ ] 1  [ ] 2  [ ] 3` |
| **7. Lesson 2 (`____`)** | Completes `Hear It` $\to$ `Say It` $\to$ `Find It` for second letter | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | Stars earned: `[ ] 1  [ ] 2  [ ] 3` |
| **8. Blend It Milestone** | Taps phoneme tiles in order and blends 3 CVC words (`AM`, `SAM`, `MAT`) | `[ ] Pass [ ] Fail` | `[   ]` | `____ s` | Words solved: `[  ] / 3` |

### Part B2 — Trial-by-Trial `Say It` Blind Teacher-Rater vs. Vosk ASR Log (`SMART-03`, `SI-1`–`SI-3`)
For each spoken `Say It` attempt by learner `C-____`, the two teachers independently mark whether the child's pronunciation was acceptable for Grade 1 ($1 = \text{Correct Target}$, $0 = \text{Incorrect / Letter Name Foil / Added Schwa / Silence}$), while the observer records the app's decision ($A \in \{0, 1\}$) and latency:

| Trial # | Target Letter / Word | Attempt # (`1, 2, 3`) | Child Utterance Type Observed (`Target` / `Letter-Name Foil` / `Schwa Foil` / `Other` / `Silence`) | Teacher Rater 1 ($T_1 \in \{0,1\}$) | Teacher Rater 2 ($T_2 \in \{0,1\}$) | Teacher Consensus ($T \in \{0,1\}$) | Vosk ASR Decision ($A \in \{0,1\}$) | Confusion Matrix Class (`TP`, `TN`, `FP`, `FN`) | Mouth Cue Used on Att. 2/3? (`Yes`/`No`/`N/A`) |
| :---: | :---: | :---: | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **01** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |
| **02** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |
| **03** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |
| **04** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |
| **05** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |
| **06** | `____` | `[ ]1 [ ]2 [ ]3` | `[                ]` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]1 [ ]0` | `[ ]TP [ ]TN [ ]FP [ ]FN` | `[ ]Y [ ]N [ ]N/A` |

> **Scoring Formulas for Part B2 (`SMART-03` / `SI-3`):**  
> - **Teacher-ASR Agreement:** $P_{\text{agree}} = \frac{TP + TN}{TP + TN + FP + FN} \times 100\%$ *(Target: $\ge 80.0\%$)*  
> - **False-Reject Rate (FRR):** $P_{\text{FR}} = \frac{FN}{TP + FN} \times 100\%$ *(Target: $\le 15.0\%$)*  

---

## Instrument C: Automated App Telemetry & Room Database Extraction Protocol

To substantiate observational records with objective system logs (`SMART-03`, `SMART-04`, `SMART-05`, `SMART-07`), the following local SQLite tables in `playit_database` and the exported PDF report are archived after each validation session:

| Room Entity / Table | Extracted Columns | Validation Metric Derived |
| :--- | :--- | :--- |
| **`profiles`** | `id`, `name`, `avatarResId`, `currentStreak`, `lastActiveDate`, `createdAt` | Onboarding completion, streak increment accuracy (`FR-09`, `FR-10`). |
| **`say_it_attempts`** | `id`, `profileId`, `phonemeId`, `recognizedText`, `isCorrect`, `attemptNumber`, `timestamp` | Empirical `Say It` attempt count ($1\text{–}3$), pass rate per phoneme, Attempt 2/3 recovery rate (`SI-1`, `SI-2`). |
| **`find_it_attempts`** | `id`, `profileId`, `phonemeId`, `selectedPhonemeId`, `isCorrect`, `roundNumber`, `heartsRemaining`, `timestamp` | Empirical `Find It` first-tap discrimination accuracy, hearts lost per lesson (`FR-05`–`FR-08`). |
| **`blend_it_attempts`** | `attemptId`, `profileId`, `wordId`, `groupId`, `isCorrect`, `attemptsTaken`, `heartsRemaining`, `timestamp` | Empirical `Blend It` CVC word decoding accuracy and attempts per word (`FR-NEW-01`, `FR-13`). |
| **`lesson_progress`** | `id`, `profileId`, `phonemeId`, `isUnlocked`, `isCompleted`, `starsEarned`, `bestAccuracy`, `lastPracticedAt` | Star distribution ($1\star, 2\star, 3\star$), 7-day retention window, `Green`/`Yellow`/`At-Risk` status (`FR-09`, `FR-12`). |

**ADB Extraction Command (Executed by QA Evaluator at End of Testing Day):**
```bash
adb exec-out run-as com.playit.app sqlite3 -header -csv databases/playit_database "SELECT * FROM say_it_attempts;" > validation_say_it_attempts.csv
adb exec-out run-as com.playit.app sqlite3 -header -csv databases/playit_database "SELECT * FROM find_it_attempts;" > validation_find_it_attempts.csv
adb exec-out run-as com.playit.app sqlite3 -header -csv databases/playit_database "SELECT * FROM blend_it_attempts;" > validation_blend_it_attempts.csv
adb exec-out run-as com.playit.app sqlite3 -header -csv databases/playit_database "SELECT * FROM lesson_progress;" > validation_lesson_progress.csv
```

---

## Instrument D: Technical, Performance & Accessibility Verification Checklist (`D-01` to `D-12`)

**Evaluator Code:** `QA-____` | **Test Device:** `Samsung Galaxy A21s (SM-A217F, Android 12, Exynos 850, 3 GB RAM)` | **Date:** `2026-10-____`

| Check ID | Linked Requirement | Verification Procedure & Workload | Expected Acceptance Criterion | Measured Value / Actual Result | Status (`PASS`/`FAIL`) |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **`D-01`** | `NFR-Perf-01` (`Say It` Latency) | Execute $N=30$ spoken `Say It` utterances on `SM-A217F`; measure $t_{\text{feedback}} - t_{\text{EOS}}$ (ms). | **P90 $\le 500\text{ ms}$** | `P50: ____ ms | P90: ____ ms | Max: ____ ms` | `[ ] PASS [ ] FAIL` |
| **`D-02`** | `NFR-Perf-03` (`Find It` Latency) | Execute $N=30$ tile taps in `Find It`; measure tap-to-audio/visual feedback delta (ms). | **P90 $\le 300\text{ ms}$** | `P50: ____ ms | P90: ____ ms | Max: ____ ms` | `[ ] PASS [ ] FAIL` |
| **`D-03`** | `NFR-Perf-02` (Audio Clip Length) | Inspect duration of all 26 `audio/phonemes/*.mp3` and 33 `audio/words/*.mp3` clips. | **100% of clips $\le 5.0\text{ s}$ ($5000\text{ ms}$)** | `Max phoneme: ____ s | Max word: ____ s` | `[ ] PASS [ ] FAIL` |
| **`D-04`** | `NFR-Perf` (Cold Start, 60 FPS & RAM) | Run `adb shell am start -W` ($10\times$), `dumpsys gfxinfo` during map scroll & lesson play, and `dumpsys meminfo`. | **Cold start $\le 2500\text{ ms}$**; **Jank $\le 10\%$**; **PSS RAM $\le 200\text{ MB}$** | `Start: ____ ms | Jank: ____% | RAM: ____ MB` | `[ ] PASS [ ] FAIL` |
| **`D-05`** | `NFR-Off-02` (20× Force-Close Persistence) | In Airplane Mode, complete/update progress, execute `adb shell am force-stop com.playit.app`, relaunch, and verify SQLite state **20 consecutive times**. | **20 / 20 cycles ($100\%$)** retain exact stars, hearts, and streaks with 0 corruption. | `____ / 20 cycles intact` | `[ ] PASS [ ] FAIL` |
| **`D-06`** | `NFR-Off-01` (Airplane Mode Operability) | With Airplane Mode ON and Wi-Fi/Data OFF, execute a full cycle of `Hear It`, `Say It` (Vosk), `Find It`, `Blend It`, and `PDF Export`. | **4 / 4 learning modules + PDF export ($100\%$)** operate with zero network errors. | `____ / 4 modules + PDF verified` | `[ ] PASS [ ] FAIL` |
| **`D-07`** | `NFR-Acc-01` ($\ge 64\text{ dp}$ Touch Targets) | Verify bounds of all child interactive controls across Compact (`320x640`), A21s (`360x800`), Phone (`393x852`), and Tablet (`800x1280`) via `LayoutMatrixTest`. | **100%** of child buttons (`Play`, `Mic`, `Next`, `Back`, `MapNode`) $\ge 64\text{ dp} \times 64\text{ dp}$. | `Min measured target: ____ dp x ____ dp` | `[ ] PASS [ ] FAIL` |
| **`D-08`** | `NFR-Acc-02` (Contrast, Text Floor & Zero Red) | Audit WCAG contrast ratios, font sizes (`>=16sp` map, `>=24sp` lesson), element group count (`<=5`), and zero `#D63031` harsh red / zero emojis (`ZeroEmojiPolicyTest`). | **Contrast $\ge 4.5:1$**; **$\le 5$ groups/screen**; **0 harsh red**; **0 emojis**. | `Min contrast: ____:1 | Emojis: 0` | `[ ] PASS [ ] FAIL` |
| **`D-09`** | `SI-2` (Articulatory Mouth Cues) | Trigger Attempt 2 across all 7 `ArticulationGroup` categories and verify `PictureAssetsTest.everyMouthCueIsReleased`. | **7 / 7 ($100\%$)** mouth-shape illustrations render left of the mic without clipping. | `____ / 7 mouth cues verified` | `[ ] PASS [ ] FAIL` |
| **`D-10`** | `SI-1` ($\le 100\text{ ms}$ Mic State Cue) | Tap 96dp mic button in `Say It` and measure transition from `Idle` to `Listening` visual pulse + chime. | **Mic state transition $\le 100\text{ ms}$**. | `Measured transition: ____ ms` | `[ ] PASS [ ] FAIL` |
| **`D-11`** | `NFR-Sec-01`..`03` (Privacy & Manifest) | Inspect merged `AndroidManifest.xml` (`ManifestPrivacyTest`) and network traffic during full session. | **0 `INTERNET` permissions**; only `RECORD_AUDIO` declared; **0 outbound packets**. | `Permissions found: RECORD_AUDIO only` | `[ ] PASS [ ] FAIL` |
| **`D-12`** | `FR-12`, `FR-13` (PDF & Dashboard Parity + Stress) | Export A4 PDF from `ParentDashboardScreen` and run 5,000-event `adb shell monkey` stress test. | **100% metric parity** between Dashboard & PDF; **0 crashes / 0 ANRs**. | `PDF size: ____ KB | Crashes/ANRs: 0` | `[ ] PASS [ ] FAIL` |

---

## Instrument E: Post-Session Child Picture Choice & Adult Stakeholder Survey (SUS + TAM)

### Part E1 — Child Post-Session Exit Sheet (Non-Reading Protocol: `SMART-05`, `O3`)
*Administered verbally by the facilitator in $\le 90\text{ seconds}$ at the end of the child's play session (`C-____`):*

1. **Item `E1-01` (Visual Smileyometer):** *"Point to the face that shows how you felt playing with Lily today:"*  
   - `[ ] 3 — Big Smile (Loved it / Happy)`  
   - `[ ] 2 — Neutral Face (Okay)`  
   - `[ ] 1 — Frown (Did not like it / Frustrated)`
2. **Item `E1-02` (Picture-Card Hardest Stage Choice):** Show the 4 stage picture cards (`Hear It` Speaker, `Say It` Microphone, `Find It` Picture Grid, `Blend It` Word Tiles) and ask: *"Which game was the trickiest or hardest for you today?"*  
   - `[ ] Hear It (Listening)` | `[ ] Say It (Speaking into Mic)` | `[ ] Find It (Finding Pictures)` | `[ ] Blend It (Building Words)` | `[ ] None — All Easy`
3. **Item `E1-03` (Behavioral Voluntary Replay Prompt):** *"You finished our game! Would you like to play one more letter right now, or stop here?"*  
   - `[ ] Chose to play another letter (Voluntary Replay = 1)`  
   - `[ ] Chose to stop now (Voluntary Replay = 0)`

---

### Part E2 — Adult Stakeholder Questionnaire (Teachers `T-____` & Parents `P-____`: `SMART-07`, `O6`)

**Respondent Code:** `[ ] T-____  [ ] P-____` | **Role:** `[ ] Grade 1 Teacher  [ ] Parent / Guardian` | **Date:** `2026-10-____`

#### Section 1: Adult Hands-On Task Verification (Observed Before Survey)
- **`Task A1` (Parent Gate):** Unlocked `Parent Zone` via `ArithmeticGuardDialog` on first attempt? `[ ] Yes  [ ] No`
- **`Task A2` (Profile Management):** Created/renamed learner profile and switched active learner? `[ ] Yes  [ ] No`
- **`Task A3` (Diagnostic Interpretation):** Correctly identified which letters are `Mastered (Green)`, `Developing (Yellow)`, and `At-Risk (Amber)`? `[ ] Yes  [ ] No`
- **`Task A4` (Offline PDF Report):** Exported and opened the A4 PDF Progress Report offline? `[ ] Yes  [ ] No`

#### Section 2: Standardized System Usability Scale (Brooke SUS — 10 Items, 5-Point Likert `1 = Strongly Disagree` to `5 = Strongly Agree`)

| Item ID | SUS Statement | Score (`1`–`5`) |
| :---: | :--- | :---: |
| **`SUS-01`** | I think that I would like to use the **playIT** application frequently for my child's / students' phonics practice. | `[1] [2] [3] [4] [5]` |
| **`SUS-02`** | I found the **playIT** application unnecessarily complex. *(Reverse-scored)* | `[1] [2] [3] [4] [5]` |
| **`SUS-03`** | I thought the **playIT** application was easy to use for both children and adults. | `[1] [2] [3] [4] [5]` |
| **`SUS-04`** | I think that I would need the support of a technical person to be able to use this application. *(Reverse-scored)* | `[1] [2] [3] [4] [5]` |
| **`SUS-05`** | I found the various functions in **playIT** (`Hear It`, `Say It`, `Find It`, `Blend It`, and `Parent Zone`) were well integrated. | `[1] [2] [3] [4] [5]` |
| **`SUS-06`** | I thought there was too much inconsistency in this application. *(Reverse-scored)* | `[1] [2] [3] [4] [5]` |
| **`SUS-07`** | I would imagine that most Grade 1 learners, parents, and teachers would learn to use **playIT** very quickly. | `[1] [2] [3] [4] [5]` |
| **`SUS-08`** | I found the application very cumbersome or awkward to use. *(Reverse-scored)* | `[1] [2] [3] [4] [5]` |
| **`SUS-09`** | I felt very confident navigating the lessons and interpreting the Parent Zone progress report. | `[1] [2] [3] [4] [5]` |
| **`SUS-10`** | I needed to learn a lot of things before I could get going with this application. *(Reverse-scored)* | `[1] [2] [3] [4] [5]` |

#### Section 3: Technology Acceptance Model (TAM) & Diagnostic Value Scale (`1 = Strongly Disagree` to `5 = Strongly Agree`)

| Item ID | Construct | Statement | Score (`1`–`5`) |
| :---: | :--- | :--- | :---: |
| **`TAM-PU1`** | Perceived Usefulness | **playIT** helps Grade 1 learners recognize and pronounce individual letter sounds (`Hear It` & `Say It`) more effectively. | `[1] [2] [3] [4] [5]` |
| **`TAM-PU2`** | Perceived Usefulness | The `Find It` and `Blend It` stages help learners connect letter sounds to pictures and blend sounds into simple CVC words. | `[1] [2] [3] [4] [5]` |
| **`TAM-PU3`** | Perceived Usefulness | The `Parent Zone` (`Green` / `Yellow` / `At-Risk` flags and 7-day retention score) clearly shows me which specific letters a child needs to practice. | `[1] [2] [3] [4] [5]` |
| **`TAM-PU4`** | Perceived Usefulness | The offline **A4 PDF Progress Report** is useful for sharing a child's phonics progress between home and school without needing internet access. | `[1] [2] [3] [4] [5]` |
| **`TAM-PEOU1`** | Perceived Ease of Use | The avatar-only profile setup and large 3D buttons allow a 6-to-7-year-old child to start playing without needing to type or read instructions. | `[1] [2] [3] [4] [5]` |
| **`TAM-PEOU2`** | Perceived Ease of Use | The calm stage backgrounds, clear illustrations, and mouth-shape cues keep the child focused without visual clutter or distraction. | `[1] [2] [3] [4] [5]` |
| **`TAM-PEOU3`** | Perceived Ease of Use | Running **100% offline** on a standard budget Android phone makes **playIT** practical for real households and classrooms. | `[1] [2] [3] [4] [5]` |
| **`TAM-BI1`** | Behavioral Intention | I would recommend and use **playIT** as a supplementary phonics learning tool for Grade 1 learners. | `[1] [2] [3] [4] [5]` |

#### Section 4: Diagnostic Open-Ended Feedback
1. **`OE-01` (Observed Strengths):** Which part of **playIT** (`Hear It`, `Say It`, `Find It`, `Blend It`, Map, or Parent Dashboard/PDF) was most helpful for the learner, and why?
2. **`OE-02` (Usability or Pedagogical Friction):** Did you or the learner encounter any confusing screen, audio clip, or recognition issue during the session?
3. **`OE-03` (Recommendations for Future Iteration):** What single improvement would make **playIT** more effective for Grade 1 reading remediation?
