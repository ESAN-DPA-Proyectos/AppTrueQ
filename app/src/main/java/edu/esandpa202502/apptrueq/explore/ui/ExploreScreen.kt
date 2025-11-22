package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Publication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    exploreViewModel: ExploreViewModel = viewModel() // Inyecta el ViewModel
) {
    // La UI ahora obtiene el estado directamente desde el ViewModel
    val uiState by exploreViewModel.uiState
    val tabs = listOf("Ofertas", "Necesidades")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(12.dp))
        Text(
            text = "Explorar Ofertas y Necesidades",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // Los Tabs ahora reaccionan a los eventos del ViewModel
        TabRow(
            selectedTabIndex = uiState.selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTabIndex]),
                    color = Color.Black
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTabIndex == index,
                    onClick = { exploreViewModel.onTabChanged(index) }, // Notifica al ViewModel del cambio
                    text = { Text(title) },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray
                )
            }
        }

        // SearchBar notifica al ViewModel del cambio de texto
        SearchBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { exploreViewModel.onSearchQueryChanged(it) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )

            Button(onClick = { /* TODO: HU-05 Handle category filter */ }, colors = buttonColors) {
                Text("Categoría")
            }
            Button(onClick = { /* TODO: HU-05 Handle location filter */ }, colors = buttonColors) {
                Text("Ubicación")
            }
            Button(onClick = { /* TODO: HU-05 Handle date filter */ }, colors = buttonColors) {
                Text("Fecha")
            }
        }

        // Muestra un indicador de carga mientras se obtienen los datos
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            // Muestra la lista de publicaciones o el mensaje de "no encontrado"
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                if (uiState.filteredPublications.isNotEmpty()) {
                    items(uiState.filteredPublications) { publication ->
                        PublicationCard(publication = publication) {
                            navController.navigate(Routes.PublicationDetail.createRoute(publication.id))
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No se encontraron publicaciones con los criterios seleccionados.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationCard(publication: Publication, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = publication.imageUrl).apply {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }.build()
            )

            Image(
                painter = painter,
                contentDescription = "Publication Image",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = publication.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = publication.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
