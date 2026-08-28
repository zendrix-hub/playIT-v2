package com.playit.app.presentation.hearit

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
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
    fun playPhonemeSound_playsCorrectAsset() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { audioResolver.getPhonemePath("m") } returns "audio/phonemes/phoneme_m.mp3"

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        verify(atLeast = 1) { audioPlayer.playAssetAudio("audio/phonemes/phoneme_m.mp3", any()) }
        
        viewModel.playPhonemeSound()
        verify(atLeast = 2) { audioPlayer.playAssetAudio("audio/phonemes/phoneme_m.mp3", any()) }
    }

    @Test
    fun playPhonemeSound_incrementsPlayCount() = runTest {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "path", exampleWord = "mouse")
        coEvery { phonemeRepository.getPhonemeById(1) } returns fakePhoneme
        every { audioResolver.getPhonemePath("m") } returns "audio/phonemes/phoneme_m.mp3"

        viewModel = HearItViewModel(phonemeRepository, audioPlayer, audioResolver, savedStateHandle)
        advanceUntilIdle()

        // Init loads and plays once
        assertEquals(1, viewModel.playCount.value)

        viewModel.playPhonemeSound()
        assertEquals(2, viewModel.playCount.value)
    }
}
