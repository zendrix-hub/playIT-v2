package com.playit.app.presentation.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.*

data class GuidebookLetter(
    val symbol: String,
    val phoneme: String,
    val exampleWord: String,
    val description: String
)

data class GuidebookUnitData(
    val unitNumber: Int,
    val title: String,
    val biomeName: String,
    val letters: List<GuidebookLetter>,
    val blendWords: List<String>
)

object GuidebookRepository {
    fun getUnitData(unitNumber: Int): GuidebookUnitData {
        return when (unitNumber) {
            1 -> GuidebookUnitData(
                unitNumber = 1,
                title = "Chocolate Hills",
                biomeName = "Chocolate Hills, Bohol",
                letters = listOf(
                    GuidebookLetter("M", "m", "Mouse", "m as in mouse"),
                    GuidebookLetter("S", "s", "Sun", "s as in sun"),
                    GuidebookLetter("A", "a", "Apple", "a as in apple"),
                    GuidebookLetter("I", "i", "Insect", "i as in insect")
                ),
                blendWords = listOf("SAM", "SIS", "AM")
            )
            2 -> GuidebookUnitData(
                unitNumber = 2,
                title = "Loboc River",
                biomeName = "Loboc River, Bohol",
                letters = listOf(
                    GuidebookLetter("O", "o", "Orange", "o as in orange"),
                    GuidebookLetter("B", "b", "Ball", "b as in ball"),
                    GuidebookLetter("E", "e", "Elephant", "e as in elephant"),
                    GuidebookLetter("U", "u", "Umbrella", "u as in umbrella")
                ),
                blendWords = listOf("BUS", "SUB", "MOM", "SUM", "BIB")
            )
            3 -> GuidebookUnitData(
                unitNumber = 3,
                title = "Panglao Beach",
                biomeName = "Panglao, Bohol",
                letters = listOf(
                    GuidebookLetter("T", "t", "Tiger", "t as in tiger"),
                    GuidebookLetter("K", "k", "Kite", "k as in kite"),
                    GuidebookLetter("L", "l", "Lion", "l as in lion"),
                    GuidebookLetter("Y", "y", "Yoyo", "y as in yoyo")
                ),
                blendWords = listOf("BAT", "MAT", "KIT", "TUB", "YAM")
            )
            4 -> GuidebookUnitData(
                unitNumber = 4,
                title = "Tarsier Forest",
                biomeName = "Tarsier forest, Bohol",
                letters = listOf(
                    GuidebookLetter("N", "n", "Nest", "n as in nest"),
                    GuidebookLetter("G", "g", "Goat", "g as in goat"),
                    GuidebookLetter("P", "p", "Pig", "p as in pig")
                ),
                blendWords = listOf("PIG", "PAN", "BUG", "PIN", "NAP")
            )
            5 -> GuidebookUnitData(
                unitNumber = 5,
                title = "Bohol Mountains",
                biomeName = "Bohol mountains",
                letters = listOf(
                    GuidebookLetter("R", "r", "Rabbit", "r as in rabbit"),
                    GuidebookLetter("D", "d", "Dog", "d as in dog"),
                    GuidebookLetter("H", "h", "Hat", "h as in hat"),
                    GuidebookLetter("W", "w", "Watch", "w as in watch")
                ),
                blendWords = listOf("DOG", "HAT", "HEN", "BED", "WEB")
            )
            6 -> GuidebookUnitData(
                unitNumber = 6,
                title = "Baclayon Church",
                biomeName = "Baclayon, Bohol",
                letters = listOf(
                    GuidebookLetter("C", "c", "Cat", "c as in cat"),
                    GuidebookLetter("F", "f", "Fish", "f as in fish"),
                    GuidebookLetter("J", "j", "Jug", "j as in jug")
                ),
                blendWords = listOf("CAT", "FAN", "CAP", "CUP", "JAM")
            )
            7 -> GuidebookUnitData(
                unitNumber = 7,
                title = "Mount Pulag",
                biomeName = "Mount Pulag, Benguet",
                letters = listOf(
                    GuidebookLetter("Q", "q", "Queen", "q as in queen"),
                    GuidebookLetter("V", "v", "Van", "v as in van"),
                    GuidebookLetter("X", "x", "Box", "x at the end, as in box"),
                    GuidebookLetter("Z", "z", "Zebra", "z as in zebra")
                ),
                blendWords = listOf("VAN", "BOX", "FOX", "ZIP", "QUIZ")
            )
            else -> getUnitData(1)
        }
    }
}

/**
 * Duolingo-style Unit Guidebook Bottom Sheet / Modal Dialog.
 * Provides parents and learners with phonics sounds, example words, and biome overview.
 */
@Composable
fun UnitGuidebookDialog(
    unitNumber: Int,
    onDismiss: () -> Unit
) {
    val data = GuidebookRepository.getUnitData(unitNumber)
    val theme = BiomeThemes.forSection(unitNumber)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceCard)
                .border(2.5.dp, ModernBorder, RoundedCornerShape(28.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(theme.primaryColor, theme.shelfColor)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // The title takes what the 64 dp close button leaves, so a long title wraps
                        // instead of squeezing the button (card 31e).
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoStories,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "CHAPTER $unitNumber",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White.copy(alpha = 0.90f),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = data.title,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        // 64 dp touch area around a 40 dp circle (it was 36 dp, card 31e).
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(64.dp).testTag("guidebook_close")
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.30f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Guidebook Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "SOUNDS IN THIS CHAPTER",
                            fontFamily = LexendFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }

                    items(data.letters) { letter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CanvasLight)
                                .border(1.5.dp, ModernBorderSoft, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Letter 3D Pill
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(theme.primaryColor)
                                    .border(2.dp, ModernBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter.symbol,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Sound: ${letter.phoneme}",
                                        fontFamily = LexendFontFamily,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextMidnight
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.VolumeUp,
                                        contentDescription = null,
                                        tint = theme.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "As in ${letter.exampleWord}",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "WORDS TO BUILD",
                            fontFamily = LexendFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            data.blendWords.forEach { word ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(theme.primaryColor.copy(alpha = 0.15f))
                                        .border(1.5.dp, theme.primaryColor.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = word,
                                        fontFamily = LexendFontFamily,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.shelfColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer CTA Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    GummyButton(
                        text = "OK",
                        onClick = onDismiss,
                        backgroundColor = theme.primaryColor,
                        shadowColor = theme.shelfColor,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                    )
                }
            }
        }
    }
}
