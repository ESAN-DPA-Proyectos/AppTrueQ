package edu.esandpa202502.apptrueq.repository.offer

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OfferRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Publicar una nueva oferta con subida de imágenes.
     */
    suspend fun addOffer(offer: Offer, imageUris: List<Uri>): Offer {
        val user = auth.currentUser ?: throw IllegalStateException("Usuario no autenticado.")

        // Subir imágenes
        val photoUrls = mutableListOf<String>()
        for (uri in imageUris) {
            val filePath = "offers/${user.uid}/${UUID.randomUUID()}"
            val ref = storage.reference.child(filePath)

            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            photoUrls.add(downloadUrl)
        }

        val docRef = db.collection("offers").document()

        val offerToSave = offer.copy(
            id = docRef.id,
            ownerId = user.uid,
            ownerName = user.displayName ?: "",
            photos = photoUrls,
            createdAt = Timestamp.now()
        )

        docRef.set(offerToSave).await()

        return offerToSave
    }

    /**
     * Obtener ofertas del usuario actual, ordenadas de más reciente a más antigua.
     */
    suspend fun getOffersByUser(userId: String): List<Offer> {
        return try {
            db.collection("offers")
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
                .toObjects(Offer::class.java)
                .sortedByDescending { it.createdAt?.toDate() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Actualizar oferta. Si se envían nuevas imágenes, se reemplazan las anteriores.
     * Si la lista de nuevas imágenes está vacía, se mantienen las fotos actuales.
     */
    suspend fun updateOffer(offer: Offer, newImageUris: List<Uri>): Offer {
        val user = auth.currentUser ?: throw IllegalStateException("Usuario no autenticado.")

        val finalPhotos: List<String> =
            if (newImageUris.isNotEmpty()) {
                val urls = mutableListOf<String>()
                for (uri in newImageUris) {
                    val filePath = "offers/${user.uid}/${UUID.randomUUID()}"
                    val ref = storage.reference.child(filePath)

                    ref.putFile(uri).await()
                    val downloadUrl = ref.downloadUrl.await().toString()
                    urls.add(downloadUrl)
                }
                urls
            } else {
                offer.photos
            }

        if (finalPhotos.isEmpty()) {
            throw IllegalArgumentException("La oferta debe tener al menos una foto.")
        }

        val updatedOffer = offer.copy(
            ownerId = user.uid,
            ownerName = user.displayName ?: "",
            photos = finalPhotos
        )

        db.collection("offers")
            .document(updatedOffer.id)
            .set(updatedOffer)
            .await()

        return updatedOffer
    }

    /**
     * Obtener una oferta por id.
     */
    suspend fun getOfferById(id: String): Offer? {
        return try {
            db.collection("offers")
                .document(id)
                .get()
                .await()
                .toObject(Offer::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Eliminar una oferta por id.
     */
    suspend fun deleteOffer(id: String) {
        db.collection("offers")
            .document(id)
            .delete()
            .await()
    }
}
