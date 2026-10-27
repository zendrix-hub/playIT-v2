package com.playit.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// PlayIT "Lily's island" (design refresh, branch design/visual-refresh, 2026-10-27)
//
// The colours come from Lily and her island, and the UI shares the pictures' dark-brown line, so the
// screens and the art look drawn by the same hand. The token names are the old ones, so every screen follows.
//   • Primary action:  Lagoon teal (#0B7F7A); white text on it 4.8:1
//   • Listen / stars:  Sunflower (#FFC53D); brown text on it 7.9:1
//   • Success:         Leaf (#2E9A3E); white text on it 3.6:1 (large text only)
//   • Hearts / retry:  Hibiscus (#F2545B)
//   • Hint:            Apricot (#F7922F)
//   • Sky:             Sea sky (#3BA7D9)
//   • Outline, text:   Lily brown (#4A2E18, the art's line); text #3B2416
//   • Ground:          morning sky (#E1F3FA) to sand (#FFF2DC); cards are shell white (#FFFDF8)
// ═══════════════════════════════════════════════════════════════════════════

// ── 1. Brand & Joy Core Tokens ──────────────────────────────────────────────
val PrimaryJoy = Color(0xFF0B7F7A)
val PrimaryJoyDark = Color(0xFF096C68)
val PrimaryJoyLight = Color(0xFFDDF2F0)
val PrimaryJoyShadow = Color(0xFF075C58)

val SunnyGold = Color(0xFFFFC53D)
val SunnyGoldDark = Color(0xFFF2B01E)
val SunnyGoldLight = Color(0xFFFFF6DC)
val SunnyGoldShadow = Color(0xFFDFA019)

val EmeraldLeaf = Color(0xFF2E9A3E)
val EmeraldLeafDark = Color(0xFF268434)
val EmeraldLeafLight = Color(0xFFE6F5E7)
val EmeraldLeafShadow = Color(0xFF1F6E2B)

val CoralBerry = Color(0xFFF2545B)
val CoralBerryDark = Color(0xFFDE434A)
val CoralBerryLight = Color(0xFFFDE8E9)
val CoralBerryShadow = Color(0xFFC2383E)

val ApricotGlow = Color(0xFFF7922F)
val ApricotGlowDark = Color(0xFFE57F1D)
val ApricotGlowLight = Color(0xFFFFF0DF)
val ApricotGlowShadow = Color(0xFFC86A12)

val AquaAdventure = Color(0xFF3BA7D9)
val AquaAdventureDark = Color(0xFF2B90BF)
val AquaAdventureLight = Color(0xFFE3F3FB)
val AquaAdventureShadow = Color(0xFF227AA3)

val FuchsiaMagic = Color(0xFFE86A9A)
val FuchsiaMagicDark = Color(0xFFC94F7E)
val FuchsiaMagicShadow = Color(0xFFA33D65)

// ── 2. Modern Surfaces & Backdrops ──────────────────────────────────────────
val CanvasLight = Color(0xFFFFF9EF)
val CanvasSoft = Color(0xFFE1F3FA)
val CanvasWarm = Color(0xFFFFF2DC)

// The screen ground: morning sky at the top to sand at the bottom (was a literal on six screens)
val GroundSky = Color(0xFFE1F3FA)
val GroundHaze = Color(0xFFF3F5EC)
val GroundSand = Color(0xFFFFF2DC)
val GroundColors = listOf(GroundSky, GroundHaze, GroundSand)

// Locked and inactive things: warm stone instead of cool slate, so they sit with the art
val LockedFace = Color(0xFFF3EEE6)
val LockedShelf = Color(0xFFDCD2C3)
val LockedIcon = Color(0xFFA89884)
val LockedInk = Color(0xFF7D6C5A)

val SurfaceCard = Color(0xFFFFFDF8)
val SurfaceCardSubtle = Color(0xFFFBF6EC)
val SurfaceCardShadow = Color(0xFFEADCC4)

// Outlines in Lily brown, the line of every picture (style guide #4A2E18)
val ModernBorder = Color(0xFF4A2E18)
val ModernBorderSoft = Color(0xFFE2D3BC)
val ModernBorderFaint = Color(0xFFEFE5D5)

