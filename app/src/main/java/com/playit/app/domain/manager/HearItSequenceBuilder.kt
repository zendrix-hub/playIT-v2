package com.playit.app.domain.manager

/**
 * Builds the Hear It modeling sequence (I-do step) from a template.
 *
 * Implements FR-02 (01_REQUIREMENTS_SUMMARY.md §6 / hear-say-refactor.md §2.1 Table 3).
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 */
object HearItSequenceBuilder {

    val DEFAULT_TEMPLATE: List<String> = listOf(
        "car_listen",
        "car_this_letter_says",
        "PHONEME",
        "PAUSE_500",
        "PHONEME",
        "PAUSE_500",
        "PHONEME",
        "KEYWORD",
        "PHONEME",
        "car_say_it_with_me"
    )

    /**
     * Maps tokens in [template] to concrete asset paths:
     * - `car_*` -> `audio/vo/tutor/<id>.wav`
     * - `PHONEME` -> `audio/phonemes/phoneme_<letter>.mp3`
     * - `KEYWORD` -> `audio/words/word_<keyWord>.mp3`
     * - `PAUSE_<ms>` -> literal token preserved for the audio player
     */
    fun build(
        template: List<String> = DEFAULT_TEMPLATE,
        letter: String,
        keyWord: String
    ): List<String> {
        val cleanLetter = letter.lowercase().trim()
        val phonemeKey = when (cleanLetter) {
            "ñ", "enye" -> "enye"
            else -> cleanLetter
        }
        val cleanWord = keyWord.lowercase().trim()

        return template.map { token ->
            when {
                token.startsWith("car_") -> "audio/vo/tutor/$token.wav"
                token == "PHONEME" -> "audio/phonemes/phoneme_$phonemeKey.mp3"
                token == "KEYWORD" -> "audio/words/word_$cleanWord.mp3"
                else -> token
            }
        }
    }

    /**
     * Builds the replay sequence starting from `car_this_letter_says` onward (skipping `car_listen`).
     */
    fun buildReplay(
        template: List<String> = DEFAULT_TEMPLATE,
        letter: String,
        keyWord: String
    ): List<String> {
        val startIndex = template.indexOf("car_this_letter_says").takeIf { it >= 0 } ?: 0
        val replayTemplate = template.subList(startIndex, template.size)
        return build(replayTemplate, letter, keyWord)
    }
}
