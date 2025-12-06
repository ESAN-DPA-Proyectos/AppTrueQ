package edu.esandpa202502.apptrueq.publicationQR.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repositorio dedicado a obtener las publicaciones del usuario actual para la función de compartir QR.
 */
class PublicationQRRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getMyPublications(): List<Publication> = coroutineScope {
        val userId = auth.currentUser?.uid ?: return@coroutineScope emptyList()

        try {
            val offersDeferred = async {
                db.collection("offers")
                    .whereEqualTo("ownerId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            val needsDeferred = async {
                db.collection("needs")
                    .whereEqualTo("ownerId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            val offerPublications = offersDeferred.await().documents.mapNotNull { doc ->
                val offer = doc.toObject(edu.esandpa202502.apptrueq.model.Offer::class.java)
                offer?.let {
                    Publication(
                        id = doc.id,
                        title = it.title,
                        description = it.offerText,
                        category = it.category,
                        location = "", // SOLUCIÓN: Parámetro añadido
                        imageUrl = it.photos.firstOrNull() ?: "",
                        date = it.createdAt?.toDate() ?: Date(),
                        userId = it.ownerId,
                        type = PublicationType.OFFER
                    )
                }
            }

            val needPublications = needsDeferred.await().documents.mapNotNull { doc ->
                val need = doc.toObject(edu.esandpa202502.apptrueq.model.Need::class.java)
                need?.let {
                    val text = it.needText
                    Publication(
                        id = doc.id,
                        title = text.take(40) + if (text.length > 40) "..." else "",
                        description = text,
                        category = it.category,
                        location = "", // SOLUCIÓN: Parámetro añadido
                        imageUrl = "",
                        date = it.createdAt?.toDate() ?: Date(),
                        userId = it.ownerId,
                        type = PublicationType.NEED
                    )
                }
            }

            (offerPublications + needPublications).sortedByDescending { it.date }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
