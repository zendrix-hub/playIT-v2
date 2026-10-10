package com.playit.app.di

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Card 15: the Blend It words are decodable with the letters taught up to their group. */
class BlendItWordSeedsTest {

    /** Letters each group adds (DatabaseModule groupMemberPhonemeIds, Marungko order). */
    private val lettersByGroup = mapOf(
        1 to "msai", 2 to "obeu", 3 to "tkly", 4 to "ngp", 5 to "rdhw", 6 to "cfj", 7 to "qvxz"
    )

    private fun taughtBy(group: Int): Set<Char> =
        (1..group).flatMap { lettersByGroup.getValue(it).toList() }.toSet()

    @Test fun thirtyThreeWords_threeThenFivePerGroup() {
        assertEquals(33, BLEND_IT_WORD_SEEDS.size)
        val perGroup = BLEND_IT_WORD_SEEDS.groupingBy { it.groupId }.eachCount()
        assertEquals(mapOf(1 to 3, 2 to 5, 3 to 5, 4 to 5, 5 to 5, 6 to 5, 7 to 5), perGroup)
        assertEquals((1..33).toList(), BLEND_IT_WORD_SEEDS.map { it.wordId })
    }

    @Test fun everyWordUsesOnlyTaughtLetters() {
        BLEND_IT_WORD_SEEDS.filter { it.word != "QUIZ" }.forEach { w ->
            val untaught = w.word.lowercase().filter { it !in taughtBy(w.groupId) }
            assertTrue("${w.word} in group ${w.groupId} uses untaught letters '$untaught'", untaught.isEmpty())
        }
    }

    @Test fun nonDecodableWordsAreGone() {
        val words = BLEND_IT_WORD_SEEDS.map { it.word }
        listOf("AIM", "BEE", "TOY", "BOY", "ZOO").forEach { assertTrue("$it still seeded", it !in words) }
        listOf("AM", "SUM", "TUB", "YAM", "ZIP").forEach { assertTrue("$it missing", it in words) }
    }

    @Test fun replacementsKeepTheirSlots() {
        val byId = BLEND_IT_WORD_SEEDS.associateBy { it.wordId }
        assertEquals("AM", byId.getValue(3).word)
        assertEquals("SUM", byId.getValue(7).word)
        assertEquals("TUB", byId.getValue(12).word)
        assertEquals("YAM", byId.getValue(13).word)
        assertEquals("ZIP", byId.getValue(32).word)
        assertEquals("A-M", byId.getValue(3).wordPattern)
        assertEquals("audio/words/word_zip.mp3", byId.getValue(32).audioPath)
    }
}
