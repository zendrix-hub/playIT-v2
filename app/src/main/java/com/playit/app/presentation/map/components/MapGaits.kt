package com.playit.app.presentation.map.components

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

// How the child's animal moves on the map (user, 2026-10-29: "on the map like the animals walk fly etc. depends on
// the animal that you choose, like when you finish the level"). Each animal goes from the stop it just finished to the
// new one in its own way, as that animal does, and none walks on two legs (user, 2026-11-01): the cat trots on four legs,
// the bear ambles on four, the bunny hops front paws first, the frog leaps with its legs flung out behind, the monkey
// leaps from all fours with a somersault, and the owl flies. A tap, or the map opening, plays the same move on the spot.
// Each gait is a pure function of time that starts on the ground and ends at rest, and it says where the limbs are too
// (MapLimbs.kt), so they keep time.

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

    /** It goes on foot, a pair of feet on the ground at a time, moving over them. */
    val walks: Boolean get() = this == TROT || this == LUMBER
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
 * its tilt (degrees, clockwise), its squash and stretch (kept standing on the ground), the dust of a landing (0..1
 * while it spreads, 0 when there is none), the owl's wings: how far out they are (0 folded away, 1 spread) and
 * where they are in a beat (-1 up, 1 down), and where its limbs are.
 */
data class GaitFrame(
    val along: Float,
    val liftDp: Float,
    val rotation: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val dust: Float = 0f,
    val wings: Float = 0f,
    val flap: Float = 0f,
    val limbs: LimbPose = LimbPose.REST
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

/** A foot at [phase] in its stride, in cycles: on the ground going back from [stride] to -[stride], then up and forward. */
private fun strideX(phase: Float, stride: Float): Float {
    val f = phase % 1f
    return if (f < 0.5f) stride * (1f - 4f * f) else stride * (2f * smooth((f - 0.5f) * 2f) - 1f)
}

private fun strideUp(phase: Float, stepDp: Float): Float {
    val f = phase % 1f
    return if (f < 0.5f) 0f else stepDp * sin(PI.toFloat() * (f - 0.5f) * 2f)
}

/**
 * A walker's limbs at [p], over two full strides, so four beats: on each, one pair of feet stays on the ground as the
 * body rises by [lift], and the other pair swings forward [stepDp] off it. The pairs are [diagonal] (near front with far
 * hind, as in a trot) or on the same side (as in a bear's amble). It walks with its body [sinkDp] lower, to leave the
 * legs the reach; [roll] tips its chest and rump against each other.
 */
private fun walking(p: Float, lift: Float, stride: Float, stepDp: Float, diagonal: Boolean, sinkDp: Float, roll: Float = 0f, tail: Float = 0f): LimbPose {
    val walking = smooth(seg(p, 0f, 0.05f)) * (1f - smooth(seg(p, 0.95f, 1f)))
    val c = 2f * p
    val hindNear = if (diagonal) 0.5f else 0f
    val hindFar = if (diagonal) 0f else 0.5f
    return LimbPose(
        frontNearX = strideX(c, stride) * walking,
        frontNearUp = strideUp(c, stepDp) * walking - lift,
        frontFarX = strideX(c + 0.5f, stride) * walking,
        frontFarUp = strideUp(c + 0.5f, stepDp) * walking - lift,
        hindNearX = strideX(c + hindNear, stride) * walking,
        hindNearUp = strideUp(c + hindNear, stepDp) * walking - lift,
        hindFarX = strideX(c + hindFar, stride) * walking,
        hindFarUp = strideUp(c + hindFar, stepDp) * walking - lift,
        frontSink = sinkDp * walking + roll,
        rearSink = sinkDp * walking - roll,
        tail = tail
    )
}

/** The cat trots, low and light on four legs: diagonal pairs together, its tail swaying. */
private fun trot(p: Float): GaitFrame {
    val step = walkAt(p)
    val beat = wave(p, 2f)
    // A little of walkAt's bounce: the feet on the ground stay there, and legs can only stretch so far.
    val lift = step.liftDp * 0.15f
    return GaitFrame(
        step.along, lift, rotation = 1.5f * beat,
        limbs = walking(p, lift, stride = 7f, stepDp = 6f, diagonal = true, sinkDp = 2f, tail = 14f * wave(p, 1f))
    )
}

/** The bear ambles: slow and heavy on four legs, both legs of a side together, its shoulders and rump rolling. */
private fun lumber(p: Float): GaitFrame {
    val rock = wave(p, 2f)
    val footfall = (1f - abs(rock)).let { it * it * it * it } * bump(p)
    val lift = abs(rock)
    return GaitFrame(
        along = smooth(p),
        liftDp = lift,
        rotation = 2f * rock,
        scaleX = 1f + 0.04f * footfall,
        scaleY = 1f - 0.06f * footfall,
        limbs = walking(p, lift, stride = 5f, stepDp = 5f, diagonal = false, sinkDp = 1.5f, roll = rock)
    )
}

/**
 * The owl takes off from a crouch, its wings opening and pushing down; flies up and across with them beating; glides
 * down with them held out, and folds them away as it lands (user, 2026-10-29: wings, as its picture is a head).
 */
private const val OWL_TAKEOFF = 0.12f
private const val OWL_LANDS = 0.88f
private const val OWL_HEIGHT_DP = 64f
private const val OWL_FLAPS = 6f

private fun fly(p: Float, direction: Float): GaitFrame = when {
    p < OWL_TAKEOFF -> {
        val t = seg(p, 0f, OWL_TAKEOFF)
        val q = bump(t)
        // The wings open and come up for the first beat.
        GaitFrame(0f, 0f, scaleX = 1f + 0.06f * q, scaleY = 1f - 0.10f * q, wings = smooth(t), flap = -smooth(t), limbs = LimbPose(frontSink = 4f * q))
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
            scaleY = 1f - 0.04f * beat * high,
            wings = 1f,
            // From the raised first beat into the flight's beats; held still and out in the glide down.
            flap = (1f - seg(f, 0.75f, 0.9f)) * wingBeat(f),
            // Its feet tuck up while it flies and reach down for the ground.
            limbs = LimbPose(hindNearUp = 6f * high, hindFarUp = 6f * high)
        )
    }
    else -> {
        val t = seg(p, OWL_LANDS, 1f)
        val q = bump(t)
        GaitFrame(1f, 0f, scaleX = 1f + 0.06f * q, scaleY = 1f - 0.10f * q, wings = 1f - smooth(t), limbs = LimbPose(frontSink = 4f * q))
    }
}

