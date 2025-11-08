package edu.esandpa202502.apptrueq.model

/**
 * Representa una oferta publicada por un usuario.
 * Este modelo es parte de la capa de dominio (domain/model)
 */
data class Offer(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val imagenUrl: String
)
