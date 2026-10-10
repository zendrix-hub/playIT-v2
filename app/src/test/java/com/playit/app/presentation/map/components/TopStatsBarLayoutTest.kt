package com.playit.app.presentation.map.components

import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.theme.PlayItTheme
import com.playit.app.screenshot.Devices
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Review Focus 3: a 16-character name must not push the stat pills off the A21s screen. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = Devices.A21S)
class TopStatsBarLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Test fun longName_pillsStayVisible() {
        compose.setContent {
            PlayItTheme {
                TopStatsBar(
                    totalStars = 120,
                    currentStreak = 14,
                    unlockedBadgesCount = 3,
                    lettersCompleted = 5,
                    profileName = "Maximilianoooooo"
                )
            }
        }
        compose.waitForIdle()
        val pills = compose.onNodeWithTag("stats_pills").getBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertTrue("pills right ${pills.right} > window ${root.right}", pills.right <= root.right)
    }
}
