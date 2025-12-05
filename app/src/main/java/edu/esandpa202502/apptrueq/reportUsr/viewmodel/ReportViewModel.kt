package edu.esandpa202502.apptrueq.reportUsr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.repository.reportUsr.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estado simple para HU-10
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

    /**
     * Envía un reporte usando los campos que llenas en el formulario.
     * (No hay más `isSuccess`, ni `exceptionOrNull`, ni parámetros raros.)
     */
    fun submitReport(
        reportedEmail: String,
        reason: String,
        description: String,
        reporterId: String
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                reportRepository.submitReport(
                    reportedEmail = reportedEmail,
                    reason = reason,
                    description = description,
                    reporterId = reporterId
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
