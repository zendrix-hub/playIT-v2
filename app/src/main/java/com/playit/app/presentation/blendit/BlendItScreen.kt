package com.playit.app.presentation.blendit

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun BlendItScreen(
    viewModel: BlendItViewModel,
    onSessionComplete: (Int) -> Unit,
    onBack: () -> Unit
) {
    val words by viewModel.words.collectAsState()
    val currentIndex by viewModel.currentWordIndex.collectAsState()
    val currentWord by viewModel.currentWord.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val placedTiles by viewModel.placedTiles.collectAsState()
    val tileBank by viewModel.tileBank.collectAsState()
    val wrongAttempts by viewModel.wrongAttemptsForCurrentWord.collectAsState()
    val isHintVisible by viewModel.isHintModalVisible.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is BlendItUiState.SessionComplete) {
            onSessionComplete(viewModel.groupId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftSky)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }
                Text(
                    text = "Blend It — Group ${viewModel.groupId} (${currentIndex + 1}/${words.size})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "❤️ ".repeat(hearts),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Word Picture Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🖼️", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Construct the word!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Constructed Word Slot Row
            Row(
                modifier = Modifier.shake(trigger = uiState is BlendItUiState.WordIncorrect),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val targetLength = currentWord?.word?.length ?: 3
                for (i in 0 until targetLength) {
                    val tileChar = placedTiles.getOrNull(i)
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = if (tileChar != null) FriendlyPurple else CreamWhite,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(2.dp, FriendlyPurple, RoundedCornerShape(16.dp))
                            .bounceClick(enabled = tileChar != null) {
                                viewModel.removeTile(i)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tileChar?.uppercase() ?: "_",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tileChar != null) CreamWhite else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tile Bank Row
            Text(
                text = "Tap letters to build:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tileBank.forEach { char ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(LearningBlue, RoundedCornerShape(16.dp))
                            .bounceClick { viewModel.placeTile(char) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CreamWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Constraint #2: Interactive Pop-up Modal Button for Hint (Active after 2 wrong attempts)
            if (wrongAttempts >= 2) {
                Button(
                    onClick = { viewModel.openHintModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = AchievementGold),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "💡 Tap for Hint",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Check Answer Button
            Button(
                onClick = { viewModel.submitWord() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = placedTiles.size == (currentWord?.word?.length ?: 3),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GrowthGreen)
            ) {
                Text(
                    text = "Check Word ✨",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }

        // Constraint #2: Interactive Pop-up Modal Dialog
        if (isHintVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.closeHintModal() },
                title = {
                    Text(
                        text = "💡 Word Hint",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "The word starts with '${currentWord?.word?.firstOrNull()?.uppercase() ?: "S"}' and has ${currentWord?.word?.length ?: 3} letters!",
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.closeHintModal() }) {
                        Text("Got it!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LearningBlue)
                    }
                },
                containerColor = CreamWhite,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Constraint #1: Standard 3-Heart Depletion Restart Dialog
        if (uiState is BlendItUiState.HeartDepleted) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = "💔 Out of Hearts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GentleCorrectionOrange
                    )
                },
                text = {
                    Text(
                        text = "Don't worry! Practice makes progress. Tap below to restart with 3 full hearts!",
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.restartSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = GrowthGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Restart with ❤️❤️❤️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CreamWhite
                        )
                    }
                },
                containerColor = CreamWhite,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
