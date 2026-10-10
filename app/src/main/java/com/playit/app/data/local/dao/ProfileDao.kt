package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.playit.app.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Every profile with `totalStars` computed from its progress: the stars of each letter (lesson_progress)
 * plus each Blend It group (blend_it_progress), the same sum as ReportGenerator, so the map, the profile
 * cards and the parent report always agree (card 28). The stored profiles.totalStars column is never
 * read; reading the sum needs no schema change and so no migration.
 */
private const val PROFILE_WITH_LIVE_STARS = """
    SELECT p.profileId, p.name, p.avatarResId, p.currentStreak, p.lastPlayedAt, p.createdAt,
        (SELECT COALESCE(SUM(lp.starsEarned), 0) FROM lesson_progress lp WHERE lp.profileId = p.profileId)
      + (SELECT COALESCE(SUM(bp.starsEarned), 0) FROM blend_it_progress bp WHERE bp.profileId = p.profileId)
        AS totalStars
    FROM profiles p
"""

@Dao
interface ProfileDao {
    /** Room re-runs this when profiles, lesson_progress or blend_it_progress change, so stars update live. */
    @Query("$PROFILE_WITH_LIVE_STARS ORDER BY p.createdAt DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("$PROFILE_WITH_LIVE_STARS WHERE p.profileId = :profileId")
    suspend fun getProfileById(profileId: Long): ProfileEntity?

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)
}
