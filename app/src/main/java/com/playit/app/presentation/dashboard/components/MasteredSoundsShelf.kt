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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Leaf.copy(alpha = 0.2f))
                        .border(2.dp, Leaf, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Mastered",
                        tint = Leaf,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Mastered Sounds (${masteredLetters.size})",
                        fontFamily = LexendFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Sounds successfully decoded & practiced",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (masteredLetters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Sand.copy(alpha = 0.4f))
                        .border(2.dp, Sand, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sounds mastered yet. Start with Letter 'M' on the map!",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
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
    // Layered face+shadow rather than routing through GummyContainer: a mastered badge is a
    // static achievement token, not a button, and GummyContainer's onClick would give it a false
    // tappable affordance (ripple, focus ring) it shouldn't have. Same depth ratio as the
    // ArithmeticGuardDialog keypad (~4dp reveal) for a consistent gummy feel.
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
                .clip(RoundedCornerShape(14.dp))
                .background(LeafShadow)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Leaf)
                .border(2.5.dp, DarkBrownOutline, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.symbol,
                fontFamily = LexendFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Cloud
            )
        }
    }
}
