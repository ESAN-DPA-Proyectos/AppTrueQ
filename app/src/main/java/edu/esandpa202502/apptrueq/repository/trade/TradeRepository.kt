package edu.esandpa202502.apptrueq.repository.trade

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Trade
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para manejar las operaciones de datos relacionadas con el Historial de Trueques (Trades).
 */
class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tradesCollection = db.collection("trades")

    /**
     * Añade un nuevo registro de trueque a Firestore.
     */
    suspend fun createTrade(trade: Trade) {
        try {
            tradesCollection.add(trade).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * HU-09: Obtiene el historial de trueques para un usuario específico.
     * Un usuario puede ser tanto el que ofrece (`offerentId`) como el que recibe (`receiverId`).
     * Por eso, necesitamos hacer dos consultas y combinar los resultados.
     */
    suspend fun getTradeHistoryForUser(userId: String): List<Trade> {
        try {
            // Consulta 1: Trueques donde el usuario es el OFERTANTE.
            val offeredTradesQuery = tradesCollection.whereEqualTo("offerentId", userId).get().await()
            val offeredTrades = offeredTradesQuery.documents.mapNotNull { doc ->
                doc.toObject(Trade::class.java)?.copy(id = doc.id)
            }

            // Consulta 2: Trueques donde el usuario es el RECEPTOR.
            val receivedTradesQuery = tradesCollection.whereEqualTo("receiverId", userId).get().await()
            val receivedTrades = receivedTradesQuery.documents.mapNotNull { doc ->
                doc.toObject(Trade::class.java)?.copy(id = doc.id)
            }

            // Combinamos las dos listas, eliminamos duplicados (por si acaso) y ordenamos por fecha.
            return (offeredTrades + receivedTrades)
                .distinctBy { it.id }
                .sortedByDescending { it.createdAt }

        } catch (e: Exception) {
            println("Error getting trade history: ${e.message}")
            throw e // Propagamos la excepción para que el ViewModel la maneje.
        }
    }
}