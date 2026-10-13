package com.playit.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LilyBlush
import com.playit.app.presentation.theme.LilyCream
import com.playit.app.presentation.theme.LilyEarInner
import com.playit.app.presentation.theme.LilyOrange
import com.playit.app.presentation.theme.LilyOrangeShade
import com.playit.app.presentation.theme.LilyOutline
import com.playit.app.presentation.theme.LilyPupil
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fraction of Lily's drawing height where her face ends (the bottom of her smile). The start panel and
 * her paws stay below it on every screen size (card 31a; checked by LayoutMatrixTest).
 */
internal const val LILY_FACE_BOTTOM = 0.66f

/** Width over height of Lily's drawing. */
private const val LILY_ASPECT = 0.92f

/** What moves on the splash by itself. With reduced motion nothing does; a tap still makes Lily wink, instantly. */
internal data class SplashMotion(val breatheY: Float, val breatheX: Float, val blinks: Boolean) {
    val breathes: Boolean get() = breatheY != 1f || breatheX != 1f
}

internal fun splashMotion(reducedMotion: Boolean): SplashMotion =
    if (reducedMotion) SplashMotion(breatheY = 1f, breatheX = 1f, blinks = false)
    else SplashMotion(breatheY = 1.03f, breatheX = 1.012f, blinks = true)

/** How long a wink holds before the eye opens again. */
internal const val WINK_HOLD_MS = 450L

/**
 * Lily's eyes, 0 = open and 1 = closed. Both eyes blink on their own; a tap winks only her right eye.
 * With [instant] (reduced motion) a wink snaps shut and open instead of animating.
 */
internal class LilyEyes(private val instant: Boolean) {
    val blink = Animatable(0f)
    val wink = Animatable(0f)

    suspend fun blinkOnce() {
        blink.animateTo(1f, tween(120, easing = LinearEasing))
        delay(80L)
        blink.animateTo(0f, tween(120, easing = LinearEasing))
    }

    suspend fun winkOnce() {
        if (instant) {
            wink.snapTo(1f); delay(WINK_HOLD_MS); wink.snapTo(0f)
        } else {
            wink.animateTo(1f, tween(90)); delay(WINK_HOLD_MS); wink.animateTo(0f, tween(180))
        }
    }

    /** How closed one eye is: her right eye (side > 0) also closes for a wink. */
    fun closed(side: Float): Float =
        (if (side > 0f) maxOf(blink.value, wink.value) else blink.value).coerceIn(0f, 1f)
}

/** The start panel: a 64 dp button with 20 dp above and below. */
private val PanelContent = 104.dp

/** Lily stands behind the panel; its top edge covers this much of her body. */
private val PanelOverlap = 28.dp

private val PanelCorner = 32.dp

/**
 * The first screen: Lily, drawn in the Headspace style (one soft dome, flat colour, no detail she does
 * not need), rises from behind a white panel and rests her paws on its edge. The Start button sits in the
 * panel, in the thumb's reach, and never covers her face (card 31a).
 *
 * Motion: she breathes slowly, blinks every few seconds and winks when tapped. With reduced motion she
 * stays still and the wink is instant. Only the draw phase reads the animated values, so a blink redraws
 * the drawing without recomposing the screen.
 */
