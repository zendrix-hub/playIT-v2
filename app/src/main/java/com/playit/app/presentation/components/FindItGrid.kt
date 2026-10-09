package com.playit.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.manager.FindItPictureItem
import com.playit.app.domain.model.Phoneme
import com.playit.app.presentation.theme.*

@Composable
fun FindItCard(
    item: FindItPictureItem,
    borderColor: Color,
    faceColor: Color = SurfaceCard,
    index: Int,
    isCorrect: Boolean = false,
    isIncorrect: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rotationAngle = remember(index) { ((index * 37) % 5 - 2).toFloat() }

    GummyContainer(
        onClick = onClick,
        faceColor = faceColor,
        shadowColor = faceColor.deriveShadow(),
        shape = Squircle20,
        strokeWidth = 2.5.dp,
        strokeColor = borderColor,
        depthHeight = 5.dp,
        isSquashed = isCorrect,
        modifier = modifier
            .graphicsLayer { rotationZ = rotationAngle }
            .shake(trigger = isIncorrect)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // The picture takes the height the word leaves, up to 120 dp.
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .sizeIn(maxWidth = 120.dp, maxHeight = 120.dp)
                        .aspectRatio(1f)
                ) {
                    // Ambient backing circle
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.87f)
                            .background(
                                color = PrimaryJoy.copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                    )

                    GummyMotionAsset(
                        assetPath = item.imagePath,
                        contentDescription = item.word,
                        floatDistance = 3.dp,
                        celebrateTrigger = isCorrect,
                        modifier = Modifier.fillMaxSize(0.84f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.word,
                    fontFamily = LexendFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMidnight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Emerald Checkmark Badge when Found
            if (isCorrect) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp)
                        .background(EmeraldLeaf, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Found",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * The Find It picture grid: 2 columns, the 5th card centred at the same width. Card height
 * comes from the profile (min height and width/height ratio), so 3 rows fit on short phones.
 */
@Composable
fun FindItGrid(
    items: List<FindItPictureItem>,
    modifier: Modifier = Modifier,
    card: @Composable (item: FindItPictureItem, index: Int, modifier: Modifier) -> Unit
) {
    val d = LocalPlayItDimens.current
    Column(
        modifier = modifier.fillMaxWidth().testTag("findit_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(2).forEachIndexed { rowIndex, row ->
            Row(
                // A lone 5th card: two half-weight spacers with 6 dp gaps give it the same width as the others.
                horizontalArrangement = Arrangement.spacedBy(if (row.size == 1) 6.dp else 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (row.size == 1) Spacer(Modifier.weight(0.5f))
                row.forEachIndexed { i, item ->
                    card(
                        item,
                        rowIndex * 2 + i,
                        Modifier
                            .weight(1f)
                            .heightIn(min = d.findItCardMinHeight)
                            .aspectRatio(d.findItCardAspect)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(0.5f))
            }
        }
    }
}

/**
 * Legacy overload for backward compatibility with existing tests
 */
@Composable
fun FindItCard(
    phoneme: Phoneme,
    borderColor: Color,
    faceColor: Color = CreamWhite,
    index: Int,
    isCorrect: Boolean = false,
    isIncorrect: Boolean = false,
    onClick: () -> Unit
) {
    val word = phoneme.exampleWord.lowercase()
    val assetPath = "images/pictures/picture_$word.png"
    val item = FindItPictureItem(
        id = phoneme.id.toString(),
        phonemeLetter = phoneme.letter,
        word = phoneme.exampleWord,
        imagePath = assetPath,
        isCorrect = isCorrect
    )
    FindItCard(
        item = item,
        borderColor = borderColor,
        faceColor = faceColor,
        index = index,
        isCorrect = isCorrect,
        isIncorrect = isIncorrect,
        onClick = onClick
    )
}
