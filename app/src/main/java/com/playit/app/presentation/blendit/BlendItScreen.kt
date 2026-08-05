package com.playit.app.presentation.blendit

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.HeartBar
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
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

    val wordEmojis = mapOf(
        "sam" to "👦", "sis" to "👧", "aim" to "🎯", "mat" to "🫐", "sit" to "🪑"
    )
    val currentEmoji = wordEmojis[currentWord?.word?.lowercase()] ?: "🧩"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SoftSky,
                        CreamWhite
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(CreamWhite, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Text(text = "⬅️", fontSize = 22.sp)
                }

                Text(
                    text = "Blend ${viewModel.groupId} (${currentIndex + 1}/${words.size})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                // 5-Heart Status Bar
                HeartBar(currentHearts = hearts, maxHearts = 5)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mascot Prompt Header
            MascotBubble(
                message = "Tap the letter tiles below to build the word!",
                mascotEmoji = "🧩"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Word Picture Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .shadow(8.dp, RoundedCornerShape(32.dp), spotColor = FriendlyPurple),
                shape = RoundedCornerShape(32.dp),
                color = CreamWhite,
                border = androidx.compose.foundation.BorderStroke(3.dp, SoftSky)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = currentEmoji, fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Build the word sound by sound!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Constructed Word Slot Row
            Row(
                modifier = Modifier.shake(trigger = uiState is BlendItUiState.WordIncorrect),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val targetLength = currentWord?.word?.length ?: 3
                for (i in 0 until targetLength) {
                    val tileChar = placedTiles.getOrNull(i)
                    Surface(
                        modifier = Modifier
                            .size(68.dp)
                            .bounceClick(enabled = tileChar != null) {
                                viewModel.removeTile(i)
                            }
                            .shadow(if (tileChar != null) 6.dp else 1.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        color = if (tileChar != null) FriendlyPurple else SoftSky.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.5.dp,
                            color = if (tileChar != null) CreamWhite else FriendlyPurple.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tileChar?.uppercase() ?: "_",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (tileChar != null) CreamWhite else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tile Bank Row
            Text(
                text = "Letter Bank:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tileBank.forEach { char ->
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .bounceClick { viewModel.placeTile(char) }
                            .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = LearningBlue),
                        shape = RoundedCornerShape(18.dp),
                        color = LearningBlue,
                        border = androidx.compose.foundation.BorderStroke(2.dp, CreamWhite)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char.uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CreamWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Hint Button (Active after 2 wrong attempts)
            if (wrongAttempts >= 2) {
                PediatricButton(
                    text = "💡 Tap for Hint",
                    onClick = { viewModel.openHintModal() },
                    backgroundColor = AchievementGold,
                    contentColor = TextPrimary,
                    fontSize = 17
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Check Answer Button
            PediatricButton(
                text = "Check Word ✨",
                onClick = { viewModel.submitWord() },
                enabled = placedTiles.size == (currentWord?.word?.length ?: 3),
                backgroundColor = GrowthGreen,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Hint Modal Dialog
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

        // 3-Heart Depletion Restart Dialog
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
                    PediatricButton(
                        text = "Restart with ❤️❤️❤️",
                        onClick = { viewModel.restartSession() },
                        backgroundColor = GrowthGreen,
                        fontSize = 18
                    )
                },
                containerColor = CreamWhite,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

