package edu.esandpa202502.apptrueq.publicationQR.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.publicationQR.repository.PublicationQRRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de "Mis Publicaciones".
 */
data class MyPublicationsUiState(
    val isLoading: Boolean = false,
    val publications: List<Publication> = emptyList(),
    val error: String? = null
)

class PublicationQRViewModel : ViewModel() {

    private val repository = PublicationQRRepository()

    private val _uiState = MutableStateFlow(MyPublicationsUiState())
    val uiState: StateFlow<MyPublicationsUiState> = _uiState.asStateFlow()

    init {
        loadMyPublications()
    }

    private fun loadMyPublications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val publications = repository.getMyPublications()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        publications = publications
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar tus publicaciones: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun trackQrCodeGeneration(publicationId: String) {
        // TODO: Implementar lógica de tracking si es necesario en el futuro
        println("Generando QR para la publicación: $publicationId")
    }
}
