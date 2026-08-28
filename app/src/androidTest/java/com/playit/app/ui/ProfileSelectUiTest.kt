package com.playit.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.playit.app.domain.model.Profile
import com.playit.app.presentation.profile.components.AddProfileButton
import com.playit.app.presentation.profile.components.ProfileCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileSelectUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addProfileButtonDisplaysEnabledStateWhenUnderSixProfiles() {
        var clicked = false

        composeTestRule.setContent {
            AddProfileButton(
                enabled = true,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Add New Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add New Profile").performClick()
        assertTrue(clicked)
    }

    @Test
    fun addProfileButtonDisplaysDisabledLimitMessageWhenSixProfilesExist() {
        composeTestRule.setContent {
            AddProfileButton(
                enabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Profile Limit Reached (Max 6)").assertIsDisplayed()
    }

    @Test
    fun profileCardDisplaysProfileNameAndTriggersSelection() {
        val sampleProfile = Profile(
            id = 42L,
            name = "Maya",
            avatarResId = 0
        )
        var selectedProfileId: Long? = null

        composeTestRule.setContent {
            ProfileCard(
                profile = sampleProfile,
                onSelect = { selectedProfileId = it }
            )
        }

        composeTestRule.onNodeWithText("Maya").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maya").performClick()
        assertEquals(42L, selectedProfileId)
    }
}
