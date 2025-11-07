package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.esandpa202502.apptrueq.core.QrCodeGenerator
import edu.esandpa202502.apptrueq.publication.viewmodel.PublicationViewModel

@Composable
fun PublicationDetailScreen(
    publicationId: String,
    publicationViewModel: PublicationViewModel = viewModel()
) {
    var showProposalForm by remember { mutableStateOf(false) }
    var showQrGenerator by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            showProposalForm -> {
                ProposalForm(publicationId = publicationId) {
                    showProposalForm = false
                }
            }
            showQrGenerator -> {
                QrCodeGenerator(publicationId = publicationId, viewModel = publicationViewModel)
            }
            else -> {
                Text(text = "Detalle de la Publicación: $publicationId")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showProposalForm = true }) {
                    Text(text = "Enviar propuesta")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showQrGenerator = true }) {
                    Text(text = "Compartir por QR")
                }
            }
        }
    }
}

@Composable
fun ProposalForm(publicationId: String, onDismiss: () -> Unit) {
    var proposalText by remember { mutableStateOf("") }
    val isFormValid = proposalText.length >= 10 && proposalText.length <= 250

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enviar Propuesta a Publicación: $publicationId")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = proposalText,
            onValueChange = { proposalText = it },
            label = { Text("Mensaje de la propuesta (10-250 caracteres)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // TODO: Add option to attach user's own offer
        Button(
            onClick = {
                // TODO: Handle proposal submission
                onDismiss()
            },
            enabled = isFormValid
        ) {
            Text(text = "Enviar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDismiss) {
            Text(text = "Cancelar")
        }
    }
}
