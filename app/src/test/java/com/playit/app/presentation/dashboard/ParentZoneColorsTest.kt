package com.playit.app.presentation.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.presentation.dashboard.components.TileState
import com.playit.app.presentation.theme.LevelColors
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

/**
 * The Parent Zone is easy to read (user decision 2026-11-07, "make sure that parents zone screen is really presented
 * and visible well"): every text colour has a contrast of at least 4.5:1 on what it sits on (WCAG AA), and a tile's
 * state follows its level.
 */
class ParentZoneColorsTest {

    private fun luminance(argb: Int): Double {
        fun ch(v: Int): Double { val c = v / 255.0; return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4) }
        return 0.2126 * ch((argb shr 16) and 0xFF) + 0.7152 * ch((argb shr 8) and 0xFF) + 0.0722 * ch(argb and 0xFF)
    }

    private fun contrast(a: Int, b: Int): Double {
        val (hi, lo) = listOf(luminance(a), luminance(b)).sortedDescending()
        return (hi + 0.05) / (lo + 0.05)
    }

    @Test fun everyTileAndChip_readsAtLeast4point5() {
        TileState.values().forEach { s ->
            val c = contrast(s.ink.toInt(), s.fill.toInt())
            assertTrue("${s.word}: $c", c >= 4.5)
        }
        ProgressLevel.values().forEach { level ->
            val l = LevelColors.of(level)
            val c = contrast(l.ink, l.fill)
            assertTrue("${level.parentLabel}: $c", c >= 4.5)
        }
    }

    @Test fun cardText_readsAtLeast4point5() {
        listOf(TextMidnight, TextMuted).forEach { ink ->
            val c = contrast(ink.toArgb(), SurfaceCard.toArgb())
            assertTrue("$ink on the card: $c", c >= 4.5)
        }
        // The tick on a known letter is white on the tile's ink.
        assertTrue(contrast(Color.White.toArgb(), TileState.KNOWS_IT.ink.toInt()) >= 4.5)
    }

    @Test fun aTilesState_followsItsLevel() {
        assertEquals(TileState.KNOWS_IT, TileState.of(ProgressLevel.OUTSTANDING))
        assertEquals(TileState.KNOWS_IT, TileState.of(ProgressLevel.SATISFACTORY))
        assertEquals(TileState.STILL_LEARNING, TileState.of(ProgressLevel.FAIRLY_SATISFACTORY))
        assertEquals(TileState.STILL_LEARNING, TileState.of(ProgressLevel.DID_NOT_MEET))
        assertEquals(TileState.STILL_LEARNING, TileState.of(ProgressLevel.IN_PROGRESS))
        assertEquals(TileState.NOT_YET, TileState.of(ProgressLevel.NOT_STARTED))
    }
}
