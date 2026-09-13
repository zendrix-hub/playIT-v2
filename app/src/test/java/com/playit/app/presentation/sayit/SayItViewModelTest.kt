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
        every { audioResolver.getWordPath(any()) } returns "word_path"
        every { audioResolver.getVoPath(any()) } returns "vo_path"
        every { audioResolver.getSfxPath(any()) } returns "sfx_path"
        every { audioResolver.getRotatingCorrectVo() } returns "correct_vo"
        every { audioResolver.getRotatingEncourageVo() } returns "encourage_vo"
        every { speechValidator.validate(any(), any()) } returns false
        every { speechValidator.validateWord(any(), any()) } returns false
        every { audioPlayer.isAudioPlaying } returns MutableStateFlow(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakePhoneme(
        id: Int = 1,
        letter: String = "m",
        exampleWord: String = "mouse"
    ) = Phoneme(id = id, letter = letter, audioPath = "path", imagePath = "path", exampleWord = exampleWord)

    private fun createViewModel() {
        viewModel = SayItViewModel(
            phonemeRepository, sayItAttemptRepository, speechValidator,
            voskRecognizer, audioPlayer, audioResolver, sessionManager, savedStateHandle
        )
    }

    @Test
    fun loadPhoneme_validId_loadsPhoneme() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()

        createViewModel()
        advanceUntilIdle()

        assertEquals(fakePhoneme(), viewModel.phoneme.value)
        assertFalse(viewModel.loadError.value)
        coVerify { voskRecognizer.initModel() }
    }

    @Test
    fun loadPhoneme_invalidId_setsLoadError() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns null

        createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.phoneme.value)
        assertTrue(viewModel.loadError.value)
    }

    @Test
    fun loadPhoneme_wordMode_exposesNormalizedTargetWord() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme(exampleWord = "Mouse")

        createViewModel()
        advanceUntilIdle()

        assertEquals("mouse", viewModel.targetWord.value)
    }

    @Test
    fun loadPhoneme_smePendingLetter_usesLegacyLetterMode() = runTest {
        // ng/ñ have no approved example word — they must stay in letter-sound mode
        // (targetWord == null) and still pass via the phoneme validator.
        coEvery { phonemeRepository.getPhonemeById(1) } returns
            fakePhoneme(letter = "ng", exampleWord = "PENDING_SME_REVIEW")
        every { speechValidator.validate("ng", "ng") } returns true

        createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.targetWord.value)
        viewModel.evaluateSpeech("ng")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is SayItState.Correct)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, true) }
    }

    @Test
    fun evaluateSpeech_correctTranscript_setsCorrectState() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.validateWord("mouse", "mouse") } returns true

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("mouse")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Correct)
        assertEquals("mouse", (state as SayItState.Correct).transcript)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, true) }
    }

    @Test
    fun evaluateSpeech_incorrectTranscript_deductsHeart() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.validateWord("cat", "mouse") } returns false

        createViewModel()
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.evaluateSpeech("cat")
        advanceUntilIdle()

        assertEquals(initialHearts - 1, viewModel.hearts.value)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, false) }
    }

    @Test
    fun evaluateSpeech_incorrectTranscript_setsIncorrectState() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("sub")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals("sub", (state as SayItState.Incorrect).transcript)
        assertEquals(listOf(false), viewModel.attempts.value)
    }

    @Test
    fun evaluateSpeech_recordsMultipleAttemptsInList() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.validateWord("cat", "mouse") } returns false
        every { speechValidator.validateWord("mouse", "mouse") } returns true

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("cat")
        viewModel.evaluateSpeech("mouse")
        advanceUntilIdle()

        assertEquals(listOf(false, true), viewModel.attempts.value)
    }

    @Test
    fun wordMode_letterSoundTranscript_isIncorrect() = runTest {
        // Whole word required — saying only the letter sound must not pass in word mode.
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.validateWord("m", "mouse") } returns false

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("m")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        coVerify { sayItAttemptRepository.saveAttempt(1L, 1, false) }
    }

    @Test
    fun wordMode_promptPlaysIntroVoThenWordAudio() = runTest {
        // Auto prompt on load: word-mode intro VO, then the example-word audio.
        val playedPaths = mutableListOf<String>()
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { audioResolver.getVoPath(any()) } returns "vo_word_intro"
        every { audioResolver.getWordPath("mouse") } returns "audio/words/word_mouse.mp3"
        every { audioPlayer.playAssetAudio(any(), any()) } answers {
            playedPaths.add(firstArg())
            secondArg<(() -> Unit)?>()?.invoke()
        }

        createViewModel()
        advanceUntilIdle()

        assertEquals(listOf("vo_word_intro", "audio/words/word_mouse.mp3"), playedPaths)
    }

    @Test
    fun wordMode_startListening_grammarIsWordVariantsPlusDecoys() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.getAcceptedWordVariants("mouse") } returns listOf("mouse")

        createViewModel()
        advanceUntilIdle()

        val grammarSlot = slot<List<String>>()
        every { voskRecognizer.setGrammar(capture(grammarSlot)) } just Runs

        viewModel.startListening()

        assertTrue(grammarSlot.isCaptured)
        val grammar = grammarSlot.captured
        assertTrue(grammar.contains("mouse"))
        assertTrue(grammar.containsAll(listOf("cat", "dog", "sun", "ball", "yes", "no")))
        assertFalse(grammar.contains("m"))
        assertFalse(grammar.contains("em"))
        assertFalse(grammar.contains("muh"))

        // Drain the 3.8s auto-stop timer so runTest has no pending coroutines.
        advanceUntilIdle()
    }

    @Test
    fun playWordAudio_whenListening_stopsListeningAndPlaysAudio() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.getAcceptedWordVariants("mouse") } returns listOf("mouse")
        every { audioResolver.getWordPath("mouse") } returns "audio/words/word_mouse.mp3"

        createViewModel()
        advanceUntilIdle()

        viewModel.startListening()
        assertTrue(viewModel.state.value is SayItState.Listening)

        viewModel.playWordAudio()
        assertEquals(SayItState.Idle, viewModel.state.value)
        verify { voskRecognizer.stopListening() }
        verify { audioPlayer.playAssetAudio("audio/words/word_mouse.mp3", any()) }
    }

    @Test
    fun playWordAudio_rapidTaps_debounced() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { audioResolver.getWordPath("mouse") } returns "audio/words/word_mouse.mp3"

        createViewModel()
        advanceUntilIdle()

        clearMocks(audioPlayer, answers = false)

        // Rapid taps
        viewModel.playWordAudio()
        viewModel.playWordAudio()
        viewModel.playWordAudio()

        // Only the first tap within debounce window should trigger playAssetAudio
        verify(exactly = 1) { audioPlayer.playAssetAudio("audio/words/word_mouse.mp3", any()) }
    }
}
