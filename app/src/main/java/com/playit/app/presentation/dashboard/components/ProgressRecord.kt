package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.CardShape
import com.playit.app.presentation.theme.LevelColors
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.PillShape
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldLight
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

/*
 * The learner's record in the words of a DepEd report card (adviser, 2026-11-05: parents and teachers should
 * "understand the progress by just looking at it"): a level in words and colour on every row, a one-line remark, and
 * no bare percentages.
 */

/** A level in a parent's words, on its colour (the DepEd word is in the legend and on the PDF). */
@Composable
fun LevelChip(level: ProgressLevel, modifier: Modifier = Modifier, small: Boolean = false) {
    val c = LevelColors.of(level)
    Box(
        modifier = modifier
            .widthIn(max = if (small) 132.dp else 220.dp)
            .clip(PillShape)
            .background(Color(c.fill))
            .padding(horizontal = if (small) 9.dp else 12.dp, vertical = if (small) 3.dp else 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.parentLabel,
            fontFamily = LexendFontFamily,
            fontSize = if (small) 12.sp else 14.sp,
            lineHeight = if (small) 14.sp else 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(c.ink),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun RecordCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun CardTitle(title: String, subtitle: String? = null) {
    Text(
        title, fontFamily = LexendFontFamily, fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.ExtraBold,
        color = TextMidnight
    )
    if (subtitle != null) {
        Text(subtitle, fontFamily = LexendFontFamily, fontSize = 13.sp, color = TextMuted, lineHeight = 17.sp)
    }
    Spacer(modifier = Modifier.height(10.dp))
}

/** "Learned 5 of 26 sounds". */
fun learnedText(data: ProfileDashboardData) =
    "Learned ${data.completedLettersCount} of ${data.totalLettersCount} sounds"

/** Every child on this phone, one row each, with a coloured cell per chapter. Tapping a row shows that child. */
@Composable
fun ClassRecordCard(
    learners: List<ProfileDashboardData>,
    selectedId: Long?,
    onSelect: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordCard(modifier) {
        CardTitle("All the children", "Every child who plays on this phone. Tap a name to see how they are doing.")
        learners.forEach { learner ->
            val selected = learner.profile.id == selectedId
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) SunnyGoldLight else Color.Transparent)
                    .border(2.dp, if (selected) SunnyGold else ModernBorderSoft, RoundedCornerShape(16.dp))
                    .clickable { onSelect(learner.profile) }
                    .heightIn(min = 64.dp)
                    .padding(10.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${learner.profile.name}: ${learner.overallLevel.parentLabel}. ${learnedText(learner)}."
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle(avatarId = learner.profile.avatarResId, size = 40)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            learner.profile.name, fontFamily = LexendFontFamily, fontSize = 16.sp, lineHeight = 20.sp,
                            fontWeight = FontWeight.ExtraBold, color = TextMidnight, maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${learner.completedLettersCount} of ${learner.totalLettersCount} sounds",
                            fontFamily = LexendFontFamily, fontSize = 13.sp, color = TextMuted, lineHeight = 17.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    LevelChip(learner.overallLevel, small = true)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    learner.chapters.forEach { ChapterCell(it, Modifier.weight(1f)) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            "Each box is a chapter. Its colour shows how the child is doing; the colours are explained below.",
            fontFamily = LexendFontFamily, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp
        )
    }
}

@Composable
private fun ChapterCell(chapter: ChapterRecord, modifier: Modifier = Modifier) {
    val c = LevelColors.of(chapter.level)
    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(c.fill))
            .border(1.dp, Color(c.ink).copy(alpha = 0.35f), RoundedCornerShape(7.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${chapter.chapter}", fontFamily = LexendFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = Color(c.ink)
        )
    }
}

/** One chapter: its letters, each with a level and a remark, then its word game. */
@Composable
fun ChapterRecordCard(chapter: ChapterRecord, modifier: Modifier = Modifier) {
    RecordCard(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Chapter ${chapter.chapter}", fontFamily = LexendFontFamily, fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold, color = TextMidnight
                )
                Text(
                    "Sounds ${chapter.letterList}. ${chapter.lettersDone} of ${chapter.lettersTotal} finished.",
                    fontFamily = LexendFontFamily, fontSize = 13.sp, color = TextMuted, lineHeight = 17.sp
                )
            }
            LevelChip(chapter.level, small = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        chapter.letters.forEach { LetterRow(it) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp).semantics(mergeDescendants = true) {}
        ) {
            Icon(Icons.Rounded.Extension, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Word game: " + if (chapter.blendItCompleted) {
                    "done, ${chapter.blendItStars} of 3 stars"
                } else "not yet",
                fontFamily = LexendFontFamily, fontSize = 13.sp, color = TextMuted
            )
        }
    }
}

