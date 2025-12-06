package edu.esandpa202502.apptrueq.repository.proposal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import kotlinx.coroutines.tasks.await

class ProposalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val proposalsCollection = db.collection("proposals")

    suspend fun createProposalAndNotify(proposal: Proposal): String {
        try {
            val batch = db.batch()
            val newProposalRef = proposalsCollection.document()

            val proposalWithId = proposal.copy(id = newProposalRef.id)

            val notificationRef = db.collection("notifications").document()
            val notification = NotificationItem(
                userId = proposal.publicationOwnerId,
                title = "¡Has recibido una nueva propuesta!",
                message = "${proposal.proposerName} ha hecho una propuesta para tu publicación: '${proposal.publicationTitle}'.",
                type = "new_proposal",
                referenceId = newProposalRef.id
            )

            batch.set(newProposalRef, proposalWithId)
            batch.set(notificationRef, notification)
            batch.commit().await()
            return newProposalRef.id
        } catch (e: Exception) {
            println("Error creating proposal and notification: ${e.message}")
            throw e
        }
    }
    
    suspend fun getPublicationTitle(publicationId: String): String? {
        return try {
            var doc = db.collection("offers").document(publicationId).get().await()
            if (doc.exists()) {
                return doc.getString("title")
            }

            doc = db.collection("needs").document(publicationId).get().await()
            if (doc.exists()) {
                val text = doc.getString("needText") ?: ""
                return text.take(40) + if (text.length > 40) "..." else ""
            }
            null
        } catch (e: Exception) {
            println("Error getting publication title: ${e.message}")
            null
        }
    }

    suspend fun getProposalById(proposalId: String): Proposal? {
        return try {
            val document = proposalsCollection.document(proposalId).get().await()
            document.toObject(Proposal::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            println("Error getting proposal by ID: ${e.message}")
            null
        }
    }

    suspend fun updateProposalStatus(proposalId: String, newStatus: String) {
        try {
            proposalsCollection.document(proposalId).update("status", newStatus).await()
        } catch (e: Exception) {
            println("Error updating proposal status: ${e.message}")
            throw e
        }
    }

    suspend fun getProposalsReceivedForUser(userId: String): List<Proposal> {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("publicationOwnerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val proposals = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Proposal::class.java)?.copy(id = doc.id)
            }

            return proposals.map { proposal ->
                if (proposal.offeredPublicationId != null && proposal.offeredPublicationTitle == null) {
                    val title = getPublicationTitle(proposal.offeredPublicationId)
                    proposal.copy(offeredPublicationTitle = title)
                } else {
                    proposal
                }
            }
        } catch (e: Exception) {
            println("Error getting received proposals: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProposalsSentByUser(userId: String): List<Proposal> {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("proposerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val proposals = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Proposal::class.java)?.copy(id = doc.id)
            }

            return proposals.map { proposal ->
                if (proposal.offeredPublicationId != null && proposal.offeredPublicationTitle == null) {
                    val title = getPublicationTitle(proposal.offeredPublicationId)
                    proposal.copy(offeredPublicationTitle = title)
                } else {
                    proposal
                }
            }
        } catch (e: Exception) {
            println("Error getting sent proposals: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProposalHistoryForUser(userId: String): List<Proposal> {
        return try {
            val received = getProposalsReceivedForUser(userId)
            val sent = getProposalsSentByUser(userId)
            (received + sent).distinctBy { it.id }.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            println("Error getting proposal history: ${e.message}")
            emptyList()
        }
    }

    suspend fun hasExistingProposal(userId: String, publicationId: String): Boolean {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("proposerId", userId)
                .whereEqualTo("publicationId", publicationId)
                .limit(1)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            println("Error checking for existing proposal: ${e.message}")
            false
        }
    }
}
