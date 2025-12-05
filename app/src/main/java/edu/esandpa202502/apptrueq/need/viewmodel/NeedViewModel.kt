package edu.esandpa202502.apptrueq.need.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.repository.need.NeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NeedState {
    object Idle : NeedState()
    object Loading : NeedState()
    data class Success(val need: Need) : NeedState()
    data class Error(val message: String) : NeedState()
}

class NeedViewModel(
    private val repository: NeedRepository = NeedRepository()
) : ViewModel() {

    private val _needState = MutableStateFlow<NeedState>(NeedState.Idle)
    val needState: StateFlow<NeedState> = _needState

    private val _myNeeds = MutableStateFlow<List<Need>>(emptyList())
    val myNeeds: StateFlow<List<Need>> = _myNeeds

    /**
     * Publicar una necesidad (HU-04).
     */
    fun publishNeed(
        needText: String,
        category: String?
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return setError("Usuario no autenticado.")

        // Validaciones HU-04
        when {
            needText.isBlank() ->
                return setError("Debe indicar lo que necesita.")

            needText.length < 5 ->
                return setError("La necesidad debe tener al menos 5 caracteres.")

            needText.length > 200 ->
                return setError("La necesidad no puede superar los 200 caracteres.")

            containsOffensiveWords(needText) ->
                return setError("El texto contiene palabras no permitidas.")
        }

        val need = Need(
            id = "",
            ownerId = userId,
            ownerName = user.displayName ?: "",
            needText = needText,
            category = category ?: "",
            status = "ACTIVE",
            createdAt = null
        )

        viewModelScope.launch {
            try {
                _needState.value = NeedState.Loading
                val created = repository.addNeed(need)
                _needState.value = NeedState.Success(created)
                loadMyNeeds()
            } catch (e: Exception) {
                setError(e.message ?: "Error al publicar la necesidad.")
            }
        }
    }

    /**
     * Cargar "Mis necesidades".
     */
    fun loadMyNeeds() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _myNeeds.value = repository.getNeedsByUser(userId)
        }
    }

    fun resetState() {
        _needState.value = NeedState.Idle
    }

    private fun setError(msg: String) {
        _needState.value = NeedState.Error(msg)
    }

    // Filtro simple de palabras prohibidas
    private fun containsOffensiveWords(text: String): Boolean {
        val blacklist = listOf("mierda", "tonto", "estúpido") // ejemplo
        return blacklist.any { text.contains(it, ignoreCase = true) }
    }
}
