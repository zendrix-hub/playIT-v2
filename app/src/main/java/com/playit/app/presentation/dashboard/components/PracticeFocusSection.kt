package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.presentation.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeFocusSection(
    atRiskLetters: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ApricotGlowLight)
                        .border(2.dp, ApricotGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = "Practice Focus",
                        tint = ApricotGlowDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Practice & Focus Sounds",
                        fontFamily = LexendFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )
                    Text(
                        text = "Pedagogical recommendations for today",
                        fontFamily = LexendFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (atRiskLetters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Squircle16)
                        .background(EmeraldLeafLight)
                        .border(1.5.dp, EmeraldLeaf, Squircle16)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Great job! All practiced sounds meet mastery criteria. Keep exploring the learning path!",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldLeafDark
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    atRiskLetters.take(6).forEach { letter ->
                        Box(
                            modifier = Modifier
                                .clip(Squircle12)
                                .background(ApricotGlowLight)
                                .border(1.5.dp, ApricotGlow, Squircle12)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = letter.symbol,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextMidnight
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "${letter.accuracyPercentage.toInt()}%",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApricotGlowDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actionable advice card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Squircle14)
                        .background(CanvasLight)
                        .border(1.dp, ModernBorderSoft, Squircle14)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Tip: Spend 2-3 minutes practicing short pronunciation in 'Hear It' and 'Say It' to boost sound recognition.",
                        fontFamily = LexendFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
