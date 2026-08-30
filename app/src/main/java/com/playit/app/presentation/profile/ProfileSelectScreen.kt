package com.playit.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.GameplayConstants
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.dashboard.components.ArithmeticGuardDialog
import com.playit.app.presentation.profile.components.AddProfileButton
import com.playit.app.presentation.profile.components.ProfileCard
import com.playit.app.presentation.theme.*

@Composable
fun ProfileSelectScreen(
    viewModel: ProfileViewModel,
    onProfileSelected: (Long) -> Unit,
    onAddProfileClick: () -> Unit,
    onParentDashboardClick: () -> Unit
) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val showArithmeticGuard by viewModel.showArithmeticGuard.collectAsStateWithLifecycle()
    val isPlayingGreeting by viewModel.isPlayingGreeting.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Snappy, warm auto-greeting on screen open
    LaunchedEffect(Unit) {
        viewModel.playWelcomeGreeting()
    }

    if (showArithmeticGuard) {
        ArithmeticGuardDialog(
            onPass = {
                viewModel.onArithmeticGuardPassed()
                onParentDashboardClick()
            },
            onDismiss = {
                viewModel.dismissArithmeticGuard()
            },
            onCorrectSound = { viewModel.playArithmeticSuccessSound() },
            onIncorrectSound = { viewModel.playArithmeticFailureSound() }
        )
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

        // Screen Content (Header, Mascot prompt, and Profile list)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Row: Screen Title & Parent Zone Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Who is playing?",
                        fontFamily = LexendFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Choose your profile",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }

                GummyButton(
                    text = "Parent Zone",
                    icon = Icons.Filled.Lock,
                    onClick = { viewModel.requestParentAccess() },
                    backgroundColor = Ube,
                    shadowColor = UbeShadow,
                    contentColor = Cloud,
                    fontSize = 14,
                    modifier = Modifier.height(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Companion Mascot Dialogue (Warm auto-greeting + visible speaker replay badge with 100% verbatim text)
            MascotSpeechHeader(
                message = if (profiles.isEmpty()) {
                    "Welcome to PlayIT! Tap '+ Add New Profile' below to begin your sound adventure!"
                } else {
                    "Hi there! I'm Lily. Let's play and learn together! Tap your name to start!"
                },
                mascotState = if (profiles.isEmpty()) MascotState.POINTING else MascotState.WAVING,
                isPlayingAudio = isPlayingGreeting,
                onMascotTap = { viewModel.playWelcomeGreeting() },
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Fast, High-Performance Scrollable Profile List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                profiles.forEach { profile ->
                    ProfileCard(
                        profile = profile,
                        onSelect = { id ->
                            viewModel.playProfileSelectSound()
                            viewModel.selectProfile(id)
                            onProfileSelected(id)
                        }
                    )
                }

                AddProfileButton(
                    enabled = viewModel.canAddProfile(),
                    currentCount = profiles.size,
                    maxCount = GameplayConstants.MAX_PROFILES,
                    isPrimaryAction = profiles.isEmpty(),
                    onClick = onAddProfileClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}
