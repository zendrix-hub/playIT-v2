package com.playit.app.presentation.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Teacher, 2026-11-06: "Tap the big button" is hard for a child, because every button is big. Lily names the word on
 * the button, and the button shows that word (release 2026-11-06-next-lines).
 */
class NextLinesTest {
    @Test fun eachLine_namesTheWordOnItsButton() {
        listOf(NextLines.HEAR_IT, NextLines.FIND_IT, NextLines.COMPLETE).forEach {
            assertTrue(it, it.contains("Tap ${NextLines.NEXT}"))
        }
        assertTrue(NextLines.NODE_START.lowercase().contains(NextLines.PLAY.lowercase()))
        assertTrue(NextLines.PLAY_AGAIN.startsWith(NextLines.PLAY))
    }

    @Test fun noLineSaysTheBigButton() {
        listOf(NextLines.HEAR_IT, NextLines.FIND_IT, NextLines.COMPLETE, NextLines.NODE_START).forEach {
            assertFalse(it, it.lowercase().contains("big button"))
        }
    }

    /** Every screen that plays a Next line labels its button Next (no "Continue to Map", "Complete Lesson"...). */
    @Test fun theNextButtons_sayNext() {
        val screens = listOf("hearit/HearItScreen.kt", "sayit/SayItScreen.kt", "findit/FindItScreen.kt",
            "lettercomplete/LetterCompleteScreen.kt", "blendit/BlendItCompleteScreen.kt")
        screens.forEach { path ->
            val code = File("src/main/java/com/playit/app/presentation/$path").readText()
            assertTrue(path, code.contains("NextLines.NEXT"))
            listOf("Continue to Map", "Complete Lesson", "Next: Say It", "Next: Find It").forEach { old ->
                assertFalse("$path still says $old", code.contains("\"$old\""))
            }
        }
    }
}
