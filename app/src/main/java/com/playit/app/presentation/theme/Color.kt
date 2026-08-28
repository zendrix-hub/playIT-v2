package com.playit.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// PlayIT Filipino-Themed Palette — Phase 10 UI Redesign
//
// Derived from playit-mockup.html with these stakeholder-confirmed
// conflict resolutions (see 13_MASTER_TASKS.md Phase 10 Open Decisions):
//
//   • Fonts:     Lexend + Andika (single-story a/g, pedagogically correct)
//   • Errors:    Warm orange/pink, NEVER red (03_DESIGN_SYSTEM_SUMMARY §2)
//   • Outlines:  DarkBrownOutline 3dp borders RETAINED
//   • Mascot:    Lily (Tiko kept as future alternative)
//   • Culture:   Chocolate Hills / palm / nipa hut theming approved
//
// Shadow convention: each face color has a -20% per-channel luminance
// companion for the gummy depth-band (channel × 0.8, rounded to nearest
// int). This matches the convention established in Phase 9.
// ═══════════════════════════════════════════════════════════════════════════


// ── Mango: Primary accent ──────────────────────────────────────────────────
// CTAs, streak pill, current-node highlight, warm encouragement
val Mango = Color(0xFFFFC93C)
val MangoDark = Color(0xFFEFA400)           // gradient / pressed variant
val MangoShadow = Color(0xFFCCA130)         // depth-band: FF→CC, C9→A1, 3C→30

// ── Ube: Learning / challenge accent ───────────────────────────────────────
// Play buttons, progress bars, Blend It, challenge screens
val Ube = Color(0xFF8B5FBF)
val UbeDark = Color(0xFF6E3FA3)             // gradient / pressed variant
val UbeLight = Color(0xFFEEE3F8)            // pill backgrounds, tinted surfaces
val UbeShadow = Color(0xFF6F4C99)           // depth-band: 8B→6F, 5F→4C, BF→99

// ── Guava: Interaction accent (warm pink) ──────────────────────────────────
// Mic button, hearts, interactive highlights — warm pink, NOT red
val Guava = Color(0xFFFF6F91)
val GuavaDark = Color(0xFFE14C71)           // gradient / pressed variant
val GuavaShadow = Color(0xFFCC5974)         // depth-band: FF→CC, 6F→59, 91→74

// ── Leaf: Success ──────────────────────────────────────────────────────────
// Correct answers, complete nodes, positive feedback
val Leaf = Color(0xFF45AE6D)
val LeafDark = Color(0xFF2E8A51)            // gradient / pressed variant
val LeafShadow = Color(0xFF378B57)          // depth-band: 45→37, AE→8B, 6D→57

// ── Kalamansi: Gentle correction (warm orange) ─────────────────────────────
// Incorrect answers, retry prompts, guidance — NEVER red.
// Mockup's #FF5A5F replaced with warm amber-orange per the "no red for
// errors" rule (03_DESIGN_SYSTEM_SUMMARY §2, stakeholder-confirmed).
val Kalamansi = Color(0xFFFFB74D)
val KalamansiDark = Color(0xFFE69500)       // gradient / pressed variant
val KalamansiShadow = Color(0xFFCC923E)     // depth-band: FF→CC, B7→92, 4D→3E

// ── Tan / Rope: Cultural decoration ────────────────────────────────────────
// Chocolate Hills, rattan weave, nipa hut, cultural map decorations
val Tan = Color(0xFFC9A06B)
val TanDark = Color(0xFF8A6A42)
val TanShadow = Color(0xFFA18056)           // depth-band: C9→A1, A0→80, 6B→56
val Rope = Color(0xFFB98A4F)                // trail / dashed-path color
val RopeShadow = Color(0xFF946E3F)          // depth-band: B9→94, 8A→6E, 4F→3F

// ── Ink: Text ──────────────────────────────────────────────────────────────
// Ink replaces TextPrimary (#2D3748 → #1F3A3D); 10.2:1 on CreamWhite.
// InkSoft replaces TextSecondary (#4A5568 → #506B6E); WCAG-tuned from
// mockup's #5C7679 (which only achieves 4.3:1) to 5.6:1 on CreamWhite.
val Ink = Color(0xFF1F3A3D)                 // primary text
val InkSoft = Color(0xFF506B6E)             // secondary text (WCAG 5.6:1)
val InkFaint = Color(0xFF9DB0B2)            // disabled / placeholder (decorative only)

