package edu.esandpa202502.apptrueq.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Need(
    @DocumentId
    val id: String = "",
    val category: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val ownerId: String = "",
    val status: String = "",
    val text: String = ""
)