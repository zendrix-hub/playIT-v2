package com.playit.app.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.DisabledColorShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun AddProfileButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faceColor = CreamWhite
    val shadowColor = if (enabled) LearningBlueShadow.copy(alpha = 0.4f) else DisabledColorShadow.copy(alpha = 0.3f)
    val textColor = if (enabled) LearningBlue else DisabledColor

    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = faceColor,
        shadowColor = shadowColor,
        shape = RoundedCornerShape(32.dp),
        strokeWidth = 3.dp,
        strokeColor = TextPrimary,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (enabled) "Add New Profile" else "Profile Limit Reached (Max 6)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
