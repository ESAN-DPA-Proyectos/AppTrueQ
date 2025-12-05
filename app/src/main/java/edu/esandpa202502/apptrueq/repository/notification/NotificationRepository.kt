package edu.esandpa202502.apptrueq.repository.notification

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    /**
     * Obtiene las notificaciones de un usuario en tiempo real.
     * SOLUCIÓN DEFINITIVA: Se reemplaza `toObjects()` por un mapeo manual (`mapNotNull`)
     * para asegurar que el ID del documento siempre se asigne correctamente a la propiedad `id` del modelo.
     * Esto previene el crash de `LazyColumn` por claves duplicadas.
     */
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

                // Mapeo manual para asegurar la correcta asignación del ID.
                val notifications = snapshot.documents.mapNotNull { document ->
                    try {
                        // Convierte el documento al objeto y le asigna el ID del documento.
                        document.toObject(NotificationItem::class.java)?.apply {
                            id = document.id
                        }
                    } catch (e: Exception) {
                        // Si un documento está corrupto y no se puede mapear, se ignora en lugar de crashear la app.
                        println("Error mapeando notificación: ${e.message}")
                        null
                    }
                }

                trySend(notifications).isSuccess
            }
        awaitClose { listener.remove() }
    }

    /**
     * Añade una nueva notificación a Firestore.
     * Se usa .add() para que Firestore genere el ID automáticamente.
     * La anotación @get:Exclude en el modelo se encarga de no guardar el campo 'id'.
     */
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