package com.playit.app.presentation.sayit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.speech.VoskRecognizer
import com.playit.app.domain.manager.SpeechValidator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
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
class SayItViewModelTest {

    private lateinit var viewModel: SayItViewModel
    private val phonemeRepository: PhonemeRepository = mockk()
    private val sayItAttemptRepository: SayItAttemptRepository = mockk(relaxed = true)
    private val speechValidator: SpeechValidator = mockk()
    private val voskRecognizer: VoskRecognizer = mockk(relaxed = true)
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val sessionManager: SessionManager = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { phonemeRepository.getPhonemeById(any()) } returns null
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { sessionManager.shouldPlayScreenIntro(any()) } returns false
        every { sessionManager.activeProfileId } returns MutableStateFlow(1L)
        every { audioResolver.getPhonemePath(any()) } returns "test_path"
        every { audioResolver.getSfxPath(any()) } returns "sfx_path"
        every { audioResolver.getRotatingCorrectVo() } returns "correct_vo"
        every { audioResolver.getRotatingEncourageVo() } returns "encourage_vo"
        every { speechValidator.validate(any(), any()) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadPhoneme_validId_loadsPhoneme() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        assertEquals(fakePhoneme, viewModel.phoneme.value)
        assertFalse(viewModel.loadError.value)
        coVerify { voskRecognizer.initModel() }
    }

    @Test
    fun loadPhoneme_invalidId_setsLoadError() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns null

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        assertNull(viewModel.phoneme.value)
        assertTrue(viewModel.loadError.value)
    }

    @Test
    fun evaluateSpeech_correctTranscript_setsCorrectState() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { speechValidator.validate("m", "m") } returns true

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.evaluateSpeech("m")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Correct)
        assertEquals("m", (state as SayItState.Correct).transcript)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, true) }
    }

    @Test
    fun evaluateSpeech_incorrectTranscript_deductsHeart() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { speechValidator.validate("s", "m") } returns false

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.evaluateSpeech("s")
        advanceUntilIdle()

        assertEquals(initialHearts - 1, viewModel.hearts.value)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, false) }
    }

    @Test
    fun evaluateSpeech_incorrectTranscript_setsIncorrectState() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { speechValidator.validate("s", "m") } returns false

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.evaluateSpeech("s")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals("s", (state as SayItState.Incorrect).transcript)
        assertEquals(listOf(false), viewModel.attempts.value)
    }

    @Test
    fun evaluateSpeech_recordsMultipleAttemptsInList() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { speechValidator.validate("s", "m") } returns false
        every { speechValidator.validate("m", "m") } returns true

        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
        advanceUntilIdle()

        viewModel.evaluateSpeech("s")
        viewModel.evaluateSpeech("m")
        advanceUntilIdle()

        assertEquals(listOf(false, true), viewModel.attempts.value)
    }
}
