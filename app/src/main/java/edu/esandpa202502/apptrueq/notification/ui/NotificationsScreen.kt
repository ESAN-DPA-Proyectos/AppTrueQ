package edu.esandpa202502.apptrueq.notification.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.notification.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val uiState by notificationViewModel.uiState.collectAsState()
    var showOnlyUnread by remember { mutableStateOf(false) }

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
                            text = "Error: ${uiState.error}",
                            modifier = Modifier.align(Alignment.Center),
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
                            items(filteredNotifications, key = { it.id }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        notificationViewModel.markAsRead(notification.id)
                                        // Deep linking
                                        val route = when (notification.type) {
                                            "new_proposal", "proposal_accepted", "proposal_rejected" -> Routes.ProposalsReceived.route
                                            else -> null // O una ruta por defecto
                                        }
                                        route?.let { navController.navigate(it) }
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

@Composable
fun NotificationCard(notification: NotificationItem, onClick: () -> Unit) {
    val cardColor = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
    val icon = if (notification.isRead) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, fontWeight = fontWeight, style = MaterialTheme.typography.titleMedium)
                Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
