package edu.esandpa202502.apptrueq.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.esandpa202502.apptrueq.repository.explore.ExploreRepository

// QA: CORRECCIÓN DEFINITIVA - Se corrige el import para que apunte a la interfaz del repositorio en el paquete consolidado.

class ExploreViewModelFactory(private val repository: ExploreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExploreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