/**
 * Where the wings are in their beat during the flight [f] 0..1: they start raised (-1), as the take-off left them, and
 * beat [OWL_FLAPS] times, each a quick push down and a slower lift.
 */
private fun wingBeat(f: Float): Float {
    val phase = (f * OWL_FLAPS) % 1f
    // Down (-1 to 1) over the first 40 % of a beat, back up over the rest.
    return if (phase < 0.4f) -1f + 2f * smooth(phase / 0.4f) else 1f - 2f * smooth((phase - 0.4f) / 0.6f)
}

// ---- The jumpers ------------------------------------------------------------------------------------------------

/**
 * A run of [count] jumps: each a crouch to [crouchEnd] of its time, a flight to [airEnd] at [heightDp] at its highest,
 * and a landing. [crouch], [stretch] and [land] are how much it squashes in the crouch, stretches leaving and arriving,
 * and squashes on landing. [lean] tips it back as it leaves and forward as it lands; [spin] turns it full circles.
 * Its body and limbs go through five poses: crouched, pushing off, in the air, reaching for the ground (its feet level
 * with where they stand, so it touches down on them), and landed, then back to standing.
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
    val spin: Float = 0f,
    val crouched: LimbPose,
    val pushing: LimbPose,
    val inAir: LimbPose,
    val reaching: LimbPose,
    val landed: LimbPose
)

/** The same x and up for both front feet and both hind feet, as a jumper's feet go in pairs. */
private fun pairs(
    frontX: Float = 0f, frontUp: Float = 0f, hindX: Float = 0f, hindUp: Float = 0f,
    frontSink: Float = 0f, rearSink: Float = 0f, frontOut: Float = 0f, rearOut: Float = 0f, tail: Float = 0f
) = LimbPose(frontX, frontUp, frontX, frontUp, hindX, hindUp, hindX, hindUp, frontSink, rearSink, frontOut, rearOut, tail)

/**
 * The bunny: three quick hops. It bunches up, its long hind legs drive it out stretched long, it comes down on its front
 * paws with its rump still up, and its hind feet swing in behind them.
 */
private val HOP = Jumps(
    count = 3, heightDp = 24f, crouchEnd = 0.15f, airEnd = 0.85f, crouch = 0.12f, stretch = 0.10f, land = 0.14f, lean = 6f,
    crouched = pairs(frontSink = 4f, rearSink = 4f, frontOut = -2f),
    pushing = pairs(frontX = 8f, frontUp = 2f, hindX = -9f, hindUp = -3f, frontSink = -3f, rearSink = -4f, frontOut = 4f, rearOut = 3f, tail = -20f),
    inAir = pairs(frontX = 9f, hindX = -10f, hindUp = 3f, frontSink = -1f, rearSink = -3f, frontOut = 4f, rearOut = 3f),
    reaching = pairs(frontX = 6f, hindX = -4f, frontSink = 1f, rearSink = -3.5f, frontOut = 2f, rearOut = 1f),
    landed = pairs(frontX = 4f, hindX = 4f, frontSink = 3f, rearSink = 1f)
)

