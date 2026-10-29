package com.playit.app.presentation.splash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

/** Lily as a puppet (user, 2026-10-29: "how can it be natural"): her parts fit, and each moves on its own. */
class LilyPuppetTest {

    private val end = OPENING_END_MS.toFloat()

    @Test fun bothLooks_keepHerFaceClearOfStartAndHerHeadOnScreen() {
        LilyLook.values().forEach { look ->
            val s = shapeOf(look)
            assertTrue("$look: face ends at ${faceBottom(s)}", faceBottom(s) < LILY_FACE_BOTTOM)
            // Her drawing is 0.92 of its height wide: her head must fit in it.
            assertTrue("$look", s.headRx * FIGURE_SCALE <= 0.46f)
            assertTrue("$look: the head sits on the body", s.headY + s.headRy > s.bodyY - s.bodyR)
        }
        val redesign = shapeOf(LilyLook.REDESIGN)
        assertTrue(redesign.brows && redesign.scarf && redesign.irisR > 0f)
        assertTrue("the redesign's head is bigger", redesign.headRx > shapeOf(LilyLook.CLASSIC).headRx)
    }

    @Test fun herEyes_followTheOpening() {
        assertEquals(0f, openingGazeAt(0f).x, 0f)
        val peek = (LILY_PEEK_END_MS..LILY_CROUCH_MS).map { openingGazeAt(it.toFloat()).x }
        assertTrue("she looks left and right as she peeks", peek.min() < -0.6f && peek.max() > 0.6f)
        assertTrue("up at the letters", openingGazeAt(TITLE_MS + 300f).y < -0.8f)
        val lastLetter = TITLE_MS + (TITLE.length - 1) * TITLE_STAGGER_MS + LETTER_MS / 2
        val letters = (TITLE_MS..lastLetter step 50).map { openingGazeAt(it.toFloat()).x }
        letters.zipWithNext().forEach { (a, b) -> assertTrue("left to right with the letters", b >= a - 0.001f) }
        assertTrue("down at Start", openingGazeAt(START_POP_MS + 200f).y > 0.8f)
        assertEquals("at the child when she winks", 0f, openingGazeAt(LILY_WINK_MS.toFloat()).y, 0.001f)
        assertEquals(0f, openingGazeAt(end).x, 0f)
        (0..OPENING_END_MS step 10).forEach { val g = openingGazeAt(it.toFloat()); assertTrue(abs(g.x) <= 1f && abs(g.y) <= 1f) }
    }

    @Test fun herEarsAndBrows_perkUpForStartAndRestAtTheEnds() {
        assertEquals(0f, earPerkAt(0f), 0f)
        assertEquals(0f, earPerkAt(end), 0f)
        assertTrue(earPerkAt(START_POP_MS + 250f) > 10f)
        assertEquals(0f, browRaiseAt(end), 0f)
        assertTrue(browRaiseAt(START_POP_MS + 300f) > 0.9f)
    }

    @Test fun herTail_swingsBehindHerJumpAndRestsWhenSheDoes() {
        assertEquals(0f, tailLagAt(0f), 0f)
        assertEquals(0f, tailLagAt(end), 0f)
        val jump = (LILY_JUMP_MS..LILY_SETTLED_MS + 200).map { tailLagAt(it.toFloat()) }
        assertTrue("it swings: ${jump.max()}", jump.max() > 10f)
        assertTrue(jump.all { abs(it) <= 22f })
        // A moment late: the swing peaks after she leaves the ground.
        val peakAt = LILY_JUMP_MS + jump.indexOf(jump.max())
        assertTrue(peakAt > LILY_JUMP_MS + 40)
    }

    @Test fun herLittleLife_isNeverOnABeat() {
        val r = Random(7)
        val blinks = (1..50).map { nextBlinkMs(r) }
        assertTrue(blinks.all { it in 2200L..5600L } && blinks.toSet().size > 30)
        assertTrue((1..50).map { nextGlanceMs(r) }.all { it in 1800L..4200L })
        assertTrue((1..50).map { nextEarTwitchMs(r) }.all { it in 3500L..8000L })
        assertTrue((1..50).map { nextTiltMs(r) }.all { it in 6000L..11000L })
    }

    @Test fun taps_getEachReactionInTurn() {
        assertEquals(TapReaction.values().toList(), (0 until TapReaction.values().size).map { tapReaction(it) })
        assertEquals(TapReaction.GIGGLE, tapReaction(TapReaction.values().size))
    }

    @Test fun aTapHop_liftsThenLandsWithASquash() {
        assertEquals(0f to 0f, tapHopAt(0f))
        assertEquals(0f to 0f, tapHopAt(1f))
        assertTrue(tapHopAt(0.375f).first > 0.05f)
        assertTrue(tapHopAt(0.875f).second > 0.9f && tapHopAt(0.875f).first == 0f)
    }
}
