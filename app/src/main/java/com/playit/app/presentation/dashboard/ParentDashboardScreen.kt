package com.playit.app.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.dashboard.components.AtRiskSection
import com.playit.app.presentation.dashboard.components.BlendItSummaryCard
import com.playit.app.presentation.dashboard.components.LetterPerformanceTable
import com.playit.app.presentation.dashboard.components.OverallStatsCard
import com.playit.app.presentation.dashboard.components.ProfileSwitcherDropdown
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary
import java.io.File

@Composable
fun ParentDashboardScreen(
    viewModel: ParentDashboardViewModel,
    onBack: () -> Unit,
    onReportPreview: (File) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.exportStatus) {
        when (val status = uiState.exportStatus) {
            is ExportStatus.Success -> {
                Toast.makeText(context, "PDF Report generated successfully!", Toast.LENGTH_SHORT).show()
                onReportPreview(status.file)
                viewModel.resetExportStatus()
            }
            is ExportStatus.Error -> {
                Toast.makeText(context, "Error: ${status.message}", Toast.LENGTH_LONG).show()
                viewModel.resetExportStatus()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftSky)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }

                Text(
                    text = "Parent Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Button(
                    onClick = { viewModel.exportPdfReport() },
                    colors = ButtonDefaults.buttonColors(containerColor = FriendlyPurple),
                    enabled = uiState.exportStatus !is ExportStatus.Exporting && uiState.selectedProfile != null
                ) {
                    if (uiState.exportStatus is ExportStatus.Exporting) {
                        CircularProgressIndicator(
                            color = CreamWhite,
                            modifier = Modifier.height(18.dp)
                        )
                    } else {
                        Text("📄 Export PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CreamWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Switcher Dropdown
            ProfileSwitcherDropdown(
                profiles = uiState.profiles,
                selectedProfile = uiState.selectedProfile,
                onProfileSelect = { profile -> viewModel.selectProfile(profile) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FriendlyPurple)
                }
            } else {
                val dashboardData = uiState.dashboardData
                if (dashboardData != null) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            OverallStatsCard(data = dashboardData)
                        }

                        item {
                            AtRiskSection(atRiskLetters = dashboardData.atRiskLetters)
                        }

                        item {
                            BlendItSummaryCard(completedGroups = dashboardData.blendItCompletedCount)
                        }

                        item {
                            LetterPerformanceTable(letterPerformances = dashboardData.letterPerformances)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No profile data available.",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
