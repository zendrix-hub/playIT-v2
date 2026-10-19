package com.playit.app.domain.manager

/** On-screen caption for a clip being played (NFR-ACC-01): what the child would hear, as text. */
object CaptionText {
    private val carriers = mapOf(
        "car_listen" to "Listen!", "car_this_letter_says" to "This letter says...",
        "car_say_it_with_me" to "Say it with me!", "car_your_turn" to "Your turn!",
        "car_watch_my_lips" to "Watch my lips.", "car_lets_say_together" to "Let's say it together.",
        "fb_try_later" to "Nice try! We'll practice this one again later.",
    )

    /** Null for pauses and for clips with no caption (sound effects, unknown lines). */
    fun forClip(path: String, letter: String, word: String): String? {
        val file = path.substringAfterLast('/').substringBeforeLast('.')
        return when {
            path.startsWith("pause:") || HearItSequenceBuilder.pauseMillis(path) != null -> null
            path.contains("/phonemes/") -> "/$letter/"
            path.contains("/keywords/") -> word
            else -> carriers[file]
        }
    }
}
