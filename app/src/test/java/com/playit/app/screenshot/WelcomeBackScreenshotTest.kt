package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.domain.manager.ReturnGreetingPolicy
import com.playit.app.presentation.map.WelcomeBack
import com.playit.app.presentation.map.components.WelcomeBackCard
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Lily's welcome-back card on the smallest phone: the words, the next letter, and two 64 dp buttons. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = Devices.COMPACT)
class WelcomeBackScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { com.playit.app.presentation.components.AssetImageConfig.decodeSynchronously = true }

    @Test fun keepGoing_withTheNextLetter() {
        var went = 0
        val card = WelcomeBack(ReturnGreetingPolicy.KeepGoing("2", "S"), "Frog", "s", "Sun", "images/pictures/picture_sun.png")
        compose.setContent { PlayItTheme { WelcomeBackCard(card, onGo = { went++ }, onNotNow = {}, onLilyTap = {}) } }
        compose.waitForIdle()
        compose.onNodeWithText("Welcome back, Frog!").assertExists()
        compose.onNodeWithText("Do you want to keep going with this sound?").assertExists()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/welcome_back_keep_going.png")
        compose.onNodeWithTag("welcome_back_go").performClick()
        assertEquals(1, went)
    }

    @Test fun buildWords() {
        val card = WelcomeBack(ReturnGreetingPolicy.BuildWords("blend_1", 1), "Bear")
        compose.setContent { PlayItTheme { WelcomeBackCard(card, onGo = {}, onNotNow = {}, onLilyTap = {}) } }
        compose.waitForIdle()
        compose.onNodeWithText("Do you want to build some words?").assertExists()
        compose.onNodeWithTag("welcome_back_not_now").assertExists()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/welcome_back_build_words.png")
    }
}
