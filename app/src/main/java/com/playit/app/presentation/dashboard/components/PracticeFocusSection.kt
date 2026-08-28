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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        // Kalamansi, not Mango: this section IS the non-punitive framing
                        // objective, and Mango is reserved for primary-action, not retry/practice.
                        .background(Kalamansi.copy(alpha = 0.2f))
                        .border(2.dp, Kalamansi, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = "Practice Focus",
                        tint = DarkBrownOutline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Practice & Focus Sounds",
                        fontFamily = LexendFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Pedagogical recommendations for today",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (atRiskLetters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Leaf.copy(alpha = 0.1f))
                        .border(2.dp, Leaf, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Great job! All practiced sounds meet mastery criteria. Keep exploring the learning path!",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Ink
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(Kalamansi.copy(alpha = 0.15f))
                                .border(2.dp, Kalamansi, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = letter.symbol,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Ink
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "${letter.accuracyPercentage.toInt()}%",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrownOutline
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actionable advice card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Sand.copy(alpha = 0.35f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Tip: Spend 2-3 minutes practicing short pronunciation in 'Hear It' and 'Say It' to boost sound recognition.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = InkSoft
                    )
                }
            }
        }
    }
}
