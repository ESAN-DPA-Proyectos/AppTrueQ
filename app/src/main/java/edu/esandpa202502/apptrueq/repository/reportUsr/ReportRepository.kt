package edu.esandpa202502.apptrueq.repository.reportUsr

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Report
import kotlinx.coroutines.tasks.await

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("reports")

    /**
     * Guarda un nuevo reporte en la base de datos con los campos correctos.
     */
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


    /**
     * Obtiene todos los reportes, mapeando el ID del documento para evitar claves duplicadas.
     * ESTA ES LA CORRECCIÓN DEFINITIVA.
     */
    suspend fun getAllReports(): List<Report> {
        return try {
            val querySnapshot = reportsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            // Mapea los documentos y asigna el ID a cada objeto Report.
            querySnapshot.documents.mapNotNull { document ->
                // Convierte el documento a un objeto Report
                val report = document.toObject(Report::class.java)
                // Asigna el ID del documento de Firestore al campo 'id' del objeto.
                report?.copy(id = document.id)
            }
        } catch (e: Exception) {
            // En caso de error, devuelve una lista vacía para no romper la app.
            emptyList()
        }
    }
}
