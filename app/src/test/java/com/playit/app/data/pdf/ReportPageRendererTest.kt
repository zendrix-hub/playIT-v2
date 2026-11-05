package com.playit.app.data.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.playit.app.domain.calculator.RecordSamples
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.ReportData
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Draws every page of a three-child report into images (build/outputs/roborazzi/report_page_N.png, for review) and
 * checks what is written: the class record, every letter, a level in words on every scored row, the legend and the
 * signature lines.
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

    @Test fun theClassPage_listsEveryChildWithLevelsInWords() {
        val page = render().first()
        assertTrue(page.contains("CLASS RECORD"))
        listOf("Frog", "Bear", "Owl").forEach { assertTrue(it, page.contains(it)) }
        assertTrue(page.contains("Not started yet"))
        assertTrue(page.any { it.startsWith("School:") })
        assertTrue(page.contains("Prepared by:"))
    }

    @Test fun eachChildsRecord_hasEveryLetter_aLevelForEachScore_andTheLegend() {
        val pages = render()
        val all = pages.flatten()
        RecordSamples.words.forEach { assertTrue(it, all.contains(it)) }
        (1..7).forEach { ch -> assertTrue("chapter $ch", all.any { it.startsWith("Chapter $ch:") }) }
        // Every percentage on a letter row sits beside a level in words.
        frog.letterPerformances.filter { it.score != null }.forEach { l ->
            assertTrue("${l.symbol}: ${l.level.label}", all.any { it == l.level.label || l.level.label.startsWith(it) })
        }
        assertTrue(all.count { it == "LEARNER PROGRESS RECORD" } == 3)
        assertTrue(all.count { it.startsWith("What the words mean") } >= 4)
        ProgressLevel.graded.forEach { level -> assertTrue(level.label, all.any { it.startsWith("Grade ${level.band}") }) }
        assertTrue(pages.last().contains("Page ${pages.size} of ${pages.size}"))
    }
}
