package com.playit.app.presentation.components

import android.content.Context
import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.data.audio.VoiceClip
import com.playit.app.domain.model.articulationFor
import com.playit.app.presentation.components.lipsync.BLINK_MS
import com.playit.app.presentation.components.lipsync.HOP_MS
import com.playit.app.presentation.components.lipsync.Mouth
import com.playit.app.presentation.components.lipsync.PoseEyes
import com.playit.app.presentation.components.lipsync.PoseMouth
import com.playit.app.presentation.components.lipsync.TALKING_WIDE
import com.playit.app.presentation.components.lipsync.VoiceEnvelopes
import com.playit.app.presentation.components.lipsync.articulationMouth
import com.playit.app.presentation.components.lipsync.blinkClosed
import com.playit.app.presentation.components.lipsync.hopAt
import com.playit.app.presentation.components.lipsync.phonemeLetter
import com.playit.app.presentation.components.lipsync.poseEyes
import com.playit.app.presentation.components.lipsync.poseMouth
import com.playit.app.presentation.theme.LilyMouthInside
import com.playit.app.presentation.theme.LilyOrange
import com.playit.app.presentation.theme.LilyOutline
import com.playit.app.presentation.theme.LilyTeeth
import com.playit.app.presentation.theme.LilyTongue
import com.playit.app.presentation.theme.LocalReducedMotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** The speech clip playing now (AudioPlayer.voice, provided by MainActivity); previews and tests see none. */
val LocalVoicePlayback = staticCompositionLocalOf<StateFlow<VoiceClip?>> { MutableStateFlow(null) }

/** The picture of a pose with its mouth painted out (tools/design/make_lily_talk.py). */
fun MascotState.talkFramePath(): String = assetPath.replace("images/mascot/", "images/mascot/talk/")

// MediaPlayer reports the start before the sound reaches the speaker; the mouth waits this long
private const val OUTPUT_LATENCY_MS = 60L

private object EnvelopeCache {
    @Volatile private var envelopes: VoiceEnvelopes? = null

    fun get(context: Context): VoiceEnvelopes = envelopes ?: synchronized(this) {
        envelopes ?: runCatching {
            context.assets.open("lipsync/voice_envelopes.json").bufferedReader().use { VoiceEnvelopes.parse(it.readText()) }
        }.getOrElse { VoiceEnvelopes(40, emptyMap()) }.also { envelopes = it }
    }
}

/**
 * The speech clip playing now, measured each frame: [speaking], its loudness [level] 0..1 (opens fast, closes a little
 * slower) and, for a letter's sound, the [letter]. [lines] counts the clips started, so Lily can blink as each line
 * begins. Drawing code reads [level] in the draw phase, so a frame only redraws.
 */
@Stable
class VoiceMouth {
    var speaking by mutableStateOf(false)
        internal set
    var lines by mutableIntStateOf(0)
        internal set
    var letter by mutableStateOf<String?>(null)
        internal set
    var level by mutableFloatStateOf(0f)
        internal set
}

/** Follows [LocalVoicePlayback]; it runs a frame loop only while a clip plays, never as an idle loop. */
@Composable
fun rememberVoiceMouth(): VoiceMouth {
    val clip by LocalVoicePlayback.current.collectAsState()
    val context = LocalContext.current
    val mouth = remember { VoiceMouth() }
    LaunchedEffect(clip) {
        val playing = clip
        mouth.speaking = playing != null
        mouth.letter = playing?.let { phonemeLetter(it.assetPath) }
        mouth.level = 0f
        if (playing == null) return@LaunchedEffect
        mouth.lines++
        val envelopes = EnvelopeCache.get(context)
        while (true) {
            withFrameMillis { }
            val elapsed = SystemClock.uptimeMillis() - playing.startedAtMs - OUTPUT_LATENCY_MS
            val raw = envelopes.levelAt(playing.assetPath, elapsed) ?: fallbackLevel(elapsed)
            mouth.level += (raw - mouth.level) * if (raw > mouth.level) 0.55f else 0.3f
        }
    }
    return mouth
}

