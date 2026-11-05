package com.playit.app.presentation.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.components.lipsync.MOUTH_MESH_CELLS
import com.playit.app.presentation.components.lipsync.MOUTH_MESH_FLOATS
import com.playit.app.presentation.components.lipsync.MouthMove
import com.playit.app.presentation.components.lipsync.fillMouthMesh
import com.playit.app.presentation.components.lipsync.mouthAtRest
import com.playit.app.presentation.components.lipsync.mouthMove
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.SurfaceCard

/** How a mouth settles into its shape: quick, with a small overshoot. */
internal val MOUTH_SETTLE = spring<Float>(dampingRatio = 0.55f, stiffness = 260f)

/** How loud the voice must be (its envelope level, 0 to 1) to count as the sound playing. */
internal const val MOUTH_SOUNDING = 0.08f
/** While the sound plays, the mouth pulses this far out of its shape, this many times a second. */
internal const val MOUTH_PULSE = 0.22f
internal const val MOUTH_PULSE_HZ = 4.5f
/** Before, between and after the sounds the mouth is nearly relaxed. */
internal const val MOUTH_RESTING = 0.12f

/**
 * Where the mouth goes ([open]: 0 relaxed, 1 the picture's shape) at [seconds] into the model, for a voice [level]:
 * the shape while the sound plays, pulsing with it, and nearly relaxed in the gaps.
 */
internal fun mouthSaying(level: Float, seconds: Float): Float =
    if (level < MOUTH_SOUNDING) MOUTH_RESTING
    else 1f - MOUTH_PULSE * (0.5f + 0.5f * kotlin.math.sin(2f * kotlin.math.PI.toFloat() * MOUTH_PULSE_HZ * seconds))

/**
 * Mouth-shape picture for the current sound (NFR-ACC-01). Pictures arrive through an image
 * release (card 25); until a group's picture is in assets this draws nothing at all, with no
 * empty box (Review Focus 4).
 *
 * "Watch my lips": it opens into its shape as it arrives, and moves with the voice each time the letter's sound or key
 * word plays (MouthMotion.kt). Between those it holds the released picture, so the child can copy it. Nothing loops,
 * and with reduced motion the picture stays still.
 */
@Composable
fun ArticulationCue(group: ArticulationGroup, size: Dp, modifier: Modifier = Modifier) =
    ArticulationCue(assetPath = group.assetPath, size = size, modifier = modifier, move = mouthMove(group))

/** By path, so a test can name a picture that is not in assets (card 24b). */
@Composable
internal fun ArticulationCue(assetPath: String, size: Dp, modifier: Modifier = Modifier, move: MouthMove? = null) {
    val context = LocalContext.current
    val present = remember(assetPath) {
        val dir = assetPath.substringBeforeLast('/')
        val file = assetPath.substringAfterLast('/')
        runCatching { context.assets.list(dir)?.contains(file) == true }.getOrDefault(false)
    }
    if (!present) return

    val reduced = LocalReducedMotion.current
    val voice = rememberVoiceMouth()
    val open = remember { Animatable(if (reduced) 1f else 0f) }
    val following = voice.model && !reduced
    LaunchedEffect(following, reduced) {
        if (reduced) {
            open.snapTo(1f)
        } else if (following) {
            // The mouth "says" the sound (user, 2026-11-05: "the mouth needs to move when saying the letter"): it
            // relaxes before and between the sounds, goes into the shape while the sound plays, and pulses there as
            // the voice does, so a held sound such as mmm visibly moves too. Only while the sound plays.
            val start = withFrameMillis { it }
            while (true) {
                val now = withFrameMillis { it }
                open.snapTo(open.value + (mouthSaying(voice.level, (now - start) / 1000f) - open.value) * 0.3f)
            }
        } else {
            open.animateTo(1f, MOUTH_SETTLE)
        }
    }
    MouthPicture(assetPath, move, open = { open.value }, size, modifier)
}

/** The picture bent by [move] for [open] (0 relaxed, 1 the picture itself), read in the draw phase so a frame only redraws. */
@Composable
internal fun MouthPicture(assetPath: String, move: MouthMove?, open: () -> Float, size: Dp, modifier: Modifier = Modifier) {
    val bitmap = rememberAssetBitmap(assetPath, maxSize = size)
    val image = remember(bitmap) { bitmap?.asImageBitmap() }
    val mesh = remember { FloatArray(MOUTH_MESH_FLOATS) }
    val paint = remember { Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG) }
    Box(
        modifier
            .size(size)
            .background(SurfaceCard, CircleShape)
            .border(1.5.dp, ModernBorderSoft, CircleShape)
            .padding(6.dp)
            .semantics {
                contentDescription = "Mouth shape for this sound"
                role = Role.Image
            }
            .drawBehind {
                val picture = bitmap ?: return@drawBehind
                val opening = open()
                if (move == null || mouthAtRest(opening)) {
                    drawImage(image!!, dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()))
                } else {
                    fillMouthMesh(move, opening, this.size.width, this.size.height, mesh)
                    drawIntoCanvas {
                        it.nativeCanvas.drawBitmapMesh(picture, MOUTH_MESH_CELLS, MOUTH_MESH_CELLS, mesh, 0, null, 0, paint)
                    }
                }
            }
    )
}
