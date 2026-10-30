package com.playit.app.presentation.components.lipsync

import com.playit.app.presentation.components.MascotState

// Lily acts out each pose (user, 2026-10-30: "the movements that shes [doing] i want that to be a good animation or
// motion design"). Her pictures stay as they are; the whole picture moves, pivoting on her feet:
// - a change of pose is one motion: she crouches, the new picture shows at the bottom of the crouch, and she springs
//   up out of it a little taller, then settles;
// - each pose then has its own gesture, once: a wave rocks her side to side, pointing leans her toward her hand twice,
//   listening tilts her toward her cupped ear and leans in, thinking tilts her slowly with two little nods, encouraging
//   bounces twice toward her thumb, and celebrating jumps high and hops again;
// - listening and thinking hold their tilt while the pose lasts; every other pose ends standing as her picture shows her.
// Nothing loops (TC-POL-02): each move plays once per change of pose, per tap, or while her voice plays.

/**
 * How Lily's picture moves at one moment, pivoting on her feet: she leans [rotation] degrees (more leans her head to
 * the right), shifts [x] to the right and rises [lift] (shares of her height), and squashes or stretches.
 */
data class LilyMove(
    val rotation: Float = 0f,
    val x: Float = 0f,
    val lift: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
) {
    /** This move with [other] on top: leans and shifts add, squashes multiply. */
    operator fun plus(other: LilyMove) = LilyMove(
        rotation + other.rotation, x + other.x, lift + other.lift, scaleX * other.scaleX, scaleY * other.scaleY
    )

    fun lerp(to: LilyMove, t: Float) = LilyMove(
        rotation + (to.rotation - rotation) * t,
        x + (to.x - x) * t,
        lift + (to.lift - lift) * t,
        scaleX + (to.scaleX - scaleX) * t,
        scaleY + (to.scaleY - scaleY) * t,
    )
}

/** Standing as her picture shows her. */
val STANDING = LilyMove()

/** Where her feet are in her 512 x 512 pictures, as a share of their height: she leans and squashes from there. */
const val LILY_FEET_Y = 453f / 512f

/** How long she crouches into a new pose; the new picture shows at the bottom of the crouch. */
const val SWAP_MS = 90

/** The bottom of the crouch: lower and wider. */
val CROUCH = LilyMove(scaleX = 1.07f, scaleY = 0.90f)

private fun smooth(t: Float) = t * t * (3f - 2f * t)
private fun easeOut(t: Float) = 1f - (1f - t) * (1f - t)
private fun easeIn(t: Float) = t * t

/** One key of a move: where she is [ms] in, and how she eases there from the key before. */
private class Key(val ms: Int, val move: LilyMove, val ease: (Float) -> Float = ::smooth)

private fun List<Key>.at(ms: Float): LilyMove {
    if (ms <= first().ms) return first().move
    val i = indexOfFirst { it.ms >= ms }
    if (i == -1) return last().move
    val a = this[i - 1]
    val b = this[i]
    return a.move.lerp(b.move, b.ease((ms - a.ms) / (b.ms - a.ms)))
}

private fun lean(degrees: Float, x: Float = 0f) = LilyMove(rotation = degrees, x = x)

/** Out of the crouch: up a little taller and thinner, then back to her size. */
private val RELEASE = listOf(
    Key(0, CROUCH),
    Key(110, LilyMove(lift = 0.015f, scaleX = 0.95f, scaleY = 1.07f), ::easeOut),
    Key(210, LilyMove(scaleX = 1.02f, scaleY = 0.98f)),
    Key(300, STANDING),
)

/** Hello: she rocks side to side three times, each swing smaller. */
private val WAVE = listOf(
    Key(0, STANDING), Key(170, lean(-9f)), Key(350, lean(8f)), Key(520, lean(-6f)),
    Key(680, lean(4f)), Key(830, lean(-1.5f)), Key(960, STANDING),
)

