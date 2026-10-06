# Card 17b: ViewModel init blocks run after every property (crash fix found by Claude's dry run)

Status: done

## Why
Claude dry-ran plan Task 2 on 2026-10-06. `BlendItScreenshotTest` crashed with:

    NullPointerException: ... because "this._isPlayingPrompt" is null
      at BlendItViewModel.playIntroThenWordAudio(BlendItViewModel.kt:176)
      at BlendItViewModel.setupWordAtIndex(BlendItViewModel.kt:168)
      at BlendItViewModel$loadSessionWords$1 (BlendItViewModel.kt:124)

Kotlin runs property initializers and `init {}` blocks **top to bottom**:
- `BlendItViewModel` has `init { loadSessionWords() }` at line 109, but `_isPlayingPrompt` (line 128), `_nextHighlighted` and `idleTimer` are declared below it.
- When the words arrive immediately, the coroutine resumes inside `init` and touches a property that doesn't exist yet. This happens on `Dispatchers.Main.immediate` with data already in memory: tests today, and a fast phone or a cached database tomorrow.
- On a phone today, Room usually answers a moment later, which is why it hasn't crashed yet. It is a timing-dependent crash.

The same ordering, `init` above later properties, is in `FindItViewModel`, `HearItViewModel`, `SayItViewModel` and `MapViewModel`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/blendit/BlendItViewModel.kt`, `presentation/findit/FindItViewModel.kt`, `presentation/hearit/HearItViewModel.kt`, `presentation/sayit/SayItViewModel.kt`, `presentation/map/MapViewModel.kt`
- New test: `ViewModelInitOrderTest.kt` (package `com.playit.app`)

## Changes
1. In each of the 5 ViewModels, **move the whole `init { ... }` block, unchanged,** to just after the last class-level property declaration (`val`/`var` at 4-space indent, including multi-line initializers such as `private val idleTimer = IdleTimer(...)`).
2. Change nothing else: no logic, names or order of the properties themselves.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| ViewModelInitOrderTest | `initBlocksComeAfterAllProperties` | no `*ViewModel.kt` has a class-level property below its `init {` |

The test file, exactly as it passed in the dry run:
```kotlin
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
```

All other tests must still pass. In the dry run, after this fix, the full suite was 260 tests with 0 failed (with cards 17 and 18 applied); `BlendItScreenshotTest` is the one that reproduced the crash. Run `./gradlew testDebugUnitTest`.

## Commit
`fix(ui): ViewModel init blocks run after every property (crash-safe start-up)`

Decisions used: none new. Found by Claude's dry run of plan Task 2.
