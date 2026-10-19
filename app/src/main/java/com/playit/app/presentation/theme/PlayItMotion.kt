package com.playit.app.presentation.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring

object PlayItMotion {
    const val MICRO_MS = 200
    const val STANDARD_MS = 350
    const val CELEBRATION_MS = 900
    const val SCREEN_MS = 250

    /** Tap press and release (21_ANIMATION_GUIDE: 100 -> 92 -> 100 %, MediumBouncy). */
    fun <T> tap(reduced: Boolean): AnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)

    /** Critically damped settle, no overshoot (colours, opacity, screen moves). */
    fun <T> settle(reduced: Boolean): AnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow)
}
