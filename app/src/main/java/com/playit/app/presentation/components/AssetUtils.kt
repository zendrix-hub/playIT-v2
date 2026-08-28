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

/**
 * Safely loads an image asset from the app's assets/ directory into a Compose Painter.
 * Fallback to a solid ColorPainter if asset loading fails.
 */
@Composable
fun rememberAssetPainter(assetPath: String): Painter {
    val context = LocalContext.current
    return remember(assetPath) {
        try {
            val inputStream = context.assets.open(assetPath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
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
