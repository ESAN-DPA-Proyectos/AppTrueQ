package edu.esandpa202502.apptrueq.offer.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferFormScreen(
    vm: OfferViewModel,
    onSuccess: () -> Unit = {}
) {
    // Campos del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()

    // Lanzador para elegir imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Validación de campos
    val isValid = title.length >= 5 && description.length >= 20 && category.isNotEmpty()

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
            color = Color.Gray,
            fontSize = 12.sp,
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
            color = Color.Gray,
            fontSize = 12.sp,
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

        Spacer(Modifier.height(16.dp))

        // Botón para subir imagen
        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Icon(
                imageVector = Icons.Filled.Upload,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Subir una imagen", color = Color.White)
        }

        // Mostrar imagen seleccionada
        imageUri?.let { uri ->
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Botón de publicar
        Button(
            onClick = {
                vm.addOffer(
                    titulo = title,
                    descripcion = description,
                    categoria = category,
                    imagenUrl = imageUri?.toString()
                        ?: "https://via.placeholder.com/600x400?text=${title.replace(" ", "+")}"
                )
                // Limpia campos
                title = ""
                description = ""
                category = ""
                imageUri = null
                onSuccess()
            },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Publicar Oferta", color = Color.White)
        }
    }
}
