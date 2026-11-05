package com.playit.app.data.pdf

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.playit.app.domain.model.ReportData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Writes the progress report as a PDF in the form of a DepEd class record (adviser, 2026-11-05): a class page for
 * every child on this phone, then each child's record letter by letter, on landscape A4 pages. Offline, with
 * Android's own PdfDocument; the pages come from [ReportLayout] and are drawn by [ReportPageRenderer].
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §6 FR-12.
 */
@Singleton
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportReport(reportData: ReportData): Result<File> = try {
        val pages = ReportLayout.pages(reportData)
        val renderer = ReportPageRenderer()
        val document = PdfDocument()
        try {
            pages.forEachIndexed { index, page ->
                val info = PdfDocument.PageInfo.Builder(ReportLayout.PAGE_WIDTH, ReportLayout.PAGE_HEIGHT, index + 1).create()
                val pdfPage = document.startPage(info)
                renderer.draw(pdfPage.canvas, page, reportData, index + 1, pages.size)
                document.finishPage(pdfPage)
            }
            val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            if (!docsDir.exists()) docsDir.mkdirs()
            val who = reportData.learners.singleOrNull()?.profile?.name?.replace(Regex("[^a-zA-Z0-9]"), "_") ?: "Class"
            val file = File(docsDir, "PlayIT_Progress_Record_${who}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { document.writeTo(it) }
            Result.success(file)
        } finally {
            document.close()
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
