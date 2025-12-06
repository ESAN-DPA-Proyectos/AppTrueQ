package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa un documento en la colección `notifications`.
 */
data class NotificationItem(

    @DocumentId
    var id: String = "",

    val userId: String = "", // A quién se le muestra la notificación
    val title: String = "",
    val message: String = "",
    val type: String = "", // Ej: "offer_accepted", "offer_rejected", "confirmation"
    val referenceId: String = "", // ID del documento relacionado (oferta, necesidad, etc.)

    @JvmField
    var isRead: Boolean = false,

    @ServerTimestamp
    val createdAt: Timestamp? = null
)
