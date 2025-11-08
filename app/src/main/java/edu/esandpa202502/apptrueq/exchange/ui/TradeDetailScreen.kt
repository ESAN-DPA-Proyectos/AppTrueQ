package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Esta pantalla muestra los detalles de un trueque específico del historial.
 * Recibe el ID del trueque para saber qué información mostrar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailScreen(tradeId: String?, onNavigateBack: () -> Unit) {

    // --- LÓGICA PARA ENCONTRAR EL TRUEQUE ---
    // Por ahora, buscamos en la misma lista de datos de ejemplo.
    // Cuando conectemos a Firebase, aquí se haría una consulta a la base de datos con el tradeId.
    val allTrades = listOf(
        HistorialTrueque("1", "Necesito libro de Phyton 3.07", "Juan Rodriguez", "Aceptado", "25/09/2025 - 10:25 pm", android.R.drawable.ic_dialog_info),
        HistorialTrueque("2", "Ofrezco Laptop Apple i9", "Abigail Gutierrez", "Rechazado", "23/09/2025 - 7:07 am", android.R.drawable.ic_dialog_info),
        HistorialTrueque("3", "Necesito bicicleta para niño", "Raul Romero", "Pendiente", "21/09/2025 - 10:07 am", android.R.drawable.ic_dialog_info)
    )
    // `find` es una función de Kotlin que busca el primer elemento que cumple una condición.
    val tradeToShow = allTrades.find { it.id == tradeId }

    // --- ESTRUCTURA DE LA PANTALLA ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Trueque") },
                navigationIcon = {
                    // Usamos la función que nos pasan para volver atrás.
                    IconButton(onClick = onNavigateBack) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tradeToShow != null) {
                // Si se encontró el trueque, mostramos sus detalles.
                Image(
                    painter = painterResource(id = tradeToShow.imagenUsuario),
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
                
                Text(tradeToShow.nombreUsuario, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                
                Text("\"${tradeToShow.titulo}\"", fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Estado: ${tradeToShow.estado}", style = MaterialTheme.typography.bodyLarge)
                Text("Última actualización: ${tradeToShow.ultimaActualizacion}", style = MaterialTheme.typography.bodyMedium)

            } else {
                // Si no se encontró (por si acaso), mostramos un mensaje de error.
                Text("No se encontraron los detalles del trueque.")
            }
        }
    }
}

// --- VISTA PREVIA ---
@Preview(showBackground = true)
@Composable
fun TradeDetailScreenPreview() {
    // Hacemos una vista previa pasando un ID de ejemplo y una función vacía.
    TradeDetailScreen(tradeId = "1", onNavigateBack = {})
}
