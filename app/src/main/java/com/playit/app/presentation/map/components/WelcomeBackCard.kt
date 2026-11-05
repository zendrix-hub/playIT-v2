package com.playit.app.presentation.map.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.manager.ReturnGreetingPolicy
import com.playit.app.presentation.components.GummyMotionAsset
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.TalkingLily
import com.playit.app.presentation.components.popIn
import com.playit.app.presentation.map.WelcomeBack
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.EmeraldLeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldLight
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.SurfaceCardShadow
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

/**
 * Lily's welcome back over the map (adviser, 2026-11-05): "Welcome back! I missed you.", then whether the child wants
 * to keep going, with the next letter big and its picture. Two big buttons: "Let's go!" opens that letter, "Not now"
 * stays on the map. Tapping Lily says it again.
 */
@Composable
fun WelcomeBackCard(
    card: WelcomeBack,
    onGo: () -> Unit,
    onNotNow: () -> Unit,
    onLilyTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.38f))
            // The map under the card takes no taps while it shows.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceCard,
            border = BorderStroke(3.dp, ModernBorder),
            shadowElevation = 10.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .popIn()
                .testTag("welcome_back")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable(onClickLabel = "Hear Lily again", onClick = onLilyTap)) {
                        TalkingLily(pose = MascotState.WAVING, maxSize = 96.dp, modifier = Modifier.size(96.dp))
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = if (card.name.isBlank()) "Welcome back!" else "Welcome back, ${card.name}!",
                            fontFamily = LexendFontFamily, fontSize = 22.sp, lineHeight = 27.sp,
                            fontWeight = FontWeight.ExtraBold, color = TextMidnight
                        )
                        Text(
                            text = "I missed you.",
                            fontFamily = LexendFontFamily, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                OfferPicture(card)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = ReturnGreetingPolicy.question(card.offer),
                    fontFamily = LexendFontFamily, fontSize = 18.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold,
                    color = TextMidnight, textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                val allDone = card.offer is ReturnGreetingPolicy.AllDone
                GummyButton(
                    text = if (allDone) "OK" else "Let's go!",
                    icon = Icons.Rounded.PlayArrow,
                    onClick = onGo,
                    backgroundColor = EmeraldLeaf,
                    shadowColor = EmeraldLeafShadow,
                    contentColor = Color.White,
                    modifier = Modifier.heightIn(min = 64.dp).testTag("welcome_back_go")
                )
                if (!allDone) {
                    Spacer(modifier = Modifier.height(10.dp))
                    GummyButton(
                        text = "Not now",
                        icon = Icons.Rounded.Map,
                        onClick = onNotNow,
                        backgroundColor = SurfaceCard,
                        shadowColor = SurfaceCardShadow,
                        contentColor = TextMidnight,
                        modifier = Modifier.heightIn(min = 64.dp).testTag("welcome_back_not_now")
                    )
                }
            }
        }
    }
}

@Composable
private fun OfferPicture(card: WelcomeBack) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SunnyGoldLight)
                .border(3.dp, ModernBorder, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (card.offer) {
                is ReturnGreetingPolicy.KeepGoing -> {
                    val l = card.letter ?: card.offer.letter
                    Text(
                        l.uppercase() + l.lowercase(), fontFamily = LexendFontFamily, fontSize = 44.sp,
                        fontWeight = FontWeight.Black, color = TextMidnight
                    )
                }
                is ReturnGreetingPolicy.BuildWords ->
                    Icon(Icons.Rounded.Extension, contentDescription = "Word game", tint = TextMidnight, modifier = Modifier.size(56.dp))
                ReturnGreetingPolicy.AllDone ->
                    Icon(Icons.Rounded.Star, contentDescription = "All the sounds", tint = SunnyGold, modifier = Modifier.size(64.dp))
            }
        }
        val image = card.imagePath
        if (card.offer is ReturnGreetingPolicy.KeepGoing && image != null) {
            GummyMotionAsset(
                assetPath = image,
                contentDescription = card.keyWord,
                modifier = Modifier.size(104.dp)
            )
        }
    }
}
