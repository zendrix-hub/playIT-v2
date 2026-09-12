package com.playit.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// PlayIT Modern Vibrant Design Language (2026 Grade 1 Edition)
//
// A fresh, joyful, high-energy palette tailored for 6–7 year old learners:
//   • Primary Action:  Electric Royal Joy (#6C5CE7 / #5843E4)
//   • Energy / Stars:  Sunny Buttercup Gold (#FFB800 / #FFA000)
//   • Success / Leaf:  Neon Emerald (#00C853 / #00E676)
//   • Hearts / Mic:    Juicy Strawberry Coral (#FF4757 / #FF6B81)
//   • Hint / Guidance: Radiant Apricot Honey (#FF9F43)
//   • Adventure Sky:   Aqua Marine (#00D2D3 / #0ABDE3)
//   • Modern Outline:  Deep Slate Navy (#1E293B) — clean, sharp, non-muddy
// ═══════════════════════════════════════════════════════════════════════════

// ── 1. Brand & Joy Core Tokens ──────────────────────────────────────────────
val PrimaryJoy = Color(0xFF6C5CE7)
val PrimaryJoyDark = Color(0xFF5843E4)
val PrimaryJoyLight = Color(0xFFEDE9FE)
val PrimaryJoyShadow = Color(0xFF4834D4)

val SunnyGold = Color(0xFFFFB800)
val SunnyGoldDark = Color(0xFFE5A600)
val SunnyGoldLight = Color(0xFFFFF9E6)
val SunnyGoldShadow = Color(0xFFC78C00)

val EmeraldLeaf = Color(0xFF00C853)
val EmeraldLeafDark = Color(0xFF00A844)
val EmeraldLeafLight = Color(0xFFE8F5E9)
val EmeraldLeafShadow = Color(0xFF008A37)

val CoralBerry = Color(0xFFFF4757)
val CoralBerryDark = Color(0xFFEE3848)
val CoralBerryLight = Color(0xFFFFECEF)
val CoralBerryShadow = Color(0xFFD63031)

val ApricotGlow = Color(0xFFFF9F43)
val ApricotGlowDark = Color(0xFFEE8729)
val ApricotGlowLight = Color(0xFFFFF3E0)
val ApricotGlowShadow = Color(0xFFD07018)

val AquaAdventure = Color(0xFF0ABDE3)
val AquaAdventureDark = Color(0xFF09A5C7)
val AquaAdventureLight = Color(0xFFE0F7FA)
val AquaAdventureShadow = Color(0xFF088CA8)

val FuchsiaMagic = Color(0xFFFF6B8B)
val FuchsiaMagicDark = Color(0xFFBE2EDD)
val FuchsiaMagicShadow = Color(0xFF8819A0)

// ── 2. Modern Surfaces & Backdrops ──────────────────────────────────────────
val CanvasLight = Color(0xFFF7FAFC)
val CanvasSoft = Color(0xFFEEF5FF)
val CanvasWarm = Color(0xFFFFF9F0)

val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceCardSubtle = Color(0xFFF8FAFC)
val SurfaceCardShadow = Color(0xFFCBD5E1)

// Clean modern game outlines (sleek slate navy, replacing muddy brown)
val ModernBorder = Color(0xFF1E293B)
val ModernBorderSoft = Color(0xFFCBD5E1)
val ModernBorderFaint = Color(0xFFE2E8F0)

// ── 3. High-Contrast Typography ─────────────────────────────────────────────
val TextMidnight = Color(0xFF1A202C)    // 12.5:1 on White, hyper-legible
val TextMuted = Color(0xFF4A5568)       // WCAG AAA secondary text
val TextLight = Color(0xFF718096)       // Subdued / disabled text

// ── 4. System Mapping & Backward Compatibility Aliases ───────────────────────
// Maps legacy theme variables to the fresh, vibrant system so all screens
// immediately adopt the modern visual identity.

val Mango = SunnyGold
val MangoDark = SunnyGoldDark
val MangoShadow = SunnyGoldShadow

val Ube = PrimaryJoy
val UbeDark = PrimaryJoyDark
val UbeLight = PrimaryJoyLight
val UbeShadow = PrimaryJoyShadow

val Guava = CoralBerry
val GuavaDark = CoralBerryDark
val GuavaShadow = CoralBerryShadow

val Leaf = EmeraldLeaf
val LeafDark = EmeraldLeafDark
val LeafShadow = EmeraldLeafShadow

val Kalamansi = ApricotGlow
val KalamansiDark = ApricotGlowDark
val KalamansiShadow = ApricotGlowShadow

val Tan = Color(0xFFE2C499)
val TanDark = Color(0xFFC79E66)
val TanShadow = Color(0xFFA87D43)
val Rope = Color(0xFFD4A359)
val RopeShadow = Color(0xFFA6762B)

