package com.playit.app.presentation.hearit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.HearItSequenceBuilder
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HearItViewModelTest {

    private lateinit var viewModel: HearItViewModel
    private val phonemeRepository: PhonemeRepository = mockk()
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { phonemeRepository.getPhonemeById(any()) } returns null
        every { savedStateHandle.get<String>("phonemeId") } returns "1"
        every { audioResolver.getPhonemePath(any()) } returns "test_path"
        every { audioResolver.getVoPath(any()) } returns "test_vo_path"
        every { audioResolver.getWordPath(any()) } returns "word_path"
        every { audioResolver.getKeyWordPath(any()) } returns "word_path"
        every { audioResolver.getTutorPath(any()) } answers { "tutor/${firstArg<String>()}.wav" }
        every { audioResolver.getUiPath(any()) } answers { "ui/${firstArg<String>()}.wav" }
        every { audioPlayer.playAssetAudio(any(), any()) } answers {
            secondArg<(() -> Unit)?>()?.invoke()
        }
        every { audioPlayer.playSequence(any(), any()) } answers {
            secondArg<(() -> Unit)?>()?.invoke()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadPhoneme_validId_loadsPhoneme() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertEquals(fakePhoneme, viewModel.phoneme.value)
        assertFalse(viewModel.loadError.value)
    }

    @Test
    fun loadPhoneme_invalidId_setsLoadError() = runTest {
        coEvery { phonemeRepository.getPhonemeById(1) } returns null

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertNull(viewModel.phoneme.value)
        assertTrue(viewModel.loadError.value)
    }

    @Test
    fun loadPhoneme_nullId_defaultsToId1() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        every { savedStateHandle.get<String>("phonemeId") } returns null
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertEquals(fakePhoneme, viewModel.phoneme.value)
        coVerify { phonemeRepository.getPhonemeById(1) }
    }

    @Test
    fun load_playsFullModelingSequence() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        val expectedSequence = HearItSequenceBuilder.build(
            HearItSequenceBuilder.TEMPLATE, "test_path", "word_path"
        ) { "tutor/$it.wav" }

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        verify { audioPlayer.playSequence(expectedSequence, any()) }
        verify { audioResolver.getKeyWordPath("mouse") }
    }

    @Test
    fun playPhonemeSound_playsReplaySegment() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        val expectedReplaySequence = HearItSequenceBuilder.build(
            HearItSequenceBuilder.replayTemplate(), "test_path", "word_path"
        ) { "tutor/$it.wav" }

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        viewModel.playPhonemeSound()
        advanceUntilIdle()

        verify { audioPlayer.playSequence(expectedReplaySequence, any()) }
    }

    @Test
    fun load_pendingExampleWord_dropsKeyword() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "ng", audioPath = "path", imagePath = "path", exampleWord = "PENDING_SME_REVIEW")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        val expectedSequence = HearItSequenceBuilder.build(
            HearItSequenceBuilder.TEMPLATE, "test_path", null
        ) { "tutor/$it.wav" }

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        verify { audioPlayer.playSequence(expectedSequence, any()) }
        verify(exactly = 0) { audioResolver.getKeyWordPath(any()) }
    }

    @Test
    fun playPhonemeSound_incrementsPlayCount() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        // Init loads and plays modeling sequence once
        assertEquals(1, viewModel.playCount.value)

        viewModel.playPhonemeSound()
        assertEquals(2, viewModel.playCount.value)
    }

    @Test
    fun firstSequenceEnd_playsNextCue_andHighlights() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertTrue(viewModel.nextHighlighted.value)
        verify(exactly = 1) { audioPlayer.playAssetAudio("ui/ui_hearit_next.wav", any()) }
    }

    @Test
    fun idle_afterHighlight_playsNextCue() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        viewModel.onScreenVisible()
        advanceTimeBy(10_001)

        verify(exactly = 2) { audioPlayer.playAssetAudio("ui/ui_hearit_next.wav", any()) }
    }

    @Test
    fun noIdlePrompt_withoutScreenVisible() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        advanceTimeBy(60_000)
        verify(exactly = 1) { audioPlayer.playAssetAudio("ui/ui_hearit_next.wav", any()) }
    }

    @Test
    fun nextStaysOff_whileFirstSequencePlays() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { audioPlayer.playSequence(any(), any()) } just Runs

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertFalse(viewModel.nextHighlighted.value)
        verify(exactly = 0) { audioPlayer.playAssetAudio("ui/ui_hearit_next.wav", any()) }
    }

    @Test
    fun replayEnd_alsoUnlocks() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        var n = 0
        every { audioPlayer.playSequence(any(), any()) } answers {
            n++
            if (n >= 2) secondArg<(() -> Unit)?>()?.invoke()
        }

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        assertFalse(viewModel.nextHighlighted.value)
        viewModel.playPhonemeSound()
        advanceUntilIdle()

        assertTrue(viewModel.nextHighlighted.value)
        verify(exactly = 1) { audioPlayer.playAssetAudio("ui/ui_hearit_next.wav", any()) }
    }
}
