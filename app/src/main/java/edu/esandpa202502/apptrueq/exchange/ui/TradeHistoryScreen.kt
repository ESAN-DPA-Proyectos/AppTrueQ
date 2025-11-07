package edu.esandpa202502.apptrueq.exchange.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data class (clase de datos) para representar un solo elemento en el historial de trueques.
 * Usar una data class es una forma moderna y concisa en Kotlin para guardar datos.
 * Más adelante, cuando conectemos a Firebase, este modelo representará un "documento" de la base de datos.
 */
data class HistorialTrueque(
    val id: String, // Usaremos un ID para identificar cada trueque de forma única
    val titulo: String,
    val nombreUsuario: String,
    val estado: String, // Puede ser "Aceptado", "Rechazado", "Pendiente"
    val ultimaActualizacion: String,
    val imagenUsuario: Int // Por ahora, usamos un ID de recurso drawable. En el futuro, será una URL de Firebase.
)

@OptIn(ExperimentalMaterial3Api::class) // Necesario para usar componentes de Material 3 como Scaffold y TopAppBar
@Composable
fun TradeHistoryScreen() {

    // --- DATOS DE EJEMPLO ---
    // Creamos una lista de datos falsos para mostrar en la pantalla.
    // Cuando conectemos a Firebase, esta lista vendrá de la base de datos.
    val listaDeTrueques = listOf(
        HistorialTrueque(
            id = "1",
            titulo = "Necesito libro de Phyton 3.07",
            nombreUsuario = "Juan Rodriguez",
            estado = "Aceptado",
            ultimaActualizacion = "25/09/2025 - 10:25 pm",
            imagenUsuario = android.R.drawable.ic_dialog_info // Ícono de ejemplo
        ),
        HistorialTrueque(
            id = "2",
            titulo = "Ofrezco Laptop Apple i9",
            nombreUsuario = "Abigail Gutierrez",
            estado = "Rechazado",
            ultimaActualizacion = "23/09/2025 - 7:07 am",
            imagenUsuario = android.R.drawable.ic_dialog_info
        ),
        HistorialTrueque(
            id = "3",
            titulo = "Necesito bicicleta para niño",
            nombreUsuario = "Raul Romero",
            estado = "Pendiente",
            ultimaActualizacion = "21/09/2025 - 10:07 am",
            imagenUsuario = android.R.drawable.ic_dialog_info
        )
    )

    // --- ESTRUCTURA DE LA PANTALLA ---
    // Scaffold es un componente de Material Design que nos da una estructura básica de pantalla
    // (barra superior, contenido, botón flotante, etc.). Es como el esqueleto de la pantalla.
    Scaffold(
        topBar = {
            // La barra de navegación superior
            TopAppBar(
                title = { Text("Historial de Trueques") },
                navigationIcon = {
                    // El ícono para volver atrás
                    IconButton(onClick = { /* TODO: Implementar la navegación hacia atrás */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues -> // `paddingValues` contiene el espacio que ocupa la TopAppBar

        // Column organiza los elementos verticalmente, uno debajo del otro.
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el espacio disponible
                .padding(paddingValues) // Aplica el padding para no solaparse con la TopAppBar
                .padding(16.dp) // Añade un padding general a los bordes de la pantalla
        ) {
            // Fila para los filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre los filtros
            ) {
                // Usamos un TextField que parece un menú desplegable, como en la imagen.
                // El `weight(1f)` hace que ambos filtros ocupen el mismo espacio.
                FilterDropdown(text = "Seleccione estado", modifier = Modifier.weight(1f))
                FilterDropdown(text = "Fecha", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp)) // Un espacio vertical entre los filtros y la lista

            // --- LISTA DE TRUEQUES ---
            // `LazyColumn` es una lista optimizada. Solo renderiza los elementos que son visibles
            // en la pantalla, lo que es muy eficiente para listas largas.
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre cada tarjeta
            ) {
                // `items` es una función de LazyColumn que recorre nuestra lista de datos.
                items(listaDeTrueques) { trueque ->
                    // `trueque` es cada uno de los elementos de `listaDeTrueques` en cada iteración.
                    TradeHistoryCard(historialTrueque = trueque)
                }
            }
        }
    }
}

/**
 * Composable para la tarjeta individual del historial.
 * Dividir la UI en componentes más pequeños (como esta tarjeta) hace el código más limpio y reutilizable.
 */
@Composable
fun TradeHistoryCard(historialTrueque: HistorialTrueque) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título del trueque
            Text(text = "\"${historialTrueque.titulo}\"", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            // Fila para la imagen y nombre de usuario
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = historialTrueque.imagenUsuario),
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape) // Hace la imagen redonda
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = historialTrueque.nombreUsuario)
            }

            // Estado y fecha
            Text(text = "Estado: \"${historialTrueque.estado}\"")
            Text(text = "Última actualización: ${historialTrueque.ultimaActualizacion}", fontSize = 12.sp, color = Color.Gray)

            // Botón para ver el detalle
            Button(
                onClick = { /* TODO: Navegar a la pantalla de detalle del trueque */ },
                modifier = Modifier.align(Alignment.End), // Alinea el botón a la derecha
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Ver Detalle", color = Color.White)
            }
        }
    }
}

/**
 * Composable para simular un campo de filtro desplegable.
 */
@Composable
fun FilterDropdown(text: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = text,
        onValueChange = {}, // No hace nada por ahora
        readOnly = true, // Para que el usuario no pueda escribir en él
        modifier = modifier,
        trailingIcon = { // Ícono al final del campo de texto
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
        }
    )
}


// --- VISTA PREVIA ---
// El @Preview nos permite ver cómo se ve nuestro Composable directamente en Android Studio
// sin necesidad de ejecutar la aplicación en el emulador. Es muy útil para agilizar el desarrollo de la UI.
@Preview(showBackground = true)
@Composable
fun TradeHistoryScreenPreview() {
    TradeHistoryScreen()
}
