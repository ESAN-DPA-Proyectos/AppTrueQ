package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.exchange.viewmodel.TradeHistoryViewModel
import edu.esandpa202502.apptrueq.model.Trade
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeHistoryScreen(navController: NavController) {
    val viewModel: TradeHistoryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Trueques") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
                }
                uiState.trades.isEmpty() -> {
                    Text("Aún no tienes trueques en tu historial.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.trades) { trade ->
                            TradeHistoryCard(trade = trade, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TradeHistoryCard(trade: Trade, navController: NavController) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val counterPartyName = if (trade.offerentId == currentUserId) trade.receiverName else trade.offerentName
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Routes.TradeDetail.createRoute(trade.id)) }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = trade.publicationTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Contraparte: $counterPartyName", style = MaterialTheme.typography.bodyMedium)
            Text("Estado: ${trade.status.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            trade.updatedAt?.toDate()?.let {
                Text("Última actualización: ${formatter.format(it)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
