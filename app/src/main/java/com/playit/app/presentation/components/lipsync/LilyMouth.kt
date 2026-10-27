package com.playit.app.presentation.components.lipsync

import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.components.MascotState

/**
 * One shape of Lily's mouth. Sizes are fractions of the pose's mouth width ([PoseMouth.width]).
 * [open] 0 draws a line; [curve] bends it (and the top lip) into a smile; [round] 1 makes the lips an "oo" ring.
 */
data class Mouth(
    val width: Float,
    val open: Float,
    val curve: Float,
    val teethTop: Float = 0f,
    val teethBottom: Float = 0f,
    val tongue: Float = 0f,
    val tongueUp: Float = 0f,
    val round: Float = 0f,
) {
    fun lerp(to: Mouth, t: Float): Mouth {
        val k = t.coerceIn(0f, 1f)
        fun mix(a: Float, b: Float) = a + (b - a) * k
        return Mouth(
            mix(width, to.width), mix(open, to.open), mix(curve, to.curve), mix(teethTop, to.teethTop),
            mix(teethBottom, to.teethBottom), mix(tongue, to.tongue), mix(tongueUp, to.tongueUp), mix(round, to.round)
        )
    }
}

/**
 * Where a pose's mouth is in its 512 x 512 picture (tools/design/make_lily_talk.py prints these), and the
 * mouth it rests in between words: a smile line, or the open grin of the celebrating and waving poses.
 * [maxOpen] keeps the mouth clear of a hand drawn under it (the thinking pose's finger).
 */
data class PoseMouth(val centerX: Float, val top: Float, val width: Float, val rest: Mouth, val maxOpen: Float = 1f)

private val SMILE_LINE = Mouth(width = 1f, open = 0f, curve = 1f)

fun poseMouth(pose: MascotState): PoseMouth = when (pose) {
    MascotState.CELEBRATING -> PoseMouth(254f, 232f, 68f, Mouth(0.8f, 0.5f, 0.8f, teethTop = 0.1f, tongue = 0.6f))
    MascotState.ENCOURAGING -> PoseMouth(256f, 228f, 65f, SMILE_LINE)
    MascotState.IDLE -> PoseMouth(248f, 226f, 65f, SMILE_LINE)
    MascotState.LISTENING -> PoseMouth(249f, 231f, 50f, SMILE_LINE)
    MascotState.POINTING -> PoseMouth(248f, 231f, 63f, SMILE_LINE)
    MascotState.THINKING -> PoseMouth(262f, 232f, 46f, Mouth(width = 0.7f, open = 0f, curve = 0.1f), maxOpen = 0.35f)
    MascotState.WAVING -> PoseMouth(252f, 226f, 58f, Mouth(0.8f, 0.35f, 0.8f, teethTop = 0.1f, tongue = 0.6f))
}

/** Lily's mouth wide open in speech; ordinary talking moves between the rest mouth and this with the loudness. */
val TALKING_WIDE = Mouth(width = 0.62f, open = 0.7f, curve = 0.4f, teethTop = 0.15f, tongue = 0.55f)

/**
 * The mouth for a letter's sound, after the mouth-shape pictures (ArticulationGroup): what a child sees in a mirror,
 * drawn in Lily's style. m, b and p press the lips together; a and o open wide.
 */
fun articulationMouth(group: ArticulationGroup): Mouth = when (group) {
    ArticulationGroup.LIPS_TOGETHER -> Mouth(width = 0.6f, open = 0f, curve = 0f)
    ArticulationGroup.TEETH_ON_LIP -> Mouth(width = 0.6f, open = 0.28f, curve = 0.2f, teethTop = 0.5f)
    ArticulationGroup.TONGUE_UP -> Mouth(width = 0.62f, open = 0.38f, curve = 0.3f, teethTop = 0.18f, tongue = 0.6f, tongueUp = 1f)
    ArticulationGroup.TEETH_CLOSE -> Mouth(width = 0.72f, open = 0.22f, curve = 0.4f, teethTop = 0.5f, teethBottom = 0.5f)
    ArticulationGroup.BACK_OF_MOUTH -> Mouth(width = 0.6f, open = 0.45f, curve = 0.3f, tongue = 0.5f)
    ArticulationGroup.ROUND_LIPS -> Mouth(width = 0.4f, open = 0.4f, curve = 0f, round = 1f)
    ArticulationGroup.WIDE_OPEN -> Mouth(width = 0.66f, open = 0.72f, curve = 0.3f, teethTop = 0.12f, tongue = 0.5f)
    ArticulationGroup.SMILE -> Mouth(width = 0.82f, open = 0.26f, curve = 0.8f, teethTop = 0.4f)
    ArticulationGroup.OPEN_BREATH -> Mouth(width = 0.55f, open = 0.42f, curve = 0.2f, tongue = 0.3f)
}
