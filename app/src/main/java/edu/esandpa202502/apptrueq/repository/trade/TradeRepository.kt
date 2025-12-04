package edu.esandpa202502.apptrueq.repository.trade

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Proposal
import edu.esandpa202502.apptrueq.model.Trade
import edu.esandpa202502.apptrueq.model.TradeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tradesCollection = db.collection("trades")

    /**
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
            status = TradeStatus.ACCEPTED // El trueque nace como 'ACEPTADO'
        )

        val tradeDocument = tradesCollection.add(newTrade).await()
        return tradeDocument.id
    }

    /**
     * Obtiene el historial completo de trueques para un usuario, tanto los que ha ofrecido como los que ha recibido.
     */
    suspend fun getTradeHistory(userId: String): List<Trade> {
        val offeredTrades = tradesCollection
            .whereEqualTo("offerentId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Trade::class.java)

        val receivedTrades = tradesCollection
            .whereEqualTo("receiverId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Trade::class.java)

        // Combinamos las listas, eliminamos duplicados y re-ordenamos por la fecha más reciente.
        return (offeredTrades + receivedTrades).distinctBy { it.id }.sortedByDescending { it.updatedAt }
    }
}
