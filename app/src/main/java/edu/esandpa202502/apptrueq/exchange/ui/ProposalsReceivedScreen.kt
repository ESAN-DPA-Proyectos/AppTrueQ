package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.exchange.viewmodel.ExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsReceivedScreen(
    navController: NavController,
    exchangeViewModel: ExchangeViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uiState by exchangeViewModel.uiState.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let {
            exchangeViewModel.listenForReceivedOffers(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Propuestas Recibidas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
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
                    Text(
                        text = "Error: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                uiState.offers.isEmpty() -> {
                    Text(
                        text = "No tienes propuestas pendientes.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    ProposalsList(
                        offers = uiState.offers,
                        onAccept = { exchangeViewModel.onAcceptOffer(it) },
                        onReject = { exchangeViewModel.onRejectOffer(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalsList(
    offers: List<Offer>,
    onAccept: (Offer) -> Unit,
    onReject: (Offer) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(offers, key = { it.id }) { offer ->
            ProposalCard(offer = offer, onAccept = { onAccept(offer) }, onReject = { onReject(offer) })
        }
    }
}

@Composable
private fun ProposalCard(
    offer: Offer,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Propuesta de: ${offer.ownerName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Para tu necesidad: \"${offer.needText}\"")
            Text("A cambio de: \"${offer.title}\"")

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onAccept, modifier = Modifier.weight(1f)) {
                    Text("Aceptar")
                }
                Button(onClick = onReject, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Rechazar")
                }
            }
        }
    }
}
