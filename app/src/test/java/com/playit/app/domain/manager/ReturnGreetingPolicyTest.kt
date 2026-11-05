package com.playit.app.domain.manager

import com.playit.app.domain.model.MapNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** What Lily offers when a child comes back (adviser, 2026-11-05). */
class ReturnGreetingPolicyTest {
    private fun letter(id: Int, symbol: String, unlocked: Boolean, stars: Int, chapter: Int = 1) =
        MapNode.LetterNode(id = "$id", orderIndex = id, isUnlocked = unlocked, groupNumber = chapter, symbol = symbol, starsEarned = stars)

    private fun blend(chapter: Int, unlocked: Boolean) =
        MapNode.BlendItNode(id = "blend_$chapter", orderIndex = 100 + chapter, isUnlocked = unlocked, groupNumber = chapter,
            groupId = "$chapter", starsEarned = 0)

    @Test fun onlyAChildWhoHasPlayed_onTheFirstMapOfTheLaunch() {
        assertTrue(ReturnGreetingPolicy.shouldGreet(hasPlayedBefore = true, firstMapThisLaunch = true))
        assertFalse(ReturnGreetingPolicy.shouldGreet(hasPlayedBefore = false, firstMapThisLaunch = true))
        assertFalse(ReturnGreetingPolicy.shouldGreet(hasPlayedBefore = true, firstMapThisLaunch = false))
    }

    @Test fun theNextLetter_isTheFirstOpenOneNotYetPlayed() {
        val nodes = listOf(letter(1, "M", true, 3), letter(2, "S", true, 0), letter(3, "A", false, 0), blend(1, false))
        assertEquals(ReturnGreetingPolicy.KeepGoing("2", "S"), ReturnGreetingPolicy.offerFor(nodes))
        assertEquals(listOf("ret_welcome_back", "ret_keep_going"),
            ReturnGreetingPolicy.lines(ReturnGreetingPolicy.offerFor(nodes)!!))
    }

    @Test fun aFinishedChapter_offersItsWordGame() {
        val nodes = listOf(letter(1, "M", true, 3), letter(2, "S", true, 2), blend(1, true), letter(3, "O", false, 0, chapter = 2))
        assertEquals(ReturnGreetingPolicy.BuildWords("blend_1", 1), ReturnGreetingPolicy.offerFor(nodes))
    }

    @Test fun everyLetterDone_saysSo_andGoesNowhere() {
        val nodes = listOf(letter(1, "M", true, 3), letter(2, "S", true, 1), blend(1, true))
        val offer = ReturnGreetingPolicy.offerFor(nodes)
        assertEquals(ReturnGreetingPolicy.AllDone, offer)
        assertNull(offer!!.nodeId)
    }

    @Test fun theCard_saysWhatLilySays_andNeverALetterName() {
        assertEquals("Do you want to keep going with this sound?", ReturnGreetingPolicy.question(ReturnGreetingPolicy.KeepGoing("2", "S")))
        listOf(ReturnGreetingPolicy.KeepGoing("1", "M"), ReturnGreetingPolicy.BuildWords("blend_1", 1), ReturnGreetingPolicy.AllDone).forEach {
            assertTrue(ReturnGreetingPolicy.lines(it).all { id -> id.startsWith("ret_") })
        }
        assertNull(ReturnGreetingPolicy.offerFor(emptyList()))
    }
}
