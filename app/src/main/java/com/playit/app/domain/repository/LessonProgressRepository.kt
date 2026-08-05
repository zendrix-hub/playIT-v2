package com.playit.app.domain.repository

import com.playit.app.domain.model.LessonProgress
import kotlinx.coroutines.flow.Flow

interface LessonProgressRepository {
    fun getProgressForProfile(profileId: Long): Flow<List<LessonProgress>>
    suspend fun getProgressForPhoneme(profileId: Long, phonemeId: Int): LessonProgress?
    suspend fun saveProgress(progress: LessonProgress)
}
