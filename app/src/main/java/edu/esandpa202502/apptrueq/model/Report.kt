package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp

data class Report(
    val id: String = "",
    val publicationId: String = "", 
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val reportingUserId: String = "",
    val reportingUserName: String = "", // Añadido
    val reason: String = "",
    val description: String = "",
    val status: String = "Pendiente de revisión",
    val createdAt: Timestamp? = null
)
