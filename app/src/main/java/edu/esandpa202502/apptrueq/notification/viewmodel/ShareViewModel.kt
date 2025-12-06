package edu.esandpa202502.apptrueq.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel compartido a nivel de la actividad principal para gestionar estados globales
 * como el contador de notificaciones no leídas.
 */
class SharedViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()

    // Expone el número de notificaciones no leídas como un StateFlow.
    // Se inicia con 0 y se actualiza automáticamente gracias al flow del repositorio.
    val unreadNotificationCount: StateFlow<Int> = notificationRepository.getUnreadNotificationCountStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}