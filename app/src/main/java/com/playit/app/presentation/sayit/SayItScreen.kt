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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.playit.app.presentation.components.AudioWaveformBar
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.CloudShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.GuavaShadow
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.UbeDark

private val MIC_CTA_SIZE = 88.dp
private val MIC_CTA_RING_BOUNDS = 180.dp

@Composable
fun SayItScreen(
    viewModel: SayItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val phoneme by viewModel.phoneme.collectAsState()
    val state by viewModel.state.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val attempts by viewModel.attempts.collectAsState()
    val audioAmplitude by viewModel.audioAmplitude.collectAsState()
    val isNoisyEnvironment by viewModel.isNoisyEnvironment.collectAsState()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"
    val isListening = state is SayItState.Listening
    var permissionDeniedMessage by remember { mutableStateOf(false) }

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
            .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand)))
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
                        permissionDeniedMessage -> "Kailangan ng mikropono para marinig ka ni Lily! • Please allow microphone permission."
                        isNoisyEnvironment -> "Medyo maingay! Humanap ng tahimik na lugar. • Find a quiet spot so Lily can hear you."
                        state is SayItState.Listening -> "Nakikinig nang mabuti... Bigkasin ang /${phoneme?.letter ?: "m"}/! • Listening closely... Say /${phoneme?.letter ?: "m"}/!"
                        state is SayItState.Correct -> "Napakagaling! Tama ang iyong pagbigkas! • Awesome pronunciation! You got it right!"
                        state is SayItState.Incorrect -> "Magandang subok! Makinig muli at subukan pa. • Good try! Listen again and retry."
                        else -> "Ikaw naman — bigkasin ang tunog /${phoneme?.letter ?: "m"}/! • Your turn — say the sound /${phoneme?.letter ?: "m"}/!"
                    },
                    mascotState = when {
                        permissionDeniedMessage -> MascotState.ENCOURAGING
                        isNoisyEnvironment -> MascotState.THINKING
                        state is SayItState.Listening -> MascotState.LISTENING
                        state is SayItState.Correct -> MascotState.CELEBRATING
                        state is SayItState.Incorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    onMascotTap = { viewModel.playPhonemeSound() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                GummyContainer(
                    onClick = toggleListening,
                    faceColor = Cloud,
                    shadowColor = CloudShadow,
                    shape = RoundedCornerShape(24.dp),
                    strokeWidth = 3.dp,
                    strokeColor = DarkBrownOutline,
                    depthHeight = 5.dp,
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
                        Text(
                            text = "Bigkasin • Say: /${phoneme?.letter ?: "m"}/",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UbeDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = targetLetter,
                            fontFamily = LexendFontFamily,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AudioWaveformBar(isRecording = isListening, activeColor = Guava)

                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.size(MIC_CTA_RING_BOUNDS), contentAlignment = Alignment.Center) {
                    if (isListening) {
                        val dynamicBoost = 1f + (audioAmplitude * 0.35f)
                        Box(
                            modifier = Modifier
                                .size(MIC_CTA_SIZE)
                                .scale(micPulseScale * dynamicBoost)
                                .clip(CircleShape)
                                .background(Guava.copy(alpha = micPulseAlpha))
                        )
                    }

                    GummyContainer(
                        onClick = toggleListening,
                        faceColor = if (isListening) Guava else Mango,
                        shadowColor = if (isListening) GuavaShadow else MangoShadow,
                        shape = CircleShape,
                        strokeWidth = 3.dp,
                        strokeColor = DarkBrownOutline,
                        depthHeight = 5.dp,
                        modifier = Modifier.size(MIC_CTA_SIZE)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Record Voice",
                            tint = Cloud,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isListening) "Nakikinig... Sabihin ang tunog!" else "Pindutin para magsalita",
                    fontFamily = LexendFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) Guava else InkSoft
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val maxAttempts = 3
                    for (i in 0 until maxAttempts) {
                        if (i < attempts.size) {
                            val isAttemptOk = attempts[i]
                            Box(
                                modifier = Modifier.size(22.dp).clip(CircleShape).background(if (isAttemptOk) Leaf else Kalamansi),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAttemptOk) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Correct attempt",
                                        tint = Cloud,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE4E9E7))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .background(Cloud, RoundedCornerShape(999.dp))
                        .border(1.5.dp, DarkBrownOutline.copy(alpha = 0.2f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isNoisyEnvironment) Kalamansi else Leaf))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isNoisyEnvironment) "Ingay sa Paligid: Mataas • Noise: High" else "Ingay sa Paligid: Maayos • Noise: Good",
                        color = InkSoft,
                        fontSize = 11.5.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (com.playit.app.BuildConfig.DEBUG) {
                    Text(
                        text = "Tap here to simulate correct voice (DEBUG)",
                        fontFamily = LexendFontFamily,
                        fontSize = 12.sp,
                        color = InkSoft,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable { viewModel.simulateCorrectForTesting() }
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                            .background(color = if (isCorrect) Leaf else Kalamansi, shape = RoundedCornerShape(16.dp))
                            .border(2.5.dp, DarkBrownOutline, RoundedCornerShape(16.dp))
                            .padding(vertical = 10.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCorrect) "Napakagaling! • Awesome pronunciation!" else "Magandang subok! Subukan muli. • Good try! Let's try again.",
                            color = if (isCorrect) Cloud else Ink,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 12.dp)) {
                GummyButton(
                    text = "Susunod: Hanapin • Next: Find It",
                    onClick = { if (state is SayItState.Correct) onNext(phoneme?.id?.toString() ?: "1") },
                    enabled = state is SayItState.Correct,
                    backgroundColor = Mango,
                    shadowColor = MangoShadow,
                    contentColor = Ink,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }
}