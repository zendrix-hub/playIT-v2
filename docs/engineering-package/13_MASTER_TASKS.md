# 13 — Master Tasks

Single checklist to work from. Check items off in place (`[x]`) as you go — this file is meant to be edited, unlike the rest of the package. Organized by `06_IMPLEMENTATION_ROADMAP.md` phase, plus a standing "Open Questions" section that must reach zero unchecked items before final ship.

## Open Questions — Require a One-Line Stakeholder Answer Before Ship

These are not blockers to *starting* implementation (each has a documented interim resolution elsewhere in this package), but each needs an explicit sign-off before release, since they involve a judgment call this package made on the requirements owner's behalf:

- [x] **Blend It heart depletion asymmetry** — confirm intentional that Blend It has no restart-with-3-hearts, unlike every other module (`01_REQUIREMENTS_SUMMARY.md §7.3`). (Resolved: Standard 3-heart restarts enabled upon depletion)
- [x] **Blend It star thresholds** — confirm or revise the draft rule (`01 §7.4`) before it ships as the real `BlendItStarThresholds` constant. (Resolved: Draft 3★/2★/1★ thresholds confirmed and implemented)
- [x] **Heart recovery cap** — confirm the recommended cap-at-starting-pool rule (`01 §7.5`). (Resolved: Capped at starting pool size)
- [x] **Parent Dashboard arithmetic gate** — confirm adding this at dashboard entry (not just destructive actions) is desired (`01 §7.6`); update the SRS if so. (Resolved: Placed strictly at dashboard entry point)
- [x] **Multi-profile support formalization** — SDD-only feature with no SRS FR entry; confirm scope and add FR-14 to a future SRS revision (`01 §7.2`). (Resolved: FR-14 entry formalized covering up to 6 profiles, avatar picker, and isolated profile progress)
- [x] **`ng` and `ñ` phonics content** — needs a reading-curriculum SME to supply approved English-phonics example words/audio scripts before these two letters can leave `PENDING_SME_REVIEW` status (`01 §5`, `08 §5`, `14`, `15`, `19`). (Resolved: Flagged pending SME review; placeholder content handled safely without user-facing regression)
- [x] **Curriculum Scope: 28-Letter DepEd Sequence vs. 26-Letter English Phonics (`Ñ` and `NG`)** — Confirmed and resolved: Exclude `NG` and `Ñ` for English phonics curriculum alignment and Vosk speech recognition accuracy; implement 26 standard letters (A–Z) across 7 chapters (4, 4, 4, 3, 4, 3, 4 letters) with 33 decodable CVC words.
- [x] **Blend It Group 1 word bank** — only 3 solid words (`SAM`, `SIS`, `AIM`) are constructible from `m,s,a,i`; confirm whether a 5-word session is required for every group or whether Group 1 is an accepted exception (`01 §5`, `19_AUDIO_SCRIPTS.md`). (Resolved: Group 1 restricted to 3 words)
- [x] **APK size budget / first-launch privacy notice** — both are recommended additions not in the SRS (`01 §2`); confirm before treating them as requirements. The privacy-notice copy specifically should be drafted with legal input, not engineering guesswork. (Resolved: Enforce <150MB APK size budget; omit first-launch privacy notice pending legal input)
- [x] **Typography switch to Lexend/Andika** — this is a visual-identity change from the shipped Design System (`03 §5.2`); confirm before implementation, since it affects every child-facing screen. (Resolved: Lexend/Andika font family switch implemented globally)
- [x] **Color palette WCAG validation pass** — confirm the shipped hex values pass 4.5:1/3.0:1 contrast before visual QA sign-off (`03 §5.1`); if any pairing fails, decide whether to patch the shipped palette or adopt values from the `Color_Palette.md` research. (Resolved: WCAG contrast check completed and color palette tuned for 4.5:1 / 3.0:1 compliance)
- [x] **Reduced-motion Settings entry point** — there is currently no Settings screen anywhere in the 12-screen inventory to host a manual toggle; confirm whether system-level detection alone is sufficient for v1, or whether a minimal Settings surface needs to be added to scope (`03 §6`, `10 §6`). (Resolved: System-level detection via `Settings.Global.TRANSITION_ANIMATION_SCALE` / `ANIMATOR_DURATION_SCALE` implemented without creating a new Settings screen)
- [x] **MapScreen Back Navigation Destination** — decide what action/destination occurs when tapping a Back button on `MapScreen` (e.g., return to `ProfileSelectScreen` to switch player, show Exit confirmation dialog, or minimize app). (Resolved: Return to `ProfileSelectScreen` with session cleared to enable player switching)

## Phase 0 — Scaffolding
- [x] Android project init, Gradle deps per `02_ARCHITECTURE_SUMMARY.md §8`
- [x] Hilt application class + empty DI modules
- [x] `PlayItDatabase` skeleton
- [x] Empty NavGraph + `SplashScreen`


## Phase 1 — Profiles
- [x] `Profile` entity/DAO/repository
- [x] `SessionManager`
- [x] `ProfileSelectScreen`, `NamePromptScreen`, `AvatarPicker` (placeholder assets acceptable)
- [x] 6-profile cap enforced


## Phase 2 — First Vertical Slice (Letter M, end to end)
- [x] `Phoneme`/`LessonProgress` entities + repos
- [x] `UnlockManager`, `MapViewModel`, minimal `MapScreen`
- [x] `HearItScreen` with real `AudioPlayer`
- [x] `SayItScreen` with real `VoskRecognizer`/`AudioCapture`/`SpeechValidator`/`HeartManager`
- [x] `FindItScreen` with real `GridGenerator`, exercising the Letter-1 fallback-distractor case
- [x] `LetterCompleteScreen` with `StarCalculator`


## Phase 3 — Scale to 28 Letters
- [x] Seed all 28 `Phoneme` rows (flag `ng`/`ñ` per Open Questions)
- [x] `LetterGroup`/`LetterGroupMember` seed data, 7×4
- [x] Full `MapScreen` winding path, 28 nodes


## Phase 4 — Blend It
- [x] `BlendItWord`/`BlendItProgress`/`BlendItAttempt` entities + repos
- [x] `BlendItWordSelector`, `GroupUnlockManager` wired
- [x] `BlendItScreen` (tap-only tile placement), hint-after-2-wrong, standard 3-heart restart
- [x] `BlendItCompleteScreen` with `BlendItStarThresholds`


## Phase 5 — Gamification
- [x] `StreakTracker`, milestone badges, `Achievement` table
- [x] Heart-recovery cap implemented

## Phase 6 — Parent Dashboard
- [x] Aggregation queries (`LetterStatusCalculator`, `RetentionCalculator`)
- [x] `ParentDashboardScreen` + arithmetic gate + profile switcher
- [x] `ReportGenerator` + `PdfExporter` + `ReportPreviewScreen`

## Phase 7 — Asset & UI Polish
- [x] All placeholder audio replaced (`18`, `19`)
- [x] All placeholder illustrations/icons replaced (26/26 letter cards, 26/26 picture cards, 33/33 blend word scenes, 6/6 profile avatars, 13/13 map props, 4/4 reward badges completed with 100% transparent RGBA backgrounds & anchor style sheet matching; `ng`/`ñ` flagged pending SME review)
- [x] Align FindIt screen layout, mascot dock, and card proportions with HearIt and SayIt
- [x] Mascot interactive tap responses and dynamic emotional state expressions across all sublevels
- [x] Animations implemented per `21_ANIMATION_GUIDE.md`
- [x] `22_FILE_NAMING_CONVENTION.md` compliance verified across all asset folders

## Phase 8 — Hardening
- [x] Performance pass on 2GB-RAM/API-26 profile
- [x] Full `12_TESTING_STRATEGY.md` suite green
- [x] Accessibility pass (`03 §6`, `10 §6`)
- [x] All Open Questions above resolved

## Phase 9 — Post-Audit Improvements (added 2026-08-18)

### W1 — Backend Hardening
- [x] Seed BlendIt words for Groups 4–7 (20 words, IDs 14–33 in `DatabaseModule.kt`)
- [x] Replace hardcoded "M" fallbacks with `_loadError` StateFlows in `HearItViewModel`, `SayItViewModel`, `FindItViewModel`
- [x] Guard empty audio/image paths in `AudioPlayer.playAssetAudio()` (already had blank-path early return)
- [x] Add `try-catch` error handling to all repository implementations (9 repos hardened)
- [x] Add ViewModel unit tests (`HearItViewModelTest`, `SayItViewModelTest`, `FindItViewModelTest` — 76 tests green)
- [x] Clean up empty `SpeechModule.kt` (deleted — Vosk uses `@Inject` directly)

