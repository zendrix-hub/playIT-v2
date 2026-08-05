package com.playit.app.domain.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechValidator @Inject constructor() {
    fun validate(recognizedText: String?, targetPhoneme: String): Boolean {
        if (recognizedText.isNullOrBlank()) return false

        val cleanText = recognizedText.lowercase().trim()
        val cleanTarget = targetPhoneme.lowercase().trim()

        if (cleanText.contains(cleanTarget)) return true

        // Accept common phonetic spellings / sounds for letter M
        if (cleanTarget == "m" && (cleanText.contains("em") || cleanText.contains("mm") || cleanText.contains("mouse"))) {
            return true
        }

        return false
    }
}
