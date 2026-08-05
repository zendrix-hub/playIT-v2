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

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Created(val profileId: Long) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val profiles: StateFlow<List<Profile>> = profileRepository.getAllProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun selectProfile(profileId: Long) {
        sessionManager.setActiveProfile(profileId)
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
