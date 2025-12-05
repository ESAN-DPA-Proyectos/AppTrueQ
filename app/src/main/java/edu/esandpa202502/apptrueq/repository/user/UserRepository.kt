package edu.esandpa202502.apptrueq.repository.user

import com.google.firebase.firestore.FirebaseFirestore
import edu.esandpa202502.apptrueq.model.User
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para gestionar las operaciones de datos relacionadas con los usuarios.
 */
class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    /**
     * Obtiene un usuario específico por su ID de la colección `users`.
     * @param userId El ID del usuario a buscar.
     * @return El objeto User si se encuentra, o null si no existe o hay un error.
     */
    suspend fun getUserById(userId: String): User? {
        return try {
            usersCollection.document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            // En caso de error, simplemente devolvemos null.
            // Podríamos registrar el error si fuera necesario para depuración.
            println("Error fetching user: ${e.message}")
            null
        }
    }
}