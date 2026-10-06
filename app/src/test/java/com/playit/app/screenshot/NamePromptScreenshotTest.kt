package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.profile.NamePromptScreen
import com.playit.app.presentation.profile.ProfileViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class NamePromptScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun namePrompt_avatarOnly() {
        val profileRepo: ProfileRepository = mockk(relaxed = true)
        every { profileRepo.getAllProfiles() } returns MutableStateFlow(emptyList())
        val sessionManager: SessionManager = mockk(relaxed = true)
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)

        val vm = ProfileViewModel(profileRepo, sessionManager, player, resolver)

        compose.setContent { PlayItTheme { NamePromptScreen(vm, onProfileCreated = {}, onBack = {}) } }
        compose.waitForIdle()
        compose.waitUntil(5_000) { com.playit.app.presentation.components.AssetDecodeTracker.isIdle() }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/nameprompt.png")
    }
}
