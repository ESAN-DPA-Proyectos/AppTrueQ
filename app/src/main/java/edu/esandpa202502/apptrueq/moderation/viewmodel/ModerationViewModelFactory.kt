package edu.esandpa202502.apptrueq.moderation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ModerationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModerationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModerationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
