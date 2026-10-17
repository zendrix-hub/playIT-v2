# Card 32: Holistic Background UI/UX Realignment & PDF Diagnostic Refresh

Status: ready
Requirement: FR-05, FR-12, FR-13, NFR-ACC-01, NFR-ACC-02
Branch: refactor/hear-say-it
Origin: User grilling session (2026-10-17) after Argon 4 2D vector asset suite completion

---

## 1. Context & Motivation

Following the release of 90 newly audited Argon 4 2D vector illustrations across Find It, Blend It, map terrain props, and child rewards (Releases `2026-10-15` through `2026-10-17-map-and-rewards`), the application's background visual layer must be systematically realigned. 

Currently:
1. `FindItScreen` and `BlendItScreen` use an outdated, hardcoded 3-stop vertical gradient (`#E8F0FE` -> `#F3E8FF` -> `#FEF3C7`) that competes visually with foreground illustrations and distracts pre-readers (violating Cognitive Load Theory).
2. `HearItScreen` and `SayItScreen` lack physical spatial grounding, allowing cards and mascot Lily to float ambiguously in gradient space.
3. `ProfileSelectScreen` and `NamePromptScreen` retain legacy pastel gradients and uncalibrated procedural green hill strips.
4. `MapScreen` has 16 newly released Archipelago terrain props in `app/src/main/assets/images/backgrounds/` that are not yet anchored alongside the scrolling trail path.
5. `PdfExporter.kt` uses legacy hardcoded blues (`#4C68D7`), harsh red (`#D63031`), and outdated typography, conflicting with the zero-harsh-red mandate and the high-contrast slate aesthetic of `ParentDashboardScreen`.

This card adopts the validated **Duolingo ABC pediatric stage architecture** for learning activities while anchoring rich cultural Filipino environmental assets on the exploration hubs (`MapScreen` and `SplashScreen`).

---

## 2. Agreed Architectural Decisions (Grilling Session 2026-10-17)

### A. Learning Minigames (`HearIt`, `SayIt`, `FindIt`, `BlendIt`)
- **Duolingo ABC Activity Stage**: Backgrounds are calm, non-distracting functional pastels with a grounded bottom horizon baseline (~18–20% screen height) behind primary action controls (Play, Mic, Next CTA).
- **Functional Step Tints**:
  - **Hear It** (Auditory Modeling): Soft Calm Sky Blue (`#EBF5FB`)
  - **Say It** (Speech Production): Warm Morning Apricot / Peach (`#FEF5E7`)
  - **Find It** (Visual Recognition Grid): Soothing Mint / Leaf (`#EAFAF1`)
  - **Blend It** (Phoneme Blending & Decoding): Gentle Lavender / Joy (`#F4ECF7`)
- **Figure-Ground Contrast**: All transparent 2D illustrations with `#4A2E18` dark-brown outlines sit inside crisp, elevated white gummy cards (`SurfaceCard` `#FFFFFF` with 3D depth borders). No transparent asset is placed bare onto a tinted canvas without contrast protection.

### B. Map Screen (`MapScreen`, `ChocolateHillsBackground`)
- **Thematic Pathside Landmarks**: The 16 newly generated Archipelago map props (`mapprop_*.png` and `map_prop_*.png`) are placed as curated landmarks along the procedural rope trail path (e.g., nipa hut at Chapter 1 onset, crayon bridge spanning early letter turns, pencil tower & book stack near milestone chests, palm trees framing beach curves).
- **Touch Target Defense**: Props are strictly decorative background elements (`Modifier.zIndex(0f)`) positioned with safe margins ensuring 100% unobstructed 64dp touch targets for all lesson nodes and dialogue buttons.

### C. Splash Screen (`SplashScreen`)
- **Warm Sky & Morning Horizon**: Preserve the clean, inviting `Sky` (`#CBE7FF`) -> `Sand` (`#FDE4BE`) soft horizon.
- **Headspace/Duolingo Staging**: Mascot Lily is framed above a clean white bottom entry panel (`SurfaceCard`) with the Start CTA reachable and unclipped across all 4 benchmark viewports.

