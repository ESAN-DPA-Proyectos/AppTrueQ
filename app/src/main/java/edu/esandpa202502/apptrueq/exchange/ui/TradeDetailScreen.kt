package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Pantalla simple de detalle de trueque (HU-09).
 *
 * Se ha diseñado para coincidir con la llamada desde NavGraph:
 * TradeDetailScreen(
 *     navController = navController,
 *     tradeId = ...,
 *     onNavigateBack = { ... }
 * )
 *
 * Por ahora solo mostramos el ID del trueque. Más adelante,
 * si quieres, se puede cargar el objeto completo desde Firestore.
 */
@Composable
fun TradeDetailScreen(
    navController: NavHostController,
    tradeId: String?,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalle de trueque",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        if (tradeId == null) {
            Text(
                text = "No se ha proporcionado información del trueque.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "ID del trueque: $tradeId",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            onNavigateBack()
        }) {
            Text("Volver")
        }
    }
}
