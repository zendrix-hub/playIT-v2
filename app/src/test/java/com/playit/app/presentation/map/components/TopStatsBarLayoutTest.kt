package com.playit.app.presentation.map.components

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.theme.PlayItTheme
import com.playit.app.screenshot.Devices
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Review Focus 3: a 16-character name must not push the stat pills off the A21s screen.
 * Card 31e: the greeting lives in this card now, is never cut off, and the card is Lily's 64 dp tap
 * target; the map dialogs' close buttons are 64 dp too.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = Devices.A21S)
class TopStatsBarLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    /** As wide as on the map: 360 dp less the 64 dp back button, the 8 dp gap and the 14 dp side paddings. */
    private fun showBar(onLilyTap: (() -> Unit)? = null) {
        compose.setContent {
            PlayItTheme {
                TopStatsBar(
                    totalStars = 120,
                    currentStreak = 14,
                    unlockedBadgesCount = 3,
                    lettersCompleted = 5,
                    profileName = "Maximilianoooooo",
                    onLilyTap = onLilyTap,
                    modifier = Modifier.width(260.dp)
                )
            }
        }
        compose.waitForIdle()
    }

    @Test fun longName_pillsStayVisible() {
        showBar()
        val pills = compose.onNodeWithTag("stats_pills").getBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertTrue("pills right ${pills.right} > window ${root.right}", pills.right <= root.right)
    }

    /** "Hi, Maximilianoooooo!" shows whole (it may wrap to a second line, never an ellipsis). */
    @Test fun longName_greetingIsNotCut() {
        showBar()
        val layout = textLayout("stats_greeting")
        assertEquals("Hi, Maximilianoooooo!", layout.layoutInput.text.text)
        assertFalse("the greeting is cut off", layout.anyLineEllipsized())
    }

    /** The letters learned fit beside the pills, even with three-digit stars (the A21s is 360 dp wide). */
    @Test fun lettersCount_isNotCut() {
        showBar()
        val layout = textLayout("stats_letters")
        assertEquals("5/26", layout.layoutInput.text.text)
        assertFalse("the letters count is cut off", layout.anyLineEllipsized())
    }

    /**
     * Whether the text was cut with an ellipsis. (hasVisualOverflow is no use here: a wrap-content text that
     * may ellipsize is laid out at the full width it may use, so it reports overflow even when it fits.)
     */
    private fun TextLayoutResult.anyLineEllipsized() = (0 until lineCount).any { isLineEllipsized(it) }

    private fun textLayout(tag: String): TextLayoutResult {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        node.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(layouts)
        return layouts.single()
    }

    @Test fun card_isLilysTapTarget() {
        var taps = 0
        showBar(onLilyTap = { taps++ })
        val card = compose.onNodeWithTag("stats_card", useUnmergedTree = true).getBoundsInRoot()
        assertTrue("card ${card.bottom - card.top} shorter than 64 dp", card.bottom - card.top >= 63.5.dp)
        compose.onNodeWithTag("stats_card", useUnmergedTree = true).performClick()
        compose.waitForIdle()
        assertEquals(1, taps)
    }

    @Test fun nodeDialog_closeIs64dp() {
        compose.setContent {
            PlayItTheme {
                NodeActionPopupDialog(
                    node = MapNode.LetterNode(id = "m", orderIndex = 0, isUnlocked = true, symbol = "M", starsEarned = 2),
                    onStartChallenge = {},
                    onDismiss = {}
                )
            }
        }
        compose.waitForIdle()
        assertAtLeast64dp("node_popup_close")
    }

    @Test fun guidebookDialog_closeIs64dp() {
        compose.setContent { PlayItTheme { UnitGuidebookDialog(unitNumber = 1, onDismiss = {}) } }
        compose.waitForIdle()
        assertAtLeast64dp("guidebook_close")
    }

    private fun assertAtLeast64dp(tag: String) {
        val b = compose.onNodeWithTag(tag, useUnmergedTree = true).getBoundsInRoot()
        // 63.5: pixels can round to 63.99999 dp
        assertTrue("$tag is ${b.right - b.left} x ${b.bottom - b.top}", b.right - b.left >= 63.5.dp && b.bottom - b.top >= 63.5.dp)
    }
}