val Ink = TextMidnight
val InkSoft = TextMuted
val InkFaint = TextLight

val Sand = Color(0xFFFFFBF0)               // Luminous fresh cream
val SandDeep = Color(0xFFFFE8B5)
val SandShadow = Color(0xFFE6D2A8)
val Sky = Color(0xFFEEF5FF)                // Bright clean sky
val SkyDeep = Color(0xFFD8EAFF)
val SkyShadow = Color(0xFFBACDE5)
val Cloud = SurfaceCard
val CloudShadow = SurfaceCardShadow

// Upgraded crisp border outline
val DarkBrownOutline = ModernBorder

val DestructiveRed = Color(0xFFE53935)
val DestructiveRedShadow = Color(0xFFB71C1C)

val LearningBlue = Color(0xFF2563EB)
val LearningBlueShadow = Color(0xFF1D4ED8)

val GrowthGreen = EmeraldLeaf
val GrowthGreenShadow = EmeraldLeafShadow
val AchievementGold = SunnyGold
val AchievementGoldShadow = SunnyGoldShadow
val EnergyOrange = ApricotGlow
val EnergyOrangeShadow = ApricotGlowShadow
val FriendlyPurple = PrimaryJoy
val FriendlyPurpleShadow = PrimaryJoyShadow
val GentleCorrectionOrange = ApricotGlow
val GentleCorrectionOrangeShadow = ApricotGlowShadow

val TextPrimary = TextMidnight
val TextSecondary = TextMuted

val SoftSky = Sky
val SoftSkyShadow = SkyShadow
val CreamWhite = SurfaceCard
val CreamWhiteShadow = SurfaceCardShadow

val BorderColor = ModernBorderSoft
val DisabledColor = Color(0xFF94A3B8)
val DisabledColorShadow = Color(0xFF64748B)

val StreakFire = Color(0xFFFF5722)
val StreakFireShadow = Color(0xFFD84315)
val BadgePurple = Color(0xFF8E24AA)
val BadgePurpleShadow = Color(0xFF6A1B9A)

val SunnyYellow = SunnyGold
val SunnyYellowShadow = SunnyGoldShadow
val FriendlyBlue = AquaAdventure
val FriendlyBlueShadow = AquaAdventureShadow
val SoftGreen = Color(0xFF26DE81)
val SoftGreenShadow = Color(0xFF20BF6B)
val SuccessJoy = EmeraldLeaf
val SuccessJoyShadow = EmeraldLeafShadow
val HeartRed = CoralBerry
val HeartRedShadow = CoralBerryShadow
val InkBlue = Color(0xFF1E3A8A)
val SoftSurface = SurfaceCardSubtle

/**
 * Derives a vibrant shadow companion for tactile 3D depth-bands.
 */
fun Color.deriveShadow(): Color {
    return when (this) {
        SunnyGold, Mango -> SunnyGoldShadow
        SunnyGoldDark, MangoDark -> Color(0xFFA67400)
        PrimaryJoy, Ube -> PrimaryJoyShadow
        PrimaryJoyDark, UbeDark -> Color(0xFF3825AB)
        PrimaryJoyLight, UbeLight -> Color(0xFFC7D2FE)
        CoralBerry, Guava -> CoralBerryShadow
        CoralBerryDark, GuavaDark -> Color(0xFFB71C1C)
        EmeraldLeaf, Leaf -> EmeraldLeafShadow
        EmeraldLeafDark, LeafDark -> Color(0xFF00692C)
        ApricotGlow, Kalamansi -> ApricotGlowShadow
        ApricotGlowDark, KalamansiDark -> Color(0xFFA35200)
        Tan -> TanShadow
        TanDark -> Color(0xFF8F6325)
        Rope -> RopeShadow
        Sand -> SandShadow
        Sky -> SkyShadow
        SkyDeep -> Color(0xFFA8C4E6)
        Cloud, SurfaceCard -> SurfaceCardShadow
        ModernBorder, DarkBrownOutline -> Color(0xFF0F172A)
        LearningBlue -> LearningBlueShadow
        GrowthGreen -> GrowthGreenShadow
        AchievementGold -> AchievementGoldShadow
        GentleCorrectionOrange -> GentleCorrectionOrangeShadow
        FriendlyPurple -> FriendlyPurpleShadow
        EnergyOrange -> EnergyOrangeShadow
        DestructiveRed -> DestructiveRedShadow
        CreamWhite -> CreamWhiteShadow
        DisabledColor -> DisabledColorShadow
        StreakFire -> StreakFireShadow
        BadgePurple -> BadgePurpleShadow
        SunnyYellow -> SunnyYellowShadow
        FriendlyBlue -> FriendlyBlueShadow
        SoftGreen -> SoftGreenShadow
        SuccessJoy -> SuccessJoyShadow
        else -> this.copy(red = red * 0.78f, green = green * 0.78f, blue = blue * 0.78f)
    }
}