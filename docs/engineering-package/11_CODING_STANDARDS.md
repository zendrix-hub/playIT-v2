# 11 — Coding Standards

## 1. Kotlin Style
- Follow the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) / Android Kotlin style guide as the baseline (4-space indent, `PascalCase` classes, `camelCase` functions/properties, `SCREAMING_SNAKE_CASE` for top-level constants).
- Prefer `val` over `var` everywhere; a `var` in a domain class is a signal to double-check whether state should instead live in a `StateFlow` owned by a ViewModel.
- No `!!` (non-null assertion) outside of test code. If you're tempted to use it, the surrounding type should be non-nullable in the first place, or the call site needs an explicit `Result`/sealed-class handling path.
- Every `public` class/function in `domain/` gets a KDoc comment stating the rule it implements and a reference to the `01_REQUIREMENTS_SUMMARY.md` section it comes from (e.g., `// Implements 01 §1 Module 3: exactly 3 targets, 2 distractors from mastered letters`). This is the single highest-leverage commenting rule in this codebase — it's what lets a future engineer (human or agent) verify a change against the spec without re-reading this whole package.

## 2. Naming Conventions (project-specific)
- ViewModel per screen: `<Screen>ViewModel`, exposing one or more `StateFlow<T>` named `<thing>State` or `<thing>Flow` — not `uiState` as a single catch-all blob for screens with genuinely independent pieces of state (`05 §4` recomposition-scoping note).
- Repository interfaces live in `domain/repository/`, named `<Noun>Repository`; implementations live in `data/repository/`, named `<Noun>RepositoryImpl`.
- Domain manager classes are named for what they compute/decide, not how (`StarCalculator`, not `StarLogicHelper`).
- Room entities: `<Noun>Entity`; DAOs: `<Noun>Dao`; domain-layer plain models (no Room annotations) drop the `Entity` suffix (`Profile`, not `ProfileEntity`) — the repository impl maps between them. Do not leak Room entity types past the repository boundary into `domain/` or `presentation/`.

## 3. Architecture Discipline
- No `android.*` imports in `domain/` (enforced by convention now; consider a Lint/Detekt custom rule or a Gradle module boundary once the project is large enough to justify the setup cost).
- ViewModels depend on domain interfaces via constructor injection (Hilt `@Inject`), never on concrete `data/` classes.
- Every gameplay constant (heart pool sizes, star thresholds, timing budgets, distractor counts) lives in `domain/model/GameplayConstants.kt` — no magic numbers in ViewModels or Composables (`05 §1.5`).

## 4. Coroutines & Flow
- Repository read methods return `Flow<T>` (Room-generated); write methods are `suspend fun`.
- ViewModels collect flows in `viewModelScope`; never launch a coroutine from a Composable directly for anything beyond a `LaunchedEffect`-scoped, UI-local animation trigger.
- Use `distinctUntilChanged()` on derived flows that feed Compose state, to avoid recomposition storms from duplicate emissions.

## 5. Compose Conventions
- One `@Composable` per named SDD component (`07_FOLDER_STRUCTURE.md`'s component lists) — don't inline a named component's implementation directly into its parent screen.
- State hoisting: a component receives state + lambdas, never reaches into a ViewModel or `SessionManager` directly — this is what keeps `presentation/components/` reusable and previewable.
- Mark stable data classes passed into Composables with `@Immutable`/`@Stable` (`05 §4`).
- `@Preview` for every reusable component in `presentation/components/`, with at least a light-content example — this project has no dedicated design tool handoff, so Compose Previews are the de facto visual QA surface during development.

## 6. Error Handling
- Expected gameplay outcomes (wrong answer, heart depletion, session-ends-with-0-stars) are modeled as sealed-class results returned from domain functions — not thrown exceptions.
- Genuine failures (missing asset, Vosk model failed to load, Room write failure) are caught at the repository/data boundary and surfaced to the ViewModel as an explicit error state, which the screen renders via a mascot-guided recovery UI (`04 §5`) — never an unhandled crash, never a raw system dialog on a child-facing screen.

## 7. Testing Conventions
See `12_TESTING_STRATEGY.md` for the full strategy; at the code level: every `domain/manager/` class ships with a corresponding JVM unit test class in `test/`, named `<Class>Test`, with one test method per rule/edge case enumerated in `01_REQUIREMENTS_SUMMARY.md §3`/`§5`.

## 8. Commit / PR Hygiene
- Reference the relevant `FR-XX` acceptance-criteria ID (`01 §6`) or `13_MASTER_TASKS.md` checklist item in every commit message touching gameplay logic.
- A PR that changes a number called out in `01 §7`'s conflict-resolution list (star thresholds, heart caps, touch-target sizes, etc.) must update this documentation package in the same PR — these docs are meant to stay the living source of truth, not a one-time handoff artifact.
