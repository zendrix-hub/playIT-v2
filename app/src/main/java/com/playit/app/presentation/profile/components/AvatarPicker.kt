package com.playit.app.presentation.profile.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.hop
import com.playit.app.presentation.theme.*

val AVATAR_NAMES = listOf("Cat", "Monkey", "Bunny", "Bear", "Frog", "Owl")

/** The animal's circle, and how much a picked animal grows. */
const val AVATAR_CIRCLE_DP = 76
const val AVATAR_SELECTED_SCALE = 1.12f

/**
 * Room kept above and below the grid, in dp, so a picked animal's ring is never cut (user, 2026-10-29: "the circle is
 * getting cut"): the screen scrolls the grid, and a scrolling area trims whatever is drawn past its edge, which the
 * grow and the hop on a pick both are (AvatarPickerRoomTest).
 */
const val AVATAR_PICK_ROOM_DP = 24

@Composable
fun AvatarPicker(
    selectedAvatarId: Int,
    onAvatarSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    // No headings of its own: Lily's bubble above it says "Pick your animal friend!" (card 31d).
    // Each pick makes that animal hop once, so the child sees their friend come alive (design refresh).
    val hops = remember { mutableStateMapOf<Int, Int>() }
    Column(modifier = modifier.fillMaxWidth().padding(vertical = AVATAR_PICK_ROOM_DP.dp)) {
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
                        val avatarName = AVATAR_NAMES.getOrElse(avatarId - 1) { "Friend" }

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) AVATAR_SELECTED_SCALE else 1.0f,
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
                                // No ripple: the default one drew a dark box over the whole tile (user, 2026-10-29).
                                // The tile presses down like the app's other buttons, then the animal hops.
                                .bounceClick {
                                    hops[avatarId] = (hops[avatarId] ?: 0) + 1
                                    onAvatarSelect(avatarId)
                                }
                                .semantics {
                                    role = Role.RadioButton
                                    contentDescription = "$avatarName avatar" + if (isSelected) ", selected" else ""
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .hop(hops[avatarId] ?: 0)
                                    .size(AVATAR_CIRCLE_DP.dp)
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