### W2 — UI/UX Cohesion
- [x] Create `GummyDialog` component (`presentation/components/GummyDialog.kt`)
- [x] Create `GummyTextField` component (`presentation/components/GummyTextField.kt`)
- [x] Create `ErrorStateContent` component (`presentation/components/ErrorStateContent.kt`)
- [x] Add `DestructiveRedShadow`, `StreakFire`, `StreakFireShadow`, `BadgePurple`, `BadgePurpleShadow` to `Color.kt`
- [x] Upgrade `ArithmeticGuardDialog` from stock `AlertDialog` to `GummyDialog` + `GummyTextField`
- [x] Upgrade `NamePromptScreen` from `OutlinedTextField` to `GummyTextField`
- [x] Replace hardcoded hex colors in `TopStatsBar` with `StreakFire` / `BadgePurple` semantic tokens
- [x] Gate SayItScreen QA bypass (`simulateCorrectForTesting`) behind `BuildConfig.DEBUG`
- [x] Clean up stale `MascotBubble` comment in `PediatricComponents.kt`
- [x] Hoist ArithmeticGuard state to ViewModel in `ProfileSelectScreen`
- [x] Extract magic DP offsets in `MapScreen` mascot positioning to named constants
- [x] Replace `Toast` with `Snackbar` in `ParentDashboardScreen`

### W3 — Experience Layer
- [x] Add audio feedback to Profile & Dashboard screens
- [x] Create `GummyLoader` loading transition component
- [x] Add Compose-native celebration animations (`CelebrationOverlay.kt` — confetti, star burst, sparkle)
- [x] Add progress timeline on Map (`X of 28 letters learned`)
- [x] Consolidate `PediatricButton` into `GummyButton` (auto-shadow mapping, all usages migrated, wrapper deleted)

## Phase 10 — UI Redesign (from `playit-mockup.html`)

Reference mockup: `playit-mockup.html` (root of workspace). Full analysis: conversation `fce7ee58-32ff-4abb-aaf1-e4163555c70e`, artifact `mockup_v2_analysis.md`.

### Open Decisions (all resolved)
- [x] **Font choice**: Keep **Lexend + Andika** (single-story a/g, pedagogically correct for early readers). Do not adopt mockup's Baloo 2 / Nunito.
- [x] **Error color**: Stick to **"no red" rule**. Replace mockup's `kalamansi #FF5A5F` with a warm orange/coral alternative that stays clearly non-red (e.g. Gentle Correction Orange `#FFB74D` or similar warm hue).
- [x] **Outline retention**: **Keep outlines** — do not drop `DarkBrownOutline` borders. Adapt new palette colors while preserving the outlined gummy depth style.
- [x] **Mascot name**: Primary name stays **Lily**. Retain **Tiko** as an alternative for possible future rebranding.
- [x] **Cultural map theming**: Approved — proceed with **partial cultural theming** now (Chocolate Hills, palm trees, nipa hut decorations on MapScreen). Polish and expand later as assets become available.
- [x] **Zero-Emoji Policy**: Confirmed — emojis are strictly not needed and prohibited in UI text, button labels, speech bubbles, and headers across all screens. All visual icons use Material Icons or transparent PNG assets.

### T1 — Foundation (color + typography + depth system)
- [x] Update `Color.kt` with Filipino-themed palette: mango, ube, guava, leaf, kalamansi (warm orange, NOT red), tan, rope, ink, sand, sky (+ depth tokens at -20% luminance)
- [x] Update `Type.kt` with Lexend + Andika font families (bundle TTF files in `res/font/`; 5 Lexend weights + 1 Andika regular)
- [x] Update gummy depth system: keep two-layer face+depth stack AND DarkBrownOutline outlines (per stakeholder decision); expand `toShadow()` with all Filipino palette tokens
- [x] Update `TextPrimary` from `#2D3748` → ink `#1F3A3D` (10.2:1); `TextSecondary` from `#4A5568` → ink-soft `#506B6E` (WCAG-tuned to 5.6:1)

### T2 — Map Screen
- [x] Sky-to-sand gradient background with Chocolate Hills (rounded shapes), clouds, sun, cultural deco (palm, hut, flowers)
- [x] Frosted-glass top bar (`alpha 0.85 white + blur`)
- [x] Stat pills: Streak (mango bg) + Stars (ube-light bg)
- [x] Trail: SVG/Canvas dashed path in `rope` color
- [x] Node styles: complete (leaf gradient + checkmark + stars), current (mango gradient + ring pulse), locked (ink-faint + lock)
- [x] Blend It badge: rattan weave pattern pill
- [x] Bilingual Marungko Group Chapter Milestone Banners (`Pangkat X • Group X`, letters summary, completion status chip)
- [x] Smooth auto-scroll to active lesson node on screen open
- [x] Dynamic left/right companion mascot positioning with anchored interactive mini dialogue speech bubble (`Tara na! • Let's go!`)
- [x] Active node pulsing focus aura ring + tactile locked node shake/wobble animation on tap

### T3 — Hear It Screen
- [x] Sand-colored letter card with picture card illustration asset (breathing anim), big letter (ube-dark), word + phoneme labels
- [x] Ube gradient play button (88dp) with expanding ring pulse on tap
- [x] Replay dots (5 dots, fill on each play)
- [x] Mango gradient Next CTA (disabled until played, 18dp radius)

### T4 — Say It Screen
- [x] Hearts row (kalamansi/guava colored, heartpop anim on loss, gray when lost)
- [x] White prompt card with phoneme instruction + large letter (24sp reading floor)
- [x] Guava mic button (88dp) with dynamic audio-amplitude pulse ring while listening (180dp bounds)
- [x] Waveform bars (5 bars, guava, animated while listening)
- [x] Feedback card: leaf (correct) / kalamansi (retry), slide-in
- [x] Attempt tracker dots (green Leaf ✓ / warm Kalamansi retry dot — strictly non-red, no harsh X marks)
- [x] Noise pill indicator
- [x] 64dp bottom Next CTA button floor

### T5 — Find It Screen
- [x] Score badge (ube-light pill, 24sp reading floor)
- [x] Audio replay pill (constrained to 64dp min touch target, 24sp reading floor)
- [x] Picture cards: 22dp radius, 5dp shadow, 24sp reading floor, Leaf squash-pop / Kalamansi warm retry shake
- [x] Confetti particles on completion (theme colors: ube, mango, guava, leaf)
- [x] 64dp bottom Next CTA button floor

### T6 — Complete Screen
- [x] Full ube→ube-dark gradient background
- [x] Stardrop animation (bounce-in stars with staggered delay)
- [x] Stats pill (semi-transparent white, 24sp reading floor)
- [x] White continue button with 5dp depth (64dp button floor)
- [x] Confetti particle celebration with Mango, Leaf, Ube, Guava, Cloud palette

### T7 — UI Polish & Clean Aesthetics (Zero Emoji + Anti-Overlap Button Architecture)
- [x] Zero-emoji policy implemented across all screens, dialogs, buttons, pills, and speech bubbles
- [x] Replaced emoji visuals in `LetterCard` with real transparent picture card illustration assets (`images/pictures/picture_<word>.png` calibrated to 72.7% ratio)
- [x] Pinned non-overlapping bottom action bars with `Modifier.navigationBarsPadding().padding(horizontal = 24.dp, vertical = 12.dp)` across all screens (`HearIt`, `SayIt`, `FindIt`, `LetterComplete`, `BlendIt`, `BlendItComplete`, `NamePrompt`)
- [x] Replaced all emoji glyphs with clean Material Icons (`Icons.Filled.*` / `Icons.AutoMirrored.*`) and transparent PNG reward painters in `TopStatsBar`, `MapScreen`, `ProfileCard`, `OverallStatsCard`, `LetterPerformanceTable`, `BlendItSummaryCard`, and `ArithmeticGuardDialog`
- [x] BlendIt word challenge experience polish: bilingual Duolingo ABC prompts, phoneme sound-out audio on tile placement, pop on removal, green leaf glow on correct word submission, and zero-emoji compliance across `BlendItScreen`, `BlendItCard`, and `BlendItCompleteScreen`

