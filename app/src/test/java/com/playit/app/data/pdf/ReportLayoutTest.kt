package com.playit.app.data.pdf

import com.playit.app.domain.calculator.RecordSamples
import com.playit.app.domain.model.ReportData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The report's pages: a class page for two or more children, then every row of every child, none dropped. */
class ReportLayoutTest {
    private val frog = RecordSamples.learner(1, "Frog", done = 9)
    private val bear = RecordSamples.learner(2, "Bear", done = 4)
    private val owl = RecordSamples.learner(3, "Owl", done = 0, startedNext = false)

    @Test fun oneChild_hasNoClassPage() {
        val pages = ReportLayout.pages(ReportData("Nov 5, 2026", listOf(frog)))
        assertTrue(pages.none { it is ReportLayout.ClassPage })
        assertTrue((pages.first() as ReportLayout.LearnerPage).first)
    }

    @Test fun severalChildren_startWithTheClassRecord_thenEachChild() {
        val pages = ReportLayout.pages(ReportData("Nov 5, 2026", listOf(frog, bear, owl)))
        val classPage = pages.first() as ReportLayout.ClassPage
        assertEquals(listOf("Frog", "Bear", "Owl"), classPage.learners.map { it.profile.name })
        val learners = pages.filterIsInstance<ReportLayout.LearnerPage>()
        assertEquals(listOf("Frog", "Bear", "Owl"), learners.filter { it.first }.map { it.learner.profile.name })
    }

    /**
     * User decisions 2026-11-07: only the chapters a child has started get rows (a chapter line, its letters, its word
     * game), then one line for the rest; every row is on a page, and each child ends with the legend.
     */
    @Test fun onlyStartedChapters_thenOneLineForTheRest_onOnePage() {
        mapOf(frog to listOf(1, 2, 3), bear to listOf(1, 2), owl to emptyList()).forEach { (learner, started) ->
            val pages = ReportLayout.learnerPages(learner)
            val rows = pages.flatMap { it.rows }
            assertEquals(ReportLayout.rows(learner), rows)
            assertEquals(started, rows.filterIsInstance<ReportLayout.ChapterRow>().map { it.chapter.chapter })
            val letters = learner.chapters.filter { it.chapter in started }.sumOf { it.letters.size }
            assertEquals(letters, rows.count { it is ReportLayout.LetterRow })
            assertEquals((1..7).filterNot { it in started }, rows.filterIsInstance<ReportLayout.NotStartedRow>().single().chapters)
            assertTrue(pages.last().closing)
            assertEquals(1, pages.count { it.closing })
            assertEquals("${learner.profile.name} fits on one page", 1, pages.size)
        }
    }

    @Test fun chapterNames_readAsARangeOrAList() {
        assertEquals("Chapter 7", ReportLayout.chapterNames(listOf(7)))
        assertEquals("Chapters 4 to 7", ReportLayout.chapterNames(listOf(4, 5, 6, 7)))
        assertEquals("Chapters 3, 5 and 7", ReportLayout.chapterNames(listOf(7, 3, 5)))
    }

    @Test fun noPage_runsPastItsBottom() {
        val bottom = ReportLayout.PAGE_HEIGHT - 2 * ReportLayout.MARGIN - ReportLayout.FOOTER
        ReportLayout.learnerPages(frog).forEach { page ->
            val header = if (page.first) ReportLayout.FIRST_HEADER else ReportLayout.NEXT_HEADER
            val used = header + ReportLayout.TABLE_HEADER + page.rows.sumOf { it.height.toDouble() }.toFloat() +
                if (page.closing) ReportLayout.CLOSING else 0f
            assertTrue("page uses $used of $bottom", used <= bottom)
        }
    }
}
