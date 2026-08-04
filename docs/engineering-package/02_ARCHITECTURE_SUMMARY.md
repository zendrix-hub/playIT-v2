# 02 — Architecture Summary

Condensed from SDD v1.0. This is the authoritative shape of the codebase — `07_FOLDER_STRUCTURE.md` turns it into an actual directory tree.

## 1. Architectural Style

**Clean Architecture with MVVM on the presentation layer**, three strict layers, dependencies pointing inward only (Presentation → Domain ← Data; Domain has no Android/framework imports):

```
┌─────────────────────────────────────────────┐
│  Presentation Layer                          │
│  Jetpack Compose Screens + ViewModels        │
└───────────────────┬───────────────────────────┘
                     │ (observes StateFlow, calls use-cases)
┌───────────────────▼───────────────────────────┐
│  Domain Layer                                │
│  Managers / Calculators / Repository          │
│  *interfaces* — pure Kotlin, no Android deps  │
└───────────────────┬───────────────────────────┘
                     │ (interface implemented by)
┌───────────────────▼───────────────────────────┐
│  Data Layer                                  │
│  Room DB · Vosk engine · MediaPlayer ·        │
│  PdfDocument · device hardware                │
└─────────────────────────────────────────────┘
```

## 2. Layer Responsibilities

| Layer | Purpose | Components |
|---|---|---|
| **Presentation** | UI rendering, user input, navigation | 12 Compose Screens (§4), matching ViewModels, reusable Composables |
| **Domain** | Business rules, orchestration, repository *interfaces* | `HeartManager`, `StarCalculator`, `StreakTracker`, `UnlockManager`, `GroupUnlockManager`, `SpeechValidator`, `GridGenerator`, `ReportGenerator`, `RetentionCalculator`, `LetterStatusCalculator`, `BlendItWordSelector`, plus all Repository interfaces |
| **Data** | Persistence + external I/O, repository *implementations* | Room DB + DAOs, `VoskRecognizer`, `AudioCapture`, `AudioPlayer`, `PdfExporter`, `NoiseMonitor` |

Cross-cutting: `SessionManager` (in-memory singleton holding `activeProfileId`) is read by every ViewModel and every Repository call to scope queries/writes to the active child profile. It is not persisted — it is set on profile selection and lives for the app process lifetime.

## 3. Design Patterns in Use

| Pattern | Where | Why |
|---|---|---|
| Repository | Every data-access seam (`ProfileRepository`, `PhonemeRepository`, `LessonProgressRepository`, `SayItAttemptRepository`, `FindItAttemptRepository`, `BlendItWordRepository`, `BlendItAttemptRepository`, `BlendItProgressRepository`, `AchievementRepository`, `ReportLogRepository`, `PictureAssetRepository`) | Decouples ViewModels/domain logic from Room; enables fakes in unit tests |
| MVVM | Every screen | Compose recomposes from `StateFlow`; ViewModel owns state and use-case orchestration |
| Singleton (DI-scoped) | `SessionManager` | Single source of truth for "who is playing right now" without threading a profile ID through every function signature |
| Strategy-ish manager objects | `HeartManager`, `StarCalculator`, `GridGenerator` | Each encapsulates one deterministic rule from `01_REQUIREMENTS_SUMMARY.md §3`, independently unit-testable, reused across modules (e.g., `HeartManager` is instantiated fresh per session but the class is shared by Say It, Find It, and Blend It) |
| Sealed class | `MapNode` (→ `LetterNode` | `BlendItNode`) | Exhaustive `when` handling for the two node types rendered on the map |
| Unidirectional data flow | All screens | Composable reads `StateFlow`, dispatches events up to ViewModel; ViewModel is the only writer of its own state |

## 4. Screen Inventory (12 screens — see conflict note)

