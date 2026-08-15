package com.playit.app.presentation.blendit

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.BlendItCard
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.HeartBar
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.AchievementGoldShadow
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.FriendlyPurpleShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.GrowthGreenShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
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

    // Dynamic mascot prompt message & state
    val (mascotMessage, mascotState) = remember(uiState, wrongAttempts, isHintVisible, currentWord) {
        when {
            uiState is BlendItUiState.WordCorrect -> 
                "Awesome job! You blended '${currentWord?.word}'! 🎉" to MascotState.CELEBRATING
            uiState is BlendItUiState.WordIncorrect -> 
                "Oops! Try again! Touch a tile to move it back. 💡" to MascotState.ENCOURAGING
            isHintVisible -> 
                "Here is a helpful clue for you! 🧠" to MascotState.THINKING
            wrongAttempts >= 2 -> 
                "Stuck? Tap the yellow hint button below! 💡" to MascotState.POINTING
            else -> 
                "Tap the letter tiles below to build the word!" to MascotState.ENCOURAGING
        }
    }

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
            // Header Bar with Gummy back button and 5-Heart Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GummyContainer(
                    onClick = onBack,
                    faceColor = CreamWhite,
                    shadowColor = SoftSky,
                    shape = CircleShape,
                    strokeWidth = 3.dp,
                    strokeColor = TextPrimary,
                    depthHeight = 4.dp,
                    modifier = Modifier.size(52.dp)
                ) {
                    Text(text = "⬅️", fontSize = 22.sp)
                }

                Text(
                    text = "Blend ${viewModel.groupId} (${currentIndex + 1}/${words.size})",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                HeartBar(currentHearts = hearts, maxHearts = 5)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contextual Mascot Companion Speech Bubble
            MascotBubble(
                message = mascotMessage,
                mascotState = mascotState
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Word Picture Scene Card (with click-to-replay & bouncy success animation)
            BlendItCard(
                word = currentWord?.word ?: "sam",
                isCorrect = uiState is BlendItUiState.WordCorrect,
                onReplayAudio = { viewModel.playTargetWordAudio() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Constructed Word Slot Row (Gummy letter slots with 6dp depth band, 68dp touch targets & static rotation)
            Row(
                modifier = Modifier.shake(trigger = uiState is BlendItUiState.WordIncorrect),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val targetLength = currentWord?.word?.length ?: 3
                for (i in 0 until targetLength) {
                    val tileChar = placedTiles.getOrNull(i)
                    val slotRotation = remember(i) { ((i * 17) % 5 - 2).toFloat() }

                    if (tileChar != null) {
                        // Placed Tile with 6dp depth band and 68dp touch target
                        GummyContainer(
                            onClick = { viewModel.removeTile(i) },
                            faceColor = FriendlyPurple,
                            shadowColor = FriendlyPurpleShadow,
                            shape = RoundedCornerShape(20.dp),
                            strokeWidth = 3.dp,
                            strokeColor = TextPrimary,
                            depthHeight = 6.dp,
                            modifier = Modifier
                                .size(68.dp)
                                .graphicsLayer { rotationZ = slotRotation }
                        ) {
                            Text(
                                text = tileChar.uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CreamWhite
                            )
                        }
                    } else {
                        // Empty Slot Landing Area with 68dp target
                        GummyContainer(
                            onClick = {},
                            enabled = false,
                            faceColor = SoftSky.copy(alpha = 0.5f),
                            shadowColor = SoftSky.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            strokeWidth = 3.dp,
                            strokeColor = FriendlyPurple.copy(alpha = 0.6f),
                            depthHeight = 4.dp,
                            modifier = Modifier
                                .size(68.dp)
                                .graphicsLayer { rotationZ = slotRotation }
                        ) {
                            Text(
                                text = "_",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Letter Bank Row (Circle gummy tiles with 6dp depth band, 3dp outline and 68dp touch target)
            Text(
                text = "Letter Bank:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tileBank.forEachIndexed { index, char ->
                    val tileRotation = remember(index) { ((index * 29) % 5 - 2).toFloat() }
                    GummyContainer(
                        onClick = { viewModel.placeTile(char) },
                        faceColor = LearningBlue,
                        shadowColor = LearningBlueShadow,
                        shape = CircleShape,
                        strokeWidth = 3.dp,
                        strokeColor = TextPrimary,
                        depthHeight = 6.dp,
                        modifier = Modifier
                            .size(68.dp)
                            .graphicsLayer { rotationZ = tileRotation }
                    ) {
                        Text(
                            text = char.uppercase(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CreamWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Hint Button (Active after 2 wrong attempts)
            if (wrongAttempts >= 2) {
                GummyButton(
                    text = "💡 Tap for Hint",
                    onClick = { viewModel.openHintModal() },
                    backgroundColor = AchievementGold,
                    shadowColor = AchievementGoldShadow,
                    contentColor = TextPrimary,
                    fontSize = 17
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Check Answer Button (Squashes wide on correct answer)
            GummyButton(
                text = "Check Word ✨",
                onClick = { viewModel.submitWord() },
                enabled = placedTiles.size == (currentWord?.word?.length ?: 3),
                backgroundColor = GrowthGreen,
                shadowColor = GrowthGreenShadow,
                fontSize = 22,
                isSquashed = uiState is BlendItUiState.WordCorrect,
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
                    GummyButton(
                        text = "Restart with ❤️❤️❤️",
                        onClick = { viewModel.restartSession() },
                        backgroundColor = GrowthGreen,
                        shadowColor = GrowthGreenShadow,
                        fontSize = 18
                    )
                },
                containerColor = CreamWhite,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
