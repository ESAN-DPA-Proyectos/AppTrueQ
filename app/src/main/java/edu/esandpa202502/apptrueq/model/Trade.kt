package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa un trueque confirmado en la colección `trades`.
 * SOLUCIÓN DEFINITIVA: Se actualiza el modelo para que incluya todos los campos necesarios
 * que el resto de la aplicación (repositorios y vistas) ya esperan. 
 * Esto soluciona el error `Unresolved reference`.
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
    val status: String = "propuesto",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    var updatedAt: Date? = null
)
