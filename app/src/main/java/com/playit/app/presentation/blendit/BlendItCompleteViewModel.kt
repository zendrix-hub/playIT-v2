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
import com.playit.app.domain.repository.ProfileRepository
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
    private val profileRepository: ProfileRepository,
    private val streakTracker: StreakTracker,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupIdArg: String? = savedStateHandle["groupId"]
    val groupId: Int = groupIdArg?.toIntOrNull() ?: 1

    private val heartsLostArg: String? = savedStateHandle["heartsLost"]
    val heartsLost: Int = heartsLostArg?.toIntOrNull()?.coerceIn(0, 5) ?: 0

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()
    val isAudioPlaying: StateFlow<Boolean> = audioPlayer.isAudioPlaying

    init {
        completeSession()
    }

    private fun completeSession() {
        val profileId = sessionManager.activeProfileId.value ?: 1L
        viewModelScope.launch {
            val stars = BlendItStarThresholds.calculateStars(groupId, totalHeartsLost = heartsLost)
            _starsEarned.value = stars

            // Query existing progress to calculate star delta and prevent downgrading
            val existing = blendItProgressRepository.getProgressForGroup(profileId, groupId)
            val previousStars = existing?.starsEarned ?: 0
            val bestStars = maxOf(previousStars, stars)
            val starDelta = (bestStars - previousStars).coerceAtLeast(0)

            blendItProgressRepository.saveProgress(
                BlendItProgress(
                    id = existing?.id ?: 0,
                    profileId = profileId,
                    groupId = groupId,
                    starsEarned = bestStars,
                    heartsLost = heartsLost,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis()
                )
            )
            if (starDelta > 0) {
                profileRepository.addStars(profileId, starDelta)
            }
            streakTracker.recordActivity(profileId)

            // Play completion fanfare + complete VO line + milestone/streak badge unlock audio
            val fanfareSfx = audioResolver.getSfxPath(SfxEvent.LEVEL_COMPLETE_FANFARE)
            val completeVo = audioResolver.getVoPath(VoContext.COMPLETE_01)
            val streakSfx = audioResolver.getSfxPath(SfxEvent.STREAK_BADGE_UNLOCK)
            val streakVo = audioResolver.getVoPath(VoContext.STREAK_01)

            audioPlayer.playSequence(listOf(fanfareSfx, completeVo, streakSfx, streakVo))
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
