package com.playit.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.*

enum class LessonStep(val stepIndex: Int) {
    HEAR_IT(0),
    SAY_IT(1),
    FIND_IT(2),
    BLEND_IT(3)
}

/**
 * Clean 3-segment capsule lesson top bar matching Duolingo ABC / playit-mockup.html.
 * Renders back button, segmented progress pill capsules, and optional heart bar.
 */
@Composable
fun LessonTopBar(
    currentStep: LessonStep,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    hearts: Int? = null,
    maxHearts: Int = 3
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        GummyBackButton(onClick = onBack)

        // Segmented Capsule Progress Bar
        Row(
            modifier = Modifier
                .weight(1f)
                .height(9.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val stepsToRender = if (currentStep == LessonStep.BLEND_IT) {
                listOf(LessonStep.BLEND_IT)
            } else {
                listOf(LessonStep.HEAR_IT, LessonStep.SAY_IT, LessonStep.FIND_IT)
            }

            for (step in stepsToRender) {
                val isCompleted = step.stepIndex < currentStep.stepIndex
                val isActive = step.stepIndex == currentStep.stepIndex

                val targetColor = when {
                    isCompleted -> EmeraldLeaf
                    isActive -> PrimaryJoy
                    else -> ModernBorderFaint
                }

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 350),
                    label = "pillColor_${step.name}"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(PillShape)
                        .background(animatedColor)
                )
            }
        }

        // Hearts Status Indicator
        if (hearts != null) {
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(SurfaceCard)
                    .border(1.dp, ModernBorderSoft, PillShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..maxHearts) {
                    val isAlive = i <= hearts
                    Image(
                        painter = rememberAssetPainter("images/rewards/reward_heart.png"),
                        contentDescription = if (isAlive) "Heart Active" else "Heart Lost",
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { alpha = if (isAlive) 1f else 0.22f }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
