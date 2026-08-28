package com.playit.app.presentation.findit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.GridGenerator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.navigation.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FindItViewModelTest {

    private lateinit var viewModel: FindItViewModel
    private val phonemeRepository: PhonemeRepository = mockk()
    private val findItAttemptRepository: FindItAttemptRepository = mockk(relaxed = true)
    private val gridGenerator: GridGenerator = mockk()
    private val sessionManager: SessionManager = mockk()
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    private val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
    private val fakeWrongPhoneme = Phoneme(id = 2, letter = "s", audioPath = "path2", imagePath = "path2", exampleWord = "sun")
    private val allPhonemes = listOf(fakePhoneme, fakeWrongPhoneme)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { phonemeRepository.getAllPhonemes() } returns flowOf(allPhonemes)
        every { gridGenerator.generateGrid(any(), any()) } returns allPhonemes
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { sessionManager.shouldPlayScreenIntro(any()) } returns false
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getPhonemePath(any()) } returns "test_path"
        every { audioResolver.getSfxPath(any()) } returns "sfx_path"
        every { audioResolver.getRotatingCorrectVo() } returns "correct_vo"
        every { audioResolver.getRotatingEncourageVo() } returns "encourage_vo"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadGrid_validId_loadsTarget() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(fakePhoneme, viewModel.targetPhoneme.value)
        assertEquals(allPhonemes, viewModel.gridItems.value)
        assertFalse(viewModel.loadError.value)
    }

    @Test
    fun loadGrid_invalidId_setsLoadError() = runTest {
        every { savedStateHandle.get<String>("phonemeId") } returns "999"
        
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertNull(viewModel.targetPhoneme.value)
        assertTrue(viewModel.loadError.value)
    }

    @Test
    fun selectItem_correctItem_setsCorrectState() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.selectItem(fakePhoneme)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is FindItState.Correct)
        assertEquals(fakePhoneme, (state as FindItState.Correct).phoneme)
        coVerify { findItAttemptRepository.saveAttempt(1L, 1, 1, true) }
    }

    @Test
    fun selectItem_incorrectItem_deductsHeart() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.selectItem(fakeWrongPhoneme)
        advanceUntilIdle()

        assertEquals(initialHearts - 1, viewModel.hearts.value)
        coVerify { findItAttemptRepository.saveAttempt(1L, 1, 2, false) }
    }

    @Test
    fun selectItem_allHeartsLost_setsGameOver() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // Default hearts is 5, loop to deduct all
        repeat(5) {
            viewModel.selectItem(fakeWrongPhoneme)
        }
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is FindItState.GameOver)
        assertEquals(0, viewModel.hearts.value)
    }

    @Test
    fun restartSession_resetsHeartsAndState() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.selectItem(fakeWrongPhoneme)
        advanceUntilIdle()
        
        assertTrue(viewModel.hearts.value < 5)
        
        viewModel.restartSession()
        advanceUntilIdle()
        
        assertEquals(3, viewModel.hearts.value) // DEPLETED_RESTART_HEARTS = 3
        assertFalse(viewModel.state.value is FindItState.GameOver)
    }
}
