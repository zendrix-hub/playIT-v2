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
        val builder = Room.databaseBuilder(
            context,
            PlayItDatabase::class.java,
            PlayItDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dbInstance = databaseProvider.get()

                    // Seed 28 Phonemes
                    // MARUNGKO SEQUENCE ADAPTATION NOTE:
                    // The phoneme ordering below follows the project's adapted Marungko sequence
                    // as established in 01_REQUIREMENTS_SUMMARY.md §7 (Conflict Resolution).
                    // This ordering was optimized for CVC word availability in English phonics
                    // while preserving the Marungko principle of cumulative letter introduction.
                    // Group 1: m,s,a,i | Group 2: o,b,e,u | Group 3: t,k,l,y
                    // Group 4: n,g,ng,p | Group 5: r,d,h,w | Group 6: c,f,j,ñ | Group 7: q,v,x,z
                    // See thesis Chapter 3 §3.x for pedagogical justification.
                    val phonemeList = listOf(
                        PhonemeEntity(1, "m", "audio/phonemes/phoneme_m.mp3", "images/pictures/picture_mouse.png", "Mouse"),
                        PhonemeEntity(2, "s", "audio/phonemes/phoneme_s.mp3", "images/pictures/picture_sun.png", "Sun"),
                        PhonemeEntity(3, "a", "audio/phonemes/phoneme_a.mp3", "images/pictures/picture_apple.png", "Apple"),
                        PhonemeEntity(4, "i", "audio/phonemes/phoneme_i.mp3", "images/pictures/picture_insect.png", "Insect"),
                        PhonemeEntity(5, "o", "audio/phonemes/phoneme_o.mp3", "images/pictures/picture_orange.png", "Orange"),
                        PhonemeEntity(6, "b", "audio/phonemes/phoneme_b.mp3", "images/pictures/picture_ball.png", "Ball"),
                        PhonemeEntity(7, "e", "audio/phonemes/phoneme_e.mp3", "images/pictures/picture_elephant.png", "Elephant"),
                        PhonemeEntity(8, "u", "audio/phonemes/phoneme_u.mp3", "images/pictures/picture_umbrella.png", "Umbrella"),
                        PhonemeEntity(9, "t", "audio/phonemes/phoneme_t.mp3", "images/pictures/picture_tiger.png", "Tiger"),
                        PhonemeEntity(10, "k", "audio/phonemes/phoneme_k.mp3", "images/pictures/picture_kite.png", "Kite"),
                        PhonemeEntity(11, "l", "audio/phonemes/phoneme_l.mp3", "images/pictures/picture_lion.png", "Lion"),
                        PhonemeEntity(12, "y", "audio/phonemes/phoneme_y.mp3", "images/pictures/picture_yoyo.png", "Yoyo"),
                        PhonemeEntity(13, "n", "audio/phonemes/phoneme_n.mp3", "images/pictures/picture_nest.png", "Nest"),
                        PhonemeEntity(14, "g", "audio/phonemes/phoneme_g.mp3", "images/pictures/picture_goat.png", "Goat"),
                        PhonemeEntity(15, "ng", "", "", "PENDING_SME_REVIEW"),
                        PhonemeEntity(16, "p", "audio/phonemes/phoneme_p.mp3", "images/pictures/picture_pig.png", "Pig"),
                        PhonemeEntity(17, "r", "audio/phonemes/phoneme_r.mp3", "images/pictures/picture_rabbit.png", "Rabbit"),
                        PhonemeEntity(18, "d", "audio/phonemes/phoneme_d.mp3", "images/pictures/picture_dog.png", "Dog"),
                        PhonemeEntity(19, "h", "audio/phonemes/phoneme_h.mp3", "images/pictures/picture_hat.png", "Hat"),
                        PhonemeEntity(20, "w", "audio/phonemes/phoneme_w.mp3", "images/pictures/picture_watch.png", "Watch"),
                        PhonemeEntity(21, "c", "audio/phonemes/phoneme_c.mp3", "images/pictures/picture_cat.png", "Cat"),
                        PhonemeEntity(22, "f", "audio/phonemes/phoneme_f.mp3", "images/pictures/picture_fish.png", "Fish"),
                        PhonemeEntity(23, "j", "audio/phonemes/phoneme_j.mp3", "images/pictures/picture_jug.png", "Jug"),
                        PhonemeEntity(24, "ñ", "", "", "PENDING_SME_REVIEW"),
                        PhonemeEntity(25, "q", "audio/phonemes/phoneme_q.mp3", "images/pictures/picture_queen.png", "Queen"),
                        PhonemeEntity(26, "v", "audio/phonemes/phoneme_v.mp3", "images/pictures/picture_van.png", "Van"),
                        PhonemeEntity(27, "x", "audio/phonemes/phoneme_x.mp3", "images/pictures/picture_box.png", "Box"),
                        PhonemeEntity(28, "z", "audio/phonemes/phoneme_z.mp3", "images/pictures/picture_zebra.png", "Zebra")
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
                        // Group 1 (3 words: M, S, A, I)
                        BlendItWordEntity(1, 1, "SAM", "S-A-M", "audio/words/word_sam.mp3", "images/pictures/blendword_sam.png"),
                        BlendItWordEntity(2, 1, "SIS", "S-I-S", "audio/words/word_sis.mp3", "images/pictures/blendword_sis.png"),
                        BlendItWordEntity(3, 1, "AIM", "A-I-M", "audio/words/word_aim.mp3", "images/pictures/blendword_aim.png"),

                        // Group 2 (5 words: + O, B, E, U)
                        BlendItWordEntity(4, 2, "BUS", "B-U-S", "audio/words/word_bus.mp3", "images/pictures/blendword_bus.png"),
                        BlendItWordEntity(5, 2, "SUB", "S-U-B", "audio/words/word_sub.mp3", "images/pictures/blendword_sub.png"),
                        BlendItWordEntity(6, 2, "MOM", "M-O-M", "audio/words/word_mom.mp3", "images/pictures/blendword_mom.png"),
                        BlendItWordEntity(7, 2, "BEE", "B-E-E", "audio/words/word_bee.mp3", "images/pictures/blendword_bee.png"),
                        BlendItWordEntity(8, 2, "BIB", "B-I-B", "audio/words/word_bib.mp3", "images/pictures/blendword_bib.png"),

                        // Group 3 (5 words: + T, K, L, Y)
                        BlendItWordEntity(9, 3, "BAT", "B-A-T", "audio/words/word_bat.mp3", "images/pictures/blendword_bat.png"),
                        BlendItWordEntity(10, 3, "MAT", "M-A-T", "audio/words/word_mat.mp3", "images/pictures/blendword_mat.png"),
                        BlendItWordEntity(11, 3, "KIT", "K-I-T", "audio/words/word_kit.mp3", "images/pictures/blendword_kit.png"),
                        BlendItWordEntity(12, 3, "TOY", "T-O-Y", "audio/words/word_toy.mp3", "images/pictures/blendword_toy.png"),
                        BlendItWordEntity(13, 3, "BOY", "B-O-Y", "audio/words/word_boy.mp3", "images/pictures/blendword_boy.png"),

                        // Group 4 (5 words: + N, G, NG, P)
                        BlendItWordEntity(14, 4, "PIG", "P-I-G", "audio/words/word_pig.mp3", "images/pictures/blendword_pig.png"),
                        BlendItWordEntity(15, 4, "PAN", "P-A-N", "audio/words/word_pan.mp3", "images/pictures/blendword_pan.png"),
                        BlendItWordEntity(16, 4, "BUG", "B-U-G", "audio/words/word_bug.mp3", "images/pictures/blendword_bug.png"),
                        BlendItWordEntity(17, 4, "PIN", "P-I-N", "audio/words/word_pin.mp3", "images/pictures/blendword_pin.png"),
                        BlendItWordEntity(18, 4, "NAP", "N-A-P", "audio/words/word_nap.mp3", "images/pictures/blendword_nap.png"),

                        // Group 5 (5 words: + R, D, H, W)
                        BlendItWordEntity(19, 5, "DOG", "D-O-G", "audio/words/word_dog.mp3", "images/pictures/blendword_dog.png"),
                        BlendItWordEntity(20, 5, "HAT", "H-A-T", "audio/words/word_hat.mp3", "images/pictures/blendword_hat.png"),
                        BlendItWordEntity(21, 5, "HEN", "H-E-N", "audio/words/word_hen.mp3", "images/pictures/blendword_hen.png"),
                        BlendItWordEntity(22, 5, "BED", "B-E-D", "audio/words/word_bed.mp3", "images/pictures/blendword_bed.png"),
                        BlendItWordEntity(23, 5, "HAND", "H-A-N-D", "audio/words/word_hand.mp3", "images/pictures/blendword_hand.png"),

                        // Group 6 (5 words: + C, F, J, Ñ)
                        BlendItWordEntity(24, 6, "CAT", "C-A-T", "audio/words/word_cat.mp3", "images/pictures/blendword_cat.png"),
                        BlendItWordEntity(25, 6, "FAN", "F-A-N", "audio/words/word_fan.mp3", "images/pictures/blendword_fan.png"),
                        BlendItWordEntity(26, 6, "CAP", "C-A-P", "audio/words/word_cap.mp3", "images/pictures/blendword_cap.png"),
                        BlendItWordEntity(27, 6, "CUP", "C-U-P", "audio/words/word_cup.mp3", "images/pictures/blendword_cup.png"),
                        BlendItWordEntity(28, 6, "JAM", "J-A-M", "audio/words/word_jam.mp3", "images/pictures/blendword_jam.png"),

                        // Group 7 (5 words: + Q, V, X, Z)
                        BlendItWordEntity(29, 7, "VAN", "V-A-N", "audio/words/word_van.mp3", "images/pictures/blendword_van.png"),
                        BlendItWordEntity(30, 7, "BOX", "B-O-X", "audio/words/word_box.mp3", "images/pictures/blendword_box.png"),
                        BlendItWordEntity(31, 7, "FOX", "F-O-X", "audio/words/word_fox.mp3", "images/pictures/blendword_fox.png"),
                        BlendItWordEntity(32, 7, "ZOO", "Z-O-O", "audio/words/word_zoo.mp3", "images/pictures/blendword_zoo.png"),
                        BlendItWordEntity(33, 7, "WEB", "W-E-B", "audio/words/word_web.mp3", "images/pictures/blendword_web.png")
                    )
                        dbInstance.blendItWordDao().insertWords(blendWordList)
                    } catch (e: Exception) {
                        android.util.Log.e("DatabaseModule", "Failed to seed database in callback", e)
                    }
                }
            }
        })
        if (com.playit.app.BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration()
        }
        return builder.build()
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
