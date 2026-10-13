# PlayIT — IT411 Submission Package: Overview and Review Checklist
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027
**Degree Program:** Bachelor of Science in Information Technology
**Institution:** College of Computer Studies, Cebu Institute of Technology – University
**Submission Date:** October 14, 2026
**Branch:** `refactor/hear-say-it`. Code at `89604d7` (card 29b, debug APK G); package synchronized and rebuilt by Claude on October 13, 2026 (card 30).

Rebuild everything with `python3 tools/docs/render_submission_package.py` (python-docx and Microsoft Word). The Markdown files in `docs/` are the sources; never edit the .docx or .pdf copies by hand.

---

## 1. Deliverables

| # | Deliverable | Files | Source | Contents |
|:---:|---|---|---|---|
| 1 | **MVP Validation Findings & Refactoring Priorities** (course form) | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` / `.docx` | `MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`, filled into the course template | Sections 1–7 and the declaration (with a blank line for the Team Lead's signature): 5 findings, 6 refactoring priorities, summary matrix, non-priority screening |
| 2 | **Software Design Description (SDD v2.0, rev. 2.7)** | `playIT_SDD_v2.0.pdf` / `.docx` | `SDD_v2.0_Refactored.md` | §2.0 implementation status; system ERD (§3.5.3), 6 class diagrams (§3.7) and 6 sequence diagrams (§4) |
| 3 | **Software Requirements Specification (SRS v3.0, rev. 3.7)** | `playIT_SRS_v3.0.pdf` / `.docx` | `SRS_v3.0_Refactored.md` | Use case diagram (§2.5), RTM v3.0 (16 findings) and §4.1 automated test coverage |
| 4 | **Software Project Management Plan (SPMP v2.0, rev. 2.5)** | `playIT_SPMP_v2.0.pdf` / `.docx` | `SPMP_v2.0_Refactored.md` | WBS, revised schedule with sprint status, risk register R1–R8 |

## 2. Technical evidence (code at `89604d7`)

- **Unit tests:** 409 run, 0 failures, 6 skipped by design (font-scale checks on the 360x640 profile). Claude's own run on October 13 matches agy's.
- **Roborazzi screenshots:** 19 layout checks on each of 4 sizes (360x640, 360x740, 411x891, 800x1280) plus single-screen captures. The layout checks use unclipped bounds, so content scrolled out of view fails them; Hear It's step dots stay above Next, and Say It's mouth cue sits beside the mic.
- **Debug APK:** `playit-debug-G-89604d7.apk` (built by agy from `89604d7`; kept in `Documents/playIT-apk/`, not in Git). The manifest has no `INTERNET` permission, so the app cannot send data off the device.
- **Accessibility (NFR-ACC-01):** sound captions are not shown in the child view (team decision after the phone tests, 2026-10-10 and 2026-10-11); the mouth-shape cue (`ArticulationCue`, 9 pictures) is the visual support, beside the Say It mic from the second miss. Hear It shows the letter only (card 28c). The adviser still has to confirm both changes.
- **Audio sources:** Kokoro-82M and Chatterbox-Turbo, plus ElevenLabs Text to Speech (Free plan) for the Blend It words AM, TUB, YAM and the short /i/. The Parent Zone shows the credit the Free plan requires: "Some word voices made with ElevenLabs (elevenlabs.io)".
- **Zero-emoji policy:** `ZeroEmojiPolicyTest` passes on the app's UI strings. (The course form's own priority options use colored-circle symbols; they are part of the template and left as issued.)

## 3. Before turning in (team)

- [ ] Sign the MVP form's Final Declaration by hand (Team Lead): the form prints a blank signature line under the name.
- [ ] Confirm the group name, section ("IT411 G7") and member list on the MVP form. The adviser's name (Mr. Joemarie C. Amparo) and the date (October 14, 2026) were confirmed by the team on October 13.
- [ ] Open each PDF and check that the table of contents, page numbers and landscape matrices (SRS RTM, SPMP risk register) and the landscape diagram pages look right.
- [ ] Install APK G on the test phone (`adb install -r playit-debug-G-89604d7.apk`) and play one letter from Hear It to Find It.
- [ ] The faculty bundle holds only the 4 PDFs and 4 DOCX files from `docs/submission/` and `playit-debug-G-89604d7.apk` (team decision, 2026-10-13); no task cards or relay files.

## 4. Still pending after this submission (stated in the documents)

- Gate 3 teacher audit of every shipped clip, including the ElevenLabs clips; adviser confirmation of the **[proposed]** items.
- Chapter 1's short /a/, and the pictures for the Blend It words AM, TUB, YAM and ZIP.
- Round 2 field validation (Weeks 7–8): ASR agreement, latency, hesitation, completion.
- Planned components: `LessonEngine`, `AudioComposer`, Room schema v4 with telemetry and CSV export, the input-level mic ripple.
- Post-midterm UI and sound polish (card 31), such as the splash screen with Lily waving and calmer feedback sounds.
