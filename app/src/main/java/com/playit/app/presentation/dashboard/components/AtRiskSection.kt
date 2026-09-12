package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.playit.app.presentation.theme.*

@Composable
fun AtRiskSection(
    atRiskLetters: List<LetterPerformance>,
    modifier: Modifier = Modifier
) {
    if (atRiskLetters.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = ApricotGlowLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, ApricotGlow)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Practice Recommended (${atRiskLetters.size} Letters)",
                    fontFamily = LexendFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMidnight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "These sounds need a bit more practice. Extra time in Hear It / Say It will help strengthen mastery:",
                fontFamily = LexendFontFamily,
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

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
            .background(SurfaceCard, shape = Squircle12)
            .border(1.5.dp, ModernBorderSoft, Squircle12)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = symbol,
            fontFamily = LexendFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ApricotGlowDark
        )
        Text(
            text = "$accuracy%",
            fontFamily = LexendFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
    }
}