/** Look there: she leans toward her pointing hand twice, holds it, and stands up again. */
private val POINT = listOf(
    Key(0, STANDING), Key(180, lean(8f, 0.015f)), Key(280, lean(4.5f, 0.008f)), Key(390, lean(8f, 0.015f)),
    Key(500, lean(4.5f, 0.008f)), Key(850, lean(4.5f, 0.008f)), Key(1100, STANDING),
)

/** I'm listening: she tilts toward her cupped ear and leans in, a little past and back, and stays so. */
private val LISTEN = listOf(
    Key(0, STANDING),
    Key(280, LilyMove(rotation = -8.5f, scaleX = 1.045f, scaleY = 1.045f)),
    Key(450, LilyMove(rotation = -6.5f, scaleX = 1.03f, scaleY = 1.03f)),
    Key(600, LilyMove(rotation = -7f, scaleX = 1.035f, scaleY = 1.035f)),
)

/** Hmm: she tilts slowly toward the hand at her chin, nods twice as she thinks, and stays tilted. */
private val THINK = run {
    val tilted = lean(6f)
    val nod = LilyMove(rotation = 6f, scaleX = 1.02f, scaleY = 0.965f)
    listOf(Key(0, STANDING), Key(600, tilted), Key(750, nod), Key(880, tilted), Key(1030, nod), Key(1180, tilted))
}

/** You can do it: two bounces toward her thumb, the second smaller, from the crouch (no RELEASE). */
private val ENCOURAGE = listOf(
    Key(0, CROUCH),
    Key(130, LilyMove(rotation = 4f, lift = 0.05f, scaleX = 0.95f, scaleY = 1.07f), ::easeOut),
    Key(250, LilyMove(rotation = 3f, scaleX = 1.06f, scaleY = 0.91f), ::easeIn),
    Key(370, LilyMove(rotation = 5f, lift = 0.035f, scaleX = 0.97f, scaleY = 1.04f), ::easeOut),
    Key(480, LilyMove(rotation = 3f, scaleX = 1.04f, scaleY = 0.94f), ::easeIn),
    Key(600, LilyMove(rotation = 2f, scaleX = 0.99f, scaleY = 1.01f)),
    Key(800, STANDING),
)

/** Yay: from the crouch she jumps high, stretched, twisting in the air, lands squashed, and hops once more (no RELEASE). */
private val CELEBRATE = listOf(
    Key(0, CROUCH),
    Key(130, LilyMove(rotation = -3f, lift = 0.10f, scaleX = 0.93f, scaleY = 1.10f), ::easeOut),
    Key(240, LilyMove(rotation = 3f, lift = 0.15f, scaleX = 0.99f, scaleY = 1.02f), ::easeOut),
    Key(350, LilyMove(rotation = -2f, lift = 0.09f, scaleX = 0.98f, scaleY = 1.04f), ::easeIn),
    Key(430, LilyMove(lift = 0f, scaleX = 0.97f, scaleY = 1.05f), ::easeIn),
    Key(510, LilyMove(scaleX = 1.09f, scaleY = 0.87f), ::easeOut),
    Key(620, LilyMove(lift = 0.06f, scaleX = 0.97f, scaleY = 1.05f), ::easeOut),
    Key(730, LilyMove(lift = 0f, scaleX = 0.99f, scaleY = 1.02f), ::easeIn),
    Key(800, LilyMove(scaleX = 1.05f, scaleY = 0.93f), ::easeOut),
    Key(900, LilyMove(scaleX = 0.99f, scaleY = 1.01f)),
    Key(980, STANDING),
)

/** The gesture [pose] adds to RELEASE; null for the poses with a whole move of their own. */
private fun gesture(pose: MascotState): List<Key>? = when (pose) {
    MascotState.IDLE -> listOf(Key(0, STANDING))
    MascotState.WAVING -> WAVE
    MascotState.POINTING -> POINT
    MascotState.LISTENING -> LISTEN
    MascotState.THINKING -> THINK
    MascotState.ENCOURAGING, MascotState.CELEBRATING -> null
}

