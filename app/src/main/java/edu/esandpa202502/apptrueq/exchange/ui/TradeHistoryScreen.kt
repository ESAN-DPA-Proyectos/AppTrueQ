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
import edu.esandpa202502.apptrueq.exchange.viewmodel.TradeHistoryViewModel
import edu.esandpa202502.apptrueq.model.Proposal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para HU-09: Historial de Trueques.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeHistoryScreen(
    navController: NavController,
    viewModel: TradeHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedStatus by viewModel.statusFilter.collectAsState()

    val statusOptions = listOf("Todos", "Aceptado", "Rechazado")

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
                    uiState.proposals.isEmpty() -> {
                        Text("No se encontraron propuestas con esos filtros.", modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.proposals, key = { it.id }) { proposal ->
                                ProposalHistoryCard(proposal = proposal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProposalHistoryCard(proposal: Proposal) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
    sdf.timeZone = TimeZone.getTimeZone("America/Lima")
    val formattedDate = proposal.createdAt?.toDate()?.let { sdf.format(it) } ?: "Fecha no disponible"

    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(proposal.publicationTitle, fontWeight = FontWeight.Bold)
            Text("Proponente: ${proposal.proposerName}", style = MaterialTheme.typography.bodyMedium)

            if (proposal.proposalText.isNotBlank()) {
                Text("Mensaje: ${proposal.proposalText}", style = MaterialTheme.typography.bodySmall)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = proposal.status)
                Spacer(modifier = Modifier.weight(1f))
                Text(formattedDate, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val backgroundColor = when (status.uppercase()) {
        "ACEPTADA" -> MaterialTheme.colorScheme.primaryContainer
        "RECHAZADA" -> MaterialTheme.colorScheme.errorContainer
        "PENDIENTE" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (status.uppercase()) {
        "ACEPTADA" -> MaterialTheme.colorScheme.onPrimaryContainer
        "RECHAZADA" -> MaterialTheme.colorScheme.onErrorContainer
        "PENDIENTE" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = status.uppercase(),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
