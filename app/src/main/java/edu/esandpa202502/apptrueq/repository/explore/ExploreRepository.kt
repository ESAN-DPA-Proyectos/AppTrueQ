package edu.esandpa202502.apptrueq.repository.explore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class ExploreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPublications(): List<Publication> = coroutineScope {
        try {
            // -------- CONSULTAS EN PARALELO --------
            val offersDeferred = async {
                db.collection("offers")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            val needsDeferred = async {
                db.collection("needs")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            val offersSnapshot = offersDeferred.await()
            val needsSnapshot = needsDeferred.await()

            // -------- MAPEO: OFFER -> PUBLICATION --------
            val offerPublications = offersSnapshot.documents.mapNotNull { document ->
                val offer = document.toObject(Offer::class.java) ?: return@mapNotNull null
                val createdAt = offer.createdAt?.toDate() ?: return@mapNotNull null

                Publication(
                    id = document.id,
                    title = offer.title,
                    description = offer.offerText,          // <- ya no se usa description viejo
                    category = offer.category,
                    location = "",
                    imageUrl = offer.photos.firstOrNull() ?: "",
                    date = createdAt,
                    userId = offer.ownerId,
                    type = PublicationType.OFFER,
                    needText = offer.needText              // texto de lo que busca a cambio
                )
            }

            // -------- MAPEO: NEED -> PUBLICATION --------
            val needPublications = needsSnapshot.documents.mapNotNull { document ->
                val need = document.toObject(Need::class.java) ?: return@mapNotNull null
                val createdAt = need.createdAt?.toDate() ?: return@mapNotNull null

                val text = need.needText

                Publication(
                    id = document.id,
                    title = text.take(40) + "...",
                    description = text,                    // <- usamos need.needText
                    category = need.category,
                    location = "",
                    imageUrl = "",
                    date = createdAt,
                    userId = need.ownerId,
                    type = PublicationType.NEED,
                    needText = text
                )
            }

            // -------- UNIR Y ORDENAR --------
            (offerPublications + needPublications)
                .sortedByDescending { it.date }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