/** Her move [ms] after the new picture of [pose] shows, starting from the crouch. */
fun actingAt(pose: MascotState, ms: Float): LilyMove = when (pose) {
    MascotState.ENCOURAGING -> ENCOURAGE.at(ms)
    MascotState.CELEBRATING -> CELEBRATE.at(ms)
    else -> RELEASE.at(ms) + gesture(pose)!!.at(ms)
}

/** How long [pose]'s acting lasts after the new picture shows; after it she holds actingAt(pose, this). */
fun actingMs(pose: MascotState): Int = when (pose) {
    MascotState.ENCOURAGING -> ENCOURAGE.last().ms
    MascotState.CELEBRATING -> CELEBRATE.last().ms
    else -> maxOf(RELEASE.last().ms, gesture(pose)!!.last().ms)
}

/**
 * Her move [ms] after her pose changed to [pose], from wherever she was then ([from]: standing, or part way through
 * the last move): she crouches for SWAP_MS, the new picture shows, and she acts it out.
 */
fun poseChangeAt(pose: MascotState, ms: Float, from: LilyMove): LilyMove =
    if (ms < SWAP_MS) from.lerp(CROUCH, smooth((ms / SWAP_MS).coerceAtLeast(0f)))
    else actingAt(pose, ms - SWAP_MS)

/** The whole length of a change to [pose], crouch included. */
fun poseChangeMs(pose: MascotState): Int = SWAP_MS + actingMs(pose)

/** What a tap on Lily does, one after the other. */
enum class LilyTap { BOING, HOP, GIGGLE }

/** The reaction to the [tap]-th tap (1 is the first). */
fun lilyTap(tap: Int): LilyTap = LilyTap.values()[(tap - 1).mod(LilyTap.values().size)]

private val BOING = listOf(
    Key(0, STANDING),
    Key(90, LilyMove(scaleX = 1.12f, scaleY = 0.86f), ::easeOut),
    Key(210, LilyMove(lift = 0.02f, scaleX = 0.93f, scaleY = 1.09f), ::easeOut),
    Key(320, LilyMove(scaleX = 1.04f, scaleY = 0.97f)),
    Key(420, LilyMove(scaleX = 0.99f, scaleY = 1.01f)),
    Key(500, STANDING),
)

private val HOP = listOf(
    Key(0, STANDING),
    Key(80, LilyMove(scaleX = 1.06f, scaleY = 0.92f)),
    Key(220, LilyMove(lift = 0.08f, scaleX = 0.96f, scaleY = 1.06f), ::easeOut),
    Key(360, LilyMove(lift = 0f, scaleX = 0.98f, scaleY = 1.03f), ::easeIn),
    Key(430, LilyMove(scaleX = 1.06f, scaleY = 0.92f), ::easeOut),
    Key(540, STANDING),
)

private val GIGGLE = run {
    fun shake(deg: Float) = LilyMove(rotation = deg, scaleX = 1.03f, scaleY = 1.03f)
    listOf(
        Key(0, STANDING), Key(70, shake(-5f)), Key(150, shake(5f)), Key(230, shake(-5f)),
        Key(310, shake(4f)), Key(390, shake(-2f)), Key(480, STANDING),
    )
}

private fun LilyTap.keys() = when (this) {
    LilyTap.BOING -> BOING
    LilyTap.HOP -> HOP
    LilyTap.GIGGLE -> GIGGLE
}

/** Her move [ms] into the reaction to a tap. */
fun tapMoveAt(tap: LilyTap, ms: Float): LilyMove = tap.keys().at(ms)

/** How long a tap's reaction lasts. */
fun tapMs(tap: LilyTap): Int = tap.keys().last().ms

/** While she speaks, her body goes with her voice: up and a little taller on the loud parts ([level] 0..1). */
fun talkMove(level: Float): LilyMove {
    val l = level.coerceIn(0f, 1f)
    return LilyMove(lift = 0.012f * l, scaleX = 1f - 0.02f * l, scaleY = 1f + 0.045f * l)
}
