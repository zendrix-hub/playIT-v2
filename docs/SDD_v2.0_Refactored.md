# Software Design Description (SDD)
## Project: PlayIT — An Offline-First Gamified Early Literacy Mobile Application
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Department:** College of Computer Studies, Cebu Institute of Technology – University  
**Document Version:** 2.0 (Renewed & Fully Refactored Post-MVP Validation)  
**Publication Date:** September 26, 2026  
**Document Status:** Approved System Architecture Blueprint  

---

## Document Revision History

| Version | Date | Primary Author(s) | Architectural Refactoring & Traceability Description |
|:---:|:---:|:---:|---|
| **0.1** | May 11, 2026 | System Architect | Initial SDD draft based on SRS v2.0 (MVVM, Clean Architecture, Room SQLite, Vosk ASR). |
| **0.2** | May 15, 2026 | System Architect | Added Word Challenge (Blend It) CVC synthesis checkpoint per adviser directive. |
| **1.0** | May 20, 2026 | System Architect & Dev Team | Final Capstone 1 SDD: decoupled game modules, established baseline Room DB schema v1. |
| **2.0** | September 26, 2026 | Lead Architect & Capstone Team | **Comprehensive Renewal & Architectural Refactoring Based on MVP Validation:**<br>• **Tutoring & Pedagogy Layer (§2.2):** Introduced `LessonEngine`, `TutorPolicy` finite state machine (FSM), and `SayItJudge` with per-letter dynamic grammars and error tagging; **eliminated heart deductions in Say It**.<br>• **Audio Subsystem Architecture (§3.1):** Refactored `AudioPlaybackManager` with `AudioComposer` and pre-cached `SoundPool` for instant phoneme playback; isolated pure phoneme (`ph_m.wav`) and key-word (`kw_m_mouse.wav`) assets (FR-02, NFR-AUD-01).<br>• **Speech Recognition Subsystem (§3.2):** Replaced `SpeechService` with an asynchronous `AudioRecord` 16kHz mono loop streaming raw PCM buffers to calculate normalized RMS amplitude for `MicStateVisualizer` while feeding Vosk `Recognizer.acceptWaveForm()`; guaranteed tap-to-listening transition in ≤100ms (FR-03, NFR-PERF-01).<br>• **Decodable Word Bank Refactoring (§3.4):** Purged 5 invalid CVC words (AIM, BEE, TOY, BOY, ZOO) containing untaught vowel teams/diphthongs; replaced with AM, SUM, TUB, YAM, ZIP; flagged QUIZ exception (FR-13).<br>• **Persistence & Telemetry (Room Schema v3, §4):** Added `ProfileEntity` (multi-profile up to 6), `LetterProgressEntity` (spaced retrieval mastery), and `TelemetryEventEntity` (microsecond-accurate monotonic timestamps); added local PIN-gated CSV/PDF exporters (FR-14, FR-NEW-TEL).<br>• **Pediatric Tokens & Accessibility (§3.6):** Formalized `Modifier.pediatricTouchTarget(64.dp)` and created `ArticulationCue` composable (NFR-ACC-01, NFR-ACC-02). |

---

## 1. Introduction

### 1.1 Purpose
This Software Design Description (SDD v2.0) provides the definitive technical architecture, component breakdown, data schemas, and interface workflows for **PlayIT**. It translates the refactored functional and non-functional requirements defined in **SRS v3.0** into a concrete, testable, and robust implementation blueprint. This renewed version directly resolves the technical and pedagogical bottlenecks identified during the Weeks 1–2 MVP Field Validation (schwa audio intrusion, microphone button hesitation, missing multi-profile telemetry, non-decodable words, and accessibility targets).

### 1.2 System Scope
This design document governs the entire PlayIT Android mobile application:
- Three-layer Clean Architecture with Model-View-ViewModel (MVVM) in Jetpack Compose.
- Autonomous, teacher-independent **Tutoring & Pedagogy Engine** (`LessonEngine`, `TutorPolicy` FSM, `SayItJudge`).
- Low-latency Audio Subsystem utilizing pre-cached `SoundPool` and runtime `AudioComposer`.
- Privacy-preserving, completely offline speech recognition via Vosk 0.3.47.
- Room SQLite Database (Schema v3) supporting multi-profile isolation and structured interaction telemetry.
- Pediatric-first design system strictly enforcing a zero-emoji policy and $\ge 64\,\text{dp}$ touch targets.
- Local, unauthenticated Parent/Facilitator Dashboard with arithmetic safety gates and PDF/CSV report generation.

