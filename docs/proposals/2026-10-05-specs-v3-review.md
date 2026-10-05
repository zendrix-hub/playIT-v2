# Review of the Week 3 specifications (SRS v3.0, SDD v2.0, SPMP v2.0), 2026-10-05

Claude's review of commit `dcff981` (`docs/SRS_v3.0_Refactored.md`, `docs/SDD_v2.0_Refactored.md`, `docs/SPMP_v2.0_Refactored.md`), for the Monday sync. The checks were:
- the AGENTS.md decisions;
- `docs/specs/hear-say-refactor.md`, `docs/spikes/vosk-foil-spike.md`, and 01 §7 / 03 §5;
- the code at `e864efd`.

No document was changed. Each item says what the document says, what the source says, and the evidence.

## Blocking (the document contradicts a decision or the code)
1. **Say It scoring.** SDD §3.3.1 and §4.2 Branch A, and SRS FR-03, score a pure-sound grammar (`<target_m>`).
   - Decision (AGENTS.md): Say It is hybrid. The echo step uses word mode ("mouse"), and the recall check uses the pure sound only if the Vosk spike passes.
   - The spike found that Vosk cannot confirm a pure sound. `SpeechValidator.SOUND_MODE_ENABLED = false` (vosk-foil-spike.md:25-33).
   - The fixed `confidenceThreshold 0.65` has no source. The spec says to tune the threshold and keep it in config (§6.2, §6.4).
2. **Room schema "v3".** The code is already at `version = 3` (`PlayItDatabase.kt:43`), so the new entities need v4. `exportSchema = false` (line 44) also blocks the `MigrationTestHelper` test in SDD §6.
3. **The SDD revision history lists work that is not built.**
   - "Replaced SpeechService with an AudioRecord loop": `VoskRecognizer.kt:25` still uses `SpeechService`.
   - `ProfileEntity` already exists, with fields `profileId`, `name` and `avatarResId`. The SDD gives it `id`, `displayName` and `avatarResName`, and its foreign keys point to `id`.
   - None of these exist yet: SayItJudge, LearnerModel, AudioComposer, LessonEngine, LetterProgress, TelemetryEvent, pediatricTouchTarget, ArticulationCue, MicStateVisualizer, CsvExportManager.
   - TutorPolicy exists, but as a stateless function in `domain/manager/`, not as the FSM in a `tutoring/` layer that the SDD describes.
   - Fix: write these as "planned", not "added".
4. **"Approved" headers on items that are still open.** These are [proposed] in the spec with no decision recorded:
   - the 15 s sequence limit;
   - the short-sound lengths;
   - recall of at least 70%;
   - the mic opening by itself;
   - the 12-minute session;
   - the review intervals.

   The Chatterbox /m/ amendment to NFR-AUD-01 also waits for the adviser's confirmation, but SRS NFR-AUD-01 states it as settled. SPMP Milestone 1 says "Completed/Submitted".

## Should fix
5. **Audio gates (SRS NFR-AUD-01.3, SPMP §7.2).** Spec Table 6 differs on three points:
   - Gate 2 is a blind listening screen, not a "Vosk technical verification".
   - Gate 3 needs at least 3 of 4 teachers. SRS HI-1 says 3 of 4, but NFR-AUD-01 says only "≥3 teachers".
   - "SNR ≥25 dB" and "≤800 ms" have no source.
6. **Manifest path.** The release manifest lives in `docs/audio-release/<date>/manifest.json`. The SDD AudioComposer reads a `docs/` path at runtime, which the app cannot do. The spec puts the runtime manifest in assets (§6.5).
7. **Asset layout (SDD §3.1.1).** The SDD names `res/raw/ph_*.wav` and `kw_m_mouse.wav`. The real layout is `assets/audio/keywords/kw_<word>.wav`. SPMP line 35 uses a third name.
8. **Touch targets (SRS NFR-ACC-02).** The SRS asks for 64 dp everywhere. 03 §5.3 sets 64 dp for child screens and 48/56 dp for adult screens.
9. **Hearts (SRS FR-07, FR-13).**
   - FR-07 limits heart recovery to Find It. Blend It also recovers hearts (01 §1 Module 5, card 11).
   - FR-13 drops three Blend It rules: no restart and 0 stars, the hint auto-lock, and the 5-word session.
10. **Telemetry (FR-NEW-TEL).**
    - Missing fields from spec §6.7: mode (ECHO/RECALL) and prompt level.
    - Missing events: `instruction_replay`, `demo_shown`, `lead_done`, `recall_result`.
11. **Onboarding (SRS FR-14, SDD ProfileEntity).** The avatar-only decision is missing: the name is optional, and a parent adds it later (card 07b). `displayName` is non-null.
12. **Chapter letters (SDD §3.4, SRS FR-13).** Both docs list Ch2 o,b,u,t and Ch3 k,l,y,n. The code seeds Ch2 o,b,e,u; Ch3 t,k,l,y; Ch4 n,g,p (`DatabaseModule.kt:105-111`).
13. **Test IDs differ between the documents.**
    - TC-AUD-01 is a 15 s sequence check in the SRS and SoundPool latency in the SDD.
    - TC-DB-01 is a crash test in the SRS and a migration suite in the SDD.
    - TC-SAY-01..04 are missing from the RTM.
    - The TC-REV, TC-IND, TC-BLD, TC-ACC, TC-PERF and TC-SES families are missing from the SDD test plan.

## Minor
14. **Versions.** The docs say Kotlin 1.9.22 and Compose 1.5.4. The code uses Kotlin 1.9.23 and Compose BOM 2024.05.00.
15. **Voice.** The docs say "Kokoro-82M / Bella". The released voice is the mix `af_heart*0.7 + af_bella*0.3`.
16. **Sound classes (FR-02.1).** It calls h, j, q, w, x, y "stops", and it leaves out the x ≤350 ms limit from spec Table 4.
17. **Find It and stars.**
    - FR-04 says a 3×2 grid, which has 6 cells, but the grid shows 5 items.
    - FR-06 doesn't say what happens below 50% accuracy. The code gives 1 star.
18. **NG in Chapter 8.** "NG deferred to Chapter 8" has no decision behind it. 13_MASTER_TASKS only excludes NG and Ñ.
19. **Emoji (Zero-Emoji Policy).** SPMP line 36 and SRS line 319 use 🙂/😐. Write "smiling/neutral faces" instead.
20. **Dates and counts.**
    - The implementation weeks differ: "Weeks 4-7", "Weeks 1-6", and a Weeks 3-9 schedule.
    - The dry run is N=5 in one place and 3-5 in another.
21. **Build-time gate (SPMP §7.1).** The SPMP says the build bundles only assets that passed the gates. `app/build.gradle.kts` has no such check; today the review step and `review_card.py` enforce the gates.

## Suggested next step
Fix 1-4 before the documents go to the adviser: they describe the product differently from what the code and the decisions say. Items 5-13 can be fixed in one editing pass. Items 14-21 are cleanup.
