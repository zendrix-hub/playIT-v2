package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.playit.app.presentation.theme.KalamansiShadow
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sky

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhonemeHeatmapSection(
    letterPerformances: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    var selectedLetter by remember { mutableStateOf<LetterPerformance?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔤 Phoneme Recognition Heatmap",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = LexendFontFamily,
                    color = Ink
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(label = "Mastered", color = Leaf)
                LegendIndicator(label = "Practicing", color = Mango)
                LegendIndicator(label = "Needs Help", color = Kalamansi)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FlowRow of Gummy Heatmap Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                letterPerformances.forEach { lp ->
                    HeatmapChip(
                        letterPerformance = lp,
                        isSelected = selectedLetter?.symbol == lp.symbol,
                        onClick = {
                            selectedLetter = if (selectedLetter?.symbol == lp.symbol) null else lp
                        }
                    )
                }
            }

            // Selected Letter Detail Pill
            selectedLetter?.let { lp ->
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Sky,
                    border = androidx.compose.foundation.BorderStroke(2.dp, DarkBrownOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Letter \"${lp.symbol}\": ${(lp.accuracyPercentage * 100).toInt()}% Accuracy",
                            fontFamily = LexendFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Ink
                        )
                        Text(
                            text = "⭐ ${lp.starsEarned} Stars (${lp.totalAttempts} attempts)",
                            fontFamily = LexendFontFamily,
                            fontSize = 13.sp,
                            color = InkSoft
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendIndicator(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, DarkBrownOutline, CircleShape)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.Bold,
            color = InkSoft
        )
    }
}

@Composable
private fun HeatmapChip(
    letterPerformance: LetterPerformance,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (faceColor, shadowColor) = when (letterPerformance.riskStatus) {
        RiskStatus.GREEN -> Leaf to LeafShadow
        RiskStatus.YELLOW -> Mango to MangoShadow
        RiskStatus.RED -> Kalamansi to KalamansiShadow
    }

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 48.dp)
            .clickable(onClick = onClick)
    ) {
        // Gummy Bottom Depth Shadow (3dp)
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 44.dp)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(shadowColor)
                .border(2.dp, DarkBrownOutline, RoundedCornerShape(12.dp))
        )

        // Top Face
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) faceColor.copy(alpha = 0.85f) else faceColor)
                .border(2.dp, DarkBrownOutline, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letterPerformance.symbol.lowercase(),
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = if (letterPerformance.riskStatus == RiskStatus.YELLOW) Ink else Cloud
            )
        }
    }
}
