package edu.esandpa202502.apptrueq.need.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.esandpa202502.apptrueq.need.viewmodel.NeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedFormScreen(
    vm: NeedViewModel,
    onSuccess: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

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
            label = { Text("¿Qué necesitas?*") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "${description.length}/200 caracteres",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = "Mínimo 5 caracteres",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }


        Spacer(Modifier.height(12.dp))

        val categories = listOf("Hogar", "Libros", "Servicios", "Tecnología")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                label = { Text("Categoría (Opcional)") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = {
                        category = cat
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                vm.addNeed(description, category)
                description = ""
                category = ""
                onSuccess()
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Publicar Necesidad", color = Color.White)
        }
    }
}
