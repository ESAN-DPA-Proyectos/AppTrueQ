package edu.esandpa202502.apptrueq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.notification.repository.NotificationRepository
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.publication.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PublicationDetailUiState(
    val isLoading: Boolean = true,
    val publication: Publication? = null,
    val userPublications: List<Publication> = emptyList(),
    val error: String? = null,
    val proposalSent: Boolean = false,
    val isSubmitting: Boolean = false
)

class PublicationDetailViewModel(private val publicationId: String) : ViewModel() {

    private val publicationRepository = PublicationRepository()
    private val proposalRepository = ProposalRepository()
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

    private val offensiveWords = listOf("tonto", "estúpido", "idiota")

    init {
        loadPublication()
        loadUserPublications()
    }

    private fun loadPublication() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val publication = publicationRepository.getPublicationById(publicationId)
                _uiState.value = _uiState.value.copy(isLoading = false, publication = publication)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al cargar la publicación.")
            }
        }
    }

    private fun loadUserPublications() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val publications = publicationRepository.getPublicationsByUserId(currentUser.uid)
                _uiState.value = _uiState.value.copy(userPublications = publications)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = _uiState.value.error ?: "Error al cargar tus publicaciones.")
            }
        }
    }

    fun submitProposal(proposalText: String, offeredPublicationId: String?) {
        val publication = _uiState.value.publication ?: return
        val currentUser = auth.currentUser ?: return

        if (proposalText.length !in 10..250) {
            _uiState.value = _uiState.value.copy(error = "El mensaje debe tener entre 10 y 250 caracteres.")
            return
        }
        if (offeredPublicationId == null) {
            _uiState.value = _uiState.value.copy(error = "Debes seleccionar una de tus publicaciones para ofrecer a cambio.")
            return
        }
        if (offensiveWords.any { proposalText.contains(it, ignoreCase = true) }) {
            _uiState.value = _uiState.value.copy(error = "El mensaje contiene lenguaje inapropiado.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            try {
                val hasActive = proposalRepository.hasActiveProposal(currentUser.uid, publication.id)
                if (hasActive) {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, error = "Ya tienes una propuesta pendiente para esta publicación.")
                    return@launch
                }

                val proposal = Proposal(
                    publicationId = publication.id,
                    publicationOwnerId = publication.userId,
                    publicationTitle = publication.title,
                    proposerId = currentUser.uid,
                    proposerName = currentUser.displayName ?: "Usuario Anónimo",
                    proposalText = proposalText,
                    offeredPublicationId = offeredPublicationId,
                    status = "PENDIENTE"
                )
                
                val newProposalId = proposalRepository.addProposal(proposal)

                val notification = NotificationItem(
                    title = "¡Has recibido una nueva propuesta!",
                    message = "${currentUser.displayName ?: "Alguien"} ha hecho una propuesta para tu publicación: '${publication.title}'.",
                    userId = publication.userId,
                    type = "new_proposal",
                    referenceId = newProposalId
                )
                notificationRepository.addNotification(notification)

                _uiState.value = _uiState.value.copy(isSubmitting = false, proposalSent = true)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSubmitting = false, error = "Error al enviar la propuesta: ${e.message}")
            }
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(error = null, proposalSent = false)
    }
}

class PublicationDetailViewModelFactory(private val publicationId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicationDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PublicationDetailViewModel(publicationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}