package edu.esandpa202502.apptrueq.proposal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.proposal.viewmodel.ProposalsReceivedViewModel

/**
 * Pantalla para mostrar las propuestas recibidas por el usuario (HU-07).
 * Permite al usuario aceptar o rechazar las propuestas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsReceivedScreen(
    navController: NavController,
    // CORRECCIÓN: Se utiliza el ViewModel correcto que hemos estado desarrollando.
    viewModel: ProposalsReceivedViewModel = viewModel()
) {
    // Se observa el estado de la UI desde el ViewModel.
    val uiState = viewModel.uiState.collectAsState()

    // Estados para manejar el diálogo de confirmación.
    val proposalToAction = remember { mutableStateOf<Proposal?>(null) }
    val actionType = remember { mutableStateOf<String?>(null) }

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
                // Muestra un indicador de carga mientras se obtienen los datos.
                uiState.value.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Muestra un mensaje de error si algo falló.
                uiState.value.error != null -> {
                    Text(
                        "Error: ${uiState.value.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Muestra un mensaje si no hay propuestas pendientes.
                uiState.value.proposals.isEmpty() -> {
                    Text(
                        "No tienes propuestas pendientes.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Muestra la lista de propuestas si todo está correcto.
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.value.proposals, key = { it.id }) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onAcceptClick = {
                                    proposalToAction.value = proposal
                                    actionType.value = "aceptar"
                                },
                                onRejectClick = {
                                    proposalToAction.value = proposal
                                    actionType.value = "rechazar"
                                }
                            )
                        }
                    }
                }
            }
        }

        // Muestra el diálogo de confirmación si hay una acción pendiente.
        if (proposalToAction.value != null && actionType.value != null) {
            ConfirmationDialog(
                actionType = actionType.value!!,
                onConfirm = {
                    if (actionType.value == "aceptar") {
                        viewModel.acceptProposal(proposalToAction.value!!)
                    } else {
                        viewModel.rejectProposal(proposalToAction.value!!)
                    }
                    proposalToAction.value = null
                    actionType.value = null
                },
                onDismiss = {
                    proposalToAction.value = null
                    actionType.value = null
                }
            )
        }
    }
}

/**
 * Tarjeta que muestra el detalle de una propuesta recibida.
 */
@Composable
fun ProposalCard(
    proposal: Proposal,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Propuesta para: ${proposal.publicationTitle}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "De: ${proposal.proposerName}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
            Text(
                proposal.proposalText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))

            // MEJORA: Muestra de forma más clara qué se ofrece a cambio.
            val offerText = when {
                proposal.offeredItemTitle != null -> proposal.offeredItemTitle
                proposal.offeredPublicationId != null -> "una de sus publicaciones"
                else -> "Nada a cambio"
            }
            Text(
                "Ofrece a cambio: $offerText",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Aceptar")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onRejectClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

/**
 * Diálogo de confirmación genérico para acciones de Aceptar/Rechazar.
 */
@Composable
fun ConfirmationDialog(
    actionType: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Acción") },
        text = {
            Text(
                "¿Estás seguro de que quieres $actionType esta propuesta? " +
                        "Esta acción no se puede deshacer."
            )
        },
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
