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
                ColorPainter(Color.Red)
            }
        } catch (e: Exception) {
            ColorPainter(Color.Red)
        }
    }
}

/**
 * Mascot Lily character states mapped to their production PNG asset paths in assets/images/mascot/.
 */
enum class MascotState(val assetPath: String) {
    IDLE("images/mascot/mascot_lily_idle.png"),
    HAPPY("images/mascot/mascot_lily_cheer.png"),
    THINKING("images/mascot/mascot_lily_thinking.png"),
    ENCOURAGING("images/mascot/mascot_lily_encouraging.png"),
    CELEBRATING("images/mascot/mascot_lily_celebrating.png"),
    LISTENING("images/mascot/mascot_lily_listening.png"),
    POINTING("images/mascot/mascot_lily_point.png"),
    EXCITED("images/mascot/mascot_lily_excited.png")
}
