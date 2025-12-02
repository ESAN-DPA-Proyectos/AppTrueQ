package edu.esandpa202502.apptrueq.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Offer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getReceivedOffers(userId: String): Flow<List<Offer>> = callbackFlow {
        val listener = db.collectionGroup("offers")
            .whereEqualTo("needOwnerId", userId)
            .whereEqualTo("status", "PENDIENTE")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val offers = snapshot?.toObjects(Offer::class.java) ?: emptyList()
                trySend(offers).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun acceptOffer(offer: Offer): Result<Unit> = try {
        db.runTransaction {
            transaction ->
            val offerRef = db.collection("needs").document(offer.needId).collection("offers").document(offer.id)
            val needRef = db.collection("needs").document(offer.needId)

            transaction.update(offerRef, "status", "ACEPTADA")
            transaction.update(needRef, "status", "COMPLETADA")

            // Crear notificaciones
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

    fun getOfferHistory(userId: String): Flow<List<Offer>> {
        val sentOffersFlow = getOffersByField("ownerId", userId)
        val receivedOffersFlow = getOffersByField("needOwnerId", userId)

        return combine(sentOffersFlow, receivedOffersFlow) { sent, received ->
            (sent + received).distinctBy { it.id }.sortedByDescending { it.createdAt }
        }
    }

    private fun getOffersByField(fieldName: String, userId: String): Flow<List<Offer>> = callbackFlow {
        val listener = db.collectionGroup("offers")
            .whereEqualTo(fieldName, userId)
            .whereIn("status", listOf("ACEPTADA", "RECHAZADA"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val offers = snapshot?.toObjects(Offer::class.java) ?: emptyList()
                trySend(offers).isSuccess
            }
        awaitClose { listener.remove() }
    }
}