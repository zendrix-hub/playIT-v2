package com.playit.app.presentation.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkFaint
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafDark
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoDark
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.TanShadow

enum class GroupBannerStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED
}

/**
 * Returns canonical Marungko letters summary for each group number (1..6).
 */
fun getMarungkoBiomeTitle(groupNumber: Int): Pair<String, String> {
    return when (groupNumber) {
        1 -> "Chocolate Hills Trail" to "m • s • a • i • o • b"
        2 -> "Loboc River Valley" to "e • u • t • k • l • y"
        3 -> "Panglao Shoreline" to "n • g • ng • p • r • d"
        4 -> "Tarsier Sanctuary" to "h • w • c • f • j • v"
        5 -> "Bohol Mountain Summit" to "z • q • x"
        else -> "Marungko Trail" to "Mga Titik"
    }
}

/**
 * Returns canonical Marungko letters formatted string for tests and UI banners.
 */
fun getMarungkoLettersForGroup(groupNumber: Int): String {
    return when (groupNumber) {
        1 -> "M • S • A • I • O"
        2 -> "B • U • T • K • L"
        3 -> "Y • N • G • R • P"
        4 -> "D • H • W • C • V"
        5 -> "Z • J • F • X • Q"
        6 -> "Ñ • NG"
        else -> ""
    }
}


/**
 * Bilingual Marungko Group Milestone Banner separating learning chapters along the map.
 * Duolingo ABC inspired Chapter / Unit header.
 */
@Composable
fun MarungkoGroupBanner(
    groupNumber: Int,
    status: GroupBannerStatus,
    modifier: Modifier = Modifier
) {
    val (biomeTitle, lettersSummary) = getMarungkoBiomeTitle(groupNumber)

    val faceColor = when (status) {
        GroupBannerStatus.COMPLETED -> Cloud
        GroupBannerStatus.IN_PROGRESS -> Cloud
        GroupBannerStatus.LOCKED -> Cloud.copy(alpha = 0.90f)
    }

    val shadowColor = when (status) {
        GroupBannerStatus.COMPLETED -> LeafShadow.copy(alpha = 0.55f)
        GroupBannerStatus.IN_PROGRESS -> MangoShadow.copy(alpha = 0.65f)
        GroupBannerStatus.LOCKED -> TanShadow.copy(alpha = 0.45f)
    }

    val borderColor = when (status) {
        GroupBannerStatus.COMPLETED -> LeafDark
        GroupBannerStatus.IN_PROGRESS -> MangoDark
        GroupBannerStatus.LOCKED -> DarkBrownOutline
    }

    val statusBadgeBg = when (status) {
        GroupBannerStatus.COMPLETED -> Leaf
        GroupBannerStatus.IN_PROGRESS -> Mango
        GroupBannerStatus.LOCKED -> InkFaint
    }

    val statusBadgeText = when (status) {
        GroupBannerStatus.COMPLETED -> "Complete"
        GroupBannerStatus.IN_PROGRESS -> "In Progress"
        GroupBannerStatus.LOCKED -> "Locked"
    }

    val statusTextColor = when (status) {
        GroupBannerStatus.COMPLETED -> Cloud
        GroupBannerStatus.IN_PROGRESS -> Ink
        GroupBannerStatus.LOCKED -> InkSoft
    }

    GummyContainer(
        onClick = {},
        enabled = false,
        faceColor = faceColor,
        shadowColor = shadowColor,
        strokeColor = borderColor,
        strokeWidth = 2.5.dp,
        depthHeight = 4.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Pangkat $groupNumber: $biomeTitle, $statusBadgeText, Letters: $lettersSummary"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Biome Book Icon Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = when (status) {
                                GroupBannerStatus.COMPLETED -> Leaf.copy(alpha = 0.18f)
                                GroupBannerStatus.IN_PROGRESS -> Mango.copy(alpha = 0.22f)
                                GroupBannerStatus.LOCKED -> InkFaint.copy(alpha = 0.25f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoStories,
                        contentDescription = null,
                        tint = when (status) {
                            GroupBannerStatus.COMPLETED -> LeafDark
                            GroupBannerStatus.IN_PROGRESS -> MangoDark
                            GroupBannerStatus.LOCKED -> InkSoft
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Chapter Biome Title & Letters
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Pangkat $groupNumber • $biomeTitle",
                        fontFamily = LexendFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = lettersSummary,
                        fontFamily = LexendFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InkSoft
                    )
                }
            }

            // Status Pill
            Box(
                modifier = Modifier
                    .background(statusBadgeBg, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (status) {
                            GroupBannerStatus.COMPLETED -> Icons.Rounded.Check
                            GroupBannerStatus.IN_PROGRESS -> Icons.Rounded.PlayArrow
                            GroupBannerStatus.LOCKED -> Icons.Rounded.Lock
                        },
                        contentDescription = null,
                        tint = statusTextColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = statusBadgeText,
                        fontFamily = LexendFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusTextColor
                    )
                }
            }
        }
    }
}

