package edu.esandpa202502.apptrueq.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.explore.ui.ExploreUiState
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import edu.esandpa202502.apptrueq.repository.explore.ExploreRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: ExploreRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allPublications: List<Publication> = emptyList()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas las categorías")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _locationQuery = MutableStateFlow("")
    val locationQuery: StateFlow<String> = _locationQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow("Todos")
    val typeFilter: StateFlow<String> = _typeFilter.asStateFlow()


    init {
        fetchPublications()

        viewModelScope.launch {
            combine(_searchQuery, _selectedCategory, _locationQuery, _typeFilter) { query, category, location, type ->
                FilterCriteria(query, category, location, type)
            }.collect { criteria ->
                val filteredList = allPublications.filter { publication ->
                    val matchesCategory = criteria.category == "Todas las categorías" || publication.category.equals(criteria.category, ignoreCase = true)
                    val matchesQuery = publication.title.contains(criteria.query, ignoreCase = true) || publication.description.contains(criteria.query, ignoreCase = true)
                    val matchesLocation = criteria.location.isBlank() || (publication.location ?: "").contains(criteria.location, ignoreCase = true)
                    val matchesType = when (criteria.type) {
                        "Ofertas" -> publication.type == PublicationType.OFFER
                        "Necesidades" -> publication.type == PublicationType.NEED
                        else -> true
                    }
                    matchesCategory && matchesQuery && matchesLocation && matchesType
                }
                _uiState.update { it.copy(publications = filteredList) }
            }
        }
    }

    private fun fetchPublications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Obtener todas las publicaciones
                allPublications = repository.getPublications()

                // 2. Obtener los nombres de los autores en paralelo
                val authorIds = allPublications.map { it.userId }.distinct()
                val authorNamesMap = authorIds.map { id -> 
                    async { id to repository.getUserName(id) } 
                }.map { it.await() }.toMap()

                // 3. Actualizar el estado de la UI con las publicaciones y los nombres
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        publications = allPublications,
                        authorNames = authorNamesMap,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar publicaciones: ${e.message}"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChanged(category: String) {
        _selectedCategory.value = category
    }

    fun onLocationChanged(location: String) {
        _locationQuery.value = location
    }

    fun onTypeFilterChanged(type: String) {
        _typeFilter.value = type
    }

    private data class FilterCriteria(val query: String, val category: String, val location: String, val type: String)
}
