package com.playit.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.playit.app.R

// ═══════════════════════════════════════════════════════════════════════════
// Lexend + Andika — pedagogically correct for early readers (ages 5–8).
//
// Lexend has single-story 'a' and 'g' letterforms matching how children
// are taught to handwrite, generous x-height and counters, and is derived
// from Quicksand (rounded, friendly, geometric) — satisfying the Design
// System's "child-friendly, rounded" brief while fixing the letterform gap
// that Nunito/Poppins have.  See 03_DESIGN_SYSTEM_SUMMARY.md §5.2.
//
// Andika (SIL) is the static fallback for edge cases or missing glyph
// coverage.  It shares the same pedagogical single-story letterforms.
//
// Confirmed by stakeholder: Lexend + Andika, NOT Baloo 2 / Nunito from
// the playit-mockup.html (13_MASTER_TASKS.md Phase 10 Open Decisions).
// ═══════════════════════════════════════════════════════════════════════════

val LexendFontFamily = FontFamily(
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_semibold, FontWeight.SemiBold),
    Font(R.font.lexend_bold, FontWeight.Bold),
    Font(R.font.lexend_extrabold, FontWeight.ExtraBold),
)

val AndikaFontFamily = FontFamily(
    Font(R.font.andika_regular, FontWeight.Normal),
)

// ═══════════════════════════════════════════════════════════════════════════
// Type Scale
//
// Child-facing reading content uses bodyLarge (24sp) as the floor — anything
// the child is meant to sound out must be at least this size per
// Typography_Guide.md.  The shipped 16sp/18sp tiers remain for adult-only
// surfaces (Parent Dashboard, Report Preview).
//
// See 10_UI_IMPLEMENTATION_GUIDE.md §2 for the full screen-by-screen
// type-scale mapping.
// ═══════════════════════════════════════════════════════════════════════════

val Typography = Typography(
    // Letter cards, celebrations — biggest display text
    displayLarge = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 48.sp
    ),
    // Screen titles
    headlineMedium = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    // Instructions, subheadings
    titleMedium = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Child-facing reading content (24sp floor per Typography_Guide.md)
    bodyLarge = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    // Mascot messages, parent dashboard body text
    bodyMedium = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // Helper text, captions — never below 16sp
    labelMedium = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
)
