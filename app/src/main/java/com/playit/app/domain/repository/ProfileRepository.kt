package com.playit.app.domain.repository

import com.playit.app.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllProfiles(): Flow<List<Profile>>
    suspend fun getProfileById(id: Long): Profile?
    suspend fun createProfile(name: String, avatarResId: Int): Result<Long>
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile(profile: Profile)
    suspend fun addStars(profileId: Long, starDelta: Int)
    suspend fun updateTotalStars(profileId: Long, totalStars: Int)
}
