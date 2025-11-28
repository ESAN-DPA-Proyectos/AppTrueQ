package edu.esandpa202502.apptrueq.repository.exchange

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import edu.esandpa202502.apptrueq.model.Need
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExchangeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // --- LÓGICA CORREGIDA PARA "MIS PUBLICACIONES" ---
    fun getMyOffers(userId: String): Flow<List<Offer>> = callbackFlow {
        val allNeedsListener = db.collection("needs").addSnapshotListener { allNeedsSnapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val allNeeds = allNeedsSnapshot?.toObjects(Need::class.java) ?: emptyList()
            if (allNeeds.isEmpty()) {
                trySend(emptyList()).isSuccess
                return@addSnapshotListener
            }

            val combinedOffers = mutableMapOf<String, Offer>()
            allNeeds.forEach { need ->
                if (need.id.isNotEmpty()) {
                    db.collection("needs").document(need.id).collection("offers")
                        .whereEqualTo("ownerId", userId) // Busca ofertas creadas por el usuario
                        .addSnapshotListener { offersSnapshot, _ ->
                            offersSnapshot?.documents?.forEach { doc ->
                                doc.toObject(Offer::class.java)?.let { offer -> combinedOffers[doc.id] = offer }
                            }
                            trySend(combinedOffers.values.toList()).isSuccess
                        }
                }
            }
        }
        awaitClose { allNeedsListener.remove() }
    }

    suspend fun addOfferAndNotify(offer: Offer, imageUris: List<Uri>): Offer {
        val imageUrls = imageUris.map { uri ->
            val imageRef = storage.reference.child("offer/${uri.lastPathSegment}")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        }
        val offerWithImages = offer.copy(photos = imageUrls)
        val batch = db.batch()
        val offerRef = db.collection("needs").document(offer.needId).collection("offers").document()
        val finalOffer = offerWithImages.copy(id = offerRef.id)
        val notificationRef = db.collection("notifications").document()
        val notification = NotificationItem(
            id = notificationRef.id, userId = offer.needOwnerId, title = "¡Nueva Oferta Recibida!",
            message = "${offer.ownerName} ha hecho una oferta para tu necesidad '${offer.needText}'.",
            type = "new_offer", referenceId = offerRef.id
        )
        batch.set(offerRef, finalOffer)
        batch.set(notificationRef, notification)
        batch.commit().await()
        return finalOffer
    }

    fun getReceivedOffers(userId: String): Flow<List<Offer>> = callbackFlow { 
         var offerListeners = listOf<ListenerRegistration>()
        val needsListener = db.collection("needs").whereEqualTo("userId", userId)
            .addSnapshotListener { needsSnapshot, needsError ->
                if (needsError != null) {
                    close(needsError)
                    return@addSnapshotListener
                }
                offerListeners.forEach { it.remove() }
                val needs = needsSnapshot?.toObjects(Need::class.java) ?: emptyList()
                if (needs.isEmpty()) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val combinedOffers = mutableMapOf<String, Offer>()
                val newListeners = mutableListOf<ListenerRegistration>()
                needs.forEach { need ->
                    if (need.id.isNotEmpty()) {
                        val listener = db.collection("needs").document(need.id).collection("offers")
                            .whereEqualTo("status", "PENDIENTE")
                            .addSnapshotListener { offersSnapshot, _ ->
                                offersSnapshot?.documents?.forEach { doc ->
                                    doc.toObject(Offer::class.java)?.let { offer -> combinedOffers[doc.id] = offer }
                                }
                                trySend(combinedOffers.values.toList()).isSuccess
                            }
                        newListeners.add(listener)
                    }
                }
                offerListeners = newListeners
            }
        
        awaitClose { 
            needsListener.remove()
            offerListeners.forEach { it.remove() } 
        }
     }

    suspend fun acceptOffer(offer: Offer): Result<Unit> = try {
        db.runTransaction {
            transaction ->
            val offerRef = db.collection("needs").document(offer.needId).collection("offers").document(offer.id)
            val needRef = db.collection("needs").document(offer.needId)
            transaction.update(offerRef, "status", "ACEPTADA")
            transaction.update(needRef, "status", "COMPLETADA")

            val notificationToOffererRef = db.collection("notifications").document()
            val notificationToNeedOwnerRef = db.collection("notifications").document()
            val notificationToOfferer = NotificationItem(userId = offer.ownerId, title = "¡Tu oferta fue aceptada!", message = "El dueño de '${offer.needText}' aceptó tu oferta por '${offer.title}'.", type = "offer_accepted")
            val notificationToNeedOwner = NotificationItem(userId = offer.needOwnerId, title = "Oferta Aceptada", message = "Has aceptado la oferta de ${offer.ownerName} para tu necesidad '${offer.needText}'.", type = "offer_accepted")

            transaction.set(notificationToOffererRef, notificationToOfferer)
            transaction.set(notificationToNeedOwnerRef, notificationToNeedOwner)
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun rejectOffer(offer: Offer): Result<Unit> = try {
        db.runBatch {
            batch ->
            val offerRef = db.collection("needs").document(offer.needId).collection("offers").document(offer.id)
            val notificationRef = db.collection("notifications").document()
            val notification = NotificationItem(userId = offer.ownerId, title = "Oferta Rechazada", message = "Tu oferta por '${offer.needText}' fue rechazada.", type = "offer_rejected")

            batch.update(offerRef, "status", "RECHAZADA")
            batch.set(notificationRef, notification)
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getOfferHistory(userId: String): Flow<List<Offer>> = callbackFlow {
        val allNeedsListener = db.collection("needs").addSnapshotListener { allNeedsSnapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val allNeeds = allNeedsSnapshot?.toObjects(Need::class.java) ?: emptyList()
            if (allNeeds.isEmpty()) {
                trySend(emptyList()).isSuccess
                return@addSnapshotListener
            }

            val combinedHistory = mutableMapOf<String, Offer>()
            allNeeds.forEach { need ->
                if (need.id.isNotEmpty()) {
                    db.collection("needs").document(need.id).collection("offers")
                        .whereIn("status", listOf("ACEPTADA", "RECHAZADA"))
                        .addSnapshotListener { offersSnapshot, _ ->
                            offersSnapshot?.documents?.forEach { doc ->
                                val offer = doc.toObject(Offer::class.java)
                                if (offer != null && (offer.ownerId == userId || offer.needOwnerId == userId)) {
                                    combinedHistory[doc.id] = offer
                                }
                            }
                            trySend(combinedHistory.values.toList()).isSuccess
                        }
                }
            }
        }
        awaitClose { allNeedsListener.remove() }
    }
}