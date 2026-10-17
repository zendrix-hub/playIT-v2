package com.playit.app.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/** Qualifiers for the 4 sizes; each LayoutMatrix subclass puts one of these in its @Config. */
object Devices {
    const val COMPACT = "w360dp-h640dp-xhdpi"
    const val A21S = "w360dp-h740dp-xhdpi"
    const val PHONE = "w411dp-h891dp-xxhdpi"
    const val TABLET = "w800dp-h1280dp-mdpi"
}

/** Renders [content] as if the system font size were [fontScale] (Samsung "Large" is about 1.3). */
@Composable
fun WithFontScale(fontScale: Float, content: @Composable () -> Unit) {
    val d = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale)) { content() }
}
