package edu.esandpa202502.apptrueq.repository.proposal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Proposal
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProposalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val proposalsCollection = db.collection("proposals")

    /**
     * Añade una nueva propuesta a Firestore y devuelve su ID.
     */
    suspend fun addProposal(proposal: Proposal): String {
        val document = proposalsCollection.add(proposal).await()
        // Opcional: Actualizamos el ID del objeto para que coincida con el de Firestore
        proposalsCollection.document(document.id).update("id", document.id).await()
        return document.id
    }

    /**
     * Obtiene un flujo de propuestas recibidas por un usuario específico.
     */
    fun getReceivedProposals(userId: String): Flow<List<Proposal>> = callbackFlow {
        val listener = proposalsCollection
            .whereEqualTo("publicationOwnerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val proposals = snapshot?.toObjects(Proposal::class.java) ?: emptyList()
                trySend(proposals).isSuccess
            }
        awaitClose { listener.remove() }
    }

    /**
     * Actualiza el estado de una propuesta (ej. a ACEPTADA o RECHAZADA).
     */
    suspend fun updateProposalStatus(proposalId: String, status: String) {
        if (proposalId.isEmpty()) return
        proposalsCollection.document(proposalId).update("status", status).await()
    }

    /**
     * Verifica si un usuario ya tiene una propuesta activa (pendiente) para una publicación específica.
     */
    suspend fun hasActiveProposal(userId: String, publicationId: String): Boolean {
        val query = proposalsCollection
            .whereEqualTo("proposerId", userId)
            .whereEqualTo("publicationId", publicationId)
            .whereEqualTo("status", "PENDIENTE")
            .limit(1)
            .get()
            .await()

        return !query.isEmpty
    }
}
