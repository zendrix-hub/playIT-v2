package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.navigation.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlendItCompleteViewModelTest {

    private lateinit var viewModel: BlendItCompleteViewModel
    private val blendItProgressRepository: BlendItProgressRepository = mockk(relaxed = true)
    private val streakTracker: StreakTracker = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk()
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { savedStateHandle.get<String>("groupId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns null
        every { savedStateHandle.get<String>("wordsCorrect") } returns null
        every { savedStateHandle.get<String>("totalWords") } returns null
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getSfxPath(any()) } returns "sfx_path.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo_path.mp3"
        every { audioResolver.getUiPath(any()) } answers { "ui/${firstArg<String>()}.wav" }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun completeSession_calculatesStarsAndSavesProgress() = runTest {
        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(3, viewModel.starsEarned.value)
        assertEquals(1, viewModel.groupId)

        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.profileId == 1L && it.starsEarned == 3 && it.isCompleted }
            )
        }
        coVerify { streakTracker.recordActivity(1L) }
        verify { audioPlayer.playSequence(any(), any()) }
    }

    @Test
    fun completion_appendsNextCue() = runTest {
        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        verify {
            audioPlayer.playSequence(
                match { it.lastOrNull() == "ui/ui_complete_next.wav" },
                any()
            )
        }
        assertTrue(viewModel.nextHighlighted.value)
    }

    @Test
    fun idle_waitsForCompletionSequence() = runTest {
        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.onScreenVisible()
        advanceTimeBy(30_000)

        verify(exactly = 0) {
            audioPlayer.playAssetAudio("ui/ui_complete_next.wav", any())
        }
    }

    @Test
    fun starsUseNavResults() = runTest {
        every { savedStateHandle.get<String>("groupId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "1"
        every { savedStateHandle.get<String>("wordsCorrect") } returns "4"
        every { savedStateHandle.get<String>("totalWords") } returns "5"

        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.starsEarned.value)
        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.profileId == 1L && it.starsEarned == 2 && it.heartsLost == 1 }
            )
        }
    }
}
