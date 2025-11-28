package edu.esandpa202502.apptrueq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.notification.repository.NotificationRepository
import edu.esandpa202502.apptrueq.repository.publication.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PublicationDetailUiState(
    val isLoading: Boolean = true,
    val publication: Publication? = null,
    val userPublications: List<Publication> = emptyList(), // Nueva lista para las publicaciones del usuario
    val error: String? = null,
    val proposalSent: Boolean = false
)

class PublicationDetailViewModel(private val publicationId: String) : ViewModel() {

    private val publicationRepository = PublicationRepository()
    private val notificationRepository = NotificationRepository()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

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
                // Manejar error silenciosamente por ahora para no bloquear la UI principal
                _uiState.value = _uiState.value.copy(error = _uiState.value.error ?: "Error al cargar tus publicaciones.")
            }
        }
    }

    fun submitProposal(proposalText: String, offeredPublicationId: String? = null) {
        val publication = _uiState.value.publication ?: return
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                val proposal = Proposal(
                    publicationId = publication.id,
                    publicationOwnerId = publication.userId,
                    publicationTitle = publication.title,
                    proposerId = currentUser.uid,
                    proposerName = currentUser.displayName ?: "Usuario Anónimo",
                    proposalText = proposalText,
                    offeredPublicationId = offeredPublicationId // ID de la publicación que se ofrece
                )

                val proposalRef = db.collection("proposals").add(proposal).await()

                val notification = NotificationItem(
                    title = "¡Has recibido una nueva propuesta!",
                    message = "${currentUser.displayName ?: "Alguien"} ha hecho una propuesta para tu publicación: '${publication.title}'.",
                    userId = publication.userId,
                    type = "new_proposal",
                    referenceId = proposalRef.id
                )
                notificationRepository.addNotification(notification)

                _uiState.value = _uiState.value.copy(proposalSent = true)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al enviar la propuesta.")
            }
        }
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
