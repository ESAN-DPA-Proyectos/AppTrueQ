package edu.esandpa202502.apptrueq.reportUsr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.repository.reportUsr.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}

class ReportViewModel(
    private val reportRepository: ReportRepository = ReportRepository()
) : ViewModel() {

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState

    fun submitReport(
        publicationId: String,
        reportedUserId: String,
        reportedUserName: String,
        reason: String,
        description: String,
        reporterId: String,
        reporterName: String // Añadido
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                reportRepository.submitReport(
                    publicationId = publicationId,
                    reportedUserId = reportedUserId,
                    reportedUserName = reportedUserName,
                    reason = reason,
                    description = description,
                    reporterId = reporterId,
                    reporterName = reporterName // Añadido
                )
                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(
                    e.message ?: "Error al enviar el reporte"
                )
            }
        }
    }

    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}
