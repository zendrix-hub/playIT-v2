package com.playit.app.presentation.components.lipsync

import com.playit.app.presentation.components.GREET_MS
import com.playit.app.presentation.components.MascotState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Lily acts out each pose (user, 2026-10-30: a good animation or motion design for her movements): one smooth motion
 * from pose to pose, a gesture of its own for each pose, all inside her place, and nothing that loops.
 */
class LilyActingTest {

    private val holds = setOf(MascotState.LISTENING, MascotState.THINKING)

    private fun assertClose(message: String, a: LilyMove, b: LilyMove) {
        assertEquals("$message: lean", a.rotation, b.rotation, 1e-3f)
        assertEquals("$message: x", a.x, b.x, 1e-4f)
        assertEquals("$message: lift", a.lift, b.lift, 1e-4f)
        assertEquals("$message: width", a.scaleX, b.scaleX, 1e-4f)
        assertEquals("$message: height", a.scaleY, b.scaleY, 1e-4f)
    }

    /** Samples [move] every 4 ms over [ms] and checks that no step jumps: she always moves, never pops. */
    private fun assertSmooth(what: String, ms: Int, move: (Float) -> LilyMove) {
        var last = move(0f)
        for (t in 4..ms + 200 step 4) {
            val now = move(t.toFloat())
            assertTrue("$what leans in a jump at $t ms", abs(now.rotation - last.rotation) < 1f)
            assertTrue("$what shifts in a jump at $t ms", abs(now.x - last.x) < 0.005f)
            assertTrue("$what rises in a jump at $t ms", abs(now.lift - last.lift) < 0.01f)
            assertTrue("$what squashes in a jump at $t ms", abs(now.scaleX - last.scaleX) < 0.02f && abs(now.scaleY - last.scaleY) < 0.02f)
            last = now
        }
    }

    private fun span(pose: MascotState) = (0..poseChangeMs(pose)).map { poseChangeAt(pose, it.toFloat(), STANDING) }

    @Test fun aChangeOfPose_isOneSmoothMotion_everyPose() {
        MascotState.values().forEach { pose ->
            assertSmooth(pose.name, poseChangeMs(pose)) { poseChangeAt(pose, it, STANDING) }
        }
    }

    @Test fun theNewPictureShows_atTheBottomOfTheCrouch() {
        MascotState.values().forEach { pose ->
            assertClose("${pose.name} just before the swap", CROUCH, poseChangeAt(pose, SWAP_MS - 0.01f, STANDING))
            assertClose("${pose.name} as the new picture shows", CROUCH, poseChangeAt(pose, SWAP_MS.toFloat(), STANDING))
            assertTrue("crouched: lower and wider", CROUCH.scaleY < 0.95f && CROUCH.scaleX > 1.03f)
        }
    }

    @Test fun shePopsOutTallerThanSheIs_thenSettles() {
        MascotState.values().forEach { pose ->
            assertTrue("${pose.name} stretches up out of the crouch", span(pose).maxOf { it.scaleY } > 1.03f)
        }
    }

    @Test fun everyPoseEndsStanding_exceptListeningAndThinking_whichHoldTheirTilt() {
        MascotState.values().filterNot { it in holds }.forEach { pose ->
            assertClose("${pose.name} at the end", STANDING, poseChangeAt(pose, poseChangeMs(pose).toFloat(), STANDING))
            assertClose("${pose.name} long after", STANDING, poseChangeAt(pose, poseChangeMs(pose) + 5000f, STANDING))
        }
        val listening = poseChangeAt(MascotState.LISTENING, 9999f, STANDING)
        assertTrue("listening tilts toward her cupped ear, on the picture's left", listening.rotation in -8f..-5f)
        assertTrue("and leans in", listening.scaleX > 1.02f && listening.scaleX == listening.scaleY)
        val thinking = poseChangeAt(MascotState.THINKING, 9999f, STANDING)
        assertTrue("thinking tilts toward the hand at her chin", thinking.rotation in 4f..8f)
        assertEquals(1f, thinking.scaleY, 1e-4f)
    }

