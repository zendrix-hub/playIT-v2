package com.playit.app.domain.model

/**
 * One chapter of a learner's record: its letters, its word game (Blend It) and the chapter's level. Once every letter
 * is finished the chapter's [grade] is the mean of its letters' grades, as a general average is; before that the
 * chapter is "Still learning" (or "Not started yet").
 */
data class ChapterRecord(
    val chapter: Int,
    val letters: List<LetterPerformance>,
    val blendItCompleted: Boolean,
    val blendItStars: Int,
    val grade: Int?,
    val level: ProgressLevel,
    /** The chapter's sound checks, oldest first (user choice, 2026-11-06). */
    val soundChecks: List<SoundCheckResult> = emptyList()
) {
    val lettersDone: Int get() = letters.count { it.isCompleted }
    val lettersTotal: Int get() = letters.size
    val letterList: String get() = letters.joinToString(", ") { it.symbol.lowercase() }
}
