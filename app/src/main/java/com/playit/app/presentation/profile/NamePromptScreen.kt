package com.playit.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    var name by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableIntStateOf(1) }
    val uiState by viewModel.uiState.collectAsState()

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
                        SkyDeep,
                        Sky,
                        Sand,
                        SandDeep
                    )
                )
            )
    ) {
        // Bohol Chocolate Hills bottom silhouette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hillWidths = listOf(70.dp, 100.dp, 85.dp, 115.dp, 80.dp, 95.dp)
            hillWidths.forEachIndexed { index, width ->
                Box(
                    modifier = Modifier
                        .size(width = width, height = width * 0.55f)
                        .offset(x = if (index == 0) 0.dp else ((-14) * index).dp)
                        .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
                        .background(
                            if (index % 2 == 0) Tan.copy(alpha = 0.35f) else TanDark.copy(alpha = 0.25f)
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
                        color = Ink
                    )
                    Text(
                        text = "Gumawa ng bagong profile",
                        fontFamily = LexendFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Companion Mascot Dialogue
            MascotSpeechHeader(
                message = "What's your name? Type your name and pick an animal friend!",
                mascotState = mascotState,
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
                        if (filtered.length <= 16) name = filtered
                    },
                    label = "Child's Name",
                    placeholder = "Enter your name...",
                    modifier = Modifier.fillMaxWidth()
                )

                AvatarPicker(
                    selectedAvatarId = selectedAvatarId,
                    onAvatarSelect = { selectedAvatarId = it }
                )

                if (uiState is ProfileUiState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Kalamansi.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = (uiState as ProfileUiState.Error).message,
                            color = Kalamansi,
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GummyButton(
                text = if (uiState is ProfileUiState.Loading) "Creating..." else "Let's Play",
                onClick = { viewModel.createProfile(name.trim(), selectedAvatarId) },
                backgroundColor = Leaf,
                shadowColor = LeafShadow,
                contentColor = Cloud,
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
