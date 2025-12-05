package edu.esandpa202502.apptrueq.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

object FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Registro de usuario
    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            // 1️⃣ Crear usuario en FirebaseAuth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("No se pudo obtener el usuario creado.")

            // 2️⃣ Actualizar el perfil de FirebaseAuth con el nombre
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates).await()

            // 3️⃣ Guardar el documento en la colección "users"
            val userDoc = hashMapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to email,
                "createdAt" to LocalDateTime.now()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userDoc)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login de usuario
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is FirebaseAuthInvalidUserException || e is FirebaseAuthInvalidCredentialsException) {
                Result.failure(Exception("El correo o la contraseña son incorrectos."))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        firestore.clearPersistence()
    }
}
