package edu.esandpa202502.apptrueq.proposal.ui

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
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.proposal.viewmodel.ProposalsReceivedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsReceivedScreen(
    vm: ProposalsReceivedViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    val viewMode by vm.viewMode.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // --- Lógica de filtrado en la UI ---
    val displayedProposals = remember(state.proposals, searchQuery, viewMode) {
        val allProposals = state.proposals
        
        val viewFiltered = if (viewMode == "Pendientes") {
            allProposals.filter { it.status.equals("PENDIENTE", ignoreCase = true) }
        } else {
            allProposals
        }

        if (searchQuery.isBlank()) {
            viewFiltered
        } else {
            viewFiltered.filter { proposal ->
                proposal.publicationTitle.contains(searchQuery, ignoreCase = true) ||
                proposal.proposerName.contains(searchQuery, ignoreCase = true) ||
                proposal.proposalText.contains(searchQuery, ignoreCase = true) ||
                (proposal.offeredItemTitle ?: "").contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Propuestas recibidas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Pendientes", "Todos").forEach { mode ->
                FilterChip(
                    selected = viewMode == mode,
                    onClick = { vm.onViewModeChanged(mode) },
                    label = { Text(mode) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar en la lista actual…") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (displayedProposals.isEmpty()) {
            val message = if (viewMode == "Pendientes" && searchQuery.isBlank()) {
                "No tienes propuestas pendientes."
            } else if (searchQuery.isBlank()) {
                 "No has recibido ninguna propuesta."
            } else {
                "No se encontraron propuestas con los filtros actuales."
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 Text(message, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedProposals, key = { it.id }) { proposal ->
                    ProposalCard(
                        proposal = proposal,
                        onAccept = { vm.acceptProposal(proposal) },
                        onReject = { vm.rejectProposal(proposal) }
                    )
                }
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: Proposal,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val isPending = proposal.status.equals("PENDIENTE", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(proposal.publicationTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Proponente: ${proposal.proposerName}", style = MaterialTheme.typography.bodyMedium)

            if (proposal.proposalText.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Mensaje: ${proposal.proposalText}", style = MaterialTheme.typography.bodySmall)
            }

            if (!proposal.offeredPublicationId.isNullOrBlank() || !proposal.offeredItemTitle.isNullOrBlank()) {
                 Spacer(Modifier.height(8.dp))
                 Text("Ofrece a cambio:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                 proposal.offeredItemTitle?.let { Text("- $it", style = MaterialTheme.typography.bodySmall) }
                 if (!proposal.offeredPublicationId.isNullOrBlank()) {
                     Text("- Publicación (ID: ${proposal.offeredPublicationId})", style = MaterialTheme.typography.bodySmall)
                 }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = proposal.status)
                Row(horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onReject, enabled = isPending) {
                        Text("RECHAZAR")
                    }
                    Spacer(Modifier.width(4.dp))
                    Button(onClick = onAccept, enabled = isPending) {
                        Text("ACEPTAR")
                    }
                }
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
