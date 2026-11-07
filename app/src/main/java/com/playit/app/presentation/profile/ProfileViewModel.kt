package com.playit.app.presentation.profile

import kotlinx.coroutines.flow.first
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
import com.playit.app.presentation.profile.components.AVATAR_NAMES

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Created(val profileId: Long) : ProfileUiState()
}

/** What Lily says on "Who is playing?": the long hello the first time, then the short question. */
const val FIRST_HELLO = "Hi there! I'm so happy you're here. Let's play and learn together!"
const val WHO_IS_PLAYING = "Who is playing? Tap your animal!"

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

    /**
     * This screen has handed over to the next one (the map, the name step, the Parent Zone) and stopped its own audio
     * then. The player is shared, and this ViewModel is cleared only after the next screen has begun to speak, so
     * onCleared must not stop that voice (the map's welcome back, user 2026-11-07).
     */
    private var handedOff = false

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _nameInput = MutableStateFlow("")
    val nameInput: StateFlow<String> = _nameInput.asStateFlow()

    private val _selectedAvatarId = MutableStateFlow(1)
    val selectedAvatarId: StateFlow<Int> = _selectedAvatarId.asStateFlow()

    private val _showArithmeticGuard = MutableStateFlow(false)
    val showArithmeticGuard: StateFlow<Boolean> = _showArithmeticGuard.asStateFlow()

    private val _isPlayingGreeting = MutableStateFlow(false)
    val isPlayingGreeting: StateFlow<Boolean> = _isPlayingGreeting.asStateFlow()

    fun onNameChanged(name: String) { _nameInput.value = name }
    fun onAvatarSelected(id: Int) {
        _selectedAvatarId.value = id
        audioPlayer.playAssetAudio(audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME))
    }
    fun resetForm() {
        _nameInput.value = ""
        _selectedAvatarId.value = 1
    }

    fun requestParentAccess() { _showArithmeticGuard.value = true }
    fun dismissArithmeticGuard() { _showArithmeticGuard.value = false }
    fun onArithmeticGuardPassed() {
        _showArithmeticGuard.value = false
    }

    fun selectProfile(profileId: Long) {
        leave()
        sessionManager.setActiveProfile(profileId)
    }

    /** Leaving this screen for the next: its own audio stops now, not when it is cleared. */
    fun leave() {
        audioPlayer.stop()
        _isPlayingGreeting.value = false
        handedOff = true
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

    /**
     * The long hello only the first time, before any child has a profile; after that Lily just asks "Who is playing?
     * Tap your animal!" (release 2026-11-05-name-lines), so a returning child is not greeted twice: the map's
     * welcome back says hello.
     */
    private val _greeting = MutableStateFlow(FIRST_HELLO)
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    fun playWelcomeGreeting() {
        handedOff = false  // on screen again: what plays is its own
        viewModelScope.launch {
            val firstUse = profileRepository.getAllProfiles().first().isEmpty()
            _greeting.value = if (firstUse) FIRST_HELLO else WHO_IS_PLAYING
            _isPlayingGreeting.value = true
            val clip = if (firstUse) audioResolver.getVoPath(VoContext.WELCOME_01) else audioResolver.getUiPath("ui_who_is_playing")
            audioPlayer.playAssetAudio(clip) {
                _isPlayingGreeting.value = false
            }
        }
    }

    /** Once the child has picked an animal: "Now type your name!" (the child types it, user 2026-11-05). */
    fun playTypeNamePrompt() {
        _isPlayingIntro.value = true
        audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_type_name")) {
            _isPlayingIntro.value = false
        }
    }

    private val _isPlayingIntro = MutableStateFlow(false)
    val isPlayingIntro: StateFlow<Boolean> = _isPlayingIntro.asStateFlow()

    fun playNamePromptIntro() {
        _isPlayingIntro.value = true
        audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_pick_avatar")) {
            _isPlayingIntro.value = false
        }
    }

    fun playParentGateAudio() {
        audioPlayer.playAssetAudio(audioResolver.getVoPath(VoContext.PARENT_GATE))
    }

    override fun onCleared() {
        super.onCleared()
        if (!handedOff) audioPlayer.stop()
    }

    fun createProfile(name: String, avatarResId: Int) {
        val finalName = name.trim().ifBlank { AVATAR_NAMES.getOrElse(avatarResId - 1) { "Friend" } }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = profileRepository.createProfile(finalName, avatarResId)
            result.onSuccess { newId ->
                sessionManager.setActiveProfile(newId)
                leave()
                _uiState.value = ProfileUiState.Created(newId)
            }.onFailure { exception ->
                _uiState.value = ProfileUiState.Error(
                    exception.message ?: "Something went wrong. Please try again."
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
