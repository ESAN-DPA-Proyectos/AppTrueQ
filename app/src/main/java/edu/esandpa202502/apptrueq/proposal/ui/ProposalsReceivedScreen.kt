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
fun ProposalsReceivedScreen() {
    val viewModel: ProposalsReceivedViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Propuestas Recibidas") })
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
            } else if (uiState.proposals.isEmpty()) {
                Text("No has recibido ninguna propuesta todavía.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.proposals) { proposal ->
                        ProposalReceivedCard(proposal = proposal, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ProposalReceivedCard(proposal: Proposal, viewModel: ProposalsReceivedViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var actionToConfirm: (() -> Unit)? by remember { mutableStateOf(null) }
    var dialogText by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Acción") },
            text = { Text(dialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        actionToConfirm?.invoke()
                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Propuesta de: ${proposal.proposerName}", style = MaterialTheme.typography.titleMedium)
            Text("Para tu publicación: '${proposal.publicationTitle}'", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text(proposal.proposalText, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))

            // Solo mostrar botones si la propuesta está pendiente
            if (proposal.status == "PENDIENTE") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.align(Alignment.End)) {
                    Button(onClick = {
                        dialogText = "¿Estás seguro de que quieres aceptar esta propuesta?"
                        actionToConfirm = { viewModel.acceptProposal(proposal) }
                        showDialog = true
                    }) {
                        Text("Aceptar")
                    }
                    OutlinedButton(onClick = {
                        dialogText = "¿Estás seguro de que quieres rechazar esta propuesta?"
                        actionToConfirm = { viewModel.rejectProposal(proposal) }
                        showDialog = true
                    }) {
                        Text("Rechazar")
                    }
                }
            } else {
                Text("Estado: ${proposal.status}", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}
