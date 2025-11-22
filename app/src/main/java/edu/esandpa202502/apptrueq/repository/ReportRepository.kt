package edu.esandpa202502.apptrueq.repository

import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.Report
import kotlinx.coroutines.tasks.await

class ReportRepository {

    // SE UTILIZA EL MÉTODO ESTÁNDAR QUE SABEMOS QUE FUNCIONA
    private val db = FirebaseFirestore.getInstance()

    suspend fun submitReport(
        reportedEmail: String,
        reason: String,
        description: String,
        reporterId: String
    ): Result<Unit> = try {
        val userQuery = db.collection("users").whereEqualTo("email", reportedEmail).limit(1).get().await()

        if (userQuery.isEmpty) {
            throw Exception("No se encontró ningún usuario con el correo electrónico proporcionado.")
        }

        val reportedUser = userQuery.documents.first()
        val reportedUserId = reportedUser.id
        val reportedUserName = reportedUser.getString("name") ?: ""

        val newReportRef = db.collection("reports").document()
        val newReport = Report(
            id = newReportRef.id,
            reportedUserId = reportedUserId,
            reportedUserName = reportedUserName,
            reportingUserId = reporterId,
            reason = reason,
            description = description
        )

        newReportRef.set(newReport).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
