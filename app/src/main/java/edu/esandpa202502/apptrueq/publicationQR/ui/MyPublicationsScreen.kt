package edu.esandpa202502.apptrueq.publicationQR.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.publicationQR.viewmodel.PublicationQRViewModel

@Composable
fun MyPublicationsScreen() {
    val viewModel: PublicationQRViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showQrDialogFor by remember { mutableStateOf<Publication?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Publicaciones para Compartir",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.publications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no has creado ninguna publicación.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.publications, key = { it.id }) { publication ->
                    MyPublicationCard(
                        publication = publication,
                        onShareClick = { showQrDialogFor = publication }
                    )
                }
            }
        }
    }

    // Diálogo para mostrar el QR, usando el QrCodeGenerator existente
    if (showQrDialogFor != null) {
        AlertDialog(
            onDismissRequest = { showQrDialogFor = null },
            title = { Text("Comparte tu Publicación") },
            text = { 
                QrCodeGenerator(
                    publicationId = showQrDialogFor!!.id, 
                    viewModel = viewModel,
                    onDismiss = { showQrDialogFor = null }
                )
            },
            confirmButton = {} // Los botones están dentro del QrCodeGenerator
        )
    }
}

@Composable
private fun MyPublicationCard(publication: Publication, onShareClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = publication.imageUrl,
                contentDescription = publication.title,
                modifier = Modifier.size(80.dp), // Tamaño fijo para la imagen
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(publication.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(publication.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = onShareClick) {
                Icon(Icons.Default.Share, contentDescription = "Compartir Publicación")
            }
        }
    }
}
