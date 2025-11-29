package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.notification.repository.NotificationRepository
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class ProposalsReceivedUiState(
    val isLoading: Boolean = true,
    val proposals: List<Proposal> = emptyList(),
    val error: String? = null
)

class ProposalsReceivedViewModel : ViewModel() {

    private val proposalRepository = ProposalRepository()
    private val tradeRepository = TradeRepository()
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProposalsReceivedUiState())
    val uiState: StateFlow<ProposalsReceivedUiState> = _uiState.asStateFlow()

    init {
        loadReceivedProposals()
    }

    private fun loadReceivedProposals() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ProposalsReceivedUiState(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            proposalRepository.getReceivedProposals(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
                .collect { proposals ->
                    _uiState.value = ProposalsReceivedUiState(isLoading = false, proposals = proposals)
                }
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                // 1. Actualizar estado de la propuesta
                proposalRepository.updateProposalStatus(proposal.id, "ACEPTADA")

                // 2. Crear el registro del trueque
                val currentUser = auth.currentUser
                tradeRepository.createTradeFromProposal(proposal, currentUser?.displayName ?: "Usuario Anónimo")

                // 3. Notificar al emisor
                val notification = NotificationItem(
                    title = "¡Tu propuesta ha sido aceptada!",
                    message = "Tu propuesta para '${proposal.publicationTitle}' fue aceptada. ¡Coordina el intercambio!",
                    userId = proposal.proposerId,
                    type = "proposal_accepted",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al aceptar la propuesta: ${e.message}")
            }
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                // 1. Actualizar estado de la propuesta
                proposalRepository.updateProposalStatus(proposal.id, "RECHAZADA")

                // 2. Notificar al emisor
                val notification = NotificationItem(
                    title = "Propuesta rechazada",
                    message = "Lamentablemente, tu propuesta para '${proposal.publicationTitle}' fue rechazada.",
                    userId = proposal.proposerId,
                    type = "proposal_rejected",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al rechazar la propuesta: ${e.message}")
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
