# 00 — Project Summary

> Read this first. It orients you (the implementation agent) to what playIT is, who it's for, and the hard boundaries of the build. Details live in `01`–`04`; engineering specifics live in `05`–`13`; asset specs live in `14`–`22`. If anything here conflicts with a later document, the later, more specific document wins **except** where this document explicitly flags an unresolved conflict — those are called out below and must not be silently resolved by you.

## 1. Product Vision

playIT is an **offline-first Android educational app** that teaches English phonics to Filipino Grade 1 learners (ages 6–7) using the **Marungko Approach** — a phono-syllabic reading method that sequences letters by sound frequency rather than alphabetical order. The app replaces/supplements paper-based home practice with a gamified, speech-interactive companion that a child can use independently, with a parent checking in periodically via an offline dashboard.

The pedagogical bet is specific and testable: **decoding automaticity** (recognizing a letter's sound instantly, producing it correctly, and discriminating it from similar sounds) must be over-practiced before a child can progress to blending sounds into words. Every mechanic in this app — the 3-sublevel structure, the strict unlock gating, the heart penalty, the star rating — exists to enforce that repetition without feeling punitive.

## 2. Objectives (from the Proposal / SRS)

1. Deliver **100% accurate, native-speaker phoneme audio** for all 28 Marungko letters ("Hear It").
2. Provide **offline speech recognition** with immediate binary (correct/incorrect) feedback on a child's pronunciation ("Say It"), at a calibrated ≥75% baseline detection accuracy.
3. Reinforce sound discrimination through a **5-image picture grid** with 3 correct/2 distractor images ("Find It").
4. Consolidate letters into **whole-word construction** once a group of 4 letters is mastered ("Blend It").
5. Track mastery and motivate continued practice via a **heart/star/streak gamification layer**, enforced by strict sequential unlocking.
6. Give parents/guardians a **zero-setup, fully offline progress dashboard** with exportable PDF reports and at-risk-letter flags.

## 3. Scope

**In scope (build this):**
- Android app, Kotlin + Jetpack Compose + Material 3, MVVM + Clean Architecture, min SDK 26 (Android 8.0).
- 28-letter Marungko sequence, each with Hear It → Say It → Find It, strictly gated.
- 7 letter groups (4 letters each) each ending in a Blend It word-construction checkpoint.
- Room/SQLite local persistence for all progress, telemetry, and profiles — no network calls, ever, after install.
- Vosk offline speech recognition, bundled English small model.
- Up to 6 local child profiles per device, independently tracked.
- Parent/Guardian dashboard: per-letter accuracy/attempts/hearts-lost/time/retention, color-coded risk flags, PDF export via Android's built-in `PdfDocument` API.
- Hearts (penalty/recovery), stars (1–3 per letter and per Blend It session), streak badges (5/10/15/20 days).

**Explicitly out of scope (do not build without a scope-change sign-off):**
- iOS, web, or any cloud sync — the system is 100% offline post-installation.
- Teacher-facing dashboards, classroom/multi-student aggregation, or DepEd LMS integration.
- CVC decoding lessons, reading comprehension, grammar instruction, multi-syllabic words beyond the defined Blend It word banks.
- Any adaptive-difficulty / spaced-repetition algorithm — the pass/fail and distractor logic are **deterministic and fully specified** in `01_REQUIREMENTS_SUMMARY.md`; do not add hidden difficulty scaling.
- In-app purchases, ads, accounts/logins, or any token/currency economy.

See `04_RESEARCH_SUMMARY.md §7` for a longer list of researched-but-out-of-scope ideas (teacher mode, spaced repetition, token economy, push notifications) that surfaced in the research documents but are **not** authorized by the SRS/Proposal — do not implement them.

## 4. Users

| User | Role | Key need |
|---|---|---|
| **Grade 1 learner** (6–7 yo, primary user) | Plays the lessons | Large touch targets, zero-reading-required navigation, immediate multisensory feedback, forgiving error recovery |
| **Parent/Guardian** (secondary user) | Supervises practice, checks dashboard | Zero setup, no login, plain-language progress signals, exportable proof for school |
| *(Teacher — referenced in research only, not an in-scope user; see conflict note below)* | — | — |

## 5. Technology Stack (authoritative)

| Layer | Choice | Notes |
|---|---|---|
| Language | Kotlin 1.9+ | |
| UI | Jetpack Compose 1.5+, Material 3 | Full details in `03` / `10` |
| Architecture | MVVM + Clean Architecture (Presentation → Domain → Data) | See `02` |
| DI | Hilt | |
| Local DB | Room 2.6+ over SQLite | See `08` |
| Navigation | Navigation Compose 2.7+ | See `09` |
| Async | Kotlin Coroutines + StateFlow | |
| Speech recognition | **Vosk** (offline), target `0.3.47`, floor `0.3.45`, bundled small English model | **Not PocketSphinx** — see conflict #1 below |
| Audio playback | Android `MediaPlayer` | |
| Image loading | Coil 2.5+ | |
| PDF export | Android `PdfDocument` API (no third-party lib) | |
| Min SDK | API 26 (Android 8.0) | |

## 6. Constraints

- **Fully offline after install.** One internet touch is allowed: downloading the APK and the bundled Vosk model at first install. Nothing else may call the network.
- **Child-hardware realities.** Budget Android tablets/phones (as low as 2GB RAM, Android 8/9, quad-core ~1.3GHz) are common in the target deployment context. Build and test with these as the performance floor, not the ceiling.
- **Speech recognition is imperfect.** Vosk's baseline detection accuracy target is ≥75% for children's/accented voices — this is a system engineering target, distinct from the ≥80% *pedagogical* mastery threshold used for star ratings. Do not conflate the two (see `01`).
- **No accounts, no login, no CAPTCHA-style gates anywhere.** Destructive actions (reset progress, delete profile) are gated by a simple arithmetic problem instead — a UX pattern, not authentication.

## 7. How This Package Was Built — Read This Before Trusting Any Single Source Document

Twelve source documents were supplied: the Proposal, SRS v2.0, SDD v1.0, Design System, and eight research reports (Personas, Journey, UX, UI, Wireframes, Competitive Analysis, Color Palette, Typography). They **do not fully agree with each other**, and two of them contain content that does not describe this project at all. Every downstream document in this package resolves these conflicts explicitly rather than silently picking one source. The two data-quality issues below are the most important to understand before reading anything else:

- **`Wireframes.md` is not usable.** It is a complete, self-consistent low-fidelity wireframe spec for a generic enterprise SaaS product (auth screens, "Data Analyst"/"Executive Manager" personas, billing settings, analytics data grids). It contains **zero** references to playIT, Marungko, Grade 1, or any of the five learning modules. Treat it as a mismatched upload. The real screen structure comes from the SDD's inline mockup descriptions (§3, Modules 1–6) plus `10_UI_IMPLEMENTATION_GUIDE.md`.
- **`UI_Research.md` mis-describes the starting point.** It repeatedly frames "playIT" as an *existing* video player/downloader app (floating pop-ups, file management, ad banners) being "pivoted" into an education app — this is almost certainly contamination from an unrelated commercial app that happens to share the name. This project is a **greenfield build**; there is no legacy codebase, no video playback, and no ads to remove. The document's general Material 3 Expressive / pediatric HCI research (touch-target sizing by age, spring-motion parameters, contrast tables) is still valid and is incorporated into `03` and `10` — only the "pivot from a media player" framing is discarded.

All other conflicts (speech engine name, letter-count discrepancies, color/type/touch-target numbers, screen counts, etc.) are itemized with recommended resolutions in `01_REQUIREMENTS_SUMMARY.md §7`, `02_ARCHITECTURE_SUMMARY.md §5`, and `03_DESIGN_SYSTEM_SUMMARY.md §5`. **Do not average or split the difference between conflicting numbers** — each conflict has one recommended resolution; follow it, and if a task forces a genuinely new decision, add it to `13_MASTER_TASKS.md` as an open question rather than guessing silently.

## 8. Document Map

| Phase | Docs |
|---|---|
| 1 — Understanding | `01_REQUIREMENTS_SUMMARY`, `02_ARCHITECTURE_SUMMARY`, `03_DESIGN_SYSTEM_SUMMARY`, `04_RESEARCH_SUMMARY` |
| 2 — Engineering | `05_ENGINEERING_BLUEPRINT` → `13_MASTER_TASKS` |
| 3 — Assets | `14_ASSET_MANIFEST` → `22_FILE_NAMING_CONVENTION` |
| 4 — Bootstrap | `99_AGENT_BOOTSTRAP` — your single re-entry point after any context reset |
