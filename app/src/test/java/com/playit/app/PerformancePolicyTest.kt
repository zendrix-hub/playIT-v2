package com.playit.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PerformancePolicyTest {
    private val main = if (File("src/main").exists()) File("src/main") else File("app/src/main")
    private fun kotlinSources() = File(main, "java").walkTopDown().filter { it.extension == "kt" }

    @Test fun noHapticFeedbackCalls() {
        val hits = kotlinSources().filter { it.readText().contains("performHapticFeedback") }.map { it.name }.toList()
        assertTrue("Haptics are removed (user decision 2026-10-06): $hits", hits.isEmpty())
    }

    @Test fun activityIsPortraitOnly() {
        val manifest = File(main, "AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android:screenOrientation=\"portrait\""))
    }

    @Test fun noIdleFloatingRequestedByCallers() {
        // Dry run 2026-10-06: four callers passed isIdleFloating = true explicitly, so changing the default alone kept them floating.
        val hits = kotlinSources().filter { Regex("isIdleFloating\\s*=\\s*(true|!)").containsMatchIn(it.readText()) }.map { it.name }.toList()
        assertTrue("Idle floating is removed (effects on meaningful moments only): $hits", hits.isEmpty())
    }

    @Test fun noEndlessIdleAnimationOnLetterCardOrMascotHeader() {
        val card = File(main, "java/com/playit/app/presentation/components/LetterCard.kt").readText()
        val header = File(main, "java/com/playit/app/presentation/components/MascotSpeechHeader.kt").readText()
        assertFalse(card.contains("breathingPulse("))
        assertFalse(header.contains("rememberInfiniteTransition("))   // the call, not a leftover import
    }
}
