package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    offerViewModel: OfferViewModel = viewModel()
) {
    val uiState by offerViewModel.exploreUiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }
    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text("Explorar Publicaciones", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                offerViewModel.onExploreSearchQueryChanged(it)
            },
            label = { Text("Buscar en todas las publicaciones...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = {
                        selectedCategory = cat
                        offerViewModel.onExploreCategoryChanged(cat)
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
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
                uiState.allOffers.isEmpty() -> {
                    Text(
                        text = "No se encontraron publicaciones.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.allOffers, key = { it.id }) { offer ->
                            PublicationCard(publication = offer) { 
                                // Navega al detalle de la publicación al hacer clic
                                navController.navigate(Routes.PublicationDetail.createRoute(offer.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicationCard(publication: Offer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = publication.photos.firstOrNull(),
                contentDescription = publication.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(16.dp)) {
                Text(publication.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(publication.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                 Spacer(Modifier.height(8.dp))
                // Botón para iniciar el flujo de trueque
                Button(onClick = { /* TODO: Navegar a la pantalla para crear la oferta (Need) */ }) {
                    Text("Realizar Oferta")
                }
            }
        }
    }
}
