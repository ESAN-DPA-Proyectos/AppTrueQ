package edu.esandpa202502.apptrueq.offer.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.ui.viewmodel.TradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferFormScreen(
    vm: TradeViewModel,
    onSuccess: () -> Unit = {}
) {
    // Campos del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var needText by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scrollState = rememberScrollState()

    // Lanzador para elegir imágenes
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    // Validación de campos
    val isValid = title.length >= 5 && description.length >= 20 && category.isNotEmpty() && needText.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo título
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título *") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${title.length} caracteres (mínimo 5)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(12.dp))

        // Campo descripción
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Text(
            text = "${description.length} caracteres (mínimo 20)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(12.dp))

        // Selector de categoría
        val categories = listOf("Hogar", "Libros", "Servicios", "Tecnología")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                label = { Text("Categoría *") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
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

        Spacer(Modifier.height(12.dp))

        // Campo texto de necesidad
        OutlinedTextField(
            value = needText,
            onValueChange = { needText = it },
            label = { Text("¿Qué pides a cambio? *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Botón para subir imágenes (secundario)
        OutlinedButton(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Upload,
                contentDescription = null,
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Subir Imágenes")
        }

        // Mostrar imágenes seleccionadas
        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = null,
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón de publicar (principal)
        Button(
            onClick = {
                val newOffer = Offer(
                    title = title,
                    description = description,
                    category = category,
                    needText = needText,
                    status = "ACTIVE",
                    // NOTA: El ownerId debe ser reemplazado por el ID del usuario autenticado
                    ownerId = ""
                )
                vm.addOffer(newOffer, imageUris)
                // Limpia campos
                title = ""
                description = ""
                category = ""
                needText = ""
                imageUris = emptyList()
                onSuccess()
            },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Publicar Oferta")
        }
    }
}
