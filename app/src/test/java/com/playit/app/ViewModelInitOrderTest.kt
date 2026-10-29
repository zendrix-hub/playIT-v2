package com.playit.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * An init block that starts work (collecting flows, playing audio) must come after every property
 * declaration. Kotlin runs initializers top to bottom, so a coroutine that resumes immediately
 * (Dispatchers.Main.immediate, or a flow that already has data) would otherwise touch a property
 * that is still null. Found by Claude's card 18 dry run, 2026-10-06 (BlendItViewModel NPE).
 */
class ViewModelInitOrderTest {
    private val main = if (File("src/main").exists()) File("src/main") else File("app/src/main")

    @Test fun initBlocksComeAfterAllProperties() {
        val bad = File(main, "java").walkTopDown()
            .filter { it.name.endsWith("ViewModel.kt") }
            .filter { f ->
                val lines = f.readLines()
                val init = lines.indexOfFirst { it.startsWith("    init {") }
                val lastProp = lines.indexOfLast { Regex("^    (private |internal |)(val|var) ").containsMatchIn(it) }
                init >= 0 && lastProp > init
            }.map { it.name }.toList()
        assertTrue("Move init {} below the last property in: $bad", bad.isEmpty())
    }
}
