package com.playit.app.presentation.map

import androidx.compose.runtime.Recomposer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.GroupUnlockManager
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.manager.UnlockManager
import com.playit.app.domain.model.LetterGroup
import com.playit.app.domain.model.LetterGroupMember
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.Profile
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The map scrolls smoothly (user, 2026-10-30: "when i scroll down the fps is like low"): scrolling only moves
 * the map, it never rebuilds it. The header's colours follow the section on screen, so they change a few
 * times over the whole trail, never on every frame.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MapScrollTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Test fun scrolling_movesTheMapWithoutRebuildingIt() {
        compose.setContent { PlayItTheme { MapScreen(mapViewModel(), onNodeSelected = {}, onBack = {}) } }
        compose.mainClock.advanceTimeBy(5_000)   // the stops have popped in and the explorer has hopped
        compose.waitForIdle()
        val top = currentNodeTop()
        val changes = recompositions()

        // A slow drag up near the left edge, clear of every stop, inside the first section.
        compose.onNode(hasScrollAction()).performTouchInput {
            swipe(Offset(12f, centerY), Offset(12f, centerY - 150f), durationMillis = 800)
        }
        compose.waitForIdle()

        assertTrue("the map scrolled (${currentNodeTop()} vs $top)", currentNodeTop() < top - 20f)
        assertEquals("nothing on the map was rebuilt while it scrolled", changes, recompositions())
    }

    @Test fun section_followsHowFarTheMapIsScrolled() {
        assertEquals("a map that doesn't scroll is all section 1", 1, mapSectionAt(0, 0))
        assertEquals(1, mapSectionAt(0, 600))
        assertEquals(1, mapSectionAt(99, 600))
        assertEquals(2, mapSectionAt(100, 600))
        assertEquals("the bottom of the trail", 7, mapSectionAt(600, 600))
        assertEquals(7, mapSectionAt(900, 600))
    }

    private fun currentNodeTop() =
        compose.onNodeWithTag("map_current_node", useUnmergedTree = true).getUnclippedBoundsInRoot().top.value

    /** How many times the screen's compositions have changed, summed over every running recomposer. */
    private fun recompositions() = Recomposer.runningRecomposers.value.sumOf { it.changeCount }

    /** Built like LayoutMatrixTest's map: 2 groups of 3 letters, M is the current lesson. */
    private fun mapViewModel(): MapViewModel {
        val letters = listOf("m", "s", "a", "t", "p", "i")
        val phonemes = letters.mapIndexed { i, l -> Phoneme(i + 1, l, "p", "i", "w") }
        val groups = listOf(LetterGroup(groupId = 1, groupNumber = 1), LetterGroup(groupId = 2, groupNumber = 2))
        val members = phonemes.map { LetterGroupMember(memberId = it.id, groupId = if (it.id <= 3) 1 else 2, phonemeId = it.id, position = it.id) }
        val session: SessionManager = mockk(relaxed = true) { every { activeProfileId } returns MutableStateFlow(1L) }
        val unlock: UnlockManager = mockk { every { isPhonemeUnlocked(any(), any()) } answers { firstArg<Int>() == 1 } }
        return MapViewModel(
            phonemeRepository = mockk { every { getAllPhonemes() } returns flowOf(phonemes) },
            letterGroupRepository = mockk { every { getAllGroups() } returns flowOf(groups) },
            letterGroupMemberRepository = mockk { every { getAllMembers() } returns flowOf(members) },
            lessonProgressRepository = mockk { every { getProgressForProfile(1L) } returns flowOf(emptyList()) },
            profileRepository = mockk { every { getAllProfiles() } returns flowOf(listOf(Profile(id = 1L, name = "Mia", avatarResId = 2))) },
            achievementRepository = mockk { every { getUnlockedAchievements(1L) } returns flowOf(emptyList()) },
            sessionManager = session,
            unlockManager = unlock,
            groupUnlockManager = mockk<GroupUnlockManager>(relaxed = true),
            streakTracker = mockk<StreakTracker>(relaxed = true),
            audioPlayer = mockk<AudioPlayer>(relaxed = true),
            audioResolver = mockk<AudioResolver>(relaxed = true)
        )
    }
}
