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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playit.app.domain.manager.ArithmeticGateManager
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.SkyShadow
import com.playit.app.presentation.theme.Ube
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
            shape = RoundedCornerShape(28.dp),
            color = Cloud,
            border = BorderStroke(3.dp, DarkBrownOutline),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .offset(x = shakeOffset.value.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Lock Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Sky)
                        .border(3.dp, DarkBrownOutline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Parent Lock",
                        tint = Ube,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Parent Zone",
                    fontFamily = LexendFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Grown-ups only. Solve: ${problem.displayExpression} = ?",
                    fontFamily = LexendFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkSoft
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN / Answer Display Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isError) Kalamansi.copy(alpha = 0.15f) else Sky,
                    border = BorderStroke(3.dp, if (isError) Kalamansi else DarkBrownOutline),
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
                            color = if (isError) Kalamansi else Ube
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3x3 + 1 Keypad Grid
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
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
                                if (key.isEmpty()) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else if (key == "DEL") {
                                    KeypadDeleteButton(
                                        onClick = {
                                            if (answerInput.isNotEmpty()) {
                                                answerInput = answerInput.dropLast(1)
                                                isError = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Action Button (explicit 52dp adult height)
                GummyButton(
                    text = "Verify & Enter",
                    backgroundColor = Leaf,
                    shadowColor = LeafShadow,
                    contentColor = Cloud,
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
                                // Decaying gentle wobble (8 -> -8 -> 5 -> -5 -> 0)
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
                        color = InkSoft
                    )
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
        faceColor = Sky,
        shadowColor = SkyShadow,
        shape = RoundedCornerShape(14.dp),
        strokeWidth = 2.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 4.dp,
        modifier = modifier.height(52.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = number,
                fontFamily = LexendFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink
            )
        }
    }
}

@Composable
private fun KeypadDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Neutral Sky treatment rather than permanent Kalamansi amber
    GummyContainer(
        onClick = onClick,
        faceColor = Sky,
        shadowColor = SkyShadow,
        shape = RoundedCornerShape(14.dp),
        strokeWidth = 2.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 4.dp,
        modifier = modifier.height(52.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Delete",
                tint = Ink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
