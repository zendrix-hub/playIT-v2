# Claude AI Collaboration & Prompting Guide for PlayIT

This guide documents the prompting protocol, token-budget management, and prompt structure when pairing with Claude AI for PlayIT's UI/UX redesign.

---

## 1. Chat Lifecycle Protocol: New Chat vs. Current Chat

Every time a prompt is prepared, follow this decision rule:

| Action | When to Use | Why |
|---|---|---|
| **STAY in Current Chat** | Minor follow-ups, tweaking an unverified snippet, or answering Claude's clarifying question on the **same screen**. | Keeps immediate context alive for fast 1-to-1 diffs. |
| **START A NEW CHAT** *(Recommended)* | Moving to a **new screen/module** (e.g. from LetterComplete $\to$ BlendIt), or when approaching **70%+ of your quota**. | **Token accumulation prevention.** Long chats re-send the entire conversation history on every message, consuming 50k+ tokens per turn and triggering rate limits after 3–4 messages. A new chat resets the context window to zero, giving you 15–25+ messages per window. PlayIT's background context is already preserved in Claude's Project Memory. |

---

## 2. The 4-Part Prompt Structure

Always format screen review prompts in this exact 4-part hierarchy:

```markdown
### Part 1: Context Anchor (1–2 lines)
Briefly state the app identity, active phase, and triad benchmarks. Claude already has the memory; this simply activates the right persona.
Example:
"Hi Claude! Continuing our PlayIT presentation-layer refactor (Grade 1 Filipino phonics, Duolingo ABC + Headspace + Drops triad, 64dp button floor, 24sp reading floor, zero-emoji policy)."

### Part 2: Status & Immediate Objective
State what was completed and what specific screen/components are being reviewed.
Example:
"All previous screens (HearIt, SayIt, FindIt, LetterComplete) are verified and passing unit tests. Now reviewing BlendItScreen.kt & BlendItCard.kt."

### Part 3: Tri-Benchmark Review Checklist
Explicitly list what Claude should inspect against our 3 design pillars:
- Duolingo ABC: Chunky gummy tactile depth, bouncy feedback, mascot co-player presence.
- Headspace: Non-punitive Kalamansi amber (#FFB74D) retry, zero red error family, gentle wiggles, reassuring tone.
- Drops: High-contrast card silhouettes, clear reading typography, instant state feedback.
- Pediatric & Architectural Floors: Strict 64dp touch-target floor, 24sp child reading floor, pure Kotlin domain (no android.* in domain).

### Part 4: Complete Code File(s)
Provide the actual Kotlin Compose files in full markdown code blocks.
```

---

## 3. Screen Status & Action Tracker

### Completed & Verified Screens (Passed `./gradlew.bat testDebugUnitTest`)
- [x] **`MapScreen.kt`**: Chocolate Hills background, frosted top bar, rope path, dynamic Lily mascot companion, active node breathing pulse.
- [x] **`HearItScreen.kt` & `LetterCard.kt`**: 88dp play button with expanding pulse ring, 64dp Next button, 24sp text reading floor, breathing pulse animation.
- [x] **`SayItScreen.kt` & `MascotSpeechHeader.kt`**: 88dp mic CTA with audio amplitude pulse ring, 64dp Next button, non-red Kalamansi retry dot, 24sp reading floor.
- [x] **`FindItScreen.kt`, `FindItGrid.kt`, & `GummyBackButton.kt`**: 64dp back button, 64dp bottom buttons, 64dp audio replay pill, 24sp reading floor, Leaf squash-pop / Kalamansi retry shake.
- [x] **`LetterCompleteScreen.kt` & `CelebrationOverlay.kt`**: 64dp continue button, 24sp labels/stats pill, Mango celebration tokens, StarBurst + Sparkle canvas.
- [x] **`BlendItScreen.kt` & `BlendItCard.kt`**:
  - `LessonTopBar` updated with `LessonStep.BLEND_IT` challenge rendering.
  - `onComplete` lifecycle hook connected to `BlendItUiState.SessionComplete`.
  - Gentle Headspace tile wobble (±6°) on incorrect attempt.
  - Kalamansi non-red border stroke on incorrect tile placement.
  - `BlendItCard` elevated to `Cloud`/`CloudShadow` high contrast with 2-line 24sp audio caption.
  - 68dp tile sizes with `idleBounce()` on available bank.
  - 64dp bottom "Check Word" button with 24sp font floor.
