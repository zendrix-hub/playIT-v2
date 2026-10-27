package com.playit.app.presentation.components.lipsync

/**
 * The loudness of every speech clip, from assets/lipsync/voice_envelopes.json (tools/design/build_voice_envelopes.py):
 * one digit 0..9 per [frameMs] of the clip, 0 silent and 9 as loud as the clip gets.
 */
class VoiceEnvelopes(val frameMs: Int, private val clips: Map<String, String>) {

    fun has(assetPath: String): Boolean = assetPath in clips

    /** The loudness 0..1 at [elapsedMs] into the clip, between frames; 0 before and after it; null for an unknown clip. */
    fun levelAt(assetPath: String, elapsedMs: Long): Float? {
        val digits = clips[assetPath] ?: return null
        if (elapsedMs < 0 || digits.isEmpty()) return 0f
        val position = elapsedMs.toFloat() / frameMs
        val index = position.toInt()
        if (index >= digits.length) return 0f
        val here = digits[index].digitToInt() / 9f
        val next = if (index + 1 < digits.length) digits[index + 1].digitToInt() / 9f else 0f
        return here + (next - here) * (position - index)
    }

    companion object {
        private val FRAME = Regex(""""frameMs"\s*:\s*(\d+)""")
        private val CLIP = Regex(""""((?:[^"\\]|\\.)+)"\s*:\s*"([0-9]*)"""")
        private val UNICODE = Regex("""\\u([0-9a-fA-F]{4})""")

        /** Reads the tool's JSON; the format is fixed, so no JSON library is needed (and the JVM tests run it as is). */
        fun parse(json: String): VoiceEnvelopes {
            val frameMs = FRAME.find(json)?.groupValues?.get(1)?.toInt() ?: 40
            val clips = CLIP.findAll(json).associate { m ->
                UNICODE.replace(m.groupValues[1]) { it.groupValues[1].toInt(16).toChar().toString() } to m.groupValues[2]
            }
            return VoiceEnvelopes(frameMs, clips)
        }
    }
}

/** The letter a phoneme clip sounds ("audio/phonemes/phoneme_m.mp3" and "audio/phonemes/ph_m.wav" give "m"), or null. */
fun phonemeLetter(assetPath: String): String? {
    if (!assetPath.startsWith("audio/phonemes/")) return null
    val name = assetPath.substringAfterLast('/').substringBeforeLast('.')
    val letter = name.removePrefix("phoneme_").removePrefix("ph_")
    return letter.takeIf { it.length == 1 && it[0] in 'a'..'z' }
}