@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {}
) {
    val reducedMotion = LocalReducedMotion.current
    val motion = splashMotion(reducedMotion)

    // Breathing: a slow 3.8 s rise and fall from her feet. With reduced motion there is no animation at all.
    val breatheY: State<Float>
    val breatheX: State<Float>
    if (motion.breathes) {
        val breathe = rememberInfiniteTransition(label = "SplashBreathe")
        val spec = infiniteRepeatable<Float>(tween(3800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
        breatheY = breathe.animateFloat(1f, motion.breatheY, spec, label = "BreatheY")
        breatheX = breathe.animateFloat(1f, motion.breatheX, spec, label = "BreatheX")
    } else {
        breatheY = remember { mutableFloatStateOf(1f) }
        breatheX = remember { mutableFloatStateOf(1f) }
    }

    val eyes = remember(reducedMotion) { LilyEyes(instant = reducedMotion) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(eyes, motion.blinks) {
        if (!motion.blinks) return@LaunchedEffect
        while (true) {
            delay(3600L)
            eyes.blinkOnce()
        }
    }
    val onLilyTap: () -> Unit = { scope.launch { eyes.winkOnce() } }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Sky, Sand)))
    ) {
        // As big as the screen allows: at most 55% of its height, 400 dp tall, and its width less a margin.
        val lilyHeight = minOf(maxHeight * 0.55f, 400.dp, (maxWidth - 24.dp) / LILY_ASPECT)
        val lilyWidth = lilyHeight * LILY_ASPECT

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The title is centred in the space above Lily, so on a tall screen it stays near her.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PlayIT",
                        style = MaterialTheme.typography.displayLarge,
                        maxLines = 1,
                        color = Ink,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mabuhay! Ready to learn to read?",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        color = InkSoft,
                        textAlign = TextAlign.Center
                    )
                }
            }

            HeadspaceTarsierCanvas(
                eyes = eyes,
                modifier = Modifier
                    .size(lilyWidth, lilyHeight)
                    .graphicsLayer {
                        scaleX = breatheX.value
                        scaleY = breatheY.value
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Wink",
                        onClick = onLilyTap
                    )
                    .semantics { contentDescription = "Lily the tarsier" }
                    .testTag("splash_lily")
            )

            // Lily's lower body goes behind the panel: she stops PanelOverlap above the panel's bottom edge.
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(PanelContent - PanelOverlap)
            )
        }

        StartPanel(
            lilyWidth = lilyWidth,
            onStartClick = onStartClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/** The white panel Lily peeks over: the Start button inside, her paws on its top edge. */
@Composable
private fun StartPanel(lilyWidth: Dp, onStartClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Cloud, RoundedCornerShape(topStart = PanelCorner, topEnd = PanelCorner))
                .drawBehind { drawTopOutline(LilyOutline, 2.5.dp.toPx(), PanelCorner.toPx()) }
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .testTag("splash_panel")
        ) {
            GummyButton(
                text = "Start",
                onClick = onStartClick,
                backgroundColor = Leaf,
                shadowColor = LeafShadow,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .testTag("splash_start")
            )
        }
        TarsierPaws(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(width = lilyWidth * 0.62f, height = 30.dp)
        )
    }
}

/** The panel's outline along its top edge only, round corners included (its sides meet the screen edges). */
private fun DrawScope.drawTopOutline(color: Color, width: Float, radius: Float) {
    val half = width / 2f
    val path = Path().apply {
        moveTo(half, radius)
        arcTo(Rect(half, half, 2 * radius - half, 2 * radius - half), 180f, 90f, false)
        lineTo(size.width - radius, half)
        arcTo(Rect(size.width - 2 * radius + half, half, size.width - half, 2 * radius - half), 270f, 90f, false)
    }
    drawPath(path, color, style = Stroke(width = width, cap = StrokeCap.Round))
}

/**
 * Lily as one soft dome: ears, a squircle head and body (a superellipse, n = 2.6, between a circle and a
 * square), cream eye patches, dark-brown pupils with two catchlights, pink cheeks, a small nose, a smile,
 * and her cream belly. Colours and the dark-brown outline come from her pictures (LilyOrange and friends).
 * The shapes are built once per size; a blink or a wink only redraws.
 */
