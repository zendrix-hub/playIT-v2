package com.playit.app.presentation.findit

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.shake
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Phoneme
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Find It — Which starts with /${targetPhoneme?.letter ?: "m"}/?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4-Card Picture Discrimination Grid
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
                        else -> Color.Transparent
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

            // Feedback / Continue CTA
            Button(
                onClick = { onNext(targetPhoneme?.id?.toString() ?: "1") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = state is FindItState.Correct,
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GrowthGreen)
            ) {
                Text(
                    text = "Finish Lesson! 🎉",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(4.dp, borderColor, RoundedCornerShape(24.dp))
            .shake(trigger = isIncorrect)
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🖼️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = phoneme.exampleWord,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
