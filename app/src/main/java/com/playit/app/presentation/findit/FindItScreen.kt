package com.playit.app.presentation.findit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.playit.app.presentation.theme.*

@Composable
fun FindItScreen(
    viewModel: FindItViewModel,
    onNext: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    val targetPhoneme by viewModel.targetPhoneme.collectAsStateWithLifecycle()
    val pictureGrid by viewModel.pictureGrid.collectAsStateWithLifecycle()
    val foundItemIds by viewModel.foundItemIds.collectAsStateWithLifecycle()
    val foundCount by viewModel.foundCount.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val hearts by viewModel.hearts.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isPlayingPrompt by viewModel.isPlayingPrompt.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()

    val targetLetter = targetPhoneme?.letter?.uppercase() ?: "M"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F0FE),
                        Color(0xFFF3E8FF),
                        Color(0xFFFEF3C7)
                    )
                )
            )
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
                // Mascot speech bubble prompt (Tapping Lily replays the game rule voiceover)
                MascotSpeechHeader(
                    message = when (state) {
                        is FindItState.GameOver -> "Good try! Let's listen again."
                        is FindItState.Completed -> "You did it! I'm so proud of you!"
                        is FindItState.FoundOne -> "Perfect! Great job! Find ${(3 - foundCount).coerceAtLeast(1)} more!"
                        is FindItState.Incorrect -> "Good try! Let's listen again."
                        else -> "Can you find all three pictures that start with this sound?"
                    },
                    mascotState = when (state) {
                        is FindItState.Completed -> MascotState.CELEBRATING
                        is FindItState.FoundOne -> MascotState.CELEBRATING
                        is FindItState.GameOver -> MascotState.THINKING
                        is FindItState.Incorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    isPlayingAudio = isPlayingPrompt,
                    onMascotTap = { if (!isAudioPlaying) viewModel.playFindItIntroAudio() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Score pill + Dedicated Target Sound Replay Pill (Tapping replays the pure phoneme sound)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found: $foundCount / 3",
                        fontFamily = LexendFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryJoyDark,
                        modifier = Modifier
                            .background(PrimaryJoyLight.copy(alpha = 0.25f), PillShape)
                            .border(1.5.dp, PrimaryJoy.copy(alpha = 0.35f), PillShape)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    GummyContainer(
                        onClick = if (isAudioPlaying) null else ({ viewModel.playTargetSound() }),
                        enabled = !isAudioPlaying,
                        faceColor = if (isAudioPlaying) SunnyGold else SurfaceCard,
                        shadowColor = if (isAudioPlaying) SunnyGoldShadow else SurfaceCardShadow,
                        shape = PillShape,
                        strokeWidth = 2.dp,
                        strokeColor = ModernBorder,
                        depthHeight = 4.dp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .heightIn(min = 56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Playing" else "Hear Sound",
                                tint = if (isPlaying) TextMidnight else PrimaryJoyDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Hear: /${targetPhoneme?.letter?.uppercase() ?: "M"}/",
                                fontFamily = LexendFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlaying) TextMidnight else PrimaryJoyDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5-Card Uniform Grid: All boxes have the EXACT same dimensions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1: 2 cards (Equal size)
                    if (pictureGrid.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (i in 0 until minOf(2, pictureGrid.size)) {
                                val item = pictureGrid[i]
                                val isFound = item.id in foundItemIds
                                val isIncorrectSelection = state is FindItState.Incorrect &&
                                        (state as FindItState.Incorrect).selectedItem.id == item.id
                                val borderColor = when {
                                    isFound -> EmeraldLeaf
                                    isIncorrectSelection -> CoralBerry
                                    else -> ModernBorderSoft
                                }
                                val faceColor = when {
                                    isFound -> EmeraldLeaf.copy(alpha = 0.15f)
                                    isIncorrectSelection -> CoralBerry.copy(alpha = 0.12f)
                                    else -> SurfaceCard
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    FindItCard(
                                        item = item,
                                        borderColor = borderColor,
                                        faceColor = faceColor,
                                        index = i,
                                        isCorrect = isFound,
                                        isIncorrect = isIncorrectSelection,
                                        onClick = {
                                            if (!isAudioPlaying && state !is FindItState.GameOver && state !is FindItState.Completed) {
                                                viewModel.selectPictureItem(item)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: 2 cards (Equal size)
                    if (pictureGrid.size > 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (i in 2 until minOf(4, pictureGrid.size)) {
                                val item = pictureGrid[i]
                                val isFound = item.id in foundItemIds
                                val isIncorrectSelection = state is FindItState.Incorrect &&
                                        (state as FindItState.Incorrect).selectedItem.id == item.id
                                val borderColor = when {
                                    isFound -> EmeraldLeaf
                                    isIncorrectSelection -> CoralBerry
                                    else -> ModernBorderSoft
                                }
                                val faceColor = when {
                                    isFound -> EmeraldLeaf.copy(alpha = 0.15f)
                                    isIncorrectSelection -> CoralBerry.copy(alpha = 0.12f)
                                    else -> SurfaceCard
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    FindItCard(
                                        item = item,
                                        borderColor = borderColor,
                                        faceColor = faceColor,
                                        index = i,
                                        isCorrect = isFound,
                                        isIncorrect = isIncorrectSelection,
                                        onClick = {
                                            if (!isAudioPlaying && state !is FindItState.GameOver && state !is FindItState.Completed) {
                                                viewModel.selectPictureItem(item)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Row 3: 5th card (Centered, with identical width to Row 1 & 2 cards)
                    if (pictureGrid.size > 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val i = 4
                            val item = pictureGrid[i]
                            val isFound = item.id in foundItemIds
                            val isIncorrectSelection = state is FindItState.Incorrect &&
                                     (state as FindItState.Incorrect).selectedItem.id == item.id
                            val borderColor = when {
                                isFound -> EmeraldLeaf
                                isIncorrectSelection -> CoralBerry
                                else -> ModernBorderSoft
                            }
                            val faceColor = when {
                                isFound -> EmeraldLeaf.copy(alpha = 0.15f)
                                isIncorrectSelection -> CoralBerry.copy(alpha = 0.12f)
                                else -> SurfaceCard
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(horizontal = 2.5.dp)
                            ) {
                                FindItCard(
                                    item = item,
                                    borderColor = borderColor,
                                    faceColor = faceColor,
                                    index = i,
                                    isCorrect = isFound,
                                    isIncorrect = isIncorrectSelection,
                                    onClick = {
                                        if (!isAudioPlaying && state !is FindItState.GameOver && state !is FindItState.Completed) {
                                            viewModel.selectPictureItem(item)
                                        }
                                    }
                                )
                            }
                        }
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
                        text = "Complete Lesson",
                        onClick = {
                            if (!isAudioPlaying) {
                                onNext(targetPhoneme?.id?.toString() ?: "1", viewModel.heartManager.heartsLost)
                            }
                        },
                        enabled = !isAudioPlaying,
                        backgroundColor = EmeraldLeaf,
                        shadowColor = EmeraldLeafShadow,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .graphicsLayer {
                                alpha = if (!isAudioPlaying) 1f else 0.5f
                            }
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
