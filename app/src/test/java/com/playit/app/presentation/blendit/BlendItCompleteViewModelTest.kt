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
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getSfxPath(any()) } returns "sfx_path.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo_path.mp3"
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
        verify { audioPlayer.playSequence(any()) }
    }
}
