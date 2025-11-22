package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Publication // Asegúrate que esta línea aparezca solo una vez
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.launch

// Estado de la UI para la pantalla de exploración.
data class ExploreUiState(
    val allPublications: List<Publication> = emptyList(),
    val filteredPublications: List<Publication> = emptyList(),
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

/**
 * ViewModel que contiene toda la lógica de negocio para la pantalla de exploración.
 */
class ExploreViewModel(private val repository: ExploreRepository = ExploreRepository()) :
    ViewModel() {

    private val _uiState = mutableStateOf(ExploreUiState())
    val uiState: State<ExploreUiState> = _uiState

    init {
        // Al iniciar el ViewModel, carga las publicaciones iniciales.
        loadPublications()
    }

    private fun loadPublications() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val publications = repository.getPublications()
                _uiState.value = _uiState.value.copy(allPublications = publications, isLoading = false)
                // Aplica los filtros iniciales
                applyFilters()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al cargar publicaciones.")
            }
        }
    }

    // Acciones que la UI puede llamar
    fun onSearchQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
        applyFilters()
    }

    fun onTabChanged(newIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = newIndex)
        applyFilters()
    }

    // Lógica de filtrado centralizada
    private fun applyFilters() {
        val currentState = _uiState.value
        val filteredList = currentState.allPublications.filter {
            // Filtro por tipo (Oferta/Necesidad)
            val typeMatches = if (currentState.selectedTabIndex == 0) {
                it.type == PublicationType.OFFER
            } else {
                it.type == PublicationType.NEED
            }

            // Filtro por texto de búsqueda
            val queryMatches = if (currentState.searchQuery.isBlank()) {
                true
            } else {
                it.title.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.description.contains(currentState.searchQuery, ignoreCase = true)
            }

            typeMatches && queryMatches
        }
        _uiState.value = _uiState.value.copy(filteredPublications = filteredList)
    }
}
