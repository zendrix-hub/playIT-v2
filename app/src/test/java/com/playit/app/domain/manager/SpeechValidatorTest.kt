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
    fun exactOrContainingTarget_returnsTrue() {
        assertTrue(speechValidator.validate("m", "m"))
        assertTrue(speechValidator.validate("mmm", "m"))
    }

    @Test
    fun acceptedPhoneticVariations_forLetterM_returnsTrue() {
        assertTrue(speechValidator.validate("mouse", "m"))
        assertTrue(speechValidator.validate("em", "m"))
    }

    @Test
    fun unrelatedText_returnsFalse() {
        assertFalse(speechValidator.validate("cat", "m"))
        assertFalse(speechValidator.validate("dog", "s"))
    }
}
