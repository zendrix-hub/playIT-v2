package com.playit.app.presentation.blendit

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.platform.testTag
import com.playit.app.presentation.components.LessonScaffold
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.playit.app.presentation.components.resetsIdle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.BlendItCard
import com.playit.app.presentation.components.CelebrationOverlay
import com.playit.app.presentation.components.CelebrationType
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlendItScreen(
    viewModel: BlendItViewModel,
    onSessionComplete: (BlendItResult) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val words by viewModel.words.collectAsStateWithLifecycle()
    val currentWord by viewModel.currentWord.collectAsStateWithLifecycle()
    val tileBank by viewModel.tileBank.collectAsStateWithLifecycle()
    val placedTiles by viewModel.placedTiles.collectAsStateWithLifecycle()
    val hearts by viewModel.hearts.collectAsStateWithLifecycle()
    val currentWordIndex by viewModel.currentWordIndex.collectAsStateWithLifecycle()
    val isPlayingPrompt by viewModel.isPlayingPrompt.collectAsStateWithLifecycle()
    val highlightedSlotIndex by viewModel.highlightedSlotIndex.collectAsStateWithLifecycle()
    val totalWords = words.size.coerceAtLeast(1)
    val d = LocalPlayItDimens.current

    // Fires onSessionComplete once SessionComplete is emitted
    LaunchedEffect(uiState) {
        if (uiState is BlendItUiState.SessionComplete) {
            delay(800L) // allow completion chime and celebration animation to play
            onSessionComplete(viewModel.result())
        }
    }

    // Headspace: gentle non-punitive tile wobble on incorrect attempt
    val wobble = remember { Animatable(0f) }
    LaunchedEffect(uiState) {
        if (uiState is BlendItUiState.WordIncorrect) {
            wobble.animateTo(-6f, tween(60))
            wobble.animateTo(6f, tween(120))
            wobble.animateTo(-4f, tween(120))
            wobble.animateTo(0f, tween(80))
        }
    }

    DisposableEffect(Unit) {
        viewModel.onScreenVisible()
        onDispose {
            viewModel.onScreenHidden()
        }
    }

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
            topBar = { LessonTopBar(currentStep = LessonStep.BLEND_IT, onBack = onBack, hearts = hearts) },
            header = {
                MascotSpeechHeader(
                    message = when (uiState) {
                        is BlendItUiState.WordCorrect -> "Perfect! Great job!"
                        is BlendItUiState.WordIncorrect -> "Good try! Let's listen again."
                        else -> "Let's blend letter sounds together to build words!"
                    },
                    mascotState = when (uiState) {
                        is BlendItUiState.WordCorrect -> MascotState.CELEBRATING
                        is BlendItUiState.WordIncorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    isPlayingAudio = isPlayingPrompt,
                    onMascotTap = { viewModel.playBlendItIntroAudio() }
                )
            },
            bottomBar = {
                val isReady = placedTiles.size == (currentWord?.word?.length ?: 3)
                val isWordCorrect = uiState is BlendItUiState.WordCorrect
                GummyContainer(
                    onClick = {
                        if (isReady && !isWordCorrect) {
                            viewModel.submitWord()
                        }
                    },
                    faceColor = if (isWordCorrect) EmeraldLeaf else SunnyGold,
                    shadowColor = if (isWordCorrect) EmeraldLeafShadow else SunnyGoldShadow,
                    shape = ButtonShape,
                    strokeWidth = 2.5.dp,
                    strokeColor = ModernBorder,
                    depthHeight = 6.dp,
                    isSquashed = isWordCorrect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .graphicsLayer {
                            alpha = if (isReady) 1f else 0.45f
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isWordCorrect) "Blending..." else "Check Word",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isWordCorrect) Color.White else TextMidnight
                        )
                    }
                }
            }
        ) {
            // Word progress pill (24sp child reading floor)
            Text(
                text = "Word: ${currentWordIndex + 1} / $totalWords",
                fontFamily = LexendFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryJoyDark,
                modifier = Modifier
                    .background(color = PrimaryJoy.copy(alpha = 0.12f), shape = PillShape)
                    .border(width = 1.5.dp, color = PrimaryJoy.copy(alpha = 0.35f), shape = PillShape)
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            )

            // Illustrated Target Word Card
            currentWord?.let { wordItem ->
                BlendItCard(
                    word = wordItem.word,
                    isCorrect = uiState is BlendItUiState.WordCorrect,
                    onReplayAudio = { viewModel.playTargetWordAudio() },
                    // On 360x640 the card (with its smaller compact picture) gives up 8 dp of its 176 dp
                    // minimum so the tiles clear the bottom bar.
                    modifier = if (d.profile == WindowProfile.COMPACT) Modifier.heightIn(max = 168.dp) else Modifier
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Target Letter Slots (Target Drop Area) with gentle wobble on incorrect and sequential sound-out bounce
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().graphicsLayer { rotationZ = wobble.value }
                ) {
                    val wordLength = currentWord?.word?.length ?: 3
                    for (i in 0 until wordLength) {
                        val tile = placedTiles.getOrNull(i)
                        val isHighlighted = highlightedSlotIndex == i || highlightedSlotIndex == BlendItViewModel.ALL_SLOTS
                        val slotScale by animateFloatAsState(
                            targetValue = if (isHighlighted) 1.14f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "slotScale_$i"
                        )
                        GummyContainer(
                            onClick = {
                                if (tile != null && uiState !is BlendItUiState.WordCorrect) {
                                    viewModel.removeTile(i)
                                }
                            },
                            faceColor = when {
                                isHighlighted -> SunnyGold
                                tile != null && uiState is BlendItUiState.WordCorrect -> EmeraldLeaf
                                tile != null -> SurfaceCard
                                else -> CanvasLight
                            },
                            shadowColor = when {
                                isHighlighted -> SunnyGoldShadow
                                tile != null && uiState is BlendItUiState.WordCorrect -> EmeraldLeafShadow
                                tile != null -> SurfaceCardShadow
                                else -> SurfaceCardShadow
                            },
                            shape = Squircle16,
                            strokeWidth = if (isHighlighted) 3.dp else 2.dp,
                            strokeColor = when {
                                isHighlighted -> SunnyGold
                                uiState is BlendItUiState.WordCorrect -> EmeraldLeaf
                                uiState is BlendItUiState.WordIncorrect -> GentleCorrectionOrange
                                else -> ModernBorderSoft
                            },
                            depthHeight = if (isHighlighted) 6.dp else 4.dp,
                            modifier = Modifier
                                .size(d.tileSize)
                                .graphicsLayer {
                                    scaleX = slotScale
                                    scaleY = slotScale
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = tile?.uppercase() ?: "_",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when {
                                        isHighlighted -> TextMidnight
                                        tile != null && uiState is BlendItUiState.WordCorrect -> Color.White
                                        tile != null -> TextMidnight
                                        else -> TextMuted.copy(alpha = 0.4f)
                                    }
                                )
                            }
                        }
                    }
                }

                // Available Tile Bank with idle interaction bounce
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("blendit_tiles")
                ) {
                    tileBank.forEachIndexed { _, tileLetter ->
                        GummyContainer(
                            onClick = {
                                if (uiState !is BlendItUiState.WordCorrect) {
                                    viewModel.placeTile(tileLetter)
                                }
                            },
                            faceColor = SunnyGold,
                            shadowColor = SunnyGoldShadow,
                            shape = Squircle16,
                            strokeWidth = 2.dp,
                            strokeColor = ModernBorder,
                            depthHeight = 5.dp,
                            modifier = Modifier
                                .size(d.tileSize)
                                .idleBounce()
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = tileLetter.uppercase(),
                                    fontFamily = LexendFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextMidnight
                                )
                            }
                        }
                    }
                }
            }
        }

        CelebrationOverlay(
            type = CelebrationType.STAR_BURST,
            isPlaying = uiState is BlendItUiState.HeartDepleted,
            onFinished = { viewModel.restartSession() }
        )
    }
}
