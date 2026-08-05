package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.manager.BlendItStarThresholds
import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlendItCompleteViewModel @Inject constructor(
    private val blendItProgressRepository: BlendItProgressRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupIdArg: String? = savedStateHandle["groupId"]
    val groupId: Int = groupIdArg?.toIntOrNull() ?: 1

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    init {
        completeSession()
    }

    private fun completeSession() {
        val profileId = sessionManager.activeProfileId.value ?: 1L
        viewModelScope.launch {
            val stars = BlendItStarThresholds.calculateStars(groupId, totalHeartsLost = 0)
            _starsEarned.value = stars

            blendItProgressRepository.saveProgress(
                BlendItProgress(
                    profileId = profileId,
                    groupId = groupId,
                    starsEarned = stars,
                    heartsLost = 0,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
