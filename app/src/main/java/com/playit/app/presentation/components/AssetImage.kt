package com.playit.app.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

/**
 * Test switch: when true, pictures decode during composition instead of on a background thread, so
 * screenshot and layout tests always capture them. Production code never sets it (card 17c; Claude's
 * review of 2026-10-07 found the background load raced the capture in about 1 run in 5).
 */
object AssetImageConfig {
    @Volatile var decodeSynchronously: Boolean = false
}

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
private fun decodeAsset(context: android.content.Context, assetPath: String, reqPx: Int): Painter? =
    runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, bounds) }
        val opts = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, reqPx, reqPx)
        }
        context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, opts) }
    }.getOrNull()?.let { bmp ->
        AssetBitmapCache.put("$assetPath@$reqPx", bmp)
        BitmapPainter(bmp.asImageBitmap())
    }

@Composable
fun rememberAssetPainter(assetPath: String, maxSize: Dp = 160.dp): Painter {
    val context = LocalContext.current
    val reqPx = with(LocalDensity.current) { maxSize.roundToPx() }
    val key = "$assetPath@$reqPx"
    if (AssetImageConfig.decodeSynchronously) {
        return remember(key) {
            AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) }
                ?: decodeAsset(context, assetPath, reqPx) ?: transparent
        }
    }
    // The state is keyed on the picture: produceState keeps its old value when its key changes, so a
    // screen that changed letter in place kept the previous letter's picture (card 28d).
    val painter = remember(key) {
        mutableStateOf<Painter>(AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) } ?: transparent)
    }
    LaunchedEffect(key) {
        if (painter.value !== transparent) return@LaunchedEffect
        AssetDecodeTracker.start()
        try {
            painter.value = withContext(Dispatchers.IO) { decodeAsset(context, assetPath, reqPx) } ?: transparent
        } finally {
            AssetDecodeTracker.done()   // after the value is set, so a waiting test sees the picture
        }
    }
    return painter.value
}
