# IT411 Midterm Portal: Link Sheet (Group 56, PlayIT)

For the team lead. One row per portal field: the file to link, where it is in the Drive folder, and a blank for the Drive link. The field names follow the portal layout recorded in `docs/tasks/SESSION_HANDOFF.md` ("Directive for Claude: Midterm Submission Review, Challenge, and Portal Link Mapping").

## Before pasting any link

1. **Build the Drive folder from this commit.** `dist/` is not in Git, so a folder built before this commit is out of date. It lacks `README.pdf`, `FUNCTIONAL_TEST_RESULTS.pdf` and `INTEGRATION_SYSTEM_AND_REGRESSION_TEST_RESULTS.pdf`, and its Traceability Matrix still says "Page 16 of 20". From the repo root:
   ```
   gradlew :app:recordRoborazziDebug --tests "com.playit.app.screenshot.*"
   python tools/docs/assemble_midterm_submission.py
   python tools/docs/apply_signatures.py
   ```
   The first command makes the README screenshots. The second needs APK H (`playit-debug-H-0320385.apk`, SHA-256 `d301007f...937d`) in the repo root or in `Documents/playIT-apk/`; it stops if the file is missing or different. The third signs the acceptance report: the team lead runs it, nobody else. It leaves the already-signed STD as it is and puts the MVP form's signature back in the same place.
2. **Upload** the whole `dist/PlayIT_IT411_Midterm_Submission/` folder to Google Drive, keeping its three subfolders.
3. **Share** the uploaded master folder with the instructor, `rlaviste@gmail.com`, as **Viewer**. The files inside inherit it. If the portal is opened by other graders, also set General access to "Anyone with the link: Viewer".
4. **GitHub needs no invitation:** https://github.com/zendrix-hub/playIt is public. Do not add the instructor as a collaborator; that gives write access.
5. **Copy each link** in Drive: right-click the file or folder, Share, Copy link. Paste it below, then into the portal. Open every link in a private window to check that it opens without your account.

## Folder links

| Portal field | Folder | Drive link |
|---|---|---|
| 01_Software_and_Deployment | `01_Software_and_Deployment/` | |
| 02_Updated_Documentation | `02_Updated_Documentation/` | |
| 03_Testing_and_QA | `03_Testing_and_QA/` | |

## 01_Software_and_Deployment

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Link to Deployed System | `playit-debug-H-0320385.apk` | APK H, the build every document describes (108 MB; Drive cannot preview an APK, the grader downloads it). Install steps are in the deployment guide below. | |
| Link to GitHub Repository | https://github.com/zendrix-hub/playIt | Paste this URL itself. If the field accepts only a PDF, link `README.pdf`: it gives the repository URL on its first page. | (the URL) |
| Link to README (PDF) | `README.pdf` | The repository's README as a PDF: the app, the curriculum, build and test, how the repository relates to APK H, the AI disclosure and the ElevenLabs credit. | |
| Link to Deployment Evidence | `DEPLOYED_SYSTEM_AND_ACCESS_INSTRUCTIONS.pdf` | How to install and run APK H, its checksum, permissions and known limitations. `DEPLOYMENT_AND_PERMISSIONS_INSPECTION.txt` (same folder) is the APK inspection. Add the photos of APK H running on the A21s here once they exist (see "Gaps" below). | |
| Link to Refactoring Evidence | `CODE_REFACTORING_EVIDENCE.pdf` | The 6 MVP-form priorities, what APK H does for each, the STD result, and the 34 card commits. | |
| Link to Feature Completion Matrix | `FEATURE_COMPLETION_MATRIX.pdf` | Every requirement in STD section 3 with its test cases, result and status (complete, built with checks pending, open defect, planned). | |