| Screen | Purpose | Scoped by |
|---|---|---|
| `SplashScreen` | Boot, routes to profile select | — |
| `ProfileSelectScreen` | Grid of existing profiles, create-new affordance | device |
| `NamePromptScreen` | New profile: name + avatar | device |
| `MapScreen` | Candy-Crush-style winding path, 28 letter nodes + 7 Blend It nodes | `activeProfileId` |
| `HearItScreen` | Audio modeling, by `phonemeId` nav arg | `activeProfileId` |
| `SayItScreen` | Speech recognition, by `phonemeId` nav arg | `activeProfileId` |
| `FindItScreen` | Picture discrimination, by `phonemeId` nav arg | `activeProfileId` |
| `LetterCompleteScreen` | Star celebration after all 3 sublevels | `activeProfileId` |
| `BlendItScreen` | Word construction session, by `groupId` nav arg | `activeProfileId` |
| `BlendItCompleteScreen` | Session summary + star drop-in | `activeProfileId` |
| `ParentDashboardScreen` | Offline analytics, profile switcher | device (all profiles) |
| `ReportPreviewScreen` | PDF preview + save | `selectedProfileId` (dashboard's active selection) |

**Conflict flagged:** the SDD's own Tech Stack Summary table claims "10 screens," but its Screen Inventory table (reproduced above) lists 12. **Resolution: 12 is correct** — it matches the actual inventory table and every downstream Module section references all 12 by name. Treat "10" as a stale summary-table figure.

## 5. Data Flow (representative — Say It module)

```
Child taps mic → SayItScreen → SayItViewModel.startRecording()
  → AudioCapture streams 16kHz mono PCM
  → VoskRecognizer.recognize(audio) → RecognitionResult(text, confidence)
  → SpeechValidator.validate(result, targetPhoneme) → Boolean
  → HeartManager.deductHeart() / checkRecovery()  [domain, pure logic]
  → SayItViewModel updates StateFlow<FeedbackState>
  → SayItScreen recomposes: green/red highlight, plays AudioPlayer corrective clip
  → on sub-level completion: SayItAttemptRepository.save() + LessonProgressRepository.save()
      → Room DAO write → SQLite
  → SessionManager.activeProfileId scopes every write above
```

Every module (Hear It, Find It, Blend It, Dashboard) follows the same shape: **UI event → ViewModel → Domain manager (pure Kotlin decision) → Repository (persist) → StateFlow (re-render)**. No domain class ever imports `android.*` or touches Room/Vosk/MediaPlayer directly — those live behind repository/manager interfaces implemented in the Data layer.

## 6. Package Responsibilities

See `07_FOLDER_STRUCTURE.md` for the literal tree. At a glance:

- `presentation/<module>/` — one folder per screen-family (map, hearit, sayit, findit, blendit, dashboard, profile), each with `Screen.kt`, `ViewModel.kt`, and a `components/` subfolder for module-local Composables.
- `domain/manager/`, `domain/repository/` (interfaces only), `domain/model/` — framework-free Kotlin.
- `data/local/` (Room: `entity/`, `dao/`, `PlayItDatabase.kt`), `data/repository/` (impls), `data/speech/` (Vosk wrapper + `AudioCapture` + `NoiseMonitor`), `data/audio/` (`AudioPlayer`), `data/pdf/` (`PdfExporter`).
- `di/` — Hilt modules, one per layer seam (`DatabaseModule`, `RepositoryModule`, `SpeechModule`).
- `navigation/` — `NavGraph.kt`, route definitions (`09_NAVIGATION_SPEC.md`).

## 7. Dependency Relationships (build-time)

`presentation` depends on `domain` (interfaces + models) and is injected `data` implementations via Hilt — presentation code never imports `data.*` directly except through DI-provided interfaces. `domain` depends on nothing outside pure Kotlin + Coroutines. `data` depends on `domain` (to implement its interfaces) plus Android/Room/Vosk/Coil. This ordering is what makes the domain layer unit-testable without an emulator (`12_TESTING_STRATEGY.md`).

## 8. Technology Stack (full table)

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Language | Kotlin | 1.9+ | |
| UI | Jetpack Compose | 1.5+ | |
| Architecture | MVVM + Clean Architecture | — | |
| DB | Room (SQLite) | 2.6+ | |
| Speech | Vosk | 0.3.47 (floor 0.3.45) | Offline phoneme detection |
| Audio | Android `MediaPlayer` | API 26+ | |
| PDF | Android `PdfDocument` | API 26+ | No third-party library |
| Images | Coil | 2.5+ | |
| Concurrency | Coroutines + Flow | 1.7+ | |
| Nav | Navigation Compose | 2.7+ | |
| Min SDK | API 26 (Android 8.0) | — | |
