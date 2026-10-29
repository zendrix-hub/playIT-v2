package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Entrances of 2026-10-29 (user: more on the opening, the map, lesson starts and rewards). Pure timings and curves. */
class EntranceMotionTest {

    @Test fun dropIn_startsAboveAndInvisibleAndEndsInPlace() {
        assertEquals(1f, dropOffsetAt(0f), 0f)
        assertEquals(0f, dropAlphaAt(0f), 0f)
        assertEquals(0f, dropOffsetAt(1f), 0f)
        assertEquals(1f, dropAlphaAt(1f), 0f)
        assertTrue("a spring past its place bounces below it", dropOffsetAt(1.1f) < 0f)
    }

    @Test fun mapWave_endsAtTheChildsStop() {
        val active = 10
        val delays = (0 until 25).map { mapPopDelayMs(it, active) }
        assertEquals(delays.max(), delays[active])
        assertTrue("stops far away pop first", delays[0] == MAP_POP_BASE_MS && delays[24] == MAP_POP_BASE_MS)
        (1..MAP_POP_WAVE).forEach { d -> assertTrue(mapPopDelayMs(active - d, active) < mapPopDelayMs(active - d + 1, active)) }
        assertTrue("the animal hops after its stop lands", MAP_OPEN_HOP_MS > mapPopDelayMs(active, active))
    }

    @Test fun lessonButton_popsAfterTheCardStarts() {
        assertTrue(LESSON_BUTTON_POP_MS in 300..800)
        assertTrue("Lily waves for over a second", GREET_MS in 1000L..2000L)
    }

    @Test fun stars_landOneAfterAnotherAndSparkleOnLanding() {
        assertTrue(STAR_STAGGER_MS >= 250)
        assertTrue(STAR_LAND_MS < STAR_STAGGER_MS)
        assertTrue(STAR_FALL_DP >= 48f)
    }

    @Test fun heartWonBack_swellsAndSettles() {
        assertEquals(1f, gainPopScale(0f), 0f)
        assertEquals(1.5f, gainPopScale(GAIN_POP_MS / 2f), 0.001f)
        assertEquals(1f, gainPopScale(GAIN_POP_MS.toFloat()), 0f)
    }
}
