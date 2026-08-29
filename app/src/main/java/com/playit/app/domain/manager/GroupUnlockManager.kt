package com.playit.app.domain.manager

import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.LetterGroupMember
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupUnlockManager @Inject constructor() {

    /**
     * Group N letter exploration is unlocked when all members of Group N-1 are completed.
     * Group 1 is unlocked by default.
     */
    fun isGroupUnlocked(
        groupId: Int,
        groupMembers: List<LetterGroupMember>,
        lessonProgressList: List<LessonProgress>
    ): Boolean {
        // Group 1 is unlocked by default
        if (groupId == 1) return true

        // Group N is unlocked when all members of Group N-1 are completed
        val prevGroupId = groupId - 1
        val prevGroupMembers = groupMembers.filter { it.groupId == prevGroupId }

        if (prevGroupMembers.isEmpty()) return false

        return prevGroupMembers.all { member ->
            val progress = lessonProgressList.find { it.phonemeId == member.phonemeId }
            progress?.isCompleted == true
        }
    }

    /**
     * Blend It checkpoint for Group N unlocks ONLY when all letter members of Group N
     * have been completed (mastered / passed all 3 sublevels).
     */
    fun isBlendItUnlocked(
        groupId: Int,
        groupMembers: List<LetterGroupMember>,
        lessonProgressList: List<LessonProgress>
    ): Boolean {
        val currentGroupMembers = groupMembers.filter { it.groupId == groupId }
        if (currentGroupMembers.isEmpty()) return false

        return currentGroupMembers.all { member ->
            val progress = lessonProgressList.find { it.phonemeId == member.phonemeId }
            progress?.isCompleted == true
        }
    }
}
