package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.domain.model.ChapterRecord
import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.CardShape
import com.playit.app.presentation.theme.DialogShape
import com.playit.app.presentation.theme.LevelColors
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.PillShape
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.PrimaryJoyShadow
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldLight
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

/*
 * The Parent Zone, short and plain (adviser, 2026-11-05: parents should "understand the progress by just looking at
 * it"; user decisions 2026-11-07: "too much text or info may confuse"): a row per child, the child's summary, then the
 * 26 letters as one grid of coloured tiles with a mark on each, details one tap away. Parents see plain words only;
 * the DepEd words and grades are in the PDF for the teacher.
 */

/** The smallest text on these screens (user decision 2026-11-07); body text is at least 16 sp. */
const val PARENT_LABEL_SP = 14
const val PARENT_BODY_SP = 16
/** A letter tile is never smaller than this, so a thumb hits the right one. */
val LETTER_TILE_MIN: Dp = 56.dp

/** A level in a parent's words, on its colour. */
@Composable
fun LevelChip(level: ProgressLevel, modifier: Modifier = Modifier, small: Boolean = false) {
    val c = LevelColors.of(level)
    Box(
        modifier = modifier
            .widthIn(max = if (small) 140.dp else 220.dp)
            .clip(PillShape)
            .background(Color(c.fill))
            .padding(horizontal = if (small) 9.dp else 12.dp, vertical = if (small) 3.dp else 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.parentLabel,
            fontFamily = LexendFontFamily,
            fontSize = if (small) PARENT_LABEL_SP.sp else 15.sp,
            lineHeight = if (small) 17.sp else 19.sp,
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
        Column(modifier = Modifier.padding(12.dp)) { content() }
    }
}

/** "Learned 5 of 26 sounds". */
fun learnedText(data: ProfileDashboardData) =
    "Learned ${data.completedLettersCount} of ${data.totalLettersCount} sounds"

/** Every child on this phone, one short row each; tapping a row shows that child below. */
@Composable
fun ChildrenCard(
    learners: List<ProfileDashboardData>,
    selectedId: Long?,
    onSelect: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordCard(modifier.testTag("children_card")) {
        Text("Tap a child to see how they are doing.", fontFamily = LexendFontFamily, fontSize = PARENT_LABEL_SP.sp,
            lineHeight = 18.sp, color = TextMuted, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        learners.forEach { learner ->
            val selected = learner.profile.id == selectedId
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) SunnyGoldLight else Color.Transparent)
                    .border(2.dp, if (selected) SunnyGold else ModernBorderSoft, RoundedCornerShape(14.dp))
                    .clickable(role = Role.Button) { onSelect(learner.profile) }
                    .heightIn(min = 52.dp)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${learner.profile.name}: ${learner.overallLevel.parentLabel}. ${learnedText(learner)}."
                    }
            ) {
                AvatarCircle(avatarId = learner.profile.avatarResId, size = 36)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(learner.profile.name, fontFamily = LexendFontFamily, fontSize = PARENT_BODY_SP.sp, lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold, color = TextMidnight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${learner.completedLettersCount} of ${learner.totalLettersCount} sounds", fontFamily = LexendFontFamily,
                        fontSize = PARENT_LABEL_SP.sp, lineHeight = 18.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.width(6.dp))
                LevelChip(learner.overallLevel, small = true)
            }
        }
    }
}

// ---- The letter grid -----------------------------------------------------------------------------------------------

/** A letter's tile, in three states a parent tells apart by colour and by its mark. */
enum class TileState(val word: String, val fill: Long, val ink: Long) {
    KNOWS_IT("Knows it", 0xFFD8F0DC, 0xFF1B5E28),       // a tick
    STILL_LEARNING("Still learning", 0xFFFFEFD1, 0xFF7A4F00), // a dot
    NOT_YET("Not yet", 0xFFF1ECE4, 0xFF6E5E4D);         // no mark

    companion object {
        /** Finished at Satisfactory or better: knows it. Started, or finished below Satisfactory: still learning. */
        fun of(level: ProgressLevel): TileState = when {
            level == ProgressLevel.NOT_STARTED -> NOT_YET
            level.isGraded && !level.needsPractice -> KNOWS_IT
            else -> STILL_LEARNING
        }
    }
}

