package com.playit.app.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.*

@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashCloudAnim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudDrift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyDeep,
                        Sky,
                        Sand,
                        SandDeep
                    )
                )
            )
    ) {
        // Bohol Chocolate Hills bottom landscape silhouette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hillWidths = listOf(90.dp, 125.dp, 105.dp, 140.dp, 100.dp, 120.dp)
            hillWidths.forEachIndexed { index, width ->
                Box(
                    modifier = Modifier
                        .size(width = width, height = width * 0.65f)
                        .offset(x = if (index == 0) 0.dp else ((-20) * index).dp)
                        .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
                        .background(
                            if (index % 2 == 0) Tan.copy(alpha = 0.45f) else TanDark.copy(alpha = 0.35f)
                        )
                )
            }
        }

        // Floating ambient cloud elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (20 + cloudOffset).dp, y = 40.dp)
                    .size(width = 80.dp, height = 30.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Cloud.copy(alpha = 0.45f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-30 - cloudOffset).dp, y = 70.dp)
                    .size(width = 95.dp, height = 34.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Cloud.copy(alpha = 0.4f))
            )
        }

        // Main Layout Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable hero + value-prop content. The CTA below stays outside this
            // scroll so it's always reachable even where the pediatric font floor
            // pushes this section past a shorter device's viewport.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Upper Section: Speech Bubble + Mascot Hero + Wordmark
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 3D Comic Speech Bubble Greeting
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .background(Cloud)
                            .border(
                                width = 2.5.dp,
                                color = DarkBrownOutline,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Mabuhay! Handa ka na bang matuto?",
                                fontFamily = LexendFontFamily,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Ink,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Welcome! Ready to learn to read?",
                                fontFamily = LexendFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = InkSoft,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hero Full-Body Standing Lily Tarsier
                    Box(
                        modifier = Modifier
                            .size(width = 135.dp, height = 150.dp)
                            .breathingPulse(enabled = true)
                            .idleBounce(enabled = true),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAssetPainter(MascotState.WAVING.assetPath),
                            contentDescription = "Lily the Tarsier Mascot",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // PlayIT Wordmark
                    Text(
                        text = "PlayIT",
                        fontFamily = LexendFontFamily,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Curriculum & Methodology Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Ube.copy(alpha = 0.15f))
                            .border(
                                width = 2.dp,
                                color = DarkBrownOutline.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Grade 1 · Marungko Phonics",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ube
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Middle Section: Duolingo ABC 3-Pillar Value Bento Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeaturePill(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        iconBg = Ube.copy(alpha = 0.25f),
                        iconTint = Ube,
                        title = "Pakinggan at Sabihin • Hear & Say",
                        subtitle = "Sound recognition & speech practice"
                    )
                    FeaturePill(
                        icon = Icons.Rounded.Extension,
                        iconBg = Mango.copy(alpha = 0.25f),
                        iconTint = DarkBrownOutline,
                        title = "Pagsamahin ang Tunog • Blend Words",
                        subtitle = "Tactile syllable & word building"
                    )
                    FeaturePill(
                        icon = Icons.Rounded.Star,
                        iconBg = Leaf.copy(alpha = 0.20f),
                        iconTint = Leaf,
                        title = "Maglaro at Matuto • Play & Master",
                        subtitle = "Earn stars and progress along the map"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Section: Sticky Gummy Button CTA (outside the scroll, always visible)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GummyButton(
                    text = "Tara, Simulan Na! • Get Started",
                    onClick = onStartClick,
                    backgroundColor = Mango,
                    shadowColor = MangoShadow,
                    contentColor = Ink,
                    fontSize = 24,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 350.dp)
                        .heightIn(min = 64.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Cloud)
            .border(
                width = 2.dp,
                color = DarkBrownOutline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = subtitle,
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = InkSoft
                )
            }
        }
    }
}
