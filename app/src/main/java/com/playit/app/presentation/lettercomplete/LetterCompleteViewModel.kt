package com.playit.app.presentation.lettercomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.StarCalculator
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterCompleteViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val profileRepository: ProfileRepository,
    private val streakTracker: StreakTracker,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]
    private val heartsLostArg: String? = savedStateHandle["heartsLost"]

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme.asStateFlow()

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()
    val isAudioPlaying: StateFlow<Boolean> = audioPlayer.isAudioPlaying

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    init {
        completeLesson()
    }

    fun retry() {
        _loadError.value = false
        completeLesson()
    }

    private fun completeLesson() {
        val phonemeId = phonemeIdArg?.toIntOrNull() ?: 1
        val heartsLost = heartsLostArg?.toIntOrNull()?.coerceIn(0, 2) ?: 0
        val profileId = sessionManager.activeProfileId.value ?: 1L

        viewModelScope.launch {
            val p = phonemeRepository.getPhonemeById(phonemeId)
            if (p == null) {
                _loadError.value = true
                return@launch
            }
            _loadError.value = false
            _phoneme.value = p

            val stars = StarCalculator.calculateStars(heartsLost = heartsLost)
            _starsEarned.value = stars

            // Query existing progress to calculate star delta and prevent downgrading
            val existing = lessonProgressRepository.getProgressForPhoneme(profileId, phonemeId)
            val previousStars = existing?.starsEarned ?: 0
            val bestStars = maxOf(previousStars, stars)
            val starDelta = (bestStars - previousStars).coerceAtLeast(0)

            // Save lesson completion with highest stars earned
            lessonProgressRepository.saveProgress(
                LessonProgress(
                    id = existing?.id ?: 0,
                    profileId = profileId,
                    phonemeId = phonemeId,
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

            // Play completion fanfare + complete VO line, followed by unlock chime + unlock VO
            val fanfareSfx = audioResolver.getSfxPath(SfxEvent.LEVEL_COMPLETE_FANFARE)
            val completeVo = audioResolver.getVoPath(VoContext.COMPLETE_01)
            val unlockSfx = audioResolver.getSfxPath(SfxEvent.NODE_UNLOCK_CHIME)
            val unlockVo = audioResolver.getVoPath(VoContext.UNLOCK_01)

            audioPlayer.playSequence(listOf(fanfareSfx, completeVo, unlockSfx, unlockVo))
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
