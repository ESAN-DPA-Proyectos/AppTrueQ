package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.exchange.viewmodel.ExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeHistoryScreen(
    navController: NavController,
    exchangeViewModel: ExchangeViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uiState by exchangeViewModel.historyUiState.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let {
            exchangeViewModel.listenForHistory(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Trueques") },
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
                uiState.completedOffers.isEmpty() -> {
                    Text(
                        text = "Aún no tienes trueques en tu historial.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.completedOffers, key = { it.id }) { offer ->
                            HistoryCard(offer = offer, currentUserId = currentUser?.uid ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(offer: Offer, currentUserId: String) {
    val isAccepted = offer.status == "ACEPTADA"
    val cardColor = if (isAccepted) Color.Transparent else MaterialTheme.colorScheme.errorContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val title = if (offer.ownerId == currentUserId) {
                "Tú ofreciste '${offer.title}' por '${offer.needText}'"
            } else {
                "${offer.ownerName} te ofreció '${offer.title}' por '${offer.needText}'"
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Estado: ${offer.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
