package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Offer(
    @DocumentId
    val id: String = "",
    val category: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val description: String = "",
    val needText: String = "",
    val ownerId: String = "",
    val photos: List<String> = emptyList(),
    val status: String = "",
    val title: String = ""
)