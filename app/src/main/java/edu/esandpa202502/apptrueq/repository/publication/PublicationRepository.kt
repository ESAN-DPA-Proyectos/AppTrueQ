package edu.esandpa202502.apptrueq.repository.publication

import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.tasks.await
import java.util.Date

class PublicationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPublicationsByUserId(userId: String): List<Publication> {
        val publications = mutableListOf<Publication>()

        // Buscar en la colección 'offers'
        val offersSnapshot = db.collection("offers").whereEqualTo("ownerId", userId).get().await()
        for (document in offersSnapshot.documents) {
            val offer = document.toObject(Offer::class.java)
            offer?.let { it ->
                publications.add(
                    Publication(
                        id = document.id,
                        title = it.title ?: "",
                        description = it.description ?: "",
                        category = it.category ?: "Sin categoría",
                        location = "",
                        imageUrl = it.photos.firstOrNull() ?: "",
                        date = it.createdAt?.toDate() ?: Date(), // CORREGIDO
                        userId = it.ownerId ?: "",
                        type = PublicationType.OFFER,
                        needText = it.needText ?: ""
                    )
                )
            }
        }

        // Buscar en la colección 'needs'
        val needsSnapshot = db.collection("needs").whereEqualTo("userId", userId).get().await()
        for (document in needsSnapshot.documents) {
            val need = document.toObject(Need::class.java)
            need?.let { it ->
                publications.add(
                    Publication(
                        id = document.id,
                        title = it.text ?: "",
                        description = it.text ?: "",
                        category = it.category ?: "Sin categoría",
                        location = "",
                        imageUrl = "",
                        date = it.createdAt?.toDate() ?: Date(), // CORREGIDO
                        userId = it.userId ?: "",
                        type = PublicationType.NEED,
                        needText = ""
                    )
                )
            }
        }

        return publications
    }

    suspend fun getPublicationById(publicationId: String): Publication? {
        var document = db.collection("offers").document(publicationId).get().await()
        if (document.exists()) {
            val offer = document.toObject(Offer::class.java)
            return offer?.let { it ->
                Publication(
                    id = document.id,
                    title = it.title ?: "",
                    description = it.description ?: "",
                    category = it.category ?: "Sin categoría",
                    location = "",
                    imageUrl = it.photos.firstOrNull() ?: "",
                    date = it.createdAt?.toDate() ?: Date(), // CORREGIDO
                    userId = it.ownerId ?: "",
                    type = PublicationType.OFFER,
                    needText = it.needText ?: ""
                )
            }
        }

        document = db.collection("needs").document(publicationId).get().await()
        if (document.exists()) {
            val need = document.toObject(Need::class.java)
            return need?.let { it ->
                Publication(
                    id = document.id,
                    title = it.text ?: "",
                    description = it.text ?: "",
                    category = it.category ?: "Sin categoría",
                    location = "",
                    imageUrl = "",
                    date = it.createdAt?.toDate() ?: Date(), // CORREGIDO
                    userId = it.userId ?: "",
                    type = PublicationType.NEED,
                    needText = ""
                )
            }
        }

        return null
    }
}
