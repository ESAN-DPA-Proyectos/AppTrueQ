package edu.esandpa202502.apptrueq.offer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.model.Offer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun OfferCard(
    offer: Offer,
    onViewClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    // Formateo de fecha
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE")).apply {
        timeZone = TimeZone.getTimeZone("America/Lima")
    }
    val dateText = offer.createdAt?.toDate()?.let { sdf.format(it) } ?: "Fecha no disponible"

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            // Imagen principal (primera foto)
            if (!offer.photos.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(offer.photos.first()),
                    contentDescription = "Imagen de la oferta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Fila categoría + estado + fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Categoría
                if (offer.category.isNotBlank()) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = offer.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Estado (ACTIVO / INACTIVO) usando colores del tema
                if (!offer.status.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))

                    val statusLower = offer.status.trim().lowercase(Locale.ROOT)

                    val statusLabel = when (statusLower) {
                        "active", "activo" -> "ACTIVO"
                        "inactive", "inactivo" -> "INACTIVO"
                        else -> offer.status.uppercase()
                    }

                    val (statusColor, statusTextColor) = when (statusLower) {
                        // similar al chip de categoría pero otro color del tema
                        "active", "activo" ->
                            MaterialTheme.colorScheme.secondaryContainer to
                                    MaterialTheme.colorScheme.onSecondaryContainer

                        "inactive", "inactivo" ->
                            MaterialTheme.colorScheme.errorContainer to
                                    MaterialTheme.colorScheme.onErrorContainer

                        else ->
                            MaterialTheme.colorScheme.surfaceVariant to
                                    MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = statusColor
                    ) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = statusTextColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Fecha alineada a la derecha
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = offer.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Texto "Necesita a cambio"
            if (offer.needText.isNotBlank()) {
                Text(
                    text = "Necesita a cambio: ${offer.needText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CardButton(text = "Ver", onClick = onViewClick)
                CardButton(text = "Editar", onClick = onEditClick)
                CardButton(text = "Eliminar", onClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun CardButton(text: String, onClick: () -> Unit = {}) {
    TextButton(onClick = onClick) {
        Text(text.uppercase())
    }
}
