package com.playit.app.presentation.dashboard

import com.playit.app.data.pdf.PdfExporter
import com.playit.app.domain.calculator.ReportGenerator
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ReportData
import com.playit.app.domain.repository.ProfileRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ParentDashboardViewModelTest {

    private lateinit var viewModel: ParentDashboardViewModel
    private val profileRepository: ProfileRepository = mockk(relaxed = true)
    private val reportGenerator: ReportGenerator = mockk(relaxed = true)
    private val pdfExporter: PdfExporter = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val profilesFlow = MutableSharedFlow<List<Profile>>(replay = 1)

    private val sampleProfile1 = Profile(id = 1L, name = "Maya", avatarResId = 1)
    private val sampleProfile2 = Profile(id = 2L, name = "Liam", avatarResId = 2)

    private val sampleDashboardData = ProfileDashboardData(
        profile = sampleProfile1,
        totalStars = 24,
        retentionScore = 0.85f,
        overallAccuracy = 0.90f,
        completedLettersCount = 8,
        totalLettersCount = 26,
        letterPerformances = emptyList(),
        blendItCompletedCount = 2,
        blendItTotalCount = 7,
        atRiskLetters = emptyList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { profileRepository.getAllProfiles() } returns profilesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_selectsFirstProfileAndLoadsDashboardData() = runTest {
        coEvery { reportGenerator.generateDashboardData(1L) } returns sampleDashboardData

        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(listOf(sampleProfile1, sampleProfile2))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.profiles.size)
        assertEquals(sampleProfile1, state.selectedProfile)
        assertEquals(sampleDashboardData, state.dashboardData)
        assertFalse(state.isLoading)
        assertTrue(state.exportStatus is ExportStatus.Idle)
    }

    @Test
    fun init_withEmptyProfiles_setsLoadingFalse() = runTest {
        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(emptyList())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.profiles.isEmpty())
        assertNull(state.selectedProfile)
        assertFalse(state.isLoading)
    }

    @Test
    fun selectProfile_loadsDataForSelectedProfile() = runTest {
        val sampleDashboardData2 = sampleDashboardData.copy(profile = sampleProfile2)
        coEvery { reportGenerator.generateDashboardData(1L) } returns sampleDashboardData
        coEvery { reportGenerator.generateDashboardData(2L) } returns sampleDashboardData2

        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(listOf(sampleProfile1, sampleProfile2))
        advanceUntilIdle()

        viewModel.selectProfile(sampleProfile2)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(sampleProfile2, state.selectedProfile)
        assertEquals(sampleDashboardData2, state.dashboardData)
        assertFalse(state.isLoading)
    }

    @Test
    fun selectProfile_handlesDashboardDataError() = runTest {
        coEvery { reportGenerator.generateDashboardData(any()) } throws RuntimeException("Data error")

        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(listOf(sampleProfile1))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.exportStatus is ExportStatus.Error)
        assertEquals("Data error", (state.exportStatus as ExportStatus.Error).message)
    }

    @Test
    fun exportPdfReport_success_updatesExportStatus() = runTest {
        val dummyReportData: ReportData = mockk()
        val dummyFile = File("test_report.pdf")
        coEvery { reportGenerator.generateDashboardData(1L) } returns sampleDashboardData
        coEvery { reportGenerator.generateReportData(1L) } returns dummyReportData
        coEvery { pdfExporter.exportReport(dummyReportData) } returns Result.success(dummyFile)

        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(listOf(sampleProfile1))
        advanceUntilIdle()

        viewModel.exportPdfReport()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.exportStatus is ExportStatus.Success)
        assertEquals(dummyFile, (state.exportStatus as ExportStatus.Success).file)
    }

    @Test
    fun exportPdfReport_failure_updatesExportStatus() = runTest {
        val dummyReportData: ReportData = mockk()
        coEvery { reportGenerator.generateDashboardData(1L) } returns sampleDashboardData
        coEvery { reportGenerator.generateReportData(1L) } returns dummyReportData
        coEvery { pdfExporter.exportReport(dummyReportData) } returns Result.failure(RuntimeException("Disk full"))

        viewModel = ParentDashboardViewModel(profileRepository, reportGenerator, pdfExporter)
        profilesFlow.emit(listOf(sampleProfile1))
        advanceUntilIdle()

        viewModel.exportPdfReport()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.exportStatus is ExportStatus.Error)
        assertEquals("Disk full", (state.exportStatus as ExportStatus.Error).message)

        viewModel.resetExportStatus()
        assertTrue(viewModel.uiState.value.exportStatus is ExportStatus.Idle)
    }
}