// ── 3. High-Contrast Typography ─────────────────────────────────────────────
val TextMidnight = Color(0xFF3B2416)    // 14:1 on the shell-white cards
val TextMuted = Color(0xFF6E5643)       // secondary text, 6.6:1 on shell white
val TextLight = Color(0xFF8A6F58)       // subdued text, 4.6:1 on shell white

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

val Sand = Color(0xFFFFF8EA)
val SandDeep = Color(0xFFFFE6B8)
val SandShadow = Color(0xFFE6D2A8)
val Sky = Color(0xFFE1F3FA)
val SkyDeep = Color(0xFFCBEAF5)
val SkyShadow = Color(0xFFA9D2E3)
val Cloud = SurfaceCard
val CloudShadow = SurfaceCardShadow

// Upgraded crisp border outline
val DarkBrownOutline = ModernBorder

// Lily the tarsier, sampled from images/mascot/lily_idle.png (image release 2026-10-12-mascot), so the
// drawn splash tarsier matches her pictures. LilyOutline is the art's dark-brown line (style guide #4A2E18).
val LilyOrange = Color(0xFFF97B27)
val LilyOrangeShade = Color(0xFFDD621F)
val LilyEarInner = Color(0xFFFEAC7A)
val LilyCream = Color(0xFFFDEBD3)
val LilyBlush = Color(0xFFFC9898)
val LilyPupil = Color(0xFF3B1F0A)
val LilyOutline = Color(0xFF4A2E18)
// Her open mouth, sampled from lily_celebrating.png, for the mouth TalkingLily draws
val LilyMouthInside = Color(0xFF402517)
val LilyTongue = Color(0xFFDE5147)
val LilyTeeth = Color(0xFFFFF8EE)

val DestructiveRed = Color(0xFFE53935)
val DestructiveRedShadow = Color(0xFFB71C1C)

val LearningBlue = Color(0xFF2F7FC1)
val LearningBlueShadow = Color(0xFF236496)

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
val DisabledColor = Color(0xFFB9AC9B)
val DisabledColorShadow = Color(0xFF8E7F6C)

val StreakFire = Color(0xFFFF5722)
val StreakFireShadow = Color(0xFFD84315)
val BadgePurple = Color(0xFF8E24AA)
val BadgePurpleShadow = Color(0xFF6A1B9A)

val SunnyYellow = SunnyGold
val SunnyYellowShadow = SunnyGoldShadow
val FriendlyBlue = AquaAdventure
val FriendlyBlueShadow = AquaAdventureShadow
val SoftGreen = Color(0xFF5CC46A)
val SoftGreenShadow = Color(0xFF2E9A3E)
val SuccessJoy = EmeraldLeaf
val SuccessJoyShadow = EmeraldLeafShadow
val HeartRed = CoralBerry
val HeartRedShadow = CoralBerryShadow
val InkBlue = Color(0xFF3B2416)
val SoftSurface = SurfaceCardSubtle

/**
 * Derives a vibrant shadow companion for tactile 3D depth-bands.
 */
fun Color.deriveShadow(): Color {
    return when (this) {
        SunnyGold, Mango -> SunnyGoldShadow
        SunnyGoldDark, MangoDark -> Color(0xFFB98411)
        PrimaryJoy, Ube -> PrimaryJoyShadow
        PrimaryJoyDark, UbeDark -> Color(0xFF064E4B)
        PrimaryJoyLight, UbeLight -> Color(0xFFB4E0DC)
        CoralBerry, Guava -> CoralBerryShadow
        CoralBerryDark, GuavaDark -> Color(0xFFB71C1C)
        EmeraldLeaf, Leaf -> EmeraldLeafShadow
        EmeraldLeafDark, LeafDark -> Color(0xFF226329)
        ApricotGlow, Kalamansi -> ApricotGlowShadow
        ApricotGlowDark, KalamansiDark -> Color(0xFFA35200)
        Tan -> TanShadow
        TanDark -> Color(0xFF8F6325)
        Rope -> RopeShadow
        Sand -> SandShadow
        Sky -> SkyShadow
        SkyDeep -> Color(0xFF9CCDE0)
        Cloud, SurfaceCard -> SurfaceCardShadow
        ModernBorder, DarkBrownOutline -> Color(0xFF2E1C0F)
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