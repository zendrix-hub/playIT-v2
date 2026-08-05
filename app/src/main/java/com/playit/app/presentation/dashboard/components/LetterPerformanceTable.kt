package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.RiskStatus
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DestructiveRed
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun LetterPerformanceTable(
    letterPerformances: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🔤 Phoneme Mastery Matrix (${letterPerformances.size} Letters)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEDF2F7), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Letter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(text = "Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.5f))
                Text(text = "Accuracy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1.2f))
                Text(text = "Attempts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(text = "Stars", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rows
            letterPerformances.forEachIndexed { index, lp ->
                TableRow(lp = lp)
                if (index < letterPerformances.size - 1) {
                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun TableRow(lp: LetterPerformance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = lp.symbol,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        StatusBadge(status = lp.riskStatus, modifier = Modifier.weight(1.5f))

        Text(
            text = "${lp.accuracyPercentage.toInt()}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1.2f)
        )

        Text(
            text = "${lp.totalAttempts}",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "⭐ ${lp.starsEarned}",
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun StatusBadge(status: RiskStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        RiskStatus.GREEN -> Triple(Color(0xFFE6F4EA), GrowthGreen, "Mastered")
        RiskStatus.YELLOW -> Triple(Color(0xFFFEF3D6), Color(0xFFD69E2E), "Developing")
        RiskStatus.RED -> Triple(Color(0xFFFFECEC), DestructiveRed, "At-Risk")
    }

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
