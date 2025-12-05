package edu.esandpa202502.apptrueq.proposal.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.repository.proposal.ProposalRepository
import edu.esandpa202502.apptrueq.repository.publication.PublicationRepository
import edu.esandpa202502.apptrueq.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PublicationDetailUiState(
    val isLoading: Boolean = true,
    val publication: Publication? = null,
    val userPublications: List<Publication> = emptyList(),
    val error: String? = null,
    val proposalError: String? = null,
    val proposalSent: Boolean = false
)

class PublicationDetailViewModel(private val publicationId: String) : ViewModel() {

    private val publicationRepository = PublicationRepository()
    private val proposalRepository = ProposalRepository()
    private val userRepository = UserRepository()
    private val storage = FirebaseStorage.getInstance()
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

        if (proposalText.length !in 10..250) {
            _uiState.update { it.copy(proposalError = "El mensaje debe tener entre 10 y 250 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, proposalError = null) }
            try {
                val hasExisting = proposalRepository.hasExistingProposal(currentUser.uid, publication.id)
                if (hasExisting) {
                    _uiState.update { it.copy(isLoading = false, proposalError = "Ya has enviado una propuesta para esta publicación.") }
                    return@launch
                }

                val proposer = userRepository.getUserById(currentUser.uid)
                val proposerName = proposer?.name ?: currentUser.displayName ?: "Usuario Anónimo"

                val proposal = Proposal(
                    publicationId = publication.id,
                    publicationOwnerId = publication.userId,
                    publicationTitle = publication.title,
                    proposerId = currentUser.uid,
                    proposerName = proposerName,
                    proposalText = proposalText,
                    offeredPublicationId = offeredPublicationId
                )

                // SOLUCIÓN: Se llama al método del repositorio que se encarga de todo.
                proposalRepository.createProposalAndNotify(proposal)

                _uiState.update { it.copy(isLoading = false, proposalSent = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar la propuesta: ${e.message}") }
            }
        }
    }

    fun submitProposalWithNewOffer(
        proposalText: String,
        offerTitle: String,
        offerDescription: String,
        offerImageUri: Uri?
    ) {
        val publication = _uiState.value.publication ?: return
        val currentUser = auth.currentUser ?: return

        if (offerTitle.isBlank()) {
            _uiState.update { it.copy(proposalError = "El título de tu oferta no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, proposalError = null) }
            try {
                val hasExisting = proposalRepository.hasExistingProposal(currentUser.uid, publication.id)
                if (hasExisting) {
                    _uiState.update { it.copy(isLoading = false, proposalError = "Ya has enviado una propuesta para esta publicación.") }
                    return@launch
                }

                var imageUrl: String? = null
                if (offerImageUri != null) {
                    val imageRef = storage.reference.child("proposal_offers/${System.currentTimeMillis()}_${offerImageUri.lastPathSegment}")
                    imageUrl = imageRef.putFile(offerImageUri).await().storage.downloadUrl.await().toString()
                }

                val proposer = userRepository.getUserById(currentUser.uid)
                val proposerName = proposer?.name ?: currentUser.displayName ?: "Usuario Anónimo"

                val proposal = Proposal(
                    publicationId = publication.id,
                    publicationOwnerId = publication.userId,
                    publicationTitle = publication.title,
                    proposerId = currentUser.uid,
                    proposerName = proposerName,
                    proposalText = proposalText,
                    offeredItemTitle = offerTitle,
                    offeredItemDescription = offerDescription,
                    offeredItemImageUrl = imageUrl
                )

                // SOLUCIÓN: Se llama al método del repositorio que se encarga de todo.
                proposalRepository.createProposalAndNotify(proposal)

                _uiState.update { it.copy(isLoading = false, proposalSent = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al enviar la propuesta: ${e.message}") }
            }
        }
    }

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