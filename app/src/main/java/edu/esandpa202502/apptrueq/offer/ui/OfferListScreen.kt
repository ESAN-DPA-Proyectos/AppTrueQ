package edu.esandpa202502.apptrueq.offer.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.offer.ui.components.OfferCard
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferListScreen(
    vm: OfferViewModel
) {
    LaunchedEffect(Unit) {
        vm.loadMyOffers()
    }

    val offers: List<Offer> by vm.offers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }

    val categoryOptions = listOf(
        "Todas las categorías",
        "Hogar",
        "Libros",
        "Servicios",
        "Tecnología"
    )

    var offerToView by remember { mutableStateOf<Offer?>(null) }
    var offerToEdit by remember { mutableStateOf<Offer?>(null) }
    var offerToDelete by remember { mutableStateOf<Offer?>(null) }

    // NUEVO: imagen a pantalla completa
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    val filteredOffers = offers
        .filter { offer ->
            val matchesCategory =
                selectedCategory == "Todas las categorías" ||
                        offer.category == selectedCategory

            val matchesSearch =
                offer.title.contains(searchQuery, ignoreCase = true) ||
                        offer.offerText.contains(searchQuery, ignoreCase = true) ||
                        offer.needText.contains(searchQuery, ignoreCase = true)

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
            label = { Text("Buscar en mis ofertas...") },
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryOptions.forEach { cat ->
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

        Text(
            text = "Total: ${filteredOffers.size} publicación(es)",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredOffers) { offer ->
                OfferCard(
                    offer = offer,
                    onViewClick = { offerToView = offer },
                    onEditClick = { offerToEdit = offer },
                    onDeleteClick = { offerToDelete = offer }
                )
            }
        }
    }

    // VER
    offerToView?.let { offer ->
        OfferDetailDialog(
            offer = offer,
            onDismiss = { offerToView = null },
            onImageDoubleTap = { url -> fullScreenImageUrl = url }   // NUEVO
        )
    }

    // EDITAR (solo texto, SIN cambiar imágenes)
    offerToEdit?.let { offer ->
        OfferEditDialog(
            offer = offer,
            onDismiss = { offerToEdit = null },
            onSave = { updated, _ ->
                // segundo parámetro vacío porque ya no cambiamos imágenes
                vm.updateOffer(updated, emptyList())
                offerToEdit = null
            }
        )
    }

    // ELIMINAR
    offerToDelete?.let { offer ->
        AlertDialog(
            onDismissRequest = { offerToDelete = null },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Seguro que deseas eliminar la oferta \"${offer.title}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteOffer(offer.id)
                        offerToDelete = null
                    }
                ) {
                    Text("ELIMINAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { offerToDelete = null }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // DIALOGO DE IMAGEN A PANTALLA COMPLETA
    fullScreenImageUrl?.let { url ->
        FullScreenImageDialog(
            imageUrl = url,
            onDismiss = { fullScreenImageUrl = null }
        )
    }
}

/* ---------- Diálogo de detalle (VER) ---------- */

@Composable
private fun OfferDetailDialog(
    offer: Offer,
    onDismiss: () -> Unit,
    onImageDoubleTap: (String) -> Unit   // NUEVO
) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE")).apply {
        timeZone = TimeZone.getTimeZone("America/Lima")
    }
    val dateText = offer.createdAt?.toDate()?.let { sdf.format(it) } ?: "Fecha no disponible"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(offer.title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (offer.photos.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        items(offer.photos) { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(220.dp)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                onImageDoubleTap(url)   // doble toque → pantalla completa
                                            }
                                        )
                                    }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Text("Categoría: ${offer.category}", style = MaterialTheme.typography.bodyMedium)
                Text("Fecha: $dateText", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(8.dp))

                Text("Descripción:", style = MaterialTheme.typography.titleSmall)
                Text(offer.offerText, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(8.dp))

                Text("Pides a cambio:", style = MaterialTheme.typography.titleSmall)
                Text(offer.needText, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CERRAR")
            }
        }
    )
}

/* ---------- Diálogo de edición (EDITAR, solo texto) ---------- */

@Composable
private fun OfferEditDialog(
    offer: Offer,
    onDismiss: () -> Unit,
    onSave: (Offer, List<Uri>) -> Unit
) {
    // Título solo lectura
    val title = offer.title

    var description by remember { mutableStateOf(offer.offerText) }
    var needText by remember { mutableStateOf(offer.needText) }

    val descriptionValid = description.length >= 20
    val needValid = needText.isNotBlank()

    // Ahora las imágenes NO se modifican: solo comprobamos que existan
    val hasAtLeastOnePhoto = offer.photos.isNotEmpty()

    val isValid = descriptionValid && needValid && hasAtLeastOnePhoto

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar publicación") },
        text = {
            Column {
                // Título solo lectura
                OutlinedTextField(
                    value = title,
                    onValueChange = {},
                    label = { Text("Título (no editable)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    readOnly = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = !descriptionValid && description.isNotEmpty()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = needText,
                    onValueChange = { needText = it },
                    label = { Text("¿Qué pides a cambio?") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !needValid && needText.isNotEmpty()
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Imágenes (máx. 5)",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))

                // SOLO vista de las imágenes actuales (sin botón para cambiarlas)
                if (offer.photos.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        items(offer.photos) { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }

                if (!hasAtLeastOnePhoto) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Debe haber al menos una foto.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updated = offer.copy(
                        // title se mantiene igual (no se modifica)
                        offerText = description,
                        needText = needText
                    )
                    onSave(updated, emptyList())   // no cambiamos imágenes
                },
                enabled = isValid
            ) {
                Text("GUARDAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}

/* ---------- Imagen a pantalla completa ---------- */

@Composable
private fun FullScreenImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() }, // toque simple para cerrar
                contentScale = ContentScale.Fit   // ajusta la imagen al tamaño del móvil
            )
        }
    }
}
