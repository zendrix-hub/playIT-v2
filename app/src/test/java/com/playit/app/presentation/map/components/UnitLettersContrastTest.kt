package com.playit.app.presentation.map.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

/** "Practice sounds O, B, E, U" blended into the map in stone ink (user, 2026-10-31): it reads on its own plate now. */
class UnitLettersContrastTest {

    private fun contrast(a: Color, b: Color): Float {
        val hi = max(a.luminance(), b.luminance())
        val lo = min(a.luminance(), b.luminance())
        return (hi + 0.05f) / (lo + 0.05f)
    }

    @Test fun theInkReadsOnItsPlate_wellPastWcagAaa() {
        assertTrue(contrast(UnitLettersInk, UnitLettersPlate) >= 7f)
    }

    @Test fun thePlateStandsOutFromTheMap_byItsOutline() {
        // Sampled from the user's screenshot of unit 2, where the rope runs behind the line.
        val mapMint = Color(0xFFB2F1C6)
        val river = Color(0xFF8CEDDA)
        val rope = Color(0xFFCBAB78)
        listOf(mapMint, river, rope).forEach { assertTrue(contrast(UnitLettersLine, it) >= 4.5f) }
    }
}
