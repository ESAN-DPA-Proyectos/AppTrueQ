package edu.esandpa202502.apptrueq.offer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.offer.ui.components.OfferCard

@OptIn(ExperimentalMaterial3Api::class) //Suprime el warning de TopAppBar en Material3
@Composable
fun OfferListScreen(navController: NavController) {
    val sampleOffers = remember {
        listOf("Laptop usada", "Bicicleta nueva", "Celular Samsung")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ofertas disponibles") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("offerForm") }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(sampleOffers.size) { index ->
                OfferCard(title = sampleOffers[index])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


