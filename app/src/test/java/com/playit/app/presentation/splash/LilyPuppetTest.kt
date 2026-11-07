package com.playit.app.presentation.splash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

/** Lily as a puppet (user, 2026-10-29: "how can it be natural"): her parts fit, and each moves on its own. */
class LilyPuppetTest {

    private val end = OPENING_END_MS.toFloat()

    @Test fun herShape_keepsHerFaceClearOfStartAndHerHeadOnScreen() {
        val s = LILY_SHAPE
        assertTrue("face ends at ${faceBottom(s)}", faceBottom(s) < LILY_FACE_BOTTOM)
        // Her drawing is 0.92 of its height wide: her head and ears must fit in it.
        assertTrue((s.earX + s.earR) * FIGURE_SCALE <= 0.46f)
        assertTrue(s.bodyHalfW * FIGURE_SCALE <= 0.46f)
        // The new Lily (2026-11-07): her eye rings sit on her body, their side tabs just reach past its edge.
        assertTrue("eye rings inside her body", s.eyeX + s.glassesR < s.bodyHalfW)
        assertTrue("tabs reach her edge", s.templeX in s.bodyHalfW..(s.bodyHalfW + 0.02f))
        assertTrue("the white, its ring and the glasses round it", s.eyeR < s.ringR && s.ringR < s.glassesR)
        assertTrue("her ears' tops are her figure's top", s.earY - s.earR in -0.001f..0.001f)
    }

    @Test fun herEyes_areHerPictures_bigPupilsThatStillMove() {
        // lily_idle.png (new Lily): the pupil fills about two thirds of the white, and leaves room to look about.
        val s = LILY_SHAPE
        assertTrue(s.pupilR / s.eyeR in 0.6f..0.72f)
        assertTrue("room to look about", (s.eyeR - s.pupilR) * 0.9f > 0.02f)
        assertTrue("the shade is a thin band", SHADE_SHIFT in 0.03f..0.06f)
    }

    /**
     * User, 2026-11-07: on the opening her body felt "like its not attached together". Her head is the top of her one
     * round body, so one move carries every part (no part leans, breathes or bobs on its own), and her arms always end
     * on her hands on the panel, however she moves.
     */
    @Test fun sheMovesAsOnePiece_andHerArmsStayOnHerHands() {
        val f = 1000f
        val feet = androidx.compose.ui.geometry.Offset(500f, 1100f)
        val hinge = androidx.compose.ui.geometry.Offset(500f, 900f)
        val hand = androidx.compose.ui.geometry.Offset(300f, 900f)
        listOf(
            LilyPose(androidx.compose.ui.geometry.Offset.Zero, 0f, 0f, 0f, 7f, 0.004f, 1f, 0f, 0f, 0f, 0f),   // tilt, breath
            LilyPose(androidx.compose.ui.geometry.Offset.Zero, 0f, 0f, 0f, -6f, -0.004f, -1f, 0f, 0f, 0.06f, 0f), // hop
            LilyPose(androidx.compose.ui.geometry.Offset.Zero, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),       // landing squash
        ).forEach { q ->
            val m = figureMove(q)
            val back = hand.intoFigure(m, f, feet, hinge).outOfFigure(m, f, feet, hinge)
            assertEquals("arm on the hand x for $m", hand.x, back.x, 0.01f)
            assertEquals("arm on the hand y for $m", hand.y, back.y, 0.01f)
        }
        // A tilt of her head is a gentle lean of her whole body.
        val lean = figureMove(LilyPose(androidx.compose.ui.geometry.Offset.Zero, 0f, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 0f, 0f)).lean
        assertTrue("lean $lean", lean in 3f..5f)
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

    @Test fun herEars_perkUpForStartAndRestAtTheEnds() {
        assertEquals(0f, earPerkAt(0f), 0f)
        assertEquals(0f, earPerkAt(end), 0f)
        assertTrue(earPerkAt(START_POP_MS + 250f) > 10f)
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
