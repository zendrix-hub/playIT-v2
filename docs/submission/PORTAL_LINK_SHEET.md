# IT411 Midterm Portal: Link Sheet (Group 56, PlayIT)

For the team lead. One row per portal field: the file to link, where it is in the Drive folder, and a blank for the Drive link. The field names follow the portal layout recorded in `docs/tasks/SESSION_HANDOFF.md` ("Directive for Claude: Midterm Submission Review, Challenge, and Portal Link Mapping").

## Before pasting any link

1. **Build the Drive folder from this commit.** `dist/` is not in Git, so a folder built before this commit is out of date. It lacks `README.pdf`, `FUNCTIONAL_TEST_RESULTS.pdf` and `INTEGRATION_SYSTEM_AND_REGRESSION_TEST_RESULTS.pdf`, and its Traceability Matrix still says "Page 16 of 20". From the repo root:
   ```
   gradlew :app:recordRoborazziDebug --tests "com.playit.app.screenshot.*"
   python tools/docs/assemble_midterm_submission.py
   python tools/docs/apply_signatures.py
   ```
   The first command makes the README screenshots. The second needs APK H (`playit-debug-H-0320385.apk`, SHA-256 `b5a54c18...42d4`) in the repo root or in `Documents/playIT-apk/`; it stops if the file is missing or different. The third signs the acceptance report: the team lead runs it, nobody else. It leaves the already-signed STD and MVP form as they are.
2. **Upload** the whole `dist/PlayIT_IT411_Midterm_Submission/` folder to Google Drive, keeping its three subfolders.
3. **Share** the uploaded master folder with the course instructor, Mr. Ralph P. Laviste (`rlaviste@gmail.com`), as **Viewer**. The files inside inherit it. If the portal is opened by other graders, also set General access to "Anyone with the link: Viewer".
4. **GitHub needs no invitation:** https://github.com/zendrix-hub/playIt is public. Do not add the instructor as a collaborator; that gives write access.
5. **Copy each link** in Drive: right-click the file or folder, Share, Copy link. Paste it below, then into the portal. Open every link in a private window to check that it opens without your account.

## Folder links

| Portal field | Folder | Drive link |
|---|---|---|
| 01_Software_and_Deployment | `01_Software_and_Deployment/` | https://drive.google.com/drive/folders/19y2PNJLLmOChneSHfq82soiTdurSNkPY?usp=sharing |
| 02_Updated_Documentation | `02_Updated_Documentation/` | https://drive.google.com/drive/folders/1D-0J_3_nOet_kDw30mjC0NcYp_u4xamg?usp=sharing |
| 03_Testing_and_QA | `03_Testing_and_QA/` | https://drive.google.com/drive/folders/1MEXSlitap358FMSiD8rWhqxs6EZt6fSG?usp=drive_link |

