Regression re-run after APK H

What:     the whole unit test suite, on a fresh clone of the public repository
          https://github.com/zendrix-hub/playIt at commit 0da13c5 (cloned from GitHub, nothing cached).
Contents: the code of APK H (0320385) with the pictures released after it (image releases of
          2026-10-15 to 2026-10-17) and the ten keyword pictures restored from APK H. No code changed after APK H.
Command:  gradlew :app:testDebugUnitTest   (BUILD SUCCESSFUL in 4m 29s)
When:     2026-10-18 (timestamps in the XML files are UTC)
Result:   433 tests in 66 classes: 427 passed, 6 skipped by design, 0 failed.
Files:    junit/  the 66 JUnit XML files exactly as Gradle wrote them.
