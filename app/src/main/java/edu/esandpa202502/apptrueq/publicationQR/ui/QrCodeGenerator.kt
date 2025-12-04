package edu.esandpa202502.apptrueq.publicationQR.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import edu.esandpa202502.apptrueq.publicationQR.viewmodel.PublicationQRViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun QrCodeGenerator(publicationId: String, viewModel: PublicationQRViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val qrContent = "trueq.app/pub/$publicationId"
    val qrCodeBitmap = remember(qrContent) {
        generateQrCode(qrContent)
    }

    // El Ojo que Todo lo Ve: Registra la generación del QR.
    LaunchedEffect(key1 = publicationId) {
        viewModel.trackQrCodeGeneration(publicationId)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (qrCodeBitmap != null) {
            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "Código QR de la publicación",
                modifier = Modifier.size(250.dp)
            )
        } else {
            Text(text = "Error al generar el código QR.")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            qrCodeBitmap?.let { saveQrCodeToGallery(context, it, publicationId) }
        }) {
            Text(text = "Guardar Imagen")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { 
            qrCodeBitmap?.let { shareQrCode(context, it, publicationId) }
        }) {
            Text(text = "Compartir")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDismiss) {
            Text(text = "Volver")
        }
    }
}

private fun generateQrCode(content: String): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun saveQrCodeToGallery(context: Context, bitmap: Bitmap, publicationId: String) {
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val imageName = "QR_TrueQ_$publicationId.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
    }

    var outputStream: OutputStream? = null
    try {
        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(collection, contentValues)
        outputStream = imageUri?.let { contentResolver.openOutputStream(it) }
        outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        Toast.makeText(context, "¡Sello QR guardado en la galería!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error al guardar el Sello QR.", Toast.LENGTH_SHORT).show()
    } finally {
        outputStream?.close()
    }
}

private fun shareQrCode(context: Context, bitmap: Bitmap, publicationId: String) {
    val imageFolder = File(context.cacheDir, "images")
    var fileOutputStream: FileOutputStream? = null
    try {
        imageFolder.mkdirs()
        val file = File(imageFolder, "QR_TrueQ_$publicationId.jpg")
        fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_SUBJECT, "¡Mira esta publicación en AppTrueQ!")
            putExtra(Intent.EXTRA_TEXT, "Escanea este código QR para ver la publicación: trueq.app/pub/$publicationId")
        }
        context.startActivity(Intent.createChooser(intent, "Compartir Sello QR"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error al compartir el Sello QR.", Toast.LENGTH_SHORT).show()
    } finally {
        fileOutputStream?.close()
    }
}
