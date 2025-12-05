package edu.esandpa202502.apptrueq.proposal.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.proposal.viewmodel.PublicationDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDialog(viewModel: PublicationDetailViewModel, onDismiss: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Elegir Publicación", "Crear Oferta")

    var selectedPublicationId by remember { mutableStateOf<String?>(null) }

    var newOfferTitle by remember { mutableStateOf("") }
    var newOfferDescription by remember { mutableStateOf("") }
    var newOfferImageUri by remember { mutableStateOf<Uri?>(null) }

    var proposalMessage by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> newOfferImageUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Haz tu Propuesta de Trueque") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> {
                        LazyColumn(modifier = Modifier.padding(top = 16.dp).heightIn(max = 200.dp)) {
                            items(uiState.userPublications, key = { it.id }) { publication ->
                                SelectablePublicationCard(
                                    publication = publication,
                                    isSelected = selectedPublicationId == publication.id,
                                    onClick = { selectedPublicationId = publication.id }
                                )
                            }
                        }
                    }
                    1 -> {
                        Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newOfferTitle,
                                onValueChange = { newOfferTitle = it },
                                label = { Text("Título de tu oferta") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newOfferDescription,
                                onValueChange = { newOfferDescription = it },
                                label = { Text("Describe lo que ofreces") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(onClick = { imagePicker.launch("image/*") }) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Añadir Imagen")
                            }
                            newOfferImageUri?.let {
                                Image(painter = rememberAsyncImagePainter(it), contentDescription = "Imagen seleccionada", modifier = Modifier.size(80.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = proposalMessage,
                    onValueChange = { proposalMessage = it },
                    label = { Text("Añade un mensaje (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedTabIndex == 0) {
                    selectedPublicationId?.let {
                        viewModel.submitProposal(proposalMessage, offeredPublicationId = it)
                    }
                } else {
                    // SOLUCIÓN: Se descomenta la línea para activar la funcionalidad.
                    viewModel.submitProposalWithNewOffer(proposalMessage, newOfferTitle, newOfferDescription, newOfferImageUri)
                }
                onDismiss()
            }) {
                Text("Enviar Propuesta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectablePublicationCard(publication: Publication, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, MaterialTheme.shapes.medium)
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = publication.imageUrl),
                contentDescription = publication.title,
                modifier = Modifier.size(50.dp).clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(publication.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}