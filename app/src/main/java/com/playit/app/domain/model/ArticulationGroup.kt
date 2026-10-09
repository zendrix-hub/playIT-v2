package com.playit.app.domain.model

/**
 * Mouth-shape groups for articulation cues; pictures come from docs/image-release (card 25).
 * The groups are approximations for a child's cue, not phonetics: a teacher checks them during
 * Gate 3, and the user approves the pictures.
 */
enum class ArticulationGroup(val assetPath: String) {
    LIPS_TOGETHER("images/mouth/mouth_lips_together.png"),   // m b p
    TEETH_ON_LIP("images/mouth/mouth_teeth_on_lip.png"),     // f v
    TONGUE_UP("images/mouth/mouth_tongue_up.png"),           // t d n l
    TEETH_CLOSE("images/mouth/mouth_teeth_close.png"),       // s z x
    BACK_OF_MOUTH("images/mouth/mouth_back.png"),            // k c g q
    ROUND_LIPS("images/mouth/mouth_round.png"),              // o u w
    WIDE_OPEN("images/mouth/mouth_wide_open.png"),           // a
    SMILE("images/mouth/mouth_smile.png"),                   // e i y
    OPEN_BREATH("images/mouth/mouth_open_breath.png"),       // h j r
}

fun articulationFor(letter: String): ArticulationGroup = when (letter.lowercase()) {
    "m", "b", "p" -> ArticulationGroup.LIPS_TOGETHER
    "f", "v" -> ArticulationGroup.TEETH_ON_LIP
    "t", "d", "n", "l" -> ArticulationGroup.TONGUE_UP
    "s", "z", "x" -> ArticulationGroup.TEETH_CLOSE
    "k", "c", "g", "q" -> ArticulationGroup.BACK_OF_MOUTH
    "o", "u", "w" -> ArticulationGroup.ROUND_LIPS
    "a" -> ArticulationGroup.WIDE_OPEN
    "e", "i", "y" -> ArticulationGroup.SMILE
    else -> ArticulationGroup.OPEN_BREATH // h j r
}
