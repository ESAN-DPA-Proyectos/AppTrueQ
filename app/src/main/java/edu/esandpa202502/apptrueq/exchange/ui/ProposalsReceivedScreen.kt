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
import edu.esandpa202502.apptrueq.model.Proposal

/**
 * Pantalla para HU-07: Listar las propuestas de trueque recibidas por el usuario.
 */
// CORRECCIÓN: Se añade @OptIn para poder usar componentes de Material 3 como Scaffold y TopAppBar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsReceivedScreen(
    navController: NavController,
    viewModel: ExchangeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var proposalToAction by remember { mutableStateOf<Proposal?>(null) }
    var actionType by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Propuestas Recibidas") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
                }
                uiState.proposals.isEmpty() -> {
                    Text("No tienes propuestas pendientes.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.proposals, key = { it.id }) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onAcceptClick = {
                                    proposalToAction = proposal
                                    actionType = "aceptar"
                                },
                                onRejectClick = {
                                    proposalToAction = proposal
                                    actionType = "rechazar"
                                }
                            )
                        }
                    }
                }
            }
        }

        if (proposalToAction != null && actionType != null) {
            ConfirmationDialog(
                actionType = actionType!!,
                onConfirm = {
                    if (actionType == "aceptar") {
                        viewModel.acceptProposal(proposalToAction!!)
                    } else {
                        viewModel.rejectProposal(proposalToAction!!)
                    }
                    proposalToAction = null
                    actionType = null
                },
                onDismiss = {
                    proposalToAction = null
                    actionType = null
                }
            )
        }
    }
}

// CORRECCIÓN: Card también es un componente de Material 3.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalCard(
    proposal: Proposal,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Propuesta para: ${proposal.publicationTitle}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text("De: ${proposal.proposerName}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text(proposal.proposalText, style = MaterialTheme.typography.bodyMedium)
            
            if (proposal.offeredPublicationId != null) {
                Text("Ofrece a cambio: [ID: ${proposal.offeredPublicationId}]", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Aceptar")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onRejectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

// CORRECCIÓN: AlertDialog también es un componente experimental de Material 3.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    actionType: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Acción") },
        text = { Text("¿Estás seguro de que quieres $actionType esta propuesta? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
