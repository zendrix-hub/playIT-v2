package com.playit.app.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.theme.PlayItTheme
import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.speech.VoskRecognizer
import com.playit.app.domain.manager.SpeechValidator
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.SpeechErrorType
import com.playit.app.domain.model.SpeechJudgement
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.hearit.HearItScreen
import com.playit.app.presentation.hearit.HearItViewModel
import com.playit.app.presentation.sayit.SayItScreen
import com.playit.app.presentation.sayit.SayItViewModel
import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.domain.manager.BlendItWordSelector
import com.playit.app.domain.manager.GridGenerator
import com.playit.app.domain.model.BlendItWord
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.presentation.blendit.BlendItScreen
import com.playit.app.presentation.blendit.BlendItViewModel
import com.playit.app.presentation.findit.FindItScreen
import com.playit.app.presentation.findit.FindItViewModel
import com.playit.app.domain.manager.GroupUnlockManager
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.manager.UnlockManager
import com.playit.app.domain.model.LetterGroup
import com.playit.app.domain.model.LetterGroupMember
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.AchievementRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.LetterGroupRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.presentation.map.MapScreen
import com.playit.app.presentation.map.MapViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.playit.app.presentation.lettercomplete.LetterCompleteScreen
import com.playit.app.presentation.lettercomplete.LetterCompleteViewModel
import com.playit.app.presentation.profile.NamePromptScreen
import com.playit.app.presentation.profile.ProfileViewModel
import com.playit.app.presentation.splash.LILY_FACE_BOTTOM
import com.playit.app.presentation.splash.SplashScreen
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// (the size must be set before the test activity starts, so it can't be a runtime parameter).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class LayoutMatrixTest(private val deviceName: String) {
    @get:Rule val compose = createComposeRule()

    /** Pictures decode on the first frame in tests, so captures always include them (card 17c). */
    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    /** Fails if the node's bottom is below the window (the child would have to scroll to reach it). */
    protected fun assertOnScreen(tag: String) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertTrue("$tag bottom ${node.bottom} > window ${root.bottom} on $deviceName", node.bottom <= root.bottom)
    }

    /**
     * Fails if [upper] reaches below the top of [lower]. Unclipped bounds, so a node pushed out of the
     * lesson body's scroll area still counts (clipped bounds stop at the body's edge and always pass).
     */
    protected fun assertAbove(upper: String, lower: String) {
        val u = compose.onNodeWithTag(upper, useUnmergedTree = true).getUnclippedBoundsInRoot()
        val l = compose.onNodeWithTag(lower, useUnmergedTree = true).getBoundsInRoot()
        assertTrue("$upper bottom ${u.bottom} > $lower top ${l.top} on $deviceName", u.bottom <= l.top)
    }

    protected fun capture(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/${name}_$deviceName.png")
    }

    /** Font-scale checks run on every size except compact (360x640 at 1.3 is below our support floor). */
    protected fun assumeFontScaleChecks() = assumeTrue(deviceName != "compact")

    @Composable
    private fun scaffoldUnderTest() = LessonScaffold(
        topBar = { Box(Modifier.height(64.dp)) },
        header = { MascotSpeechHeader(message = "Listen closely to the sound of the letter, then tap play.") },
        bottomBar = { Box(Modifier.fillMaxWidth().height(64.dp).testTag("bottom")) },
    ) { Box(Modifier.size(120.dp).testTag("main")) }

    @Test fun lessonScaffold_bottomBarAndBodyVisible() {
        compose.setContent { PlayItTheme { scaffoldUnderTest() } }
        assertOnScreen("main"); assertOnScreen("bottom"); capture("scaffold")
    }

    @Test fun fontScale13_mainActionVisible() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { scaffoldUnderTest() } } }
        assertOnScreen("main"); assertOnScreen("bottom")
    }

    @Test fun tablet_contentWidthCapped() {
        compose.setContent {
            PlayItTheme {
                LessonScaffold(topBar = {}, header = {}, bottomBar = {}) {
                    Box(Modifier.fillMaxWidth().height(10.dp).testTag("wide"))
                }
            }
        }
        val w = compose.onNodeWithTag("wide").getBoundsInRoot().let { it.right - it.left }
        assertTrue(w <= 560.dp)
    }
    private val mouse = Phoneme(1, "m", "p", "images/pictures/picture_mouse.png", "mouse")
    private val phonemes = object : PhonemeRepository {
        override fun getAllPhonemes(): Flow<List<Phoneme>> = flowOf(listOf(mouse))
        override suspend fun getPhonemeById(id: Int): Phoneme? = mouse
        override suspend fun getPhonemeByLetter(letter: String): Phoneme? = mouse
    }
    private fun handle(): SavedStateHandle = mockk { every { get<String>("phonemeId") } returns "1" }

    /** Built as HearItScreenshotTest builds it. */
    @Composable private fun hearIt() {
        val vm = remember { HearItViewModel(phonemes, mockk<AudioPlayer>(relaxed = true), mockk<AudioResolver>(relaxed = true), handle()) }
        HearItScreen(vm, onNext = {}, onBack = {})
    }

    /** Built with relaxed mocks as in SayItViewModelTest; the screen sits in Idle with the word "mouse". */
    @Composable private fun sayIt() {
        val vm = remember {
            SayItViewModel(
                phonemes, mockk<SayItAttemptRepository>(relaxed = true), mockk<SpeechValidator>(relaxed = true),
                mockk<VoskRecognizer>(relaxed = true), mockk<AudioPlayer>(relaxed = true),
                mockk<AudioResolver>(relaxed = true), mockk<SessionManager>(relaxed = true), handle()
            )
        }
        SayItScreen(vm, onNext = {}, onBack = {})
    }

    @Test fun hearIt_playVisible() {
        compose.setContent { PlayItTheme { hearIt() } }
        compose.waitForIdle()
        assertOnScreen("hearit_play"); capture("hearit")
    }

    @Test fun sayIt_micVisible() {
        compose.setContent { PlayItTheme { sayIt() } }
        compose.waitForIdle()
        assertOnScreen("sayit_mic"); capture("sayit")
    }

    @Test fun sayIt_micVisible_fontScale13() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { sayIt() } } }
        compose.waitForIdle()
        assertOnScreen("sayit_mic")
    }

    /** Hear It with centered play button: the step dots stay above Next (card 28c). */
    @Test fun hearIt_stepsAboveNext() {
        compose.setContent { PlayItTheme { hearIt() } }
        compose.waitForIdle()
        assertAbove("hearit_steps", "hearit_next")
    }

    /** Say It after two misses: the mouth picture shows left of the mic, and the mic stays on screen (card 24b). */
    @Test fun sayIt_mouthCueBesideMic() {
        val validator = mockk<SpeechValidator>(relaxed = true)
        every { validator.judgeWord(any(), any(), any()) } returns
            SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.OTHER_WORD, heard = "cat")
        // The miss is recorded for the active profile, so the relaxed mock needs a real Long here.
        val session = mockk<SessionManager>(relaxed = true) { every { activeProfileId } returns MutableStateFlow<Long?>(1L) }
        val vm = SayItViewModel(
            phonemes, mockk<SayItAttemptRepository>(relaxed = true), validator,
            mockk<VoskRecognizer>(relaxed = true), mockk<AudioPlayer>(relaxed = true),
            mockk<AudioResolver>(relaxed = true), session, handle()
        )
        compose.setContent { PlayItTheme { SayItScreen(vm, onNext = {}, onBack = {}) } }
        compose.waitForIdle()
        compose.runOnIdle { repeat(2) { vm.evaluateSpeech("cat") } }
        compose.waitForIdle()
        val cue = compose.onNodeWithContentDescription("Mouth shape for this sound").getUnclippedBoundsInRoot()
        val mic = compose.onNodeWithTag("sayit_mic", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("cue right ${cue.right} > mic left ${mic.left} on $deviceName", cue.right <= mic.left)
        assertOnScreen("sayit_mic")
        // "Watch my lips" is the line under the mic now, not a banner in the pinned bar (card 31d);
        // the dots still stay above the bar (card 28b).
        assertAbove("sayit_attempts", "sayit_bottom"); capture("sayit_cue")
        // The feedback shows once, as plain text under the mic, and nothing in the pinned bar repeats it.
        // Unmerged: the mic merges its label into one node, and only the label's own bounds sit under the mic.
        compose.onAllNodesWithText("Watch my lips", useUnmergedTree = true).assertCountEquals(1)
        val line = compose.onNodeWithText("Watch my lips", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val micBox = compose.onNodeWithTag("sayit_mic", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val bar = compose.onNodeWithTag("sayit_bottom", useUnmergedTree = true).getBoundsInRoot()
        assertTrue("feedback not under the mic on $deviceName", line.top >= micBox.bottom)
        assertTrue("feedback inside the pinned bar on $deviceName", line.bottom <= bar.top)
    }
    /** Built as FindItScreenshotTest builds it. */
    @Composable private fun findIt() {
        val vm = remember {
            val sun = Phoneme(id = 2, letter = "s", audioPath = "path2", imagePath = "images/pictures/picture_sun.png", exampleWord = "sun")
            val repo: PhonemeRepository = mockk { every { getAllPhonemes() } returns flowOf(listOf(mouse, sun)) }
            val h: SavedStateHandle = mockk { every { get<String>("phonemeId") } returns "1" }
            FindItViewModel(
                phonemeRepository = repo,
                findItAttemptRepository = mockk<FindItAttemptRepository>(relaxed = true),
                gridGenerator = GridGenerator(),
                sessionManager = mockk<SessionManager>(relaxed = true),
                audioPlayer = mockk<AudioPlayer>(relaxed = true),
                audioResolver = mockk<AudioResolver>(relaxed = true),
                savedStateHandle = h
            )
        }
        FindItScreen(vm, onNext = { _, _ -> }, onBack = {})
    }

    /** Built as BlendItScreenshotTest builds it. */
    @Composable private fun blendIt() {
        val vm = remember {
            val words = listOf(
                BlendItWord(wordId = 1, groupId = 1, word = "SAM", wordPattern = "CVC", audioPath = "audio/words/word_sam.mp3", imagePath = "images/pictures/blendword_sam.png"),
                BlendItWord(wordId = 2, groupId = 1, word = "SIS", wordPattern = "CVC", audioPath = "audio/words/word_sis.mp3", imagePath = "images/pictures/blendword_sis.png"),
                BlendItWord(wordId = 3, groupId = 1, word = "AIM", wordPattern = "VVC", audioPath = "audio/words/word_aim.mp3", imagePath = "images/pictures/blendword_aim.png")
            )
            val repo: BlendItWordRepository = mockk { every { getWordsForGroup(1) } returns flowOf(words) }
            val h: SavedStateHandle = mockk { every { get<String>("groupId") } returns "1" }
            BlendItViewModel(
                blendItWordRepository = repo,
                blendItAttemptRepository = mockk<BlendItAttemptRepository>(relaxed = true),
                blendItWordSelector = BlendItWordSelector(),
                sessionManager = mockk<SessionManager>(relaxed = true),
                audioPlayer = mockk<AudioPlayer>(relaxed = true),
                audioResolver = mockk<AudioResolver>(relaxed = true),
                savedStateHandle = h
            )
        }
        BlendItScreen(vm, onSessionComplete = {}, onBack = {})
    }

    @Test fun findIt_wholeGridVisible() {
        compose.setContent { PlayItTheme { findIt() } }
        compose.waitForIdle()
        assertOnScreen("findit_grid"); capture("findit")
    }

    @Test fun findIt_fontScale13() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { findIt() } } }
        compose.waitForIdle()
        assertOnScreen("findit_grid")
    }

    @Test fun blendIt_tilesVisible() {
        compose.setContent { PlayItTheme { blendIt() } }
        compose.waitForIdle()
        assertOnScreen("blendit_tiles"); capture("blendit")
    }
    /** Built with the mocks of MapViewModelTest: 2 groups of 3 letters, M is the current lesson. */
    @Composable private fun map() {
        val vm = remember {
            val letters = listOf("m", "s", "a", "t", "p", "i")
            val phonemes = letters.mapIndexed { i, l -> Phoneme(i + 1, l, "p", "i", "w") }
            val groups = listOf(LetterGroup(groupId = 1, groupNumber = 1), LetterGroup(groupId = 2, groupNumber = 2))
            val members = phonemes.map { LetterGroupMember(memberId = it.id, groupId = if (it.id <= 3) 1 else 2, phonemeId = it.id, position = it.id) }
            val session: SessionManager = mockk(relaxed = true) { every { activeProfileId } returns MutableStateFlow(1L) }
            val unlock: UnlockManager = mockk { every { isPhonemeUnlocked(any(), any()) } answers { firstArg<Int>() == 1 } }
            MapViewModel(
                phonemeRepository = mockk { every { getAllPhonemes() } returns flowOf(phonemes) },
                letterGroupRepository = mockk<LetterGroupRepository> { every { getAllGroups() } returns flowOf(groups) },
                letterGroupMemberRepository = mockk<LetterGroupMemberRepository> { every { getAllMembers() } returns flowOf(members) },
                lessonProgressRepository = mockk<LessonProgressRepository> { every { getProgressForProfile(1L) } returns flowOf(emptyList()) },
                profileRepository = mockk<ProfileRepository> {
                    every { getAllProfiles() } returns flowOf(listOf(Profile(id = 1L, name = "Maximilianoooooo", avatarResId = 2)))
                },
                achievementRepository = mockk<AchievementRepository> { every { getUnlockedAchievements(1L) } returns flowOf(emptyList()) },
                sessionManager = session,
                unlockManager = unlock,
                groupUnlockManager = mockk<GroupUnlockManager>(relaxed = true),
                streakTracker = mockk<StreakTracker>(relaxed = true),
                audioPlayer = mockk<AudioPlayer>(relaxed = true),
                audioResolver = mockk<AudioResolver>(relaxed = true)
            )
        }
        MapScreen(vm, onNodeSelected = {}, onBack = {})
    }

    @Test fun map_currentNodeVisible() {
        compose.setContent { PlayItTheme { map() } }
        compose.waitForIdle()
        assertOnScreen("map_current_node"); capture("map")
    }

    /** The map's back and guidebook buttons are 64 dp, like every child-facing control (card 31e). */
    @Test fun map_buttonsAre64dp() {
        compose.setContent { PlayItTheme { map() } }
        compose.waitForIdle()
        for (tag in listOf("map_back", "unit_guidebook_1")) {
            val b = compose.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()
            // 63.5: pixels round to 63.999996 dp at 2.625x
            assertTrue("$tag is ${b.right - b.left} x ${b.bottom - b.top} on $deviceName",
                b.right - b.left >= 63.5.dp && b.bottom - b.top >= 63.5.dp)
        }
    }

    /** The unit banner's title sits inside the card face, above the 6 dp shelf (card 28b). */
    @Test fun map_unitBannerTitleFits() {
        compose.setContent { PlayItTheme { map() } }
        compose.waitForIdle()
        val banner = compose.onNodeWithTag("unit_banner_1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val title = compose.onNodeWithTag("unit_banner_title_1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("title bottom ${title.bottom} > face bottom ${banner.bottom - 6.dp} on $deviceName",
            title.bottom <= banner.bottom - 6.dp)
    }
    /** Built as LetterCompleteScreenshotTest builds it (1 star after 3 hearts lost). */
    @Composable private fun complete() {
        val vm = remember {
            val repo: PhonemeRepository = mockk(relaxed = true)
            coEvery { repo.getPhonemeById(any()) } returns mouse
            val h: SavedStateHandle = mockk {
                every { get<String>("phonemeId") } returns "1"
                every { get<String>("heartsLost") } returns "3"
            }
            LetterCompleteViewModel(
                phonemeRepository = repo,
                lessonProgressRepository = mockk<LessonProgressRepository>(relaxed = true),
                streakTracker = mockk<StreakTracker>(relaxed = true),
                sessionManager = mockk<SessionManager>(relaxed = true) { every { activeProfileId } returns MutableStateFlow(1L) },
                audioPlayer = mockk<AudioPlayer>(relaxed = true),
                audioResolver = mockk<AudioResolver>(relaxed = true),
                savedStateHandle = h
            )
        }
        LetterCompleteScreen(vm, onReturnToMap = {})
    }

    /** Built as NamePromptScreenshotTest builds it. */
    @Composable private fun namePrompt() {
        val vm = remember {
            val repo: ProfileRepository = mockk(relaxed = true) { every { getAllProfiles() } returns MutableStateFlow(emptyList()) }
            ProfileViewModel(repo, mockk<SessionManager>(relaxed = true), mockk<AudioPlayer>(relaxed = true), mockk<AudioResolver>(relaxed = true))
        }
        NamePromptScreen(vm, onProfileCreated = {}, onBack = {})
    }

    @Test fun complete_continueVisible() {
        compose.setContent { PlayItTheme { complete() } }
        compose.waitForIdle()
        assertOnScreen("complete_continue"); capture("complete")
    }

    @Test fun complete_continueVisible_fontScale13() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { complete() } } }
        compose.waitForIdle()
        assertOnScreen("complete_continue")
    }

    @Test fun splash_startVisible() {
        compose.setContent { PlayItTheme { SplashScreen(onStartClick = {}) } }
        compose.waitForIdle()
        assertOnScreen("splash_start"); capture("splash")
    }

    @Test fun splash_startVisible_fontScale13() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { SplashScreen(onStartClick = {}) } } }
        compose.waitForIdle()
        assertOnScreen("splash_start")
        assertLilyFaceClear()
    }

    /** Lily peeks over the start panel: the panel, her paws (14 dp above it) and Start stay below her face (card 31a). */
    @Test fun splash_lilyFaceClearOfStart() {
        compose.setContent { PlayItTheme { SplashScreen(onStartClick = {}) } }
        compose.waitForIdle()
        assertLilyFaceClear()
    }

    private fun assertLilyFaceClear() {
        val lily = compose.onNodeWithTag("splash_lily").getUnclippedBoundsInRoot()
        val face = lily.top + (lily.bottom - lily.top) * LILY_FACE_BOTTOM
        val panel = compose.onNodeWithTag("splash_panel", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val start = compose.onNodeWithTag("splash_start", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("paws top ${panel.top - 14.dp} above face bottom $face on $deviceName", panel.top - 14.dp >= face)
        assertTrue("Start top ${start.top} above face bottom $face on $deviceName", start.top >= face)
    }

    @Test fun nameprompt_letsPlayVisible() {
        compose.setContent { PlayItTheme { namePrompt() } }
        compose.waitForIdle()
        assertOnScreen("nameprompt_play"); capture("nameprompt")
    }

    @Test fun nameprompt_letsPlayVisible_fontScale13() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { namePrompt() } } }
        compose.waitForIdle()
        assertOnScreen("nameprompt_play")
    }
}

@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.COMPACT)
class LayoutMatrixCompactTest : LayoutMatrixTest("compact")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.A21S)
class LayoutMatrixA21sTest : LayoutMatrixTest("a21s")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.PHONE)
class LayoutMatrixPhoneTest : LayoutMatrixTest("phone")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.TABLET)
class LayoutMatrixTabletTest : LayoutMatrixTest("tablet")
