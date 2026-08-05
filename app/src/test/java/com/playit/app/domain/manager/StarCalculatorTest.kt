package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class StarCalculatorTest {

    @Test
    fun zeroHeartsLost_returnsThreeStars() {
        assertEquals(3, StarCalculator.calculateStars(heartsLost = 0))
    }

    @Test
    fun oneOrTwoHeartsLost_returnsTwoStars() {
        assertEquals(2, StarCalculator.calculateStars(heartsLost = 1))
        assertEquals(2, StarCalculator.calculateStars(heartsLost = 2))
    }

    @Test
    fun threeOrMoreHeartsLost_returnsOneStar() {
        assertEquals(1, StarCalculator.calculateStars(heartsLost = 3))
        assertEquals(1, StarCalculator.calculateStars(heartsLost = 4))
    }
}
