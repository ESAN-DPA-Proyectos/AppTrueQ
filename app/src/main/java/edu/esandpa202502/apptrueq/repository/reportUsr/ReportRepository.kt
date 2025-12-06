package edu.esandpa202502.apptrueq.repository.reportUsr

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Report
import kotlinx.coroutines.tasks.await

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("reports")

    suspend fun submitReport(
        publicationId: String,
        reportedUserId: String,
        reportedUserName: String,
        reason: String,
        description: String,
        reporterId: String
    ) {
        val data = hashMapOf(
            "publicationId" to publicationId,
            "reportedUserId" to reportedUserId,
            "reportedUserName" to reportedUserName,
            "reason" to reason,
            "description" to description,
            "reporterId" to reporterId,
            "createdAt" to Timestamp.now(),
            "status" to "Pendiente de revisión"
        )
        reportsCollection.add(data).await()
    }
    
    suspend fun resolveReport(reportId: String) {
        reportsCollection.document(reportId).update("status", "Resuelta").await()
    }

    suspend fun getAllReports(): List<Report> {
        return try {
            val querySnapshot = reportsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                Report(
                    id = document.id,
                    publicationId = document.getString("publicationId") ?: "",
                    reportedUserId = document.getString("reportedUserId") ?: "",
                    reportedUserName = document.getString("reportedUserName") ?: "",
                    reportingUserId = document.getString("reporterId") ?: "",
                    reason = document.getString("reason") ?: "",
                    description = document.getString("description") ?: "",
                    status = document.getString("status") ?: "Pendiente de revisión",
                    createdAt = document.getTimestamp("createdAt")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
