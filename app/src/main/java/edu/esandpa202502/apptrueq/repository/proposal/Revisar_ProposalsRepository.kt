package edu.esandpa202502.apptrueq.repository.proposal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Proposal
import kotlinx.coroutines.tasks.await

class Revisar_ProposalsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val proposalsCollection = db.collection("proposals")

    suspend fun getProposalById(proposalId: String): Proposal? {
        return proposalsCollection.document(proposalId).get().await().toObject(Proposal::class.java)
    }

    suspend fun updateProposalStatus(proposalId: String, newStatus: String) {
        proposalsCollection.document(proposalId).update("status", newStatus).await()
    }

    suspend fun getSentProposals(userId: String): List<Proposal> {
        return proposalsCollection
            .whereEqualTo("proposerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Proposal::class.java)
    }

    suspend fun getReceivedProposals(userId: String): List<Proposal> {
        return proposalsCollection
            .whereEqualTo("publicationOwnerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Proposal::class.java)
    }

    suspend fun getAcceptedProposals(userId: String): List<Proposal> {
        return proposalsCollection
            .whereEqualTo("proposerId", userId)
            .whereEqualTo("status", "ACEPTADA") // Coincide con el nuevo estado
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Proposal::class.java)
    }
}