package com.playit.app.presentation.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Card 28: one tap starts the next letter; Lily's greeting fits one line. */
class MapTapRulesTest {

    @Test
    fun activeNode_launchesDirectly() =
        assertEquals(NodeTapAction.LAUNCH, nodeTapAction(isUnlocked = true, index = 4, activeNodeIndex = 4))

    @Test
    fun otherUnlockedNode_opensPopup() =
        assertEquals(NodeTapAction.POPUP, nodeTapAction(isUnlocked = true, index = 1, activeNodeIndex = 4))

    @Test
    fun lockedNode_staysLocked() {
        assertEquals(NodeTapAction.LOCKED, nodeTapAction(isUnlocked = false, index = 5, activeNodeIndex = 4))
        // Even at the active index: a locked node never launches.
        assertEquals(NodeTapAction.LOCKED, nodeTapAction(isUnlocked = false, index = 4, activeNodeIndex = 4))
    }

    @Test
    fun greeting_fitsOneLine() {
        val sixteen = "Alexandria Marie" // 16 characters, the longest name card 22 tests
        assertEquals(16, sixteen.length)
        assertEquals("Hi, Alexandria Marie!", greetingFor(sixteen))
        assertTrue(greetingFor(sixteen).length <= 21)
        assertEquals("Hi, Ana!", greetingFor("  Ana "))
        assertEquals("Let's play!", greetingFor(""))
        assertEquals("Let's play!", greetingFor("   "))
    }
}
