package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.esandpa202502.apptrueq.R
import edu.esandpa202502.apptrueq.core.navigation.Routes
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ofertas", "Necesidades")

    // Sample data
    val publications = remember {
        listOf(
            Publication("1", "Laptop Gamer", "Laptop en buen estado", "Tecnología", "Lima", "https://picsum.photos/id/10/200/300", Date(), "user1", PublicationType.OFFER),
            Publication("2", "Libro de Kotlin", "Busco libro de Kotlin", "Libros", "Surco", "https://picsum.photos/id/20/200/300", Date(), "user2", PublicationType.NEED),
            Publication("3", "Ropa de invierno", "Casaca talla M", "Ropa", "Miraflores", "https://picsum.photos/id/30/200/300", Date(), "user3", PublicationType.OFFER)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(12.dp))
        Text(text = "Explorar Ofertas y Necesidades", modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge)

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        SearchBar(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* TODO: Handle category filter */ }) {
                Text("Categoría")
            }
            Button(onClick = { /* TODO: Handle location filter */ }) {
                Text("Ubicación")
            }
            Button(onClick = { /* TODO: Handle date filter */ }) {
                Text("Fecha")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            val filteredList = publications.filter {
                val typeMatches = if (selectedTab == 0) it.type == PublicationType.OFFER else it.type == PublicationType.NEED
                val queryMatches = it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
                typeMatches && queryMatches
            }

            if (filteredList.isNotEmpty()) {
                items(filteredList) { publication ->
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
                ImageRequest.Builder(LocalContext.current).data(data = publication.imageUrl).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }).build()
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