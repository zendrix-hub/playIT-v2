package com.playit.app.domain.model

/** The PDF report: every learner on this phone, as a class record and one record per learner. */
data class ReportData(
    val generatedDate: String,
    val learners: List<ProfileDashboardData>
)
