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

    @Test fun everyRowIsOnAPage_andEachChildEndsWithTheLegend() {
        listOf(frog, bear, owl).forEach { learner ->
            val pages = ReportLayout.learnerPages(learner)
            assertEquals("${learner.profile.name}: 7 chapters, 26 letters, 7 word games", 40, pages.sumOf { it.rows.size })
            assertEquals(ReportLayout.rows(learner), pages.flatMap { it.rows })
            assertTrue(pages.last().closing)
            assertEquals(1, pages.count { it.closing })
            assertTrue("fits in two pages", pages.size <= 2)
        }
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
