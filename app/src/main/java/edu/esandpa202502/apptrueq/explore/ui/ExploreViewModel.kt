package edu.esandpa202502.apptrueq.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Publication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: ExploreRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allPublications: List<Publication> = emptyList()

    init {
        fetchPublications()
    }

    private fun fetchPublications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                allPublications = repository.getPublications()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    publications = allPublications
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        filterPublications(query = query, category = _uiState.value.publications.firstOrNull()?.category ?: "Todas las categorías")
    }

    fun onCategoryChanged(category: String) {
        filterPublications(query = "", category = category)
    }

    private fun filterPublications(query: String, category: String) {
        val filteredList = allPublications.filter { publication ->
            val matchesCategory = category == "Todas las categorías" || publication.category.equals(category, ignoreCase = true)
            val matchesQuery = publication.title.contains(query, ignoreCase = true) || publication.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
        _uiState.value = _uiState.value.copy(publications = filteredList)
    }
}