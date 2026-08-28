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
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sky

@Composable
fun BlendItSummaryCard(
    completedGroups: Int,
    totalGroups: Int = 7,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Blend-It Word Construction Progress",
                    fontFamily = LexendFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Completed $completedGroups of $totalGroups phoneme group challenges",
                fontFamily = LexendFontFamily,
                fontSize = 14.sp,
                color = InkSoft
            )

            Spacer(modifier = Modifier.height(14.dp))

            val progress = if (totalGroups > 0) completedGroups.toFloat() / totalGroups.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                color = Leaf,
                trackColor = Sky,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.5.dp, DarkBrownOutline, RoundedCornerShape(6.dp))
            )
        }
    }
}
