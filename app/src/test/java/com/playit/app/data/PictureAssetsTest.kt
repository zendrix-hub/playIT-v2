package com.playit.app.data

import com.playit.app.domain.manager.GridGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class PictureAssetsTest {

    private val assetsDir = listOf(
        File("src/main/assets"),
        File("app/src/main/assets")
    ).firstOrNull { it.exists() } ?: error("assets directory not found")

    private val docsDir = listOf(
        File("docs"),
        File("../docs")
    ).firstOrNull { it.exists() } ?: error("docs directory not found")

    private val allCurriculumLetters = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "z"
    )

    private fun sha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    @Test
    fun everyGridPictureExists() {
        val gridGenerator = GridGenerator()
        val checkedPaths = mutableSetOf<String>()

        for (letter in allCurriculumLetters) {
            repeat(200) {
                val grid = gridGenerator.generate5ItemGrid(letter)
                for (item in grid) {
                    if (checkedPaths.add(item.imagePath)) {
                        val file = File(assetsDir, item.imagePath)
                        assertTrue(
                            "Picture asset does not exist: ${item.imagePath} (word=${item.word}, target=$letter)",
                            file.exists()
                        )
                    }
                }
            }
        }
    }

    @Test
    fun releasedPicturesMatchManifest() {
        val imageReleaseDir = File(docsDir, "image-release")
        assertTrue("image-release directory should exist", imageReleaseDir.exists())

        val manifestFiles = imageReleaseDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?.mapNotNull { dir ->
                val manifest = File(dir, "manifest.json")
                if (manifest.exists()) Pair(dir.name, manifest) else null
            } ?: emptyList()

        assertTrue("At least one release manifest must exist", manifestFiles.isNotEmpty())

        val latestEntries = mutableMapOf<String, String>()
        for ((_, manifestFile) in manifestFiles) {
            val content = manifestFile.readText()
            val chunks = content.split(Regex(""""itemId"\s*:""")).drop(1)
            for (chunk in chunks) {
                val appPath = Regex(""""appPath"\s*:\s*"([^"]+)"""").find(chunk)?.groupValues?.get(1)
                val sha256 = Regex(""""sha256"\s*:\s*"([a-f0-9]{64})"""").find(chunk)?.groupValues?.get(1)
                if (appPath != null && sha256 != null) {
                    latestEntries[appPath] = sha256
                }
            }
        }

        assertTrue("Must have released images in manifest", latestEntries.isNotEmpty())

        for ((appPath, expectedSha256) in latestEntries) {
            val file = File(assetsDir, appPath)
            assertTrue("Asset file does not exist: $appPath", file.exists())
            val actualSha256 = sha256(file.readBytes())
            assertEquals("SHA-256 mismatch for $appPath", expectedSha256, actualSha256)
        }
    }
}
