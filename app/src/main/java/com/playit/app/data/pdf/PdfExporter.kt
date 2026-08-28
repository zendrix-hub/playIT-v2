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
 * Formats executive summary, 7-day retention, at-risk letter alerts, and full 28-phoneme performance matrices.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §6 FR-12, FR-13.
 */
@Singleton
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

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

            // 1. Header Banner (Theme FriendlyPurple / LearningBlue)
            paint.color = Color.parseColor("#4C68D7")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 72f, paint)

            paint.color = Color.WHITE
            paint.textSize = 20f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("playIT — Phonics Progress & Retention Report", 24f, 36f, paint)

            paint.textSize = 10.5f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Learner: ${reportData.profileName}   |   Generated: ${reportData.generatedDate}   |   Offline Assessment", 24f, 56f, paint)

            var yPos = 86f

            // 2. Executive Summary Card
            paint.color = Color.parseColor("#F4F7FC")
            val overviewHeight = 64f
            val overviewRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + overviewHeight)
            canvas.drawRoundRect(overviewRect, 10f, 10f, paint)

            paint.color = Color.parseColor("#2D3436")
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Executive Summary", 38f, yPos + 20f, paint)

            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Overall Accuracy: ${reportData.overallAccuracyPercentage}%", 38f, yPos + 38f, paint)
            canvas.drawText("7-Day Retention: ${reportData.retentionScorePercentage}%", 38f, yPos + 54f, paint)
            canvas.drawText("Letters Mastered: ${reportData.completedLettersCount} / ${reportData.totalLettersCount}", 290f, yPos + 38f, paint)
            canvas.drawText("Total Stars: ${reportData.totalStars} ★   |   Blend-It: ${reportData.blendItCompletedCount}/${reportData.blendItTotalCount}", 290f, yPos + 54f, paint)

            yPos += overviewHeight + 12f

            // 3. At-Risk Warning Banner (if any)
            if (reportData.atRiskLetters.isNotEmpty()) {
                paint.color = Color.parseColor("#FFF1F0")
                val atRiskHeight = 36f
                val atRiskRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + atRiskHeight)
                canvas.drawRoundRect(atRiskRect, 8f, 8f, paint)

                paint.color = Color.parseColor("#D63031")
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("⚠️ At-Risk Phonemes Requiring Practice (${reportData.atRiskLetters.size}):", 38f, yPos + 16f, paint)

                paint.typeface = Typeface.DEFAULT
                val symbols = reportData.atRiskLetters.take(8).joinToString(", ") { "${it.symbol} (${it.accuracyPercentage.toInt()}%)" }
                canvas.drawText(symbols, 38f, yPos + 30f, paint)

                yPos += atRiskHeight + 10f
            }

            // 4. Letter Matrix Table Header
            paint.color = Color.parseColor("#4C68D7")
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("28-Letter Phonics Mastery Matrix", 24f, yPos + 12f, paint)

            yPos += 18f
            paint.color = Color.parseColor("#E4EBF5")
            canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 18f, paint)

            paint.color = Color.parseColor("#2D3436")
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Letter", 34f, yPos + 13f, paint)
            canvas.drawText("Status", 110f, yPos + 13f, paint)
            canvas.drawText("Accuracy", 220f, yPos + 13f, paint)
            canvas.drawText("Attempts", 310f, yPos + 13f, paint)
            canvas.drawText("Hearts Lost", 400f, yPos + 13f, paint)
            canvas.drawText("Stars", 490f, yPos + 13f, paint)

            yPos += 18f

            // 5. Letter Matrix Table Rows (All 28 Phonemes)
            paint.typeface = Typeface.DEFAULT
            val rowHeight = 17f

            reportData.letterPerformances.take(28).forEachIndexed { index, lp ->
                if (yPos + rowHeight > pageHeight - 30) return@forEachIndexed

                if (index % 2 == 1) {
                    paint.color = Color.parseColor("#F9FBFE")
                    canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + rowHeight, paint)
                }

                paint.color = Color.parseColor("#2D3436")
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
                    RiskStatus.GREEN -> Color.parseColor("#00B894")
                    RiskStatus.YELLOW -> Color.parseColor("#E67E22")
                    RiskStatus.RED -> Color.parseColor("#D63031")
                }
                paint.color = statusColor
                canvas.drawText(statusText, 110f, yPos + 12f, paint)

                paint.color = Color.parseColor("#2D3436")
                canvas.drawText("${lp.accuracyPercentage.toInt()}%", 220f, yPos + 12f, paint)
                canvas.drawText("${lp.totalAttempts}", 310f, yPos + 12f, paint)
                canvas.drawText("${lp.heartsLost}", 400f, yPos + 12f, paint)
                canvas.drawText("${lp.starsEarned} ★", 490f, yPos + 12f, paint)

                yPos += rowHeight
            }

            // 6. Footer Note
            paint.color = Color.parseColor("#8395A7")
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
