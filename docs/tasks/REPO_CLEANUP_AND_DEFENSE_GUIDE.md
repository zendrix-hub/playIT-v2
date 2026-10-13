# PlayIT — Repository Cleanup and Defense Packaging Guide

**Target Milestone:** Capstone 2 Final Defense & Production Release  
**Status:** Planning Guide (Do NOT execute deletions until final development is complete)  
**Branch Strategy:** Development on `refactor/hear-say-it` -> Final PR merge to `main` -> Tagged release for defense.

---

## 1. Overview & Core Philosophy

During the fast-paced agile refactoring phase (Weeks 1–6), the repository accumulated extensive agentic task cards, relay review scripts, temporary review pages, audio/image batches, and diagnostic logs. 

Before the final defense and external repository presentation to the Capstone panel:
1. **The code repository must look professional, clean, and publication-ready.**
2. **The evaluation package given to the panel must contain only official deliverables.**
3. **Internal agent scratchpads must not clutter the academic codebase.**

> [!IMPORTANT]
> **Timing Guardrail**: No files should be deleted during active refactoring. Deletions and archival must only take place **after** all refactor cards are accepted and merged into `main`, and all unit tests pass green on CI.

---

## 2. Classification of Repository Files

### A. Critical Files — NEVER Delete

These files constitute the actual product, academic deliverables, build systems, and legal compliance artifacts:

| Category | Paths / Patterns | Rationale |
|---|---|---|
| **App Source Code** | `app/src/main/java/**`, `app/src/test/**` | The core pediatric learning application and automated test suites. |
| **Android Resources** | `app/src/main/res/**`, `app/src/main/AndroidManifest.xml` | Themes, strings, drawables, manifests. |
| **Shipped Production Assets** | `app/src/main/assets/**` | Production phoneme WAVs, word audio, mouth PNGs, character/mascot art. |
| **Academic Deliverables** | `docs/submission/**` | Official PDFs and DOCXs (SRS v3.4, SDD v2.4, SPMP v2.4, MVP form). |
| **Formal Specifications** | `docs/SRS_v3.0_Refactored.md`, `docs/SDD_v2.0_Refactored.md`, `docs/SPMP_v2.0_Refactored.md` | Single source of truth for academic requirements, architecture, and project management. |
| **Engineering Baseline** | `docs/engineering-package/**` (00 to 12) | Core requirements, pedagogical rationale, design tokens, data dictionary. |
| **Legal & Licensing** | `LICENSE`, `NOTICE`, `docs/audio-release/**/manifest.json`, `docs/image-release/**/manifest.json` | Provenance tracking, Apache-2.0 licenses, ElevenLabs non-commercial attribution records. |
| **Build & CI Automation** | `build.gradle.kts`, `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/**`, `.github/workflows/**` | Ensures anyone cloning can run `./gradlew assembleDebug` and pass CI out-of-the-box. |
| **Primary Project Docs** | `README.md` | Clean, user-facing project presentation and installation instructions. |

---

### B. Internal Agent & Relay Files — Candidates for Post-Sprint Archival / Removal

These files were generated solely for Claude-agy pair programming, incremental verification, and temporary coordination:

| Item | Current Location | Post-Refactor Action | Why It Can Be Removed / Archived |
|---|---|---|---|
| **Task Cards** | `docs/tasks/card-*.md` | Move to `docs/archive/tasks/` or prune | Cards exist for session execution; formal requirements live in SRS/SDD. |
| **Session Handoffs** | `docs/tasks/SESSION_HANDOFF.md`, `SPRINT_OCT10_ORCHESTRATION.md` | Archive | Relay communication bridge between Claude and agy; superseded once sprint ends. |
| **One-Off Spikes** | `docs/spikes/**` | Archive to `docs/archive/spikes/` | Investigative exploratory experiments (e.g., Vosk foil spike). |
| **Day-Specific Notes** | `docs/tasks/TONIGHT_*.md`, `PHONE_TEST_*.md`, `GROUP_TEST_*.md` | Remove or archive | Nightly scratch checklists; consolidated into `docs/evidence-log.md`. |
| **Briefs & Prompts** | `docs/assets/briefs/**` | Archive or prune | Asset prompts are compiled into `15_IMAGE_GENERATION_PROMPTS.md`. |
| **Interim Artifacts** | Root `.apk` files (`playit-debug.apk`), scratch scripts in tools | Remove from Git | Binaries should not bloat Git tree; official release APK belongs in GitHub Releases. |

