# Card 30: IT411 Midterm Submission Package Synchronization (SDD v2.4, SRS v3.4, SPMP v2.4)

Status: done
Type: docs
Auditor: Claude (Windows PC)

## Outcome (Claude, 2026-10-13)
- **Baseline checked:** Claude's own run at `89604d7` gives 409 tests, 0 failures, 6 skipped by design, the same as agy's. Every shipped file of releases `2026-10-11-words`, `2026-10-12-i` and `2026-10-12-mascot` matches its manifest SHA-256.
- **Revision numbers:** the card's "v2.4 / v3.4 / v2.4" are already used: SDD rev. 2.4 and SRS rev. 3.4 were card 28, and SPMP rev. 2.4 was the October 11 issue. This revision is therefore SDD rev. 2.7, SRS rev. 3.7 and SPMP rev. 2.5. The file names keep the document versions (v2.0, v3.0, v2.0).
- **Synchronized:** sections 1 to 4 below, plus the four diagrams that showed the Hear It mouth cue:
  - Figure 2 and Figure 8 (Hear It class and sequence diagrams) lose it.
  - Figure 3 (Say It class diagram) gains `ArticulationCue`.
  - SRS Figure 1 moves the mouth-cue use case to Say It.
- **Course form:** the declaration prints a blank signature line under the Team Lead's name (`fill_mvp_validation_form.py`). The date is October 14, 2026.
- **Adviser:** Mr. Joemarie C. Amparo on all four documents.
- **Package:** re-rendered with `tools/docs/render_submission_package.py`: 4 DOCX and 4 PDF, all 14 figures at full resolution, plus the checklist.
- **Audit (section 5) and SFX (section 6):** the findings and the plan are in `card-31-post-midterm-polish.md`. Verdict: **GO** for the October 14 submission with APK G; no finding blocks it.

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
  1. **Intro / Splash Screen (`SplashScreen.kt`)**: The opening moment upon launching the app. Currently draws an older Headspace canvas arch (`HeadspaceTarsierCanvas`); must be modernized to feature Lily the tarsier mascot in her welcoming pose (`lily_waving.png`), clean Duolingo ABC typography, and an inviting 64dp `GummyButton` with tactile tap feedback.
  2. **Map Screen (`MapScreen.kt`)**: Does the winding rope trail and chapter progression feel tactile and intuitive like Duolingo ABC without visual crowding on compact screens (360x740dp)?
  3. **Hear It Screen (`HearItScreen.kt`)**: Is the auditory modeling uncluttered? With mouth cues moved out (Card 28c), does the centered play button feel prominent, calm, and purposeful?
  4. **Say It Screen (`SayItScreen.kt`)**: Does the 4-state mic visualizer and gentle feedback banner feel supportive and game-like rather than testing-oriented?
  5. **Find It Screen (`FindItScreen.kt`)**: Is the 2x2 grid spacing balanced, with clear tactile card feedback on selection?
  6. **Blend It Screen (`BlendItScreen.kt`)**: Does the sequential 1 -> 2 -> 3 letter sound-out card provide clear visual focus as each phoneme is spoken?
  7. **Pediatric Compliance**: Verify 64dp touch target floor, 16sp/18sp text floor, zero emojis across all screens, and zero harsh red `#FF0000`.
- **Submission Go / No-Go Verdict**:
  - Claude must explicitly state whether APK G meets the threshold for tomorrow's midterm submission.
  - If any non-blocking visual polish remains, Claude will catalog them into a prioritized post-midterm polish card rather than risking regressions tonight.

### 6. Tactile Sound Effects (SFX) Strategy: The Duolingo ABC Acoustic Kit
- **Core Directive**: Eliminate harsh or jarring sounds and establish joyful, effective audio feedback.
- **Immediate Replacements**:
  - Purge `sfx_blendit_buzz.mp3`: Replace with a gentle bubble/wooden "nudge" so children never experience game-show alarm buzzer feedback.
  - Upgrade core feedback tones: Adopt a cohesive, high-polish acoustic foley set (soft wooden tile pop for taps, joyful marimba/xylophone chords for correct answers, gentle sparkle chimes for unlocks, and celebratory fanfare).
- **Audio Gate & Provenance**:
  - Source replacement SFX from verified CC0 / Public Domain acoustic game UI packs (e.g., Kenney Interface Sounds).
  - Package through `docs/audio-release/` with standard SHA-256 manifest to guarantee academic licensing compliance.

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
