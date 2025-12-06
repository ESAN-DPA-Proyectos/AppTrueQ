package edu.esandpa202502.apptrueq.repository.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationsCollection = db.collection("notifications")

    /**
     * Crea un Flow que emite el usuario actual cada vez que el estado de autenticación cambia.
     * Emite el usuario (FirebaseUser) si está logueado, o null si ha cerrado sesión.
     */
    private val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    /**
     * Obtiene el recuento de notificaciones no leídas en tiempo real.
     * Usa `flatMapLatest` para reaccionar a los cambios en `authStateFlow`.
     * Cuando el usuario inicia sesión, crea un nuevo listener de Firestore.
     * Cuando el usuario cierra sesión, cancela el listener y emite 0.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUnreadNotificationCountStream(): Flow<Int> = authStateFlow.flatMapLatest { user ->
        if (user == null) {
            // Si no hay usuario, simplemente emite 0.
            flowOf(0)
        } else {
            // Si hay un usuario, crea el listener para sus notificaciones no leídas.
            callbackFlow {
                val listener = notificationsCollection
                    .whereEqualTo("userId", user.uid)
                    .whereEqualTo("isRead", false)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            channel.close(error)
                            return@addSnapshotListener
                        }
                        val count = snapshot?.size() ?: 0
                        trySend(count)
                    }
                // Esta corrutina se cancelará automáticamente por flatMapLatest cuando el usuario cambie.
                awaitClose { listener.remove() }
            }
        }
    }

    fun getNotifications(userId: String): Flow<List<NotificationItem>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val notifications = snapshot.documents.mapNotNull { document ->
                    try {
                        document.toObject(NotificationItem::class.java)?.apply {
                            id = document.id
                        }
                    } catch (e: Exception) {
                        println("Error mapeando notificación: ${e.message}")
                        null
                    }
                }

                trySend(notifications).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun addNotification(notification: NotificationItem) {
        try {
            notificationsCollection.add(notification).await()
        } catch (e: Exception) {
            println("Error adding notification: ${e.message}")
            throw e
        }
    }

    suspend fun markAsRead(notificationId: String) {
        if (notificationId.isEmpty()) return
        notificationsCollection.document(notificationId).update("isRead", true).await()
    }
}
