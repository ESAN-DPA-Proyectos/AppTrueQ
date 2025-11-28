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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.exchange.viewmodel.ExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationId: String?,
    referenceId: String?,
    exchangeViewModel: ExchangeViewModel = viewModel()
) {
    // CORREGIDO: Se observa la propiedad `offersUiState` que contiene la lista necesaria.
    val uiState by exchangeViewModel.offersUiState.collectAsState()
    val offerToShow = uiState.offers.find { it.id == referenceId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Oferta") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (offerToShow != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Propuesta de: ${offerToShow.ownerName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Para tu necesidad: \"${offerToShow.needText}\"")
                        Text("A cambio de: \"${offerToShow.title}\"")
                        Text("Estado: ${offerToShow.status}")

                        Spacer(modifier = Modifier.height(16.dp))

                        if (offerToShow.status == "PENDIENTE") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { 
                                    exchangeViewModel.onAcceptOffer(offerToShow)
                                    navController.popBackStack()
                                }) {
                                    Text("Aceptar")
                                }
                                Button(
                                    onClick = { 
                                        exchangeViewModel.onRejectOffer(offerToShow)
                                        navController.popBackStack()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            } else {
                // Muestra un indicador si la oferta no se encuentra en la lista (o está cargando)
                CircularProgressIndicator()
            }
        }
    }
}
