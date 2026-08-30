package com.playit.app.presentation.map.components

import androidx.compose.ui.graphics.Color

/**
 * Visual theme data for each of the 6 Bohol geographical biomes (derived from tools/ reference images):
 * 1. Chocolate Hills (tools/chocolate_hills.jpg) - Sunny Gold & Meadow Green
 * 2. Loboc River (tools/loboc_river.jpg) - Jade River & Canyon Canopy
 * 3. Panglao Coral (tools/panglao_coral.jpg) - Azure Ocean & Coral Reef
 * 4. Tarsier Forest (tools/tarsier_forest.webp) - Emerald Jungle & Moss Canopy
 * 5. Mountain Summit (tools/mountain_summit.jpg) - Sunset Amber & Twilight Violet
 * 6. Baclayon Heritage (tools/baclayon.jpg) - Coral Stone & Terracotta Red
 */
data class BiomeTheme(
    val sectionNumber: Int,
    val title: String,
    val lettersSummary: String,
    val mascotDialogue: String,
    val primaryColor: Color,
    val shelfColor: Color,
    val backgroundTint: Color,
    val borderTint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val pillBg: Color,
    val progressColor: Color
)

object BiomeThemes {
    val SECTION_1 = BiomeTheme(
        sectionNumber = 1,
        title = "Chocolate Hills Phonics",
        lettersSummary = "Practice sounds M, S, A, I",
        mascotDialogue = "Welcome to Chocolate Hills! Let's master the first phonemes!",
        primaryColor = Color(0xFFFFC800),
        shelfColor = Color(0xFFE5A500),
        backgroundTint = Color(0xFFFEFCE8),
        borderTint = Color(0xFFFDE047),
        textPrimary = Color(0xFF451A03),
        textSecondary = Color(0xFF78350F),
        pillBg = Color(0xFFFEF08A),
        progressColor = Color(0xFFEAB308)
    )

    val SECTION_2 = BiomeTheme(
        sectionNumber = 2,
        title = "Loboc River Valley",
        lettersSummary = "Practice sounds O, B, E, U",
        mascotDialogue = "Navigating the Loboc River! Listen closely to the sounds!",
        primaryColor = Color(0xFF0D9488),
        shelfColor = Color(0xFF0F766E),
        backgroundTint = Color(0xFFF0FDFA),
        borderTint = Color(0xFF99F6E4),
        textPrimary = Color(0xFF134E4A),
        textSecondary = Color(0xFF115E59),
        pillBg = Color(0xFFCCFBF1),
        progressColor = Color(0xFF14B8A6)
    )

    val SECTION_3 = BiomeTheme(
        sectionNumber = 3,
        title = "Panglao Coral Shore",
        lettersSummary = "Practice sounds T, K, L, Y",
        mascotDialogue = "Diving into Panglao Coral Shore! Catch new letter sounds!",
        primaryColor = Color(0xFF0284C7),
        shelfColor = Color(0xFF0369A1),
        backgroundTint = Color(0xFFF0F9FF),
        borderTint = Color(0xFFBAE6FD),
        textPrimary = Color(0xFF0C4A6E),
        textSecondary = Color(0xFF0369A1),
        pillBg = Color(0xFFE0F2FE),
        progressColor = Color(0xFF0EA5E9)
    )

    val SECTION_4 = BiomeTheme(
        sectionNumber = 4,
        title = "Tarsier Forest Sanctuary",
        lettersSummary = "Practice sounds N, G, NG, P",
        mascotDialogue = "Deep in the Tarsier Forest Sanctuary! Keep up the great pace!",
        primaryColor = Color(0xFF15803D),
        shelfColor = Color(0xFF166534),
        backgroundTint = Color(0xFFF0FDF4),
        borderTint = Color(0xFFBBF7D0),
        textPrimary = Color(0xFF14532D),
        textSecondary = Color(0xFF166534),
        pillBg = Color(0xFFDCFCE7),
        progressColor = Color(0xFF22C55E)
    )

    val SECTION_5 = BiomeTheme(
        sectionNumber = 5,
        title = "Bohol Mountain Summit",
        lettersSummary = "Practice sounds R, D, H, W",
        mascotDialogue = "Reaching the Bohol Mountain Summit! You're almost a master!",
        primaryColor = Color(0xFFD97706),
        shelfColor = Color(0xFFB45309),
        backgroundTint = Color(0xFFFFF7ED),
        borderTint = Color(0xFFFED7AA),
        textPrimary = Color(0xFF7C2D12),
        textSecondary = Color(0xFF9A3412),
        pillBg = Color(0xFFFFEDD5),
        progressColor = Color(0xFFF97316)
    )

    val SECTION_6 = BiomeTheme(
        sectionNumber = 6,
        title = "Baclayon Heritage",
        lettersSummary = "Practice sounds C, F, J, Ñ, Q, V, X, Z",
        mascotDialogue = "At historic Baclayon Heritage! Complete the final adventure!",
        primaryColor = Color(0xFFEA580C),
        shelfColor = Color(0xFFC2410C),
        backgroundTint = Color(0xFFFFFBEB),
        borderTint = Color(0xFFFDE68A),
        textPrimary = Color(0xFF78350F),
        textSecondary = Color(0xFF92400E),
        pillBg = Color(0xFFFEF3C7),
        progressColor = Color(0xFFF59E0B)
    )

    fun forSection(groupNumber: Int): BiomeTheme {
        return when (groupNumber) {
            1 -> SECTION_1
            2 -> SECTION_2
            3 -> SECTION_3
            4 -> SECTION_4
            5 -> SECTION_5
            6 -> SECTION_6
            else -> SECTION_1
        }
    }
}
