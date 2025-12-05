package edu.esandpa202502.apptrueq.repository.proposal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.NotificationItem
import edu.esandpa202502.apptrueq.model.Proposal
import kotlinx.coroutines.tasks.await

class ProposalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val proposalsCollection = db.collection("proposals")

    /**
     * SOLUCIÓN: Crea una propuesta y la notificación correspondiente en una sola operación atómica (batch).
     * Esto asegura que si una de las dos escrituras falla, ninguna se complete.
     * Centraliza la lógica de negocio, quitándosela al ViewModel.
     * @param proposal El objeto Proposal a guardar.
     * @return El ID de la propuesta recién creada.
     */
    suspend fun createProposalAndNotify(proposal: Proposal): String {
        try {
            val batch = db.batch()

            // 1. Referencia para la nueva propuesta (con ID autogenerado)
            val newProposalRef = proposalsCollection.document()
            // Se crea una copia de la propuesta para asignarle el ID que acabamos de generar.
            val proposalWithId = proposal.copy(id = newProposalRef.id)

            // 2. Referencia para la nueva notificación
            val notificationRef = db.collection("notifications").document()
            val notification = NotificationItem(
                userId = proposal.publicationOwnerId,
                title = "¡Has recibido una nueva propuesta!",
                message = "${proposal.proposerName} ha hecho una propuesta para tu publicación: '${proposal.publicationTitle}'.",
                type = "new_proposal",
                referenceId = newProposalRef.id // Se usa el ID de la nueva propuesta
            )

            // 3. Se añaden ambas operaciones al lote de escritura
            batch.set(newProposalRef, proposalWithId)
            batch.set(notificationRef, notification)

            // 4. Se ejecuta el lote
            batch.commit().await()

            return newProposalRef.id
        } catch (e: Exception) {
            println("Error creating proposal and notification: ${e.message}")
            throw e
        }
    }

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