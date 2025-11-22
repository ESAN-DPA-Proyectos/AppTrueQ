package edu.esandpa202502.apptrueq.need.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.repository.need.NeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NeedViewModel : ViewModel() {

    private val repository = NeedRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _needs = MutableStateFlow<List<Need>>(emptyList())
    val needs: StateFlow<List<Need>> = _needs

    init {
        // Al iniciar, cargamos solo las necesidades del usuario actual
        loadMyNeeds()
    }

    // Carga solo las necesidades del usuario logueado
    private fun loadMyNeeds() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                _needs.value = repository.getMyNeeds(userId)
            }
        }
    }

    fun addNeed(need: Need) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val needWithOwner = need.copy(ownerId = userId)
                repository.addNeed(needWithOwner)
                loadMyNeeds() // Recargamos solo la lista del usuario
            }
        }
    }
}