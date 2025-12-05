package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * SOLUCIÓN: Se rediseña el UiState para que contenga las 3 listas que la UI necesita.
 */
data class ProposalsHistoryUiState(
    val isLoading: Boolean = true,
    val sentProposals: List<Proposal> = emptyList(),
    val receivedProposals: List<Proposal> = emptyList(),
    val acceptedProposals: List<Proposal> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel rediseñado para la pantalla de historial de propuestas.
 */
class ProposalsHistoryViewModel : ViewModel() {

    private val proposalRepository = ProposalRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProposalsHistoryUiState())
    val uiState: StateFlow<ProposalsHistoryUiState> = _uiState.asStateFlow()

    init {
        loadProposalsHistory()
    }

    private fun loadProposalsHistory() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Obtiene las dos listas de propuestas desde el repositorio.
                val sent = proposalRepository.getProposalsSentByUser(userId)
                val received = proposalRepository.getProposalsReceivedForUser(userId)

                // 2. Clasifica las propuestas en las 3 categorías que la UI necesita.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sentProposals = sent.filter { p -> p.status == "PENDIENTE" },
                        receivedProposals = received, // Ya vienen filtradas como PENDIENTE desde el repo
                        acceptedProposals = sent.filter { p -> p.status == "ACEPTADA" }
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar el historial de propuestas.") }
            }
        }
    }
}