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
            val canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            // Header Background (Purple)
            paint.color = Color.parseColor("#6C5CE7")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 100f, paint)

            // Header Text
            paint.color = Color.WHITE
            paint.textSize = 24f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("playIT — Progress & Retention Report", 24f, 45f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Student Profile: ${reportData.profileName}  |  Generated: ${reportData.generatedDate}", 24f, 75f, paint)

            var yPos = 130f

            // Overview Section Card
            paint.color = Color.parseColor("#F4F6F9")
            val overviewRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + 100f)
            canvas.drawRoundRect(overviewRect, 12f, 12f, paint)

            paint.color = Color.parseColor("#2D3436")
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Executive Summary", 40f, yPos + 28f, paint)

            paint.textSize = 11f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Overall Accuracy: ${reportData.overallAccuracyPercentage}%", 40f, yPos + 52f, paint)
            canvas.drawText("7-Day Retention: ${reportData.retentionScorePercentage}%", 40f, yPos + 72f, paint)
            canvas.drawText("Letters Completed: ${reportData.completedLettersCount} / ${reportData.totalLettersCount}", 300f, yPos + 52f, paint)
            canvas.drawText("Total Stars: ${reportData.totalStars}  |  Blend-It: ${reportData.blendItCompletedCount}/${reportData.blendItTotalCount}", 300f, yPos + 72f, paint)

            yPos += 120f

            // At Risk Warning Section (if any)
            if (reportData.atRiskLetters.isNotEmpty()) {
                paint.color = Color.parseColor("#FFECEC")
                val atRiskRect = RectF(24f, yPos, (pageWidth - 24).toFloat(), yPos + 60f)
                canvas.drawRoundRect(atRiskRect, 8f, 8f, paint)

                paint.color = Color.parseColor("#D63031")
                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("⚠️ At-Risk Letters Needing Attention (${reportData.atRiskLetters.size}):", 40f, yPos + 25f, paint)

                paint.typeface = Typeface.DEFAULT
                val symbols = reportData.atRiskLetters.joinToString(", ") { "${it.symbol} (${it.accuracyPercentage.toInt()}%)" }
                canvas.drawText("Letters: $symbols", 40f, yPos + 45f, paint)

                yPos += 75f
            }

            // Letter Matrix Table Header
            paint.color = Color.parseColor("#00CEC9")
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Detailed Letter Performance Matrix", 24f, yPos, paint)

            yPos += 15f
            paint.color = Color.parseColor("#DFE6E9")
            canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 22f, paint)

            paint.color = Color.parseColor("#2D3436")
            paint.textSize = 10f
            canvas.drawText("Letter", 34f, yPos + 15f, paint)
            canvas.drawText("Status", 100f, yPos + 15f, paint)
            canvas.drawText("Accuracy", 210f, yPos + 15f, paint)
            canvas.drawText("Attempts", 300f, yPos + 15f, paint)
            canvas.drawText("Hearts Lost", 400f, yPos + 15f, paint)
            canvas.drawText("Stars", 490f, yPos + 15f, paint)

            yPos += 22f

            // Table Rows
            paint.typeface = Typeface.DEFAULT
            reportData.letterPerformances.take(28).forEachIndexed { index, lp ->
                if (yPos > pageHeight - 40) return@forEachIndexed

                if (index % 2 == 1) {
                    paint.color = Color.parseColor("#F9FAFC")
                    canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 20f, paint)
                }

                paint.color = Color.parseColor("#2D3436")
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(lp.symbol, 34f, yPos + 14f, paint)

                paint.typeface = Typeface.DEFAULT
                val statusText = when (lp.riskStatus) {
                    RiskStatus.GREEN -> "Mastered"
                    RiskStatus.YELLOW -> "Developing"
                    RiskStatus.RED -> "At-Risk"
                }
                val statusColor = when (lp.riskStatus) {
                    RiskStatus.GREEN -> Color.parseColor("#00B894")
                    RiskStatus.YELLOW -> Color.parseColor("#FDCB6E")
                    RiskStatus.RED -> Color.parseColor("#D63031")
                }
                paint.color = statusColor
                canvas.drawText(statusText, 100f, yPos + 14f, paint)

                paint.color = Color.parseColor("#2D3436")
                canvas.drawText("${lp.accuracyPercentage.toInt()}%", 210f, yPos + 14f, paint)
                canvas.drawText("${lp.totalAttempts}", 300f, yPos + 14f, paint)
                canvas.drawText("${lp.heartsLost}", 400f, yPos + 14f, paint)
                canvas.drawText("${lp.starsEarned} ★", 490f, yPos + 14f, paint)

                yPos += 20f
            }

            pdfDocument.finishPage(page)

            // Save PDF to documents/files directory
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
