package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.ProfileRepository
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
    private val profileRepository: ProfileRepository = mockk(relaxed = true)
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
        every { savedStateHandle.get<String>("heartsLost") } returns "0"
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getSfxPath(any()) } returns "sfx_path.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo_path.mp3"
        coEvery { blendItProgressRepository.getProgressForGroup(1L, 1) } returns null
        every { audioPlayer.isAudioPlaying } returns MutableStateFlow(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun completeSession_0HeartsLost_calculates3StarsAndSavesProgress() = runTest {
        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, profileRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(3, viewModel.starsEarned.value)
        assertEquals(1, viewModel.groupId)

        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.profileId == 1L && it.starsEarned == 3 && it.heartsLost == 0 && it.isCompleted }
            )
        }
        coVerify { profileRepository.addStars(1L, 3) }
        coVerify { streakTracker.recordActivity(1L) }
        verify { audioPlayer.playSequence(any()) }
    }

    @Test
    fun completeSession_1HeartLost_calculates2Stars() = runTest {
        every { savedStateHandle.get<String>("heartsLost") } returns "1"

        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, profileRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.starsEarned.value)
        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.starsEarned == 2 && it.heartsLost == 1 }
            )
        }
        coVerify { profileRepository.addStars(1L, 2) }
    }

    @Test
    fun completeSession_3HeartsLost_calculates1Star() = runTest {
        every { savedStateHandle.get<String>("heartsLost") } returns "3"

        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, profileRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(1, viewModel.starsEarned.value)
        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.starsEarned == 1 && it.heartsLost == 3 }
            )
        }
        coVerify { profileRepository.addStars(1L, 1) }
    }

    @Test
    fun completeSession_replayWithLowerStars_preservesPreviousHighestStars() = runTest {
        every { savedStateHandle.get<String>("heartsLost") } returns "3" // 1 star attempt
        coEvery { blendItProgressRepository.getProgressForGroup(1L, 1) } returns BlendItProgress(
            profileId = 1L,
            groupId = 1,
            starsEarned = 3, // previously achieved 3 stars
            heartsLost = 0,
            isCompleted = true
        )

        viewModel = BlendItCompleteViewModel(
            blendItProgressRepository, profileRepository, streakTracker, sessionManager,
            audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(1, viewModel.starsEarned.value)
        coVerify {
            blendItProgressRepository.saveProgress(
                match { it.groupId == 1 && it.starsEarned == 3 }
            )
        }
        // No additional stars added
        coVerify(exactly = 0) { profileRepository.addStars(any(), any()) }
    }
}
