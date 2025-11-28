package edu.esandpa202502.apptrueq.explore.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.esandpa202502.apptrueq.model.Publication
import kotlinx.coroutines.tasks.await

class PublicationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPublications(): List<Publication> {
        val publications = mutableListOf<Publication>()
        try {
            val snapshot = db.collection("publications")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            for (document in snapshot.documents) {
                document.toObject(Publication::class.java)?.let {
                    publications.add(it)
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
        return publications
    }
}
