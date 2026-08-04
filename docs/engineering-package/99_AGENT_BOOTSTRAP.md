# 99 — Agent Bootstrap

**Read this file first, every time, including after any context reset.** It is the map to everything else in this package. If you remember nothing else about playIT across a session boundary, re-read this file and it will tell you where to find the rest.

## 1. What playIT Is (30-second version)

An offline-first Android app teaching English phonics to Grade 1 Filipino learners (ages 6–7) via the Marungko Approach: 28 letters, each taught through Hear It (audio) → Say It (offline speech recognition via Vosk) → Find It (picture discrimination), consolidated every 4 letters via a Blend It word-construction checkpoint. Hearts/stars/streaks gamify it; a Parent Dashboard reports progress, fully offline, with PDF export. Full detail: `00_PROJECT_SUMMARY.md`.

## 2. The One Thing to Internalize Before Writing Any Code

**Two of the twelve source documents supplied for this project don't describe playIT at all** (`Wireframes.md` is an unrelated enterprise SaaS spec; `UI_Research.md` partially mis-describes playIT as a pre-existing video player). Several others conflict with each other on specific numbers (letter counts, speech engine name, touch-target sizes, color/type choices). Every one of these has been resolved with a documented recommendation — **do not re-derive these from the raw source PDFs/markdown files; use this package's resolutions**, listed centrally in `01_REQUIREMENTS_SUMMARY.md §7` and `03_DESIGN_SYSTEM_SUMMARY.md §5`, and tracked as open sign-off items in `13_MASTER_TASKS.md`.

## 3. Technology Stack

Kotlin 1.9+, Jetpack Compose 1.5+/Material 3, MVVM + Clean Architecture, Hilt, Room 2.6+, Navigation Compose 2.7+, Coroutines/Flow, **Vosk 0.3.47** offline speech recognition (not PocketSphinx — see conflict resolution `01 §7.7`), Android `MediaPlayer`, Coil, Android `PdfDocument` (no third-party PDF lib), min SDK 26. Full table: `02_ARCHITECTURE_SUMMARY.md §8`.

## 4. Architecture (one paragraph)

Three-layer Clean Architecture: Presentation (12 Compose screens + ViewModels) → Domain (pure Kotlin managers + repository interfaces, zero Android imports) → Data (Room, Vosk, MediaPlayer, PdfDocument implementing those interfaces). `SessionManager.activeProfileId` scopes every read/write across all layers for multi-profile support. Full detail + class contracts: `02_ARCHITECTURE_SUMMARY.md`, `05_ENGINEERING_BLUEPRINT.md`.

## 5. Folder Structure

Literal package tree, asset folder layout, and file-per-component rules: `07_FOLDER_STRUCTURE.md`. Naming rules for every file type (audio, images, animations, DB, packages, Kotlin files, resources): `22_FILE_NAMING_CONVENTION.md`.

## 6. Database

12 Room tables (`Profile`, `Phoneme`, `LetterGroup`, `LetterGroupMember`, `LessonProgress`, `SayItAttempt`, `FindItAttempt`, `BlendItWord`, `BlendItAttempt`, `BlendItProgress`, `Achievement`, `ReportLog`), all cascade-scoped to `Profile`. Full schema, relationships, indices, seed-data requirements: `08_DATABASE_SPEC.md`.

## 7. Navigation

