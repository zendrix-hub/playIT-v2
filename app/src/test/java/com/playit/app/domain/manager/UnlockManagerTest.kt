package com.playit.app.domain.manager

import com.playit.app.domain.model.LessonProgress
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UnlockManagerTest {

    private lateinit var unlockManager: UnlockManager

    @Before
    fun setUp() {
        unlockManager = UnlockManager()
    }

    @Test
    fun phonemeOne_unlockedByDefault() {
        assertTrue(unlockManager.isPhonemeUnlocked(phonemeId = 1, progressList = emptyList()))
    }

    @Test
    fun phonemeN_lockedWhenPreviousNotCompleted() {
        val progress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = false)
        )
        assertFalse(unlockManager.isPhonemeUnlocked(phonemeId = 2, progressList = progress))
    }

    @Test
    fun phonemeN_unlockedWhenPreviousCompleted() {
        val progress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = true)
        )
        assertTrue(unlockManager.isPhonemeUnlocked(phonemeId = 2, progressList = progress))
    }
}
