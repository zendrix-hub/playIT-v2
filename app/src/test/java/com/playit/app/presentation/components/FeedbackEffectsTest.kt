package com.playit.app.presentation.components

import com.playit.app.presentation.theme.PlayItMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.absoluteValue

/** The effect curves are pure functions; the modifiers only animate time and apply them. */
class FeedbackEffectsTest {
    @Test fun heartWobble_reducedMotionIsStill() =
        (0L..600L step 50).forEach { assertEquals(0f, heartWobbleAngle(it, reduced = true), 0f) }

    @Test fun heartWobble_movesThenSettles() {
        assertTrue(heartWobbleAngle(100, reduced = false).absoluteValue > 1f)
        assertEquals(0f, heartWobbleAngle(PlayItMotion.STANDARD_MS.toLong(), reduced = false), 0.01f)
    }

    @Test fun correctPop_peaksAt108Percent() {
        val peak = (0L..PlayItMotion.MICRO_MS.toLong() step 10).maxOf { correctPopScale(it, reduced = false) }
        assertEquals(1.08f, peak, 0.01f)
        assertEquals(1f, correctPopScale(80, reduced = true), 0f)
    }
}
