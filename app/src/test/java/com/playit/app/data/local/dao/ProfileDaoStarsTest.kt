package com.playit.app.data.local.dao

import androidx.room.Room
import com.playit.app.data.local.PlayItDatabase
import com.playit.app.data.local.entity.BlendItProgressEntity
import com.playit.app.data.local.entity.LessonProgressEntity
import com.playit.app.data.local.entity.LetterGroupEntity
import com.playit.app.data.local.entity.PhonemeEntity
import com.playit.app.data.local.entity.ProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Card 28: the star total comes from the progress rows (the same sum as ReportGenerator: letter stars
 * from lesson_progress plus Blend It stars from blend_it_progress), on a real in-memory Room database,
 * so the SQL itself is tested, not a mock.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProfileDaoStarsTest {

    private lateinit var db: PlayItDatabase
    private lateinit var profiles: ProfileDao
    private lateinit var lessons: LessonProgressDao
    private lateinit var blends: BlendItProgressDao

    @Before
    fun setUp() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), PlayItDatabase::class.java).build()
        profiles = db.profileDao()
        lessons = db.lessonProgressDao()
        blends = db.blendItProgressDao()
        // Foreign keys: progress rows need their phoneme and letter group.
        db.phonemeDao().insertPhonemes(
            listOf(
                PhonemeEntity(1, "m", "a", "i", "mouse"),
                PhonemeEntity(2, "s", "a", "i", "sun")
            )
        )
        db.letterGroupDao().insertGroups(listOf(LetterGroupEntity(groupId = 1, groupNumber = 1)))
    }

    @After
    fun tearDown() = db.close()

    private suspend fun newProfile(name: String) = profiles.insertProfile(ProfileEntity(name = name, avatarResId = 1))

    @Test
    fun totalStars_sumsLessonAndBlendStars() = runBlocking {
        val ana = newProfile("Ana")
        lessons.saveProgress(LessonProgressEntity(profileId = ana, phonemeId = 1, starsEarned = 3, isCompleted = true))
        lessons.saveProgress(LessonProgressEntity(profileId = ana, phonemeId = 2, starsEarned = 2, isCompleted = true))
        blends.saveProgress(BlendItProgressEntity(profileId = ana, groupId = 1, starsEarned = 3, isCompleted = true))

        assertEquals(8, profiles.getProfileById(ana)?.totalStars)
        assertEquals(8, profiles.getAllProfiles().first().single().totalStars)
    }

    @Test
    fun totalStars_isZeroWithoutProgress() = runBlocking {
        val ana = newProfile("Ana")

        assertEquals(0, profiles.getProfileById(ana)?.totalStars)
        assertEquals(0, profiles.getAllProfiles().first().single().totalStars)
    }

    @Test
    fun totalStars_isPerProfile() = runBlocking {
        val ana = newProfile("Ana")
        val ben = newProfile("Ben")
        lessons.saveProgress(LessonProgressEntity(profileId = ana, phonemeId = 1, starsEarned = 2))
        lessons.saveProgress(LessonProgressEntity(profileId = ben, phonemeId = 1, starsEarned = 3))
        blends.saveProgress(BlendItProgressEntity(profileId = ben, groupId = 1, starsEarned = 1))

        assertEquals(2, profiles.getProfileById(ana)?.totalStars)
        assertEquals(4, profiles.getProfileById(ben)?.totalStars)
        assertEquals(mapOf("Ana" to 2, "Ben" to 4), profiles.getAllProfiles().first().associate { it.name to it.totalStars })
    }

    @Test
    fun getAllProfiles_reEmitsWhenProgressChanges() = runBlocking {
        val ana = newProfile("Ana")
        val totals = Channel<Int>(Channel.UNLIMITED)
        // A live collector, like MapViewModel.userStats: it must get the new total without re-subscribing.
        val collector = launch(Dispatchers.IO) {
            profiles.getAllProfiles().collect { list -> totals.send(list.single().totalStars) }
        }
        assertEquals(0, withTimeout(5_000) { totals.receive() })

        lessons.saveProgress(LessonProgressEntity(profileId = ana, phonemeId = 1, starsEarned = 3))
        assertEquals(3, withTimeout(5_000) { totals.receive() })

        blends.saveProgress(BlendItProgressEntity(profileId = ana, groupId = 1, starsEarned = 2))
        assertEquals(5, withTimeout(5_000) { totals.receive() })
        collector.cancel()
    }
}
