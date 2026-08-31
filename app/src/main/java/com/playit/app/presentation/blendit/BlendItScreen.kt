package com.playit.app.presentation.blendit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.BlendItCard
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.CloudShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.SandShadow
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.UbeDark
import com.playit.app.presentation.theme.UbeLight
import kotlinx.coroutines.delay

@Composable
fun BlendItScreen(
    viewModel: BlendItViewModel,
    onSessionComplete: (Int) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val words by viewModel.words.collectAsStateWithLifecycle()
    val currentWord by viewModel.currentWord.collectAsStateWithLifecycle()
    val tileBank by viewModel.tileBank.collectAsStateWithLifecycle()
    val placedTiles by viewModel.placedTiles.collectAsStateWithLifecycle()
    val hearts by viewModel.hearts.collectAsStateWithLifecycle()
    val currentWordIndex by viewModel.currentWordIndex.collectAsStateWithLifecycle()
    val totalWords = words.size.coerceAtLeast(1)

    // Fires onSessionComplete once all words are completed or SessionComplete is emitted
    LaunchedEffect(uiState, currentWordIndex) {
        if (uiState is BlendItUiState.SessionComplete || 
            (uiState is BlendItUiState.WordCorrect && currentWordIndex >= totalWords - 1)) {
            delay(1200L) // allow completion chime and celebration animation to play
            onSessionComplete(viewModel.groupId)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LessonTopBar(
                currentStep = LessonStep.BLEND_IT,
                onBack = onBack,
                hearts = hearts
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    onMascotTap = { viewModel.playTargetWordAudio() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Word progress pill (24sp child reading floor)
                Text(
                    text = "Word: ${currentWordIndex + 1} / $totalWords",
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = UbeDark,
                    modifier = Modifier
                        .background(color = UbeLight, shape = RoundedCornerShape(999.dp))
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Illustrated Target Word Card
                currentWord?.let { wordItem ->
                    BlendItCard(
                        word = wordItem.word,
                        isCorrect = uiState is BlendItUiState.WordCorrect,
                        onReplayAudio = { viewModel.playTargetWordAudio() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Letter Slots (Target Drop Area) with gentle wobble on incorrect
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.graphicsLayer { rotationZ = wobble.value }
                ) {
                    val wordLength = currentWord?.word?.length ?: 3
                    for (i in 0 until wordLength) {
                        val tile = placedTiles.getOrNull(i)
                        GummyContainer(
                            onClick = { if (tile != null) viewModel.removeTile(i) },
                            faceColor = if (tile != null) Cloud else Sand.copy(alpha = 0.5f),
                            shadowColor = if (tile != null) CloudShadow else SandShadow,
                            shape = RoundedCornerShape(16.dp),
                            strokeWidth = 2.5.dp,
                            strokeColor = when (uiState) {
                                is BlendItUiState.WordCorrect -> Leaf
                                is BlendItUiState.WordIncorrect -> Kalamansi
                                else -> DarkBrownOutline
                            },
                            depthHeight = 4.dp,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = tile?.uppercase() ?: "_",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (tile != null) Ink else InkSoft.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Available Tile Bank with idle interaction bounce
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tileBank.forEachIndexed { _, tileLetter ->
                        GummyContainer(
                            onClick = {
                                viewModel.placeTile(tileLetter)
                            },
                            faceColor = Mango,
                            shadowColor = MangoShadow,
                            shape = RoundedCornerShape(16.dp),
                            strokeWidth = 2.5.dp,
                            strokeColor = DarkBrownOutline,
                            depthHeight = 4.dp,
                            modifier = Modifier
                                .size(68.dp)
                                .idleBounce()
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = tileLetter.uppercase(),
                                    fontFamily = LexendFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Ink
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Pinned Bottom Action Button (64dp floor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                val isReady = placedTiles.size == (currentWord?.word?.length ?: 3)
                GummyContainer(
                    onClick = {
                        if (isReady) {
                            viewModel.submitWord()
                        }
                    },
                    faceColor = if (uiState is BlendItUiState.WordCorrect) Leaf else Mango,
                    shadowColor = if (uiState is BlendItUiState.WordCorrect) LeafShadow else MangoShadow,
                    shape = RoundedCornerShape(18.dp),
                    strokeWidth = 3.dp,
                    strokeColor = DarkBrownOutline,
                    depthHeight = 5.dp,
                    isSquashed = uiState is BlendItUiState.WordCorrect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
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
                            text = "Check Word",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState is BlendItUiState.WordCorrect) Cloud else Ink
                        )
                    }
                }
            }
        }
    }
}
