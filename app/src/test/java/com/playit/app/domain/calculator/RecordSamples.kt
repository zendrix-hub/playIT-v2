package com.playit.app.domain.calculator

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.SayItAttempt

/** Learners' records built from the app's real curriculum, for the record, PDF and screenshot tests. */
object RecordSamples {
    val letters = listOf("m", "s", "a", "i", "o", "b", "e", "u", "t", "k", "l", "y", "n", "g", "p", "r", "d", "h", "w",
        "c", "f", "j", "q", "v", "x", "z")
    val words = listOf("Mouse", "Sun", "Apple", "Insect", "Orange", "Ball", "Elephant", "Umbrella", "Tiger", "Kite",
        "Lion", "Yoyo", "Nest", "Goat", "Pig", "Rabbit", "Dog", "Hat", "Watch", "Cat", "Fish", "Jug", "Queen", "Van",
        "Box", "Zebra")
    private val sizes = listOf(4, 4, 4, 3, 4, 3, 4)
    val chapterOf: Map<Int, Int> = sizes.flatMapIndexed { i, n -> List(n) { i + 1 } }
        .mapIndexed { index, chapter -> index + 1 to chapter }.toMap()

    val phonemes = letters.mapIndexed { i, l ->
        Phoneme(i + 1, l, "audio/phonemes/phoneme_$l.mp3", "images/pictures/picture.png", words[i])
    }

    /** A learner who has finished [done] letters, each with [sayMisses] Say It and [findMisses] Find It misses. */
    fun learner(
        id: Long,
        name: String,
        done: Int,
        sayMisses: (Int) -> Int = { 0 },
        findMisses: (Int) -> Int = { 0 },
        startedNext: Boolean = true,
        avatar: Int = ((id - 1) % 6 + 1).toInt()
    ): ProfileDashboardData {
        val day = 86_400_000L
        val t0 = 1_793_000_000_000L
        val rows = phonemes.map { ph ->
            val i = ph.id
            val finished = i <= done
            val started = finished || (startedNext && i == done + 1)
            val say = if (!started) emptyList() else {
                val misses = if (finished) sayMisses(i) else 1
                List(misses) { SayItAttempt(profileId = id, phonemeId = i, isCorrect = false, attemptedAt = t0 + i * day) } +
                    if (finished) listOf(SayItAttempt(profileId = id, phonemeId = i, isCorrect = true, attemptedAt = t0 + i * day)) else emptyList()
            }
            val find = if (!finished) emptyList() else
                List(findMisses(i)) { FindItAttempt(profileId = id, phonemeId = i, selectedPhonemeId = 99, isCorrect = false, attemptedAt = t0 + i * day) } +
                    List(3) { FindItAttempt(profileId = id, phonemeId = i, selectedPhonemeId = i, isCorrect = true, attemptedAt = t0 + i * day) }
            val progress = if (finished) LessonProgress(profileId = id, phonemeId = i,
                starsEarned = (3 - findMisses(i)).coerceAtLeast(1), heartsLost = findMisses(i), isCompleted = true,
                completedAt = t0 + i * day) else null
            ProgressRecordCalculator.letter(ph, chapterOf.getValue(i), progress, say, find)
        }
        val chapters = rows.groupBy { it.chapter }.toSortedMap().map { (ch, ls) ->
            val blend = if (ls.all { it.isCompleted }) BlendItProgress(profileId = id, groupId = ch, starsEarned = 3, isCompleted = true) else null
            ProgressRecordCalculator.chapter(ch, ls, blend)
        }
        val (grade, level) = ProgressRecordCalculator.overall(rows)
        return ProfileDashboardData(
            profile = Profile(id = id, name = name, avatarResId = avatar, currentStreak = 3, createdAt = id),
            totalStars = rows.sumOf { it.starsEarned },
            completedLettersCount = rows.count { it.isCompleted },
            totalLettersCount = rows.size,
            letterPerformances = rows,
            chapters = chapters,
            overallGrade = grade,
            overallLevel = level,
            recentScore = if (done > 0) 86 else null,
            blendItCompletedCount = chapters.count { it.blendItCompleted },
            blendItTotalCount = chapters.size,
            firstPlayedAt = if (done > 0) t0 + day else null,
            lastPlayedAt = if (done > 0) t0 + done * day else null
        )
    }
}
