package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lock
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
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.SkyShadow

@Composable
fun WordBlendingShelf(
    completedGroups: Int,
    totalGroups: Int = 7,
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
                        .background(Sky.copy(alpha = 0.2f))
                        .border(2.dp, Sky, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Extension,
                        contentDescription = "Word Blending",
                        tint = DarkBrownOutline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Word Construction ($completedGroups of $totalGroups)",
                        fontFamily = LexendFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Tactile syllable and word building progress",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Group Completion Pods (Drops Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..totalGroups).forEach { groupNum ->
                    val isDone = groupNum <= completedGroups
                    val isCurrent = groupNum == completedGroups + 1
                    val hasDepth = isDone || isCurrent

                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (hasDepth) {
                            // Shadow+face pair for done/current pods only. Locked pods stay flat
                            // and translucent below - not yet "active" gets no raised depth.
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .offset(y = 4.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) LeafShadow else SkyShadow)
                            )
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) Leaf else Sky)
                                    .border(2.dp, DarkBrownOutline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Group $groupNum: Completed",
                                        tint = Cloud,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$groupNum",
                                        fontFamily = LexendFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Ink,
                                        modifier = Modifier.semantics(mergeDescendants = true) {
                                            contentDescription = "Group $groupNum: in progress"
                                        }
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Sand.copy(alpha = 0.5f))
                                    .border(2.dp, DarkBrownOutline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Group $groupNum: Locked",
                                    tint = InkSoft,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
