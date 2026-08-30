package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.*

private val PopGreenFace = Color(0xFF58CC02)
private val PopGreenShelf = Color(0xFF46A302)
private val PopGoldFace = Color(0xFFFFC800)
private val PopGoldShelf = Color(0xFFE5A500)
private val PopLockedFace = Color(0xFFE2E8F0)
private val PopLockedShelf = Color(0xFFCBD5E1)

/**
 * Duolingo Signature Floating 3D Node Action Popup Dialog.
 * Triggered on tapping a letter node or challenge milestone.
 */
@Composable
fun NodeActionPopupDialog(
    node: MapNode,
    onStartChallenge: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isLetter = node is MapNode.LetterNode
    val isUnlocked = node.isUnlocked
    val starsEarned = if (node is MapNode.LetterNode) node.starsEarned else 0
    val isCompleted = starsEarned > 0

    val primaryColor = when {
        !isUnlocked -> PopLockedFace
        isCompleted -> PopGoldFace
        else -> PopGreenFace
    }
    val shelfColor = when {
        !isUnlocked -> PopLockedShelf
        isCompleted -> PopGoldShelf
        else -> PopGreenShelf
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .background(Cloud)
                .border(3.5.dp, DarkBrownOutline, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = InkSoft,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 3D Disc Badge (Always displaying the letter symbol clearly)
                Box(
                    modifier = Modifier.size(width = 84.dp, height = 92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Bottom 3D Shelf
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(shelfColor, CircleShape)
                    )
                    // Top Face
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.TopCenter)
                            .background(primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Crescent Gleam Highlight
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color(0x60FFFFFF),
                                startAngle = 175f,
                                sweepAngle = 90f,
                                useCenter = false,
                                topLeft = Offset(8.dp.toPx(), 6.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 4.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        if (isLetter) {
                            val letterNode = node as MapNode.LetterNode
                            Text(
                                text = letterNode.symbol,
                                fontFamily = LexendFontFamily,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isUnlocked) Color.White else Color(0xFF94A3B8)
                            )
                        } else {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Rounded.Star else Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) Color(0xFF78350F) else Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Node Title & Phonics Sound
                if (isLetter) {
                    val letterNode = node as MapNode.LetterNode
                    Text(
                        text = "LETTER ${letterNode.symbol}",
                        fontFamily = LexendFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isUnlocked) "Phonics Sound: /${letterNode.symbol.lowercase()}/" else "Unit ${letterNode.groupNumber} Phonics",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                } else {
                    val blendNode = node as MapNode.BlendItNode
                    Text(
                        text = "BLEND CHALLENGE ${blendNode.groupId}",
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Word Blending Milestone",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Star Rating Display (0-3 stars)
                if (isUnlocked && isLetter) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { starIdx ->
                            val earned = starIdx < starsEarned
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (earned) Color(0xFFFFC800) else Color(0xFFE2E8F0),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Mini Activity Summary Checklist
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLetter) {
                                MiniPill(label = "Hear It")
                                MiniPill(label = "Say It")
                                MiniPill(label = "Find It")
                            } else {
                                MiniPill(label = "Listen")
                                MiniPill(label = "Blend")
                                MiniPill(label = "Master")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Gummy Action Button
                    val buttonText = when {
                        isCompleted -> "PRACTICE AGAIN"
                        isLetter -> "START CHALLENGE"
                        else -> "START BLEND CHALLENGE"
                    }
                    val btnBg = if (isCompleted) PopGoldFace else PopGreenFace
                    val btnShelf = if (isCompleted) PopGoldShelf else PopGreenShelf

                    GummyButton(
                        text = buttonText,
                        icon = Icons.Rounded.PlayArrow,
                        onClick = {
                            val nodeId = if (isLetter) (node as MapNode.LetterNode).id else "blend_${(node as MapNode.BlendItNode).groupId}"
                            onStartChallenge(nodeId)
                            onDismiss()
                        },
                        backgroundColor = btnBg,
                        shadowColor = btnShelf,
                        contentColor = if (isCompleted) Color(0xFF78350F) else Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                } else {
                    // Locked Card Guidance
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Complete the previous sounds to unlock this challenge!",
                            fontFamily = LexendFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = InkSoft,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GummyButton(
                        text = "Keep Going!",
                        onClick = onDismiss,
                        backgroundColor = Color(0xFF94A3B8),
                        shadowColor = Color(0xFF64748B),
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )
    }
}
