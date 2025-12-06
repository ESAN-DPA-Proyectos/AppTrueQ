package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.repository.exchange.TradeRepository
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProposalsUiState(
    val isLoading: Boolean = false,
    val proposals: List<Proposal> = emptyList(),
    val offeredPublicationTitles: Map<String, String> = emptyMap(), // Mapa de ID -> Título
    val error: String? = null
)

class ProposalsReceivedViewModel : ViewModel() {

    private val proposalRepository = ProposalRepository()
    private val tradeRepository = TradeRepository()
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProposalsUiState())
    val uiState: StateFlow<ProposalsUiState> = _uiState.asStateFlow()

    // --- Estado para el nuevo filtro ---
    private val _viewMode = MutableStateFlow("Pendientes") // Opciones: "Pendientes", "Todos"
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()

    init {
        loadProposalsReceived()
    }

    fun onViewModeChanged(newMode: String) {
        _viewMode.value = newMode
    }

    fun loadProposalsReceived() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val proposals = proposalRepository.getProposalsReceivedForUser(userId)
                
                // Carga los títulos de las publicaciones ofrecidas en paralelo
                val titleJobs = proposals.mapNotNull { it.offeredPublicationId }.distinct().map {
                    async { it to proposalRepository.getPublicationTitle(it) }
                }
                val titlesMap = titleJobs.awaitAll().toMap().filterValues { it != null } as Map<String, String>

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        proposals = proposals,
                        offeredPublicationTitles = titlesMap,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar las propuestas: ${e.message}")
                }
            }
        }
    }

    fun acceptProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "ACEPTADA")

                tradeRepository.createTrade(
                    offerId = proposal.publicationId,
                    receiverId = proposal.publicationOwnerId,
                    proposerId = proposal.proposerId,
                    proposerName = proposal.proposerName
                )

                notificationRepository.addNotification(
                    NotificationItem(
                        userId = proposal.proposerId,
                        title = "¡Tu propuesta fue aceptada!",
                        message = "El dueño de '${proposal.publicationTitle}' aceptó tu propuesta.",
                        type = "proposal_accepted",
                        referenceId = proposal.id
                    )
                )

                loadProposalsReceived() 
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al aceptar la propuesta: ${e.message}") }
            }
        }
    }

    fun rejectProposal(proposal: Proposal) {
        viewModelScope.launch {
            try {
                proposalRepository.updateProposalStatus(proposal.id, "RECHAZADA")

                notificationRepository.addNotification(
                    NotificationItem(
                        userId = proposal.proposerId,
                        title = "Propuesta rechazada",
                        message = "Tu propuesta para '${proposal.publicationTitle}' fue rechazada.",
                        type = "proposal_rejected",
                        referenceId = proposal.id
                    )
                )

                loadProposalsReceived()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al rechazar la propuesta.") }
            }
        }
    }
}
