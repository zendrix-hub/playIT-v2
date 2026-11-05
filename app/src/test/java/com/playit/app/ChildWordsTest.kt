package com.playit.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Words on the child's screens are ones a 5 to 7 year old hears every day (adviser, 2026-11-05: "change the buttons
 * or audios like the terms used that might not help the users because it is not intended for their age"). Scans the
 * string literals of the child-facing screens; the Parent Zone is for adults and is not scanned.
 */
class ChildWordsTest {
    private val childScreens = listOf("splash", "profile", "map", "hearit", "sayit", "findit", "lettercomplete", "blendit")
        .map { File("src/main/java/com/playit/app/presentation/$it") }
    private val tooAdvanced = listOf("challenge", "milestone", "mastered", "phonics", "section ", "unit ", "guidebook",
        "blend-it", "blending", "limit reached", "profile")

    @Test fun childScreens_avoidWordsTooAdvancedForTheirAge() {
        val found = mutableListOf<String>()
        childScreens.flatMap { dir -> dir.walkTopDown().filter { it.extension == "kt" }.toList() }.forEach { file ->
            file.readLines().forEachIndexed { i, line ->
                val code = line.substringBefore("//").trim()
                if (code.startsWith("*") || code.startsWith("/*")) return@forEachIndexed
                Regex("\"([^\"]*)\"").findAll(code).map { it.groupValues[1] }
                    .map { it.replace(Regex("\\$\\{[^}]*\\}|\\$\\w+"), "") }          // ${code} and $name
                    .filterNot { it.matches(Regex("[a-z0-9_]+")) }                     // test tags and ids
                    .forEach { literal ->
                    tooAdvanced.firstOrNull { literal.lowercase().contains(it) }?.let { word ->
                        found += "${file.name}:${i + 1} \"$literal\" ($word)"
                    }
                }
            }
        }
        assertTrue("Words too advanced for a Grade 1 child:\n" + found.joinToString("\n"), found.isEmpty())
    }
}
