package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa una propuesta hecha por un usuario sobre una publicación.
 * Se almacena en la colección `proposals`.
 */
data class Proposal(
    @DocumentId
    val id: String = "",

    // --- Datos de la publicación original sobre la que se hace la propuesta ---
    val publicationId: String = "",
    val publicationOwnerId: String = "",
    val publicationTitle: String = "",

    // --- Datos del usuario que hace la propuesta ---
    val proposerId: String = "",
    val proposerName: String = "",

    // --- Contenido de la propuesta ---
    val proposalText: String = "", // Un comentario o mensaje adicional
    val status: String = "PENDIENTE", // PENDIENTE, ACEPTADA, RECHAZADA

    // --- Detalles de lo que se ofrece a cambio (puede ser una de las dos opciones) ---

    // Opción A: Se ofrece una publicación ya existente
    val offeredPublicationId: String? = null,

    // Opción B: Se ofrece un item nuevo creado en el momento
    val offeredItemTitle: String? = null,
    val offeredItemDescription: String? = null,
    val offeredItemImageUrl: String? = null,

    @ServerTimestamp
    val createdAt: Timestamp? = null
)
