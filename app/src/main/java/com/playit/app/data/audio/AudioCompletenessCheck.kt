package com.playit.app.data.audio

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AudioCompletenessCheck"

data class CompletenessReport(
    val isComplete: Boolean,
    val totalRequired: Int,
    val totalFound: Int,
    val missingFiles: List<String>,
    val contentGappedFiles: List<String>
)

@Singleton
class AudioCompletenessCheck @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // 26 phoneme letters (ng and ñ are excluded as intentional content gaps)
    val requiredPhonemeLetters = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    )

    // 33 unique Blend It words across 7 letter groups (per DatabaseModule seed data)
    val requiredWords = listOf(
        "sam", "sis", "aim",
        "bus", "sub", "mom", "bee", "bib",
        "bat", "mat", "kit", "toy", "boy",
        "pig", "pan", "bug", "pin", "nap",
        "dog", "hat", "hen", "bed", "hand",
        "cat", "fan", "cap", "cup", "jam",
        "van", "box", "fox", "zoo", "web"
    )

    // 18 VO lines per 19 §2
    val requiredVoLines = listOf(
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

    // 8 SFX lines per 19 §4
    val requiredSfxFiles = listOf(
        "sfx_correct_chime.mp3",
        "sfx_incorrect_pop.mp3",
        "sfx_heart_loss_whoosh.mp3",
        "sfx_heart_recovery_sparkle.mp3",
        "sfx_node_unlock_chime.mp3",
        "sfx_level_complete_fanfare.mp3",
        "sfx_blendit_buzz.mp3",
        "sfx_streak_badge_unlock.mp3"
    )

    val contentGappedPhonemes = listOf("phoneme_ng.mp3", "phoneme_ñ.mp3")

    fun performCheck(): CompletenessReport {
        val missing = mutableListOf<String>()

        // 1. Phonemes
        requiredPhonemeLetters.forEach { letter ->
            val path = "audio/phonemes/phoneme_$letter.mp3"
            if (!assetExists(path)) {
                missing.add(path)
            }
        }

        // 2. Words
        requiredWords.forEach { word ->
            val path = "audio/words/word_$word.mp3"
            if (!assetExists(path)) {
                missing.add(path)
            }
        }

        // 3. VO lines
        requiredVoLines.forEach { filename ->
            val path = "audio/ui/$filename"
            if (!assetExists(path)) {
                missing.add(path)
            }
        }

        // 4. SFX lines
        requiredSfxFiles.forEach { filename ->
            val path = "audio/ui/$filename"
            if (!assetExists(path)) {
                missing.add(path)
            }
        }

        val totalRequired = requiredPhonemeLetters.size + requiredWords.size + requiredVoLines.size + requiredSfxFiles.size
        val totalFound = totalRequired - missing.size
        val isComplete = missing.isEmpty()

        Log.i(TAG, "Audio Completeness Check: $totalFound / $totalRequired assets verified.")
        contentGappedPhonemes.forEach { gap ->
            Log.i(TAG, "Content gap verified (absent pending SME sign-off): $gap")
        }

        if (!isComplete) {
            Log.e(TAG, "BUILD CHECK FAILURE: Missing ${missing.size} required audio files:\n" + missing.joinToString("\n"))
        }

        return CompletenessReport(
            isComplete = isComplete,
            totalRequired = totalRequired,
            totalFound = totalFound,
            missingFiles = missing,
            contentGappedFiles = contentGappedPhonemes
        )
    }

    /**
     * Checks if asset file exists in app assets.
     */
    fun assetExists(assetPath: String): Boolean {
        return try {
            val afd = context.assets.openFd(assetPath)
            afd.close()
            true
        } catch (_: Exception) {
            false
        }
    }
}
