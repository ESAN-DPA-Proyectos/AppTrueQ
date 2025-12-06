package edu.esandpa202502.apptrueq.repository.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.MainActivity
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationsCollection = db.collection("notifications")

    suspend fun addNotification(notification: NotificationItem) {
        try {
            notificationsCollection.add(notification).await()
        } catch (e: Exception) {
            println("Error adding notification: ${e.message}")
        }
    }

    fun getNotifications(userId: String): Flow<List<NotificationItem>> = callbackFlow {
        val listenerRegistration = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(NotificationItem::class.java)
                    trySend(notifications)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Obtiene un Flow con el NÚMERO de notificaciones no leídas para el usuario actual.
     * Es eficiente porque solo cuenta los documentos en lugar de traerlos todos.
     */
    fun getUnreadNotificationCountStream(): Flow<Int> {
        val userId = auth.currentUser?.uid
        // Si no hay usuario, devuelve un Flow que emite 0.
        if (userId == null) {
            return flowOf(0)
        }

        return callbackFlow {
            val listener = notificationsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val count = snapshot?.size() ?: 0
                    trySend(count)
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun markAsRead(notificationId: String) {
        try {
            notificationsCollection.document(notificationId).update("isRead", true).await()
        } catch (e: Exception) {
            println("Error marking notification as read: ${e.message}")
            throw e
        }
    }

    fun showLocalNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val channelId = "default_channel_id"
        val channelName = "Notificaciones Generales"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para notificaciones generales de la app"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("Permiso POST_NOTIFICATIONS no concedido.")
                return
            }
            notify(notificationId, builder.build())
        }
    }
}
