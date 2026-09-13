package com.playit.app.presentation.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                title = "Chocolate Hills Phonics",
                biomeName = "Chocolate Hills, Bohol",
                letters = listOf(
                    GuidebookLetter("M", "/m/", "Mouse", "Continuous humming sound"),
                    GuidebookLetter("S", "/s/", "Sun", "Hissing continuous sound"),
                    GuidebookLetter("A", "/æ/", "Apple", "Open vowel sound"),
                    GuidebookLetter("I", "/ɪ/", "Insect", "Front high vowel sound")
                ),
                blendWords = listOf("SAM", "SIS", "AIM")
            )
            2 -> GuidebookUnitData(
                unitNumber = 2,
                title = "Loboc River Phonics",
                biomeName = "Loboc River Valley",
                letters = listOf(
                    GuidebookLetter("O", "/ɒ/", "Orange", "Round back vowel sound"),
                    GuidebookLetter("B", "/b/", "Ball", "Voiced bilabial stop"),
                    GuidebookLetter("E", "/e/", "Elephant", "Mid front vowel sound"),
                    GuidebookLetter("U", "/ʌ/", "Umbrella", "Short central vowel sound")
                ),
                blendWords = listOf("BUS", "SUB", "MOM", "BEE", "BIB")
            )
            3 -> GuidebookUnitData(
                unitNumber = 3,
                title = "Panglao Shore Phonics",
                biomeName = "Panglao Coral Shore",
                letters = listOf(
                    GuidebookLetter("T", "/t/", "Tiger", "Voiceless alveolar stop"),
                    GuidebookLetter("K", "/k/", "Kite", "Voiceless velar stop"),
                    GuidebookLetter("L", "/l/", "Lion", "Alveolar lateral liquid"),
                    GuidebookLetter("Y", "/j/", "Yoyo", "Palatal glide sound")
                ),
                blendWords = listOf("BAT", "MAT", "KIT", "TOY", "BOY")
            )
            4 -> GuidebookUnitData(
                unitNumber = 4,
                title = "Tarsier Forest Phonics",
                biomeName = "Tarsier Rainforest Sanctuary",
                letters = listOf(
                    GuidebookLetter("N", "/n/", "Nest", "Alveolar nasal sound"),
                    GuidebookLetter("G", "/g/", "Goat", "Voiced velar stop"),
                    GuidebookLetter("P", "/p/", "Pig", "Voiceless bilabial stop")
                ),
                blendWords = listOf("PIG", "PAN", "BUG", "PIN", "NAP")
            )
            5 -> GuidebookUnitData(
                unitNumber = 5,
                title = "Mountain Summit Phonics",
                biomeName = "Bohol Mountain Summit",
                letters = listOf(
                    GuidebookLetter("R", "/r/", "Rabbit", "Alveolar liquid sound"),
                    GuidebookLetter("D", "/d/", "Dog", "Voiced alveolar stop"),
                    GuidebookLetter("H", "/h/", "Hat", "Glottal fricative sound"),
                    GuidebookLetter("W", "/w/", "Watch", "Labio-velar glide")
                ),
                blendWords = listOf("DOG", "HAT", "HEN", "BED", "WEB")
            )
            6 -> GuidebookUnitData(
                unitNumber = 6,
                title = "Baclayon Heritage Phonics",
                biomeName = "Baclayon Heritage Trail",
                letters = listOf(
                    GuidebookLetter("C", "/k/", "Cat", "Hard velar consonant"),
                    GuidebookLetter("F", "/f/", "Fish", "Voiceless labiodental fricative"),
                    GuidebookLetter("J", "/dʒ/", "Jug", "Voiced postalveolar affricate")
                ),
                blendWords = listOf("CAT", "FAN", "CAP", "CUP", "JAM")
            )
            7 -> GuidebookUnitData(
                unitNumber = 7,
                title = "Mount Pulag Summit Phonics",
                biomeName = "Mount Pulag Summit",
                letters = listOf(
                    GuidebookLetter("Q", "/kw/", "Queen", "Velar stop with glide"),
                    GuidebookLetter("V", "/v/", "Van", "Voiced labiodental fricative"),
                    GuidebookLetter("X", "/ks/", "Box", "Consonant blend sound"),
                    GuidebookLetter("Z", "/z/", "Zebra", "Voiced alveolar fricative")
                ),
                blendWords = listOf("VAN", "BOX", "FOX", "ZOO", "QUIZ")
            )
            else -> GuidebookUnitData(
                unitNumber = 1,
                title = "Chocolate Hills Phonics",
                biomeName = "Chocolate Hills, Bohol",
                letters = listOf(
                    GuidebookLetter("M", "/m/", "Mouse", "Continuous humming sound"),
                    GuidebookLetter("S", "/s/", "Sun", "Hissing continuous sound"),
                    GuidebookLetter("A", "/æ/", "Apple", "Open vowel sound"),
                    GuidebookLetter("I", "/ɪ/", "Insect", "Front high vowel sound")
                ),
                blendWords = listOf("SAM", "SIS", "AIM")
            )
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
                        Row(
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
                                    text = "UNIT $unitNumber GUIDEBOOK",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 11.sp,
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

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.30f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
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
                            text = "PHONICS SOUNDS IN THIS UNIT",
                            fontFamily = LexendFontFamily,
                            fontSize = 12.sp,
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
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextMidnight
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = null,
                                        tint = theme.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Example: ${letter.exampleWord}",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "BLEND WORDS PRACTICE",
                            fontFamily = LexendFontFamily,
                            fontSize = 12.sp,
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
                                        fontSize = 13.sp,
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
                        text = "Got It!",
                        onClick = onDismiss,
                        backgroundColor = theme.primaryColor,
                        shadowColor = theme.shelfColor,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    )
                }
            }
        }
    }
}
