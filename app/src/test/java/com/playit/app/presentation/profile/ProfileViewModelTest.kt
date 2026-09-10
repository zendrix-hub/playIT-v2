package com.playit.app.presentation.profile

import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val profileRepository: ProfileRepository = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val profilesFlow = MutableStateFlow<List<Profile>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { profileRepository.getAllProfiles() } returns profilesFlow
        every { audioResolver.getSfxPath(any()) } returns "sfx_path.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo_path.mp3"
        viewModel = ProfileViewModel(profileRepository, sessionManager, audioPlayer, audioResolver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun createProfile_withBlankName_emitsError() = runTest {
        viewModel.createProfile("   ", 1)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProfileUiState.Error)
        assertEquals("Please enter a valid name.", (viewModel.uiState.value as ProfileUiState.Error).message)
        coVerify(exactly = 0) { profileRepository.createProfile(any(), any()) }
    }

    @Test
    fun createProfile_success_updatesSessionAndEmitsCreated() = runTest {
        coEvery { profileRepository.createProfile("Maya", 2) } returns Result.success(42L)

        viewModel.createProfile("Maya", 2)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProfileUiState.Created)
        assertEquals(42L, (viewModel.uiState.value as ProfileUiState.Created).profileId)
        verify { sessionManager.setActiveProfile(42L) }
    }

    @Test
    fun createProfile_failure_emitsError() = runTest {
        coEvery { profileRepository.createProfile("Maya", 2) } returns Result.failure(RuntimeException("DB error"))

        viewModel.createProfile("Maya", 2)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProfileUiState.Error)
        assertEquals("DB error", (viewModel.uiState.value as ProfileUiState.Error).message)
    }

    @Test
    fun canAddProfile_returnsTrueWhenUnderLimit_andFalseWhenAtMax() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.profiles.collect()
        }

        profilesFlow.value = (1 until com.playit.app.domain.model.GameplayConstants.MAX_PROFILES).map {
            Profile(id = it.toLong(), name = "Child $it", avatarResId = it)
        }
        advanceUntilIdle()
        assertTrue(viewModel.canAddProfile())

        profilesFlow.value = (1..com.playit.app.domain.model.GameplayConstants.MAX_PROFILES).map {
            Profile(id = it.toLong(), name = "Child $it", avatarResId = it)
        }
        advanceUntilIdle()
        assertFalse(viewModel.canAddProfile())

        collectJob.cancel()
    }

    @Test
    fun arithmeticGuard_showAndDismissTransitions() {
        assertFalse(viewModel.showArithmeticGuard.value)

        viewModel.requestParentAccess()
        assertTrue(viewModel.showArithmeticGuard.value)

        viewModel.dismissArithmeticGuard()
        assertFalse(viewModel.showArithmeticGuard.value)
    }

    @Test
    fun selectProfile_stopsAudioAndSetsActiveProfile() {
        viewModel.selectProfile(10L)

        verify { audioPlayer.stop() }
        verify { sessionManager.setActiveProfile(10L) }
    }

    @Test
    fun onAvatarSelected_updatesAvatarIdAndPlaysSound() {
        viewModel.onAvatarSelected(3)

        assertEquals(3, viewModel.selectedAvatarId.value)
        verify { audioPlayer.playAssetAudio(any(), any()) }
    }

    @Test
    fun resetForm_restoresDefaultValues() {
        viewModel.onNameChanged("Testing")
        viewModel.onAvatarSelected(4)

        viewModel.resetForm()

        assertEquals("", viewModel.nameInput.value)
        assertEquals(1, viewModel.selectedAvatarId.value)
    }
}
