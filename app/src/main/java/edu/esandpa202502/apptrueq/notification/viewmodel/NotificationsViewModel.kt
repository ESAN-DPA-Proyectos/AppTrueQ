package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

class NotificationsViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = NotificationsUiState(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            notificationRepository.getNotifications(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
                .collect { notifications ->
                    _uiState.value = NotificationsUiState(isLoading = false, notifications = notifications)
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al marcar la notificación como leída: ${e.message}")
            }
        }
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
