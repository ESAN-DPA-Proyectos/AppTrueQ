package edu.esandpa202502.apptrueq.repository.proposal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Proposal
import kotlinx.coroutines.tasks.await

// QA: Se crea un repositorio específico para la lógica de propuestas, usado por NotificationDetailViewModel.
class ProposalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val proposalsCollection = db.collection("proposals")

    suspend fun getProposalById(proposalId: String): Proposal? {
        return try {
            val document = proposalsCollection.document(proposalId).get().await()
            document.toObject(Proposal::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateProposalStatus(proposalId: String, newStatus: String) {
        try {
            proposalsCollection.document(proposalId).update("status", newStatus).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getProposalsReceivedForUser(userId: String): List<Proposal> {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("publicationOwnerId", userId)
                .whereEqualTo("status", "PENDIENTE")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Proposal::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("Error getting proposals for user: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun hasExistingProposal(userId: String, publicationId: String): Boolean {
        return try {
            val query = proposalsCollection
                .whereEqualTo("proposerId", userId)
                .whereEqualTo("publicationId", publicationId)
                .limit(1)
                .get()
                .await()
            !query.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}