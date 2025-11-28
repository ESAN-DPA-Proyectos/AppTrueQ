package edu.esandpa202502.apptrueq.notification.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun createNotification(notification: NotificationItem) {
        db.collection("notifications").add(notification).await()
    }

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

    suspend fun markAsRead(notificationId: String) {
        try {
            db.collection("notifications").document(notificationId)
                .update("read", true)
                .await()
        } catch (e: Exception) {
            // Silently fail
        }
    }

    // --- NUEVA FUNCIÓN DE BORRADO ---
    suspend fun deleteNotification(notificationId: String) {
        if (notificationId.isNotBlank()) {
            db.collection("notifications").document(notificationId).delete().await()
        }
    }
}