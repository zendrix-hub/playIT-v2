package com.playit.app.data.audio

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "AudioResolver"

enum class SfxEvent(val filenameSuffix: String) {
    CORRECT_CHIME("correct_chime"),
    INCORRECT_POP("incorrect_pop"),
    HEART_LOSS_WHOOSH("heart_loss_whoosh"),
    HEART_RECOVERY_SPARKLE("heart_recovery_sparkle"),
    NODE_UNLOCK_CHIME("node_unlock_chime"),
    LEVEL_COMPLETE_FANFARE("level_complete_fanfare"),
    BLENDIT_BUZZ("blendit_buzz"),
    STREAK_BADGE_UNLOCK("streak_badge_unlock")
}

enum class VoContext(val filenameSuffix: String) {
    WELCOME_01("welcome_01"),
    ENCOURAGE_01("encourage_01"),
    ENCOURAGE_02("encourage_02"),
    ENCOURAGE_03("encourage_03"),
    CORRECT_01("correct_01"),
    CORRECT_02("correct_02"),
    HINT_01("hint_01"),
    HINT_02("hint_02"),
    MILESTONE_01("milestone_01"),
    STREAK_01("streak_01"),
    COMPLETE_01("complete_01"),
    UNLOCK_01("unlock_01"),
    HEARIT_INTRO_01("hearit_intro_01"),
    BLENDIT_INTRO_01("blendit_intro_01"),
    FINDIT_INTRO_01("findit_intro_01"),
    SAYIT_INTRO_01("sayit_intro_01"),
    QUIET_CHECK_01("quiet_check_01"),
    RETURN_WELCOME_01("return_welcome_01"),
    NOISE_ALERT_01("noise_alert_01"),
    SPLASH_TAGLINE("splash_tagline"),
    NAMEPROMPT_INTRO("nameprompt_intro"),
    MAP_TARANA("map_tarana"),
    BLENDIT_COMPLETE("blendit_complete"),
    STAR_CELEBRATION("star_celebration"),
    PARENT_GATE("parent_gate")
}

@Singleton
class AudioResolver @Inject constructor() {

    private val correctCounter = AtomicInteger(0)
    private val encourageCounter = AtomicInteger(0)
    private val hintCounter = AtomicInteger(0)

    /**
     * Resolves the asset path for a phoneme letter.
     */
    fun getPhonemePath(letter: String): String {
        val clean = letter.lowercase().trim()
        val key = when (clean) {
            "ñ", "enye" -> "enye"
            else -> clean
        }
        return "audio/phonemes/phoneme_$key.mp3"
    }

    /**
     * Resolves the asset path for a Blend It word.
     */
    fun getWordPath(word: String): String {
        val clean = word.lowercase().trim()
        return "audio/words/word_$clean.mp3"
    }

    /**
     * Resolves the asset path for an SFX event.
     */
    fun getSfxPath(event: SfxEvent): String {
        return "audio/ui/sfx_${event.filenameSuffix}.mp3"
    }

    /**
     * Resolves the asset path for a mascot VO line.
     */
    fun getVoPath(vo: VoContext): String {
        return "audio/ui/vo_${vo.filenameSuffix}.mp3"
    }

    /**
     * Rotates between vo_correct_01 and vo_correct_02.
     */
    fun getRotatingCorrectVo(): String {
        val count = correctCounter.getAndIncrement()
        val suffix = if (count % 2 == 0) "correct_01" else "correct_02"
        return "audio/ui/vo_$suffix.mp3"
    }

    /**
     * Rotates among vo_encourage_01, vo_encourage_02, and vo_encourage_03.
     */
    fun getRotatingEncourageVo(): String {
        val count = encourageCounter.getAndIncrement()
        val idx = (count % 3) + 1
        return "audio/ui/vo_encourage_0$idx.mp3"
    }

    /**
     * Rotates between vo_hint_01 and vo_hint_02.
     */
    fun getRotatingHintVo(): String {
        val count = hintCounter.getAndIncrement()
        val suffix = if (count % 2 == 0) "hint_01" else "hint_02"
        return "audio/ui/vo_$suffix.mp3"
    }

    /**
     * Resolves the dev placeholder path for a category.
     */
    fun getDevPlaceholderPath(category: DevAudioCategory): String {
        return category.assetPath
    }

    /**
     * Maps a production asset path to its corresponding dev placeholder audio asset.
     */
    fun getDevPlaceholderForAsset(assetPath: String): String {
        return when {
            assetPath.startsWith("audio/phonemes/") -> DevAudioCategory.PHONEME.assetPath
            assetPath.startsWith("audio/words/") -> DevAudioCategory.WORD.assetPath
            assetPath.contains("sfx_") -> DevAudioCategory.SFX.assetPath
            assetPath.contains("vo_") -> DevAudioCategory.VO.assetPath
            else -> DevAudioCategory.SFX.assetPath
        }
    }
}

enum class DevAudioCategory(val assetPath: String) {
    PHONEME("audio/_dev_placeholder/phoneme_beep.wav"),
    WORD("audio/_dev_placeholder/word_beep.wav"),
    VO("audio/_dev_placeholder/vo_tone.wav"),
    SFX("audio/_dev_placeholder/sfx_chime.wav")
}

