package com.playit.app.presentation.lettercomplete

import org.junit.Assert.assertEquals
import org.junit.Test

/** Lily's words match the stars: one star is "finished", two or three are "mastered" (card 31d). */
class LetterCompleteMessageTest {

    @Test fun oneStar_saysFinished() {
        assertEquals("You finished Letter M! Amazing sound practice!", letterCompleteMessage("M", 1))
    }

    @Test fun twoOrThreeStars_sayMastered() {
        assertEquals("You mastered Letter S! Amazing sound practice!", letterCompleteMessage("S", 2))
        assertEquals("You mastered Letter S! Amazing sound practice!", letterCompleteMessage("S", 3))
    }
}
