# PlayIT — IT411 Submission Package: Overview and Review Checklist
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027
**Degree Program:** Bachelor of Science in Information Technology
**Institution:** College of Computer Studies, Cebu Institute of Technology – University
**Submission Date:** October 11, 2026
**Branch:** `refactor/hear-say-it`. Code at `6933e3e` (card 24b, debug APK D); package reviewed and rebuilt by Claude on October 11, 2026.

Rebuild everything with `python3 tools/docs/render_submission_package.py` (python-docx and Microsoft Word). The Markdown files in `docs/` are the sources; never edit the .docx or .pdf copies by hand.

---

## 1. Deliverables

| # | Deliverable | Files | Source | Contents |
|:---:|---|---|---|---|
| 1 | **MVP Validation Findings & Refactoring Priorities** (course form) | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` / `.docx` | `MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`, filled into the course template | Sections 1–7 and the declaration: 5 findings, 6 refactoring priorities, summary matrix, non-priority screening |
| 2 | **Software Design Description (SDD v2.0, rev. 2.6)** | `playIT_SDD_v2.0.pdf` / `.docx` | `SDD_v2.0_Refactored.md` | §2.0 implementation status; system ERD (§3.5.3), 6 class diagrams (§3.7) and 6 sequence diagrams (§4) |
| 3 | **Software Requirements Specification (SRS v3.0, rev. 3.6)** | `playIT_SRS_v3.0.pdf` / `.docx` | `SRS_v3.0_Refactored.md` | Use case diagram (§2.5), RTM v3.0 (16 findings) and §4.1 automated test coverage |
| 4 | **Software Project Management Plan (SPMP v2.0, rev. 2.4)** | `playIT_SPMP_v2.0.pdf` / `.docx` | `SPMP_v2.0_Refactored.md` | WBS, revised schedule with sprint status, risk register R1–R8 |

## 2. Technical evidence (code at `6933e3e`)

- **Unit tests:** 386 run, 0 failures, 6 skipped by design (font-scale checks on the 360x640 profile).
- **Roborazzi screenshots:** 11 layout checks on 4 sizes (360x640, 360x740, 411x891, 800x1280) plus single-screen captures. The layout checks use unclipped bounds, so content scrolled out of view fails them; Hear It's step dots stay above Next, and Say It's mouth cue sits beside the mic.
- **Debug APK:** `playit-debug-D-6933e3e.apk` (103 MB), tested on a Samsung Galaxy A21s. The manifest has no `INTERNET` permission, so the app cannot send data off the device.
- **Accessibility (NFR-ACC-01):** sound captions are not shown in the child view (team decision after the phone tests, 2026-10-10 and 2026-10-11; the adviser will be informed); the mouth-shape cue (`ArticulationCue`, 9 pictures) is the visual support.
- **Zero-emoji policy:** `ZeroEmojiPolicyTest` passes on the app's UI strings. (The course form's own priority options use colored-circle symbols; they are part of the template and left as issued.)

## 3. Before turning in (team)

- [ ] Confirm the adviser's name on the MVP form and the three cover pages (Mr. Joemarie C. Amparo, taken from the team's transmittal notes).
- [ ] Confirm the group name, section ("IT411 G1–G8") and member list on the MVP form.
- [ ] Sign the MVP form's Final Declaration (Team Lead) on the printed copy if the course requires a wet signature.
- [ ] Open each PDF and check that the table of contents, page numbers and landscape matrices (SRS RTM, SPMP risk register) and the landscape diagram pages look right.
- [ ] Optional: install the APK on the test phone (`adb install -r playit-debug-D-6933e3e.apk`).

## 4. Still pending after this submission (stated in the documents)

- Gate 3 teacher audit of every shipped clip, and the short-vowel clips (ElevenLabs, due Oct 11).
- Round 2 field validation (Weeks 7–8): ASR agreement, latency, hesitation, completion.
- Planned components: `LessonEngine`, `AudioComposer`, Room schema v4 with telemetry and CSV export, the input-level mic ripple.
