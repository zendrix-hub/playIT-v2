package com.playit.app.presentation.findit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.CelebrationOverlay
import com.playit.app.presentation.components.CelebrationType
import com.playit.app.presentation.components.FindItCard
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.UbeDark
import com.playit.app.presentation.theme.UbeLight
import com.playit.app.presentation.theme.UbeShadow

@Composable
fun FindItScreen(
    viewModel: FindItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val targetPhoneme by viewModel.targetPhoneme.collectAsState()
    val pictureGrid by viewModel.pictureGrid.collectAsState()
    val foundItemIds by viewModel.foundItemIds.collectAsState()
    val foundCount by viewModel.foundCount.collectAsState()
    val state by viewModel.state.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val targetLetter = targetPhoneme?.letter?.uppercase() ?: "M"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 3-Segment Capsule Progress Bar + Back Button + Hearts Status
            LessonTopBar(
                currentStep = LessonStep.FIND_IT,
                onBack = onBack,
                hearts = hearts
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot speech bubble prompt
                MascotSpeechHeader(
                    message = when (state) {
                        is FindItState.GameOver -> "Out of hearts! Let's try again."
                        is FindItState.Completed -> "You found all 3 pictures for /$targetLetter/!"
                        is FindItState.FoundOne -> "Great find! Find ${(3 - foundCount).coerceAtLeast(1)} more!"
                        is FindItState.Incorrect -> "Doesn't start with /$targetLetter/. Try another!"
                        else -> "Find all 3 pictures that start with /$targetLetter/!"
                    },
                    mascotState = when (state) {
                        is FindItState.Completed -> MascotState.CELEBRATING
                        is FindItState.FoundOne -> MascotState.CELEBRATING
                        is FindItState.GameOver -> MascotState.THINKING
                        is FindItState.Incorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    onMascotTap = { if (!isPlaying) viewModel.playTargetSound() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Score pill + Sound Replay Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found: $foundCount / 3",
                        fontFamily = LexendFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = UbeDark,
                        modifier = Modifier
                            .background(color = UbeLight, shape = RoundedCornerShape(999.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    GummyContainer(
                        onClick = if (isPlaying) null else ({ viewModel.playTargetSound() }),
                        enabled = !isPlaying,
                        faceColor = UbeLight,
                        shadowColor = UbeShadow,
                        shape = RoundedCornerShape(999.dp),
                        depthHeight = 2.dp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .heightIn(min = 44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Playing" else "Hear Sound",
                                tint = Ink,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Hear Sound",
                                fontFamily = LexendFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5-Card Grid: Row 1 has 2 items (span 3), Row 2 has 3 items (span 2)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = pictureGrid,
                        key = { _, item -> item.id },
                        span = { index, _ ->
                            if (index < 2) GridItemSpan(3) else GridItemSpan(2)
                        }
                    ) { index, item ->
                        val isFound = item.id in foundItemIds
                        val isIncorrectSelection = state is FindItState.Incorrect &&
                                (state as FindItState.Incorrect).selectedItem.id == item.id

                        val borderColor = when {
                            isFound -> Leaf
                            isIncorrectSelection -> Kalamansi
                            else -> DarkBrownOutline
                        }
                        val faceColor = when {
                            isFound -> Color(0xFFEAF7EE)
                            isIncorrectSelection -> Color(0xFFFFF4E4)
                            else -> CreamWhite
                        }

                        FindItCard(
                            item = item,
                            borderColor = borderColor,
                            faceColor = faceColor,
                            index = index,
                            isCorrect = isFound,
                            isIncorrect = isIncorrectSelection,
                            onClick = {
                                if (state !is FindItState.GameOver && state !is FindItState.Completed) {
                                    viewModel.selectPictureItem(item)
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Action / Continue Bar
            if (state is FindItState.Completed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    GummyButton(
                        text = "Next Lesson",
                        onClick = { onNext("sayit") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }
        }

        // Celebration Confetti Overlay on Lesson Complete
        CelebrationOverlay(
            type = CelebrationType.CONFETTI,
            isPlaying = state is FindItState.Completed
        )

        // Game Over Overlay
        CelebrationOverlay(
            type = CelebrationType.STAR_BURST,
            isPlaying = state is FindItState.GameOver,
            onFinished = { viewModel.restartSession() }
        )
    }
}
