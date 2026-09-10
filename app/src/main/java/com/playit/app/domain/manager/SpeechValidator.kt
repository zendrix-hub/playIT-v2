package com.playit.app.domain.manager

import javax.inject.Inject
import javax.inject.Singleton

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
        "m" to listOf("m", "em", "muh", "mm", "mmm", "ma", "me", "moo", "um", "am", "mom", "him", "ham", "mouse", "mata", "man"),
        "s" to listOf("s", "es", "ss", "sss", "se", "see", "say", "so", "sa", "sea", "sun", "saging", "snake"),
        "a" to listOf("a", "ah", "ay", "aah", "aa", "ae", "eh", "apple", "at", "an", "aso", "ant"),
        "i" to listOf("i", "ih", "ee", "eye", "iii", "ihhh", "iiih", "iy", "insect", "isda", "ink", "in"),
        "o" to listOf("o", "oh", "oo", "aw", "ow", "or", "on", "of", "orange", "oras", "octopus"),
        "b" to listOf("b", "be", "bee", "buh", "bb", "by", "bay", "ba", "ball", "bata", "boy", "bat"),
        "e" to listOf("e", "eh", "ee", "ay", "elephant", "elepante", "egg", "end", "every", "elbow"),
        "u" to listOf("u", "uh", "oo", "yoo", "you", "yu", "up", "us", "under", "umbrella", "ulan"),
        "t" to listOf("t", "te", "tee", "tuh", "tt", "to", "too", "the", "tea", "ta", "tiger", "talong", "top", "ten"),
        "k" to listOf("k", "ka", "kay", "kuh", "key", "ca", "ok", "kite", "kambing", "kid"),
        "l" to listOf("l", "el", "ll", "lll", "luh", "la", "lay", "lee", "lion", "lapis", "leg", "lamp"),
        "y" to listOf("y", "why", "ye", "ya", "yuh", "yes", "yay", "you", "yoyo", "yellow"),
        "n" to listOf("n", "en", "nn", "nnn", "nuh", "na", "no", "now", "knee", "nest", "nars", "net", "nut"),
        "g" to listOf("g", "ge", "gee", "guh", "gg", "go", "ga", "goat", "gatas", "girl", "gum"),
        "ng" to listOf("ng", "eng", "ing", "ung", "ang", "ring", "sing", "king", "ngipin", "song"),
        "p" to listOf("p", "pe", "pee", "puh", "pp", "pay", "pie", "pa", "pig", "pusa", "pen", "pot"),
        "r" to listOf("r", "ar", "er", "rr", "rrr", "ray", "row", "rah", "ruh", "ra", "rabbit", "relo", "red", "run"),
        "d" to listOf("d", "de", "dee", "duh", "dd", "day", "do", "da", "dog", "dahon", "duck", "door"),
        "h" to listOf("h", "ha", "huh", "hh", "hay", "he", "hi", "aitch", "hat", "halaman", "hen", "hot"),
        "w" to listOf("w", "wa", "wuh", "dub", "way", "we", "why", "double", "watch", "watawat", "water", "win"),
        "c" to listOf("c", "see", "ce", "se", "kuh", "ca", "cow", "cup", "cat", "car", "can"),
        "f" to listOf("f", "ef", "ff", "fff", "fee", "few", "far", "fuh", "fa", "fish", "fan", "fox"),
        "j" to listOf("j", "jay", "je", "juh", "jaw", "joy", "joe", "ja", "jug", "jam", "jar", "jet"),
        "ñ" to listOf("ñ", "ny", "ni", "enya", "nye", "nya", "nino", "piña", "pina", "niño", "canyon", "onion"),
        "q" to listOf("q", "cue", "kyoo", "kw", "kwa", "queue", "quit", "queen", "quilt", "quick"),
        "v" to listOf("v", "ve", "vee", "vuh", "van", "very", "via", "va", "vase", "vest"),
        "x" to listOf("x", "ex", "eks", "zz", "exit", "extra", "box", "fox", "six", "xray", "x-ray"),
        "z" to listOf("z", "zed", "zee", "ze", "zuh", "zoo", "zero", "za", "zebra", "zip")
    )

    fun getAcceptedVariants(targetPhoneme: String): List<String> {
        val clean = targetPhoneme.lowercase().trim()
        return phonemeAcceptedVariants[clean] ?: listOf(clean)
    }

    /**
     * Word-mode accepted variants, keyed by the lowercase example word seeded per phoneme
     * (di/DatabaseModule.kt `exampleWord` column — 26 letters; ng/ñ are SME-pending).
     *
     * Calibration policy (same spirit as CB-1 strictness — no fuzzy/prefix tolerance):
     * - Entries must be whole-word renderings of the SAME target word only. Never letter
     *   names ("m", "muh", "em"), prefixes, syllable fragments, Tagalog words, or other
     *   curriculum words (BlendIt words, other letters' example words).
     * - Additions require an observed on-device Vosk transcript or SME input, backed by a
     *   unit test each.
     * Exceptions: "yoyo" also accepts "yo-yo" (the transcript tokenizer splits on '-', so
     * the exact-match branch needs the hyphenated spelling to accept the natural utterance).
     */
    private val wordAcceptedVariants: Map<String, List<String>> = mapOf(
        "apple" to listOf("apple"),
        "ball" to listOf("ball"),
        "box" to listOf("box"),
        "cat" to listOf("cat"),
        "dog" to listOf("dog"),
        "elephant" to listOf("elephant"),
        "fish" to listOf("fish"),
        "goat" to listOf("goat"),
        "hat" to listOf("hat"),
        "insect" to listOf("insect"),
        "jug" to listOf("jug"),
        "kite" to listOf("kite"),
        "lion" to listOf("lion"),
        "mouse" to listOf("mouse"),
        "nest" to listOf("nest"),
        "orange" to listOf("orange"),
        "pig" to listOf("pig"),
        "queen" to listOf("queen"),
        "rabbit" to listOf("rabbit"),
        "sun" to listOf("sun"),
        "tiger" to listOf("tiger"),
        "umbrella" to listOf("umbrella"),
        "van" to listOf("van"),
        "watch" to listOf("watch"),
        "yoyo" to listOf("yoyo", "yo-yo"),
        "zebra" to listOf("zebra")
    )

    /**
     * Returns the accepted whole-word variants for a target example word (lowercased).
     * Unknown words fall back to the canonical word itself so the grammar stays scoped.
     */
    fun getAcceptedWordVariants(targetWord: String): List<String> {
        val clean = targetWord.lowercase().trim()
        return wordAcceptedVariants[clean] ?: listOf(clean)
    }

    /**
     * Validates whether the recognized speech transcript is the target example WORD.
     *
     * Whole-word mode (Say It word lessons): the transcript must be the target word or a
     * curated variant of it. Letter sounds alone ("m", "muh", "em"), prefixes, misspellings,
     * and other words do NOT pass — there is deliberately no prefix/length/Levenshtein
     * tolerance here (per CB-1), so "s" can never satisfy "sun".
     *
     * @param recognizedText Raw text output from speech recognition.
     * @param targetWord Target example word identifier (e.g. "mouse", "sun", "elephant").
     * @return true if the transcript is the target word (or a curated variant of it).
     */
    fun validateWord(recognizedText: String?, targetWord: String): Boolean {
        if (recognizedText.isNullOrBlank()) return false

        val cleanText = recognizedText.lowercase().trim()
        val cleanTarget = targetWord.lowercase().trim()

        // 1. Exact match
        if (cleanText == cleanTarget) return true

        val acceptedList = getAcceptedWordVariants(cleanTarget)

        // 2. Tokenize transcript into individual words and clean tokens
        val tokens = cleanText.split(Regex("[\\s,.-]+")).filter { it.isNotBlank() }

        // 3. Direct match against accepted whole-word variants
        for (accepted in acceptedList) {
            if (cleanText == accepted) return true
            if (tokens.contains(accepted)) return true
        }

        // 4. No fuzzy/prefix/partial tolerance — whole word required
        return false
    }

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
        }

        // 4. Starting phonetic match (e.g. saying "muh" or "ma" for 'm')
        for (token in tokens) {
            if (token.startsWith(cleanTarget) && token.length <= cleanTarget.length + 2) {
                return true
            }
        }

        return false
    }
}
