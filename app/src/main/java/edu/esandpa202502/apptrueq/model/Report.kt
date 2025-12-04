package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa un documento en la colección `reports` (HU-10).
 */
data class Report(
    val id: String = "",
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val reportingUserId: String = "",
    val reason: String = "",
    val description: String = "",
    // HU-10: Se añade el campo de estado para el reporte.
    val status: String = "Pendiente de revisión", // Por defecto, todo reporte nuevo está pendiente.
    @ServerTimestamp
    val createdAt: Timestamp? = null
)