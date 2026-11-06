package com.playit.app.presentation.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.PlayItTheme
import com.playit.app.presentation.theme.SunnyGoldDark
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The lid and the rim of the Blend box stay inside its rounded corners (user, 2026-11-01: "its peaking out"). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BlendNodeShapeTest {
    @get:Rule val compose = createComposeRule()

    private val behind = Color(0xFFFF00FF)

    private fun node(unlocked: Boolean) =
        MapNode.BlendItNode(id = "blend_1", orderIndex = 0, isUnlocked = unlocked, groupId = "1", starsEarned = 0)

    /** The box's face is 90 x 76. */
    @Config(sdk = [34], qualifiers = "mdpi")
    @Test fun theBandsStayInsideTheCornersOfTheBox() {
        lateinit var view: View
        compose.setContent {
            view = LocalView.current
            PlayItTheme {
                // Inside the theme, which sets its own: the unlocked box's aura would otherwise never let the test idle.
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    Box(Modifier.background(behind)) {
                        BlendItChallengeNodeCard(node(unlocked = true), onClick = {})
                    }
                }
            }
        }
        compose.waitForIdle()
        val pixels = compose.runOnIdle {
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also { view.draw(Canvas(it)) }
        }

        // Found in the picture: the label under the box is wider than the box, and centres it.
        val sky = behind.toArgb()
        val middleRow = (0 until pixels.height).first { y -> (0 until pixels.width).any { pixels.getPixel(it, y) != sky } } + 38
        val left = (0 until pixels.width).first { pixels.getPixel(it, middleRow) != sky }
        val right = (0 until pixels.width).last { pixels.getPixel(it, middleRow) != sky }
        val top = (0 until pixels.height).first { pixels.getPixel((left + right) / 2, it) != sky }
        assertEquals("the box's width", 89, right - left)

        // Just outside the 24 dp corners at the top, the lid used to cover the sky.
        assertEquals("top left", sky, pixels.getPixel(left + 5, top + 5))
        assertEquals("top right", sky, pixels.getPixel(right - 5, top + 5))
        // Just outside the 16 dp corners at the bottom, the rim used to cover the depth band behind the box.
        assertEquals("bottom left", SunnyGoldDark.toArgb(), pixels.getPixel(left + 4, top + 72))
        assertEquals("bottom right", SunnyGoldDark.toArgb(), pixels.getPixel(right - 4, top + 72))
    }

    /** User, 2026-11-06: the stop says BLEND and its chapter again (it said WORDS for a while). */
    @Config(sdk = [34])
    @Test fun theLabelSaysBlendAndTheChapter() {
        compose.setContent {
            PlayItTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    BlendItChallengeNodeCard(node(unlocked = true), onClick = {})
                }
            }
        }
        compose.onNodeWithText("BLEND 1").assertExists()
        compose.onNodeWithText("WORDS 1").assertDoesNotExist()
    }

    /** Locked and unlocked, large enough to judge the corners. */
    @Config(sdk = [34], qualifiers = "w420dp-h220dp-xxxhdpi")
    @Test fun blend_box() {
        compose.setContent {
            PlayItTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    Row(Modifier.background(Color(0xFFDDF1F7)).padding(20.dp)) {
                        BlendItChallengeNodeCard(node(unlocked = false), onClick = {})
                        Box(Modifier.padding(start = 40.dp)) {
                            BlendItChallengeNodeCard(node(unlocked = true), onClick = {})
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/blend_box.png")
    }
}
