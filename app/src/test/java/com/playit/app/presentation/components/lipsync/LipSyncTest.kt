package com.playit.app.presentation.components.lipsync

import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.talkFramePath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LipSyncTest {
    private val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.exists() }

    /** Width and height from a PNG's header (javax.imageio is not on the Android test classpath). */
    private fun pngSize(file: File): Pair<Int, Int> {
        val b = file.readBytes()
        fun int(at: Int) = ((b[at].toInt() and 0xFF) shl 24) or ((b[at + 1].toInt() and 0xFF) shl 16) or
            ((b[at + 2].toInt() and 0xFF) shl 8) or (b[at + 3].toInt() and 0xFF)
        return int(16) to int(20)
    }

    @Test fun parse_readsFrameAndClips_includingEscapedNames() {
        val e = VoiceEnvelopes.parse("""{"frameMs": 40, "clips": {"audio/a.mp3": "09", "audio/phonemes/phoneme_ñ.mp3": "5"}}""")
        assertEquals(40, e.frameMs)
        assertTrue(e.has("audio/a.mp3"))
        assertTrue(e.has("audio/phonemes/phoneme_ñ.mp3"))
    }

    @Test fun levelAt_interpolatesBetweenFrames_andIsSilentOutsideTheClip() {
        val e = VoiceEnvelopes(40, mapOf("audio/a.mp3" to "09"))
        assertEquals(0f, e.levelAt("audio/a.mp3", 0)!!, 1e-4f)
        assertEquals(0.5f, e.levelAt("audio/a.mp3", 20)!!, 1e-4f)
        assertEquals(1f, e.levelAt("audio/a.mp3", 40)!!, 1e-4f)
        assertEquals(0f, e.levelAt("audio/a.mp3", -10)!!, 1e-4f)
        assertEquals(0f, e.levelAt("audio/a.mp3", 400)!!, 1e-4f)
        assertNull(e.levelAt("audio/unknown.mp3", 20))
    }

    @Test fun phonemeLetter_onlyForSingleLetterPhonemeClips() {
        assertEquals("m", phonemeLetter("audio/phonemes/phoneme_m.mp3"))
        assertEquals("s", phonemeLetter("audio/phonemes/ph_s.wav"))
        assertNull(phonemeLetter("audio/phonemes/phoneme_ng.mp3"))
        assertNull(phonemeLetter("audio/phonemes/phoneme_enye.mp3"))
        assertNull(phonemeLetter("audio/words/word_mouse.mp3"))
    }

    @Test fun lipsTogether_keepsTheMouthClosed_andWideOpenIsTheMostOpen() {
        assertEquals(0f, articulationMouth(ArticulationGroup.LIPS_TOGETHER).open, 0f)
        val wide = articulationMouth(ArticulationGroup.WIDE_OPEN).open
        ArticulationGroup.values().forEach { assertTrue(it.name, articulationMouth(it).open <= wide) }
    }

    @Test fun lerp_reachesBothEnds() {
        val a = Mouth(1f, 0f, 1f)
        val b = Mouth(0.5f, 0.8f, 0f, tongue = 1f)
        assertEquals(a, a.lerp(b, 0f))
        assertEquals(b, a.lerp(b, 1f))
        assertEquals(0.4f, a.lerp(b, 0.5f).open, 1e-4f)
    }

    /** Each pose has its talking frame: the same 512 x 512 picture, so the drawn mouth lands where the old one was. */
    @Test fun everyPoseHasATalkingFrameOfTheSameSize() {
        MascotState.values().forEach { pose ->
            val frame = File(assets, pose.talkFramePath())
            assertTrue("missing ${pose.talkFramePath()}: run tools/design/make_lily_talk.py", frame.exists())
            assertEquals(pose.name, pngSize(File(assets, pose.assetPath)), pngSize(frame))
            val m = poseMouth(pose)
            assertTrue(pose.name, m.centerX in 0f..512f && m.top in 0f..512f && m.width > 0f)
        }
    }

    /** Every speech clip has its loudness, so a new or changed clip fails here until the tool is run again. */
    @Test fun everySpeechClipHasAnEnvelope_andEveryEnvelopeHasItsClip() {
        val envelopes = VoiceEnvelopes.parse(File(assets, "lipsync/voice_envelopes.json").readText())
        val speech = File(assets, "audio").walkTopDown()
            .filter { it.isFile && it.extension in setOf("mp3", "wav") && !it.name.startsWith("sfx_") }
            .map { it.relativeTo(assets).invariantSeparatorsPath }
            .filter { "_dev_placeholder" !in it && "vosk-model" !in it }
            .toList()
        assertTrue(speech.isNotEmpty())
        val missing = speech.filterNot { envelopes.has(it) }
        assertTrue("No loudness for $missing: run tools/design/build_voice_envelopes.py", missing.isEmpty())
        val json = File(assets, "lipsync/voice_envelopes.json").readText()
        val keys = Regex(""""(audio/[^"]+)"\s*:""").findAll(json).map {
            Regex("""\\u([0-9a-fA-F]{4})""").replace(it.groupValues[1]) { u -> u.groupValues[1].toInt(16).toChar().toString() }
        }
        val stale = keys.filterNot { File(assets, it).exists() }.toList()
        assertTrue("Loudness for clips that are gone: $stale", stale.isEmpty())
    }
}
