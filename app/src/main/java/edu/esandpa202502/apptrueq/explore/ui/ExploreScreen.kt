package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExploreScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ofertas", "Necesidades")

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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar en títulos y descripciones") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

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
            // Aquí se mostrará la lista de publicaciones
            // Implementaremos la carga infinita y la visualización de detalles más adelante.
            item {
                Text(
                    text = if (selectedTab == 0) "Mostrando Ofertas" else "Mostrando Necesidades",
                    modifier = Modifier.padding(16.dp)
                )
            }
            // Cuando no hay resultados
            // item {
            //     Text(
            //         text = "No se encontraron publicaciones con los criterios seleccionados.",
            //         modifier = Modifier.padding(16.dp)
            //     )
            // }
        }
    }
}
