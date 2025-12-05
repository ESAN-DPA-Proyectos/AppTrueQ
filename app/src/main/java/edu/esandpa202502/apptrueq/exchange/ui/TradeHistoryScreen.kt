package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.model.Trade
// SOLUCIÓN: Se importa el ViewModel único y consolidado que creamos.
import edu.esandpa202502.apptrueq.proposal.viewmodel.ProposalsHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Pantalla para HU-09: Historial de Trueques.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeHistoryScreen(
    navController: NavController,
    // SOLUCIÓN: Se utiliza el ViewModel correcto y definitivo.
    viewModel: ProposalsHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedStatus by viewModel.statusFilter.collectAsState()
    
    // Se incluyen todos los posibles estados para el filtro.
    val statusOptions = listOf("Todos", "Aceptado", "Rechazado", "Cancelado", "Propuesto", "Completado")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Trueques") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text("Filtrar por estado:", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statusOptions.forEach { status ->
                    FilterChip(
                        selected = selectedStatus.equals(status, ignoreCase = true),
                        onClick = { viewModel.onStatusFilterChanged(status) },
                        label = { Text(status.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.trades.isEmpty() -> {
                        Text("No se encontraron trueques con esos filtros.", modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(uiState.trades, key = { it.id }) { trade ->
                                TradeHistoryCard(trade = trade)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TradeHistoryCard(trade: Trade) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
    sdf.timeZone = TimeZone.getTimeZone("America/Lima")
    val formattedDate = trade.createdAt?.let { sdf.format(it) } ?: "Fecha no disponible"

    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // MEJORA: Se usan los campos enriquecidos del modelo para una UI más clara.
            Text("Propuesta para: ${trade.publicationTitle}", fontWeight = FontWeight.Bold)
            Text("De: ${trade.offerentName} a ${trade.receiverName}")
            Divider()
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Estado: ", fontWeight = FontWeight.SemiBold)
                Text(trade.status.uppercase(), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.weight(1f))
                Text(formattedDate, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
