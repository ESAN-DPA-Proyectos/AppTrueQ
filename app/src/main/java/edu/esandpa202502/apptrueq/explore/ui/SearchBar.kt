package edu.esandpa202502.apptrueq.explore.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Buscar en títulos y descripciones") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        // --- INICIO DE LA CORRECCIÓN ---
        // Se personalizan los colores del componente a negro.
        // Los parámetros 'unfocusedBorderColor' y 'focusedBorderColor' han sido renombrados.
        colors = TextFieldDefaults.colors(
            // Colores para el estado no enfocado (cuando el campo no está seleccionado)
            unfocusedLeadingIconColor = Color.Black,
            unfocusedIndicatorColor = Color.Black, // Cambiado de unfocusedBorderColor
            unfocusedLabelColor = Color.Black,

            // Colores para el estado enfocado (cuando el usuario está escribiendo)
            focusedLeadingIconColor = Color.Black,
            focusedIndicatorColor = Color.Black,  // Cambiado de focusedBorderColor
            focusedLabelColor = Color.Black,

            // Color del cursor de texto
            cursorColor = Color.Black
        ),
        // --- FIN DE LA CORRECCIÓN ---
    )
}
