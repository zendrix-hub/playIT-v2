package com.playit.app.domain.model

/**
 * Mouth-shape groups for articulation cues; pictures come from docs/image-release (card 25).
 * Each letter maps to what a child can see in a mirror for the sound the app teaches (the key
 * word's first sound), sorted as in phonics placement cues (lip poppers, tip tappers, lip rounders,
 * open vowels). A teacher still checks the pairs during Gate 3; the user approved the pictures.
 */
enum class ArticulationGroup(val assetPath: String) {
    LIPS_TOGETHER("images/mouth/mouth_lips_together.png"),   // m b p
    TEETH_ON_LIP("images/mouth/mouth_teeth_on_lip.png"),     // f v
    TONGUE_UP("images/mouth/mouth_tongue_up.png"),           // t d n l r
    TEETH_CLOSE("images/mouth/mouth_teeth_close.png"),       // s z x
    BACK_OF_MOUTH("images/mouth/mouth_back.png"),            // k c g u
    ROUND_LIPS("images/mouth/mouth_round.png"),              // w q j
    WIDE_OPEN("images/mouth/mouth_wide_open.png"),           // a o
    SMILE("images/mouth/mouth_smile.png"),                   // e i y
    OPEN_BREATH("images/mouth/mouth_open_breath.png"),       // h
}

/**
 * Card 28b (phone test of APK D, 2026-10-11) moved five letters whose picture showed the wrong mouth:
 * - o (orange, octopus): the short o opens the jaw wide; the tight round lips are the "oo" of w.
 * - u (umbrella): the short u is a relaxed, half-open "uh" with the tongue low and back, not round lips.
 * - j (jug): lips pushed forward and rounded, teeth close; nothing like the open breath of h.
 * - r (rabbit): the tongue tip lifts and curls. Not round lips: rounding teaches "wabbit".
 * - q (queen, /kw/): the /k/ is hidden at the back; what shows is the lips rounding for /w/.
 */
fun articulationFor(letter: String): ArticulationGroup = when (letter.lowercase().trim()) {
    "m", "b", "p" -> ArticulationGroup.LIPS_TOGETHER
    "f", "v" -> ArticulationGroup.TEETH_ON_LIP
    "t", "d", "n", "l", "r" -> ArticulationGroup.TONGUE_UP
    "s", "z", "x" -> ArticulationGroup.TEETH_CLOSE
    "k", "c", "g", "u" -> ArticulationGroup.BACK_OF_MOUTH
    "w", "q", "j" -> ArticulationGroup.ROUND_LIPS
    "a", "o" -> ArticulationGroup.WIDE_OPEN
    "e", "i", "y" -> ArticulationGroup.SMILE
    else -> ArticulationGroup.OPEN_BREATH // h
}
