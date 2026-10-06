package com.playit.app.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Largest power of two that keeps the decoded image at least as big as the requested size. */
fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeight: Int): Int {
    if (reqWidth <= 0 || reqHeight <= 0) return 1
    var sample = 1
    while (srcWidth / (sample * 2) >= reqWidth && srcHeight / (sample * 2) >= reqHeight) sample *= 2
    return sample
}

private val transparent = ColorPainter(Color.Transparent)

/** Counts decodes in flight, so screenshot tests can wait until every picture on screen has loaded. */
object AssetDecodeTracker {
    private val pending = java.util.concurrent.atomic.AtomicInteger(0)
    fun isIdle(): Boolean = pending.get() == 0
    internal fun start() { pending.incrementAndGet() }
    internal fun done() { pending.decrementAndGet() }
}

/**
 * Loads an asset PNG at about [maxSize] (in dp) on a background thread. Shows nothing until
 * it is ready, and nothing if the file is missing (a missing asset never crashes a screen).
 */
@Composable
fun rememberAssetPainter(assetPath: String, maxSize: Dp = 160.dp): Painter {
    val context = LocalContext.current
    val reqPx = with(LocalDensity.current) { maxSize.roundToPx() }
    val key = "$assetPath@$reqPx"
    val painter by produceState<Painter>(
        initialValue = AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) } ?: transparent,
        key1 = key
    ) {
        if (value !== transparent) return@produceState
        AssetDecodeTracker.start()
        try {
            value = withContext(Dispatchers.IO) {
                runCatching {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, bounds) }
                    val opts = BitmapFactory.Options().apply {
                        inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, reqPx, reqPx)
                    }
                    context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, opts) }
                }.getOrNull()?.let { bmp ->
                    AssetBitmapCache.put(key, bmp)
                    BitmapPainter(bmp.asImageBitmap())
                } ?: transparent
            }
        } finally {
            AssetDecodeTracker.done()   // after the value is set, so a waiting test sees the picture
        }
    }
    return painter
}