- [x] **Parent Dashboard & PDF Export Suite**:
  - `ArithmeticGuardDialog.kt`: GummyContainer keypad (52dp), neutral Sky delete key, decaying gentle wobble.
  - `ParentDashboardScreen.kt`: Normalized container weights, 14sp subtitle, 52dp export button, Exporting state.
  - `PracticeFocusSection.kt`: Kalamansi non-punitive token swap, 14sp secondary text floor.
  - `LearnerHeroCard.kt`: 14sp secondary text floor for streak, mastery ratios, and subtitles.
  - `MasteredSoundsShelf.kt`: Manual 3D static gummy badge depth without false click affordances, merged semantics, 14sp labels.
  - `WordBlendingShelf.kt`: Manual 3D static gummy depth on active pods, Ink high-contrast text on Sky, group numbers in a11y labels.
  - `BadgeCollectionCase.kt`: Translucent 3D shadow depth on unlocked companion stamps, corner lock badge overlay on locked slots.
  - `ProfileSwitcherDropdown.kt`: Rebuilt with GummyContainer (52dp), Lexend typography, Ink color token, and ArrowDropDown icon.
  - `ReportPreviewScreen.kt`: Box-centered header, 14sp secondary text floor, 52dp button heights, remembered file stats.
- [x] **Learner Onboarding & Profile Flow**:
  - `ProfileSelectScreen.kt`: 52dp Parent Zone button, empty-state primary action breathing pulse.
  - `ProfileCard.kt`: 24sp name and star count reading floors, star pluralization fix, 22dp star icon.
  - `AddProfileButton.kt`: 24sp reading floor, overflow ellipsis, breathingPulse for empty primary action, `MAX_LEARNER_PROFILES` bound to `GameplayConstants.MAX_PROFILES`.
  - `AvatarCircle.kt`: Extracted dedicated component loading production assets (`avatar_01` to `06`), palette backgrounds, zero emojis.
  - `NamePromptScreen.kt`: 28sp/24sp typography floors, 64dp button floor, diacritic/letter emoji-defense filter, error-aware mascot reactivity.
  - `AvatarPicker.kt`: 26sp/24sp typography floors, ellipsis protection, 76dp touch targets with bouncy spring animation.
- [x] **`SplashScreen.kt`**:
  - 26sp/24sp speech bubble greeting and value card reading floors.
  - Scrollable viewport container protecting against screen overflow on smaller devices.
  - Pinned 64dp primary CTA ("Tara, Simulan Na! • Get Started") with 24sp text.
  - Canonical `Cloud` card faces and alpha-tinted `Ube` tokens.

---

## 4. Key Architectural Patterns in Active Design System

1. **Pediatric & Adult Floor Rules**:
   - Child touch targets: Minimum 64dp explicit height for all interactive child buttons/cards.
   - Adult touch targets: 52dp explicit height for adult buttons/inputs (`ArithmeticGuardDialog`, `ParentDashboard`, `ReportPreview`).
   - Child reading floor: Strict 24sp minimum for all child-facing text, speech bubbles, card titles, and buttons.
   - Adult reading floor: 14sp minimum floor for secondary labels, subtitles, and captions (never 12–13sp).
2. **Static Gummy Token Pattern**:
   - Static tokens (achievement badges, completion pods) use **manual layered face + shadow Box depth**, never `GummyContainer` (which forces an interactive `onClick` / ripple affordance).
3. **Color Semantic Guardrails**:
   - `Mango` is strictly primary CTA / active state.
   - `Kalamansi` is strictly gentle retry / at-risk / focus guidance (never everyday buttons or delete keys).
   - Zero red error family across the entire learner experience.
4. **Zero-Emoji Policy**:
   - Zero unicode emojis across all child and adult screens. All visuals use vector graphics or production PNGs (`images/rewards/`, `images/mascot/`, `images/pictures/`).
   - Name input sanitization filter prevents emojis from entering the profile database.

---

## 5. Status: All Application Screens Fully Elevated & Verified! 🏆

All screens across PlayIT v2 are now completely elevated, verified against our Tri-Benchmark (Duolingo ABC + Headspace + Drops), and passing the full test suite (`./gradlew.bat testDebugUnitTest`).



