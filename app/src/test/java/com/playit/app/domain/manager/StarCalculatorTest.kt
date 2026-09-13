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

    @Test
    fun fractionalAccuracy_normalizedCorrectly() {
        // 1.0f should be normalized to 100%
        assertEquals(3, StarCalculator.calculateStars(heartsLost = 0, accuracyPercent = 1.0f))
        // 0.85f should be normalized to 85% -> 2 stars with 1 heart lost
        assertEquals(2, StarCalculator.calculateStars(heartsLost = 1, accuracyPercent = 0.85f))
        // 0.70f should be normalized to 70% -> 1 star
        assertEquals(1, StarCalculator.calculateStars(heartsLost = 0, accuracyPercent = 0.70f))
    }

    @Test
    fun percentageAccuracy_evaluatedCorrectly() {
        assertEquals(3, StarCalculator.calculateStars(heartsLost = 0, accuracyPercent = 100f))
        assertEquals(2, StarCalculator.calculateStars(heartsLost = 2, accuracyPercent = 80f))
        assertEquals(1, StarCalculator.calculateStars(heartsLost = 0, accuracyPercent = 79f))
    }
}
