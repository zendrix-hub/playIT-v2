package com.playit.app.domain.manager

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Validates speech recognition output against target phonemes.
 * Specially calibrated for Grade 1 Filipino learners (ages 6-7) practicing English
 * phonics via the Marungko Sequence, accounting for child speech characteristics,
 * localized phoneme pronunciations, and Vosk offline transcription variations.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 2 / §6 FR-02, FR-03: Process Speech Input
 * and Enforce Pass Condition (Vosk baseline detection accuracy ≥75%).
 *
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 */
@Singleton
class SpeechValidator @Inject constructor() {

    /**
     * Map of each of the 28 Marungko phonemes to their accepted phonetic transcriptions,
     * letter names, phonetic sounds (onomatopoeias), and anchor curriculum words.
     */
    private val phonemeAcceptedVariants: Map<String, List<String>> = mapOf(
        "m" to listOf("m", "em", "mm", "mmm", "ma", "moo", "me", "mouse", "mata", "man"),
        "s" to listOf("s", "es", "ss", "sss", "sa", "see", "sea", "sun", "saging", "snake"),
        "a" to listOf("a", "ah", "ay", "ae", "eh", "apple", "aso", "ant"),
        "i" to listOf("i", "ih", "ee", "eye", "insect", "isda", "ink", "in"),
        "o" to listOf("o", "oh", "aw", "ow", "orange", "oras", "octopus", "on"),
        "b" to listOf("b", "be", "bee", "buh", "ba", "ball", "bata", "boy", "bat"),
        "e" to listOf("e", "eh", "ee", "elephant", "elepante", "egg", "elbow"),
        "u" to listOf("u", "uh", "oo", "you", "yu", "umbrella", "ulan", "up"),
        "t" to listOf("t", "te", "tee", "tuh", "ta", "tiger", "talong", "top", "ten"),
        "k" to listOf("k", "ka", "kay", "kuh", "kite", "kambing", "key", "kid"),
        "l" to listOf("l", "el", "ll", "luh", "la", "lion", "lapis", "leg", "lamp"),
        "y" to listOf("y", "why", "ye", "yuh", "ya", "yoyo", "yes", "yellow"),
        "n" to listOf("n", "en", "nn", "nuh", "na", "nest", "nars", "net", "nut"),
        "g" to listOf("g", "gee", "guh", "ga", "goat", "gatas", "girl", "gum"),
        "ng" to listOf("ng", "eng", "ing", "ung", "ang", "ring", "ngipin", "sing", "song"),
        "p" to listOf("p", "pe", "pee", "puh", "pa", "pig", "pusa", "pen", "pot"),
        "r" to listOf("r", "ar", "er", "ruh", "ra", "rabbit", "relo", "red", "run"),
        "d" to listOf("d", "de", "dee", "duh", "da", "dog", "dahon", "duck", "door"),
        "h" to listOf("h", "aitch", "he", "huh", "ha", "hat", "halaman", "hen", "hot"),
        "w" to listOf("w", "double", "wa", "wuh", "watch", "watawat", "water", "win"),
        "c" to listOf("c", "see", "ce", "kuh", "ca", "cat", "car", "cup", "can"),
        "f" to listOf("f", "ef", "ff", "fuh", "fa", "fish", "fan", "fox"),
        "j" to listOf("j", "jay", "je", "juh", "ja", "jug", "jam", "jar", "jet"),
        "ñ" to listOf("ñ", "enya", "nye", "nya", "nino", "piña", "pina", "niño"),
        "q" to listOf("q", "cue", "kyoo", "kuh", "kwa", "queen", "quilt", "quick"),
        "v" to listOf("v", "ve", "vee", "vuh", "va", "van", "vase", "vest"),
        "x" to listOf("x", "ex", "eks", "box", "fox", "six", "xray", "x-ray"),
        "z" to listOf("z", "zed", "zee", "zuh", "za", "zebra", "zoo", "zip")
    )

    /**
     * Validates if the recognized speech transcript corresponds to the target phoneme.
     *
     * @param recognizedText Raw text output from speech recognition.
     * @param targetPhoneme Target letter or phoneme identifier (e.g. "m", "ng", "ñ").
     * @return true if speech matches the phoneme's accepted variations or phonetic profile.
     */
    fun validate(recognizedText: String?, targetPhoneme: String): Boolean {
        if (recognizedText.isNullOrBlank()) return false

        val cleanText = recognizedText.lowercase().trim()
        val cleanTarget = targetPhoneme.lowercase().trim()

        // 1. Exact match or direct substring match
        if (cleanText == cleanTarget) return true

        val acceptedList = phonemeAcceptedVariants[cleanTarget] ?: listOf(cleanTarget)

        // 2. Tokenize transcript into individual words and clean tokens
        val tokens = cleanText.split(Regex("[\\s,.-]+")).filter { it.isNotBlank() }

        // 3. Direct match against accepted list
        for (accepted in acceptedList) {
            if (cleanText == accepted) return true
            if (tokens.contains(accepted)) return true
            // If the transcript contains the anchor word or letter name
            if (cleanText.contains(accepted) && accepted.length >= 3) return true
        }

        // 4. Starting phonetic match (e.g. saying "muh" or "ma" for 'm')
        for (token in tokens) {
            if (token.startsWith(cleanTarget) && token.length <= cleanTarget.length + 2) {
                return true
            }
            // 5. Fuzzy edit distance for slight acoustic transcription variances
            for (accepted in acceptedList) {
                if (accepted.length >= 3 && levenshteinDistance(token, accepted) <= 1) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Calculates the Levenshtein distance between two character sequences.
     */
    private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }
}
