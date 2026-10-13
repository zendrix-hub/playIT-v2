package com.playit.app.presentation.hearit

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Hear It shows one message at a time (card 31d). While the sound plays (the relaxed AudioPlayer mock
 * never finishes the sequence), "Sound: /m/" appears once, on the card, and the play button keeps its
 * colour (it stays enabled and ignores taps) instead of turning grey.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp-xhdpi")
class HearItScreenFocusTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    private val mouse = Phoneme(1, "m", "p", "images/pictures/picture_mouse.png", "mouse")
    private val phonemes = object : PhonemeRepository {
        override fun getAllPhonemes(): Flow<List<Phoneme>> = flowOf(listOf(mouse))
        override suspend fun getPhonemeById(id: Int): Phoneme? = mouse
        override suspend fun getPhonemeByLetter(letter: String): Phoneme? = mouse
    }

    private fun showPlaying() {
        val handle: SavedStateHandle = mockk { every { get<String>("phonemeId") } returns "1" }
        val vm = HearItViewModel(phonemes, mockk<AudioPlayer>(relaxed = true), mockk<AudioResolver>(relaxed = true), handle)
        compose.setContent { PlayItTheme { HearItScreen(vm, onNext = {}, onBack = {}) } }
        compose.waitForIdle()
    }

    @Test fun whilePlaying_soundTextShowsOnce_andLilySaysListen() {
        showPlaying()
        compose.onNodeWithContentDescription("Playing").assertExists()
        compose.onAllNodesWithText("Sound: /m/").assertCountEquals(1)
        compose.onAllNodesWithText("Listen!").assertCountEquals(1)
    }

    @Test fun whilePlaying_playButtonKeepsItsColour() {
        showPlaying()
        compose.onNodeWithTag("hearit_play").assertIsEnabled()
    }
}
