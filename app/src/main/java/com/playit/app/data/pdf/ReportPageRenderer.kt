package com.playit.app.data.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.playit.app.data.pdf.ReportLayout.BLEND_ROW
import com.playit.app.data.pdf.ReportLayout.CLASS_ROW
import com.playit.app.data.pdf.ReportLayout.FIRST_HEADER
import com.playit.app.data.pdf.ReportLayout.MARGIN
import com.playit.app.data.pdf.ReportLayout.NEXT_HEADER
import com.playit.app.data.pdf.ReportLayout.PAGE_HEIGHT
import com.playit.app.data.pdf.ReportLayout.PAGE_WIDTH
import com.playit.app.data.pdf.ReportLayout.TABLE_HEADER
import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.ReportData
import com.playit.app.presentation.theme.LevelColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Draws one page of the report in the form of a DepEd class record (adviser, 2026-11-05): a header with the school
 * fields to fill in by hand, a table with a level in words on every row, the legend and the signature lines. It draws
 * on any Canvas, so the PDF and the tests share it.
 */
class ReportPageRenderer {
    private val ink = 0xFF3B2416.toInt()
    private val muted = 0xFF6E5643.toInt()
    private val line = 0xFFD9C9AE.toInt()
    private val headFill = 0xFFF3E6CF.toInt()
    private val chapterFill = 0xFFFFF6DC.toInt()

