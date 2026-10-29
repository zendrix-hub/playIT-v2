package com.playit.app.presentation.map.components

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

// How the child's animal moves on the map (user, 2026-10-29: "on the map like the animals walk fly etc. depends on
// the animal that you choose, like when you finish the level"). Each animal goes from the stop it just finished to the
// new one in its own way: the cat trots, the monkey leaps with a somersault, the bunny hops, the bear lumbers, the frog
// leaps and the owl flies. Their pictures are heads, so the motion tells it all. A tap, or the map opening, plays the
// same move on the spot. Each gait is a pure function of time that starts on the ground and ends at rest.

/** How long the dust of a landing takes to spread and fade. */
const val DUST_MS = 320

enum class Gait(val moveMs: Int) {
    TROT(WALK_MS),
    FLIP(1300),
    HOP(1350),
    LUMBER(1700),
    LEAP(1400),
    FLY(1700);

    /** The move, and the dust of its last landing settling. */
    val totalMs: Int get() = moveMs + DUST_MS
}

val CompanionAnimal.gait: Gait
    get() = when (this) {
        CompanionAnimal.CAT -> Gait.TROT
        CompanionAnimal.MONKEY -> Gait.FLIP
        CompanionAnimal.BUNNY -> Gait.HOP
        CompanionAnimal.BEAR -> Gait.LUMBER
        CompanionAnimal.FROG -> Gait.LEAP
        CompanionAnimal.OWL -> Gait.FLY
    }

/**
 * One frame of a gait: how far along the way it is (0 at the old stop, 1 at the new), how high above the ground (dp),
 * its tilt (degrees, clockwise), its squash and stretch (kept standing on the ground), and the dust of a landing
 * (0..1 while it spreads, 0 when there is none).
 */
data class GaitFrame(
    val along: Float,
    val liftDp: Float,
    val rotation: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val dust: Float = 0f
) {
    companion object {
        val REST = GaitFrame(1f, 0f)
    }
}

/** [gait] at [ms] from its start, travelling in [direction] (+1 right, -1 left), which it leans and turns towards. */
fun gaitAt(gait: Gait, ms: Float, direction: Float = 1f): GaitFrame {
    val p = (ms / gait.moveMs).coerceIn(0f, 1f)
    val frame = when (gait) {
        Gait.TROT -> trot(p)
        Gait.LUMBER -> lumber(p)
        Gait.FLY -> fly(p, direction)
        Gait.HOP -> jumps(p, HOP, direction)
        Gait.LEAP -> jumps(p, LEAP, direction)
        Gait.FLIP -> jumps(p, FLIP, direction)
    }
    val rested = if (p >= 1f) GaitFrame.REST else frame
    return rested.copy(dust = dustAt(gait, ms))
}

// ---- Curves -----------------------------------------------------------------------------------------------------

private fun seg(t: Float, start: Float, end: Float): Float = ((t - start) / (end - start)).coerceIn(0f, 1f)
private fun smooth(t: Float): Float = t * t * (3f - 2f * t)
private fun easeInOut(t: Float): Float = if (t < 0.5f) 2f * t * t else 1f - (-2f * t + 2f).let { it * it } / 2f

/** Up and back down: 0 at both ends (exactly, so a move ends at rest), 1 halfway. */
private fun bump(t: Float): Float = if (t <= 0f || t >= 1f) 0f else sin(PI.toFloat() * t)

/** A wave that is 0 at both ends: [cycles] full turns of a sine over 0..1. */
private fun wave(t: Float, cycles: Float): Float = if (t <= 0f || t >= 1f) 0f else sin(2f * PI.toFloat() * cycles * t)

// ---- The walkers ------------------------------------------------------------------------------------------------

/** The cat trots: walkAt's four light steps, tipping to one side and the other with each. */
private fun trot(p: Float): GaitFrame {
    val step = walkAt(p)
    return GaitFrame(step.along, step.liftDp, rotation = 6f * wave(p, 2f))
}

/** The bear lumbers: slow, heavy, rocking from side to side over four steps and sinking a little on each. */
private fun lumber(p: Float): GaitFrame {
    val rock = wave(p, 2f)
    val footfall = (1f - abs(rock)).let { it * it * it * it } * bump(p)
    return GaitFrame(
        along = smooth(p),
        liftDp = 6f * abs(rock),
        rotation = 9f * rock,
        scaleX = 1f + 0.04f * footfall,
        scaleY = 1f - 0.06f * footfall
    )
}

/** The owl takes off from a crouch, flies up and across with its wings beating, glides down and lands. */
private const val OWL_TAKEOFF = 0.12f
private const val OWL_LANDS = 0.88f
private const val OWL_HEIGHT_DP = 64f
private const val OWL_FLAPS = 6f

