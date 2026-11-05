package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Harder chapters and replays show fewer words under the Find It pictures (adviser and user, 2026-11-05). */
class WordLabelPolicyTest {
    private val clear = listOf("mouse", "map", "sun", "apple", "ball").mapIndexed { i, w ->
        FindItPictureItem("id$i", "m", w, "images/pictures/picture_$w.png", isCorrect = i < 3)
    }

    @Test fun chapterOne_everyWord() {
        assertTrue(WordLabelPolicy.apply(clear, chapter = 1, isReplay = false).all { it.showWord })
    }

    @Test fun chapterTwo_exactlyTwoWords_differentEachRound() {
        val rounds = (1..20).map { seed ->
            WordLabelPolicy.apply(clear, chapter = 2, isReplay = false, random = Random(seed))
                .filter { it.showWord }.map { it.id }.toSet()
        }
        rounds.forEach { assertEquals(2, it.size) }
        assertTrue("picked at random", rounds.toSet().size > 1)
    }

    @Test fun chapterThreeOn_andReplays_noWords() {
        (3..7).forEach { ch -> assertTrue(WordLabelPolicy.apply(clear, ch, isReplay = false).none { it.showWord }) }
        assertTrue(WordLabelPolicy.apply(clear, chapter = 1, isReplay = true).none { it.showWord })
    }

    @Test fun aPictureAChildWouldMisname_keepsItsWord() {
        val insect = FindItPictureItem("i0", "i", "Insect", "images/pictures/picture_insect.png", isCorrect = true)
        val grid = WordLabelPolicy.apply(clear + insect, chapter = 5, isReplay = false)
        assertEquals(listOf("Insect"), grid.filter { it.showWord }.map { it.word })
    }

    @Test fun everyPictureThatKeepsItsWord_exists() {
        val pictures = java.io.File("src/main/assets/images/pictures")
        WordLabelPolicy.keepsWord.forEach { path ->
            assertTrue(path, java.io.File(pictures, path.removePrefix("images/pictures/")).exists())
        }
    }
}
