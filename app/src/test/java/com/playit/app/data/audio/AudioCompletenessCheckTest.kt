package com.playit.app.data.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AudioCompletenessCheckTest {

    private val assetsAudioDir = File("src/main/assets/audio")

    private val requiredPhonemeLetters = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    )

    private val requiredWords = listOf(
        "sam", "sis", "aim",
        "bus", "sub", "sum", "bam", "mob",
        "bat", "cat", "mat", "kit", "lit",
        "pig", "pan", "gap", "spin", "nap",
        "bird", "hand", "warm", "road", "draw",
        "face", "cake", "fish", "fan",
        "zoo", "van", "box", "quiz", "fox"
    )

    private val requiredVoLines = listOf(
        "vo_welcome_01.mp3",
        "vo_encourage_01.mp3",
        "vo_encourage_02.mp3",
        "vo_encourage_03.mp3",
        "vo_correct_01.mp3",
        "vo_correct_02.mp3",
        "vo_hint_01.mp3",
        "vo_hint_02.mp3",
        "vo_milestone_01.mp3",
        "vo_streak_01.mp3",
        "vo_complete_01.mp3",
        "vo_unlock_01.mp3",
        "vo_blendit_intro_01.mp3",
        "vo_findit_intro_01.mp3",
        "vo_sayit_intro_01.mp3",
        "vo_quiet_check_01.mp3",
        "vo_return_welcome_01.mp3",
        "vo_noise_alert_01.mp3"
    )

    private val requiredSfxFiles = listOf(
        "sfx_correct_chime.mp3",
        "sfx_incorrect_pop.mp3",
        "sfx_heart_loss_whoosh.mp3",
        "sfx_heart_recovery_sparkle.mp3",
        "sfx_node_unlock_chime.mp3",
        "sfx_level_complete_fanfare.mp3",
        "sfx_blendit_buzz.mp3",
        "sfx_streak_badge_unlock.mp3"
    )

    @Test
    fun verifyRequiredAssetCounts() {
        assertEquals(26, requiredPhonemeLetters.size)
        assertEquals(32, requiredWords.size)
        assertEquals(18, requiredVoLines.size)
        assertEquals(8, requiredSfxFiles.size)
    }

    @Test
    fun verifyAllRequiredAudioFilesExistOnDisk() {
        val phonemesDir = File(assetsAudioDir, "phonemes")
        val wordsDir = File(assetsAudioDir, "words")
        val uiDir = File(assetsAudioDir, "ui")

        val missing = mutableListOf<String>()

        requiredPhonemeLetters.forEach { letter ->
            val f = File(phonemesDir, "phoneme_$letter.mp3")
            if (!f.exists()) missing.add(f.path)
        }

        requiredWords.forEach { word ->
            val f = File(wordsDir, "word_$word.mp3")
            if (!f.exists()) missing.add(f.path)
        }

        requiredVoLines.forEach { vo ->
            val f = File(uiDir, vo)
            if (!f.exists()) missing.add(f.path)
        }

        requiredSfxFiles.forEach { sfx ->
            val f = File(uiDir, sfx)
            if (!f.exists()) missing.add(f.path)
        }

        assertTrue(
            "Missing ${missing.size} required audio files:\n" + missing.joinToString("\n"),
            missing.isEmpty()
        )
    }

    @Test
    fun verifyAll28PhonemesArePresent() {
        val phonemesDir = File(assetsAudioDir, "phonemes")
        val ngFile = File(phonemesDir, "phoneme_ng.mp3")
        val enyeFile = File(phonemesDir, "phoneme_enye.mp3")

        assertTrue("phoneme_ng.mp3 must be present", ngFile.exists())
        assertTrue("phoneme_enye.mp3 must be present", enyeFile.exists())
    }
}
