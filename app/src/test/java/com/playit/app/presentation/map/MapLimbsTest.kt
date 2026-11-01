package com.playit.app.presentation.map

import androidx.compose.ui.geometry.Offset
import com.playit.app.presentation.map.components.CompanionAnimal
import com.playit.app.presentation.map.components.FOOT_Y
import com.playit.app.presentation.map.components.Gait
import com.playit.app.presentation.map.components.GaitFrame
import com.playit.app.presentation.map.components.Leg
import com.playit.app.presentation.map.components.LimbPose
import com.playit.app.presentation.map.components.RIG_SIZE
import com.playit.app.presentation.map.components.build
import com.playit.app.presentation.map.components.gait
import com.playit.app.presentation.map.components.gaitAt
import com.playit.app.presentation.map.components.headReach
import com.playit.app.presentation.map.components.hipOf
import com.playit.app.presentation.map.components.kneeOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * The animals move as animals, not on two legs, and their limbs keep time with the move (user, 2026-11-01: "Dont let them
 * walk on two legs, let the cat be cat and bear be bear on how they walk").
 */
class MapLimbsTest {

    private val walkers = listOf(Gait.TROT, Gait.LUMBER)
    private val fourLegged = CompanionAnimal.values().filter { it != CompanionAnimal.OWL }

    private fun frames(g: Gait, direction: Float = 1f) = (0..g.totalMs step 10).map { gaitAt(g, it.toFloat(), direction) }
    private fun Offset.to(o: Offset) = sqrt((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y))

    private fun LimbPose.values() = listOf(
        frontNearX, frontNearUp, frontFarX, frontFarUp, hindNearX, hindNearUp, hindFarX, hindFarUp,
        frontSink, rearSink, frontOut, rearOut, tail
    )

    /** How high each foot is above the ground: front near, front far, hind near, hind far. */
    private fun GaitFrame.feetAboveGround() = listOf(limbs.frontNearUp, limbs.frontFarUp, limbs.hindNearUp, limbs.hindFarUp).map { it + liftDp }

    @Test fun everyAnimalButTheOwl_hasFourLegsATailWhereItHasOne_andItsHeadInFront() {
        fourLegged.forEach { a ->
            val b = a.build
            assertNotNull("${a.name} has front legs", b.front)
            assertTrue("${a.name}'s head is at the front", b.headX > 0f)
            assertTrue("${a.name}'s rump is behind its chest", b.rump.x < b.chest.x)
            assertTrue("${a.name}'s front feet are forward of its hind feet", b.front!!.footX > b.hind.footX + 15f)
        }
        listOf(CompanionAnimal.CAT, CompanionAnimal.MONKEY, CompanionAnimal.BUNNY, CompanionAnimal.BEAR).forEach {
            assertNotNull("${it.name} has a tail", it.build.tail)
        }
        assertNull("a frog has no tail", CompanionAnimal.FROG.build.tail)
        assertNull("the owl perches on two feet and flies", CompanionAnimal.OWL.build.front)
    }

    @Test fun eachAnimalIsBuiltLikeItself() {
        val bear = CompanionAnimal.BEAR.build
        val cat = CompanionAnimal.CAT.build
        val bunny = CompanionAnimal.BUNNY.build
        val frog = CompanionAnimal.FROG.build
        val monkey = CompanionAnimal.MONKEY.build
        assertTrue("the bear is bigger than the cat", bear.rump.h > cat.rump.h + 10f)
        assertTrue("on thicker legs", bear.front!!.width > cat.front!!.width + 2f)
        assertTrue("and carries its head low", bear.chinY > cat.chinY + 5f)
        assertTrue("the bunny's hind feet are long", bunny.hind.footWidth > 2f * bunny.front!!.footWidth)
        assertTrue("the frog's hind legs are long", frog.hind.upper + frog.hind.lower > 2f * (frog.front!!.upper + frog.front!!.lower))
        assertTrue("and fold, knee forward", frog.hind.bendsForward)
        assertTrue("the monkey's arms are longer than its legs", monkey.front!!.upper + monkey.front!!.lower > monkey.hind.upper + monkey.hind.lower + 3f)
        assertFalse("a cat's hind leg bends back at the hock", cat.hind.bendsForward)
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
                val xs = listOf(x.frontNearX to y.frontNearX, x.frontFarX to y.frontFarX, x.hindNearX to y.hindNearX, x.hindFarX to y.hindFarX)
                val ups = listOf(x.frontNearUp to y.frontNearUp, x.frontFarUp to y.frontFarUp, x.hindNearUp to y.hindNearUp, x.hindFarUp to y.hindFarUp)
                assertTrue("$g a foot jumps forward at ${i * 10} ms", xs.all { (p, q) -> abs(p - q) < 6f })
                assertTrue("$g a foot jumps up at ${i * 10} ms", ups.all { (p, q) -> abs(p - q) < 3f })
                assertTrue("$g the body jumps at ${i * 10} ms", abs(x.frontSink - y.frontSink) < 3f && abs(x.rearSink - y.rearSink) < 3f)
            }
        }
    }

    @Test fun walkers_alwaysHaveTwoFeetOnTheGround_neverJustTheirHindFeet() {
        walkers.forEach { g ->
            frames(g).forEach { f ->
                val down = f.feetAboveGround().map { abs(it) < 0.01f }
                assertTrue("$g has ${down.count { it }} feet down", down.count { it } >= 2)
                assertTrue("$g has a front foot down", down[0] || down[1])
                assertTrue("$g has a hind foot down", down[2] || down[3])
            }
        }
    }

    @Test fun theCatTrots_diagonalPairsTogether() {
        frames(Gait.TROT).forEach { f ->
            val l = f.limbs
            assertEquals("near front goes with far hind", l.frontNearUp, l.hindFarUp, 0.001f)
            assertEquals("near front goes with far hind", l.frontNearX, l.hindFarX, 0.001f)
            assertEquals("far front goes with near hind", l.frontFarUp, l.hindNearUp, 0.001f)
        }
        assertTrue("they do lift", frames(Gait.TROT).maxOf { it.feetAboveGround()[0] } > 4f)
    }

    @Test fun theBearAmbles_bothLegsOfASideTogether_rollingItsShouldersAndRump() {
        val f = frames(Gait.LUMBER)
        f.forEach {
            assertEquals("a side's legs go together", it.limbs.frontNearUp, it.limbs.hindNearUp, 0.001f)
            assertEquals("a side's legs go together", it.limbs.frontFarUp, it.limbs.hindFarUp, 0.001f)
        }
        assertTrue("its shoulders and rump roll", f.any { it.limbs.frontSink > it.limbs.rearSink + 1f } && f.any { it.limbs.rearSink > it.limbs.frontSink + 1f })
        assertTrue("slower than the cat", Gait.LUMBER.moveMs > Gait.TROT.moveMs)
        assertTrue("heavier: stays lower", f.maxOf { it.liftDp } < frames(Gait.TROT).maxOf { it.liftDp } + 0.01f)
    }

    @Test fun walkers_feetGoBackOnTheGround_andForwardInTheAir() {
        walkers.forEach { g ->
            val f = frames(g).filter { it.along in 0.15f..0.85f }
            f.zipWithNext().forEach { (a, b) ->
                val up = a.limbs.frontNearUp + a.liftDp
                val moved = b.limbs.frontNearX - a.limbs.frontNearX
                if (up > 1f && b.limbs.frontNearUp + b.liftDp > 1f) assertTrue("$g swings forward", moved >= -0.01f)
                if (abs(up) < 0.01f && abs(b.limbs.frontNearUp + b.liftDp) < 0.01f) assertTrue("$g pushes back", moved <= 0.01f)
            }
        }
    }

    @Test fun jumpers_haveAllTheirFeetOnTheGroundWheneverTheyAreOnIt() {
        listOf(Gait.HOP, Gait.LEAP, Gait.FLIP).forEach { g ->
            val grounded = frames(g).filter { it.liftDp == 0f }
            grounded.forEach { f -> f.feetAboveGround().forEach { assertEquals("$g", 0f, it, 0.001f) } }
            assertTrue("$g crouches", grounded.any { it.limbs.frontSink > 3f && it.limbs.rearSink > 3f })
            // Its chest sinks the same frames it squashes: both come from one crouch. It lands front first, so the rump can follow.
            assertTrue("$g sinks as it squashes", grounded.filter { it.scaleY < 0.95f }.all { it.limbs.frontSink > 0.2f })
        }
    }

    @Test fun inTheAir_eachAnimalDoesItsOwnThing() {
        fun high(g: Gait) = frames(g).filter { it.liftDp > 15f }
        assertTrue("the bunny stretches out: front paws forward", high(Gait.HOP).maxOf { it.limbs.frontNearX } > 7f)
        assertTrue("hind feet back", high(Gait.HOP).minOf { it.limbs.hindNearX } < -7f)
        assertTrue("and comes down nose first", frames(Gait.HOP).any { it.limbs.rearSink < -3f && it.limbs.frontSink > 0f })
        assertTrue("then its hind feet swing in", frames(Gait.HOP).filter { it.liftDp == 0f }.maxOf { it.limbs.hindNearX } > 3f)
        assertTrue("the frog flings its legs out behind", high(Gait.LEAP).minOf { it.limbs.hindNearX } < -35f)
        assertTrue("the monkey curls up in its somersault", high(Gait.FLIP).any { it.limbs.frontNearUp > 7f && it.limbs.hindNearUp > 7f && it.limbs.hindNearX > 7f })
        assertTrue("the owl tucks its feet up", frames(Gait.FLY).filter { it.liftDp > 20f }.maxOf { it.limbs.hindNearUp } > 4f)
    }

    /** Each leg reaches its foot throughout a walk, for the animal that walks that way. */
    @Test fun walkersLegsReachTheirFeet() {
        listOf(CompanionAnimal.CAT, CompanionAnimal.BEAR).forEach { a ->
            val b = a.build
            val front = b.front!!
            fun reach(leg: Leg, isFront: Boolean, f: GaitFrame, x: Float, up: Float): Float =
                hipOf(leg, isFront, f.limbs).to(Offset(leg.footX + x, FOOT_Y - up))
            frames(a.gait).forEach { f ->
                val l = f.limbs
                assertTrue("${a.name} front", reach(front, true, f, l.frontNearX, l.frontNearUp) <= front.upper + front.lower + 0.5f)
                assertTrue("${a.name} hind", reach(b.hind, false, f, l.hindNearX, l.hindNearUp) <= b.hind.upper + b.hind.lower + 0.5f)
            }
        }
    }

    @Test fun atRest_everyAnimalStandsOnItsFeet() {
        CompanionAnimal.values().forEach { a ->
            val b = a.build
            val legs = listOfNotNull(b.front?.let { it to true }, b.hind to false)
            legs.forEach { (leg, isFront) ->
                val reach = hipOf(leg, isFront, LimbPose.REST).to(Offset(leg.footX, FOOT_Y))
                assertTrue("${a.name} leg reaches the ground: $reach", reach <= leg.upper + leg.lower + 0.01f)
            }
        }
    }

    @Test fun aJoint_isOneBoneFromEachEnd_onTheSideItBends() {
        val hip = Offset(-18f, 58f)
        val foot = Offset(-17f, 72f)
        for (side in listOf(-1f, 1f)) {
            val knee = kneeOf(hip, foot, 9f, 9f, side)
            assertEquals(9f, knee.to(hip), 0.01f)
            assertEquals(9f, knee.to(foot), 0.01f)
            assertTrue("bends to its side", (knee.x - (hip.x + foot.x) / 2f) * side > 0f)
        }
    }

    /** The explorer stands clear of its stop: a head at the front of a body reaches no further than a centred one did. */
    @Test fun aHeadStaysClearOfTheStopItFaces() {
        val centredHead = 0.31f * RIG_SIZE
        CompanionAnimal.values().forEach { assertTrue("${it.name}'s head reaches ${it.build.headReach}", it.build.headReach <= centredHead + 0.01f) }
    }

    @Test fun theCatsTailSwaysAsItTrots() {
        val f = frames(Gait.TROT)
        assertTrue(f.any { it.limbs.tail > 8f } && f.any { it.limbs.tail < -8f })
    }
}
