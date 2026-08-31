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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePlayItDatabase(
        @ApplicationContext context: Context
    ): PlayItDatabase {
        val builder = Room.databaseBuilder(
            context,
            PlayItDatabase::class.java,
            PlayItDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedDatabaseRaw(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                seedDatabaseRaw(db)
            }

            private fun seedDatabaseRaw(db: SupportSQLiteDatabase) {
                try {
                    val cursor = db.query("SELECT COUNT(*) FROM phonemes")
                    var count = 0
                    if (cursor.moveToFirst()) {
                        count = cursor.getInt(0)
                    }
                    cursor.close()

                    if (count < 28) {
                        db.beginTransaction()
                        try {
                            // 1. Seed 28 Phonemes
                            val phonemes = listOf(
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (1, 'm', 'audio/phonemes/phoneme_m.mp3', 'images/pictures/picture_mouse.png', 'Mouse');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (2, 's', 'audio/phonemes/phoneme_s.mp3', 'images/pictures/picture_sun.png', 'Sun');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (3, 'a', 'audio/phonemes/phoneme_a.mp3', 'images/pictures/picture_apple.png', 'Apple');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (4, 'i', 'audio/phonemes/phoneme_i.mp3', 'images/pictures/picture_insect.png', 'Insect');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (5, 'o', 'audio/phonemes/phoneme_o.mp3', 'images/pictures/picture_orange.png', 'Orange');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (6, 'b', 'audio/phonemes/phoneme_b.mp3', 'images/pictures/picture_ball.png', 'Ball');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (7, 'e', 'audio/phonemes/phoneme_e.mp3', 'images/pictures/picture_elephant.png', 'Elephant');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (8, 'u', 'audio/phonemes/phoneme_u.mp3', 'images/pictures/picture_umbrella.png', 'Umbrella');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (9, 't', 'audio/phonemes/phoneme_t.mp3', 'images/pictures/picture_tiger.png', 'Tiger');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (10, 'k', 'audio/phonemes/phoneme_k.mp3', 'images/pictures/picture_kite.png', 'Kite');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (11, 'l', 'audio/phonemes/phoneme_l.mp3', 'images/pictures/picture_lion.png', 'Lion');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (12, 'y', 'audio/phonemes/phoneme_y.mp3', 'images/pictures/picture_yoyo.png', 'Yoyo');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (13, 'n', 'audio/phonemes/phoneme_n.mp3', 'images/pictures/picture_nest.png', 'Nest');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (14, 'g', 'audio/phonemes/phoneme_g.mp3', 'images/pictures/picture_goat.png', 'Goat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (15, 'ng', '', '', 'PENDING_SME_REVIEW');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (16, 'p', 'audio/phonemes/phoneme_p.mp3', 'images/pictures/picture_pig.png', 'Pig');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (17, 'r', 'audio/phonemes/phoneme_r.mp3', 'images/pictures/picture_rabbit.png', 'Rabbit');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (18, 'd', 'audio/phonemes/phoneme_d.mp3', 'images/pictures/picture_dog.png', 'Dog');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (19, 'h', 'audio/phonemes/phoneme_h.mp3', 'images/pictures/picture_hat.png', 'Hat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (20, 'w', 'audio/phonemes/phoneme_w.mp3', 'images/pictures/picture_watch.png', 'Watch');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (21, 'c', 'audio/phonemes/phoneme_c.mp3', 'images/pictures/picture_cat.png', 'Cat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (22, 'f', 'audio/phonemes/phoneme_f.mp3', 'images/pictures/picture_fish.png', 'Fish');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (23, 'j', 'audio/phonemes/phoneme_j.mp3', 'images/pictures/picture_jug.png', 'Jug');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (24, 'ñ', '', '', 'PENDING_SME_REVIEW');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (25, 'q', 'audio/phonemes/phoneme_q.mp3', 'images/pictures/picture_queen.png', 'Queen');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (26, 'v', 'audio/phonemes/phoneme_v.mp3', 'images/pictures/picture_van.png', 'Van');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (27, 'x', 'audio/phonemes/phoneme_x.mp3', 'images/pictures/picture_box.png', 'Box');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (28, 'z', 'audio/phonemes/phoneme_z.mp3', 'images/pictures/picture_zebra.png', 'Zebra');"
                            )
                            phonemes.forEach { db.execSQL(it) }

                            // 2. Seed 7 Groups
                            for (g in 1..7) {
                                db.execSQL("INSERT OR REPLACE INTO letter_groups (groupId, groupNumber) VALUES ($g, $g);")
                            }

                            // 3. Seed 28 Group Members
                            var pId = 1
                            for (g in 1..7) {
                                for (pos in 0..3) {
                                    db.execSQL("INSERT OR REPLACE INTO letter_group_members (groupId, phonemeId, position) VALUES ($g, $pId, $pos);")
                                    pId++
                                }
                            }

                            // 4. Seed 33 BlendIt Words
                            val words = listOf(
                                // Group 1 (3 words: M, S, A, I)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (1, 1, 'SAM', 'S-A-M', 'audio/words/word_sam.mp3', 'images/pictures/blendword_sam.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (2, 1, 'SIS', 'S-I-S', 'audio/words/word_sis.mp3', 'images/pictures/blendword_sis.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (3, 1, 'AIM', 'A-I-M', 'audio/words/word_aim.mp3', 'images/pictures/blendword_aim.png');",

                                // Group 2 (5 words: + O, B, E, U)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (4, 2, 'BUS', 'B-U-S', 'audio/words/word_bus.mp3', 'images/pictures/blendword_bus.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (5, 2, 'SUB', 'S-U-B', 'audio/words/word_sub.mp3', 'images/pictures/blendword_sub.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (6, 2, 'MOM', 'M-O-M', 'audio/words/word_mom.mp3', 'images/pictures/blendword_mom.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (7, 2, 'BEE', 'B-E-E', 'audio/words/word_bee.mp3', 'images/pictures/blendword_bee.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (8, 2, 'BIB', 'B-I-B', 'audio/words/word_bib.mp3', 'images/pictures/blendword_bib.png');",

                                // Group 3 (5 words: + T, K, L, Y)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (9, 3, 'BAT', 'B-A-T', 'audio/words/word_bat.mp3', 'images/pictures/blendword_bat.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (10, 3, 'MAT', 'M-A-T', 'audio/words/word_mat.mp3', 'images/pictures/blendword_mat.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (11, 3, 'KIT', 'K-I-T', 'audio/words/word_kit.mp3', 'images/pictures/blendword_kit.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (12, 3, 'TOY', 'T-O-Y', 'audio/words/word_toy.mp3', 'images/pictures/blendword_toy.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (13, 3, 'BOY', 'B-O-Y', 'audio/words/word_boy.mp3', 'images/pictures/blendword_boy.png');",

                                // Group 4 (5 words: + N, G, NG, P)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (14, 4, 'PIG', 'P-I-G', 'audio/words/word_pig.mp3', 'images/pictures/blendword_pig.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (15, 4, 'PAN', 'P-A-N', 'audio/words/word_pan.mp3', 'images/pictures/blendword_pan.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (16, 4, 'BUG', 'B-U-G', 'audio/words/word_bug.mp3', 'images/pictures/blendword_bug.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (17, 4, 'PIN', 'P-I-N', 'audio/words/word_pin.mp3', 'images/pictures/blendword_pin.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (18, 4, 'NAP', 'N-A-P', 'audio/words/word_nap.mp3', 'images/pictures/blendword_nap.png');",

                                // Group 5 (5 words: + R, D, H, W)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (19, 5, 'DOG', 'D-O-G', 'audio/words/word_dog.mp3', 'images/pictures/blendword_dog.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (20, 5, 'HAT', 'H-A-T', 'audio/words/word_hat.mp3', 'images/pictures/blendword_hat.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (21, 5, 'HEN', 'H-E-N', 'audio/words/word_hen.mp3', 'images/pictures/blendword_hen.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (22, 5, 'BED', 'B-E-D', 'audio/words/word_bed.mp3', 'images/pictures/blendword_bed.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (23, 5, 'HAND', 'H-A-N-D', 'audio/words/word_hand.mp3', 'images/pictures/blendword_hand.png');",

                                // Group 6 (5 words: + C, F, J, Ñ)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (24, 6, 'CAT', 'C-A-T', 'audio/words/word_cat.mp3', 'images/pictures/blendword_cat.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (25, 6, 'FAN', 'F-A-N', 'audio/words/word_fan.mp3', 'images/pictures/blendword_fan.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (26, 6, 'CAP', 'C-A-P', 'audio/words/word_cap.mp3', 'images/pictures/blendword_cap.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (27, 6, 'CUP', 'C-U-P', 'audio/words/word_cup.mp3', 'images/pictures/blendword_cup.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (28, 6, 'JAM', 'J-A-M', 'audio/words/word_jam.mp3', 'images/pictures/blendword_jam.png');",

                                // Group 7 (5 words: + Q, V, X, Z)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (29, 7, 'VAN', 'V-A-N', 'audio/words/word_van.mp3', 'images/pictures/blendword_van.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (30, 7, 'BOX', 'B-O-X', 'audio/words/word_box.mp3', 'images/pictures/blendword_box.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (31, 7, 'FOX', 'F-O-X', 'audio/words/word_fox.mp3', 'images/pictures/blendword_fox.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (32, 7, 'ZOO', 'Z-O-O', 'audio/words/word_zoo.mp3', 'images/pictures/blendword_zoo.png');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (33, 7, 'WEB', 'W-E-B', 'audio/words/word_web.mp3', 'images/pictures/blendword_web.png');"
                            )
                            words.forEach { db.execSQL(it) }

                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DatabaseModule", "Failed to seed database synchronously", e)
                }
            }
        })
        builder.fallbackToDestructiveMigration()
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
