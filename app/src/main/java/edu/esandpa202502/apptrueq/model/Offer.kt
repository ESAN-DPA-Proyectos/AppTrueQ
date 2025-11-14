package edu.esandpa202502.apptrueq.model

import java.util.Date

data class Offer(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val imagenUrl: String,
    val createdAt: Date = Date()
)
