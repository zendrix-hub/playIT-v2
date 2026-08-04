# 10 — UI Implementation Guide

How to actually build the Compose UI. This is the practical, screen-by-screen companion to `03_DESIGN_SYSTEM_SUMMARY.md`. **Reminder: `Wireframes.md` is not a source for this document** — see `04_RESEARCH_SUMMARY.md §0`. The layout ground truth is the SDD's inline mockup descriptions per module, cross-referenced with the SDD's Front-end Component tables (already fully enumerated in `07_FOLDER_STRUCTURE.md`).

## 1. Theming Setup

- Implement `presentation/theme/` using Material 3's `Typography`/`ColorScheme`/`Shapes` overrides, per `03`. Do not use M3 dynamic/wallpaper-derived color — this app uses a fixed custom palette (`03 §2`).
- Apply Material 3 Expressive's spring-based `MotionScheme` where the Compose version in use supports it (`04 §4`); if unavailable in the pinned Compose version, hand-roll `spring()` animation specs with the damping/stiffness values in §4 below rather than falling back to duration-tweens.

## 2. Typography — Concrete Type Scale (implements `03 §5.2`)

Two type scales, selected per screen based on audience:

**Child-facing scale (Lexend variable / Andika fallback)** — used on Splash, Profile, Map, Hear It, Say It, Find It, Letter Complete, Blend It, Blend It Complete:

| Token | Size | Weight | Use |
|---|---|---|---|
| `displayLarge` | 40sp | ExtraBold | Letter cards, celebration screens |
| `headlineLarge` | 28sp | Bold | Screen titles ("Word Adventures!") |
| `titleMedium` | 22sp | SemiBold | Instructions, mascot prompts |
| `bodyLarge` | 24sp | Medium | Anything the child is meant to sound out — raised from the shipped 18sp per `03 §5.2` |
| `labelLarge` | 18sp | Medium | Button text — **sentence case only, never all-caps** (`04 §6.2`) |

**Adult-facing scale (Nunito/Poppins, shipped Design System scale, unchanged)** — used on Parent Dashboard, Report Preview only: Heading 28sp/Bold, Subheading 22sp/SemiBold, Body 18sp/Medium, Caption 16sp/Regular.

Implementation note: this means **two `FontFamily` definitions coexist** in the theme, selected per-screen (not a single global override) — wrap child-facing screens in a `CompositionLocalProvider(LocalTextStyle...)` or a dedicated `PlayItChildTheme` wrapper rather than threading font choice through every individual `Text()` call.

## 3. Touch Targets & Spacing (implements `03 §5.3`)

- **64dp minimum** for every interactive element on the 9 child-facing screens (letter tiles, picture cards, mic button, map nodes, all buttons) — this supersedes the shipped Design System's 48dp general floor for this app specifically.
- **48dp/56dp** remain acceptable on Parent Dashboard / Report Preview only.
- 8dp base spacing scale (`03 §4`) applies everywhere.

## 4. Motion Parameters (implements `04 §4`)

| Interaction | Spec | Damping/Stiffness |
|---|---|---|
| Button/tile tap | `animateScaleAsState` (100%→92%→100%) | `MediumBouncy` / `Low` |
| Screen transitions | `slideInHorizontally` + `fadeIn` | `NoBouncy` / `Medium` (critically damped — no overshoot) |
| Incorrect-answer shake | `animateOffsetAsState` | `HighBouncy` / `High`, short duration |
| Reward modal entrance | `AnimatedContent` | `LowBouncy` / `Low` |
| Color/opacity changes (Effect tokens) | never bouncy | fully damped, no overshoot — a pulsing color reads as a bug, not delight |

All microinteractions: 200–400ms. Screen entry: fade + slight upward motion, 200–300ms (`03 §4`).

## 5. Screen-by-Screen Notes

### `SplashScreen`
Loading state must actively mask Vosk-model + Room-init latency (target ≤5s worst case) — use a looping mascot animation or accelerating progress indicator, never a static spinner (`04 §5`).

