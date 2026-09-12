package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.RiskStatus
import androidx.compose.ui.graphics.Color
import com.playit.app.presentation.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MasteredSoundsShelf(
    letterPerformances: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    val masteredLetters = letterPerformances.filter { it.isCompleted || it.riskStatus == RiskStatus.GREEN }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldLeafLight)
                        .border(2.dp, EmeraldLeaf, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Mastered",
                        tint = EmeraldLeaf,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Mastered Sounds (${masteredLetters.size})",
                        fontFamily = LexendFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )
                    Text(
                        text = "Sounds successfully decoded & practiced",
                        fontFamily = LexendFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (masteredLetters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Squircle16)
                        .background(CanvasLight)
                        .border(1.5.dp, ModernBorderSoft, Squircle16)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sounds mastered yet. Start with Letter 'M' on the map!",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    masteredLetters.forEach { letter ->
                        GummyMasteredBadge(letter = letter)
                    }
                }
            }
        }
    }
}

@Composable
private fun GummyMasteredBadge(
    letter: LetterPerformance,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .semantics(mergeDescendants = true) { contentDescription = "Mastered: ${letter.symbol}" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .offset(y = 4.dp)
                .clip(Squircle14)
                .background(EmeraldLeafShadow)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(Squircle14)
                .background(EmeraldLeaf)
                .border(2.dp, ModernBorder, Squircle14),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.symbol,
                fontFamily = LexendFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
