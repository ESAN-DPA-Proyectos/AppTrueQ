package edu.esandpa202502.apptrueq.exchange.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.trade.TradeRepository
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Estado de la UI para la pantalla de Propuestas Recibidas (HU-07).
 */
data class ExchangeUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val error: String? = null
)

class ExchangeViewModel : ViewModel() {

    // --- REPOSITORIOS ---
    private val proposalRepository = ProposalRepository()
    private val tradeRepository = TradeRepository()
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    // --- ESTADO DE LA UI ---
    private val _uiState = MutableStateFlow(ExchangeUiState())
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    init {
        // Al iniciar el ViewModel, cargamos las propuestas recibidas por el usuario actual.
        loadProposalsReceived()
    }

    /**
     * Carga la lista de propuestas pendientes recibidas por el usuario.
     */
    fun loadProposalsReceived() {
        // Obtenemos el ID del usuario actual. Si no está logueado, no hacemos nada.
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val proposals = proposalRepository.getProposalsReceivedForUser(userId)
                _uiState.update { it.copy(isLoading = false, proposals = proposals) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar las propuestas: ${e.message}") }
            }
        }
    }

    /**
     * Acepta una propuesta, crea el trueque y notifica al proponente.
     */
    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "ACEPTADA")
                val newTrade = Trade(
                    publicationId = proposal.publicationId,
                    offerentId = proposal.proposerId,
                    receiverId = proposal.publicationOwnerId,
                    status = "aceptado"
                )
                tradeRepository.createTrade(newTrade)

                val notification = NotificationItem(
                    userId = proposal.proposerId,
                    title = "¡Tu propuesta fue aceptada!",
                    message = "El dueño de '${proposal.publicationTitle}' ha aceptado tu propuesta.",
                    type = "proposal_accepted",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)
                
                // Recargamos la lista para que la propuesta aceptada ya no aparezca.
                loadProposalsReceived()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al aceptar la propuesta.") }
            }
        }
    }

    /**
     * Rechaza una propuesta y notifica al proponente.
     */
    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "RECHAZADA")

                val notification = NotificationItem(
                    userId = proposal.proposerId,
                    title = "Propuesta rechazada",
                    message = "Tu propuesta para '${proposal.publicationTitle}' fue rechazada.",
                    type = "proposal_rejected",
                    referenceId = proposal.id
                )
                notificationRepository.addNotification(notification)

                // Recargamos la lista para que la propuesta rechazada ya no aparezca.
                loadProposalsReceived()
                
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al rechazar la propuesta.") }
            }
        }
    }
}
