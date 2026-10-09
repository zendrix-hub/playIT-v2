package com.playit.app.presentation.findit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.resetsIdle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.playit.app.presentation.components.FindItGrid
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.theme.*

@Composable
fun FindItScreen(
    viewModel: FindItViewModel,
    onNext: (phonemeId: String, heartsLost: Int) -> Unit,
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
    val nextHighlighted by viewModel.nextHighlighted.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.onScreenVisible()
        onDispose {
            viewModel.onScreenHidden()
        }
    }

    val targetLetter = targetPhoneme?.letter?.uppercase() ?: "M"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .resetsIdle { viewModel.onUserInteraction() }
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
        LessonScaffold(
            // 3-Segment Capsule Progress Bar + Back Button + Hearts Status
            topBar = { LessonTopBar(currentStep = LessonStep.FIND_IT, onBack = onBack, hearts = hearts) },
            header = {
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
                    onMascotTap = { viewModel.playFindItIntroAudio() }
                )
            },
            bottomBar = {
                // Bottom Action / Continue Bar
                if (state is FindItState.Completed) {
                    GummyButton(
                        text = "Complete Lesson",
                        onClick = { onNext(targetPhoneme?.id?.toString() ?: "1", viewModel.sessionHeartsLost) },
                        backgroundColor = EmeraldLeaf,
                        shadowColor = EmeraldLeafShadow,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .breathingPulse(enabled = nextHighlighted)
                    )
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Score cell + target sound replay cell, two equal cells on one line at any font
                // size. The score is a check icon plus "n / 3"; the replay is a play icon plus /M/.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                            .background(color = PrimaryJoy.copy(alpha = 0.12f), shape = PillShape)
                            .border(width = 1.5.dp, color = PrimaryJoy.copy(alpha = 0.35f), shape = PillShape)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .semantics(mergeDescendants = true) { contentDescription = "Found $foundCount of 3" },
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryJoyDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "$foundCount / 3",
                            fontFamily = LexendFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryJoyDark,
                            maxLines = 1
                        )
                    }

                    GummyContainer(
                        onClick = if (isPlaying) null else ({ viewModel.playTargetSound() }),
                        enabled = !isPlaying,
                        faceColor = if (isPlaying) SunnyGold else SurfaceCard,
                        shadowColor = if (isPlaying) SunnyGoldShadow else SurfaceCardShadow,
                        shape = PillShape,
                        strokeWidth = 2.dp,
                        strokeColor = ModernBorder,
                        depthHeight = 4.dp,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Playing" else "Hear Sound",
                                tint = if (isPlaying) TextMidnight else PrimaryJoyDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "/$targetLetter/",
                                fontFamily = LexendFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlaying) TextMidnight else PrimaryJoyDark,
                                maxLines = 1
                            )
                        }
                    }
                }

                // 5-card grid: 2 columns, the 5th card centred at the same width
                FindItGrid(items = pictureGrid) { item, i, cardModifier ->
                    val isFound = item.id in foundItemIds
                    val isIncorrectSelection = state is FindItState.Incorrect &&
                            (state as FindItState.Incorrect).selectedItem.id == item.id
                    val borderColor = when {
                        isFound -> EmeraldLeaf
                        isIncorrectSelection -> GentleCorrectionOrange
                        else -> ModernBorderSoft
                    }
                    val faceColor = when {
                        isFound -> EmeraldLeaf.copy(alpha = 0.15f)
                        isIncorrectSelection -> GentleCorrectionOrange.copy(alpha = 0.12f)
                        else -> SurfaceCard
                    }
                    FindItCard(
                        item = item,
                        borderColor = borderColor,
                        faceColor = faceColor,
                        index = i,
                        isCorrect = isFound,
                        isIncorrect = isIncorrectSelection,
                        modifier = cardModifier,
                        onClick = {
                            if (state !is FindItState.GameOver && state !is FindItState.Completed) {
                                viewModel.selectPictureItem(item)
                            }
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
