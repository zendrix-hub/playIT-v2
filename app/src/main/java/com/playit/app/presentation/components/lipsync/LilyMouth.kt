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
 * Where a pose's mouth is in its 512 x 512 picture (its box, measured by tools/images/make_new_lily.py: [top] and
 * [width] of the box, [centerX] its middle), and the mouth it rests in between words, drawn as the picture draws it:
 * the open smile with its tongue, the grin with its teeth, the jump's wide-open cheer, the thinking pose's little "oo".
 * [maxOpen] keeps the mouth clear of a paw drawn near it (the thinking pose's paw at her chin).
 */
data class PoseMouth(val centerX: Float, val top: Float, val width: Float, val rest: Mouth, val maxOpen: Float = 1f)

/** Her pictures' open smile: a flat-topped mouth that dips in the middle, with the tongue at the bottom. */
private fun openSmile(open: Float) = Mouth(width = 1f, open = open, curve = 0.5f, tongue = 0.6f)

fun poseMouth(pose: MascotState): PoseMouth = when (pose) {
    MascotState.CELEBRATING -> PoseMouth(255.5f, 218f, 45f, openSmile(0.7f))
    MascotState.ENCOURAGING -> PoseMouth(255f, 232f, 52f, Mouth(width = 1f, open = 0.31f, curve = 0.6f, teethTop = 1f))
    MascotState.IDLE -> PoseMouth(255.5f, 236f, 33f, openSmile(0.36f))
    MascotState.LISTENING -> PoseMouth(257.5f, 226f, 35f, openSmile(0.5f))
    MascotState.POINTING -> PoseMouth(257.5f, 225f, 35f, openSmile(0.39f))
    // her "oo" is 13 px across; talking opens it wider, as far as her paw allows
    MascotState.THINKING -> PoseMouth(264.5f, 242f, 30f, Mouth(width = 0.43f, open = 0.47f, curve = 0f, round = 1f), maxOpen = 0.6f)
    MascotState.WAVING -> PoseMouth(256f, 230f, 44f, openSmile(0.33f))
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