// ── Sand / Sky / Cloud: Surfaces ───────────────────────────────────────────
val Sand = Color(0xFFFFF3D9)                // warm card surfaces (letter cards)
val SandDeep = Color(0xFFEBD9A6)            // gradient accent
val SandShadow = Color(0xFFCCC2AE)          // depth-band: FF→CC, F3→C2, D9→AE
val Sky = Color(0xFFEAF6FF)                 // main background (= original SoftSky)
val SkyDeep = Color(0xFFCFE9FF)             // gradient accent
val SkyShadow = Color(0xFFBBC5CC)           // depth-band: EA→BB, F6→C5, FF→CC
val Cloud = Color(0xFFFFFFFF)               // white surfaces
val CloudShadow = Color(0xFFCCCCCC)         // depth-band


// ── Outline ────────────────────────────────────────────────────────────────
// Global 3dp border standard — RETAINED per stakeholder decision.
val DarkBrownOutline = Color(0xFF512C18)

// ── Destructive red (system dialogs only) ──────────────────────────────────
// Reserved STRICTLY for delete-profile / storage-full / system-error dialogs.
// Never used for gameplay incorrect-answer feedback.
val DestructiveRed = Color(0xFFB3261E)       // 5.8:1 contrast on white
val DestructiveRedShadow = Color(0xFF8F1E18) // depth-band: B3→8F, 26→1E, 1E→18


// ═══════════════════════════════════════════════════════════════════════════
// Legacy Semantic Aliases
//
// Pre-Phase-10 color names used across existing screens. Each retains its
// original WCAG-tuned hex value (contrast-sensitive) or is remapped where
// the design decision explicitly calls for a value change.
//
// Screens being redesigned in T2–T6 should migrate to the Filipino tokens
// (Mango, Ube, Guava, Leaf, Kalamansi, etc.) directly.
// ═══════════════════════════════════════════════════════════════════════════

// LearningBlue: no direct Filipino alias — retained as-is for components
// that still reference it.  T2–T6 will migrate to Ube or Mango per screen.
val LearningBlue = Color(0xFF1D62B4)
val LearningBlueShadow = Color(0xFF174E90)

// Legacy palette — original WCAG-tuned values, kept for backward compat.
// New screens should use Filipino tokens (Leaf, Mango, Ube) instead.
val GrowthGreen = Color(0xFF2E7D32)
val GrowthGreenShadow = Color(0xFF256428)
val AchievementGold = Color(0xFFFFC107)
val AchievementGoldShadow = Color(0xFFCC9A06)
val EnergyOrange = Color(0xFFD97706)
val EnergyOrangeShadow = Color(0xFFAE5F05)
val FriendlyPurple = Color(0xFF6B5BCE)
val FriendlyPurpleShadow = Color(0xFF5649A5)
val GentleCorrectionOrange = Color(0xFFFF8F00)
val GentleCorrectionOrangeShadow = Color(0xFFCC7200)

// Text colors: remapped to Ink variants.
// Value change: TextPrimary #2D3748→#1F3A3D, TextSecondary #4A5568→#506B6E
val TextPrimary = Ink
val TextSecondary = InkSoft

// Surface colors
val SoftSky = Sky                            // same hex #EAF6FF, now aliased
val SoftSkyShadow = SkyShadow
val CreamWhite = Color(0xFFFFFDF8)           // distinct from Sand — keeps existing card surfaces
val CreamWhiteShadow = Color(0xFFCCCAC6)

// UI utility
val BorderColor = Color(0xFFCBD5E0)
val DisabledColor = Color(0xFFA0AEC0)
val DisabledColorShadow = Color(0xFF808B9A)


// ═══════════════════════════════════════════════════════════════════════════
// Semantic Tokens — TopStatsBar
// ═══════════════════════════════════════════════════════════════════════════
val StreakFire = Color(0xFFE64A19)
val StreakFireShadow = Color(0xFFB83A14)
val BadgePurple = Color(0xFF7B1FA2)
val BadgePurpleShadow = Color(0xFF621982)


// ═══════════════════════════════════════════════════════════════════════════
// Celebration / Stitch UI Colors (CelebrationOverlay.kt)
// ═══════════════════════════════════════════════════════════════════════════
val SunnyYellow = Color(0xFFFFD700)
val SunnyYellowShadow = Color(0xFFC79A00)
val FriendlyBlue = Color(0xFF4CC9F0)
val FriendlyBlueShadow = Color(0xFF006780)
val SoftGreen = Color(0xFF72EFDD)
val SoftGreenShadow = Color(0xFF209A65)
val SuccessJoy = Color(0xFF2DCE89)
val SuccessJoyShadow = Color(0xFF209A65)
val HeartRed = Guava                         // remapped: warm pink, NOT red
val HeartRedShadow = GuavaShadow
val InkBlue = Color(0xFF023E8A)
val SoftSurface = Color(0xFFF8F9FA)