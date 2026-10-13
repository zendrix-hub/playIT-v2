# Card 30: IT411 Midterm Submission Package Synchronization (SDD v2.4, SRS v3.4, SPMP v2.4)

Status: ready
Type: docs
Auditor: Claude (WSL)

## Why
The academic submission package in `docs/submission/` was compiled on October 11 at commit `6933e3e` (Card 24b / APK D, 386 tests). Since then, the codebase advanced through Cards 28, 28b, 28c, 28d, 15b, 09c, and 29b to commit `89604d7` (APK G, 409 passing tests).
Submitting without synchronizing would present stale design claims to the panel and adviser (e.g., claiming mouth cues are on Hear It when they were removed in Card 28c, or citing Kokoro-only audio when ElevenLabs is now used and credited).

## Discrepancies to Audit & Synchronize

### 1. SDD v2.0 (`docs/SDD_v2.0_Refactored.md`) -> Bump to v2.4
- **Hear It vs. Say It Mouth Cues (§2.0 line 85, §3.6.2 line 453, §4.1 line 504)**:
  - *Current doc says*: "Hear It shows the cue beside the play button".
  - *Actual code (Card 28c)*: Mouth articulation cues removed completely from Hear It to avoid pediatric split-attention during auditory modeling. Play button is centered. `ArticulationCue` is reserved exclusively for Say It expressive scaffolding (attempt 2 "Watch my lips").
- **Captions (§2.0 line 85, §3.6.2 line 453)**:
  - Re-confirm captions are excluded from child view per user decision 2026-10-11, with visual articulation cues on Say It fulfilling the visual support role for hearing difficulties (NFR-ACC-01).
- **Audio Subsystem (§3.1)**:
  - Note ElevenLabs Free Tier source for CVC words AM, TUB, YAM and short vowel /i/, and document the "elevenlabs.io" attribution in Parent Zone (`VoiceCredit.kt`).
- **Mascot & Avatars (§3.6)**:
  - Note redrawn 7 Lily poses and 6 companion animal avatars from release `2026-10-12-mascot` (Card 29b).
- **Test Baseline**: Update from 386 tests to 409 tests (0 failures, 6 skipped by design).

### 2. SRS v3.0 (`docs/SRS_v3.0_Refactored.md`) -> Bump to v3.4
- **NFR-ACC-01 (Accessibility - Visual Support)**:
  - Align text: mouth-shape cue on Say It attempt 2 provides visual phonetic articulation support; pre-reader sound captions removed from child UI.
- **Section 4.1 Automated Test Suite**:
  - Update suite metric to 409 passing unit tests across all 7 functional requirement categories.
- **FR-13 (Decodable Words)**:
  - Confirm AM, TUB, YAM approved audio clips and their runtime attribution credit.

### 3. SPMP v2.0 (`docs/SPMP_v2.0_Refactored.md`) -> Bump to v2.4
- **WBS & Milestone Closure**:
  - Update October 10–13 refactoring sprint status to 100% complete across all cards (Cards 00 through 29b).
  - Note APK G build (`playit-debug-G-89604d7.apk`) as the midterm submission testing artifact.
- **Risk Register**:
  - Update R1 (Synthetic voice naturalness) with ElevenLabs hybrid fallback for difficult vowels and words.

### 4. Course Form (`docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md`)
- Verify Team Lead signature, date (October 14, 2026), and adviser confirmation details.

### 5. Pedagogical UI & Architecture Audit: "Minimalist Duolingo ABC" Benchmark
The user has requested an explicit, rigorous challenge of the app's visual design and user experience before tomorrow's submission:
- **Design Target**: "Duolingo ABC, but a minimal version" — chunky, tactile 3D gummy elements (`Squircle`, `GummyButton`), joyful saturated accents, but stripped of sensory overload, clutter, or visual noise.
- **Audit Checklist for Claude**:
  1. **Map Screen (`MapScreen.kt`)**: Does the winding rope trail and chapter progression feel tactile and intuitive like Duolingo ABC without visual crowding on compact screens (360x740dp)?
  2. **Hear It Screen (`HearItScreen.kt`)**: Is the auditory modeling uncluttered? With mouth cues moved out (Card 28c), does the centered play button feel prominent, calm, and purposeful?
  3. **Say It Screen (`SayItScreen.kt`)**: Does the 4-state mic visualizer and gentle feedback banner feel supportive and game-like rather than testing-oriented?
  4. **Find It Screen (`FindItScreen.kt`)**: Is the 2x2 grid spacing balanced, with clear tactile card feedback on selection?
  5. **Blend It Screen (`BlendItScreen.kt`)**: Does the sequential 1 -> 2 -> 3 letter sound-out card provide clear visual focus as each phoneme is spoken?
  6. **Pediatric Compliance**: Verify 64dp touch target floor, 16sp/18sp text floor, zero emojis across all screens, and zero harsh red `#FF0000`.
- **Submission Go / No-Go Verdict**:
  - Claude must explicitly state whether APK G meets the threshold for tomorrow's midterm submission.
  - If any non-blocking visual polish remains, Claude will catalog them into a prioritized post-midterm polish card rather than risking regressions tonight.

## Agreed Submission Metadata (User Decision 2026-10-13)
- **Capstone Adviser:** Mr. Joemarie C. Amparo
- **Submission Date Header:** October 14, 2026
- **Signature:** Leave Team Lead final declaration signature line blank for manual sign-off on the submitted copy.
- **Submission Deliverable Scope:** The ZIP/package for faculty submission contains ONLY the 4 PDFs + 4 DOCXs from `docs/submission/`, plus `playit-debug-G-89604d7.apk`. No internal markdown task cards or relay files are included in the student submission bundle.
- **Repository Cleanup Strategy:** See `docs/tasks/REPO_CLEANUP_AND_DEFENSE_GUIDE.md` for the full post-refactor archival plan prior to thesis defense.

## How to Review and Re-render

1. **Review Diff**: Claude reads the Markdown changes against the codebase in `domain/` and `presentation/`.
2. **Re-render Submission Package**:
   ```bash
   python3 tools/docs/render_submission_package.py
   ```
   *Prerequisites*: Windows environment with MS Word installed (or WSL calling PowerShell Word COM).
3. **Verify Output Deliverables in `docs/submission/`**:
   - `playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf` & `.docx`
   - `playIT_SDD_v2.0.pdf` & `.docx`
   - `playIT_SRS_v3.0.pdf` & `.docx`
   - `playIT_SPMP_v2.0.pdf` & `.docx`
4. **Inspect PDFs**: Check landscape matrix formatting (SRS RTM, SPMP risk register) and page numbering.
