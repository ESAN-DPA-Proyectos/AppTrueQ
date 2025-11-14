package edu.esandpa202502.apptrueq.model

import java.util.Date

data class Need(
    val id: Int,
    val description: String,
    val category: String,
    val status: String,
    val createdAt: Date = Date()
)
