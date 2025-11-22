package edu.esandpa202502.apptrueq.notification.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Escucha en tiempo real las notificaciones para un usuario específico, ordenadas por fecha.
     */
    fun getNotifications(userId: String): Flow<List<NotificationItem>> = callbackFlow {
        val listener = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects(NotificationItem::class.java) ?: emptyList()
                trySend(notifications).isSuccess
            }
        // Se asegura de remover el listener cuando el colector del Flow se cancela
        awaitClose { listener.remove() }
    }
}