package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa un documento en la colección `notifications`.
 */
data class NotificationItem(
    val id: String = "",
    val userId: String = "", // A quién se le muestra la notificación
    val title: String = "",
    val message: String = "",
    val type: String = "", // Ej: "offer_accepted", "offer_rejected", "confirmation"
    val referenceId: String = "", // ID del documento relacionado (oferta, necesidad, etc.)
    var isRead: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)