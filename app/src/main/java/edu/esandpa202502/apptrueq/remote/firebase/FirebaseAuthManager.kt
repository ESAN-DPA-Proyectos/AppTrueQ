package edu.esandpa202502.apptrueq.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime


object FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    //registro de usuario
    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid.toString()

            val user = hashMapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "createdAt" to LocalDateTime.now()
            )

            firestore.collection("users").document(uid).set(user).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)

        }

        }
    //login user
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try{
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)

        } catch (e: Exception){
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

}
