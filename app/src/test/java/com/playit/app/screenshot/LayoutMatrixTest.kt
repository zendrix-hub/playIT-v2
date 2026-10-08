package com.playit.app.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// (the size must be set before the test activity starts, so it can't be a runtime parameter).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class LayoutMatrixTest(private val deviceName: String) {
    @get:Rule val compose = createComposeRule()

    /** Pictures decode on the first frame in tests, so captures always include them (card 17c). */
    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    /** Fails if the node's bottom is below the window (the child would have to scroll to reach it). */
    protected fun assertOnScreen(tag: String) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true).getBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertTrue("$tag bottom ${node.bottom} > window ${root.bottom} on $deviceName", node.bottom <= root.bottom)
    }

    protected fun capture(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/${name}_$deviceName.png")
    }

    /** Font-scale checks run on every size except compact (360x640 at 1.3 is below our support floor). */
    protected fun assumeFontScaleChecks() = assumeTrue(deviceName != "compact")

    @Composable
    private fun scaffoldUnderTest() = LessonScaffold(
        topBar = { Box(Modifier.height(64.dp)) },
        header = { MascotSpeechHeader(message = "Listen closely to the sound of the letter, then tap play.") },
        bottomBar = { Box(Modifier.fillMaxWidth().height(64.dp).testTag("bottom")) },
    ) { Box(Modifier.size(120.dp).testTag("main")) }

    @Test fun lessonScaffold_bottomBarAndBodyVisible() {
        compose.setContent { PlayItTheme { scaffoldUnderTest() } }
        assertOnScreen("main"); assertOnScreen("bottom"); capture("scaffold")
    }

    @Test fun fontScale13_mainActionVisible() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { scaffoldUnderTest() } } }
        assertOnScreen("main"); assertOnScreen("bottom")
    }

    @Test fun tablet_contentWidthCapped() {
        compose.setContent {
            PlayItTheme {
                LessonScaffold(topBar = {}, header = {}, bottomBar = {}) {
                    Box(Modifier.fillMaxWidth().height(10.dp).testTag("wide"))
                }
            }
        }
        val w = compose.onNodeWithTag("wide").getBoundsInRoot().let { it.right - it.left }
        assertTrue(w <= 560.dp)
    }
}

@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.COMPACT)
class LayoutMatrixCompactTest : LayoutMatrixTest("compact")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.A21S)
class LayoutMatrixA21sTest : LayoutMatrixTest("a21s")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.PHONE)
class LayoutMatrixPhoneTest : LayoutMatrixTest("phone")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.TABLET)
class LayoutMatrixTabletTest : LayoutMatrixTest("tablet")
