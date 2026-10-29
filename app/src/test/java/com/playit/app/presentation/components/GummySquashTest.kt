package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

/** The correct-answer squash is one boing: it must end at the box's own shape, so a picture is never left stretched. */
class GummySquashTest {

    @Test fun squash_startsAndEndsAtTheBoxsOwnShape() {
        assertEquals(1f to 1f, squashAt(0f))
        val (endX, endY) = squashAt(1f)
        assertEquals(1f, endX, 0.0001f)
        assertEquals(1f, endY, 0.0001f)
    }

    @Test fun squash_peaksAt108By94PercentHalfway() {
        val (x, y) = squashAt(0.5f)
        assertEquals(1.08f, x, 0.0001f)
        assertEquals(0.94f, y, 0.0001f)
    }

    @Test fun squash_neverGoesPastItsPeak() {
        (0..100).map { squashAt(it / 100f) }.forEach { (x, y) ->
            assertEquals(true, x in 1f..1.0801f)
            assertEquals(true, y in 0.9399f..1f)
        }
    }
}
