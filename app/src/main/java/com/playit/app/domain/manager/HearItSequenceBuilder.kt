package com.playit.app.domain.manager

/**
 * Builds the Hear It modeling sequence (I-do step) from a template.
 *
 * Implements FR-02 (hear-say-refactor.md §2.1 Table 3 / §6.2).
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 * Asset paths come from the caller (AudioResolver), so path rules live in one place.
 */
object HearItSequenceBuilder {

    // Spec §2.1 Table 3 / §6.2, without the SHOW_LETTER animation token.
    val TEMPLATE = listOf(
        "car_listen", "car_this_letter_says",
        "PHONEME", "PAUSE_500", "PHONEME", "PAUSE_500", "PHONEME",
        "KEYWORD", "PHONEME", "car_say_it_with_me"
    )

    private val PAUSE_TOKEN = Regex("PAUSE_(\\d+)")

    /** Maps tokens to asset paths. car_* -> tutorPath(id); PHONEME -> phonemePath;
     *  KEYWORD -> keyWordPath (dropped when null); PAUSE_<ms> stays as-is. */
    fun build(
        template: List<String>,
        phonemePath: String,
        keyWordPath: String?,
        tutorPath: (String) -> String
    ): List<String> = template.mapNotNull { token ->
        when {
            token.startsWith("car_") -> tutorPath(token)
            token == "PHONEME" -> phonemePath
            token == "KEYWORD" -> keyWordPath
            else -> token
        }
    }

    /** Steps 3 to 6 for the ear button: from "car_this_letter_says" through the last PHONEME. */
    fun replayTemplate(template: List<String> = TEMPLATE): List<String> {
        val start = template.indexOf("car_this_letter_says")
        val end = template.lastIndexOf("PHONEME")
        if (start < 0 || end < start) return emptyList()
        return template.subList(start, end + 1)
    }

    /** 500 for "PAUSE_500"; null for anything that is not a PAUSE_<ms> token. */
    fun pauseMillis(entry: String): Long? =
        PAUSE_TOKEN.matchEntire(entry)?.groupValues?.get(1)?.toLongOrNull()
}
