package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Profile
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun ProfileSwitcherDropdown(
    profiles: List<Profile>,
    selectedProfile: Profile?,
    onProfileSelect: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedProfile?.name?.let { "👤 Student: $it ▼" } ?: "Select Profile ▼",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            profiles.forEach { profile ->
                DropdownMenuItem(
                    text = { Text(text = profile.name, fontWeight = if (profile.id == selectedProfile?.id) FontWeight.Bold else FontWeight.Normal) },
                    onClick = {
                        onProfileSelect(profile)
                        expanded = false
                    }
                )
            }
        }
    }
}
