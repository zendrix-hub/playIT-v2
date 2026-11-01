package com.playit.app.presentation.map

import com.playit.app.presentation.map.components.CompanionAnimal
import com.playit.app.presentation.map.components.Gait
import com.playit.app.presentation.map.components.GaitFrame
import com.playit.app.presentation.map.components.LimbPose
import com.playit.app.presentation.map.components.build
import com.playit.app.presentation.map.components.gait
import com.playit.app.presentation.map.components.gaitAt
import com.playit.app.presentation.map.components.headDrop
import com.playit.app.presentation.map.components.headWidth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * The animals are little front-facing plush animals on four paws, and their legs keep time with the move (user,
 * 2026-11-01: "make it front facing ... make it cute", "too fat", "Dont let them walk on two legs").
 */
class MapLimbsTest {

    private val walkers = listOf(Gait.TROT, Gait.LUMBER)
    private val fourLegged = CompanionAnimal.values().filter { it != CompanionAnimal.OWL }

    private fun frames(g: Gait, direction: Float = 1f) = (0..g.totalMs step 10).map { gaitAt(g, it.toFloat(), direction) }

    private fun LimbPose.values() = listOf(frontLeftUp, frontRightUp, hindLeftUp, hindRightUp, frontSpread, hindSpread, sink, tail)

    /** How high each foot is above the ground: front left, front right, hind left, hind right. */
    private fun GaitFrame.feetAboveGround() = listOf(limbs.frontLeftUp, limbs.frontRightUp, limbs.hindLeftUp, limbs.hindRightUp).map { it + liftDp }

    @Test fun everyAnimalButTheOwl_hasFourLegs() {
        fourLegged.forEach { assertNotNull("${it.name} has front legs", it.build.front) }
        assertNull("the owl perches on two feet and flies", CompanionAnimal.OWL.build.front)
    }

    @Test fun noBodyIsFat_andEachHeadIsCuteButNotTooBig() {
        CompanionAnimal.values().forEach {
            val b = it.build
            assertTrue("${it.name}'s body is ${b.bodyW} wide", b.bodyW <= 30f)
            assertTrue("${it.name}'s head is wider than its body", b.headWidth > b.bodyW)
            assertTrue("${it.name}'s head is ${b.headWidth / b.bodyW} of its body", b.headWidth / b.bodyW < 2.1f)
        }
        assertTrue("the bear is not fat", CompanionAnimal.BEAR.build.bodyW <= 28f)
        assertTrue("the bunny is not fat", CompanionAnimal.BUNNY.build.bodyW <= 24f)
        val owl = CompanionAnimal.OWL.build
        assertTrue("the owl's head doesn't dwarf its body", owl.headWidth / owl.bodyW < 1.3f)
    }

    @Test fun everyHead_sitsOnItsBody_insideItsSquare() {
        CompanionAnimal.values().forEach {
            val b = it.build
            assertTrue("${it.name}'s chin overlaps its body", b.chinY > b.bodyY - b.bodyH / 2f)
            // The top of its picture is at most 62 % of the square above the bottom of it.
            assertTrue("${it.name} moves its head by ${b.headDrop(LimbPose.REST)}", abs(b.headDrop(LimbPose.REST)) < 20f)
        }
    }

    @Test fun everyMove_startsAndEndsStanding() {
        Gait.values().forEach { g ->
            gaitAt(g, 0f).limbs.values().forEach { assertEquals("$g starts standing", 0f, it, 0.001f) }
            gaitAt(g, g.totalMs.toFloat()).limbs.values().forEach { assertEquals("$g ends standing", 0f, it, 0.001f) }
        }
    }

    @Test fun limbsNeverJump() {
        Gait.values().forEach { g ->
            frames(g).zipWithNext().forEachIndexed { i, (a, b) ->
                val x = a.limbs
                val y = b.limbs
                val ups = listOf(x.frontLeftUp to y.frontLeftUp, x.frontRightUp to y.frontRightUp, x.hindLeftUp to y.hindLeftUp, x.hindRightUp to y.hindRightUp)
                assertTrue("$g a foot jumps up at ${i * 10} ms", ups.all { (p, q) -> abs(p - q) < 3f })
                assertTrue("$g feet jump out at ${i * 10} ms", abs(x.frontSpread - y.frontSpread) < 3f && abs(x.hindSpread - y.hindSpread) < 3f)
                assertTrue("$g the body jumps at ${i * 10} ms", abs(x.sink - y.sink) < 3f)
            }
        }
    }

    @Test fun walkers_alwaysHaveAFrontAndAHindFootOnTheGround() {
        walkers.forEach { g ->
            frames(g).forEach { f ->
                val down = f.feetAboveGround().map { abs(it) < 0.01f }
                assertTrue("$g has a front foot down", down[0] || down[1])
                assertTrue("$g has a hind foot down", down[2] || down[3])
            }
        }
    }

