package edu.esandpa202502.apptrueq.offer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OfferCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: abrir detalles */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Categoría: General", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


