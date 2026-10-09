package com.playit.app.data.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AudioResolverTest {

    private lateinit var audioResolver: AudioResolver

    @Before
    fun setUp() {
        audioResolver = AudioResolver()
    }

    @Test
    fun getPhonemePath_returnsCorrectPathForStandardLetters() {
        assertEquals("audio/phonemes/phoneme_b.mp3", audioResolver.getPhonemePath("b"))
        assertEquals("audio/phonemes/phoneme_a.mp3", audioResolver.getPhonemePath("a"))
        assertEquals("audio/phonemes/phoneme_t.mp3", audioResolver.getPhonemePath("t"))
    }

    @Test
    fun phonemeM_usesReleasedHeldSound() {
        assertEquals("audio/phonemes/ph_m.wav", audioResolver.getPhonemePath("m"))
        assertEquals("audio/phonemes/ph_m.wav", audioResolver.getPhonemePath("M "))
    }

    @Test
    fun phonemeS_usesReleasedHeldSound() {
        assertEquals("audio/phonemes/ph_s.wav", audioResolver.getPhonemePath("s"))
        assertEquals("audio/phonemes/ph_s.wav", audioResolver.getPhonemePath("S "))
    }

    @Test
    fun otherPhonemes_keepOldPath() {
        assertEquals("audio/phonemes/phoneme_a.mp3", audioResolver.getPhonemePath("a"))
        assertEquals("audio/phonemes/phoneme_t.mp3", audioResolver.getPhonemePath("t"))
    }

    @Test
    fun releasedPhoneme_fileExists() {
        val assetsDir = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() } ?: error("assets not found")
        val wavM = File(assetsDir, "audio/phonemes/ph_m.wav")
        assertTrue("ph_m.wav should exist", wavM.exists())
        assertTrue("ph_m.wav should not be empty", wavM.length() > 0L)
        val wavS = File(assetsDir, "audio/phonemes/ph_s.wav")
        assertTrue("ph_s.wav should exist", wavS.exists())
        assertTrue("ph_s.wav should not be empty", wavS.length() > 0L)
    }

    @Test
    fun correctionFragments_fileExists() {
        // Card 03b: every clip a Say It correction plays (release 2026-10-01, plus the card 05 carrier).
        val assetsDir = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() } ?: error("assets not found")
        listOf("fb_listen", "fb_letter_name", "fb_its_sound_is", "fb_almost_just", "fb_no_ah", "car_your_turn").forEach { id ->
            val path = audioResolver.getTutorPath(id)
            val file = File(assetsDir, path)
            assertTrue("$path should exist", file.exists())
            assertTrue("$path should not be empty", file.length() > 0L)
        }
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
    fun getTutorPath_returnsWavInTutorFolder() {
        assertEquals("audio/vo/tutor/car_your_turn.wav", audioResolver.getTutorPath("car_your_turn"))
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
        assertEquals("audio/vo/lesson/vo_welcome_01.wav", audioResolver.getVoPath(VoContext.WELCOME_01))
        assertEquals("audio/ui/vo_return_welcome_01.mp3", audioResolver.getVoPath(VoContext.RETURN_WELCOME_01))
        assertEquals("audio/vo/lesson/vo_findit_intro_01.wav", audioResolver.getVoPath(VoContext.FINDIT_INTRO_01))
        assertEquals("audio/vo/lesson/vo_sayit_intro_01.wav", audioResolver.getVoPath(VoContext.SAYIT_INTRO_01))
        assertEquals("audio/vo/lesson/vo_blendit_intro_01.wav", audioResolver.getVoPath(VoContext.BLENDIT_INTRO_01))
    }

    @Test
    fun rotationalHelpers_alternateCorrectly() {
        val firstCorrect = audioResolver.getRotatingCorrectVo()
        val secondCorrect = audioResolver.getRotatingCorrectVo()
        val thirdCorrect = audioResolver.getRotatingCorrectVo()

        assertEquals("audio/vo/lesson/vo_correct_01.wav", firstCorrect)
        assertEquals("audio/vo/lesson/vo_correct_02.wav", secondCorrect)
        assertEquals("audio/vo/lesson/vo_correct_01.wav", thirdCorrect)

        val firstEncourage = audioResolver.getRotatingEncourageVo()
        val secondEncourage = audioResolver.getRotatingEncourageVo()
        val thirdEncourage = audioResolver.getRotatingEncourageVo()
        val fourthEncourage = audioResolver.getRotatingEncourageVo()

        assertEquals("audio/vo/lesson/vo_encourage_01.wav", firstEncourage)
        assertEquals("audio/vo/lesson/vo_encourage_02.wav", secondEncourage)
        assertEquals("audio/vo/lesson/vo_encourage_03.wav", thirdEncourage)
        assertEquals("audio/vo/lesson/vo_encourage_01.wav", fourthEncourage)

        val firstHint = audioResolver.getRotatingHintVo()
        val secondHint = audioResolver.getRotatingHintVo()
        val thirdHint = audioResolver.getRotatingHintVo()

        assertEquals("audio/vo/lesson/vo_hint_01.wav", firstHint)
        assertEquals("audio/vo/lesson/vo_hint_02.wav", secondHint)
        assertEquals("audio/vo/lesson/vo_hint_01.wav", thirdHint)
    }

    @Test
    fun lessonVo_allReleasedLinesUseWav() {
        val manifestFile = listOf(
            File("docs/audio-release/2026-10-07/manifest.json"),
            File("../docs/audio-release/2026-10-07/manifest.json")
        ).firstOrNull { it.exists() } ?: error("manifest.json not found")

        val assetsDir = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).firstOrNull { it.exists() } ?: error("assets not found")

        val text = manifestFile.readText()
        val clipIdPattern = Regex("\"clipId\":\\s*\"([^\"]+)\"")
        val clipIds = clipIdPattern.findAll(text).map { it.groupValues[1] }.toList()
        assertTrue("Manifest should have at least 19 clips", clipIds.size >= 19)

        for (clipId in clipIds) {
            val wavFile = File(assetsDir, "audio/vo/lesson/$clipId.wav")
            assertTrue("Expected released line to exist on disk: ${wavFile.path}", wavFile.exists())
            assertTrue("Expected non-empty audio file: ${wavFile.path}", wavFile.length() > 0L)
        }
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
        assertEquals("audio/_dev_placeholder/vo_tone.wav", audioResolver.getDevPlaceholderForAsset("audio/ui/vo_return_welcome_01.mp3"))
        assertEquals("audio/_dev_placeholder/vo_tone.wav", audioResolver.getDevPlaceholderForAsset("audio/vo/lesson/vo_welcome_01.wav"))
        assertEquals("audio/_dev_placeholder/sfx_chime.wav", audioResolver.getDevPlaceholderForAsset("audio/ui/sfx_correct_chime.mp3"))
    }

    @Test
    fun getKeyWordPath_returnsWavInKeywordsFolder() {
        assertEquals("audio/keywords/kw_mouse.wav", audioResolver.getKeyWordPath("Mouse"))
        assertEquals("audio/keywords/kw_yoyo.wav", audioResolver.getKeyWordPath("Yo-yo"))
    }

    @Test
    fun devPlaceholder_mapsKeywordsAndTutor() {
        assertEquals(DevAudioCategory.WORD.assetPath, audioResolver.getDevPlaceholderForAsset("audio/keywords/kw_mouse.wav"))
        assertEquals(DevAudioCategory.VO.assetPath, audioResolver.getDevPlaceholderForAsset("audio/vo/tutor/car_listen.wav"))
    }

    @Test
    fun getUiPath_returnsWavInUiFolder() {
        assertEquals("audio/vo/ui/ui_hearit_next.wav", audioResolver.getUiPath("ui_hearit_next"))
    }

    @Test
    fun devPlaceholder_mapsUiLines() {
        assertEquals(DevAudioCategory.VO.assetPath, audioResolver.getDevPlaceholderForAsset("audio/vo/ui/ui_hearit_next.wav"))
    }
}

