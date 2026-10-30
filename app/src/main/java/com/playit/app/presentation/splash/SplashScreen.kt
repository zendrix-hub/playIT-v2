package com.playit.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.rememberVoiceMouth
import com.playit.app.presentation.theme.ApricotGlow
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.CoralBerry
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LilyOutline
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.SnigletFontFamily
import com.playit.app.presentation.theme.SunnyGold
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
 * The first screen: Lily, drawn in parts as in her pictures (LilyPuppet.kt), rises
 * from behind a white panel and rests her hands on its edge. The Start button sits in the panel, in the
 * thumb's reach, and never covers her face (card 31a).
 *
 * Motion: the opening (SplashOpening.kt); then she breathes with her belly, her tail sways, and she blinks,
 * glances about, twitches an ear and tilts her head at odd moments. A tap gets a giggle, an ear wiggle, a
 * wink or a hop, in turn. With reduced motion she stays still and a tap is an instant wink. Only the draw
 * phase reads the animated values, so her life redraws the drawing without recomposing the screen.
 *
 * Sound: [onSound] plays each of the opening's sounds as its beat comes (OPENING_CUES); with reduced motion,
 * the voice once. [titleEffect] plays round the title after its wave: a different one each time the app opens.
 */
@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {},
    onSound: (OpeningCue) -> Unit = {},
    titleEffect: TitleEffect = TitleEffect.TWINKLE
) {
    val reducedMotion = LocalReducedMotion.current
    val motion = splashMotion(reducedMotion)

    // Her breath (3.8 s) and her tail's sway (5.2 s), as phases 0..1 that never fall in step. With reduced motion
    // there is no animation at all.
    val breath: State<Float>
    val sway: State<Float>
    if (motion.breathes) {
        val idle = rememberInfiniteTransition(label = "SplashIdle")
        breath = idle.animateFloat(0f, 1f, infiniteRepeatable(tween(3800, easing = LinearEasing)), label = "Breath")
        sway = idle.animateFloat(0f, 1f, infiniteRepeatable(tween(5200, easing = LinearEasing)), label = "Sway")
    } else {
        breath = remember { mutableFloatStateOf(0f) }
        sway = remember { mutableFloatStateOf(0f) }
    }

    val eyes = remember(reducedMotion) { LilyEyes(instant = reducedMotion) }
    val life = remember { LilyLife() }
    val voice = rememberVoiceMouth()
    val scope = rememberCoroutineScope()
    val onLilyTap: () -> Unit = { scope.launch { if (reducedMotion) eyes.winkOnce() else life.react(eyes) } }
    // For the little letters of the title's effect, measured once each in the logo's font.
    val measurer = rememberTextMeasurer()

    // The opening (SplashOpening.kt): one clock, in ms, started once the screen draws smoothly. Every part reads it
    // only while drawing, so the opening redraws layers but never recomposes the screen.
    val opening = remember { Animatable(if (reducedMotion) OPENING_END_MS.toFloat() else 0f) }
    val playSound by rememberUpdatedState(onSound)
    LaunchedEffect(Unit) {
        if (reducedMotion) {
            playSound(STILL_OPENING_CUE)
            return@LaunchedEffect
        }
        awaitSmoothFrames()
        OPENING_CUES.forEach { cue -> launch { delay(cue.atMs.toLong()); playSound(cue) } }
        launch { delay(LILY_BLINK_MS); eyes.blinkOnce() }   // as she peeks
        launch { delay(LILY_WINK_MS); eyes.winkOnce() }     // the last beat: hello
        opening.animateTo(OPENING_END_MS.toFloat(), tween(OPENING_END_MS, easing = LinearEasing))
        // Her own little life from now on, never on a beat. A tap may interrupt a move; the loop carries on.
        if (!motion.blinks) return@LaunchedEffect
        val r = Random.Default
        launch { while (true) { delay(nextBlinkMs(r)); keepGoing { eyes.blinkOnce(); if (r.nextFloat() < 0.2f) eyes.blinkOnce() } } }
        launch { while (true) { delay(nextGlanceMs(r)); keepGoing { life.glance(r) } } }
        launch { while (true) { delay(nextEarTwitchMs(r)); keepGoing { life.twitchEar(r) } } }
        launch { while (true) { delay(nextTiltMs(r)); keepGoing { life.tiltHead(r) } } }
    }
    val clock: () -> Float = { opening.value }

    // Everything that moves her, read only while she is drawn.
    val pose: () -> LilyPose = {
        val ms = opening.value
        val b = 2f * PI.toFloat() * breath.value
        val breathing = if (motion.breathes) 1f else 0f
        val hop = tapHopAt(life.hop.value)
        LilyPose(
            gaze = if (ms < OPENING_END_MS) openingGazeAt(ms) else Offset(life.gazeX.value, life.gazeY.value),
            earOut = earPerkAt(ms) + breathing * 1.5f * sin(b - 1.4f),   // the ears follow the breath a moment late
            earL = life.earL.value,
            earR = life.earR.value,
            tilt = life.tilt.value,
            headBob = breathing * 0.004f * sin(b - 0.8f),
            bellyBreath = breathing * sin(b),
            tailBend = breathing * 5f * sin(2f * PI.toFloat() * sway.value) + tailLagAt(ms) + 15f * hop.first / 0.06f,
            happy = life.happy.value,
            hopLift = hop.first,
            hopSquash = hop.second
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Sky, Sand)))
    ) {
        // The clouds, in their own layer so their drift redraws only them.
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer()
                .drawBehind { drawOpeningClouds(clock()) }
        )


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
                    // One Text per letter so each can pop up; read out as one word. The title's effect draws round it.
                    Row(
                        modifier = Modifier
                            .graphicsLayer()
                            .drawWithContent {
                                drawContent()
                                drawTitleEffect(titleEffect, clock(), measurer, EffectLetterStyle, LogoColors, LilyOutline, 2.dp.toPx())
                            }
                            .clearAndSetSemantics { contentDescription = TITLE }
                    ) {
                        TITLE.forEachIndexed { i, letter ->
                            LogoLetter(
                                letter = letter,
                                color = LogoColors[i % LogoColors.size],
                                modifier = Modifier.graphicsLayer {
                                    val f = letterAt(i, clock())
                                    translationY = -f.lift * LETTER_LIFT_DP.dp.toPx()
                                    scaleX = f.scaleX
                                    scaleY = f.scaleY
                                    alpha = f.alpha
                                    rotationZ = f.rotation
                                    transformOrigin = TransformOrigin(0.5f, 1f)   // it grows and wobbles from the line it stands on
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mabuhay! Ready to learn to read?",
                        // Balanced lines, so a word is never left alone on the second.
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = SnigletFontFamily, fontWeight = FontWeight.Normal, lineBreak = LineBreak.Heading),
                        maxLines = 2,
                        color = Ink,
                        textAlign = TextAlign.Center,
                        // Wiped in from the left as it rises a little; never through the title. A plain clip: a soft
                        // edge drawn with a blend mode showed as a black box where the text was still hidden.
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = taglineRiseAt(clock()) * TAGLINE_RISE_DP.dp.toPx()
                            }
                            .drawWithContent {
                                val shown = taglineAt(clock())
                                if (shown >= 1f) drawContent()
                                else if (shown > 0f) clipRect(right = shown * size.width) { this@drawWithContent.drawContent() }
                            }
                    )
                }
            }

            LilyPuppet(
                eyes = eyes,
                voice = voice,
                ledge = PanelOverlap,
                pose = pose,
                modifier = Modifier
                    .size(lilyWidth, lilyHeight)
                    .graphicsLayer {
                        // Opening: she peeks over the panel, crouches, jumps above her place and lands (lilyAt).
                        val f = lilyAt(clock())
                        scaleX = f.scaleX
                        scaleY = f.scaleY
                        translationY = (1f - f.shown) * size.height
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Play with Lily",
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
            clock = clock,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * The logo (user, 2026-10-29: the opening's fonts "same as the pictures, cartoonish"): Sniglet, each letter in one of
 * the island colours in turn, in the pictures' dark-brown outline, with a little depth below like their shadow side.
 */
private val LogoColors = listOf(PrimaryJoy, ApricotGlow, EmeraldLeaf, CoralBerry, SunnyGold)
private val LogoSize = 56.sp
/** The little letters of the title's effect: the logo's font, measured at this size and scaled as they fly. */
private val EffectLetterStyle = TextStyle(fontFamily = SnigletFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
private val LogoOutline = 4.dp
private val LogoDepth = 4.dp

/** One letter of the logo: its depth, its outline and its colour, drawn in that order. */
@Composable
private fun LogoLetter(letter: Char, color: Color, modifier: Modifier = Modifier) {
    val base = TextStyle(fontFamily = SnigletFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = LogoSize, lineHeight = LogoSize * 1.15f)
    // A stroke is centred on the letter's edge, and the colour covers its inner half.
    val outline = Stroke(width = with(LocalDensity.current) { (LogoOutline * 2).toPx() }, join = StrokeJoin.Round)
    val text = letter.toString()
    Box(modifier = modifier) {
        Text(text, style = base.copy(color = LilyOutline), maxLines = 1, modifier = Modifier.offset(y = LogoDepth))
        Text(text, style = base.copy(color = LilyOutline, drawStyle = outline), maxLines = 1, modifier = Modifier.offset(y = LogoDepth))
        Text(text, style = base.copy(color = LilyOutline, drawStyle = outline), maxLines = 1)
        Text(text, style = base.copy(color = color), maxLines = 1)
    }
}

/** The Start button face's corner: GummyButton's 32 dp, a pill at 64 dp tall. */
private val StartCorner = 32.dp

/** The white panel Lily peeks over: the Start button inside, her hands on its top edge. */
@Composable
private fun StartPanel(lilyWidth: Dp, onStartClick: () -> Unit, clock: () -> Float, modifier: Modifier = Modifier) {
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
                    // Opening: it pops in, then a shine crosses it once to say: tap me.
                    .graphicsLayer {
                        val pop = startPopAt(clock())
                        scaleX = pop.scale
                        scaleY = pop.scale
                        alpha = pop.alpha
                    }
                    .drawWithContent {
                        drawContent()
                        shineAt(clock())?.let { drawShine(it, StartCorner.toPx()) }
                    }
                    .testTag("splash_start")
            )
        }
        LilyHands(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(width = lilyWidth * HAND_SPAN, height = 30.dp)
                // Her hands drop onto the edge and grab it as she lands.
                .graphicsLayer {
                    val f = pawsAt(clock())
                    translationY = -f.lift * PAWS_DROP_DP.dp.toPx()
                    scaleX = f.scaleX
                    scaleY = f.scaleY
                    alpha = f.alpha
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
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

/** Runs a move of hers; if a tap's move takes over the same part, the interrupted move just stops. */
private suspend fun keepGoing(move: suspend () -> Unit) {
    try {
        move()
    } catch (e: CancellationException) {
        if (!currentCoroutineContext().isActive) throw e
    }
}
