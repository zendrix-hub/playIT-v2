package com.playit.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertCountEquals
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.GridGenerator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.findit.FindItScreen
import com.playit.app.presentation.findit.FindItViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class FindItScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @org.junit.Before fun syncImages() { com.playit.app.presentation.components.AssetImageConfig.decodeSynchronously = true }

    @Test
    fun findIt_letterM_fiveHearts() {
        val fakePhoneme = Phoneme(id = 1, letter = "m", audioPath = "path", imagePath = "images/pictures/picture_mouse.png", exampleWord = "mouse")
        val fakeDistractor = Phoneme(id = 2, letter = "s", audioPath = "path2", imagePath = "images/pictures/picture_sun.png", exampleWord = "sun")
        val phonemeRepo: PhonemeRepository = mockk()
        every { phonemeRepo.getAllPhonemes() } returns flowOf(listOf(fakePhoneme, fakeDistractor))
        val attemptRepo: FindItAttemptRepository = mockk(relaxed = true)
        val gridGenerator = GridGenerator()
        val sessionManager: SessionManager = mockk(relaxed = true)
        val player: AudioPlayer = mockk(relaxed = true)
        val resolver: AudioResolver = mockk(relaxed = true)
        val handle: SavedStateHandle = mockk()
        every { handle.get<String>("phonemeId") } returns "1"

        val vm = FindItViewModel(
            phonemeRepository = phonemeRepo,
            findItAttemptRepository = attemptRepo,
            gridGenerator = gridGenerator,
            sessionManager = sessionManager,
            audioPlayer = player,
            audioResolver = resolver,
            savedStateHandle = handle,
            letterGroupMemberRepository = mockk { every { getAllMembers() } returns flowOf(emptyList()) },
            lessonProgressRepository = mockk(relaxed = true)
        )

        compose.setContent { PlayItTheme { FindItScreen(vm, onNext = { _, _ -> }, onBack = {}) } }
        compose.waitForIdle()
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/findit_letter_m.png")
    }

    /** Chapter 3 (t): pictures only, so the child picks by the sound (adviser, 2026-11-05). */
    @Test
    fun findIt_chapterThree_picturesOnly() {
        val tiger = Phoneme(id = 9, letter = "t", audioPath = "path", imagePath = "images/pictures/picture_tiger.png", exampleWord = "tiger")
        val sun = Phoneme(id = 2, letter = "s", audioPath = "path2", imagePath = "images/pictures/picture_sun.png", exampleWord = "sun")
        val phonemeRepo: PhonemeRepository = mockk()
        every { phonemeRepo.getAllPhonemes() } returns flowOf(listOf(tiger, sun))
        val handle: SavedStateHandle = mockk()
        every { handle.get<String>("phonemeId") } returns "9"
        val vm = FindItViewModel(
            phonemeRepository = phonemeRepo,
            findItAttemptRepository = mockk(relaxed = true),
            gridGenerator = GridGenerator(),
            sessionManager = mockk(relaxed = true) { every { activeProfileId } returns kotlinx.coroutines.flow.MutableStateFlow<Long?>(1L) },
            audioPlayer = mockk(relaxed = true),
            audioResolver = mockk(relaxed = true),
            savedStateHandle = handle,
            letterGroupMemberRepository = mockk {
                every { getAllMembers() } returns flowOf(listOf(com.playit.app.domain.model.LetterGroupMember(groupId = 3, phonemeId = 9, position = 0)))
            },
            lessonProgressRepository = mockk { io.mockk.coEvery { getProgressForPhoneme(any(), any()) } returns null }
        )
        compose.setContent { PlayItTheme { FindItScreen(vm, onNext = { _, _ -> }, onBack = {}) } }
        compose.waitForIdle()
        // The cards settle in one by one (stickerIn), after a delay on the main looper: run it on before the capture.
        repeat(30) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper(100, java.util.concurrent.TimeUnit.MILLISECONDS)
            compose.mainClock.advanceTimeBy(100)
        }
        compose.waitForIdle()
        compose.onAllNodesWithText("Tiger").assertCountEquals(0)
        compose.onAllNodesWithText("Tree").assertCountEquals(0)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/findit_chapter3_pictures_only.png")
    }
}
