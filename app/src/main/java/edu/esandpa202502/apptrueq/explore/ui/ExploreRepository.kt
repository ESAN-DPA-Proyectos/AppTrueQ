package edu.esandpa202502.apptrueq.explore.ui

import edu.esandpa202502.apptrueq.model.Publication
import edu.esandpa202502.apptrueq.model.PublicationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repositorio para manejar los datos de las publicaciones.
 * En el futuro, este será el único lugar que hable con Firestore.
 */
class ExploreRepository {

    // Por ahora, simulamos una llamada a la base de datos con los datos de ejemplo.
    // La función es 'suspend' para simular una operación de red asíncrona.
    suspend fun getPublications(): List<Publication> {
        return withContext(Dispatchers.IO) {
            // Simula un retraso de red
            // kotlinx.coroutines.delay(1000)
            listOf(
                Publication("1", "Laptop Gamer", "Laptop en buen estado", "Tecnología", "Lima", "https://picsum.photos/id/10/200/300", Date(), "user1", PublicationType.OFFER),
                Publication("2", "Libro de Kotlin", "Busco libro de Kotlin", "Libros", "Surco", "https://picsum.photos/id/20/200/300", Date(), "user2", PublicationType.NEED),
                Publication("3", "Ropa de invierno", "Casaca talla M", "Ropa", "Miraflores", "https://picsum.photos/id/30/200/300", Date(), "user3", PublicationType.OFFER)
            )
        }
    }
}
