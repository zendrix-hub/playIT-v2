package com.playit.app.presentation.findit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.manager.GridGenerator
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
}

@HiltViewModel
class FindItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val findItAttemptRepository: FindItAttemptRepository,
    private val gridGenerator: GridGenerator,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phonemeIdArg: String? = savedStateHandle["phonemeId"]

    private val _targetPhoneme = MutableStateFlow<Phoneme?>(null)
    val targetPhoneme: StateFlow<Phoneme?> = _targetPhoneme.asStateFlow()

    private val _gridItems = MutableStateFlow<List<Phoneme>>(emptyList())
    val gridItems: StateFlow<List<Phoneme>> = _gridItems.asStateFlow()

    private val _state = MutableStateFlow<FindItState>(FindItState.Idle)
    val state: StateFlow<FindItState> = _state.asStateFlow()

    init {
        loadGrid()
    }

    private fun loadGrid() {
        val id = phonemeIdArg?.toIntOrNull() ?: 1
        viewModelScope.launch {
            phonemeRepository.getAllPhonemes().collect { allPhonemes ->
                val target = allPhonemes.find { it.id == id } ?: Phoneme(
                    id = 1,
                    letter = "m",
                    audioPath = "audio/phonemes/phoneme_m.mp3",
                    imagePath = "images/pictures/word_mouse.png",
                    exampleWord = "Mouse"
                )
                _targetPhoneme.value = target
                _gridItems.value = gridGenerator.generateGrid(id, allPhonemes)
            }
        }
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
            _state.value = FindItState.Correct(selected)
        } else {
            _state.value = FindItState.Incorrect(selected)
        }
    }
}
