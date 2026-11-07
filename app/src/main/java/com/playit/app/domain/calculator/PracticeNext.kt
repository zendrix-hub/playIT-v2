package com.playit.app.domain.calculator

import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.ProgressLevel

/**
 * What to practice next (user decision 2026-11-07): at most two letters, worst first, from the letters the child
 * finished below Satisfactory (a grade under 80); with none, the letter the child is playing now; with neither,
 * nothing, and a word to keep playing. The Parent Zone's "Practice next" and the PDF's "Needs help with" use it, so
 * the parent and the teacher see the same letters.
 */
object PracticeNext {
    const val MAX = 2

    sealed interface Pick {
        /** Letters finished below Satisfactory, worst first. */
        data class NeedsHelp(val letters: List<LetterPerformance>) : Pick
        /** Nothing needs help; this is the letter being played now. */
        data class PlayingNow(val letter: LetterPerformance) : Pick
        /** Nothing needs help and nothing is half done. */
        data object AllGood : Pick
    }

    /** [chapters] in their order, each with its letters in order. */
    fun of(chapters: List<ChapterRecord>): Pick {
        val inOrder = chapters.sortedBy { it.chapter }.flatMap { it.letters }
        val weak = inOrder.withIndex()
            .filter { it.value.level.needsPractice }
            .sortedWith(compareBy({ it.value.grade ?: 0 }, { it.index }))
            .take(MAX)
            .map { it.value }
        if (weak.isNotEmpty()) return Pick.NeedsHelp(weak)
        val now = inOrder.firstOrNull { it.level == ProgressLevel.IN_PROGRESS }
        return if (now != null) Pick.PlayingNow(now) else Pick.AllGood
    }

    /** "b (Ball), k (Kite)". */
    fun names(letters: List<LetterPerformance>): String =
        letters.joinToString(", ") { "${it.symbol.lowercase()} (${it.keyWord})" }
}
