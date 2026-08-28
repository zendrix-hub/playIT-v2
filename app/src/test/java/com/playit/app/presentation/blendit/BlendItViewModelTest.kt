package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.BlendItWordSelector
import com.playit.app.domain.model.BlendItAttempt
import com.playit.app.domain.model.BlendItWord
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.BlendItWordRepository
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
class BlendItViewModelTest {

    private lateinit var viewModel: BlendItViewModel
    private val blendItWordRepository: BlendItWordRepository = mockk()
    private val blendItAttemptRepository: BlendItAttemptRepository = mockk(relaxed = true)
    private val blendItWordSelector: BlendItWordSelector = mockk()
    private val sessionManager: SessionManager = mockk()
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private val fakeWords = listOf(
        BlendItWord(wordId = 1, groupId = 1, word = "SAM", wordPattern = "CVC", audioPath = "audio/words/word_sam.mp3", imagePath = "images/pictures/blendword_sam.png"),
        BlendItWord(wordId = 2, groupId = 1, word = "SIS", wordPattern = "CVC", audioPath = "audio/words/word_sis.mp3", imagePath = "images/pictures/blendword_sis.png")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { savedStateHandle.get<String>("groupId") } returns "1"
        every { blendItWordRepository.getWordsForGroup(1) } returns flowOf(fakeWords)
        every { blendItWordSelector.selectWordsForSession(1, any()) } returns fakeWords
        every { sessionManager.shouldPlayScreenIntro(any()) } returns false
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getWordPath(any()) } returns "word_audio.mp3"
        every { audioResolver.getPhonemePath(any()) } returns "phoneme_audio.mp3"
        every { audioResolver.getSfxPath(any()) } returns "sfx_audio.mp3"
        every { audioResolver.getRotatingCorrectVo() } returns "correct_vo.mp3"
        every { audioResolver.getRotatingEncourageVo() } returns "encourage_vo.mp3"
        every { audioResolver.getRotatingHintVo() } returns "hint_vo.mp3"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadSessionWords_initializesFirstWordAndTileBank() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.words.value.size)
        assertEquals(0, viewModel.currentWordIndex.value)
        assertEquals("SAM", viewModel.currentWord.value?.word)
        assertEquals(3, viewModel.tileBank.value.size)
        assertTrue(viewModel.placedTiles.value.isEmpty())
        assertEquals(BlendItUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun placeAndRemoveTiles_updatesBankAndPlacedLists() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        val firstLetter = viewModel.tileBank.value.first()
        viewModel.placeTile(firstLetter)
        advanceUntilIdle()

        assertEquals(listOf(firstLetter), viewModel.placedTiles.value)
        assertEquals(2, viewModel.tileBank.value.size)

        viewModel.removeTile(0)
        advanceUntilIdle()

        assertTrue(viewModel.placedTiles.value.isEmpty())
        assertEquals(3, viewModel.tileBank.value.size)
    }

    @Test
    fun submitWord_correctWord_transitionsToWordCorrectAndAdvances() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // Place S, A, M in order
        viewModel.placeTile('S')
        viewModel.placeTile('A')
        viewModel.placeTile('M')
        advanceUntilIdle()

        viewModel.submitWord()
        runCurrent()

        assertEquals(BlendItUiState.WordCorrect, viewModel.uiState.value)
        coVerify { blendItAttemptRepository.saveAttempt(match { it.isCorrect && it.wordId == 1 }) }

        // Advance delay of 1200ms to advance to next word
        advanceTimeBy(1300)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentWordIndex.value)
        assertEquals("SIS", viewModel.currentWord.value?.word)
    }

    @Test
    fun submitWord_incorrectWord_deductsHeart() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        // Place incorrect order: M, A, S
        viewModel.placeTile('M')
        viewModel.placeTile('A')
        viewModel.placeTile('S')
        advanceUntilIdle()

        viewModel.submitWord()
        advanceUntilIdle()

        assertEquals(initialHearts - 1, viewModel.hearts.value)
        assertEquals(1, viewModel.wrongAttemptsForCurrentWord.value)
        assertTrue(viewModel.uiState.value is BlendItUiState.WordIncorrect)
        coVerify { blendItAttemptRepository.saveAttempt(match { !it.isCorrect && it.wordId == 1 }) }
    }

    @Test
    fun applyHint_afterTwoMistakes_locksCorrectCharacter() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // Submit wrong word twice
        repeat(2) {
            viewModel.submitWord()
            advanceUntilIdle()
        }
        assertEquals(2, viewModel.wrongAttemptsForCurrentWord.value)

        // Apply hint for 'SAM' -> first letter 'S' locked
        viewModel.applyHint()
        advanceUntilIdle()

        assertEquals(1, viewModel.lockedHintCount.value)
        assertEquals('S', viewModel.placedTiles.value.first())
    }

    @Test
    fun restartSession_resetsHeartsAndCurrentWordIndex() = runTest {
        viewModel = BlendItViewModel(
            blendItWordRepository, blendItAttemptRepository, blendItWordSelector,
            sessionManager, audioPlayer, audioResolver, savedStateHandle
        )
        advanceUntilIdle()

        // Deduct hearts until 0
        repeat(5) {
            viewModel.submitWord()
            advanceUntilIdle()
        }

        assertEquals(0, viewModel.hearts.value)
        assertEquals(BlendItUiState.HeartDepleted, viewModel.uiState.value)

        viewModel.restartSession()
        advanceUntilIdle()

        assertEquals(5, viewModel.hearts.value)
        assertEquals(0, viewModel.currentWordIndex.value)
        assertEquals(BlendItUiState.Idle, viewModel.uiState.value)
    }
}
