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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.playit.app.presentation.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhonemeHeatmapSection(
    letterPerformances: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    var selectedLetter by remember { mutableStateOf<LetterPerformance?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Abc,
                    contentDescription = null,
                    tint = TextMidnight,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Phoneme Recognition Heatmap",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = LexendFontFamily,
                    color = TextMidnight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(label = "Mastered", color = EmeraldLeaf)
                LegendIndicator(label = "Practicing", color = SunnyGold)
                LegendIndicator(label = "Needs Help", color = CoralBerry)
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
                    shape = Squircle14,
                    color = CanvasLight,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ModernBorderSoft),
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
                            color = TextMidnight
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = SunnyGoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${lp.starsEarned} Stars (${lp.totalAttempts} attempts)",
                                fontFamily = LexendFontFamily,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
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
                .border(1.5.dp, ModernBorder, CircleShape)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.Bold,
            color = TextMuted
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
        RiskStatus.GREEN -> EmeraldLeaf to EmeraldLeafShadow
        RiskStatus.YELLOW -> SunnyGold to SunnyGoldShadow
        RiskStatus.RED -> CoralBerry to CoralBerryShadow
    }

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 48.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 44.dp)
                .offset(y = 3.dp)
                .clip(Squircle12)
                .background(shadowColor)
        )

        // Top Face
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 44.dp)
                .clip(Squircle12)
                .background(if (isSelected) faceColor.copy(alpha = 0.85f) else faceColor)
                .border(1.5.dp, ModernBorder, Squircle12),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letterPerformance.symbol.lowercase(),
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = if (letterPerformance.riskStatus == RiskStatus.YELLOW) TextMidnight else Color.White
            )
        }
    }
}