    @Test fun eachPoseActsItsOwnGesture() {
        val wave = span(MascotState.WAVING)
        assertTrue("a wave rocks her to the left...", wave.minOf { it.rotation } <= -8f)
        assertTrue("...and to the right", wave.maxOf { it.rotation } >= 7f)
        assertTrue("pointing leans her toward her hand", span(MascotState.POINTING).maxOf { it.rotation } >= 7f)
        assertTrue("pointing never leans away", span(MascotState.POINTING).minOf { it.rotation } >= 0f)
        assertTrue("encouraging bounces", span(MascotState.ENCOURAGING).maxOf { it.lift } >= 0.04f)
        assertTrue("toward her thumb", span(MascotState.ENCOURAGING).maxOf { it.rotation } >= 4f)
        assertTrue("celebrating jumps high", span(MascotState.CELEBRATING).maxOf { it.lift } >= 0.14f)
        val thinking = span(MascotState.THINKING)
        assertTrue("thinking nods", thinking.minOf { it.scaleY } < 0.97f && thinking.last().scaleY == 1f)
        assertTrue("idle only springs out of the crouch", span(MascotState.IDLE).all { it.rotation == 0f && it.lift < 0.02f })
    }

    @Test fun sheStaysInHerPlace() {
        val all = MascotState.values().flatMap { span(it) } +
            LilyTap.values().flatMap { tap -> (0..tapMs(tap)).map { tapMoveAt(tap, it.toFloat()) } }
        all.forEach {
            assertTrue("lean ${it.rotation}", abs(it.rotation) <= 10f)
            assertTrue("lift ${it.lift}", it.lift in 0f..0.16f)
            assertTrue("shift ${it.x}", abs(it.x) <= 0.02f)
            assertTrue("width ${it.scaleX}", it.scaleX in 0.85f..1.13f)
            assertTrue("height ${it.scaleY}", it.scaleY in 0.85f..1.11f)
        }
    }

    @Test fun aChangeInTheMiddleOfAMove_carriesOnFromWhereSheIs() {
        listOf(60f, 200f, 330f, 700f).forEach { t ->
            val midJump = poseChangeAt(MascotState.CELEBRATING, t, STANDING)
            assertClose("cut off at $t ms", midJump, poseChangeAt(MascotState.LISTENING, 0f, midJump))
            assertSmooth("listening after a jump cut off at $t ms", poseChangeMs(MascotState.LISTENING)) {
                poseChangeAt(MascotState.LISTENING, it, midJump)
            }
        }
        val listening = poseChangeAt(MascotState.LISTENING, 9999f, STANDING)
        assertSmooth("out of listening's tilt", poseChangeMs(MascotState.POINTING)) { poseChangeAt(MascotState.POINTING, it, listening) }
    }

    @Test fun eachChangeIsQuick_andTheWaveIsDoneBeforeTheLessonPose() {
        MascotState.values().forEach { assertTrue("${it.name} takes ${poseChangeMs(it)} ms", poseChangeMs(it) <= 1300) }
        assertTrue("the hello wave ends before the greeting does", poseChangeMs(MascotState.WAVING) < GREET_MS)
    }

    @Test fun tapsAreAnsweredInTurn_eachStartingAndEndingStanding() {
        assertEquals(LilyTap.BOING, lilyTap(1))
        assertEquals(LilyTap.HOP, lilyTap(2))
        assertEquals(LilyTap.GIGGLE, lilyTap(3))
        assertEquals(LilyTap.BOING, lilyTap(4))
        LilyTap.values().forEach { tap ->
            assertClose("${tap.name} starts", STANDING, tapMoveAt(tap, 0f))
            assertClose("${tap.name} ends", STANDING, tapMoveAt(tap, tapMs(tap).toFloat()))
            assertTrue("${tap.name} answers within 0.6 s", tapMs(tap) <= 600)
            assertSmooth(tap.name, tapMs(tap)) { tapMoveAt(tap, it) }
        }
        assertTrue("a hop leaves the ground", (0..tapMs(LilyTap.HOP)).maxOf { tapMoveAt(LilyTap.HOP, it.toFloat()).lift } > 0.06f)
    }

    @Test fun herBodyGoesWithHerVoice_andRestsWhenQuiet() {
        assertClose("quiet", STANDING, talkMove(0f))
        val loud = talkMove(1f)
        assertTrue("taller on the loud parts", loud.scaleY > 1.03f && loud.lift > 0f)
        assertTrue("a little, so her mouth stays the thing to watch", loud.scaleY < 1.06f && loud.lift < 0.02f)
        assertClose("never past full voice", loud, talkMove(3f))
    }

    @Test fun movesAddUp() {
        val a = LilyMove(rotation = 2f, x = 0.01f, lift = 0.02f, scaleX = 1.1f, scaleY = 0.9f)
        assertClose("on standing", a, a + STANDING)
        assertClose("half way", LilyMove(1f, 0.005f, 0.01f, 1.05f, 0.95f), STANDING.lerp(a, 0.5f))
    }
}
