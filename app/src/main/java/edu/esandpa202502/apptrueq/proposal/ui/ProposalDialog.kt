package edu.esandpa202502.apptrueq.proposal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.viewmodel.PublicationDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDialog(
    viewModel: PublicationDetailViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var proposalText by remember { mutableStateOf("") }
    var selectedPublication: Publication? by remember { mutableStateOf(null) }
    val isTextValid = proposalText.length in 10..250

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enviar Propuesta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = proposalText,
                    onValueChange = { proposalText = it },
                    label = { Text("Mensaje de la propuesta") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isTextValid && proposalText.isNotEmpty(),
                    supportingText = { 
                        if (!isTextValid && proposalText.isNotEmpty()) {
                            Text("Debe tener entre 10 y 250 caracteres.")
                        }
                    }
                )

                if (uiState.userPublications.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = selectedPublication?.title ?: "Selecciona una de tus publicaciones para ofrecer",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ofrecer a cambio (obligatorio)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            uiState.userPublications.forEach { pub ->
                                DropdownMenuItem(
                                    text = { Text(pub.title) },
                                    onClick = {
                                        selectedPublication = pub
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text("No tienes publicaciones para ofrecer a cambio.", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.submitProposal(proposalText, selectedPublication?.id)
                    onDismiss()
                },
                // Habilitar solo si el texto es válido y se ha seleccionado una publicación
                enabled = isTextValid && selectedPublication != null
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
