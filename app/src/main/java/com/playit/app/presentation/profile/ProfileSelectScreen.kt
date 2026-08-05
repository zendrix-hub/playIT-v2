package com.playit.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.dashboard.components.ArithmeticGuardDialog
import com.playit.app.presentation.profile.components.AddProfileButton
import com.playit.app.presentation.profile.components.ProfileCard
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun ProfileSelectScreen(
    viewModel: ProfileViewModel,
    onProfileSelected: (Long) -> Unit,
    onAddProfileClick: () -> Unit,
    onParentDashboardClick: () -> Unit
) {
    val profiles by viewModel.profiles.collectAsState()
    var showArithmeticGuard by remember { mutableStateOf(false) }

    if (showArithmeticGuard) {
        ArithmeticGuardDialog(
            onPass = {
                showArithmeticGuard = false
                onParentDashboardClick()
            },
            onDismiss = {
                showArithmeticGuard = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftSky)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Who is playing?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = { showArithmeticGuard = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FriendlyPurple),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🔒 Parent Zone",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(profiles, key = { it.id }) { profile ->
                    ProfileCard(
                        profile = profile,
                        onSelect = { id ->
                            viewModel.selectProfile(id)
                            onProfileSelected(id)
                        }
                    )
                }

                item {
                    AddProfileButton(
                        enabled = viewModel.canAddProfile(),
                        onClick = onAddProfileClick
                    )
                }
            }
        }
    }
}
