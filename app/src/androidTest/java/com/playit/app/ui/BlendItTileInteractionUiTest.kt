package com.playit.app.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BlendItTileInteractionUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun letterTilesSupportTapOnlyPlacement() {
        composeTestRule.setContent {
            val bankTiles = remember { mutableStateListOf("S", "A", "M") }
            val placedTiles = remember { mutableStateListOf<String>() }

            // Render placed slot
            if (placedTiles.isNotEmpty()) {
                Text(text = "Word: ${placedTiles.joinToString("")}")
            }

            // Render interactive bank tiles
            bankTiles.forEach { letter ->
                GummyContainer(
                    onClick = {
                        bankTiles.remove(letter)
                        placedTiles.add(letter)
                    },
                    faceColor = LearningBlue,
                    shadowColor = LearningBlueShadow,
                    shape = CircleShape,
                    strokeWidth = 3.dp,
                    strokeColor = DarkBrownOutline,
                    depthHeight = 6.dp,
                    modifier = Modifier.size(68.dp)
                ) {
                    Text(
                        text = letter,
                        fontSize = 30.sp,
                        color = CreamWhite
                    )
                }
            }
        }

        // Assert all 3 letters are in the bank initially
        composeTestRule.onNodeWithText("S").assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("M").assertIsDisplayed()

        // Tap 'S' -> moves to placed tiles via tap-only interaction (WCAG 2.5.7)
        composeTestRule.onNodeWithText("S").performClick()
        composeTestRule.onNodeWithText("Word: S").assertIsDisplayed()

        // Tap 'A' -> word becomes "SA"
        composeTestRule.onNodeWithText("A").performClick()
        composeTestRule.onNodeWithText("Word: SA").assertIsDisplayed()

        // Tap 'M' -> word becomes "SAM"
        composeTestRule.onNodeWithText("M").performClick()
        composeTestRule.onNodeWithText("Word: SAM").assertIsDisplayed()
    }
}
