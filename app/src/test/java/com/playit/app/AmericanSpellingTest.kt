package com.playit.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Text on screen and in the PDF uses American spelling (user, 2026-11-06: "practise ... thats like british aus, change
 * it to practice"). Scans the string literals of all app code, child and parent screens alike.
 */
class AmericanSpellingTest {
    private val british = mapOf(
        "practis" to "practic", "colour" to "color", "favourite" to "favorite", "behaviour" to "behavior",
        "centre" to "center", "recognis" to "recogniz", "organis" to "organiz", "realis" to "realiz",
        "honour" to "honor", "neighbour" to "neighbor", "theatre" to "theater", "grey" to "gray",
        "travell" to "travel", "learnt" to "learned", "spelt" to "spelled"
    )

    @Test fun textInTheApp_isAmericanEnglish() {
        val found = mutableListOf<String>()
        File("src/main/java").walkTopDown().filter { it.extension == "kt" }.forEach { file ->
            file.readLines().forEachIndexed { i, line ->
                val code = line.substringBefore("//").trim()
                if (code.startsWith("*") || code.startsWith("/*")) return@forEachIndexed
                Regex("\"([^\"]*)\"").findAll(code).map { it.groupValues[1] }.forEach { literal ->
                    british.entries.firstOrNull { literal.lowercase().contains(it.key) }?.let { (uk, us) ->
                        found += "${file.name}:${i + 1} \"$literal\" ($uk -> $us)"
                    }
                }
            }
        }
        assertTrue("British spelling in the app's text:\n" + found.joinToString("\n"), found.isEmpty())
    }
}