@Composable
private fun HeadspaceTarsierCanvas(
    eyes: LilyEyes,
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .clipToBounds()
            .drawWithCache {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val line = maxOf(2.5.dp.toPx(), w * 0.010f)

                val earR = w * 0.135f
                val ears = listOf(-1f, 1f).map { side -> side to Offset(cx + side * w * 0.33f, h * 0.28f) }
                val domeB = h * 0.64f
                val dome = squirclePath(Offset(cx, h * 0.20f + domeB), w * 0.40f, domeB, 2.6f)
                val shine = Offset(cx - w * 0.17f, h * 0.285f)

                val eyeY = h * 0.45f
                val eyeDx = w * 0.15f
                val ringR = w * 0.105f
                val pupilR = w * 0.062f
                val smileW = w * 0.15f
                val smileH = w * 0.07f
                val smileTop = h * 0.578f

                onDrawBehind {
                    // 1. Ears first, so the head covers their inner edge.
                    for ((side, c) in ears) {
                        drawCircle(LilyOrange, earR, c)
                        drawCircle(LilyEarInner, earR * 0.62f, c + Offset(-side * earR * 0.12f, earR * 0.08f))
                        drawCircle(LilyOutline, earR, c, style = Stroke(line))
                    }

                    // 2. Head and body. The art's darker orange shows as a thin crescent down her right side,
                    // which gives the flat dome a little volume; the clip keeps it inside her outline.
                    clipPath(dome) {
                        drawPath(dome, LilyOrangeShade)
                        translate(left = -w * 0.03f, top = -h * 0.015f) { drawPath(dome, LilyOrange) }
                    }
                    drawPath(dome, LilyOutline, style = Stroke(line))

                    // 3. Belly and a glossy highlight, as in her pictures.
                    drawOval(LilyCream, Offset(cx - w * 0.21f, h * 0.80f), Size(w * 0.42f, h * 0.34f))
                    rotate(-24f, shine) {
                        drawOval(Color.White.copy(alpha = 0.55f), shine - Offset(w * 0.045f, h * 0.016f), Size(w * 0.09f, h * 0.032f))
                    }

                    // 4. Eyes: closed amount 0..1; her right eye also closes for a wink.
                    for (side in listOf(-1f, 1f)) {
                        val c = Offset(cx + side * eyeDx, eyeY)
                        drawCircle(LilyCream, ringR, c)
                        val closed = eyes.closed(side)
                        val open = 1f - closed
                        if (open > 0f) {
                            val r = pupilR * (0.35f + 0.65f * open)
                            drawCircle(LilyPupil.copy(alpha = open), r, c)
                            drawCircle(Color.White.copy(alpha = open), r * 0.34f, c + Offset(-r * 0.32f, -r * 0.34f))
                            drawCircle(Color.White.copy(alpha = open), r * 0.15f, c + Offset(r * 0.36f, r * 0.30f))
                        }
                        if (closed > 0f) {
                            val arc = Path().apply {
                                arcTo(Rect(c.x - ringR * 0.62f, c.y - ringR * 0.30f, c.x + ringR * 0.62f, c.y + ringR * 0.30f), 10f, 160f, true)
                            }
                            drawPath(arc, LilyPupil.copy(alpha = closed), style = Stroke(line * 1.3f, cap = StrokeCap.Round))
                        }
                    }

                    // 5. Cheeks, nose and smile.
                    for (side in listOf(-1f, 1f)) {
                        drawOval(LilyBlush.copy(alpha = 0.85f), Offset(cx + side * w * 0.255f - w * 0.055f, h * 0.555f), Size(w * 0.11f, h * 0.045f))
                    }
                    drawOval(LilyPupil, Offset(cx - w * 0.017f, h * 0.545f), Size(w * 0.034f, w * 0.024f))
                    val smile = Path().apply {
                        arcTo(Rect(cx - smileW / 2f, smileTop, cx + smileW / 2f, smileTop + smileH), 20f, 140f, true)
                    }
                    drawPath(smile, LilyOutline, style = Stroke(line * 1.2f, cap = StrokeCap.Round))
                }
            }
    )
}

/** Two paws resting on the panel's edge, fingers over it, as if Lily peeks over a wall. */
@Composable
private fun TarsierPaws(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height
            val line = 2.5.dp.toPx()
            val pawW = minOf(w * 0.26f, 46.dp.toPx())
            val pawH = h * 0.86f
            val top = (h - pawH) / 2f
            val lefts = listOf(w * 0.18f, w * 0.82f).map { it - pawW / 2f }
            onDrawBehind {
                for (left in lefts) {
                    drawRoundRect(LilyOrange, Offset(left, top), Size(pawW, pawH), CornerRadius(pawH / 2f))
                    drawRoundRect(LilyOutline, Offset(left, top), Size(pawW, pawH), CornerRadius(pawH / 2f), style = Stroke(line))
                    for (k in 1..2) {
                        val x = left + pawW * k / 3f
                        drawLine(LilyOutline, Offset(x, top + pawH * 0.58f), Offset(x, top + pawH * 0.92f), strokeWidth = line * 0.8f, cap = StrokeCap.Round)
                    }
                }
            }
        }
    )
}

/** A superellipse |x/a|^n + |y/b|^n = 1 around [center]: n = 2 is an ellipse, higher n is more square. */
private fun squirclePath(center: Offset, a: Float, b: Float, n: Float, steps: Int = 120): Path {
    val path = Path()
    val e = 2.0 / n
    for (i in 0..steps) {
        val t = 2.0 * PI * i / steps
        val ct = cos(t)
        val st = sin(t)
        val x = center.x + a * (sign(ct) * abs(ct).pow(e)).toFloat()
        val y = center.y + b * (sign(st) * abs(st).pow(e)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
