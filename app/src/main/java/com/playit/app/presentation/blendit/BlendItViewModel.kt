package com.playit.app.presentation.blendit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.BlendItWordSelector
import com.playit.app.domain.manager.HeartManager
import com.playit.app.domain.model.BlendItAttempt
import com.playit.app.domain.model.BlendItWord
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BlendItUiState {
    object Idle : BlendItUiState()
    object WordCorrect : BlendItUiState()
    data class WordIncorrect(val heartsLeft: Int) : BlendItUiState()
    object HeartDepleted : BlendItUiState() // Triggers standard 3-heart restart dialog
    object SessionComplete : BlendItUiState()
}

@HiltViewModel
class BlendItViewModel @Inject constructor(
    private val blendItWordRepository: BlendItWordRepository,
    private val blendItAttemptRepository: BlendItAttemptRepository,
    private val blendItWordSelector: BlendItWordSelector,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupIdArg: String? = savedStateHandle["groupId"]
    val groupId: Int = groupIdArg?.toIntOrNull() ?: 1

    private val _words = MutableStateFlow<List<BlendItWord>>(emptyList())
    val words: StateFlow<List<BlendItWord>> = _words.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    val currentWord: StateFlow<BlendItWord?> = _currentWordIndex.let { indexFlow ->
        MutableStateFlow<BlendItWord?>(null).apply {
            viewModelScope.launch {
                indexFlow.collect { index ->
                    value = _words.value.getOrNull(index)
                }
            }
        }
    }

    val heartManager = HeartManager()

    private val _hearts = MutableStateFlow(heartManager.currentHearts)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _totalHeartsLost = MutableStateFlow(0)
    val totalHeartsLost: StateFlow<Int> = _totalHeartsLost.asStateFlow()

    private val _placedTiles = MutableStateFlow<List<Char>>(emptyList())
    val placedTiles: StateFlow<List<Char>> = _placedTiles.asStateFlow()

    private val _tileBank = MutableStateFlow<List<Char>>(emptyList())
    val tileBank: StateFlow<List<Char>> = _tileBank.asStateFlow()

    private val _wrongAttemptsForCurrentWord = MutableStateFlow(0)
    val wrongAttemptsForCurrentWord: StateFlow<Int> = _wrongAttemptsForCurrentWord.asStateFlow()

    private val _isHintModalVisible = MutableStateFlow(false)
    val isHintModalVisible: StateFlow<Boolean> = _isHintModalVisible.asStateFlow()

    private val _uiState = MutableStateFlow<BlendItUiState>(BlendItUiState.Idle)
    val uiState: StateFlow<BlendItUiState> = _uiState.asStateFlow()

    init {
        loadSessionWords()
        triggerScreenIntroIfNeeded()
    }

    private fun triggerScreenIntroIfNeeded() {
        if (sessionManager.shouldPlayScreenIntro("blendit")) {
            val introVo = audioResolver.getVoPath(VoContext.BLENDIT_INTRO_01)
            audioPlayer.playAssetAudio(introVo)
        }
    }

    private fun loadSessionWords() {
        viewModelScope.launch {
            blendItWordRepository.getWordsForGroup(groupId).collect { availableWords ->
                val selected = blendItWordSelector.selectWordsForSession(groupId, availableWords)
                _words.value = selected.ifEmpty {
                    listOf(
                        BlendItWord(1, 1, "SAM", "S-A-M", "audio/words/word_sam.mp3", "images/pictures/blendword_sam.png"),
                        BlendItWord(2, 1, "SIS", "S-I-S", "audio/words/word_sis.mp3", "images/pictures/blendword_sis.png"),
                        BlendItWord(3, 1, "AIM", "A-I-M", "audio/words/word_aim.mp3", "images/pictures/blendword_aim.png")
                    )
                }
                setupWordAtIndex(0)
            }
        }
    }

    private fun setupWordAtIndex(index: Int) {
        val wordObj = _words.value.getOrNull(index) ?: return
        _currentWordIndex.value = index
        _placedTiles.value = emptyList()
        _wrongAttemptsForCurrentWord.value = 0
        _isHintModalVisible.value = false
        _uiState.value = BlendItUiState.Idle

        val letters = wordObj.word.toCharArray().toList().shuffled()
        _tileBank.value = letters

        // Play current target word audio
        playTargetWordAudio()
    }

    fun playTargetWordAudio() {
        val targetObj = _words.value.getOrNull(_currentWordIndex.value) ?: return
        val path = audioResolver.getWordPath(targetObj.word)
        audioPlayer.playAssetAudio(path)
    }

    fun placeTile(letter: Char) {
        val currentBank = _tileBank.value.toMutableList()
        val index = currentBank.indexOf(letter)
        if (index != -1) {
            currentBank.removeAt(index)
            _tileBank.value = currentBank
            _placedTiles.value = _placedTiles.value + letter
        }
    }

    fun removeTile(index: Int) {
        val currentPlaced = _placedTiles.value.toMutableList()
        if (index in currentPlaced.indices) {
            val removedChar = currentPlaced.removeAt(index)
            _placedTiles.value = currentPlaced
            _tileBank.value = _tileBank.value + removedChar
        }
    }

    fun submitWord() {
        val targetWordObj = _words.value.getOrNull(_currentWordIndex.value) ?: return
        val targetWord = targetWordObj.word
        val constructedWord = _placedTiles.value.joinToString("")
        val isCorrect = constructedWord.equals(targetWord, ignoreCase = true)
        val profileId = sessionManager.activeProfileId.value ?: 1L

        viewModelScope.launch {
            blendItAttemptRepository.saveAttempt(
                BlendItAttempt(
                    profileId = profileId,
                    groupId = groupId,
                    wordId = targetWordObj.wordId,
                    isCorrect = isCorrect
                )
            )
        }

        if (isCorrect) {
            _uiState.value = BlendItUiState.WordCorrect
            val sfx = audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME)
            val vo = audioResolver.getRotatingCorrectVo()
            audioPlayer.playSequence(listOf(sfx, vo))

            viewModelScope.launch {
                kotlinx.coroutines.delay(1200)
                if (_currentWordIndex.value + 1 < _words.value.size) {
                    setupWordAtIndex(_currentWordIndex.value + 1)
                } else {
                    _uiState.value = BlendItUiState.SessionComplete
                }
            }
        } else {
            val isGameOver = heartManager.deductHeart()
            _hearts.value = heartManager.currentHearts
            _totalHeartsLost.value = heartManager.heartsLost
            _wrongAttemptsForCurrentWord.value += 1

            val sfxBuzz = audioResolver.getSfxPath(SfxEvent.BLENDIT_BUZZ)
            val sfxWhoosh = audioResolver.getSfxPath(SfxEvent.HEART_LOSS_WHOOSH)
            val voEncourage = audioResolver.getRotatingEncourageVo()
            audioPlayer.playSequence(listOf(sfxBuzz, sfxWhoosh, voEncourage))

            if (isGameOver) {
                _uiState.value = BlendItUiState.HeartDepleted
            } else {
                _uiState.value = BlendItUiState.WordIncorrect(heartManager.currentHearts)
            }
        }
    }

    fun openHintModal() {
        _isHintModalVisible.value = true
        val hintVo = audioResolver.getRotatingHintVo()
        audioPlayer.playAssetAudio(hintVo)
    }

    fun closeHintModal() {
        _isHintModalVisible.value = false
    }

    fun restartSession() {
        heartManager.reset()
        _hearts.value = heartManager.currentHearts
        _totalHeartsLost.value = 0
        setupWordAtIndex(0)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
