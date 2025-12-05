package edu.esandpa202502.apptrueq.repository.exchange

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Offer
import edu.esandpa202502.apptrueq.model.Need
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ExchangeRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Explorar ofertas disponibles (HU-05)
     * Solo ofertas activas.
     */
    fun getAvailableOffers(): Flow<List<Offer>> = callbackFlow {
        val listener = db.collection("offers")
            .whereEqualTo("status", "ACTIVE")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Offer::class.java) ?: emptyList()
                trySend(list).isSuccess
            }

        awaitClose { listener.remove() }
    }

    /**
     * Explorar necesidades publicadas (HU-04)
     */
    fun getPublishedNeeds(): Flow<List<Need>> = callbackFlow {
        val listener = db.collection("needs")
            .whereEqualTo("status", "ACTIVE")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.toObjects(Need::class.java) ?: emptyList()
                trySend(list).isSuccess
            }

        awaitClose { listener.remove() }
    }

    /**
     * Buscar ofertas por texto.
     */
    fun searchOffers(query: String): Flow<List<Offer>> = callbackFlow {
        val listener = db.collection("offers")
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.toObjects(Offer::class.java) ?: emptyList()
                val filtered = list.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.offerText.contains(query, ignoreCase = true) ||
                            it.needText.contains(query, ignoreCase = true)
                }
                trySend(filtered).isSuccess
            }

        awaitClose { listener.remove() }
    }

    /**
     * Buscar necesidades por texto.
     */
    fun searchNeeds(query: String): Flow<List<Need>> = callbackFlow {
        val listener = db.collection("needs")
            .whereEqualTo("status", "ACTIVE")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.toObjects(Need::class.java) ?: emptyList()
                val filtered = list.filter {
                    it.needText.contains(query, ignoreCase = true)
                }
                trySend(filtered).isSuccess
            }

        awaitClose { listener.remove() }
    }
}
