package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.explore.viewmodel.ExploreViewModel
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.repository.explore.ExploreRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController
) {
    val viewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModelFactory(ExploreRepository())
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val locationQuery by viewModel.locationQuery.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()

    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")
    val typeOptions = listOf("Todos", "Ofertas", "Necesidades")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text("Explorar Publicaciones", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            typeOptions.forEach { type ->
                FilterChip(
                    selected = typeFilter == type,
                    onClick = { viewModel.onTypeFilterChanged(type) },
                    label = { Text(type) }
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            label = { Text("Buscar por título o descripción...") },
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
                        viewModel.onCategoryChanged(cat)
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = locationQuery,
            onValueChange = { viewModel.onLocationChanged(it) },
            label = { Text("Filtrar por ubicación...") },
            modifier = Modifier.fillMaxWidth()
        )

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
                uiState.publications.isEmpty() -> {
                    Text(
                        text = "No se encontraron publicaciones con esos criterios.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.publications, key = { it.id }) { publication ->
                            val authorName = uiState.authorNames[publication.userId] ?: "Usuario Desconocido"
                            PublicationCard(
                                publication = publication,
                                authorName = authorName,
                                onClick = { 
                                    navController.navigate(Routes.PublicationDetail.createRoute(publication.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicationCard(publication: Publication, authorName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = publication.imageUrl,
                contentDescription = publication.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(16.dp)) {
                Text(publication.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(publication.category, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text("Publicado por: $authorName", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(6.dp))
                Text(publication.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                 Spacer(Modifier.height(8.dp))
                Button(onClick = onClick) {
                    Text("Ver mas")
                }
            }
        }
    }
}