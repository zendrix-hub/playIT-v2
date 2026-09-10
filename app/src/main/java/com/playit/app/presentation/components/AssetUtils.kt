package com.playit.app.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext

import androidx.collection.LruCache

/**
 * Global in-memory cache for decoded asset bitmaps to prevent redundant disk I/O
 * and reduce heap allocation churn during Compose recompositions.
 */
object AssetBitmapCache {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 8).coerceAtLeast(1024) // 1/8th of heap

    private val lruCache = object : LruCache<String, android.graphics.Bitmap>(cacheSize) {
        override fun sizeOf(key: String, value: android.graphics.Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    fun get(key: String): android.graphics.Bitmap? = lruCache.get(key)
    fun put(key: String, bitmap: android.graphics.Bitmap) {
        lruCache.put(key, bitmap)
    }
}

/**
 * Safely loads an image asset from the app's assets/ directory into a Compose Painter.
 * Uses in-memory caching and auto-closes input streams to prevent file descriptor leaks.
 * Fallback to a solid ColorPainter if asset loading fails.
 */
@Composable
fun rememberAssetPainter(assetPath: String): Painter {
    val context = LocalContext.current
    return remember(assetPath) {
        val cached = AssetBitmapCache.get(assetPath)
        if (cached != null) {
            return@remember BitmapPainter(cached.asImageBitmap())
        }

        try {
            val bitmap = context.assets.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            if (bitmap != null) {
                AssetBitmapCache.put(assetPath, bitmap)
                BitmapPainter(bitmap.asImageBitmap())
            } else {
                ColorPainter(Color.Transparent)
            }
        } catch (e: Exception) {
            ColorPainter(Color.Transparent)
        }
    }
}

/**
 * Mascot Lily character states mapped to their production PNG asset paths in assets/images/mascot/.
 */
enum class MascotState(val assetPath: String) {
    IDLE("images/mascot/lily_idle.png"),
    CELEBRATING("images/mascot/lily_celebrating.png"),
    ENCOURAGING("images/mascot/lily_encouraging.png"),
    LISTENING("images/mascot/lily_listening.png"),
    POINTING("images/mascot/lily_pointing.png"),
    WAVING("images/mascot/lily_waving.png"),
    THINKING("images/mascot/lily_thinking.png")
}