### D. Onboarding & Profiles (`ProfileSelectScreen`, `NamePromptScreen`)
- **Cohesive Pediatric Campus**: Replace the arbitrary 3-color gradient with `SoftSky` (`#E6F4FE`) fading into a gentle grounded meadow/sand floor.
- **Card Framing**: Learner profile cards, avatar circles, and the numeric keypad parent gate sit on crisp white gummy surfaces with 10.2:1 WCAG AAA text contrast.

### E. Parent Zone & Diagnostic PDF (`ParentDashboardScreen`, `ReportPreviewScreen`, `PdfExporter`)
- **Adult Diagnostic Palette**: Subdued professional neutrals (`CanvasSoft` `#F8FAFC` to `CanvasLight` `#F1F5F9`) with crisp white cards (`SurfaceCard`) and 1dp slate borders (`#E2E8F0`).
- **PDF Report Parity (`PdfExporter.kt`)**:
  - Banner updated from `#4C68D7` to `PrimaryJoy` (`#6C5CE7`) or `DeepNavy` (`#1E293B`).
  - Total elimination of `#D63031` harsh red; at-risk phonemes formatted in `DestructiveRed` / `ApricotGlow` (`#FF4757` / `#FF9F43`).
  - Mastery status badges colored with `EmeraldLeaf` (`#00C853`) and `SunnyGold` (`#FFB800`).
  - Strict print parity with on-screen `ParentDashboard` tables.

---

## 3. Challenge & Technical Review Checklist for Claude

Before implementing, Claude must rigorously verify the following architectural boundaries:

1. **4-Screen Viewport Bounds (Layout Matrix)**:
   - Does every screen fit without scrolling or clipping on:
     - Samsung Galaxy A21s (360 x 800 dp, density 2.0)
     - Compact Small Phone (320 x 640 dp, density 1.5)
     - Standard Modern Phone (393 x 852 dp, density 2.75)
     - Educational Android Tablet (800 x 1280 dp, density 1.5)
2. **Zero-Emoji Policy Audit**:
   - Zero Unicode emojis in any string, button, card header, or dialog. Exclusively vector icons (`Icons.Filled.*`) or release-manifested PNGs.
3. **Touch Target Enforcement**:
   - Every interactive control (Play, Mic, Next, Back, Node, Keypad key) must maintain $\ge 64\,\text{dp}$ tap targets.
4. **Pure Kotlin Architecture**:
   - Zero `android.*` imports in `domain/`. Any color, brush, or canvas logic lives strictly in `presentation/` or `data/pdf/`.
5. **Deterministic Testing**:
   - All tests pass locally and under Robolectric (`./gradlew testDebugUnitTest`).

---

## 4. Target Files

- `app/src/main/java/com/playit/app/presentation/hearit/HearItScreen.kt`
- `app/src/main/java/com/playit/app/presentation/sayit/SayItScreen.kt`
- `app/src/main/java/com/playit/app/presentation/findit/FindItScreen.kt`
- `app/src/main/java/com/playit/app/presentation/blendit/BlendItScreen.kt`
- `app/src/main/java/com/playit/app/presentation/profile/ProfileSelectScreen.kt`
- `app/src/main/java/com/playit/app/presentation/profile/NamePromptScreen.kt`
- `app/src/main/java/com/playit/app/presentation/map/MapScreen.kt`
- `app/src/main/java/com/playit/app/presentation/map/components/ChocolateHillsBackground.kt`
- `app/src/main/java/com/playit/app/presentation/dashboard/ParentDashboardScreen.kt`
- `app/src/main/java/com/playit/app/presentation/dashboard/ReportPreviewScreen.kt`
- `app/src/main/java/com/playit/app/data/pdf/PdfExporter.kt`
- `app/src/main/java/com/playit/app/presentation/theme/Color.kt`
- `docs/engineering-package/13_MASTER_TASKS.md`
- `docs/evidence-log.md`

---

## 5. Verification & Tests

1. `LayoutMatrixTest`: Roborazzi screenshot captures verifying unclipped 4-size layout bounds with new grounded stages.
2. `PictureAssetsTest`: Verify all 141 release-manifested assets and map props load properly.
3. `PdfExporterTest` / `ParentDashboardViewModelTest`: Verify PDF generates cleanly without exception and adheres to the non-red palette.
4. `./gradlew testDebugUnitTest`: Full suite passes with 0 failures.
