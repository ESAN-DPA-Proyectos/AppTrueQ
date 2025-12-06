package edu.esandpa202502.apptrueq.moderation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Report
import edu.esandpa202502.apptrueq.repository.reportUsr.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModerationViewModel : ViewModel() {

    private val reportRepository = ReportRepository()

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _pendingReportsCount = MutableStateFlow(0)
    val pendingReportsCount: StateFlow<Int> = _pendingReportsCount

    private val _inReviewReportsCount = MutableStateFlow(0)
    val inReviewReportsCount: StateFlow<Int> = _inReviewReportsCount

    init {
        fetchReports()
    }

    private fun fetchReports() {
        viewModelScope.launch {
            val allReports = reportRepository.getAllReports()
            _reports.value = allReports
            updateCounts(allReports)
        }
    }

    fun resolveReport(reportId: String) {
        viewModelScope.launch {
            try {
                reportRepository.resolveReport(reportId)
                // Actualizar la lista local para reflejar el cambio en la UI
                val updatedReports = _reports.value.map {
                    if (it.id == reportId) {
                        it.copy(status = "Resuelta")
                    } else {
                        it
                    }
                }
                _reports.value = updatedReports
                updateCounts(updatedReports)
            } catch (e: Exception) {
                // Manejar el error, por ejemplo, mostrando un snackbar
            }
        }
    }

    private fun updateCounts(reports: List<Report>) {
        _pendingReportsCount.value = reports.count { it.status == "Pendiente de revisión" }
        _inReviewReportsCount.value = reports.count { it.status == "En revisión" }
    }
}
