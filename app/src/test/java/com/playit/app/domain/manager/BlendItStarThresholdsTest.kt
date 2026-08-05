package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class BlendItStarThresholdsTest {

    @Test
    fun zeroHeartsLost_returnsThreeStars() {
        assertEquals(3, BlendItStarThresholds.calculateStars(_groupId = 1, totalHeartsLost = 0))
    }

    @Test
    fun oneOrTwoHeartsLost_returnsTwoStars() {
        assertEquals(2, BlendItStarThresholds.calculateStars(_groupId = 1, totalHeartsLost = 1))
        assertEquals(2, BlendItStarThresholds.calculateStars(_groupId = 1, totalHeartsLost = 2))
    }

    @Test
    fun threeOrMoreHeartsLost_returnsOneStar() {
        assertEquals(1, BlendItStarThresholds.calculateStars(_groupId = 1, totalHeartsLost = 3))
    }
}
