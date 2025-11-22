package edu.esandpa202502.apptrueq.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun getNeeds(): List<Need> {
        return db.collection("needs").get().await().toObjects(Need::class.java)
    }

    suspend fun getOffers(): List<Offer> {
        return db.collection("offers").get().await().toObjects(Offer::class.java)
    }

    suspend fun addNeed(need: Need) {
        db.collection("needs").add(need).await()
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