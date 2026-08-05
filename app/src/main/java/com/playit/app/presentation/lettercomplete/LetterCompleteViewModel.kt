package com.playit.app.presentation.lettercomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.manager.StarCalculator
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
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
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme.asStateFlow()

    private val _starsEarned = MutableStateFlow(3)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    init {
        completeLesson()
    }

    private fun completeLesson() {
        val phonemeId = phonemeIdArg?.toIntOrNull() ?: 1
        val profileId = sessionManager.activeProfileId.value ?: 1L

        viewModelScope.launch {
            val p = phonemeRepository.getPhonemeById(phonemeId) ?: Phoneme(
                id = 1,
                letter = "m",
                audioPath = "audio/phonemes/phoneme_m.mp3",
                imagePath = "images/pictures/word_mouse.png",
                exampleWord = "Mouse"
            )
            _phoneme.value = p

            val stars = StarCalculator.calculateStars(heartsLost = 0)
            _starsEarned.value = stars

            // Save lesson completion
            lessonProgressRepository.saveProgress(
                LessonProgress(
                    profileId = profileId,
                    phonemeId = phonemeId,
                    starsEarned = stars,
                    heartsLost = 0,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