/** The frog: two big leaps from a deep crouch, long hind legs flung straight out behind, landing in a splat. */
private val LEAP = Jumps(
    count = 2, heightDp = 46f, crouchEnd = 0.22f, airEnd = 0.82f, crouch = 0.22f, stretch = 0.18f, land = 0.22f, lean = 10f,
    crouched = pairs(hindX = -2f, frontSink = 5f, rearSink = 4f, frontOut = -1f),
    pushing = pairs(frontX = 8f, frontUp = 4f, hindX = -30f, hindUp = 6f, frontSink = -6f, rearSink = -7f, frontOut = 3f, rearOut = 6f),
    inAir = pairs(frontX = 10f, frontUp = 2f, hindX = -42f, hindUp = 14f, frontSink = -3f, rearSink = -5f, frontOut = 3f, rearOut = 6f),
    reaching = pairs(frontX = 9f, hindX = -10f, rearSink = -3f, frontOut = 1f, rearOut = 2f),
    landed = pairs(frontX = 6f, hindX = -1f, frontSink = 6f, rearSink = 5f, frontOut = -1f)
)

/** The monkey: one high leap off all fours with a somersault, curled up tight at the top, and down on all fours again. */
private val FLIP = Jumps(
    count = 1, heightDp = 56f, crouchEnd = 0.22f, airEnd = 0.84f, crouch = 0.16f, stretch = 0.08f, land = 0.16f, spin = 1f,
    crouched = pairs(frontX = -3f, hindX = 3f, frontSink = 4f, rearSink = 6f),
    pushing = pairs(frontX = 10f, frontUp = 2f, hindX = -6f, hindUp = -3f, frontSink = -4f, rearSink = -4f, frontOut = 2f, rearOut = 2f),
    inAir = pairs(frontX = -8f, frontUp = 8f, hindX = 10f, hindUp = 10f, frontSink = -1f, rearSink = 1f, tail = 40f),
    reaching = pairs(frontX = 6f, hindX = -4f),
    landed = pairs(frontX = 6f, hindX = -4f, frontSink = 4f, rearSink = 5f)
)

private fun jumps(p: Float, j: Jumps, direction: Float): GaitFrame {
    val i = floor(p * j.count).toInt().coerceAtMost(j.count - 1)
    val t = p * j.count - i
    return when {
        t < j.crouchEnd -> {
            val q = easeInOut(seg(t, 0f, j.crouchEnd))
            GaitFrame(
                i.toFloat() / j.count, 0f, scaleX = 1f + 0.7f * j.crouch * q, scaleY = 1f - j.crouch * q,
                limbs = LimbPose.REST.towards(j.crouched, q)
            )
        }
        t < j.airEnd -> {
            // A thrown ball: steady across, a parabola up and down, most stretched where it is fastest.
            val u = seg(t, j.crouchEnd, j.airEnd)
            val fast = abs(1f - 2f * u)
            val limbs = when {
                u < 0.2f -> j.crouched.towards(j.pushing, smooth(seg(u, 0f, 0.2f)))
                u < 0.5f -> j.pushing.towards(j.inAir, smooth(seg(u, 0.2f, 0.5f)))
                else -> j.inAir.towards(j.reaching, smooth(seg(u, 0.5f, 1f)))
            }
            GaitFrame(
                along = (i + u) / j.count,
                liftDp = j.heightDp * 4f * u * (1f - u),
                rotation = (-direction * j.lean * wave(u, 1f) + direction * 360f * j.spin * easeInOut(u)) % 360f,
                scaleX = 1f - 0.6f * j.stretch * fast,
                scaleY = 1f + j.stretch * fast,
                limbs = limbs
            )
        }
        else -> {
            val after = seg(t, j.airEnd, 1f)
            val q = bump(after)
            val limbs = if (after < 0.5f) j.reaching.towards(j.landed, smooth(after / 0.5f))
            else j.landed.towards(LimbPose.REST, smooth((after - 0.5f) / 0.5f))
            GaitFrame((i + 1f) / j.count, 0f, scaleX = 1f + 0.8f * j.land * q, scaleY = 1f - j.land * q, limbs = limbs)
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