private fun fly(p: Float, direction: Float): GaitFrame = when {
    p < OWL_TAKEOFF -> {
        val q = bump(seg(p, 0f, OWL_TAKEOFF))
        GaitFrame(0f, 0f, scaleX = 1f + 0.06f * q, scaleY = 1f - 0.10f * q)
    }
    p < OWL_LANDS -> {
        val f = seg(p, OWL_TAKEOFF, OWL_LANDS)
        // Up for the first 30 %, across, and down for the last 30 %.
        val high = smooth(seg(f, 0f, 0.3f)) * (1f - smooth(seg(f, 0.7f, 1f)))
        val beat = wave(f, OWL_FLAPS)
        GaitFrame(
            along = smooth(seg(f, 0.15f, 0.85f)),
            liftDp = OWL_HEIGHT_DP * high + 6f * beat * high,
            rotation = direction * 8f * high + 3f * wave(f, 1.5f),
            scaleX = 1f + 0.07f * beat * high,
            scaleY = 1f - 0.04f * beat * high
        )
    }
    else -> {
        val q = bump(seg(p, OWL_LANDS, 1f))
        GaitFrame(1f, 0f, scaleX = 1f + 0.06f * q, scaleY = 1f - 0.10f * q)
    }
}

// ---- The jumpers ------------------------------------------------------------------------------------------------

/**
 * A run of [count] jumps: each a crouch to [crouchEnd] of its time, a flight to [airEnd] at [heightDp] at its highest,
 * and a landing. [crouch], [stretch] and [land] are how much it squashes in the crouch, stretches leaving and arriving,
 * and squashes on landing. [lean] tips it back as it leaves and forward as it lands; [spin] turns it full circles.
 */
private class Jumps(
    val count: Int,
    val heightDp: Float,
    val crouchEnd: Float,
    val airEnd: Float,
    val crouch: Float,
    val stretch: Float,
    val land: Float,
    val lean: Float = 0f,
    val spin: Float = 0f
)

/** The bunny: three quick hops. */
private val HOP = Jumps(count = 3, heightDp = 24f, crouchEnd = 0.15f, airEnd = 0.85f, crouch = 0.12f, stretch = 0.10f, land = 0.14f, lean = 6f)

/** The frog: two big leaps from a deep crouch, landing with a splat. */
private val LEAP = Jumps(count = 2, heightDp = 46f, crouchEnd = 0.22f, airEnd = 0.82f, crouch = 0.22f, stretch = 0.18f, land = 0.22f, lean = 10f)

/** The monkey: one high leap with a somersault. */
private val FLIP = Jumps(count = 1, heightDp = 56f, crouchEnd = 0.22f, airEnd = 0.84f, crouch = 0.16f, stretch = 0.08f, land = 0.16f, spin = 1f)

private fun jumps(p: Float, j: Jumps, direction: Float): GaitFrame {
    val i = floor(p * j.count).toInt().coerceAtMost(j.count - 1)
    val t = p * j.count - i
    return when {
        t < j.crouchEnd -> {
            val q = easeInOut(seg(t, 0f, j.crouchEnd))
            GaitFrame(i.toFloat() / j.count, 0f, scaleX = 1f + 0.7f * j.crouch * q, scaleY = 1f - j.crouch * q)
        }
        t < j.airEnd -> {
            // A thrown ball: steady across, a parabola up and down, most stretched where it is fastest.
            val u = seg(t, j.crouchEnd, j.airEnd)
            val fast = abs(1f - 2f * u)
            GaitFrame(
                along = (i + u) / j.count,
                liftDp = j.heightDp * 4f * u * (1f - u),
                rotation = (-direction * j.lean * wave(u, 1f) + direction * 360f * j.spin * easeInOut(u)) % 360f,
                scaleX = 1f - 0.6f * j.stretch * fast,
                scaleY = 1f + j.stretch * fast
            )
        }
        else -> {
            val q = bump(seg(t, j.airEnd, 1f))
            GaitFrame((i + 1f) / j.count, 0f, scaleX = 1f + 0.8f * j.land * q, scaleY = 1f - j.land * q)
        }
    }
}

/** The dust of the latest landing at [ms]: 0..1 while it spreads, 0 when there is none. */
private fun dustAt(gait: Gait, ms: Float): Float {
    val j = when (gait) {
        Gait.HOP -> HOP
        Gait.LEAP -> LEAP
        Gait.FLIP -> FLIP
        Gait.FLY -> return dustSince(ms - OWL_LANDS * gait.moveMs)
        else -> return 0f
    }
    val landings = (0 until j.count).map { (it + j.airEnd) / j.count * gait.moveMs }
    val last = landings.lastOrNull { it <= ms } ?: return 0f
    return dustSince(ms - last)
}

private fun dustSince(ms: Float): Float = if (ms <= 0f || ms >= DUST_MS) 0f else ms / DUST_MS
