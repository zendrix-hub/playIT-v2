package com.playit.app.presentation.sayit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import android.util.Log
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playit.app.BuildConfig
import com.playit.app.domain.manager.SayItFeedbackCopy
import com.playit.app.domain.model.SpeechErrorType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.playit.app.presentation.components.ErrorStateContent
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LetterCard
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.shake
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.sayit.components.MicButton
import com.playit.app.presentation.theme.*

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
    val canContinue by viewModel.canContinue.collectAsStateWithLifecycle()
    val attempts by viewModel.attempts.collectAsStateWithLifecycle()
    val audioAmplitude by viewModel.audioAmplitude.collectAsStateWithLifecycle()
    val isNoisyEnvironment by viewModel.isNoisyEnvironment.collectAsStateWithLifecycle()
    val isPlayingPhoneme by viewModel.isPlayingPhoneme.collectAsStateWithLifecycle()
    val isPlayingPrompt by viewModel.isPlayingPrompt.collectAsStateWithLifecycle()
    val loadError by viewModel.loadError.collectAsStateWithLifecycle()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"
    val d = LocalPlayItDimens.current
    val targetWord by viewModel.targetWord.collectAsStateWithLifecycle()
    val wordMode = targetWord != null
    val displayWord = targetWord?.replaceFirstChar { it.uppercase() }
    var permissionDeniedMessage by remember { mutableStateOf(false) }

    val tutorAction by viewModel.tutorAction.collectAsStateWithLifecycle()
    val lastHeard by viewModel.lastHeard.collectAsStateWithLifecycle()

    if (BuildConfig.DEBUG) {
        LaunchedEffect(lastHeard) {
            lastHeard?.let {
                Log.d("PlayIT-SayIt", "Heard: \"${it.transcript}\" -> ${it.errorType} (attempt ${it.attempt})")
            }
        }
    }

    val currentFeedbackCopy = if ((state is SayItState.Correct || state is SayItState.Incorrect) && tutorAction != null) {
        val errorType = (state as? SayItState.Incorrect)?.errorType ?: SpeechErrorType.NONE
        SayItFeedbackCopy.forResult(
            action = tutorAction!!,
            errorType = errorType,
            wordMode = wordMode,
            word = targetWord
        )
    } else null

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

    // The mic takes taps only in Idle and Try again; listening ends on a result or the timeout.
    val startAttempt = {
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

    val micStatus by viewModel.micStatus.collectAsStateWithLifecycle()
    val showFeedback = state is SayItState.Correct || state is SayItState.Incorrect

    // MainActivity.onStop stops Vosk; drop the attempt here too so the mic is never stuck
    // in Listening when the child comes back (Review Focus 2).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.onScreenHidden()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onScreenHidden()
        }
    }

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
        LessonScaffold(
            topBar = { LessonTopBar(currentStep = LessonStep.SAY_IT, onBack = onBack, hearts = null) },
            header = {
                MascotSpeechHeader(
                    message = when {
                        permissionDeniedMessage -> "Please allow microphone access so Lily can hear you."
                        isModelInitializing -> "Lily is getting ready..."
                        isNoisyEnvironment -> "It's a little noisy right now. Let's find a quiet spot to practice!"
                        state is SayItState.Listening ->
                            if (wordMode) "Listening... Say $displayWord!" else "Listening... Say /${phoneme?.letter ?: "m"}/ into the microphone!"
                        currentFeedbackCopy != null -> currentFeedbackCopy.mascot
                        state is SayItState.Correct -> "Yes! You said it!"
                        state is SayItState.Incorrect -> "Good try! Listen again."
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
                    onMascotTap = { viewModel.playSayItIntroAudio() }
                )
            },
            bottomBar = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedVisibility(
                        visible = showFeedback,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { 20 })
                    ) {
                        val isCorrect = state is SayItState.Correct
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .background(color = if (isCorrect) EmeraldLeaf else ApricotGlow, shape = Squircle16)
                                    .border(2.5.dp, ModernBorder, Squircle16)
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentFeedbackCopy?.banner
                                        ?: if (isCorrect) "Great listening!" else "Listen again",
                                    color = if (isCorrect) Color.White else TextMidnight,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 24.sp,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (BuildConfig.DEBUG && lastHeard != null) {
                                Text(
                                    text = "Heard: \"${lastHeard?.transcript}\" -> ${lastHeard?.errorType} (attempt ${lastHeard?.attempt})",
                                    color = TextMidnight.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = LexendFontFamily,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    GummyButton(
                        text = "Next: Find It",
                        onClick = { if (canContinue) onNext(phoneme?.id?.toString() ?: "1") },
                        enabled = canContinue,
                        backgroundColor = EmeraldLeaf,
                        shadowColor = EmeraldLeafShadow,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
                    )
                }
            }
        ) {
            if (wordMode) {
                // Word prompt card: reuses the Hear It LetterCard (same picture_<word>.png
                // illustration, same gummy rendering). Tap replays the word audio.
                LetterCard(
                    letter = targetLetter,
                    soundText = "Say the word $displayWord",
                    wordOverride = displayWord,
                    promptMode = true,
                    showSpeakerIcon = true,
                    isPlaying = isPlayingPhoneme,
                    // Shorter than in Hear It: the mic, its label and the attempt dots sit below it.
                    modifier = Modifier.heightIn(max = d.letterCardHeight * 0.8f).shake(trigger = state is SayItState.Incorrect),
                    onTapReplay = { viewModel.playWordAudio() }
                )
            } else {
                // Legacy letter-sound card (ng/ñ SME-pending letters): pure phoneme audio
                GummyContainer(
                    onClick = { viewModel.playPhonemeSound() },
                    faceColor = SurfaceCard,
                    shadowColor = SurfaceCardShadow,
                    shape = CardShape,
                    strokeWidth = 2.5.dp,
                    strokeColor = ModernBorder,
                    depthHeight = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = d.letterCardHeight * 0.8f)
                        .shake(trigger = state is SayItState.Incorrect)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
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
                                maxLines = 1,
                                color = if (isPlayingPhoneme) SunnyGoldDark else PrimaryJoyDark
                            )
                        }
                        Text(
                            text = targetLetter,
                            fontFamily = LexendFontFamily,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            color = TextMidnight
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MicButton(
                    status = micStatus,
                    onTap = startAttempt,
                    enabled = !isPlayingPhoneme && !isModelInitializing,
                    labelOverride = if (isModelInitializing) "Lily is getting ready..." else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Attempt dots: a check for a correct try, a plain orange dot for a miss.
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
            }
        }
    }
}