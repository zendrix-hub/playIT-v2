package com.playit.app.domain.manager

import com.playit.app.domain.model.Phoneme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GridGeneratorTest {

    private lateinit var gridGenerator: GridGenerator

    @Before
    fun setUp() {
        gridGenerator = GridGenerator()
    }

    @Test
    fun letterOne_usesFallbackDistractorsWhenNotEnoughMasteredLetters() {
        val letterM = Phoneme(1, "m", "audio/phonemes/phoneme_m.mp3", "images/pictures/word_mouse.png", "Mouse")
        val availablePhonemes = listOf(letterM)

        val grid = gridGenerator.generateGrid(targetPhonemeId = 1, availablePhonemes = availablePhonemes)

        assertEquals(4, grid.size)
        assertTrue(grid.any { it.id == 1 })
    }

    @Test
    fun standardGrid_containsTargetAndDistractors() {
        val phonemes = listOf(
            Phoneme(1, "m", "audio/phonemes/phoneme_m.mp3", "images/pictures/word_mouse.png", "Mouse"),
            Phoneme(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/word_sun.png", "Sun"),
            Phoneme(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/word_apple.png", "Apple"),
            Phoneme(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/word_iguana.png", "Iguana")
        )

        val grid = gridGenerator.generateGrid(targetPhonemeId = 1, availablePhonemes = phonemes)

        assertEquals(4, grid.size)
        assertTrue(grid.any { it.id == 1 })
    }

    @Test
    fun generate5ItemGrid_returnsExactly5ItemsWith3CorrectAnd2Distractors() {
        val grid5 = gridGenerator.generate5ItemGrid("m")

        assertEquals("Must generate exactly 5 cards", 5, grid5.size)
        val correctCount = grid5.count { it.isCorrect }
        val distractorCount = grid5.count { !it.isCorrect }

        assertEquals("Must have exactly 3 correct target cards", 3, correctCount)
        assertEquals("Must have exactly 2 distractor cards", 2, distractorCount)
    }
}
