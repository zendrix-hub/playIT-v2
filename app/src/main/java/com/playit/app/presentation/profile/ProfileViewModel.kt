package com.playit.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.model.GameplayConstants
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Created(val profileId: Long) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver
) : ViewModel() {

    val profiles: StateFlow<List<Profile>> = profileRepository.getAllProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _showArithmeticGuard = MutableStateFlow(false)
    val showArithmeticGuard: StateFlow<Boolean> = _showArithmeticGuard.asStateFlow()

    fun requestParentAccess() { _showArithmeticGuard.value = true }
    fun dismissArithmeticGuard() { _showArithmeticGuard.value = false }
    fun onArithmeticGuardPassed() {
        _showArithmeticGuard.value = false
    }

    fun selectProfile(profileId: Long) {
        sessionManager.setActiveProfile(profileId)
    }

    fun playProfileSelectSound() {
        audioPlayer.playAssetAudio(audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME))
    }

    fun playArithmeticSuccessSound() {
        audioPlayer.playAssetAudio(audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME))
    }

    fun playArithmeticFailureSound() {
        audioPlayer.playAssetAudio(audioResolver.getSfxPath(SfxEvent.INCORRECT_POP))
    }

    fun playWelcomeGreeting() {
        audioPlayer.playAssetAudio(audioResolver.getVoPath(VoContext.WELCOME_01))
    }

    fun playNamePromptIntro() {
        audioPlayer.playAssetAudio(audioResolver.getVoPath(VoContext.NAMEPROMPT_INTRO))
    }

    fun playParentGateAudio() {
        audioPlayer.playAssetAudio(audioResolver.getVoPath(VoContext.PARENT_GATE))
    }

    fun createProfile(name: String, avatarResId: Int) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            _uiState.value = ProfileUiState.Error("Please enter a valid name.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = profileRepository.createProfile(trimmedName, avatarResId)
            result.onSuccess { newId ->
                sessionManager.setActiveProfile(newId)
                _uiState.value = ProfileUiState.Created(newId)
            }.onFailure { exception ->
                _uiState.value = ProfileUiState.Error(
                    exception.message ?: "Failed to create profile."
                )
            }
        }
    }

    fun canAddProfile(): Boolean {
        return profiles.value.size < GameplayConstants.MAX_PROFILES
    }

    fun clearUiState() {
        _uiState.value = ProfileUiState.Idle
    }
}
