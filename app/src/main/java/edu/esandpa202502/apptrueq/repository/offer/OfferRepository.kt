package edu.esandpa202502.apptrueq.repository.offer

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.tasks.await

class OfferRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Función para obtener TODAS las ofertas (para la pantalla "Explorar")
    suspend fun getAllOffers(): List<Offer> {
        return db.collection("offers").get().await().toObjects(Offer::class.java)
    }

    // Nueva función optimizada para obtener solo las ofertas del usuario actual
    suspend fun getMyOffers(userId: String): List<Offer> {
        return db.collection("offers")
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
            .toObjects(Offer::class.java)
    }

    suspend fun addOffer(offer: Offer, imageUris: List<Uri>): Offer {
        val imageUrls = imageUris.map { uri ->
            val imageRef = storage.reference.child("offer/${uri.lastPathSegment}")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        }
        val newOffer = offer.copy(photos = imageUrls)
        val docRef = db.collection("offers").add(newOffer).await()
        return newOffer.copy(id = docRef.id)
    }
}
