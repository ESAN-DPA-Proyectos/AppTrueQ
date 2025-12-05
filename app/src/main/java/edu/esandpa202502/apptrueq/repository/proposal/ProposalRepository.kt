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

            // Se usa el ID autogenerado para guardarlo también dentro del documento.
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

    /**
     * SOLUCIÓN: Se implementa la lógica que faltaba.
     * Obtiene una propuesta por su ID.
     */
    suspend fun getProposalById(proposalId: String): Proposal? {
        return try {
            val document = proposalsCollection.document(proposalId).get().await()
            document.toObject(Proposal::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            println("Error getting proposal by ID: ${e.message}")
            null
        }
    }

    /**
     * SOLUCIÓN: Se implementa la lógica que faltaba.
     * Actualiza el estado de una propuesta.
     */
    suspend fun updateProposalStatus(proposalId: String, newStatus: String) {
        try {
            proposalsCollection.document(proposalId).update("status", newStatus).await()
        } catch (e: Exception) {
            println("Error updating proposal status: ${e.message}")
            throw e
        }
    }

    /**
     * Obtiene todas las propuestas PENDIENTES que un usuario ha RECIBIDO.
     */
    suspend fun getProposalsReceivedForUser(userId: String): List<Proposal> {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("publicationOwnerId", userId)
                .whereEqualTo("status", "PENDIENTE")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            // SOLUCIÓN: Se usa mapeo manual para asegurar que se incluye el ID del documento.
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Proposal::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("Error getting received proposals: ${e.message}")
            emptyList()
        }
    }

    /**
     * Obtiene todas las propuestas que un usuario ha ENVIADO.
     */
    suspend fun getProposalsSentByUser(userId: String): List<Proposal> {
        return try {
            val querySnapshot = proposalsCollection
                .whereEqualTo("proposerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            // SOLUCIÓN: Se usa mapeo manual para asegurar que se incluye el ID del documento.
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Proposal::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("Error getting sent proposals: ${e.message}")
            emptyList()
        }
    }

    /**
     * SOLUCIÓN: Se implementa la lógica que faltaba.
     * Comprueba si ya existe una propuesta de un usuario para una publicación específica.
     */
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