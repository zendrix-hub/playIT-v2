package com.playit.app.presentation.hearit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
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
            playPhonemeSound()
        }
    }

    fun playPhonemeSound() {
        val letter = _phoneme.value?.letter ?: "m"
        val path = audioResolver.getPhonemePath(letter) ?: _phoneme.value?.audioPath ?: "audio/phonemes/phoneme_m.mp3"
        _isPlaying.value = true
        audioPlayer.playAssetAudio(path) {
            _isPlaying.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
