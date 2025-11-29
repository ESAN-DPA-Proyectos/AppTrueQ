package edu.esandpa202502.apptrueq.explore.ui

import edu.esandpa202502.apptrueq.model.Publication

enum class DateSortOrder {
    RECENT_FIRST,
    OLDEST_FIRST
}

data class ExploreUiState(
    val isInitiallyLoading: Boolean = true,
    val publications: List<Publication> = emptyList(),
    val error: String? = null,
    val selectedTabIndex: Int = 0, // 0: Todos, 1: Ofertas, 2: Necesidades
    val dateSortOrder: DateSortOrder = DateSortOrder.RECENT_FIRST,
    val endReached: Boolean = false // Indica si se han cargado todas las publicaciones
)
