package com.playit.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ZeroEmojiPolicyTest {

    private val srcMainDir = if (File("src/main").exists()) {
        File("src/main")
    } else {
        File("app/src/main")
    }

    private fun isForbidden(cp: Int): Boolean {
        return (cp in 0x1F000..0x1FAFF) ||
                (cp in 0x2600..0x27BF) ||
                (cp in 0x2300..0x23FF) ||
                (cp in 0x2B00..0x2BFF) ||
                cp == 0xFE0F
    }

    @Test
    fun noEmojiInAppSources() {
        assertTrue("Source directory not found: ${srcMainDir.absolutePath}", srcMainDir.exists())

        val violations = mutableListOf<String>()

        srcMainDir.walkTopDown()
            .filter { it.isFile && (it.extension == "kt" || it.extension == "xml") }
            .forEach { file ->
                file.useLines { lines ->
                    lines.forEachIndexed { lineIndex, line ->
                        var offset = 0
                        while (offset < line.length) {
                            val cp = line.codePointAt(offset)
                            if (isForbidden(cp)) {
                                val hex = String.format("U+%04X", cp)
                                violations.add("${file.relativeTo(srcMainDir).path}:${lineIndex + 1}: Forbidden character $hex found in: \"${line.trim()}\"")
                            }
                            offset += Character.charCount(cp)
                        }
                    }
                }
            }

        assertTrue(
            "Found ${violations.size} zero-emoji policy violation(s):\n" + violations.joinToString("\n"),
            violations.isEmpty()
        )
    }
}