### 1.3 Definitions and Acronyms
- **ASR:** Automatic Speech Recognition.
- **CVC:** Consonant-Vowel-Consonant syllable/word structure.
- **DAO:** Data Access Object (Room persistence layer).
- **FSM:** Finite State Machine.
- **MVVM:** Model-View-ViewModel architectural pattern.
- **PCM:** Pulse-Code Modulation (uncompressed raw audio format).
- **RMS:** Root Mean Square (audio signal power measurement).
- **Vosk:** Lightweight offline speech recognition toolkit.

### 1.4 References
1. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
2. Martin, R. C. (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall.
3. IEEE Std 1016-2009: *IEEE Standard for Information Technology—Systems Design—Software Design Descriptions*.
4. PlayIT Software Requirements Specification (SRS v3.0), September 2026.

---

## 2. Architectural Design

### 2.1 System Architecture Paradigm
PlayIT is structured around **Clean Architecture** principles combined with **MVVM** and unidirectional data flow (UDF) using Kotlin Coroutines and Jetpack Compose `StateFlow`. To guarantee autonomous child operation without requiring adult supervision, this refactored design situates a specialized **Tutoring Layer** between the Domain and Presentation layers.

```
+-----------------------------------------------------------------------------------+
|                           PRESENTATION LAYER (Jetpack Compose)                    |
|  [HomeScreen]  [MapScreen]  [HearItScreen]  [SayItScreen]  [FindItScreen]        |
|  [BlendItScreen]  [ProfileSwitcher]  [ParentDashboard]  [MicStateVisualizer]     |
+-----------------------------------------+-----------------------------------------+
                                          | StateFlow / UI Events
+-----------------------------------------v-----------------------------------------+
|                                TUTORING & PEDAGOGY LAYER                          |
|  +--------------------+  +----------------------+  +---------------------------+  |
|  | LessonEngine       |  | TutorPolicy (FSM)    |  | SayItJudge                |  |
|  | - Sequence Runner  |  | - Error Dispatcher   |  | - Vosk Grammar Matcher    |  |
|  | - Step Emitter     |  | - Prompt Ladder      |  | - Error Categorizer       |  |
|  +--------------------+  +----------------------+  +---------------------------+  |
|  +--------------------+  +----------------------+  +---------------------------+  |
|  | AudioComposer      |  | LearnerModel         |  | TelemetryLogger           |  |
|  | - Runtime Assembly |  | - Spaced Review Queue|  | - Monotonic Latency Clock |  |
|  +--------------------+  +----------------------+  +---------------------------+  |
+-----------------------------------------+-----------------------------------------+
                                          | Pure Domain Interfaces
+-----------------------------------------v-----------------------------------------+
|                                    DOMAIN LAYER                                   |
|  Repositories (Pure Kotlin - Zero Android Imports):                               |
|  - SpeechRepository  - AudioRepository  - LessonRepository  - ProfileRepository   |
|  - TelemetryRepository  - GameProgressRepository  - WordBankRepository            |
+-----------------------------------------+-----------------------------------------+
                                          | Repository Implementations
+-----------------------------------------v-----------------------------------------+
|                             DATA & INFRASTRUCTURE LAYER                           |
|  +--------------------+  +----------------------+  +---------------------------+  |
|  | Vosk Engine        |  | Audio Subsystem      |  | Room Database (Schema v3) |  |
|  | - AudioRecord Loop |  | - SoundPool Cache    |  | - ProfileEntity           |  |
|  | - 16kHz PCM Buffer |  | - MediaPlayer Fallback| | - LetterProgressEntity    |  |
|  | - RMS Calculator   |  | - Asset Manifest     |  | - TelemetryEventEntity    |  |
|  +--------------------+  +----------------------+  +---------------------------+  |
+-----------------------------------------------------------------------------------+
```

### 2.2 Decomposition & Architectural Layers
1. **Presentation Layer (`presentation/`):** Contains UI composables and ViewModels. ViewModels observe domain StateFlows and emit immutable UI state objects. Composables remain purely declarative and react to state mutations without containing gameplay logic.
2. **Tutoring & Pedagogy Layer (`tutoring/`):** Governs learner pacing, scaffolding, and formative remediation. Decouples educational decision-making from UI view code:
   - `LessonEngine`: Executes letter lesson scripts in sequential steps.
   - `TutorPolicy`: Implements a finite state machine managing We Do, You Do, error corrections, and lead steps.
   - `SayItJudge`: Evaluates captured speech buffers against constrained per-letter grammars, returning decisions and diagnostic error codes.
   - `LearnerModel`: Tracks memory retention across sessions to power spaced retrieval warm-ups.
3. **Domain Layer (`domain/`):** Houses pure Kotlin enterprise business rules, data models, and repository interfaces. Contains zero `android.*` imports.
4. **Data Layer (`data/`):** Implements domain interfaces using hardware drivers (Vosk ASR, Android AudioRecord, SoundPool) and SQLite persistence (Room 2.6+).

---

## 3. Detailed Component Design

### 3.1 Audio Subsystem Refactoring (FR-02, NFR-AUD-01)
To ensure pure phoneme delivery with zero trailing vowel intrusion and instantaneous acoustic response, the audio subsystem decouples short acoustic phoneme bursts from long musical playback.

#### 3.1.1 AudioPlaybackManager Implementation
- **Underlying Driver:** Android native `SoundPool` API with `AudioAttributes.USAGE_GAME`.
- **Preloading Lifecycle:** When the learner navigates to a chapter node on the Map Screen, all phoneme bursts for that letter (`ph_<letter>.wav`) are loaded into uncompressed memory.
- **Physical Asset Segregation:**
  - `res/raw/ph_<letter>.wav`: Pure phoneme burst ($800\,\text{ms}$ continuous; $\le 250\,\text{ms}$ stops). Zero schwa trailing.
  - `res/raw/kw_<letter>_<word>.wav`: Key word pronunciation (e.g., `kw_m_mouse.wav`).
  - `res/raw/car_<phrase>.wav`: Spoken carrier phrases (e.g., `car_listen.wav`, `car_this_letter_says.wav`, `car_say_it_with_me.wav`, `car_your_turn.wav`).

#### 3.1.2 AudioComposer Component
`AudioComposer` manages runtime assembly of the modeling sequence using duration metadata from `docs/audio-release/manifest.json`:
```kotlin
class AudioComposer @Inject constructor(
    private val playbackManager: AudioPlaybackManager,
    private val assetManifest: AssetManifestRepository
) {
    suspend fun executeHearItSequence(letter: String, onStepUpdate: (SequenceStep) -> Unit) {
        val script = assetManifest.getScript(letter)
        for (step in script.steps) {
            when (step) {
                is Step.PlayCarrier -> {
                    onStepUpdate(SequenceStep.Carrier(step.id))
                    playbackManager.play(step.id)
                    delay(step.durationMs)
                }
                is Step.RevealMnemonic -> {
                    onStepUpdate(SequenceStep.Mnemonic(letter))
                    delay(step.durationMs)
                }
                is Step.PlayPhoneme -> {
                    onStepUpdate(SequenceStep.Phoneme(letter, isArticulationActive = true))
                    playbackManager.play("ph_$letter")
                    delay(step.durationMs + 500L) // 500ms pause between models
                }
                is Step.PlayKeyword -> {
                    onStepUpdate(SequenceStep.Keyword(step.word))
                    playbackManager.play("kw_${letter}_${step.word}")
                    delay(step.durationMs)
                }
            }
        }
    }
}
```

---

### 3.2 Speech Recognition Subsystem & Visualizer (FR-03, NFR-ASR-01, NFR-PERF-01)
To eliminate child hesitation at the mic and enable real-time visualization, the standard Vosk `SpeechService` wrapper is replaced with a low-level, non-blocking `AudioRecord` pipeline.

#### 3.2.1 AudioRecord Loop & RMS Amplitude Pipeline
```
[Device Microphone (16 kHz Mono PCM)]
                |
                v
       [AudioRecord Buffer] (1024 samples / 64 ms chunk)
                |
       +--------+--------+
       |                 |
       v                 v
[Compute RMS Amplitude]  [Vosk Recognizer.acceptWaveForm()]
       |                 |
       v (0.0 to 1.0)    v (Partial/Final Result)
[MicStateVisualizer]     [SayItJudge Decision Logic]
```

- **Audio Settings:** Sample Rate: $16,000\,\text{Hz}$, Channel: `CHANNEL_IN_MONO`, Encoding: `ENCODING_PCM_16BIT`.
- **RMS Calculation:** For each 1024-sample buffer, root-mean-square amplitude is calculated:
  $$\text{RMS} = \sqrt{\frac{1}{N}\sum_{i=1}^N x_i^2}, \quad \text{Normalized Level} = \text{coerceIn}\left(\frac{20 \log_{10}(\text{RMS} + 10^{-5}) - \text{Floor}}{\text{Range}}, 0.0, 1.0\right)$$
- **Tap-to-Listening Transition:** When the user taps the mic button (or carrier finishes), the ViewModel immediately mutates UI state to `MicState.LISTENING` in $\le 20\,\text{ms}$, while the background recording coroutine initializes.

#### 3.2.2 MicStateVisualizer Composable
```kotlin
@Composable
fun MicStateVisualizer(
    state: MicState,
    audioLevel: Float, // 0.0 to 1.0
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (state == MicState.LISTENING) 1.0f + (audioLevel * 0.45f) else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val rippleAlpha by animateFloatAsState(
        targetValue = if (state == MicState.LISTENING) 0.35f + (audioLevel * 0.5f) else 0.0f
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(100.dp)) {
        if (state == MicState.LISTENING) {
            Box(
                Modifier
                    .size(96.dp)
                    .scale(animatedScale)
                    .background(Color(0xFFFBBF24).copy(alpha = rippleAlpha), CircleShape)
            )
        }
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp).background(state.backgroundColor, CircleShape)
        ) {
            Icon(state.icon, contentDescription = state.description, tint = Color.White)
        }
    }
}
```

---

### 3.3 Tutoring Layer & State Machine (FR-03, NFR-ASR-01, FR-NEW-REC)

#### 3.3.1 SayItJudge with Dynamic Letter Grammars
Vosk is instantiated with a constrained per-letter runtime grammar rather than open vocabulary:
```json
{
  "letter": "m",
  "grammar": ["<target_m>", "em", "ma", "[unk]"],
  "targetToken": "<target_m>",
  "foils": {
    "em": "LETTER_NAME",
    "ma": "ADDED_VOWEL"
  },
  "confidenceThreshold": 0.65
}
```
- **Error Classification:**
  - Utterance matches `targetToken` and confidence $\ge 0.65$ $\rightarrow$ `JudgeResult.CORRECT`.
  - Utterance matches `"em"` $\rightarrow$ `JudgeResult.ERROR(ErrorType.LETTER_NAME)`.
  - Utterance matches `"ma"` $\rightarrow$ `JudgeResult.ERROR(ErrorType.ADDED_VOWEL)`.
  - Utterance matches `"[unk]"` or confidence $<0.65$ $\rightarrow$ `JudgeResult.ERROR(ErrorType.UNKNOWN)`.

#### 3.3.2 TutorPolicy Finite State Machine (FSM)
```
      +------------+
      |    IDLE    |
      +-----+------+
            | Carrier "Say it with me!" (We Do x2)
            v
      +------------+
      |  WE_DO     |
      +-----+------+
            | Mascot Choral Practice Complete -> Carrier "Your Turn!"
            v
      +------------+       Mic Tap or Auto-Listen
+---> | LISTENING  | <---------------------------------+
|     +-----+------+                                   |
|           | User speaks / AudioRecord captures       |
|           v                                          |
|     +------------+                                   |
|     | EVALUATING | (Vosk ASR Grammar Match)          |
|     +-----+------+                                   |
|           |                                          |
|           +---> Match == Target -------------------> | PRAISE -> Advance Node
|           |                                          |
|           +---> Match == Foil / Silence (Attempt < 3) |
|           |     |                                    |
|           |     v                                    |
|           |  [ERROR_CORRECTION] (Formative prompt) -+
|           |
|           +---> Match == Foil / Silence (Attempt == 3)
|                 |
|                 v
|              [LEAD] ("Let's say it together" -> Mark NEEDS_PRACTICE -> Advance)
```

---

### 3.4 Decodable Blend It Word Bank Refactoring (FR-13)
The seeded database entity `BlendItWord` is updated via Room Schema v3 migration. The 5 invalid words identified during MVP validation are formally replaced:

| Chapter | Unlocked Letters | Purged Word | Violation Reason | Replacement Word | Valid Letters Used |
|:---:|---|:---:|---|:---:|:---:|
| **Ch 1** | m, s, a, i | **AIM** | Contains vowel team *ai* | **AM** | a, m |
| **Ch 2** | + o, b, u, t | **BEE** | Contains vowel team *ee* | **SUM** | s, u, m |
| **Ch 3** | + k, l, y, n | **TOY** | Contains diphthong *oy* | **TUB** | t, u, b |
| **Ch 3** | + k, l, y, n | **BOY** | Contains diphthong *oy* | **YAM** | y, a, m |
| **Ch 7** | + q, x, z | **ZOO** | Contains vowel team *oo* | **ZIP** | z, i, p |
| **Ch 7** | + q, x, z | **QUIZ** | *qu* = /kw/ digraph | **QUIZ** | Flagged with `isDocumentedException = true` |

---

### 3.5 Database Architecture (Room Schema v3) & Telemetry Logger (FR-14, FR-NEW-TEL)

#### 3.5.1 Room Database Entities
```kotlin
// Profile Entity: Multi-Profile Management (FR-14)
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val avatarResName: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

// Letter Progress Entity: Spaced Retrieval Mastery (FR-NEW-REC)
@Entity(
    tableName = "letter_progress",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId", "letter"], unique = true)]
)
data class LetterProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val letter: String,
    val masteryState: String, // "NEW", "INTRODUCED", "NEEDS_PRACTICE", "SECURE"
    val echoPassCount: Int = 0,
    val recallPassCount: Int = 0,
    val lastSeenEpoch: Long = 0,
    val nextReviewEpoch: Long = 0
)

// Telemetry Event Entity: Diagnostic Interaction Logging (FR-NEW-TEL)
@Entity(
    tableName = "telemetry_events",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId"), Index("eventType")]
)
data class TelemetryEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val sessionId: String,
    val chapter: Int,
    val letter: String,
    val module: String, // "HEAR", "SAY", "FIND", "BLEND"
    val eventType: String, // "mic_tap", "speech_end", "asr_result", "feedback_shown", etc.
    val elapsedRealtimeMs: Long, // Monotonic SystemClock.elapsedRealtime() for latency
    val wallClockEpoch: Long, // Epoch timestamp for audit correlation
    val resultValue: String?, // Recognized text or correct/incorrect
    val asrConfidence: Float?,
    val errorType: String? // "LETTER_NAME", "ADDED_VOWEL", "SUBSTITUTION", "UNKNOWN"
)
```

#### 3.5.2 Local CSV Export Manager
A local exporter reads `telemetry_events` for the active profile, formats records into RFC 4180 CSV, writes the file to `context.filesDir/exports/`, and launches the native Android `Intent.ACTION_SEND` share sheet behind the arithmetic gate.

---

### 3.6 Pediatric Design System Tokens & Accessibility (NFR-ACC-01, NFR-ACC-02)

#### 3.6.1 MinTouchTarget Modifier
All clickable Jetpack Compose elements apply a global ergonomic modifier ensuring compliance with the $64\times 64\,\text{dp}$ touch bounding box:
```kotlin
fun Modifier.pediatricTouchTarget(): Modifier = this.then(
    Modifier
        .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
        .padding(4.dp)
)
```

#### 3.6.2 ArticulationCue Composable
```kotlin
@Composable
fun ArticulationCue(
    letter: String,
    cueDrawableRes: Int,
    captionText: String, // e.g. "mmm"
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, if (isHighlighted) Color(0xFF2B7A78) else Color(0xFFCBD5E0), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = cueDrawableRes),
            contentDescription = "Mouth articulation guide for $letter",
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = captionText,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1F3A3D)
        )
    }
}
```

---

## 4. Detailed Interface & Interaction Workflows

### 4.1 Hear It Modeling Sequence Workflow
```
User selects Letter -> Screen Loads -> SoundPool Preloads Letter Clips
  -> Step 1: Mascot plays "Listen!" carrier (Audio + Animation)
  -> Step 2: Letter card flips, displaying embedded Picture Mnemonic
  -> Step 3: Mascot plays "This letter says..." carrier
  -> Step 4: Pure phoneme plays 3 times (800ms hold or <=250ms stop)
             accompanied by ArticulationCue highlight and caption
  -> Step 5: Mascot plays Keyword audio ("mouse") + Keyword image
  -> Step 6: Pure phoneme plays 1 time
  -> Step 7: Mascot plays "Say it with me!" -> Transition to Say It
```

### 4.2 Say It Interaction & Correction Workflow
```
Screen Enters -> Mascot leads 2 Choral Turns ("Say it with me!")
  -> Mascot plays "Your turn!"
  -> MicState changes to LISTENING (<= 100ms)
  -> Child vocalizes -> AudioRecord computes RMS -> MicStateVisualizer pulses
  -> Vosk signals End-of-Speech -> MicState changes to PROCESSING
  -> SayItJudge evaluates audio buffer against per-letter grammar:
       [Branch A: Target Match]
         -> MicState changes to RESULT (Green)
         -> Success chime plays + specific praise ("Yes! /m/, lips together!")
         -> Award Star -> Advance to Find It
       [Branch B: Foil Match or Silence (Attempt < 3)]
         -> MicState changes to RESULT (Amber)
         -> Formative prompt plays (e.g. "Almost! Just /m/, no 'ah'")
         -> Lip cue enlarges -> Mic reactivates for retry
       [Branch C: Foil Match or Silence (Attempt == 3)]
         -> Mascot leads together-practice ("Let's say it together: /m/")
         -> LetterProgressEntity marked NEEDS_PRACTICE
         -> Advance to Find It (Zero hearts deducted)
```

---

## 5. Technology Stack & External Dependencies

| Layer / Subsystem | Technology / Library | Version | Technical Justification & Usage Scope |
|---|---|:---:|---|
| **Language** | Kotlin | 1.9.22 | 100% pure Kotlin domain layer with coroutines and Flow. |
| **UI Framework** | Jetpack Compose | 1.5.4 | Declarative reactive UI toolkit utilizing Material Design 3 tokens. |
| **Speech Engine** | Vosk Android SDK | 0.3.47 | Lightweight offline speech recognition (`vosk-model-small-en-us-0.15`). |
| **Audio I/O** | Android AudioRecord / SoundPool | Native | Non-blocking 16kHz PCM recording and zero-latency SoundPool playback. |
| **Local Database** | Android Jetpack Room | 2.6.1 | SQLite ORM managing profiles, spaced retrieval states, and telemetry. |
| **Document Export** | Android PdfDocument | Native | Zero-dependency local PDF progress report generation. |
| **Audio Synthesis** | Kokoro-82M / Chatterbox-Turbo | Apache-2.0 / MIT | Offline neural TTS for carrier phrases and cloned held phonemes. |

---

## 6. Verification & Architectural Testing Plan

1. **Audio Latency & Purity Test Suite (`TC-AUD-01`, `TC-AUD-02`):** Automated instrumentation tests verifying SoundPool stream start latency $\le 50\,\text{ms}$ and gate verification integrity.
2. **Microphone Reactive Visualizer Suite (`TC-MIC-01`, `TC-MIC-02`):** Robolectric/Espresso tests measuring state transition timestamp deltas ($\le 100\,\text{ms}$) and ripple scaling proportional to mock PCM input.
3. **ASR Discrimination Suite (`TC-ASR-01` to `06`):** Audio replay harness feeding benchmark child recordings (normal, foil, noise) into Vosk to verify $\ge 80\%$ agreement and $\le 15\%$ false reject limits.
4. **Room Schema Migration Suite (`TC-DB-01`, `TC-DB-02`):** `MigrationTestHelper` verifying flawless upgrade from Schema v1/v2 to Schema v3 without data loss.
