package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.model.TradeStatus
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            _uiState.update { it.copy(isLoading = true) }
            try {
                val proposals = proposalRepository.getProposalsReceivedForUser(userId)
                _uiState.update { it.copy(isLoading = false, proposals = proposals) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                // 1. Actualiza el estado de la propuesta
                proposalRepository.updateProposalStatus(proposal.id, "ACEPTADA")

                // 2. CORRECCIÓN: Llama al método correcto del repositorio (`createTradeFromProposal`)
                // que se encarga de crear el objeto Trade internamente.
                tradeRepository.createTradeFromProposal(
                    proposal = proposal,
                    receiverName = auth.currentUser?.displayName ?: "Usuario"
                )

                // 3. Notifica al proponente que su propuesta fue aceptada
                val notification = NotificationItem(
                    userId = proposal.proposerId,
                    title = "¡Tu propuesta ha sido aceptada!",
                    message = "Tu propuesta para '${proposal.publicationTitle}' fue aceptada. ¡Coordina el intercambio!",
                    type = "proposal_accepted",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)

                // 4. Recarga la lista para que la propuesta aceptada desaparezca de la UI
                loadReceivedProposals()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al aceptar la propuesta: ${e.message}") }
            }
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "RECHAZADA")

                val notification = NotificationItem(
                    title = "Propuesta rechazada",
                    message = "Lamentablemente, tu propuesta para '${proposal.publicationTitle}' fue rechazada.",
                    userId = proposal.proposerId,
                    type = "proposal_rejected",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)

                loadReceivedProposals()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al rechazar la propuesta: ${e.message}") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}