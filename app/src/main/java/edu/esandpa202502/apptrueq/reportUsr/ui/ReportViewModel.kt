package edu.esandpa202502.apptrueq.reportUsr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Report
import edu.esandpa202502.apptrueq.repository.reportUsr.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Reportar Usuario (HU-10).
 */
data class ReportUiState(
    val isLoading: Boolean = false,
    val reportSent: Boolean = false,
    val error: String? = null
)

class ReportViewModel : ViewModel() {

    private val reportRepository = ReportRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Envía un reporte a la base de datos.
     */
    fun submitReport(reportedUserId: String, reason: String, comments: String) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "El motivo del reporte no puede estar vacío.") }
            return
        }

        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _uiState.update { it.copy(error = "No se pudo identificar al reportante. Inicia sesión de nuevo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // CORRECCIÓN: Se usan los nombres de campo correctos del modelo `Report.kt`.
                val newReport = Report(
                    reportingUserId = currentUserId, // El nombre correcto es reportingUserId
                    reportedUserId = reportedUserId,
                    reason = reason,
                    description = comments // El nombre correcto es description
                    // No se envía `createdAt`, ya que @ServerTimestamp lo maneja automáticamente.
                )
                reportRepository.submitReport(newReport)
                _uiState.update { it.copy(isLoading = false, reportSent = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar el reporte: ${e.message}") }
            }
        }
    }
}
