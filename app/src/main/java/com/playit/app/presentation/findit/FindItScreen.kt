package com.playit.app.presentation.findit

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.playit.app.domain.model.Phoneme
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun FindItScreen(
    viewModel: FindItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val targetPhoneme by viewModel.targetPhoneme.collectAsState()
    val gridItems by viewModel.gridItems.collectAsState()
    val state by viewModel.state.collectAsState()

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
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Find It — Sound /${targetPhoneme?.letter ?: "m"}/",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mascot Guidance Prompt
            MascotBubble(
                message = when (state) {
                    is FindItState.Correct -> "Great job! That picture starts with /${targetPhoneme?.letter ?: "m"}/! 🎉"
                    is FindItState.Incorrect -> "Oops! That picture doesn't start with /${targetPhoneme?.letter ?: "m"}/. Try again!"
                    else -> "Which picture starts with the /${targetPhoneme?.letter ?: "m"}/ sound?"
                },
                mascotEmoji = when (state) {
                    is FindItState.Correct -> "🌟"
                    is FindItState.Incorrect -> "🤔"
                    else -> "🦜"
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Picture Choice Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(gridItems, key = { it.id }) { item ->
                    val isSelected = when (state) {
                        is FindItState.Correct -> (state as FindItState.Correct).phoneme.id == item.id
                        is FindItState.Incorrect -> (state as FindItState.Incorrect).selectedPhoneme.id == item.id
                        else -> false
                    }
                    val borderColor = when {
                        isSelected && state is FindItState.Correct -> GrowthGreen
                        isSelected && state is FindItState.Incorrect -> GentleCorrectionOrange
                        else -> SoftSky
                    }

                    PictureChoiceCard(
                        phoneme = item,
                        borderColor = borderColor,
                        isIncorrect = isSelected && state is FindItState.Incorrect,
                        onClick = { viewModel.selectItem(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Finish Lesson CTA
            PediatricButton(
                text = "Complete Lesson! 🎉",
                onClick = { onNext(targetPhoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                enabled = state is FindItState.Correct,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PictureChoiceCard(
    phoneme: Phoneme,
    borderColor: Color,
    isIncorrect: Boolean = false,
    onClick: () -> Unit
) {
    val pictureEmojis = mapOf(
        "m" to "🦧", "s" to "🐍", "a" to "🍎", "i" to "🦎",
        "t" to "🐯", "o" to "🐙", "b" to "🍌", "e" to "🐘",
        "u" to "☂️", "n" to "🥜", "g" to "🦍", "p" to "🍕",
        "r" to "🚀", "d" to "🐬", "h" to "🐴", "l" to "🦁",
        "c" to "🐱", "k" to "🪃", "w" to "🍉", "j" to "🧃", "y" to "🪀"
    )
    val emoji = pictureEmojis[phoneme.letter.lowercase()] ?: "🖼️"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(146.dp)
            .shake(trigger = isIncorrect)
            .bounceClick(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(28.dp), spotColor = LearningBlue),
        shape = RoundedCornerShape(28.dp),
        color = CreamWhite,
        border = androidx.compose.foundation.BorderStroke(3.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = emoji, fontSize = 52.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = phoneme.exampleWord,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }
        }
    }
}

