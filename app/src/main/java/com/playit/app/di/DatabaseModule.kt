package com.playit.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.playit.app.data.local.PlayItDatabase
import com.playit.app.data.local.dao.AchievementDao
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
import com.playit.app.data.local.entity.BlendItWordEntity
import com.playit.app.data.local.entity.LetterGroupEntity
import com.playit.app.data.local.entity.LetterGroupMemberEntity
import com.playit.app.data.local.entity.PhonemeEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePlayItDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<PlayItDatabase>
    ): PlayItDatabase {
        return Room.databaseBuilder(
            context,
            PlayItDatabase::class.java,
            PlayItDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dbInstance = databaseProvider.get()

                    // Seed 28 Phonemes
                    val phonemeList = listOf(
                        PhonemeEntity(1, "m", "audio/phonemes/phoneme_m.mp3", "images/pictures/word_mouse.png", "Mouse"),
                        PhonemeEntity(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/word_sun.png", "Sun"),
                        PhonemeEntity(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/word_apple.png", "Apple"),
                        PhonemeEntity(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/word_iguana.png", "Iguana"),
                        PhonemeEntity(5, "o", "audio/phonemes/phoneme_o.mp3", "images/pictures/word_octopus.png", "Octopus"),
                        PhonemeEntity(6, "b", "audio/phonemes/phoneme_b.mp3", "images/pictures/word_ball.png", "Ball"),
                        PhonemeEntity(7, "e", "audio/phonemes/phoneme_e.mp3", "images/pictures/word_elephant.png", "Elephant"),
                        PhonemeEntity(8, "u", "audio/phonemes/phoneme_u.mp3", "images/pictures/word_umbrella.png", "Umbrella"),
                        PhonemeEntity(9, "t", "audio/phonemes/phoneme_t.mp3", "images/pictures/word_turtle.png", "Turtle"),
                        PhonemeEntity(10, "k", "audio/phonemes/phoneme_k.mp3", "images/pictures/word_kite.png", "Kite"),
                        PhonemeEntity(11, "l", "audio/phonemes/phoneme_l.mp3", "images/pictures/word_lion.png", "Lion"),
                        PhonemeEntity(12, "y", "audio/phonemes/phoneme_y.mp3", "images/pictures/word_yoyo.png", "Yo-yo"),
                        PhonemeEntity(13, "n", "audio/phonemes/phoneme_n.mp3", "images/pictures/word_nest.png", "Nest"),
                        PhonemeEntity(14, "g", "audio/phonemes/phoneme_g.mp3", "images/pictures/word_goat.png", "Goat"),
                        PhonemeEntity(15, "p", "audio/phonemes/phoneme_p.mp3", "images/pictures/word_pencil.png", "Pencil"),
                        PhonemeEntity(16, "ng", "", "", "PENDING_SME_REVIEW"),
                        PhonemeEntity(17, "r", "audio/phonemes/phoneme_r.mp3", "images/pictures/word_rabbit.png", "Rabbit"),
                        PhonemeEntity(18, "d", "audio/phonemes/phoneme_d.mp3", "images/pictures/word_dog.png", "Dog"),
                        PhonemeEntity(19, "h", "audio/phonemes/phoneme_h.mp3", "images/pictures/word_hat.png", "Hat"),
                        PhonemeEntity(20, "w", "audio/phonemes/phoneme_w.mp3", "images/pictures/word_watch.png", "Watch"),
                        PhonemeEntity(21, "c", "audio/phonemes/phoneme_c.mp3", "images/pictures/word_cat.png", "Cat"),
                        PhonemeEntity(22, "f", "audio/phonemes/phoneme_f.mp3", "images/pictures/word_fish.png", "Fish"),
                        PhonemeEntity(23, "j", "audio/phonemes/phoneme_j.mp3", "images/pictures/word_jug.png", "Jug"),
                        PhonemeEntity(24, "ñ", "", "", "PENDING_SME_REVIEW"),
                        PhonemeEntity(25, "q", "audio/phonemes/phoneme_q.mp3", "images/pictures/word_queen.png", "Queen"),
                        PhonemeEntity(26, "v", "audio/phonemes/phoneme_v.mp3", "images/pictures/word_van.png", "Van"),
                        PhonemeEntity(27, "x", "audio/phonemes/phoneme_x.mp3", "images/pictures/word_xylophone.png", "Xylophone"),
                        PhonemeEntity(28, "z", "audio/phonemes/phoneme_z.mp3", "images/pictures/word_zebra.png", "Zebra")
                    )
                    dbInstance.phonemeDao().insertPhonemes(phonemeList)

                    // Seed 7 Letter Groups
                    val groupList = (1..7).map { gId ->
                        LetterGroupEntity(groupId = gId, groupNumber = gId)
                    }
                    dbInstance.letterGroupDao().insertGroups(groupList)

                    // Seed 28 Group Members
                    val memberList = mutableListOf<LetterGroupMemberEntity>()
                    var phonemeIdCounter = 1
                    for (gId in 1..7) {
                        for (pos in 0..3) {
                            memberList.add(
                                LetterGroupMemberEntity(
                                    groupId = gId,
                                    phonemeId = phonemeIdCounter,
                                    position = pos
                                )
                            )
                            phonemeIdCounter++
                        }
                    }
                    dbInstance.letterGroupMemberDao().insertMembers(memberList)

                    // Seed BlendIt Words (Constraint: Group 1 restricted to exactly 3 words: SAM, SIS, AIM)
                    val blendWordList = listOf(
                        // Group 1 (3 words)
                        BlendItWordEntity(1, 1, "SAM", "S-A-M", "audio/words/word_sam.mp3", "images/pictures/blendword_sam.png"),
                        BlendItWordEntity(2, 1, "SIS", "S-I-S", "audio/words/word_sis.mp3", "images/pictures/blendword_sis.png"),
                        BlendItWordEntity(3, 1, "AIM", "A-I-M", "audio/words/word_aim.mp3", "images/pictures/blendword_aim.png"),

                        // Group 2 (5 words)
                        BlendItWordEntity(4, 2, "BUS", "B-U-S", "audio/words/word_bus.mp3", "images/pictures/blendword_bus.png"),
                        BlendItWordEntity(5, 2, "SUB", "S-U-B", "audio/words/word_sub.mp3", "images/pictures/blendword_sub.png"),
                        BlendItWordEntity(6, 2, "SUM", "S-U-M", "audio/words/word_sum.mp3", "images/pictures/blendword_sum.png"),
                        BlendItWordEntity(7, 2, "BAM", "B-A-M", "audio/words/word_bam.mp3", "images/pictures/blendword_bam.png"),
                        BlendItWordEntity(8, 2, "MOB", "M-O-B", "audio/words/word_mob.mp3", "images/pictures/blendword_mob.png"),

                        // Group 3 (5 words)
                        BlendItWordEntity(9, 3, "BAT", "B-A-T", "audio/words/word_bat.mp3", "images/pictures/blendword_bat.png"),
                        BlendItWordEntity(10, 3, "CAT", "C-A-T", "audio/words/word_cat.mp3", "images/pictures/blendword_cat.png"),
                        BlendItWordEntity(11, 3, "MAT", "M-A-T", "audio/words/word_mat.mp3", "images/pictures/blendword_mat.png"),
                        BlendItWordEntity(12, 3, "KIT", "K-I-T", "audio/words/word_kit.mp3", "images/pictures/blendword_kit.png"),
                        BlendItWordEntity(13, 3, "LIT", "L-I-T", "audio/words/word_lit.mp3", "images/pictures/blendword_lit.png")
                    )
                    dbInstance.blendItWordDao().insertWords(blendWordList)
                }
            }
        }).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideProfileDao(db: PlayItDatabase): ProfileDao = db.profileDao()

    @Provides
    fun providePhonemeDao(db: PlayItDatabase): PhonemeDao = db.phonemeDao()

    @Provides
    fun provideLetterGroupDao(db: PlayItDatabase): LetterGroupDao = db.letterGroupDao()

    @Provides
    fun provideLetterGroupMemberDao(db: PlayItDatabase): LetterGroupMemberDao = db.letterGroupMemberDao()

    @Provides
    fun provideLessonProgressDao(db: PlayItDatabase): LessonProgressDao = db.lessonProgressDao()

    @Provides
    fun provideSayItAttemptDao(db: PlayItDatabase): SayItAttemptDao = db.sayItAttemptDao()

    @Provides
    fun provideFindItAttemptDao(db: PlayItDatabase): FindItAttemptDao = db.findItAttemptDao()

    @Provides
    fun provideBlendItWordDao(db: PlayItDatabase): BlendItWordDao = db.blendItWordDao()

    @Provides
    fun provideBlendItProgressDao(db: PlayItDatabase): BlendItProgressDao = db.blendItProgressDao()

    @Provides
    fun provideBlendItAttemptDao(db: PlayItDatabase): BlendItAttemptDao = db.blendItAttemptDao()

    @Provides
    fun provideAchievementDao(db: PlayItDatabase): AchievementDao = db.achievementDao()
}
