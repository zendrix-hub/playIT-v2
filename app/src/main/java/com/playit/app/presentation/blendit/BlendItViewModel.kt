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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.playit.app.presentation.components.IdleTimer
import javax.inject.Inject

sealed class BlendItUiState {
    object Idle : BlendItUiState()
    object WordCorrect : BlendItUiState()
    data class WordIncorrect(val heartsLeft: Int) : BlendItUiState()
    object HeartDepleted : BlendItUiState() // Triggers standard 3-heart restart dialog
    object SessionComplete : BlendItUiState()
}

data class BlendItResult(
    val groupId: Int,
    val heartsLost: Int,
    val wordsCorrect: Int,
    val totalWords: Int
)

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

    val currentWord: StateFlow<BlendItWord?> = combine(_words, _currentWordIndex) { wordsList, index ->
        wordsList.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val heartManager = HeartManager()

    private var wordsSolvedFirstTry: Int = 0
    private var consecutiveCorrectWords: Int = 0

    fun result(): BlendItResult = BlendItResult(
        groupId = groupId,
        heartsLost = heartManager.sessionHeartsLost,
        wordsCorrect = wordsSolvedFirstTry,
        totalWords = _words.value.size
    )

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

    private val _isHintApplied = MutableStateFlow(false)
    val isHintApplied: StateFlow<Boolean> = _isHintApplied.asStateFlow()

    private val _lockedHintCount = MutableStateFlow(0)
    val lockedHintCount: StateFlow<Int> = _lockedHintCount.asStateFlow()

    private var soundOutJob: kotlinx.coroutines.Job? = null

    /**
     * Plays [path] and returns when it ends, but no later than [maxMs] (a clip that never reports its end
     * cannot stall the lesson) and no sooner than [minMs]. A null path just waits [minMs].
     */
    private suspend fun playAndAwait(path: String?, minMs: Long, maxMs: Long) = coroutineScope {
        val minimum = launch { kotlinx.coroutines.delay(minMs) }
        if (path != null) {
            val ended = CompletableDeferred<Unit>()
            audioPlayer.playAssetAudio(path) { ended.complete(Unit) }
            withTimeoutOrNull(maxMs) { ended.await() }
        }
        minimum.join()
    }

    private val _highlightedSlotIndex = MutableStateFlow<Int?>(null)
    val highlightedSlotIndex: StateFlow<Int?> = _highlightedSlotIndex.asStateFlow()

    private val _uiState = MutableStateFlow<BlendItUiState>(BlendItUiState.Idle)
    val uiState: StateFlow<BlendItUiState> = _uiState.asStateFlow()

    private fun loadSessionWords() {
        viewModelScope.launch {
            val availableWords = blendItWordRepository.getWordsForGroup(groupId).first()
            val selected = blendItWordSelector.selectWordsForSession(groupId, availableWords)
            _words.value = selected.ifEmpty {
                listOf(
                    BlendItWord(1, 1, "SAM", "S-A-M", "audio/words/word_sam.mp3", "images/pictures/blendword_sam.png"),
                    BlendItWord(2, 1, "SIS", "S-I-S", "audio/words/word_sis.mp3", "images/pictures/blendword_sis.png"),
                    BlendItWord(3, 1, "AM", "A-M", "audio/words/word_am.mp3", "images/pictures/blendword_am.png")
                )
            }
            setupWordAtIndex(0)
        }
    }

    private val _isPlayingPrompt = MutableStateFlow(false)
    val isPlayingPrompt: StateFlow<Boolean> = _isPlayingPrompt.asStateFlow()

    private val _nextHighlighted = MutableStateFlow(false)
    val nextHighlighted: StateFlow<Boolean> = _nextHighlighted.asStateFlow()

    private val idleTimer = IdleTimer(
        scope = viewModelScope,
        isBusy = { _isPlayingPrompt.value },
        onIdle = { playBlendItIntroAudio() }
    )

    init {
        loadSessionWords()
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

    private fun setupWordAtIndex(index: Int) {
        val wordObj = _words.value.getOrNull(index) ?: return
        soundOutJob?.cancel()
        _currentWordIndex.value = index
        _placedTiles.value = emptyList()
        _wrongAttemptsForCurrentWord.value = 0
        _isHintApplied.value = false
        _lockedHintCount.value = 0
        _highlightedSlotIndex.value = null
        _uiState.value = BlendItUiState.Idle

        val letters = wordObj.word.toCharArray().toList().shuffled()
        _tileBank.value = letters

        // Play prompt first on session start, or word audio directly on subsequent words
        if (index == 0) {
            playIntroThenWordAudio()
        } else {
            playTargetWordAudio()
        }
    }

    fun playIntroThenWordAudio() {
        audioPlayer.stop()
        _isPlayingPrompt.value = true
        val introVo = audioResolver.getVoPath(VoContext.BLENDIT_INTRO_01)
        audioPlayer.playAssetAudio(introVo) {
            _isPlayingPrompt.value = false
            playTargetWordAudio()
        }
    }

    fun playBlendItIntroAudio() {
        playIntroThenWordAudio()
    }

    fun playTargetWordAudio() {
        audioPlayer.stop()
        _isPlayingPrompt.value = false
        val targetObj = _words.value.getOrNull(_currentWordIndex.value) ?: return
        val path = audioResolver.getWordPath(targetObj.word)
        audioPlayer.playAssetAudio(path)
    }

    fun playHintAudio() {
        audioPlayer.stop()
        _isPlayingPrompt.value = false
        val hintVo = audioResolver.getRotatingHintVo()
        audioPlayer.playAssetAudio(hintVo)
    }

    fun placeTile(letter: Char) {
        audioPlayer.stop()
        val currentBank = _tileBank.value.toMutableList()
        val index = currentBank.indexOf(letter)
        if (index != -1) {
            currentBank.removeAt(index)
            _tileBank.value = currentBank
            _placedTiles.value = _placedTiles.value + letter

            val phonemeAudio = audioResolver.getPhonemePath(letter.toString())
            if (phonemeAudio != null) {
                audioPlayer.playAssetAudio(phonemeAudio)
            }
        }
    }

    fun removeTile(index: Int) {
        if (index < _lockedHintCount.value) return
        val currentPlaced = _placedTiles.value.toMutableList()
        if (index in currentPlaced.indices) {
            val removedChar = currentPlaced.removeAt(index)
            _placedTiles.value = currentPlaced
            _tileBank.value = _tileBank.value + removedChar

            val popSfx = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
            audioPlayer.playAssetAudio(popSfx)
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
            if (_wrongAttemptsForCurrentWord.value == 0) {
                wordsSolvedFirstTry++
            }
            consecutiveCorrectWords++
            heartManager.checkRecovery(consecutiveCorrectWords)
            _hearts.value = heartManager.currentHearts

            _uiState.value = BlendItUiState.WordCorrect
            soundOutJob?.cancel()
            soundOutJob = viewModelScope.launch {
                // Sound out each letter and wait for its clip: AudioPlayer stops a clip when the next one
                // starts, and held sounds run over a second (ph_s.wav is 1.19 s). At least 750 ms per tile
                // (user decision 2026-10-10, card 28).
                for (i in targetWord.indices) {
                    _highlightedSlotIndex.value = i
                    playAndAwait(audioResolver.getPhonemePath(targetWord[i].toString()), LETTER_MIN_MS, LETTER_MAX_MS)
                }

                // Blend: every tile lights together for a moment, and stays lit while the whole word plays.
                _highlightedSlotIndex.value = ALL_SLOTS
                kotlinx.coroutines.delay(BLEND_PAUSE_MS)
                playAndAwait(audioResolver.getWordPath(targetWord), 0L, WORD_MAX_MS)
                _highlightedSlotIndex.value = null

                // Celebration chime and VO
                val sfx = audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME)
                val vo = audioResolver.getRotatingCorrectVo()
                audioPlayer.playSequence(listOf(sfx, vo))

                kotlinx.coroutines.delay(1000)
                if (_currentWordIndex.value + 1 < _words.value.size) {
                    setupWordAtIndex(_currentWordIndex.value + 1)
                } else {
                    _uiState.value = BlendItUiState.SessionComplete
                }
            }
        } else {
            consecutiveCorrectWords = 0
            val isGameOver = heartManager.deductHeart()
            _hearts.value = heartManager.currentHearts
            _totalHeartsLost.value = heartManager.sessionHeartsLost
            _wrongAttemptsForCurrentWord.value += 1

            val sfxPop = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
            val sfxWhoosh = audioResolver.getSfxPath(SfxEvent.HEART_LOSS_WHOOSH)
            val voEncourage = audioResolver.getRotatingEncourageVo()
            audioPlayer.playSequence(listOf(sfxPop, sfxWhoosh, voEncourage))

            if (isGameOver) {
                _uiState.value = BlendItUiState.HeartDepleted
            } else {
                _uiState.value = BlendItUiState.WordIncorrect(heartManager.currentHearts)
            }
        }
    }

    fun applyHint() {
        val targetWord = _words.value.getOrNull(_currentWordIndex.value)?.word ?: return
        val hintIndex = _lockedHintCount.value
        if (hintIndex >= targetWord.length) return
        
        val correctChar = targetWord[hintIndex]
        
        val bank = _tileBank.value.toMutableList()
        val bankIndex = bank.indexOfFirst { it.equals(correctChar, ignoreCase = true) }
        
        if (bankIndex != -1) {
            bank.removeAt(bankIndex)
        } else {
            val placed = _placedTiles.value.toMutableList()
            val placedIdx = placed.indexOfLast { it.equals(correctChar, ignoreCase = true) && placed.indexOf(it) >= hintIndex }
            if (placedIdx != -1) {
                placed.removeAt(placedIdx)
                _placedTiles.value = placed
            }
        }
        
        val newPlaced = _placedTiles.value.toMutableList()
        if (newPlaced.size > hintIndex) {
            val displacedChar = newPlaced.removeAt(hintIndex)
            newPlaced.add(hintIndex, correctChar)
            if (!displacedChar.equals(correctChar, ignoreCase = true)) {
                _tileBank.value = _tileBank.value + displacedChar
            }
        } else {
            newPlaced.add(hintIndex, correctChar)
        }
        
        _placedTiles.value = newPlaced
        if (bankIndex != -1) {
            _tileBank.value = bank
        }
        
        _lockedHintCount.value += 1
        val hintSfx = audioResolver.getSfxPath(SfxEvent.HEART_RECOVERY_SPARKLE)
        val hintVo = audioResolver.getRotatingHintVo()
        audioPlayer.playSequence(listOf(hintSfx, hintVo))
    }

    fun restartSession() {
        wordsSolvedFirstTry = 0
        consecutiveCorrectWords = 0
        heartManager.resetForRestart()
        _hearts.value = heartManager.currentHearts
        _totalHeartsLost.value = heartManager.sessionHeartsLost
        setupWordAtIndex(0)
    }

    override fun onCleared() {
        super.onCleared()
        idleTimer.stop()
        audioPlayer.stop()
    }

    companion object {
        /** [highlightedSlotIndex] value that lights every tile: the blend moment before the whole word. */
        const val ALL_SLOTS = -1
        internal const val LETTER_MIN_MS = 750L
        internal const val LETTER_MAX_MS = 2000L
        internal const val BLEND_PAUSE_MS = 500L
        internal const val WORD_MAX_MS = 2500L
    }
}
