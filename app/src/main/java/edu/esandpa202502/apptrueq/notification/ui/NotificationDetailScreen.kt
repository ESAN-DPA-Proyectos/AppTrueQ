package edu.esandpa202502.apptrueq.notification.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Pantalla que muestra el detalle de una propuesta recibida a través de una notificación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(notificationId: String?, navController: NavController) {

    // Por ahora, mostraremos datos de ejemplo. En el futuro, usaríamos el notificationId
    // para buscar la propuesta real en la base de datos (Firebase).
    val proposal = edu.esandpa202502.apptrueq.exchange.ui.Proposal(
        id = 1, 
        from = "Juan Rodriguez", 
        offer = "Ofrezco Laptop y Computadoras", 
        message = "Te ofrezco Laptop Apple i9", 
        date = "25/09/2025 -- 10:25 am", 
        status = "Aceptado", 
        userImage = android.R.drawable.ic_dialog_info
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva propuesta recibida") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = proposal.userImage),
                            contentDescription = "User Image",
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "De: ${proposal.from}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Oferta: \"${proposal.offer}\"")
                    Text(text = "Mensaje: \"${proposal.message}\"")
                    Text(text = "Fecha: ${proposal.date}")
                    Text(text = "Estado: \"${proposal.status}\"")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { /* TODO: Lógica Aceptar */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text("Aceptar", color = Color.White)
                        }
                        Button(onClick = { /* TODO: Lógica Rechazar */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text("Rechazar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailScreenPreview() {
    // Pasamos un ID de ejemplo y un NavController falso para la vista previa.
    NotificationDetailScreen("1", NavController(LocalContext.current))
}
