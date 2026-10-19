package com.playit.app.presentation.splash

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.PlayItTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The Headspace splash (card 31a): Start works, Lily keeps still with reduced motion, and a tap makes her wink. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp-xhdpi")
class SplashScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun start_callsOnStart() {
        var started = 0
        compose.setContent {
            PlayItTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) { SplashScreen(onStartClick = { started++ }) }
            }
        }
        compose.onNodeWithTag("splash_lily").assertExists()
        compose.onNodeWithTag("splash_start").performClick()
        compose.waitForIdle()
        assertEquals(1, started)
    }

    /** Breathing and blinking run only when the system allows motion, and the breathing stays gentle. */
    @Test fun motionPolicy_followsReducedMotion() {
        val still = splashMotion(reducedMotion = true)
        assertFalse(still.breathes); assertFalse(still.blinks)
        val alive = splashMotion(reducedMotion = false)
        assertTrue(alive.breathes); assertTrue(alive.blinks)
        assertTrue("breathing stays gentle", alive.breatheY <= 1.05f && alive.breatheX <= 1.05f)
    }

    /** With reduced motion the wink is instant: only her right eye shuts, then opens after WINK_HOLD_MS. */
    @Test fun wink_closesOnlyTheRightEye_thenReopens() = runTest {
        val eyes = LilyEyes(instant = true)
        launch { eyes.winkOnce() }
        runCurrent()
        assertEquals(1f, eyes.closed(side = 1f))
        assertEquals(0f, eyes.closed(side = -1f))
        advanceTimeBy(WINK_HOLD_MS + 10)
        runCurrent()
        assertEquals(0f, eyes.closed(side = 1f))
    }

    /** A blink shuts both eyes (the wink only ever shuts the right one). */
    @Test fun blink_closesBothEyes() = runTest {
        val eyes = LilyEyes(instant = true)
        eyes.blink.snapTo(1f)
        assertEquals(1f, eyes.closed(side = -1f))
        assertEquals(1f, eyes.closed(side = 1f))
    }
}
