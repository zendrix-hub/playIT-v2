package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.BlendItWordSelector
import com.playit.app.domain.model.BlendItWord
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.blendit.BlendItScreen
import com.playit.app.presentation.blendit.BlendItViewModel
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
class BlendItScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun blendIt_group1_firstWord() {
        val wordRepo: BlendItWordRepository = mockk()
        val fakeWords = listOf(
            BlendItWord(wordId = 1, groupId = 1, word = "SAM", wordPattern = "CVC", audioPath = "audio/words/word_sam.mp3", imagePath = "images/pictures/blendword_sam.png"),
            BlendItWord(wordId = 2, groupId = 1, word = "SIS", wordPattern = "CVC", audioPath = "audio/words/word_sis.mp3", imagePath = "images/pictures/blendword_sis.png"),
            BlendItWord(wordId = 3, groupId = 1, word = "AIM", wordPattern = "VVC", audioPath = "audio/words/word_aim.mp3", imagePath = "images/pictures/blendword_aim.png")
        )
        every { wordRepo.getWordsForGroup(1) } returns flowOf(fakeWords)
        val attemptRepo: BlendItAttemptRepository = mockk(relaxed = true)
        val wordSelector = BlendItWordSelector()
        val sessionManager: SessionManager = mockk(relaxed = true)
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)
        val handle: SavedStateHandle = mockk()
        every { handle.get<String>("groupId") } returns "1"

        val vm = BlendItViewModel(
            blendItWordRepository = wordRepo,
            blendItAttemptRepository = attemptRepo,
            blendItWordSelector = wordSelector,
            sessionManager = sessionManager,
            audioPlayer = player,
            audioResolver = resolver,
            savedStateHandle = handle
        )

        compose.setContent { PlayItTheme { BlendItScreen(vm, onSessionComplete = {}, onBack = {}) } }
        compose.waitForIdle()
        compose.waitUntil(5_000) { com.playit.app.presentation.components.AssetDecodeTracker.isIdle() }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/blendit_group1.png")
    }
}
