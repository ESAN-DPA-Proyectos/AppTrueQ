package edu.esandpa202502.apptrueq.need.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.need.ui.components.NeedCard
import edu.esandpa202502.apptrueq.need.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedListScreen(
    vm: NeedViewModel
) {
    LaunchedEffect(Unit) {
        vm.loadMyNeeds()
    }

    val needs: List<Need> by vm.myNeeds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }

    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")

    val filteredNeeds = needs
        .filter { need ->
            val matchesCategory =
                selectedCategory == "Todas las categorías" ||
                        need.category.equals(selectedCategory, ignoreCase = true)

            val matchesSearch =
                need.needText.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
        .sortedByDescending { it.createdAt?.toDate() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar en mis necesidades...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            selectedCategory = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Total: ${filteredNeeds.size} publicación(es)")

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredNeeds) { need ->
                NeedCard(need = need)
            }
        }
    }
}
