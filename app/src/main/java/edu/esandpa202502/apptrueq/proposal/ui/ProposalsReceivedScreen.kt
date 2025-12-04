//package edu.esandpa202502.apptrueq.exchange.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.navigation.NavController
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import edu.esandpa202502.apptrueq.exchange.viewmodel.ProposalExchangeViewModel
//import edu.esandpa202502.apptrueq.model.Proposal
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProposalsReceivedScreen(
//    navController: NavController,
//    viewModel: ProposalExchangeViewModel = viewModel()
//) {
//    val uiState = viewModel.uiState.collectAsState()
//
//    val proposalToAction = remember { mutableStateOf<Proposal?>(null) }
//    val actionType = remember { mutableStateOf<String?>(null) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Propuestas Recibidas") })
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            when {
//                uiState.value.isLoading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//
//                uiState.value.error != null -> {
//                    Text(
//                        "Error: ${uiState.value.error}",
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//
//                uiState.value.proposals.isEmpty() -> {
//                    Text(
//                        "No tienes propuestas pendientes.",
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//
//                else -> {
//                    LazyColumn(
//                        contentPadding = PaddingValues(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        items(uiState.value.proposals, key = { it.id }) { proposal ->
//                            ProposalCard(
//                                proposal = proposal,
//                                onAcceptClick = {
//                                    proposalToAction.value = proposal
//                                    actionType.value = "aceptar"
//                                },
//                                onRejectClick = {
//                                    proposalToAction.value = proposal
//                                    actionType.value = "rechazar"
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        if (proposalToAction.value != null && actionType.value != null) {
//            ConfirmationDialog(
//                actionType = actionType.value!!,
//                onConfirm = {
//                    if (actionType.value == "aceptar") {
//                        viewModel.acceptProposal(proposalToAction.value!!)
//                    } else {
//                        viewModel.rejectProposal(proposalToAction.value!!)
//                    }
//                    proposalToAction.value = null
//                    actionType.value = null
//                },
//                onDismiss = {
//                    proposalToAction.value = null
//                    actionType.value = null
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProposalCard(
//    proposal: Proposal,
//    onAcceptClick: () -> Unit,
//    onRejectClick: () -> Unit
//) {
//    Card(
//        elevation = CardDefaults.cardElevation(4.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            Text(
//                text = "Propuesta para: ${proposal.publicationTitle}",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(Modifier.height(4.dp))
//            Text(
//                "De: ${proposal.proposerName}",
//                style = MaterialTheme.typography.bodySmall
//            )
//            Spacer(Modifier.height(8.dp))
//            Text(
//                proposal.proposalText,
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            if (proposal.offeredPublicationId != null) {
//                Text(
//                    "Ofrece a cambio: [ID: ${proposal.offeredPublicationId}]",
//                    style = MaterialTheme.typography.bodySmall,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            Spacer(Modifier.height(16.dp))
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                Button(
//                    onClick = onAcceptClick,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    )
//                ) {
//                    Text("Aceptar")
//                }
//                Spacer(Modifier.width(8.dp))
//                Button(
//                    onClick = onRejectClick,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.error
//                    )
//                ) {
//                    Text("Rechazar")
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ConfirmationDialog(
//    actionType: String,
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Confirmar Acción") },
//        text = {
//            Text(
//                "¿Estás seguro de que quieres $actionType esta propuesta? " +
//                        "Esta acción no se puede deshacer."
//            )
//        },
//        confirmButton = {
//            Button(onClick = onConfirm) {
//                Text("Confirmar")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancelar")
//            }
//        }
//    )
//}
