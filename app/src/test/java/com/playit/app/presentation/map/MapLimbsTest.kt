package com.playit.app.presentation.map

import androidx.compose.ui.geometry.Offset
import com.playit.app.presentation.map.components.ARM_REST
import com.playit.app.presentation.map.components.Gait
import com.playit.app.presentation.map.components.LimbPose
import com.playit.app.presentation.map.components.SHIN
import com.playit.app.presentation.map.components.THIGH
import com.playit.app.presentation.map.components.footAt
import com.playit.app.presentation.map.components.gaitAt
import com.playit.app.presentation.map.components.hipAt
import com.playit.app.presentation.map.components.kneeOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/** The animals have limbs, and the limbs keep time with the move (user, 2026-11-01: "it needs to aligns on the animation"). */
class MapLimbsTest {

    private fun frames(g: Gait, direction: Float = 1f) = (0..g.totalMs step 10).map { gaitAt(g, it.toFloat(), direction) }
    private fun Offset.to(o: Offset) = sqrt((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y))

    @Test fun everyMove_endsStandingWithLimbsAtRest() {
        fun assertRest(what: String, p: LimbPose) {
            val r = LimbPose.REST
            listOf(p.armL to r.armL, p.armR to r.armR, p.footL to r.footL, p.footR to r.footR, p.spread to r.spread, p.sink to r.sink)
                .forEach { (a, b) -> assertEquals(what, b, a, 0.001f) }
        }
        Gait.values().forEach { g ->
            assertRest("$g starts standing", gaitAt(g, 0f).limbs)
            assertRest("$g ends standing", gaitAt(g, g.totalMs.toFloat()).limbs)
        }
    }

    @Test fun limbsNeverJump() {
        Gait.values().forEach { g ->
            frames(g).zipWithNext().forEachIndexed { i, (a, b) ->
                val x = a.limbs
                val y = b.limbs
                assertTrue("$g arms jump at ${i * 10} ms", abs(x.armL - y.armL) < 12f && abs(x.armR - y.armR) < 12f)
                assertTrue("$g feet jump at ${i * 10} ms", abs(x.footL - y.footL) < 2.5f && abs(x.footR - y.footR) < 2.5f)
                assertTrue("$g hips jump at ${i * 10} ms", abs(x.sink - y.sink) < 2.5f && abs(x.spread - y.spread) < 2.5f)
            }
        }
    }

    @Test fun walkers_keepOneFootOnTheGroundAsTheBodyRises() {
        listOf(Gait.TROT, Gait.LUMBER).forEach { g ->
            frames(g).forEach { f ->
                assertEquals("$g: the lower foot is on the ground", 0f, min(f.limbs.footL, f.limbs.footR) + f.liftDp, 0.001f)
            }
        }
    }

    @Test fun walkers_liftTheFootAwayFromTheirTilt_andSwingTheOppositeArm() {
        listOf(Gait.TROT, Gait.LUMBER).forEach { g ->
            frames(g).filter { abs(it.rotation) > 2f }.forEach { f ->
                val l = f.limbs
                if (f.rotation > 0f) {
                    assertTrue("$g tips right, lifts left", l.footL > l.footR)
                    assertTrue("$g right arm out", l.armR > l.armL)
                } else {
                    assertTrue("$g tips left, lifts right", l.footR > l.footL)
                    assertTrue("$g left arm out", l.armL > l.armR)
                }
            }
        }
    }

    @Test fun walkers_stepFourTimes_twoOnEachFoot() {
        listOf(Gait.TROT, Gait.LUMBER).forEach { g ->
            val f = frames(g)
            val l = f.zipWithNext().count { (a, b) -> a.limbs.footL + a.liftDp < 2f && b.limbs.footL + b.liftDp >= 2f }
            val r = f.zipWithNext().count { (a, b) -> a.limbs.footR + a.liftDp < 2f && b.limbs.footR + b.liftDp >= 2f }
            assertEquals("$g left steps", 2, l)
            assertEquals("$g right steps", 2, r)
        }
    }

    @Test fun onTheGround_jumpersBothFeetStayPut_andTheirKneesBendInTheCrouch() {
        listOf(Gait.HOP, Gait.LEAP, Gait.FLIP, Gait.FLY).forEach { g ->
            val grounded = frames(g).filter { it.liftDp == 0f }
            grounded.forEach {
                assertEquals("$g", 0f, it.limbs.footL, 0.001f)
                assertEquals("$g", 0f, it.limbs.footR, 0.001f)
            }
            assertTrue("$g crouches", grounded.any { it.limbs.sink > 3f })
            // Sinks the same frames it squashes: both come from one crouch.
            assertTrue("$g sinks as it squashes", grounded.filter { it.scaleY < 0.95f }.all { it.limbs.sink > 0.5f })
        }
    }

    @Test fun inTheAir_eachAnimalDoesItsOwnThing() {
        fun high(g: Gait) = frames(g).filter { it.liftDp > 20f }
        assertTrue("the monkey curls its feet up in the somersault", high(Gait.FLIP).maxOf { it.limbs.footL } > 7f)
        assertTrue("and throws its arms up to land", frames(Gait.FLIP).filter { it.liftDp == 0f }.maxOf { it.limbs.armL } > 100f)
        assertTrue("the frog flings its legs straight", high(Gait.LEAP).filter { it.liftDp > 40f }.all { it.limbs.footL == 0f })
        assertTrue("and its arms out", high(Gait.LEAP).maxOf { it.limbs.armL } > 90f)
        assertTrue("the frog's knees go wide in its crouch", frames(Gait.LEAP).maxOf { it.limbs.spread } > 6f)
        assertTrue("the bunny's feet stay together", frames(Gait.HOP).all { it.limbs.spread == 0f })
        assertTrue("the owl tucks its feet up", high(Gait.FLY).maxOf { it.limbs.footL } > 4f)
    }

    @Test fun legsAlwaysReachTheirFeet() {
        Gait.values().forEach { g ->
            frames(g).forEach { f ->
                for (side in listOf(-1f, 1f)) {
                    val reach = hipAt(side, f.limbs).to(footAt(side, f.limbs))
                    assertTrue("$g asks a leg to reach $reach", reach <= THIGH + SHIN + 0.01f)
                    // A foot never comes up past its own hip.
                    assertTrue("$g foot above hip", footAt(side, f.limbs).y > hipAt(side, f.limbs).y - 0.01f)
                }
            }
        }
    }

    @Test fun aKnee_isOneThighFromTheHipAndOneShinFromTheFoot_onTheOutside() {
        for (side in listOf(-1f, 1f)) {
            val hip = Offset(side * 6f, 64f)
            val foot = Offset(side * 8f, 72f)
            val knee = kneeOf(hip, foot, side)
            assertEquals(THIGH, knee.to(hip), 0.01f)
            assertEquals(SHIN, knee.to(foot), 0.01f)
            assertTrue("bends out", knee.x * side > foot.x * side)
        }
    }

    @Test fun restingArmsHangOutward() {
        assertEquals(ARM_REST, LimbPose.REST.armL, 0f)
        assertEquals(LimbPose.REST.armL, LimbPose.REST.armR, 0f)
    }
}
