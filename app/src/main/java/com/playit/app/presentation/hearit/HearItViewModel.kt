package com.playit.app.presentation.hearit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.HearItSequenceBuilder
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
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
            playModelingSequence()
        }
    }

    fun retry() {
        _loadError.value = false
        loadPhoneme()
    }

    private val _isPlayingPrompt = MutableStateFlow(false)
    val isPlayingPrompt: StateFlow<Boolean> = _isPlayingPrompt.asStateFlow()

    /** Full "I do" sequence (spec §2.1 Table 3): on load and on mascot tap. */
    fun playModelingSequence() {
        audioPlayer.stop()
        _isPlayingPrompt.value = true
        _isPlaying.value = true
        _playCount.value++
        audioPlayer.playSequence(buildSequence(HearItSequenceBuilder.TEMPLATE)) {
            _isPlayingPrompt.value = false
            _isPlaying.value = false
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
        audioPlayer.playSequence(buildSequence(HearItSequenceBuilder.replayTemplate())) {
            _isPlaying.value = false
        }
    }

    private fun buildSequence(template: List<String>): List<String> {
        val letter = _phoneme.value?.letter ?: "m"
        val exampleWord = _phoneme.value?.exampleWord?.trim().orEmpty()
        val keyWordPath = if (exampleWord.isEmpty() || exampleWord.equals("PENDING_SME_REVIEW", ignoreCase = true)) {
            null
        } else {
            audioResolver.getWordPath(exampleWord)
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
        audioPlayer.stop()
    }
}
