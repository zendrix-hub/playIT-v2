package com.playit.app.presentation.hearit

import com.playit.app.presentation.components.ArticulationCue
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.resetsIdle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.ErrorStateContent
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.LetterCard
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.LocalPlayItDimens
import com.playit.app.presentation.theme.WindowProfile
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeLight
import com.playit.app.presentation.theme.UbeShadow

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
    val nextHighlighted by viewModel.nextHighlighted.collectAsStateWithLifecycle()
    val loadError by viewModel.loadError.collectAsStateWithLifecycle()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"
    val d = LocalPlayItDimens.current

    DisposableEffect(Unit) {
        viewModel.onScreenVisible()
        onDispose {
            viewModel.onScreenHidden()
        }
    }

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
    val isUnlocked = nextHighlighted
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
            .resetsIdle { viewModel.onUserInteraction() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFFE8F0FE),
                        androidx.compose.ui.graphics.Color(0xFFF3E8FF),
                        androidx.compose.ui.graphics.Color(0xFFFEF3C7)
                    )
                )
            )
    ) {
        LessonScaffold(
            topBar = { LessonTopBar(currentStep = LessonStep.HEAR_IT, onBack = onBack) },
            header = {
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
            },
            bottomBar = {
                GummyButton(
                    text = "Next: Say It",
                    onClick = {
                        if (isUnlocked) {
                            onNext(phoneme?.id?.toString() ?: "1")
                        }
                    },
                    enabled = isUnlocked,
                    backgroundColor = com.playit.app.presentation.theme.SunnyGold,
                    shadowColor = com.playit.app.presentation.theme.SunnyGoldShadow,
                    contentColor = com.playit.app.presentation.theme.TextMidnight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .breathingPulse(enabled = nextHighlighted)
                        .graphicsLayer {
                            scaleX = unlockScale.value
                            scaleY = unlockScale.value
                        }
                )
            }
        ) {
            // 3D Bento Animated Letter Card with breathing pulse & 24sp floor. 1.2x tall where the
            // window has room; on COMPACT the play button would leave the window (card 28).
            LetterCard(
                letter = targetLetter,
                soundText = "Sound: /${phoneme?.letter ?: "m"}/",
                cardRotation = cardRotation,
                wordOverride = phoneme?.exampleWord,
                onTapReplay = { if (!isPlaying) viewModel.playPhonemeSound() },
                height = if (d.profile == WindowProfile.COMPACT) d.letterCardHeight else d.letterCardHeight * 1.2f
            )

            // Mouth-shape cue (NFR-ACC-01). No caption in the child view: pre-readers cannot read the
            // carrier sentences (user decision 2026-10-10, card 28). The row keeps its height, so the
            // play button doesn't jump when the cue's picture loads.
            Row(
                modifier = Modifier.heightIn(min = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArticulationCue(group = viewModel.articulation, size = 72.dp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Pulsating PrimaryJoy Speaker Replay Button; the ring draws outside its bounds
                Box(contentAlignment = Alignment.Center) {
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(d.primaryCta)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(com.playit.app.presentation.theme.PrimaryJoyLight.copy(alpha = pulseAlpha))
                        )
                    }

                    GummyContainer(
                        onClick = if (isPlaying) null else ({ viewModel.playPhonemeSound() }),
                        enabled = !isPlaying,
                        faceColor = com.playit.app.presentation.theme.PrimaryJoy,
                        shadowColor = com.playit.app.presentation.theme.PrimaryJoyDark,
                        shape = CircleShape,
                        strokeWidth = 2.5.dp,
                        strokeColor = com.playit.app.presentation.theme.ModernBorder,
                        depthHeight = 6.dp,
                        modifier = Modifier.size(d.primaryCta).testTag("hearit_play")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Playing" else "Play Sound",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(d.primaryCta * 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                                             listOf(com.playit.app.presentation.theme.PrimaryJoy, com.playit.app.presentation.theme.PrimaryJoyDark)
                                        } else {
                                            listOf(com.playit.app.presentation.theme.PrimaryJoyLight.copy(alpha = 0.35f), com.playit.app.presentation.theme.PrimaryJoyLight.copy(alpha = 0.15f))
                                        }
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = com.playit.app.presentation.theme.ModernBorder.copy(alpha = if (filled) 0.4f else 0.15f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}
