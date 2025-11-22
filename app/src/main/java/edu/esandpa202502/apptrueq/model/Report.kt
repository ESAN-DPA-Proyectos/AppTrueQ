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
    @ServerTimestamp
    val createdAt: Timestamp? = null
)