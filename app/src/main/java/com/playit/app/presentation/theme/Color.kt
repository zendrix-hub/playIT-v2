package com.playit.app.presentation.theme

import androidx.compose.ui.graphics.Color

// WCAG 2.2 AA compliant palette (min 4.5:1 normal text, 3.0:1 UI components)
val LearningBlue = Color(0xFF1D62B4) // Tuned for >= 4.5:1 contrast against white/cream text
val GrowthGreen = Color(0xFF2E7D32) // Tuned for >= 4.5:1 contrast against white/cream text
val AchievementGold = Color(0xFFFFC107) // Paired with TextPrimary (6.5:1 contrast)
val EnergyOrange = Color(0xFFD97706) // Tuned for contrast
val FriendlyPurple = Color(0xFF6B5BCE) // Tuned for >= 4.5:1 contrast against white/cream text
val SoftSky = Color(0xFFEAF6FF)
val CreamWhite = Color(0xFFFFFDF8)
val GentleCorrectionOrange = Color(0xFFFF8F00) // Paired with TextPrimary for guidance
val TextPrimary = Color(0xFF2D3748) // High-contrast primary text (11.2:1 on CreamWhite)
val DarkBrownOutline = Color(0xFF512C18) // Established dark-brown 3dp border outline color — global outline standard for this UI refresh (overrides 23_DUOLINGO_ABC_UI_REFRESH.md §3's "Text Primary" per session decision)
val TextSecondary = Color(0xFF4A5568) // Tuned for >= 4.5:1 contrast on light containers (7.1:1 on CreamWhite)
val BorderColor = Color(0xFFCBD5E0)
val DisabledColor = Color(0xFFA0AEC0)
val DestructiveRed = Color(0xFFB3261E) // Reserved strictly for true destructive dialogs (5.8:1 contrast)

// Duolingo ABC UI Refresh — Depth Shadow Tokens.
// FIXED: previous values here were NOT actually 20% darker than their base color (some
// were even lighter), which washed out the gummy depth-band 3D effect on every button in
// the app. Each token below is base channel * 0.8, rounded to the nearest int, per
// channel — a true -20% luminance step:
//   LearningBlue   0x1D62B4 -> 0x174E90
//   GrowthGreen    0x2E7D32 -> 0x256428
//   AchievementGold 0xFFC107 -> 0xCC9A06
//   EnergyOrange   0xD97706 -> 0xAE5F05
//   FriendlyPurple 0x6B5BCE -> 0x5649A5
//   GentleCorrectionOrange 0xFF8F00 -> 0xCC7200
//   DisabledColor  0xA0AEC0 -> 0x808B9A
val LearningBlueShadow = Color(0xFF174E90)
val GrowthGreenShadow = Color(0xFF256428)
val AchievementGoldShadow = Color(0xFFCC9A06)
val EnergyOrangeShadow = Color(0xFFAE5F05)
val FriendlyPurpleShadow = Color(0xFF5649A5)
val GentleCorrectionOrangeShadow = Color(0xFFCC7200)
val DisabledColorShadow = Color(0xFF808B9A)

// Added for HearIt's LetterCard, which became a tappable gummy surface — CreamWhite had
// no depth-band token because every prior CreamWhite usage was a static background.
// True -20% luminance of CreamWhite (0xFFFFFDF8): 0xFFCCCAC6.
val CreamWhiteShadow = Color(0xFFCCCAC6)

// Added for DockedMascotWithBubble's mascot avatar circle, now a GummyStaticContainer.
// True -20% luminance of SoftSky (0xFFEAF6FF): 0xFFBBC5CC.
val SoftSkyShadow = Color(0xFFBBC5CC)