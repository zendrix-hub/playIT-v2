package com.playit.app.domain.manager

import com.playit.app.domain.model.Phoneme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GridGenerator @Inject constructor() {

    fun generateGrid(targetPhonemeId: Int, availablePhonemes: List<Phoneme>): List<Phoneme> {
        val target = availablePhonemes.find { it.id == targetPhonemeId }
            ?: return availablePhonemes.take(4)

        // If target is Letter 1 (Letter M) and no other phonemes exist yet, use fallback distractors
        val distractors = availablePhonemes.filter { it.id != targetPhonemeId }
        val gridItems = mutableListOf<Phoneme>()
        gridItems.add(target)

        if (distractors.size >= 3) {
            gridItems.addAll(distractors.shuffled().take(3))
        } else {
            // Fallback distractor items for Letter 1 edge case (01_REQUIREMENTS_SUMMARY.md §5)
            gridItems.addAll(distractors)
            val fallbackCandidates = listOf(
                Phoneme(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/word_sun.png", "Sun"),
                Phoneme(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/word_apple.png", "Apple"),
                Phoneme(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/word_iguana.png", "Iguana")
            )
            for (candidate in fallbackCandidates) {
                if (gridItems.size < 4 && gridItems.none { it.id == candidate.id }) {
                    gridItems.add(candidate)
                }
            }
        }

        return gridItems.shuffled()
    }
}
