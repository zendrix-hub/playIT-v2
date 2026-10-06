package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.GridGenerator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.findit.FindItScreen
import com.playit.app.presentation.findit.FindItViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class FindItScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun findIt_letterM_fiveHearts() {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "images/pictures/picture_mouse.png", exampleWord = "mouse")
        val fakeDistractor = Phoneme(id = 2, letter = "s", audioPath = "path2", imagePath = "images/pictures/picture_sun.png", exampleWord = "sun")
        val phonemeRepo: PhonemeRepository = mockk()
        every { phonemeRepo.getAllPhonemes() } returns flowOf(listOf(fakePhoneme, fakeDistractor))
        val attemptRepo: FindItAttemptRepository = mockk(relaxed = true)
        val gridGenerator = GridGenerator()
        val sessionManager: SessionManager = mockk(relaxed = true)
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)
        val handle: SavedStateHandle = mockk()
        every { handle.get<String>("phonemeId") } returns "1"

        val vm = FindItViewModel(
            phonemeRepository = phonemeRepo,
            findItAttemptRepository = attemptRepo,
            gridGenerator = gridGenerator,
            sessionManager = sessionManager,
            audioPlayer = player,
            audioResolver = resolver,
            savedStateHandle = handle
        )

        compose.setContent { PlayItTheme { FindItScreen(vm, onNext = { _, _ -> }, onBack = {}) } }
        compose.waitForIdle()
        compose.waitUntil(5_000) { com.playit.app.presentation.components.AssetDecodeTracker.isIdle() }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/findit_letter_m.png")
    }
}
