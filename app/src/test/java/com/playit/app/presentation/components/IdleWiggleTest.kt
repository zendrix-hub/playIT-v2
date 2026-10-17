package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The idle wiggle's shape (card 28b): a short shake that starts and ends level and stays small. */
class IdleWiggleTest {
    @Test fun startsAndEndsLevel() {
        assertEquals(0f, wiggleAngleAt(0f), 0f)
        assertEquals(0f, wiggleAngleAt(WIGGLE_MS.toFloat()), 0f)
        assertEquals(0f, wiggleAngleAt(-50f), 0f)
        assertEquals(0f, wiggleAngleAt(WIGGLE_MS + 50f), 0f)
    }

    @Test fun shakesBothWays() {
        assertEquals(-8f, wiggleAngleAt(100f), 0.001f)
        assertEquals(8f, wiggleAngleAt(250f), 0.001f)
        assertEquals(-4f, wiggleAngleAt(50f), 0.001f)   // halfway to the first key
    }

    @Test fun staysWithinEightDegrees() {
        (0..WIGGLE_MS step 10).forEach { assertTrue("at $it ms", kotlin.math.abs(wiggleAngleAt(it.toFloat())) <= 8f) }
    }

    @Test fun isShort() = assertTrue(WIGGLE_MS <= 800)
}
