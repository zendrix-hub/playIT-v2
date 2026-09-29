package com.playit.app.presentation.sayit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.speech.VoskRecognizer
import com.playit.app.domain.manager.SpeechValidator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.SpeechErrorType
import com.playit.app.domain.model.SpeechJudgement
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
        every { speechValidator.grammarFor(any(), any()) } returns listOf("mouse")
        every { speechValidator.judgeWord(any(), any(), any()) } answers {
            SpeechJudgement(
                isCorrect = false,
                errorType = SpeechErrorType.OTHER_WORD,
                heard = firstArg<String?>() ?: ""
            )
        }
        every { speechValidator.judgeSound(any(), any(), any()) } answers {
            SpeechJudgement(
                isCorrect = false,
                errorType = SpeechErrorType.OTHER_WORD,
                heard = firstArg<String?>() ?: ""
            )
        }
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
        every { speechValidator.judgeWord("mouse", "mouse", "m") } returns
            SpeechJudgement(isCorrect = true, errorType = SpeechErrorType.NONE, heard = "mouse")

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
        every { speechValidator.judgeWord("cat", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.OTHER_WORD, heard = "cat")
        every { speechValidator.judgeWord("mouse", "mouse", "m") } returns
            SpeechJudgement(isCorrect = true, errorType = SpeechErrorType.NONE, heard = "mouse")

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
        every { speechValidator.judgeWord("m", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.LETTER_NAME, heard = "m")

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
    fun startListening_setsLetterScopedGrammar() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        val expectedGrammar = listOf("mouse", "m", "em", "ma", "muh")
        every { speechValidator.grammarFor("m", "mouse") } returns expectedGrammar

        createViewModel()
        advanceUntilIdle()

        viewModel.startListening()

        verify { voskRecognizer.setGrammar(expectedGrammar) }

        // Drain the 3.8s auto-stop timer so runTest has no pending coroutines.
        advanceUntilIdle()
    }

    @Test
    fun evaluateSpeech_letterName_setsIncorrectWithLetterName() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.judgeWord("em", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.LETTER_NAME, heard = "em")

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("em")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals(SpeechErrorType.LETTER_NAME, (state as SayItState.Incorrect).errorType)
        assertEquals("em", state.transcript)
    }

    @Test
    fun evaluateSpeech_addedVowel_setsIncorrectWithAddedVowel() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        every { speechValidator.judgeWord("ma", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.ADDED_VOWEL, heard = "ma")

        createViewModel()
        advanceUntilIdle()

        viewModel.evaluateSpeech("ma")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals(SpeechErrorType.ADDED_VOWEL, (state as SayItState.Incorrect).errorType)
        assertEquals("ma", state.transcript)
    }

    @Test
    fun onResult_partialFoil_keepsListening() = runTest {
        // A wrong partial can be the start of the target word, so it must not end the attempt.
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        val callbackSlot = slot<(String, Boolean) -> Unit>()
        every { voskRecognizer.startListening(capture(callbackSlot)) } just Runs
        every { speechValidator.judgeWord("em", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.LETTER_NAME, heard = "em")
        every { speechValidator.judgeWord("ma", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.ADDED_VOWEL, heard = "ma")

        createViewModel()
        advanceUntilIdle()

        viewModel.startListening()
        assertTrue(callbackSlot.isCaptured)

        callbackSlot.captured.invoke("em", false)
        callbackSlot.captured.invoke("ma", false)

        assertEquals(SayItState.Listening, viewModel.state.value)
        assertTrue(viewModel.attempts.value.isEmpty())
        verify(exactly = 0) { voskRecognizer.stopListening() }

        // Drain the 3.8s auto-stop timer so runTest has no pending coroutines.
        advanceUntilIdle()
    }

    @Test
    fun onResult_partialAddedVowelThenFinalWord_isCorrect() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        val callbackSlot = slot<(String, Boolean) -> Unit>()
        every { voskRecognizer.startListening(capture(callbackSlot)) } just Runs
        every { speechValidator.judgeWord("ma", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.ADDED_VOWEL, heard = "ma")
        every { speechValidator.judgeWord("mouse", "mouse", "m") } returns
            SpeechJudgement(isCorrect = true, errorType = SpeechErrorType.NONE, heard = "mouse")

        createViewModel()
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.startListening()
        callbackSlot.captured.invoke("ma", false)
        assertEquals(SayItState.Listening, viewModel.state.value)

        callbackSlot.captured.invoke("mouse", true)
        advanceUntilIdle()

        assertEquals(SayItState.Correct("mouse"), viewModel.state.value)
        assertEquals(listOf(true), viewModel.attempts.value)
        assertEquals(initialHearts, viewModel.hearts.value)
        coVerify(exactly = 0) { sayItAttemptRepository.saveAttempt(any(), any(), false) }
    }

    @Test
    fun onResult_partialLetterNameThenFinalWord_isCorrect() = runTest {
        // "a" is both the letter name and the start of "apple".
        coEvery { phonemeRepository.getPhonemeById(1) } returns
            fakePhoneme(letter = "a", exampleWord = "apple")
        val callbackSlot = slot<(String, Boolean) -> Unit>()
        every { voskRecognizer.startListening(capture(callbackSlot)) } just Runs
        every { speechValidator.judgeWord("a", "apple", "a") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.LETTER_NAME, heard = "a")
        every { speechValidator.judgeWord("apple", "apple", "a") } returns
            SpeechJudgement(isCorrect = true, errorType = SpeechErrorType.NONE, heard = "apple")

        createViewModel()
        advanceUntilIdle()

        val initialHearts = viewModel.hearts.value
        viewModel.startListening()
        callbackSlot.captured.invoke("a", false)
        assertEquals(SayItState.Listening, viewModel.state.value)

        callbackSlot.captured.invoke("apple", true)
        advanceUntilIdle()

        assertEquals(SayItState.Correct("apple"), viewModel.state.value)
        assertEquals(listOf(true), viewModel.attempts.value)
        assertEquals(initialHearts, viewModel.hearts.value)
        coVerify(exactly = 0) { sayItAttemptRepository.saveAttempt(any(), any(), false) }
    }

    @Test
    fun onResult_finalLetterName_setsIncorrectWithLetterName() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        val callbackSlot = slot<(String, Boolean) -> Unit>()
        every { voskRecognizer.startListening(capture(callbackSlot)) } just Runs
        every { speechValidator.judgeWord("em", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.LETTER_NAME, heard = "em")

        createViewModel()
        advanceUntilIdle()

        viewModel.startListening()
        callbackSlot.captured.invoke("em", true)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals(SpeechErrorType.LETTER_NAME, (state as SayItState.Incorrect).errorType)
        verify { voskRecognizer.stopListening() }
    }

    @Test
    fun onResult_partialAddedVowelThenTimeout_setsIncorrectWithAddedVowel() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
        val callbackSlot = slot<(String, Boolean) -> Unit>()
        every { voskRecognizer.startListening(capture(callbackSlot)) } just Runs
        every { voskRecognizer.stopListening() } returns "ma"
        every { speechValidator.judgeWord("ma", "mouse", "m") } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.ADDED_VOWEL, heard = "ma")

        createViewModel()
        advanceUntilIdle()

        viewModel.startListening()
        callbackSlot.captured.invoke("ma", false)
        assertEquals(SayItState.Listening, viewModel.state.value)

        // The 3.8s auto-stop judges the last transcript.
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SayItState.Incorrect)
        assertEquals(SpeechErrorType.ADDED_VOWEL, (state as SayItState.Incorrect).errorType)
    }

    @Test
    fun playWordAudio_whenListening_stopsListeningAndPlaysAudio() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme()
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
