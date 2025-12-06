package edu.esandpa202502.apptrueq.moderation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Report
import edu.esandpa202502.apptrueq.moderation.ui.ReportStatus
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

    private val _resolvedReportsCount = MutableStateFlow(0)
    val resolvedReportsCount: StateFlow<Int> = _resolvedReportsCount

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

    fun reviewReport(reportId: String) {
        viewModelScope.launch {
            try {
                reportRepository.reviewReport(reportId)
                updateReportStatus(reportId, ReportStatus.IN_REVIEW)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun resolveReport(reportId: String) {
        viewModelScope.launch {
            try {
                reportRepository.resolveReport(reportId)
                updateReportStatus(reportId, ReportStatus.RESOLVED)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    private fun updateReportStatus(reportId: String, newStatus: String) {
        val updatedReports = _reports.value.map {
            if (it.id == reportId) it.copy(status = newStatus) else it
        }
        _reports.value = updatedReports
        updateCounts(updatedReports)
    }

    private fun updateCounts(reports: List<Report>) {
        _pendingReportsCount.value = reports.count { it.status == ReportStatus.PENDING }
        _inReviewReportsCount.value = reports.count { it.status == ReportStatus.IN_REVIEW }
        _resolvedReportsCount.value = reports.count { it.status == ReportStatus.RESOLVED }
    }
}
