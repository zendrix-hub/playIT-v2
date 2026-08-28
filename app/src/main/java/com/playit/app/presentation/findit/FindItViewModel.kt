package com.playit.app.presentation.findit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.GridGenerator
import com.playit.app.domain.manager.HeartManager
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FindItState {
    object Idle : FindItState()
    data class Correct(val phoneme: Phoneme) : FindItState()
    data class Incorrect(val selectedPhoneme: Phoneme) : FindItState()
    object GameOver : FindItState()
}

@HiltViewModel
class FindItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val findItAttemptRepository: FindItAttemptRepository,
    private val gridGenerator: GridGenerator,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]

    private val _targetPhoneme = MutableStateFlow<Phoneme?>(null)
    val targetPhoneme: StateFlow<Phoneme?> = _targetPhoneme.asStateFlow()

    private val _gridItems = MutableStateFlow<List<Phoneme>>(emptyList())
    val gridItems: StateFlow<List<Phoneme>> = _gridItems.asStateFlow()

    private val _state = MutableStateFlow<FindItState>(FindItState.Idle)
    val state: StateFlow<FindItState> = _state.asStateFlow()

    val heartManager = HeartManager()

    private val _hearts = MutableStateFlow(heartManager.currentHearts)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private var consecutiveCorrect = 0

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    init {
        loadGrid()
        triggerScreenIntroIfNeeded()
    }

    private fun triggerScreenIntroIfNeeded() {
        if (sessionManager.shouldPlayScreenIntro("findit")) {
            val introVo = audioResolver.getVoPath(VoContext.FINDIT_INTRO_01)
            audioPlayer.playAssetAudio(introVo) {
                playTargetSound()
            }
        }
    }

    private fun loadGrid() {
        val id = phonemeIdArg?.toIntOrNull() ?: 1
        viewModelScope.launch {
            phonemeRepository.getAllPhonemes().collect { allPhonemes ->
                val target = allPhonemes.find { it.id == id }
                if (target == null) {
                    _loadError.value = true
                    return@collect
                }
                _targetPhoneme.value = target
                _gridItems.value = gridGenerator.generateGrid(id, allPhonemes)

                // Play target phoneme sound if screen intro was already played
                if (!sessionManager.shouldPlayScreenIntro("findit")) {
                    playTargetSound()
                }
            }
        }
    }

    fun playTargetSound() {
        val target = _targetPhoneme.value ?: return
        val path = audioResolver.getPhonemePath(target.letter) ?: target.audioPath
        _isPlaying.value = true
        audioPlayer.playAssetAudio(path) {
            _isPlaying.value = false
        }
    }

    fun playHintAudio() {
        val hintVo = audioResolver.getRotatingHintVo()
        audioPlayer.playAssetAudio(hintVo)
    }

    fun selectItem(selected: Phoneme) {
        val target = _targetPhoneme.value ?: return
        val isCorrect = selected.id == target.id
        val profileId = sessionManager.activeProfileId.value ?: 1L

        viewModelScope.launch {
            findItAttemptRepository.saveAttempt(
                profileId = profileId,
                phonemeId = target.id,
                selectedPhonemeId = selected.id,
                isCorrect = isCorrect
            )
        }

        if (isCorrect) {
            consecutiveCorrect++
            val isRecovered = heartManager.checkRecovery(consecutiveCorrect)
            if (isRecovered) {
                _hearts.value = heartManager.currentHearts
            }
            _state.value = FindItState.Correct(selected)
            val sfx = audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME)
            val recoverySfx = if (isRecovered) listOf(audioResolver.getSfxPath(SfxEvent.HEART_RECOVERY_SPARKLE)) else emptyList()
            val vo = audioResolver.getRotatingCorrectVo()
            audioPlayer.playSequence(listOf(sfx) + recoverySfx + listOf(vo))
        } else {
            consecutiveCorrect = 0
            val isGameOver = heartManager.deductHeart()
            _hearts.value = heartManager.currentHearts

            val sfxPop = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
            val sfxWhoosh = audioResolver.getSfxPath(SfxEvent.HEART_LOSS_WHOOSH)
            val vo = audioResolver.getRotatingEncourageVo()

            if (isGameOver) {
                _state.value = FindItState.GameOver
                audioPlayer.playSequence(listOf(sfxPop, sfxWhoosh, vo))
            } else {
                _state.value = FindItState.Incorrect(selected)
                audioPlayer.playSequence(listOf(sfxPop, sfxWhoosh, vo))
            }
        }
    }

    fun restartSession() {
        heartManager.resetForRestart()
        _hearts.value = heartManager.currentHearts
        _state.value = FindItState.Idle
        loadGrid()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
