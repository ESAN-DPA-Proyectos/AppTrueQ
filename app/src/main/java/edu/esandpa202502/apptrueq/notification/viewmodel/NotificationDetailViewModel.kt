package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// QA: Se crea un estado de UI específico para la pantalla de detalle.
data class NotificationDetailUiState(
    val isLoading: Boolean = false,
    val proposal: Proposal? = null,
    val error: String? = null,
    val actionCompleted: Boolean = false // Se activa después de aceptar/rechazar
)

// QA: Se crea un ViewModel específico para esta pantalla para seguir la arquitectura MVVM.
class NotificationDetailViewModel : ViewModel() {

    private val proposalRepository = ProposalRepository()

    private val _detailUiState = MutableStateFlow(NotificationDetailUiState())
    val detailUiState: StateFlow<NotificationDetailUiState> = _detailUiState.asStateFlow()

    fun loadProposal(proposalId: String) {
        if (proposalId.isBlank()) return

        viewModelScope.launch {
            _detailUiState.update { it.copy(isLoading = true) }
            try {
                val proposal = proposalRepository.getProposalById(proposalId)
                _detailUiState.update { it.copy(isLoading = false, proposal = proposal) }
            } catch (e: Exception) {
                _detailUiState.update { it.copy(isLoading = false, error = "Error al cargar la propuesta: ${e.message}") }
            }
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "ACEPTADA")
                _detailUiState.update { it.copy(actionCompleted = true) }
            } catch (e: Exception) {
                _detailUiState.update { it.copy(error = "Error al aceptar la propuesta.") }
            }
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "RECHAZADA")
                _detailUiState.update { it.copy(actionCompleted = true) }
            } catch (e: Exception) {
                _detailUiState.update { it.copy(error = "Error al rechazar la propuesta.") }
            }
        }
    }
}
