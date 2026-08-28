package com.playit.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.map.LetterMapNodeCard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MapNodeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unlockedLetterNodeDisplaysSymbolAndTriggersClick() {
        val unlockedNode = MapNode.LetterNode(
            id = "node_1",
            orderIndex = 1,
            isUnlocked = true,
            symbol = "M",
            starsEarned = 3
        )
        var clicked = false

        composeTestRule.setContent {
            LetterMapNodeCard(
                node = unlockedNode,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("M").assertIsDisplayed()
        composeTestRule.onNodeWithText("M").performClick()
        assertTrue(clicked)
    }

    @Test
    fun lockedLetterNodeDisplaysLockSymbolAndDoesNotTriggerClick() {
        val lockedNode = MapNode.LetterNode(
            id = "node_2",
            orderIndex = 2,
            isUnlocked = false,
            symbol = "S",
            starsEarned = 0
        )
        var clicked = false

        composeTestRule.setContent {
            LetterMapNodeCard(
                node = lockedNode,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("🔒").assertIsDisplayed()
        composeTestRule.onNodeWithText("🔒").performClick()
        assertFalse(clicked)
    }
}
