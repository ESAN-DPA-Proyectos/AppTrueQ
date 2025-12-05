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

    /**
     * Obtiene todas las publicaciones (ofertas + necesidades)
     * realizadas por un usuario.
     *
     * Se usa para pantallas de “Mis publicaciones”, reportes, etc.
     */
    suspend fun getPublicationsByUserId(userId: String): List<Publication> {
        val publications = mutableListOf<Publication>()

        // 1️⃣ Ofertas creadas por el usuario
        val offersSnapshot = db.collection("offers")
            .whereEqualTo("ownerId", userId)
            .get()
            .await()

        for (document in offersSnapshot.documents) {
            val offer = document.toObject(Offer::class.java)
            if (offer != null) {
                publications.add(
                    Publication(
                        id = document.id,
                        title = offer.title,
                        description = offer.offerText,
                        category = offer.category.ifBlank { "Sin categoría" },
                        location = "", // si luego agregas ubicación, la mapeas aquí
                        imageUrl = offer.photos.firstOrNull() ?: "",
                        date = offer.createdAt?.toDate() ?: Date(),
                        userId = offer.ownerId,
                        type = PublicationType.OFFER,
                        needText = offer.needText
                    )
                )
            }
        }

        // 2️⃣ Necesidades creadas por el usuario
        val needsSnapshot = db.collection("needs")
            .whereEqualTo("ownerId", userId)
            .get()
            .await()

        for (document in needsSnapshot.documents) {
            val need = document.toObject(Need::class.java)
            if (need != null) {
                publications.add(
                    Publication(
                        id = document.id,
                        title = need.needText,
                        description = need.needText,
                        category = need.category.ifBlank { "Sin categoría" },
                        location = "",
                        imageUrl = "",
                        date = need.createdAt?.toDate() ?: Date(),
                        userId = need.ownerId,
                        type = PublicationType.NEED,
                        needText = need.needText
                    )
                )
            }
        }

        // Ordenar por fecha descendente (más recientes primero)
        return publications.sortedByDescending { it.date }
    }

    /**
     * Obtiene TODAS las publicaciones de la plataforma
     * (útil para panel de moderador o reportes globales).
     */
    suspend fun getAllPublications(): List<Publication> {
        val publications = mutableListOf<Publication>()

        // Ofertas
        val offersSnapshot = db.collection("offers").get().await()
        for (document in offersSnapshot.documents) {
            val offer = document.toObject(Offer::class.java)
            if (offer != null) {
                publications.add(
                    Publication(
                        id = document.id,
                        title = offer.title,
                        description = offer.offerText,
                        category = offer.category.ifBlank { "Sin categoría" },
                        location = "",
                        imageUrl = offer.photos.firstOrNull() ?: "",
                        date = offer.createdAt?.toDate() ?: Date(),
                        userId = offer.ownerId,
                        type = PublicationType.OFFER,
                        needText = offer.needText
                    )
                )
            }
        }

        // Necesidades
        val needsSnapshot = db.collection("needs").get().await()
        for (document in needsSnapshot.documents) {
            val need = document.toObject(Need::class.java)
            if (need != null) {
                publications.add(
                    Publication(
                        id = document.id,
                        title = need.needText,
                        description = need.needText,
                        category = need.category.ifBlank { "Sin categoría" },
                        location = "",
                        imageUrl = "",
                        date = need.createdAt?.toDate() ?: Date(),
                        userId = need.ownerId,
                        type = PublicationType.NEED,
                        needText = need.needText
                    )
                )
            }
        }

        return publications.sortedByDescending { it.date }
    }

    /**
     * Obtiene una publicación (oferta o necesidad) por ID.
     * Se usa en pantallas de detalle.
     */
    suspend fun getPublicationById(id: String): Publication? {
        // Buscar primero en "offers"
        val offerDoc = db.collection("offers").document(id).get().await()
        val offer = offerDoc.toObject(Offer::class.java)
        if (offer != null) {
            return Publication(
                id = offerDoc.id,
                title = offer.title,
                description = offer.offerText,
                category = offer.category.ifBlank { "Sin categoría" },
                location = "",
                imageUrl = offer.photos.firstOrNull() ?: "",
                date = offer.createdAt?.toDate() ?: Date(),
                userId = offer.ownerId,
                type = PublicationType.OFFER,
                needText = offer.needText
            )
        }

        // Si no está en offers, buscar en "needs"
        val needDoc = db.collection("needs").document(id).get().await()
        val need = needDoc.toObject(Need::class.java)
        if (need != null) {
            return Publication(
                id = needDoc.id,
                title = need.needText,
                description = need.needText,
                category = need.category.ifBlank { "Sin categoría" },
                location = "",
                imageUrl = "",
                date = need.createdAt?.toDate() ?: Date(),
                userId = need.ownerId,
                type = PublicationType.NEED,
                needText = need.needText
            )
        }

        return null
    }
}
