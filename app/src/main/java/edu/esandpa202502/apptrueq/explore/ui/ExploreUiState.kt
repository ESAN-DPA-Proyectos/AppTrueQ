package edu.esandpa202502.apptrueq.explore.ui

import edu.esandpa202502.apptrueq.model.Publication

/**
 * Define el estado de la UI para la pantalla de Exploración (HU-05).
 */
data class ExploreUiState(
    val isLoading: Boolean = false,
    val publications: List<Publication> = emptyList(),
    val error: String? = null
)
