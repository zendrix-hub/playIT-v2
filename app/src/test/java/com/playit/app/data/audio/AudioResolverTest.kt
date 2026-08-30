package com.playit.app.data.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AudioResolverTest {

    private lateinit var audioResolver: AudioResolver

    @Before
    fun setUp() {
        audioResolver = AudioResolver()
    }

    @Test
    fun getPhonemePath_returnsCorrectPathForStandardLetters() {
        assertEquals("audio/phonemes/phoneme_m.mp3", audioResolver.getPhonemePath("M"))
        assertEquals("audio/phonemes/phoneme_a.mp3", audioResolver.getPhonemePath("a"))
        assertEquals("audio/phonemes/phoneme_s.mp3", audioResolver.getPhonemePath("s"))
    }

    @Test
    fun getPhonemePath_returnsCorrectPathForSpecialLetters() {
        assertEquals("audio/phonemes/phoneme_ng.mp3", audioResolver.getPhonemePath("ng"))
        assertEquals("audio/phonemes/phoneme_enye.mp3", audioResolver.getPhonemePath("ñ"))
        assertEquals("audio/phonemes/phoneme_ng.mp3", audioResolver.getPhonemePath("NG"))
        assertEquals("audio/phonemes/phoneme_enye.mp3", audioResolver.getPhonemePath("enye"))
    }

    @Test
    fun getPhonemePath_returnsDraftPathForX() {
        assertEquals("audio/phonemes/phoneme_x.mp3", audioResolver.getPhonemePath("x"))
    }

    @Test
    fun getWordPath_returnsCorrectPath() {
        assertEquals("audio/words/word_sam.mp3", audioResolver.getWordPath("SAM"))
        assertEquals("audio/words/word_kite.mp3", audioResolver.getWordPath("kite"))
    }

    @Test
    fun getSfxPath_returnsCorrectPaths() {
        assertEquals("audio/ui/sfx_correct_chime.mp3", audioResolver.getSfxPath(SfxEvent.CORRECT_CHIME))
        assertEquals("audio/ui/sfx_incorrect_pop.mp3", audioResolver.getSfxPath(SfxEvent.INCORRECT_POP))
        assertEquals("audio/ui/sfx_heart_loss_whoosh.mp3", audioResolver.getSfxPath(SfxEvent.HEART_LOSS_WHOOSH))
        assertEquals("audio/ui/sfx_heart_recovery_sparkle.mp3", audioResolver.getSfxPath(SfxEvent.HEART_RECOVERY_SPARKLE))
        assertEquals("audio/ui/sfx_node_unlock_chime.mp3", audioResolver.getSfxPath(SfxEvent.NODE_UNLOCK_CHIME))
        assertEquals("audio/ui/sfx_level_complete_fanfare.mp3", audioResolver.getSfxPath(SfxEvent.LEVEL_COMPLETE_FANFARE))
        assertEquals("audio/ui/sfx_blendit_buzz.mp3", audioResolver.getSfxPath(SfxEvent.BLENDIT_BUZZ))
        assertEquals("audio/ui/sfx_streak_badge_unlock.mp3", audioResolver.getSfxPath(SfxEvent.STREAK_BADGE_UNLOCK))
    }

    @Test
    fun getVoPath_returnsCorrectPaths() {
        assertEquals("audio/ui/vo_welcome_01.mp3", audioResolver.getVoPath(VoContext.WELCOME_01))
        assertEquals("audio/ui/vo_return_welcome_01.mp3", audioResolver.getVoPath(VoContext.RETURN_WELCOME_01))
        assertEquals("audio/ui/vo_findit_intro_01.mp3", audioResolver.getVoPath(VoContext.FINDIT_INTRO_01))
        assertEquals("audio/ui/vo_sayit_intro_01.mp3", audioResolver.getVoPath(VoContext.SAYIT_INTRO_01))
        assertEquals("audio/ui/vo_blendit_intro_01.mp3", audioResolver.getVoPath(VoContext.BLENDIT_INTRO_01))
    }

    @Test
    fun rotationalHelpers_alternateCorrectly() {
        val firstCorrect = audioResolver.getRotatingCorrectVo()
        val secondCorrect = audioResolver.getRotatingCorrectVo()
        val thirdCorrect = audioResolver.getRotatingCorrectVo()

        assertEquals("audio/ui/vo_correct_01.mp3", firstCorrect)
        assertEquals("audio/ui/vo_correct_02.mp3", secondCorrect)
        assertEquals("audio/ui/vo_correct_01.mp3", thirdCorrect)

        val firstEncourage = audioResolver.getRotatingEncourageVo()
        val secondEncourage = audioResolver.getRotatingEncourageVo()
        val thirdEncourage = audioResolver.getRotatingEncourageVo()
        val fourthEncourage = audioResolver.getRotatingEncourageVo()

        assertEquals("audio/ui/vo_encourage_01.mp3", firstEncourage)
        assertEquals("audio/ui/vo_encourage_02.mp3", secondEncourage)
        assertEquals("audio/ui/vo_encourage_03.mp3", thirdEncourage)
        assertEquals("audio/ui/vo_encourage_01.mp3", fourthEncourage)
    }

    @Test
    fun getDevPlaceholderPath_returnsCorrectCategoryPaths() {
        assertEquals("audio/_dev_placeholder/phoneme_beep.wav", audioResolver.getDevPlaceholderPath(DevAudioCategory.PHONEME))
        assertEquals("audio/_dev_placeholder/word_beep.wav", audioResolver.getDevPlaceholderPath(DevAudioCategory.WORD))
        assertEquals("audio/_dev_placeholder/vo_tone.wav", audioResolver.getDevPlaceholderPath(DevAudioCategory.VO))
        assertEquals("audio/_dev_placeholder/sfx_chime.wav", audioResolver.getDevPlaceholderPath(DevAudioCategory.SFX))
    }

    @Test
    fun getDevPlaceholderForAsset_mapsProductionPathsToDevPlaceholders() {
        assertEquals("audio/_dev_placeholder/phoneme_beep.wav", audioResolver.getDevPlaceholderForAsset("audio/phonemes/phoneme_a.mp3"))
        assertEquals("audio/_dev_placeholder/word_beep.wav", audioResolver.getDevPlaceholderForAsset("audio/words/word_apple.mp3"))
        assertEquals("audio/_dev_placeholder/vo_tone.wav", audioResolver.getDevPlaceholderForAsset("audio/ui/vo_welcome_01.mp3"))
        assertEquals("audio/_dev_placeholder/sfx_chime.wav", audioResolver.getDevPlaceholderForAsset("audio/ui/sfx_correct_chime.mp3"))
    }
}

