package com.playit.app.presentation.hearit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.ErrorStateContent
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.LetterCard
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeLight
import com.playit.app.presentation.theme.UbeShadow

private val AUDIO_CTA_SIZE = 88.dp
private val AUDIO_CTA_RING_BOUNDS = 132.dp

@Composable
fun HearItScreen(
    viewModel: HearItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val phoneme by viewModel.phoneme.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isPlayingPrompt by viewModel.isPlayingPrompt.collectAsStateWithLifecycle()
    val playCount by viewModel.playCount.collectAsStateWithLifecycle()
    val loadError by viewModel.loadError.collectAsStateWithLifecycle()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"

    if (loadError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand))),
            contentAlignment = Alignment.Center
        ) {
            ErrorStateContent(
                message = "Oops! We couldn't load this sound.",
                onRetry = { viewModel.retry() }
            )
        }
        return
    }

    val cardRotation = remember(phoneme?.id) {
        val seed = phoneme?.id ?: 0
        ((seed * 37) % 5 - 2).toFloat()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseRing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Unlock-pop on transition from locked -> unlocked state
    val isUnlocked = playCount > 0
    var wasUnlocked by remember { mutableStateOf(isUnlocked) }
    val unlockScale = remember { Animatable(1f) }
    LaunchedEffect(isUnlocked) {
        if (isUnlocked && !wasUnlocked) {
            unlockScale.animateTo(
                targetValue = 1.12f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            unlockScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        wasUnlocked = isUnlocked
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Sky,
                        Sand
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 3-Segment Capsule Progress Top Bar
            LessonTopBar(
                currentStep = LessonStep.HEAR_IT,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot speech bubble prompt (Tapping Lily replays the lesson intro voiceover)
                MascotSpeechHeader(
                    message = if (isPlaying) {
                        "Sound: /${phoneme?.letter ?: "m"}/"
                    } else {
                        "Listen closely to the sound of the letter, then tap play."
                    },
                    mascotState = if (isPlaying) MascotState.LISTENING else if (playCount > 0) MascotState.POINTING else MascotState.IDLE,
                    isPlayingAudio = isPlayingPrompt,
                    onMascotTap = { viewModel.playHearItIntroAudio() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3D Bento Animated Letter Card with breathing pulse & 24sp floor
                LetterCard(
                    letter = targetLetter,
                    soundText = "Sound: /${phoneme?.letter ?: "m"}/",
                    cardRotation = cardRotation,
                    wordOverride = phoneme?.exampleWord,
                    onTapReplay = { if (!isPlaying) viewModel.playPhonemeSound() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsating 88dp Ube Speaker Replay Button
                Box(
                    modifier = Modifier.size(AUDIO_CTA_RING_BOUNDS),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(AUDIO_CTA_SIZE)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(UbeLight.copy(alpha = pulseAlpha))
                        )
                    }

                    GummyContainer(
                        onClick = if (isPlaying) null else ({ viewModel.playPhonemeSound() }),
                        enabled = !isPlaying,
                        faceColor = Ube,
                        shadowColor = UbeShadow,
                        shape = CircleShape,
                        strokeWidth = 3.dp,
                        strokeColor = DarkBrownOutline,
                        depthHeight = 5.dp,
                        modifier = Modifier.size(AUDIO_CTA_SIZE)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Playing" else "Play Sound",
                            tint = CreamWhite,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gummy Radial Gradient Replay Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 5) {
                        val filled = i < playCount
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (filled) {
                                             listOf(Ube, UbeShadow)
                                        } else {
                                            listOf(UbeLight.copy(alpha = 0.35f), UbeLight.copy(alpha = 0.15f))
                                        }
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = DarkBrownOutline.copy(alpha = if (filled) 0.4f else 0.15f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Pinned Clean Bottom Action Bar (Non-overlapping, 64dp floor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                GummyButton(
                    text = "Next: Say It",
                    onClick = {
                        if (isUnlocked) {
                            onNext(phoneme?.id?.toString() ?: "1")
                        }
                    },
                    enabled = isUnlocked,
                    backgroundColor = Mango,
                    shadowColor = MangoShadow,
                    contentColor = Ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .graphicsLayer {
                            scaleX = unlockScale.value
                            scaleY = unlockScale.value
                        }
                )
            }
        }
    }
}
