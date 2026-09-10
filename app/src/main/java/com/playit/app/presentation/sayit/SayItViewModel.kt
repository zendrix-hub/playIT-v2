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

    /**
     * The example word the child must utter (lowercased), e.g. "mouse" for letter M.
     * null = legacy letter-sound mode, kept for SME-pending letters (ng/ñ).
     */
    private val _targetWord = MutableStateFlow<String?>(null)
    val targetWord: StateFlow<String?> = _targetWord.asStateFlow()

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

    private val _isModelInitializing = MutableStateFlow(false)
    val isModelInitializing: StateFlow<Boolean> = _isModelInitializing.asStateFlow()

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
            _targetWord.value = resolveWordTarget(p)
            _isModelInitializing.value = !voskRecognizer.isModelReady()
            voskRecognizer.initModel()
            _isModelInitializing.value = false

            // Automatically play the intro prompt, then the model audio (word or letter sound)
            playIntroThenPromptAudio()
        }
    }

    /**
     * Word mode = the child utters the phoneme's seeded example word (e.g. "Mouse").
     * SME-pending letters (ng/ñ) have no approved example content, so they stay in the
     * legacy letter-sound mode (null target word).
     */
    private fun resolveWordTarget(p: Phoneme): String? {
        val letter = p.letter.lowercase().trim()
        if (letter == "ng" || letter == "ñ") return null
        val word = p.exampleWord.trim()
        if (word.isEmpty() || word.equals("PENDING_SME_REVIEW", ignoreCase = true)) return null
        return word.lowercase()
    }

    private val isWordMode: Boolean
        get() = _targetWord.value != null

    fun retry() {
        _loadError.value = false
        loadPhoneme()
    }

    private val _isPlayingPrompt = MutableStateFlow(false)
    val isPlayingPrompt: StateFlow<Boolean> = _isPlayingPrompt.asStateFlow()

    fun playSayItIntroAudio() {
        playIntroThenPromptAudio()
    }

    /**
     * Plays the spoken intro prompt (word-mode VO in word mode, letter-sound VO in legacy
     * mode), then the model audio the child must imitate — the example word or the pure
     * phoneme. Matches the on-screen speech bubble 1:1 (HP-4).
     */
    private fun playIntroThenPromptAudio() {
        if (_state.value is SayItState.Listening) {
            autoStopJob?.cancel()
            voskRecognizer.stopListening()
            _state.value = SayItState.Idle
            _audioAmplitude.value = 0f
        }
        audioPlayer.stop()
        _isPlayingPhoneme.value = false
        _isPlayingPrompt.value = true
        val introVo = audioResolver.getVoPath(
            if (isWordMode) VoContext.SAYIT_WORD_INTRO_01 else VoContext.SAYIT_INTRO_01
        )
        audioPlayer.playAssetAudio(introVo) {
            _isPlayingPrompt.value = false
            if (isWordMode) playWordAudio(force = true) else playPhonemeSound(force = true)
        }
    }

    private var lastAudioPlayTime: Long = 0L
    private val AUDIO_DEBOUNCE_MS = 500L

    /**
     * Plays the target example-word audio (e.g. audio/words/word_mouse.mp3) as the model
     * utterance. Falls back to the pure phoneme sound in legacy (letter-sound) mode.
     * Includes debouncing guard against rapid repeated taps.
     */
    fun playWordAudio(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastAudioPlayTime < AUDIO_DEBOUNCE_MS || _isPlayingPhoneme.value)) {
            return
        }
        lastAudioPlayTime = now

        val target = _targetWord.value
        if (target == null) {
            playPhonemeSound(force = true)
            return
        }
        if (_state.value is SayItState.Listening) {
            autoStopJob?.cancel()
            voskRecognizer.stopListening()
            _state.value = SayItState.Idle
            _audioAmplitude.value = 0f
        }
        audioPlayer.stop()
        _isPlayingPrompt.value = false
        val path = audioResolver.getWordPath(target)
        _isPlayingPhoneme.value = true
        audioPlayer.playAssetAudio(path) {
            _isPlayingPhoneme.value = false
        }
    }

    fun playPhonemeSound(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastAudioPlayTime < AUDIO_DEBOUNCE_MS || _isPlayingPhoneme.value)) {
            return
        }
        lastAudioPlayTime = now

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
        // Word mode: target = the example word to say (e.g. "mouse"). Legacy mode: the
        // letter sound. Grammar is scoped to the accepted variants + generic decoy words.
        val targetWord = _targetWord.value
        val target = targetWord ?: _phoneme.value?.letter?.lowercase() ?: "m"
        val acceptedList = if (targetWord != null) {
            speechValidator.getAcceptedWordVariants(targetWord)
        } else {
            speechValidator.getAcceptedVariants(target)
        }
        voskRecognizer.setGrammar((acceptedList + listOf("cat", "dog", "sun", "ball", "yes", "no")).distinct())

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
                    val isCorrect = if (targetWord != null) {
                        speechValidator.validateWord(transcript, targetWord)
                    } else {
                        speechValidator.validate(transcript, target)
                    }
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
        val targetWord = _targetWord.value
        val targetLetter = _phoneme.value?.letter ?: "m"
        val isCorrect = if (targetWord != null) {
            speechValidator.validateWord(transcript, targetWord)
        } else {
            speechValidator.validate(transcript, targetLetter)
        }
        val profileId = sessionManager.activeProfileId.value ?: 1L
        val phonemeId = _phoneme.value?.id ?: 1

        _attempts.value = _attempts.value + isCorrect

        viewModelScope.launch {
            sayItAttemptRepository.saveAttempt(profileId, phonemeId, isCorrect)
        }

        if (isCorrect) {
            _state.value = SayItState.Correct(transcript.ifBlank { targetWord ?: targetLetter })
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
        evaluateSpeech(_targetWord.value ?: _phoneme.value?.letter ?: "m")
    }

    override fun onCleared() {
        super.onCleared()
        autoStopJob?.cancel()
        voskRecognizer.stopListening()
        audioPlayer.stop()
    }
}
