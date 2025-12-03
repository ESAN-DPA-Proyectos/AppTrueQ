package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.proposal.ui.ProposalDialog
import edu.esandpa202502.apptrueq.viewmodel.PublicationDetailViewModel
import edu.esandpa202502.apptrueq.viewmodel.PublicationDetailViewModelFactory

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
    var showProposalDialog by remember { mutableStateOf(false) }

    if (showProposalDialog) {
        ProposalDialog(
            viewModel = viewModel,
            onDismiss = { showProposalDialog = false }
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
                // HU-10: Se añade la sección de acciones en la TopAppBar.
                actions = {
                    // El botón de reportar solo aparece si el usuario actual NO es el dueño de la publicación.
                    val publicationOwnerId = uiState.publication?.userId
                    if (publicationOwnerId != null && publicationOwnerId != currentUser?.uid) {
                        IconButton(onClick = {
                            // Navega a la pantalla de reporte, pasando el ID del usuario a reportar.
                            navController.navigate(Routes.ReportUser.createRoute(publicationOwnerId))
                        }) {
                            Icon(Icons.Default.Report, contentDescription = "Reportar Usuario")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.publication?.userId != currentUser?.uid) {
                ExtendedFloatingActionButton(
                    onClick = { showProposalDialog = true },
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
                        Text(publication.title, style = MaterialTheme.typography.titleLarge)
                        Text("Categoría: ${publication.category}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(publication.description, style = MaterialTheme.typography.bodyLarge)
                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        if (publication.userId == currentUser?.uid) {
                            Text("Esta es una de tus publicaciones.", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}