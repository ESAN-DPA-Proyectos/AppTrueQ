package edu.esandpa202502.apptrueq.offer.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.model.Offer

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(offer.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(offer.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text("Categoría: ${offer.categoria}", style = MaterialTheme.typography.labelSmall)
        }
    }
}


