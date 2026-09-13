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

                    if (count != 26) {
                        db.beginTransaction()
                        try {
                            db.execSQL("DELETE FROM blend_it_words;")
                            db.execSQL("DELETE FROM letter_group_members;")
                            db.execSQL("DELETE FROM letter_groups;")
                            db.execSQL("DELETE FROM phonemes;")

                            // 1. Seed 26 Phonemes (A-Z Marungko sequence, excluding 'ng' and 'ñ')
                            val phonemes = listOf(
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (1, 'm', 'audio/phonemes/phoneme_m.mp3', 'images/pictures/picture_mouse.webp', 'Mouse');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (2, 's', 'audio/phonemes/phoneme_s.mp3', 'images/pictures/picture_sun.webp', 'Sun');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (3, 'a', 'audio/phonemes/phoneme_a.mp3', 'images/pictures/picture_apple.webp', 'Apple');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (4, 'i', 'audio/phonemes/phoneme_i.mp3', 'images/pictures/picture_insect.webp', 'Insect');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (5, 'o', 'audio/phonemes/phoneme_o.mp3', 'images/pictures/picture_orange.webp', 'Orange');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (6, 'b', 'audio/phonemes/phoneme_b.mp3', 'images/pictures/picture_ball.webp', 'Ball');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (7, 'e', 'audio/phonemes/phoneme_e.mp3', 'images/pictures/picture_elephant.webp', 'Elephant');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (8, 'u', 'audio/phonemes/phoneme_u.mp3', 'images/pictures/picture_umbrella.webp', 'Umbrella');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (9, 't', 'audio/phonemes/phoneme_t.mp3', 'images/pictures/picture_tiger.webp', 'Tiger');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (10, 'k', 'audio/phonemes/phoneme_k.mp3', 'images/pictures/picture_kite.webp', 'Kite');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (11, 'l', 'audio/phonemes/phoneme_l.mp3', 'images/pictures/picture_lion.webp', 'Lion');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (12, 'y', 'audio/phonemes/phoneme_y.mp3', 'images/pictures/picture_yoyo.webp', 'Yoyo');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (13, 'n', 'audio/phonemes/phoneme_n.mp3', 'images/pictures/picture_nest.webp', 'Nest');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (14, 'g', 'audio/phonemes/phoneme_g.mp3', 'images/pictures/picture_goat.webp', 'Goat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (15, 'p', 'audio/phonemes/phoneme_p.mp3', 'images/pictures/picture_pig.webp', 'Pig');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (16, 'r', 'audio/phonemes/phoneme_r.mp3', 'images/pictures/picture_rabbit.webp', 'Rabbit');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (17, 'd', 'audio/phonemes/phoneme_d.mp3', 'images/pictures/picture_dog.webp', 'Dog');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (18, 'h', 'audio/phonemes/phoneme_h.mp3', 'images/pictures/picture_hat.webp', 'Hat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (19, 'w', 'audio/phonemes/phoneme_w.mp3', 'images/pictures/picture_watch.webp', 'Watch');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (20, 'c', 'audio/phonemes/phoneme_c.mp3', 'images/pictures/picture_cat.webp', 'Cat');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (21, 'f', 'audio/phonemes/phoneme_f.mp3', 'images/pictures/picture_fish.webp', 'Fish');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (22, 'j', 'audio/phonemes/phoneme_j.mp3', 'images/pictures/picture_jug.webp', 'Jug');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (23, 'q', 'audio/phonemes/phoneme_q.mp3', 'images/pictures/picture_queen.webp', 'Queen');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (24, 'v', 'audio/phonemes/phoneme_v.mp3', 'images/pictures/picture_van.webp', 'Van');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (25, 'x', 'audio/phonemes/phoneme_x.mp3', 'images/pictures/picture_box.webp', 'Box');",
                                "INSERT OR REPLACE INTO phonemes (phonemeId, letter, audioPath, imagePath, exampleWord) VALUES (26, 'z', 'audio/phonemes/phoneme_z.mp3', 'images/pictures/picture_zebra.webp', 'Zebra');"
                            )
                            phonemes.forEach { db.execSQL(it) }

                            // 2. Seed 7 Groups
                            for (g in 1..7) {
                                db.execSQL("INSERT OR REPLACE INTO letter_groups (groupId, groupNumber) VALUES ($g, $g);")
                            }

                            // 3. Seed 26 Group Members (7 Groups: 4, 4, 4, 3, 4, 3, 4 letters)
                            val groupMemberPhonemeIds = listOf(
                                listOf(1, 2, 3, 4),       // Group 1: m, s, a, i
                                listOf(5, 6, 7, 8),       // Group 2: o, b, e, u
                                listOf(9, 10, 11, 12),    // Group 3: t, k, l, y
                                listOf(13, 14, 15),       // Group 4: n, g, p (3 letters)
                                listOf(16, 17, 18, 19),   // Group 5: r, d, h, w
                                listOf(20, 21, 22),       // Group 6: c, f, j (3 letters)
                                listOf(23, 24, 25, 26)    // Group 7: q, v, x, z
                            )
                            groupMemberPhonemeIds.forEachIndexed { groupIdx, phonemeIds ->
                                val groupId = groupIdx + 1
                                phonemeIds.forEachIndexed { pos, phonemeId ->
                                    db.execSQL("INSERT OR REPLACE INTO letter_group_members (groupId, phonemeId, position) VALUES ($groupId, $phonemeId, $pos);")
                                }
                            }

                            // 4. Seed 33 BlendIt Words
                            val words = listOf(
                                // Group 1 (3 words: M, S, A, I)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (1, 1, 'SAM', 'S-A-M', 'audio/words/word_sam.mp3', 'images/pictures/blendword_sam.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (2, 1, 'SIS', 'S-I-S', 'audio/words/word_sis.mp3', 'images/pictures/blendword_sis.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (3, 1, 'AIM', 'A-I-M', 'audio/words/word_aim.mp3', 'images/pictures/blendword_aim.webp');",

                                // Group 2 (5 words: + O, B, E, U)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (4, 2, 'BUS', 'B-U-S', 'audio/words/word_bus.mp3', 'images/pictures/blendword_bus.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (5, 2, 'SUB', 'S-U-B', 'audio/words/word_sub.mp3', 'images/pictures/blendword_sub.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (6, 2, 'MOM', 'M-O-M', 'audio/words/word_mom.mp3', 'images/pictures/blendword_mom.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (7, 2, 'BEE', 'B-E-E', 'audio/words/word_bee.mp3', 'images/pictures/blendword_bee.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (8, 2, 'BIB', 'B-I-B', 'audio/words/word_bib.mp3', 'images/pictures/blendword_bib.webp');",

                                // Group 3 (5 words: + T, K, L, Y)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (9, 3, 'BAT', 'B-A-T', 'audio/words/word_bat.mp3', 'images/pictures/blendword_bat.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (10, 3, 'MAT', 'M-A-T', 'audio/words/word_mat.mp3', 'images/pictures/blendword_mat.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (11, 3, 'KIT', 'K-I-T', 'audio/words/word_kit.mp3', 'images/pictures/blendword_kit.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (12, 3, 'TOY', 'T-O-Y', 'audio/words/word_toy.mp3', 'images/pictures/blendword_toy.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (13, 3, 'BOY', 'B-O-Y', 'audio/words/word_boy.mp3', 'images/pictures/blendword_boy.webp');",

                                // Group 4 (5 words: + N, G, P)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (14, 4, 'PIG', 'P-I-G', 'audio/words/word_pig.mp3', 'images/pictures/blendword_pig.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (15, 4, 'PAN', 'P-A-N', 'audio/words/word_pan.mp3', 'images/pictures/blendword_pan.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (16, 4, 'BUG', 'B-U-G', 'audio/words/word_bug.mp3', 'images/pictures/blendword_bug.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (17, 4, 'PIN', 'P-I-N', 'audio/words/word_pin.mp3', 'images/pictures/blendword_pin.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (18, 4, 'NAP', 'N-A-P', 'audio/words/word_nap.mp3', 'images/pictures/blendword_nap.webp');",

                                // Group 5 (5 words: + R, D, H, W)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (19, 5, 'DOG', 'D-O-G', 'audio/words/word_dog.mp3', 'images/pictures/blendword_dog.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (20, 5, 'HAT', 'H-A-T', 'audio/words/word_hat.mp3', 'images/pictures/blendword_hat.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (21, 5, 'HEN', 'H-E-N', 'audio/words/word_hen.mp3', 'images/pictures/blendword_hen.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (22, 5, 'BED', 'B-E-D', 'audio/words/word_bed.mp3', 'images/pictures/blendword_bed.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (23, 5, 'WEB', 'W-E-B', 'audio/words/word_web.mp3', 'images/pictures/blendword_web.webp');",

                                // Group 6 (5 words: + C, F, J)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (24, 6, 'CAT', 'C-A-T', 'audio/words/word_cat.mp3', 'images/pictures/blendword_cat.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (25, 6, 'FAN', 'F-A-N', 'audio/words/word_fan.mp3', 'images/pictures/blendword_fan.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (26, 6, 'CAP', 'C-A-P', 'audio/words/word_cap.mp3', 'images/pictures/blendword_cap.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (27, 6, 'CUP', 'C-U-P', 'audio/words/word_cup.mp3', 'images/pictures/blendword_cup.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (28, 6, 'JAM', 'J-A-M', 'audio/words/word_jam.mp3', 'images/pictures/blendword_jam.webp');",

                                // Group 7 (5 words: + Q, V, X, Z)
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (29, 7, 'VAN', 'V-A-N', 'audio/words/word_van.mp3', 'images/pictures/blendword_van.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (30, 7, 'BOX', 'B-O-X', 'audio/words/word_box.mp3', 'images/pictures/blendword_box.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (31, 7, 'FOX', 'F-O-X', 'audio/words/word_fox.mp3', 'images/pictures/blendword_fox.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (32, 7, 'ZOO', 'Z-O-O', 'audio/words/word_zoo.mp3', 'images/pictures/blendword_zoo.webp');",
                                "INSERT OR REPLACE INTO blend_it_words (wordId, groupId, word, wordPattern, audioPath, imagePath) VALUES (33, 7, 'QUIZ', 'Q-U-I-Z', 'audio/words/word_quiz.mp3', 'images/pictures/blendword_quiz.webp');"
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
