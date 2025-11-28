package edu.esandpa202502.apptrueq.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val allPublications: List<Publication> = emptyList(),
    val filteredPublications: List<Publication> = emptyList(),
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ExploreViewModel(private val repository: ExploreRepository = ExploreRepository()) :
    ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadPublications()
    }

    private fun loadPublications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val publications = repository.getPublications()
                _uiState.update {
                    it.copy(
                        allPublications = publications,
                        isLoading = false
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar publicaciones.") }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        applyFilters()
    }

    fun onTabChanged(newIndex: Int) {
        _uiState.update { it.copy(selectedTabIndex = newIndex) }
        applyFilters()
    }

    private fun applyFilters() {
        _uiState.update { currentState ->
            val filteredList = currentState.allPublications.filter {
                val typeMatches = when (currentState.selectedTabIndex) {
                    1 -> it.type == PublicationType.OFFER
                    2 -> it.type == PublicationType.NEED
                    else -> true // "Todo" tab
                }

                val queryMatches = if (currentState.searchQuery.isBlank()) {
                    true
                } else {
                    it.title.contains(currentState.searchQuery, ignoreCase = true) ||
                            it.description.contains(currentState.searchQuery, ignoreCase = true)
                }

                typeMatches && queryMatches
            }
            currentState.copy(filteredPublications = filteredList)
        }
    }
}
