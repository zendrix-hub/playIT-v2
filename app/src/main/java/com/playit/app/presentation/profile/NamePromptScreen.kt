package com.playit.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.profile.components.AvatarPicker
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun NamePromptScreen(
    viewModel: ProfileViewModel,
    onProfileCreated: (Long) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableIntStateOf(1) }
    val uiState by viewModel.uiState.collectAsState()

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
            .background(SoftSky)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What is your name?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }



            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Child's Name", fontSize = 18.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CreamWhite,
                    unfocusedContainerColor = CreamWhite,
                    focusedBorderColor = LearningBlue,
                    unfocusedBorderColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            AvatarPicker(
                selectedAvatarId = selectedAvatarId,
                onAvatarSelect = { selectedAvatarId = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState is ProfileUiState.Error) {
                Text(
                    text = (uiState as ProfileUiState.Error).message,
                    color = GentleCorrectionOrange,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.createProfile(name, selectedAvatarId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = name.trim().isNotBlank() && uiState !is ProfileUiState.Loading,
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LearningBlue)
            ) {
                Text(
                    text = if (uiState is ProfileUiState.Loading) "Creating..." else "Start Playing!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }
    }
}
