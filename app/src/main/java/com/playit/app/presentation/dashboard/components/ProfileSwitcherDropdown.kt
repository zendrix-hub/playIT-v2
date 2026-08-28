package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Profile
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.CloudShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.LexendFontFamily

@Composable
fun ProfileSwitcherDropdown(
    profiles: List<Profile>,
    selectedProfile: Profile?,
    onProfileSelect: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Rebuilt with GummyContainer (52dp adult height) and Lexend font family
        GummyContainer(
            onClick = { expanded = true },
            faceColor = Cloud,
            shadowColor = CloudShadow,
            shape = RoundedCornerShape(16.dp),
            strokeWidth = 2.5.dp,
            strokeColor = DarkBrownOutline,
            depthHeight = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedProfile?.name?.let { "Learner: $it" } ?: "Select Profile",
                    fontFamily = LexendFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Ink
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            profiles.forEach { profile ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = profile.name,
                            fontFamily = LexendFontFamily,
                            fontSize = 16.sp,
                            fontWeight = if (profile.id == selectedProfile?.id) FontWeight.Bold else FontWeight.Normal,
                            color = Ink
                        )
                    },
                    onClick = {
                        onProfileSelect(profile)
                        expanded = false
                    }
                )
            }
        }
    }
}
