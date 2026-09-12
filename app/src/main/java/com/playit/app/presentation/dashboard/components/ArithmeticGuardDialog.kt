package com.playit.app.presentation.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.domain.manager.ArithmeticGateManager
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun ArithmeticGuardDialog(
    gateManager: ArithmeticGateManager = remember { ArithmeticGateManager() },
    onPass: () -> Unit,
    onDismiss: () -> Unit,
    onCorrectSound: () -> Unit = {},
    onIncorrectSound: () -> Unit = {}
) {
    var problem by remember { mutableStateOf(gateManager.generateProblem()) }
    var answerInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = DialogShape,
            color = SurfaceCard,
            border = BorderStroke(2.5.dp, ModernBorder),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .offset(x = shakeOffset.value.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Lock Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryJoyLight)
                            .border(2.dp, PrimaryJoy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Parent Lock",
                            tint = PrimaryJoy,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Parent Zone",
                        fontFamily = LexendFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Grown-ups only. Solve: ${problem.displayExpression} = ?",
                        fontFamily = LexendFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PIN / Answer Display Box
                    Surface(
                        shape = Squircle16,
                        color = if (isError) CoralBerryLight else CanvasLight,
                        border = BorderStroke(2.dp, if (isError) CoralBerry else ModernBorderSoft),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (answerInput.isEmpty()) "--" else answerInput,
                                fontFamily = LexendFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 4.sp,
                                color = if (isError) CoralBerryDark else PrimaryJoy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3x4 Keypad Grid
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "DEL")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        keys.forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { key ->
                                    when (key) {
                                        "C" -> {
                                            KeypadClearButton(
                                                onClick = {
                                                    answerInput = ""
                                                    isError = false
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        "DEL" -> {
                                            KeypadDeleteButton(
                                                onClick = {
                                                    if (answerInput.isNotEmpty()) {
                                                        answerInput = answerInput.dropLast(1)
                                                        isError = false
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        else -> {
                                            KeypadNumberButton(
                                                number = key,
                                                onClick = {
                                                    if (answerInput.length < 4) {
                                                        answerInput += key
                                                        isError = false
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Action Button
                    GummyButton(
                        text = "Verify & Enter",
                        backgroundColor = EmeraldLeaf,
                        shadowColor = EmeraldLeafShadow,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        onClick = {
                            if (gateManager.validateAnswer(problem, answerInput)) {
                                onCorrectSound()
                                onPass()
                            } else {
                                onIncorrectSound()
                                isError = true
                                coroutineScope.launch {
                                    shakeOffset.animateTo(8f, tween(45))
                                    shakeOffset.animateTo(-8f, tween(90))
                                    shakeOffset.animateTo(5f, tween(90))
                                    shakeOffset.animateTo(-5f, tween(90))
                                    shakeOffset.animateTo(0f, tween(60))
                                }
                                problem = gateManager.generateProblem()
                                answerInput = ""
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            fontFamily = LexendFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadNumberButton(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GummyContainer(
        onClick = onClick,
        faceColor = CanvasLight,
        shadowColor = SurfaceCardShadow,
        shape = Squircle12,
        strokeWidth = 1.5.dp,
        strokeColor = ModernBorderSoft,
        depthHeight = 3.dp,
        modifier = modifier.height(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = number,
                fontFamily = LexendFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMidnight
            )
        }
    }
}

@Composable
private fun KeypadDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GummyContainer(
        onClick = onClick,
        faceColor = CoralBerryLight,
        shadowColor = SurfaceCardShadow,
        shape = Squircle12,
        strokeWidth = 1.5.dp,
        strokeColor = CoralBerry.copy(alpha = 0.4f),
        depthHeight = 3.dp,
        modifier = modifier.height(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = CoralBerryDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun KeypadClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GummyContainer(
        onClick = onClick,
        faceColor = CanvasLight,
        shadowColor = SurfaceCardShadow,
        shape = Squircle12,
        strokeWidth = 1.5.dp,
        strokeColor = ModernBorderSoft,
        depthHeight = 3.dp,
        modifier = modifier.height(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "C",
                fontFamily = LexendFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted
            )
        }
    }
}
