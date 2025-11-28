
package edu.esandpa202502.apptrueq.proposal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.proposal.repository.ProposalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProposalsHistoryUiState(
    val isLoading: Boolean = true,
    val sentProposals: List<Proposal> = emptyList(),
    val receivedProposals: List<Proposal> = emptyList(),
    val acceptedProposals: List<Proposal> = emptyList(),
    val error: String? = null
)

class ProposalsHistoryViewModel : ViewModel() {

    private val repository = ProposalsRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProposalsHistoryUiState())
    val uiState: StateFlow<ProposalsHistoryUiState> = _uiState.asStateFlow()

    init {
        loadProposals()
    }

    private fun loadProposals() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = ProposalsHistoryUiState(isLoading = true)
            try {
                val sent = repository.getSentProposals(userId)
                val received = repository.getReceivedProposals(userId)
                val accepted = repository.getAcceptedProposals(userId)
                _uiState.value = ProposalsHistoryUiState(
                    isLoading = false,
                    sentProposals = sent,
                    receivedProposals = received,
                    acceptedProposals = accepted
                )
            } catch (e: Exception) {
                _uiState.value = ProposalsHistoryUiState(isLoading = false, error = "Error al cargar las propuestas.")
            }
        }
    }
}