## 01_Software_and_Deployment

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Link to Deployed System | `playit-debug-H-0320385.apk` | APK H, the build every document describes: 108,317,310 bytes, SHA-256 `b5a54c18...42d4`. Drive cannot preview an APK or scan a file over 100 MB, so the grader clicks "Download anyway". Install steps are in the deployment guide below. | https://drive.google.com/file/d/1SeIXJS1z2YJwvh5mfNbVmIP9cExbRvkI/view?usp=sharing |
| Link to GitHub Repository | https://github.com/zendrix-hub/PlayIT | Public academic GitHub repository (or link `README.pdf` if a PDF link is required). | https://github.com/zendrix-hub/PlayIT |
| Link to README (PDF) | `README.pdf` | The repository's README as a PDF: the app, the curriculum, build and test, how the repository relates to APK H, the AI disclosure and the ElevenLabs credit. | https://drive.google.com/file/d/13yW2TLtzZQYhlksqZ3Xjh-M_xJjMM99o/view?usp=sharing |
| Link to Deployment Evidence | `DEPLOYED_SYSTEM_AND_ACCESS_INSTRUCTIONS.pdf` | How to install and run APK H, its checksum, permissions and known limitations. | https://drive.google.com/file/d/1hyae8ibXs7kEwuxZSpgeWBIgBlWgxwcQ/view?usp=sharing |
| Link to Refactoring Evidence | `CODE_REFACTORING_EVIDENCE.pdf` | The 6 MVP-form priorities, what APK H does for each, the STD result, the 34 card commits, and the link to the development pull request (https://github.com/zendrix-hub/playIT-v2/pull/2). | https://drive.google.com/file/d/104coSrKXUXeLXYImqFK2jK1PgXiHk-Oq/view?usp=sharing |
| Link to Feature Completion Matrix | `FEATURE_COMPLETION_MATRIX.pdf` | Every requirement in STD section 3 with its test cases, result and status (complete, built with checks pending, open defect, planned). | https://drive.google.com/file/d/1Nxvdq5413DWgVFjj1xXXoA2AsAwp8hws/view?usp=sharing |

## 02_Updated_Documentation

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Updated Software Requirements Specification | `playIT_SRS_v3.0.pdf` | SRS v3.0, rev. 3.8, with the use case diagram. | https://drive.google.com/file/d/1CES28KKpoirBaTg4QRFfOuFK1JgeNKm9/view?usp=sharing |
| Updated Software Design Description | `playIT_SDD_v2.0.pdf` | SDD v2.0, rev. 2.8, with 13 diagrams (ERD, 6 class, 6 sequence). | https://drive.google.com/file/d/1YlNbgI8W9HyclNRjA8vaYR9-pQbcOTfr/view?usp=sharing |
| Updated Software Project Management Plan | `playIT_SPMP_v2.0.pdf` | SPMP v2.0, rev. 2.6: WBS 1 to 9, schedule, risks R1 to R8. | https://drive.google.com/file/d/1uydxU1-p9EPdNh8C8vBqTfWSMBrkihGD/view?usp=sharing |
| Updated Software Test Document | `playIT_STD_v1.0.pdf` | STD v1.0 (ISO/IEC/IEEE 29119-3): test plan, 83 test cases, execution record of the 433-test run, layout matrix, incidents, summary; team lead signature in section 8.4. | https://drive.google.com/file/d/1iKCt8GnXDOyluabKS3xOUZke3UfY2gq4/view?usp=sharing |
| Updated Requirements Traceability Matrix | `playIT_Traceability_Matrix.pdf` | SRS sections 4 and 5 as a standalone PDF (cover + 5 pages, footers "Page 1 of 5 (SRS p. 16)"). | https://drive.google.com/file/d/1qgqHXBDeIyfGc61UvS5mxWoS1i8BN2_v/view?usp=sharing |
| Requirements Inventory | `playIT_Requirements_Inventory.pdf` | SRS section 3 as a standalone PDF (cover + 7 pages). | https://drive.google.com/file/d/1V8iLgULH39656W3kr-ROXQjPtsoVn3bH/view?usp=sharing |
| MVP Validation Findings and Refactoring Priorities | `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` | The course form, signed by the team lead on page 23. | https://drive.google.com/file/d/1lyeNBdyaAy86ECDJG5BjV9SClz3YzQbs/view?usp=sharing |

The DOCX copies in this folder are editable sources; link the PDFs.

## 03_Testing_and_QA

| Portal field | File | What it is | Drive link |
|---|---|---|---|
| Link to Functional Testing Results | `FUNCTIONAL_TEST_RESULTS.pdf` | All 83 STD test cases with input, expected result, actual result, evidence and status (57 pass, 1 partial, 2 fail, 23 not run, with reasons). | https://drive.google.com/file/d/1RqPFfm_qMtcvtX89d0msvQoecmBX9aAj/view?usp=sharing |
| Link to Integration, System, and Regression Testing Results | `INTEGRATION_SYSTEM_AND_REGRESSION_TEST_RESULTS.pdf` | The course's test types mapped to the STD levels; the 7 integration test classes; the 4-profile layout matrix, APK inspection and A21s sessions; the suite re-run at every card from 298 to 433 tests, a regression test for each closed incident, and a re-run after APK H. States what is not covered. | https://drive.google.com/file/d/19lm4eWZqUaLGXIJDXaYI6sU6EpCkO6WA/view?usp=sharing |
| Link to Performance Testing Report | `PERFORMANCE_TESTING_REPORT.pdf` | Runtime timing is not measured on this build (Round 2, Instrument D); the design rules that were verified; the APK size. | https://drive.google.com/file/d/1MltLK621ffioXTGTpYiJiNnJBVDTkIrQ/view?usp=sharing |
| Usability Testing Report | `USABILITY_TESTING_AND_IMPROVEMENTS_REPORT.pdf` | Round 1 (25 respondents, SUS 75.5, Smileyometer), findings #1 to #5 and the changes made, the A21s sessions; independent completion (TC-IND-03) is Round 2. | https://drive.google.com/file/d/1cj7rwZ_Y6uhT2ulv5AEJb90PV90XihD1/view?usp=sharing |
| Defect Log | `DEFECT_LOG_AND_RETEST_RECORD.pdf` | INC-01 to INC-07 (closed, with re-test evidence), DEF-01 and DEF-02 (open), OBS-01 to OBS-04. | https://drive.google.com/file/d/1gQOnsrHzGpF3mFLEWVH9ePRMG7vmk1nR/view?usp=sharing |
| Acceptance testing (if the portal has a field) | `ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf` | APK H against SRS v3.0: results, assessment, acceptance decision, sign-off. | https://drive.google.com/file/d/1svNXwBcypMXiwOCmImUo9-0Wl95-BMFR/view?usp=sharing |
| Test execution logs (if asked) | `test_execution_evidence/` (folder) | The 66 raw JUnit XML files of the STD run, the APK inspection, the audio release check, and the regression re-run of October 18. | https://drive.google.com/drive/folders/13Lul4mIeoaANY1anqPSvkwbSQ46PeRHO?usp=sharing |

## Gaps a grader can see (not fixable by documents)

- **The phone check has no photos.** STD section 5.5 records the team's acceptance check of the submitted APK on the A21s (October 18, pass), but nothing in the package shows it. If the team has photos or a screen recording, put them in `01_Software_and_Deployment/DEPLOYMENT_EVIDENCE_A21s/` and link that folder from the Deployment Evidence field as well. It is the only device test of the submitted build.
- **No runtime performance figure.** The course directive asks for the workload, response times, throughput, error rate and other metrics, with actual results; the performance report measures only the APK size. This needs a measured run of APK H on the A21s.
- **Usability:** the course directive puts final user and stakeholder validation after the midterm, so Round 1 results with the changes made answer this item. The refactored app has not yet been tried by users.
- **Not feature-complete:** telemetry and CSV export, spaced recall and session pacing are planned, not built (the feature matrix says so).
- **Integration testing is thin:** 7 classes; the ViewModel tests mock their collaborators, and the 4 instrumented UI test classes left from the MVP (`app/src/androidTest`) were not run, so nothing automated ran on a device (the integration report says so).
