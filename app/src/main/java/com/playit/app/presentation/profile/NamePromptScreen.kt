package com.playit.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyBackButton
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyTextField
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.profile.components.AvatarPicker
import com.playit.app.presentation.theme.*

@Composable
fun NamePromptScreen(
    viewModel: ProfileViewModel,
    onProfileCreated: (Long) -> Unit,
    onBack: () -> Unit
) {
    val name by viewModel.nameInput.collectAsStateWithLifecycle()
    val selectedAvatarId by viewModel.selectedAvatarId.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPlayingIntro by viewModel.isPlayingIntro.collectAsStateWithLifecycle()

    val isNameValid = name.trim().isNotBlank()
    val mascotState = when {
        // Error takes priority: don't let Lily celebrate next to an error banner.
        uiState is ProfileUiState.Error -> MascotState.POINTING
        isNameValid -> MascotState.CELEBRATING
        else -> MascotState.POINTING
    }

    LaunchedEffect(Unit) {
        viewModel.playNamePromptIntro()
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Created) {
            val id = (uiState as ProfileUiState.Created).profileId
            viewModel.resetForm()
            viewModel.clearUiState()
            onProfileCreated(id)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F0FE), // Airy crisp sky
                        Color(0xFFF3E8FF), // Soft playful lavender
                        Color(0xFFFEF3C7)  // Warm sunny cream
                    )
                )
            )
    ) {
        // Vibrant Emerald rolling playground hills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hillWidths = listOf(90.dp, 120.dp, 105.dp, 135.dp, 100.dp, 115.dp)
            hillWidths.forEachIndexed { index, width ->
                Box(
                    modifier = Modifier
                        .size(width = width, height = width * 0.52f)
                        .offset(x = if (index == 0) 0.dp else ((-18) * index).dp)
                        .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
                        .background(
                            if (index % 2 == 0) EmeraldLeaf.copy(alpha = 0.20f) else EmeraldLeaf.copy(alpha = 0.32f)
                        )
                )
            }
        }

        // Main Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GummyBackButton(onClick = onBack)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "New Player Profile",
                        fontFamily = LexendFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )
                    Text(
                        text = "Create your player profile",
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Companion Mascot Dialogue
            MascotSpeechHeader(
                message = "What is your name? Let's choose your friendly animal buddy!",
                mascotState = mascotState,
                isPlayingAudio = isPlayingIntro,
                onMascotTap = { viewModel.playNamePromptIntro() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GummyTextField(
                    value = name,
                    onValueChange = { input ->
                        // Zero-emoji policy: filter to letters, whitespace, hyphens, and apostrophes
                        val filtered = input.filter {
                            it.isLetter() || it.isWhitespace() || it == '-' || it == '\''
                        }
                        if (filtered.length <= 16) viewModel.onNameChanged(filtered)
                    },
                    label = "Child's Name",
                    placeholder = "Enter your name...",
                    modifier = Modifier.fillMaxWidth()
                )

                AvatarPicker(
                    selectedAvatarId = selectedAvatarId,
                    onAvatarSelect = { viewModel.onAvatarSelected(it) }
                )

                if (uiState is ProfileUiState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = CoralBerry.copy(alpha = 0.12f),
                                shape = Squircle12
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = (uiState as ProfileUiState.Error).message,
                            color = CoralBerry,
                            fontFamily = LexendFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GummyButton(
                text = if (uiState is ProfileUiState.Loading) "Creating..." else "Let's Play",
                onClick = { viewModel.createProfile(name.trim(), selectedAvatarId) },
                backgroundColor = EmeraldLeaf,
                shadowColor = EmeraldLeafShadow,
                contentColor = Color.White,
                enabled = isNameValid && uiState !is ProfileUiState.Loading,
                fontSize = 24,
                isSquashed = isNameValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
