package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Harder chapters and replays show fewer words under the Find It pictures (adviser and user, 2026-11-05); Chapter 1 shows them all (user, 2026-11-07). */
class WordLabelPolicyTest {
    private val clear = listOf("mouse", "map", "sun", "apple", "ball").mapIndexed { i, w ->
        FindItPictureItem("id$i", "m", w, "images/pictures/picture_$w.png", isCorrect = i < 3)
    }

    /**
     * User decision 2026-11-07: Chapter 1 is the easy chapter, so every picture shows its word, on all four letters
     * (m, s, a, i) and on replays. Only Chapter 1 does (Chapter 2 on is unchanged, below).
     */
    @Test fun chapterOne_everyWord_onEveryLetterAndEveryReplay() {
        (0..3).forEach { position ->
            listOf(false, true).forEach { replay ->
                (1..20).forEach { seed ->
                    val grid = WordLabelPolicy.apply(clear, chapter = 1, positionInChapter = position, isReplay = replay, random = Random(seed))
                    assertTrue("letter ${position + 1}, replay $replay", grid.all { it.showWord })
                }
            }
        }
    }

    @Test fun chapterTwo_exactlyTwoWords_differentEachRound() {
        val rounds = (1..20).map { seed ->
            WordLabelPolicy.apply(clear, chapter = 2, positionInChapter = 0, isReplay = false, random = Random(seed))
                .filter { it.showWord }.map { it.id }.toSet()
        }
        rounds.forEach { assertEquals(2, it.size) }
        assertTrue("picked at random", rounds.toSet().size > 1)
    }

    @Test fun chapterThreeOn_andReplaysFromChapterTwo_noWords() {
        (3..7).forEach { ch ->
            (0..3).forEach { pos -> assertTrue(WordLabelPolicy.apply(clear, ch, pos, isReplay = false).none { it.showWord }) }
        }
        (2..7).forEach { ch ->
            (0..3).forEach { pos -> assertTrue(WordLabelPolicy.apply(clear, ch, pos, isReplay = true).none { it.showWord }) }
        }
    }

    @Test fun aPictureAChildWouldMisname_keepsItsWord() {
        val insect = FindItPictureItem("i0", "i", "Insect", "images/pictures/picture_insect.png", isCorrect = true)
        val grid = WordLabelPolicy.apply(clear + insect, chapter = 5, positionInChapter = 0, isReplay = false)
        assertEquals(listOf("Insect"), grid.filter { it.showWord }.map { it.word })
    }

    @Test fun everyPictureThatKeepsItsWord_exists() {
        val pictures = java.io.File("src/main/assets/images/pictures")
        WordLabelPolicy.keepsWord.forEach { path ->
            assertTrue(path, java.io.File(pictures, path.removePrefix("images/pictures/")).exists())
        }
    }
}
