package com.playit.app.data.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.playit.app.domain.model.ReportData
import com.playit.app.domain.model.RiskStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Native Android PdfDocument exporter generating offline, thesis-ready academic progress reports.
 * Formats executive summary, 7-day retention, at-risk letter alerts, and full 26-letter performance matrices.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §6 FR-12, FR-13.
 */
@Singleton
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val HEADER_BANNER_HEX = "#6C5CE7"
        const val TEXT_NAVY_HEX = "#1E293B"
        const val TEXT_MUTED_HEX = "#475569"
        const val SURFACE_SOFT_HEX = "#F8FAFC"
        const val BORDER_SLATE_HEX = "#E2E8F0"
        const val AT_RISK_BG_HEX = "#FFF7ED"
        const val AT_RISK_ACCENT_HEX = "#FF9F43"
        const val STATUS_MASTERED_HEX = "#00C853"
        const val STATUS_DEVELOPING_HEX = "#FFB800"
    }

    fun exportReport(reportData: ReportData): Result<File> {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            // 1. Header Banner (PrimaryJoy #6C5CE7 matching ParentDashboard)
            paint.color = Color.parseColor(HEADER_BANNER_HEX)
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 72f, paint)

            paint.color = Color.WHITE
            paint.textSize = 20f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("playIT — Phonics Progress & Retention Report", 24f, 36f, paint)

            paint.textSize = 10.5f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Learner: ${reportData.profileName}   |   Generated: ${reportData.generatedDate}   |   Offline Assessment", 24f, 56f, paint)

            var yPos = 86f

            // 2. Executive Summary Card (AdultCanvasSoft #F8FAFC with 1dp slate border #E2E8F0)
            paint.color = Color.parseColor(SURFACE_SOFT_HEX)
            val overviewHeight = 64f
            val overviewRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + overviewHeight)
            canvas.drawRoundRect(overviewRect, 10f, 10f, paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = Color.parseColor(BORDER_SLATE_HEX)
            canvas.drawRoundRect(overviewRect, 10f, 10f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.parseColor(TEXT_NAVY_HEX)
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Executive Summary", 38f, yPos + 20f, paint)

            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Overall Accuracy: ${reportData.overallAccuracyPercentage}%", 38f, yPos + 38f, paint)
            canvas.drawText("7-Day Retention: ${reportData.retentionScorePercentage}%", 38f, yPos + 54f, paint)
            canvas.drawText("Letters Mastered: ${reportData.completedLettersCount} / ${reportData.totalLettersCount}", 290f, yPos + 38f, paint)
            canvas.drawText("Total Stars: ${reportData.totalStars}   |   Blend-It: ${reportData.blendItCompletedCount}/${reportData.blendItTotalCount}", 290f, yPos + 54f, paint)

            yPos += overviewHeight + 12f

            // 3. At-Risk Warning Banner (ApricotGlow #FF9F43 — zero harsh red)
            if (reportData.atRiskLetters.isNotEmpty()) {
                paint.color = Color.parseColor(AT_RISK_BG_HEX)
                val atRiskHeight = 36f
                val atRiskRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + atRiskHeight)
                canvas.drawRoundRect(atRiskRect, 8f, 8f, paint)

                paint.color = Color.parseColor(AT_RISK_ACCENT_HEX)
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("At-Risk Phonemes Requiring Practice (${reportData.atRiskLetters.size}):", 38f, yPos + 16f, paint)

                paint.typeface = Typeface.DEFAULT
                val symbols = reportData.atRiskLetters.take(8).joinToString(", ") { "${it.symbol} (${it.accuracyPercentage.toInt()}%)" }
                canvas.drawText(symbols, 38f, yPos + 30f, paint)

                yPos += atRiskHeight + 10f
            }

            // 4. Letter Matrix Table Header
            paint.color = Color.parseColor(HEADER_BANNER_HEX)
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("26-Letter Phonics Mastery Matrix", 24f, yPos + 12f, paint)

            yPos += 18f
            paint.color = Color.parseColor(BORDER_SLATE_HEX)
            canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 18f, paint)

            paint.color = Color.parseColor(TEXT_NAVY_HEX)
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Letter", 34f, yPos + 13f, paint)
            canvas.drawText("Status", 110f, yPos + 13f, paint)
            canvas.drawText("Accuracy", 220f, yPos + 13f, paint)
            canvas.drawText("Attempts", 310f, yPos + 13f, paint)
            canvas.drawText("Hearts Lost", 400f, yPos + 13f, paint)
            canvas.drawText("Stars", 490f, yPos + 13f, paint)

            yPos += 18f

            // 5. Letter Matrix Table Rows (All 26 Phonemes)
            paint.typeface = Typeface.DEFAULT
            val rowHeight = 17f

            reportData.letterPerformances.take(26).forEachIndexed { index, lp ->
                if (yPos + rowHeight > pageHeight - 30) return@forEachIndexed

                if (index % 2 == 1) {
                    paint.color = Color.parseColor(SURFACE_SOFT_HEX)
                    canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + rowHeight, paint)
                }

                paint.color = Color.parseColor(TEXT_NAVY_HEX)
                paint.textSize = 9.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(lp.symbol.uppercase(), 34f, yPos + 12f, paint)

                paint.typeface = Typeface.DEFAULT
                val statusText = when (lp.riskStatus) {
                    RiskStatus.GREEN -> "Mastered"
                    RiskStatus.YELLOW -> "Developing"
                    RiskStatus.RED -> "At-Risk"
                }
                val statusColor = when (lp.riskStatus) {
                    RiskStatus.GREEN -> Color.parseColor(STATUS_MASTERED_HEX)
                    RiskStatus.YELLOW -> Color.parseColor(STATUS_DEVELOPING_HEX)
                    RiskStatus.RED -> Color.parseColor(AT_RISK_ACCENT_HEX)
                }
                paint.color = statusColor
                canvas.drawText(statusText, 110f, yPos + 12f, paint)

                paint.color = Color.parseColor(TEXT_NAVY_HEX)
                canvas.drawText("${lp.accuracyPercentage.toInt()}%", 220f, yPos + 12f, paint)
                canvas.drawText("${lp.totalAttempts}", 310f, yPos + 12f, paint)
                canvas.drawText("${lp.heartsLost}", 400f, yPos + 12f, paint)
                canvas.drawText("${lp.starsEarned} stars", 490f, yPos + 12f, paint)

                yPos += rowHeight
            }

            // 6. Footer Note
            paint.color = Color.parseColor(TEXT_MUTED_HEX)
            paint.textSize = 8.5f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("playIT Early Literacy Phonics System — Evaluated via Marungko Phonics Progression", 24f, pageHeight - 16f, paint)

            pdfDocument.finishPage(page)

            // Save PDF to documents directory
            val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            if (!docsDir.exists()) docsDir.mkdirs()

            val sanitizedProfile = reportData.profileName.replace(Regex("[^a-zA-Z0-9]"), "_")
            val pdfFile = File(docsDir, "playIT_Report_${sanitizedProfile}_${System.currentTimeMillis()}.pdf")
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            Result.success(pdfFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
