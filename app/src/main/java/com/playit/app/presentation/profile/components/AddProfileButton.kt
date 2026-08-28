package com.playit.app.presentation.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.GameplayConstants
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.theme.*

const val MAX_LEARNER_PROFILES = GameplayConstants.MAX_PROFILES

@Composable
fun AddProfileButton(
    enabled: Boolean,
    currentCount: Int = 0,
    maxCount: Int = MAX_LEARNER_PROFILES,
    isPrimaryAction: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faceColor = if (enabled) Mango else Cloud
    val shadowColor = if (enabled) MangoShadow else CloudShadow
    val textColor = if (enabled) Ink else InkFaint
    val strokeColor = if (enabled) DarkBrownOutline else DarkBrownOutline.copy(alpha = 0.35f)

    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = faceColor,
        shadowColor = shadowColor,
        shape = RoundedCornerShape(24.dp),
        strokeWidth = 3.dp,
        strokeColor = strokeColor,
        depthHeight = 5.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .breathingPulse(enabled = enabled && isPrimaryAction)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (enabled) Icons.Filled.Add else Icons.Filled.Lock,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (enabled) {
                    if (currentCount > 0) "Add Profile ($currentCount/$maxCount)" else "Add Profile"
                } else {
                    "Limit Reached ($maxCount/$maxCount)"
                },
                fontFamily = LexendFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
