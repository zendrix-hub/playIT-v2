# 05 — Engineering Blueprint

How to actually build this. Read after `01`–`04`; this is where requirements + architecture become build decisions.

## 1. Core Engineering Principles

1. **Domain layer is pure Kotlin.** No `android.*`, no Room annotations, no Vosk types inside `domain/`. Every rule from `01_REQUIREMENTS_SUMMARY.md §3` (heart math, star math, unlock logic, distractor sourcing) is a plain Kotlin class/function that a JVM unit test can call with zero mocking of Android.
2. **Repositories are the only crossing point** between domain and data. A ViewModel never touches a DAO directly.
3. **`SessionManager.activeProfileId` scopes every read and write.** Any repository method that doesn't take a `profileId` parameter (explicitly, or implicitly via `SessionManager`) is a bug — multi-profile data bleed is the single easiest correctness mistake to make in this codebase.
4. **Auto-save is per-completed-sub-level, not continuous.** Don't build a more aggressive autosave than the spec calls for (`01 §5`) — it adds write volume for no spec'd benefit and risks masking bugs in the completion-detection logic.
5. **Every hard-coded gameplay number is a named constant, not a magic number** — heart pool sizes, star thresholds, distractor counts, timing budgets. Put them in one `domain/model/GameplayConstants.kt` so a future rule change (e.g., `01 §7.4`'s draft Blend It thresholds) is a one-file diff.
6. **No network code, anywhere, after first install.** Don't add a `NetworkModule`, don't add Retrofit/OkHttp as a dependency. If a future ticket asks for cloud sync, that's a new architecture decision, not an extension of this one.

## 2. Domain Layer — Class Contracts

| Class | Responsibility | Key methods (signatures indicative, not literal Kotlin) |
|---|---|---|
| `HeartManager` | Owns one session's heart pool | `deductHeart(): Int`, `checkRecovery(consecutiveCorrect: Int): Boolean`, `resetForRestart(newPool: Int)`, `getHearts(): Int` — capped at the session's starting pool per `01 §7.5` |
| `StarCalculator` | Letter-level star math | `calculate(accuracy: Float, heartsLost: Int): Int` → 1/2/3 per `01 §1 Module 5` |
| `BlendItStarThresholds` *(new — see `01 §7.4`)* | Blend It star math, behind a named constant so it's swappable once confirmed | `calculate(wordsCorrect: Int, heartsLost: Int): Int` |
| `StreakTracker` | Daily streak + milestone badges | `recordActivity(profileId)`, `checkMilestone(currentStreak): Badge?`, `resetIfInactive(lastPlayedAt)` |
| `UnlockManager` | Letter N→N+1 gating | `isUnlocked(profileId, phonemeId): Boolean` |
| `GroupUnlockManager` | Blend It node gating | `isGroupComplete(profileId, groupId): Boolean` |
| `SpeechValidator` | Vosk output → pass/fail | `validate(result: RecognitionResult, targetPhonemeId: Int): Boolean` against the ≥75% baseline (`01 §2`) |
| `GridGenerator` | Find It grid assembly | `generateGrid(targetPhonemeId, masteredPhonemeIds): List<GridItem>` — enforces exactly 3 targets/2 distractors, and the Letter-1 fallback pool from `01 §5` |
| `BlendItWordSelector` | Blend It session word pool | `selectWords(groupId, masteredGroupIds): List<BlendItWord>` — 5 words, ≥1 from current group |
| `RetentionCalculator` | 7-day retention score for the dashboard | `calculate(profileId): Float` |
| `LetterStatusCalculator` | Dashboard risk color | `status(accuracy, failedAttempts): RiskStatus` (Green/Yellow/Red per `01 §1 Module 6`) |
| `ReportGenerator` | Aggregates dashboard data into a print-ready structure | `generate(profileId): ReportData` — consumed by `PdfExporter` (data layer) |

## 3. Data Layer — Key Implementation Notes

- **Room:** single `PlayItDatabase` with the entities in `08_DATABASE_SPEC.md`. Use `OnConflictStrategy.REPLACE` for progress upserts. All DAOs expose `Flow<T>` for read paths so Compose recomposes reactively; use `suspend fun` for writes.
- **Vosk (`VoskRecognizer`):** load the bundled small English model once at app start (not per-screen) and hold it as a Hilt-provided singleton — reloading the model per `SayItScreen` visit would blow the ≤0.5s feedback budget. Dynamically restrict the recognizer's grammar to the current lesson's expected phoneme/word set before each recognition call (this is both an accuracy and a memory-footprint optimization — see `12_TESTING_STRATEGY.md` for how to test it). Release/free the recognizer instance explicitly in `onCleared()`/lifecycle teardown to avoid native memory leaks.
- **`AudioCapture`:** 16kHz mono PCM stream, matching Vosk's expected input format.
- **`NoiseMonitor`:** samples ambient dB continuously while `SayItScreen` is active; feeds the `NoiseLevelIndicator` composable and the pre-mic "quiet as a mouse" check (`04 §7`).
- **`AudioPlayer`:** thin `MediaPlayer` wrapper; must support rapid sequential playback (a child tapping replay repeatedly) without leaking players — reuse one instance per screen, `stop()`/`reset()` between plays rather than recreating.
- **`PdfExporter`:** builds the report purely from `ReportGenerator`'s output using `android.graphics.pdf.PdfDocument` — no external PDF library per the SDD.

## 4. Cross-Cutting Concerns

| Concern | Approach |
|---|---|
| Error handling | Domain functions return typed results (e.g., sealed `Result` types), not exceptions, for expected outcomes (wrong answer, heart depletion). Reserve exceptions for genuinely exceptional conditions (asset missing, DB write failure) and surface them as a friendly mascot-guided error state (`04 §5`), never a raw dialog. |
| State restoration | ViewModels expose `StateFlow`; on process death, rely on Room as the source of truth (re-query on ViewModel re-creation) rather than `SavedStateHandle` for gameplay state — an interrupted sub-level is *meant* to be lost per `01 §5`. |
| Performance on low-end hardware | Stable `@Immutable`/`@Stable` data classes for Compose state; `LazyColumn`/`LazyRow` with stable keys for the Map's node list; avoid recomposition storms by scoping `StateFlow` narrowly (e.g., `heartsFlow` separate from `feedbackFlow`) rather than one giant screen-state blob. |
| Accessibility | Every screen composed to satisfy `03`'s contrast/touch-target/dual-coding rules from first implementation, not retrofitted. |
| Testing seams | Every domain class takes its dependencies (repositories) as constructor params (interfaces) so it can be unit tested with fakes — see `12_TESTING_STRATEGY.md`. |

## 5. Build Order Rationale (detail behind `06_IMPLEMENTATION_ROADMAP.md`)

Build **bottom-up through one vertical slice first** (Profile → Map → Hear It → Say It → Find It for a single letter, end to end, with real Room persistence and real Vosk integration) before horizontally scaling to all 28 letters. This surfaces integration risk (Vosk model loading, Room schema, navigation args) early, when it's cheap to fix, rather than after 28 letters' worth of content is wired to a broken pattern.
