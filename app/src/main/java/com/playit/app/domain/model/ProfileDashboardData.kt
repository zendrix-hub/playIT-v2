package com.playit.app.domain.model

/**
 * A learner's whole record. [overallGrade] is the mean grade of the letters finished so far, and [overallLevel] its
 * level. [recentScore] is the share of right answers in the last 7 days (null with no recent attempts).
 * [firstPlayedAt] and [lastPlayedAt] bound the period the record covers (null before the first attempt).
 */
data class ProfileDashboardData(
    val profile: Profile,
    val totalStars: Int,
    val completedLettersCount: Int,
    val totalLettersCount: Int,
    val letterPerformances: List<LetterPerformance>,
    val chapters: List<ChapterRecord> = emptyList(),
    val overallGrade: Int? = null,
    val overallLevel: ProgressLevel = ProgressLevel.NOT_STARTED,
    val recentScore: Int? = null,
    val blendItCompletedCount: Int,
    val blendItTotalCount: Int,
    val firstPlayedAt: Long? = null,
    val lastPlayedAt: Long? = null
) {
    /** Letters finished at one of the two lowest levels, to practice at home. */
    val needsPractice: List<LetterPerformance> get() = letterPerformances.filter { it.level.needsPractice }
}
