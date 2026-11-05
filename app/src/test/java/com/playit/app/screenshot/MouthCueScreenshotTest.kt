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

    /**
     * The mouth saying its sound twice, as on Say It's second miss (user, 2026-11-05: "the mouth needs to move when
     * saying the letter"): the real voice envelopes, the cue's own smoothing at 60 frames a second, a frame every
     * 80 ms. Rows: m (mouse), a (apple), s (the held sound).
     */
    @Config(sdk = [34], qualifiers = "w3400dp-h400dp-mdpi")
    @Test fun mouth_saying() {
        val env = com.playit.app.presentation.components.lipsync.VoiceEnvelopes.parse(
            java.io.File("src/main/assets/lipsync/voice_envelopes.json").readText()
        )
        val rows = listOf(
            ArticulationGroup.LIPS_TOGETHER to "audio/keywords/kw_mouse.wav",
            ArticulationGroup.WIDE_OPEN to "audio/keywords/kw_apple.wav",
            ArticulationGroup.TEETH_CLOSE to "audio/phonemes/ph_s.wav"
        )
        val frames = rows.map { (_, clip) ->
            val length = generateSequence(0L) { it + 40 }.takeWhile { it < 4000 && (env.levelAt(clip, it) ?: 0f) >= 0f }
                .last { (env.levelAt(clip, it) ?: 0f) > 0f } + 120
            val pause = 500L
            val total = length * 2 + pause + 400
            var open = 0f
            val out = mutableListOf<Float>()
            var t = 0L
            while (t < total) {
                val inFirst = t < length
                val inSecond = t >= length + pause && t < 2 * length + pause
                open = if (inFirst || inSecond) {
                    val local = if (inFirst) t else t - length - pause
                    val level = env.levelAt(clip, local) ?: 0f
                    open + (com.playit.app.presentation.components.mouthSaying(level, local / 1000f) - open) * 0.3f
                } else {
                    open + (1f - open) * 0.12f       // settling back into the picture
                }
                if (t % 80 == 0L) out += open
                t += 16
            }
            out
        }
        compose.setContent {
            PlayItTheme {
                Column(Modifier.background(Color(0xFFFFF4DF))) {
                    rows.forEachIndexed { r, (g, _) ->
                        Row { frames[r].forEach { o -> MouthPicture(g.assetPath, mouthMove(g), { o }, 80.dp, Modifier.padding(1.dp)) } }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/mouth_saying.png")
        println("MOUTH_SAYING frames per row: " + frames.map { it.size })
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
