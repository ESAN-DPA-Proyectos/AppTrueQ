package edu.esandpa202502.apptrueq.offer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.offer.viewmodel.OfferViewModel
import edu.esandpa202502.apptrueq.model.Offer
import androidx.compose.runtime.collectAsState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferListScreen(vm: OfferViewModel) {
    val offersFromVm by vm.offers.collectAsState()
    val simulatedOffers = listOf(
        Offer(id = 1, titulo = "Silla ergonómica", descripcion = "En buen estado, color negro ajustable. Ideal para oficina o estudio.", categoria = "Hogar", imagenUrl = "https://via.placeholder.com/600x400?text=Silla"),
        Offer(id = 2, titulo = "Libro Kotlin Avanzado", descripcion = "Excelente referencia para Android Developers, casi nuevo.", categoria = "Libros", imagenUrl = "https://via.placeholder.com/600x400?text=Kotlin+Avanzado"),
        Offer(id = 3, titulo = "Cafetera Philips", descripcion = "Automática, poco uso, ideal para oficina o casa.", categoria = "Servicios", imagenUrl = "https://via.placeholder.com/600x400?text=Cafetera")
    )
    val allOffers = remember(offersFromVm) { simulatedOffers + offersFromVm }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas las categorías") }
    val categories = listOf("Todas las categorías", "Hogar", "Libros", "Servicios", "Tecnología")

    val filteredOffers = allOffers.filter {
        val matchesCategory = selectedCategory == "Todas las categorías" || it.categoria == selectedCategory
        val matchesSearch = it.titulo.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mis Publicaciones",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

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

@Composable
fun OfferCard(offer: Offer) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
    sdf.timeZone = TimeZone.getTimeZone("America/Lima")
    val date = sdf.format(offer.createdAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(offer.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Surface(shape = MaterialTheme.shapes.small, color = Color.DarkGray) {
                    Text(offer.categoria, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.White)
                }
            }

            Text(date, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = rememberAsyncImagePainter(model = offer.imagenUrl),
                contentDescription = offer.titulo,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(offer.descripcion, fontSize = 14.sp)

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                CardButton(text = "Ver")
                CardButton(text = "Editar")
                CardButton(text = "Borrar")
            }
        }
    }
}

@Composable
private fun CardButton(text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        modifier = Modifier.defaultMinSize(minWidth = 90.dp)
    ) {
        Text(text.uppercase())
    }
}
