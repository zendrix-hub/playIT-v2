package com.playit.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun letterComplete_carriesHeartsLost() {
        assertEquals("letter_complete/3?heartsLost=2", Routes.letterComplete("3", 2))
    }

    @Test
    fun blendItComplete_carriesResults() {
        assertEquals(
            "blendit_complete/1?heartsLost=1&wordsCorrect=4&totalWords=5",
            Routes.blendItComplete("1", 1, 4, 5)
        )
    }

    @Test
    fun letterComplete_defaultHeartsLostIsZero() {
        assertEquals("letter_complete/3?heartsLost=0", Routes.letterComplete("3"))
    }

    @Test
    fun blendItComplete_defaultsAreReasonable() {
        assertEquals(
            "blendit_complete/1?heartsLost=0&wordsCorrect=5&totalWords=5",
            Routes.blendItComplete("1")
        )
    }
}
