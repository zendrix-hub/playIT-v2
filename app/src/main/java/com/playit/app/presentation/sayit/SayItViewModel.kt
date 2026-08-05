package com.playit.app.presentation.sayit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.speech.VoskRecognizer
import com.playit.app.domain.manager.HeartManager
import com.playit.app.domain.manager.SpeechValidator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SayItState {
    object Idle : SayItState()
    object Listening : SayItState()
    data class Correct(val transcript: String) : SayItState()
    data class Incorrect(val transcript: String) : SayItState()
}

@HiltViewModel
class SayItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val speechValidator: SpeechValidator,
    private val voskRecognizer: VoskRecognizer,
    private val audioPlayer: AudioPlayer,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme.asStateFlow()

    private val _state = MutableStateFlow<SayItState>(SayItState.Idle)
    val state: StateFlow<SayItState> = _state.asStateFlow()

    val heartManager = HeartManager()

    private val _hearts = MutableStateFlow(heartManager.currentHearts)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    init {
        loadPhoneme()
    }

    private fun loadPhoneme() {
        val id = phonemeIdArg?.toIntOrNull() ?: 1
        viewModelScope.launch {
            val p = phonemeRepository.getPhonemeById(id) ?: Phoneme(
                id = 1,
                letter = "m",
                audioPath = "audio/phonemes/phoneme_m.mp3",
                imagePath = "images/pictures/word_mouse.png",
                exampleWord = "Mouse"
            )
            _phoneme.value = p
            voskRecognizer.initModel()
        }
    }

    fun startListening() {
        if (_state.value is SayItState.Listening) return
        _state.value = SayItState.Listening
        viewModelScope.launch {
            voskRecognizer.startListening { transcript ->
                evaluateSpeech(transcript)
            }
        }
    }

    fun stopListening() {
        val transcript = voskRecognizer.stopListening()
        if (_state.value is SayItState.Listening) {
            evaluateSpeech(transcript)
        }
    }

    fun evaluateSpeech(transcript: String) {
        val targetLetter = _phoneme.value?.letter ?: "m"
        val isCorrect = speechValidator.validate(transcript, targetLetter)
        val profileId = sessionManager.activeProfileId.value ?: 1L
        val phonemeId = _phoneme.value?.id ?: 1

        viewModelScope.launch {
            sayItAttemptRepository.saveAttempt(profileId, phonemeId, isCorrect)
        }

        if (isCorrect) {
            _state.value = SayItState.Correct(transcript.ifBlank { targetLetter })
        } else {
            heartManager.deductHeart()
            _hearts.value = heartManager.currentHearts
            _state.value = SayItState.Incorrect(transcript.ifBlank { "Try again!" })
        }
    }

    fun simulateCorrectForTesting() {
        evaluateSpeech(_phoneme.value?.letter ?: "m")
    }

    override fun onCleared() {
        super.onCleared()
        voskRecognizer.stopListening()
        voskRecognizer.release()
        audioPlayer.stop()
    }
}
