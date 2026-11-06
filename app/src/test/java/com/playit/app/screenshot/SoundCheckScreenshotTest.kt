package com.playit.app.screenshot

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.domain.model.SoundCheckResult
import com.playit.app.presentation.soundcheck.SoundCheckContent
import com.playit.app.presentation.soundcheck.SoundCheckSpeech
import com.playit.app.presentation.soundcheck.SoundCheckTap
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The sound check before the word game (user choice, 2026-11-06) fits from the smallest phone to the tablet. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SoundCheckScreenshotTest {
    @get:Rule val compose = createComposeRule()

    private fun asking(name: String, tapped: MutableList<String> = mutableListOf()) {
        compose.setContent {
            PlayItTheme {
                SoundCheckContent(
                    tiles = listOf("s", "i", "m", "a"), index = 1, total = 4,
                    speech = SoundCheckSpeech.GOOD_TRY, lastTap = SoundCheckTap("s", right = false),
                    accepting = true, isPlaying = false, result = null,
                    onTap = { tapped += it }, onReplay = {}, onInteraction = {}, onDone = {}, onBack = {}
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Sound 2 of 4").assertExists()
        listOf("m", "s", "a", "i").forEach {
            compose.onNodeWithTag("soundcheck_tile_$it").assertIsDisplayed().assertWidthIsAtLeast(64.dp).assertHeightIsAtLeast(64.dp)
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/soundcheck_asking_$name.png")
    }

    private fun done(name: String) {
        var built = 0
        compose.setContent {
            PlayItTheme {
                SoundCheckContent(
                    tiles = emptyList(), index = 4, total = 4, speech = SoundCheckSpeech.DONE, lastTap = null,
                    accepting = false, isPlaying = false,
                    result = SoundCheckResult(1, 0, known = listOf("m", "s", "i"), notYet = listOf("a")),
                    onTap = {}, onReplay = {}, onInteraction = {}, onDone = { built++ }, onBack = {}
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("You know these sounds!").assertIsDisplayed()
        compose.onNodeWithTag("soundcheck_known_m").assertIsDisplayed()
        compose.onNodeWithTag("soundcheck_practise_a").assertIsDisplayed()
        compose.onNodeWithTag("soundcheck_build_words").assertIsDisplayed().assertHeightIsAtLeast(64.dp).performClick()
        assertEquals(1, built)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/soundcheck_done_$name.png")
    }

    @Config(sdk = [34], qualifiers = Devices.COMPACT)
    @Test fun asking_compact() {
        val tapped = mutableListOf<String>()
        asking("compact", tapped)
        compose.onNodeWithTag("soundcheck_tile_m").performClick()
        assertEquals(listOf("m"), tapped)
    }

    @Config(sdk = [34], qualifiers = Devices.A21S)
    @Test fun asking_a21s() = asking("a21s")

    @Config(sdk = [34], qualifiers = Devices.PHONE)
    @Test fun asking_phone() = asking("phone")

    @Config(sdk = [34], qualifiers = Devices.TABLET)
    @Test fun asking_tablet() = asking("tablet")

    @Config(sdk = [34], qualifiers = Devices.COMPACT)
    @Test fun done_compact() = done("compact")

    @Config(sdk = [34], qualifiers = Devices.PHONE)
    @Test fun done_phone() = done("phone")

    @Config(sdk = [34], qualifiers = Devices.TABLET)
    @Test fun done_tablet() = done("tablet")
}
