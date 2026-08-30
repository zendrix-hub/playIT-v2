package com.playit.app.data.repository

import com.playit.app.data.local.dao.LessonProgressDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.data.local.entity.toEntity
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.repository.LessonProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonProgressRepositoryImpl @Inject constructor(
    private val lessonProgressDao: LessonProgressDao
) : LessonProgressRepository {

    override fun getProgressForProfile(profileId: Long): Flow<List<LessonProgress>> {
        return lessonProgressDao.getProgressForProfile(profileId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProgressForPhoneme(profileId: Long, phonemeId: Int): LessonProgress? {
        return lessonProgressDao.getProgressForPhoneme(profileId, phonemeId)?.toDomain()
    }

    override suspend fun saveProgress(progress: LessonProgress) {
        lessonProgressDao.saveProgress(progress.toEntity())
    }
}
