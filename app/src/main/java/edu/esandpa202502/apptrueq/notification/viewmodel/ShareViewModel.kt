package edu.esandpa202502.apptrueq.notification.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.repository.notification.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel compartido a nivel de la actividad principal para gestionar estados globales
 * como el contador de notificaciones no leídas y mostrar notificaciones locales.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ShareViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationRepository = NotificationRepository()
    private var isFirstLoad = true

    // Flow que emite el estado de autenticación del usuario (logueado o no).
    private val authState: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(authStateListener) }
    }

    // StateFlow que contiene la lista de notificaciones. Reacciona a los cambios de usuario.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val allNotifications: StateFlow<List<NotificationItem>> = authState
        .flatMapLatest { user ->
            if (user != null) {
                notificationRepository.getNotifications(user.uid)
            } else {
                isFirstLoad = true // Reiniciar para el próximo login
                flowOf(emptyList()) // Si no hay usuario, emite una lista vacía.
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expone el número de notificaciones no leídas. Se resetea al cerrar sesión.
    val unreadNotificationCount: StateFlow<Int> = allNotifications
        .map { notifications -> notifications.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        listenForNewNotifications()
    }

    private fun listenForNewNotifications() {
        viewModelScope.launch {
            allNotifications.collect { notifications ->
                if (isFirstLoad) {
                    isFirstLoad = false
                    return@collect
                }

                val latestNotification = notifications.firstOrNull() ?: return@collect

                val notificationTime = latestNotification.createdAt?.toDate()?.time ?: 0
                val currentTime = System.currentTimeMillis()
                val fifteenSecondsInMillis = TimeUnit.SECONDS.toMillis(15)

                if (currentTime - notificationTime < fifteenSecondsInMillis && !latestNotification.isRead) {
                    notificationRepository.showLocalNotification(
                        context = getApplication(),
                        title = latestNotification.title,
                        message = latestNotification.message
                    )
                }
            }
        }
    }
}