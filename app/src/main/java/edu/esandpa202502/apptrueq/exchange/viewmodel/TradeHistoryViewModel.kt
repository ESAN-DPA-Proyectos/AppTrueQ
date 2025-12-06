package edu.esandpa202502.apptrueq.exchange.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Historial de Trueques (HU-09).
 */
data class TradeHistoryUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val error: String? = null
)

class TradeHistoryViewModel : ViewModel() {

    private val proposalRepository = ProposalRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TradeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    private var allProposals: List<Proposal> = emptyList()
    private val _statusFilter = MutableStateFlow("Todos")
    val statusFilter = _statusFilter.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                allProposals = proposalRepository.getProposalHistoryForUser(userId)
                applyFilter(allProposals, _statusFilter.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar el historial: ${e.message}") }
            } finally {
                 _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onStatusFilterChanged(newStatus: String) {
        _statusFilter.value = newStatus
        applyFilter(allProposals, newStatus)
    }

    private fun applyFilter(proposals: List<Proposal>, status: String) {
        val filteredList = if (status.equals("Todos", ignoreCase = true)) {
            proposals
        } else {
            proposals.filter { it.status.equals(status, ignoreCase = true) }
        }
        _uiState.update { it.copy(proposals = filteredList) }
    }
}
