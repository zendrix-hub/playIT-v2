package com.playit.app.domain.calculator

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.SayItAttempt
import com.playit.app.domain.model.SoundCheckResult
import kotlin.math.roundToInt

/**
 * Builds a learner's record the way a DepEd class record does: a percentage score per letter, transmuted into a
 * grade, a level for the grade, and a mean grade for each chapter and overall. Pure functions over the saved rows.
 */
object ProgressRecordCalculator {

    fun letter(
        phoneme: Phoneme,
        chapter: Int,
        progress: LessonProgress?,
        sayIt: List<SayItAttempt>,
        findIt: List<FindItAttempt>
    ): LetterPerformance {
        val sayItCorrect = sayIt.count { it.isCorrect }
        val findItCorrect = findIt.count { it.isCorrect }
        val attempts = sayIt.size + findIt.size
        val correct = sayItCorrect + findItCorrect
        val isCompleted = progress?.isCompleted == true
        // A finished letter with no saved attempts (data from before attempts were logged) counts as all right.
        val (score, grade) = when {
            attempts > 0 -> (correct * 100.0 / attempts).roundToInt() to ProgressLevel.transmute(correct, attempts)
            isCompleted -> 100 to 100
            else -> null to null
        }
        val level = when {
            isCompleted -> ProgressLevel.forGrade(grade ?: 100)
            attempts > 0 -> ProgressLevel.IN_PROGRESS
            else -> ProgressLevel.NOT_STARTED
        }
        return LetterPerformance(
            phonemeId = phoneme.id,
            symbol = phoneme.letter,
            name = phoneme.letter.uppercase(),
            keyWord = phoneme.exampleWord,
            chapter = chapter,
            sayItCorrect = sayItCorrect,
            sayItAttempts = sayIt.size,
            findItCorrect = findItCorrect,
            findItAttempts = findIt.size,
            heartsLost = progress?.heartsLost ?: 0,
            starsEarned = progress?.starsEarned ?: 0,
            isCompleted = isCompleted,
            score = score,
            grade = grade,
            level = level,
            imagePath = phoneme.imagePath
        )
    }

    fun chapter(
        chapter: Int,
        letters: List<LetterPerformance>,
        blendIt: BlendItProgress?,
        soundChecks: List<SoundCheckResult> = emptyList()
    ): ChapterRecord {
        val allDone = letters.isNotEmpty() && letters.all { it.isCompleted }
        val grade = if (allDone) meanGrade(letters) else null
        val level = when {
            grade != null -> ProgressLevel.forGrade(grade)
            letters.any { it.level != ProgressLevel.NOT_STARTED } || blendIt != null -> ProgressLevel.IN_PROGRESS
            else -> ProgressLevel.NOT_STARTED
        }
        return ChapterRecord(
            chapter = chapter,
            letters = letters,
            blendItCompleted = blendIt?.isCompleted == true,
            blendItStars = blendIt?.starsEarned ?: 0,
            grade = grade,
            level = level,
            soundChecks = soundChecks.sortedBy { it.checkedAt }
        )
    }

    /** The mean grade of the finished letters and its level; "Still learning" until one is finished. */
    fun overall(letters: List<LetterPerformance>): Pair<Int?, ProgressLevel> {
        val grade = meanGrade(letters.filter { it.isCompleted })
        return when {
            grade != null -> grade to ProgressLevel.forGrade(grade)
            letters.any { it.level != ProgressLevel.NOT_STARTED } -> null to ProgressLevel.IN_PROGRESS
            else -> null to ProgressLevel.NOT_STARTED
        }
    }

    /** The rounded mean of the letters' grades, as a general average; null with no graded letter. */
    fun meanGrade(letters: List<LetterPerformance>): Int? {
        val grades = letters.mapNotNull { it.grade.takeIf { _ -> it.isCompleted } }
        return if (grades.isEmpty()) null else grades.average().roundToInt()
    }

    /** The rounded share of right answers, or null with no attempts. */
    fun score(correct: Int, attempts: Int): Int? =
        if (attempts == 0) null else (correct * 100.0 / attempts).roundToInt()
}
