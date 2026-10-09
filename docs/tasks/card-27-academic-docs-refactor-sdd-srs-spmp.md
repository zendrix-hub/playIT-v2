# Card 27: Academic Documentation Refactoring & Synchronization (SDD v2.0, SRS v3.0, SPMP v2.0)

Status: done

## Why
Per IT411 course guidelines (`midterm_goal/midterm_goal.md`) and the institutional deliverable `docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`, the empirical findings from the Weeks 1–2 MVP validation must be reflected across all three foundational academic engineering documents:
1. **SRS (Software Requirements Specification)**: Must accurately document the functional and non-functional requirements post-validation, including test traceability.
2. **SDD (Software Design Description)**: Must reflect the real architectural implementation that landed in the codebase (converting planned items in §2.0 to implemented components).
3. **SPMP (Software Project Management Plan)**: Must update milestone progress, WBS work packages, and evidence-to-implementation traceability through the Oct 10 sprint.

## Files
- Edit: `docs/SDD_v2.0_Refactored.md`
- Edit: `docs/SRS_v3.0_Refactored.md`
- Edit: `docs/SPMP_v2.0_Refactored.md`
- Edit: `docs/engineering-package/13_MASTER_TASKS.md`
- Edit: `docs/evidence-log.md`

## Changes Required

### 1. `docs/SDD_v2.0_Refactored.md` (Update to v2.2)
- **Section 2.0 Implementation Status Table**:
  - Update `MicStateVisualizer` / `MicStatus` / `MicButton`: change status from *Planned* to **Implemented** (`presentation/sayit/MicStatus.kt`, `MicButton.kt`, 4 dynamic states, tested in `MicStatusTest`).
  - Update `Modifier.pediatricTouchTarget()` / `Dimens`: change status from *Planned* to **Implemented** (`presentation/theme/Dimens.kt`, 64dp touch floor, 16sp text floor, tested in `DimensTest`).
  - Update `LessonScaffold`: mark **Implemented** (`presentation/components/LessonScaffold.kt`).
  - Update `MapLayout`: mark **Implemented** (`presentation/map/MapLayout.kt`, responsive rope trail, 16-char name fit).
  - Update Spoken Say It corrections: mark **Implemented** (`presentation/sayit/SayItViewModel.kt`, `fb_no_ah`, `fb_letter_name`, Kokoro clips).
  - Update Decodable Word Bank (Blend It): mark **Implemented** (`di/DatabaseModule.kt`, 5 non-decodable words replaced with AM, SUM, TUB, YAM, ZIP).
  - Update Purposeful Effects & Transitions: mark **Implemented** (`navigation/NavGraph.kt`, `FeedbackEffects.kt`, zero haptics).
  - Update Articulation / Captions: mark **Implemented** (`ArticulationGroup.kt`, `CaptionBubble.kt`, `ArticulationCue.kt`).
- **Section 3 & 4 Architectural Alignments**:
  - Synchronize component signatures and class references with actual implemented Kotlin classes in `presentation/` and `domain/`.
- **Revision History**:
  - Add Version 2.2 entry documenting the October 10 refactoring sprint implementation synchronization.

### 2. `docs/SRS_v3.0_Refactored.md` (Update to v3.2)
- **Requirements Traceability Matrix (Section 5 / RTM)**:
  - Verify every requirement updated in the sprint maps directly to passing automated test classes:
    * `FR-02`: `HearItViewModelTest`, `HearItSequenceBuilderTest`, `AudioResolverTest`.
    * `FR-03`: `MicStatusTest`, `SayItViewModelTest`, `SayItFeedbackCopyTest`.
    * `FR-05`: `FindItViewModelTest`, `FindItGridTest`, `LayoutMatrixTest`.
    * `FR-13`: `DatabaseSeedTest`, `BlendItCardLayoutTest`, `BlendItViewModelTest`.
    * `NFR-ASR-01`: `SpeechValidatorTest`, `SayItViewModelTest`.
    * `NFR-ACC-01`: `ArticulationGroupTest`, `CaptionTextTest`, `ArticulationCueTest`.
    * `NFR-ACC-02`: `DimensTest`, `GummyContainerLayoutTest`, `LayoutMatrixTest`.
- **Revision History**:
  - Add Version 3.2 entry recording verified automated test coverage for refactored requirements.

### 3. `docs/SPMP_v2.0_Refactored.md` (Update to v2.2)
- **Section 1.2 Interconnected Deliverables Traceability Baseline Table**:
  - Update all entries in the findings table to note completion of the refactored engineering components.
- **Section 2 & 3 Work Breakdown Structure (WBS) & Milestones**:
  - Mark Week 3 (Requirements & Design Refactoring) and the October 10 Refactoring Sprint as **Complete / Shipped**.
  - Cross-reference `docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md` and `13_MASTER_TASKS.md`.
- **Revision History**:
  - Add Version 2.2 entry reflecting closure of the 2-day refactoring sprint.

## Verification
- Run `python3 tools/dev/review_card.py 27` (or manual consistency review).
- Ensure all markdown links, table alignments, and academic headers conform to CIT-U IT411 formatting.
- Ensure Zero-Emoji Policy compliance in all three documents.

## Commit
`docs(academic): refactor and synchronize SDD v2.0, SRS v3.0, and SPMP v2.0 with sprint implementation`

Requirement: IT411 Midterm Deliverables, RTM v3.0
Tests run: docs consistency check
Decisions used: user directive 2026-10-09 to synchronize academic docs with codebase
