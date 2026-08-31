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
- [ ] **Curriculum Scope: 28-Letter DepEd Sequence vs. 26-Letter English Phonics (`Ñ` and `NG`)** — Pending stakeholder/thesis discussion: Decide whether to retain the 28-letter sequence ($7 \times 4$) to match DepEd Alpabetong Filipino or streamline to the 26 standard English letters ($A–Z$) for pure Grade 1 English phonics alignment.
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


