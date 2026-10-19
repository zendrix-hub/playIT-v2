package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyTextField
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.*

@Composable
fun LearnerHeroCard(
    data: ProfileDashboardData,
    onRename: (String) -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    val masteryFraction = if (data.totalLettersCount > 0) {
        (data.completedLettersCount.toFloat() / data.totalLettersCount.toFloat()).coerceIn(0f, 1f)
    } else 0f

    if (showRenameDialog) {
        Dialog(onDismissRequest = { showRenameDialog = false }) {
            Surface(
                shape = DialogShape,
                color = SurfaceCard,
                border = BorderStroke(2.5.dp, ModernBorder),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rename Learner",
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GummyTextField(
                        value = newName,
                        onValueChange = { input ->
                            val filtered = input.filter {
                                it.isLetter() || it.isWhitespace() || it == '-' || it == '\''
                            }
                            if (filtered.length <= 16) {
                                newName = filtered
                            }
                        },
                        label = "Learner's Name",
                        placeholder = "Enter new name...",
                        maxLength = 16,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showRenameDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = LexendFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }

                        GummyButton(
                            text = "Save",
                            backgroundColor = EmeraldLeaf,
                            shadowColor = EmeraldLeafShadow,
                            contentColor = Color.White,
                            enabled = newName.trim().isNotBlank() && newName.trim().length <= 16,
                            onClick = {
                                val trimmed = newName.trim()
                                if (trimmed.isNotBlank() && trimmed.length <= 16) {
                                    onRename(trimmed)
                                    showRenameDialog = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Surface(
                shape = DialogShape,
                color = SurfaceCard,
                border = BorderStroke(2.5.dp, ModernBorder),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete ${data.profile.name}'s data?",
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This removes this child's profile, stars and progress from this device. It cannot be undone.",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = LexendFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }

                        GummyButton(
                            text = "Delete",
                            backgroundColor = GentleCorrectionOrange,
                            shadowColor = GentleCorrectionOrangeShadow,
                            contentColor = Color.White,
                            onClick = {
                                showDeleteDialog = false
                                onDelete()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        )
                    }
                }
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Row 1: Avatar, Name, Streak & Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(
                    avatarId = data.profile.avatarResId,
                    size = 64
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.profile.name,
                            fontFamily = LexendFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMidnight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                newName = data.profile.name
                                showRenameDialog = true
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit name",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Marungko Phonics Explorer",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.height(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Delete this child's data",
                            fontFamily = LexendFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }

                // Streak Pill
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(SunnyGoldLight)
                        .border(2.dp, SunnyGold, PillShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Streak: ${data.profile.currentStreak}d",
                            fontFamily = LexendFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SunnyGoldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row 2: Mastery Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curriculum Mastery",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMidnight
                    )
                    Text(
                        text = "${data.completedLettersCount} of ${data.totalLettersCount} Sounds (${(masteryFraction * 100).toInt()}%)",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryJoy
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { masteryFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(PillShape),
                    color = EmeraldLeaf,
                    trackColor = ModernBorderFaint
                )
            }
        }
    }
}
