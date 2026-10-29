package com.playit.app.presentation.splash

import org.junit.Assert.assertTrue
import org.junit.Test

/** The opening sequence (user, 2026-10-29) plays in order and is over in about a second and a half. */
class SplashOpeningTest {

    @Test fun title_sparklesAfterItsLastLetterStartsDropping() {
        val lastLetterStarts = (TITLE.length - 1) * TITLE_STAGGER_MS
        assertTrue(TITLE_SPARKLE_MS > lastLetterStarts)
    }

    @Test fun lily_risesBeforeHerPawsLandAndTheStartButtonComesLast() {
        assertTrue(LILY_RISE_MS < PAWS_POP_MS)
        assertTrue(PAWS_POP_MS < START_POP_MS)
        assertTrue("the wiggle follows the pop", START_WIGGLE_MS > START_POP_MS)
    }

    @Test fun opening_isOverInAboutASecondAndAHalf() {
        assertTrue(START_WIGGLE_MS <= 1800L)
    }
}