### `ProfileSelectScreen` / `NamePromptScreen`
Grid of `ProfileCard`s (avatar, name, last-played date, total stars) + `AddProfileButton`, hidden/disabled at 6 profiles (`01 §5`). `AvatarPicker` is a grid of curated animal avatars (`14_ASSET_MANIFEST.md`) — no free-text/photo upload, no external image picker.

### `MapScreen`
Winding-path layout per the SDD mockup (28 `LetterNode`s + 7 `BlendItChallengeNode`s connected by `PathConnector`s, `TopStatsBar` showing streak + profile name + total stars, `MascotBubble` for contextual prompts). Use `LazyColumn`/stable keys for the node list (`05 §4` performance note) — do not render all 35 nodes as a single non-lazy `Column` on low-end hardware. Locked nodes render visibly dimmed/locked, not hidden.

### `HearItScreen`
`AnimatedLetterCard` + `PlayButton` (large, center) + `ReplayCounter` (dot indicators) + `MascotBubble` (prompt changes after first play) + `NextButton` (disabled until ≥1 full playback — `01 §1 Module 1`).

### `SayItScreen`
`HeartDisplay` (5 hearts) + `PhonemePromptCard` + `MicrophoneButton` (hold-to-record, pulse while held) + `ListeningAnimation` + `FeedbackCard` (binary green/red) + `AttemptTracker` (max 5 dots) + `NoiseLevelIndicator` (red above 40dB, `01 §2`) + `HeartRecoveryAnimation`. Implement the pre-mic "quiet as a mouse" 3-second check here (`04 §7`) before the recognizer activates.

### `FindItScreen`
`HeartDisplay` (continues the Say It pool per `01 §1 Module 3`'s pre-condition — reset to 5) + `ImageGrid` (5-card responsive grid) + `PictureCard` (tap → green/red flash) + `ScoreIndicator` ("X of 3") + `MascotBubble` + `CompletionAnimation`.

### `LetterCompleteScreen`
`StarAnimation` — 1–3 stars, bounce drop-in, per `03 §4`'s Star Reward spec (drop/bounce/glow + confetti + sound).

### `BlendItScreen`
`TargetWordImage` (tap replays audio) + `WordAudioButton` (auto-plays on load) + `LetterSlotRow`/`LetterSlot` (tap-to-place, tap-to-remove — **never drag-only**, per `04 §4`'s WCAG 2.5.7 note) + `TileBank`/`LetterTile` (shuffled, correct + 1–3 distractors) + `SubmitButton` (enabled only when all slots filled) + `WordFeedbackCard` + `HintIndicator` (fires after 2 wrong attempts on the same word) + `HeartDisplay` (fresh 5, no restart-with-3 on depletion — session just ends, `01 §1 Module 4`) + `BlendItProgressIndicator` ("X/5 words").

### `BlendItCompleteScreen`
Session summary + `StarAnimation`, using `BlendItStarThresholds` (`01 §7.4`).

### `ParentDashboardScreen`
Entry gated by a simple arithmetic `AlertDialog` (`01 §7.6`, `09 §3`). `ProfileSwitcherDropdown` + `OverallStatsCard` + `LetterPerformanceTable` (28 rows, color-coded) + `BlendItSummaryCard` + `AtRiskSection` + `ExportButton`. Use the adult type/contrast scale (`§2` above, 7:1 preferred per `03 §6`).

### `ReportPreviewScreen`
PDF content preview + save-to-storage button, scoped to the dashboard's selected `profileId` (not `activeProfileId` — `09 §3`).

## 6. Accessibility Checklist (apply per-screen during implementation, not as a final pass)

- Every instruction: text **and** audio, always.
- Never color-only signaling — pair with icon/shape/label/animation (`03 §6`).
- 4.5:1 text contrast minimum; 7:1 on Parent Dashboard.
- Reduced-motion: swap particle/bounce effects for fades when the system accessibility setting is on — **flagging again that there's currently no in-app Settings screen to host a manual override; system-level `Settings.Global` reduced-motion detection should still be honored even without an in-app toggle** (`03 §6`, `13`).
