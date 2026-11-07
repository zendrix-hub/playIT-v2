package com.playit.app.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.presentation.components.VoiceMouth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The opening's Lily at the far ends of her idle motion, side by side, for review (user, 2026-11-07: her body felt
 * "like its not attached together"): at rest, leaning each way while she breathes, high in a hop and landing. Every
 * part should stay on her body, and her arms on her hands.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w900dp-h400dp-mdpi")
class LilyPuppetMotionScreenshotTest {
    @get:Rule val compose = createComposeRule()

    private fun pose(tilt: Float = 0f, bob: Float = 0f, breath: Float = 0f, tail: Float = 0f, lift: Float = 0f, squash: Float = 0f) =
        LilyPose(Offset.Zero, 0f, 0f, 0f, tilt, bob, breath, tail, 0f, lift, squash)

    @Test fun idleMotion_extremes() {
        val frames = listOf(
            "rest" to pose(),
            "lean right, breath in" to pose(tilt = 7f, bob = 0.004f, breath = 1f, tail = 10f),
            "lean left, breath out" to pose(tilt = -7f, bob = -0.004f, breath = -1f, tail = -10f),
            "hop, high" to pose(lift = 0.06f, tail = 18f),
            "landing" to pose(squash = 1f),
        )
        compose.setContent {
            Row(Modifier.background(Color(0xFFE3F2F7)).padding(8.dp)) {
                frames.forEach { (label, q) ->
                    Column(Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(width = 168.dp, height = 240.dp)) {
                            Box(Modifier.size(width = 168.dp, height = 210.dp)) {
                                LilyPuppet(LilyEyes(instant = true), VoiceMouth(), ledge = 28.dp, pose = { q },
                                    modifier = Modifier.size(width = 168.dp, height = 210.dp))
                            }
                            // the panel she peeks over, and her hands on its edge
                            Box(Modifier.offset(y = 182.dp).fillMaxWidth().height(58.dp).background(Color(0xFFFFFBF2)))
                            LilyHands(Modifier.offset(x = (168 - 168 * HAND_SPAN).dp / 2, y = 168.dp)
                                .size(width = (168 * HAND_SPAN).dp, height = 30.dp))
                        }
                        Text(label, fontSize = 12.sp)
                    }
                }
            }
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/splash_lily_motion.png")
    }
}