    private val regular = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    private val bold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)

    private val left = MARGIN
    private val right = PAGE_WIDTH - MARGIN
    private val width = right - left

    fun draw(canvas: Canvas, page: ReportLayout.Page, report: ReportData, pageNumber: Int, pageCount: Int) {
        canvas.drawColor(0xFFFFFFFF.toInt())
        when (page) {
            is ReportLayout.ClassPage -> drawClassPage(canvas, page, report)
            is ReportLayout.LearnerPage -> drawLearnerPage(canvas, page, report)
        }
        footer(canvas, pageNumber, pageCount)
    }

    // ------------------------------------------------------------------------------------------------ text helpers

    private fun text(c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int = ink, isBold: Boolean = false,
                     align: Paint.Align = Paint.Align.LEFT) {
        p.style = Paint.Style.FILL
        p.color = color
        p.textSize = size
        p.typeface = if (isBold) bold else regular
        p.textAlign = align
        c.drawText(s, x, y, p)
        p.textAlign = Paint.Align.LEFT
    }

    /** Words wrapped to [maxWidth] at [size]. */
    private fun wrap(s: String, maxWidth: Float, size: Float, isBold: Boolean = false): List<String> {
        p.textSize = size
        p.typeface = if (isBold) bold else regular
        val lines = mutableListOf<String>()
        var current = ""
        s.split(" ").forEach { word ->
            val trial = if (current.isEmpty()) word else "$current $word"
            if (p.measureText(trial) <= maxWidth || current.isEmpty()) current = trial else {
                lines += current
                current = word
            }
        }
        if (current.isNotEmpty()) lines += current
        return lines
    }

    private fun fill(c: Canvas, l: Float, t: Float, r: Float, b: Float, color: Int, radius: Float = 0f) {
        p.style = Paint.Style.FILL
        p.color = color
        if (radius > 0f) c.drawRoundRect(RectF(l, t, r, b), radius, radius, p) else c.drawRect(l, t, r, b, p)
    }

    private fun hline(c: Canvas, l: Float, y: Float, r: Float, color: Int = line, stroke: Float = 0.6f) {
        p.style = Paint.Style.STROKE
        p.color = color
        p.strokeWidth = stroke
        c.drawLine(l, y, r, y, p)
        p.style = Paint.Style.FILL
    }

    /** A level on its colour, its words wrapped to fit the cell. */
    private fun levelCell(c: Canvas, level: ProgressLevel, l: Float, t: Float, r: Float, b: Float, size: Float,
                          suffix: String? = null) {
        val colors = LevelColors.of(level)
        fill(c, l + 1.5f, t + 1.5f, r - 1.5f, b - 1.5f, colors.fill, radius = 4f)
        val words = wrap(level.label, r - l - 8f, size, isBold = true) + listOfNotNull(suffix)
        val lineHeight = size + 1.5f
        var y = (t + b) / 2f - (words.size - 1) * lineHeight / 2f + size * 0.35f
        words.forEachIndexed { i, s ->
            val isSuffix = suffix != null && i == words.lastIndex
            text(c, s, (l + r) / 2f, y, if (isSuffix) size - 0.5f else size, colors.ink, isBold = !isSuffix,
                align = Paint.Align.CENTER)
            y += lineHeight
        }
    }

    private fun blank(c: Canvas, label: String, x: Float, y: Float, lineWidth: Float): Float {
        text(c, label, x, y, 8.5f, muted)
        p.textSize = 8.5f
        p.typeface = regular
        val start = x + p.measureText(label) + 4f
        hline(c, start, y + 2f, start + lineWidth, ink, 0.6f)
        return start + lineWidth + 18f
    }

    // ------------------------------------------------------------------------------------------------ blocks

    private fun title(c: Canvas, heading: String): Float {
        var y = MARGIN + 14f
        text(c, heading, PAGE_WIDTH / 2f, y, 15f, ink, isBold = true, align = Paint.Align.CENTER)
        y += 15f
        text(c, "Reading: letter sounds (Marungko approach), Grade 1. Made with PlayIT.", PAGE_WIDTH / 2f, y, 9f, muted,
            align = Paint.Align.CENTER)
        y += 20f
        var x = left
        x = blank(c, "School:", x, y, 170f)
        x = blank(c, "School Year:", x, y, 70f)
        x = blank(c, "Grade and Section:", x, y, 100f)
        blank(c, "Adviser:", x, y, 150f)
        return y + 8f
    }

    private fun footer(c: Canvas, page: Int, count: Int) {
        val y = PAGE_HEIGHT - MARGIN + 6f
        hline(c, left, y - 10f, right)
        text(c, "PlayIT: offline letter-sound practice. Made on this phone; no data leaves it.", left, y, 7.5f, muted)
        text(c, "Page $page of $count", right, y, 7.5f, muted, align = Paint.Align.RIGHT)
    }

    private fun legend(c: Canvas, top: Float) {
        var y = top + 12f
        text(c, "What the words mean (DepEd Order 8, s. 2015)", left, y, 9.5f, ink, isBold = true)
        y += 6f
        val levels = ProgressLevel.graded + listOf(ProgressLevel.IN_PROGRESS, ProgressLevel.NOT_STARTED)
        val colWidth = width / 2f
        levels.forEachIndexed { i, level ->
            val x = left + (i % 2) * colWidth
            val rowY = y + (i / 2) * 15f
            val colors = LevelColors.of(level)
            fill(c, x, rowY + 3f, x + 120f, rowY + 15f, colors.fill, radius = 3f)
            text(c, level.label, x + 60f, rowY + 12f, 7.5f, colors.ink, isBold = true, align = Paint.Align.CENTER)
            val band = level.band?.let { "Grade $it. " } ?: ""
            text(c, band + level.remark, x + 126f, rowY + 12f, 7.8f, ink)
        }
        y += 4 * 15f + 14f
        wrap(
            "A letter is graded once the child finishes it. Say It and Find It show the right answers out of the " +
                "tries. The score is the share of right answers in both; the grade is that score changed with the " +
                "DepEd transmutation table (60% right is a grade of 75, 100% is 100). A chapter's grade is the " +
                "average of its letters.",
            width, 7.5f
        ).forEach {
            text(c, it, left, y, 7.5f, muted)
            y += 10f
        }
        y += 16f
        var x = left
        x = blank(c, "Prepared by:", x, y, 190f)
        x = blank(c, "Checked by:", x, y, 190f)
        blank(c, "Date:", x, y, 90f)
        text(c, "(parent, guardian or teacher)", left + 60f, y + 11f, 7f, muted)
    }

    // ------------------------------------------------------------------------------------------------ class page

    private val classCols = floatArrayOf(24f, 150f, 92f, 62f, 62f, 62f, 62f, 62f, 62f, 62f, 86f)

    private fun drawClassPage(c: Canvas, page: ReportLayout.ClassPage, report: ReportData) {
        var y = title(c, "CLASS RECORD")
        text(c, "Every child on this phone. Date made: ${report.generatedDate}.", left, y + 12f, 9f, ink)
        y += 24f
        val headers = listOf("No.", "Learner", "Letters learned") + (1..7).map { "Chapter $it" } + "Overall"
        fill(c, left, y, right, y + TABLE_HEADER, headFill)
        var x = left
        headers.forEachIndexed { i, h ->
            text(c, h, x + classCols[i] / 2f, y + 13f, 7.8f, ink, isBold = true, align = Paint.Align.CENTER)
            x += classCols[i]
        }
        y += TABLE_HEADER
        page.learners.forEachIndexed { index, learner ->
            x = left
            val b = y + CLASS_ROW
            text(c, "${index + 1}", x + classCols[0] / 2f, y + 20f, 9f, ink, align = Paint.Align.CENTER)
            x += classCols[0]
            text(c, learner.profile.name, x + 4f, y + 20f, 10f, ink, isBold = true)
            x += classCols[1]
            text(c, "${learner.completedLettersCount} of ${learner.totalLettersCount}", x + classCols[2] / 2f, y + 20f,
                9f, ink, align = Paint.Align.CENTER)
            x += classCols[2]
            (0 until 7).forEach { i ->
                val chapter = learner.chapters.getOrNull(i)
                if (chapter != null) levelCell(c, chapter.level, x, y, x + classCols[3 + i], b, 7f)
                x += classCols[3 + i]
            }
            levelCell(c, learner.overallLevel, x, y, x + classCols[10], b, 7.2f,
                suffix = learner.overallGrade?.let { "grade $it" })
            hline(c, left, b, right)
            y = b
        }
        legend(c, y + 10f)
    }

    // ------------------------------------------------------------------------------------------------ learner pages

    private val cols = floatArrayOf(96f, 64f, 64f, 44f, 46f, 44f, 140f, 288f)
    private val colNames = listOf("Letter and word", "Say It", "Find It", "Stars", "Score", "Grade", "Level", "Remarks")

    private fun drawLearnerPage(c: Canvas, page: ReportLayout.LearnerPage, report: ReportData) {
        val learner = page.learner
        var y: Float
        if (page.first) {
            y = title(c, "LEARNER PROGRESS RECORD")
            learnerSummary(c, learner, report, y)
            y = MARGIN + FIRST_HEADER
        } else {
            text(c, "LEARNER PROGRESS RECORD (continued)", left, MARGIN + 14f, 11f, ink, isBold = true)
            text(c, "Learner: ${learner.profile.name}", left, MARGIN + 32f, 9.5f, ink)
            y = MARGIN + NEXT_HEADER
        }
        if (page.rows.isNotEmpty()) {
            fill(c, left, y, right, y + TABLE_HEADER, headFill)
            var x = left
            colNames.forEachIndexed { i, h ->
                val alignLeft = i == 0 || i == colNames.lastIndex
                text(c, h, if (alignLeft) x + 4f else x + cols[i] / 2f, y + 13f, 7.8f, ink, isBold = true,
                    align = if (alignLeft) Paint.Align.LEFT else Paint.Align.CENTER)
                x += cols[i]
            }
            y += TABLE_HEADER
            page.rows.forEach { row ->
                when (row) {
                    is ReportLayout.ChapterRow -> chapterRow(c, row.chapter, y)
                    is ReportLayout.LetterRow -> letterRow(c, row, y)
                    is ReportLayout.BlendRow -> blendRow(c, row.chapter, y)
                }
                y += row.height
            }
        }
        if (page.closing) legend(c, y + 8f)
    }

    private fun learnerSummary(c: Canvas, learner: ProfileDashboardData, report: ReportData, top: Float) {
        var y = top + 12f
        text(c, "Learner: ${learner.profile.name}", left, y, 11f, ink, isBold = true)
        text(c, "Period covered: ${period(learner)}", left + 230f, y, 9f, ink)
        text(c, "Date made: ${report.generatedDate}", right, y, 9f, ink, align = Paint.Align.RIGHT)
        y += 8f
        fill(c, left, y, right, y + 34f, chapterFill, radius = 6f)
        text(c, "Overall so far", left + 10f, y + 21f, 9.5f, ink, isBold = true)
        levelCell(c, learner.overallLevel, left + 92f, y + 4f, left + 232f, y + 30f, 8.5f)
        var x = left + 244f
        learner.overallGrade?.let {
            text(c, "Average grade $it", x, y + 21f, 9f, ink)
            x += 104f
        }
        val facts = listOfNotNull(
            "Learned ${learner.completedLettersCount} of ${learner.totalLettersCount} sounds",
            "Word games done: ${learner.blendItCompletedCount} of ${learner.blendItTotalCount}",
            "Stars: ${learner.totalStars}",
            learner.recentScore?.let { "Last 7 days: $it% right" }
        )
        text(c, facts.joinToString("     "), x, y + 21f, 9f, ink)
    }

    private fun period(learner: ProfileDashboardData): String {
        val first = learner.firstPlayedAt ?: return "not played yet"
        val last = learner.lastPlayedAt ?: first
        val f = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val a = f.format(Date(first))
        val b = f.format(Date(last))
        return if (a == b) a else "$a to $b"
    }

    private fun chapterRow(c: Canvas, chapter: ChapterRecord, top: Float) {
        fill(c, left, top + 1f, right, top + ReportLayout.CHAPTER_ROW, chapterFill)
        val grade = chapter.grade?.let { ", average grade $it" } ?: ""
        text(c, "Chapter ${chapter.chapter}: sounds ${chapter.letterList}. ${chapter.lettersDone} of " +
            "${chapter.lettersTotal} finished. ${chapter.level.label}$grade.", left + 4f, top + 11.5f, 8.5f, ink,
            isBold = true)
    }

    private fun letterRow(c: Canvas, row: ReportLayout.LetterRow, top: Float) {
        val l = row.letter
        val b = top + row.height
        val base = top + 11.5f
        var x = left
        text(c, l.symbol.uppercase() + l.symbol.lowercase(), x + 4f, base, 9f, ink, isBold = true)
        text(c, l.keyWord, x + 26f, base, 8.5f, ink)
        x += cols[0]
        val cells = listOf(
            if (l.sayItAttempts > 0) "${l.sayItCorrect} of ${l.sayItAttempts}" else "-",
            if (l.findItAttempts > 0) "${l.findItCorrect} of ${l.findItAttempts}" else "-",
            if (l.isCompleted) "${l.starsEarned}" else "-",
            // A score and a grade only for a finished letter, as the level: "Still learning" shows no 0%.
            if (l.isCompleted) l.score?.let { "$it%" } ?: "-" else "-",
            if (l.isCompleted) l.grade?.toString() ?: "-" else "-"
        )
        cells.forEachIndexed { i, s ->
            text(c, s, x + cols[i + 1] / 2f, base, 8.5f, ink, align = Paint.Align.CENTER)
            x += cols[i + 1]
        }
        levelCell(c, l.level, x, top, x + cols[6], b, 7.5f)
        x += cols[6]
        text(c, l.level.remark, x + 4f, base, 8f, muted)
        hline(c, left, b, right)
    }

    private fun blendRow(c: Canvas, chapter: ChapterRecord, top: Float) {
        val s = if (chapter.blendItCompleted) "done, ${chapter.blendItStars} of 3 stars" else "not yet"
        text(c, "Word game (Blend It) for chapter ${chapter.chapter}: $s", left + 26f, top + BLEND_ROW - 4f, 8f, muted)
        hline(c, left, top + BLEND_ROW, right)
    }
}
