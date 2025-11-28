package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.exchange.viewmodel.ExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailScreen(tradeId: String?, onNavigateBack: () -> Unit, exchangeViewModel: ExchangeViewModel = viewModel()) {

    val historyState by exchangeViewModel.historyUiState.collectAsState()
    // Buscamos la oferta específica en la lista del historial que ya tenemos en el ViewModel
    val offerToShow = historyState.completedOffers.find { it.id == tradeId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Trueque") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (offerToShow != null) {
                Text(
                    text = "Intercambio por: \"${offerToShow.needText}\"",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ofertante: ${offerToShow.ownerName}",
                    fontSize = 20.sp
                )
                
                Text(
                    text = "Producto ofrecido: ${offerToShow.title}",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Estado: ${offerToShow.status}", style = MaterialTheme.typography.bodyLarge)

            } else {
                Text("No se encontraron los detalles del trueque.")
            }
        }
    }
}