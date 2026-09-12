package com.playit.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyBackButton
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.dashboard.components.BadgeCollectionCase
import com.playit.app.presentation.dashboard.components.LearnerHeroCard
import com.playit.app.presentation.dashboard.components.MasteredSoundsShelf
import com.playit.app.presentation.dashboard.components.PracticeFocusSection
import com.playit.app.presentation.dashboard.components.ProfileSwitcherDropdown
import com.playit.app.presentation.dashboard.components.WordBlendingShelf
import androidx.compose.ui.graphics.Color
import com.playit.app.presentation.theme.*
import java.io.File

@Composable
fun ParentDashboardScreen(
    viewModel: ParentDashboardViewModel,
    onBack: () -> Unit,
    onReportPreview: (File) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.exportStatus) {
        when (val status = uiState.exportStatus) {
            is ExportStatus.Success -> {
                onReportPreview(status.file)
                viewModel.resetExportStatus()
            }
            is ExportStatus.Error -> {
                snackbarHostState.showSnackbar("Error: ${status.message}")
                viewModel.resetExportStatus()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CanvasSoft,
                        Color(0xFFF1F5F9),
                        CanvasLight
                    )
                )
            )
    ) {
        // Soft rolling playground hills silhouette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hillWidths = listOf(70.dp, 100.dp, 85.dp, 115.dp, 80.dp, 95.dp)
            hillWidths.forEachIndexed { index, width ->
                Box(
                    modifier = Modifier
                        .size(width = width, height = width * 0.55f)
                        .offset(x = if (index == 0) 0.dp else ((-14) * index).dp)
                        .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
                        .background(
                            if (index % 2 == 0) EmeraldLeaf.copy(alpha = 0.12f) else EmeraldLeafDark.copy(alpha = 0.18f)
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Header Row: Back button, Title & PDF Export CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GummyBackButton(onClick = onBack)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Learner Insights",
                        fontFamily = LexendFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )
                    Text(
                        text = "Progress & Phonics Mastery",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }

                GummyButton(
                    text = if (uiState.exportStatus is ExportStatus.Exporting) "Exporting" else "PDF",
                    icon = Icons.Filled.PictureAsPdf,
                    onClick = { viewModel.exportPdfReport() },
                    backgroundColor = PrimaryJoy,
                    shadowColor = PrimaryJoyShadow,
                    contentColor = Color.White,
                    enabled = uiState.exportStatus !is ExportStatus.Exporting && uiState.selectedProfile != null,
                    fontSize = 13,
                    modifier = Modifier.height(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Profile Switcher Dropdown
            ProfileSwitcherDropdown(
                profiles = uiState.profiles,
                selectedProfile = uiState.selectedProfile,
                onProfileSelect = { profile -> viewModel.selectProfile(profile) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryJoy)
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
                        item { LearnerHeroCard(data = dashboardData) }
                        item { MasteredSoundsShelf(letterPerformances = dashboardData.letterPerformances) }
                        item { PracticeFocusSection(atRiskLetters = dashboardData.atRiskLetters) }
                        item {
                            WordBlendingShelf(
                                completedGroups = dashboardData.blendItCompletedCount,
                                totalGroups = dashboardData.blendItTotalCount
                            )
                        }
                        item { BadgeCollectionCase(completedLettersCount = dashboardData.completedLettersCount) }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No profile data available.",
                            fontFamily = LexendFontFamily,
                            fontSize = 16.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
