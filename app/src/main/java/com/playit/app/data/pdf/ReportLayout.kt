package com.playit.app.data.pdf

import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ReportData

/**
 * How the report falls into landscape A4 pages, like a DepEd class record: a class page with every learner (when there
 * are two or more), then each learner's letters chapter by chapter, with the legend and the signature lines after
 * the last row. A row that doesn't fit moves to the next page; none is dropped. Plain Kotlin, so it can be tested
 * without drawing.
 */
object ReportLayout {
    const val PAGE_WIDTH = 842
    const val PAGE_HEIGHT = 595
    const val MARGIN = 28f
    const val FOOTER = 22f

    /** Space taken by the blocks around the rows, in points. */
    const val FIRST_HEADER = 132f
    const val NEXT_HEADER = 52f
    const val TABLE_HEADER = 20f
    const val CLOSING = 150f   // the legend and the signature lines

    const val CHAPTER_ROW = 16f
    const val LETTER_ROW = 16f
    const val BLEND_ROW = 14f
    const val CLASS_ROW = 34f

    sealed interface Row {
        val height: Float
    }

    data class ChapterRow(val chapter: ChapterRecord) : Row { override val height = CHAPTER_ROW }
    data class LetterRow(val letter: LetterPerformance) : Row { override val height = LETTER_ROW }
    data class BlendRow(val chapter: ChapterRecord) : Row { override val height = BLEND_ROW }

    sealed interface Page

    data class ClassPage(val learners: List<ProfileDashboardData>) : Page

    /** One page of a learner's record. [closing] puts the legend and signature lines under the rows. */
    data class LearnerPage(
        val learner: ProfileDashboardData,
        val rows: List<Row>,
        val first: Boolean,
        val closing: Boolean
    ) : Page

    fun rows(learner: ProfileDashboardData): List<Row> = learner.chapters.flatMap { chapter ->
        listOf<Row>(ChapterRow(chapter)) + chapter.letters.map { LetterRow(it) } + BlendRow(chapter)
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
