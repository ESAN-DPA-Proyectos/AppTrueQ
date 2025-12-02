package edu.esandpa202502.apptrueq.notification.repository

import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getNotifications(userId: String): Flow<List<NotificationItem>> = callbackFlow {
        val listener = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects(NotificationItem::class.java) ?: emptyList()
                trySend(notifications).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun addNotification(notification: NotificationItem) {
        try {
            db.collection("notifications").add(notification).await()
        } catch (e: Exception) {
            // Manejar o registrar la excepción
            e.printStackTrace()
        }
    }

    suspend fun markAsRead(notificationId: String) {
        if (notificationId.isEmpty()) return
        db.collection("notifications").document(notificationId).update("isRead", true).await()
    }
}