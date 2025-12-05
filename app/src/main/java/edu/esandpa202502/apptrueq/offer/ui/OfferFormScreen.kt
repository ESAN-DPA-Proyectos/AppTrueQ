package edu.esandpa202502.apptrueq.offer.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferFormScreen(
    vm: OfferViewModel,
    onSuccess: () -> Unit = {}
) {
    // --- Campos del formulario ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var needText by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scrollState = rememberScrollState()

    // --- Lanzador para elegir imágenes (solo imágenes) ---
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // Se permite máximo 5 fotos
        imageUris = (imageUris + uris).take(5)
    }

    // --- Validaciones de HU-03 ---
    val categoryIsService = category == "Servicios"

    val titleValid = title.length >= 5
    val descriptionValid = description.length >= 20
    val categoryValid = category.isNotEmpty()
    // Si la categoría es "Servicios", no se necesitan imágenes.
    val imagesValid = if (categoryIsService) true else imageUris.isNotEmpty() && imageUris.size <= 5
    val needTextValid = needText.isNotEmpty() // campo obligatorio según tu diseño

    // Botón habilitado solo si TODO es válido
    val isFormValid =
        titleValid && descriptionValid && categoryValid && imagesValid && needTextValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1️⃣ TÍTULO
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título *") },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isNotEmpty() && !titleValid
        )
        Text(
            text = "${title.length} caracteres (mínimo 5)",
            color = if (title.isNotEmpty() && !titleValid)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(12.dp))

        // 2️⃣ DESCRIPCIÓN
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            isError = description.isNotEmpty() && !descriptionValid
        )
        Text(
            text = "${description.length} caracteres (mínimo 20)",
            color = if (description.isNotEmpty() && !descriptionValid)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(12.dp))

        // 3️⃣ CATEGORÍA (predefinida)
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
                    .fillMaxWidth(),
                isError = !categoryValid && title.isNotEmpty() // se marca cuando ya empezó a escribir
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
                            if (cat == "Servicios") {
                                imageUris = emptyList() // Limpiar imágenes si se elige "Servicios"
                            }
                        }
                    )
                }
            }
        }
        if (!categoryValid && title.isNotEmpty()) {
            Text(
                text = "Debe seleccionar una categoría para la publicación",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(Modifier.height(12.dp))

        // 4️⃣ TEXTO DE LO QUE PIDE A CAMBIO
        OutlinedTextField(
            value = needText,
            onValueChange = { needText = it },
            label = { Text("¿Qué pides a cambio? *") },
            modifier = Modifier.fillMaxWidth(),
            isError = needText.isEmpty() && (title.isNotEmpty() || description.isNotEmpty())
        )

        Spacer(Modifier.height(16.dp))

        // 5️⃣ SUBIR IMÁGENES (si no es "Servicios")
        if (!categoryIsService) {
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
                Text(text = "Subir imágenes (1 a 5)")
            }

            // Mensaje de validación para imágenes
            val imagesMessage = when {
                imageUris.isEmpty() -> "Debe subir al menos una foto (máximo 5)."
                imageUris.size > 5 -> "Se permiten máximo 5 fotos; se usarán solo las primeras."
                else -> null
            }
            if (imagesMessage != null) {
                Text(
                    text = imagesMessage,
                    color = if (imageUris.isEmpty())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }

            // Mostrar imágenes seleccionadas
            if (imageUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUris) { uri ->
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(100.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageUris = imageUris - uri },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Eliminar imagen",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 6️⃣ BOTÓN PUBLICAR
        Button(
            onClick = {
                // Delegamos la lógica de creación y validación al ViewModel
                vm.publishOffer(
                    title = title,
                    offerText = description,
                    needText = needText,
                    category = category,
                    imageUris = imageUris.take(5) // `take` no da error si la lista es más pequeña
                )

                // Limpiar campos locales del formulario
                title = ""
                description = ""
                category = ""
                needText = ""
                imageUris = emptyList()

                onSuccess()
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Publicar oferta")
        }
    }
}
