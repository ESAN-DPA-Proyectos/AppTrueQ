package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class NotificationUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

class NotificationViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            listenForNotifications(userId)
        }
    }

    fun listenForNotifications(userId: String) {
        viewModelScope.launch {
            notificationRepository.getNotifications(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
                .collect { notifications ->
                    _uiState.value = NotificationUiState(isLoading = false, notifications = notifications)
                }
        }
    }

    fun markAsRead(notificationId: String) {
        if (notificationId.isBlank()) return
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
            } catch (e: Exception) {
                // El error se puede registrar o mostrar de forma más sutil
                println("Error al marcar como leída: ${e.message}")
            }
        }
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