12 screens, route table, flow diagram, back-stack/gating rules (locked nodes aren't navigable, celebration screens clear the sublevel back-stack, the Parent Dashboard arithmetic gate is a dialog not a route): `09_NAVIGATION_SPEC.md`.

## 8. Build Order — Follow This Sequence

`06_IMPLEMENTATION_ROADMAP.md` defines 9 phases (0–8): Scaffolding → Profiles → **first full vertical slice on Letter M** (this is the highest-risk phase — real Vosk integration, do this before scaling) → scale to 28 letters → Blend It → Gamification → Parent Dashboard → Asset Integration → Hardening. **Live checklist with checkboxes**, including every open sign-off question: `13_MASTER_TASKS.md` — this is the file to update as you complete work, in place, across sessions.

## 9. Coding Conventions & Testing

Naming/style/architecture-discipline rules: `11_CODING_STANDARDS.md`. Full test pyramid (unit/instrumented/Compose-UI/manual QA script), mapped directly to the acceptance-criteria IDs below: `12_TESTING_STRATEGY.md`.

## 10. Requirements & Acceptance Criteria

Functional requirements by module, NFRs, business rules, validation rules, edge cases: `01_REQUIREMENTS_SUMMARY.md §1–§5`. Formal acceptance criteria (FR-01 through FR-14, one per testable requirement): `01_REQUIREMENTS_SUMMARY.md §6`. Every conflict between source documents and its resolution: `01_REQUIREMENTS_SUMMARY.md §7`.

## 11. Design System & UI Implementation

Colors, typography, spacing, touch targets, motion, mascot/feedback/sound rules, and the three flagged research conflicts (color palette, typography — **this one's pedagogically significant, read it** —, touch-target sizing): `03_DESIGN_SYSTEM_SUMMARY.md`. Concrete screen-by-screen Compose implementation notes, type-scale mapping, motion parameters: `10_UI_IMPLEMENTATION_GUIDE.md`.

## 12. Research Context

Personas, 10-stage user journey, competitive landscape, UX/UI principles, color/typography rationale, and a list of researched-but-out-of-scope ideas (teacher mode, spaced repetition, token economy — **do not build these without a scope-change sign-off**): `04_RESEARCH_SUMMARY.md`.

## 13. Asset Production Package

| Doc | Contents |
|---|---|
| `14_ASSET_MANIFEST.md` | Full inventory, every asset type, exact counts |
| `15_IMAGE_GENERATION_PROMPTS.md` | Templates + 28-letter data table for AI image generation |
| `16_ILLUSTRATION_STYLE_GUIDE.md` | Visual/shape language, consistency rules |
| `17_CHARACTER_DESIGN_GUIDE.md` | Mascot design (proposed name "Kuting" — confirm before lock) |
| `18_AUDIO_PRODUCTION_GUIDE.md` | Full audio pipeline spec |
| `19_AUDIO_SCRIPTS.md` | Every spoken line, scripted, production-ready |
| `20_ICON_GUIDE.md` | Icon inventory and rules |
| `21_ANIMATION_GUIDE.md` | Every animation, trigger, and timing spec |
| `22_FILE_NAMING_CONVENTION.md` | Naming rules for every asset type |

**Content gap, repeated because it matters:** letters `ng` and `ñ` (and draft `x`) have no approved English-phonics example content yet — every asset doc above flags them `PENDING SME REVIEW`. Do not invent pedagogical content for these to unblock a build; ship the pipeline so they slot in later, per `08_DATABASE_SPEC.md §5`.

## 14. Current Project Status

**This package is a design/specification handoff — zero application code exists yet.** Status as of this handoff: all 27 documents in this package (`00`–`22`, `99`) are complete and internally cross-referenced. `13_MASTER_TASKS.md`'s Phase 0 checklist has not been started. If you are resuming work and some code *does* exist, your first action should be to check `13_MASTER_TASKS.md` for which boxes are already checked before assuming any particular phase is the current one.

## 15. Priority Order (if you can only do one thing next)

1. Resolve or explicitly accept the Open Questions in `13_MASTER_TASKS.md` that block correctness decisions (heart-recovery cap, Blend It star thresholds, Blend It heart-depletion asymmetry) — these are cheap to decide now and expensive to silently guess wrong inside gameplay code later.
2. `06_IMPLEMENTATION_ROADMAP.md` Phase 0 (scaffolding) → Phase 1 (profiles) → Phase 2 (Letter M vertical slice).
3. Everything else follows the roadmap in order; asset production (`14`–`22`) can proceed in parallel with app-code phases 3–6 once Phase 2 proves the pattern.

## 16. Resuming After a Context Reset

1. Re-read this file.
2. Open `13_MASTER_TASKS.md` — this is the only file in the package meant to be edited as work progresses; it tells you exactly what's done.
3. Re-read `01_REQUIREMENTS_SUMMARY.md §7` and `03_DESIGN_SYSTEM_SUMMARY.md §5` before touching any gameplay-numeric or visual-identity code — these hold the conflict resolutions that are easy to accidentally re-litigate from a fresh read of the raw source PDFs.
4. Do not re-run analysis on the raw uploaded source documents (Proposal, SDD, Design System, the 8 research reports) — this package is the already-synthesized, already-conflict-resolved output of that analysis. Treat `00`–`22` as the source of truth going forward; the raw uploads are historical inputs, not a second opinion to reconcile against.
