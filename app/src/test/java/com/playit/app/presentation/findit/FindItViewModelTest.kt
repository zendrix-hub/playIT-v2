package com.playit.app.presentation.findit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.FindItPictureItem
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

    private val mockPictureGrid = listOf(
        FindItPictureItem("m_0", "m", "Mouse", "images/pictures/picture_mouse.png", true),
        FindItPictureItem("m_1", "m", "Mat", "images/pictures/blendword_mat.png", true),
        FindItPictureItem("m_2", "m", "Map", "images/pictures/picture_map.png", true),
        FindItPictureItem("s_0", "s", "Sun", "images/pictures/picture_sun.png", false),
        FindItPictureItem("a_0", "a", "Apple", "images/pictures/picture_apple.png", false)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { phonemeRepository.getAllPhonemes() } returns flowOf(allPhonemes)
        every { gridGenerator.generate5ItemGrid(any(), any()) } returns mockPictureGrid
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
    fun loadGrid_validId_loadsTargetAnd5ItemGrid() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(fakePhoneme, viewModel.targetPhoneme.value)
        assertEquals(5, viewModel.pictureGrid.value.size)
        assertEquals(0, viewModel.foundCount.value)
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
    fun selectPictureItem_correctItem_incrementsFoundCount() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.selectPictureItem(mockPictureGrid[0]) // m_0 (Mouse)
        advanceUntilIdle()

        assertEquals(1, viewModel.foundCount.value)
        assertTrue(viewModel.state.value is FindItState.FoundOne)
        coVerify { findItAttemptRepository.saveAttempt(1L, 1, 1, true) }
    }

    @Test
    fun selectPictureItem_allThreeCorrect_completesLesson() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.selectPictureItem(mockPictureGrid[0]) // 1/3
        viewModel.selectPictureItem(mockPictureGrid[1]) // 2/3
        viewModel.selectPictureItem(mockPictureGrid[2]) // 3/3
        advanceUntilIdle()

        assertEquals(3, viewModel.foundCount.value)
        assertTrue(viewModel.state.value is FindItState.Completed)
    }

    @Test
    fun selectPictureItem_incorrectItem_deductsHeart() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.selectPictureItem(mockPictureGrid[3]) // s_0 (Sun - distractor)
        advanceUntilIdle()

        assertEquals(initialHearts - 1, viewModel.hearts.value)
        assertTrue(viewModel.state.value is FindItState.Incorrect)
        coVerify { findItAttemptRepository.saveAttempt(1L, 1, 1, false) }
    }

    @Test
    fun selectPictureItem_allHeartsLost_setsGameOver() = runTest {
        viewModel = FindItViewModel(
            phonemeRepository, findItAttemptRepository, gridGenerator,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // Deduct all 5 hearts
        repeat(5) {
            viewModel.selectPictureItem(mockPictureGrid[3])
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

        viewModel.selectPictureItem(mockPictureGrid[3])
        advanceUntilIdle()
        
        assertTrue(viewModel.hearts.value < 5)
        
        viewModel.restartSession()
        advanceUntilIdle()
        
        assertEquals(3, viewModel.hearts.value) // DEPLETED_RESTART_HEARTS = 3
        assertEquals(0, viewModel.foundCount.value)
        assertFalse(viewModel.state.value is FindItState.GameOver)
    }
}
