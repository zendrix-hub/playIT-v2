package com.playit.app.screenshot

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.data.pdf.PdfExporter
import com.playit.app.domain.calculator.RecordSamples
import com.playit.app.domain.calculator.ReportGenerator
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.dashboard.ParentDashboardScreen
import com.playit.app.presentation.dashboard.ParentDashboardViewModel
import com.playit.app.presentation.theme.PlayItTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The Parent Zone as a class record (adviser, 2026-11-05): levels in words on every row, and no bare percentage.
 * Tall captures show the whole scrolling page for review.
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
        val vm = ParentDashboardViewModel(profiles, generator, mockk<PdfExporter>(relaxed = true), session)
        compose.setContent { PlayItTheme { ParentDashboardScreen(vm, onBack = {}, onReportPreview = {}) } }
        compose.waitForIdle()
    }

    @Config(sdk = [34], qualifiers = "w360dp-h3000dp-xhdpi")
    @Test fun threeChildren_classRecordThenTheSelectedChild() {
        show(listOf(frog, bear, owl))
        compose.onNodeWithText("All the children").assertExists()
        compose.onNodeWithText("Practise at home").assertExists()
        compose.onNodeWithText("Chapter 1").assertExists()
        compose.onAllNodesWithText("%", substring = true).assertCountEquals(0)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_three_children.png")
    }

    @Config(sdk = [34], qualifiers = "w411dp-h3000dp-xxhdpi")
    @Test fun oneChild_hasNoClassRecord() {
        show(listOf(bear))
        compose.onAllNodesWithText("All the children").assertCountEquals(0)
        compose.onNodeWithText("Learned 4 of 26 sounds").assertExists()
        compose.onAllNodesWithText("%", substring = true).assertCountEquals(0)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_one_child.png")
    }

    @Config(sdk = [34], qualifiers = Devices.COMPACT)
    @Test fun smallestPhone_headerAndClassRecordFit() {
        show(listOf(frog, bear))
        compose.onNodeWithText("How your child is doing").assertExists()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/dashboard_compact.png")
    }
}
