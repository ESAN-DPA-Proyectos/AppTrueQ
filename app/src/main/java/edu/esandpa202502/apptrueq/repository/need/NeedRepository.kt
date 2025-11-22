package edu.esandpa202502.apptrueq.repository.need

import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.Need
import kotlinx.coroutines.tasks.await

class NeedRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función para obtener TODAS las necesidades (para la pantalla "Explorar")
    suspend fun getAllNeeds(): List<Need> {
        return db.collection("needs").get().await().toObjects(Need::class.java)
    }

    // Nueva función optimizada para obtener solo las necesidades del usuario actual
    suspend fun getMyNeeds(userId: String): List<Need> {
        return db.collection("needs")
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
            .toObjects(Need::class.java)
    }

    suspend fun addNeed(need: Need) {
        db.collection("needs").add(need).await()
    }
}