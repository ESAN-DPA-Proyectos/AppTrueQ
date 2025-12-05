package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa un trueque confirmado en la colección `trades`.
 * Se crea cuando una propuesta es aceptada.
 * SOLUCIÓN: Se añaden todos los campos necesarios para que coincida con la lógica
 * del repositorio y poder mostrar un historial de trueques completo y detallado.
 */
data class Trade(
    @DocumentId
    val id: String = "",
    val proposalId: String = "",
    val publicationId: String = "",
    val publicationTitle: String = "",
    val offerentId: String = "",
    val offerentName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val status: String = "propuesto", // Estados: propuesto, aceptado, rechazado, completado
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
)
