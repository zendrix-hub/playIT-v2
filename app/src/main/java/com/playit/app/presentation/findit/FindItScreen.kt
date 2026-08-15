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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Phoneme
import com.playit.app.presentation.components.DockedMascotWithBubble
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlueShadow
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

            // Docked Mascot Prompt Guidance
            DockedMascotWithBubble(
                message = when (state) {
                    is FindItState.Correct -> "Great job! That picture starts with /${targetPhoneme?.letter ?: "m"}/! 🎉"
                    is FindItState.Incorrect -> "Oops! That picture doesn't start with /${targetPhoneme?.letter ?: "m"}/. Try again!"
                    else -> "Which picture starts with the /${targetPhoneme?.letter ?: "m"}/ sound?"
                },
                mascotState = when (state) {
                    is FindItState.Correct -> com.playit.app.presentation.components.MascotState.CELEBRATING
                    is FindItState.Incorrect -> com.playit.app.presentation.components.MascotState.THINKING
                    else -> com.playit.app.presentation.components.MascotState.POINTING
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Picture Choice Grid (Static per-index card rotation & thick stroke outline)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(gridItems, key = { _, item -> item.id }) { index, item ->
                    val isSelected = when (state) {
                        is FindItState.Correct -> (state as FindItState.Correct).phoneme.id == item.id
                        is FindItState.Incorrect -> (state as FindItState.Incorrect).selectedPhoneme.id == item.id
                        else -> false
                    }
                    val isCorrect = isSelected && state is FindItState.Correct
                    val borderColor = when {
                        isCorrect -> GrowthGreen
                        isSelected && state is FindItState.Incorrect -> GentleCorrectionOrange
                        else -> TextPrimary
                    }

                    com.playit.app.presentation.components.FindItCard(
                        phoneme = item,
                        borderColor = borderColor,
                        index = index,
                        isCorrect = isCorrect,
                        isIncorrect = isSelected && state is FindItState.Incorrect,
                        onClick = { viewModel.selectItem(item) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Finish Lesson CTA (Triggers squash motion on correct answer feedback)
            PediatricButton(
                text = "Complete Lesson! 🎉",
                onClick = { onNext(targetPhoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                enabled = state is FindItState.Correct,
                fontSize = 22,
                isSquashed = state is FindItState.Correct,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
