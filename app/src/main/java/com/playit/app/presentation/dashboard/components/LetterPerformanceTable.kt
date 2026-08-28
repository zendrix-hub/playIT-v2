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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.Sky

@Composable
fun LetterPerformanceTable(
    letterPerformances: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Phoneme Mastery Matrix (${letterPerformances.size} Letters)",
                fontFamily = LexendFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Sky, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Letter", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.weight(1f))
                Text(text = "Status", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.weight(1.5f))
                Text(text = "Accuracy", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.weight(1.2f))
                Text(text = "Attempts", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.weight(1f))
                Text(text = "Stars", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rows
            letterPerformances.forEachIndexed { index, lp ->
                TableRow(lp = lp)
                if (index < letterPerformances.size - 1) {
                    HorizontalDivider(color = DarkBrownOutline.copy(alpha = 0.15f), thickness = 0.5.dp)
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
            fontFamily = LexendFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Ink,
            modifier = Modifier.weight(1f)
        )

        StatusBadge(status = lp.riskStatus, modifier = Modifier.weight(1.5f))

        Text(
            text = "${lp.accuracyPercentage.toInt()}%",
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Ink,
            modifier = Modifier.weight(1.2f)
        )

        Text(
            text = "${lp.totalAttempts}",
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            color = InkSoft,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${lp.starsEarned}",
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            color = Ink,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
private fun StatusBadge(status: RiskStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        RiskStatus.GREEN -> Triple(Color(0xFFEAF7EE), Leaf, "Mastered")
        RiskStatus.YELLOW -> Triple(Mango.copy(alpha = 0.2f), Mango, "Developing")
        RiskStatus.RED -> Triple(Kalamansi.copy(alpha = 0.2f), Kalamansi, "At-Risk")
    }

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
