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
}
