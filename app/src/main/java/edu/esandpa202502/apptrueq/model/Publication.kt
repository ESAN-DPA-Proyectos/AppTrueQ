package edu.esandpa202502.apptrueq.model

import java.util.Date

data class Publication(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val imageUrl: String,
    val date: Date,
    val userId: String,
    val type: PublicationType, // "Offer" or "Need"
    val needText: String = "" // Texto de la necesidad asociada
)

enum class PublicationType {
    OFFER,
    NEED
}