@Composable
private fun LetterRow(letter: LetterPerformance) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Letter ${letter.symbol}, ${letter.keyWord}: ${letter.level.parentLabel}. ${letter.level.remark}"
            }
    ) {
        LetterTile(letter.symbol)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                letter.keyWord, fontFamily = LexendFontFamily, fontSize = 15.sp, lineHeight = 19.sp,
                fontWeight = FontWeight.Bold, color = TextMidnight
            )
            Text(
                letter.level.remark, fontFamily = LexendFontFamily, fontSize = 12.5.sp, color = TextMuted,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        LevelChip(letter.level, small = true)
    }
}

@Composable
private fun LetterTile(symbol: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SunnyGoldLight)
            .border(2.dp, ModernBorder, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol.uppercase() + symbol.lowercase(), fontFamily = LexendFontFamily, fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold, color = TextMidnight
        )
    }
}

/** The letters finished at the two lowest levels, with a simple thing to do at home. */
@Composable
fun PracticeAtHomeCard(data: ProfileDashboardData, modifier: Modifier = Modifier) {
    RecordCard(modifier) {
        CardTitle("Practise at home")
        val letters = data.needsPractice
        when {
            letters.isNotEmpty() -> letters.forEach { letter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp).semantics(mergeDescendants = true) {}
                ) {
                    LetterTile(letter.symbol)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${letter.keyWord}: ${letter.level.parentLabel}", fontFamily = LexendFontFamily,
                            fontSize = 15.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold, color = TextMidnight
                        )
                        Text(
                            "Say the sound together, then find things at home that start with it. Then play this " +
                                "letter again.",
                            fontFamily = LexendFontFamily, fontSize = 12.5.sp, color = TextMuted, lineHeight = 16.sp
                        )
                    }
                }
            }
            data.completedLettersCount > 0 -> Text(
                "No letter needs extra practice right now. A few minutes of play each day keeps it that way.",
                fontFamily = LexendFontFamily, fontSize = 14.sp, color = TextMuted, lineHeight = 19.sp
            )
            else -> Text(
                "Tips show here once your child finishes a letter.",
                fontFamily = LexendFontFamily, fontSize = 14.sp, color = TextMuted, lineHeight = 19.sp
            )
        }
    }
}

/** What each word means, with the DepEd grade bands. */
@Composable
fun LevelLegendCard(modifier: Modifier = Modifier) {
    RecordCard(modifier) {
        CardTitle("What the words mean", "For teachers, each one is shown with its DepEd report card word and grade.")
        (ProgressLevel.graded + listOf(ProgressLevel.IN_PROGRESS, ProgressLevel.NOT_STARTED)).forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp).semantics(mergeDescendants = true) {}
            ) {
                Box(modifier = Modifier.width(132.dp)) { LevelChip(level, small = true) }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (level.band != null) {
                        Text(
                            "${level.label}, grade ${level.band}", fontFamily = LexendFontFamily, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = TextMidnight
                        )
                    }
                    Text(level.remark, fontFamily = LexendFontFamily, fontSize = 12.5.sp, color = TextMuted, lineHeight = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "A letter gets its words once your child finishes it, from how many answers were right in Say It and " +
                "Find It. Teachers: the grade uses the DepEd transmutation table (DepEd Order 8, s. 2015), so 6 right " +
                "out of 10 is a grade of 75.",
            fontFamily = LexendFontFamily, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp
        )
    }
}
