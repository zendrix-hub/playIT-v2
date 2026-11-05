package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** While the model plays, the mouth says the sound: relaxed in the gaps, in its shape and pulsing while it sounds. */
class MouthSayingTest {
    @Test fun inTheGaps_theMouthIsNearlyRelaxed() {
        assertEquals(MOUTH_RESTING, mouthSaying(level = 0f, seconds = 0.3f), 0f)
        assertTrue(MOUTH_RESTING < 0.2f)
    }

    @Test fun whileTheSoundPlays_itIsInItsShape_andPulses() {
        val frames = (0 until 100).map { mouthSaying(level = 0.6f, seconds = it / 100f) }
        assertTrue("in its shape", frames.all { it >= 1f - MOUTH_PULSE - 1e-4f && it <= 1f })
        assertTrue("it moves: ${frames.minOrNull()} to ${frames.maxOrNull()}", frames.max() - frames.min() > MOUTH_PULSE * 0.9f)
    }

    @Test fun aHeldSoundStillMovesVisibly() {
        // Even a held mmm opens and closes a few times a second, far enough to see on a 96 dp cue.
        assertTrue(MOUTH_PULSE >= 0.2f)
        assertTrue(MOUTH_PULSE_HZ in 3f..6f)
    }
}
