package edu.esandpa202502.apptrueq.need.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.need.viewmodel.NeedState
import edu.esandpa202502.apptrueq.need.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedFormScreen(
    vm: NeedViewModel,
    onSuccess: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val state by vm.needState.collectAsState()

    // Reaccionar a cambios de estado del ViewModel
    LaunchedEffect(state) {
        when (state) {
            is NeedState.Success -> {
                // Limpiar el formulario y notificar éxito
                description = ""
                category = ""
                errorMessage = null
                vm.resetState()
                onSuccess()
            }

            is NeedState.Error -> {
                errorMessage = (state as NeedState.Error).message
            }

            else -> Unit
        }
    }

    val isValid = description.length >= 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("¿Qué necesitas? *") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${description.length}/200 caracteres",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Mínimo 5 caracteres",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(12.dp))

        // Categorías opcionales
        val categories = listOf("Hogar", "Libros", "Servicios", "Tecnología")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (category.isBlank()) "Sin categoría" else category,
                onValueChange = {},
                label = { Text("Categoría (Opcional)") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Mensaje de error (si lo hay)
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Indicador de carga
        if (state is NeedState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                vm.publishNeed(
                    needText = description,
                    category = if (category.isBlank()) null else category
                )
            },
            enabled = isValid && state !is NeedState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Publicar necesidad")
        }
    }
}