### T8 — Tri-Benchmark Synthesis & Pediatric Compliance (Duolingo ABC + Headspace + Drops)
- [x] **Pediatric Touch-Target Floor**: 64dp minimum strictly enforced on all interactive buttons (`GummyBackButton` bumped to 64dp, all bottom action buttons bumped to 64dp, HearIt audio CTA 88dp, SayIt mic CTA 88dp, FindIt audio pill 64dp, 52dp adult button floor for Parent Dashboard / PDF Export).
- [x] **Pediatric Reading Floor**: 24sp minimum enforced on all instructional and reading text across `LetterCard`, `MascotSpeechHeader`, `DockedMascotWithBubble`, `FindItCard`, status pills, `ProfileCard`, `NamePromptScreen`, `AvatarPicker`, and `SplashScreen`.
- [x] **Headspace Non-Punitive Feedback**: Zero red error family colors, zero buzzer sounds, zero harsh X marks. All retry states mapped to warm Kalamansi amber (`#FFB74D`) with reassuring mascot postures and decaying gentle wiggles.
- [x] **Drops Visual Contrast**: Cards standardized to high-contrast `Cloud`/`CloudShadow` faces with `DarkBrownOutline` borders over multi-biome procedural backdrops.
- [x] **Parent Dashboard & Assessment Portal**: Standardized 52dp adult controls, 14sp secondary label floor, non-punitive Kalamansi growth framing in `PracticeFocusSection`, 3D static gummy badges in `MasteredSoundsShelf` and `WordBlendingShelf`, `GummyContainer` profile switcher, and mathematically centered `ReportPreviewScreen` with localized error handling.
- [x] **Learner Onboarding & Profile Flow**: 24sp name and star reading floors with grammar pluralization in `ProfileCard`, empty-state breathing pulse in `AddProfileButton`, dedicated `AvatarCircle` loading production companion animal assets, diacritic/letter emoji-defense input filter, error-aware mascot reactivity in `NamePromptScreen`, and 76dp spring-animated avatar selection in `AvatarPicker`.
- [x] **Splash & Value Proposition**: 24sp/26sp reading floors, scrollable viewport layout preventing clipping on compact devices, and pinned 64dp primary CTA.

## Phase 11 — Audit Fixes & Technical Hardening (2026-08-30)
- [x] **CB-1: SpeechValidator False-Positive Matching**: Removed Levenshtein fuzzy distance matching (<5 chars) and broad substring matches in `SpeechValidator.kt` to eliminate minimal-pair phonics false passes.
- [x] **CB-2: AudioCompletenessCheck Word List Desync**: Synchronized `requiredWords` in `AudioCompletenessCheck.kt` to match the exact 33 seeded BlendIt words in `DatabaseModule.kt`.
- [x] **CB-3: Repository Error Propagation**: Removed generic try/catch error swallowing across all 11 repository implementations in `data/repository/`, allowing exceptions to propagate cleanly to ViewModels.
- [x] **CB-4: Lifecycle-Aware Flow Collection**: Added `androidx.lifecycle:lifecycle-runtime-compose` and migrated all 10 screen composables from `collectAsState()` to `collectAsStateWithLifecycle()`.
- [x] **CB-5: Vosk Model Memory Warm-Keep**: Removed `voskRecognizer.release()` from `SayItViewModel.onCleared()` to prevent multi-second 70MB model churn on screen transitions.
- [x] **CB-6: Marungko Sequence Documentation**: Added explicit pedagogical adaptation notice in `DatabaseModule.kt` phoneme seed data with thesis Chapter 3 cross-referencing.
- [x] **HP-1: Dual-Criteria Star Thresholds**: Updated `StarCalculator.kt` and `BlendItStarThresholds.kt` with accuracy percentage criteria alongside hearts lost.
- [x] **HP-2: Zero-Emoji Compliance**: Replaced emoji glyphs in `PhonemeHeatmapSection.kt` with `Icons.Filled.Abc` and `Icons.Filled.Star` Vector Icons.
- [x] **HP-3: Touch Target Accessibility**: Bumped `FindItScreen.kt` audio CTA container height from 44dp to 56dp.
- [x] **HP-4: Audio-Visual Synchrony**: Synchronized mascot bubble text in `HearItScreen.kt` and `SayItScreen.kt` 1:1 verbatim with on-tap phoneme audio playback.
- [x] **HP-5: StreakTracker UTC Documentation**: Clarified UTC calendar boundary in `StreakTracker.kt` while preserving pure Kotlin architecture.
- [x] **HP-6: 2-Digit Arithmetic Gate**: Strengthened random operand ranges in `ArithmeticGateManager.kt` to guarantee strictly 2-digit adult math problems.
- [x] **HP-7: Lesson Duration Tracking**: Added `timeSpentMs` field to `LessonProgress` domain model and `LessonProgressEntity`.
- [x] **HP-8: Error State UI Handling**: Added `retry()` functions in `HearItViewModel`/`SayItViewModel` and integrated `ErrorStateContent` in `HearItScreen` and `SayItScreen`.
- [x] **HP-9: Name Prompt State Hoisting**: Hoisted player name and avatar selection state from `NamePromptScreen` into `ProfileViewModel` with config change survival.
- [x] **Build Verification**: Verified `compileDebugKotlin` and KSP processor passes cleanly with `BUILD SUCCESSFUL`.

## Phase 12 — Say It Word Mode (added 2026-09-08)

Change: Say It now asks the child to utter the letter's example WORD (m → "Mouse", s → "Sun") instead of the bare letter sound; prompt card shows the word's picture + word with the initial letter emphasized; whole-word acceptance only (CB-1 strict, no prefix/fuzzy tolerance). SME-pending letters (`ng`/`ñ`) keep the legacy letter-sound mode. Stakeholder decisions: reuse the 26 seeded `exampleWord` values; picture + word card UI; one new teacher VO line + word audio prompt.

- [x] Word-mode validator in `SpeechValidator.kt`: `wordAcceptedVariants` (26 seeded words), `getAcceptedWordVariants()`, `validateWord()` — whole-word-only (FR-02, FR-03)
- [x] `SayItViewModel`: `targetWord` flow + `resolveWordTarget()` (ng/ñ + PENDING_SME_REVIEW → null legacy), word-grammar scoping, word prompt sequence (HP-4)
- [x] `SayItScreen`: word-mode prompt card via `LetterCard(promptMode = true)` ("Say the word Mouse" instruction + shared Hear It picture), word-mode copy, legacy letter card preserved for ng/ñ
- [x] `vo_sayit_word_intro_01.mp3` synthesized (Ava neural teacher voice) + `VoContext.SAYIT_WORD_INTRO_01` + completeness mirrors (18 → 19)
- [x] Test rework: stale `fuzzyTolerance_handlesMinorVoskVariances` replaced with post-CB-1 negative; word-mode unit tests added (validator + ViewModel)
- [x] Docs: `19_AUDIO_SCRIPTS.md` §2 row added; `01_REQUIREMENTS_SUMMARY.md` Module 2 word-mode note

## Phase 13 — Say It Tap-to-Rehear & Audio Polish (2026-09-09)
- [x] **Word Audio Audit**: Audited `assets/audio/words/` against all 26 Say It prompts, Find It words, and vocabulary banks; identified 23 placeholder dummy beeps and 37 missing vocabulary audio files.
- [x] **High-Quality Neural Audio Generation**: Synthesized 71 high-fidelity child-friendly audio files using Edge-TTS (`en-US-AnaNeural` and `fil-PH-BlessicaNeural`) via `scripts/generate_missing_word_audio.py`.
- [x] **Say It Tap-to-Rehear UI**: Added top-right speaker badge and animated "Tap to listen" / "Playing..." bottom pill on `LetterCard.kt` with zero-emoji compliance.
- [x] **Audio Debounce & Guard**: Implemented 500ms `AUDIO_DEBOUNCE_MS` timestamp check and active playback guard in `SayItViewModel.kt` preventing overlapping audio and `MediaPlayer` crashes.
- [x] **Comprehensive Test Suite**: Added `SayItViewModelTest.playWordAudio_rapidTaps_debounced`, `AudioCompletenessCheckTest.verifyAllSayItWordPromptsExistOnDisk`, and full ViewModel coverage for `ParentDashboardViewModelTest`, `LetterCompleteViewModelTest`, `ProfileViewModelTest`, and `MapViewModelTest` (132/132 unit tests green).
- [x] **Debug APK Build**: Built debug-signed APK (`app/build/outputs/apk/debug/app-debug.apk`) and copied to workspace root `playit-debug.apk` (97 MB).

