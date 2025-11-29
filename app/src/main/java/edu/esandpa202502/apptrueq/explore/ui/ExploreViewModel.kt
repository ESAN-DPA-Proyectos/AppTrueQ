package edu.esandpa202502.apptrueq.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 10 // Definimos un tamaño de página

class ExploreViewModel(private val repository: ExploreRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allPublications: List<Publication> = emptyList()
    private var filteredAndSortedPublications: List<Publication> = emptyList()
    private var currentPage = 1
    private var currentQuery = ""
    private var currentCategory = "Todas las categorías"

    init {
        fetchPublications()
    }

    private fun fetchPublications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isInitiallyLoading = true)
            try {
                allPublications = repository.getPublications()
                applyFiltersAndPagination(resetPagination = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isInitiallyLoading = false, error = e.message)
            }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        if (_uiState.value.selectedTabIndex == tabIndex) return
        _uiState.value = _uiState.value.copy(selectedTabIndex = tabIndex)
        applyFiltersAndPagination(resetPagination = true)
    }

    fun onSortOrderChange(sortOrder: DateSortOrder) {
        if (_uiState.value.dateSortOrder == sortOrder) return
        _uiState.value = _uiState.value.copy(dateSortOrder = sortOrder)
        applyFiltersAndPagination(resetPagination = true)
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        applyFiltersAndPagination(resetPagination = true)
    }

    fun onCategoryChanged(category: String) {
        currentCategory = category
        applyFiltersAndPagination(resetPagination = true)
    }

    fun loadNextPage() {
        if (_uiState.value.endReached || _uiState.value.isInitiallyLoading) return
        currentPage++
        applyFiltersAndPagination(resetPagination = false)
    }

    private fun applyFiltersAndPagination(resetPagination: Boolean) {
        if (resetPagination) {
            currentPage = 1
        }

        // 1. Filtrar y ordenar
        val filtered = allPublications.filter { publication ->
            val matchesTab = when (_uiState.value.selectedTabIndex) {
                1 -> publication.type == PublicationType.OFFER
                2 -> publication.type == PublicationType.NEED
                else -> true
            }
            val matchesCategory = currentCategory == "Todas las categorías" || publication.category.equals(currentCategory, ignoreCase = true)
            val matchesQuery = currentQuery.isBlank() || publication.title.contains(currentQuery, ignoreCase = true) || publication.description.contains(currentQuery, ignoreCase = true)
            matchesTab && matchesCategory && matchesQuery
        }

        val sorted = when (_uiState.value.dateSortOrder) {
            DateSortOrder.RECENT_FIRST -> filtered.sortedByDescending { it.date }
            DateSortOrder.OLDEST_FIRST -> filtered.sortedBy { it.date }
        }
        filteredAndSortedPublications = sorted

        // 2. Paginar
        val toIndex = (currentPage * PAGE_SIZE).coerceAtMost(filteredAndSortedPublications.size)
        val paginatedList = filteredAndSortedPublications.subList(0, toIndex)

        _uiState.value = _uiState.value.copy(
            isInitiallyLoading = false,
            publications = paginatedList,
            endReached = toIndex == filteredAndSortedPublications.size
        )
    }
}