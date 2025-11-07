package edu.esandpa202502.apptrueq.notification.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Notificacion(
    val id: String,
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val tiempo: String,
    val icono: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen() {
    val listaDeNotificaciones = listOf(
        Notificacion("1", "propuesta_recibida", "Nueva propuesta recibida", "Carlos Enrique esta interesado en tu publicacion de Clases de ...", "Hace 4 horas", Icons.Default.Email),
        Notificacion("2", "propuesta_aceptada", "Propuesta aceptada!", "Ana Lopez acepto tu propuesta para diseño web", "Hace 8 horas", Icons.Default.CheckCircle),
        Notificacion("3", "recordatorio", "Recordatorio de encuentro", "Tu intercambio con Roberto Carlos es mañana a las 3:00 pm", "Hace 10 horas", Icons.Default.Schedule),
        Notificacion("4", "propuesta_rechazada", "Propuesta rechazada", "Tu propuesta para Laptop Dell no fue aceptada", "Hace 17 horas", Icons.Default.Cancel)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Navegación hacia atrás */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "2 sin leer", modifier = Modifier.weight(1f))
                Button(onClick = { /* TODO: Simular notificación PUSH */ }) {
                    Text("Demo Push")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { /* TODO: Lógica para mostrar todas */ }) {
                    Text("Todas")
                }
                OutlinedButton(onClick = { /* TODO: Lógica para mostrar solo no leídas */ }) {
                    Text("Solo no leídas")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listaDeNotificaciones) { notificacion ->
                    NotificationCard(notificacion = notificacion)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notificacion: Notificacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navegar a la pantalla de detalle de la notificación (HU:08-02) */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(notificacion.icono, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notificacion.titulo, style = MaterialTheme.typography.titleMedium)
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
    NotificationsScreen()
}
