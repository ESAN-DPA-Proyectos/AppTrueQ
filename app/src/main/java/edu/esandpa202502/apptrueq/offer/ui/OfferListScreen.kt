package edu.esandpa202502.apptrueq.offer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.offer.ui.components.OfferCard
import edu.esandpa202502.apptrueq.ui.viewmodel.TradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferListScreen(vm: TradeViewModel) {
    val offers by vm.offers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }
    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")

    val filteredOffers = offers.filter {
        val matchesCategory = selectedCategory == "Todas las categorías" || it.category == selectedCategory
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp)) // Espacio para que no se pegue a las pestañas

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar en mis publicaciones...") },
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
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Total: ${filteredOffers.size} publicación(es)")

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredOffers) { offer ->
                OfferCard(offer = offer)
            }
        }
    }
}
