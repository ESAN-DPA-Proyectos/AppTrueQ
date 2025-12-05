package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Trade(
    @DocumentId
    val id: String = "",
    val publicationId: String = "",
    val offerentId: String = "",
    val receiverId: String = "",
    val status: String = "propuesto", // Estados: propuesto, aceptado, rechazado, completado
    @ServerTimestamp
    val createdAt: Date? = null
)
