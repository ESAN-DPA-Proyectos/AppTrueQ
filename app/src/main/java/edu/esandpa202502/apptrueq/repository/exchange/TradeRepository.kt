package edu.esandpa202502.apptrueq.repository.exchange

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.model.Trade
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene las ofertas recibidas por un usuario (donde él es el dueño de la oferta)
     */
    fun getReceivedOffers(userId: String): Flow<List<Offer>> = callbackFlow {
        val listener = db.collection("offers")
            .whereEqualTo("ownerId", userId)
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

    /**
     * Aceptar oferta
     */
    suspend fun acceptOffer(offer: Offer): Result<Unit> = try {
        db.runTransaction { transaction ->

            val offerRef = db.collection("offers").document(offer.id)

            // Cambiar estado de la oferta
            transaction.update(offerRef, "status", "ACEPTADA")

            // Notificación al ofertante
            val notifOfferer = NotificationItem(
                userId = offer.ownerId,
                title = "¡Tu oferta fue aceptada!",
                message = "Han aceptado tu oferta '${offer.title}'.",
                type = "offer_accepted"
            )

            val notifRef = db.collection("notifications").document()
            transaction.set(notifRef, notifOfferer)
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Rechazar oferta
     */
    suspend fun rejectOffer(offer: Offer): Result<Unit> = try {
        db.runBatch { batch ->

            val offerRef = db.collection("offers").document(offer.id)

            batch.update(offerRef, "status", "RECHAZADA")

            val notification = NotificationItem(
                userId = offer.ownerId,
                title = "Oferta Rechazada",
                message = "Tu oferta por '${offer.title}' fue rechazada.",
                type = "offer_rejected"
            )

            val notifRef = db.collection("notifications").document()
            batch.set(notifRef, notification)
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Historial de ofertas: enviadas + recibidas.
     * (lo puedes seguir usando donde ya lo tengas referenciado)
     */
    fun getOfferHistory(userId: String): Flow<List<Offer>> {

        // Ofertas enviadas por el usuario
        val sentFlow = getOffersByField("ownerId", userId)

        // Ofertas recibidas (en este modelo NO hay needOwnerId, así que será igual)
        val receivedFlow = getOffersByField("ownerId", userId)

        return combine(sentFlow, receivedFlow) { sent, received ->
            (sent + received)
                .distinctBy { it.id }
                .sortedByDescending { it.createdAt }
        }
    }

    /**
     * Obtener ofertas filtradas por campo
     */
    private fun getOffersByField(field: String, userId: String): Flow<List<Offer>> =
        callbackFlow {
            val listener = db.collection("offers")
                .whereEqualTo(field, userId)
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

    // -----------------------------------------------------------
    // 🔵 SECCIÓN TRUEQUES (HU-07 y HU-09)
    // -----------------------------------------------------------

    /**
     * Crear un trueque cuando se acepta una propuesta.
     */
    suspend fun createTrade(
        offerId: String,
        receiverId: String,
        proposerId: String,
        proposerName: String
    ) {
        val tradeRef = db.collection("trades").document()

        val tradeData = mapOf(
            "id" to tradeRef.id,
            "offerId" to offerId,
            "receiverId" to receiverId,
            "proposerId" to proposerId,
            "proposerName" to proposerName,
            "status" to "propuesto",   // estado inicial
            "createdAt" to Timestamp.now()
        )

        tradeRef.set(tradeData).await()
    }

    /**
     * Historial de trueques del usuario (HU-09).
     * Incluye trueques donde el usuario fue proponente o receptor.
     */
    suspend fun getTradeHistory(userId: String): List<Trade> {
        return try {
            val sentSnapshot = db.collection("trades")
                .whereEqualTo("proposerId", userId)
                .get()
                .await()

            val receivedSnapshot = db.collection("trades")
                .whereEqualTo("receiverId", userId)
                .get()
                .await()

            val sent = sentSnapshot.toObjects(Trade::class.java)
            val received = receivedSnapshot.toObjects(Trade::class.java)

            (sent + received)
                .distinctBy { it.id }
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
