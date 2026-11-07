package com.playit.app.screenshot

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.pdf.PdfExporter
import com.playit.app.domain.calculator.RecordSamples
import com.playit.app.domain.calculator.ReportGenerator
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.dashboard.ParentDashboardScreen
import com.playit.app.presentation.dashboard.ParentDashboardViewModel
import com.playit.app.presentation.dashboard.components.PARENT_LABEL_SP
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The Parent Zone, short and plain (user decisions 2026-11-07): a row per child, the child's summary with what to
 * practice next, the 26 letters as a grid with details one tap away, then the PDF report. Parents see plain words only.
 * "Visible well" is checked on all four screen sizes: the summary and the first row of letters show without scrolling,
 * no text is under 14 sp, and every tile says its state in words as well as colour. Tall captures show the whole page.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ParentDashboardScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { com.playit.app.presentation.components.AssetImageConfig.decodeSynchronously = true }

    private val frog = RecordSamples.learner(1, "Frog", done = 9, sayMisses = { if (it % 3 == 0) 1 else 0 },
        findMisses = { if (it == 6) 3 else if (it % 4 == 0) 1 else 0 })
    private val bear = RecordSamples.learner(2, "Bear", done = 4, sayMisses = { 1 })
    private val owl = RecordSamples.learner(3, "Owl", done = 0, startedNext = false)

    private fun show(learners: List<ProfileDashboardData>) {
        val profiles: ProfileRepository = mockk(relaxed = true)
        every { profiles.getAllProfiles() } returns flowOf(learners.map { it.profile })
        val generator: ReportGenerator = mockk(relaxed = true)
        learners.forEach { l -> coEvery { generator.generateDashboardData(l.profile.id) } returns l }
        coEvery { generator.generateClassRecord() } returns learners
        val session: SessionManager = mockk(relaxed = true)
        every { session.activeProfileId } returns MutableStateFlow(null)
        val vm = ParentDashboardViewModel(profiles, generator, mockk<PdfExporter>(relaxed = true), session, mockk(relaxed = true))
        compose.setContent { PlayItTheme { ParentDashboardScreen(vm, onBack = {}, onReportPreview = {}) } }
        compose.waitForIdle()
    }

    /** No teacher words, grades or percentages on the parent's screen. */
    private fun assertPlainWordsOnly() {
        compose.onAllNodesWithText("%", substring = true).assertCountEquals(0)
        compose.onAllNodesWithText("For teachers", substring = true).assertCountEquals(0)
        compose.onAllNodesWithText("grade", substring = true, ignoreCase = true).assertCountEquals(0)
        ProgressLevel.graded.forEach { compose.onAllNodesWithText(it.label).assertCountEquals(0) }
    }

    /** Every text in the tree is at least 14 sp. */
    private fun assertNoSmallText() {
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
            .fetchSemanticsNodes().forEach { node ->
                val results = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
                results.forEach { r ->
                    val size = r.layoutInput.style.fontSize.value
                    assertTrue("\"${r.layoutInput.text}\" is $size sp", size >= PARENT_LABEL_SP)
                }
            }
    }

    /** The summary and the whole first row of letters are on screen without scrolling (not just the part shown). */
    private fun assertSummaryAndFirstRowOnScreen() {
        val screen = compose.onRoot().fetchSemanticsNode().size.height
        listOf("child_summary", "grid_row_1").forEach { tag ->
            val node = compose.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode()
            val bottom = node.positionInRoot.y + node.size.height
            assertTrue("$tag ends at $bottom, the screen at $screen", bottom <= screen)
        }
    }

    @Config(sdk = [34], qualifiers = "w360dp-h3000dp-xhdpi")
    @Test fun threeChildren_rowsThenTheChild_plainWords_letterDetailsOnATap() {
        show(listOf(frog, bear, owl))
        compose.onNodeWithTag("children_card").assertExists()
        compose.onNodeWithText("Tap a child to see how they are doing.").assertExists()
        compose.onNodeWithText("Learned 9 of 26 sounds").assertExists()
        compose.onNodeWithText("Practice next: b (Ball)").assertExists()
        // Each tile says its state in words; the colour is never the only sign.
        compose.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Letter m, Mouse: Knows it")))
            .assertCountEquals(1)
        compose.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Letter b, Ball: Still learning")))
            .assertCountEquals(1)
        compose.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Letter z, Zebra: Not yet")))
            .assertCountEquals(1)
        compose.onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Word game done")))
            .assertCountEquals(2)
        // The old record's long cards and teacher legend are gone.
        listOf("Practice at home", "What the words mean", "All the children", "Chapter 1").forEach {
            compose.onAllNodesWithText(it).assertCountEquals(0)
        }
        assertPlainWordsOnly()
        assertNoSmallText()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_three_children.png")

        compose.onNodeWithTag("tile_b", useUnmergedTree = true).performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("letter_detail").assertExists()
        compose.onNodeWithText("Say It: 1 of 2 right").assertExists()
        compose.onNodeWithText("Find It: 3 of 6 right").assertExists()
        compose.onNodeWithText("Say \"b, b, ball\" together, then play this letter again.").assertExists()
        compose.onNodeWithTag("letter_detail").captureRoboImage("build/outputs/roborazzi/dashboard_letter_detail.png")
    }

    @Config(sdk = [34], qualifiers = "w411dp-h3000dp-xxhdpi")
    @Test fun oneChild_noChildRows_summaryThenTheGrid() {
        show(listOf(bear))
        compose.onAllNodesWithText("Tap a child to see how they are doing.").assertCountEquals(0)
        compose.onNodeWithText("Learned 4 of 26 sounds").assertExists()
        compose.onNodeWithText("Playing now: o (Orange)").assertExists()
        compose.onNodeWithText("Make the PDF report").assertExists()
        compose.onNodeWithText("Badges: 2 of 6").assertExists()
        assertPlainWordsOnly()
        assertNoSmallText()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_one_child.png")
    }

    private fun visibleWell(learners: List<ProfileDashboardData>, name: String) {
        show(learners)
        assertSummaryAndFirstRowOnScreen()
        assertNoSmallText()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_$name.png")
    }

    @Config(sdk = [34], qualifiers = Devices.COMPACT)
    @Test fun visibleWell_smallestPhone_twoChildren() = visibleWell(listOf(frog, bear), "compact")

    @Config(sdk = [34], qualifiers = Devices.COMPACT)
    @Test fun visibleWell_smallestPhone_oneChild() = visibleWell(listOf(bear), "compact_one_child")

    @Config(sdk = [34], qualifiers = Devices.A21S)
    @Test fun visibleWell_a21s() = visibleWell(listOf(frog, bear), "a21s")

    @Config(sdk = [34], qualifiers = Devices.PHONE)
    @Test fun visibleWell_phone() = visibleWell(listOf(frog, bear, owl), "phone")

    @Config(sdk = [34], qualifiers = Devices.TABLET)
    @Test fun visibleWell_tablet() = visibleWell(listOf(frog, bear, owl), "tablet")
}
