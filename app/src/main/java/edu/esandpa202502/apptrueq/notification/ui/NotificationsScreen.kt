package edu.esandpa202502.apptrueq.notification.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

data class Notificacion(
    val id: String,
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val tiempo: String,
    val icono: ImageVector,
    var isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    var notifications by remember {
        mutableStateOf(listOf(
            Notificacion("1", "propuesta_recibida", "Nueva propuesta recibida", "Carlos Enrique esta interesado...", "Hace 4 horas", Icons.Default.Email, isRead = false),
            Notificacion("2", "propuesta_aceptada", "Propuesta aceptada!", "Ana Lopez acepto tu propuesta...", "Hace 8 horas", Icons.Default.CheckCircle, isRead = true),
            Notificacion("3", "recordatorio", "Recordatorio de encuentro", "Tu intercambio con Roberto Carlos...", "Hace 10 horas", Icons.Default.Schedule, isRead = false),
            Notificacion("4", "propuesta_rechazada", "Propuesta rechazada", "Tu propuesta para Laptop Dell...", "Hace 17 horas", Icons.Default.Cancel, isRead = true)
        ))
    }

    var showOnlyUnread by remember { mutableStateOf(false) }

    val filteredNotifications by remember(showOnlyUnread, notifications) {
        derivedStateOf {
            if (showOnlyUnread) {
                notifications.filter { !it.isRead }
            } else {
                notifications
            }
        }
    }

    val unreadCount = notifications.count { !it.isRead }
    val context = LocalContext.current

    // --- LÓGICA DE PERMISOS DE NOTIFICACIÓN ---
    // 1. Preparamos el lanzador que solicitará el permiso.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Si el usuario ACEPTA el permiso, enviamos la notificación inmediatamente.
                showSystemNotification(context, "¡Permiso Concedido!", "Ahora recibirás notificaciones.")
            } else {
                // Si el usuario RECHAZA, no hacemos nada. En una app real, podrías mostrar un mensaje.
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$unreadCount sin leer", modifier = Modifier.weight(1f))
                Button(onClick = {
                    // --- LÓGICA DEL BOTÓN DEMO PUSH MEJORADA ---
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // El permiso solo es necesario en Android 13+
                        when {
                            // 2. Comprobamos si el permiso ya está concedido.
                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                                // Si ya lo tenemos, simplemente enviamos la notificación.
                                showSystemNotification(context, "¡Nueva Propuesta!", "Tienes una nueva oferta para tu item.")
                            }
                            // 3. Si no lo tenemos, lanzamos el diálogo de solicitud.
                            else -> {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    } else {
                        // En versiones antiguas de Android, no se necesita permiso.
                        showSystemNotification(context, "¡Nueva Propuesta!", "Tienes una nueva oferta para tu item.")
                    }
                }) {
                    Text("Demo Push")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showOnlyUnread = false }, enabled = showOnlyUnread) {
                    Text("Todas")
                }
                Button(onClick = { showOnlyUnread = true }, enabled = !showOnlyUnread) {
                    Text("Solo no leídas")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredNotifications) { notification ->
                    NotificationCard(
                        notificacion = notification,
                        onClick = {
                            notifications = notifications.map {
                                if (it.id == notification.id) it.copy(isRead = true) else it
                            }
                            navController.navigate("notification_detail/${notification.id}")
                        }
                    )
                }
            }
        }
    }
}

fun showSystemNotification(context: Context, title: String, message: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "default_channel_id"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}

@Composable
fun NotificationCard(notificacion: Notificacion, onClick: () -> Unit) {
    val cardColor = if (notificacion.isRead) Color.LightGray else MaterialTheme.colorScheme.surface
    val fontWeight = if (notificacion.isRead) FontWeight.Normal else FontWeight.Bold

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(notificacion.icono, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notificacion.titulo, fontWeight = fontWeight, style = MaterialTheme.typography.titleMedium)
                Text(text = notificacion.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = notificacion.tiempo, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(navController = NavController(LocalContext.current))
}
