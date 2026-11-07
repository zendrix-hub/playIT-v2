package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.BestResult
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
    private val heartsLostArg: String? = savedStateHandle["heartsLost"]
    private val wordsCorrectArg: String? = savedStateHandle["wordsCorrect"]
    private val totalWordsArg: String? = savedStateHandle["totalWords"]

    val groupId: Int = groupIdArg?.toIntOrNull() ?: 1

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _nextHighlighted = MutableStateFlow(false)
    val nextHighlighted: StateFlow<Boolean> = _nextHighlighted.asStateFlow()

    /** Goes up on each 10 s idle prompt; the screen wiggles the button to tap next (card 28b). */
    private val _idleNudge = MutableStateFlow(0)
    val idleNudge: StateFlow<Int> = _idleNudge.asStateFlow()

    private val idleTimer = IdleTimer(
        scope = viewModelScope,
        isBusy = { _isPlaying.value || audioPlayer.isSpeaking() },
        onIdle = {
            _idleNudge.value++
            audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_complete_next"))
        }
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
        val heartsLost = heartsLostArg?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val wordsCorrect = wordsCorrectArg?.toIntOrNull()?.coerceAtLeast(0) ?: 5
        val totalWords = totalWordsArg?.toIntOrNull()?.coerceAtLeast(1) ?: 5
        val profileId = sessionManager.activeProfileId.value ?: 1L

        viewModelScope.launch {
            val stars = BlendItStarThresholds.calculateStars(
                groupId = groupId,
                totalHeartsLost = heartsLost,
                wordsCorrect = wordsCorrect,
                totalWords = totalWords
            )
            _starsEarned.value = stars

            blendItProgressRepository.saveProgress(
                BestResult.blendIt(
                    saved = blendItProgressRepository.getProgressForGroup(profileId, groupId),
                    played = BlendItProgress(
                        profileId = profileId,
                        groupId = groupId,
                        starsEarned = stars,
                        heartsLost = heartsLost,
                        isCompleted = true,
                        completedAt = System.currentTimeMillis()
                    )
                )
            )
            val streak = streakTracker.recordActivity(profileId)?.currentStreak ?: 0

            // Fanfare, the complete line, then the streak badge and "You've been practicing every day" only on a real
            // streak (it used to play every time, even on a child's first day), then the next cue.
            val fanfareSfx = audioResolver.getSfxPath(SfxEvent.LEVEL_COMPLETE_FANFARE)
            val completeVo = audioResolver.getVoPath(VoContext.COMPLETE_01)
            val streakLines = if (streak >= 2) {
                listOf(audioResolver.getSfxPath(SfxEvent.STREAK_BADGE_UNLOCK), audioResolver.getVoPath(VoContext.STREAK_01))
            } else emptyList()
            val nextCue = audioResolver.getUiPath("ui_complete_next")

            _nextHighlighted.value = true
            _isPlaying.value = true
            audioPlayer.playSequence(listOf(fanfareSfx, completeVo) + streakLines + nextCue) {
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
