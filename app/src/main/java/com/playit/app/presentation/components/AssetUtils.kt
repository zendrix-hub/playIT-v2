package com.playit.app.presentation.components

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
