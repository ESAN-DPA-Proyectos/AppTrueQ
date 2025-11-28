package edu.esandpa202502.apptrueq.notification.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.notification.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uiState by notificationViewModel.uiState.collectAsState()
    var showOnlyUnread by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let {
            notificationViewModel.listenForNotifications(it)
        }
    }

    val filteredNotifications = remember(uiState.notifications, showOnlyUnread) {
        if (showOnlyUnread) {
            uiState.notifications.filter { !it.isRead }
        } else {
            uiState.notifications
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = !showOnlyUnread, onClick = { showOnlyUnread = false }, label = { Text("Todas") })
                FilterChip(selected = showOnlyUnread, onClick = { showOnlyUnread = true }, label = { Text("No Leídas") })
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text(
                            text = "No hay notificaciones en la base de datos.",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    filteredNotifications.isEmpty() -> {
                        val message = if (showOnlyUnread) "No tienes notificaciones sin leer." else "No tienes notificaciones."
                        Text(
                            text = message,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(filteredNotifications, key = { it.id!! }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        notification.id?.let { id ->
                                            notificationViewModel.markAsRead(id)
                                            navController.navigate(
                                                Routes.NotificationDetail.createRoute(
                                                    notificationId = id,
                                                    referenceId = notification.referenceId
                                                )
                                            )
                                        }
                                    },
                                    onDeleteClick = { // <-- Acción de borrado
                                        notification.id?.let { notificationViewModel.deleteNotification(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit // <-- Nuevo parámetro
) {
    val cardColor = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
    val icon = if (notification.isRead) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, fontWeight = fontWeight, style = MaterialTheme.typography.titleMedium)
                Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
            }
            // --- Botón de Borrar ---
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar Notificación")
            }
        }
    }
}
