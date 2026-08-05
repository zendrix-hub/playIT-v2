package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.presentation.theme.DestructiveRed

@Composable
fun AtRiskSection(
    atRiskLetters: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    if (atRiskLetters.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚠️ At-Risk Phonemes (${atRiskLetters.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DestructiveRed
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The child is encountering difficulty with these target sounds. Extra practice in Hear It / Say It is recommended:",
                fontSize = 12.sp,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                atRiskLetters.forEach { lp ->
                    BoxBadge(symbol = lp.symbol, accuracy = lp.accuracyPercentage.toInt())
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BoxBadge(symbol: String, accuracy: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = symbol, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DestructiveRed)
        Text(text = "$accuracy%", fontSize = 10.sp, color = Color.Gray)
    }
}
