package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.presentation.hearit.HearItScreen
import com.playit.app.presentation.hearit.HearItViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
class HearItScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun hearIt_letterM() {
        val fakePhoneme = Phoneme(1, "m", "p", "images/pictures/picture_mouse.png", "mouse")
        val repo = object : PhonemeRepository {
            override fun getAllPhonemes(): Flow<List<Phoneme>> = flowOf(listOf(fakePhoneme))
            override suspend fun getPhonemeById(id: Int): Phoneme? {
                delay(1)
                return fakePhoneme
            }
            override suspend fun getPhonemeByLetter(letter: String): Phoneme? = fakePhoneme
        }
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)
        val handle: SavedStateHandle = mockk()
        every { handle.get<String>("phonemeId") } returns "1"
        val vm = HearItViewModel(repo, player, resolver, handle)
        compose.setContent { PlayItTheme { HearItScreen(vm, onNext = {}, onBack = {}) } }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/hearit_letter_m.png")
    }
}
