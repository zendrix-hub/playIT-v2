package com.playit.app.presentation.map

import com.playit.app.presentation.map.components.CompanionAnimal
import com.playit.app.presentation.map.components.Gait
import com.playit.app.presentation.map.components.GaitFrame
import com.playit.app.presentation.map.components.gait
import com.playit.app.presentation.map.components.gaitAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** Each animal moves on the map in its own way (user, 2026-10-29: "the animals walk fly etc."). */
class MapGaitTest {

    private fun frames(g: Gait, direction: Float = 1f) = (0..g.totalMs step 10).map { gaitAt(g, it.toFloat(), direction) }

    @Test fun everyAnimal_hasItsOwnMove() {
        val gaits = CompanionAnimal.values().map { it.gait }
        assertEquals(CompanionAnimal.values().size, gaits.toSet().size)
        assertEquals(Gait.FLY, CompanionAnimal.OWL.gait)
        assertEquals(Gait.HOP, CompanionAnimal.BUNNY.gait)
        assertEquals(Gait.LEAP, CompanionAnimal.FROG.gait)
    }

    @Test fun everyMove_startsOnTheGroundAtTheOldStopAndEndsAtRest() {
        Gait.values().forEach { g ->
            val start = gaitAt(g, 0f)
            assertEquals("$g", 0f, start.along, 0f)
            assertEquals("$g", 0f, start.liftDp, 0.001f)
            assertEquals("$g", GaitFrame.REST, gaitAt(g, g.totalMs.toFloat()))
            assertEquals("$g ends where it travelled", 1f, gaitAt(g, g.moveMs.toFloat()).along, 0f)
        }
    }

    @Test fun everyMove_neverGoesBackAndStaysOnOrAboveTheGround() {
        Gait.values().forEach { g ->
            val f = frames(g)
            f.zipWithNext().forEach { (a, b) -> assertTrue("$g", b.along >= a.along - 0.0001f) }
            assertTrue("$g", f.all { it.liftDp >= -0.001f })
            assertTrue("$g keeps its shape mostly", f.all { it.scaleX in 0.7f..1.3f && it.scaleY in 0.7f..1.3f })
        }
    }

    @Test fun theOwlFliesHighest_theFrogLeapsHigherThanTheBunnyHops_theBearKeepsLow() {
        val high = Gait.values().associateWith { g -> frames(g).maxOf { it.liftDp } }
        assertTrue(high.getValue(Gait.FLY) >= 60f)
        assertTrue(high.getValue(Gait.FLY) > high.values.filter { it != high.getValue(Gait.FLY) }.max())
        assertTrue(high.getValue(Gait.LEAP) > high.getValue(Gait.HOP) + 15f)
        assertTrue(high.getValue(Gait.LUMBER) <= 8f)
        assertTrue("the bear is the slowest walker", Gait.LUMBER.moveMs > Gait.TROT.moveMs)
    }

    @Test fun theMonkeyTurnsAFullSomersaultTheWayItGoes() {
        val mid = Gait.FLIP.moveMs * (0.22f + 0.84f) / 2f
        assertEquals(180f, gaitAt(Gait.FLIP, mid, 1f).rotation, 1f)
        assertEquals(-180f, gaitAt(Gait.FLIP, mid, -1f).rotation, 1f)
    }

    @Test fun jumpers_squashAsTheyLandAndKickUpDust() {
        listOf(Gait.HOP, Gait.LEAP, Gait.FLIP, Gait.FLY).forEach { g ->
            val f = frames(g)
            assertTrue("$g squashes", f.any { it.scaleY < 0.92f && it.liftDp == 0f })
            assertTrue("$g kicks up dust", f.any { it.dust > 0.3f })
        }
        listOf(Gait.TROT, Gait.LUMBER).forEach { g -> assertTrue("$g walks, no dust", frames(g).all { it.dust == 0f }) }
    }

    @Test fun theBunnyHopsThreeTimesAndTheFrogLeapsTwice() {
        fun landings(g: Gait) = frames(g).zipWithNext().count { (a, b) -> a.liftDp > 0f && b.liftDp == 0f }
        assertEquals(3, landings(Gait.HOP))
        assertEquals(2, landings(Gait.LEAP))
        assertEquals(1, landings(Gait.FLIP))
    }

    @Test fun walkers_tipFromSideToSide() {
        listOf(Gait.TROT, Gait.LUMBER).forEach { g ->
            val f = frames(g)
            assertTrue("$g", f.any { it.rotation > 4f } && f.any { it.rotation < -4f })
            assertTrue("$g", f.all { abs(it.rotation) <= 10f })
        }
    }
}
