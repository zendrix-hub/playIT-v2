package com.playit.app.presentation.theme

import androidx.compose.animation.core.SnapSpec
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayItMotionTest {
    @Test fun durationsInsideAnimationGuide() {
        assertTrue(PlayItMotion.MICRO_MS in 150..250)
        assertTrue(PlayItMotion.STANDARD_MS in 300..500)
        assertTrue(PlayItMotion.CELEBRATION_MS in 600..1200)
        assertTrue(PlayItMotion.SCREEN_MS in 200..300)
    }

    @Test fun reducedMotionSnaps() = assertTrue(PlayItMotion.tap<Float>(reduced = true) is SnapSpec)
}
