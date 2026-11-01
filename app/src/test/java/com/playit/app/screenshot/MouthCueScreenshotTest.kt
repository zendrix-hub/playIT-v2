package com.playit.app.screenshot

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.components.MOUTH_SETTLE
import com.playit.app.presentation.components.MouthPicture
import com.playit.app.presentation.components.lipsync.mouthMove
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The nine mouth shapes opening, on the real settling curve, a frame every 40 ms (user, 2026-11-01). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MouthCueScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    private val settle = TargetBasedAnimation(MOUTH_SETTLE, Float.VectorConverter, 0f, 1f)
    private fun openAt(ms: Int) = settle.getValueFromNanos(ms * 1_000_000L)

    @Config(sdk = [34])
    @Test fun theMouthSettlesOnThePicture_withASmallOvershoot() {
        assertEquals(0f, openAt(0), 0f)
        assertEquals(1f, settle.targetValue, 0f)
        assertEquals(1f, settle.getValueFromNanos(settle.durationNanos), 0f)
        val peak = (0..2000 step 10).maxOf { openAt(it) }
        assertTrue("it overshoots: $peak", peak > 1.03f)
        assertTrue("not by much: $peak", peak < 1.25f)
        assertTrue("it has opened quickly", openAt(300) > .9f)
        assertTrue("and settled soon: ${settle.durationNanos / 1_000_000} ms", settle.durationNanos < 1_200_000_000L)
    }

    /** Rows of the nine shapes, then frames across. */
    @Config(sdk = [34], qualifiers = "w2860dp-h1000dp-mdpi")
    @Test fun mouth_filmstrips() {
        compose.setContent {
            PlayItTheme {
                Column(Modifier.background(Color(0xFFFFF4DF))) {
                    ArticulationGroup.values().forEach { g ->
                        Row {
                            for (i in 0 until 26) {
                                val open = openAt(i * 40)
                                MouthPicture(g.assetPath, mouthMove(g), { open }, 106.dp, Modifier.padding(2.dp))
                            }
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/mouth_cues.png")
    }

    /** Closed, half way and settled, large enough to judge the drawing. */
    @Config(sdk = [34], qualifiers = "w940dp-h2760dp-mdpi")
    @Test fun mouth_large() {
        compose.setContent {
            PlayItTheme {
                Column(Modifier.background(Color(0xFFFFF4DF))) {
                    ArticulationGroup.values().forEach { g ->
                        Row {
                            listOf(0f, .5f, 1f).forEach { open ->
                                MouthPicture(g.assetPath, mouthMove(g), { open }, 300.dp, Modifier.padding(4.dp))
                            }
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/mouth_large.png")
    }
}
