package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import edu.esandpa202502.apptrueq.model.Publication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController
) {
    val viewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModelFactory(ExploreRepository())
    )
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Todos", "Ofertas", "Necesidades")

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
                viewModel.onSearchQueryChanged(it)
            },
            label = { Text("Buscar en todas las publicaciones...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryFilter(viewModel = viewModel, modifier = Modifier.weight(1f))
            SortOrderFilter(viewModel = viewModel, modifier = Modifier.weight(1f))
        }

        TabRow(selectedTabIndex = uiState.selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTabIndex == index,
                    onClick = { viewModel.onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isInitiallyLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.publications.isEmpty()) {
                Text(
                    text = "No se encontraron publicaciones con los criterios seleccionados.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.publications, key = { it.id }) { publication ->
                        PublicationCard(publication = publication) {
                            navController.navigate(Routes.PublicationDetail.createRoute(publication.id))
                        }
                    }

                    if (!uiState.endReached) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }

                // Lógica para detectar el final del scroll
                val isScrolledToEnd by remember {
                    derivedStateOf {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        lastVisibleItem != null && lastVisibleItem.index == listState.layoutInfo.totalItemsCount - 1
                    }
                }

                LaunchedEffect(isScrolledToEnd) {
                    if (isScrolledToEnd) {
                        viewModel.loadNextPage()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(viewModel: ExploreViewModel, modifier: Modifier = Modifier) {
    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { cat ->
                DropdownMenuItem(text = { Text(cat) }, onClick = {
                    selectedCategory = cat
                    viewModel.onCategoryChanged(cat)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOrderFilter(viewModel: ExploreViewModel, modifier: Modifier = Modifier) {
    val sortOptions = mapOf(DateSortOrder.RECENT_FIRST to "Recientes primero", DateSortOrder.OLDEST_FIRST to "Antiguos primero")
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = sortOptions[uiState.dateSortOrder] ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Ordenar por") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { (sortOrder, text) ->
                DropdownMenuItem(text = { Text(text) }, onClick = {
                    viewModel.onSortOrderChange(sortOrder)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun PublicationCard(publication: Publication, onClick: () -> Unit) {
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
                Text(publication.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                 Spacer(Modifier.height(8.dp))
                Button(onClick = { /* TODO: Navegar a la pantalla para crear la oferta (Need) */ }) {
                    Text("Realizar Oferta")
                }
            }
        }
    }
}
