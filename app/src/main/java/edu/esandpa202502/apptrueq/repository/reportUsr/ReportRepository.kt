package edu.esandpa202502.apptrueq.repository.reportUsr

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Report
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para manejar las operaciones de datos relacionadas con los Reportes.
 */
class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("reports")

    /**
     * HU-10: Guarda un nuevo reporte en la base de datos
     * a partir de campos sueltos (más fácil de usar desde el ViewModel).
     */
    suspend fun submitReport(
        reportedEmail: String,
        reason: String,
        description: String,
        reporterId: String
    ) {
        val data = hashMapOf(
            "reportedEmail" to reportedEmail,
            "reason" to reason,
            "description" to description,
            "reporterId" to reporterId,
            "createdAt" to Timestamp.now()
        )

        reportsCollection.add(data).await()
    }

    /**
     * Versión que recibe un objeto Report completo.
     * (La mantengo por si la quieres usar en otro contexto.)
     */
    suspend fun submitReport(report: Report) {
        try {
            reportsCollection.add(report).await()
        } catch (e: Exception) {
            // Propaga la excepción para que sea manejada por el ViewModel.
            throw e
        }
    }

    /**
     * Obtiene todos los reportes (para un futuro panel de moderación).
     */
    suspend fun getAllReports(): List<Report> {
        return try {
            reportsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Report::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
