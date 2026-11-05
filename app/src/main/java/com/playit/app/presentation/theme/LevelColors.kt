package com.playit.app.presentation.theme

import com.playit.app.domain.model.ProgressLevel

/**
 * The colour of each progress level, shared by the Parent Zone and the PDF report so both read the same. Soft fills
 * with dark ink (every pair is at least 4.5:1); the lowest level is a muted brick, never the harsh red the app avoids.
 */
data class LevelColor(val fill: Int, val ink: Int)

object LevelColors {
    fun of(level: ProgressLevel): LevelColor = when (level) {
        ProgressLevel.OUTSTANDING -> LevelColor(0xFFD8F0DC.toInt(), 0xFF1B5E28.toInt())
        ProgressLevel.VERY_SATISFACTORY -> LevelColor(0xFFDDF1EF.toInt(), 0xFF0A625D.toInt())
        ProgressLevel.SATISFACTORY -> LevelColor(0xFFE1ECF8.toInt(), 0xFF1D5890.toInt())
        ProgressLevel.FAIRLY_SATISFACTORY -> LevelColor(0xFFFFEFD1.toInt(), 0xFF7A4F00.toInt())
        ProgressLevel.DID_NOT_MEET -> LevelColor(0xFFFBE3DF.toInt(), 0xFF9A3B2B.toInt())
        ProgressLevel.IN_PROGRESS -> LevelColor(0xFFECE7F6.toInt(), 0xFF54457F.toInt())
        ProgressLevel.NOT_STARTED -> LevelColor(0xFFF1ECE4.toInt(), 0xFF6E5E4D.toInt())
    }
}
