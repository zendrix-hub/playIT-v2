package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.*

@Composable
fun BlendItSummaryCard(
    completedGroups: Int,
    totalGroups: Int = 7,
    modifier: Modifier = Modifier
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Blend-It Word Construction Progress",
                    fontFamily = LexendFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMidnight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completed $completedGroups of $totalGroups phoneme group challenges",
                fontFamily = LexendFontFamily,
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            val progress = if (totalGroups > 0) completedGroups.toFloat() / totalGroups.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                color = EmeraldLeaf,
                trackColor = ModernBorderFaint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(PillShape)
            )
        }
    }
}
