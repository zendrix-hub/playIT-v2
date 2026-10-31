package com.playit.app.presentation.map.components

import com.playit.app.presentation.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.testTag
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

internal val UnitLettersInk = TextMidnight
internal val UnitLettersPlate = SurfaceCard
internal val UnitLettersLine = ModernBorder

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
        7 -> "Q • V • X • Z"
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
    modifier: Modifier = Modifier,
    onGuidebookClick: () -> Unit = {}
) {
    val theme = BiomeThemes.forSection(groupNumber)

    val bannerBg = when (status) {
        GroupBannerStatus.COMPLETED -> theme.primaryColor
        GroupBannerStatus.IN_PROGRESS -> theme.primaryColor
        GroupBannerStatus.LOCKED -> LockedShelf
    }

    val bannerShelf = when (status) {
        GroupBannerStatus.COMPLETED -> theme.shelfColor
        GroupBannerStatus.IN_PROGRESS -> theme.shelfColor
        GroupBannerStatus.LOCKED -> LockedShelf
    }

    val headerColor = when (status) {
        GroupBannerStatus.LOCKED -> LockedInk
        else -> Color.White.copy(alpha = 0.90f)
    }

    val titleColor = when (status) {
        GroupBannerStatus.LOCKED -> TextMidnight
        else -> Color.White
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Duolingo Unit Card. The face grows with its text (a fixed 72 dp clipped the title on
        // every size, and a long title wraps on 360 dp phones); the shelf follows it (card 28b).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unit_banner_$groupNumber")
        ) {
            // 3D Shelf Extrusion: the card's full height, 6 dp lower than the face
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(bannerShelf)
            )

            // Top Card Face
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(bannerBg)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
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
                        // 16 sp floor for all map text (it was 11 sp, card 31e).
                        Text(
                            text = "SECTION 1, UNIT $groupNumber".uppercase(),
                            fontFamily = LexendFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = headerColor,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = theme.title,
                            fontFamily = LexendFontFamily,
                            fontSize = 17.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Black,
                            color = titleColor,
                            maxLines = 2,
                            modifier = Modifier.testTag("unit_banner_title_$groupNumber")
                        )
                    }

                    // Interactive Guidebook notebook icon button on the right: 64 dp like every
                    // child-facing control (it was 44 dp, card 31e).
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (status == GroupBannerStatus.LOCKED) LockedShelf else theme.shelfColor)
                            .clickable { onGuidebookClick() }
                            .testTag("unit_guidebook_$groupNumber"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = "Guidebook for Unit $groupNumber",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtitle Divider line: ──── Practice sounds M, S, A, I ────
        // On a cream plate of its own in dark ink: bare stone ink blended into the map behind it,
        // and the rope and the river run through this line (user, 2026-10-31).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = UnitLettersLine
            )
            Text(
                text = theme.lettersSummary,
                fontFamily = LexendFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UnitLettersInk,
                maxLines = 1,
                modifier = Modifier
                    .testTag("unit_letters_$groupNumber")
                    .background(UnitLettersPlate, RoundedCornerShape(14.dp))
                    .border(2.dp, UnitLettersLine, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 2.dp,
                color = UnitLettersLine
            )
        }
    }
}
