package com.playit.app.presentation.map

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The map is a child-facing screen: no text on it, its banners or its dialogs is smaller than 16 sp
 * (NFR-ACC-02 text floor; card 31e raised the 9.5 to 15 sp labels). A source scan, like ZeroEmojiPolicyTest.
 */
class MapTextFloorTest {

    private val mapDir = listOf(
        File("src/main/java/com/playit/app/presentation/map"),
        File("app/src/main/java/com/playit/app/presentation/map")
    ).first { it.exists() }

    private val fontSize = Regex("""fontSize\s*=\s*(\d+(?:\.\d+)?)\.sp""")

    @Test fun noMapTextUnder16sp() {
        val small = mutableListOf<String>()
        mapDir.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { file ->
            file.readLines().forEachIndexed { i, line ->
                fontSize.findAll(line).forEach { m ->
                    if (m.groupValues[1].toFloat() < 16f) small += "${file.name}:${i + 1}: ${line.trim()}"
                }
            }
        }
        assertTrue("map text under 16 sp:\n" + small.joinToString("\n"), small.isEmpty())
    }
}
