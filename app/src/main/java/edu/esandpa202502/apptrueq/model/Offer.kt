package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa una oferta que un usuario hace para una necesidad específica.
 */
data class Offer(
    @DocumentId
    val id: String = "",
    val needId: String = "", // ID del documento `Need` al que responde esta oferta. CRÍTICO.
    val needOwnerId: String = "", // ID de a QUIEN se le hace la oferta (dueño de la necesidad).
    val ownerId: String = "",     // ID de quien HACE la oferta.
    val ownerName: String = "", // Nombre de quien HACE la oferta (para la UI).
    val needText: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val photos: List<String> = emptyList(),
    val status: String = "PENDIENTE", // PENDIENTE, ACEPTADA, RECHAZADA
    @ServerTimestamp
    val createdAt: Date? = null
)