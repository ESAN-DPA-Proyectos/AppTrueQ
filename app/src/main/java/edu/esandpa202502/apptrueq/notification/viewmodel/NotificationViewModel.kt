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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Notificaciones.
 * Contiene la lista de notificaciones, el estado de carga y cualquier posible error.
 */
data class NotificationUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel ÚNICO y CONSOLIDADO para la pantalla de notificaciones (HU-08).
 * Gestiona la obtención de notificaciones y la interacción del usuario (marcar como leídas).
 */
class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        // En cuanto el ViewModel se crea, empieza a escuchar las notificaciones del usuario actual.
        listenForNotifications()
    }

    /**
     * Se suscribe al repositorio para obtener las notificaciones del usuario en tiempo real.
     * Ordena las notificaciones para mostrar primero las más recientes.
     */
    private fun listenForNotifications() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado.") }
            return
        }

        viewModelScope.launch {
            repository.getNotifications(userId)
                .catch { e ->
                    // En caso de error en el Flow, se actualiza el estado de la UI.
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { notifications ->
                    // Cada vez que Firestore notifica un cambio, se actualiza la lista en la UI.
                    // Se ordenan por fecha de creación, mostrando las más nuevas primero.
                    val sortedNotifications = notifications.sortedByDescending { it.createdAt }
                    _uiState.update { it.copy(isLoading = false, notifications = sortedNotifications) }
                }
        }
    }

    /**
     * Llama al repositorio para marcar una notificación específica como leída.
     * La actualización en la UI ocurrirá automáticamente gracias al listener de Firestore.
     * @param notification La notificación que se va a marcar como leída.
     */
    fun markNotificationAsRead(notification: NotificationItem) {
        // Solo se marca como leída si no lo está ya, para evitar escrituras innecesarias en la BD.
        if (!notification.isRead) {
            viewModelScope.launch {
                try {
                    repository.markAsRead(notification.id)
                } catch (e: Exception) {
                    // Opcional: manejar el error, por ejemplo, mostrando un Toast.
                    _uiState.update { it.copy(error = "Error al marcar la notificación como leída.") }
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error del estado de la UI.
     * La UI puede llamar a esta función después de mostrar un error para resetear el estado.
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}