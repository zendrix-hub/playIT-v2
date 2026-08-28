package com.playit.app.presentation.findit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.CelebrationOverlay
import com.playit.app.presentation.components.CelebrationType
import com.playit.app.presentation.components.FindItCard
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Kalamansi
import com.playit.app.presentation.theme.KalamansiShadow
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeDark
import com.playit.app.presentation.theme.UbeLight
import com.playit.app.presentation.theme.UbeShadow

@Composable
fun FindItScreen(
    viewModel: FindItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val targetPhoneme by viewModel.targetPhoneme.collectAsState()
    val gridItems by viewModel.gridItems.collectAsState()
    val state by viewModel.state.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(Sky, Sand)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 3-Segment Capsule Progress Bar + Back Button + Hearts Status
            LessonTopBar(
                currentStep = LessonStep.FIND_IT,
                onBack = onBack,
                hearts = hearts
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot speech bubble prompt
                MascotSpeechHeader(
                    message = when (state) {
                        is FindItState.GameOver -> "Naubusan ng puso! Subukan muli. • Out of hearts! Let's try again."
                        is FindItState.Correct -> "Napakagaling! Nagsisimula sa /${targetPhoneme?.letter ?: "m"}/ ang larawan! • Great job! Starts with /${targetPhoneme?.letter ?: "m"}/!"
                        is FindItState.Incorrect -> "Hindi nagsisimula sa /${targetPhoneme?.letter ?: "m"}/ ang larawan. Subukan muli! • Doesn't start with /${targetPhoneme?.letter ?: "m"}/. Try again!"
                        else -> "Piliin ang larawang nagsisimula sa /${targetPhoneme?.letter ?: "m"}/! • Tap the picture that starts with /${targetPhoneme?.letter ?: "m"}/!"
                    },
                    mascotState = when (state) {
                        is FindItState.Correct -> MascotState.CELEBRATING
                        is FindItState.GameOver -> MascotState.THINKING
                        is FindItState.Incorrect -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    onMascotTap = { viewModel.playTargetSound() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Score pill + Sound Replay Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state is FindItState.Correct) "Nahanap: 1/1 • Found: 1/1" else "Hanapin • Find: /${targetPhoneme?.letter ?: "m"}/",
                        fontFamily = LexendFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = UbeDark,
                        modifier = Modifier
                            .background(color = UbeLight, shape = RoundedCornerShape(999.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )

                    GummyContainer(
                        onClick = { viewModel.playTargetSound() },
                        faceColor = UbeLight,
                        shadowColor = UbeShadow,
                        shape = RoundedCornerShape(999.dp),
                        depthHeight = 2.dp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .heightIn(min = 64.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.AutoMirrored.Rounded.VolumeUp else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Playing" else "Hear Sound",
                                tint = Ink,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Pakinggan • Hear",
                                fontFamily = LexendFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Picture Choice Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(gridItems, key = { _, item -> item.id }) { index, item ->
                        val isSelected = when (state) {
                            is FindItState.Correct -> (state as FindItState.Correct).phoneme.id == item.id
                            is FindItState.Incorrect -> (state as FindItState.Incorrect).selectedPhoneme.id == item.id
                            else -> false
                        }
                        val isCorrect = isSelected && state is FindItState.Correct
                        val borderColor = when {
                            isCorrect -> Leaf
                            isSelected && state is FindItState.Incorrect -> Kalamansi
                            else -> DarkBrownOutline
                        }
                        val faceColor = when {
                            isCorrect -> Color(0xFFEAF7EE)
                            isSelected && state is FindItState.Incorrect -> Color(0xFFFFF4E4)
                            else -> CreamWhite
                        }

                        FindItCard(
                            phoneme = item,
                            borderColor = borderColor,
                            faceColor = faceColor,
                            index = index,
                            isCorrect = isCorrect,
                            isIncorrect = isSelected && state is FindItState.Incorrect,
                            onClick = {
                                if (state !is FindItState.GameOver) {
                                    viewModel.selectItem(item)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Pinned Clean Bottom Action Bar (64dp floor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                if (state is FindItState.GameOver) {
                    GummyButton(
                        text = "Subukan Muli • Try Again",
                        onClick = { viewModel.restartSession() },
                        backgroundColor = Kalamansi,
                        shadowColor = KalamansiShadow,
                        contentColor = Ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                } else {
                    GummyButton(
                        text = "Tapusin ang Aralin • Complete Lesson",
                        onClick = {
                            if (state is FindItState.Correct) {
                                onNext(targetPhoneme?.id?.toString() ?: "1")
                            }
                        },
                        enabled = state is FindItState.Correct,
                        backgroundColor = Mango,
                        shadowColor = MangoShadow,
                        contentColor = Ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }
        }

        CelebrationOverlay(
            type = CelebrationType.CONFETTI,
            isPlaying = state is FindItState.Correct,
            colors = listOf(Ube, Mango, Guava, Leaf)
        )
    }
}
