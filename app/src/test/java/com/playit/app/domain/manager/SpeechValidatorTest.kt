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
    fun fuzzyTolerance_handlesMinorVoskVariances() {
        // "elphant" (1 char missing from elephant) should match 'e'
        assertTrue(speechValidator.validate("elphant", "e"))
        // "rabit" (1 char missing from rabbit) should match 'r'
        assertTrue(speechValidator.validate("rabit", "r"))
    }
}
