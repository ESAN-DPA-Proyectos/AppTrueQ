package edu.esandpa202502.apptrueq.reportUsr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun submitReport(reportedEmail: String, reason: String, description: String) {
        // Se utiliza el método estándar para obtener la instancia de FirebaseAuth
        val reporterId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _uiState.value = ReportUiState(error = "No se pudo identificar al usuario actual.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ReportUiState(isLoading = true)
            val result = repository.submitReport(
                reportedEmail = reportedEmail,
                reason = reason,
                description = description,
                reporterId = reporterId
            )
            if (result.isSuccess) {
                _uiState.value = ReportUiState(isSuccess = true)
            } else {
                _uiState.value = ReportUiState(error = result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState()
    }
}