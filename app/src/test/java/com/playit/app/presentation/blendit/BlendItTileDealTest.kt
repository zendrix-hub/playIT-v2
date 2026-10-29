package com.playit.app.presentation.blendit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.random.Random

/** Blend It never deals a word's tiles in the answer's own order (card 31f). */
class BlendItTileDealTest {

    @Test fun deal_isNeverTheAnswer_andAlwaysTheSameLetters() {
        val random = Random(31)
        for (word in listOf("SAM", "SIS", "AM", "TUB", "YAM", "ZIP", "QUIZ", "MOM")) {
            repeat(200) {
                val deal = dealTiles(word, random)
                assertNotEquals("$word was dealt solved", word.toList(), deal)
                assertEquals("$word lost or gained a letter", word.toList().sorted(), deal.sorted())
            }
        }
    }

    @Test fun deal_ofOneRepeatedLetter_keepsItsOnlyOrder() {
        assertEquals("MM".toList(), dealTiles("MM", Random(1)))
        assertEquals("A".toList(), dealTiles("A", Random(1)))
    }
}
