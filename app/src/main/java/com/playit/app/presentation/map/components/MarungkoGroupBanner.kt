package com.playit.app.presentation.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.LexendFontFamily

enum class GroupBannerStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED
}

/**
 * Returns canonical Marungko letters summary for each group number (1..6).
 */
fun getMarungkoBiomeTitle(groupNumber: Int): Pair<String, String> {
    val theme = BiomeThemes.forSection(groupNumber)
    return theme.title to theme.lettersSummary
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
 * Duolingo-style Section / Unit Header Banner (matching duoling_map_sample.jpg):
 * - Distinctive Bohol Biome palette (derived from tools/ images)
 * - "SECTION 1, UNIT X" label
 * - Bold unit title
 * - Right notebook / guidebook icon button
 * - Clean subtitle with horizontal divider wings: "── Practice sounds M, S, A, I ──"
 */
@Composable
fun MarungkoGroupBanner(
    groupNumber: Int,
    status: GroupBannerStatus,
    modifier: Modifier = Modifier
) {
    val theme = BiomeThemes.forSection(groupNumber)

    val bannerBg = when (status) {
        GroupBannerStatus.COMPLETED -> theme.primaryColor
        GroupBannerStatus.IN_PROGRESS -> theme.primaryColor
        GroupBannerStatus.LOCKED -> Color(0xFFE2E8F0)
    }

    val bannerShelf = when (status) {
        GroupBannerStatus.COMPLETED -> theme.shelfColor
        GroupBannerStatus.IN_PROGRESS -> theme.shelfColor
        GroupBannerStatus.LOCKED -> Color(0xFFCBD5E1)
    }

    val headerColor = when (status) {
        GroupBannerStatus.LOCKED -> Color(0xFF64748B)
        else -> Color.White.copy(alpha = 0.90f)
    }

    val titleColor = when (status) {
        GroupBannerStatus.LOCKED -> Color(0xFF334155)
        else -> Color.White
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Duolingo Unit Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
        ) {
            // 3D Shelf Extrusion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(18.dp))
                    .background(bannerShelf)
            )

            // Top Card Face
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(18.dp))
                    .background(bannerBg)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "SECTION 1, UNIT $groupNumber".uppercase(),
                            fontFamily = LexendFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = headerColor,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = theme.title,
                            fontFamily = LexendFontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = titleColor
                        )
                    }

                    // Guidebook notebook icon button on the right
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (status == GroupBannerStatus.LOCKED) Color(0xFFCBD5E1) else theme.shelfColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = "Guidebook",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtitle Divider line: ──── Practice sounds M, S, A, I ────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = Color(0xFFE2E8F0)
            )
            Text(
                text = "  ${theme.lettersSummary}  ",
                fontFamily = LexendFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = Color(0xFFE2E8F0)
            )
        }
    }
}
