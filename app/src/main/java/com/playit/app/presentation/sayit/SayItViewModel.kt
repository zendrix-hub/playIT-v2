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
import com.playit.app.domain.manager.TutorAction
import com.playit.app.domain.manager.TutorPolicy
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.model.SpeechErrorType
import com.playit.app.domain.model.SpeechJudgement
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SayItState {
    object Idle : SayItState()
    object Listening : SayItState()
    data class Correct(val transcript: String) : SayItState()
    data class Incorrect(
        val transcript: String,
        val errorType: SpeechErrorType = SpeechErrorType.OTHER_WORD
    ) : SayItState()
}

data class HeardAttempt(
    val transcript: String,
    val errorType: SpeechErrorType,
    val isCorrect: Boolean,
    val attempt: Int
)

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

    /** True once the recognizer returned any speech in the current listening window. */
    private val _heardSpeech = MutableStateFlow(false)

    val micStatus: StateFlow<MicStatus> = combine(_state, _heardSpeech) { s, h -> micStatusFor(s, h) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MicStatus.IDLE)

    val heartManager = HeartManager()

    private val _hearts = MutableStateFlow(heartManager.currentHearts)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val tutorPolicy = TutorPolicy()
    private var attemptNumber = 0

    private val _tutorAction = MutableStateFlow<TutorAction?>(null)
    val tutorAction: StateFlow<TutorAction?> = _tutorAction.asStateFlow()

    private val _lastHeard = MutableStateFlow<HeardAttempt?>(null)
    val lastHeard: StateFlow<HeardAttempt?> = _lastHeard.asStateFlow()

    /** True after praise or lead-and-move-on: no more scored attempts until the phoneme reloads. */
    private val _canContinue = MutableStateFlow(false)
    val canContinue: StateFlow<Boolean> = _canContinue.asStateFlow()

    /** "Watch my lips": the mouth-shape cue shows from the second miss on (spec Table 7 attempt 2). */
    private val _showMouthCue = MutableStateFlow(false)
    val showMouthCue: StateFlow<Boolean> = _showMouthCue.asStateFlow()

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

    private fun loadPhoneme() {
        val id = phonemeIdArg?.toIntOrNull() ?: 1
        viewModelScope.launch {
            attemptNumber = 0
            _tutorAction.value = null
            _canContinue.value = false
            _lastHeard.value = null
            _showMouthCue.value = false
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

    init {
        loadPhoneme()
    }

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
        val path = audioResolver.getKeyWordPath(target)
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
        if (_canContinue.value) return

        // Instantly silence any voiceover or phoneme audio so it never bleeds into the mic
        audioPlayer.stop()
        _isPlayingPhoneme.value = false

        autoStopJob?.cancel()
        val targetWord = _targetWord.value
        val letter = _phoneme.value?.letter?.lowercase() ?: "m"
        voskRecognizer.setGrammar(speechValidator.grammarFor(letter, targetWord))

        _heardSpeech.value = false
        _state.value = SayItState.Listening
        _isNoisyEnvironment.value = false

        // Auto-timeout after 3.8s maximum window if child hasn't finished speaking
        autoStopJob = viewModelScope.launch {
            delay(3800L)
            if (_state.value is SayItState.Listening) {
                stopListening()
            }
        }

        // Stop early only on a correct result. A wrong partial can be the start of the
        // target word ("ma" in "mouse", "a" in "apple"), so wrong answers are judged only
        // on a final result or at the timeout.
        voskRecognizer.startListening(
            onResult = { transcript, isFinal ->
                if (transcript.isNotBlank() && _state.value is SayItState.Listening) {
                    _heardSpeech.value = true
                    val judgement = judgeTranscript(transcript, targetWord, letter)
                    if (judgement.isCorrect || isFinal) {
                        autoStopJob?.cancel()
                        voskRecognizer.stopListening()
                        evaluateSpeech(transcript)
                    }
                }
            }
        )
    }

    private fun judgeTranscript(transcript: String, targetWord: String?, letter: String): SpeechJudgement {
        return when {
            targetWord != null -> speechValidator.judgeWord(transcript, targetWord, letter)
            letter == "ng" || letter == "ñ" -> {
                val ok = speechValidator.validate(transcript, letter)
                SpeechJudgement(
                    isCorrect = ok,
                    errorType = if (ok) SpeechErrorType.NONE else SpeechErrorType.OTHER_WORD,
                    heard = transcript
                )
            }
            else -> speechValidator.judgeSound(transcript, letter, null)
        }
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
        attemptNumber++
        autoStopJob?.cancel()
        _audioAmplitude.value = 0f
        val targetWord = _targetWord.value
        val letter = _phoneme.value?.letter?.lowercase() ?: "m"
        val judgement = judgeTranscript(transcript, targetWord, letter)
        val isCorrect = judgement.isCorrect
        _lastHeard.value = HeardAttempt(
            transcript = transcript,
            errorType = judgement.errorType,
            isCorrect = isCorrect,
            attempt = attemptNumber
        )
        val profileId = sessionManager.activeProfileId.value ?: 1L
        val phonemeId = _phoneme.value?.id ?: 1

        val action = tutorPolicy.next(attemptNumber, judgement)
        _tutorAction.value = action

        _attempts.value = _attempts.value + isCorrect

        viewModelScope.launch {
            sayItAttemptRepository.saveAttempt(profileId, phonemeId, isCorrect)
        }

        if (isCorrect) {
            _state.value = SayItState.Correct(transcript.ifBlank { targetWord ?: letter })
        } else {
            _state.value = SayItState.Incorrect(
                transcript = transcript.ifBlank { "Try again!" },
                errorType = judgement.errorType
            )
        }

        val model = if (targetWord != null) {
            audioResolver.getKeyWordPath(targetWord)
        } else {
            audioResolver.getPhonemePath(letter)
        }

        when (action) {
            is TutorAction.Praise -> {
                val sfx = audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME)
                val vo = audioResolver.getRotatingCorrectVo()
                audioPlayer.playSequence(listOf(sfx, vo))
                _canContinue.value = true
            }
            is TutorAction.Correct -> {
                if (action.supportLevel >= 2) _showMouthCue.value = true
                val pop = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
                val yourTurn = audioResolver.getTutorPath("car_your_turn")
                val sequence = if (action.supportLevel == 1) {
                    listOf(pop) + levelOneCorrection(action.errorType, letter, targetWord) + yourTurn
                } else {
                    listOf(pop, audioResolver.getTutorPath("car_watch_my_lips"), model, yourTurn)
                }
                audioPlayer.playSequence(sequence)
            }
            is TutorAction.LeadAndMoveOn -> {
                _showMouthCue.value = true
                // TODO(FR-NEW-REC): mark the letter NEEDS_PRACTICE and queue a recall check
                audioPlayer.playSequence(
                    listOf(
                        audioResolver.getTutorPath("car_lets_say_together"),
                        model,
                        audioResolver.getTutorPath("fb_try_later")
                    )
                )
                _canContinue.value = true
            }
        }
    }

    /**
     * First-miss correction between the pop and "Your turn!" (spec §3.2; tools/audio/tutor_script.py
     * COMPOSE corr_letter_name, corr_added_vowel, remodel). The key word is left out in
     * letter-sound mode (ng, ñ).
     */
    private fun levelOneCorrection(errorType: SpeechErrorType, letter: String, word: String?): List<String> {
        val sound = audioResolver.getPhonemePath(letter)
        val keyWord = listOfNotNull(word?.let { audioResolver.getKeyWordPath(it) })
        return when (errorType) {
            SpeechErrorType.LETTER_NAME -> listOf(
                audioResolver.getTutorPath("fb_letter_name"),
                audioResolver.getTutorPath("fb_its_sound_is"),
                sound
            ) + keyWord
            SpeechErrorType.ADDED_VOWEL -> listOf(
                audioResolver.getTutorPath("fb_almost_just"),
                sound,
                audioResolver.getTutorPath("fb_no_ah")
            ) + keyWord
            else -> listOf(audioResolver.getTutorPath("fb_listen"), sound) + keyWord
        }
    }

    /**
     * The screen went to the background (MainActivity.onStop stops Vosk) or left composition.
     * A listening attempt is dropped unjudged, so the mic is never stuck in Listening.
     */
    fun onScreenHidden() {
        autoStopJob?.cancel()
        if (_state.value is SayItState.Listening) {
            voskRecognizer.stopListening()
            _state.value = SayItState.Idle
            _audioAmplitude.value = 0f
        }
        _heardSpeech.value = false
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
