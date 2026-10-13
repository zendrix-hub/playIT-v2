package com.playit.app.presentation.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SplashScreenTest {
    @get:Rule val compose = createComposeRule()

    @Before fun setUp() {
        AssetImageConfig.decodeSynchronously = true
    }

    @Test fun lilyWaving_displayedAboveStartButton() {
        compose.setContent {
            PlayItTheme {
                SplashScreen()
            }
        }
        val lily = compose.onNodeWithContentDescription("Lily waving").assertIsDisplayed().getUnclippedBoundsInRoot()
        val start = compose.onNodeWithTag("splash_start").assertIsDisplayed().getUnclippedBoundsInRoot()

        assertTrue("Lily bottom (${lily.bottom}) must be at or above Start top (${start.top})", lily.bottom <= start.top)
    }
}
