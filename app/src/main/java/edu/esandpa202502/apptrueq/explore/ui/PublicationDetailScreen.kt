package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.proposal.ui.ProposalDialog
import edu.esandpa202502.apptrueq.proposal.viewmodel.PublicationDetailViewModel
import edu.esandpa202502.apptrueq.proposal.viewmodel.PublicationDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

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
                    val publication = uiState.publication
                    if (currentUser != null && publication != null && publication.userId != currentUser.uid) {
                        // SOLUCIÓN: Componente personalizado para el botón de reportar.
                        Column(
                            modifier = Modifier
                                .clickable { navController.navigate(Routes.ReportUser.createRoute(publication.userId, publication.id)) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Reportar Usuario",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Reportar\nUsuario",
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val publicationOwnerId = uiState.publication?.userId
            if (currentUser != null && publicationOwnerId != null && publicationOwnerId != currentUser.uid) {
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
                        Text(publication.title, style = MaterialTheme.typography.titleLarge)

                        val authorName =  uiState.publicationOwnerName ?: "Usuario Desconocido"
                        val formattedDate = remember(publication.date) {
                            publication.date?.let {
                                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE")).format(it)
                            } ?: "Fecha no disponible"
                        }

                        Text(
                            text = "Publicado por: $authorName, el día $formattedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
