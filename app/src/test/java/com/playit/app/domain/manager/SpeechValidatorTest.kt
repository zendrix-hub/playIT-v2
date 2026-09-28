package com.playit.app.domain.manager

import com.playit.app.domain.model.SpeechErrorType
import org.junit.Assert.assertEquals
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
    fun legacyNgEnye_exactMatch_returnsTrue() {
        assertTrue(speechValidator.validate("ng", "ng"))
        assertTrue(speechValidator.validate("ñ", "ñ"))
    }

    @Test
    fun judgeSound_letterName_rejected() {
        val cases = listOf(
            Triple("em", "m", 600),
            Triple("es", "s", 600),
            Triple("bee", "b", 600),
            Triple("zee", "z", 600),
            Triple("ay", "a", 600),
            Triple("m", "m", 600)
        )
        for ((text, letter, duration) in cases) {
            val result = speechValidator.judgeSound(text, letter, duration)
            assertFalse("Expected isCorrect false for ($text, $letter, $duration)", result.isCorrect)
            assertEquals("Expected LETTER_NAME for ($text, $letter, $duration)", SpeechErrorType.LETTER_NAME, result.errorType)
        }
    }

    @Test
    fun judgeSound_addedVowel_rejected() {
        val cases = listOf(
            Triple("ma", "m", 600),
            Triple("muh", "m", 600),
            Triple("sa", "s", 600),
            Triple("buh", "b", 600)
        )
        for ((text, letter, duration) in cases) {
            val result = speechValidator.judgeSound(text, letter, duration)
            assertFalse("Expected isCorrect false for ($text, $letter, $duration)", result.isCorrect)
            assertEquals("Expected ADDED_VOWEL for ($text, $letter, $duration)", SpeechErrorType.ADDED_VOWEL, result.errorType)
        }
    }

    @Test
    fun judgeSound_substitution_rejected() {
        val cases = listOf(
            Triple("pa", "f", 600),
            Triple("ba", "v", 600),
            Triple("sa", "z", 600)
        )
        for ((text, letter, duration) in cases) {
            val result = speechValidator.judgeSound(text, letter, duration)
            assertFalse("Expected isCorrect false for ($text, $letter, $duration)", result.isCorrect)
            assertEquals("Expected SUBSTITUTION for ($text, $letter, $duration)", SpeechErrorType.SUBSTITUTION, result.errorType)
        }
    }

    @Test
    fun judgeSound_anchorWord_isOtherWord() {
        val result = speechValidator.judgeSound("mouse", "m", 600)
        assertFalse(result.isCorrect)
        assertEquals(SpeechErrorType.OTHER_WORD, result.errorType)
    }

    @Test
    fun judgeSound_sustainedContinuous_accepted() {
        val case1 = speechValidator.judgeSound("", "m", 600)
        assertTrue(case1.isCorrect)
        assertEquals(SpeechErrorType.NONE, case1.errorType)

        val case2 = speechValidator.judgeSound("[unk]", "s", 450)
        assertTrue(case2.isCorrect)
        assertEquals(SpeechErrorType.NONE, case2.errorType)
    }

    @Test
    fun judgeSound_tooShort_rejected() {
        val result = speechValidator.judgeSound("", "m", 200)
        assertFalse(result.isCorrect)
        assertEquals(SpeechErrorType.NO_SPEECH, result.errorType)
    }

    @Test
    fun judgeSound_stop_neverAcceptedBySustain() {
        val result = speechValidator.judgeSound("", "b", 800)
        assertFalse(result.isCorrect)
        assertEquals(SpeechErrorType.UNCONFIRMED, result.errorType)
    }

    @Test
    fun judgeSound_noDuration_unconfirmed() {
        val result = speechValidator.judgeSound("", "m", null)
        assertFalse(result.isCorrect)
        assertEquals(SpeechErrorType.UNCONFIRMED, result.errorType)
    }

    @Test
    fun validate_prefixRuleRemoved() {
        assertFalse(speechValidator.validate("mo", "m"))
        assertFalse(speechValidator.validate("muh", "m"))
    }

    @Test
    fun judgeWord_paths() {
        val case1 = speechValidator.judgeWord("mouse", "mouse", "m")
        assertTrue(case1.isCorrect)
        assertEquals(SpeechErrorType.NONE, case1.errorType)

        val case2 = speechValidator.judgeWord("em", "mouse", "m")
        assertFalse(case2.isCorrect)
        assertEquals(SpeechErrorType.LETTER_NAME, case2.errorType)

        val case3 = speechValidator.judgeWord("ma", "mouse", "m")
        assertFalse(case3.isCorrect)
        assertEquals(SpeechErrorType.ADDED_VOWEL, case3.errorType)

        val case4 = speechValidator.judgeWord("cat", "mouse", "m")
        assertFalse(case4.isCorrect)
        assertEquals(SpeechErrorType.OTHER_WORD, case4.errorType)

        val case5 = speechValidator.judgeWord("", "mouse", "m")
        assertFalse(case5.isCorrect)
        assertEquals(SpeechErrorType.NO_SPEECH, case5.errorType)
    }

    @Test
    fun grammarFor_wordMode_hasFoilsNotGenericDecoys() {
        val grammar = speechValidator.grammarFor("m", "mouse")
        assertTrue(grammar.contains("mouse"))
        assertTrue(grammar.contains("em"))
        assertTrue(grammar.contains("ma"))
        assertFalse(grammar.contains("cat"))
        assertFalse(grammar.contains("dog"))
        assertFalse(grammar.contains("yes"))
        assertFalse(grammar.contains("no"))
    }

    @Test
    fun grammarFor_soundMode_hasNoAcceptedTokens() {
        val grammar = speechValidator.grammarFor("m", null)
        assertTrue(grammar.contains("em"))
        assertTrue(grammar.contains("ma"))
        assertFalse(grammar.contains("mm"))
        assertFalse(grammar.contains("mmm"))
        assertFalse(grammar.contains("mouse"))
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
