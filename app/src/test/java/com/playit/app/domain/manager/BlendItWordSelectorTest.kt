package com.playit.app.domain.manager

import com.playit.app.domain.model.BlendItWord
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BlendItWordSelectorTest {

    private lateinit var selector: BlendItWordSelector

    @Before
    fun setUp() {
        selector = BlendItWordSelector()
    }

    @Test
    fun groupOne_restrictedToThreeWords() {
        val groupOneWords = listOf(
            BlendItWord(wordId = 1, groupId = 1, word = "SAM", wordPattern = "CVC", audioPath = "audio/words/sam.mp3", imagePath = "images/words/sam.png"),
            BlendItWord(wordId = 2, groupId = 1, word = "SIS", wordPattern = "CVC", audioPath = "audio/words/sis.mp3", imagePath = "images/words/sis.png"),
            BlendItWord(wordId = 3, groupId = 1, word = "AIM", wordPattern = "VC", audioPath = "audio/words/aim.mp3", imagePath = "images/words/aim.png"),
            BlendItWord(wordId = 4, groupId = 1, word = "EXTRA", wordPattern = "VCCV", audioPath = "audio/words/extra.mp3", imagePath = "images/words/extra.png")
        )

        val selected = selector.selectWordsForSession(groupId = 1, availableWords = groupOneWords)
        assertEquals(3, selected.size)
    }

    @Test
    fun standardGroup_takesUpToFiveWords() {
        val groupTwoWords = (1..6).map {
            BlendItWord(wordId = it, groupId = 2, word = "WORD$it", wordPattern = "CVC", audioPath = "audio/words/w.mp3", imagePath = "images/words/w.png")
        }

        val selected = selector.selectWordsForSession(groupId = 2, availableWords = groupTwoWords)
        assertEquals(5, selected.size)
    }
}
