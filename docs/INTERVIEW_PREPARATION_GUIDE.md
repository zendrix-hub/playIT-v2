# PlayIT — Capstone Defense & Technical Interview Master Reviewer

> **Project Title:** PlayIT — Gamified Phonics Mobile Learning Application for Grade 1 Filipino Learners  
> **Target Demographic:** Grade 1 Public/Private School Children (Ages 6–7) in the Philippines  
> **Key Framework:** DepEd Marungko Reading Sequence (28 letters, 7 groups)  
> **Core Constraint:** 100% Offline-First Architecture (Vosk ASR, Room SQLite, Android PdfDocument)

---

## Table of Contents
1. [The 60–75s Elevator Pitch (Script)](#1-the-6075s-elevator-pitch-script)
2. [Role Customization: How to Own Your Contribution](#2-role-customization-how-to-own-your-contribution)
3. [System Architecture & Tech Stack Deep Dive](#3-system-architecture--tech-stack-deep-dive)
4. [Pedagogical & UX Design Foundations](#4-pedagogical--ux-design-foundations)
5. [Top 12 Technical Interview Questions & Model Answers](#5-top-12-technical-interview-questions--model-answers)
6. [STAR Method Problem-Solving Stories (Real Challenges)](#6-star-method-problem-solving-stories-real-challenges)
7. [Panel Traps, Edge Cases & How to Answer Gracefully](#7-panel-traps-edge-cases--how-to-answer-gracefully)
8. [Future Roadmap ("What would you build next?")](#8-future-roadmap-what-would-you-build-next)

---

## 1. The 60–75s Elevator Pitch (Script)

> **Pro-Tip for Delivery:** Speak at a steady pace, smile, and emphasize the *why* (offline-first for Filipino learners) before diving into the *how* (Clean Architecture, Vosk, Jetpack Compose).

```text
"My capstone project is PlayIT — an offline-first Android phonics mobile application designed for Grade 1 Filipino learners ages 6 to 7. 

Traditional literacy apps rely on Western phonics sequences and require cloud internet, which leaves behind public school students with limited connectivity. PlayIT solves this by following the Department of Education's Marungko Approach — teaching 28 letters organized into 7 progressive groups.

The child journeys through a gamified, winding candy-crush style map. Each letter features three sequential mini-games:
1. 'Hear It' for native audio phoneme modeling,
2. 'Say It' for pronunciation practice powered by offline Vosk speech recognition, and
3. 'Find It' for phoneme-to-picture discrimination.

After completing a 4-letter group, children unlock 'Blend It', a hands-on word construction checkpoint. We designed a child-friendly gamification engine with hearts, stars, and streaks, and provided an offline Parent Dashboard with PDF progress export supporting up to 6 independent child profiles per device.

My personal focus on the team was [Your Role, e.g., developing the core MVVM/Clean Architecture and the offline speech validation pipeline / building the responsive Compose UI and gamification managers]. I'm excited to walk you through our technical decisions and architecture today."
```

---

## 2. Role Customization: How to Own Your Contribution

Pick the track that best matches your contribution or combine them:

### Option A: Frontend & Interactive UI (Jetpack Compose / M3)
* **What you built:** `MapScreen`, `HearItScreen`, `SayItScreen`, `FindItScreen`, `BlendItScreen`, Custom Gummy UI Components (Pressable buttons, Heart HUD, Confetti/Lottie celebrations).
* **Key Components/Classes:** `GummyButton`, `MapPathRenderer`, `HeartPoolHUD`, `WordSlotGrid`, `FeedbackCard`.
* **Talking Points:**
  - Designed with pediatric HCI guidelines: large touch targets ($\ge 56\text{dp} - 64\text{dp}$), spring animations (150–300ms), single-story typography (Lexend/Poppins) for early readers.
  - Implemented strictly Unidirectional Data Flow (UDF): Composables observe immutable `StateFlow` from ViewModels and emit UI events upward without mutating state directly.

### Option B: Speech & Data Layer / Backend (Vosk, Audio, Room DB)
* **What you built:** Offline Speech Recognition pipeline, Room Database schema (12 tables), Audio playback engine, PDF Generation.
* **Key Components/Classes:** `AudioCapture`, `VoskRecognizer`, `SpeechValidator`, `PlayItDatabase`, `SessionManager`, `PdfExporter`.
* **Talking Points:**
  - Integrated Vosk 0.3.47 for 16kHz mono PCM acoustic transcription on-device, handling low-latency child voice verification ($\le 500\text{ms}$).
  - Designed the 12-table normalized Room schema with foreign-key cascade deletions and multi-profile session scoping via `SessionManager`.

### Option C: Domain Layer & Game Logic (Clean Architecture Core)
* **What you built:** Pure Kotlin business rules, state managers, progress calculation, and repository interfaces.
* **Key Components/Classes:** `HeartManager`, `StarCalculator`, `StreakTracker`, `UnlockManager`, `GroupUnlockManager`, `RetentionCalculator`.
* **Talking Points:**
  - Maintained 100% architectural purity in `domain/` with zero Android framework imports (`android.*`), enabling fast JVM unit testing.
  - Reusable deterministic managers (e.g., `HeartManager` for 5-heart pooling and recovery, `StarCalculator` for weighted accuracy scoring).

---

## 3. System Architecture & Tech Stack Deep Dive

### 3.1 Architectural Diagram (MVVM + Clean Architecture)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           PRESENTATION LAYER                            │
│  - Jetpack Compose (Material 3, Gummy Design System)                    │
│  - ViewModels (SayItViewModel, MapViewModel, DashboardViewModel)        │
│  - Unidirectional Data Flow (StateFlow / SharedFlow UI Events)          │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ (Observes UI State / Dispatches Events)
┌────────────────────────────────────▼────────────────────────────────────┐
│                              DOMAIN LAYER                               │
│  - Business Managers: HeartManager, StarCalculator, SpeechValidator    │
│  - Repository Interfaces: ProfileRepository, LessonProgressRepository   │
│  - Strict Rule: Pure Kotlin / Zero `android.*` dependencies             │
└────────────────────────────────────▲────────────────────────────────────┘
                                     │ (Implements Interfaces via Hilt DI)
┌────────────────────────────────────┴────────────────────────────────────┐
│                               DATA LAYER                                │
│  - Room 2.6+ SQLite Database (12 Entities, DAOs, Migrations)            │
│  - Vosk ASR Engine (Offline 16kHz mono PCM audio recognition)           │
│  - Android MediaPlayer & Native PdfDocument                             │
│  - In-Memory SessionManager (activeProfileId context)                   │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 The 12-Table Room Database Schema

All child-specific data cascades to the active `Profile`:

| Entity | Purpose | Key Relations / Fields |
|---|---|---|
| `Profile` | Child account (max 6 per device) | `profileId` (PK), `name`, `avatarResId`, `totalStars`, `currentStreak` |
| `Phoneme` | 28 Marungko letters | `phonemeId` (PK, 1–28), `letter`, `audioPath`, `imagePath`, `exampleWord` |
| `LetterGroup` | 7 sequence groups | `groupId` (PK, 1–7), `groupNumber` |
| `LetterGroupMember` | Maps 4 letters per group | `memberId` (PK), `groupId` (FK), `phonemeId` (FK), `position` (0–3) |
| `LessonProgress` | Rollup per letter | `profileId` (FK), `phonemeId` (FK), `starsEarned` (0–3), `heartsLost` |
| `SayItAttempt` | Voice attempt log | `profileId` (FK), `phonemeId` (FK), `isCorrect`, `attemptedAt` |
| `FindItAttempt` | Discrimination log | `profileId` (FK), `phonemeId` (FK), `selectedPhonemeId`, `isCorrect` |
| `BlendItWord` | Pool of blendable words | `wordId` (PK), `groupId` (FK), `word`, `wordPattern` (e.g. `"S-A-M"`) |
| `BlendItAttempt` | Word building log | `profileId` (FK), `wordId` (FK), `isCorrect`, `attemptedAt` |
| `BlendItProgress` | Group completion rollup | `profileId` (FK), `groupId` (FK), `starsEarned`, `isCompleted` |
| `Achievement` | Badges & Milestones | `profileId` (FK), `title`, `isUnlocked`, `unlockedAt` |
| `ReportLog` | PDF generation history | `profileId` (FK), `filePath`, `generatedAt` |

### 3.3 Technology Stack Summary

- **Language:** Kotlin 1.9+ (Coroutines & StateFlow for reactive asynchronous streaming)
- **UI:** Jetpack Compose 1.5+ with Material 3 and custom Duolingo ABC-inspired visual depth
- **Dependency Injection:** Dagger Hilt
- **Local Persistence:** Room Database 2.6+ (SQLite abstraction with compile-time query verification)
- **Speech Recognition:** Vosk Android SDK 0.3.47 (Offline small English acoustic model)
- **Audio Output:** Android `MediaPlayer` (API 26+)
- **Reporting:** Native Android `PdfDocument` (zero external heavyweight PDF libraries)
- **Image Loading:** Coil 2.5+

---

## 4. Pedagogical & UX Design Foundations

When the panel asks *"Why did you design it this way?"*, reference these pedagogical and UX pillars:

1. **DepEd Marungko Approach:**
   - Unlike Western alphabetical order (A–Z), Marungko begins with high-frequency, easily blendable phonemes:  
     **Group 1:** `m, s, a, i` $\rightarrow$ immediately allows a child to read words like *"SAM"*, *"AM"*, *"MAS"*.
   - Total of 28 letters across 7 groups of 4.
2. **Audio-First Scaffolding (Hear It $\rightarrow$ Say It $\rightarrow$ Find It $\rightarrow$ Blend It):**
   - **Hear It:** Phonemic awareness (input).
   - **Say It:** Expressive phonological production (oral output).
   - **Find It:** Visual-auditory discrimination (identification).
   - **Blend It:** Synthetic phonics synthesis (reading & spelling).
3. **Encouragement-First Pediatric Design:**
   - **No punitive visuals:** We never show harsh red screens, buzzer sounds, or flashing red "X" icons. Instead, we use *Gentle Correction Orange* (`#FFB74D`) and positive corrective audio.
   - **Touch Target Floor:** Children have developing motor skills, so interactive elements maintain a $\ge 56\text{dp}$ to $64\text{dp}$ touch target floor.
   - **Single-Story Letterforms:** Early readers learn handwriting with single-story `'a'` and `'g'` (Lexend/Poppins), avoiding confusing double-story typographic glyphs.

---

## 5. Top 12 Technical Interview Questions & Model Answers

### Q1: "Why did you build an offline-first architecture instead of using Firebase / Cloud Backend?"
> **Answer:**  
> "We made an intentional socio-technical decision based on our target users: Grade 1 public school classrooms and rural households in the Philippines where internet connectivity is either non-existent or prohibitively expensive.  
> Additionally, because our app captures children's voice audio, processing speech on-device with Vosk guarantees 100% data privacy — no children's audio ever leaves the device. Using Room SQLite with multi-profile scoping allows up to 6 siblings or classmates to share a single low-cost tablet without accounts, passwords, or latency."

---

### Q2: "How does your speech recognition pipeline work under the hood?"
> **Answer:**  
> "When the child taps the microphone on the `SayItScreen`, our `AudioCapture` service streams 16kHz 16-bit mono PCM audio chunks directly to `VoskRecognizer`.  
> Vosk matches the acoustic signal against its lightweight on-device acoustic model and grammar list, emitting a JSON result with the recognized text and confidence score.  
> The result passes into `SpeechValidator` (in our Domain layer), which verifies whether the spoken phoneme or target word matches the accepted word dictionary at or above our 75% confidence threshold. The entire loop executes in under 500 milliseconds without internet."

---

### Q3: "What is Clean Architecture, and how did you enforce it in your code?"
> **Answer:**  
> "We separated the codebase into three concentric layers: Presentation, Domain, and Data.  
> The key rule is that dependencies only point inward:
> - **Domain Layer** contains pure business logic (`HeartManager`, `StarCalculator`, repository interfaces) and has **zero** `android.*` imports. It is 100% pure Kotlin.
> - **Data Layer** implements those repository interfaces using Room, Vosk, and Android hardware APIs.
> - **Presentation Layer** consists of Jetpack Compose UI and ViewModels that communicate exclusively with Domain use-cases/managers.  
> This allows us to unit-test all gameplay rules, heart mechanics, and calculations on a standard JVM in milliseconds without needing an Android emulator."

---

### Q4: "How do you handle multi-child profile support on a single device?"
> **Answer:**  
> "We implemented an in-memory `SessionManager` singleton injected via Hilt. When a child selects their avatar on the `ProfileSelectScreen`, `SessionManager.activeProfileId` is updated.  
> Every repository query (for progress, attempts, stars, and streaks) filters by this `activeProfileId`. In Room, all attempt and progress tables declare foreign keys pointing to `Profile.profileId` with `onDelete = CASCADE`. This guarantees full data isolation between up to 6 children and clean database integrity when a profile is removed."

---

### Q5: "What happens when a child makes a mistake during a game?"
> **Answer:**  
> "Our `HeartManager` deducts 1 heart from the session's 5-heart pool.  
> The ViewModel emits a failure `FeedbackState`, triggering a gentle shake animation and *Gentle Correction Orange* highlighting alongside a supportive audio cue.  
> The attempt is logged asynchronously into `SayItAttempt` or `FindItAttempt`. If the child depletes all 5 hearts, the sub-level restarts and reinitializes with a fresh 3-heart buffer so the child is never stuck in a punishing loop."

---

### Q6: "How do you calculate stars and progress?"
> **Answer:**  
> "`StarCalculator` computes a 1 to 3 star rating upon completing all three sub-levels of a letter:
> - **3 Stars:** 100% accuracy and 0 hearts lost.
> - **2 Stars:** $\ge 80\%$ accuracy with minimal mistakes.
> - **1 Star:** Completed with retries or depleted hearts.  
> Furthermore, consecutive correct answers award bonus heart recovery (+1 heart every 3 consecutive correct actions, capped at starting pool size) to reward sustained focus."

---

### Q7: "Why did you choose Jetpack Compose over traditional XML Layouts?"
> **Answer:**  
> "Jetpack Compose provides a declarative UI model where the UI is a direct function of state (`UI = f(State)`).  
> For a gamified app with dynamic animations, heart meters, tile-dragging, and celebration overlays, managing state in XML with ViewHolders and listeners leads to state synchronization bugs. With Compose, our screens reactively recompose from immutable `StateFlow` streams emitted by ViewModels, making our UI deterministic, responsive, and far more maintainable with significantly less boilerplate."

---

### Q8: "How does Blend It differ from Hear It, Say It, and Find It?"
> **Answer:**  
> "Hear It, Say It, and Find It operate at the individual letter/phoneme level.  
> `Blend It` is a group-level synthesis milestone unlocked only after all 4 letters of a Marungko group are mastered. It presents a 5-word construction challenge where children tap and arrange letter tiles into empty slots to form complete words (e.g., placing 'M', 'A', 'P' to spell MAP). It bridges the gap from single-sound recognition to full synthetic reading."

---

### Q9: "Why did you generate PDFs natively with `PdfDocument` instead of using iText or another library?"
> **Answer:**  
> "Third-party PDF libraries like iText or PDFBox are heavyweight, increase our APK size by several megabytes, and often carry restrictive licensing (e.g., AGPL or commercial licenses).  
> Android's built-in `android.graphics.pdf.PdfDocument` (API 26+) allows us to draw directly to a `Canvas` using standard vector drawables, custom fonts, and text layout engines. This keeps our APK ultra-light, guarantees 100% offline generation, and has zero third-party licensing dependencies."

---

### Q10: "How do you prevent children from accidentally entering the Parent Dashboard?"
> **Answer:**  
> "Because we don't require internet accounts or passwords, we implemented a cognitive arithmetic speed-bump dialog (e.g., solving a randomized multiplication or multi-digit addition challenge like *'7 × 8 = ?'*).  
> Grade 1 learners (ages 6–7) cannot solve this without an adult, while parents and teachers can pass it in 2 seconds."

---

### Q11: "What design patterns did you utilize across the app?"
> **Answer:**  
> "1. **Repository Pattern:** Decouples our ViewModels from the underlying Room database and Vosk engine.  
> 2. **Dependency Injection (Hilt):** Provides singletons (`SessionManager`, `PlayItDatabase`) and provides repository fakes for automated testing.  
> 3. **Strategy / Manager Pattern:** Encapsulates isolated rules (`HeartManager`, `StarCalculator`, `SpeechValidator`, `GridGenerator`) into pure Kotlin classes.  
> 4. **Sealed Class Hierarchy:** Models finite UI states (`MapNode.LetterNode` vs `MapNode.BlendItNode`, `UiState.Loading`, `UiState.Success`, `UiState.Error`).  
> 5. **Unidirectional Data Flow (UDF):** State flows down from ViewModel to Composables; events flow up."

---

### Q12: "How do you test your application?"
> **Answer:**  
> "We follow the Android Testing Pyramid:
> 1. **Unit Tests (JVM):** Pure Kotlin domain managers (`StarCalculatorTest`, `HeartManagerTest`, `SpeechValidatorTest`) tested with JUnit and Truth without needing Robolectric or emulators.
> 2. **Repository & DAO Integration Tests:** In-memory Room database tests using `Room.inMemoryDatabaseBuilder()` and Kotlin Coroutines `runTest` with `Turbine` for `Flow` assertions.
> 3. **UI Component Tests:** Compose UI tests using `createComposeRule()` to verify button click events and state rendering."

---

## 6. STAR Method Problem-Solving Stories (Real Challenges)

When asked: *"Tell me about a difficult technical challenge you encountered and how you solved it."*

### Story 1: Offline Speech Recognition Latency & Ambient Noise in Classrooms
- **Situation:** Early tests with speech recognition in classroom settings caused false negatives due to high ambient chatter and background noise from other children.
- **Task:** We needed a robust, offline-capable acoustic validation pipeline that returned results in $\le 500\text{ms}$ while filtering out background noise.
- **Action:** 
  - Configured Vosk with a constrained vocabulary grammar list specifically limited to the target phoneme and example words, reducing acoustic search space.
  - Implemented an RMS-based `NoiseMonitor` to reject recording if background audio levels exceeded a baseline threshold before speech began.
  - Set a tuned 75% acoustic confidence threshold in `SpeechValidator` to account for young children's high-pitch formant frequencies.
- **Result:** Voice processing latency dropped to under 400ms, and recognition accuracy for target phonemes improved significantly in noisy environments.

### Story 2: Enforcing Multi-Profile Data Isolation in an Offline SQLite Database
- **Situation:** With up to 6 children sharing one tablet, progress, stars, and attempt history could easily leak across profiles if queries missed a profile filter.
- **Task:** Create a fail-safe data scoping mechanism that guarantees total separation without requiring manual `profileId` parameter passing across hundreds of UI components.
- **Action:**
  - Built an in-memory `SessionManager` provided via Hilt dependency injection.
  - Configured Room with `@ForeignKey(entity = Profile::class, parentColumns = ["profileId"], childColumns = ["profileId"], onDelete = ForeignKey.CASCADE)`.
  - Wrapped all Repository queries to pull `activeProfileId` automatically at the repository boundary before dispatching DAO calls.
- **Result:** Achieved 100% data isolation; deleting a profile cleanly cascades and deletes all related attempts and progress logs without orphan records.

---

## 7. Panel Traps, Edge Cases & How to Answer Gracefully

| Tricky Question / Trap | What the Interviewer is Testing | The Winning Answer |
|---|---|---|
| *"Why didn't you use PocketSphinx instead of Vosk?"* | Do you research your library dependencies? | *"PocketSphinx relies on older GMM-HMM models and is no longer actively maintained for modern Android APIs. Vosk uses modern Kaldi-based neural network acoustic models, supports Android 8.0+ natively, and delivers superior phoneme accuracy at lower latency."* |
| *"What happens if the app process is killed in the middle of a game?"* | Lifecycle & state restoration awareness. | *"Because all game attempts and star completions are committed immediately to Room via Coroutine dispatchers, progress is never lost. On process recreation, the ViewModel reads the latest persisted state from `LessonProgressRepository`."* |
| *"Why didn't you use SharedPreferences / DataStore for progress?"* | Database architecture & relational integrity. | *"With 28 letters, 7 groups, 3 mini-games per letter, multiple attempts, timestamps, and 6 user profiles, progress is highly relational. Room provides compile-time SQL verification, foreign-key cascade consistency, and indexed query performance for dashboard aggregations that DataStore cannot match."* |
| *"How do you handle letters like 'NG' and 'Ñ' in English phonics?"* | Domain knowledge & DepEd Marungko understanding. | *"The Filipino alphabet and Marungko sequence include 'NG' and 'Ñ'. In our seed data, we mapped 'NG' to its standard digraph phoneme (`/ŋ/`), while 'Ñ' is preserved for sequence completeness with an explicit SME review flag to ensure pedagogical alignment."* |

---

## 8. Future Roadmap ("What would you build next?")

If the panel asks *"If you had another semester or additional budget, what would you add?"*:

1. **Bluetooth Low Energy (BLE) Local Teacher Sync:**  
   Allow teachers in offline classrooms to sync all 6 student profiles from classroom tablets to a teacher tablet over local peer-to-peer Wi-Fi or BLE without requiring internet.
2. **Spaced Repetition & Adaptive Difficulty Engine:**  
   Implement a dynamic review scheduler that automatically surfaces letters where a child scored $< 2$ stars into future Find It distractor pools.
3. **Multilingual Phonics Packs:**  
   Expand the core engine from English Marungko to Mother Tongue-Based Multilingual Education (MTB-MLE) languages such as Tagalog, Cebuano, and Ilocano by hot-swapping phoneme asset packs.

---

## 💡 Quick Pre-Interview Checklist (5 Minutes Before)
- [ ] Review the 60-second elevator pitch out loud 3 times.
- [ ] Memorize the 3 architectural layers: **Presentation (Compose/VM) $\rightarrow$ Domain (Pure Kotlin) $\leftarrow$ Data (Room/Vosk)**.
- [ ] Remember the key numbers: **28 letters, 7 groups of 4, 5 hearts per session, max 6 profiles, $\ge 56\text{dp}$ touch targets**.
- [ ] Breathe, smile, and speak clearly. You know this app inside and out!