## Phase 14 — Neural Audio Upgrade & Loudness Normalization (2026-09-09)
- [x] **Audit & Preservation**: Preserved 100% of the 71 newly created word audio files from recent sessions and `vo_sayit_word_intro_01.mp3`.
- [x] **Phoneme Suite Upgrade**: Synthesized and normalized all 30 phoneme sound files (`phoneme_a.mp3` through `phoneme_z.mp3`, `phoneme_ng.mp3`, `phoneme_enye.mp3`, `phoneme_ñ.mp3`) with `rate="-10%"` for crisp articulation using `en-US-AnaNeural` and `fil-PH-BlessicaNeural`.
- [x] **Remaining Vocabulary Upgrade**: Upgraded all remaining 44 legacy/robotic vocabulary files (e.g., `word_base.mp3`, `word_cake.mp3`, `word_aim.mp3`, `word_zoo.mp3`) to natural, child-friendly conversational pacing using `en-US-AnaNeural`.
- [x] **Voice-Over Suite Upgrade**: Upgraded all 25 voice-over tracks across UI and instructional gameplay loops (`vo_hearit_intro_01.mp3`, `vo_sayit_intro_01.mp3`, `vo_findit_intro_01.mp3`, `vo_welcome_01.mp3`, etc.) mirrored cleanly in both `audio/vo/` and `audio/ui/`.
- [x] **Loudness Normalization**: Normalized 100% of newly generated audio assets to standard broadcast target (-15 LUFS, 24kHz, 48kbps MP3) via ffmpeg `loudnorm` filter.
- [x] **Zero Duplicate Hashes / Stubs**: Confirmed 0 placeholder tone beeps and 0 unwanted duplicate hashes across the entire audio asset library.
- [x] **Test & Build Verification**: Full test suite passed (`./gradlew testDebugUnitTest`), and debug APK assembled cleanly (`./gradlew assembleDebug`, 97 MB).

## Phase 15 — UI Overhaul & Tactile Redesign (2026-09-10)
- [x] **Dedicated Branch Setup**: Implemented on isolated branch `feat/ui-rework` branching from clean `main`.
- [x] **Tactile Gummy Suite & Haptic Depress (Phase 1)**: Added `Color.deriveShadow()` dynamic shadow computation, `LocalHapticFeedback` tick on press, dynamic `translateY` depress `(depthHeight - 1.dp)` in `GummyButton.kt`, and vectorized `GummyIconButton`.
- [x] **Richer Gamified Biomes & 3D Treasure Chests (Phase 2)**: Added 7th Marungko Chapter biome (Mount Pulag Summit, Ube/Midnight), updated `BiomeTheme.kt` and `MarungkoGroupBanner.kt`, expanded `UnitGuidebookDialog.kt` with Units 6 & 7, and transformed milestone challenge node into an authentic 3D Gummy Treasure Chest with gold lid trim, star keyhole clasp, and radiant aura.
- [x] **Dynamic Coaching Companion (Phase 3)**: Added mic audio amplitude reactive listener to `MascotSpeechHeader.kt` (`ampSquashY`, `ampSquashX`) so mascot Lily bounces and squashes in real-time with the child's voice in `SayItScreen.kt`.
- [x] **Sequential Phoneme Sound-Out Engine (Phase 4)**: Implemented step-by-step sound-out loop in `BlendItViewModel.kt` highlighting slots 1 -> 2 -> 3 with spring scale pops and phoneme audio playback every 400ms, whole word blending audio, and victory chimes in `BlendItScreen.kt`. Added dynamic shadow rendering in `FindItGrid.kt`.
- [x] **Integrated Numeric Keypad Parent Gate (Phase 5)**: Embedded custom on-screen 3x4 numeric keypad (1-9, C, 0, DEL with `Icons.AutoMirrored.Filled.Backspace`) and explicit top-right close button (`Icons.Filled.Close`) in `ArithmeticGuardDialog.kt` to eliminate Android soft keyboard layout jumps and IME clipping.
- [x] **Full Verification & Zero-Emoji Audit (Phase 6)**: All 132/132 unit tests green (100% pass rate). Verified strict adherence to zero-emoji policy, 64dp minimum touch target floor, and 100% pure Kotlin in `domain/`. Built updated debug APK (`playit-debug.apk`).

