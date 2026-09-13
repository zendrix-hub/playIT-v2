package com.playit.app.presentation.sayit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.playit.app.presentation.components.AudioWaveformBar
import com.playit.app.presentation.components.ErrorStateContent
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LetterCard
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.*

private val MIC_CTA_SIZE = 88.dp
private val MIC_CTA_RING_BOUNDS = 180.dp

@Composable
fun SayItScreen(
    viewModel: SayItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val phoneme by viewModel.phoneme.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isModelInitializing by viewModel.isModelInitializing.collectAsStateWithLifecycle()
    val hearts by viewModel.hearts.collectAsStateWithLifecycle()
    val attempts by viewModel.attempts.collectAsStateWithLifecycle()
    val audioAmplitude by viewModel.audioAmplitude.collectAsStateWithLifecycle()
    val isNoisyEnvironment by viewModel.isNoisyEnvironment.collectAsStateWithLifecycle()
    val isPlayingPhoneme by viewModel.isPlayingPhoneme.collectAsStateWithLifecycle()
    val isPlayingPrompt by viewModel.isPlayingPrompt.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val loadError by viewModel.loadError.collectAsStateWithLifecycle()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"
    val targetWord by viewModel.targetWord.collectAsStateWithLifecycle()
    val wordMode = targetWord != null
    val displayWord = targetWord?.replaceFirstChar { it.uppercase() }
    val isListening = state is SayItState.Listening
    var permissionDeniedMessage by remember { mutableStateOf(false) }

    if (loadError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand))),
            contentAlignment = Alignment.Center
        ) {
            ErrorStateContent(
                message = "Oops! We couldn't load this lesson.",
                onRetry = { viewModel.retry() }
            )
        }
        return
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = false
            viewModel.startListening()
        } else {
            permissionDeniedMessage = true
        }
    }

    val toggleListening = {
        if (isListening) {
            viewModel.stopListening()
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                permissionDeniedMessage = false
                viewModel.startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "micPulseScale"
    )
    val micPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "micPulseAlpha"
    )

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
            LessonTopBar(currentStep = LessonStep.SAY_IT, onBack = onBack, hearts = hearts)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MascotSpeechHeader(
                    message = when {
                        permissionDeniedMessage -> "Please allow microphone access so Lily can hear you."
                        isModelInitializing -> "Lily is getting ready..."
                        isNoisyEnvironment -> "It's a little noisy right now. Let's find a quiet spot to practice!"
                        state is SayItState.Listening ->
                            if (wordMode) "Listening... Say $displayWord!" else "Listening... Say /${phoneme?.letter ?: "m"}/ into the microphone!"
                        state is SayItState.Correct -> "Yes! That's it! Great job!"
                        state is SayItState.Incorrect -> "Good try! Let's listen again."
                        wordMode -> "Now it's your turn! Say the whole word clearly into the microphone!"
                        else -> "Now it's your turn. Say the sound clearly into the microphone!"
                    },
                    mascotState = when {
                        permissionDeniedMessage -> MascotState.ENCOURAGING
                        isModelInitializing -> MascotState.THINKING
                        isNoisyEnvironment -> MascotState.THINKING
                        state is SayItState.Listening -> MascotState.LISTENING
                        state is SayItState.Correct -> MascotState.CELEBRATING
                        state is SayItState.Incorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    isPlayingAudio = isPlayingPrompt,
                    amplitude = audioAmplitude,
                    onMascotTap = { if (!isAudioPlaying) viewModel.playSayItIntroAudio() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (wordMode) {
                    // Word prompt card — reuses the exact Hear It LetterCard (same
                    // picture_<word>.png illustration, same gummy rendering), so the
                    // image that shows in Hear It shows here too. Tap replays the word
                    // audio.
                    LetterCard(
                        letter = targetLetter,
                        soundText = "Say the word $displayWord",
                        wordOverride = displayWord,
                        promptMode = true,
                        showSpeakerIcon = true,
                        isPlaying = isPlayingPhoneme,
                        modifier = Modifier.shake(trigger = state is SayItState.Incorrect),
                        onTapReplay = { if (!isAudioPlaying && !isListening) viewModel.playWordAudio() }
                    )
                } else {
                    // Legacy letter-sound card (ng/ñ SME-pending letters) — pure phoneme audio
                    GummyContainer(
                        onClick = { if (!isAudioPlaying && !isListening) viewModel.playPhonemeSound() },
                        enabled = !isAudioPlaying && !isListening,
                        faceColor = SurfaceCard,
                        shadowColor = SurfaceCardShadow,
                        shape = CardShape,
                        strokeWidth = 2.5.dp,
                        strokeColor = ModernBorder,
                        depthHeight = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(116.dp)
                            .padding(horizontal = 16.dp)
                            .shake(trigger = state is SayItState.Incorrect)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "Hear Sound",
                                    tint = if (isPlayingPhoneme) SunnyGold else PrimaryJoyDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Sound: /${phoneme?.letter ?: "m"}/",
                                    fontFamily = LexendFontFamily,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isPlayingPhoneme) SunnyGoldDark else PrimaryJoyDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = targetLetter,
                                fontFamily = LexendFontFamily,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = TextMidnight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AudioWaveformBar(isRecording = isListening, activeColor = CoralBerry)

                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.size(MIC_CTA_RING_BOUNDS), contentAlignment = Alignment.Center) {
                    if (isListening) {
                        val dynamicBoost = 1f + (audioAmplitude * 0.35f)
                        Box(
                            modifier = Modifier
                                .size(MIC_CTA_SIZE * dynamicBoost)
                                .scale(micPulseScale)
                                .clip(CircleShape)
                                .background(CoralBerry.copy(alpha = micPulseAlpha))
                        )
                    }

                    val isMicEnabled = !isAudioPlaying && !isModelInitializing && state !is SayItState.Correct
                    GummyContainer(
                        onClick = if (isMicEnabled) toggleListening else null,
                        enabled = isMicEnabled,
                        faceColor = when {
                            isModelInitializing -> CanvasLight
                            isListening -> CoralBerry
                            else -> SunnyGold
                        },
                        shadowColor = when {
                            isModelInitializing -> SurfaceCardShadow
                            isListening -> CoralBerryDark
                            else -> SunnyGoldShadow
                        },
                        shape = CircleShape,
                        strokeWidth = 2.5.dp,
                        strokeColor = ModernBorder,
                        depthHeight = 6.dp,
                        modifier = Modifier.size(MIC_CTA_SIZE)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Record Voice",
                            tint = if (isModelInitializing) TextMuted else Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when {
                        isModelInitializing -> "Lily is getting ready..."
                        isListening -> if (wordMode) "Listening... Say $displayWord!" else "Listening... Say the sound!"
                        else -> "Tap to speak"
                    },
                    fontFamily = LexendFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isListening -> CoralBerry
                        else -> TextMuted
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val maxAttempts = 3
                    for (i in 0 until maxAttempts) {
                        if (i < attempts.size) {
                            val isAttemptOk = attempts[i]
                            Box(
                                modifier = Modifier.size(22.dp).clip(CircleShape).background(if (isAttemptOk) EmeraldLeaf else ApricotGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAttemptOk) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Correct attempt",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(ModernBorderSoft.copy(alpha = 0.5f))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .background(SurfaceCard, PillShape)
                        .border(1.5.dp, ModernBorderSoft, PillShape)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isNoisyEnvironment) ApricotGlow else EmeraldLeaf))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isNoisyEnvironment) "Noise: High" else "Noise: Good",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = state is SayItState.Correct || state is SayItState.Incorrect,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { 20 })
                ) {
                    val isCorrect = state is SayItState.Correct
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .background(color = if (isCorrect) EmeraldLeaf else ApricotGlow, shape = Squircle16)
                            .border(2.5.dp, ModernBorder, Squircle16)
                            .padding(vertical = 10.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCorrect) "Awesome pronunciation!" else "Good try! Let's try again.",
                            color = if (isCorrect) Color.White else TextMidnight,
                            fontWeight = FontWeight.Black,
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 12.dp)) {
                val isNextEnabled = state is SayItState.Correct && !isAudioPlaying
                GummyButton(
                    text = "Next: Find It",
                    onClick = { if (isNextEnabled) onNext(phoneme?.id?.toString() ?: "1") },
                    enabled = isNextEnabled,
                    backgroundColor = EmeraldLeaf,
                    shadowColor = EmeraldLeafShadow,
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .graphicsLayer {
                            alpha = if (isNextEnabled) 1f else 0.5f
                        }
                )
            }
        }
    }
}