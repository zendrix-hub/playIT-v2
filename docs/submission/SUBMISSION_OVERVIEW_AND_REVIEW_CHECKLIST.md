# PlayIT — IT411 Submission Package: Overview and Review Checklist
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027
**Degree Program:** Bachelor of Science in Information Technology
**Institution:** College of Computer Studies, Cebu Institute of Technology – University
**Submission Date:** October 10, 2026
**Branch:** `refactor/hear-say-it`. Code validated at `eaf8634` (agy batch validation `c07206e`); package reviewed and rebuilt by Claude on October 10, 2026.

Rebuild everything with `python3 tools/docs/render_submission_package.py` (python-docx and Microsoft Word). The Markdown files in `docs/` are the sources; never edit the .docx or .pdf copies by hand.

---

## 1. Deliverables

| # | Deliverable | Files | Source | Contents |
|:---:|---|---|---|---|
| 1 | **MVP Validation Findings & Refactoring Priorities** (course form) | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` / `.docx` | `MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`, filled into the course template | Sections 1–7 and the declaration: 5 findings, 6 refactoring priorities, summary matrix, non-priority screening |
| 2 | **Software Design Description (SDD v2.0, rev. 2.5)** | `playIT_SDD_v2.0.pdf` / `.docx` | `SDD_v2.0_Refactored.md` | §2.0 implementation status; system ERD (§3.5.3), 6 class diagrams (§3.7) and 6 sequence diagrams (§4) |
| 3 | **Software Requirements Specification (SRS v3.0, rev. 3.5)** | `playIT_SRS_v3.0.pdf` / `.docx` | `SRS_v3.0_Refactored.md` | Use case diagram (§2.5), RTM v3.0 (16 findings) and §4.1 automated test coverage |
| 4 | **Software Project Management Plan (SPMP v2.0, rev. 2.3)** | `playIT_SPMP_v2.0.pdf` / `.docx` | `SPMP_v2.0_Refactored.md` | WBS, revised schedule with sprint status, risk register R1–R8 |

## 2. Technical evidence (agy validation, `df32c51`)

- **Unit tests:** 386 run, 0 failures, 6 skipped by design (font-scale checks on the 360x640 profile). 100% passing across all test suites.
- **Roborazzi screenshots:** 41 images: 9 layout checks on 4 sizes (360x640, 360x740, 411x891, 800x1280) plus 5 single-screen captures. Unclipped bounds verified; step dots stay above Next button on Samsung A21s viewports.
- **Debug APK:** `playit-debug-D-6933e3e.apk` (103 MB) and root `playit-debug.apk`. The manifest has no `INTERNET` permission, so the app cannot send data off the device (100% offline).
- **Pediatric compliance & accessibility:** On-screen sound captions dropped in child view per teacher feedback and pre-reader usability testing; visual mouth articulation cues (`ArticulationCue`) provide the accessibility support (NFR-ACC-01).
- **Zero-emoji policy:** `ZeroEmojiPolicyTest` passes across all app UI strings, buttons, and dialogs.

## 3. Before turning in (team)

- [ ] Confirm the adviser's name on the MVP form and the three cover pages (Mr. Joemarie C. Amparo, taken from the team's transmittal notes).
- [ ] Confirm the group name, section ("IT411 G1–G8") and member list on the MVP form.
- [ ] Sign the MVP form's Final Declaration (Team Lead) on the printed copy if the course requires a wet signature.
- [ ] Open each PDF and check that the table of contents, page numbers and landscape matrices (SRS RTM, SPMP risk register) and the landscape diagram pages look right.
- [ ] Optional: install the APK on the test phone (`adb install -r playit-debug-D-6933e3e.apk` or `adb install -r playit-debug.apk`).

## 4. Still pending after this submission (stated in the documents)

- Gate 3 teacher audit of every shipped clip, and the short-vowel clips (ElevenLabs, due Oct 11).
- Round 2 field validation (Weeks 7–8): ASR agreement, latency, hesitation, completion.
- Planned components: `LessonEngine`, `AudioComposer`, Room schema v4 with telemetry and CSV export, the input-level mic ripple.