---

## 3. The Do's and Don'ts of Repository Cleanup

### DO:
1. **DO tag or branch before cleaning:** Create an explicit Git tag (`v1.0-refactor-history`) or archive branch (`archive/hear-say-refactor-history`) before deleting any task files, so 100% of the granular agent development history is permanently preserved in Git.
2. **DO verify CI passes after cleanup:** Run `./gradlew testDebugUnitTest` and check GitHub Actions CI after removing files to ensure no test or script accidentally depended on a deleted document.
3. **DO replace the root README with an executive presentation:** Ensure the root `README.md` introduces PlayIT clearly to faculty and panelists: features, pediatric pedagogy, tech stack, screenshots, and instructions to build and run.
4. **DO keep release manifests intact:** Always retain `docs/audio-release/` and `docs/image-release/` manifests; they prove asset licenses and teacher audit compliance.
5. **DO provide a self-contained submission folder:** For faculty evaluation, supply a dedicated folder or ZIP containing solely the 4 formal deliverables (`.pdf` and `.docx`), the final signed MVP form, and the production debug APK.

### DON'T:
1. **DON'T delete files while cards are actively in progress:** Never delete `docs/tasks/` while development is ongoing; `review_card.py` and the agent runbook rely on them to validate commits.
2. **DON'T commit large APK binaries directly to the repository:** Keep APKs in Git-ignored release directories (`Documents/playIT-apk/` or GitHub Releases) to avoid ballooning repo clone size for panelists.
3. **DON'T delete the evidence log prematurely:** Keep `docs/evidence-log.md`; it is vital evidence for your thesis defense showing systematic test-driven validation.
4. **DON'T leave emojis or unformatted scratch files in public view:** Ensure all outward-facing documents comply with the strict Zero-Emoji Policy and academic typography standards.
5. **DON'T delete attribution files:** Never remove `VoiceCredit.kt` or Parent Zone attribution components; doing so violates third-party license terms.

---

## 4. Final Defense Preparation Checklist (To Run Before Defense)

When all coding is complete and the team prepares for final thesis submission and defense:

- [ ] **Step 1: Code Freeze & Full Suite Verification**
  - Run `./gradlew testDebugUnitTest` (all tests green).
  - Verify zero unresolved compiler or linter warnings.
- [ ] **Step 2: Create Archive Tag**
  - `git tag -a v1.0-refactor-complete -m "Complete agentic refactor history with all task cards"`
  - `git push origin v1.0-refactor-complete`
- [ ] **Step 3: Document Archival / Relocation**
  - Consolidate active tasks into `docs/archive/tasks/`.
  - Remove obsolete nightly scratch files (`TONIGHT_*.md`).
  - Keep `docs/submission/` pristine with the final rendered academic PDFs.
- [ ] **Step 4: Polish Root README**
  - Update root `README.md` with system architecture diagrams, screenshot gallery, and build guide.
- [ ] **Step 5: Generate Final Release APK**
  - Build signed release or clean debug APK (`playit-release-v1.0.apk`).
  - Verify installation on Samsung Galaxy A21s physical hardware.
- [ ] **Step 6: Defense Deliverable Package Assembly**
  - Create the defense ZIP package:
    - `1_playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf`
    - `2_playIT_SDD_v2.4.pdf`
    - `3_playIT_SRS_v3.4.pdf`
    - `4_playIT_SPMP_v2.4.pdf`
    - `playit-defense-release.apk`