/** The mouth for [pose] at this moment: the letter's shape for a letter's sound, else open with the loudness. */
fun VoiceMouth.shapeFor(pm: PoseMouth): Mouth {
    val target = letter?.let { articulationMouth(articulationFor(it)) }
    return if (target != null) pm.rest.lerp(target, (level * 3f).coerceAtMost(1f)) else pm.rest.lerp(TALKING_WIDE, level)
}

/**
 * Lily as [pose]. While she speaks (a speech clip plays), her mouth moves with the voice: for a letter's sound it takes
 * that letter's mouth shape (ArticulationGroup), and for other speech it opens with the loudness. When she is quiet
 * the released picture shows unchanged.
 *
 * She also blinks once as each line begins, and hops once when she starts celebrating. Both happen once per moment,
 * never on a loop, and not at all with reduced motion; her mouth still moves, because it carries the speech.
 */
@Composable
fun TalkingLily(
    pose: MascotState,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Lily the Tarsier",
    maxSize: Dp = 160.dp,
) {
    val voice = rememberVoiceMouth()
    // Both pictures are decoded up front, so the switch to the talking frame never shows an empty frame
    val still = rememberAssetPainter(pose.assetPath, maxSize)
    val talking = rememberAssetPainter(pose.talkFramePath(), maxSize)
    val pm = poseMouth(pose)
    val eyes = poseEyes(pose)
    val reduced = LocalReducedMotion.current

    val blink = remember { Animatable(0f) }   // ms into the blink
    LaunchedEffect(voice.lines) {
        if (voice.lines == 0 || reduced || eyes == null) return@LaunchedEffect
        blink.snapTo(0f)
        blink.animateTo(BLINK_MS.toFloat(), tween(BLINK_MS, easing = LinearEasing))
        blink.snapTo(0f)
    }
    val hop = remember { Animatable(1f) }     // 0..1 through the hop; 1 is standing
    LaunchedEffect(pose) {
        if (pose != MascotState.CELEBRATING || reduced) return@LaunchedEffect
        hop.snapTo(0f)
        hop.animateTo(1f, tween(HOP_MS, easing = LinearEasing))
    }

    Box(
        modifier.graphicsLayer {
            val frame = hopAt(hop.value)
            translationY = -frame.lift * size.height
            scaleX = 1f + 0.06f * frame.squash
            scaleY = 1f - 0.08f * frame.squash
            transformOrigin = TransformOrigin(0.5f, 1f)
        }
    ) {
        Image(
            painter = if (voice.speaking) talking else still,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(Modifier.fillMaxSize()) {
            if (voice.speaking) drawLilyMouth(pm, voice.shapeFor(pm))
            val closed = blinkClosed(blink.value)
            if (eyes != null && closed > 0f) drawLilyBlink(eyes, closed)
        }
    }
}

/**
 * Draws lids over [eyes], [closed] 0..1: an orange lid coming down with a dark lash edge, and when shut, the closed eye
 * as a soft curve. The lid is her face colour, so it reads as her own eyelid.
 */
fun DrawScope.drawLilyBlink(eyes: PoseEyes, closed: Float) {
    val side = minOf(size.width, size.height)
    val scale = side / 512f
    val origin = Offset((size.width - side) / 2f, (size.height - side) / 2f)
    val r = eyes.radius * scale
    for (eyeX in listOf(eyes.leftX, eyes.rightX)) {
        val c = Offset(origin.x + eyeX * scale, origin.y + eyes.y * scale)
        val eye = Path().apply { addOval(Rect(c, r + 1.5f * scale)) }
        val lidTop = c.y - r - 2f * scale
        val lidBottom = c.y - r + 2f * r * closed
        clipPath(eye) {
            drawRect(LilyOrange, Offset(c.x - r - 2f * scale, lidTop), Size(2f * r + 4f * scale, lidBottom - lidTop))
        }
        if (closed > 0.85f) {
            val shut = Path().apply {
                moveTo(c.x - 0.62f * r, c.y + 0.05f * r)
                quadraticBezierTo(c.x, c.y + 0.45f * r, c.x + 0.62f * r, c.y + 0.05f * r)
            }
            drawPath(shut, LilyOutline, style = Stroke(width = 5f * scale, cap = StrokeCap.Round))
        } else if (closed > 0.05f) {
            val half = kotlin.math.sqrt(maxOf(0f, r * r - (lidBottom - c.y) * (lidBottom - c.y)))
            drawLine(LilyOutline, Offset(c.x - half, lidBottom), Offset(c.x + half, lidBottom), 3.5f * scale, StrokeCap.Round)
        }
    }
}

/** A clip without a measured loudness still moves the mouth, at a speaking rhythm of about 5 syllables a second. */
private fun fallbackLevel(elapsedMs: Long): Float =
    if (elapsedMs < 0) 0f else (0.5f + 0.5f * kotlin.math.sin(elapsedMs / 1000f * 2f * Math.PI.toFloat() * 5f)) * 0.8f

/** Draws [mouth] where [pm] says the pose's mouth is, in a square picture fitted to this canvas. */
fun DrawScope.drawLilyMouth(pm: PoseMouth, mouth: Mouth) {
    val side = minOf(size.width, size.height)
    val scale = side / 512f
    val origin = Offset((size.width - side) / 2f, (size.height - side) / 2f)
    fun x(v: Float) = origin.x + v * scale
    fun y(v: Float) = origin.y + v * scale

    val w = pm.width * mouth.width
    val open = minOf(mouth.open, pm.maxOpen)
    val left = pm.centerX - w / 2f
    val right = pm.centerX + w / 2f
    val lipY = pm.top + 4f
    val line = 5f * scale

    if (open < 0.05f) {
        // Closed: a smile line, or pressed lips when flat
        val path = Path().apply {
            moveTo(x(left), y(lipY))
            quadraticBezierTo(x(pm.centerX), y(lipY + 2f * 0.22f * pm.width * mouth.curve), x(right), y(lipY))
        }
        drawPath(path, LilyOutline, style = Stroke(width = if (mouth.curve < 0.2f) line * 1.3f else line, cap = StrokeCap.Round))
        return
    }

    val h = pm.width * open
    if (mouth.round > 0.5f) {
        // "oo": a ring of lips around a small dark opening
        val rect = Rect(Offset(x(pm.centerX - w / 2f), y(lipY)), Size(w * scale, h * scale))
        drawOval(LilyMouthInside, rect.topLeft, rect.size)
        drawOval(LilyOutline, rect.topLeft, rect.size, style = Stroke(width = line * 1.4f))
        return
    }

    val sag = 0.25f * w * mouth.curve        // the top lip curves down in the middle like her smile
    val bottom = lipY + sag + h
    val path = Path().apply {
        moveTo(x(left), y(lipY))
        quadraticBezierTo(x(pm.centerX), y(lipY + 2f * sag), x(right), y(lipY))
        cubicTo(x(right), y(lipY + h * 0.9f), x(pm.centerX + w * 0.28f), y(bottom), x(pm.centerX), y(bottom))
        cubicTo(x(pm.centerX - w * 0.28f), y(bottom), x(left), y(lipY + h * 0.9f), x(left), y(lipY))
        close()
    }
    drawPath(path, LilyMouthInside)
    clipPath(path) {
        if (mouth.teethTop > 0f) {
            drawRect(LilyTeeth, Offset(x(left), y(lipY - 2f)), Size(w * scale, (2f + sag + h * mouth.teethTop) * scale))
        }
        if (mouth.teethBottom > 0f) {
            val teethH = h * mouth.teethBottom * 0.6f
            drawRect(LilyTeeth, Offset(x(left), y(bottom - teethH)), Size(w * scale, teethH * scale))
        }
        if (mouth.tongue > 0f) {
            val tw = w * 0.62f
            val th = h * mouth.tongue
            val tongueTop = bottom - th + (lipY + sag - (bottom - th)) * mouth.tongueUp
            drawOval(LilyTongue, Offset(x(pm.centerX - tw / 2f), y(tongueTop)), Size(tw * scale, th * scale * 1.3f))
        }
    }
    drawPath(path, LilyOutline, style = Stroke(width = line))
}
