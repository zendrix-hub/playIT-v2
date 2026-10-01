package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.BlendItStarThresholds
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.playit.app.presentation.components.IdleTimer
import javax.inject.Inject

@HiltViewModel
class BlendItCompleteViewModel @Inject constructor(
    private val blendItProgressRepository: BlendItProgressRepository,
    private val streakTracker: StreakTracker,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupIdArg: String? = savedStateHandle["groupId"]
    val groupId: Int = groupIdArg?.toIntOrNull() ?: 1

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _nextHighlighted = MutableStateFlow(false)
    val nextHighlighted: StateFlow<Boolean> = _nextHighlighted.asStateFlow()

    private val idleTimer = IdleTimer(
        scope = viewModelScope,
        isBusy = { _isPlaying.value },
        onIdle = { audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_complete_next")) }
    )

    fun onScreenVisible() {
        idleTimer.start()
    }

    fun onScreenHidden() {
        idleTimer.stop()
    }

    fun onUserInteraction() {
        idleTimer.touch()
    }

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
            streakTracker.recordActivity(profileId)

            // Play completion fanfare + complete VO line + milestone/streak badge unlock audio + next cue
            val fanfareSfx = audioResolver.getSfxPath(SfxEvent.LEVEL_COMPLETE_FANFARE)
            val completeVo = audioResolver.getVoPath(VoContext.COMPLETE_01)
            val streakSfx = audioResolver.getSfxPath(SfxEvent.STREAK_BADGE_UNLOCK)
            val streakVo = audioResolver.getVoPath(VoContext.STREAK_01)
            val nextCue = audioResolver.getUiPath("ui_complete_next")

            _nextHighlighted.value = true
            _isPlaying.value = true
            audioPlayer.playSequence(listOf(fanfareSfx, completeVo, streakSfx, streakVo, nextCue)) {
                _isPlaying.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        idleTimer.stop()
        audioPlayer.stop()
    }
}
