package com.playit.app.presentation.hearit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.CaptionText
import com.playit.app.domain.manager.HearItSequenceBuilder
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.domain.model.articulationFor
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.presentation.components.IdleTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HearItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playCount = MutableStateFlow(0)
    val playCount: StateFlow<Int> = _playCount.asStateFlow()

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    private var firstPlaybackDone = false

    private val _nextHighlighted = MutableStateFlow(false)
    val nextHighlighted: StateFlow<Boolean> = _nextHighlighted.asStateFlow()

    /** Caption of the clip playing now (NFR-ACC-01); null when nothing is playing. */
    private val _caption = MutableStateFlow<String?>(null)
    val caption: StateFlow<String?> = _caption.asStateFlow()

    /** Mouth-shape group of the current letter, for the articulation cue. */
    val articulation: ArticulationGroup
        get() = articulationFor(_phoneme.value?.letter ?: "m")

    private fun onSequenceItem(@Suppress("UNUSED_PARAMETER") index: Int, path: String) {
        val letter = _phoneme.value?.letter ?: "m"
        val word = _phoneme.value?.exampleWord?.trim()?.lowercase().orEmpty()
        // Pauses keep the last caption on screen.
        CaptionText.forClip(path, letter, word)?.let { _caption.value = it }
    }

    private val idleTimer = IdleTimer(
        scope = viewModelScope,
        isBusy = { _isPlaying.value || _isPlayingPrompt.value }
    ) {
        if (_nextHighlighted.value) {
            audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_hearit_next"))
        } else {
            playModelingSequence()
        }
    }

    fun onScreenVisible() {
        idleTimer.start()
    }

    fun onScreenHidden() {
        idleTimer.stop()
    }

    fun onUserInteraction() {
        idleTimer.touch()
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
            playModelingSequence()
        }
    }

    fun retry() {
        _loadError.value = false
        loadPhoneme()
    }

    private val _isPlayingPrompt = MutableStateFlow(false)
    val isPlayingPrompt: StateFlow<Boolean> = _isPlayingPrompt.asStateFlow()

    init {
        loadPhoneme()
    }

    /** Full "I do" sequence (spec §2.1 Table 3): on load and on mascot tap. */
    fun playModelingSequence() {
        audioPlayer.stop()
        _isPlayingPrompt.value = true
        _isPlaying.value = true
        _playCount.value++
        audioPlayer.playSequence(buildSequence(HearItSequenceBuilder.TEMPLATE), ::onSequenceItem) {
            _caption.value = null
            _isPlayingPrompt.value = false
            _isPlaying.value = false
            if (!firstPlaybackDone) {
                firstPlaybackDone = true
                _nextHighlighted.value = true
                audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_hearit_next"))
            }
        }
    }

    fun playHearItIntroAudio() {
        playModelingSequence()
    }

    /** Ear button and letter card tap: replays steps 3 to 6 of the sequence. */
    fun playPhonemeSound() {
        audioPlayer.stop()
        _isPlayingPrompt.value = false
        _isPlaying.value = true
        _playCount.value++
        audioPlayer.playSequence(buildSequence(HearItSequenceBuilder.replayTemplate()), ::onSequenceItem) {
            _caption.value = null
            _isPlaying.value = false
            if (!firstPlaybackDone) {
                firstPlaybackDone = true
                _nextHighlighted.value = true
                audioPlayer.playAssetAudio(audioResolver.getUiPath("ui_hearit_next"))
            }
        }
    }

    private fun buildSequence(template: List<String>): List<String> {
        val letter = _phoneme.value?.letter ?: "m"
        val exampleWord = _phoneme.value?.exampleWord?.trim().orEmpty()
        val keyWordPath = if (exampleWord.isEmpty() || exampleWord.equals("PENDING_SME_REVIEW", ignoreCase = true)) {
            null
        } else {
            audioResolver.getKeyWordPath(exampleWord)
        }
        return HearItSequenceBuilder.build(
            template,
            audioResolver.getPhonemePath(letter),
            keyWordPath,
            audioResolver::getTutorPath
        )
    }

    override fun onCleared() {
        super.onCleared()
        idleTimer.stop()
        audioPlayer.stop()
    }
}
