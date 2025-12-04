package edu.esandpa202502.apptrueq.notification.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// QA: Se importa el ViewModel correcto para esta pantalla.
import edu.esandpa202502.apptrueq.notification.viewmodel.NotificationDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationId: String,
    referenceId: String,
    // QA: Se inyecta el ViewModel correcto: NotificationDetailViewModel.
    viewModel: NotificationDetailViewModel = viewModel()
) {
    // QA: Ahora el compilador puede inferir el tipo porque `detailUiState` sí existe en el ViewModel correcto.
    val detailUiState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(referenceId) {
        viewModel.loadProposal(referenceId)
    }

    LaunchedEffect(detailUiState.actionCompleted) {
        if (detailUiState.actionCompleted) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Propuesta") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                detailUiState.isLoading -> CircularProgressIndicator()
                detailUiState.error != null -> Text("Error: ${detailUiState.error}")
                detailUiState.proposal != null -> {
                    val proposal = detailUiState.proposal!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Propuesta de: ${proposal.proposerName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Para tu publicación: \"${proposal.publicationTitle}\"")
                            Text("Propuesta: \"${proposal.proposalText}\"")
                            Text("Estado: ${proposal.status}")

                            Spacer(modifier = Modifier.height(16.dp))

                            if (proposal.status == "PENDIENTE") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = { viewModel.acceptProposal(proposal) }) {
                                        Text("Aceptar")
                                    }
                                    Button(
                                        onClick = { viewModel.rejectProposal(proposal) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text("No se encontró la propuesta.")
                }
            }
        }
    }
}