package edu.esandpa202502.apptrueq.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Representa un usuario en la colección `users` de Firestore.
 * SOLUCIÓN DEFINITIVA: Se modelan explícitamente todos los campos que existen en la base de datos,
 * incluyendo los objetos complejos como un `Map`. Esto, junto con @IgnoreExtraProperties,
 * asegura que Firestore pueda deserializar el documento a este objeto sin errores,
 * permitiendo leer el campo `name` y solucionando el problema de "Usuario Anónimo".
 */
@IgnoreExtraProperties
data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val uid: String? = null, // Se añade el campo uid que existe en la BD
    val createdAt: Map<String, Any>? = null, // Se modela el objeto complejo como un Mapa
    val chronology: Map<String, Any>? = null // Se modela el objeto complejo como un Mapa
)
