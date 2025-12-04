package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.proposal.ui.ProposalDialog
import edu.esandpa202502.apptrueq.proposal.viewmodel.PublicationDetailViewModel
import edu.esandpa202502.apptrueq.proposal.viewmodel.PublicationDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    navController: NavController,
    publicationId: String
) {
    val viewModel: PublicationDetailViewModel = viewModel(
        factory = PublicationDetailViewModelFactory(publicationId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val showProposalDialog = remember { mutableStateOf(false) }

    if (showProposalDialog.value) {
        ProposalDialog(
            viewModel = viewModel,
            onDismiss = { showProposalDialog.value = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Publicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    val publicationOwnerId = uiState.publication?.userId
                    if (publicationOwnerId != null && publicationOwnerId != currentUser?.uid) {
                        IconButton(onClick = {
                            // Navega usando una ruta simple (ajusta si tu NavGraph usa otra)
                            navController.navigate("reportUsr_user/$publicationOwnerId")
                        }) {
                            Icon(
                                Icons.Default.Report,
                                contentDescription = "Reportar Usuario"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.publication?.userId != currentUser?.uid) {
                ExtendedFloatingActionButton(
                    onClick = { showProposalDialog.value = true },
                    icon = { Icon(Icons.Filled.SwapHoriz, "Proponer Trueque") },
                    text = { Text("Proponer Trueque") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.publication != null -> {
                    val publication = uiState.publication!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        if (publication.imageUrl.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(publication.imageUrl)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = publication.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            publication.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Categoría: ${publication.category}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            publication.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
