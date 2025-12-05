package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp

data class Need(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",       // agregado, obligatorio para moderación/reportes
    val category: String = "",
    val needText: String = "",        // antes “text”
    val status: String = "ACTIVE",
    val createdAt: Timestamp? = null
)
