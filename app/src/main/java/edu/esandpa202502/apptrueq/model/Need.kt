package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Need(
    @DocumentId
    val id: String = "",
    val category: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val ownerId: String = "",
    val status: String = "",
    val text: String = ""
)