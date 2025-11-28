package edu.esandpa202502.apptrueq.repository.report

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Report
import kotlinx.coroutines.tasks.await

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllReports(): List<Report> {
        return try {
            db.collection("reports")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Report::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
