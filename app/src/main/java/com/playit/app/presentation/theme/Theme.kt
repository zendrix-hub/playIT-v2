package com.playit.app.presentation.theme

import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

val LocalReducedMotion = staticCompositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = PrimaryJoy,
    onPrimary = Color.White,
    primaryContainer = PrimaryJoyLight,
    onPrimaryContainer = PrimaryJoyDark,
    secondary = EmeraldLeaf,
    onSecondary = Color.White,
    secondaryContainer = EmeraldLeafLight,
    onSecondaryContainer = EmeraldLeafDark,
    tertiary = SunnyGold,
    onTertiary = TextMidnight,
    tertiaryContainer = SunnyGoldLight,
    onTertiaryContainer = SunnyGoldDark,
    background = CanvasLight,
    onBackground = TextMidnight,
    surface = SurfaceCard,
    onSurface = TextMidnight,
    surfaceVariant = CanvasSoft,
    onSurfaceVariant = TextMuted,
    outline = ModernBorderSoft,
    outlineVariant = ModernBorderFaint,
    error = CoralBerry,
    onError = Color.White
)

@Composable
fun PlayItTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val transitionScale = try {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1.0f)
    } catch (e: Exception) {
        1.0f
    }
    val isReducedMotion = transitionScale == 0.0f

    CompositionLocalProvider(
        LocalReducedMotion provides isReducedMotion
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
