package com.playit.app.presentation.sayit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.data.speech.VoskRecognizer
import com.playit.app.domain.manager.HeartManager
import com.playit.app.domain.manager.SpeechValidator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val audioResolver: AudioResolver,
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

    private val _attempts = MutableStateFlow<List<Boolean>>(emptyList())
    val attempts: StateFlow<List<Boolean>> = _attempts.asStateFlow()

    private val _isPlayingPhoneme = MutableStateFlow(false)
    val isPlayingPhoneme: StateFlow<Boolean> = _isPlayingPhoneme.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _isNoisyEnvironment = MutableStateFlow(false)
    val isNoisyEnvironment: StateFlow<Boolean> = _isNoisyEnvironment.asStateFlow()

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    private var autoStopJob: Job? = null

    init {
        loadPhoneme()
    }

    private fun loadPhoneme() {
        val id = phonemeIdArg?.toIntOrNull() ?: 1
        viewModelScope.launch {
            val p = phonemeRepository.getPhonemeById(id)
            if (p == null) {
                _loadError.value = true
                return@launch
            }
            _loadError.value = false
            _phoneme.value = p
            voskRecognizer.initModel()

            // Automatically play speech bubble prompt first then the letter sound
            playIntroThenPhonemeSound()
        }
    }

    fun retry() {
        _loadError.value = false
        loadPhoneme()
    }

    private val _isPlayingPrompt = MutableStateFlow(false)
    val isPlayingPrompt: StateFlow<Boolean> = _isPlayingPrompt.asStateFlow()

    fun playIntroThenPhonemeSound() {
        if (_state.value is SayItState.Listening) {
            autoStopJob?.cancel()
            voskRecognizer.stopListening()
            _state.value = SayItState.Idle
            _audioAmplitude.value = 0f
        }
        audioPlayer.stop()
        _isPlayingPhoneme.value = false
        _isPlayingPrompt.value = true
        val introVo = audioResolver.getVoPath(VoContext.SAYIT_INTRO_01)
        audioPlayer.playAssetAudio(introVo) {
            _isPlayingPrompt.value = false
            playPhonemeSound()
        }
    }

    fun playSayItIntroAudio() {
        playIntroThenPhonemeSound()
    }

    fun playPhonemeSound() {
        if (_state.value is SayItState.Listening) {
            autoStopJob?.cancel()
            voskRecognizer.stopListening()
            _state.value = SayItState.Idle
            _audioAmplitude.value = 0f
        }
        audioPlayer.stop()
        _isPlayingPrompt.value = false
        val letter = _phoneme.value?.letter ?: "m"
        val path = audioResolver.getPhonemePath(letter) ?: _phoneme.value?.audioPath ?: "audio/phonemes/phoneme_m.mp3"
        _isPlayingPhoneme.value = true
        audioPlayer.playAssetAudio(path) {
            _isPlayingPhoneme.value = false
        }
    }

    fun playQuietCheckBeforeListening(onReady: () -> Unit) {
        val quietVo = audioResolver.getVoPath(VoContext.QUIET_CHECK_01)
        audioPlayer.playAssetAudio(quietVo) {
            onReady()
        }
    }

    fun playNoiseAlert() {
        val noiseVo = audioResolver.getVoPath(VoContext.NOISE_ALERT_01)
        audioPlayer.playAssetAudio(noiseVo)
    }

    fun startListening() {
        if (_state.value is SayItState.Listening) return

        // Instantly silence any voiceover or phoneme audio so it never bleeds into the mic
        audioPlayer.stop()
        _isPlayingPhoneme.value = false

        autoStopJob?.cancel()
        val target = _phoneme.value?.letter?.lowercase() ?: "m"
        val acceptedList = speechValidator.getAcceptedVariants(target)
        voskRecognizer.setGrammar(acceptedList + listOf("cat", "dog", "sun", "ball", "yes", "no"))
        
        _state.value = SayItState.Listening
        _isNoisyEnvironment.value = false

        // Auto-timeout after 3.8s maximum window if child hasn't finished speaking
        autoStopJob = viewModelScope.launch {
            delay(3800L)
            if (_state.value is SayItState.Listening) {
                stopListening()
            }
        }

        voskRecognizer.startListening(
            onResult = { transcript ->
                if (transcript.isNotBlank() && _state.value is SayItState.Listening) {
                    val isCorrect = speechValidator.validate(transcript, target)
                    if (isCorrect) {
                        autoStopJob?.cancel()
                        voskRecognizer.stopListening()
                        evaluateSpeech(transcript)
                    }
                }
            }
        )
    }

    fun stopListening() {
        autoStopJob?.cancel()
        val transcript = voskRecognizer.stopListening()
        _audioAmplitude.value = 0f
        if (_state.value is SayItState.Listening) {
            evaluateSpeech(transcript)
        }
    }

    fun evaluateSpeech(transcript: String) {
        autoStopJob?.cancel()
        _audioAmplitude.value = 0f
        val targetLetter = _phoneme.value?.letter ?: "m"
        val isCorrect = speechValidator.validate(transcript, targetLetter)
        val profileId = sessionManager.activeProfileId.value ?: 1L
        val phonemeId = _phoneme.value?.id ?: 1

        _attempts.value = _attempts.value + isCorrect

        viewModelScope.launch {
            sayItAttemptRepository.saveAttempt(profileId, phonemeId, isCorrect)
        }

        if (isCorrect) {
            _state.value = SayItState.Correct(transcript.ifBlank { targetLetter })
            val sfx = audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME)
            val vo = audioResolver.getRotatingCorrectVo()
            audioPlayer.playSequence(listOf(sfx, vo))
        } else {
            heartManager.deductHeart()
            _hearts.value = heartManager.currentHearts
            _state.value = SayItState.Incorrect(transcript.ifBlank { "Try again!" })

            val sfxPop = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
            val sfxWhoosh = audioResolver.getSfxPath(SfxEvent.HEART_LOSS_WHOOSH)
            val voEncourage = audioResolver.getRotatingEncourageVo()
            audioPlayer.playSequence(listOf(sfxPop, sfxWhoosh, voEncourage))
        }
    }

    fun simulateCorrectForTesting() {
        evaluateSpeech(_phoneme.value?.letter ?: "m")
    }

    override fun onCleared() {
        super.onCleared()
        autoStopJob?.cancel()
        voskRecognizer.stopListening()
        audioPlayer.stop()
    }
}
