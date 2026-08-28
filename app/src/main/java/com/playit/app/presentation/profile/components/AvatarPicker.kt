package com.playit.app.presentation.profile.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.*

@Composable
fun AvatarPicker(
    selectedAvatarId: Int,
    onAvatarSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarNames = listOf("Cat", "Monkey", "Bunny", "Bear", "Frog", "Owl")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Choose Your Avatar",
            fontFamily = LexendFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Ink
        )
        Text(
            text = "Pick your animal companion",
            fontFamily = LexendFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = InkSoft,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3x2 Bento Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(1..3, 4..6).forEach { rowRange ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowRange.forEach { avatarId ->
                        val isSelected = avatarId == selectedAvatarId
                        val avatarName = avatarNames.getOrElse(avatarId - 1) { "Friend" }

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.12f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "avatarScale"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable { onAvatarSelect(avatarId) }
                                .semantics {
                                    role = Role.RadioButton
                                    contentDescription = "$avatarName avatar" + if (isSelected) ", selected" else ""
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Mango.copy(alpha = 0.35f) else Cloud)
                                    .border(
                                        width = if (isSelected) 4.dp else 2.5.dp,
                                        color = if (isSelected) Mango else DarkBrownOutline.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarCircle(avatarId = avatarId, size = 66)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = avatarName,
                                fontFamily = LexendFontFamily,
                                fontSize = 24.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) Ink else InkSoft,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
