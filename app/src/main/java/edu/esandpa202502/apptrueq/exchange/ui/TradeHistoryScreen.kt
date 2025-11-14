package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

data class HistorialTrueque(
    val id: String,
    val titulo: String,
    val nombreUsuario: String,
    val estado: String,
    val ultimaActualizacion: String,
    val imagenUsuario: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeHistoryScreen(navController: NavController) {
    // --- DATOS DE EJEMPLO ---
    val listaDeTrueques = listOf(
        HistorialTrueque("1", "Necesito libro de Phyton 3.07", "Juan Rodriguez", "Aceptado", "25/09/2025 - 10:25 pm", android.R.drawable.ic_dialog_info),
        HistorialTrueque("2", "Ofrezco Laptop Apple i9", "Abigail Gutierrez", "Rechazado", "23/09/2025 - 07:07 am", android.R.drawable.ic_dialog_info),
        HistorialTrueque("3", "Necesito bicicleta para niño", "Raul Romero", "Pendiente", "21/09/2025 - 10:07 am", android.R.drawable.ic_dialog_info)
    )

    // --- ESTADOS PARA LOS FILTROS ---
    var estadoSeleccionado by remember { mutableStateOf("Todos") }
    var menuEstadoExpanded by remember { mutableStateOf(false) }
    val opcionesEstado = listOf("Todos", "Aceptado", "Rechazado", "Pendiente")

    var fechaSeleccionada by remember { mutableStateOf("Más recientes") }
    var menuFechaExpanded by remember { mutableStateOf(false) }
    val opcionesFecha = listOf("Más recientes", "Más antiguos")

    val truequesFiltrados = remember(estadoSeleccionado, fechaSeleccionada) {
        val listaFiltradaPorEstado = if (estadoSeleccionado == "Todos") {
            listaDeTrueques
        } else {
            listaDeTrueques.filter { it.estado == estadoSeleccionado }
        }

        // CORRECCIÓN FINAL: Se usa un DateTimeFormatterBuilder para ignorar mayúsculas/minúsculas en "am/pm".
        val formatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd/MM/yyyy - h:mm a")
            .toFormatter(Locale.ENGLISH)

        try {
            when (fechaSeleccionada) {
                "Más antiguos" -> listaFiltradaPorEstado.sortedBy { LocalDateTime.parse(it.ultimaActualizacion, formatter) }
                else -> listaFiltradaPorEstado.sortedByDescending { LocalDateTime.parse(it.ultimaActualizacion, formatter) }
            }
        } catch (e: Exception) {
            // Si algo sale mal, devolvemos la lista sin ordenar para evitar que la app se cierre.
            println("Error al parsear fecha: ${e.message}")
            listaFiltradaPorEstado
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Trueques") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = menuEstadoExpanded,
                    onExpandedChange = { menuEstadoExpanded = !menuEstadoExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = estadoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEstadoExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = menuEstadoExpanded,
                        onDismissRequest = { menuEstadoExpanded = false }
                    ) {
                        opcionesEstado.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    estadoSeleccionado = opcion
                                    menuEstadoExpanded = false
                                }
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = menuFechaExpanded,
                    onExpandedChange = { menuFechaExpanded = !menuFechaExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = fechaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuFechaExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = menuFechaExpanded,
                        onDismissRequest = { menuFechaExpanded = false }
                    ) {
                        opcionesFecha.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    fechaSeleccionada = opcion
                                    menuFechaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(truequesFiltrados) { trueque ->
                    TradeHistoryCard(
                        historialTrueque = trueque,
                        onVerDetalleClick = {
                            navController.navigate("trade_detail/${trueque.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TradeHistoryCard(historialTrueque: HistorialTrueque, onVerDetalleClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "\"${historialTrueque.titulo}\"", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = historialTrueque.imagenUsuario),
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = historialTrueque.nombreUsuario)
            }

            Text(text = "Estado: \"${historialTrueque.estado}\"")
            Text(text = "Última actualización: ${historialTrueque.ultimaActualizacion}", fontSize = 12.sp, color = Color.Gray)

            Button(
                onClick = onVerDetalleClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Ver Detalle", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TradeHistoryScreenPreview() {
    TradeHistoryScreen(navController = NavController(LocalContext.current))
}