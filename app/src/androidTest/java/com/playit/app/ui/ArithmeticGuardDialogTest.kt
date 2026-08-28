package com.playit.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.playit.app.domain.manager.ArithmeticGateManager
import com.playit.app.presentation.dashboard.components.ArithmeticGuardDialog
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArithmeticGuardDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogDisplaysArithmeticProblemAndControls() {
        composeTestRule.setContent {
            ArithmeticGuardDialog(
                gateManager = ArithmeticGateManager(),
                onPass = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("🔒 Parent Access Verification").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please solve this arithmetic problem to open the Parent Dashboard:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun enteringIncorrectAnswerDisplaysErrorMessage() {
        composeTestRule.setContent {
            ArithmeticGuardDialog(
                gateManager = ArithmeticGateManager(),
                onPass = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Your Answer").performTextInput("9999")
        composeTestRule.onNodeWithText("Submit").performClick()

        composeTestRule.onNodeWithText("Incorrect answer. Please try again.").assertIsDisplayed()
    }

    @Test
    fun clickingCancelTriggersOnDismiss() {
        var dismissed = false

        composeTestRule.setContent {
            ArithmeticGuardDialog(
                gateManager = ArithmeticGateManager(),
                onPass = {},
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(dismissed)
    }
}
