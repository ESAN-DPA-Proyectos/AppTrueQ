package edu.esandpa202502.apptrueq.repository.trade

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Trade
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tradesCollection = db.collection("trades")

    /**
     * SOLUCIÓN: Se restaura el método que faltaba.
     * Crea un nuevo registro de Trade en Firestore a partir de una propuesta aceptada.
     */
    suspend fun createTradeFromProposal(proposal: Proposal, receiverName: String): String {
        val newTrade = Trade(
            proposalId = proposal.id,
            publicationId = proposal.publicationId,
            publicationTitle = proposal.publicationTitle,
            offerentId = proposal.proposerId,
            offerentName = proposal.proposerName,
            receiverId = proposal.publicationOwnerId,
            receiverName = receiverName,
            status = "Aceptado" // Se usa un String simple para el estado
        )

        val tradeDocument = tradesCollection.add(newTrade).await()
        return tradeDocument.id
    }

    /**
     * HU-09: Obtiene el historial de trueques para un usuario específico.
     */
    suspend fun getTradeHistory(userId: String): List<Trade> {
        try {
            val offeredTradesQuery = tradesCollection.whereEqualTo("offerentId", userId).get().await()
            val offeredTrades = offeredTradesQuery.documents.mapNotNull { doc ->
                doc.toObject(Trade::class.java)?.copy(id = doc.id)
            }

            val receivedTradesQuery = tradesCollection.whereEqualTo("receiverId", userId).get().await()
            val receivedTrades = receivedTradesQuery.documents.mapNotNull { doc ->
                doc.toObject(Trade::class.java)?.copy(id = doc.id)
            }

            return (offeredTrades + receivedTrades)
                .distinctBy { it.id }
                .sortedByDescending { it.createdAt }

        } catch (e: Exception) {
            println("Error getting trade history: ${e.message}")
            throw e
        }
    }
}