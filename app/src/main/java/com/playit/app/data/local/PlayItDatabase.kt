package com.playit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.playit.app.data.local.dao.BlendItAttemptDao
import com.playit.app.data.local.dao.BlendItProgressDao
import com.playit.app.data.local.dao.BlendItWordDao
import com.playit.app.data.local.dao.FindItAttemptDao
import com.playit.app.data.local.dao.LessonProgressDao
import com.playit.app.data.local.dao.LetterGroupDao
import com.playit.app.data.local.dao.LetterGroupMemberDao
import com.playit.app.data.local.dao.PhonemeDao
import com.playit.app.data.local.dao.ProfileDao
import com.playit.app.data.local.dao.SayItAttemptDao
import com.playit.app.data.local.entity.BlendItAttemptEntity
import com.playit.app.data.local.entity.BlendItProgressEntity
import com.playit.app.data.local.entity.BlendItWordEntity
import com.playit.app.data.local.entity.FindItAttemptEntity
import com.playit.app.data.local.entity.LessonProgressEntity
import com.playit.app.data.local.entity.LetterGroupEntity
import com.playit.app.data.local.entity.LetterGroupMemberEntity
import com.playit.app.data.local.entity.PhonemeEntity
import com.playit.app.data.local.entity.ProfileEntity
import com.playit.app.data.local.entity.SayItAttemptEntity

import com.playit.app.data.local.dao.AchievementDao
import com.playit.app.data.local.entity.AchievementEntity

@Database(
    entities = [
        ProfileEntity::class,
        PhonemeEntity::class,
        LetterGroupEntity::class,
        LetterGroupMemberEntity::class,
        LessonProgressEntity::class,
        SayItAttemptEntity::class,
        FindItAttemptEntity::class,
        BlendItWordEntity::class,
        BlendItProgressEntity::class,
        BlendItAttemptEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PlayItDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun phonemeDao(): PhonemeDao
    abstract fun letterGroupDao(): LetterGroupDao
    abstract fun letterGroupMemberDao(): LetterGroupMemberDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun sayItAttemptDao(): SayItAttemptDao
    abstract fun findItAttemptDao(): FindItAttemptDao
    abstract fun blendItWordDao(): BlendItWordDao
    abstract fun blendItProgressDao(): BlendItProgressDao
    abstract fun blendItAttemptDao(): BlendItAttemptDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "playit_db"
    }
}
