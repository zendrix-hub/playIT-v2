package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.domain.calculator.PracticeNext
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyTextField
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.CardShape
import com.playit.app.presentation.theme.DialogShape
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.EmeraldLeafShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GentleCorrectionOrangeShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.ModernBorderFaint
import com.playit.app.presentation.theme.PillShape
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldLight
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

/**
 * The child's summary, the first thing in the Parent Zone (user decisions 2026-11-07): how many sounds, one plain
 * word for how it is going, what to practice next, and the days in a row. A parent reads it in a few seconds; the
 * letters are in the grid below, and the teacher's grades only in the PDF. Renaming, and deleting the child's data,
 * are behind the pencil by the name.
 */
@Composable
fun LearnerHeroCard(
    data: ProfileDashboardData,
    onRename: (String) -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    if (showEdit) {
        EditChildDialog(
            name = data.profile.name,
            onDismiss = { showEdit = false },
            onSave = { onRename(it); showEdit = false },
            onDeleteTap = { showEdit = false; showDelete = true }
        )
    }
    if (showDelete) {
        DeleteChildDialog(name = data.profile.name, onDismiss = { showDelete = false }, onDelete = { showDelete = false; onDelete() })
    }

    val fraction = if (data.totalLettersCount > 0) {
        (data.completedLettersCount.toFloat() / data.totalLettersCount).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = modifier.fillMaxWidth().testTag("child_summary"),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(avatarId = data.profile.avatarResId, size = 48)
                Spacer(modifier = Modifier.width(12.dp))
                // The name has the row; the days in a row go under it, so a small phone never cuts the name.
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = data.profile.name, fontFamily = LexendFontFamily, fontSize = 21.sp, lineHeight = 25.sp,
                            fontWeight = FontWeight.ExtraBold, color = TextMidnight, maxLines = 1,
                            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false)
                        )
                        IconButton(onClick = { showEdit = true }, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Change the name, or delete this child's data",
                                tint = TextMuted, modifier = Modifier.size(22.dp))
                        }
                    }
                    if (data.profile.currentStreak > 1) {
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(SunnyGoldLight)
                                .border(2.dp, SunnyGold, PillShape)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${data.profile.currentStreak} days in a row", fontFamily = LexendFontFamily,
                                fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextMidnight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = learnedText(data), fontFamily = LexendFontFamily, fontSize = 17.sp, lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold, color = PrimaryJoy, modifier = Modifier.weight(1f)
                )
                LevelChip(data.overallLevel)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(PillShape),
                color = EmeraldLeaf,
                trackColor = ModernBorderFaint
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = practiceNextText(data), fontFamily = LexendFontFamily, fontSize = 16.sp, lineHeight = 22.sp,
                color = TextMidnight, modifier = Modifier.testTag("practice_next")
            )
        }
    }
}

/** "Practice next: b (Ball), k (Kite)", from the same rule as the PDF's "Needs help with". */
private fun practiceNextText(data: ProfileDashboardData) = buildAnnotatedString {
    fun bold(s: String) = withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append(s) }
    when (val pick = PracticeNext.of(data.chapters)) {
        is PracticeNext.Pick.NeedsHelp -> { bold("Practice next: "); append(PracticeNext.names(pick.letters)) }
        is PracticeNext.Pick.PlayingNow -> { bold("Playing now: "); append(PracticeNext.names(listOf(pick.letter))) }
        PracticeNext.Pick.AllGood -> append(
            if (data.completedLettersCount > 0) "Keep playing a few minutes each day." else "Your child has not played yet."
        )
    }
}

@Composable
private fun DialogFrame(content: @Composable () -> Unit) {
    Surface(
        shape = DialogShape,
        color = SurfaceCard,
        border = BorderStroke(2.5.dp, ModernBorder),
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { content() }
    }
}

@Composable
private fun EditChildDialog(name: String, onDismiss: () -> Unit, onSave: (String) -> Unit, onDeleteTap: () -> Unit) {
    var newName by remember { mutableStateOf(name) }
    Dialog(onDismissRequest = onDismiss) {
        DialogFrame {
            Text("Change the name", fontFamily = LexendFontFamily, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                color = TextMidnight)
            Spacer(modifier = Modifier.height(16.dp))
            GummyTextField(
                value = newName,
                onValueChange = { input ->
                    val filtered = input.filter { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' }
                    if (filtered.length <= 16) newName = filtered
                },
                label = "Child's name",
                placeholder = "Type a name",
                maxLength = 16,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp)) {
                    Text("Cancel", fontFamily = LexendFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
                GummyButton(
                    text = "Save",
                    backgroundColor = EmeraldLeaf,
                    shadowColor = EmeraldLeafShadow,
                    contentColor = Color.White,
                    enabled = newName.trim().isNotBlank(),
                    onClick = { newName.trim().takeIf { it.isNotBlank() && it.length <= 16 }?.let(onSave) },
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDeleteTap, modifier = Modifier.height(48.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("Delete this child's data", fontFamily = LexendFontFamily, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, color = TextMuted)
            }
        }
    }
}

@Composable
private fun DeleteChildDialog(name: String, onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        DialogFrame {
            Text("Delete $name's data?", fontFamily = LexendFontFamily, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                color = TextMidnight, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "This removes this child's profile, stars and progress from this device. It cannot be undone.",
                fontFamily = LexendFontFamily, fontSize = 16.sp, lineHeight = 21.sp, color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp)) {
                    Text("Cancel", fontFamily = LexendFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
                GummyButton(
                    text = "Delete",
                    backgroundColor = GentleCorrectionOrange,
                    shadowColor = GentleCorrectionOrangeShadow,
                    contentColor = Color.White,
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
        }
    }
}
