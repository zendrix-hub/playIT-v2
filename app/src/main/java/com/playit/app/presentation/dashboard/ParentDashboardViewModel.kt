package com.playit.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.pdf.PdfExporter
import com.playit.app.domain.calculator.ReportGenerator
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ReportData
import com.playit.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed interface ExportStatus {
    object Idle : ExportStatus
    object Exporting : ExportStatus
    data class Success(val file: File) : ExportStatus
    data class Error(val message: String) : ExportStatus
}

data class ParentDashboardUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfile: Profile? = null,
    val dashboardData: ProfileDashboardData? = null,
    val isLoading: Boolean = true,
    val exportStatus: ExportStatus = ExportStatus.Idle
)

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val reportGenerator: ReportGenerator,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentDashboardUiState())
    val uiState: StateFlow<ParentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            profileRepository.getAllProfiles().collectLatest { profiles ->
                val currentSelected = _uiState.value.selectedProfile ?: profiles.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    profiles = profiles,
                    selectedProfile = currentSelected
                )
                currentSelected?.let { selectProfile(it) } ?: run {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun selectProfile(profile: Profile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedProfile = profile, isLoading = true)
            val data = reportGenerator.generateDashboardData(profile.id)
            _uiState.value = _uiState.value.copy(
                dashboardData = data,
                isLoading = false
            )
        }
    }

    fun exportPdfReport() {
        val selected = _uiState.value.selectedProfile ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Exporting)
            val reportData: ReportData? = reportGenerator.generateReportData(selected.id)
            if (reportData != null) {
                val result = pdfExporter.exportReport(reportData)
                result.fold(
                    onSuccess = { file ->
                        _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Success(file))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Error(error.message ?: "PDF generation failed"))
                    }
                )
            } else {
                _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Error("Report data unavailable"))
            }
        }
    }

    fun resetExportStatus() {
        _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Idle)
    }
}
