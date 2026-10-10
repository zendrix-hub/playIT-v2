package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.lettercomplete.LetterCompleteScreen
import com.playit.app.presentation.lettercomplete.LetterCompleteViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.coEvery
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
class LetterCompleteScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @org.junit.Before fun syncImages() { com.playit.app.presentation.components.AssetImageConfig.decodeSynchronously = true }

    @Test
    fun letterComplete_threeHeartsLost_oneStar() {
        val phonemeRepo: PhonemeRepository = mockk(relaxed = true)
        val progressRepo: LessonProgressRepository = mockk(relaxed = true)
        val streakTracker: StreakTracker = mockk(relaxed = true)
        val sessionManager: SessionManager = mockk(relaxed = true)
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)
        val handle: SavedStateHandle = mockk()

        every { handle.get<String>("phonemeId") } returns "1"
        every { handle.get<String>("heartsLost") } returns "3"
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        coEvery { phonemeRepo.getPhonemeById(any()) } returns Phoneme(1, "m", "p", "images/pictures/picture_mouse.png", "mouse")

        val vm = LetterCompleteViewModel(
            phonemeRepository = phonemeRepo,
            lessonProgressRepository = progressRepo,
            streakTracker = streakTracker,
            sessionManager = sessionManager,
            audioPlayer = player,
            audioResolver = resolver,
            savedStateHandle = handle
        )

        compose.setContent { PlayItTheme { LetterCompleteScreen(vm, onReturnToMap = {}) } }
        compose.waitForIdle()
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/lettercomplete_1star.png")
    }
}
