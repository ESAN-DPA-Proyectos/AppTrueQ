package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Define los posibles estados de un trueque.
 */
enum class TradeStatus {
    PENDING,    // Propuesta recién creada
    ACCEPTED,   // Propuesta aceptada, trueque en curso
    REJECTED,   // Propuesta rechazada
    COMPLETED,  // Trueque finalizado con éxito
    CANCELLED   // Trueque cancelado por una de las partes
}

/**
 * Representa un trueque confirmado en la colección `trades`.
 * Se crea cuando una propuesta es aceptada.
 */
data class Trade(
    @DocumentId
    val id: String = "",
    val proposalId: String = "", // ID de la propuesta original
    val publicationId: String = "",
    val publicationTitle: String = "",

    // --- Datos de las partes involucradas ---
    val offerentId: String = "", // El que hizo la propuesta
    val offerentName: String = "",
    val receiverId: String = "", // El dueño de la publicación
    val receiverName: String = "",

    val status: TradeStatus = TradeStatus.PENDING,

    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    var updatedAt: Timestamp? = null
)
