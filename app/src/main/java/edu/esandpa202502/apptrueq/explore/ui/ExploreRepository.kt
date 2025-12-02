package edu.esandpa202502.apptrueq.explore.ui

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.Date

class ExploreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPublications(): List<Publication> = coroutineScope {
        try {
            val offersDeferred = async {
                db.collection("offers")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            val needsDeferred = async {
                db.collection("needs")
                    .whereEqualTo("status", "ACTIVE")
                    .get()
                    .await()
            }

            val offersSnapshot = offersDeferred.await()
            val needsSnapshot = needsDeferred.await()

            val offerPublications = offersSnapshot.documents.mapNotNull { document ->
                val offer = document.toObject(Offer::class.java) ?: return@mapNotNull null

                val createdAt = offer.createdAt ?: return@mapNotNull null
                val title = offer.title ?: return@mapNotNull null
                val ownerId = offer.ownerId ?: return@mapNotNull null

                Publication(
                    id = document.id,
                    title = title,
                    description = offer.description ?: "",
                    category = offer.category ?: "Sin categoría",
                    location = "",
                    imageUrl = offer.photos.firstOrNull() ?: "",
                    date = createdAt.toDate(),
                    userId = ownerId,
                    type = PublicationType.OFFER,
                    needText = offer.needText ?: ""
                )
            }

            val needPublications = needsSnapshot.documents.mapNotNull { document ->
                val need = document.toObject(Need::class.java) ?: return@mapNotNull null
                
                val createdAt = need.createdAt ?: return@mapNotNull null
                val text = need.text ?: return@mapNotNull null
                val ownerId = need.ownerId ?: return@mapNotNull null

                Publication(
                    id = document.id,
                    title = text.take(50) + "...",
                    description = text,
                    category = need.category ?: "Sin categoría",
                    location = "",
                    imageUrl = "",
                    date = createdAt.toDate(),
                    userId = ownerId,
                    type = PublicationType.NEED,
                    needText = ""
                )
            }

            (offerPublications + needPublications).sortedByDescending { it.date }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
