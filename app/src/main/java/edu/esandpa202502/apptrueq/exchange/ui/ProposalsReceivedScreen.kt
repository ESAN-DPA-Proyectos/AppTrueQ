package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.exchange.viewmodel.ProposalExchangeViewModel
import edu.esandpa202502.apptrueq.model.Proposal

@Composable
fun ProposalsReceivedScreen(
    vm: ProposalExchangeViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredProposals: List<Proposal> = remember(state.proposals, searchQuery) {
        if (searchQuery.isBlank()) {
            state.proposals
        } else {
            state.proposals.filter { proposal ->
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar propuestas…") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredProposals.isEmpty()) {
            Text(
                text = "No tienes propuestas pendientes.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProposals, key = { it.id }) { proposal ->
                    ProposalCard(
                        proposal = proposal,
                        onAccept = { vm.acceptProposal(proposal) },
                        onReject = { vm.rejectProposal(proposal) }
                    )
                }
            }
        }

        state.error?.let { errorMsg ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: Proposal,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = proposal.publicationTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Proponente: ${proposal.proposerName}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (proposal.proposalText.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Mensaje: ${proposal.proposalText}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!proposal.offeredPublicationId.isNullOrBlank()
                || !proposal.offeredItemTitle.isNullOrBlank()
            ) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ofrece a cambio:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                proposal.offeredItemTitle?.let {
                    Text(
                        text = "- $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!proposal.offeredPublicationId.isNullOrBlank()) {
                    Text(
                        text = "- Publicación existente (ID: ${proposal.offeredPublicationId})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onReject) {
                    Text("RECHAZAR")
                }
                Spacer(Modifier.width(4.dp))
                Button(onClick = onAccept) {
                    Text("ACEPTAR")
                }
            }
        }
    }
}