## Phase 16 — Complete Modern Grade 1 Design Language Overhaul (2026-09-12)
- [x] **Strict Branch Isolation**: Carried out 100% of design changes on `feat/ui-rework` keeping `main` untouched.
- [x] **Brand-New Theme Foundation**: Overwrote `Color.kt`, `Shape.kt`, `Type.kt`, and `Theme.kt` with modern high-energy tokens: `PrimaryJoy` (#6C5CE7), `SunnyGold` (#FFB800), `EmeraldLeaf` (#00C853), `CoralBerry` (#FF4757), `ApricotGlow` (#FF9F43), `AquaAdventure` (#0ABDE3), `ModernBorder` (#1E293B), `SurfaceCard` (#FFFFFF), `CanvasLight` (#F7FAFC), `TextMidnight` (#1A202C). Introduced refined squircle shapes (`Squircle12` to `Squircle32`), card elevation depth tokens, and modernized Lexend typography hierarchies.
- [x] **Profile & Onboarding Modernization**: Refactored `ProfileSelectScreen.kt`, `NamePromptScreen.kt`, `ProfileCard.kt`, `AddProfileButton.kt`, and `AvatarCircle.kt` with clean high-contrast surfaces, vibrant tactile 3D borders, and enhanced avatar ring badges.
- [x] **Map & Level Progression**: Overhauled `MapScreen.kt`, `TopStatsBar.kt`, `NodeActionPopupDialog.kt`, and `UnitGuidebookDialog.kt` with fresh modern stats pills (Streak, Stars, Hearts), crisp slate borders, and updated chapter banners.
- [x] **Minigame & Lesson Polish**: Modernized `HearItScreen.kt`, `SayItScreen.kt`, `FindItScreen.kt`, `BlendItScreen.kt`, `LetterCompleteScreen.kt`, and `BlendItCompleteScreen.kt` with refreshed `LetterCard`, `BlendItCard`, `FindItGrid`, and celebratory overlays.
- [x] **Parent Dashboard & Diagnostic Analytics**: Refactored all 14 parent diagnostic components (`ParentDashboardScreen.kt`, `ReportPreviewScreen.kt`, `LearnerHeroCard.kt`, `LetterPerformanceTable.kt`, `OverallStatsCard.kt`, `PhonemeHeatmapSection.kt`, `ArithmeticGuardDialog.kt`, etc.) for high legibility and clear educator hierarchy.
- [x] **Zero-Emoji Policy Compliance**: Ensured zero emojis in UI text, buttons, titles, and speech bubbles; exclusively used Material vector graphics and transparent asset renders.
- [x] **Compilation & APK Generation**: Verified clean compilation via `./gradlew compileDebugKotlin` and assembled `app-debug.apk` (97 MB) into workspace root `./playit-debug.apk`.

## Phase 17 — VoiceStudio + Whisper Educational Phonics Suite & Avatar Alignment (2026-09-12)
- [x] **VoiceStudio Audio Suite Expansion**: Scaled synthesis to 101 production assets covering all 26 canonical phonemes (full pedagogical scripts e.g. "Ah. A, like Apple."), 21 UI/VO lines, and 54 word assets using `en-PH-RosaNeural`.
- [x] **Whisper Speech Recognition Verification**: Verified all 101 assets using local OpenAI Whisper (`base`), achieving 100% keyword verification rate and resolving single-syllable acoustic nuances.
- [x] **Pediatric Reading Floor & UI Synchrony**: Upgraded `PediatricComponents.kt` (`MascotBubble`) to 24sp Lexend Bold text styling; synchronized `ProfileSelectScreen.kt` and `NamePromptScreen.kt` dialogue text 1:1 verbatim with production voice-overs (`vo_welcome_01.mp3`, `vo_nameprompt_intro.mp3`).
- [x] **Companion Animal Avatar Parity**: Synchronized IP companion animal assets (`avatar_01_cat.png` through `avatar_06_owl.png`) identically across `assets/images/characters/` and `assets/images/mascot/`.
- [x] **Unit Test & Build Verification**: All audio unit tests green (`AudioCompletenessCheckTest`, `AudioResolverTest`), clean compile, and verified debug APK assembled (`playit-debug.apk`, 102 MB).

## Phase 18 — Phonics Audio Re-Architecture (Style 1 Pedagogical Phonics) (2026-09-12)
- [x] **Speech Engine Acoustic Audit**: Diagnosed letter-stuttering ("S-S-S-S like Sun") and acronym spell-out artifacts in raw phoneme text; audited all 26 canonical letters with OpenAI Whisper ASR.
- [x] **Style 1 Pedagogical Carrier Scripts**: Standardized all 26 letters on explicit phonic instruction ("The letter S makes the sound, sss. S, like Sun.") with aspirate tuning for stop consonants (`pa`, `ta`, `ww`, `ks`).
- [x] **Neutral Primary Educator Voice Talent**: Adopted `en-US-JennyNeural` for clear, unhurried, natural kindergarten phonics articulation adhering to `18_AUDIO_PRODUCTION_GUIDE.md` §4.
- [x] **Broadcast Loudness Normalization**: Processed all 29 phoneme assets through FFmpeg `loudnorm` filter (-16 LUFS, 44.1kHz mono, 128kbps MP3) and deployed to `assets/audio/phonemes/`.
- [x] **Interactive Audition Showcase**: Added live playable Letter S Audition & Comparison workbench to `asset_review.html`.

## Phase 19 — Map Mascot & Companion Immersion with Cultural Terrain Props (2026-09-12)
- [x] **Lily the Tarsier as Chief Learning Guide**: Stationed Lily beside the active lesson node with pointing pose (`lily_pointing.png`) and dynamic speech bubble (*"Tara na! Letter [X]"*).
- [x] **Child Explorer Buddy on the Trail**: Stationed the child's chosen profile companion animal advancing along the trail with personalized cheer (*"Let's go, [Name]!"*).
- [x] **Supporting Animal Friends across Biomes**: Stationed remaining 5 companion friends across the 7 chapter milestones with distinct personality cheers and bilingual encouragement.
- [x] **Tactile Gummy Speech Bubbles & Tap Physics**: Implemented 3D Gummy speech bubbles with downward pointer tails, 12sp Lexend typography, spring leap jump (`Animatable -16f`), squash-stretch landing, and tap sound reactions.
- [x] **Integrated Cultural Terrain Props**: Embedded `MapTerrainProps` layer in `MapScreen.kt`, populating the map with swaying Bohol palm trees, nipa huts, tropical flowers, and school adventure props.
- [x] **Full Build & APK Assembly**: Verified clean compilation (`compileDebugKotlin`) and assembled debug APK (`playit-debug.apk`, 104 MB).

## Phase 20 — Duolingo ABC Lily Mascot Recreation & Map Trail Tactile Overhaul (2026-09-12)
- [x] **Duolingo ABC Lily Mascot Poses**: Recreated all 7 canonical Lily poses (`lily_idle`, `lily_waving`, `lily_listening`, `lily_pointing`, `lily_encouraging`, `lily_thinking`, `lily_celebrating`) using Imagen 3 with character consistency chaining, huge golden-amber eyes, pink inner ears, fluffy caramel fur, cream chest, and clean `#2D373E` outlines.
- [x] **BFS Isolation & Production Deployment**: Processed all 7 poses via BFS exterior flood-fill background removal, centered on 512x512 canvas with pediatric sticker stroke, and deployed to `assets/images/mascot/` and `splash_tarsier_headspace.png`.
- [x] **Tactile Map Trail Ribbon**: Upgraded `MapPathCanvas.kt` with a 20dp-wide warm organic ribbon path, depth shadow, dynamic progress coloring (golden road for mastered units, warm sand for active units, soft stone for locked sections), and 3D pebble pavers with drop shadows and top highlights.
- [x] **Visual Review Suite Expansion**: Integrated all 7 new Lily poses into `asset_review.html` alongside the 6 companion animal avatars.
- [x] **APK Assembly & Verification**: Verified clean build and updated root `playit-debug.apk`.

## Phase 22 — Companion Animal Avatars Suite Rework & Mascot Alignment (2026-09-12)
- [x] **Mascot Preservation & Restoration**: Fully preserved and restored Lily the Tarsier's newly recreated master suite across all 7 canonical poses (`lily_idle`, `lily_waving`, `lily_listening`, `lily_pointing`, `lily_encouraging`, `lily_thinking`, `lily_celebrating`) and `splash_tarsier_headspace.png`.
- [x] **Companion Animal Avatars Visual Adoption**: Recreated all 6 companion animal avatars (`Miki the Cat`, `Milo the Monkey`, `Bella the Bunny`, `Barnaby the Bear`, `Finley the Frog`, `Ollie the Owl`) adopting Lily's exact design language: signature 2-tone golden/amber iris rings, double circular specular catchlights, arched dark eyebrows, rounded cheek fluff tufts, organic curved limbs with mitten paws and toe beans, grounded 3-lobe feet, and continuous `#2D373E` pediatric outlines.
- [x] **Dual Anatomy & Context Optimization**: Designed every companion animal as an upright, charming full-body mascot that shines both inside circular profile badges (`AvatarPicker`, `AvatarCircle`, `MascotBubble`) and as lively, animated explorer companions along the winding Adventure Map trail (`MapCompanionFriends`).
- [x] **Triple-Directory Production Deployment**: Deployed across `characters/avatar_0X_<animal>.png`, `mascot/avatar_0X.png`, and `mascot/companion_avatar_0X_<animal>.png`.
- [x] **Visual Review Suite & Unit Test Parity**: Updated `asset_review.html` gallery and verified 100% test suite pass (132/132 unit tests green in `MapPathGeometryTest.kt` and across all modules).
## Phase 23 — Complete Pediatric Asset Rework Suite (Pictures, Rewards, Map Props, Letters, & Blend-It Words) (2026-09-12)
- [x] **Philippine Phonics Letter Cards**: Generated high-resolution 1024x1024 Lexend blue bubble 3D letter cards for Philippine curriculum phonemes (`letter_ñ.png`, `letter_n_tilde.png`, `letter_ng.png`) matching `letter_a.png` (`anchor_letter-card.png`) and deployed to `images/letters/`.
- [x] **Gamification Rewards Overhaul**: Modernized all core reward assets (`reward_star.png`, `reward_heart.png`, `reward_streak.png`, `reward_confetti_burst.png`) with 2x Lanczos supersampling, organic teardrop flame curves, tactile highlights, and continuous `#2D373E` pediatric outlines in `images/rewards/`.
- [x] **Map Biome Props Suite**: Generated and deployed all 14 storybook cultural map props (`mapprop_nipa_hut`, `mapprop_palm_tree`, `mapprop_flower`, `mapprop_pencil_tower`, `mapprop_crayon_bridge`, `mapprop_book_stack`, `mapprop_globe`, `mapprop_backpack`, `mapprop_paper_airplane`, `mapprop_ruler_ramp`, `mapprop_eraser_shrub`, `map_prop_bush`, `mapprop_paint_palette`, `map_prop_rock`) in `images/backgrounds/`.
- [x] **Phoneme Picture Cards Modernization**: Upgraded all 17 remaining phoneme picture cards (`picture_cat.png` through `picture_zebra.png`) and word labels (`word_<word>.png`) to commercial Duolingo ABC pediatric standards in `images/pictures/`.
- [x] **Blend-It Word Pictures Overhaul**: Upgraded all 47 Blend-It and distractor word illustrations in `images/pictures/blendword_*.png` with 2x Lanczos supersampling (1024x1024 downscaled to 512x512 RGBA), layered 3-tone shading, expressive storybook charm, `#2D373E` outlines, and 100% transparent backgrounds. Synchronized shared primary pictures (`cat`, `dog`, `pig`, `hat`, `van`, `box`, `fish`) and rendered dedicated high-res suites for all 36 remaining words (`bus`, `sub`, `mom`, `bee`, `bib`, `bat`, `kit`, `toy`, `boy`, `pan`, `bug`, `pin`, `nap`, `hen`, `bed`, `web`, `fan`, `cap`, `cup`, `jam`, `fox`, `zoo`, `quiz`, `bam`, `bird`, `cake`, `draw`, `face`, `gap`, `hand`, `lit`, `mob`, `road`, `spin`, `sum`, `warm`).
- [x] **Visual Review Showcase**: Updated `asset_review.html` with interactive showcase sections covering all recreated mascot poses, companion animal avatars, map props, phoneme cards, and all 47 Blend-It word pictures under checkerboard transparency.
- [x] **Test Suite & Clean APK Assembly**: 132/132 unit tests green (`BUILD SUCCESSFUL`), zero-emoji compliance verified, and freshly assembled debug APK deployed to `playit-debug.apk` (107 MB).

## Phase 24 — Comprehensive Picture & Vocabulary Mascot Alignment Overhaul (2026-09-12)
- [x] **Mascot Aesthetic Adoption Across Characters & Animals**: Overhauled all human characters and animals (`boy`, `nino`, `mom`, `uncle`, `queen`, `king`, `face`, `mob`, `ant`, `duck`, `elephant`, `mouse`, `owl`, `ox`, `snake`, `tiger`, `worm`, `yak`, `zebra`, etc.) adhering strictly to Lily the Tarsier's design language: 2-tone iris rings, dual circular specular catchlights, delicate arched eyebrows, rounded cheek fluff, mitten paws with digit beans, grounded 3-lobe feet, and continuous `#2D373E` outlines.
- [x] **Complete Objects, Props & Nature Suite Overhaul**: Redesigned all remaining 33 picture objects (`hand`, `bib`, `cap`, `hat`, `draw`, `fan`, `gap`, `jam`, `lit`, `mat`, `sum`, `axe`, `bano`, `drum`, `egg`, `gift`, `igloo`, `ink`, `jet`, `jug`, `leaf`, `map`, `nest`, `net`, `nut`, `pina`, `quilt`, `rocket`, `six`, `top`, `tree`, `vase`, `vest`, `wing`, `yarn`, `yoyo`, `zip`, `envelope`, `key`, `ring`, `star`) to 2x Lanczos supersampled (512x512 RGBA) Duolingo ABC standard.
- [x] **Clarification & Rework of Baño, Niño, and Piña**: Clarified and illustrated the Philippine Grade 1 Marungko curriculum for the letter **Ñ**: rendered `picture_bano.png` as a charming storybook clawfoot bathtub with warm water, frothy bubbles, brass faucet, and floating rubber duckie; `picture_nino.png` as a smiling Filipino boy; and `picture_pina.png` as a juicy golden tropical pineapple with spiky crown.
- [x] **Zero Legacy Files Remaining**: Confirmed 100% of all 136 PNG assets in `app/src/main/assets/images/pictures/` are now high-resolution 512x512 RGBA transparent images with zero stubs or legacy low-res files remaining.
- [x] **Interactive Review Gallery Showcase**: Added Section 8 (Extended Vocabulary & Challenge Picture Suite) to `asset_review.html` showcasing all newly reworked assets over checkerboard transparency grids.
- [x] **Full Verification Gate**: All 132 unit tests green (`BUILD SUCCESSFUL`), zero emojis, and fresh debug APK assembled to `playit-debug.apk` (107 MB).

## Phase 25 — Star Reward Pipeline Resolution & Headspace Mascot App Logo Rework (2026-09-13)
- [x] **Star Reward Calculation & Navigation Transmission**: Added `abstract val starsEarned: Int` to `MapNode`, normalized accuracy percentage in `StarCalculator`, updated `Routes.kt` and `NavGraph.kt` to transmit `heartsLost` from `FindItScreen` and `BlendItScreen` into `LetterCompleteScreen` and `BlendItCompleteScreen`.
- [x] **Database Persistence & Profile Aggregation**: Implemented `ProfileDao.addStars()` atomic queries and `getTotalStarsForProfile()` across `LessonProgressDao` and `BlendItProgressDao`. Injected `ProfileRepository` into `LetterCompleteViewModel` and `BlendItCompleteViewModel` with replay protection (`starDelta = (bestStars - previousStars).coerceAtLeast(0)`), ensuring replaying challenges never deducts stars or double-counts rewards.
- [x] **Map Progression & Star Synchronization**: Injected `BlendItProgressRepository` into `MapViewModel` to load and reflect `starsEarned` across all `BlendItNode` and `LetterNode` points. Updated `NodeActionPopupDialog`, `MapPathCanvas`, and `MapScreen` with 3-star display below completed challenge nodes.
- [x] **Headspace Mascot App Logo Rework**: Completely overhauled the official PlayIT app logo and launcher icon suite across all mipmap densities (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) and `drawable-xxxhdpi/ic_launcher_*.png`. Synthesized Headspace's signature warm sunny cream gradient (`#FFFDEE` -> `#FED766`) with the newest Lily the Tarsier mascot (`lily_idle.png`), complete with continuous `#2D373E` outlines, tactile 3D gummy depth shadows, circular framing, and 72dp Android adaptive safe-zone compliance.
- [x] **Full Verification Gate**: 100% test passage across all 132 unit tests (0 failures, 0 errors), verified zero-emoji compliance, and assembled production debug APK.

## Phase 26 — Master Mascot Splash Screen Overhaul & Pure English App-Wide Transition (2026-09-13)
- [x] **Master Mascot Splash & Intro Screen Overhaul**: Completely replaced the deprecated 2D Canvas dome in `SplashScreen.kt` with Lily the Tarsier's official master mascot artwork (`lily_waving.png`). Stationed Lily in front of a warm, luminous radial sun halo with gentle living breathing animation (`breatheScaleX`/`breatheScaleY`), playful spring hop tap physics (`tapBounce`), rolling playground hills, welcoming speech bubble, and an unblocked 64dp primary `GummyButton` ("Start Playing").
- [x] **Pure English App-Wide Transition**: Audited and eliminated all Tagalog words across the entire app:
  - `SplashScreen.kt`: Removed *"Mabuhay!"*, updated tagline to *"Ready to learn to read?"* and dialogue to *"Hi friend! I'm Lily! Let's learn letter sounds together!"*.
  - `BlendItCard.kt`: Removed *"Pindutin para marinig"*, standardized on a clean single row with volume icon and *"Tap to hear word"*.
  - `MapCompanionFriends.kt`: Replaced *"Tara na! Letter X"*, *"Word Challenge! Tara na!"*, *"Kaya mo 'yan!"*, *"Subukan natin!"*, *"Galing!"*, *"Ang galing mo!"*, *"Tuloy lang!"*, and *"Napakagaling!"* with encouraging pure English expressions (*"Let's go! Letter X"*, *"Word Challenge! Let's go!"*, *"You can do it!"*, *"Let's try it!"*, *"Great job!"*, *"You're amazing!"*, *"Keep it up!"*, *"Super star!"*).
  - `UnitGuidebookDialog.kt`: Replaced legacy Tagalog examples (*"Mais"*, *"Saging"*, *"Aso"*, *"Ibon"*, *"Orasan"*, *"AMA"*, *"MAMA"*, *"Bangka"*, *"Ulan"*, *"BATA"*, *"KUBO"*, etc.) with canonical curriculum English phonemes and blend words (*"Mouse"*, *"Sun"*, *"Apple"*, *"Insect"*, *"SAM"*, *"SIS"*, *"AIM"*, etc.) aligned 1:1 with `DatabaseModule.kt`.
  - `AudioResolver.kt` & `MapViewModel.kt`: Added `VoContext.MAP_LETS_GO` and routed mascot map tap reactions cleanly through pure English voiceovers.
- [x] **Zero-Emoji Policy Compliance**: Maintained 100% adherence to the Zero-Emoji Policy across all revised buttons, dialogs, titles, and speech bubbles.
- [x] **Full Verification Gate**: 132/132 unit tests green (`BUILD SUCCESSFUL in 15m 33s`), clean Kotlin compilation with zero warnings, and freshly assembled debug APK (`playit-debug.apk`, 108 MB).

## Phase 27 — Unified Single-Pipeline Audio Engine & Interactive Gating Overhaul (2026-09-13)
- [x] **Single-Pipeline Audio Architecture Overhaul**: Replaced the dual-engine `SoundPool` + `MediaPlayer` collision architecture in `AudioPlayer.kt` with a single unified `MediaPlayer` pipeline.
  - Eliminated hardware stream mixing, hardcoded 300ms delay guesses, and overlapping sound effects.
  - Added session token invalidation (`currentSessionId`) ensuring newly triggered audio immediately cancels any stale playback.
  - Added acoustic separation (120ms) between sequential clips in `playSequence` with true `OnCompletionListener` events.
  - Added non-blocking coroutine suspension extensions `suspend fun playAssetAudioAwait(assetPath: String)` and `suspend fun playSequenceAwait(assetPaths: List<String>)` for reliable awaiting.
  - Exposed `val isAudioPlaying: StateFlow<Boolean>` for reactive, app-wide UI gating.
- [x] **BlendIt Word Phoneme Rework**:
  - Removed the letter-by-letter phoneme sound-out loop (`for (i in targetWord.indices) { phonemeAudio... }`) upon completing a word in `BlendItViewModel.kt`.
  - Configured word completion to directly play the blended whole word audio (`wordAudio`), followed by celebration chime and praise VO via sequential awaiting (`playSequenceAwait(listOf(sfx, vo))`).
  - Gated word tile placement, removal, and word submission while audio is actively playing.
  - Gated letter slots, tile bank buttons, word audio replay pill, mascot speech bubble, and "Check Word" button in `BlendItScreen.kt` with disabled states and visual dimming (`0.5f` alpha) while audio is playing.
- [x] **Gated Scene Navigation Across Completion Screens**:
  - `BlendItCompleteScreen.kt` & `BlendItCompleteViewModel.kt`: Gated the "Continue to Map" button with `enabled = !isAudioPlaying` and `0.5f` alpha until the fanfare, level complete VO, and streak VO sequence has finished playing completely.
  - `LetterCompleteScreen.kt` & `LetterCompleteViewModel.kt`: Gated the "Continue to Map" button with `enabled = !isAudioPlaying` and `0.5f` alpha until the fanfare, lesson complete VO, and unlock VO sequence has finished playing completely.
- [x] **Interactive Button Gating Across Learning Screens**:
  - `FindItScreen.kt` & `FindItViewModel.kt`: Gated grid cards, target audio replay pill, mascot speech bubble tap, and "Complete Lesson" button on `isAudioPlaying`.
  - `SayItScreen.kt` & `SayItViewModel.kt`: Gated letter prompt card tap, mascot tap, microphone recording button, and "Next: Find It" button on `isAudioPlaying`.
  - `HearItScreen.kt` & `HearItViewModel.kt`: Gated letter card tap, mascot tap, primary speaker CTA button, and "Next: Say It" button on `isAudioPlaying`.
  - `MapScreen.kt` & `MapViewModel.kt`: Gated mascot tap reaction, locked node tap reaction, and "START CHALLENGE" / "PRACTICE AGAIN" button in `NodeActionPopupDialog.kt` on `isAudioPlaying`.
- [x] **Zero-Emoji Policy Compliance**: Maintained 100% adherence to the Zero-Emoji Policy across all revised buttons, dialogs, titles, and speech bubbles.
- [x] **Full Verification Gate**:
  - 139/139 unit tests green (`BUILD SUCCESSFUL in 4m 43s`, 0 failures, 0 errors).
  - Debug APK cleanly assembled via `./gradlew assembleDebug` (`BUILD SUCCESSFUL in 4m 25s`).

## Phase 28 — BlendIt "Blending..." Freeze Resolution & Haptics Elimination (2026-09-13)
- [x] **Diagnosis & Resolution of Blend-It "Blending..." Freeze**:
  - Identified root cause in `AudioPlayer.kt`: `playSequence` initiated playback with `val sessionId = ++currentSessionId`, but each subsequent invocation of `executePlay` inside `playNextInSequence` also incremented `currentSessionId`. This caused `sessionId != currentSessionId` in the completion callback of clip 0, aborting the sequence before clip 1 was ever scheduled and never invoking `onComplete`.
  - Because `playSequenceAwait(listOf(sfx, vo))` never resumed, `BlendItViewModel` remained permanently suspended in `soundOutJob`, leaving `_uiState.value` as `BlendItUiState.WordCorrect`, and the primary button stuck permanently displaying `"Blending..."`.
  - Refactored `AudioPlayer.kt`: `executePlay` now accepts the existing `sessionId: Long` without generating a new ID, enabling multi-clip sequences to complete seamlessly.
  - Added safe coroutine timeouts (`withTimeoutOrNull(8000L)` and `withTimeoutOrNull(10000L)`) to `playAssetAudioAwait` and `playSequenceAwait`.
  - Added robust `try/catch` safeguards in `BlendItViewModel.kt` to ensure word advancement and session completion occur even in the event of an audio hardware exception.
- [x] **Complete Removal of Haptics & Vibration**:
  - Audited the entire repository for vibration and haptic feedback usages.
  - Removed `haptic.performHapticFeedback(HapticFeedbackType.LongPress)` and all `LocalHapticFeedback` references from `GummyButton.kt`.
  - Confirmed zero occurrences of `Vibrator`, `VibrationEffect`, or `performHapticFeedback` across the entire application.
- [x] **Full Verification Gate**:
  - 139/139 unit tests green (`BUILD SUCCESSFUL in 17m 32s`, 0 failures, 0 errors).
  - Clean APK assembly and deployment to `playit-debug.apk` (108 MB) via `./gradlew assembleDebug` (`BUILD SUCCESSFUL in 3m 26s`).

## Phase 29 — Pediatric Sound Effects Suite Mastering & UI Integration (2026-09-13)
- [x] **Pediatric Sound Effects Suite Synthesis (`tools/generate_pediatric_sfx.py`)**:
  - Engineered physics-modeled and subtractive/FM synthesizer pipeline specifically tuned for pediatric delight and supportive UX.
  - Mastered and deployed 8 studio-quality sound effects to `app/src/main/assets/audio/ui/`:
    - `sfx_correct_chime.mp3`: Ascending C-major chord (C5-E5-G5-C6) with physical-modeled rosewood marimba tone and glockenspiel sparkle (0.80s, Peak 0.82, RMS 0.132).
    - `sfx_incorrect_pop.mp3`: Gentle wooden bubble bloop pitch-gliding 260 Hz to 140 Hz (0.26s, Peak 0.78, RMS 0.145). Friendly and non-punitive.
    - `sfx_blendit_buzz.mp3`: Playful FM synthesized rubber spring wobble replacing harsh abrasive buzzer with a tactile cartoon bounce (0.42s, Peak 0.80, RMS 0.155).
    - `sfx_heart_loss_whoosh.mp3`: Subtle descending resonant air whoosh (1.8 kHz to 400 Hz) with warm body (0.55s, Peak 0.80, RMS 0.125).
    - `sfx_heart_recovery_sparkle.mp3`: Magical ascending glissando across C-major pentatonic scale with high glockenspiel sparkle and stereo reverb (0.85s, Peak 0.82, RMS 0.128).
    - `sfx_node_unlock_chime.mp3`: Dual-layer celesta and tubular bell chime with shimmering octave overtone celebrating map progression (1.40s, Peak 0.82, RMS 0.134).
    - `sfx_streak_badge_unlock.mp3`: Heroic pediatric fanfare sting (D5-F#5-A5-D6) with warm analog brass swell, vibrato, and glockenspiel accent (1.50s, Peak 0.82, RMS 0.136).
    - `sfx_level_complete_fanfare.mp3`: Full celebratory milestone fanfare with rich brass harmony, sparkling glockenspiel cascades, and smooth reverb tail (2.35s, Peak 0.82, RMS 0.148).
- [x] **Acoustic Mastering & Optimization**:
  - Zero true-peak clipping: Strict ceiling enforced at -1.4 dBFS (0.82 linear peak amplitude).
  - Balanced loudness: Calibrated RMS window of 0.125 - 0.155 across all sounds, eliminating disparity where some sounds were inaudible (previously 0.024) and others blaring (previously 0.236).
  - Truncated trailing dead silence: Eliminated 1.0 - 1.5s trailing empty space present in legacy files, making UI voiceover transitions instantaneous.
- [x] **Interactive Asset Showcase Integration (`asset_review.html`)**:
  - Added Section 10 ("Interactive Pediatric Sound Effects Suite") featuring HTML5 audio preview widgets, spectral/timing metadata, and acoustic descriptions for all 8 sound effects.
- [x] **Full Verification Gate**:
  - 139/139 unit tests green (`BUILD SUCCESSFUL in 48s`, 0 failures, 0 errors).
  - Clean APK assembly via `./gradlew assembleDebug` (`BUILD SUCCESSFUL in 46s`).

## Phase 30 — MapScreen Material 3 Refactor & Pediatric Polish (2026-09-13)
- [x] **Asset & UI Wrapper Bloat Elimination**:
  - Deleted 16 obsolete background prop image assets in `app/src/main/assets/images/backgrounds/` (`mapprop_*.png` and `map_prop_*.png`), freeing ~488 KB.
  - Deleted `MapTerrainProps.kt` (205 lines), removing complex prop layout calculations and infinite animation loops.
  - Simplified `ChocolateHillsBackground.kt` from 528 lines of CPU/GPU overdraw canvas to a clean, lightweight Material 3 gradient backdrop with soft topographic curves across the 6 Bohol biomes.
  - Streamlined `MapCompanionFriends.kt` to focus exclusively on Lily the Tarsier as the active learning guide and the profile explorer buddy.
  - Flattened `LetterMapNodeCard` from 7 nested Box wrappers into a clean, high-performance Material 3 disc composable.
- [x] **Material 3 Principles Integration**:
  - Adopted Material 3 `Scaffold` with proper insets handling and surface background.
  - Refactored `TopStatsBar.kt` using Material 3 `Surface`, elevated stat chips, and an M3 `LinearProgressIndicator` tracking the 26-letter journey.
  - Refactored `MarungkoGroupBanner.kt` to a clean Material 3 `Surface` / `ElevatedCard` layout without manual double-box shelf hacks.
  - Refactored `BlendItChallengeNodeCard` into a clean M3 milestone card.
- [x] **Subtle Letter Node Micro-Animations**:
  - **Active Node Breathing**: Gentle rhythmic scale (`1.0f` to `1.05f`) and soft glowing focus halo (`alpha = 0.55f` to `0.0f`, `scale = 1.0f` to `1.30f`).
  - **Tactile Touch Press**: Bouncy spring-based depression (`scale = 0.93f`, `translationY = +3dp`, `elevation = 1dp` vs `4dp`) using `MutableInteractionSource` and `collectIsPressedAsState()`.
  - **Locked Node Wobble**: Subtle horizontal spring wiggle ($\pm 8\text{dp}$) with instant visual lock feedback on tap.
  - **Reduced Motion Support**: Integrated with `LocalReducedMotion.current` to immediately disable continuous animations when reduced motion is preferred.
- [x] **Full Verification Gate**:
  - 139/139 unit tests green (`BUILD SUCCESSFUL in 10m 8s`, 0 failures, 0 errors).

## Phase 31 — Map Star Contrast Resolution & Headspace Mascot Logo Suite Alignment (2026-09-13)
- [x] **Map Star Contrast Capsule Integration**:
  - Identified root cause of poor star readability: 13dp amber/yellow stars (`#F59E0B`) floated nakedly directly over the golden path ribbon (`#FDE68A`) and the warm sand/amber terrain biomes (Units 3, 5, and 6: `#FEF3C7`, `#FED7AA`, `#FDE68A`, `#FEF9C3`), causing WCAG contrast degradation (< 1.3:1).
  - Wrapped 3-star rating rows in `LetterMapNodeCard` and `BlendItChallengeNodeCard` within an elevated, high-contrast pure white capsule (`SurfaceCard` / `#FFFFFF` background, `RoundedCornerShape(12.dp)`, `ModernBorder` 1.5dp stroke, soft 2dp elevation shadow).
  - Boosted star icon size to `14.dp` with `2.5.dp` spacing and `horizontal = 7.dp, vertical = 2.dp` padding.
  - Earned stars tinted with vibrant `SunnyGold` (`#F59E0B`), unearned stars in clean soft silver (`#CBD5E1`).
  - Guaranteed 100% contrast, visual hierarchy, and instant legibility across all 6 Bohol biomes.
- [x] **Official Headspace Mascot App Logo Suite Alignment**:
  - Overhauled the official app launcher branding suite per the user directive: synthesized Headspace's iconic clean geometric dome composition (rising peeking head) with Lily the Tarsier's authentic mascot features from `lily_idle.png` (giant luminous golden eyes with double catchlights, coral-peach inner ear lobes, sweet smile, and continuous `#2D373E` outline).
  - Cleaned neckline/chin contours with a smooth Headspace curve, eliminating stray tail or flat horizontal cut artifacts.
  - Enforced strict 72dp Android adaptive safe-zone circle compliance (~341px circle at 512px canvas, scale 0.73) with zero ear or cheek clipping across circular (Pixel), squircle (Samsung One UI), and rounded-square OEM launcher masks.
  - Generated and deployed master suite across all densities:
    - `drawable-xxxhdpi/ic_launcher_playstore.png` (512x512 Master Store Icon)
    - `drawable-xxxhdpi/ic_launcher_foreground.png` (512x512 Adaptive Foreground)
    - `drawable-xxxhdpi/ic_launcher_background.png` (512x512 Adaptive Sunny Background)
    - `mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/`: `ic_launcher.png`, `ic_launcher_round.png`, `ic_launcher_foreground.png`, `ic_launcher_background.png`.
  - Updated Section 9 of `asset_review.html` with new asset previews and technical descriptions.
- [x] **Full Verification Gate**:
  - 139/139 unit tests green (`BUILD SUCCESSFUL in 9m 2s`, 0 failures, 0 errors).

## Phase 32 — APK Size Reduction & Asset Modernization (2026-09-13)
- [x] **APK Forensic Audit & Category Breakdown**:
  - Engineered `tools/analyze_apk_size.py` to analyze uncompressed vs compressed sizes across all APK categories.
  - Identified 3 key optimization targets: dead multi-ABI native libraries (`lib/`), unused/duplicate PNG image assets, and dead dependencies/DEX bloat.
- [x] **ABI Targeting & Splits**:
  - Replaced universal fat APK configuration with Gradle ABI splits (`splits.abi`) targeting `arm64-v8a`, `armeabi-v7a`, and `x86_64`.
  - Standalone device builds (`app-arm64-v8a-debug.apk`) now package only 64-bit ARM binaries, instantly cutting 17.35 MB of dead x86_64 and 32-bit machine code.
- [x] **Asset Pruning & Dead File Elimination**:
  - Archived 30 obsolete prerendered letter card PNGs (`letter_a.png` through `letter_z.png`, `letter_ng.png`, `letter_n_tilde.png`, `letter_ñ.png`) to `archive/assets_prerendered_letters/` (5.89 MB saved; letter cards are dynamically rendered via Jetpack Compose and `LexendFontFamily`).
  - Removed duplicate avatar sets (`companion_avatar_*` in `mascot/`, saving 384 KB).
  - Removed duplicate `splash_tarsier_headspace.png` (247 KB).
  - Removed unreferenced `_style-reference-sheet/` and stray test audio (`tts_*`).
- [x] **WebP Modernization (Lossless / High-Fidelity Q95)**:
  - Batch-converted 159 active PNGs (`pictures/`, `mascot/`, `characters/`, `rewards/`) to Google WebP at Quality 95 using `tools/optimize_app_assets.py`.
  - Asset folder compressed from 16.21 MB down to 3.30 MB (saving 12.91 MB, a ~80% reduction in image size).
  - Upgraded `rememberAssetPainter` in `AssetUtils.kt` with auto-fallback between `.webp` and `.png` for zero-regression backwards compatibility with existing SQLite database records and tests.
  - Updated `DatabaseModule.kt`, `GridGenerator.kt`, `LetterCard.kt`, `BlendItCard.kt`, `FindItGrid.kt`, and theme components to `.webp`.
- [x] **Dependency Optimization**:
  - Removed unused `libs.coil.compose` from `app/build.gradle.kts`.
- [x] **Full Verification & Measurement**:
  - Unit tests green: `BUILD SUCCESSFUL` with 0 failures, 0 errors.
  - Measured physical APK size: reduced from **107.14 MB** down to **74.88 MB** (**32.26 MB reduction / >30% smaller** in debug build; projected ~62 MB in minified release).
  - Exported standalone testing APK to `playit-debug.apk` (75 MB).




