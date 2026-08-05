package com.playit.app.presentation.theme

import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalReducedMotion = staticCompositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = LearningBlue,
    onPrimary = CreamWhite,
    secondary = GrowthGreen,
    onSecondary = CreamWhite,
    tertiary = AchievementGold,
    onTertiary = TextPrimary,
    background = SoftSky,
    surface = CreamWhite,
    onSurface = TextPrimary,
    onBackground = TextPrimary,
    outline = BorderColor,
    error = DestructiveRed
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