    @Test fun theCatTrots_diagonalPairsTogether() {
        frames(Gait.TROT).forEach { f ->
            assertEquals("front left goes with hind right", f.limbs.frontLeftUp, f.limbs.hindRightUp, 0.001f)
            assertEquals("front right goes with hind left", f.limbs.frontRightUp, f.limbs.hindLeftUp, 0.001f)
        }
        assertTrue("they do lift", frames(Gait.TROT).maxOf { it.feetAboveGround()[0] } > 4f)
    }

    @Test fun theBearAmbles_bothLegsOfASideTogether_slowerAndLower() {
        val f = frames(Gait.LUMBER)
        f.forEach {
            assertEquals("a side's legs go together", it.limbs.frontLeftUp, it.limbs.hindLeftUp, 0.001f)
            assertEquals("a side's legs go together", it.limbs.frontRightUp, it.limbs.hindRightUp, 0.001f)
        }
        assertTrue("slower than the cat", Gait.LUMBER.moveMs > Gait.TROT.moveMs)
        assertTrue("stays lower", f.maxOf { it.liftDp } < frames(Gait.TROT).maxOf { it.liftDp })
    }

    @Test fun walkers_swayOntoTheFootThatIsDown() {
        walkers.forEach { g ->
            frames(g).filter { it.along in 0.1f..0.9f }.forEach { f ->
                val feet = f.feetAboveGround()
                // Take off its lean the way it travels (about 2 degrees): what is left is its sway.
                if (feet[1] > 2f) assertTrue("$g lifts its right foot, sways left: ${f.rotation}", f.rotation < 2f)
                if (feet[0] > 2f) assertTrue("$g lifts its left foot, sways right: ${f.rotation}", f.rotation > 1f)
            }
        }
    }

    @Test fun walkers_stepFourTimes_twoOnEachFrontFoot() {
        walkers.forEach { g ->
            val f = frames(g)
            fun steps(foot: Int) = f.zipWithNext().count { (a, b) -> a.feetAboveGround()[foot] < 2f && b.feetAboveGround()[foot] >= 2f }
            assertEquals("$g left steps", 2, steps(0))
            assertEquals("$g right steps", 2, steps(1))
        }
    }

    @Test fun jumpers_haveAllTheirFeetOnTheGroundWheneverTheyAreOnIt() {
        listOf(Gait.HOP, Gait.LEAP, Gait.FLIP).forEach { g ->
            val grounded = frames(g).filter { it.liftDp == 0f }
            grounded.forEach { f -> f.feetAboveGround().forEach { assertEquals("$g", 0f, it, 0.001f) } }
            assertTrue("$g crouches", grounded.any { it.limbs.sink > 3f })
            // It sinks the same frames it squashes: both come from one crouch.
            assertTrue("$g sinks as it squashes", grounded.filter { it.scaleY < 0.95f }.all { it.limbs.sink > 0.2f })
        }
    }

    @Test fun inTheAir_eachAnimalDoesItsOwnThing() {
        fun high(g: Gait) = frames(g).filter { it.liftDp > 15f }
        assertTrue("the bunny tucks up all four paws", high(Gait.HOP).any { it.limbs.frontLeftUp > 4f && it.limbs.hindLeftUp > 4f })
        assertTrue("the frog flings its legs wide", high(Gait.LEAP).maxOf { it.limbs.hindSpread } > 10f && high(Gait.LEAP).maxOf { it.limbs.frontSpread } > 7f)
        assertTrue("and lands in a splat", frames(Gait.LEAP).filter { it.liftDp == 0f }.any { it.limbs.hindSpread > 6f && it.limbs.sink > 4f })
        assertTrue("the monkey curls up in its somersault", high(Gait.FLIP).any { it.limbs.frontLeftUp > 7f && it.limbs.hindLeftUp > 6f })
        assertTrue("the owl tucks its feet up", frames(Gait.FLY).filter { it.liftDp > 20f }.maxOf { it.limbs.hindLeftUp } > 4f)
    }

    /** A leg's a stub, so it just needs its foot below its hip. */
    @Test fun aFootNeverComesUpPastItsHip() {
        CompanionAnimal.values().forEach { a ->
            frames(a.gait).forEach { f ->
                val hind = a.build.hind
                assertTrue("${a.name} hind", 74.5f - maxOf(f.limbs.hindLeftUp, f.limbs.hindRightUp) > hind.hipY + f.limbs.sink - 0.01f)
                a.build.front?.let {
                    assertTrue("${a.name} front", 74.5f - maxOf(f.limbs.frontLeftUp, f.limbs.frontRightUp) > it.hipY + f.limbs.sink - 0.01f)
                }
            }
        }
    }

    @Test fun theCatsTailSwingsAsItTrots() {
        val f = frames(Gait.TROT)
        assertTrue(f.any { it.limbs.tail > 8f } && f.any { it.limbs.tail < -8f })
    }
}
