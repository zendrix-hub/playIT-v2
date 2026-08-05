package com.playit.app.domain.manager

import com.playit.app.domain.model.LessonProgress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnlockManager @Inject constructor() {

    fun isPhonemeUnlocked(phonemeId: Int, progressList: List<LessonProgress>): Boolean {
        // Phoneme 1 (Letter M) is unlocked by default
        if (phonemeId == 1) return true

        // Phoneme N unlocks when Phoneme N-1 is completed
        val previousPhonemeId = phonemeId - 1
        val previousProgress = progressList.find { it.phonemeId == previousPhonemeId }
        return previousProgress?.isCompleted == true
    }
}
