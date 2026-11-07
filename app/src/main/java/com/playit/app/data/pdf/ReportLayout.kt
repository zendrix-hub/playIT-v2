package com.playit.app.data.pdf

import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.ReportData

/**
 * How the report falls into landscape A4 pages, structured like a DepEd class record: a class page with every learner
 * (when there are two or more), then each learner's record, written for the facilitator, teacher first (user
 * decisions 2026-11-07): the chapters the child has started, letter by letter, then one line for the chapters not
 * started yet, and the legend after the last row. No lines to fill in by hand. A row that doesn't fit moves to the
 * next page; none is dropped. Plain Kotlin, so it can be tested without drawing.
 */
object ReportLayout {
    const val PAGE_WIDTH = 842
    const val PAGE_HEIGHT = 595
    const val MARGIN = 28f
    const val FOOTER = 22f

    /** Space taken by the blocks around the rows, in points. */
    const val FIRST_HEADER = 118f  // the title, the learner's line, the summary and "Needs help with"
    const val NEXT_HEADER = 52f
    const val TABLE_HEADER = 20f
    const val CLOSING = 90f    // the legend

    const val CHAPTER_ROW = 16f
    const val LETTER_ROW = 15f
    const val BLEND_ROW = 14f
    const val CLASS_ROW = 34f

    sealed interface Row {
        val height: Float
    }

    data class ChapterRow(val chapter: ChapterRecord) : Row { override val height = CHAPTER_ROW }
    data class LetterRow(val letter: LetterPerformance) : Row { override val height = LETTER_ROW }
    data class BlendRow(val chapter: ChapterRecord) : Row { override val height = BLEND_ROW }
    /** One line for every chapter not started yet, in place of their letter rows. */
    data class NotStartedRow(val chapters: List<Int>) : Row { override val height = CHAPTER_ROW }

    sealed interface Page

    data class ClassPage(val learners: List<ProfileDashboardData>) : Page

    /** One page of a learner's record. [closing] puts the legend under the rows. */
    data class LearnerPage(
        val learner: ProfileDashboardData,
        val rows: List<Row>,
        val first: Boolean,
        val closing: Boolean
    ) : Page

    /** A chapter the child has begun: a letter played or its word game done. */
    fun started(chapter: ChapterRecord): Boolean = chapter.level != ProgressLevel.NOT_STARTED || chapter.blendItCompleted

    fun rows(learner: ProfileDashboardData): List<Row> {
        val (begun, notYet) = learner.chapters.partition(::started)
        return begun.flatMap { chapter ->
            listOf<Row>(ChapterRow(chapter)) + chapter.letters.map { LetterRow(it) } + BlendRow(chapter)
        } + listOfNotNull(notYet.takeIf { it.isNotEmpty() }?.let { NotStartedRow(it.map { c -> c.chapter }) })
    }

    /** "Chapter 4", "Chapters 4 to 7", or "Chapters 3, 5 and 7". */
    fun chapterNames(numbers: List<Int>): String {
        val n = numbers.sorted()
        return when {
            n.size == 1 -> "Chapter ${n[0]}"
            n.zipWithNext().all { (a, b) -> b == a + 1 } -> "Chapters ${n.first()} to ${n.last()}"
            else -> "Chapters " + n.dropLast(1).joinToString(", ") + " and ${n.last()}"
        }
    }

    private val bodyHeight = PAGE_HEIGHT - 2 * MARGIN - FOOTER

    fun pages(report: ReportData): List<Page> {
        val pages = mutableListOf<Page>()
        if (report.learners.size > 1) pages += ClassPage(report.learners)
        report.learners.forEach { pages += learnerPages(it) }
        return pages
    }

    fun learnerPages(learner: ProfileDashboardData): List<LearnerPage> {
        val pages = mutableListOf<LearnerPage>()
        var current = mutableListOf<Row>()
        var room = bodyHeight - FIRST_HEADER - TABLE_HEADER
        rows(learner).forEach { row ->
            if (row.height > room && current.isNotEmpty()) {
                pages += LearnerPage(learner, current, first = pages.isEmpty(), closing = false)
                current = mutableListOf()
                room = bodyHeight - NEXT_HEADER - TABLE_HEADER
            }
            current += row
            room -= row.height
        }
        if (room >= CLOSING) {
            pages += LearnerPage(learner, current, first = pages.isEmpty(), closing = true)
        } else {
            pages += LearnerPage(learner, current, first = pages.isEmpty(), closing = false)
            pages += LearnerPage(learner, emptyList(), first = false, closing = true)
        }
        return pages
    }
}
