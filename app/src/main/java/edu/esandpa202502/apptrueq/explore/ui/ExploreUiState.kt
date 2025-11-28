package edu.esandpa202502.apptrueq.explore.ui

import edu.esandpa202502.apptrueq.model.Publication

data class ExploreUiState(
    val isLoading: Boolean = true,
    val publications: List<Publication> = emptyList(),
    val error: String? = null
)
