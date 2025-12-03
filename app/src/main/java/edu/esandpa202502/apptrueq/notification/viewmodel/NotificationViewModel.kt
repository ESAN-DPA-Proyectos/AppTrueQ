package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun listenForNotifications(userId: String) {
        viewModelScope.launch {
            repository.getNotifications(userId)
                .onStart { _uiState.value = NotificationsUiState(isLoading = true) }
                .catch { e -> _uiState.value = NotificationsUiState(isLoading = false, error = e.message) }
                .collect { notifications ->
                    // La ordenación ahora se hace en el cliente
                    val sortedNotifications = notifications.sortedByDescending { it.createdAt }
                    _uiState.value = NotificationsUiState(isLoading = false, notifications = sortedNotifications)
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }
}