package edu.esandpa202502.apptrueq.proposal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.proposal.viewmodel.ProposalsHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsHistoryScreen(
    navController: NavController,
    proposalsHistoryViewModel: ProposalsHistoryViewModel = viewModel()
) {
    val uiState by proposalsHistoryViewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Enviadas", "Recibidas", "Aceptadas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Propuestas") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}")
                }
            } else {
                when (selectedTabIndex) {
                    0 -> ProposalsList(navController, uiState.sentProposals, "Enviada a")
                    1 -> ProposalsList(navController, uiState.receivedProposals, "Recibida de")
                    2 -> ProposalsList(navController, uiState.acceptedProposals, "Aceptada de")
                }
            }
        }
    }
}

@Composable
fun ProposalsList(navController: NavController, proposals: List<Proposal>, typeText: String) {
    if (proposals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay propuestas en esta categoría.")
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(proposals, key = { it.id }) { proposal ->
                ProposalHistoryCard(proposal = proposal, typeText = typeText) {
                    navController.navigate(Routes.PublicationDetail.createRoute(proposal.publicationId))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalHistoryCard(proposal: Proposal, typeText: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = proposal.publicationTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Tu propuesta: \"${proposal.proposalText}\"")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                 Text("$typeText: ${proposal.proposerName}", style = MaterialTheme.typography.bodySmall)
                 Text(proposal.status, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}