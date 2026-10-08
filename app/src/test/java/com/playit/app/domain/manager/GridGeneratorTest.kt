package com.playit.app.domain.manager

import com.playit.app.domain.model.Phoneme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    private val allCurriculumLetters = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "z"
    )

    @Test
    fun distractors_neverShareTheTargetSound() {
        val sameSoundGroup = setOf("c", "k", "q")
        for (letter in allCurriculumLetters) {
            repeat(200) {
                val grid = gridGenerator.generate5ItemGrid(letter)
                val distractors = grid.filter { !it.isCorrect }
                for (d in distractors) {
                    assertFalse(
                        "Distractor ${d.phonemeLetter} shares sound with target $letter",
                        if (letter in sameSoundGroup) d.phonemeLetter in sameSoundGroup else d.phonemeLetter == letter
                    )
                }
            }
        }
    }

    @Test
    fun distractors_neverFromSpecialBanks() {
        val specialBanks = setOf("x", "ng", "ñ")
        for (letter in allCurriculumLetters) {
            repeat(200) {
                val grid = gridGenerator.generate5ItemGrid(letter)
                val distractors = grid.filter { !it.isCorrect }
                for (d in distractors) {
                    assertFalse(
                        "Distractor ${d.phonemeLetter} comes from special bank for target $letter",
                        d.phonemeLetter in specialBanks
                    )
                }
            }
        }
    }

    @Test
    fun distractorLettersFor_k_excludesSameSoundGroup() {
        val result = gridGenerator.distractorLettersFor("k")
        val excluded = setOf("c", "k", "q", "x", "ng", "ñ")
        for (ex in excluded) {
            assertFalse("Expected $ex to be excluded from distractorLettersFor('k')", result.contains(ex))
        }
        assertTrue("Expected 'm' to be in distractorLettersFor('k')", result.contains("m"))
    }

    @Test
    fun up_usesItsOwnPicture() {
        repeat(200) {
            val grid = gridGenerator.generate5ItemGrid("u")
            val upItems = grid.filter { it.word == "Up" }
            for (item in upItems) {
                assertEquals("images/pictures/picture_up.png", item.imagePath)
            }
        }
    }
}
