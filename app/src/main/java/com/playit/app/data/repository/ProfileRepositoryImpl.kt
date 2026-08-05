package com.playit.app.data.repository

import com.playit.app.data.local.dao.ProfileDao
import com.playit.app.data.local.entity.ProfileEntity
import com.playit.app.data.local.entity.toDomain
import com.playit.app.data.local.entity.toEntity
import com.playit.app.domain.model.GameplayConstants
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun getAllProfiles(): Flow<List<Profile>> {
        return profileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProfileById(id: Long): Profile? {
        return profileDao.getProfileById(id)?.toDomain()
    }

    override suspend fun createProfile(name: String, avatarResId: Int): Result<Long> {
        val count = profileDao.getProfileCount()
        if (count >= GameplayConstants.MAX_PROFILES) {
            return Result.failure(IllegalStateException("Maximum of ${GameplayConstants.MAX_PROFILES} profiles reached."))
        }
        val entity = ProfileEntity(
            name = name,
            avatarResId = avatarResId
        )
        val newId = profileDao.insertProfile(entity)
        return Result.success(newId)
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile.toEntity())
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(profile.toEntity())
    }
}
