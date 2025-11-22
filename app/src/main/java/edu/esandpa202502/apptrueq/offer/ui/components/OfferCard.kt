package edu.esandpa202502.apptrueq.offer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import edu.esandpa202502.apptrueq.model.Offer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OfferCard(offer: Offer) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
    sdf.timeZone = TimeZone.getTimeZone("America/Lima")
    val date = offer.createdAt?.let { sdf.format(it) } ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                if (offer.category.isNotEmpty()) {
                    Surface(shape = MaterialTheme.shapes.small, color = Color.DarkGray) {
                        Text(offer.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            Text(date, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(10.dp))

            if (offer.photos.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = offer.photos[0]),
                    contentDescription = offer.title,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(offer.description, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Text("Busco: ${offer.needText}", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

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
