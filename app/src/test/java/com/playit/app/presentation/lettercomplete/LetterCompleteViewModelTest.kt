package com.playit.app.presentation.lettercomplete

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
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
class LetterCompleteViewModelTest {

    private lateinit var viewModel: LetterCompleteViewModel
    private val phonemeRepository: PhonemeRepository = mockk(relaxed = true)
    private val lessonProgressRepository: LessonProgressRepository = mockk(relaxed = true)
    private val profileRepository: ProfileRepository = mockk(relaxed = true)
    private val streakTracker: StreakTracker = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk()
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private val testPhoneme = Phoneme(
        id = 1,
        letter = "m",
        audioPath = "audio/phonemes/m.mp3",
        imagePath = "images/pictures/picture_manok.png",
        exampleWord = "manok"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "0"
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getSfxPath(any()) } returns "sfx_path.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo_path.mp3"
        coEvery { phonemeRepository.getPhonemeById(1) } returns testPhoneme
        coEvery { lessonProgressRepository.getProgressForPhoneme(1L, 1) } returns null
        every { audioPlayer.isAudioPlaying } returns MutableStateFlow(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun completeLesson_calculates3StarsFor0HeartsLost_andSavesProgress() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "0"

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(testPhoneme, viewModel.phoneme.value)
        assertEquals(3, viewModel.starsEarned.value)
        assertFalse(viewModel.loadError.value)

        coVerify {
            lessonProgressRepository.saveProgress(
                match { it.phonemeId == 1 && it.profileId == 1L && it.starsEarned == 3 && it.heartsLost == 0 && it.isCompleted }
            )
        }
        coVerify { profileRepository.addStars(1L, 3) }
        coVerify { streakTracker.recordActivity(1L) }
        verify { audioPlayer.playSequence(any()) }
    }

    @Test
    fun completeLesson_calculates2StarsFor1HeartLost() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "1"

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.starsEarned.value)
        coVerify {
            lessonProgressRepository.saveProgress(
                match { it.starsEarned == 2 && it.heartsLost == 1 }
            )
        }
        coVerify { profileRepository.addStars(1L, 2) }
    }

    @Test
    fun completeLesson_calculates2StarsFor2HeartsLost() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "2"

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.starsEarned.value)
        coVerify {
            lessonProgressRepository.saveProgress(
                match { it.starsEarned == 2 && it.heartsLost == 2 }
            )
        }
    }

    @Test
    fun completeLesson_replayWithLowerStars_preservesHighestStarsAndAddsZeroDelta() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { savedStateHandle.get<String>("heartsLost") } returns "2" // 2 stars
        coEvery { lessonProgressRepository.getProgressForPhoneme(1L, 1) } returns LessonProgress(
            profileId = 1L,
            phonemeId = 1,
            starsEarned = 3, // previously 3 stars
            heartsLost = 0,
            isCompleted = true
        )

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // UI reflects current attempt
        assertEquals(2, viewModel.starsEarned.value)
        // Saved progress preserves highest (3 stars)
        coVerify {
            lessonProgressRepository.saveProgress(
                match { it.starsEarned == 3 }
            )
        }
        // Profile totalStars is not incremented (delta = 0)
        coVerify(exactly = 0) { profileRepository.addStars(any(), any()) }
    }

    @Test
    fun completeLesson_handlesMissingPhoneme_andSetsLoadError() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "99"
        coEvery { phonemeRepository.getPhonemeById(99) } returns null

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertTrue(viewModel.loadError.value)
        assertNull(viewModel.phoneme.value)
        coVerify(exactly = 0) { lessonProgressRepository.saveProgress(any()) }
    }

    @Test
    fun retry_reloadsPhonemeSuccessfully() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        coEvery { phonemeRepository.getPhonemeById(1) } returns null andThen testPhoneme

        viewModel = LetterCompleteViewModel(
            phonemeRepository, lessonProgressRepository, profileRepository, streakTracker,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()
        assertTrue(viewModel.loadError.value)

        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.loadError.value)
        assertEquals(testPhoneme, viewModel.phoneme.value)
        coVerify(exactly = 1) { lessonProgressRepository.saveProgress(any()) }
    }
}