/** The 26 letters, one row per chapter, each row ending with its word game's mark; a key underneath. */
@Composable
fun LetterGridCard(chapters: List<ChapterRecord>, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf<LetterPerformance?>(null) }
    open?.let { LetterDetailDialog(it, onDismiss = { open = null }) }
    RecordCard(modifier.testTag("letter_grid")) {
        Text("Letters", fontFamily = LexendFontFamily, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
            color = TextMidnight, modifier = Modifier.padding(start = 4.dp))
        Text("Tap a letter to see how it went.", fontFamily = LexendFontFamily, fontSize = PARENT_LABEL_SP.sp,
            lineHeight = 18.sp, color = TextMuted, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // A row holds the chapter's number, up to 4 tiles and the word game's mark; the tiles take what is left.
            val fixed = CHAPTER_LABEL_W + 4.dp + GAME_MARK_W + 4.dp + TILE_GAP * 4
            val tile = ((maxWidth - fixed) / 4).coerceIn(LETTER_TILE_MIN, 76.dp)
            // Every row is as wide as a 4-letter one, so the word-game marks line up; the grid is centred.
            Column(
                verticalArrangement = Arrangement.spacedBy(TILE_GAP),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                chapters.forEach { chapter ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("grid_row_${chapter.chapter}")
                    ) {
                        Text("${chapter.chapter}", fontFamily = LexendFontFamily, fontSize = PARENT_LABEL_SP.sp,
                            fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.Center,
                            modifier = Modifier.width(CHAPTER_LABEL_W).semantics { contentDescription = "Chapter ${chapter.chapter}" })
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(TILE_GAP), modifier = Modifier.width(tile * 4 + TILE_GAP * 3)) {
                            chapter.letters.forEach { letter -> LetterTile(letter, tile) { open = letter } }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        WordGameMark(chapter.blendItCompleted)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        GridKey()
    }
}

private val CHAPTER_LABEL_W = 20.dp
private val GAME_MARK_W = 36.dp
private val TILE_GAP = 4.dp

@Composable
private fun LetterTile(letter: LetterPerformance, size: Dp, onTap: () -> Unit) {
    val state = TileState.of(letter.level)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(state.fill))
            .border(2.dp, Color(state.ink).copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .clickable(role = Role.Button, onClick = onTap)
            .semantics(mergeDescendants = true) {
                contentDescription = "Letter ${letter.symbol}, ${letter.keyWord}: ${state.word}"
            }
            .testTag("tile_${letter.symbol}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            letter.symbol.uppercase() + letter.symbol.lowercase(), fontFamily = LexendFontFamily, fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold, color = Color(state.ink)
        )
        TileMark(state, Modifier.align(Alignment.TopEnd).padding(4.dp))
    }
}

/** The mark that goes with the colour, so a parent who can't tell the colours apart still can: a tick, a dot, none. */
@Composable
private fun TileMark(state: TileState, modifier: Modifier = Modifier) {
    when (state) {
        TileState.KNOWS_IT -> Box(
            modifier = modifier.size(16.dp).clip(CircleShape).background(Color(state.ink)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp)) }
        TileState.STILL_LEARNING -> Box(modifier = modifier.padding(3.dp).size(10.dp).clip(CircleShape).background(Color(state.ink)))
        TileState.NOT_YET -> Unit
    }
}

@Composable
private fun WordGameMark(done: Boolean, modifier: Modifier = Modifier) {
    val ink = Color(if (done) TileState.KNOWS_IT.ink else TileState.NOT_YET.ink)
    Box(
        modifier = modifier
            .size(GAME_MARK_W)
            .clip(CircleShape)
            .background(if (done) Color(TileState.KNOWS_IT.fill) else Color.Transparent)
            .border(2.dp, ink.copy(alpha = if (done) 0.8f else 0.35f), CircleShape)
            .semantics { contentDescription = if (done) "Word game done" else "Word game not yet" },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Rounded.Extension, contentDescription = null, tint = ink.copy(alpha = if (done) 1f else 0.45f),
            modifier = Modifier.size(20.dp))
    }
}

/** What the colours and marks mean. */
@Composable
private fun GridKey() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            KeyItem(TileState.KNOWS_IT)
            KeyItem(TileState.STILL_LEARNING)
            KeyItem(TileState.NOT_YET)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Extension, contentDescription = null, tint = Color(TileState.KNOWS_IT.ink), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Word game done", fontFamily = LexendFontFamily, fontSize = PARENT_LABEL_SP.sp, color = TextMidnight)
        }
    }
}

@Composable
private fun KeyItem(state: TileState) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Box(
            modifier = Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).background(Color(state.fill))
                .border(1.5.dp, Color(state.ink).copy(alpha = 0.45f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) { TileMark(state) }
        Spacer(modifier = Modifier.width(6.dp))
        Text(state.word, fontFamily = LexendFontFamily, fontSize = PARENT_LABEL_SP.sp, color = TextMidnight)
    }
}

/** A letter's details, one tap away: its word and picture, how it went in plain words, the right answers, a tip. */
@Composable
fun LetterDetailDialog(letter: LetterPerformance, onDismiss: () -> Unit) {
    val state = TileState.of(letter.level)
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = DialogShape, color = SurfaceCard, border = BorderStroke(2.5.dp, ModernBorder), shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("letter_detail")
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(Color(state.fill)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(letter.symbol.uppercase() + letter.symbol.lowercase(), fontFamily = LexendFontFamily,
                            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(state.ink))
                    }
                    if (letter.imagePath.isNotBlank()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Image(rememberAssetPainter(letter.imagePath, 96.dp), contentDescription = letter.keyWord,
                            contentScale = ContentScale.Fit, modifier = Modifier.size(80.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(letter.keyWord, fontFamily = LexendFontFamily, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    color = TextMidnight)
                Spacer(modifier = Modifier.height(8.dp))
                LevelChip(letter.level)
                Spacer(modifier = Modifier.height(12.dp))
                Text(answers("Say It", letter.sayItCorrect, letter.sayItAttempts), fontFamily = LexendFontFamily,
                    fontSize = PARENT_BODY_SP.sp, color = TextMidnight)
                Text(answers("Find It", letter.findItCorrect, letter.findItAttempts), fontFamily = LexendFontFamily,
                    fontSize = PARENT_BODY_SP.sp, color = TextMidnight)
                if (letter.level.needsPractice) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val s = letter.symbol.lowercase()
                    Text("Say \"$s, $s, ${letter.keyWord.lowercase()}\" together, then play this letter again.",
                        fontFamily = LexendFontFamily, fontSize = PARENT_BODY_SP.sp, lineHeight = 21.sp, color = TextMidnight,
                        textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(16.dp))
                GummyButton(text = "Close", onClick = onDismiss, backgroundColor = PrimaryJoy, shadowColor = PrimaryJoyShadow,
                    contentColor = Color.White, modifier = Modifier.fillMaxWidth().height(52.dp))
            }
        }
    }
}

private fun answers(game: String, right: Int, tries: Int) =
    if (tries == 0) "$game: not played yet" else "$game: $right of $tries right"
