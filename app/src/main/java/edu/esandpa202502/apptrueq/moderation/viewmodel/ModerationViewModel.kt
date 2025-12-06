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
            _pendingReportsCount.value = allReports.count { it.status == "Pendiente" }
            _inReviewReportsCount.value = allReports.count { it.status == "En revisión" }
        }
    }
}