## 02_Updated_Documentation

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Updated Software Requirements Specification | `playIT_SRS_v3.0.pdf` | SRS v3.0, rev. 3.8, with the use case diagram. | |
| Updated Software Design Description | `playIT_SDD_v2.0.pdf` | SDD v2.0, rev. 2.8, with 13 diagrams (ERD, 6 class, 6 sequence). | |
| Updated Software Project Management Plan | `playIT_SPMP_v2.0.pdf` | SPMP v2.0, rev. 2.6: WBS 1 to 9, schedule, risks R1 to R8. | |
| Updated Software Test Document | `playIT_STD_v1.0.pdf` | STD v1.0 (ISO/IEC/IEEE 29119-3): test plan, 83 test cases, execution record of the 433-test run, layout matrix, incidents, summary; team lead signature in section 8.4. | |
| Updated Requirements Traceability Matrix | `playIT_Traceability_Matrix.pdf` | SRS sections 4 and 5 as a standalone PDF (cover + 5 pages, footers "Page 1 of 5 (SRS p. 16)"). | |
| Requirements Inventory | `playIT_Requirements_Inventory.pdf` | SRS section 3 as a standalone PDF (cover + 7 pages). | |
| MVP Validation Findings and Refactoring Priorities | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` | The course form, signed by the team lead on page 23. | |

The DOCX copies in this folder are editable sources; link the PDFs.

## 03_Testing_and_QA

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Link to Functional Testing Results | `FUNCTIONAL_TEST_RESULTS.pdf` | All 83 STD test cases with input, expected result, actual result, evidence and status (57 pass, 1 partial, 2 fail, 23 not run, with reasons). | |
| Link to Integration, System, and Regression Testing Results | `INTEGRATION_SYSTEM_AND_REGRESSION_TEST_RESULTS.pdf` | The course's test types mapped to the STD levels; the 7 integration test classes; the 4-profile layout matrix, APK inspection and A21s sessions; the suite re-run at every card from 298 to 433 tests, a regression test for each closed incident, and a re-run after APK H. States what is not covered. | |
| Link to Performance Testing Report | `PERFORMANCE_TESTING_REPORT.pdf` | Runtime timing is not measured on this build (Round 2, Instrument D); the design rules that were verified; the APK size. | |
| Usability Testing Report | `USABILITY_TESTING_AND_IMPROVEMENTS_REPORT.pdf` | Round 1 (25 respondents, SUS 75.5, Smileyometer), findings #1 to #5 and the changes made, the A21s sessions; independent completion (TC-IND-03) is Round 2. | |
| Defect Log | `DEFECT_LOG_AND_RETEST_RECORD.pdf` | INC-01 to INC-07 (closed, with re-test evidence), DEF-01 and DEF-02 (open), OBS-01 to OBS-04. | |
| Acceptance testing (if the portal has a field) | `ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf` | APK H against SRS v3.0: results, assessment, acceptance decision, sign-off. | |
| Test execution logs (if asked) | `test_execution_evidence/` (folder) | The 66 raw JUnit XML files of the STD run, the APK inspection, the audio release check, and the regression re-run of October 18. | |

## Gaps a grader can see (not fixable by documents)

- **No evidence yet that APK H runs on a phone.** STD section 5.5's last row ("APK H acceptance") is still open. Install APK H on the A21s, play one letter from Hear It to Find It, and take 3 to 5 photos or a short screen recording. Put them in `01_Software_and_Deployment/DEPLOYMENT_EVIDENCE_A21s/`, and link that folder from the Deployment Evidence field as well. This is also the only device test of the submitted build.
- **Usability testing of the refactored app has not been done with users.** The report rests on Round 1 (the MVP) and the team's own A21s sessions; Weeks 8 to 9 of the course ask for usability test results. If even a few learners or teachers can try APK H before the deadline, record it honestly as a pilot; otherwise the report already says it is Round 2.
- **Not feature-complete:** telemetry and CSV export, spaced recall and session pacing are planned, not built (the feature matrix says so).
- **Integration testing is thin:** 7 classes; the ViewModel tests mock their collaborators, and the 4 instrumented UI test classes left from the MVP (`app/src/androidTest`) were not run, so nothing automated ran on a device (the integration report says so).
