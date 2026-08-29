package com.playit.app.domain.manager

import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.LetterGroupMember
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GroupUnlockManagerTest {

    private lateinit var groupUnlockManager: GroupUnlockManager

    @Before
    fun setUp() {
        groupUnlockManager = GroupUnlockManager()
    }

    @Test
    fun groupOne_unlockedByDefault() {
        assertTrue(
            groupUnlockManager.isGroupUnlocked(
                groupId = 1,
                groupMembers = emptyList(),
                lessonProgressList = emptyList()
            )
        )
    }

    @Test
    fun groupN_unlockedOnlyWhenAllPreviousMembersCompleted() {
        val groupMembers = listOf(
            LetterGroupMember(groupId = 1, phonemeId = 1, position = 1),
            LetterGroupMember(groupId = 1, phonemeId = 2, position = 2),
            LetterGroupMember(groupId = 1, phonemeId = 3, position = 3),
            LetterGroupMember(groupId = 1, phonemeId = 4, position = 4)
        )

        val partialProgress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 2, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 3, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 4, isCompleted = false)
        )

        assertFalse(
            groupUnlockManager.isGroupUnlocked(
                groupId = 2,
                groupMembers = groupMembers,
                lessonProgressList = partialProgress
            )
        )

        val fullProgress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 2, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 3, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 4, isCompleted = true)
        )

        assertTrue(
            groupUnlockManager.isGroupUnlocked(
                groupId = 2,
                groupMembers = groupMembers,
                lessonProgressList = fullProgress
            )
        )
    }

    @Test
    fun isBlendItUnlocked_lockedWhenGroupLettersNotCompleted() {
        val groupMembers = listOf(
            LetterGroupMember(groupId = 1, phonemeId = 1, position = 1),
            LetterGroupMember(groupId = 1, phonemeId = 2, position = 2),
            LetterGroupMember(groupId = 1, phonemeId = 3, position = 3),
            LetterGroupMember(groupId = 1, phonemeId = 4, position = 4)
        )

        val emptyProgress = emptyList<LessonProgress>()

        // BlendIt Group 1 is LOCKED by default if no letters completed
        assertFalse(
            groupUnlockManager.isBlendItUnlocked(
                groupId = 1,
                groupMembers = groupMembers,
                lessonProgressList = emptyProgress
            )
        )

        val partialProgress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 2, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 3, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 4, isCompleted = false)
        )

        assertFalse(
            groupUnlockManager.isBlendItUnlocked(
                groupId = 1,
                groupMembers = groupMembers,
                lessonProgressList = partialProgress
            )
        )
    }

    @Test
    fun isBlendItUnlocked_unlockedWhenAllGroupLettersCompleted() {
        val groupMembers = listOf(
            LetterGroupMember(groupId = 1, phonemeId = 1, position = 1),
            LetterGroupMember(groupId = 1, phonemeId = 2, position = 2),
            LetterGroupMember(groupId = 1, phonemeId = 3, position = 3),
            LetterGroupMember(groupId = 1, phonemeId = 4, position = 4)
        )

        val fullProgress = listOf(
            LessonProgress(profileId = 1L, phonemeId = 1, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 2, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 3, isCompleted = true),
            LessonProgress(profileId = 1L, phonemeId = 4, isCompleted = true)
        )

        assertTrue(
            groupUnlockManager.isBlendItUnlocked(
                groupId = 1,
                groupMembers = groupMembers,
                lessonProgressList = fullProgress
            )
        )
    }
}
