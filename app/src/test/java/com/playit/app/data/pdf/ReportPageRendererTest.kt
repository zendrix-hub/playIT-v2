package com.playit.app.data.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.playit.app.domain.calculator.RecordSamples
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.ReportData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Draws every page of a three-child report into images (build/outputs/roborazzi/report_page_N.png, for review) and
 * checks what is written: the class record, the letters of each started chapter with a level in words, what needs
 * help first, the legend, and no line to fill in by hand (user decisions 2026-11-07).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ReportPageRendererTest {

    private class Recording(bitmap: Bitmap) : Canvas(bitmap) {
        val texts = mutableListOf<String>()
        override fun drawText(text: String, x: Float, y: Float, paint: Paint) {
            texts += text
            super.drawText(text, x, y, paint)
        }
    }

    private val frog = RecordSamples.learner(1, "Frog", done = 9, sayMisses = { if (it % 3 == 0) 1 else 0 },
        findMisses = { if (it == 6) 3 else if (it % 4 == 0) 1 else 0 })
    private val bear = RecordSamples.learner(2, "Bear", done = 4, sayMisses = { 1 })
    private val owl = RecordSamples.learner(3, "Owl", done = 0, startedNext = false)
    private val report = ReportData("November 5, 2026", listOf(frog, bear, owl))

    private fun render(): List<List<String>> {
        val pages = ReportLayout.pages(report)
        val out = File("build/outputs/roborazzi").apply { mkdirs() }
        return pages.mapIndexed { i, page ->
            val scale = 2f
            val bitmap = Bitmap.createBitmap((ReportLayout.PAGE_WIDTH * scale).toInt(),
                (ReportLayout.PAGE_HEIGHT * scale).toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Recording(bitmap)
            canvas.scale(scale, scale)
            ReportPageRenderer().draw(canvas, page, report, i + 1, pages.size)
            File(out, "report_page_${i + 1}.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            canvas.texts
        }
    }

    private val fillIns = listOf("School", "Adviser", "Grade and Section", "Prepared by", "Checked by", "Date:")

    @Test fun theClassPage_listsEveryChildWithLevelsInWords_andADashForAChapterNotStarted() {
        val page = render().first()
        assertTrue(page.contains("CLASS RECORD"))
        listOf("Frog", "Bear", "Owl").forEach { assertTrue(it, page.contains(it)) }
        // Owl has started nothing: seven dashes and an overall "Not started yet"; Frog has started 3 chapters, Bear 2.
        assertEquals(4 + 5 + 7, page.count { it == "-" })
        assertTrue(page.contains("Not started yet"))
        fillIns.forEach { f -> assertTrue("no $f line", page.none { it.startsWith(f) }) }
    }

    @Test fun eachChildsRecord_startedChaptersOnly_whatNeedsHelpFirst_andTheLegend() {
        val pages = render()
        assertEquals("a class page and one page per child", 4, pages.size)
        val all = pages.flatten()
        listOf(frog, bear).forEach { learner ->
            learner.chapters.filter { ReportLayout.started(it) }.forEach { ch ->
                assertTrue("chapter ${ch.chapter}", all.any { it.startsWith("Chapter ${ch.chapter}:") })
                ch.letters.forEach { l -> assertTrue(l.keyWord, all.contains(l.keyWord)) }
            }
        }
        assertTrue(all.contains("Not started yet: Chapters 4 to 7."))
        assertTrue(all.contains("Not started yet: Chapters 1 to 7."))
        assertTrue("a later chapter's words are not listed", !all.contains("Zebra"))
        // Every finished letter's grade sits beside its level in words.
        frog.letterPerformances.filter { it.isCompleted }.forEach { l ->
            assertTrue("${l.symbol}: ${l.level.label}", all.any { it == l.level.label || l.level.label.startsWith(it) })
        }
        assertTrue(pages[1].any { it.startsWith("Needs help with: ") })
        assertTrue(pages[2].contains("No letter needs extra help. Playing now: o (Orange)"))
        assertTrue(pages[3].contains("Not played yet."))
        // Stars, percentages and the remark on every row are gone; grades stay.
        listOf("Stars", "Score", "Remarks").forEach { h -> assertTrue("no $h column", all.none { it == h }) }
        assertTrue("no percent on a row", all.none { Regex("""\d+%""").matches(it) })
        assertTrue(all.none { it.startsWith("Stars:") || it.startsWith("Last 7 days") })
        assertEquals(3, all.count { it == "LEARNER PROGRESS RECORD" })
        assertTrue(all.count { it.startsWith("What the words mean") } >= 4)
        ProgressLevel.graded.forEach { level -> assertTrue(level.label, all.any { it.startsWith("Grade ${level.band}:") }) }
        fillIns.forEach { f -> assertTrue("no $f line", all.none { it.startsWith(f) }) }
        assertTrue(pages.last().contains("Page ${pages.size} of ${pages.size}"))
    }
}
