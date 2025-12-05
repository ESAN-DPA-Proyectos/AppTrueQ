package edu.esandpa202502.apptrueq.repository.need

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.Need
import kotlinx.coroutines.tasks.await

class NeedRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Registrar una nueva necesidad en Firestore
     */
    suspend fun addNeed(need: Need): Need {
        val docRef = db.collection("needs").document()

        val needToSave = need.copy(
            id = docRef.id,
            createdAt = Timestamp.now()
        )

        docRef.set(needToSave).await()

        return needToSave
    }

    /**
     * Obtener todas las necesidades de un usuario
     */
    suspend fun getNeedsByUser(userId: String): List<Need> {
        return try {
            db.collection("needs")
                .whereEqualTo("ownerId", userId)
                .get()
                .await()
                .toObjects(Need::class.java)
        } catch (_: Exception) {
            emptyList()
        }
    }
}
