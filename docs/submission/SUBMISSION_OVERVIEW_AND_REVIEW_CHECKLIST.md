# PlayIT — IT411 Midterm Submission Package Overview & Review Checklist
**Course:** IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027  
**Degree Program:** Bachelor of Science in Information Technology  
**Institution:** College of Computer Studies, Cebu Institute of Technology – University  
**Submission Date:** October 10, 2026  
**Git Branch / Commit:** `refactor/hear-say-it` (`c07206e`)

---

## 1. Verified Deliverables Manifest

| # | Deliverable Title | Filename (PDF) | Filename (DOCX) | Status |
|:---:|---|---|---|:---:|
| 1 | **MVP Validation Findings & Refactoring Priorities** | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` | `.docx` | Complete (N=25 empirical data, SUS 75.5) |
| 2 | **Software Design Description (SDD v2.0)** | `playIT_SDD_v2.0.pdf` | `.docx` | Complete (§2.0 status synchronized with code) |
| 3 | **Software Requirements Specification (SRS v3.0)** | `playIT_SRS_v3.0.pdf` | `.docx` | Complete (RTM v3.0 mapped to 364 automated tests) |
| 4 | **Software Project Management Plan (SPMP v2.0)** | `playIT_SPMP_v2.0.pdf` | `.docx` | Complete (WBS & sprint milestones updated) |

---

## 2. Technical Verification Audit (Automated Evidence)

- **Unit Test Suite:** 364 tests run, 0 failures, 6 skipped by design (100% pass rate).
- **Roborazzi Screenshot Matrix:** 41 layout screenshots recorded and verified across compact (360x640), a21s (360x740), phone (411x891), and tablet (800x1280).
- **Assembled Release Binaries:** `playit-debug.apk` (102 MB, 100% offline, zero network egress).
- **Zero-Emoji Policy:** 100% compliant across UI strings, buttons, dialogs, and reports.

---

## 3. Pre-Submission Verification Checklist for Capstone Team

- [ ] Open `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` and verify all institutional sections (1–7) and reflections are populated.
- [ ] Open `playIT_SDD_v2.0.pdf` and verify Section 2.0 implementation status table matches the codebase.
- [ ] Open `playIT_SRS_v3.0.pdf` and verify Requirements Traceability Matrix (RTM).
- [ ] Open `playIT_SPMP_v2.0.pdf` and confirm Week 3–4 milestone sign-off.
- [ ] Confirm APK binary installs cleanly on test device (`adb install -r playit-debug.apk`).
