package edu.esandpa202502.apptrueq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.publication.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// HU-06: Se añade un campo para los errores de validación en el diálogo.
data class PublicationDetailUiState(
    val isLoading: Boolean = true,
    val publication: Publication? = null,
    val userPublications: List<Publication> = emptyList(),
    val error: String? = null,
    val proposalError: String? = null, // Para mostrar errores específicos del diálogo
    val proposalSent: Boolean = false
)

class PublicationDetailViewModel(private val publicationId: String) : ViewModel() {

    private val publicationRepository = PublicationRepository()
    private val proposalRepository = ProposalRepository() // Se añade el repositorio de propuestas
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

    init {
        loadPublication()
        loadUserPublications()
    }

    private fun loadPublication() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val publication = publicationRepository.getPublicationById(publicationId)
                _uiState.update { it.copy(isLoading = false, publication = publication) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar la publicación.") }
            }
        }
    }

    private fun loadUserPublications() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val publications = publicationRepository.getPublicationsByUserId(currentUser.uid)
                _uiState.update { it.copy(userPublications = publications) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = _uiState.value.error ?: "Error al cargar tus publicaciones.") }
            }
        }
    }

    fun submitProposal(proposalText: String, offeredPublicationId: String? = null) {
        val publication = _uiState.value.publication ?: return
        val currentUser = auth.currentUser ?: return

        // HU-06: Validación de la longitud del texto de la propuesta.
        if (proposalText.length !in 10..250) {
            _uiState.update { it.copy(proposalError = "El mensaje debe tener entre 10 y 250 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, proposalError = null) }
            try {
                // HU-06: Validación anti-spam. Comprueba si ya existe una propuesta.
                val hasExisting = proposalRepository.hasExistingProposal(currentUser.uid, publication.id)
                if (hasExisting) {
                    _uiState.update { it.copy(isLoading = false, proposalError = "Ya has enviado una propuesta para esta publicación.") }
                    return@launch
                }

                // El estado por defecto del modelo `Proposal` ya es "PENDIENTE", cumpliendo el requisito.
                val proposal = Proposal(
                    publicationId = publication.id,
                    publicationOwnerId = publication.userId,
                    publicationTitle = publication.title,
                    proposerId = currentUser.uid,
                    proposerName = currentUser.displayName ?: "Usuario Anónimo",
                    proposalText = proposalText,
                    offeredPublicationId = offeredPublicationId
                )

                val db = FirebaseFirestore.getInstance()
                val proposalRef = db.collection("proposals").add(proposal).await()

                val notification = NotificationItem(
                    title = "¡Has recibido una nueva propuesta!",
                    message = "${currentUser.displayName ?: "Alguien"} ha hecho una propuesta para tu publicación: '${publication.title}'.",
                    userId = publication.userId,
                    type = "new_proposal",
                    referenceId = proposalRef.id
                )
                notificationRepository.addNotification(notification)

                _uiState.update { it.copy(isLoading = false, proposalSent = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar la propuesta: ${e.message}") }
            }
        }
    }

    // Función para limpiar el error del diálogo cuando se cierra.
    fun clearProposalError() {
        _uiState.update { it.copy(proposalError = null) }
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
