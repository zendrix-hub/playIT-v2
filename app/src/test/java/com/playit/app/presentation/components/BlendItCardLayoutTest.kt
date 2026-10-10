package com.playit.app.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Card 16 (in card 20): the Blend It word card's hint line stays inside the card.
 * Card 28: the hint is English only; the Filipino line is gone.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class BlendItCardLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Test
    fun hintLine_staysInsideCard() {
        compose.setContent {
            PlayItTheme {
                BlendItCard(word = "SAM", isCorrect = false, onReplayAudio = {}, modifier = Modifier.testTag("blendCard"))
            }
        }
        compose.waitForIdle()
        // Unmerged: the clickable card merges its text into one node with the card's own bounds.
        val line = compose.onNodeWithText("Tap to hear word", useUnmergedTree = true).getBoundsInRoot()
        val card = compose.onNodeWithTag("blendCard").getBoundsInRoot()
        assertTrue("hint line bottom ${line.bottom} must be at most ${card.bottom - 6.dp}", line.bottom <= card.bottom - 6.dp)
        compose.onNodeWithText("Pindutin para marinig", useUnmergedTree = true).assertDoesNotExist()
    }
}
