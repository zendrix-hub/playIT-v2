package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * The answer motion of 2026-10-29 (user: bouncy buttons, confetti on right answers, a rapid shake and gentle flash on
 * wrong ones). The curves are pure functions; the modifiers only animate time and apply them.
 */
class AnswerMotionTest {

    @Test fun pressRelease_overshootsThenSettlesAtFullSize() {
        assertEquals(0f, overshoot(0f), 0.0001f)
        assertEquals(1f, overshoot(1f), 0.0001f)
        val peak = (0..100).maxOf { overshoot(it / 100f) }
        assertTrue("an elastic release passes 100 %: $peak", peak > 1.05f)
        assertEquals("tension 3 overshoots by 25 % of the move", 1.25f, peak, 0.001f)
    }

    @Test fun pressRelease_fitsTheRequested200To300Ms() {
        assertTrue(PRESS_OUT_MS in 200..300)
        assertEquals(0.92f, PRESS_SCALE, 0f)   // 03 §4 tap feedback: 100 -> 92 -> 100 %
    }

    @Test fun shake_isRapidAndEndsWhereItStarted() {
        assertEquals(200, SHAKE_MS)
        assertEquals(0f, shakeOffsetAt(0f), 0f)
        assertEquals(0f, shakeOffsetAt(1f), 0f)
        assertEquals(-1f, shakeOffsetAt(0.125f), 0.0001f)
        assertEquals(1f, shakeOffsetAt(0.25f), 0.0001f)
        assertTrue((0..100).all { abs(shakeOffsetAt(it / 100f)) <= 1f })
    }

    @Test fun shake_sayItKeepsItsGentleLength() {
        assertEquals(400, GENTLE_SHAKE_MS)
    }

    @Test fun confettiPop_startsAtTheCentreAndFadesAway() {
        (0 until POP_PIECES).forEach { i ->
            val start = popPieceAt(i, 0f)
            assertEquals(0f, start.dx, 0.0001f)
            assertEquals(0f, start.dy, 0.0001f)
            assertEquals(1f, start.alpha, 0f)
            assertEquals(0f, popPieceAt(i, 1f).alpha, 0f)
        }
    }

    @Test fun confettiPop_fliesOutAllRound() {
        val mid = (0 until POP_PIECES).map { popPieceAt(it, 0.5f) }
        assertTrue(mid.any { it.dx > 0.3f } && mid.any { it.dx < -0.3f })
        assertTrue(mid.any { it.dy < -0.3f } && mid.any { it.dy > 0.3f })
    }

    @Test fun correctionGlow_risesQuicklyThenFadesToNothing() {
        assertEquals(0f, glowAlphaAt(0f), 0f)
        assertEquals(GLOW_PEAK, glowAlphaAt(GLOW_MS / 4f), 0.0001f)
        assertEquals(0f, glowAlphaAt(GLOW_MS.toFloat()), 0f)
        assertTrue("gentle, never a full flash", GLOW_PEAK <= 0.5f)
    }
}
