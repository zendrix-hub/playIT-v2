package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.TextSecondary
import com.playit.app.presentation.theme.Typography

/**
 * High-fidelity 3D Gummy Letter Card — GummyContainer-backed flashcard overlaying
 * dynamic Lexend typography on top of letter illustration PNG assets
 * (word_<lowercase_word>.png, loaded from assets/images/letters/) in Z-index order.
 *
 * This is a "Learning Card," not a letter tile or map node, so per
 * 23_DUOLINGO_ABC_UI_REFRESH.md §3 it keeps its 28dp rounded-corner shape rather than
 * becoming a circle (that treatment is reserved for BlendIt's small LetterTile and
 * MapScreen's LetterNode).
 *
 * Tapping the card replays the phoneme audio. The tap feedback is GummyContainer's
 * built-in press-into-depth spring (23_DUOLINGO_ABC_UI_REFRESH.md §4), which is the
 * refresh doc's explicit replacement for the older flat scale-bounce spec in
 * 21_ANIMATION_GUIDE.md §3 for every gummy-styled tappable surface — see chat note.
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
        "q" to "queen with a crown", "r" to "rabbit", "s" to "sun",
        "t" to "tiger", "u" to "umbrella", "v" to "van", "w" to "watch",
        "y" to "yoyo", "z" to "zebra"
    )

    val word = wordOverride ?: letterMap[letter.lowercase()] ?: "apple"
    val assetPath = "images/letters/word_$word.png"

    GummyContainer(
        onClick = onTapReplay,
        faceColor = CreamWhite,
        shadowColor = CreamWhiteShadow,
        shape = RoundedCornerShape(28.dp),
        strokeWidth = 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 6.dp,
        modifier = modifier
            .size(240.dp)
            .graphicsLayer { rotationZ = cardRotation }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background illustration asset from assets/images/letters/
            Image(
                painter = rememberAssetPainter(assetPath),
                contentDescription = "$word illustration",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .graphicsLayer { alpha = 0.35f }
            )

            // Dynamic Lexend Typography layer rendered directly above illustration (Z-index top)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = letter.uppercase(),
                    style = Typography.displayLarge.copy(fontSize = 100.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = LearningBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = soundText,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AchievementGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap to hear it again! 🔊",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }
        }
    }
}
