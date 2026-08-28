package com.playit.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AndikaFontFamily
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.SandShadow
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeDark

/**
 * High-fidelity 3D Gummy Letter Card — GummyContainer-backed flashcard overlaying
 * dynamic Lexend typography on top of letter illustration PNG assets
 * (picture_<lowercase_word>.png, loaded from assets/images/pictures/) in Z-index order.
 *
 * Implements 24sp child reading floor, Headspace gentle breathingPulse, and Lexend + Andika fonts.
 */
@Composable
fun LetterCard(
    letter: String,
    soundText: String,
    modifier: Modifier = Modifier,
    cardRotation: Float = 0f,
    wordOverride: String? = null,
    onTapReplay: () -> Unit = {}
) {
    val letterMap = mapOf(
        "a" to "apple", "b" to "ball", "c" to "cat", "d" to "dog",
        "e" to "elephant", "f" to "fish", "g" to "goat", "h" to "hat",
        "i" to "insect", "j" to "jug", "k" to "kite", "l" to "lion",
        "m" to "mouse", "n" to "nest", "o" to "orange", "p" to "pig",
        "q" to "queen", "r" to "rabbit", "s" to "sun",
        "t" to "tiger", "u" to "umbrella", "v" to "van", "w" to "watch",
        "x" to "xylophone",
        "y" to "yoyo", "z" to "zebra"
    )

    val word = wordOverride ?: letterMap[letter.lowercase()] ?: "apple"
    val displayLetter = if (letter.length == 1) "${letter.uppercase()}${letter.lowercase()}" else letter.uppercase()
    val displayWord = if (word.contains("is for", ignoreCase = true)) word else "${letter.uppercase()} is for ${word.replaceFirstChar { it.uppercase() }}"

    GummyContainer(
        onClick = onTapReplay,
        faceColor = Sand,
        shadowColor = SandShadow,
        shape = RoundedCornerShape(28.dp),
        strokeWidth = 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 6.dp,
        modifier = modifier
            .width(280.dp)
            .height(290.dp)
            .padding(horizontal = 8.dp)
            .graphicsLayer { rotationZ = cardRotation }
            .breathingPulse()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Procedural decorative vector background
            androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension * 0.42f

                // Ambient pastel halo
                drawCircle(
                    color = Ube.copy(alpha = 0.08f),
                    radius = radius,
                    center = centerOffset
                )

                // Decorative corner accent bubbles
                drawCircle(
                    color = Mango.copy(alpha = 0.25f),
                    radius = 8.dp.toPx(),
                    center = Offset(size.width * 0.12f, size.height * 0.18f)
                )
                drawCircle(
                    color = Leaf.copy(alpha = 0.25f),
                    radius = 7.dp.toPx(),
                    center = Offset(size.width * 0.88f, size.height * 0.20f)
                )
                drawCircle(
                    color = Guava.copy(alpha = 0.2f),
                    radius = 6.dp.toPx(),
                    center = Offset(size.width * 0.88f, size.height * 0.82f)
                )
                drawCircle(
                    color = Mango.copy(alpha = 0.25f),
                    radius = 9.dp.toPx(),
                    center = Offset(size.width * 0.12f, size.height * 0.82f)
                )
            }

            // Dynamic Content layer with Illustration & Lexend/Andika Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val pictureAsset = "images/pictures/picture_${word.lowercase()}.png"
                GummyMotionAsset(
                    assetPath = pictureAsset,
                    contentDescription = displayWord,
                    isIdleFloating = true,
                    floatDistance = 5.dp,
                    modifier = Modifier.size(92.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayLetter,
                    fontFamily = LexendFontFamily,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UbeDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayWord,
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkSoft
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = soundText,
                    fontFamily = AndikaFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ube
                )
            }
        }
    }
}
