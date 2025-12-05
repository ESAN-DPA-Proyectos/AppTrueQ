package edu.esandpa202502.apptrueq.need.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.esandpa202502.apptrueq.model.Need
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NeedCard(
    need: Need,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Formateo de fecha con estándar regional
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
    sdf.timeZone = TimeZone.getTimeZone("America/Lima")

    val dateText = need.createdAt?.toDate()?.let { sdf.format(it) } ?: "Fecha no disponible"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Row superior: categoría + fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Categoría (si existe)
                if (need.category.isNotEmpty()) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = need.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            // Texto principal de la necesidad
            Text(
                need.needText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(10.dp))

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                CardActionButton("Ver", onView)
                CardActionButton("Editar", onEdit)
                CardActionButton("Borrar", onDelete)
            }
        }
    }
}

@Composable
private fun CardActionButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text.uppercase())
    }
}


