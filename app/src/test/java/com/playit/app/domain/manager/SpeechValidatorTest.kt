package com.playit.app.domain.manager

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpeechValidatorTest {

    private lateinit var speechValidator: SpeechValidator

    @Before
    fun setUp() {
        speechValidator = SpeechValidator()
    }

    @Test
    fun nullOrBlankText_returnsFalse() {
        assertFalse(speechValidator.validate(null, "m"))
        assertFalse(speechValidator.validate("", "m"))
        assertFalse(speechValidator.validate("   ", "m"))
    }

    @Test
    fun exactLetterMatch_returnsTrue() {
        assertTrue(speechValidator.validate("m", "m"))
        assertTrue(speechValidator.validate("s", "s"))
        assertTrue(speechValidator.validate("a", "a"))
        assertTrue(speechValidator.validate("z", "z"))
        assertTrue(speechValidator.validate("ng", "ng"))
        assertTrue(speechValidator.validate("ñ", "ñ"))
    }

    @Test
    fun letterNameVariations_returnTrue() {
        assertTrue(speechValidator.validate("em", "m"))
        assertTrue(speechValidator.validate("es", "s"))
        assertTrue(speechValidator.validate("bee", "b"))
        assertTrue(speechValidator.validate("aitch", "h"))
        assertTrue(speechValidator.validate("kay", "k"))
        assertTrue(speechValidator.validate("cue", "q"))
        assertTrue(speechValidator.validate("double", "w"))
        assertTrue(speechValidator.validate("ex", "x"))
        assertTrue(speechValidator.validate("why", "y"))
        assertTrue(speechValidator.validate("zed", "z"))
        assertTrue(speechValidator.validate("zee", "z"))
    }

    @Test
    fun phonicsSoundOnomatopoeias_returnTrue() {
        assertTrue(speechValidator.validate("mmm", "m"))
        assertTrue(speechValidator.validate("sss", "s"))
        assertTrue(speechValidator.validate("buh", "b"))
        assertTrue(speechValidator.validate("duh", "d"))
        assertTrue(speechValidator.validate("fuh", "f"))
        assertTrue(speechValidator.validate("guh", "g"))
        assertTrue(speechValidator.validate("huh", "h"))
        assertTrue(speechValidator.validate("kuh", "k"))
        assertTrue(speechValidator.validate("luh", "l"))
        assertTrue(speechValidator.validate("puh", "p"))
        assertTrue(speechValidator.validate("tuh", "t"))
        assertTrue(speechValidator.validate("vuh", "v"))
        assertTrue(speechValidator.validate("wuh", "w"))
        assertTrue(speechValidator.validate("zuh", "z"))
        assertTrue(speechValidator.validate("eng", "ng"))
        assertTrue(speechValidator.validate("enya", "ñ"))
    }

    @Test
    fun anchorCurriculumWords_returnTrue() {
        assertTrue(speechValidator.validate("apple", "a"))
        assertTrue(speechValidator.validate("ball", "b"))
        assertTrue(speechValidator.validate("cat", "c"))
        assertTrue(speechValidator.validate("dog", "d"))
        assertTrue(speechValidator.validate("elephant", "e"))
        assertTrue(speechValidator.validate("fish", "f"))
        assertTrue(speechValidator.validate("goat", "g"))
        assertTrue(speechValidator.validate("hat", "h"))
        assertTrue(speechValidator.validate("insect", "i"))
        assertTrue(speechValidator.validate("jug", "j"))
        assertTrue(speechValidator.validate("kite", "k"))
        assertTrue(speechValidator.validate("lion", "l"))
        assertTrue(speechValidator.validate("mouse", "m"))
        assertTrue(speechValidator.validate("nest", "n"))
        assertTrue(speechValidator.validate("orange", "o"))
        assertTrue(speechValidator.validate("pig", "p"))
        assertTrue(speechValidator.validate("queen", "q"))
        assertTrue(speechValidator.validate("rabbit", "r"))
        assertTrue(speechValidator.validate("sun", "s"))
        assertTrue(speechValidator.validate("tiger", "t"))
        assertTrue(speechValidator.validate("umbrella", "u"))
        assertTrue(speechValidator.validate("van", "v"))
        assertTrue(speechValidator.validate("watch", "w"))
        assertTrue(speechValidator.validate("box", "x"))
        assertTrue(speechValidator.validate("yoyo", "y"))
        assertTrue(speechValidator.validate("zebra", "z"))
        assertTrue(speechValidator.validate("ring", "ng"))
        assertTrue(speechValidator.validate("piña", "ñ"))
    }

    @Test
    fun distractorRejection_returnsFalse() {
        assertFalse(speechValidator.validate("cat", "m"))
        assertFalse(speechValidator.validate("dog", "s"))
        assertFalse(speechValidator.validate("apple", "z"))
        assertFalse(speechValidator.validate("fish", "a"))
        assertFalse(speechValidator.validate("ball", "r"))
    }

    @Test
    fun phonemeMode_rejectsMisspelledAnchorWords_afterCB1() {
        // Post-CB-1 strictness: no Levenshtein/substring tolerance — misspelled
        // anchor words must NOT pass phoneme mode (replaces the stale fuzzy test).
        assertFalse(speechValidator.validate("elphant", "e"))
        assertFalse(speechValidator.validate("rabit", "r"))
    }

    // ────────────────────────────────────────────────────────────────────────
    // Word mode (Say It word lessons — child utters the letter's example word)
    // ────────────────────────────────────────────────────────────────────────

    private val seededExampleWords = listOf(
        "apple", "ball", "box", "cat", "dog", "elephant", "fish", "goat", "hat",
        "insect", "jug", "kite", "lion", "mouse", "nest", "orange", "pig", "queen",
        "rabbit", "sun", "tiger", "umbrella", "van", "watch", "yoyo", "zebra"
    )

    @Test
    fun wordExactMatch_returnsTrue_forAll26ExampleWords() {
        // Guards against desync between wordAcceptedVariants and the seeded
        // exampleWord values in di/DatabaseModule.kt.
        seededExampleWords.forEach { word ->
            assertTrue(
                "validateWord(\"$word\", \"$word\") should pass",
                speechValidator.validateWord(word, word)
            )
        }
    }

    @Test
    fun wordAcceptedVariants_hasEntryForEverySeededExampleWord() {
        seededExampleWords.forEach { word ->
            val variants = speechValidator.getAcceptedWordVariants(word)
            assertTrue("Missing variant entry for '$word'", variants.isNotEmpty())
            assertTrue("Variant entry for '$word' must include the canonical word", variants.contains(word))
        }
    }

    @Test
    fun wordMode_caseAndWhitespaceInsensitive() {
        assertTrue(speechValidator.validateWord("  Mouse ", "mouse"))
        assertTrue(speechValidator.validateWord("MOUSE", "mouse"))
        assertTrue(speechValidator.validateWord("mouse.", "mouse"))
        assertTrue(speechValidator.validateWord("Sun", "sun"))
    }

    @Test
    fun wordMode_hyphenatedYoyoVariant_returnsTrue() {
        // The transcript tokenizer splits on '-', so the curated "yo-yo" spelling
        // must pass via the exact-match branch.
        assertTrue(speechValidator.validateWord("yo-yo", "yoyo"))
    }

    @Test
    fun wordMode_rejectsLetterSoundOnly() {
        // Whole word required — letter names, phonic sounds, and onomatopoeias fail.
        assertFalse(speechValidator.validateWord("m", "mouse"))
        assertFalse(speechValidator.validateWord("muh", "mouse"))
        assertFalse(speechValidator.validateWord("em", "mouse"))
        assertFalse(speechValidator.validateWord("mmm", "mouse"))
        assertFalse(speechValidator.validateWord("s", "sun"))
        assertFalse(speechValidator.validateWord("sss", "sun"))
        assertFalse(speechValidator.validateWord("e", "elephant"))
        assertFalse(speechValidator.validateWord("kuh", "cat"))
    }

    @Test
    fun wordMode_rejectsPartialOrMisspelledWords() {
        // No prefix/length/Levenshtein tolerance in word mode (CB-1 consistent).
        assertFalse(speechValidator.validateWord("elphant", "elephant"))
        assertFalse(speechValidator.validateWord("rabit", "rabbit"))
        assertFalse(speechValidator.validateWord("mous", "mouse"))
        assertFalse(speechValidator.validateWord("sunny", "sun"))
        assertFalse(speechValidator.validateWord("appl", "apple"))
    }

    @Test
    fun wordMode_rejectsOtherWordsAndMinimalPairs() {
        assertFalse(speechValidator.validateWord("sub", "sun"))
        assertFalse(speechValidator.validateWord("cat", "mouse"))
        assertFalse(speechValidator.validateWord("ball", "hat"))
        assertFalse(speechValidator.validateWord("box", "fox"))
        assertFalse(speechValidator.validateWord("queen", "zebra"))
    }

    @Test
    fun wordMode_unknownTargetWord_fallsBackToCanonicalOnly() {
        // Unknown target falls back to listOf(clean) — exact canonical match only.
        assertTrue(speechValidator.validateWord("aardvark", "aardvark"))
        assertFalse(speechValidator.validateWord("anything", "aardvark"))
    }

    @Test
    fun wordMode_nullOrBlankTranscript_returnsFalse() {
        assertFalse(speechValidator.validateWord(null, "mouse"))
        assertFalse(speechValidator.validateWord("", "mouse"))
        assertFalse(speechValidator.validateWord("   ", "mouse"))
    }
}
