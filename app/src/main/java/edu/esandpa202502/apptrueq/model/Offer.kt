package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp

data class Offer(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",      // nombre del usuario que publica
    val category: String = "",
    val title: String = "",
    val offerText: String = "",      // antes “description”
    val needText: String = "",       // lo que desea recibir a cambio
    val photos: List<String> = emptyList(),
    val status: String = "ACTIVE",
    val createdAt: Timestamp? = null
)
